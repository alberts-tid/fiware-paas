/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.paasmanager.manager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.mockito.Mockito.verify;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentDao;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentInstanceDao;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.dao.TierDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidOVFException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidVappException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.manager.impl.EnvironmentInstanceManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.OS;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jesus.movilla
 */
public class EnvironmentInstanceManagerImplTest {

    private EnvironmentInstanceDao environmentInstanceDao;
    private EnvironmentInstanceManagerImpl environmentInstanceManager;
    private EnvironmentDao environmentDao;
    private TierDao tierDao;
    private ProductReleaseDao productReleaseDao;
    private ProductInstanceManager productInstanceManager;

    private ProductInstance productInstance;
    private TierInstance tierInstance;
    private EnvironmentInstance environmentInstance;
    private NetworkManager networkManager;

    private EnvironmentManager environmentManager;
    private InfrastructureManager infrastructureManager;

    private ProductRelease productRelease;
    private Tier tier;
    private Environment environment;

    private List<VM> vms;

    private PaasManagerUser user;
    private ClaudiaData claudiaData;

    Collection<? extends GrantedAuthority> authorities;

    @Before
    public void setUp() throws Exception {
        // OVF
       

        claudiaData = new ClaudiaData("org", "vdc", "service");
        // Collection<GrantedAuthority> authorities = null;
        user = new PaasManagerUser("user", "password", new ArrayList<GrantedAuthority>());
        claudiaData.setUser(user);

        // Environment
        productRelease = new ProductRelease("product", "2.0");
        OS os = new OS("94", "ip", "hostname", "domain");
        List<OS> oss = new ArrayList<OS>();
        oss.add(os);
        productRelease.setSupportedOOSS(oss);

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        tier = new Tier();
        tier.setInitialNumberInstances(new Integer(1));
        tier.setMaximumNumberInstances(new Integer(5));
        tier.setMinimumNumberInstances(new Integer(1));
        tier.setName("tierName");
        tier.setProductReleases(productReleases);
        tier.setRegion("region1");
        tier.addNetwork(new Network ("uno", "VDC", "region1"));
        
        Tier tier2 = new Tier();
        tier2.setInitialNumberInstances(new Integer(1));
        tier2.setMaximumNumberInstances(new Integer(5));
        tier2.setMinimumNumberInstances(new Integer(1));
        tier2.setName("tierName2");
        tier2.setProductReleases(productReleases);
        tier2.setRegion("region2");
        tier2.addNetwork(new Network ("uno", "VDC", "region2"));

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);

        productReleaseDao = mock(ProductReleaseDao.class);
        when(productReleaseDao.load(any(String.class))).thenReturn(productRelease);

        tierDao = mock(TierDao.class);
        when(tierDao.load(any(String.class))).thenReturn(tier);

        environment = new Environment();
        environment.setName("environemntName");

        environment.setTiers(tiers);

        environmentDao = mock(EnvironmentDao.class);
        when(environmentDao.create(any(Environment.class))).thenReturn(environment);

        environmentManager = mock(EnvironmentManager.class);
        when(environmentManager.load(any(String.class), any(String.class))).thenReturn(environment);

        // Instance
        vms = new ArrayList<VM>();
        vms.add(new VM("fqn1", "ip1", "hostname1", "domain"));
        vms.add(new VM("fqn2", "ip2", "hostname2", "domain2"));

        productInstance = new ProductInstance();
        productInstance.setProductRelease(productRelease);
        // productInstance.setVm(vms.get(0));
        productInstance.setStatus(Status.INSTALLED);
        productInstance.setName("name");
        productInstance.setVdc("vdc");

        productInstanceManager = mock(ProductInstanceManager.class);
        when(
                productInstanceManager.install(any(TierInstance.class), any(ClaudiaData.class), any(String.class),
                        any(ProductRelease.class), anySet())).thenReturn(productInstance);

        List<ProductInstance> productInstances = new ArrayList<ProductInstance>();
        productInstances.add(productInstance);

        tierInstance = new TierInstance();
        tierInstance.setName("nametierInstance");
        tierInstance.setTier(tier);
        tierInstance.setVdc("vdc");
        tierInstance.setStatus(Status.INSTALLED);
        tierInstance.setProductInstances(productInstances);

        List<TierInstance> tierInstances = new ArrayList<TierInstance>();
        tierInstances.add(tierInstance);

        environmentInstance = new EnvironmentInstance();
        environmentInstance.setName("name");
        environmentInstance.setTierInstances(tierInstances);
        environmentInstance.setVdc("vdc");
        environmentInstance.setStatus(Status.INSTALLED);
        environmentInstance.setEnvironment(environment);

        environmentInstanceDao = mock(EnvironmentInstanceDao.class);
        when(environmentInstanceDao.load(any(String.class))).thenReturn(environmentInstance);
        
        environmentInstanceManager = new EnvironmentInstanceManagerImpl();
        infrastructureManager = mock (InfrastructureManager.class);
        networkManager = mock (NetworkManager.class);
        environmentInstanceManager.setInfrastructureManager(infrastructureManager);
        environmentInstanceManager.setNetworkManager(networkManager);
    }
    
    @Test
    public void testUpdateFederatedNetworks () throws InfrastructureException, EntityNotFoundException, InvalidEntityException {
    	when (infrastructureManager.getFederatedRange(any(ClaudiaData.class), any(String.class))).thenReturn("12");
    	Network net2 = new Network ("uno", "VDC", "region2");
    	when (networkManager.load( any(String.class), any(String.class), any(String.class))).thenReturn(net2);

    	environmentInstanceManager.updateFederatedNetworks(claudiaData, environment);
    //	verify (networkManager.update(any(Network.class)));

    }

}

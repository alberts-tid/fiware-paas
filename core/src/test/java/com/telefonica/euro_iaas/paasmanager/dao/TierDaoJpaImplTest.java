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

package com.telefonica.euro_iaas.paasmanager.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.model.Metadata;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;

/**
 * Unit test for TierDaoJpaImplTest
 * 
 * @author Jesus M. Movilla
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class TierDaoJpaImplTest {

    @Autowired
    private ProductReleaseDao productReleaseDao;
    @Autowired
    private NetworkDao networkDao;
    @Autowired
    private TierDao tierDao;
    
    @Autowired
    private SecurityGroupDao securityGroupDao;

    public final static String TIER_NAME = "TierName";
    public final static String PRODUCT_NAME = "Product";
    public final static String NETWORK_NAME = "NETWORK";
    public final static String VDC = "vdc";
    public final static String ENV = "env";
    public final static String PRODUCT_VERSION = "version";
    public final static Integer MAXIMUM_INSTANCES = 8;
    public final static Integer MINIMUM_INSTANCES = 1;
    public final static Integer INITIAL_INSTANCES = 1;
    public static String REGION = "region";

    /**
     * Test the create method
     */
    @Test
    public void testCreate1() throws Exception {

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);

        Tier tier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        int num = tierDao.findAll().size();
        tier = tierDao.create(tier);
        assertNotNull(tier);
        assertNotNull(tier.getId());
        
        List<Tier> tiers =  tierDao.findAll();
        assertEquals (tiers.size(),num+1);   
    }
    
    @Test
    public void testLoadTierWithProductReleaseAndMetadata() throws Exception {

    	Metadata metproduct = new Metadata("product", "product", "product");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease.addMetadata(metproduct);
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);

        Tier tier = new Tier(TIER_NAME+"pr", MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setEnviromentName(ENV);
        tier.setVdc(VDC);
        tier = tierDao.create(tier);
        
        tier=  tierDao.loadTierWithProductReleaseAndMetadata(TIER_NAME+"pr", VDC, ENV);
        assertEquals (tier.getProductReleases().size(),1);   
    }
    
    
    
    
    
    @Test
    public void testCreateNoProduct() throws Exception {
        Tier tier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, null);

        tier = tierDao.create(tier);
        assertNotNull(tier);
        assertNotNull(tier.getId());
        
        tier = tierDao.load(TIER_NAME, VDC, ENV); 
        assertNotNull(tier);

    }
    
    @Test
    public void testSecurityGroup () throws EntityNotFoundException, AlreadyExistsEntityException {
    	SecurityGroup sec = new SecurityGroup("sec2", "description");
        sec.setIdSecurityGroup("idsec");
        int num = securityGroupDao.findAll().size();
        sec = securityGroupDao.create(sec);
        
        assertNotNull (sec);
        assertEquals (sec.getName(), "sec2");
        assertNotNull(sec.getId());
        
        sec = securityGroupDao.load("sec2");
        assertNotNull (sec);
        assertEquals (sec.getName(), "sec2");
        assertEquals (sec.getIdSecurityGroup(), "idsec");
        
        assertEquals (securityGroupDao.findAll().size(), num +1);
    	
    }
    
    @Test
    public void testUpdateSecurityGroup () throws Exception {
    	SecurityGroup sec = new SecurityGroup("sec3", "description");
        sec.setIdSecurityGroup("idsec");
        sec = securityGroupDao.create(sec);
        
        securityGroupDao.updateSecurityGroupId("idsec2", sec);        
        sec = securityGroupDao.load("sec3");
        assertEquals (sec.getIdSecurityGroup(), "idsec2");
    	
    }
    
  /*  @Test
    public void testFindRegionBySecurityGroup() throws Exception {
        Tier tier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, null);
        SecurityGroup sec = new SecurityGroup("sec", "description");
        sec.setIdSecurityGroup("idsec");
        sec = securityGroupDao.create(sec);
        tier.setSecurityGroup(sec);
        tier.setRegion("region");
        tier = tierDao.create(tier);
        
        assertEquals(tier.getSecurityGroup().getIdSecurityGroup(), "idsec");
        String region = tierDao.findRegionBySecurityGroup(sec.getIdSecurityGroup()); 
        assertNotNull(tier);
        assertEquals (region, "region");
    }*/
    
    @Test
    public void testFindByCriteria () throws EntityNotFoundException, AlreadyExistsEntityException {
    	Tier tier = new Tier(TIER_NAME+"find", MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, null);
        tier.setVdc(VDC);
        tier.setEnviromentName(ENV);
        tierDao.create(tier);
        
        TierSearchCriteria criteria = new TierSearchCriteria ();
        criteria.setEnvironmentName(ENV);
        criteria.setName(TIER_NAME+"find");
        criteria.setVdc(VDC);
        List<Tier> tiers = tierDao.findByCriteria(criteria);
        assertNotNull(tiers); 
        assertEquals (tiers.size(),1);
    	
    }

    
    
    
    @Test
    public void testCreateVdcNull() throws Exception {

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);

        Tier tier = new Tier(TIER_NAME+"null", MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc("");
        tier.setEnviromentName(ENV);

        tier = tierDao.create(tier);
        assertNotNull(tier);
        
        tier = tierDao.load(TIER_NAME+"null", null, ENV); 
        assertNotNull(tier); 
    }

    /**
     * Test the create and load method
     */
    @Test
    public void testCreateLoad() throws Exception {

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);

        Tier tier = new Tier(TIER_NAME+2, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc(VDC);
        tier.setEnviromentName(ENV);

        tier = tierDao.create(tier);

        tier = tierDao.load(TIER_NAME+2, VDC, ENV); 
        assertNotNull(tier); 
        assertNotNull(tier.getName(), TIER_NAME+2); 
        assertNotNull(tier.getVdc(), VDC);
        

    }

    /**
     * Test the update
     */
    @Test
    public void testUpdate() throws Exception {

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);

        Tier tier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc(VDC);
        tier.setEnviromentName(ENV);
        tier.setFloatingip("false");

        tier = tierDao.create(tier);
        tier.setFloatingip("true");
        tier = tierDao.update(tier);
        assertNotNull(tier);
        assertNotNull(tier.getName(), TIER_NAME);
        assertNotNull(tier.getFloatingip(), "true");

    }

    /**
     * Test the create and load method
     */
    @Test
    public void testDeleted() throws Exception {

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);

        Tier tier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc(VDC);
        tier.setEnviromentName(ENV);

        tier = tierDao.create(tier);
        tierDao.remove(tier);
    }
    
    /**
     * Test the loas tier with networks
     */
    @Test
    public void testLoadTierWithNetworks() throws Exception {

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);
        
        Set<Network> networks = new HashSet<Network>();
        Network network = new Network(NETWORK_NAME, "VDC", REGION);
        network = networkDao.create(network);
        networks.add(network);
        Network network2 = new Network(NETWORK_NAME+2, "VDC", REGION);
        network2 = networkDao.create(network2);
        networks.add(network2);

        Tier tier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc(VDC);
        tier.setNetworks(networks);
        tier.setEnviromentName(ENV);

        tier = tierDao.create(tier);
        
        Tier tier3 = tierDao.load(TIER_NAME, VDC, ENV);
        assertNotNull(tier3);
        assertEquals(tier3.getName(), TIER_NAME);
        assertEquals(tier3.getNetworks().size(), 2);
        
        
        Tier tier2 = tierDao.loadTierWithNetworks(TIER_NAME, VDC, ENV);
        assertNotNull(tier2);
        assertEquals(tier2.getName(), TIER_NAME);
        assertEquals(tier2.getNetworks().size(), 2);
    }
    
    @Test
    public void testFindAllWithNetwork () throws Exception {
    	ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
    	List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        prodRelease = productReleaseDao.create(prodRelease);
        productReleases.add(prodRelease);
    	String net ="NETFIND";
    	Network network = new Network(net, "VDC", REGION);
        network = networkDao.create(network);
        
        assertEquals(tierDao.findAllWithNetwork(net).size(),0);
        
        Tier tier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc(VDC);
        tier.addNetwork(network);
        tierDao.create(tier);
        
        assertEquals(tierDao.findAllWithNetwork(net).size(),1);
        
        tier = new Tier(TIER_NAME+2, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc(VDC);
        tier.addNetwork(network);
        tierDao.create(tier);
        
        assertEquals(tierDao.findAllWithNetwork(net).size(),2);
        
        tier = new Tier(TIER_NAME+3, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, productReleases);
        tier.setVdc(VDC);
        tierDao.create(tier);
        
        assertEquals(tierDao.findAllWithNetwork(net).size(),2);
    }
    
    @Test
    public void testLoadComplete () throws Exception {
    	ProductRelease prodRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        prodRelease = productReleaseDao.create(prodRelease);

        Tier newTier = new Tier(TIER_NAME, MAXIMUM_INSTANCES, MINIMUM_INSTANCES, INITIAL_INSTANCES, null);
        newTier.setVdc(VDC);
        newTier.addProductRelease(prodRelease);
        tierDao.create(newTier);

       
        Tier tier = tierDao.loadComplete(newTier);
        assertNotNull (tier);
        assertNotNull (tier.getProductReleases());

    }



}

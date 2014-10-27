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

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import java.util.Set;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.TierDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.manager.impl.TierManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Metadata;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author jesus.movilla
 */

public class TierManagerImplTest {

    private TierManagerImpl tierManager;
    private TierDao tierDao;
    private ProductReleaseManager productReleaseManager;
    private SecurityGroupManager securityGroupManager;
    private ProductRelease productRelease;
    private NetworkManager networkManager;
    private SystemPropertiesProvider systemPropertiesProvider;
    private Tier tier;

    private List<ProductRelease> productReleases;

    private ClaudiaData data;

    public static String NETWORK_NAME = "NETWORK_NAME";
    public static String TIER_NAME = "TIER_NAME";
    public static String VDC = "VDC";
    public static String ENV = "ENV";
    public static String REGION = "region";


    @Before
    public void setUp() throws Exception {

        tierManager = new TierManagerImpl();
        tierDao = mock(TierDao.class);
        productReleaseManager = mock(ProductReleaseManager.class);
        securityGroupManager = mock(SecurityGroupManager.class);
        networkManager = mock(NetworkManager.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        tierManager.setProductReleaseManager(productReleaseManager);
        tierManager.setSecurityGroupManager(securityGroupManager);
        tierManager.setSystemPropertiesProvider(systemPropertiesProvider);
        tierManager.setTierDao(tierDao);
        tierManager.setNetworkManager(networkManager);

        productRelease = new ProductRelease("product", "2.0");

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        tier = new Tier();
        tier.setInitialNumberInstances(new Integer(1));
        tier.setMaximumNumberInstances(new Integer(5));
        tier.setMinimumNumberInstances(new Integer(1));
        tier.setName("tierName");
        tier.setProductReleases(productReleases);

        when(productReleaseManager.load(any(String.class),any(ClaudiaData.class))).thenReturn(productRelease);

        data = new ClaudiaData("dd", "dd", "dd");
        List<? extends GrantedAuthority> authorities = new ArrayList();
        PaasManagerUser user = new PaasManagerUser("user", "pass", authorities);
        data.setUser(user);
        user.setToken("token");

    }

    /** 
     * It creates a security group for a metadata restriction including just tcp rules
     * @throws EntityNotFoundException
     */
    @Test
    public void testcreateSecurityGroupTcp() throws EntityNotFoundException {
        productRelease = new ProductRelease("product", "2.0");
        productRelease.addMetadata(new Metadata("open_ports", "8080"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);
        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        when(productReleaseManager.loadWithMetadata(any(String.class))).thenReturn(productRelease);

        SecurityGroup securityGroup = tierManager.generateSecurityGroup(data, tier);
        assertEquals(securityGroup.getName(), "sg_dd_dd_" + tier.getName());
        assertEquals(securityGroup.getRules().size(), 2);
    }
    
    /** 
     * It creates a security group for a metadata restriction including tcp and upd rules
     * @throws EntityNotFoundException
     */
    @Test
    public void testcreateSecurityGroupTcpUdp() throws EntityNotFoundException {
        productRelease = new ProductRelease("product", "2.0");
        productRelease.addMetadata(new Metadata("open_ports", "8080"));
        productRelease.addMetadata(new Metadata("open_ports_udp", "1212"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);
        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        when(productReleaseManager.loadWithMetadata(any(String.class))).thenReturn(productRelease);

        SecurityGroup securityGroup = tierManager.generateSecurityGroup(data, tier);
        assertEquals(securityGroup.getName(), "sg_dd_dd_" + tier.getName());
        assertEquals(securityGroup.getRules().size(), 3);
    }
    
    /** 
     * It creates a security group for a metadata restriction including just upd rules
     * @throws EntityNotFoundException
     */
    @Test
    public void testcreateSecurityGroupUdp() throws EntityNotFoundException {
        productRelease = new ProductRelease("product", "2.0");
        productRelease.addMetadata(new Metadata("open_ports_udp", "1212"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);
        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        when(productReleaseManager.loadWithMetadata(any(String.class))).thenReturn(productRelease);

        SecurityGroup securityGroup = tierManager.generateSecurityGroup(data, tier);
        assertEquals(securityGroup.getName(), "sg_dd_dd_" + tier.getName());
        assertEquals(securityGroup.getRules().size(), 2);
        assertEquals(securityGroup.getRules().get(0).getIpProtocol(), "TCP");
        assertEquals(securityGroup.getRules().get(1).getIpProtocol(), "UCP");
        assertEquals(securityGroup.getRules().get(1).getFromPort(), "1212");
    }
    
    /** 
     * It creates a security group for a metadata restriction with a port range
     * @throws EntityNotFoundException
     */
    @Test
    public void testcreateSecurityGroupUdpRange() throws EntityNotFoundException {
        productRelease = new ProductRelease("product", "2.0");
        productRelease.addMetadata(new Metadata("open_ports_udp", "1212-2024"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);
        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        when(productReleaseManager.loadWithMetadata(any(String.class))).thenReturn(productRelease);

        SecurityGroup securityGroup = tierManager.generateSecurityGroup(data, tier);
        assertEquals(securityGroup.getName(), "sg_dd_dd_" + tier.getName());
        assertEquals(securityGroup.getRules().size(), 2);
        assertEquals(securityGroup.getRules().get(0).getIpProtocol(), "TCP");
        assertEquals(securityGroup.getRules().get(1).getIpProtocol(), "UCP");
        assertEquals(securityGroup.getRules().get(1).getFromPort(), "1212");
        assertEquals(securityGroup.getRules().get(1).getToPort(), "2024");
    }

    @Test
    public void testcreateSecurityGroupNoAttributes() throws EntityNotFoundException {
        productRelease = new ProductRelease("product", "2.0");
        // productRelease.addAttributeport(new Attribute("puerto", "8080"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);
        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        when(productReleaseManager.loadWithMetadata(any(String.class))).thenReturn(productRelease);
        SecurityGroup securityGroup = tierManager.generateSecurityGroup(data, tier);
        assertEquals(securityGroup.getName(), "sg_dd_dd_" + tier.getName());
        assertEquals(securityGroup.getRules().size(), 1);
    }

    @Test
    public void testTierAddProduct() throws Exception {

        productRelease = new ProductRelease("product", "2.0");
        productRelease.addAttribute(new Attribute("open_ports", "8080 2323"));

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image", "icono",
                "keypair", "floatingip", "payload");

        SecurityGroup securityGroup = new SecurityGroup("nanme", "description");
        securityGroup.addRule(new Rule("ipProtocol", "fromPort", "toPort", "sourceGroup", "cidr"));
        tier.setSecurityGroup(securityGroup);
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");
        when(securityGroupManager.create(anyString(), anyString(), anyString(), any(SecurityGroup.class))).thenReturn(
                securityGroup);
        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));

        tier.addProductRelease(productRelease);
        tierManager.addSecurityGroupToProductRelease(data, tier, productRelease);
        tier.addProductRelease(productRelease);
        tierManager.addSecurityGroupToProductRelease(data, tier, productRelease);
        assertEquals(tier.getSecurityGroup().getRules().size(), 1);
        assertEquals(tier.getProductReleases().size(), 2);

    }

    @Test
    public void testTierAddSecurityGroupToProductRelease() throws Exception {

        productRelease = new ProductRelease("product", "2.0");
        productRelease.addAttribute(new Attribute("open_ports", "8080 2323"));

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image", "icono",
                "keypair", "floatingip", "payload");

        SecurityGroup securityGroup = new SecurityGroup("nanme", "description");
        securityGroup.addRule(new Rule("ipProtocol", "fromPort", "toPort", "sourceGroup", "cidr"));
        tier.setSecurityGroup(securityGroup);
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");
        when(securityGroupManager.create(anyString(), anyString(), anyString(), any(SecurityGroup.class))).thenReturn(
                securityGroup);
        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));

        tierManager.addSecurityGroupToProductRelease(data, tier, productRelease);
        assertEquals(tier.getSecurityGroup().getRules().size(), 1);

    }

    @Test
    public void testTierAllData() throws Exception {

        productRelease = new ProductRelease("product", "2.0");
        productRelease.addAttribute(new Attribute("open_ports", "8080"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");

        SecurityGroup securityGroup = new SecurityGroup("nanme", "description");
        securityGroup.addRule(new Rule("ipProtocol", "fromPort", "toPort", "sourceGroup", "cidr"));
        tier.setSecurityGroup(securityGroup);
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");
        when(securityGroupManager.create(anyString(), anyString(), anyString(), any(SecurityGroup.class))).thenReturn(
                securityGroup);
        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));
        when(productReleaseManager.load(any(String.class), any(String.class))).thenReturn(productRelease);

        Tier tier3 = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image",
                "icono", "keypair", "floatingip", "payload");
        when(tierDao.update(any(Tier.class))).thenReturn(tier);

        Tier tier2 = tierManager.create(data, "env", tier3);
        assertEquals(tier2.getName(), tier.getName());
        assertEquals(tier2.getKeypair(), tier.getKeypair());

    }

    @Test
    public void testTierAllDataSecurityPortNoAttributesInProductRelease() throws Exception {

        // given
        productRelease = new ProductRelease("product", "2.0");

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);
        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases);

        SecurityGroup securityGroup = new SecurityGroup("nanme", "description");
        securityGroup.addRule(new Rule("ipProtocol", "fromPort", "toPort", "sourceGroup", "cidr"));
        tier.setSecurityGroup(securityGroup);
        // when

        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");
        when(securityGroupManager.create(anyString(), anyString(), anyString(), any(SecurityGroup.class))).thenReturn(
                securityGroup);
        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));
        when(productReleaseManager.load(any(String.class),any(ClaudiaData.class))).thenReturn(productRelease);
        Tier tier3 = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image",
                "icono", "keypair", "floatingip", "payload");
        when(tierDao.update(any(Tier.class))).thenReturn(tier);

        Tier tier2 = tierManager.create(data, "env", tier3);
        // then

        assertEquals(tier2.getName(), tier.getName());
        assertEquals(tier2.getKeypair(), tier.getKeypair());

    }

    @Test
    public void testTierNoProductRelease() throws Exception {

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image", "icono",
                "keypair", "floatingip", "payload");

        SecurityGroup securityGroup = new SecurityGroup("nanme", "description");
        securityGroup.addRule(new Rule("ipProtocol", "fromPort", "toPort", "sourceGroup", "cidr"));
        tier.setSecurityGroup(securityGroup);
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");
        when(securityGroupManager.create(anyString(), anyString(), anyString(), any(SecurityGroup.class))).thenReturn(
                securityGroup);
        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));
        Tier tier3 = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image",
                "icono", "keypair", "floatingip", "payload");

        Tier tier2 = tierManager.create(data, "env", tier3);
        assertEquals(tier2.getName(), tier.getName());
        assertEquals(tier2.getKeypair(), tier.getKeypair());

    }

    @Test
    public void testTierNetwork() throws Exception {

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image", "icono",
                "keypair", "floatingip", "payload");
        Network net = new Network("NETWORK_NAME", "vd",REGION);
        tier.addNetwork(net);
        tier.setVdc(VDC);
        tier.setEnviromentName(ENV);

        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");

        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        when(tierDao.loadTierWithNetworks(any(String.class), any(String.class), any(String.class))).thenReturn(tier);
        when(networkManager.exists(any(String.class), any(String.class), any(String.class))).thenReturn(false);
        when(networkManager.create(any(Network.class))).thenReturn(net);
        when(networkManager.load(any(String.class), any(String.class), any(String.class))).thenReturn(net);

        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));

        tierManager.create(data, "env", tier);

        Tier tier2 = tierManager.loadTierWithNetworks("name", VDC, ENV);
        assertEquals(tier2.getName(), tier.getName());
        assertEquals(tier2.getNetworks().size(), 1);

    }

    @Test
    public void testCreateAbstractTierNetwork() throws Exception {

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image", "icono",
                "keypair", "floatingip", "payload");
        Network net = new Network("NETWORK_NAME", "vd",REGION);
        tier.addNetwork(net);
        tier.setVdc(null);
        tier.setEnviromentName(ENV);

        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");

        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        when(tierDao.loadTierWithNetworks(any(String.class), any(String.class), any(String.class))).thenReturn(tier);

        when(networkManager.exists(any(String.class), any(String.class), any(String.class))).thenReturn(false);
        when(networkManager.create(any(Network.class))).thenReturn(net);
        when(networkManager.load(any(String.class), any(String.class), any(String.class))).thenReturn(net);

        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));

        tierManager.create(data, "env", tier);

        Tier tier2 = tierManager.loadTierWithNetworks("name", null, ENV);
        assertEquals(tier2.getName(), tier.getName());
        assertEquals(tier2.getNetworks().size(), 1);

    }

    @Test
    public void testTierAlreadyNetwork() throws Exception {

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), null, "flavour", "image", "icono",
                "keypair", "floatingip", "payload");
        Network net = new Network("NETWORK_NAME", "vdc",REGION);
        tier.addNetwork(net);
        tier.setVdc(VDC);
        tier.setEnviromentName(ENV);

        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");

        when(tierDao.create(any(Tier.class))).thenReturn(tier);
        when(tierDao.loadTierWithNetworks(any(String.class), any(String.class), any(String.class))).thenReturn(tier);
        when(networkManager.exists(any(String.class), any(String.class), any(String.class))).thenReturn(true);
        when(networkManager.load(any(String.class), any(String.class), any(String.class))).thenReturn(net);

        Mockito.doThrow(new EntityNotFoundException(Tier.class, "test", tier)).when(tierDao)
                .load(any(String.class), any(String.class), any(String.class));

        tierManager.create(data, "env", tier);

        Tier tier2 = tierManager.loadTierWithNetworks("name", VDC, ENV);
        assertEquals(tier2.getName(), tier.getName());
        assertEquals(tier2.getNetworks().size(), 1);

    }

    @Test
    public void testTierDeletion() throws Exception {

        productRelease = new ProductRelease("product", "2.0");
        productRelease.addAttribute(new Attribute("open_ports", "8080"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        when(tierDao.loadTierWithNetworks(any(String.class), any(String.class), any(String.class))).thenReturn(tier);

        tierManager.delete(data, tier);

    }
    
    @Test(expected=EntityNotFoundException.class)
    public void testTierDeletionNotFound() throws InvalidEntityException, InfrastructureException, EntityNotFoundException  {

        productRelease = new ProductRelease("product", "2.0");
        productRelease.addAttribute(new Attribute("open_ports", "8080"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        when(tierDao.loadTierWithNetworks(any(String.class), any(String.class), any(String.class))).
          thenThrow(new com.telefonica.euro_iaas.commons.dao.EntityNotFoundException  (Tier.class, "error", tier));

        tierManager.delete(data, tier);
    }
    
    @Test
    public void testTierDeletionTierWithNetsandSecurityGroups() throws EntityNotFoundException, InvalidEntityException, InfrastructureException  {

        productRelease = new ProductRelease("product", "2.0");
        productRelease.addAttribute(new Attribute("open_ports", "8080"));

        productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");
        SecurityGroup securityGroup = new SecurityGroup ("dd", "ddd");
        securityGroup.addRule(new Rule());
        tier.setSecurityGroup(securityGroup);
        
        Network net = new Network("NETWORK_NAME", "vdc",REGION);
        tier.addNetwork(net);
        
        when(tierDao.loadTierWithNetworks(any(String.class), any(String.class), any(String.class))).thenReturn(tier);

        tierManager.delete(data, tier);

    }

    @Test
    public void shouldMergeNewEmptyAttributesWithOld() {
        // given
        TierManagerImpl tierManagerImpl = new TierManagerImpl();
        ProductRelease productRelease1 = new ProductRelease();
        ProductRelease newProductRelease = new ProductRelease();
        Attribute attributes[] = { new Attribute("c", "v"), new Attribute("c2", "v2") };
        Set<Attribute> attributesSet = new HashSet(Arrays.asList(attributes));
        productRelease1.setAttributes(attributesSet);

        // when
        tierManagerImpl.mergeAttributes(productRelease1, newProductRelease);

        // then
        assertEquals("v", newProductRelease.getAttribute("c").getValue());
        assertEquals("v2", newProductRelease.getAttribute("c2").getValue());
    }

    @Test
    public void shouldMergeNewAttributesWithOldWhenExistsAttributesInNewProduct() {
        // given
        TierManagerImpl tierManagerImpl = new TierManagerImpl();
        ProductRelease productRelease1 = new ProductRelease();
        ProductRelease newProductRelease = new ProductRelease();
        Attribute attributes[] = { new Attribute("c", "v"), new Attribute("c2", "v2") };
        Set<Attribute> attributesSet = new HashSet(Arrays.asList(attributes));
        productRelease1.setAttributes(attributesSet);

        Attribute attributes2[] = { new Attribute("c3", "v3"), new Attribute("c4", "v4") };
        Set<Attribute> attributesSet2 = new HashSet(Arrays.asList(attributes2));
        newProductRelease.setAttributes(attributesSet2);

        // when
        tierManagerImpl.mergeAttributes(productRelease1, newProductRelease);

        // then
        assertEquals("v", newProductRelease.getAttribute("c").getValue());
        assertEquals("v2", newProductRelease.getAttribute("c2").getValue());
        assertEquals("v3", newProductRelease.getAttribute("c3").getValue());
        assertEquals("v4", newProductRelease.getAttribute("c4").getValue());
    }

    @Test
    public void shouldMergeNewAttributesWithOldWhenExistsAttributesInNewProductWithSameName() {
        // given
        TierManagerImpl tierManagerImpl = new TierManagerImpl();
        ProductRelease productRelease1 = new ProductRelease();
        ProductRelease newProductRelease = new ProductRelease();
        Attribute attributes[] = { new Attribute("c", "v"), new Attribute("c2", "v2") };
        Set<Attribute> attributesSet = new HashSet(Arrays.asList(attributes));
        productRelease1.setAttributes(attributesSet);

        Attribute attributes2[] = { new Attribute("c", "v3"), new Attribute("c4", "v4") };
        Set<Attribute> attributesSet2 = new HashSet(Arrays.asList(attributes2));
        newProductRelease.setAttributes(attributesSet2);

        // when
        tierManagerImpl.mergeAttributes(productRelease1, newProductRelease);

        // then
        assertEquals("v", newProductRelease.getAttribute("c").getValue());
        assertEquals("v2", newProductRelease.getAttribute("c2").getValue());
        assertEquals("v4", newProductRelease.getAttribute("c4").getValue());
    }

    @Test
    public void shouldMergeNewEmptyMetadataWithOld() {
        // given
        TierManagerImpl tierManagerImpl = new TierManagerImpl();
        ProductRelease productRelease1 = new ProductRelease();
        ProductRelease newProductRelease = new ProductRelease();
        Metadata metadatas[] = { new Metadata("c", "v"), new Metadata("c2", "v2") };
        Set<Metadata> metadatasSet = new HashSet(Arrays.asList(metadatas));
        productRelease1.setMetadatas(metadatasSet);

        // when
        tierManagerImpl.mergeMetadatas(productRelease1, newProductRelease);

        // then
        assertEquals("v", newProductRelease.getMetadata("c").getValue());
        assertEquals("v2", newProductRelease.getMetadata("c2").getValue());
    }

    @Test
    public void shouldMergeNewMetadatasWithOldWhenExistsMetadatasInNewProduct() {
        // given
        TierManagerImpl tierManagerImpl = new TierManagerImpl();
        ProductRelease productRelease1 = new ProductRelease();
        ProductRelease newProductRelease = new ProductRelease();
        Metadata metadatas[] = { new Metadata("c", "v"), new Metadata("c2", "v2") };
        Set<Metadata> metadatasSet = new HashSet(Arrays.asList(metadatas));
        productRelease1.setMetadatas(metadatasSet);

        Metadata metadata2[] = { new Metadata("c3", "v3"), new Metadata("c4", "v4") };
        Set<Metadata> metadatasSet2 = new HashSet(Arrays.asList(metadata2));
        newProductRelease.setMetadatas(metadatasSet2);

        // when
        tierManagerImpl.mergeMetadatas(productRelease1, newProductRelease);

        // then
        assertEquals("v", newProductRelease.getMetadata("c").getValue());
        assertEquals("v2", newProductRelease.getMetadata("c2").getValue());
        assertEquals("v3", newProductRelease.getMetadata("c3").getValue());
        assertEquals("v4", newProductRelease.getMetadata("c4").getValue());
    }

    @Test
    public void shouldMergeNewMetadatasWithOldWhenExistsMetadatasInNewProductWithSameKey() {
        // given
        TierManagerImpl tierManagerImpl = new TierManagerImpl();
        ProductRelease productRelease1 = new ProductRelease();
        ProductRelease newProductRelease = new ProductRelease();
        Metadata metadatas[] = { new Metadata("c", "v"), new Metadata("c2", "v2") };
        Set<Metadata> metadatasSet = new HashSet(Arrays.asList(metadatas));
        productRelease1.setMetadatas(metadatasSet);

        Metadata metadatas2[] = { new Metadata("c", "v3"), new Metadata("c4", "v4") };
        Set<Metadata> metadatasSet2 = new HashSet(Arrays.asList(metadatas2));
        newProductRelease.setMetadatas(metadatasSet2);

        // when
        tierManagerImpl.mergeMetadatas(productRelease1, newProductRelease);

        // then
        assertEquals("v", newProductRelease.getMetadata("c").getValue());
        assertEquals("v2", newProductRelease.getMetadata("c2").getValue());
        assertEquals("v4", newProductRelease.getMetadata("c4").getValue());
    }

}

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

package com.telefonica.euro_iaas.paasmanager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

public class OpenStackUtilImplTest {

    private OpenStackUtilImplTestable openStackUtil;
    private OpenStackConfigUtil openStackConfig;
    private SystemPropertiesProvider systemPropertiesProvider;
    private CloseableHttpClient closeableHttpClientMock;
    private StatusLine statusLine;
    private CloseableHttpResponse httpResponse;
    private PaasManagerUser paasManagerUser;
    private OpenOperationUtil openOperationUtil;

    private OpenStackRegion openStackRegion;
    final int TWICE = 2;
    final int SEVEN_TIMES = 7;
    final int FOUR_TIMES = 4;
    private static int http_code_accepted = 202;
    /**
     * HTTP code for accepted requests.
     */
    private static int http_code_ok = 200;
    /**
     * HTTP code for created requests.
     */
    private static int http_code_created = 201;
    /**
     * HTTP code for no content response.
     */
    private static int http_code_deleted = 204;

    String CONTENT_NETWORKS = "{ " + "\"networks\": [ " + "{ " + "\"status\": \"ACTIVE\", " + "\"subnets\": [ "
            + "\"81f10269-e0a2-46b0-9583-2c83aa4cc76f\" " + " ], " + "\"name\": \"jesuspg-net\", "
            + "\"provider:physical_network\": null, " + "\"admin_state_up\": true, "
            + "\"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\", " + "\"router:external\": false, "
            + "\"shared\": false, " + "\"id\": \"047e6dd3-3101-434e-af1e-eea571ab57a4\", "
            + "\"provider:segmentation_id\": 29 " + "}, " + "{ " + "\"status\": \"ACTIVE\", " + "\"subnets\": [ "
            + "\"e2d10e6b-33c3-400c-88d6-f905d4cd02f2\" " + " ], " + "\"name\": \"ext-net\", "
            + "\"provider:physical_network\": null, " + "\"admin_state_up\": true, "
            + "\"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\", " + "\"router:external\": true, "
            + "\"shared\": false, " + "\"id\": \"080b5f2a-668f-45e0-be23-361c3a7d11d0\", "
            + "\"provider:segmentation_id\": 1 " + "} ]} ";

    String ROUTERS = "{ " + "\"routers\": [ {" + "\"status\": \"ACTIVE\", " + " \"external_gateway_info\": { "
            + " \"network_id\": \"080b5f2a-668f-45e0-be23-361c3a7d11d0\" " + " }, " + " \"name\": \"test-rt1\", "
            + "\"admin_state_up\": true, " + "\"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\", "
            + "\"routes\": [], " + "\"id\": \"5af6238b-0e9c-4c20-8981-6e4db6de2e17\"" + "} ]} ";

    @Before
    public void setUp() throws OpenStackException, ClientProtocolException, IOException {
        openStackUtil = new OpenStackUtilImplTestable();
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        openStackConfig = mock(OpenStackConfigUtil.class);
        openStackUtil.setOpenStackConfigUtil(openStackConfig);
        GrantedAuthority grantedAuthority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = new HashSet();
        authorities.add(grantedAuthority);
        paasManagerUser = new PaasManagerUser("user", "aa", authorities);
        paasManagerUser.setToken("1234567891234567989");
        paasManagerUser.setTenantId("08bed031f6c54c9d9b35b42aa06b51c0");

        HttpClientConnectionManager httpClientConnectionManager = mock(HttpClientConnectionManager.class);
        openStackUtil.setConnectionManager(httpClientConnectionManager);

        httpResponse = mock(CloseableHttpResponse.class);
        statusLine = mock(StatusLine.class);
        openOperationUtil = mock(OpenOperationUtil.class);
        closeableHttpClientMock = mock(CloseableHttpClient.class);
        openStackRegion = mock(OpenStackRegion.class);
        openStackUtil.setOpenStackRegion(openStackRegion);
        openStackUtil.setOpenOperationUtil(openOperationUtil);

        String responseJSON = "{\"access\": {\"token\": {\"issued_at\": \"2014-01-13T14:00:10.103025\", \"expires\": \"2014-01-14T14:00:09Z\","
                + "\"id\": \"ec3ecab46f0c4830ad2a5837fd0ad0d7\", \"tenant\": { \"description\": null, \"enabled\": true, \"id\": \"08bed031f6c54c9d9b35b42aa06b51c0\","
                + "\"name\": \"admin\" } },         \"serviceCatalog\": []}}}";

        HttpPost httpPost = mock(HttpPost.class);

        when(closeableHttpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);

        when(
                openOperationUtil.createNovaPostRequest(anyString(), anyString(), anyString(), anyString(),
                        anyString(), anyString(), anyString())).thenReturn(httpPost);

        when(openOperationUtil.createQuantumGetRequest(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(httpPost);

    }

    @Test
    public void shouldGetAbsoluteLimitsWithResponse204() throws OpenStackException, IOException {

        // when
        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");

        String response = openStackUtil.getAbsoluteLimits(paasManagerUser, "region");

        // then
        assertNotNull(response);

    }

    @Test
    public void shouldLoadSubNetwork() throws OpenStackException, IOException {

        String region = "RegionOne";
        SubNetworkInstance subNet = new SubNetworkInstance("SUBNET", "vdc", "region", "CIDR");
        subNet.setIdSubNet("ID");

        // when
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");
        String response = openStackUtil.getSubNetworkDetails(subNet.getIdSubNet(), region, "token", "vdc");

        // then
        assertNotNull(response);
        assertEquals("ok", response);
    }

    @Test
    public void shouldDeleteSubNetwork() throws OpenStackException, IOException {

        SubNetworkInstance subNet = new SubNetworkInstance("SUBNET", "vdc", "region", "CIDR");
        subNet.setIdSubNet("ID");

        String region = "RegionOne";

        // when
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");
        openStackUtil.deleteSubNetwork(subNet.getIdSubNet(), region, "token", "vdc");

    }

    @Test
    public void shouldDeleteNetwork() throws OpenStackException, IOException {

        NetworkInstance net = new NetworkInstance("NETWORK", "vdc", "region");
        net.setIdNetwork("ID");

        // when
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");
        openStackUtil.deleteSubNetwork(net.getIdNetwork(), "region", "token", "vdc");

        verify(openOperationUtil).executeNovaRequest(any(HttpUriRequest.class));

    }

    /**
     * It adds a network interface to a public router.
     * 
     * @throws OpenStackException
     * @throws IOException
     */
    @Test
    public void shouldAddNetworkInterfacetoPublicRouter() throws OpenStackException, IOException {
        // given
        NetworkInstance net = new NetworkInstance("NETWORK", "vdc", "region");
        SubNetworkInstance subNet = new SubNetworkInstance("SUBNET", "vdc", "region", "CIDR");
        net.addSubNet(subNet);
        RouterInstance router = new RouterInstance();

        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn(ROUTERS);
        when(openStackConfig.getPublicAdminNetwork(any(PaasManagerUser.class), anyString())).thenReturn("NETWORK");
        when(openStackConfig.getPublicRouter(any(PaasManagerUser.class), anyString(), anyString()))
                .thenReturn("router");

        String response = openStackUtil.addInterfaceToPublicRouter(paasManagerUser, net, "token");

        // then
        assertNotNull(response);
    }

    /**
     * It deletes a network interface to a public router.
     * 
     * @throws OpenStackException
     * @throws IOException
     */
    @Test
    public void shouldDeleteNetworkInterfacetoPublicRouter() throws OpenStackException, IOException {
        // given
        NetworkInstance net = new NetworkInstance("NETWORK", "vdc", "region");
        SubNetworkInstance subNet = new SubNetworkInstance("SUBNET", "vdc", "region", "CIDR");
        net.addSubNet(subNet);
        String region = "RegionOne";
        RouterInstance router = new RouterInstance();

        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn(ROUTERS);
        when(openStackConfig.getPublicAdminNetwork(any(PaasManagerUser.class), anyString())).thenReturn("NETWORK");
        when(openStackConfig.getPublicRouter(any(PaasManagerUser.class), anyString(), anyString()))
                .thenReturn("router");

        String response = openStackUtil.deleteInterfaceToPublicRouter(paasManagerUser, net, region);

        // then
        assertNotNull(response);

    }

    /**
     * It adds a network interface to a public router.
     * 
     * @throws OpenStackException
     * @throws IOException
     */
    @Test
    public void shouldDestroyNetwork() throws OpenStackException, IOException {
        // given
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");

        String response = openStackUtil.deleteNetwork("networkId", "region", "token", "vdc");

        // then
        assertNotNull(response);
    }

    @Test
    public void shouldAllocateFloatingIp() throws OpenStackException, IOException {
        // given
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");

        String response = openStackUtil.allocateFloatingIP("payload", "region", "token", "vdc");

        // then
        assertNotNull(response);
    }

    @Test
    public void shouldDisallocateFloatingIp() throws OpenStackException, IOException {
        // given
        String ipsXML = " <floating_ips> <floating_ip instance_id=\"None\" ip=\"130.206.81.152\" fixed_ip=\"None\" "
                + " id=\"9c215e37-763a-43d9-b3e2-0e1339d9e238\" pool=\"ext-net\" />"
                + " <floating_ip instance_id=\"4b4aa9a8-67e1-4880-86ec-d4b05b45b3db\" ip=\"130.206.81.148\" fixed_ip=\"11.0.0.6\" "
                + " id=\"b0795d3d-d0b6-4568-8328-9961ac1a14c0\" pool=\"ext-net\" />  </floating_ips>";
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn(ipsXML);

        openStackUtil.disAllocateFloatingIP("region", "token", "vdc", "130.206.81.152");

    }

    /**
     * It adds a network interface to a public router.
     * 
     * @throws OpenStackException
     * @throws IOException
     */
    @Test
    public void shouldListNetworks() throws OpenStackException, IOException {

        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");
        String response = openStackUtil.listNetworks(paasManagerUser, "region");

        // then
        assertNotNull(response);
    }

    /**
     * It adds a network interface to a public router.
     * 
     * @throws OpenStackException
     * @throws IOException
     */
    @Test
    public void shouldListPorts() throws OpenStackException, IOException {
        // given
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn("ok");
        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);

        String response = openStackUtil.listPorts(paasManagerUser, "region");

        // then
        assertNotNull(response);

    }

    @Test(expected = OpenStackException.class)
    public void testShouldDeployVMError() throws OpenStackException, ClientProtocolException, IOException {
        // given
        String payload = "";
        String content = "<badRequest code=\"400\" xmlns=\"http://docs.openstack.org/compute/api/v1.1\">"
                + "<message>Invalid key_name provided.</message></badRequest>";

        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn(content);
        String response = openStackUtil.createServer(payload, "region", "token", "vdc");

        // then
        assertNotNull(response);
    }

    /**
     * OpenStackUtilImplTestable.
     * 
     * @author jesus
     */
    private class OpenStackUtilImplTestable extends OpenStackUtilImpl {

        public CloseableHttpClient getHttpClient() {

            return closeableHttpClientMock;
        }
    }
}

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
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

public class OpenStackConfigUtilImplTest {

    private OpenStackConfigUtilImplTestable openStackUtil;
    private CloseableHttpClient closeableHttpClientMock;
    private CloseableHttpResponse httpResponse;
    private PaasManagerUser paasManagerUser;
    private OpenOperationUtil openOperationUtil;

    private OpenStackRegion openStackRegion;

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

    String ROUTER = " { " + "\"routers\": [ {" + "\"status\": \"ACTIVE\", " + " \"external_gateway_info\": { "
            + " \"network_id\": \"080b5f2a-668f-45e0-be23-361c3a7d11d0\" " + " }, " + " \"name\": \"test-rt1\", "
            + "\"admin_state_up\": true, " + "\"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\", "
            + "\"routes\": [], " + "\"id\": \"5af6238b-0e9c-4c20-8981-6e4db6de2e17\"" + "}  ]}";

    String ROUTERS = " {\"routers\": [{\"status\": \"ACTIVE\", \"external_gateway_info\": null, \"name\": \"router\", \"admin_state_up\": true,"
            + " \"tenant_id\": \"f8b9284b4a5f4875b591d22185ba835c\", \"routes\": [], \"id\": \"084d97ec-a348-4907-94d4-95e339b1cdd4\"}, {\"status\": \"ACTIVE\", "
            + " \"external_gateway_info\": null, \"name\": \"test\", \"admin_state_up\": true, \"tenant_id\": \"f8b9284b4a5f4875b591d22185ba835c\", \"routes\": [], "
            + " \"id\": \"46a97147-27ed-4ee1-b88e-b74a5a831706\"}, {\"status\": \"ACTIVE\", \"external_gateway_info\": {\"network_id\": "
            + "\"080b5f2a-668f-45e0-be23-361c3a7d11d0\"}, \"name\": \"test-rt1\", \"admin_state_up\": true, \"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\","
            + " \"routes\": [], \"id\": \"5af6238b-0e9c-4c20-8981-6e4db6de2e17\"}, {\"status\": \"ACTIVE\", \"external_gateway_info\": {\"network_id\": "
            + " \"080b5f2a-668f-45e0-be23-361c3a7d11d0\"}, \"name\": \"prueba\", \"admin_state_up\": true, \"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\", "
            + " \"routes\": [], \"id\": \"89c6eca5-99d5-41bd-b6c6-deb8d03820ac\"}]} ";

    @Before
    public void setUp() throws OpenStackException, ClientProtocolException, IOException {
        openStackUtil = new OpenStackConfigUtilImplTestable();

        GrantedAuthority grantedAuthority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = new HashSet();
        authorities.add(grantedAuthority);
        paasManagerUser = new PaasManagerUser("user", "aa", authorities);
        paasManagerUser.setToken("1234567891234567989");
        paasManagerUser.setTenantId("08bed031f6c54c9d9b35b42aa06b51c0");
        paasManagerUser.setUsername("08bed031f6c54c9d9b35b42aa06b51c0");

        HttpClientConnectionManager httpClientConnectionManager = mock(HttpClientConnectionManager.class);
        openStackUtil.setConnectionManager(httpClientConnectionManager);

        httpResponse = mock(CloseableHttpResponse.class);

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

    /**
     * It deletes a network interface to a public router.
     * 
     * @throws OpenStackException
     * @throws IOException
     */
    @Test
    public void shouldObtainDefaultNetwork() throws OpenStackException, IOException {
        // given
        String region = "RegionOne";

        // when
        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn(CONTENT_NETWORKS);

        String net = openStackUtil.getPublicAdminNetwork(paasManagerUser, region);

        // then
        assertNotNull(net);
        assertEquals(net, "080b5f2a-668f-45e0-be23-361c3a7d11d0");
    }

    @Test
    public void shouldObtainDefaultPool() throws OpenStackException, IOException {
        // given
        String region = "RegionOne";

        // when
        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn(CONTENT_NETWORKS);

        String net = openStackUtil.getPublicFloatingPool(paasManagerUser, region);

        // then
        assertNotNull(net);
        assertEquals(net, "ext-net");

    }

    @Test
    public void shouldObtainPublicRouter() throws OpenStackException, IOException {
        // given
        String region = "RegionOne";

        // when
        when(openOperationUtil.getAdminUser(any(PaasManagerUser.class))).thenReturn(paasManagerUser);
        when(openOperationUtil.executeNovaRequest(any(HttpUriRequest.class))).thenReturn(ROUTERS);

        String router = openStackUtil.getPublicRouter(paasManagerUser, region, "080b5f2a-668f-45e0-be23-361c3a7d11d0");

        // then
        assertNotNull(router);

    }

    @Test
    public void shouldGetPublicRouterId() throws JSONException {
        String response = "{\"routers\": [{\"status\": \"ACTIVE\", \"external_gateway_info\": null, \"name\": \"router-1137229409\", \"admin_state_up\": false, "
                + "\"tenant_id\": \"00000000000000000000000000000017\", \"routes\": [], \"id\": \"2fe38e4d-a4cb-4c0a-b1b9-e87e0d147f9c\"}, {\"status\": \"ACTIVE\", "
                + "\"external_gateway_info\": {\"network_id\": \"e5892de7-38f9-4002-90f9-eedf0e72f5fc\", \"enable_snat\": true}, \"name\": \"ext-rt\", \"admin_state_up\": "
                + " true, \"tenant_id\": \"00000000000000000000000000000001\", \"routes\": [], \"id\": \"35da5189-03f8-4167-868d-932637d83105\"}]}";

        String routerId = openStackUtil.getPublicRouterId(response, "00000000000000000000000000000001",
                "e5892de7-38f9-4002-90f9-eedf0e72f5fc");
        assertEquals(routerId, "35da5189-03f8-4167-868d-932637d83105");

    }

    /**
     * OpenStackUtilImplTestable.
     * 
     * @author jesus
     */
    private class OpenStackConfigUtilImplTestable extends OpenStackConfigUtilImpl {

        public CloseableHttpClient getHttpClient() {

            return closeableHttpClientMock;
        }
    }
}

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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

public class OperationUtilImplTest {

    private SystemPropertiesProvider systemPropertiesProvider;
    private CloseableHttpClient closeableHttpClientMock;
    private StatusLine statusLine;
    private CloseableHttpResponse httpResponse;
    private PaasManagerUser paasManagerUser;
    private OperationUtilImplTestable openOperationUtil;

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

    @Before
    public void setUp() throws OpenStackException, ClientProtocolException, IOException {
        openOperationUtil = new OperationUtilImplTestable();
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        openStackRegion = mock(OpenStackRegion.class);
        openOperationUtil.setOpenStackRegion(openStackRegion);
        HttpClientConnectionManager httpClientConnectionManager = mock(HttpClientConnectionManager.class);
        openOperationUtil.setConnectionManager(httpClientConnectionManager);
        openOperationUtil.setSystemPropertiesProvider(systemPropertiesProvider);

        GrantedAuthority grantedAuthority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = new HashSet();
        authorities.add(grantedAuthority);
        paasManagerUser = new PaasManagerUser("user", "aa", authorities);
        paasManagerUser.setToken("1234567891234567989");
        paasManagerUser.setTenantId("08bed031f6c54c9d9b35b42aa06b51c0");

        httpResponse = mock(CloseableHttpResponse.class);
        statusLine = mock(StatusLine.class);
        closeableHttpClientMock = mock(CloseableHttpClient.class);

        when(openStackRegion.getNovaEndPoint(anyString(), anyString())).thenReturn("http://localhost/v2.0");

    }

    @Test
    public void getAdminUser() throws OpenStackException, IOException {

        // when

        String content = " <?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"
                + "<access xmlns=\"http://docs.openstack.org/identity/api/v2.0\">\n"
                + "<token expires=\"2013-11-06T12:02:42Z\" id=\"e563937547fd447985db4a9567528393\">\n"
                + "<tenant enabled=\"true\" name=\"admin\" id=\"6571e3422ad84f7d828ce2f30373b3d4\">\n"
                + "<description>Default tenant</description>   \n" + "</tenant>   \n" + "</token>   \n"
                + "</access> \n";
        HttpEntity entity = mock(HttpEntity.class);
        Header header = mock(Header.class);

        // when
        Collection<GrantedAuthority> authorities = new HashSet();
        GrantedAuthority grantedAuthority = mock(GrantedAuthority.class);
        authorities.add(grantedAuthority);
        PaasManagerUser user = new PaasManagerUser("user", "aa", authorities);
        user.setToken("1234567891234567989");
        user.setTenantId("08bed031f6c54c9d9b35b42aa06b51c0");

        when(closeableHttpClientMock.execute(any(HttpUriRequest.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(204);
        when(statusLine.getReasonPhrase()).thenReturn("ok");

        when(httpResponse.getEntity()).thenReturn(entity);
        when(httpResponse.getHeaders(any(String.class))).thenReturn(new Header[] { header });
        when(header.getValue()).thenReturn("value");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));
        when(statusLine.getStatusCode()).thenReturn(200);
        when(statusLine.getReasonPhrase()).thenReturn("ok");

        PaasManagerUser response = openOperationUtil.getAdminUser(user);

        // then
        assertNotNull(response);

    }

    @Test
    public void shouldCreateKeystonePostRequest() throws OpenStackException, IOException {

        HttpPost response = openOperationUtil.createKeystonePostRequest();

        // then
        assertNotNull(response);
    }

    @Test
    public void shouldCreateNovaPostRequest() throws OpenStackException, IOException {

        HttpPost response = openOperationUtil.createNovaPostRequest("resource", "payload", "content", "accept",
                "region", "token", "vdc");

        // then
        assertNotNull(response);
    }

    private class OperationUtilImplTestable extends OpenOperationUtilImpl {

        public CloseableHttpClient getHttpClient() {

            return closeableHttpClientMock;
        }
    }
}

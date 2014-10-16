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

package com.telefonica.euro_iaas.paasmanager.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.util.RegionCache;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

public class OpenStackRegionImplTest  {

    final String RESPONSE_JSON_GRIZZLY_TWO_REGIONS = "{\n" + "   \"endpoints_links\":[\n" + "\n" + "   ],\n"
            + "   \"endpoints\":[\n" + "      {\n" + "         \"name\":\"nova\",\n"
            + "         \"adminURL\":\"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"region\":\"regionOne\",\n"
            + "         \"internalURL\":\"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"type\":\"compute\",\n" + "         \"id\":\"34bb28b56ce4434f8fc2b85ca16bbda6\",\n"
            + "         \"publicURL\":\"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"\n"
            + "      },\n" + "      {\n" + "         \"name\":\"quantum\",\n"
            + "         \"adminURL\":\"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"region\":\"regionTwo\",\n"
            + "         \"internalURL\":\"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"type\":\"network\",\n" + "         \"id\":\"06fe0f86353441cdb5b0664fe5abf0ca\",\n"
            + "         \"publicURL\":\"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"\n"
            + "      },\n" + "      {\n" + "         \"name\":\"nova\",\n"
            + "         \"adminURL\":\"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"region\":\"RegionOne\",\n"
            + "         \"internalURL\":\"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"type\":\"compute\",\n" + "         \"id\":\"0a3419563dcd4e02b8cd8c865a3bc2ed\",\n"
            + "         \"publicURL\":\"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"\n"
            + "         },\n" + "      {\n" + "         \"name\":\"federatednetwork\",\n"
            + "         \"adminURL\":\"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"region\":\"RegionOne\",\n"
            + "         \"internalURL\":\"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"type\":\"compute\",\n" + "         \"id\":\"0a3419563dcd4e02b8cd8c865a3bc2ed\",\n"
            + "         \"publicURL\":\"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"\n"
            + "      },\n" + "      {\n" + "         \"name\":\"quantum\",\n"
            + "         \"adminURL\":\"http://130.206.80.58:9696/\",\n" + "         \"region\":\"RegionOne\",\n"
            + "         \"internalURL\":\"http://130.206.80.58:9696/\",\n" + "         \"type\":\"network\",\n"
            + "         \"id\":\"49d2f93d15904ff091f45b7752001057\",\n"
            + "         \"publicURL\":\"http://130.206.80.58:9696/\"\n" + "      },\n" + "      {\n"
            + "         \"name\":\"glance\",\n" + "         \"adminURL\":\"http://130.206.80.58:9292/v2\",\n"
            + "         \"region\":\"RegionOne\",\n" + "         \"internalURL\":\"http://130.206.80.58:9292/v2\",\n"
            + "         \"type\":\"image\",\n" + "         \"id\":\"24b9ab9c337b4ee38548a8f2e73d291b\",\n"
            + "         \"publicURL\":\"http://130.206.80.58:9292/v2\"\n" + "      },\n" + "      {\n"
            + "         \"name\":\"cinder\",\n"
            + "         \"adminURL\":\"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"region\":\"RegionOne\",\n"
            + "         \"internalURL\":\"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\",\n"
            + "         \"type\":\"volume\",\n" + "         \"id\":\"4fd6f49485cc443d91d4e8b12b0518c5\",\n"
            + "         \"publicURL\":\"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\"\n"
            + "      },\n" + "      {\n" + "         \"name\":\"ec2\",\n"
            + "         \"adminURL\":\"http://130.206.80.58:8773/services/Admin\",\n"
            + "         \"region\":\"RegionOne\",\n"
            + "         \"internalURL\":\"http://130.206.80.58:8773/services/Cloud\",\n"
            + "         \"type\":\"ec2\",\n" + "         \"id\":\"0ca7cd26fad842e0b06a29f2c627fa43\",\n"
            + "         \"publicURL\":\"http://130.206.80.58:8773/services/Cloud\"\n" + "      },\n" + "      {\n"
            + "         \"name\":\"keystone\",\n" + "         \"adminURL\":\"http://130.206.80.58:35357/v2.0\",\n"
            + "         \"region\":\"RegionOne\",\n" + "         \"internalURL\":\"http://130.206.80.58:5000/v2.0\",\n"
            + "         \"type\":\"identity\",\n" + "         \"id\":\"43c29c831357416c87f9068d95c73eca\",\n"
            + "         \"publicURL\":\"http://130.206.80.58:5000/v2.0\"\n" + "      }\n" + "   ]\n" + "}";

    final String RESPONSE_ESSEX = "{\n"
            + "   \"access\":{\n"
            + "      \"token\":{\n"
            + "         \"expires\":\"2013-11-29T16:09:19Z\",\n"
            + "         \"id\":\"2ba51416a4274ba8ad06a35dcc0ef83f\",\n"
            + "         \"tenant\":{\n"
            + "            \"description\":\"Demo project\",\n"
            + "            \"enabled\":true,\n"
            + "            \"id\":\"e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "            \"name\":\"demo\"\n"
            + "         }\n"
            + "      },\n"
            + "      \"serviceCatalog\":[\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.112:8080/paasmanager/rest\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.112:8080/paasmanager/rest\",\n"
            + "                  \"publicURL\":\"http://130.206.80.112:8080/paasmanager/rest\"\n"
            + "               }\n"
            + "            ],\n"
            + "            \"endpoints_links\":[\n"
            + "\n"
            + "            ],\n"
            + "            \"type\":\"paas\",\n"
            + "            \"name\":\"paasmanager\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.63:8774/v2/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.63:8774/v2/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"publicURL\":\"http://130.206.80.63:8774/v2/e7fbb1d5dd9d4983bcd2ad28e9c00caf\"\n"
            + "               },\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.64:8774/v2/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"region\":\"RegionTwo\",\n"
            + "                  \"internalURL\":\"http://130.206.80.64:8774/v2/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"publicURL\":\"http://130.206.80.64:8774/v2/e7fbb1d5dd9d4983bcd2ad28e9c00caf\"\n"
            + "               }\n"
            + "            ],\n"
            + "            \"endpoints_links\":[\n"
            + "\n"
            + "            ],\n"
            + "            \"type\":\"compute\",\n"
            + "            \"name\":\"nova\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.63:9292/v1\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.63:9292/v1\",\n"
            + "                  \"publicURL\":\"http://130.206.80.63:9292/v1\"\n"
            + "               }\n"
            + "            ],\n"
            + "            \"endpoints_links\":[\n"
            + "\n"
            + "            ],\n"
            + "            \"type\":\"image\",\n"
            + "            \"name\":\"glance\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.63:8776/v1/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.63:8776/v1/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"publicURL\":\"http://130.206.80.63:8776/v1/e7fbb1d5dd9d4983bcd2ad28e9c00caf\"\n"
            + "               }\n"
            + "            ],\n"
            + "            \"endpoints_links\":[\n"
            + "\n"
            + "            ],\n"
            + "            \"type\":\"volume\",\n"
            + "            \"name\":\"volume\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.63:8773/services/Admin\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.63:8773/services/Cloud\",\n"
            + "                  \"publicURL\":\"http://130.206.80.63:8773/services/Cloud\"\n"
            + "               }\n"
            + "            ],\n"
            + "            \"endpoints_links\":[\n"
            + "\n"
            + "            ],\n"
            + "            \"type\":\"ec2\",\n"
            + "            \"name\":\"ec2\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.41:8080/v2.0/FIWARE/vdc/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.41:8080/v2.0/FIWARE/vdc/e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"publicURL\":\"http://130.206.80.41:8080/v2.0/FIWARE/vdc/e7fbb1d5dd9d4983bcd2ad28e9c00caf\"\n"
            + "               }\n"
            + "            ],\n"
            + "            \"endpoints_links\":[\n"
            + "\n"
            + "            ],\n"
            + "            \"type\":\"sm\",\n"
            + "            \"name\":\"service_manager\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.119:8081/sdc2/rest\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.119:8081/sdc2/rest\",\n"
            + "                  \"publicURL\":\"http://130.206.80.119:8081/sdc2/rest\"\n"
            + "               }\n"
            + "            ],\n"
            + "            \"endpoints_links\":[\n"
            + "\n"
            + "            ],\n"
            + "            \"type\":\"sdc\",\n"
            + "            \"name\":\"sdc\"\n"
            + "         },\n"
            + "         {\n"
            + "            \"endpoints\":[\n"
            + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.63:8080/v1\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.63:8080/v1/AUTH_e7fbb1d5dd9d4983bcd2ad28e9c00caf\",\n"
            + "                  \"publicURL\":\"http://130.206.80.63:8080/v1/AUTH_e7fbb1d5dd9d4983bcd2ad28e9c00caf\"\n"
            + "               }\n" + "            ],\n" + "            \"endpoints_links\":[\n" + "\n"
            + "            ],\n" + "            \"type\":\"object-store\",\n" + "            \"name\":\"swift\"\n"
            + "         },\n" + "         {\n" + "            \"endpoints\":[\n" + "               {\n"
            + "                  \"adminURL\":\"http://130.206.80.63:35357/v2.0\",\n"
            + "                  \"region\":\"RegionOne\",\n"
            + "                  \"internalURL\":\"http://130.206.80.63:5000/v2.0\",\n"
            + "                  \"publicURL\":\"http://130.206.80.63:5000/v2.0\"\n" + "               }\n"
            + "            ],\n" + "            \"endpoints_links\":[\n" + "\n" + "            ],\n"
            + "            \"type\":\"identity\",\n" + "            \"name\":\"keystone\"\n" + "         }\n"
            + "      ],\n" + "      \"user\":{\n" + "         \"username\":\"henar\",\n"
            + "         \"roles_links\":[\n" + "\n" + "         ],\n"
            + "         \"id\":\"080416ca0dea4d5b8c007d3b53ec91a1\",\n" + "         \"roles\":[\n" + "\n"
            + "         ],\n" + "         \"name\":\"henar\"\n" + "      }\n" + "   }\n" + "}";

    SystemPropertiesProvider systemPropertiesProvider;
    Invocation.Builder builder;
    Response clientResponse;
    OpenStackRegionImpl openStackRegion;

    @Before
    public void setUp() {

        RegionCache regionCache = new RegionCache();
        regionCache.clear();
        openStackRegion = new OpenStackRegionImpl();
        Client client = mock(Client.class);
        WebTarget webResource = mock(WebTarget.class);
        builder = mock(Invocation.Builder.class);
        clientResponse = mock(Response.class);
        Response clientResponseAdmin = mock(Response.class);

        // when
        when(webResource.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);

        when(client.target(anyString())).thenReturn(webResource);
        openStackRegion.setClient(client);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);

        String responseJSON = "{\"access\": {\"token\": {\"issued_at\": \"2014-01-13T14:00:10.103025\", \"expires\": \"2014-01-14T14:00:09Z\","
                + "\"id\": \"ec3ecab46f0c4830ad2a5837fd0ad0d7\", \"tenant\": { \"description\": null, \"enabled\": true, \"id\": \"08bed031f6c54c9d9b35b42aa06b51c0\","
                + "\"name\": \"admin\" } },         \"serviceCatalog\": []}}}";

        when(builder.post(Entity.entity(anyString(), MediaType.APPLICATION_JSON))).thenReturn(clientResponseAdmin);
        when(clientResponseAdmin.getStatus()).thenReturn(200);
        when(clientResponseAdmin.readEntity(String.class)).thenReturn(responseJSON);

    }

    @Test
    public void testShouldGetTokenAdmin() throws OpenStackException {
        // given

        String token = "123123232";

        String url = "http://domain.com/v2.0/tokens/" + token + "/endpoints";

        // when
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(
                "http://domain.com/v2.0/");
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_USER)).thenReturn("admin");
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_PASS)).thenReturn("admin");
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_TENANT)).thenReturn("admin");
        String tokenAdmin = openStackRegion.getTokenAdmin();
        // then
        assertNotNull(tokenAdmin);
        assertEquals("ec3ecab46f0c4830ad2a5837fd0ad0d7", tokenAdmin);
    }

    @Test
    public void testShouldGetEndPointsForNovaAndARegionName() throws OpenStackException {
        // given

        String regionName = "RegionOne";
        String token = "123123232";
        String responseJSON = "{\"endpoints_links\": [], \"endpoints\": [{\"name\": \"nova\", \"adminURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionTestbed\", \"internalURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"compute\", \"id\": \"34bb28b56ce4434f8fc2b85ca16bbda6\", \"publicURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"quantum\", \"adminURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionTestbed\", \"internalURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"network\", \"id\": \"06fe0f86353441cdb5b0664fe5abf0ca\", \"publicURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"nova\", \"adminURL\": \"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"compute\", \"id\": \"0a3419563dcd4e02b8cd8c865a3bc2ed\", \"publicURL\": \"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"quantum\", \"adminURL\": \"http://130.206.80.58:9696/\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:9696/\", \"type\": \"network\", \"id\": \"49d2f93d15904ff091f45b7752001057\", \"publicURL\": \"http://130.206.80.58:9696/\"}, {\"name\": \"glance\", \"adminURL\": \"http://130.206.80.58:9292/v2\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:9292/v2\", \"type\": \"image\", \"id\": \"24b9ab9c337b4ee38548a8f2e73d291b\", \"publicURL\": \"http://130.206.80.58:9292/v2\"}, {\"name\": \"cinder\", \"adminURL\": \"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"volume\", \"id\": \"4fd6f49485cc443d91d4e8b12b0518c5\", \"publicURL\": \"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"ec2\", \"adminURL\": \"http://130.206.80.58:8773/services/Admin\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:8773/services/Cloud\", \"type\": \"ec2\", \"id\": \"0ca7cd26fad842e0b06a29f2c627fa43\", \"publicURL\": \"http://130.206.80.58:8773/services/Cloud\"}, {\"name\": \"keystone\", \"adminURL\": \"http://130.206.80.58:35357/v2.0\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:5000/v2.0\", \"type\": \"identity\", \"id\": \"43c29c831357416c87f9068d95c73eca\", \"publicURL\": \"http://130.206.80.58:5000/v2.0\"}]}";

        // when
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(
                "http://domain.com/v2.0/");
        when(builder.get()).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(200);
        when(clientResponse.readEntity(String.class)).thenReturn(responseJSON);

        String resultURL = openStackRegion.getNovaEndPoint(regionName, token);
        // then
        assertNotNull(resultURL);
        assertEquals("http://130.206.80.58:8774/v2/", resultURL);
    }

    @Test
    public void testShouldGetEndPointsForQuantumAndARegionName() throws OpenStackException {
        // given

        String regionName = "RegionOne";
        String token = "123123232";
        String responseJSON = "{\"endpoints_links\": [], \"endpoints\": [{\"name\": \"nova\", \"adminURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionTestbed\", \"internalURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"compute\", \"id\": \"34bb28b56ce4434f8fc2b85ca16bbda6\", \"publicURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"quantum\", \"adminURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionTestbed\", \"internalURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"network\", \"id\": \"06fe0f86353441cdb5b0664fe5abf0ca\", \"publicURL\": \"http://130.206.80.63:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"nova\", \"adminURL\": \"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"compute\", \"id\": \"0a3419563dcd4e02b8cd8c865a3bc2ed\", \"publicURL\": \"http://130.206.80.58:8774/v2/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"quantum\", \"adminURL\": \"http://130.206.80.58:9696/\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:9696/\", \"type\": \"network\", \"id\": \"49d2f93d15904ff091f45b7752001057\", \"publicURL\": \"http://130.206.80.58:9696/\"}, {\"name\": \"glance\", \"adminURL\": \"http://130.206.80.58:9292/v2\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:9292/v2\", \"type\": \"image\", \"id\": \"24b9ab9c337b4ee38548a8f2e73d291b\", \"publicURL\": \"http://130.206.80.58:9292/v2\"}, {\"name\": \"cinder\", \"adminURL\": \"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\", \"type\": \"volume\", \"id\": \"4fd6f49485cc443d91d4e8b12b0518c5\", \"publicURL\": \"http://130.206.80.58:8776/v1/67c979f51c5b4e89b85c1f876bdffe31\"}, {\"name\": \"ec2\", \"adminURL\": \"http://130.206.80.58:8773/services/Admin\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:8773/services/Cloud\", \"type\": \"ec2\", \"id\": \"0ca7cd26fad842e0b06a29f2c627fa43\", \"publicURL\": \"http://130.206.80.58:8773/services/Cloud\"}, {\"name\": \"keystone\", \"adminURL\": \"http://130.206.80.58:35357/v2.0\", \"region\": \"RegionOne\", \"internalURL\": \"http://130.206.80.58:5000/v2.0\", \"type\": \"identity\", \"id\": \"43c29c831357416c87f9068d95c73eca\", \"publicURL\": \"http://130.206.80.58:5000/v2.0\"}]}";
        String url = "http://domain.com/v2.0/tokens/" + token + "/endpoints";

        // when
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(
                "http://domain.com/v2.0/");
        when(builder.get()).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(200);
        when(clientResponse.readEntity(String.class)).thenReturn(responseJSON);

        String resultURL = openStackRegion.getQuantumEndPoint(regionName, token);
        // then
        assertNotNull(resultURL);
        assertEquals("http://130.206.80.58:9696/v2.0/", resultURL);
    }

    @Test
    public void testShouldReturnTwoRegionNames() throws OpenStackException {
        // given

        // when
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(
                "http://domain.com/v2.0/");

        when(builder.get()).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(200);
        when(clientResponse.readEntity(String.class)).thenReturn(RESPONSE_JSON_GRIZZLY_TWO_REGIONS);
        List<String> result = openStackRegion.getRegionNames("token");

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("regionOne", result.get(0));
        assertEquals("RegionOne", result.get(1));
    }

    @Test
    public void testShouldReturnTwoRegionNamesInEssex() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();
        Client client = mock(Client.class);
        openStackRegion.setClient(client);
        SystemPropertiesProvider systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);
        String token = "123456789";
        String responseError = "{\n" + "    \"error\": {\n"
                + "        \"message\": \"The action you have requested has not been implemented.\",\n"
                + "        \"code\": 501,\n" + "        \"title\": null\n" + "    }\n" + "}";

        String url = "http://domain.com/v2.0/tokens/" + token + "/endpoints";

        String urlEssex = "http://domain.com/v2.0/tokens";

        WebTarget webResource = mock(WebTarget.class);
        WebTarget webResourceEssex = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        Invocation.Builder builderEssex = mock(Invocation.Builder.class);

        Response clientResponse = mock(Response.class);
        Response clientResponseEssex = mock(Response.class);

        // when

        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(
                "http://domain.com/v2.0/");
        when(client.target(url)).thenReturn(webResource);
        when(webResource.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.get()).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(501);
        when(clientResponse.readEntity(String.class)).thenReturn(responseError);

        when(client.target(urlEssex)).thenReturn(webResourceEssex);
        when(webResourceEssex.request(MediaType.APPLICATION_JSON)).thenReturn(builderEssex);
        when(builderEssex.accept(MediaType.APPLICATION_JSON)).thenReturn(builderEssex);
        when(builderEssex.header("Content-type", MediaType.APPLICATION_JSON)).thenReturn(builderEssex);
        when(builderEssex.post(Entity.entity(anyString(), MediaType.APPLICATION_JSON))).thenReturn(clientResponseEssex);
        when(clientResponseEssex.getStatus()).thenReturn(200);
        when(clientResponseEssex.readEntity(String.class)).thenReturn(RESPONSE_ESSEX);

        List<String> result = openStackRegion.getRegionNames(token);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("RegionOne", result.get(0));
        assertEquals("RegionTwo", result.get(1));
    }

    @Test
    public void testShouldGetEndPointForNovaAndARegionNameForEssex() throws OpenStackException {

        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();
        Client client = mock(Client.class);
        openStackRegion.setClient(client);
        SystemPropertiesProvider systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);

        String regionName = "RegionOne";
        String token = "123123232";
        String responseError = "{\n" + "    \"error\": {\n"
                + "        \"message\": \"The action you have requested has not been implemented.\",\n"
                + "        \"code\": 501,\n" + "        \"title\": null\n" + "    }\n" + "}";

        String url = "http://domain.com/v2.0/tokens/" + token + "/endpoints";
        String urlEssex = "http://domain.com/v2.0/tokens";

        WebTarget webResource = mock(WebTarget.class);
        WebTarget webResourceEssex = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        Invocation.Builder builderEssex = mock(Invocation.Builder.class);

        Response clientResponse = mock(Response.class);
        Response clientResponseEssex = mock(Response.class);

        // when
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(
                "http://domain.com/v2.0/");
        when(client.target(url)).thenReturn(webResource);
        when(webResource.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.get()).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(501);
        when(clientResponse.readEntity(String.class)).thenReturn(responseError);

        when(client.target(urlEssex)).thenReturn(webResourceEssex);
        when(webResourceEssex.request(MediaType.APPLICATION_JSON)).thenReturn(builderEssex);
        when(builderEssex.accept(MediaType.APPLICATION_JSON)).thenReturn(builderEssex);
        when(builderEssex.header("Content-type", MediaType.APPLICATION_JSON)).thenReturn(builderEssex);
        when(builderEssex.post(Entity.entity(anyString(), MediaType.APPLICATION_JSON))).thenReturn(clientResponseEssex);
        when(clientResponseEssex.getStatus()).thenReturn(200);
        when(clientResponseEssex.readEntity(String.class)).thenReturn(RESPONSE_ESSEX);

        String resultURL = openStackRegion.getNovaEndPoint(regionName, token);
        // then
        assertNotNull(resultURL);
        assertEquals("http://130.206.80.63:8774/v2/", resultURL);

    }

    @Test
    public void testShouldGetEndPointsForNovaAndARegionNameUsingCache() throws OpenStackException {
        // given

        String regionName = "RegionOne";
        String token = "123123232";

        RegionCache regionCache = new RegionCache();
        regionCache.putUrl("RegionOne", "compute", "http://130.206.80.58:8774/v2/12321312312312321");

        // when

        String resultURL = openStackRegion.getNovaEndPoint(regionName, token);
        // then
        assertNotNull(resultURL);
        assertEquals("http://130.206.80.58:8774/v2/", resultURL);
    }

    @Test
    public void testShouldGetDefaultRegion() throws OpenStackException {
        // given

        String token = "123123232";
        // when
        when(builder.get()).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(200);
        when(clientResponse.readEntity(String.class)).thenReturn(RESPONSE_JSON_GRIZZLY_TWO_REGIONS);

        String result = openStackRegion.getDefaultRegion(token);
        // then
        assertNotNull(result);
        assertEquals("regionOne", result);
    }

    @Test
    public void testShouldGetEndPointsForFederatedNetwork() throws OpenStackException {
        // given

        String token = "123123232";
        when(builder.get()).thenReturn(clientResponse);
        when(clientResponse.getStatus()).thenReturn(200);
        when(clientResponse.readEntity(String.class)).thenReturn(RESPONSE_JSON_GRIZZLY_TWO_REGIONS);
        RegionCache regionCache = new RegionCache();
        regionCache.putUrl("regionOne", "federatednetwork", "http://130.206.80.58:8774/v2/12321312312312321");

        // when

        String resultURL = openStackRegion.getFederatedQuantumEndPoint(token);
        // then
        assertNotNull(resultURL);
        assertEquals("http://130.206.80.58:8774/v2/12321312312312321", resultURL);
    }
}

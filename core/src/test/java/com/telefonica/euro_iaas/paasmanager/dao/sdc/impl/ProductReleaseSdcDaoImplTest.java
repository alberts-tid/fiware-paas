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

package com.telefonica.euro_iaas.paasmanager.dao.sdc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.exception.SdcException;
import com.telefonica.euro_iaas.paasmanager.installator.sdc.util.SDCUtil;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author jesus.movilla
 */
public class ProductReleaseSdcDaoImplTest {
    private ProductReleaseSdcDaoImpl productReleaseSdcDaoImpl;
    Invocation.Builder builder;

    @Before
    public void setUp() {
        productReleaseSdcDaoImpl = new ProductReleaseSdcDaoImpl();
        Client client = mock(Client.class);
        SDCUtil sdcUtils = mock(SDCUtil.class);
        SystemPropertiesProvider systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        productReleaseSdcDaoImpl.setSystemPropertiesProvider(systemPropertiesProvider);
        productReleaseSdcDaoImpl.setClient(client);
        productReleaseSdcDaoImpl.setSDCUtil(sdcUtils);
        WebTarget webResource = mock(WebTarget.class);
        builder = mock(Invocation.Builder.class);

        // when
        when(client.target(anyString())).thenReturn(webResource);
        when(webResource.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);

    }

    /**
     * Tests the findAllProducts functionality
     * 
     * @throws SdcException
     */
    @Test
    public void testFindAllProducts() throws SdcException {
        // given

        String jsonProducts = "{\"product\":[{\"name\":\"tomcat\",\"description\":\"tomcat J2EE container\",\"attributes\":{\"key\":\"clave\",\"value\":\"valor\"}},{\"name\":\"nodejs\",\"description\":\"nodejs\"},{\"name\":\"mysql\",\"description\":\"mysql\"},{\"name\":\"git\",\"description\":\"git\"},{\"name\":\"mongodbshard\",\"description\":\"mongodbshard\"},{\"name\":\"mongos\",\"description\":\"mongos\"},{\"name\":\"mongodbconfig\",\"description\":\"mongodbconfig\"},{\"name\":\"contextbroker\",\"description\":\"contextbroker\"},{\"name\":\"postgresql\",\"description\":\"db manager\",\"attributes\":[{\"key\":\"username\",\"value\":\"postgres\",\"description\":\"The administrator usename\"},{\"key\":\"password\",\"value\":\"postgres\",\"description\":\"The administrator password\"}]},{\"name\":\"haproxy\",\"description\":\"balancer\",\"attributes\":[{\"key\":\"key1\",\"value\":\"value1\",\"description\":\"keyvaluedesc1\"},{\"key\":\"key2\",\"value\":\"value2\",\"description\":\"keyvaluedesc2\"},{\"key\":\"sdccoregroupid\",\"value\":\"app_server_role\",\"description\":\"idcoregroup\"}]},{\"name\":\"test\",\"description\":\"test\",\"attributes\":{\"key\":\"clave\",\"value\":\"valor\"}},{\"name\":\"mediawiki\",\"description\":\"MediaWiki Product\",\"attributes\":[{\"key\":\"wikiname\",\"value\":\"Wiki to be shown\",\"description\":\"The name of the wiki\"},{\"key\":\"path\",\"value\":\"/demo\",\"description\":\"The url context to be displayed\"}]}]}";
        // String productReleasesList =
        // "{\"productRelease\":{\"releaseNotes\":\"Tomcat server 6\",\"version\":\"6\",\"product\":{\"name\":\"tomcat\",\"description\":\"tomcat J2EE container\",\"attributes\":{\"key\":\"clave\",\"value\":\"valor\"}},\"supportedOOSS\":[{\"description\":\"Ubuntu 10.04\",\"name\":\"Ubuntu\",\"osType\":\"94\",\"version\":\"10.04\"},{\"description\":\"Debian 5\",\"name\":\"Debian\",\"osType\":\"95\",\"version\":\"5\"},{\"description\":\"Centos 2.9\",\"name\":\"Centos\",\"osType\":\"76\",\"version\":\"2.9\"}]}}\"";
        InputStream inputStream = IOUtils.toInputStream(jsonProducts);

        when(builder.get(InputStream.class)).thenReturn(inputStream);

        List<String> products = productReleaseSdcDaoImpl.findAllProducts("token", "tenant");

        // then
        assertNotNull(products);

    }

    /**
     * Tests the findAllProducts functionality
     * 
     * @throws SdcException
     */
    @Test
    public void testFindAllProductReleasesOfaProduct() throws SdcException {
        // given

        String productReleasesList = "{\"productRelease\":{\"releaseNotes\":\"Tomcat server 6\",\"version\":\"6\",\"product\":{\"name\":\"tomcat\",\"description\":\"tomcat J2EE container\",\"attributes\":{\"key\":\"clave\",\"value\":\"valor\"}},\"supportedOOSS\":[{\"description\":\"Ubuntu 10.04\",\"name\":\"Ubuntu\",\"osType\":\"94\",\"version\":\"10.04\"},{\"description\":\"Debian 5\",\"name\":\"Debian\",\"osType\":\"95\",\"version\":\"5\"},{\"description\":\"Centos 2.9\",\"name\":\"Centos\",\"osType\":\"76\",\"version\":\"2.9\"}]}}";
        InputStream inputStream = IOUtils.toInputStream(productReleasesList);

        when(builder.get(InputStream.class)).thenReturn(inputStream);

        List<ProductRelease> productReleases = productReleaseSdcDaoImpl.findAllProductReleasesOfProduct("tomcat",
                "token", "tenant");

        // then
        assertNotNull(productReleases);
        assertEquals(1, productReleases.size());

    }

    @Test
    public void testFromStringToProductReleasesTwoProductReleases() throws Exception {
        // given

        String twoProductReleaseString = "{\"productRelease\":[{" + "\"version\":\"0.8.1\"," + "\"product\":{"
                + "\"name\":\"orion\"," + "\"description\":\"\"," + "\"attributes\":["
                + "{\"key\":\"openports\",\"value\":\"1026 27017 27018 27019 28017\"},"
                + "{\"key\":\"dd\",\"value\":\"dd\",\"description\":\"dd\"}" + "]," + "\"metadatas\":["
                + "{\"key\":\"image\",\"value\":\"df44f62d-9d66-4dc5-b084-2d6c7bc4cfe4\"},"
                + "{\"key\":\"cookbook_url\",\"value\":\"\"}," + "{\"key\":\"cloud\",\"value\":\"yes\"},"
                + "{\"key\":\"installator\",\"value\":\"chef\"}," + "{\"key\":\"open_ports\",\"value\":\"80 22\"}"
                + "]" + "}}," + "{\"version\":\"0.6.0\"," + "\"product\":{" + "\"name\":\"orion\","
                + "\"description\":\"\"," + "\"attributes\":["
                + "{\"key\":\"openports\",\"value\":\"1026 27017 27018 27019 28017\"},"
                + "{\"key\":\"dd\",\"value\":\"dd\",\"description\":\"dd\"}" + "]," + "\"metadatas\":["
                + "{\"key\":\"image\",\"value\":\"df44f62d-9d66-4dc5-b084-2d6c7bc4cfe4\"},"
                + "{\"key\":\"cookbook_url\",\"value\":\"\"}," + "{\"key\":\"cloud\",\"value\":\"yes\"},"
                + "{\"key\":\"installator\",\"value\":\"chef\"}," + "{\"key\":\"open_ports\",\"value\":\"80 22\"}"
                + "]" + "}}]}";
        // when
        List<ProductRelease> productReleases = productReleaseSdcDaoImpl
                .fromStringToProductReleases(twoProductReleaseString);
        // then
        assertEquals(productReleases.size(), 2);
    }

    @Test
    public void testFromStringToProductReleasesOneProductRelease() throws Exception {
        // given

        String twoProductReleaseString = "{\"productRelease\":{" + "\"version\":\"0.6.0\"," + "\"product\":{"
                + "\"name\":\"orion\"," + "\"description\":\"\"," + "\"attributes\":["
                + "{\"key\":\"openports\",\"value\":\"1026 27017 27018 27019 28017\"},"
                + "{\"key\":\"dd\",\"value\":\"dd\",\"description\":\"dd\"}" + "]," + "\"metadatas\":["
                + "{\"key\":\"image\",\"value\":\"df44f62d-9d66-4dc5-b084-2d6c7bc4cfe4\"},"
                + "{\"key\":\"cookbook_url\",\"value\":\"\"}," + "{\"key\":\"cloud\",\"value\":\"yes\"},"
                + "{\"key\":\"installator\",\"value\":\"chef\"}," + "{\"key\":\"open_ports\",\"value\":\"80 22\"}"
                + "]" + "}}}";
        // when
        List<ProductRelease> productReleases = productReleaseSdcDaoImpl
                .fromStringToProductReleases(twoProductReleaseString);

        // then
        assertEquals(productReleases.size(), 1);

    }

}

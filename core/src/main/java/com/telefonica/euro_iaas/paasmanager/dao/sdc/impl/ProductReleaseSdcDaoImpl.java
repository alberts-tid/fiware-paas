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

import static com.telefonica.euro_iaas.paasmanager.util.Configuration.SDC_SERVER_MEDIATYPE;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.sdc.ProductReleaseSdcDao;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.exception.SdcException;
import com.telefonica.euro_iaas.paasmanager.installator.sdc.util.SDCUtil;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.sdc.client.SDCClient;
import com.telefonica.euro_iaas.sdc.client.exception.ResourceNotFoundException;

/**
 * @author jesus.movilla
 */
public class ProductReleaseSdcDaoImpl implements ProductReleaseSdcDao {

    private SystemPropertiesProvider systemPropertiesProvider;
    private static Logger log = LoggerFactory.getLogger(ProductReleaseSdcDaoImpl.class);
    Client client;
    private SDCUtil sDCUtil;
    private SDCClient sDCClient;

    public List<ProductRelease> findAll(String token, String tenant) throws SdcException {
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();

        List<String> pNames = findAllProducts(token, tenant);

        for (int i = 0; i < pNames.size(); i++) {
            final String pName = pNames.get(i);
            try {

                List<ProductRelease> productReleasesProduct = findAllProductReleasesOfProduct(pName, token, tenant);
                for (int j = 0; j < productReleasesProduct.size(); j++) {
                    productReleases.add(productReleasesProduct.get(j));
                }
            } catch (Exception ex) {
                log.warn("Error loading product from sdc:" + pName);
            }
        }
        return productReleases;
    }

    public ProductRelease load(String product, String version, ClaudiaData data) throws EntityNotFoundException,
            SdcException {
        log.debug("Load by name " + product + " " + version);
        ProductRelease p = null;

        try {
            String url = sDCUtil.getSdcUtil(data.getUser().getToken());
            log.debug("the url: " + url);
            
            com.telefonica.euro_iaas.sdc.client.services.ProductReleaseService pIService = 
            	sDCClient.getProductReleaseService(url, SDC_SERVER_MEDIATYPE);
            com.telefonica.euro_iaas.sdc.model.ProductRelease prod= pIService.load(product, version, data.getUser().getToken(), data.getUser().getTenantId() );
            p = new ProductRelease (prod.getProduct().getName(), prod.getVersion());
        } catch (OpenStackException e) {
            String message = "Error calling SDC to obtain the products " + e.getMessage();
            log.error(message);
            throw new SdcException(message);
        } catch (ResourceNotFoundException e) {
        	 String message = "The Product Release " + product + "-" + version + " is not present in SDC DataBase ";
             log.error(message);
             throw new EntityNotFoundException(ProductRelease.class, "name", "product" + "-" + "version");
		}

        return p;
    }

    public List<String> findAllProducts(String token, String tenant) throws SdcException {
        try {
            String url = sDCUtil.getSdcUtil(token) + "/catalog/product";
            log.debug("url: " + url);

            Invocation.Builder builder = createWebResource(url, token, tenant);
            builder = builder.accept(MediaType.APPLICATION_JSON);
            InputStream inputStream = builder.get(InputStream.class);
            String response;
            response = IOUtils.toString(inputStream);
            return fromSDCToProductNames(response);
        } catch (IOException e) {
            String message = "Error calling SDC to obtain the products ";
            log.error(message);
            throw new SdcException(message);
        } catch (OpenStackException e) {
            String message = "Error calling SDC to obtain the products " + e.getMessage();
            log.error(message);
            throw new SdcException(message);
        }

    }

    public List<ProductRelease> findAllProductReleasesOfProduct(String pName, String token, String tenant)
            throws SdcException {
        try {
            String url = sDCUtil.getSdcUtil(token) + "/catalog/product/" + pName + "/release";
            log.debug("url: " + url);

            Invocation.Builder builder = createWebResource(url, token, tenant);
            builder = builder.accept(MediaType.APPLICATION_JSON);
            InputStream inputStream = builder.get(InputStream.class);
            String response = IOUtils.toString(inputStream);
            return fromSDCToPaasManager(response);
        } catch (IOException e) {
            String message = "Error calling SDC to obtain the products ";
            log.error(message);
            throw new SdcException(message);
        } catch (OpenStackException e) {
            String message = "Error calling SDC to obtain the products " + e.getMessage();
            log.error(message);
            throw new SdcException(message);
        }

    }


    private String loadByName(String product, String version, String token, String tenant)
            throws EntityNotFoundException, SdcException {
        log.debug("Load by name " + product + " " + version);
        Response response;
        try {
            String url = sDCUtil.getSdcUtil(token) + "/catalog/product/" + product + "/release/" + version;
            log.debug("the url: " + url);

            Invocation.Builder builder = createWebResource(url, token, tenant);

            response = builder.get();
        } catch (OpenStackException e) {
            String message = "Error calling SDC to obtain the products " + e.getMessage();
            log.error(message);
            throw new SdcException(message);
        }

        if (response.getStatus() == 404) {
            String message = "The Product Release " + product + "-" + version + " is not present in SDC DataBase ";
            log.error(message);
            throw new EntityNotFoundException(ProductRelease.class, "name", "product" + "-" + "version");
        }

        if (response.getStatus() != 200) {

            String theString = response.readEntity(String.class);

            String message = "Error calling SDC to recover all product Releases. " + "Status " + response.getStatus()
                    + " " + theString;
            throw new SdcException(message);
        }

        return response.readEntity(String.class);
    }


    private List<String> fromSDCToProductNames(String sdcproducts) {

        JSONObject jsonNodeProducts = JSONObject.fromObject(sdcproducts);
        List<String> productNames = new ArrayList<String>();

        JSONArray jsonproductList = jsonNodeProducts.getJSONArray("product");

        for (Object o : jsonproductList) {
            JSONObject jsonProduct = (JSONObject) o;
            String productName = jsonProduct.getString("name");
            productNames.add(productName);
        }
        return productNames;
    }

    private List<ProductRelease> fromSDCToPaasManager(String sdcproductReleases) {
        List<ProductRelease> paasManagerProductReleases = fromStringToProductReleases(sdcproductReleases);
        return paasManagerProductReleases;
    }

    /**
     * Converting from a string (list of secGroups in json) to a list of SecurityGroups.
     * 
     * @return List of ProductReleases
     */
    public List<ProductRelease> fromStringToProductReleases(String sdcproductReleases) {
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        JSONObject json = (JSONObject) JSONSerializer.toJSON(sdcproductReleases);
        Object obj = json.get("productRelease");
        if (obj instanceof JSONObject) {
            JSONObject jsonProductRelease = (JSONObject) obj;
            ProductRelease productRelease = new ProductRelease();
            productRelease.fromSdcJson(jsonProductRelease);
            productReleases.add(productRelease);
        } else {
            JSONArray ja = (JSONArray) obj;
            JSONArray jsonproductReleasesList = ja;
            for (Object o : jsonproductReleasesList) {
                ProductRelease productRelease = new ProductRelease();
                JSONObject jsonProductRelease = (JSONObject) o;
                productRelease.fromSdcJson(jsonProductRelease);
                productReleases.add(productRelease);
            }
        }

        return productReleases;
    }

    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    /**
     * @param client
     *            the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public void setSDCUtil(SDCUtil sDCUtil) {
        this.sDCUtil = sDCUtil;
    }
    
    public void setSDCClient (SDCClient sDCClient) {
    	this.sDCClient=sDCClient;
    }

    private Invocation.Builder createWebResource(String url, String token, String tenant) {
        // client.addFilter(new LoggingFilter(System.out));

        WebTarget webResource = client.target(url);
        Invocation.Builder builder = webResource.request(MediaType.APPLICATION_JSON);

        builder.header("X-Auth-Token", token);
        builder.header("Tenant-Id", tenant);

        return builder;

    }

}

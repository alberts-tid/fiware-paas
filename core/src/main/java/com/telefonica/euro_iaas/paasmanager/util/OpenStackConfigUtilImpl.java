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

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

/**
 * @author jesus.movilla
 */
public class OpenStackConfigUtilImpl implements OpenStackConfigUtil {

    /**
     * The log.
     */

    private static Logger log = LoggerFactory.getLogger(OpenStackConfigUtilImpl.class);

    private HttpClientConnectionManager connectionManager;

    private OpenStackRegion openStackRegion;

    private OpenOperationUtil openOperationUtil;

    /**
     * The constructor.
     */
    public OpenStackConfigUtilImpl() {
        connectionManager = new PoolingHttpClientConnectionManager();
    }

    public HttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(HttpClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * It gets the public admin network
     */
    public String getPublicAdminNetwork(PaasManagerUser user, String region) throws OpenStackException {
        log.debug("Obtain public admin network ");
        String type = "net";
        RegionCache regionCache = new RegionCache();
        String networkId = regionCache.getUrl(region, type);
        if (networkId != null) {
            log.debug("in cache " + networkId);
            return networkId;
        }

        PaasManagerUser adminUser = openOperationUtil.getAdminUser(user);

        log.debug("tenatn id" + adminUser.getTenantId() + " tenant name " + adminUser.getTenantName() + " user name"
                + adminUser.getUserName());
        HttpUriRequest request;
        try {

            request = openOperationUtil.createQuantumGetRequest(RESOURCE_NETWORKS, APPLICATION_JSON, region,
                    adminUser.getToken(), adminUser.getTenantId());
        } catch (OpenStackException e) {
            log.warn("It is not possible to obtain the quantum endpoint for obtaining the public network net");
            return null;

        }

        String response = null;

        try {
            response = openOperationUtil.executeNovaRequest(request);
            JSONObject lNetworkString = new JSONObject(response);
            JSONArray jsonNetworks = lNetworkString.getJSONArray("networks");

            for (int i = 0; i < jsonNetworks.length(); i++) {

                JSONObject jsonNet = jsonNetworks.getJSONObject(i);
                NetworkInstance net = isPublicNetwork(jsonNet, adminUser.getUserName(), region);

                if (net != null) {
                    log.debug("net " + net.getNetworkName() + " " + net.getIdNetwork());
                    regionCache.putUrl(region, type, net.getNetworkName());
                    return net.getIdNetwork();
                }

            }

        } catch (OpenStackException e) {
            String errorMessage = "Error getting networks for obtaining the public network: " + e;
            log.error(errorMessage);
            throw new OpenStackException(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Error getting networks from OpenStack for obtaining the public network: " + e;
            log.error(errorMessage);
            throw new OpenStackException(errorMessage);
        }

        return null;
    }

    public String getPublicFloatingPool(PaasManagerUser user, String region) throws OpenStackException {
        String type = "floating";
        log.debug("Obtain public admin network");
        RegionCache regionCache = new RegionCache();
        String networkId = regionCache.getUrl(region, type);
        if (networkId != null) {
            return networkId;
        }

        PaasManagerUser adminUser = openOperationUtil.getAdminUser(user);
        HttpUriRequest request;

        try {
            request = openOperationUtil.createQuantumGetRequest(RESOURCE_NETWORKS, APPLICATION_JSON, region,
                    adminUser.getToken(), adminUser.getTenantId());
        } catch (OpenStackException e) {
            log.warn("Error to obtain the quantum request url ");
            return "net8300";
        }

        String response = null;

        try {
            response = openOperationUtil.executeNovaRequest(request);
            JSONObject lNetworkString = new JSONObject(response);
            JSONArray jsonNetworks = lNetworkString.getJSONArray("networks");

            for (int i = 0; i < jsonNetworks.length(); i++) {

                JSONObject jsonNet = jsonNetworks.getJSONObject(i);
                NetworkInstance net = isPublicNetwork(jsonNet, adminUser.getUserName(), region);
                if (net != null) {

                    regionCache.putUrl(region, type, net.getNetworkName());
                    return net.getNetworkName();
                }

            }

        } catch (OpenStackException e) {
            String errorMessage = "Error getting networks for obtaining the public network: " + e;
            log.error(errorMessage);
            throw new OpenStackException(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Error getting networks from OpenStack for obtaining the public network: " + e;
            log.error(errorMessage);
            throw new OpenStackException(errorMessage);
        }

        return null;
    }

    /**
     * It gets the public router network
     */
    public String getPublicRouter(PaasManagerUser user, String region, String publicNetworkId)
            throws OpenStackException {
        log.debug("Obtain public router for external netwrk " + publicNetworkId);
        String type = "router";
        RegionCache regionCache = new RegionCache();
        String routerId = regionCache.getUrl(region, type);
        if (routerId != null) {
            return routerId;
        }

        // String publicNetworkId = this.getPublicAdminNetwork(user, region).getIdNetwork();

        PaasManagerUser adminUser = openOperationUtil.getAdminUser(user);

        HttpUriRequest request = openOperationUtil.createQuantumGetRequest(RESOURCE_ROUTERS, APPLICATION_JSON, region,
                adminUser.getToken(), adminUser.getTenantId());

        String response = null;

        try {
            response = openOperationUtil.executeNovaRequest(request);
            routerId = getPublicRouterId(response, adminUser.getUserName(), publicNetworkId);
            if (routerId == null) {
                String errorMessage = "It is not possible to find a public router for network " + publicNetworkId
                        + ": ";
                log.error(errorMessage);
                throw new OpenStackException(errorMessage);
            }
            regionCache.putUrl(region, type, routerId);
            return routerId;

        } catch (OpenStackException e) {
            String errorMessage = "Error getting router for obtaining the public router: " + e;
            log.error(errorMessage);
            throw new OpenStackException(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Error getting router from OpenStack for obtaining the public router: " + e;
            log.error(errorMessage);
            throw new OpenStackException(errorMessage);
        }

    }

    public String getPublicRouterId(String response, String vdc, String publicNetworkId) throws JSONException {
        JSONObject lRouterString = new JSONObject(response);
        JSONArray jsonRouters = lRouterString.getJSONArray("routers");

        for (int i = 0; i < jsonRouters.length(); i++) {

            JSONObject jsonNet = jsonRouters.getJSONObject(i);
            RouterInstance router = isPublicRouter(jsonNet, vdc, publicNetworkId);
            if (router != null) {
                log.debug(router.toString());

                return router.getIdRouter();
            }

        }
        return null;

    }

    private NetworkInstance isPublicNetwork(JSONObject jsonNet, String vdc, String region) {

        NetworkInstance netInst;
        try {
            netInst = NetworkInstance.fromJson(jsonNet, region);
        } catch (JSONException e) {
            log.warn("Error to parser the json for the network");
            return null;
        }

        log.debug("net " + netInst.getNetworkName() + " " + netInst.getIdNetwork() + " " + netInst.getExternal() + " "
                + netInst.getTenantId());

        if (!netInst.getExternal()) {
            log.debug("external " + netInst.getExternal());
            return null;
        }

        if (netInst.getNetworkName().contains("public")) {
            return netInst;
        }

        if (!vdc.contains(netInst.getTenantId())) {
            log.debug("vdc " + vdc + " tenant id " + netInst.getTenantId());
            return null;
        }
        return netInst;
    }

    private RouterInstance isPublicRouter(JSONObject jsonRouter, String vdc, String networkPublic) {

        RouterInstance routerInst;
        try {
            routerInst = RouterInstance.fromJson(jsonRouter);
        } catch (JSONException e) {
            log.warn("Error to parser the json for the router");
            return null;
        }
        log.debug("router " + routerInst.getName() + " " + routerInst.getTenantId() + " " + routerInst.getNetworkId()
                + " " + networkPublic);

        if (!routerInst.getNetworkId().equals(networkPublic)) {
            return null;
        } else {
            return routerInst;
        }

    }

    public OpenStackRegion getOpenStackRegion() {
        return openStackRegion;
    }

    public void setOpenStackRegion(OpenStackRegion openStackRegion) {
        this.openStackRegion = openStackRegion;
    }

    public void setOpenOperationUtil(OpenOperationUtil openOperationUtil) {
        this.openOperationUtil = openOperationUtil;
    }

}

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

package com.telefonica.euro_iaas.paasmanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A router.
 *
 * @author Henar Munoz
 */

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "Router")
public class RouterInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    @XmlTransient
    private Long id;

    private String name;

    private String idRouter;

    private String tenantId;

    private String idPublicNetwork;

    private boolean adminStateUp;

    private String networkId;

    /**
     * Default constructor.
     */
    public RouterInstance() {

    }

    /**
     * Constructor.
     *
     * @param idPublicNetwork
     */
    public RouterInstance(String idPublicNetwork) {
        this.idPublicNetwork = idPublicNetwork;

    }

    /**
     * Constructor.
     *
     * @param idPublicNetwork
     * @param name
     */
    public RouterInstance(String idPublicNetwork, String name) {
        this.name = name;
        this.idPublicNetwork = idPublicNetwork;
    }

    /**
     * @return the id
     */
    public String getIdRouter() {
        return this.idRouter;
    }

    /**
     * @return the networkName
     */
    public String getName() {
        return name;
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Set the tenant id.
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @param id
     */
    public void setIdRouter(String id) {
        this.idRouter = id;
    }

    private void setAdminStateUp(boolean adminStateUp) {
        this.adminStateUp = adminStateUp;

    }

    private boolean getAdminStateUp() {
        return adminStateUp;
    }

    private void setNetworkId(String networkId) {
        this.networkId = networkId;

    }

    public String getNetworkId() {
        return networkId;
    }

    /**
     * @return string with the json representation for the request
     */

    public String toJson() {
        return "{"
                + "    \"router\":"
                + "    {"
                + "        \"name\": \"" + name + "\","
                + "        \"admin_state_up\": true ,"
                + "        \"external_gateway_info\" : {"
                + "             \"network_id\": \"" + idPublicNetwork + "\""
                + "        }"
                + "    }"
                + "}";
    }

    /**
     * Constructor from a json message.
     * @param jsonRouter    The json message.
     * @return
     * @throws JSONException
     */
    public static RouterInstance fromJson(JSONObject jsonRouter) throws JSONException {

        String name = (String) jsonRouter.get("name");
        String id = (String) jsonRouter.get("id");
        boolean adminStateUp = (Boolean) jsonRouter.get("admin_state_up");
        String tenantId = (String) jsonRouter.get("tenant_id");
        String networkId = "";
        try {
            JSONObject array = jsonRouter.getJSONObject("external_gateway_info");
            networkId = (String) array.get("network_id");
        } catch (Exception e) {
            if (!(jsonRouter.get("external_gateway_info") == JSONObject.NULL)) {
                networkId = (String) jsonRouter.get("external_gateway_info");
            }

        }

        RouterInstance router = new RouterInstance();
        router.setIdRouter(id);
        router.setTenantId(tenantId);
        router.setAdminStateUp(adminStateUp);
        router.setNetworkId(networkId);
        router.setName(name);
        return router;
    }

    public void setName(String name2) {
        this.name = name2;

    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[RouterInstance]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[idRouter = ").append(this.idRouter).append("]");
        sb.append("[tenantId = ").append(this.tenantId).append("]");
        sb.append("[idPublicNetwork = ").append(this.idPublicNetwork).append("]");
        sb.append("[adminStateUp = ").append(this.adminStateUp).append("]");
        sb.append("[networkId = ").append(this.networkId).append("]");
        sb.append("]");
        return sb.toString();
    }


}

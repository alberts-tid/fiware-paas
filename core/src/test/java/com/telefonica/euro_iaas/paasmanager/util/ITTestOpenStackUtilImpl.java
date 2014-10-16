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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

/**
 * This is an Integration Test against OpenStack.
 */
public class ITTestOpenStackUtilImpl {
    /**
     * This is the authToken obtained from the keystone. You have to insert a new one everyday.
     */

    private final String authToken = "886334e3b80847699753912043d13b6d"; // valid
    // for
    // 1day

    /**
     * This is the username used for the authentication.
     */
    // private final String user = "admin";
    private final String username = "jesus";

    /**
     * Tenant id from Openstack.
     */
    private final String tenant = "ebe6d9ec7b024361b7a3882c65a57dda"; // tenant
    // name:
    // demo

    /**
     * Image id from Openstack, One of them.
     */
    private final String imageId = "44dcdba3-a75d-46a3-b209-5e9035d2435e";

    /**
     * Flavour Value.
     */
    private final String flavourValue = "2";

    /**
     * KeyPiar Value
     */
    private final String keypair = "jesusmovilla";

    /**
     * KeyPiar Value
     */
    private final String security_group = "testjesus";

    /**
     * Image id from Openstack, One of them.
     */
    private final String imageId4Delete = "b45e2ee9-54e4-4263-91c9-5005fb2ff6de";

    /**
     * Server id from Openstack, One of them.
     */
    private String serverId = null; // "506f5771-0e8e-4fb6-9078-7a20279fb20b";

    /**
     * A JSON action for start a server.
     */

    private final String action1 = "{\"os-start\" : null}";

    /**
     * A JSON action for resize a server.
     */
    private final String action2 = "{\"resize\" : { \"flavorRef\" : \"2\"}}";
    /**
     * A JSON action for confirm Resizing of a server.
     */
    private final String action3 = "{\"confirmResize\" : null}";

    /**
     * A JSON action for stop a server.
     */
    private final String action4 = "{\"os-stop\" : null}";

    /**
     * A JSON action for reboot a server.
     */
    private final String action5 = "{\"reboot\" : { \"type\" : \"SOFT\" } }";

    /**
     * A JSON action for create an image from a server.
     */
    private final String action6 = "{\"createImage\" : { \"name\" : \"testSnapshot\" } }";
    /**
     * Tcloud metadata for Token.
     */
    private final String tokenMetadata = "tcloud_metadata_token";

    /**
     * Tcloud metadata for User.
     */
    private final String userMetadata = "tcloud_metadata_user";

    /**
     * Tcloud metadata for Tenant.
     */
    private final String tenantMetadata = "tcloud_metadata_tenant";

    /**
     * Server Name to be created in CreateServer test.
     */
    private final String serverName = "ITtestServer";
    /**
     * Image ref from Openstack, One of them.
     */
    private final String imageRef = "http://130.206.80.63:8774/v2/" + tenant + "/images/" + imageId;
    /**
     * flavor ref from Openstack, One of them.
     */
    private final String flavorRef = "http://130.206.80.63:8774/v2/" + tenant + "/flavors/1";
    /**
     * FloatingIP to be assigned to a Server
     */
    private String floatingIP = "X.X.X.X";

    /**
     * Name of he floatingIPPool
     */
    // private final String floatingIPPool = "fiprt1";

    OpenStackUtilImpl openStackUtil = null;
    SystemPropertiesProvider systemPropertiesProvider = null;
    PaasManagerUser user = null;
    
  

    /**
     * Build the payload to deploy a VM (createServer)
     */
    private String buildCreateServerPayload() throws OpenStackException {

        if ((imageId == null) || (flavourValue == null) || (keypair == null)) {
            String errorMsg = " The tier does not include a not-null information: " + "Image: " + imageId + "Flavour: "
                    + flavourValue + "KeyPair: " + keypair;
            throw new OpenStackException(errorMsg);
        }

        String payload = "{\"server\": " + "{\"name\": \"" + serverName + "\", " + "\"imageRef\": \"" + imageId
                + "\", " + "\"flavorRef\": \"" + flavourValue + "\", " + "\"key_name\": \"" + keypair + "\"} ";

        if (security_group != null)
            payload = payload + ", \"security_group\": \"" + security_group + "\"}";
        else
            payload += "}";

        return payload;
    }

    /**
     *
     */
    @Before
    public void setup() {
        user = new PaasManagerUser(username, authToken, new HashSet<GrantedAuthority>());
        user.setToken(authToken);
        user.setTenantId(tenant);
        user.setUsername(username);
        OpenOperationUtil openOperationUtil = mock (OpenOperationUtil.class);
        OpenStackConfigUtil openStackConfigUtil = mock (OpenStackConfigUtil.class);
        OpenStackRegion openStackRegion  = mock (OpenStackRegion.class);
        openStackUtil = new OpenStackUtilImpl ();
        openStackUtil.setOpenOperationUtil(openOperationUtil);
        openStackUtil.setOpenStackConfigUtil(openStackConfigUtil);
        openStackUtil.setOpenStackRegion(openStackRegion);

        // http://130.206.80.63:8774/

        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
    }

    // @Ignore
    //@Test
    public void testCreateGetDeleteServer() throws OpenStackException {
        try {
            // Creates a new VM

            String payload = buildCreateServerPayload();

            // Create a VM
            String postResponse = openStackUtil.createServer(payload, "region", "token", "vdc");

            String id = postResponse.split(",")[1];
            serverId = id.substring(8, id.length() - 1);
            // setServerId(id.substring(8, id.length()-1));

            // Get the VM
            String getResponse = openStackUtil.getServer(serverId, "region", "token", "vdc");

            Thread.sleep(5000);

            // Delete the VM
            String deleteResponse = openStackUtil.deleteServer(serverId, "region", "token", "vdc");

        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test to create a new Server.
     * 
     * @throws OpenStackException
     *             The exception from the server
     */
    // @Ignore
    //@Test
    public void testCreateServerAssignFloatingIP() throws OpenStackException {
        try {
            // Creates a new VM
            String payload = buildCreateServerPayload();

            // Create a VM
            String serverId = openStackUtil.createServer(payload, "region", "token", "vdc");

            Thread.sleep(5000);

            // Obtaining a freee floatingIP
            floatingIP = openStackUtil.getFloatingIP(user, "region");

            // Assign the floatingIP to the serverId
            openStackUtil.assignFloatingIP(serverId, floatingIP, "region", "token", "vdc");

            // Delete the VM
            String deleteResponse = openStackUtil.deleteServer(serverId, "region", "token", "vdc");

        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}

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

package com.telefonica.euro_iaas.paasmanager.claudia.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient;
import com.telefonica.euro_iaas.paasmanager.exception.ClaudiaResourceNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.ClaudiaRetrieveInfoException;
import com.telefonica.euro_iaas.paasmanager.exception.FileUtilsException;
import com.telefonica.euro_iaas.paasmanager.exception.IPNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.NetworkNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.OSNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.exception.VMStatusNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.manager.NetworkInstanceManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;
import com.telefonica.euro_iaas.paasmanager.model.SubNetwork;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.util.FileUtils;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackRegion;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackUtil;
import org.apache.commons.codec.binary.Base64;

/**
 * @author jesus.movilla
 */
public class ClaudiaClientOpenStackImpl implements ClaudiaClient {

    /**
     * The log.
     */

    private static Logger log = LoggerFactory.getLogger(ClaudiaClientOpenStackImpl.class);

    private OpenStackUtil openStackUtil = null;
    private OpenStackRegion openStackRegion = null;
    private FileUtils fileUtils;
    private NetworkInstanceManager networkInstanceManager = null;
    private static final int POLLING_INTERVAL = 10000;

    /**
     * Get the response of the server from openstack.
     * @param claudiaData
     * @param tier
     * @param replica
     * @param vm
     * @param region
     * @return
     * @throws ClaudiaResourceNotFoundException
     */
    public String browseVMReplica(ClaudiaData claudiaData, String tier, int replica, VM vm, String region)
        throws ClaudiaResourceNotFoundException {

        String response = "No Content";

        try {
            String token = claudiaData.getUser().getToken();
            String vdc = claudiaData.getVdc();
            response = openStackUtil.getServer(vm.getVmid(), region, token, vdc);
        } catch (OpenStackException e) {
            String errorMessage = "Error obtaining info from Server " + vm.getVmid();
            log.error(errorMessage);
            throw new ClaudiaResourceNotFoundException(errorMessage, e);
        }

        return response;
    }

    /**
     * Build the payload to deploy a VM (createServer).
     *
     * @see com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient#undeployVMReplica (java.lang.String,
     * java.lang.String)
     */
    private String buildCreateServerPayload(ClaudiaData claudiaData, TierInstance tierInstance, int replica)
        throws InfrastructureException {


        if ((tierInstance.getTier().getImage() == null) || (tierInstance.getTier().getFlavour() == null)
                || (tierInstance.getTier().getKeypair() == null)) {
            String errorMsg = " The tier does not include a not-null information: " + "Image: "
                    + tierInstance.getTier().getImage() + "Flavour: " + tierInstance.getTier().getFlavour()
                    + "KeyPair: " + tierInstance.getTier().getKeypair();
            log.error(errorMsg);
            throw new InfrastructureException(errorMsg);
        }
        String userData = getUserData(claudiaData, tierInstance);

        String payload = tierInstance.toJson(encode(userData));
        log.debug("Payload with base decoded " + payload);
        log.debug("Floating ip " + tierInstance.getTier().getFloatingip());

        return payload;
    }

    private String base64Decode(String str) {
        if (str == null) {
            return null;
        }
        byte[] bytes;
        try {
            byte[] decoded = Base64.decodeBase64(str);
            return new String(decoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("Error to encode the user data " + e.getMessage());
            return null;
        }

    }

    private static String radixBase64 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijklmnopqrstuvwxyz"
                    + "0123456789"
                    + "+/";

    public static String encode(String string) {
        if (string == null) {
            return "";
        }
        String whole_binary = "";
        for (char c : string.toCharArray()) {
            String char_to_binary = Integer.toBinaryString(c);
            while (char_to_binary.length() < 8)
                char_to_binary = "0" + char_to_binary;
            whole_binary += char_to_binary;
        }
        string = "";
        String suffix = "";
        for (int i = 0; i < whole_binary.length(); i += 6) {
            String six_binary_digits = null;
            try {
                six_binary_digits = whole_binary.substring(i, i + 6);
            } catch (StringIndexOutOfBoundsException sioobe) {
                six_binary_digits = whole_binary.substring(i);
                while (six_binary_digits.length() < 6) {
                    six_binary_digits += "00";
                    suffix += "=";
                }
            }
            string += radixBase64.charAt(Integer.parseInt(six_binary_digits, 2));
        }
        return string + suffix;
    }

    /**
     * Decodes an base64-encoded UTF-8/ASCII string to it's Base64 value.
     * Gives anomalous result for string with any other encoding.
     * Output is similar to output of this command on Linux:
     * printf "<string>" | base64 -d
     *
     * @param string
     * @return Base64 Decoding of parameter string
     */
    public static String decode(String string) {
        String binary_string = "";
        for (char c : string.toCharArray()) {
            if (c == '=')
                break;
            String char_to_binary = Integer.toBinaryString(radixBase64.indexOf(c));
            while (char_to_binary.length() < 6)
                char_to_binary = "0" + char_to_binary;
            binary_string += char_to_binary;
        }
        if (string.endsWith("=="))
            binary_string = binary_string.substring(0, binary_string.length() - 4);
        else if (string.endsWith("="))
            binary_string = binary_string.substring(0, binary_string.length() - 2);
        string = "";
        for (int i = 0; i < binary_string.length(); i += 8) {
            String eight_binary_digits = binary_string.substring(i, i + 8);
            string += (char) Integer.parseInt(eight_binary_digits, 2);
        }
        return string;
    }

    public String getUserData(ClaudiaData claudiaData, TierInstance tierInstance) {
        log.debug("Creating user data");
        String file = null;
        String hostname = tierInstance.getVM().getHostname();
        String chefServerUrl;
        String puppetUrl;
        try {
            file = fileUtils.readFile("userdata");
            log.debug("File userdata read");
        } catch (Exception e) {
            log.warn("Error to find the file" + e.getMessage());
            return file;
        }

        try {
            chefServerUrl = openStackRegion.getChefServerEndPoint(
                    tierInstance.getTier().getRegion(),
                    claudiaData.getUser().getToken());

        } catch (Exception e1) {
            log.warn("Error to obtain the chef-server url" + e1.getMessage());
            return file;
        }

        try {
            puppetUrl = openStackRegion.getPuppetMasterEndPoint(
                    tierInstance.getTier().getRegion(),
                    claudiaData.getUser().getToken());

        } catch (Exception e1) {
            log.warn("Error to obtain the puppetmaster url" + e1.getMessage());
            return file;
        }

        String chefValidationKey = "";
        try {
            chefValidationKey = fileUtils.readFile("validation.pem", "/etc/chef/");
        } catch (Exception e1) {
            log.warn("Error to find the validation key file" + e1.getMessage());
            //return file;
        }


        file = file.replace("{node_name}", hostname).replace("{server_url}", chefServerUrl).
                replace("{validation_key}", chefValidationKey).
                replace("{networks}", writeInterfaces(tierInstance)).
                replace("{if_up}", generateIfUp(tierInstance)).
                replace("{puppet_master}", puppetUrl);
        log.debug("payload " + file);
        return file;


    }

    public String writeInterfaces(TierInstance tierInstance) {
        int networkNoPublic = tierInstance.getNetworkNumberNoPublic();
        if (!(networkNoPublic > 1)) {
            return "";
        } else {

            return generateFileUbuntu(networkNoPublic) + "\n" + generateFileCentos(networkNoPublic);
        }
    }

    private String generateFileUbuntu(int networkNoPublic) {
        String interfaces = "auto lo \n" +
                "iface lo inet loopback\n";
        for (int i = 0; i < networkNoPublic; i++) {
            interfaces = interfaces + " auto eth" + i + " \n" +
                    " iface eth" + i + "  inet dhcp\n";

        }
        interfaces = encode(interfaces);

        String file = "write_files: \n" +
                "-   encoding: b64 \n" +
                "    content: | \n" +
                "        " + interfaces + "\n" +
                "    path: /etc/network/interfaces \n";
        return file;

    }

    private String generateFileCentos(int networkNoPublic) {
        String interfaces = "";
        for (int i = 0; i < networkNoPublic; i++) {
            interfaces = interfaces + "-   content: |\n" +
                    "        DEVICE=\"eth" + i + "\"\n" +
                    "        NM_CONTROLLED=\"yes\"\n" +
                    "        ONBOOT=\"yes\" \n" +
                    "        BOOTPROTO=\"dhcp\" \n" +
                    "        TYPE=\"Ethernet\"\n" +
                    "    path: /etc/sysconfig/network-scripts/ifcfg-eth" + i + "\n" +
                    "    permissions: '460'\n";
        }

        return interfaces;

    }

    private String generateIfUp(TierInstance tierInstance) {
        int networkNoPublic = tierInstance.getNetworkNumberNoPublic();
        String interfaces = "\nbootcmd:\n";
        for (int i = 0; i < networkNoPublic; i++) {
            interfaces = interfaces + "     - ifup eth" + i + " \n";
        }
        return interfaces;
    }


    private void addNetworkToTierInstance(ClaudiaData claudiaData, TierInstance tierInstance)
            throws InfrastructureException, InvalidEntityException, AlreadyExistsEntityException,
            EntityNotFoundException {
        String region = tierInstance.getTier().getRegion();
        List<NetworkInstance> networkInstances = networkInstanceManager.listNetworks(claudiaData, region);
        if (networkInstances.isEmpty()) {
            log.debug("It is essex.");
            return;

        }
        List<NetworkInstance> networkNoSharedInstances = loadNotSharedNetworksUser(networkInstances,
                claudiaData.getVdc(), region);
        if (networkNoSharedInstances.isEmpty()) {
            log.debug("There is not any network associated to the user");
            Network net = new Network(claudiaData.getUser().getTenantName(), claudiaData.getVdc(), region);
            SubNetwork subNet = new SubNetwork("default" + claudiaData.getVdc(), claudiaData.getVdc(), region);
            net.addSubNet(subNet);
            NetworkInstance netinstance = net.toNetworkInstance();
            NetworkInstance networkInstance = networkInstanceManager.create(claudiaData, netinstance, region);
            networkInstance.setDefaultNet(true);
            tierInstance.addNetworkInstance(networkInstance);
        } else {
            log.debug("Getting the default network ");
            NetworkInstance defaulNet = getDefaultNetwork(networkNoSharedInstances, claudiaData.getVdc());
            if (defaulNet == null) {
                log.debug("There is not a default network. Getting the first one");
                tierInstance.addNetworkInstance(networkInstanceManager.load(networkNoSharedInstances.get(0)
                        .getNetworkName(), claudiaData.getVdc(), region));
            }

        }
    }

    private NetworkInstance getDefaultNetwork(List<NetworkInstance> networkInstances, String vdc)
            throws EntityNotFoundException {
        for (NetworkInstance net : networkInstances) {
            if (net.isDefaultNet()) {
                return net;
            }
        }
        return null;
    }

    private List<NetworkInstance> loadNotSharedNetworksUser(List<NetworkInstance> networks, String tenantId,
                                                            String region) throws InfrastructureException {
        List<NetworkInstance> networksNotShared = new ArrayList<NetworkInstance>();
        for (NetworkInstance net : networks) {
            if (!net.getShared() && net.getTenantId().equals(tenantId)) {
                try {
                    networksNotShared.add(loadNetworkInstance(net, tenantId, region));
                } catch (Exception e) {
                    log.error("The network " + net.getNetworkName() + " cannot be added");

                }

            }
        }
        return networksNotShared;
    }

    private NetworkInstance loadNetworkInstance(NetworkInstance networkInstance, String tenantId, String region)
            throws InvalidEntityException, AlreadyExistsEntityException {
        try {
            networkInstance = networkInstanceManager.load(networkInstance.getNetworkName(), tenantId, region);
        } catch (Exception e) {
            log.warn("The network " + networkInstance.getNetworkName() + " is in Openstack but not in DB");
            networkInstanceManager.createInDB(networkInstance);
        }
        return networkInstance;
    }

    /**
     * Checks if a certain Server has been finally deleted from OpenStack.
     *
     * @param tierInstance
     * @param claudiaData
     */
    private void checkDeleteServerTaskStatus(TierInstance tierInstance, ClaudiaData claudiaData)
            throws InfrastructureException {

        while (true) {
            try {
                try {
                    Thread.sleep(POLLING_INTERVAL);
                } catch (InterruptedException e) {
                    String errorMessage = "Interrupted Exception during polling";
                    log.warn(errorMessage);
                    throw new InfrastructureException(errorMessage);
                }
                String response = openStackUtil.getServer(tierInstance.getVM().getVmid(), tierInstance.getTier()
                        .getRegion(), claudiaData.getUser().getToken(), claudiaData.getUser().getTenantId());
            } catch (OpenStackException e) {
                String errorMessage = "Error obtaining info from Server " + tierInstance.getVM().getVmid() + " "
                        + e.getMessage();
                log.warn(errorMessage);
                return;

                /*
                 * if (e.getMessage().contains("Malformed request url") || e.getMessage().contains("itemNotFound") ||
                 * e.getMessage().contains("badRequest")) { break; }
                 */

                // throw new InfrastructureException(errorMessage);
            }
        }
    }

    /*
     * @see com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient#deployVM(com
     * .telefonica.euro_iaas.paasmanager.model.ClaudiaData, java.lang.String)
     */
    public void deployVM(ClaudiaData claudiaData, TierInstance tierInstance, int replica, VM vm)
            throws InfrastructureException {
        // URL:http://130.206.80.63:8774/v2/ebe6d9ec7b024361b7a3882c65a57dda/servers
        // Headers
        // X-Auth-Token: 30e2a5dd40b3453b833780657a253ec9
        // Content-Type: application/json
        // Payload
        // {"server": {"name": "jesus5",
        // "imageRef": "44dcdba3-a75d-46a3-b209-5e9035d2435e",
        // "flavorRef": "2","key_name":"jesusmovilla"},
        // "security_group": "testjesus"}

        // openStackUtilImpl = new OpenStackUtilImpl(claudiaData.getUser());

        log.debug("Deploy server " + claudiaData.getService() + " tier instance " + tierInstance.getName()
                + " replica " + replica + " with networks " + tierInstance.getNetworkInstances().size()
                + " and region " + tierInstance.getTier().getRegion());

        if (tierInstance.getNetworkInstances().isEmpty()) {
            try {
                addNetworkToTierInstance(claudiaData, tierInstance);
            } catch (Exception e) {
                String errorMsg = "Error to obtain a default network for associating to the VM " + e.getMessage();
                log.error(errorMsg);
                // throw new InfrastructureException(errorMsg);
            }

        }

        String payload = buildCreateServerPayload(claudiaData, tierInstance, replica);

        try {

            String region = tierInstance.getTier().getRegion();
            String token = claudiaData.getUser().getToken();
            String vdc = tierInstance.getTier().getVdc();
            String serverId = openStackUtil.createServer(payload, region, token, vdc);
            if (tierInstance.getTier().getFloatingip().equals("true")) {
                String floatingIP = openStackUtil.getFloatingIP(claudiaData.getUser(), region);
                openStackUtil.assignFloatingIP(serverId, floatingIP, region, token, vdc);
                vm.setFloatingIp(floatingIP);
            }
            vm.setVmid(serverId);
        } catch (OpenStackException e) {
            String errorMessage = "Error interacting with OpenStack " + e.getMessage();
            log.error(errorMessage);
            throw new InfrastructureException(e.getMessage());

        }

    }

    @Override
    public void undeployVM(ClaudiaData claudiaData, TierInstance tierInstance) throws InfrastructureException {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void undeployService(ClaudiaData claudiaData) throws InfrastructureException {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String obtainIPFromFqn(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws IPNotRetrievedException, ClaudiaResourceNotFoundException, NetworkNotRetrievedException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String obtainOS(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws OSNotRetrievedException, ClaudiaResourceNotFoundException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getVApp(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws IPNotRetrievedException, ClaudiaResourceNotFoundException, NetworkNotRetrievedException,
            OSNotRetrievedException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String onOffScalability(ClaudiaData claudiaData, String environmentName, boolean b)
            throws InfrastructureException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String createImage(ClaudiaData claudiaData, TierInstance tierInstance) throws ClaudiaRetrieveInfoException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String switchVMOn(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws InfrastructureException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String obtainVMStatus(String vapp) throws VMStatusNotRetrievedException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> findAllVMs(ClaudiaData claudiaData, String region) throws ClaudiaResourceNotFoundException {
        List<String> vmIds;

        String response = "No Content";
        try {
            String token = claudiaData.getUser().getToken();
            String vdc = claudiaData.getVdc();
            response = openStackUtil.listServers(region, token, vdc);
            vmIds = fromJson(response);
        } catch (OpenStackException e) {
            String errorMessage = "Error obtaining list of Servers for tenant-ID "
                    + claudiaData.getUser().getTenantId();
            log.error(errorMessage);
            throw new ClaudiaResourceNotFoundException(errorMessage, e);
        }
        return vmIds;
    }

    private List<String> fromJson(String listServersResponse) {
        List<String> names = new ArrayList<String>();
        net.sf.json.JSONObject jsonNodeServers = net.sf.json.JSONObject.fromObject(listServersResponse);
        JSONArray jsonServersList = jsonNodeServers.getJSONArray("servers");

        for (Object o : jsonServersList) {
            SecurityGroup secGroup = new SecurityGroup();
            net.sf.json.JSONObject jsonSecGroup = (net.sf.json.JSONObject) o;
            String vmId = jsonSecGroup.getString("name");
            names.add(vmId);
        }
        return names;
    }

    public List<String> getIP(ClaudiaData claudiaData, String tierName, int replica, VM vm, String region)
            throws InfrastructureException {
        List<String> ips = new ArrayList<String>();

        try {
            String token = claudiaData.getUser().getToken();
            String vdc = claudiaData.getVdc();
            String response = openStackUtil.getServer(vm.getVmid(), region, token, vdc);
            String[] ipsResponse = response.split("addr=");

            for (int i = 1; i < ipsResponse.length; i++) {
                String ip = ipsResponse[i].split("\"")[1];
                ips.add(ip);
            }

        } catch (OpenStackException oes) {
            String errorMessage = "Error interacting getting ip from OpenStack ";
            log.error(errorMessage);
            throw new InfrastructureException(errorMessage);
        }
        return ips;
    }

    @Override
    public String deployServiceFull(ClaudiaData claudiaData, String ovf) throws InfrastructureException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @param openStackUtil the openStackUtil to set
     */
    public void setOpenStackUtil(OpenStackUtil openStackUtil) {
        this.openStackUtil = openStackUtil;
    }

    /**
     * @param openStackUtil the openStackUtil to set
     */
    public void setNetworkInstanceManager(NetworkInstanceManager networkInstanceManager) {
        this.networkInstanceManager = networkInstanceManager;
    }

    @Override
    public String browseVDC(ClaudiaData claudiaData) throws ClaudiaResourceNotFoundException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String browseService(ClaudiaData claudiaData) throws ClaudiaResourceNotFoundException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String browseVM(String org, String vdc, String service, String vm, PaasManagerUser user)
            throws ClaudiaResourceNotFoundException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String deployService(ClaudiaData claudiaData, String ovf) throws InfrastructureException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String deployVM(String org, String vdc, String service, String vmName, PaasManagerUser user, String vmPath)
            throws InfrastructureException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    public void undeployVMReplica(ClaudiaData claudiaData, TierInstance tierInstance) throws InfrastructureException {
        log.debug("Undeploy VM replica " + tierInstance.getName() + " for region " + tierInstance.getTier().getRegion()
                + " and user " + tierInstance.getTier().getVdc());
        try {

            String region = tierInstance.getTier().getRegion();
            String token = claudiaData.getUser().getToken();
            String vdc = tierInstance.getTier().getVdc();

            openStackUtil.deleteServer(tierInstance.getVM().getVmid(), region, token, vdc);

            checkDeleteServerTaskStatus(tierInstance, claudiaData);
            log.debug("Undeployed VM replica " + tierInstance.getName() + " for region "
                    + tierInstance.getTier().getRegion() + " and user " + tierInstance.getTier().getVdc());

            if (tierInstance.getTier().getFloatingip().equals("true")) {
                log.debug("Delete floating ip ");
                openStackUtil.disAllocateFloatingIP(region, token, vdc, tierInstance.getVM().getFloatingIp());
            }

        } catch (OpenStackException oes) {
            String errorMessage = "Error deleting serverId: " + tierInstance.getVM().getVmid();
            log.error(errorMessage);
            throw new InfrastructureException(errorMessage);
        }
    }

    @Override
    public String deployVDC(ClaudiaData claudiaData, String cpu, String mem, String disk)
            throws InfrastructureException {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    public void setFileUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public void setOpenStackRegion(OpenStackRegion openStackRegion) {
        this.openStackRegion = openStackRegion;
    }
}

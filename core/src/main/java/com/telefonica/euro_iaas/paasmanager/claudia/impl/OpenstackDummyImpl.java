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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient;
import com.telefonica.euro_iaas.paasmanager.exception.ClaudiaResourceNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.ClaudiaRetrieveInfoException;
import com.telefonica.euro_iaas.paasmanager.exception.FileUtilsException;
import com.telefonica.euro_iaas.paasmanager.exception.IPNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.NetworkNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.OSNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.exception.VMStatusNotRetrievedException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.util.FileUtils;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author jesus.movilla
 */
public class OpenstackDummyImpl implements ClaudiaClient {

    private SystemPropertiesProvider systemPropertiesProvider;
    private FileUtils fileUtils = null;
    private String auxIdVM = null;

    public String onOffScalability(ClaudiaData claudiaData, String environmentName, boolean b)
            throws InfrastructureException {
        return null;
    }

    public String browseService(ClaudiaData claudiaData) throws ClaudiaResourceNotFoundException {
        String payload = null;
        try {
            payload = fileUtils.readFile(systemPropertiesProvider.getProperty("neoclaudiaVappVMLocation"));
        } catch (FileUtilsException e) {
            throw new ClaudiaResourceNotFoundException("Error in the Claudia Dummy Utils " + e.getMessage());
        }
        return payload;
    }

    public String browseVDC(ClaudiaData claudiaData) throws ClaudiaResourceNotFoundException {
        // TODO Auto-generated method stub
        return "OK";
    }

    public String browseVM(String org, String vdc, String service, String vm, PaasManagerUser user)
            throws ClaudiaResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public String browseVMReplica(ClaudiaData claudiaData, String tierName, int replica, VM vm, String region)
            throws ClaudiaResourceNotFoundException {

        String payload = null;
        String ip = null;
        try {
            ip = obtainIPFromFqn(claudiaData.getOrg(), claudiaData.getVdc(), claudiaData.getService(), tierName,
                    claudiaData.getUser());
            ip = obtainIPFromFqn(claudiaData.getOrg(), claudiaData.getVdc(), claudiaData.getService(), tierName,
                    claudiaData.getUser());
        } catch (IPNotRetrievedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NetworkNotRetrievedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            payload = fileUtils.readFile(systemPropertiesProvider.getProperty("neoclaudiaVappVMLocation"))
                    .replace("{org}", claudiaData.getOrg()).replace("{vdc}", claudiaData.getVdc())
                    .replace("{service}", claudiaData.getService()).replace("{vm}", tierName).replace("{replica}", "1")
                    .replace("{IP}", ip);
            payload = fileUtils.readFile(systemPropertiesProvider.getProperty("neoclaudiaVappVMLocation"))
                    .replace("{org}", claudiaData.getOrg()).replace("{vdc}", claudiaData.getVdc())
                    .replace("{service}", claudiaData.getService()).replace("{vm}", tierName).replace("{replica}", "1")
                    .replace("{IP}", ip);

        } catch (FileUtilsException e) {
            throw new ClaudiaResourceNotFoundException("Error in the Claudia Dummy Utils " + e.getMessage());
        }
        return payload;
    }

    public List<String> getIP(ClaudiaData claudiaData, String tierName, int replica, VM vm, String region)
            throws InfrastructureException {

        List<String> ips = new ArrayList<String>();
        try {
            String ip = obtainIPFromFqn(claudiaData.getOrg(), claudiaData.getVdc(), claudiaData.getService(), tierName,
                    claudiaData.getUser());
            ips.add(ip);
        } catch (IPNotRetrievedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (NetworkNotRetrievedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClaudiaResourceNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ips;
    }

    public String createImage(ClaudiaData claudiaData, TierInstance tierInstance) throws ClaudiaRetrieveInfoException {
        // TODO Auto-generated method stub
        return null;
    }

    public String deployService(ClaudiaData claudiaData, String ovf) throws InfrastructureException {
        // TODO Auto-generated method stub

        return "<task href=\"http://localhost:8081/paasmanager/rest/vdc/hola/task/65\" startTime=\"2013-02-11T11:29:44.713+01:00\" status=\"SUCESS\"> "
                + "<description>Create environment testtomcatsap8</description> " + "<vdc>hola</vdc> " + "</task>";
    }

    public void undeployService(ClaudiaData claudiaData) throws InfrastructureException {
        return;
    }

    public String deployVDC(ClaudiaData claudiaData, String cpu, String mem, String disk)
            throws InfrastructureException {
        // TODO Auto-generated method stub
        return "NO used";
    }

    public String deployVM(String org, String vdc, String service, String vmName, PaasManagerUser user, String vmPath)
            throws InfrastructureException {
        // TODO Auto-generated method stub
        return null;
    }

    public void deployVM(ClaudiaData claudiaData, TierInstance tierInstance, int replica, VM vm)
            throws InfrastructureException {
        // TODO Auto-generated method stub

        String fqn = claudiaData.getOrg().replace("_", ".") + ".customers." + claudiaData.getVdc() + ".services."
                + claudiaData.getService() + ".vees." + tierInstance.getTier().getName() + ".replicas." + replica;
        String hostname = claudiaData.getService() + "-" + tierInstance.getTier().getName() + "-" + replica;

        vm.setFqn(fqn);
        vm.setHostname(hostname);

        String payload = null;

        String url = "http://130.206.80.63:8774/v2/" + claudiaData.getVdc() + "/servers";

        String name = claudiaData.getService() + "-" + tierInstance.getTier().getName() + "-" + replica;
        try {
            payload = "{\"server\": \n" + "{\"name\": \"" + name
                    + "\", \"imageRef\": \"44dcdba3-a75d-46a3-b209-5e9035d2435e\", \"flavorRef\": \"2\" }}";

            Client client = ClientBuilder.newClient();

            WebTarget wr = client.target(url);
            Invocation.Builder builder = wr.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

            Map<String, String> header = getHeaders(claudiaData.getUser());
            for (String key : header.keySet()) {
                builder = builder.header(key, header.get(key));
            }

            Response response = builder.post(Entity.entity(payload, MediaType.APPLICATION_JSON));

            String result = response.readEntity(String.class);

            String id = result.substring(result.indexOf("\"id\": \"") + "\"id\": \"".length(),
                    result.indexOf(", \"links\"") - 1);

            auxIdVM = id;

            vm.setVmid(id);

        } catch (Exception e) {
            String errorMessage = "Error performing post on the resource: " + url + " with payload: " + payload;

            throw new InfrastructureException(errorMessage);
        }

        /*
         * return
         * "<task href=\"http://130.206.80.112:8080/paasmanager/rest/vdc/test1/task/35\" startTime=\"2012-11-22T10:29:20.746+01:00\" status=\"success\">"
         * + " <description>Create environment testtomcatsap5</description>" + " <vdc>test1</vdc>    </task>";
         */

        /*
         * return
         * "<task href=\"http://130.206.80.112:8080/paasmanager/rest/vdc/test1/task/35\" startTime=\"2012-11-22T10:29:20.746+01:00\" status=\"success\">"
         * + " <description>Create environment testtomcatsap5</description>"+ " <vdc>test1</vdc>    </task>";
         */
    }

    private Map<String, String> getHeaders(PaasManagerUser claudiaData) {

        /*
         * PaasManagerUser user = (PaasManagerUser) SecurityContextHolder
         * .getContext().getAuthentication().getPrincipal();
         */

        Map<String, String> headers = new HashMap<String, String>();

        headers.put("X-Auth-Token", claudiaData.getToken());
        headers.put("X-Auth-Project-Id", claudiaData.getTenantId());

        /*
         * headers.put("X-Auth-Token", "bc3fd7adc68643d28a386a0fe5a48f7c"); headers.put("X-Auth-Project-Id",
         * "6571e3422ad84f7d828ce2f30373b3d4");
         */

        return headers;

    }

    public String getVApp(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws IPNotRetrievedException, ClaudiaResourceNotFoundException, NetworkNotRetrievedException,
            OSNotRetrievedException {

        FileUtils fileUtils = null;
        String payload = null;
        String ip = systemPropertiesProvider.getProperty("IP_VM_DummyClaudia");

        try {
            payload = fileUtils.readFile("VappTemplate.xml", "./src/main/resources").replace("{org}", org)
                    .replace("{vdc}", vdc).replace("{service}", service).replace("{vm}", vmName)
                    .replace("{replica}", "1").replace("{IP}", ip);

        } catch (FileUtilsException e) {

            throw new IPNotRetrievedException("Error in the Claudia Dummy Utils " + e.getMessage());
        }
        return payload;
    }

    public String obtainIPFromFqn(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws IPNotRetrievedException, ClaudiaResourceNotFoundException, NetworkNotRetrievedException {

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            String errorThread = "Thread Interrupted Exception " + "during delay after vm deployment";

            throw new IPNotRetrievedException(errorThread);
        }

        String url = "http://130.206.80.63:8774/v2/" + vdc + "/servers/" + this.auxIdVM;
        try {

            Client client = ClientBuilder.newClient();
            Response response;

            WebTarget wr = client.target(url);
            Invocation.Builder builder = wr.request(MediaType.APPLICATION_JSON);

            Map<String, String> header = getHeaders(user);
            for (String key : header.keySet()) {
                builder = builder.header(key, header.get(key));
            }

            response = builder.get();

            String result = response.readEntity(String.class);

            String ip = result.substring(result.indexOf("\"addr\": \"") + "\"addr\": \"".length(),
                    result.indexOf("\"}]}"));

            return ip;

        } catch (Exception e) {
            String errorMessage = "Error performing get on the resource: " + url;
            throw new IPNotRetrievedException(errorMessage);
        }

    }

    public String obtainOS(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws OSNotRetrievedException, ClaudiaResourceNotFoundException {

        return "95";
    }

    public void undeployVM(ClaudiaData claudiaData, TierInstance tierInstance) throws InfrastructureException {
        return;

    }

    public void undeployVMReplica(ClaudiaData claudiaData, TierInstance tierInstance) throws InfrastructureException {
        return;

    }

    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    public SystemPropertiesProvider getSystemPropertiesProvider() {
        return this.systemPropertiesProvider;
    }

    public void setFileUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public FileUtils getFileUtils() {
        return this.fileUtils;
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient#obtainVMStatus (java.lang.String)
     */
    public String obtainVMStatus(String vapp) throws VMStatusNotRetrievedException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient#switchVMOn (java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public String switchVMOn(String org, String vdc, String service, String vmName, PaasManagerUser user)
            throws InfrastructureException {
        // TODO Auto-generated method stub
        return null;
    }

    public String deployServiceFull(ClaudiaData claudiaData, String property) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient#findAllVMs(com.telefonica.euro_iaas.paasmanager.model
     * .ClaudiaData)
     */
    public List<String> findAllVMs(ClaudiaData claudiaData, String region) throws ClaudiaResourceNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * public String deployServiceFull(ClaudiaData claudiaData, String ovfs) throws InfrastructureException { // TODO
     * Auto-generated method stub return null; }
     */

}

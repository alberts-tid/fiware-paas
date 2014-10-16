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

package com.telefonica.euro_iaas.paasmanager.manager;

import java.util.Set;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidOVFException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidVappException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.Template;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;

public interface InfrastructureManager {

    
    /**
     * Create an EnvironmentInstance
     * 
     * @param environment
     * @param ovf
     * @param claudiaData
     * @return
     * @throws InfrastructureException
     * @throws InvalidVappException
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    EnvironmentInstance createInfrasctuctureEnvironmentInstance(EnvironmentInstance environmentInstance, Set<Tier> set,
            ClaudiaData claudiaData) throws InfrastructureException, InvalidVappException, InvalidOVFException,
            InvalidEntityException, EntityNotFoundException;

    /**
     * Delete the environemnt (vms associated to the environmentInstance)
     * 
     * @param envInstance
     * @param vdc
     * @throws InfrastructureException
     * @throws InvalidEntityException
     * @throws EntityNotFoundException 
     * @throws EntityNotFoundException
     */
    void deleteEnvironment(ClaudiaData claudiaData, EnvironmentInstance envInstance) throws InfrastructureException,
            InvalidEntityException, EntityNotFoundException;

    /**
     * Clone the template to a VM with products installed (Element of TierInstance)
     * 
     * @param templateName
     *            name of the template
     * @return the TierInstance
     * @throws InfrastructureException
     */
    TierInstance cloneTemplate(String templateName) throws InfrastructureException;

    /**
     * Scalate a vm
     * 
     * @param org
     * @param vdc
     * @param vapp
     * @param service
     * @param vmName
     * @param fqn
     * @return imagen_name
     * @throws InfrastructureException
     */
    /*
     * String ImageScalability(String org, String vdc, String vapp, String service, String vmName, String fqn,
     * PaasManagerUser user) throws InfrastructureException;
     */
    String ImageScalability(ClaudiaData claudiaData, TierInstance tierInstance) throws InfrastructureException;

    /**
     * Create a template from a tierInstance
     * 
     * @param tierInstance
     * @return the template
     * @throws InfrastructureException
     */
    Template createTemplate(TierInstance tierInstance) throws InfrastructureException;

    /**
     * Start or Stop scalability
     * 
     * @param org
     * @param vdc
     * @param service
     * @param vmName
     * @param fqn
     * @param b
     * @return
     * @throws InfrastructureException
     */
    String StartStopScalability(ClaudiaData claudiaData, boolean b) throws InfrastructureException;

    /**
     * Deploy a new VM
     * 
     * @param payload
     * @param org
     * @param vdc
     * @param service
     * @param i
     * @return
     * @throws InfrastructureException
     */
    void deployVM(ClaudiaData claudiaData, TierInstance tier, int replica, VM vm) throws InfrastructureException;

    /**
     * Delete a Vm replica
     * 
     * @param org
     * @param service
     * @param tierInstance
     * @return ProductInstance
     * @throws InfrastructureException
     */
    public void deleteVMReplica(ClaudiaData claudiaData, TierInstance tierInstance) throws InfrastructureException;

    /**
     * @param environmentInstance
     * @throws InfrastructureException
     */
    void federatedNetworks(ClaudiaData claudiaData, EnvironmentInstance environmentInstance)
            throws InfrastructureException;

    /**
     * Generates a instance name or hostname.
     */
    String generateVMName(String bluePrintName, String tierName, int numReplica, String vdc);
    
    public String getFederatedRange (ClaudiaData data, String region) throws InfrastructureException ; 

}

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

import java.util.List;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidProductInstanceRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReconfigurationException;
import com.telefonica.euro_iaas.paasmanager.exception.TierInstanceNotFoundException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

public interface TierInstanceManager {

    /**
     * Update an tierInstance
     * 
     * @param tierInstance
     * @return the tierInstance created
     * @throws InfrastructureException 
     */
    TierInstance update(ClaudiaData claudiaData, String envName, TierInstance tierInstance)
            throws EntityNotFoundException, InvalidEntityException, AlreadyExistsEntityException, InfrastructureException;

    /**
     * Scale a TierInstance
     * 
     * @param org
     * @param vdc
     * @param payload
     * @param tierInstance
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     * @throws NotUniqueResultException
     * @throws InvalidProductInstanceRequestException
     */
    void create(ClaudiaData claudiaData, TierInstance tierInstance, EnvironmentInstance envInstance,
            SystemPropertiesProvider propertiesProvider) throws InfrastructureException, EntityNotFoundException,
            InvalidEntityException, AlreadyExistsEntityException, NotUniqueResultException,
            InvalidProductInstanceRequestException, ProductInstallatorException;

    /**
     * @param criteria
     * @return
     * @throws EntityNotFoundException
     */
    List<TierInstance> findByCriteria(TierInstanceSearchCriteria criteria) throws EntityNotFoundException;

    /**
     * @param vdc
     * @param environmentInstance
     * @return
     * @throws EntityNotFoundException
     */
    List<TierInstance> findByEnvironment(String vdc, EnvironmentInstance environmentInstance)
            throws EntityNotFoundException;

    /**
     * Load a TierInstance by its name
     * 
     * @param name
     * @return
     * @throws EntityNotFoundException
     */
    TierInstance load(String name) throws EntityNotFoundException;

    /**
     * @param name
     * @return
     * @throws EntityNotFoundException
     */
    TierInstance loadByName(String name) throws EntityNotFoundException;
    
    /**
     * 
     * @param name
     * @return
     * @throws EntityNotFoundException
     */
    TierInstance loadNetworkInstnace(String name) throws EntityNotFoundException;

    /**
     * @param tierInstance
     * @throws InvalidEntityException
     */
    void remove(TierInstance tierInstance) throws InvalidEntityException;

    /**
     * @param tierInstance
     * @return
     * @throws InvalidEntityException
     * @throws InfrastructureException 
     */
    TierInstance create(ClaudiaData claudiaa, String envName, TierInstance tierInstance) throws InvalidEntityException, InfrastructureException;

    /**
     * @param org
     * @param service
     * @param tierInstance
     * @throws InfrastructureException
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     * @throws TierInstanceNotFoundException
     */
    void delete(ClaudiaData claudiaData, TierInstance tierInstance, EnvironmentInstance envInstan)
            throws InfrastructureException, InvalidEntityException, EntityNotFoundException;

    /**
     * @param claudiaData
     * @param tierInstance
     * @param envInstance
     * @throws InvalidEntityException
     * @throws NotUniqueResultException
     * @throws InvalidProductInstanceRequestException
     * @throws ProductInstallatorException
     * @throws AlreadyExistsEntityException
     * @throws ProductReconfigurationException
     * @throws EntityNotFoundException
     */
    void update(ClaudiaData claudiaData, TierInstance tierInstance, EnvironmentInstance envInstance)
            throws ProductInstallatorException, InvalidProductInstanceRequestException, NotUniqueResultException,
            InvalidEntityException, AlreadyExistsEntityException, EntityNotFoundException,
            ProductReconfigurationException;

    /**
     * Update.
     * @param tierInstance
     * @return
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     */
    TierInstance update(TierInstance tierInstance) throws EntityNotFoundException, InvalidEntityException;
}

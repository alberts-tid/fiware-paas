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

package com.telefonica.euro_iaas.paasmanager.dao;

import java.util.List;

import com.telefonica.euro_iaas.commons.dao.BaseDAO;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;

/**
 * Defines the methods needed to persist Tier objects.
 * 
 * @author Jesus M. Movilla
 */
public interface TierDao extends BaseDAO<Tier, String> {

    /**
     * @param TierId
     * @return
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     */
    Tier load(String name, String vdc, String environmentName) throws EntityNotFoundException;

    /**
     * Find the environment that matches with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the list of elements that match with the criteria.
     * @throws EntityNotFoundException
     */
    List<Tier> findByCriteria(TierSearchCriteria criteria) throws EntityNotFoundException;

    /**
     * @param tierName
     * @param vdc
     * @param environmentName
     * @return
     */
    Tier loadTierWithProductReleaseAndMetadata(String tierName, String vdc, String environmentName)
            throws EntityNotFoundException;

    /**
     * Find region name from tier by security group id.
     * 
     * @param idSecurityGroup
     * @return
     */
    String findRegionBySecurityGroup(String idSecurityGroup) throws EntityNotFoundException;

    /**
     * @param tierName
     * @param vdc
     * @param environmentName
     * @return
     * @throws EntityNotFoundException
     */
    Tier loadTierWithNetworks(String tierName, String vdc, String environmentName) throws EntityNotFoundException;

    /**
     * @param networkName
     * @return
     * @throws EntityNotFoundException
     */
    List<Tier> findAllWithNetwork(String networkName);

    /**
     * @param newTier
     * @return
     */
    Tier loadComplete(Tier newTier) throws EntityNotFoundException;
}

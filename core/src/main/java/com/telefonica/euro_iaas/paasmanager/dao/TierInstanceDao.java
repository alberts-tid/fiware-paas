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
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierInstanceSearchCriteria;

/**
 * Defines the methods needed to persist EnvironmentInstace objects.
 * 
 * @author Jesus M. Movilla
 */
public interface TierInstanceDao extends BaseDAO<TierInstance, String> {
    /**
     * Find the environment that matches with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the list of elements that match with the criteria.
     * @throws EntityNotFoundException
     */
    List<TierInstance> findByCriteria(TierInstanceSearchCriteria criteria) throws EntityNotFoundException;

    /**
     * @param tierInstanceId
     * @return
     * @throws EntityNotFoundException
     */
    TierInstance findByTierInstanceId(Long tierInstanceId) throws EntityNotFoundException;

    /**
     * @param tierInstanceName
     * @return
     * @throws EntityNotFoundException
     */
    public TierInstance findByTierInstanceName(String tierInstanceName) throws EntityNotFoundException;
    
    /**
     * 
     * @param tierInstanceName
     * @return
     * @throws EntityNotFoundException
     */
    TierInstance findByTierInstanceNameNetworkInst(String tierInstanceName) throws EntityNotFoundException;

}

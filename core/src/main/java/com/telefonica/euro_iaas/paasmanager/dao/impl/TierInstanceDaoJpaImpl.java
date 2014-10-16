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

package com.telefonica.euro_iaas.paasmanager.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.TierInstanceDao;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierInstanceSearchCriteria;

@Transactional(propagation = Propagation.REQUIRED)
public class TierInstanceDaoJpaImpl extends AbstractBaseDao<TierInstance, String> implements TierInstanceDao {

    public List<TierInstance> findAll() {
        return super.findAll(TierInstance.class);
    }

    public TierInstance load(String name) throws EntityNotFoundException {
        return super.loadByField(TierInstance.class, "name", name);
    }

    // TODO
    public List<TierInstance> findByCriteria(TierInstanceSearchCriteria criteria) throws EntityNotFoundException {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(TierInstance.class);

        if (!StringUtils.isEmpty(criteria.getVdc())) {
            baseCriteria.add(Restrictions.eq(Tier.VDC_FIELD, criteria.getVdc()));
        }

        List<TierInstance> tierInstances = setOptionalPagination(criteria, baseCriteria).list();

        if (criteria.getProductInstance() != null) {
            tierInstances = filterByProductInstance(tierInstances, criteria.getProductInstance());
        }

        if (criteria.getEnvironmentInstance() != null && criteria.getVdc() != null) {
            tierInstances = filterByEnvironmentInstanceVDC(tierInstances, criteria.getEnvironmentInstance(),
                    criteria.getVdc());
        }

        return tierInstances;
    }

    private List<TierInstance> filterByEnvironmentInstanceVDC(List<TierInstance> tierInstances,
            EnvironmentInstance environmentInstance, String vdc) throws EntityNotFoundException {
        List<TierInstance> result = new ArrayList<TierInstance>();
        // Tenemos que tener en enviromentInstance

        // ahora comprobamos que ese enviromentInstance corresponda
        // al vdc que queremos
        if (environmentInstance.getVdc().equals(vdc)) {
            result = environmentInstance.getTierInstances();
        }

        return result;
    }

    /**
     * Filter the result by environment instance
     * 
     * @param tierInstances
     * @param productInstanceInput
     * @return tierInstances
     */
    private List<TierInstance> filterByProductInstance(List<TierInstance> tierInstances,
            ProductInstance productInstanceInput) {

        List<TierInstance> result = new ArrayList<TierInstance>();

        for (TierInstance tierInstance : tierInstances) {
            List<ProductInstance> productInstances = tierInstance.getProductInstances();

            for (ProductInstance pInstance : productInstances) {
                if (pInstance.getName().equals(productInstanceInput.getName()))
                    result.add(tierInstance);
            }
        }
        return result;
    }


    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.dao.TierInstanceDao#findByTierInstanceId (java.lang.Long)
     */

    public TierInstance findByTierInstanceId(Long tierInstanceId) throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from TierInstance p join " + "fetch p.productInstances where p.id = :id");
        query.setParameter("id", tierInstanceId);
        TierInstance tierInstance = null;
        try {
            tierInstance = (TierInstance) query.getResultList().get(0);
        } catch (Exception e) {
            String message = " No TierInstance found in the database with name: " + Long.toString(tierInstanceId);
            throw new EntityNotFoundException(TierInstance.class, e.getMessage(), message);
        }
        return tierInstance;
    }

    public TierInstance findByTierInstanceName(String tierInstanceName) throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from TierInstance p left join fetch p.productInstances where p.name = :name");
        query.setParameter("name", tierInstanceName);
        TierInstance tierInstance = null;
        try {
            tierInstance = (TierInstance) query.getResultList().get(0);
            tierInstance.getNetworkInstances();
            getEntityManager().flush();
            
        } catch (Exception e) {
            throw new EntityNotFoundException(TierInstance.class, "name", tierInstanceName);

        }
        return tierInstance;
    }
    
    public TierInstance findByTierInstanceNameNetworkInst(String tierInstanceName) throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from TierInstance p left join fetch p.networkInstances where p.name = :name");
        query.setParameter("name", tierInstanceName);
        TierInstance tierInstance = null;
        try {
            tierInstance = (TierInstance) query.getResultList().get(0);
            tierInstance.getNetworkInstances();
            getEntityManager().flush();
            
        } catch (Exception e) {
            throw new EntityNotFoundException(TierInstance.class, "name", tierInstanceName);

        }
        return tierInstance;
    }

}

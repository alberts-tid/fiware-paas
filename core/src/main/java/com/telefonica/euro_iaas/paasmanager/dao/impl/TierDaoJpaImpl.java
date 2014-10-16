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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.TierDao;
import com.telefonica.euro_iaas.paasmanager.manager.impl.TierManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;

@Transactional(propagation = Propagation.REQUIRED)
public class TierDaoJpaImpl extends AbstractBaseDao<Tier, String> implements TierDao {
	 private static Logger log = LoggerFactory.getLogger(TierDaoJpaImpl.class);
    public List<Tier> findAll() {
        return super.findAll(Tier.class);
    }

    /*
     * public Tier load(String name) throws EntityNotFoundException { TierSearchCriteria criteria = new
     * TierSearchCriteria(); criteria.setName(name); List<Tier> tiers = findByCriteria(criteria); if (tiers.size() != 1)
     * { throw new EntityNotFoundException(Tier.class, "name", name); } Tier tier = tiers.get(0); return tier; }
     */

    public Tier load(String name) throws EntityNotFoundException {
        return null;
    }

    public Tier load(String name, String vdc, String environmentName) throws EntityNotFoundException {
        try {
            return this.findByNameAndVdcAndEnvironment(name, vdc, environmentName);
        } catch (Exception e) {
            return findByNameAndVdcAndEnvironmentNoProduct(name, vdc, environmentName);
        }

    }

    private Tier findByNameAndVdcAndEnvironment(String name, String vdc, String environmentname)
            throws EntityNotFoundException {
        if (vdc == null) {
            vdc = "";
        }
        Query query = getEntityManager().createQuery(
                "select p from Tier p left join " + "fetch p.productReleases where p.name = :name and p.vdc =:vdc "
                        + "and p.environmentname=:environmentname");
        query.setParameter("name", name);
        query.setParameter("vdc", vdc);
        query.setParameter("environmentname", environmentname);
        Tier tier = null;
        try {
            tier = (Tier) query.getResultList().get(0);
        } catch (Exception e) {
            String message = " No Tier found in the database with name: " + name + " vdc " + vdc
                    + " and environmentname " + environmentname + " " + e.getMessage();
            log.warn(message);
            throw new EntityNotFoundException(Tier.class, message, name);
        }

        return tier;
    }

    private Tier findByNameAndVdcAndEnvironmentNoProduct(String name, String vdc, String environmentname)
            throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from Tier p where p.name = :name and p.vdc =:vdc and p.environmentname= :environmentname");
        query.setParameter("name", name);
        query.setParameter("vdc", vdc);
        query.setParameter("environmentname", environmentname);
        Tier tier = null;
        try {
            tier = (Tier) query.getResultList().get(0);
        } catch (Exception e) {
            String message = " No Tier found in the database with name: " + name + " vdc " + vdc
                    + " no products and environmentname " + environmentname+ " " + e.getMessage();
            log.warn(message);
            throw new EntityNotFoundException(Tier.class, message, name);
        }
        return tier;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Tier> findByCriteria(TierSearchCriteria criteria) throws EntityNotFoundException {
        // Session session = (Session) getEntityManager().getDelegate();
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(Tier.class);

        if (!StringUtils.isEmpty(criteria.getVdc())) {
            baseCriteria.add(Restrictions.eq(Tier.VDC_FIELD, criteria.getVdc()));
        }
        if (!StringUtils.isEmpty(criteria.getName())) {
            baseCriteria.add(Restrictions.eq(Tier.NAME_FIELD, criteria.getName()));
        }
        if (!StringUtils.isEmpty(criteria.getEnvironmentName())) {
            baseCriteria.add(Restrictions.eq(Tier.ENVIRONMENT_NAME_FIELD, criteria.getEnvironmentName()));
        }

        List<Tier> tiers = setOptionalPagination(criteria, baseCriteria).list();

        if (criteria.getEnvironment() != null) {
            tiers = filterByEnvironment(tiers, criteria.getEnvironment());
        }

        return tiers;
    }

    private List<Tier> filterByEnvironment(List<Tier> tiers, Environment environment) throws EntityNotFoundException {
        List<Tier> filterTiers = new ArrayList();
        for (Tier tier : tiers) {
            if (environment.getTiers().contains(tier))
                filterTiers.add(tier);
        }
        return filterTiers;
    }

    @Override
    public Tier loadTierWithProductReleaseAndMetadata(String tierName, String vdc, String environmentName)
            throws EntityNotFoundException {
        Tier tier = findByNameAndVdcAndEnvironment(tierName, vdc, environmentName);

        for (ProductRelease productRelease : tier.getProductReleases()) {

            productRelease.getMetadatas();
        }

        return tier;
    }

    @Override
    public Tier loadTierWithNetworks(String name, String vdc, String environmentname) throws EntityNotFoundException {
        Query query = getEntityManager()
                .createQuery(
                        "select p from Tier p left join fetch  p.networks where p.name = :name and p.vdc =:vdc and p.environmentname= :environmentname");
        query.setParameter("name", name);
        query.setParameter("vdc", vdc);
        query.setParameter("environmentname", environmentname);
        Tier tier = null;
        try {
            tier = (Tier) query.getResultList().get(0);
            tier.getNetworks();
            getEntityManager().flush();
        } catch (Exception e) {
            String message = " No Tier found in the database with name: " + name + " vdc " + vdc
                    + " no products and environmentname " + environmentname;
            throw new EntityNotFoundException(Tier.class, message, name);
        }
        return tier;
    }

    @Override
    public String findRegionBySecurityGroup(String idSecurityGroup) throws EntityNotFoundException {

        Query query = getEntityManager().createQuery("select p from Tier p where p.securityGroup= :securityGroupId");
        query.setParameter("securityGroupId", idSecurityGroup);
        Tier tier = null;
        try {
            tier = (Tier) query.getResultList().get(0);
        } catch (Exception e) {
            String message = " No Tier found in the database with security group id: " + idSecurityGroup;
            throw new EntityNotFoundException(Tier.class, message, idSecurityGroup);
        }
        return tier.getRegion();

    }

    public List<Tier> findAllWithNetwork(String networkName) {
        Query query = getEntityManager().createQuery(
                "select tier from Tier tier left join fetch tier.networks nets where nets.name=:net");
        query.setParameter("net", networkName);
        List<Tier> tiers = (List<Tier>) query.getResultList();

        return tiers;
    }

    @Override
    public Tier loadComplete(Tier newTier) throws EntityNotFoundException {
        Query query = getEntityManager()
                .createQuery(
                        "select p from Tier p fetch all properties where p.name = :name and p.vdc =:vdc and p.environmentname= :environmentname");
        query.setParameter("name", newTier.getName());
        query.setParameter("vdc", newTier.getVdc());
        query.setParameter("environmentname", newTier.getEnviromentName());
        Tier tier = null;
        try {
            tier = (Tier) query.getResultList().get(0);
            tier.getNetworks();
            tier.getProductReleases();
            tier.getSecurityGroup();
        } catch (Exception e) {
            String message = " Tier don't exist in database ";
            throw new EntityNotFoundException(Tier.class, message, newTier.getName());
        }
        return tier;
    }
}

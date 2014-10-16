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

package com.telefonica.euro_iaas.paasmanager.manager.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentRequestException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentSearchCriteria;

public class EnvironmentManagerImpl implements EnvironmentManager {

    private EnvironmentDao environmentDao;
    private TierManager tierManager;

    /** The log. */
    private static Logger log = LoggerFactory.getLogger(EnvironmentManagerImpl.class);

    public Environment create(ClaudiaData claudiaData, Environment environment)
            throws InvalidEnvironmentRequestException {

        log.info("Creating environment " + environment.getName() + " with description " + environment.getDescription()
                + " org " + environment.getOrg() + " vdc " + environment.getVdc() + " tiers  " + environment.getTiers());

        Environment environmentDB = new Environment();

        environmentDB.setName(environment.getName());
        environmentDB.setDescription(environment.getDescription());
        environmentDB.setOrg(environment.getOrg());
        environmentDB.setVdc(environment.getVdc());

        if (environment.getOvf() != null) {
            environmentDB.setOvf(environment.getOvf());
        }
        if (environment.getTiers() != null) {
            log.info("Number of Tiers " + environment.getTiers().size());
            for (Tier tier : environment.getTiers()) {
                Tier tierDB = null;

                try {
                    tierDB = tierManager.load(tier.getName(), environment.getVdc(), environment.getName());
                } catch (EntityNotFoundException e) {
                    try {
                        tierDB = tierManager.create(claudiaData, environment.getName(), tier);
                    } catch (Exception e2) {
                        throw new InvalidEnvironmentRequestException(e2.getMessage(), e2);
                    }
                }

                environmentDB.addTier(tierDB);
            }
        }

        try {
            environmentDB = environmentDao.create(environmentDB);

        } catch (AlreadyExistsEntityException aee) {
            String errorMessage = "The Environment Object " + environment.getName() + " already exist in Database";
            throw new InvalidEnvironmentRequestException(errorMessage);
        } catch (Exception aee) {
            String errorMessage = "The Environment Object " + environment.getName() + " is " + "NOT valid";
            throw new InvalidEnvironmentRequestException(errorMessage);
        }

        return environmentDB;
    }

    public void destroy(ClaudiaData data, Environment environment) throws InvalidEntityException,
            InfrastructureException, EntityNotFoundException {

        List<Tier> tiers = new ArrayList<Tier>();
        for (Tier tier : environment.getTiers()) {
            log.info("Adding tier " + tier.getName() + " " + tier.getNetworks());
            tiers.add(tier);
        }

        for (Tier tier : tiers) {
            environment.deleteTier(tier);
            update(environment);
            tierManager.delete(data, tier);
        }

        environmentDao.remove(environment);
    }

    public Environment load(String name, String vdc) throws EntityNotFoundException {
        if (vdc == null || vdc.isEmpty()) {
            return environmentDao.load(name);
        } else {
        	try {
        		
        		Environment env = environmentDao.load(name, vdc);
        		log.info("in load before rturn");
                return env;
        	} catch (Exception e) {
        		log.info("error in load " + e.getMessage());
        		throw new EntityNotFoundException(Environment.class, "name", name);
        	}
        }
    }

    public List<Environment> findAll() {
        return environmentDao.findAll();
    }

    public List<Environment> findByCriteria(EnvironmentSearchCriteria criteria) {
        return environmentDao.findByCriteria(criteria);
    }

    /**
     * @param environmentDao
     *            the environmentDao to set
     */
    public void setEnvironmentDao(EnvironmentDao environmentDao) {
        this.environmentDao = environmentDao;
    }

    public void setTierManager(TierManager tierManager) {
        this.tierManager = tierManager;
    }

    public Environment update(Environment environment) throws EntityNotFoundException, InvalidEntityException {
        return environmentDao.update(environment);
    }

    public Environment updateTier(Environment environment, Tier tierold, Tier tiernew) throws EntityNotFoundException,
            InvalidEntityException {

        environment.updateTier(tierold, tiernew);
        return environmentDao.update(environment);
    }

}

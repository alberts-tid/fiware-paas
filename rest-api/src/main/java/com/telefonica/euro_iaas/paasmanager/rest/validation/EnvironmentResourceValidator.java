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

package com.telefonica.euro_iaas.paasmanager.rest.validation;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.AlreadyExistEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentDto;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Validator of the environment resource.
 * @author jesus.movilla
 */
public interface EnvironmentResourceValidator {

    /**
     * Validate the request to create and EnvironmentInstance from a EnvironmentDto.
     *
     * @param claudiaData   The information related to organization, vdc and service together with the user.
     * @param environmentDto    The information about the environment instance.
     * @param vdc   The vdc info (to be deprecated).
     * @throws AlreadyExistEntityException
     * @throws InvalidEntityException
     */
    void validateCreate(ClaudiaData claudiaData, EnvironmentDto environmentDto, String vdc)
        throws AlreadyExistEntityException, InvalidEntityException;

    /**
     * Validate the request to create and EnvironmentInstance from a EnvironmentDto in abstract environment.
     *
     * @param environmentDto    The information about the environment instance.
     * @throws AlreadyExistEntityException
     * @throws InvalidEntityException
     */
    void validateAbstractCreate(EnvironmentDto environmentDto)
        throws AlreadyExistEntityException, InvalidEntityException;

    /**
     * Validate the operation delete of an environment resource.
     *
     * @param envName   The environment to delete.
     * @param vdc   The vdc which contains this environment.
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    void validateDelete(String envName, String vdc)
        throws  InvalidEntityException, EntityNotFoundException;

    /**
     * Validate the update operation of an environment resource.
     *
     * @param envName   The environment to update.
     * @param vdc   The vdc which contains this environment.
     * @param systemPropertiesProvider  The properties from the default file or from
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    void validateUpdate(String envName, String vdc, SystemPropertiesProvider systemPropertiesProvider)
        throws  InvalidEntityException, EntityNotFoundException;
}

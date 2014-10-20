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

package com.telefonica.euro_iaas.paasmanager.rest.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Path;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TaskSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;

/**
 * Default TaskResource implementation.
 * 
 * @author Jesus M. Movilla
 */
@Path("/vdc/{vdc}/task")
@Component
@Scope("request")
public class TaskResourceImpl implements TaskResource {

    @Autowired
    private TaskManager taskManager;

    /**
     * Find a task for a given id.
     */
    public Task load(Long id) throws APIException {
        try {
            return taskManager.load(id);
        } catch (Exception ex) {
            throw new APIException(ex);
        }
    }

    /**
     * Retrieve the tasks that match with the given criteria.
     * 
     * @param page
     *            for pagination is 0 based number(<i>nullable</i>)
     * @param pageSize
     *            for pagination, the number of items retrieved in a query (<i>nullable</i>)
     * @param orderBy
     *            the file to order the search (id by default <i>nullable</i>)
     * @param orderType
     *            defines if the order is ascending or descending (asc by default <i>nullable</i>)
     * @param states
     *            the list containing the task states
     * @param resource
     *            the resource url the task is related to
     * @param owner
     *            the task's owner url
     * @param fromDate
     *            the initial date where the task was created (included)
     * @param toDate
     *            the final date where the task was created (included)
     * @return the tasks that match with the criteria.
     */
    public List<Task> findAll(Integer page, Integer pageSize, String orderBy, String orderType,
            List<TaskStates> states, String resource, String owner, Date fromDate, Date toDate, String vdc) {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setVdc(vdc);
        if (page != null && pageSize != null) {
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            criteria.setOrderBy(orderBy);
        }
        if (!StringUtils.isEmpty(orderType)) {
            criteria.setOrderBy(orderType);
        }
        criteria.setStates(states);
        criteria.setResource(resource);
        criteria.setOwner(owner);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        return taskManager.findByCriteria(criteria);
    }

    /**
     * Find tier instances with pagination.
     */
    public List<Task> findTierInstance(Integer page, Integer pageSize, String orderBy, String orderType,
            List<TaskStates> states, String resource, String owner, Date fromDate, Date toDate, String vdc,
            String environment, String tierInstance) {

        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setVdc(vdc);
        if (page != null && pageSize != null) {
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            criteria.setOrderBy(orderBy);
        }
        if (!StringUtils.isEmpty(orderType)) {
            criteria.setOrderBy(orderType);
        }
        criteria.setStates(states);
        criteria.setResource(resource);
        criteria.setOwner(owner);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        criteria.setEnvironment(environment);
        criteria.setTier(tierInstance);
        return taskManager.findByCriteria(criteria);

    }

    /**
     * Find an environment with pagination.
     */
    public List<Task> findEnvironment(Integer page, Integer pageSize, String orderBy, String orderType,
            List<TaskStates> states, String resource, String owner, Date fromDate, Date toDate, String vdc,
            String environment) {

        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setVdc(vdc);
        if (page != null && pageSize != null) {
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            criteria.setOrderBy(orderBy);
        }
        if (!StringUtils.isEmpty(orderType)) {
            criteria.setOrderBy(orderType);
        }
        criteria.setStates(states);
        criteria.setResource(resource);
        criteria.setOwner(owner);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        criteria.setEnvironment(environment);
        return taskManager.findByCriteria(criteria);
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

}

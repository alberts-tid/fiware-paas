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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationReleaseDto;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;

/**
 * Provides a rest api to works with ApplicationInstances.
 * 
 * @author Jesus M. Movilla
 */
public interface ApplicationInstanceResource {

    /**
     * Install a list of application in a given host running on the selected products.
     */
    @POST
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    Task install(@PathParam("org") String org, @PathParam("vdc") String vdc,
            @PathParam("environmentInstance") String environmentInstance, ApplicationReleaseDto applicationReleaseDto,
            @HeaderParam("callback") String callback) throws APIException;

    /**
     * Find the applications according to the criteria specified in the request.
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    List<ApplicationInstance> findAll(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy, @QueryParam("orderType") String orderType,
            @QueryParam("status") List<Status> status, @PathParam("vdc") String vdc,
            @PathParam("environmentInstance") String environmentInstance,
            @PathParam("productInstance") String productInstance, @QueryParam("applicationName") String applicationName)
            throws APIException;

    /**
     * Retrieve the selected application instance.
     */
    @GET
    @Path("/{name}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    ApplicationInstance load(@PathParam("vdc") String vdc,
            @PathParam("environmentInstance") String environmentInstance, @PathParam("name") String name);

    /**
     * Uninstall previously installed instance.
     * 
     * @param org
     *            The organization.
     * @param vdc
     *            The VDC.
     * @param environmentInstance
     *            The name of the environment instance.
     * @param name
     *            The name
     * @param callback
     *            The url of the callback to return.
     * @return A task to follow the execution of the task.
     */
    @DELETE
    @Path("/{name}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    Task uninstall(@PathParam("org") String org, @PathParam("vdc") String vdc,
            @PathParam("environmentInstance") String environmentInstance, @PathParam("name") String name,
            @HeaderParam("callback") String callback);

}

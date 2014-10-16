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

package com.telefonica.euro_iaas.paasmanager.dao.keystone.impl;

import java.sql.Connection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.keystone.TokenDao;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.keystone.Token;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author jesus.movilla
 */
public class TokenDaoIdmImpl implements TokenDao {

    private static Logger log = LoggerFactory.getLogger(TokenDaoIdmImpl.class);
    private SystemPropertiesProvider systemPropertiesProvider;

    /*
     * @PersistenceContext(unitName = "keystone") private EntityManager entityManagerFactoryKeystone;
     */

    /**
     * Find Last Valid Token from user.
     */
    public Token findLastTokenFromUser(Connection connection, String tenantId) throws EntityNotFoundException,
            OpenStackException {

        String url = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL) + "tokens";
        log.debug("actionUri: " + url);

        Client client = ClientBuilder.newClient();
        Response response;

        WebTarget wr = client.target(url);

        String payload = "{\"auth\":{\"passwordCredentials\":{\"username\": \"admin\", \"password\": \"openstack\"}, \"tenantName\": "
                + "\"" + tenantId + "\"}}";

        Invocation.Builder builder = wr.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

        builder = builder.header("Content-type", "application/json");
        builder = builder.header("Accept", "application/xml");

        response = builder.post(Entity.entity(payload, MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200) {
            String message = "Error calling OpenStack to an valid token. " + "Status " + response.getStatus();
            throw new OpenStackException(message);
        }
        String resp = response.readEntity(String.class);
        log.debug(resp);
        Token token = extractToken(resp);
        token.setTenantId(tenantId);
        return token;
    }

    private Token extractToken(String payload) {
        int i = payload.indexOf("id");
        int j = payload.indexOf(",\"tenant\"", i);

        String tokenstring = payload.substring(i - 1, j);
        log.info(tokenstring);

        tokenstring = tokenstring.replaceAll("\"", "");
        tokenstring = tokenstring.substring("id:".length());
        log.info(tokenstring);

        log.debug("Token  " + tokenstring);

        Token token = new Token();
        token.setId(tokenstring);
        token.setExpires("");
        token.setExtra("");
        return token;
    }

    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

}

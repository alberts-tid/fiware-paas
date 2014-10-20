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

package com.telefonica.euro_iaas.paasmanager.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Launch an exception when we detect an error in the API.
 */
public class APIException extends WebApplicationException {

    private String message;
    private String publicMessage;
    private Integer code;
    private Integer httpCode;
    private Throwable cause;

    /**
     * Assign the cause of the problem to the ServletException class.
     * 
     * @param cause
     *            The cause of the exception.
     */
    public APIException(Throwable cause) {

        super(Response.status(new ErrorResponseCode(cause).getHttpCode()).entity(new ErrorResponseConverter(cause))
                .type(MediaType.APPLICATION_JSON).build());

        this.cause = cause;

    }

    /**
     * Assign the cause and the http code of the error to the internal variables.
     * 
     * @param cause
     *            The cause of the exception.
     * @param error
     *            The http code of it.
     */
    public APIException(Throwable cause, int error) {

        super(Response.status(error).entity(new ErrorResponseConverter(cause)).type(MediaType.APPLICATION_JSON).build());

        this.httpCode = error;

    }

    /**
     * Return the http code of the error.
     * 
     * @return The http code of the error.
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * Return the message associated to the error.
     * 
     * @return The stored message or the corresponding message to the http error code.
     */
    public String getMessage() {
        if (message == null) {
            parseCause();
        }

        return this.message;
    }

    /**
     * Obtain the informacion realted to the cause of the error (code, messages and http code).
     */
    public void parseCause() {

        ErrorCode errorCode = ErrorCode.find(cause.toString());
        this.code = errorCode.getCode();
        this.publicMessage = errorCode.getPublicMessage();
        this.message = errorCode.toString() + "#" + cause.getMessage();
        this.httpCode = errorCode.getHttpCode();

    }

    /**
     * Getter method.
     */
    public String getPublicMessage() {
        return publicMessage;
    }

    /**
     * Getter method.
     */
    public Integer getHttpCode() {
        return httpCode;
    }
}

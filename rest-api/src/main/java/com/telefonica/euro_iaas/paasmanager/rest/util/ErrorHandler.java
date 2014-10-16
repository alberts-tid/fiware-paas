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

package com.telefonica.euro_iaas.paasmanager.rest.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.paasmanager.manager.impl.EnvironmentManagerImpl;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.exception.ErrorCode;

/**
 * Class to manage an HTTP error.
 */
public class ErrorHandler extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(EnvironmentManagerImpl.class);

    /**
     * Method to handle GET method request.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter out = getOutputStream(response);

        String responseMessage = createResponseMessage(request, response);
        log.info("response message: " + responseMessage);

        out.println(responseMessage);
        out.flush();

    }

    /**
     * Create a response HTTP message.
     * 
     * @param request
     * @param response
     * @return
     */
    public String createResponseMessage(HttpServletRequest request, HttpServletResponse response) {

        String responseMessage = "{\"errors\":[ ";

        String message = (String) request.getAttribute("javax.servlet.error.message");
        Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

        log.debug("message: " + message + " code: " + code + " ");

        if (throwable != null) {
            throwable.printStackTrace();

            if (throwable instanceof APIException) {
                message = ((APIException) throwable).getPublicMessage();
                code = ((APIException) throwable).getCode();
                response.setStatus(((APIException) throwable).getHttpCode());
            } else if (throwable instanceof ServletException) {

                Throwable throwable1 = ((ServletException) throwable).getRootCause();

                String cause = throwable1 != null ? throwable1.getCause().toString() : throwable.toString();
                ErrorCode errorCode = ErrorCode.find(cause);
                code = errorCode.getCode();
                String finalMessage = throwable1 != null ? throwable1.getCause().getMessage() : throwable.toString();
                message = errorCode.getPublicMessage() + "# " + finalMessage;
                response.setStatus(errorCode.getHttpCode());

            } else {
                ErrorCode errorCode = ErrorCode.find(throwable.getMessage());
                code = errorCode.getCode();
                message = errorCode.getPublicMessage();

                if (errorCode == ErrorCode.INFRASTRUCTURE) {

                    String message2 = throwable.getMessage().replace("\"", "\\\"");
                    responseMessage += "{\"message\":\"" + message2 + "\",\"code\":1000},";
                }
                response.setStatus(errorCode.getHttpCode());
            }
        }
        if ((message == null) || message.isEmpty()) {

            ErrorCode errorCode = ErrorCode.DEFAULT;
            code = errorCode.getCode();
            message = errorCode.getPublicMessage();
            response.setStatus(errorCode.getHttpCode());
        }

        responseMessage += "{\"message\":\"" + message + "\",\"code\":" + code + "}]}";
        return responseMessage;
    }

    private PrintWriter getOutputStream(HttpServletResponse response) throws IOException {
        return new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
    }

    /**
     * Method to handle POST method request.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}

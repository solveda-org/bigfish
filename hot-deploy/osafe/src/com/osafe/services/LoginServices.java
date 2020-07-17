/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package com.osafe.services;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

/**
 * <b>Title:</b> Login Services
 */
public class LoginServices {

    public static final String module = LoginServices.class.getName();
    public static final String resource = "SecurityextUiLabels";

    /**
     * Login service to do a simple check of the user's password
     *
     * @return Map of results including (userLogin) GenericValue object
     */
    public static Map<String, Object> checkUserPassword(DispatchContext ctx, Map<String, ?> context)
    {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));
        result.put("passwordMatches", "N");

        // if isServiceAuth is not specified, default to not a service auth
        boolean isServiceAuth = false;

        String username = (String) context.get("login.username");
        if (username == null)
        {
            username = (String) context.get("username");
        }
        String password = (String) context.get("login.password");
        if (password == null)
        {
            password = (String) context.get("password");
        }

        GenericValue userLogin = null;
        try 
        {
            userLogin = delegator.findOne("UserLogin", isServiceAuth, "userLoginId", username);
        } 
        catch (GenericEntityException e)
        {
            Debug.logWarning(e, "", module);
        }

        //Login service to authenticate username and password
        Map<String, Object> authResult = null;
        try 
        {
            authResult = dispatcher.runSync("userLogin", UtilMisc.toMap("login.username", username, "login.password", password));
        } 
        catch (GenericServiceException e)
        {
            result.put("passwordMatches", "N");
            Debug.logInfo("[LoginServices.userLogin] : Password Incorrect", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }

        if (ModelService.RESPOND_SUCCESS.equals(authResult.get(ModelService.RESPONSE_MESSAGE)))
        {
            Debug.logVerbose("[LoginServices.userLogin] : Password Matched", module);
            result.put("userLogin", userLogin);
            result.put("passwordMatches", "Y");
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        else
        {
            result.put("passwordMatches", "N");
            Debug.logInfo("[LoginServices.userLogin] : Password Incorrect", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        }
        return result;
    }
}

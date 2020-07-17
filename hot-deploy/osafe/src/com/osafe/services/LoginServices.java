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

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transaction;

import javolution.util.FastMap;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.authentication.AuthHelper;
import org.ofbiz.common.authentication.api.AuthenticatorException;
import org.ofbiz.common.login.LdapAuthenticationServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;

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
    public static Map<String, Object> checkUserPassword(DispatchContext ctx, Map<String, ?> context) {
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
            username = (String) context.get("username");
        String password = (String) context.get("login.password");
        if (password == null)
            password = (String) context.get("password");

        String errMsg = "";
        if (username == null || username.length() <= 0) {
            errMsg = UtilProperties.getMessage(resource, "loginservices.username_missing", locale);
        } else if (password == null || password.length() <= 0) {
            errMsg = UtilProperties.getMessage(resource, "loginservices.password_missing", locale);
        } else {

            if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "username.lowercase"))) {
                username = username.toLowerCase();
            }
            if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "password.lowercase"))) {
                password = password.toLowerCase();
            }

            boolean repeat = true;
            // starts at zero but it incremented at the beggining so in the
            // first pass passNumber will be 1
            int passNumber = 0;

            while (repeat) {
                repeat = false;
                // pass number is incremented here because there are continues
                // in this loop so it may never get to the end
                passNumber++;

                GenericValue userLogin = null;

                try {
                    // only get userLogin from cache for service calls; for web
                    // and other manual logins there is less time sensitivity
                        userLogin = delegator.findOne("UserLogin", isServiceAuth, "userLoginId", username);
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, "", module);
                }

                // see if any external auth modules want to sync the user info
                if (userLogin == null) {
                    try {
                        AuthHelper.syncUser(username);
                    } catch (AuthenticatorException e) {
                        Debug.logWarning(e, module);
                    }

                    // check the user login object again
                    try {
                        userLogin = delegator.findOne("UserLogin", isServiceAuth, "userLoginId", username);
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e, "", module);
                    }
                }

                if (userLogin != null) {
                    String encodedPassword = useEncryption ? HashCrypt.getDigestHash(password, getHashType()) : password;
                    String encodedPasswordOldFunnyHexEncode = useEncryption ? HashCrypt.getDigestHashOldFunnyHexEncode(password, getHashType()) : password;
                    String encodedPasswordUsingDbHashType = encodedPassword;

                    String currentPassword = userLogin.getString("currentPassword");
                    if (useEncryption && currentPassword != null && currentPassword.startsWith("{")) {
                        // get encode according to the type in the database
                        String dbHashType = HashCrypt.getHashTypeFromPrefix(currentPassword);
                        if (dbHashType != null) {
                            encodedPasswordUsingDbHashType = HashCrypt.getDigestHash(password, dbHashType);
                        }
                    }

                    String ldmStr = UtilProperties.getPropertyValue("security.properties", "login.disable.minutes");
                    long loginDisableMinutes = 30;

                    try {
                        loginDisableMinutes = Long.parseLong(ldmStr);
                    } catch (Exception e) {
                        loginDisableMinutes = 30;
                        Debug.logWarning("Could not parse login.disable.minutes from security.properties, using default of 30", module);
                    }

                    Timestamp disabledDateTime = userLogin.getTimestamp("disabledDateTime");
                    Timestamp reEnableTime = null;

                    if (loginDisableMinutes > 0 && disabledDateTime != null) {
                        reEnableTime = new Timestamp(disabledDateTime.getTime() + loginDisableMinutes * 60000);
                    }

                    // get the is system flag -- system accounts can only be
                    // used for service authentication
                    boolean isSystem = (isServiceAuth && userLogin.get("isSystem") != null) ? "Y".equalsIgnoreCase(userLogin.getString("isSystem")) : false;

                    // grab the hasLoggedOut flag
                    boolean hasLoggedOut = userLogin.get("hasLoggedOut") != null ? "Y".equalsIgnoreCase(userLogin.getString("hasLoggedOut")) : false;

                    if (UtilValidate.isEmpty(userLogin.getString("enabled")) || "Y".equals(userLogin.getString("enabled")) || (reEnableTime != null && reEnableTime.before(UtilDateTime.nowTimestamp())) || (isSystem)) {

                        // attempt to authenticate with Authenticator class(es)
                        boolean authFatalError = false;
                        boolean externalAuth = false;
                        try {
                            externalAuth = AuthHelper.authenticate(username, password, isServiceAuth);
                        } catch (AuthenticatorException e) {
                            // fatal error -- or single authenticator found --
                            // fail now
                            Debug.logWarning(e, module);
                            authFatalError = true;

                        }
                        // if the password.accept.encrypted.and.plain property
                        // in security is set to true allow plain or encrypted
                        // passwords
                        // if this is a system account don't bother checking the
                        // passwords
                        // if externalAuth passed; this is run as well
                        if ((!authFatalError && externalAuth)
                                || (userLogin.get("currentPassword") != null && (HashCrypt.removeHashTypePrefix(encodedPassword).equals(HashCrypt.removeHashTypePrefix(currentPassword)) || HashCrypt.removeHashTypePrefix(encodedPasswordOldFunnyHexEncode).equals(HashCrypt.removeHashTypePrefix(currentPassword))
                                        || HashCrypt.removeHashTypePrefix(encodedPasswordUsingDbHashType).equals(HashCrypt.removeHashTypePrefix(currentPassword)) || ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.accept.encrypted.and.plain")) && password.equals(userLogin.getString("currentPassword")))))) {
                            Debug.logVerbose("[LoginServices.userLogin] : Password Matched", module);

                            result.put("userLogin", userLogin);
                            result.put("passwordMatches", "Y");
                            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                        } else {

                            result.put("passwordMatches", "N");
                            Debug.logInfo("[LoginServices.userLogin] : Password Incorrect", module);
                            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                        }

                    }
                }
            }
        }

        return result;
    }

    public static String getHashType() {
        String hashType = UtilProperties.getPropertyValue("security.properties", "password.encrypt.hash.type");

        if (UtilValidate.isEmpty(hashType)) {
            Debug.logWarning("Password encrypt hash type is not specified in security.properties, use SHA", module);
            hashType = "SHA";
        }

        return hashType;
    }
    
   
}

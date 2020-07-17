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
package com.osafe.events;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * Events used for processing checkout and orders.
 */
public class CheckOutEvents {

    public static final String module = CheckOutEvents.class.getName();

    public static String autoCaptureAuthPayments(HttpServletRequest request, HttpServletResponse response) {
        // warning there can only be ONE payment preference for this to work
        // you cannot accept multiple payment type when using an external gateway
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String result = "success";

        String orderId = (String) request.getAttribute("orderId");
        try {
            /*
             * A bit of a hack here to get the admin user since to capture payments and complete the order requires a user who has
             * the proper security permissions
             */
            GenericValue sysLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
            List lOrderPaymentPreference = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_AUTHORIZED"));
            if (UtilValidate.isNotEmpty(lOrderPaymentPreference)) {
                /*
                 * This will complete the order generate invoice and capture any payments.
                 * OrderChangeHelper.completeOrder(dispatcher, sysLogin, orderId);
                 */

                /*
                 * To only capture payments and leave the order in approved status. Remove the complete order call,
                 */
                GenericValue gvOrderPayment = EntityUtil.getFirst(lOrderPaymentPreference);
                Map<String, Object> serviceContext = UtilMisc.toMap("userLogin", sysLogin, "orderId", orderId, "captureAmount", gvOrderPayment.getBigDecimal("maxAmount"));
                Map callResult = dispatcher.runSync("captureOrderPayments", serviceContext);
                ServiceUtil.getMessages(request, callResult, null);
            }
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return result;
    }

}
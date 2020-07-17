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
package com.osafe.events.ebs;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.osafe.services.ebs.EBSPaymentUtil;

/**
 * The Class EBSCheckoutEvents.
 */
public class EBSCheckoutEvents {

    /** The Constant module. */
    public static final String module = EBSCheckoutEvents.class.getName();

    public static String ebsCheckoutCancel(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        cart.removeAttribute("ebsCheckoutToken");
        cart.removeAttribute("ebsCheckoutRedirectParam");
        return "success";
    }

    public static String setEbsCheckout(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        Map<String, ? extends Object> inMap = UtilMisc.toMap("userLogin", cart.getUserLogin(), "cart", cart);
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("setEbsChekout", inMap);
        } catch (GenericServiceException e) {
            Debug.logInfo(e, module);
            return "error";
        }
        if (ServiceUtil.isError(result)) {
            Debug.logError(ServiceUtil.getErrorMessage(result), module);
            return "error";
        }
        return "success";
    }

    public static String ebsCheckoutRedirect(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        String token = (String) cart.getAttribute("ebsCheckoutToken");
        String redirectParam = (String) cart.getAttribute("ebsCheckoutRedirectParam");

        String productStoreId = null;
        String paymentGatewayConfigId = null;
        GenericValue ebsGatewayConfig = null;
        if (UtilValidate.isEmpty(token)) {
            Debug.logError("No EbsCheckout token found in cart, you must do a successful setEbsCheckout before redirecting.", module);
            return "error";
        }
        if (cart != null) {
            productStoreId = cart.getProductStoreId();
        }
        if (productStoreId != null) {
            GenericValue ebsPaymentSetting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, "EXT_EBS", null, true);
            if (ebsPaymentSetting != null) {
                paymentGatewayConfigId = ebsPaymentSetting.getString("paymentGatewayConfigId");
            }
        }
        if (paymentGatewayConfigId != null) {
            try {
            	ebsGatewayConfig = delegator.findOne("PaymentGatewayEbs", true, "paymentGatewayConfigId", paymentGatewayConfigId);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        StringBuilder redirectUrl = new StringBuilder("");
        if (ebsGatewayConfig == null) {
            redirectUrl = new StringBuilder("https://secure.ebs.in/pg/ma/sale/pay/");
        } else {
            redirectUrl = new StringBuilder(ebsGatewayConfig.getString("redirectUrl"));
        }
        redirectUrl.append("?");
        redirectUrl.append(redirectParam);
        Debug.logInfo("EBS request : " + redirectUrl, module);
        try {
            response.sendRedirect(redirectUrl.toString());
        } catch (IOException e) {
			String errMsg = "Problem redirecting to EBS: " + e.toString();
			Debug.logError(e, errMsg, module);
			return "error";
        }
        return "success";
    }

	public static String ebsCheckoutReturn(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        String productStoreId = null;
        String paymentGatewayConfigId = null;
        GenericValue ebsGatewayConfig = null;
        String secretKey = null;
        if (cart != null) {
            productStoreId = cart.getProductStoreId();
        }
        if (productStoreId != null) {
            GenericValue ebsPaymentSetting = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStoreId, "EXT_EBS", null, true);
            if (ebsPaymentSetting != null) {
                paymentGatewayConfigId = ebsPaymentSetting.getString("paymentGatewayConfigId");
            }
        }
        if (paymentGatewayConfigId != null) {
            try {
            	ebsGatewayConfig = delegator.findOne("PaymentGatewayEbs", true, "paymentGatewayConfigId", paymentGatewayConfigId);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        if (ebsGatewayConfig == null) {
        	secretKey = "ebsKey";
        } else {
        	secretKey = ebsGatewayConfig.getString("secretKey");
        }

        Map<String, Object> responseMap = EBSPaymentUtil.buildEbsPaymentResponse(request.getParameter("DR"), secretKey);
        request.setAttribute("ebsResponse", responseMap);
        Debug.logInfo("EBS response : " + responseMap.toString(), module);
        String responseCode = (String) responseMap.get("ResponseCode");
        if (UtilValidate.isNotEmpty(responseCode) && responseCode.equals("0")) {
        	return "success";
        } else {
        	return "error";
        }
     }

    public static String getEbsCheckoutDetails(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> ebsResponse = (Map) request.getAttribute("ebsResponse");

        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        Map<String, ? extends Object> inMap = UtilMisc.toMap("userLogin", cart.getUserLogin(), "cart", cart, "ebsResponse", ebsResponse);
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("getEbsCheckout", inMap);
        } catch (GenericServiceException e) {
            Debug.logError("A problem occurred while communicating with EBS, please try again or select a different checkout method", module);
            request.setAttribute("_EVENT_MESSAGE_", "A problem occurred while communicating with EBS, please try again or select a different checkout method");
            return "error";
        }
        if (ServiceUtil.isError(result)) {
            Debug.logError(ServiceUtil.getErrorMessage(result), module);
            request.setAttribute("_EVENT_MESSAGE_", ServiceUtil.getErrorMessage(result));
            return "error";
        }
        return "success";
    }
}
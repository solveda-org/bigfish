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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.LocalDispatcher;

/**
 * The Class EBSCheckoutEvents.
 */
public class EBSCheckoutEvents {

    /** The Constant module. */
    public static final String module = EBSCheckoutEvents.class.getName();

	/**
	 * The token cart map.
	 * 
	 * Used to maintain a weak reference to the ShoppingCart for customers who
	 * have gone to EBS to checkout so that we can quickly grab the cart,
	 * perform shipment estimates and send the info back to EBS. The weak key is
	 * a simple wrapper for the checkout token String and is stored as a cart
	 * attribute. The value is a weak reference to the ShoppingCart itself.
	 * Entries will be removed as carts are removed from the session (i.e. on
	 * cart clear or successful checkout) or when the session is destroyed
	 * */
	private static Map<EBSTokenWrapper, WeakReference<ShoppingCart>> tokenCartMap = new WeakHashMap<EBSTokenWrapper, WeakReference<ShoppingCart>>();

    /**
     * Initiate EBS Request.
     *
     * @param request the request
     * @param response the response
     * @return the string
     */
    public static String ebsCheckoutRedirect(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request);
		Map<String, Object> parameters = new LinkedHashMap<String, Object>();

        // Redirect URL
        String redirectUrl = null;
        
        // Retrieves the orderPartyId
		String orderPartyId = shoppingCart.getOrderPartyId();
		if (UtilValidate.isEmpty(orderPartyId) && UtilValidate.isNotEmpty(userLogin)){
			orderPartyId = userLogin.getString("partyId");
		}

        // gets the order total
        DecimalFormat df = new DecimalFormat("##.##");
        String orderTotal = df.format(shoppingCart.getDisplayGrandTotal());
   
        // get the product store
        // GA: why do we need this piece of code?
        /*GenericValue productStore = ProductStoreWorker.getProductStore(request);
        if (productStore == null) {
            Debug.logError("ProductStore is null", module);
            //request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "ProblemsGettingMerchantConfigurationError", locale));
            return "error";
        }*///??
        
		try {
			String billingContactMechId = shoppingCart.getContactMech("BILLING_LOCATION");
			GenericValue shippingAddress = shoppingCart.getShippingAddress();
			
			GenericValue paymentGatewayEBS = delegator.findOne("PaymentGatewayEbs", UtilMisc.toMap("paymentGatewayConfigId", "EBS_CONFIG"), true);
			redirectUrl = paymentGatewayEBS.getString("redirectUrl");
   
			// Generates an unique token and save shopping cart against it
			String refToken = UtilDateTime.nowAsString();
			shoppingCart.setAttribute("ebsCheckoutToken", refToken);
			EBSTokenWrapper tokenWrapper = new EBSTokenWrapper(refToken);
			shoppingCart.setAttribute("ebsCheckoutTokenObj", tokenWrapper);
			EBSCheckoutEvents.tokenCartMap.put(tokenWrapper, new WeakReference<ShoppingCart>(shoppingCart));
			
			if (UtilValidate.isEmpty(redirectUrl)) {
				Debug.logError("Redirect URL for EBS Payment Gateway is not correctly defined!", module);
				return "error";
			}

			// Customer Address
			GenericValue billingAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", billingContactMechId), true);
			String name = PartyHelper.getPartyName(delegator, orderPartyId, false);
			
			String emailAddress = null;
			Map<String, Object> emailResults = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", orderPartyId, "userLogin", userLogin));
			if (emailResults!= null && emailResults.get("emailAddress") != null) {
				emailAddress = (String) emailResults.get("emailAddress");
			}
			
			String phone = null;
			String shippingPhone = null;
			Map<String, Object> phoneResults = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("partyId", orderPartyId, "userLogin", userLogin));
			if (phoneResults != null){
				String contactMechPurTypeId = (String) phoneResults.get("contactMechPurposeTypeId");
				String areaCode = (String) phoneResults.get("areaCode");
				String contactNum = (String) phoneResults.get("contactNumber");
				if (areaCode != null && contactNum != null) {
					if ("PRIMARY_PHONE".equals(contactMechPurTypeId)) {
						phone =  areaCode + "-" + contactNum;
					}
					if ("PHONE_HOME".equals(contactMechPurTypeId)) {
						shippingPhone = areaCode + "-" + contactNum;
					}
				}
			}
			
			validateParam(parameters, "account_id", paymentGatewayEBS.getString("merchantId"));
			validateParam(parameters, "reference_no", refToken);
			validateParam(parameters, "amount", orderTotal);
			validateParam(parameters, "mode", paymentGatewayEBS.getString("mode"));
			validateParam(parameters, "description", "test");
			validateParam(parameters, "return_url", paymentGatewayEBS.getString("returnUrl"));
			validateParam(parameters, "name", name);
			validateParam(parameters, "address", billingAddress.getString("address1") + billingAddress.getString("address2"));
			validateParam(parameters, "city", billingAddress.getString("city"));
			validateParam(parameters, "state", billingAddress.getString("stateProvinceGeoId"));
			validateParam(parameters, "country", billingAddress.getString("countryGeoId"));
			validateParam(parameters, "postal_code", billingAddress.getString("postalCode"));
			validateParam(parameters, "phone", phone);
			validateParam(parameters, "email", emailAddress);
			validateParam(parameters, "ship_name", shippingAddress.getString("toName"));
			validateParam(parameters, "ship_address", shippingAddress.getString("address1") + shippingAddress.getString("address2"));
			validateParam(parameters, "ship_city", shippingAddress.getString("city"));
			validateParam(parameters, "ship_state", shippingAddress.getString("stateProvinceGeoId"));
			validateParam(parameters, "ship_country", shippingAddress.getString("countryGeoId"));
			validateParam(parameters, "ship_postal_code", shippingAddress.getString("postalCode"));
			validateParam(parameters, "ship_phone", shippingPhone);
		} catch (IllegalArgumentException e) {
			Debug.logError(e, e.toString(), module);
			return "error";
		} catch (Exception ex) {
			Debug.logError(ex, ex.toString(), module);
			return "error";
		}
   
        String encodedParameters = UtilHttp.urlEncodeArgs(parameters, false);
        String redirectString = redirectUrl + "?" + encodedParameters;
   
		try {
			response.sendRedirect(redirectString);
		} catch (IOException e) {
			String errMsg = "Problem redirecting to EBS: " + e.toString();
			Debug.logError(e, errMsg, module);
			return "error";
		}
   
        return "success";
    }
    
    /**
	 * Express checkout return.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws GenericEntityException the generic entity exception
	 */
	public static String ebsCheckoutReturn(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        
		GenericValue paymentGatewayEBS = delegator.findOne("PaymentGatewayEbs", UtilMisc.toMap("paymentGatewayConfigId", "EBS_CONFIG"), true);
		String key = paymentGatewayEBS.getString("secretKey");
        
        StringBuffer responseData = new StringBuffer().append(request.getParameter("DR"));
		for (int i = 0; i < responseData.length(); i++) {
			if (responseData.charAt(i) == ' ') {
				responseData.setCharAt(i, '+');
			}
		} 
    
        Base64 base64 = new Base64();
        byte[] data = base64.decode(responseData.toString());
        RC4 rc4 = new RC4(key);
        byte[] result = rc4.rc4(data);
        
        ByteArrayInputStream byteIn = new ByteArrayInputStream (result, 0, result.length);
        DataInputStream dataIn = new DataInputStream (byteIn);
        String recvString1 = "";
        String recvString = "";
		try {
			recvString1 = dataIn.readLine();
		} catch (IOException e) {
			Debug.logError(e, e.toString(), module);
			return "error";
		}

        int i =0;
		while (recvString1 != null) {
			i++;
			if (i > 705)
				break;
			recvString += recvString1 + "\n";
			try {
				recvString1 = dataIn.readLine();
			} catch (IOException e) {
				Debug.logError(e, e.toString(), module);
				return "error";
			}
		}
        recvString  = recvString.replace( "=&","=--&" );
        FastMap<String, String> responseMap = FastMap.newInstance();
        StringTokenizer st = new StringTokenizer(recvString, "=&");
		while (st.hasMoreTokens()) {
			responseMap.put(st.nextToken(), st.nextToken());
		}
		
        request.setAttribute("responseMap", responseMap);
        String transactionId = responseMap.get("TransactionID");
        String responseCode = responseMap.get("ResponseCode");
        String paymentID = responseMap.get("PaymentID");
        String amount = responseMap.get("Amount");
        String ebsResponseMerchantRefNo = responseMap.get("MerchantRefNo");
        String responseMessage = responseMap.get("ResponseMessage");
        String dateCreated = responseMap.get("DateCreated");
        String isFlagged = responseMap.get("isFlagged");
        
		if (transactionId != null && "0".equals(responseMap.get("ResponseCode"))) {

			String token = ebsResponseMerchantRefNo;
			WeakReference<ShoppingCart> weakCart = tokenCartMap.get(EBSTokenWrapper.getTokenWrapper(token));
			ShoppingCart cart = null;
			if (weakCart != null) {
				cart = weakCart.get();
			}

			if (cart == null) {
				Debug.logError("Could not locate the ShoppingCart for token " + token, module);
				return "error";
			}
			
			try {
				GenericValue userLoginVal = cart.getUserLogin();
				String userLoginId = null;
				if (UtilValidate.isNotEmpty(userLoginVal)) {
					userLoginId = userLoginVal.getString("userLoginId");
				}

				userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId != null ? userLoginId : "anonymous"), true);
				userLogin.setString("partyId", cart.getOrderPartyId());
				session.setAttribute("userLogin", userLogin);
				
				try {
					cart.setUserLogin(userLogin, dispatcher);
				} catch (CartItemModifyException e) {
					Debug.logError(e, module);
					return "error";
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return "error";
			}
			
			session.setAttribute("shoppingCart", cart);
			
			List<GenericValue> toBeStored = FastList.newInstance();
			GenericValue newPm = delegator.makeValue("PaymentMethod");
			toBeStored.add(newPm);
			GenericValue ebsPm = delegator.makeValue("EbsPaymentMethod");
			toBeStored.add(ebsPm);

			String newPmId = null;
			if (UtilValidate.isEmpty(newPmId)) {
				try {
					newPmId = delegator.getNextSeqId("PaymentMethod");
				} catch (IllegalArgumentException e) {
					String errMsg = "Error in generating new paymentMethodId: " + e.toString();
					Debug.logError(e, errMsg, module);
				}
			}

			newPm.set("partyId", cart.getOrderPartyId());
			newPm.set("description", responseMessage);
			newPm.set("fromDate", Timestamp.valueOf(dateCreated));

			ebsPm.set("transactionId", transactionId);
			ebsPm.set("responseCode", responseCode);
			ebsPm.set("responseMessage", responseMessage);
			ebsPm.set("paymentId", paymentID);
			ebsPm.set("dateCreated", Timestamp.valueOf(dateCreated));
			ebsPm.set("merchantRefNo", ebsResponseMerchantRefNo);
			ebsPm.set("amount", amount);
			ebsPm.set("isFlagged", isFlagged);

			newPm.set("paymentMethodId", newPmId);
			newPm.set("paymentMethodTypeId", "EXT_EBS");
			ebsPm.set("paymentMethodId", newPmId);

			try {
				delegator.storeAll(toBeStored);
			} catch (GenericEntityException e) {
				Debug.logWarning(e.getMessage(), module);
			}

			cart.addPayment(ebsPm.getString("paymentMethodId"));
		} else {
			return "error";
		}
		
        return "success";
     }
	
	/**
	 * Validate parameter.
	 *
	 * @param parameters the parameters
	 * @param paramName the parameter name
	 * @param paramValue the parameter value
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	private static void validateParam(Map<String, Object> parameters, String paramName, String paramValue) throws IllegalArgumentException {
		if (UtilValidate.isNotEmpty(paramValue)) {
			parameters.put(paramName, paramValue);
		} else {
			throw new IllegalArgumentException(paramName + " has an invalid value '" + paramValue + "'");
		}
	}
    
    /**
     * The Class EBSTokenWrapper.
     */
    @SuppressWarnings("serial")
    public static class EBSTokenWrapper implements Serializable {
    	
        /** The string. */
        String theString;
        
        /** The token ebs wrapper map. */
        private static Map<String, EBSTokenWrapper> tokenEBSWrapperMap = FastMap.newInstance();
        
        /**
         * Instantiates a new eBS token wrapper.
         *
         * @param theString the the string
         */
        public EBSTokenWrapper(String theString) {
            this.theString = theString;
            tokenEBSWrapperMap.put(theString, this);
        }

        /**
         * Gets the token wrapper.
         *
         * @param theString the the string
         * @return the token wrapper
         */
        public static EBSTokenWrapper getTokenWrapper(String theString) {
            return tokenEBSWrapperMap.get(theString);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof EBSTokenWrapper)) {
                return false;
            }
            EBSTokenWrapper other = (EBSTokenWrapper) o;
            return theString.equals(other.theString);
        }
        
        @Override
        public int hashCode() {
            return theString.hashCode();
        }
    }
}
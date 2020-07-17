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
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.LocalDispatcher;

/**
 * The Class EBSCheckoutEvents.
 */
public class EBSCheckoutEvents {

    /** The Constant resourceErr. */
    public static final String resourceErr = "AccountingErrorUiLabels";
    
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
        Locale locale = UtilHttp.getLocale(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        Map <String, Object> parameters = new LinkedHashMap <String, Object>();

        // get the orderId
        String shoppingListId =  cart.getAutoOrderShoppingListId();
		String orderPartyId =  cart.getOrderPartyId();
		if (UtilValidate.isEmpty(orderPartyId) && UtilValidate.isNotEmpty(userLogin)){
			orderPartyId = userLogin.getString("partyId");
		}

		String redirectUrl = null;
		String returnUrl = null;
		String secretKey = null;
		String account_id = null;
		String mode = null;
		String billingContactMechId = cart.getContactMech("BILLING_LOCATION");
		GenericValue shippingAddress = cart.getShippingAddress();
		GenericValue paymentGatewayEBS = null;
		GenericValue shoppingList = null;
		
		try {
			paymentGatewayEBS = delegator.findOne("PaymentGatewayEbs", UtilMisc.toMap("paymentGatewayConfigId", "EBS_CONFIG"), false);
			
			// gets the redirect URL
			redirectUrl = paymentGatewayEBS.getString("redirectUrl");
			// gets the return URL
			returnUrl = paymentGatewayEBS.getString("returnUrl");
			// gets the EBS secret key account
			secretKey = paymentGatewayEBS.getString("secretKey");
			// gets the EBS merchant account
			account_id = paymentGatewayEBS.getString("merchantId");
			// gets the EBS Mode account
			mode = paymentGatewayEBS.getString("mode");
   
			// gets the order header
			String token = null;
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, orderPartyId));
			exprs.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y"));
			exprs.add(EntityCondition.makeCondition("shoppingListTypeId", EntityOperator.EQUALS, "SLT_SPEC_PURP"));
			List<GenericValue> shoppingLists = delegator.findList("ShoppingList", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			
			if (shoppingLists != null) {
				shoppingList = EntityUtil.getFirst(shoppingLists);
				token = shoppingList.getString("shoppingListId");
				cart.setAttribute("ebsCheckoutToken", token);
				EBSTokenWrapper tokenWrapper = new EBSTokenWrapper(token);
				cart.setAttribute("ebsCheckoutTokenObj", tokenWrapper);
				EBSCheckoutEvents.tokenCartMap.put(tokenWrapper, new WeakReference<ShoppingCart>(cart));
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Cannot getting shopping list: " + shoppingListId, module);
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "ProblemsGettingOrderHeaderError", locale));
			return "error";
		}
   
        // gets the order total
        DecimalFormat df = new DecimalFormat("##.##");
        String orderTotal = df.format(cart.getDisplayGrandTotal());
   
        // get the product store
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
   
        if (productStore == null) {
            Debug.logError("ProductStore is null", module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "ProblemsGettingMerchantConfigurationError", locale));
            return "error";
        }
        
		try {
			if (UtilValidate.isEmpty(redirectUrl)) {
				Debug.logError("Payment properties is not configured properly, some notify URL from IPG is not correctly defined!", module);
				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "ProblemsGettingMerchantConfiguration", locale));
				return "error";
			}

			parameters.put("account_id", account_id);
			parameters.put("reference_no", shoppingList.getString("shoppingListId"));
			parameters.put("amount", orderTotal);
			parameters.put("mode", mode);
			parameters.put("description", "test");
			parameters.put("return_url", returnUrl);

			// Customer Address
			GenericValue billingAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", billingContactMechId), false);
			String name = PartyHelper.getPartyName(delegator, orderPartyId, false);
			String emailAddress = null;
			Map<String, Object> results = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", orderPartyId, "userLogin", userLogin));
			if (results.get("emailAddress") != null) {
				emailAddress = (String) results.get("emailAddress");
			}
			parameters.put("name", name);
			parameters.put("address", billingAddress.getString("address1") + billingAddress.getString("address2"));
			parameters.put("city", billingAddress.getString("city"));
			parameters.put("state", billingAddress.getString("stateProvinceGeoId"));
			parameters.put("country", billingAddress.getString("countryGeoId"));
			parameters.put("postal_code", billingAddress.getString("postalCode"));
			parameters.put("phone", EBSCheckoutEvents.getPartyPhoneNumber(orderPartyId, "PRIMARY_PHONE", delegator));
			parameters.put("email", emailAddress);
			parameters.put("ship_name", shippingAddress.getString("toName"));
			parameters.put("ship_address", shippingAddress.getString("address1") + shippingAddress.getString("address2"));
			parameters.put("ship_city", shippingAddress.getString("city"));
			parameters.put("ship_state", shippingAddress.getString("stateProvinceGeoId"));
			parameters.put("ship_country", shippingAddress.getString("countryGeoId"));
			parameters.put("ship_postal_code", shippingAddress.getString("postalCode"));
			parameters.put("ship_phone", EBSCheckoutEvents.getPartyPhoneNumber(orderPartyId, "PHONE_HOME", delegator));

			/*String pass = secretKey + "|" + account_id + "|" + orderTotal + "|" + shoppingListId + "|" + returnUrl + "|" + mode;
			String has = HashCrypt.getDigestHash(pass, "MD5");

			byte[] data = has.getBytes();
			BigInteger i = new BigInteger(1, data);
			String.format("%1${032}X", i).trim();*/
		} catch (Exception e) {
			Debug.log(e.getMessage());
		}
   
        String encodedParameters = UtilHttp.urlEncodeArgs(parameters, false);
        String redirectString = redirectUrl + "?" + encodedParameters;
   
        try {
            response.sendRedirect(redirectString);
        } catch (IOException e) {
            Debug.logError(e, "Problems redirecting to IPG", module);
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "ProblemsConnectingWithIPGError", locale));
            return "error";
        }
   
        return "success";
    }
    
    
	/**
	 * Gets the party phone number.
	 *
	 * @param partyId the party id
	 * @param contactMechPurposeTypeId the contact mech purpose type id
	 * @param delegator the delegator
	 * @return the party phone number
	 */
	public static String getPartyPhoneNumber(String partyId, String contactMechPurposeTypeId, Delegator delegator) {
		String partyPhoneNumber = "";
		try {
			GenericValue partyPhoneDetail = null;
			List<GenericValue> partyPhoneDetails = delegator.findByAnd("PartyContactDetailByPurpose", 
					UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId, "partyId", partyId));
			if(UtilValidate.isNotEmpty(partyPhoneDetails)) {
				partyPhoneDetails = EntityUtil.filterByDate(partyPhoneDetails);
				partyPhoneDetail = EntityUtil.getFirst(partyPhoneDetails);
				partyPhoneNumber = (String) partyPhoneDetail.get("areaCode") + "-" + (String) partyPhoneDetail.get("contactNumber");
    	    }
		} catch (GenericEntityException e) {
			Debug.logError(e, "Problems getting Party Phone Number", module);
		}
		return partyPhoneNumber;
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
        
		GenericValue paymentGatewayEBS = delegator.findOne("PaymentGatewayEbs", UtilMisc.toMap("paymentGatewayConfigId", "EBS_CONFIG"), false);
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
				Debug.logError("Could locate the ShoppingCart for token " + token, module);
				return "error";
			}
			
			try {
				GenericValue userLoginVal = cart.getUserLogin();
				String userLoginId = null;
				if (UtilValidate.isNotEmpty(userLoginVal)) {
					userLoginId = userLoginVal.getString("userLoginId");
				}

				userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId != null ? userLoginId : "anonymous"), false);
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
					Debug.log(" Error in generating new paymentMethodId");
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
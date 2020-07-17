package com.osafe.events;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.RandomStringUtils;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.control.LoginWorker;

import com.osafe.util.Util;

/**
 * LoginEvents - Events for UserLogin and Security handling.
 */
public class LoginEvents {

    public static final String module = LoginEvents.class.getName();
    public static final String resource = "SecurityextUiLabels";
    public static final String usernameCookieName = "OFBiz.Username";
    public static final String isUserSessionCookieName = "userSession";
    public static final String label_resource = "OSafeUiLabels";

    
    /**
     * The user forgot his/her password.  This will call showPasswordHint, emailPassword or simply returns "success" in case
     * no operation has been specified.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String forgotPassword(HttpServletRequest request, HttpServletResponse response) {
        if ((UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT"))) || (UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT.x")))) {
            return showPasswordHint(request, response);
        } else if ((UtilValidate.isNotEmpty(request.getParameter("EMAIL_PASSWORD"))) || (UtilValidate.isNotEmpty(request.getParameter("EMAIL_PASSWORD.x")))) {
            return emailPassword(request, response);
        } else {
            return "success";
        }
    }

    /** Show the password hint for the userLoginId specified in the request object.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String showPasswordHint(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String userLoginId = request.getParameter("USERNAME");
        String errMsg = null;

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;

        try 
        {
            String userLoginIdNameUpCase = userLoginId.toUpperCase();
        	List<GenericValue> userLoginList = delegator.findList("UserLogin", EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.EQUALS, userLoginIdNameUpCase), null, null, null, false);
        	supposedUserLogin = EntityUtil.getFirst(userLoginList);
        } 
        catch (GenericEntityException gee) {
            Debug.logWarning(gee, "", module);
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String passwordHint = supposedUserLogin.getString("passwordHint");

        if (!UtilValidate.isNotEmpty(passwordHint)) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.no_password_hint_specified_try_password_emailed", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        Map<String, String> messageMap = UtilMisc.toMap("passwordHint", passwordHint);
        errMsg = UtilProperties.getMessage(resource, "loginevents.password_hint_is", messageMap, UtilHttp.getLocale(request));
        request.setAttribute("_EVENT_MESSAGE_", errMsg);
        return "success";
    }

    /**
     *  Email the password for the userLoginId specified in the request object.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String emailPassword(HttpServletRequest request, HttpServletResponse response) {
        String defaultScreenLocation = "component://securityext/widget/EmailSecurityScreens.xml#PasswordEmail";

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);

        String errMsg = null;

        Map<String, String> subjectData = FastMap.newInstance();
        subjectData.put("productStoreId", productStoreId);

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = request.getParameter("USERNAME");
        subjectData.put("userLoginId", userLoginId);

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;

        try 
        {
            String userLoginIdNameUpCase = userLoginId.toUpperCase();
        	List<GenericValue> userLoginList = delegator.findList("UserLogin", EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.EQUALS, userLoginIdNameUpCase), null, null, null, false);
        	supposedUserLogin = EntityUtil.getFirst(userLoginList);
            if (supposedUserLogin == null) {
                // the Username was not found
                errMsg = UtilProperties.getMessage(resource, "loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            if (useEncryption) {
                // password encrypted, can't send, generate new password and email to user
            	String regPwdMinChar = Util.getProductStoreParm(request, "REG_PWD_MIN_CHAR");  
            	if(UtilValidate.isEmpty(regPwdMinChar))
            	{
            		regPwdMinChar = "6"; //default value
            	}
                passwordToSend = RandomStringUtils.randomAlphanumeric(Integer.parseInt(regPwdMinChar));
                if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "password.lowercase"))) 
                {
                	passwordToSend=passwordToSend.toLowerCase();
                }
                supposedUserLogin.set("currentPassword", HashCrypt.getDigestHash(passwordToSend, LoginServices.getHashType()));
                supposedUserLogin.set("passwordHint", "Auto-Generated Password");
                if ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.email_password.require_password_change"))){
                    supposedUserLogin.set("requirePasswordChange", "Y");
                }
            } 
            else 
            {
                passwordToSend = supposedUserLogin.getString("currentPassword");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
            errMsg = UtilProperties.getMessage(resource, "loginevents.error_accessing_password", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        if (supposedUserLogin == null) 
        {
            // the Username was not found
            Map<String, String> messageMap = UtilMisc.toMap("userLoginId", userLoginId);
            errMsg = UtilProperties.getMessage(resource, "loginevents.user_with_the_username_not_found", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        StringBuilder emails = new StringBuilder();
        GenericValue party = null;

        try {
            party = supposedUserLogin.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            party = null;
        }
        if (party != null) {
            Iterator<GenericValue> emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = emailIter.next();
                emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
            }
        }

        if (!UtilValidate.isNotEmpty(emails.toString())) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.no_primary_email_address_set_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", "PRDS_PWD_RETRIEVE");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }

        if (productStoreEmail == null) {
            errMsg = UtilProperties.getMessage(resource, "loginevents.problems_with_configuration_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }

        // set the needed variables in new context
        Map<String, Object> bodyParameters = FastMap.newInstance();
        bodyParameters.put("useEncryption", Boolean.valueOf(useEncryption));
        bodyParameters.put("password", UtilFormatOut.checkNull(passwordToSend));
        bodyParameters.put("locale", UtilHttp.getLocale(request));
        bodyParameters.put("userLogin", supposedUserLogin);
        bodyParameters.put("productStoreId", productStoreId);

        Map<String, Object> serviceContext = FastMap.newInstance();
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        serviceContext.put("bodyParameters", bodyParameters);
        serviceContext.put("subject", productStoreEmail.getString("subject"));
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", emails.toString());
        serviceContext.put("partyId", party.getString("partyId"));
        serviceContext.put("emailType", "PRDS_PWD_RETRIEVE");
        serviceContext.put("productStoreId", productStoreId);

        try {
            Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);

            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                errMsg = UtilProperties.getMessage(resource, "loginevents.error_unable_email_password_contact_customer_service_errorwas", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e, "", module);
            errMsg = UtilProperties.getMessage(resource, "loginevents.error_unable_email_password_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", module);
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
                errMsg = UtilProperties.getMessage(resource, "loginevents.error_saving_new_password_email_not_correct_password", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        }

        if (useEncryption) {
            errMsg = UtilProperties.getMessage(resource, "loginevents.new_password_createdandsent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_EVENT_MESSAGE_", errMsg);
            List osafeSuccessMessageList = UtilMisc.toList( errMsg);
            request.setAttribute("osafeSuccessMessageList", osafeSuccessMessageList);

        } else {
            errMsg = UtilProperties.getMessage(resource, "loginevents.new_password_sent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_EVENT_MESSAGE_", errMsg);
            List osafeSuccessMessageList = UtilMisc.toList( errMsg);
            request.setAttribute("osafeSuccessMessageList", osafeSuccessMessageList);
        }
        return "passwordEmailed";
    }
    
    /**
     *  Login via facebook
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String fbLogin(HttpServletRequest request, HttpServletResponse response) 
    {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
    	HttpSession session = request.getSession();
    	ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        // get the schedule parameters
        String isFBLogin = (String) params.remove("isFBLogin");
        String fbEmail = (String) params.remove("fbEmail");
        String fbNextAction = (String) params.remove("fbNextAction");
        
        if(UtilValidate.isNotEmpty(isFBLogin))
        {
        	if(UtilValidate.isNotEmpty(fbEmail))
        	{
        		GenericValue userLoginFromFbEmail = null;
        		String passwordToUseForLogin = null;
        		try 
        		{
        			userLoginFromFbEmail = delegator.findOne("UserLogin", false, "userLoginId", fbEmail);
                    if (UtilValidate.isEmpty(userLoginFromFbEmail))
                    {
                        // go to Registration page to register new user
                        return "registrationPage";
                    }
                    else
                    {
                    	boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));
                    	if (useEncryption) 
                    	{
                            // password encrypted, can't send, generate new password and email to user
                        	String regPwdMinChar = Util.getProductStoreParm(request, "REG_PWD_MIN_CHAR");  
                        	if(UtilValidate.isEmpty(regPwdMinChar))
                        	{
                        		regPwdMinChar = "6"; //default value
                        	}
                        	passwordToUseForLogin = RandomStringUtils.randomAlphanumeric(Integer.parseInt(regPwdMinChar));
                        	userLoginFromFbEmail.set("currentPassword", HashCrypt.getDigestHash(passwordToUseForLogin, LoginServices.getHashType()));
                        	userLoginFromFbEmail.set("passwordHint", "Auto-Generated Password From FB Login");
                        	userLoginFromFbEmail.store();
                        } 
                    	else 
                        {
                        	passwordToUseForLogin = userLoginFromFbEmail.getString("currentPassword");
                        }
                    }
                    
                } 
        		catch (GenericEntityException e) 
                {
                    Debug.logWarning(e, "Could not log user in via Facebook Login", module);
                    return "error";
                }
        		
        		request.setAttribute("USERNAME", fbEmail);
        		request.setAttribute("PASSWORD", passwordToUseForLogin);
        		
        		LoginWorker.login(request, response);
        		session.setAttribute("userLogin", userLoginFromFbEmail);
        		session.setAttribute("USER_LOGIN_EMAIL", userLoginFromFbEmail.get("userLoginId"));
        		try
        		{
	        		if(UtilValidate.isNotEmpty(cart) && UtilValidate.isNotEmpty(userLoginFromFbEmail))
	        		{
	        			cart.setOrderPartyId((String)userLoginFromFbEmail.get("partyId"));
	        			cart.setUserLogin(userLoginFromFbEmail, dispatcher);
	        		}
        		}
        		catch (Exception e)
        		{
        			Debug.logWarning(e, "Could not update cart with user Login via Facebook Login", module);
                    return "error";
        		}
        		if(UtilValidate.isNotEmpty(fbNextAction) && fbNextAction.equalsIgnoreCase("checkout"))
        		{
        			return "checkout";
        		}
        		return "myAccount";
        	}
        }   
        return "error";
    }

}

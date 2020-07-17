
package com.osafe.events.paynetz;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

/**
 * @author rrlakhera
 *
 */
public class PayNetzCheckoutEvents
{

    /** The Constant module. */
    public static final String module = PayNetzCheckoutEvents.class.getName();

    public static String payNetzCheckoutCancel(HttpServletRequest request, HttpServletResponse response)
    {
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        cart.removeAttribute("payNetzCheckoutToken");
        cart.removeAttribute("payNetzCheckoutRedirectUrl");
        return "success";
    }

    public static String setPayNetzCheckout(HttpServletRequest request, HttpServletResponse response)
    {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);

        Map<String, ? extends Object> inMap = UtilMisc.toMap("userLogin", cart.getUserLogin(), "cart", cart);
        Map<String, Object> result = null;
        try 
        {
            result = dispatcher.runSync("setPayNetzChekout", inMap);
        }
        catch (GenericServiceException e)
        {
            Debug.logInfo(e, module);
            return "error";
        }
        if (ServiceUtil.isError(result))
        {
            Debug.logError(ServiceUtil.getErrorMessage(result), module);
            return "error";
        }

        String payNetzCheckoutToken = (String) cart.getAttribute("payNetzCheckoutToken");
        if ("TEST".equals(payNetzCheckoutToken))
        {
            cart.removeAttribute("payNetzCheckoutToken");
            return "test";
        }

        return "success";
    }

    public static String payNetzCheckoutRedirect(HttpServletRequest request, HttpServletResponse response)
    {
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        String redirectUrl = (String) cart.getAttribute("payNetzCheckoutRedirectUrl");

        Debug.logInfo("payNetz redirectUrl request : " + redirectUrl, module);
        try
        {
            response.sendRedirect(redirectUrl.toString());
        }
        catch (IOException e)
        {
			String errMsg = "Problem redirecting to payNetz: " + e.toString();
			Debug.logError(e, errMsg, module);
			return "error";
        }
        return "success";
    }

	public static String payNetzCheckoutReturn(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException
	{

        Map<String, Object> responseMap = UtilHttp.getParameterMap(request);
        request.setAttribute("payNetzResponse", responseMap);
        Debug.logInfo("PayNetz response : " + responseMap.toString(), module);

        String responseCode = (String) responseMap.get("f_code");
        if (UtilValidate.isNotEmpty(responseCode) && responseCode.equals("OK")) 
        {
        	return "success";
        }
        else
        {
        	return "error";
        }
     }

    public static String getPayNetzCheckoutDetails(HttpServletRequest request, HttpServletResponse response)
    {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> payNetzResponse = (Map) request.getAttribute("payNetzResponse");

        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        Map<String, ? extends Object> inMap = UtilMisc.toMap("userLogin", cart.getUserLogin(), "cart", cart, "payNetzResponse", payNetzResponse);
        Map<String, Object> result = null;
        try
        {
            result = dispatcher.runSync("getPayNetzCheckout", inMap);
        } 
        catch (GenericServiceException e) 
        {
            Debug.logError("A problem occurred while communicating with payNetz, please try again or select a different checkout method", module);
            request.setAttribute("_EVENT_MESSAGE_", "A problem occurred while communicating with payNetz, please try again or select a different checkout method");
            return "error";
        }
        if (ServiceUtil.isError(result)) 
        {
            Debug.logError(ServiceUtil.getErrorMessage(result), module);
            request.setAttribute("_EVENT_MESSAGE_", ServiceUtil.getErrorMessage(result));
            return "error";
        }
        return "success";
    }
}
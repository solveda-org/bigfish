package com.osafe.events;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;  
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartPaymentInfo;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.order.shoppingcart.ShoppingCartHelper;

public class ShoppingCartEvents {

    public static String setPaymentMethodOnCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String securityCode = request.getParameter("verificationNo");
        
        if (sc.items().size() > 0) {

            Map selectedPaymentMethods = new HashMap();
            Map paymentMethodInfo = FastMap.newInstance();
            List singleUsePayments = new ArrayList();
            paymentMethodInfo.put("amount", null);
            if (UtilValidate.isNotEmpty(securityCode))
            {
                paymentMethodInfo.put("securityCode", securityCode);
            }
            String paymentMethodId = (String) request.getAttribute("paymentMethodId");
            selectedPaymentMethods.put(paymentMethodId, paymentMethodInfo);
            sc.addPayment(paymentMethodId);
            CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, sc);
            checkOutHelper.setCheckOutPayment(selectedPaymentMethods, singleUsePayments, null);
        }

        return "success";
    }

    public static String resetPaymentMethod(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        List<GenericValue> paymentMethods = sc.getPaymentMethods();
        if (UtilValidate.isNotEmpty(paymentMethods)) {
            GenericValue selectedMethod = EntityUtil.getFirst(paymentMethods);
            if (UtilValidate.isNotEmpty(selectedMethod)) {
                CartPaymentInfo paymentInfo = sc.getPaymentInfo(selectedMethod.getString("paymentMethodId"));
                paymentInfo.amount = null;
            }
        }
        if(UtilValidate.isEmpty(sc.items())) {
            return org.ofbiz.order.shoppingcart.ShoppingCartEvents.clearCart(request, response);
        }
        return "success";
    }

    public static String setProductCategoryId(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        String productCategoryId = request.getParameter("add_category_id");
        String productId = request.getParameter("add_product_id");
        if(UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isNotEmpty(productId)) 
        {
        	for (Iterator<?> item = sc.iterator(); item.hasNext();) 
        	{
            	ShoppingCartItem sci = (ShoppingCartItem)item.next();
            	if (sci.getProductId().equals(productId)) 
            	{
            		sci.setProductCategoryId(productCategoryId);
            		break;
            	}
            }	
        }
        return "success";
    }
    
    public static String addMultiItemsToCart(HttpServletRequest request, HttpServletResponse response) 
    {
    	Map<String, Object> context = FastMap.newInstance(); 
    	Map<String, Object> params = UtilHttp.getParameterMap(request);
        String categoryId = (String) params.get("add_category_id");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, sc); 
        
        for(Entry<String, Object> entry : params.entrySet()) 
        {
            String parameterName = entry.getKey();
        	BigDecimal quantity = BigDecimal.ZERO;
        	String productId = null;
        	String quantityStr = null;
        	if (parameterName.toUpperCase().startsWith("ADD_MULTI_PRODUCT_ID"))
        	{
        		productId = (String) entry.getValue();
        		//get the index so that we can get the related quantity
        		int underscorePos = parameterName.lastIndexOf('_');
        		if (underscorePos >= 0) 
        		{
        			try 
        			{
        				String indexStr = parameterName.substring(underscorePos + 1);
                        int index = Integer.parseInt(indexStr);
                        quantityStr = (String) params.get("add_multi_product_quantity_"+index);
                        if (UtilValidate.isNotEmpty(quantityStr)) 
                        {
                            try 
                            {
                                quantity = new BigDecimal(quantityStr);
                            } 
                            catch (NumberFormatException nfe) 
                            {
                                quantity = BigDecimal.ZERO;
                            }
                        }
        			}
        			catch (NumberFormatException nfe) 
        			{
        				return "error";
                    } 
        			catch (Exception e) 
        			{
        				return "error";
                    }
        		}
        		if(UtilValidate.isNotEmpty(productId) && quantity.compareTo(BigDecimal.ZERO) > 0)
                {
                	// add item and quantity to cart using the addToCart method
                	cartHelper.addToCart(null, null, null, productId, categoryId, null, null, null, null, quantity, null, null, null, null, null, null, null, null, null, context, null);
                }
        	}
        }
        return "success";
    }
}

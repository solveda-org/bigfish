package com.osafe.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartPaymentInfo;
import org.ofbiz.service.LocalDispatcher;

public class ShoppingCartEvents {

    public static String setPaymentMethodOnCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart sc = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (sc.items().size() > 0) {

            Map selectedPaymentMethods = new HashMap();
            Map paymentMethodInfo = FastMap.newInstance();
            List singleUsePayments = new ArrayList();
            paymentMethodInfo.put("amount", null);
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

        return "success";
    }

}

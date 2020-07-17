package com.osafe.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class OrderServices {

    public static final String module = OrderServices.class.getName();

    @SuppressWarnings("unchecked")
    public static Map importClientOrders(DispatchContext dctx, Map context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map respMap = null;
        try {

            List<GenericValue> clientOrders = null;
            clientOrders = delegator.findByAnd("OrderHeader", UtilMisc.toMap("statusId", "ORDER_APPROVED"));

            for (GenericValue order : clientOrders) {
                String orderId = order.getString("orderId");

                List<GenericValue> orderItemShipGroups = order.getRelated("OrderItemShipGroup");
                GenericValue orderItemShipGroup = EntityUtil.getFirst(orderItemShipGroups);
                orderItemShipGroup.setString("trackingNumber","1Z 111 111 03 9222 222");
                orderItemShipGroup.store();

                respMap = dispatcher.runSync("changeOrderStatus", UtilMisc.toMap("userLogin", userLogin, "orderId", orderId, "statusId", "ORDER_COMPLETED"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("importClientOrders failed");
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("importClientOrders failed");
        }
        return result;
    }

}

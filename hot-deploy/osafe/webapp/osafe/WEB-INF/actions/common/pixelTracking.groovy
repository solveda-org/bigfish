import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import java.lang.*;
import java.text.NumberFormat;
import org.ofbiz.webapp.taglib.*;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import org.ofbiz.product.store.*;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.product.store.ProductStoreWorker;

pixelTrackingList = [];
productStore = ProductStoreWorker.getProductStore(request);
if (UtilValidate.isNotEmpty(productStore))
{
  String productStoreId=productStore.getString("productStoreId");
  pixelTrackingList = delegator.findByAndCache("XPixelTracking",UtilMisc.toMap("productStoreId",productStoreId));
  
  if (UtilValidate.isNotEmpty(context.orderHeader)) 
  {
        orderId = orderHeader.orderId;
        orderReadHelper = OrderReadHelper.getHelper(orderHeader);
        orderItems = orderReadHelper.getOrderItems();
        orderAdjustments = orderReadHelper.getAdjustments();
        orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
        otherAdjAmount = orderReadHelper.calcOrderAdjustments(orderAdjustments, orderSubTotal, true, false, false);
        orderShippingAmount = orderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
        orderTaxAmount = orderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
        orderGrandTotal = orderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);

        context.ORDER_ID=orderId;
        context.ORDER_ITEMS=orderItems;
        context.ORDER_DATE=orderHeader.orderDate;
        context.ORDER_TOTAL_ITEM=orderSubTotal.setScale(2).toString();
        context.ORDER_ADJUSTMENT=otherAdjAmount.setScale(2).toString();
        context.ORDER_SHIP_CHARGE=orderShippingAmount.setScale(2).toString();
        context.ORDER_TAX=orderTaxAmount.setScale(2).toString();
        context.ORDER_TOTAL_NET=orderGrandTotal.setScale(2).toString();
  }
}

context.pixelTrackingList = pixelTrackingList;

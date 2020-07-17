package common;

import java.math.BigDecimal;
import java.util.List;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo;
import org.ofbiz.order.shoppingcart.ShoppingCart.CartShipInfo.CartShipItemInfo;
import org.ofbiz.order.shoppingcart.shipping.ShippingEstimateWrapper;



shipGroup = request.getAttribute("shipGroup");
shipGroupIndex = request.getAttribute("shipGroupIndex");

context.shipGroup=shipGroup;
context.shipGroupIndex=shipGroupIndex;

package shipping;

import org.ofbiz.base.util.UtilValidate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import com.ibm.icu.util.Calendar;


String srchProductStoreShipMethId = StringUtils.trimToEmpty(parameters.srchProductStoreShipMethId);
productStoreId=globalContext.productStoreId;

productStore = ProductStoreWorker.getProductStore(request);

Timestamp now = UtilDateTime.nowTimestamp(); 
orderBy = ["shipmentMethodTypeId"];
// Paging variables
viewIndex = Integer.valueOf(parameters.viewIndex  ?: 1);
viewSize = Integer.valueOf(parameters.viewSize ?: UtilProperties.getPropertyValue("osafeAdmin", "default-view-size"));
context.viewIndex = viewIndex;
context.viewSize = viewSize;

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);

//spotListMenuId=context.spotListMenuId;
List contentList = FastList.newInstance();
context.userLoginId = userLogin.userLoginId;

exprs = FastList.newInstance();
mainCond=null;
prodCond=null;
statusCond=null;

if (UtilValidate.isNotEmpty(productStoreId))
{

	exprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
}

// Product Store Ship Method Id
if (UtilValidate.isNotEmpty(srchProductStoreShipMethId))
{
	productStoreShipMethId=srchProductStoreShipMethId;
	exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productStoreShipMethId"), EntityOperator.LIKE, "%" + productStoreShipMethId.toUpperCase() + "%"));
	context.productStoreShipMethId=productStoreShipMethId;
}

if (UtilValidate.isNotEmpty(exprs))
{
	prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
	mainCond=prodCond;
}

orderBy = ["productStoreShipMethId"];

productStoreShipmentMethList=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{
	productStoreShipmentMethList = delegator.findList("ProductStoreShipmentMeth",mainCond, null, orderBy, null, false);
}


context.resultList = productStoreShipmentMethList;



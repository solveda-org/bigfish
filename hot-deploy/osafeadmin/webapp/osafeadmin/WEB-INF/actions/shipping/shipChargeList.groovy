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


preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);

if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}





nowTs = UtilDateTime.nowTimestamp();
session = context.session;
productStore = ProductStoreWorker.getProductStore(request);

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
context.userLogin=userLogin;

exprs = FastList.newInstance();
mainCond=null;
prodCond=null;
statusCond=null;

if(productStoreId)
{

    exprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
}

// Product Store Ship Method Id
if(srchProductStoreShipMethId)
{
    productStoreShipMethId=srchProductStoreShipMethId;
    exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productStoreShipMethId"), EntityOperator.LIKE, "%" + productStoreShipMethId.toUpperCase() + "%"));
    context.productStoreShipMethId=productStoreShipMethId;
}

if (UtilValidate.isNotEmpty(exprs)) {
    prodCond=EntityCondition.makeCondition(exprs, EntityOperator.AND);
    mainCond=prodCond;
}

orderBy = ["productStoreShipMethId"];

productStoreShipmentMethList=FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
 {
	productStoreShipmentMethList = delegator.findList("ProductStoreShipmentMeth",mainCond, null, orderBy, null, true);
 }
 
 
pagingListSize=productStoreShipmentMethList.size();
context.pagingListSize=pagingListSize;
context.pagingList = productStoreShipmentMethList;



package promotion;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;


srchPromoName = StringUtils.trimToEmpty(parameters.srchPromoName);
srchPromoDesc = StringUtils.trimToEmpty(parameters.srchPromoDesc);
productPromoCode = StringUtils.trimToEmpty(parameters.productPromoCodeId);
if (UtilValidate.isNotEmpty(productPromoCode))
{
    parameters.srchPromoCode=productPromoCode;
}
srchPromoCode = StringUtils.trimToEmpty(parameters.srchPromoCode);
searchByAll=StringUtils.trimToEmpty(parameters.srchall);
srchPromoActive = StringUtils.trimToEmpty(parameters.srchPromoActive);
srchPromoExpired = StringUtils.trimToEmpty(parameters.srchPromoExpired);

initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);
if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}

Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("userLogin", userLogin);
svcCtx.put("showAll", "N");
svcCtx.put("searchPartial", "Y");
svcCtx.put("searchDistinct", "N");
svcCtx.put("productStoreId",context.productStoreId);

if (UtilValidate.isNotEmpty(srchPromoName))
{
    svcCtx.put("promoName", srchPromoName);
}
if (UtilValidate.isNotEmpty(srchPromoDesc))
{
    svcCtx.put("promoText", srchPromoDesc);
}
if (UtilValidate.isNotEmpty(srchPromoCode))
{
    svcCtx.put("productPromoCodeId", srchPromoCode);
}

if ((UtilValidate.isNotEmpty(srchPromoActive) && UtilValidate.isEmpty(srchPromoExpired)) || (UtilValidate.isEmpty(searchByAll) && UtilValidate.isEmpty(srchPromoActive) && UtilValidate.isEmpty(srchPromoExpired)))
{
    //svcCtx.put("productPromoCodeStatus", "ACTIVE");
    svcCtx.put("productStorePromoApplStatus", "ACTIVE");
}

if (UtilValidate.isEmpty(srchPromoActive) && UtilValidate.isNotEmpty(srchPromoExpired))
{
    //svcCtx.put("productPromoCodeStatus", "INACTIVE");
    svcCtx.put("productStorePromoApplStatus", "INACTIVE");
}
Map<String, Object> svcRes;
List<GenericValue> productPromoList = FastList.newInstance();
if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N")
{

    svcRes = dispatcher.runSync("findPromotion", svcCtx);
    context.pagingList = svcRes.get("completeProductPromoList");
    context.pagingListSize = svcRes.get("productPromoListSize");
}
else
{
    context.pagingList = productPromoList;
    context.pagingListSize = productPromoList.size();
}

if (UtilValidate.isNotEmpty(context.pagingList))
{
    productPromoUsage = FastMap.newInstance();
    // top promotion dynamic view entity
    DynamicViewEntity topPromotionDve = new DynamicViewEntity();
    topPromotionDve.addMemberEntity("OH", "OrderHeader");
    topPromotionDve.addAlias("OH", "totalOrders", "orderTypeId", null, null, null, "count");
    topPromotionDve.addAlias("OH", "orderDate", "orderDate", null, null, null, null);
    //make relation with OrderItem
    topPromotionDve.addRelation("many", "", "OrderProductPromoCode", UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    topPromotionDve.addMemberEntity("OPPC", "OrderProductPromoCode");
    topPromotionDve.addAlias("OPPC", "productPromoCodeId", "productPromoCodeId", null, null, Boolean.TRUE, null);
    topPromotionDve.addViewLink("OH", "OPPC", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    //make relation with ProductStore
    topPromotionDve.addRelation("one-fk", "", "ProductStore", UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));
    topPromotionDve.addMemberEntity("PS", "ProductStore");
    topPromotionDve.addAlias("PS", "productStoreId", "productStoreId", null, null, null, null);
    topPromotionDve.addViewLink("OH", "PS", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("productStoreId", "productStoreId")));

    orderBy = UtilMisc.toList("-totalOrders");
    //FieldsToSelect
    List topPromotionFields = FastList.newInstance();
    topPromotionFields.add("totalOrders");
    topPromotionFields.add("productPromoCodeId");
    // set distinct
    topPromotionFindOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);


    for (productPromo in context.pagingList) 
    {

         ecl = EntityCondition.makeCondition([
             EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, globalContext.productStoreId),
             EntityCondition.makeCondition("productPromoCodeId", EntityOperator.EQUALS, productPromo.productPromoCodeId)
             ],
             EntityOperator.AND);
    
         eli = delegator.findListIteratorByCondition(topPromotionDve, ecl, null, topPromotionFields, orderBy, topPromotionFindOpts);
         productPromoUses = eli.getCompleteList();
         if (eli != null) 
         {
             try {
                 eli.close();
             } catch (GenericEntityException e) {}
         }
         if (UtilValidate.isNotEmpty(productPromoUses)) {
             productPromoUse = EntityUtil.getFirst(productPromoUses);
             productPromoUsage.put(productPromo.productPromoCodeId, productPromoUse.totalOrders);
         } else if (UtilValidate.isNotEmpty(productPromo.productPromoCodeId)){
             productPromoUsage.put(productPromo.productPromoCodeId, 0);
         }
    }
    context.productPromoUsage = productPromoUsage;
}
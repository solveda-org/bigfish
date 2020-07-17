package promotion;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;


// productStoreId    productPromoApplEnums  selectedProductPromoApplEnum
// inputParamEnums  selectedInputParamEnum  condOperEnums  selectedCondOperEnum
// productPromoActionEnums selectedProductPromoActionEnum
if (UtilValidate.isNotEmpty(globalContext.productStore))
{
    productStoreId = productStore.productStoreId;
    context.productStoreId = productStoreId;
}
// list to find all the shipmentMethods from the view named "ProductStoreShipmentMethView".
if (UtilValidate.isNotEmpty(context.productStoreId)) 
{
    context.productStoreShipmentMethList = delegator.findByAnd('ProductStoreShipmentMethView', [productStoreId: context.productStoreId], ['sequenceNumber']);
}
conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "PROD_PROMO_PCAPPL"));
conditions.add(EntityCondition.makeCondition("sequenceId", EntityOperator.IN, ["01", "02"]));
mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
productPromoApplEnums = delegator.findList("Enumeration", mainCond, null, ["sequenceId"], null, false);
if (UtilValidate.isNotEmpty(productPromoApplEnums))
{
    context.productPromoApplEnums = productPromoApplEnums;
    context.selectedProductPromoApplEnum = "PPPA_INCLUDE";
}


conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "PROD_PROMO_IN_PARAM"));
conditions.add(EntityCondition.makeCondition("sequenceId", EntityOperator.IN, ["01", "02", "03", "04", "16"]));
mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
inputParamEnums = delegator.findList("Enumeration", mainCond, null, ["sequenceId"], null, false);;
if (UtilValidate.isNotEmpty(inputParamEnums))
{
    context.inputParamEnums = inputParamEnums;
    context.selectedInputParamEnum = "PPIP_ORDER_TOTAL";
}

condOperEnums = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "PROD_PROMO_COND"), null, ["sequenceId"], null, false);
if (UtilValidate.isNotEmpty(condOperEnums))
{
    context.condOperEnums = condOperEnums;
    context.selectedCondOperEnum = "PPC_GT";
}

conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "PROD_PROMO_ACTION"));
conditions.add(EntityCondition.makeCondition("sequenceId", EntityOperator.NOT_IN, ["10"]));
mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
productPromoActionEnums = delegator.findList("Enumeration", mainCond, null, ["sequenceId"], null, false);
if (UtilValidate.isNotEmpty(productPromoActionEnums))
{
    context.productPromoActionEnums = productPromoActionEnums;
    context.selectedProductPromoActionEnum = "PROMO_GWP";
}

productPromoId = StringUtils.trimToEmpty(parameters.productPromoId);
productPromotionId = StringUtils.trimToEmpty(parameters.productPromotionId);
productPromoCodeId = StringUtils.trimToEmpty(parameters.productPromoCodeId);

if (UtilValidate.isNotEmpty(productPromoCodeId))
{
    // variables name which going to be set in context
    // productPromoCodeId productPromoCode    productPromo    productStorePromoAppl    productStoreId

    context.productPromoCodeId = productPromoCodeId;

    productPromoCode = delegator.findByAnd("ProductPromoCode", [productPromoCodeId : productPromoCodeId]);
    if (UtilValidate.isNotEmpty(productPromoCode))
    {
        context.productPromoCode = EntityUtil.getFirst(productPromoCode);

        productPromo = delegator.findByAnd("ProductPromo", [productPromoId : productPromoCode.productPromoId]);
        if (UtilValidate.isNotEmpty(productPromo))
        {
            context.productPromo = EntityUtil.getFirst(productPromo);

            productStorePromoApplList = delegator.findByAnd("ProductStorePromoAppl", [productPromoId : productPromo.productPromoId]);
            if (UtilValidate.isNotEmpty(productStorePromoApplList))
            {
                productStorePromoAppl = EntityUtil.getFirst(productStorePromoApplList);
                context.productStorePromoAppl = productStorePromoAppl;
                context.productStoreId = productStorePromoAppl.productStoreId;
            }

        }

    }
}

if (UtilValidate.isNotEmpty(productPromotionId))
{
    // variables name which going to be set in context
    // productPromotionId      productPromo           productStorePromoAppl    productStoreId

    context.productPromotionId = productPromotionId;

    productPromo = delegator.findByPrimaryKey("ProductPromo", [productPromoId : productPromotionId]);
    context.productPromo = productPromo;
 
    productStorePromoApplList = delegator.findByAnd("ProductStorePromoAppl", [productPromoId : productPromotionId, productStoreId : context.productStoreId]);
    if (UtilValidate.isNotEmpty(productStorePromoApplList))
    {
        productStorePromoAppl = EntityUtil.getFirst(productStorePromoApplList);
        context.productStorePromoAppl = productStorePromoAppl;
        context.productStoreId = productStorePromoAppl.productStoreId;
    }
}

if (UtilValidate.isNotEmpty(productPromoId))
{
    // variables name which going to be set in context
    // productPromoId      productPromo           productStorePromoAppl    productStoreId     productPromoRule
    // productPromoCond    productPromoCondCategoryList    productPromoCondProductList
    // productPromoAction  productPromoActionCategoryList  productPromoActionProductList

    context.productPromoId = productPromoId;

    productPromo = delegator.findByPrimaryKey("ProductPromo", [productPromoId : productPromoId]);
    context.productPromo = productPromo;
 
    productStorePromoApplList = delegator.findByAnd("ProductStorePromoAppl", [productPromoId : productPromoId, productStoreId : context.productStoreId]);
    if (UtilValidate.isNotEmpty(productStorePromoApplList))
    {
        productStorePromoAppl = EntityUtil.getFirst(productStorePromoApplList);
        context.productStorePromoAppl = productStorePromoAppl;
        context.productStoreId = productStorePromoAppl.productStoreId;
    }

    productPromoRuleList = delegator.findByAnd("ProductPromoRule", [productPromoId : productPromoId]);
    if (UtilValidate.isNotEmpty(productPromoRuleList))
    {
        productPromoRule = EntityUtil.getFirst(productPromoRuleList);
        context.productPromoRule = productPromoRule;

        findProductPromoCondMap = ["productPromoId" : productPromoId, "productPromoRuleId" : productPromoRule.productPromoRuleId];
        productPromoCondList = delegator.findByAnd("ProductPromoCond", findProductPromoCondMap);
        if (UtilValidate.isNotEmpty(productPromoCondList))
        {
            productPromoCond = EntityUtil.getFirst(productPromoCondList);
            context.productPromoCond = productPromoCond;

            findProductPromoCondCategoryMap =  ["productPromoId" : productPromoId, "productPromoRuleId" : productPromoRule.productPromoRuleId, "productPromoCondSeqId" : productPromoCond.productPromoCondSeqId];
            productPromoCondCategoryList = delegator.findByAnd("ProductPromoCategory", findProductPromoCondCategoryMap);
            if (UtilValidate.isNotEmpty(productPromoCondCategoryList))
            {
                context.productPromoCondCategoryList = productPromoCondCategoryList;
             }

             findProductPromoCondProductMap =  ["productPromoId" : productPromoId, "productPromoRuleId" : productPromoRule.productPromoRuleId, "productPromoCondSeqId" : productPromoCond.productPromoCondSeqId];
             productPromoCondProductList = delegator.findByAnd("ProductPromoProduct", findProductPromoCondProductMap);
             if (UtilValidate.isNotEmpty(productPromoCondProductList))
             {
                 context.productPromoCondProductList = productPromoCondProductList;
             }

         }

         findProductPromoActionMap = ["productPromoId" : productPromoId, "productPromoRuleId" : productPromoRule.productPromoRuleId];
         productPromoActionList = delegator.findByAnd("ProductPromoAction", findProductPromoActionMap);
         if (UtilValidate.isNotEmpty(productPromoActionList))
         {
             productPromoAction = EntityUtil.getFirst(productPromoActionList);
             context.productPromoAction = productPromoAction;

             findProductPromoActionCategoryMap =  ["productPromoId" : productPromoId, "productPromoRuleId" : productPromoRule.productPromoRuleId, "productPromoActionSeqId" : productPromoAction.productPromoActionSeqId];
             productPromoActionCategoryList = delegator.findByAnd("ProductPromoCategory", findProductPromoActionCategoryMap);
             if (UtilValidate.isNotEmpty(productPromoActionCategoryList))
             {
                 context.productPromoActionCategoryList = productPromoActionCategoryList;
             }

             findProductPromoActionProductMap =  ["productPromoId" : productPromoId, "productPromoRuleId" : productPromoRule.productPromoRuleId, "productPromoActionSeqId" : productPromoAction.productPromoActionSeqId];
             productPromoActionProductList = delegator.findByAnd("ProductPromoProduct", findProductPromoActionProductMap);
             if (UtilValidate.isNotEmpty(productPromoActionProductList))
             {
                 context.productPromoActionProductList = productPromoActionProductList;
             }

        }
        Map<String, Object> svcCtx = FastMap.newInstance();
        userLogin = session.getAttribute("userLogin");
        svcCtx.put("userLogin", userLogin);
        svcCtx.put("showAll", "N");
        svcCtx.put("searchPartial", "N");
        svcCtx.put("searchDistinct", "N");
        if (UtilValidate.isNotEmpty(context.productStoreId))
        {
            svcCtx.put("productStoreId", context.productStoreId);
        }
        svcCtx.put("productPromoId", productPromoId);
        svcRes = dispatcher.runSync("findPromotion", svcCtx);
        context.productPromoList = svcRes.get("completeProductPromoList");
        
        if (UtilValidate.isNotEmpty(context.productPromoList))
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


            for (productPromo in context.productPromoList) 
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
                 if (UtilValidate.isNotEmpty(productPromoUses)) 
                 {
                     productPromoUse = EntityUtil.getFirst(productPromoUses);
                     productPromoUsage.put(productPromo.productPromoCodeId, productPromoUse.totalOrders);
                 } else if (UtilValidate.isNotEmpty(productPromo.productPromoCodeId))
                 {
                     productPromoUsage.put(productPromo.productPromoCodeId, 0);
                 }
            }
            context.productPromoUsage = productPromoUsage;
        }
    }
}

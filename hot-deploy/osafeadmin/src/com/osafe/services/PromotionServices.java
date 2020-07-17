package com.osafe.services;


import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class PromotionServices {

    public static final String module = PromotionServices.class.getName();

    public static Map<String, Object> findPromotion(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String showAll = (String) context.get("showAll");
        if (showAll == null) {
            showAll = "N";
        }

        Boolean partialCompare = Boolean.FALSE;
        String searchPartial = (String) context.get("searchPartial");
        if("Y".equals(searchPartial)) {
            partialCompare = Boolean.TRUE;
        }

        Boolean distinct = Boolean.TRUE; // if this is true then projection field should belong only from ProductPromo
        String searchDistinct = (String) context.get("searchDistinct");
        if("N".equals(searchDistinct)) {
            distinct = Boolean.FALSE;
        }

        // sorting by productPromoId first
        List orderBy = UtilMisc.toList("-productPromoId");

        // list to hold the parameters
        List paramList = FastList.newInstance();

        // list of conditions
        List conditions = FastList.newInstance();

        // dynamic view entity
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        dynamicView.addMemberEntity("PP", "ProductPromo");
        dynamicView.addAlias("PP", "productPromoId");
        dynamicView.addAlias("PP", "promoName");
        dynamicView.addAlias("PP", "promoText");
        dynamicView.addAlias("PP", "productPromo" + "UserEntered", "userEntered", null, null, null, null);
        dynamicView.addAlias("PP", "showToCustomer");
        dynamicView.addAlias("PP", "requireCode");
        dynamicView.addAlias("PP", "useLimitPerOrder");
        dynamicView.addAlias("PP", "productPromo" + "UseLimitPerCustomer", "useLimitPerCustomer", null, null, null, null);
        dynamicView.addAlias("PP", "useLimitPerPromotion");
        dynamicView.addAlias("PP", "billbackFactor");
        dynamicView.addAlias("PP", "overrideOrgPartyId");
        dynamicView.addRelation("many", "", "ProductPromoCode", UtilMisc.toList(new ModelKeyMap("productPromoId", "productPromoId")));
        dynamicView.addRelation("many", "", "ProductStorePromoAppl", UtilMisc.toList(new ModelKeyMap("productPromoId", "productPromoId")));

        // list of fields to select
        List fieldsToSelect = FastList.newInstance();
        fieldsToSelect.add("productPromoId");
        fieldsToSelect.add("promoName");
        fieldsToSelect.add("promoText");
        fieldsToSelect.add("productPromo" + "UserEntered");
        fieldsToSelect.add("showToCustomer");
        fieldsToSelect.add("requireCode");
        fieldsToSelect.add("useLimitPerOrder");
        fieldsToSelect.add("productPromo" + "UseLimitPerCustomer");
        fieldsToSelect.add("useLimitPerPromotion");
        fieldsToSelect.add("billbackFactor");
        fieldsToSelect.add("overrideOrgPartyId");

        // filter on productPromoId
        String productPromoId = (String) context.get("productPromoId");
        if (UtilValidate.isNotEmpty(productPromoId)) {
            paramList.add("productPromoId=" + productPromoId);
            conditions.add(makeExpr("productPromoId", productPromoId, partialCompare, true));
        } else {
            conditions.add(makeExpr("productPromoId", "*", partialCompare, true));
        }

        // filter on promoName
        String promoName = (String) context.get("promoName");
        if (UtilValidate.isNotEmpty(promoName)) {
            paramList.add("promoName=" + promoName);
            conditions.add(makeExpr("promoName", promoName, true, true));
        }

        // filter on promoText
        String promoText = (String) context.get("promoText");
        if (UtilValidate.isNotEmpty(promoText)) {
            paramList.add("promoText=" + promoText);
            conditions.add(makeExpr("promoText", promoText, true, true));
        }

        dynamicView.addMemberEntity("PPC", "ProductPromoCode");
        dynamicView.addAlias("PPC", "productPromoCodeId");
        dynamicView.addAlias("PPC", "productPromoCode" + "UserEntered", "userEntered", null, null, null, null);
        dynamicView.addAlias("PPC", "requireEmailOrParty");
        dynamicView.addAlias("PPC", "useLimitPerCode");
        dynamicView.addAlias("PPC", "productPromoCode" + "UseLimitPerCustomer", "useLimitPerCustomer", null, null, null, null);
        dynamicView.addAlias("PPC", "productPromoCode" + "FromDate", "fromDate", null, null, null, null);
        dynamicView.addAlias("PPC", "productPromoCode" + "ThruDate", "thruDate", null, null, null, null);
        // left outer join
        dynamicView.addViewLink("PP", "PPC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productPromoId"));

        //if searchDistinct is set by N then project these field
        if (!distinct) {
            fieldsToSelect.add("productPromoCodeId");
            fieldsToSelect.add("productPromoCode" + "UserEntered");
            fieldsToSelect.add("requireEmailOrParty");
            fieldsToSelect.add("useLimitPerCode");
            fieldsToSelect.add("productPromoCode" + "UseLimitPerCustomer");
            fieldsToSelect.add("productPromoCode" + "FromDate");
            fieldsToSelect.add("productPromoCode" + "ThruDate");
        }
        // filter on productPromoCodeId
        String productPromoCodeId = (String) context.get("productPromoCodeId");
        if (UtilValidate.isNotEmpty(productPromoCodeId)) {
            paramList.add("productPromoCodeId=" + productPromoCodeId);
            conditions.add(makeExpr("productPromoCodeId", productPromoCodeId, partialCompare, true));
        }

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        // filter on productPromoCodeStatus
        String productPromoCodeStatus = (String) context.get("productPromoCodeStatus");
        if (UtilValidate.isNotEmpty(productPromoCodeStatus) && "ACTIVE".equalsIgnoreCase(productPromoCodeStatus)) {
            paramList.add("productPromoCodeStatus=" + productPromoCodeStatus);
            conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productPromoCode" + "ThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("productPromoCode" + "ThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp)));
            conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productPromoCode" + "FromDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("productPromoCode" + "FromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp)));
        } else if (UtilValidate.isNotEmpty(productPromoCodeStatus) && "INACTIVE".equalsIgnoreCase(productPromoCodeStatus)) {
            paramList.add("productPromoCodeStatus=" + productPromoCodeStatus);
            conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productPromoCode" + "ThruDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp), EntityOperator.OR, EntityCondition.makeCondition("productPromoCode" + "FromDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp)));
        }

        dynamicView.addMemberEntity("PSP", "ProductStorePromoAppl");
        dynamicView.addAlias("PSP", "productStoreId");
        dynamicView.addAlias("PSP", "productStorePromoAppl" + "FromDate", "fromDate", null, null, null, null);
        dynamicView.addAlias("PSP", "productStorePromoAppl" + "ThruDate", "thruDate", null, null, null, null);
        // left outer join  
        dynamicView.addViewLink("PP", "PSP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productPromoId"));
 
        //if searchDistinct is set by N then project these field
        if (!distinct) {
            fieldsToSelect.add("productStoreId");
            fieldsToSelect.add("productStorePromoAppl" + "FromDate");
            fieldsToSelect.add("productStorePromoAppl" + "ThruDate");
        }
        // filter on productStoreId
        String productStoreId = (String) context.get("productStoreId");
        if (UtilValidate.isNotEmpty(productStoreId)) {
            paramList.add("productStoreId=" + productStoreId);
            conditions.add(makeExpr("productStoreId", productStoreId, partialCompare, true));
        }

        // filter on productStorePromoApplStatus
        String productStorePromoApplStatus = (String) context.get("productStorePromoApplStatus");
        if (UtilValidate.isNotEmpty(productStorePromoApplStatus) && "ACTIVE".equalsIgnoreCase(productStorePromoApplStatus)) {
            paramList.add("productStorePromoApplStatus=" + productStorePromoApplStatus);
            conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productStorePromoAppl" + "ThruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("productStorePromoAppl" + "ThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp)));
            conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productStorePromoAppl" + "FromDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("productStorePromoAppl" + "FromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp)));
        } else if (UtilValidate.isNotEmpty(productStorePromoApplStatus) && "INACTIVE".equalsIgnoreCase(productStorePromoApplStatus)) {
            paramList.add("productStorePromoApplStatus=" + productStorePromoApplStatus);
            conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("productStorePromoAppl" + "ThruDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp), EntityOperator.OR, EntityCondition.makeCondition("productStorePromoAppl" + "FromDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp)));
        }

        // set the page parameters
        int viewIndex = 1;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 1;
        }
        result.put("viewIndex", Integer.valueOf(viewIndex));

        int viewSize = 20;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 20;
        }
        result.put("viewSize", Integer.valueOf(viewSize));
        List<GenericValue> productPromoList = null;
        List completeProductPromoList = FastList.newInstance();
        
        int productPromoListSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        // create the main condition
        EntityCondition mainCond = null;;
        if (conditions.size() > 0 || showAll.equalsIgnoreCase("Y")) {
            mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
        }
        // do the lookup
        if (mainCond != null || "Y".equals(showAll)) {
            try {
                // get the indexes for the partial list
                lowIndex = (((viewIndex - 1) * viewSize) + 1);
                highIndex = viewIndex * viewSize;

                // select all value
                EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, distinct);

                // using list iterator
                EntityListIterator pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);

                // attempt to get the full size
                productPromoListSize = pli.getResultsSizeAfterPartialList();
                
                // Always return the complete list
                // The next branch of logic will factor in the view size
                if (productPromoListSize > 0)
                {
                    completeProductPromoList = pli.getCompleteList();
                }
                
                pli.beforeFirst();
                if (productPromoListSize > viewSize) {
                    productPromoList = pli.getPartialList(lowIndex, viewSize);
                } else if (productPromoListSize > 0) {
                    productPromoList = pli.getCompleteList();
                }
                
                // get the partial list for this page

                if (highIndex > productPromoListSize) {
                    highIndex = productPromoListSize;
                }

                // close the list iterator
                pli.close();
            } catch (GenericEntityException e) {
                String errMsg = "Failure in ProductPromoCode find operation, rolling back transaction: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        } else {
            productPromoListSize = 0;
        }
        
        // format the param list
        String paramString = StringUtil.join(paramList, "&amp;");
        
        if (productPromoList == null) productPromoList = FastList.newInstance();
        result.put("productPromoList", productPromoList);
        result.put("completeProductPromoList", completeProductPromoList);
        result.put("productPromoListSize", Integer.valueOf(productPromoListSize));
        result.put("paramList", (paramString != null? paramString: ""));
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("lowIndex", Integer.valueOf(lowIndex));
        return result;
    

    }

    protected static EntityExpr makeExpr(String fieldName, String value) {
        return makeExpr(fieldName, value, false, false);
    }

    protected static EntityExpr makeExpr(String fieldName, String value, boolean forceLike) {
        return makeExpr(fieldName, value, forceLike, false);
    }

    protected static EntityExpr makeExpr(String fieldName, String value, boolean forceLike, boolean caseInsensitive) {
        EntityComparisonOperator op = forceLike ? EntityOperator.LIKE : EntityOperator.EQUALS;

        if (value.startsWith("*")) {
            op = EntityOperator.LIKE;
            value = "%" + value.substring(1);
        }
        else if (value.startsWith("%")) {
            op = EntityOperator.LIKE;
        }

        if (value.endsWith("*")) {
            op = EntityOperator.LIKE;
            value = value.substring(0, value.length() - 1) + "%";
        }
        else if (value.endsWith("%")) {
            op = EntityOperator.LIKE;
        }

        if (forceLike) {
            if (!value.startsWith("%")) {
                value = "%" + value;
            }
            if (!value.endsWith("%")) {
                value = value + "%";
            }
        }

        EntityExpr retExpr = EntityCondition.makeCondition(fieldName, op, value);
        if(caseInsensitive){
            retExpr = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD(fieldName), op, EntityFunction.UPPER(value));
        }

        return retExpr;
    }
}

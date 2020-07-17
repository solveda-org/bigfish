package com.osafe.services;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class ReevooServices {

    public static final String module = ReevooServices.class.getName();

    public static Map genReevooProductsFeed(DispatchContext dctx, Map context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        String productStoreId = (String) context.get("productStoreId");
        String browseRootProductCategoryId = (String) context.get("browseRootProductCategoryId");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        List<String> headerColumns = FastList.newInstance();
        headerColumns.addAll(UtilMisc.toList("manufacturer", "model", "sku", "name"));
        headerColumns.addAll(UtilMisc.toList("image-url", "product-category"));

        try {

            // Find Product Store - to find store's currency setting
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);

            // Get all unexpired Product Categories (Top Level Catalog Category)
            List<Map<String, Object>> allUnexpiredCategories = getRelatedCategories(delegator, browseRootProductCategoryId, null, true, true, true);
            GenericValue workingCategory = null;
            for (Map<String, Object> workingCategoryMap : allUnexpiredCategories) {
                workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
                // Only index products under "Catalog Categories"
                if ("CATALOG_CATEGORY".equals(workingCategory.getString("productCategoryTypeId"))) {

                    // For each category get all products
                    List<GenericValue> productCategoryMembers = workingCategory.getRelated("ProductCategoryMember");
                    productCategoryMembers = EntityUtil.orderBy(productCategoryMembers,UtilMisc.toList("sequenceNum"));

                    // Remove any expired
                    productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                    for (GenericValue productCategoryMember : productCategoryMembers) {
                        GenericValue product = productCategoryMember.getRelatedOne("Product");
                        if (UtilValidate.isNotEmpty(product)) {
                            String isVariant = product.getString("isVariant");
                            if (UtilValidate.isEmpty(isVariant)) {
                                isVariant = "N";
                            }
                            // All Non-Variant Products
                            if ("N".equals(isVariant)) {
                            
                            }
                        
                        }
                    
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError(e, e.getMessage(), module);
        }
        return result;
    }

    private static List<Map<String, Object>> getRelatedCategories(Delegator delegator, String parentId, List<String> categoryTrail, boolean limitView, boolean excludeEmpty, boolean recursive) {
        List<Map<String, Object>> categories = FastList.newInstance();
        if (categoryTrail == null) {
            categoryTrail = FastList.newInstance();
        }
        categoryTrail.add(parentId);
        if (Debug.verboseOn())
            Debug.logVerbose("[SolrServices.getRelatedCategories] ParentID: " + parentId, module);

        List<GenericValue> rollups = null;

        try {
            rollups = delegator.findByAndCache("ProductCategoryRollup", UtilMisc.toMap("parentProductCategoryId", parentId), UtilMisc.toList("sequenceNum"));
            if (limitView) {
                rollups = EntityUtil.filterByDate(rollups, true);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }
        if (rollups != null) {
            // Debug.log("Rollup size: " + rollups.size(), module);
            for (GenericValue parent : rollups) {
                // Debug.log("Adding child of: " +
                // parent.getString("parentProductCategoryId"), module);
                GenericValue cv = null;
                Map<String, Object> cvMap = FastMap.newInstance();

                try {
                    cv = parent.getRelatedOneCache("CurrentProductCategory");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                }
                if (cv != null) {

                    if (excludeEmpty) {
                        if (!CategoryWorker.isCategoryEmpty(cv)) {
                            // Debug.log("Child : " +
                            // cv.getString("productCategoryId") +
                            // " is not empty.", module);
                            cvMap.put("ProductCategory", cv);
                            categories.add(cvMap);
                            if (recursive) {
                                categories.addAll(getRelatedCategories(delegator, cv.getString("productCategoryId"), categoryTrail, limitView, excludeEmpty, recursive));
                            }
                            List<String> popList = FastList.newInstance();
                            popList.addAll(categoryTrail);
                            cvMap.put("categoryTrail", popList);
                            categoryTrail.remove(categoryTrail.size() - 1);
                        }
                    } else {
                        cvMap.put("ProductCategory", cv);
                        cvMap.put("parentProductCategoryId", parent.getString("parentProductCategoryId"));
                        categories.add(cvMap);
                        if (recursive) {
                            categories.addAll(getRelatedCategories(delegator, cv.getString("productCategoryId"), categoryTrail, limitView, excludeEmpty, recursive));
                        }
                        List<String> popList = FastList.newInstance();
                        popList.addAll(categoryTrail);
                        cvMap.put("categoryTrail", popList);
                        categoryTrail.remove(categoryTrail.size() - 1);
                    }
                }
            }
        }
        return categories;
    }

    private static GenericValue getTopMostParentProductCategory(Delegator delegator, String productCategoryId, String browseRootProductCategoryId) {
        GenericValue gvTopMost = null;
        if (Debug.verboseOn())
            Debug.logVerbose("[SolrServices.getTopParentProductCategory] productCategoryId: " + productCategoryId + ", browseRootProductCategoryId: " + browseRootProductCategoryId, module);

        List<GenericValue> rollups = null;

        try {
            rollups = delegator.findByAndCache("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", productCategoryId), UtilMisc.toList("sequenceNum"));
            rollups = EntityUtil.filterByDate(rollups, true);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }
        String parentProductCategoryId = null;
        if (rollups != null) {
            for (GenericValue child : rollups) {
                parentProductCategoryId = child.getString("parentProductCategoryId");
                if (parentProductCategoryId.equals(browseRootProductCategoryId)) {
                    return child;
                } else {
                    GenericValue topMostParentRollup = getTopMostParentProductCategory(delegator, parentProductCategoryId, browseRootProductCategoryId);
                    if (UtilValidate.isNotEmpty(topMostParentRollup)) {
                        try {
                            gvTopMost = topMostParentRollup.getRelatedOneCache("CurrentProductCategory");
                        } catch (GenericEntityException e) {
                            Debug.logWarning(e.getMessage(), module);
                        }
                    }
                }
            }
        }

        return gvTopMost;
    }
}

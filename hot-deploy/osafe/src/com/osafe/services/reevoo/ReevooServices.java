package com.osafe.services.reevoo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilIO;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.osafe.util.Util;

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
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), true);

            // Get all unexpired Product Categories (Top Level Catalog Category)
            List<Map<String, Object>> allUnexpiredCategories = getRelatedCategories(delegator, browseRootProductCategoryId, null, true, true, true);
            GenericValue workingCategory = null;
            for (Map<String, Object> workingCategoryMap : allUnexpiredCategories) {
                workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
                // Only index products under "Catalog Categories"
                if ("CATALOG_CATEGORY".equals(workingCategory.getString("productCategoryTypeId"))) {

                    // For each category get all products
                    List<GenericValue> productCategoryMembers = workingCategory.getRelatedCache("ProductCategoryMember");
                    productCategoryMembers = EntityUtil.orderBy(productCategoryMembers,UtilMisc.toList("sequenceNum"));

                    // Remove any expired
                    productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                    for (GenericValue productCategoryMember : productCategoryMembers) {
                        GenericValue product = productCategoryMember.getRelatedOneCache("Product");
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

    public static Map reevooGetProductRatingScore(DispatchContext dctx, Map context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String productStoreId = (String) context.get("productStoreId");
        String reevooApiUrl = (String) context.get("reevooApiUrl");
        String reevooApiCsvUrl = (String) context.get("reevooApiCsvUrl");
        String reevooApiUserName = (String) context.get("reevooApiUserName");
        String reevooApiPassword = (String) context.get("reevooApiPassword");
        String feedsInRatingDir = (String)context.get("feedsInRatingDir");

        // Check passed params
        if (UtilValidate.isEmpty(feedsInRatingDir)) 
        {
        	feedsInRatingDir = Util.getProductStoreParm(productStoreId, "FEEDS_IN_RATING_URL_DIR");
        }
        if (UtilValidate.isEmpty(reevooApiUrl)) 
        {
        	reevooApiUrl = Util.getProductStoreParm(productStoreId, "REEVOO_API_URL");
        }
        if (UtilValidate.isEmpty(reevooApiCsvUrl)) 
        {
        	reevooApiCsvUrl = Util.getProductStoreParm(productStoreId, "REEVOO_CSV_URL");
        }
        if (UtilValidate.isEmpty(reevooApiUserName)) 
        {
        	reevooApiUserName = Util.getProductStoreParm(productStoreId, "REEVOO_USERNAME");
        }
        if (UtilValidate.isEmpty(reevooApiPassword)) 
        {
        	reevooApiPassword = Util.getProductStoreParm(productStoreId, "REEVOO_PASSWORD");
        }
        try {
            File csvFile = null;
            File xmlFile = null;
            String xmlFileAsString = "";
            Map<String, Object> csvInput = UtilMisc.toMap("userLogin", userLogin,
                    "productStoreId", productStoreId, "reevooApiUrl", reevooApiUrl,
                    "reevooApiCsvUrl", reevooApiCsvUrl, "reevooApiUserName", reevooApiUserName, "reevooApiPassword", reevooApiPassword);
            try {
               
                Map<String, Object> outputMap = dispatcher.runSync("getReevooCsvFeed", csvInput);
                if (ModelService.RESPOND_ERROR.equals(outputMap.get(ModelService.RESPONSE_MESSAGE))) {
                    return ServiceUtil.returnError((String) outputMap.get(ModelService.ERROR_MESSAGE));
                } else {
                    csvFile = (File) outputMap.get("csvFile");
                    Map<String, Object> feedInput = UtilMisc.toMap("userLogin", userLogin, "reevooCsvFileLoc", csvFile.getAbsolutePath());
                    Map<String, Object> feedOutputMap = dispatcher.runSync("importReevooCsvToFeed", feedInput);

                    if (ModelService.RESPOND_ERROR.equals(feedOutputMap.get(ModelService.RESPONSE_MESSAGE))) {
                        return ServiceUtil.returnError((String) feedOutputMap.get(ModelService.ERROR_MESSAGE));
                    } else {
                        xmlFile = (File) feedOutputMap.get("feedFile");
                        xmlFileAsString = (String) feedOutputMap.get("feedFileAsString");
                        // copy xml file in feed in rating directory
                        if (UtilValidate.isNotEmpty(feedsInRatingDir)) {
                            File baseDir = new File(feedsInRatingDir);
                            if (baseDir.isDirectory() && baseDir.canWrite()) {
                                try {
                                    FileUtils.copyFileToDirectory(xmlFile, baseDir);
                                } catch (IOException e) {
                                    Debug.logError(e, "Error occured while copy xml file feed In Rating Dir", module);
                                    ServiceUtil.returnError("Error occured while copy xml file feed In Rating Dir");
                                }
                            } else {
                                Debug.logError("Error occured while copy xml file feed In Rating Dir", module);
                                ServiceUtil.returnError("Error occured while copy xml file feed In Rating Dir");
                            }
                        }
                    }
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
             result.put("xmlFile", xmlFile);
             result.put("xmlFileAsString", xmlFileAsString);
        } catch (Exception e) {
            Debug.logError(e, "Error occured in making xml", module);
            ServiceUtil.returnError("Error occured in making xml");
        }
        return result;
    }

    public static Map getReevooCsvFeed(DispatchContext dctx, Map context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productStoreId = (String) context.get("productStoreId");
        String reevooApiUrl = (String) context.get("reevooApiUrl");
        String reevooApiCsvUrl = (String) context.get("reevooApiCsvUrl");
        String reevooApiUserName = (String) context.get("reevooApiUserName");
        String reevooApiPassword = (String) context.get("reevooApiPassword");
        try {
             String buildApiUrl = reevooApiUrl+"/"+reevooApiUserName+"/"+reevooApiCsvUrl;
             String responseString = getHttpResponseAsString(getHttpAuthClient(reevooApiUserName, reevooApiPassword), getHttpGet(buildApiUrl));
             File csvFile = getCsvFile();
             UtilIO.writeString(csvFile, responseString);
             result.put("csvFile", csvFile);
             result.put("csvFileAsString", responseString);
        } catch (Exception e) {
            Debug.logError(e, "Error occured in downloading csv", module);
            ServiceUtil.returnError("Error occured in downloading csv");
        }
        return result;
    }

    private static HttpClient getHttpAuthClient(String userName, String password) {
        HttpClient  httpClient = getHttpClient();
        httpClient.getState().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(userName, password)
                );
        return httpClient;
    }

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        return httpClient;
    }

    private static GetMethod getHttpGet(String uri){
        GetMethod getMethod = new GetMethod(uri);
        return getMethod;
    }

    private static String getHttpResponseAsString(HttpClient httpClient, GetMethod getMethod) throws IOException,HttpException {
        String resultString = "";
        httpClient.executeMethod(getMethod);
        if (getMethod.getStatusCode() == HttpStatus.SC_OK) {
            resultString = UtilIO.readString(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet());
        }
        getMethod.releaseConnection();
        return resultString;
    }

    private static File getCsvFile() throws IOException{
        File csvFile = new File(System.getProperty("ofbiz.home")+"/runtime/tempfiles/reevooCsvFeed.csv");
        if (csvFile.exists()) {
            csvFile.delete();
        }
        csvFile.createNewFile();
        return csvFile;
    }
}

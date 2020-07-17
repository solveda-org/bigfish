package com.osafe.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.HttpClient;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.osafe.solr.SolrConstants;
import com.osafe.util.SerializationUtils;
import com.osafe.util.Util;

public class SolrServices {

    public static final String module = SolrServices.class.getName();

    @SuppressWarnings("unchecked")
    public static Map genProductsIndex(DispatchContext dctx, Map context) throws GenericTransactionException {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        String productStoreId = (String) context.get("productStoreId");
        String browseRootProductCategoryId = (String) context.get("browseRootProductCategoryId");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        // call the updateSolrSchemaXml service for update the schema.xml
//        Map updateSolrSchemaXmlParams = UtilMisc.toMap("userLogin", context.get("userLogin"));
//        try {
//            Map updateSolrSchema = dispatcher.runSync("updateSolrSchemaXml", updateSolrSchemaXmlParams);
//           
//        } catch (Exception exc) {
//            Debug.logError(exc, module);
//        }

        List<ProductDocument> documentList = FastList.newInstance();
        List<String> headerColumns = FastList.newInstance();
        headerColumns.addAll(UtilMisc.toList("id", "rowType", "productId", "name"));
        headerColumns.addAll(UtilMisc.toList("internalName", "description"));
        headerColumns.addAll(UtilMisc.toList("categoryDescription","categoryPdpDescription"));
        headerColumns.addAll(UtilMisc.toList("productCategoryId", "topMostProductCategoryId", "categoryLevel", "categoryName", "categoryImageUrl"));
        headerColumns.addAll(UtilMisc.toList("productImageSmallUrl", "productImageSmallAlt","productImageSmallAltUrl","productImageMediumUrl", "productImageLargeUrl"));
        headerColumns.addAll(UtilMisc.toList("productFeatureGroupId", "productFeatureGroupDescription","productCategoryFacetGroups"));
        headerColumns.addAll(UtilMisc.toList("price", "customerRating","sequenceNum"));
        headerColumns.addAll(UtilMisc.toList("totalTimesViewed","totalQuantityOrdered"));

        List<CellProcessor> cellProcessors = FastList.newInstance();
        cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(null), new ConvertNullTo("product"), new ConvertNullTo(""), new ConvertNullTo("")));
        cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(""), new ConvertNullTo("")));
		cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(""),new ConvertNullTo("")));
        cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(""), new ConvertNullTo(""), new ConvertNullTo(""), new ConvertNullTo(""), new ConvertNullTo("")));
        cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(""),new ConvertNullTo(""), new ConvertNullTo(""), new ConvertNullTo(""), new ConvertNullTo("")));
        cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(""), new ConvertNullTo(""), new ConvertNullTo("")));
        cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(""), new ConvertNullTo(""),new ConvertNullTo("")));
        cellProcessors.addAll(UtilMisc.toList(new ConvertNullTo(""), new ConvertNullTo("")));
        
        List<String> prodFeatureColNames = FastList.newInstance();
        ProductContentWrapper productContentWrapper = null;
        CategoryContentWrapper categoryContentWrapper = null;
        try {

            // Find Product Store - to find store's currency setting
            GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);

            ProductDocument productDocument = null;
            ProductDocument productCategoryDocument = null;
            ProductDocument facetGroupDocument = null;
            Map<String, Object> results = null;
            String productId = null;
            String productCategoryId = null;
            StringUtil.StringWrapper imageUrl = null;
            String categoryDescription = null;
            Double totalQuantityOrdered = 0.00;
            Long totalTimesViewed = 0L;
            
            String productDocumentId = null;

            // Get all unexpired Product Categories (Top Level Catalog Category)
            List<Map<String, Object>> allUnexpiredCategories = getRelatedCategories(delegator, browseRootProductCategoryId, null, true, true, true);

            // All Sub Categries
            GenericValue workingCategory = null;
            String productCategoryIdPath = null;
            int categoryLevel = 0;
            List<String> categoryTrail = null;
            for (Map<String, Object> workingCategoryMap : allUnexpiredCategories) {
                workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");

                // Only index products under "Catalog Categories"
                if ("CATALOG_CATEGORY".equals(workingCategory.getString("productCategoryTypeId"))) {
                    // Add "Product Category" SOLR documents
                    productCategoryDocument = new ProductDocument();
                    productCategoryId = (String) workingCategory.getString("productCategoryId");
                    productCategoryDocument.setId(SolrConstants.ROW_TYPE_PRODUCT_CATEGORY + "_" + productCategoryId);
                    productCategoryDocument.setRowType(SolrConstants.ROW_TYPE_PRODUCT_CATEGORY);
                    categoryContentWrapper = new CategoryContentWrapper(dispatcher, workingCategory, locale, "text/html");

                    categoryTrail = (List<String>) workingCategoryMap.get("categoryTrail");
                    categoryLevel = categoryTrail.size() - 1;
                    productCategoryIdPath = StringUtils.join(categoryTrail, " ");
                    productCategoryDocument.setProductCategoryId(productCategoryIdPath);
                    productCategoryDocument.setCategoryLevel(categoryLevel);
                    productCategoryDocument.setCategoryName(categoryContentWrapper.get("CATEGORY_NAME").toString());
                    StringUtil.StringWrapper categoryImageUrl = categoryContentWrapper.get("CATEGORY_IMAGE_URL");
                    if (UtilValidate.isNotEmpty(categoryImageUrl)) {
                        productCategoryDocument.setCategoryImageUrl(categoryImageUrl.toString());
                    }
                    // Category_DESCRIPTION
                    categoryDescription = CategoryContentWrapper.getProductCategoryContentAsText(workingCategory, "LONG_DESCRIPTION", locale, dispatcher);
                    if (UtilValidate.isNotEmpty(categoryDescription) && !"null".equalsIgnoreCase(categoryDescription)) {
                        productCategoryDocument.setCategoryDescription(categoryDescription.toString());
                    }

                    documentList.add(productCategoryDocument);

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

                                if (ProductWorker.isSellable(product)) {
                                    productContentWrapper = new ProductContentWrapper(dispatcher, product, locale, "text/html");
                                    productDocument = new ProductDocument();
                                    productId = product.getString("productId");
                                    productDocumentId = SolrConstants.ROW_TYPE_PRODUCT + "_" + productId;
                                    productDocument.setId(productDocumentId);
                                    productDocument.setProductId(productId);
                                    productDocument.setRowType(SolrConstants.ROW_TYPE_PRODUCT);
                                    productDocument.setName(productContentWrapper.get("PRODUCT_NAME").toString());
                                    productDocument.setInternalName(product.getString("internalName"));
                                    productDocument.setSequenceNum(productCategoryMember.getString("sequenceNum"));
                                    
                                    if (UtilValidate.isNotEmpty(categoryDescription) && !"null".equalsIgnoreCase(categoryDescription.toString())) {
                                    	productDocument.setCategoryDescription(categoryDescription.toString());
                                        
                                    }
                                    // LONG_DESCRIPTION
                                    String longDescription = ProductContentWrapper.getProductContentAsText(product, "LONG_DESCRIPTION", locale, dispatcher);
                                    if (UtilValidate.isNotEmpty(longDescription)) 
                                    {
                                      productDocument.setDescription(longDescription);
                                    }

                                    // SMALL_IMAGE_URL
                                    imageUrl = productContentWrapper.get("SMALL_IMAGE_URL");
                                    if (UtilValidate.isNotEmpty(imageUrl) && !"null".equalsIgnoreCase(imageUrl.toString())) {
                                        productDocument.setProductImageSmallUrl(imageUrl.toString());
                                    }

                                    // SMALL_IMAGE_ALT
                                    imageUrl = productContentWrapper.get("SMALL_IMAGE_ALT");
                                    if (UtilValidate.isNotEmpty(imageUrl) && !"null".equalsIgnoreCase(imageUrl.toString())) {
                                        productDocument.setProductImageSmallAlt(imageUrl.toString());
                                    }

                                    // SMALL_IMAGE_ALT_URL
                                    imageUrl = productContentWrapper.get("SMALL_IMAGE_ALT_URL");
                                    if (UtilValidate.isNotEmpty(imageUrl) && !"null".equalsIgnoreCase(imageUrl.toString())) 
                                    {
                                    	if (UtilValidate.isNotEmpty(imageUrl.toString()))
                                    	{
                                            productDocument.setProductImageSmallAltUrl(imageUrl.toString());
                                    		
                                    	}
                                    }
                                    // MEDIUM_IMAGE_URL
                                    imageUrl = productContentWrapper.get("MEDIUM_IMAGE_URL");
                                    if (UtilValidate.isNotEmpty(imageUrl) && !"null".equalsIgnoreCase(imageUrl.toString())) {
                                        productDocument.setProductImageMediumUrl(imageUrl.toString());
                                    }

                                    // LARGE_IMAGE_URL
                                    imageUrl = productContentWrapper.get("LARGE_IMAGE_URL");
                                    if (UtilValidate.isNotEmpty(imageUrl) && !"null".equalsIgnoreCase(imageUrl.toString())) {
                                        productDocument.setProductImageLargeUrl(imageUrl.toString());
                                    }

                                    results = dispatcher.runSync("getProductFeaturesByType", UtilMisc.toMap("productId", productId));
                                    List<String> productFeatureTypes = (List<String>) results.get("productFeatureTypes");

                                    // for each new feature type add a column heading
                                    // ex. COLOR SIZE

                                    Map<String, GenericValue> productFeaturesByType = (Map<String, GenericValue>) results.get("productFeaturesByType");
                                    for (String productFeatureType : productFeatureTypes) {

                                        List<GenericValue> productFeatures = (List<GenericValue>) productFeaturesByType.get(productFeatureType);

                                        try 
                                        {
                                        	
                                            Map<String, String> featureValuesMap = FastMap.newInstance();
                                            List<String> featureValues = FastList.newInstance();
                                            try {
                                                for (GenericValue feature : productFeatures) {
                                                    String description = feature.getString("description");
                                                    String productFeatureApplTypeId = feature.getString("productFeatureApplTypeId");
                                                    //Commented out block
                                                    //issue #25879
                                                    //implemented to meet a specific requirement for GMH Production by srufle.
                                                    //Not needed anymore, need to insure GMH is still working after comment
                                                    if ("SELECTABLE_FEATURE".equals(productFeatureApplTypeId)) {
                                                        if (description.contains("/")) {
                                                            // Split descriptions for SELECTABLE_FEATURE items that have a "/" in them
                                                            // Ex.Off Black/Burgundy would be split in to seperate facets for "Off Black"
                                                            // and
                                                            // "Burgundy"
                                                            String[] descriptionParts = StringUtils.split(description, "/");
                                                            for (String descPart : descriptionParts) {
                                                                descPart = StringUtils.trim(descPart);
                                                                featureValuesMap.put(descPart, descPart);
                                                            }
                                                            continue;
                                                        }
                                                    }
                                                    try {
                                                    	if (UtilValidate.isNotEmpty(description))
                                                    	{
                                                            featureValuesMap.put(description, description);
                                                    		
                                                    	}
                                                    }
                                                    catch (Exception eee) {
                                                    	Debug.logError(eee, "h" + eee.getMessage()+ description, module);
                                                    	
                                                    }
                                                }
                                            }
                                            catch (Exception eee) {
                                            	Debug.logError(eee, eee.getMessage(), module);
                                            	
                                            }
                                            Set<Entry<String, String>> featureValuesEntrySet = featureValuesMap.entrySet();
                                            Iterator<Entry<String, String>> featureValuesIterator = featureValuesEntrySet.iterator();
                                            while (featureValuesIterator.hasNext()) {
                                                Map.Entry<String, String> featureValueEntry = (Map.Entry<String, String>) featureValuesIterator.next();
                                                featureValues.add((String) featureValueEntry.getKey());
                                            }

                                            // Need to replace spaces in decription with underscore symbol "_"
                                            for (int i = 0; i < featureValues.size(); i++) {
                                                String val = featureValues.get(i);
                                                val = URLEncoder.encode(StringUtils.replace(val, " ", "_"), SolrConstants.DEFAULT_ENCODING);
                                                featureValues.set(i, val);
                                            }

                                            productDocument.addProductFeature(productFeatureType, featureValues);
                                            if (!prodFeatureColNames.contains(productFeatureType)) {
                                                headerColumns.add("productFeature");
                                                cellProcessors.add(new ProductFeatureCellProcessor(productFeatureType));
                                                prodFeatureColNames.add(productFeatureType);
                                            }
                                        }
                                         catch (Exception ee)
                                         {
                                        	 Debug.logError(ee, ee.getMessage(),module);
                                         }
                                        // ex. Red Small
                                    }

                                    // Product Prices
                                    String currencyUomId = productStore.getString("defaultCurrencyUomId");
                                    results = dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", product, "currencyUomId", currencyUomId));
                                    productDocument.setPrice((BigDecimal) results.get("price"));

                                    // Product Ratings
                                    BigDecimal averageProductRating = ProductWorker.getAverageProductRating(delegator, productId, productStoreId);
                                    productDocument.setCustomerRating(averageProductRating);

                                    // Product Quantity Ordered
                                   GenericValue ProductCalculatedInfo = delegator.findOne("ProductCalculatedInfo", UtilMisc.toMap("productId", productId), false);
                                   if(UtilValidate.isNotEmpty(ProductCalculatedInfo) && ProductCalculatedInfo.getDouble("totalQuantityOrdered")!= null){
                                      totalQuantityOrdered = ProductCalculatedInfo.getDouble("totalQuantityOrdered");
                                    }
                                   else{
                                      totalQuantityOrdered = 0.00;
                                   }
                                   productDocument.setTotalQuantityOrdered(totalQuantityOrdered);
                                   // Product View Count
                                   if(UtilValidate.isNotEmpty(ProductCalculatedInfo) && ProductCalculatedInfo.getLong("totalTimesViewed")!= null){
                                	   totalTimesViewed = ProductCalculatedInfo.getLong("totalTimesViewed");
                                    }
                                   else{
                                       totalTimesViewed = 0L;
                                   }
                                   productDocument.setTotalTimesViewed(totalTimesViewed);
                                    // Product Categories of product
                                    List<GenericValue> currentProductCategories = ProductWorker.getCurrentProductCategories(product);

                                    // For each Product Category a product is in find the "Top Most" category just before the
                                    // "Catalog Root"
                                    Map<String, String> topCatsMap = FastMap.newInstance();
                                    for (GenericValue productCategory : currentProductCategories) {
                                        productCategoryId = productCategory.getString("productCategoryId");
                                        GenericValue gvTopMostCategory = getTopMostParentProductCategory(delegator, productCategoryId, browseRootProductCategoryId);
                                        if (UtilValidate.isNotEmpty(gvTopMostCategory)) {
                                            String topMostProductCategoryId = gvTopMostCategory.getString("productCategoryId");

                                            // Check to see if we have processed this top category already
                                            if (topCatsMap.containsKey(topMostProductCategoryId)) {
                                                String productCategoryIds = productDocument.getProductCategoryId();
                                                productCategoryIds = productCategoryIds + " " + productCategoryId;
                                                productDocument.setProductCategoryId(productCategoryIds);
                                            } else {

                                                String lastTopMostProductCategoryId = productDocument.getTopMostProductCategoryId();
                                                if (UtilValidate.isNotEmpty(lastTopMostProductCategoryId) && !topMostProductCategoryId.equals(lastTopMostProductCategoryId)) {

                                                    // Change "Product Document" Id so it is unique
                                                    productDocumentId = SolrConstants.ROW_TYPE_PRODUCT + "_" + productId + "_" + lastTopMostProductCategoryId;
                                                    productDocument.setId(productDocumentId);

                                                    // Save current "Product Document" before creating a copy
                                                    documentList.add(productDocument);
                                                    /*
                                                     * We were having the same issue as this guy, SerializationUtils will be our
                                                     * own class
                                                     * http://www.philliprhodes.com/content/orgapachecommonslangserializationexception
                                                     * - javalangclassnotfoundexception
                                                     * org.apache.commons.lang.SerializationException:
                                                     * java.lang.ClassNotFoundException Submitted by prhodes on Tue, 12/29/2009 -
                                                     * 10:57
                                                     *
                                                     * Working on the spring batch examples, and got the following error. To
                                                     * summarize, the classloader that the serializationtutils is using is not
                                                     * able to find this class. I got around this error by creating my own copy of
                                                     * "SerializationUtils.java" in a new package and using this. Since my
                                                     * SerializationUtils is in my webapp, there are no classloader issues.
                                                     */

                                                    // Make copy and change "Product Document" Id so it is also unique
                                                    ProductDocument cloneProductDocument = (ProductDocument) SerializationUtils.clone(productDocument);
                                                    productDocument = cloneProductDocument;
                                                    productDocumentId = SolrConstants.ROW_TYPE_PRODUCT + "_" + productId + "_" + topMostProductCategoryId;
                                                    productDocument.setId(productDocumentId);
                                                }
                                                topCatsMap.put(topMostProductCategoryId, topMostProductCategoryId);
                                                productDocument.setProductCategoryId(productCategoryId);
                                                productDocument.setTopMostProductCategoryId(topMostProductCategoryId);
                                            }
                                        }

                                    }

                                    // Find "Facet Groups" available for each "Product Category"
                                    Map<String, String> productFeatureTypeMap = null;
                                    for (GenericValue productCategory : currentProductCategories) {

                                        productCategoryId = productCategory.getString("productCategoryId");
                                        List<GenericValue> productFeatureCatGrpAppls = productCategory.getRelated("ProductFeatureCatGrpAppl");
                                        productFeatureCatGrpAppls = EntityUtil.filterByDate(productFeatureCatGrpAppls);
                                        productFeatureCatGrpAppls = EntityUtil.orderBy(productFeatureCatGrpAppls, UtilMisc.toList("sequenceNum"));

                                        for (GenericValue productFeatureCatGrpAppl : productFeatureCatGrpAppls) {
                                            productFeatureTypeMap = FastMap.newInstance();
                                            GenericValue productFeatureGroup = productFeatureCatGrpAppl.getRelatedOne("ProductFeatureGroup");

                                            String productFeatureGroupDescription = productFeatureGroup.getString("description");
                                            String productFeatureGroupId = productFeatureGroup.getString("productFeatureGroupId");
                                            
                                            Long facetValueMin = productFeatureCatGrpAppl.getLong("facetValueMin");
                                            Long facetValueMax = productFeatureCatGrpAppl.getLong("facetValueMax");
                                            try {
                                                if (UtilValidate.isEmpty(facetValueMin)) {
                                                    String xProductStorePramFaceValueMin = Util.getProductStoreParm(delegator, productStoreId, "FACET_VALUE_MIN");
                                                    if (UtilValidate.isNotEmpty(xProductStorePramFaceValueMin)) 
                                                    {
                                                    	facetValueMin = Long.parseLong(xProductStorePramFaceValueMin);
                                                    }
                                                }
                                                if (UtilValidate.isEmpty(facetValueMax)) {
                                                    String xProductStorePramFaceValueMax = Util.getProductStoreParm(delegator, productStoreId, "FACET_VALUE_MAX");
                                                    if (UtilValidate.isNotEmpty(xProductStorePramFaceValueMax)) 
                                                    {
                                                        facetValueMax = Long.parseLong(xProductStorePramFaceValueMax);
                                                    }
                                                }
                                            } catch (NumberFormatException nfe) {
                                                facetValueMax = 0L;
                                                facetValueMin = 0L;
                                                Debug.logError(nfe, nfe.getMessage(), module);
                                            }

                                            facetGroupDocument = new ProductDocument();
                                            facetGroupDocument.setId(SolrConstants.ROW_TYPE_FACET_GROUP + "_" + productCategoryId + "_" + productFeatureGroupId);
                                            facetGroupDocument.setRowType(SolrConstants.ROW_TYPE_FACET_GROUP);
                                            facetGroupDocument.setProductCategoryId(productCategoryId);
                                            facetGroupDocument.setProductFeatureGroupFacetValueMin(facetValueMin);
                                            facetGroupDocument.setProductFeatureGroupFacetValueMax(facetValueMax);

                                            facetGroupDocument.setProductFeatureGroupDescription(productFeatureGroupDescription);
                                            facetGroupDocument.setProductFeatureGroupId(productFeatureGroupId);

                                            List<GenericValue> productFeatureGroupAppls = delegator.findByAnd("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", productFeatureGroupId));
                                            productFeatureGroupAppls = EntityUtil.filterByDate(productFeatureGroupAppls);
                                            productFeatureGroupAppls = EntityUtil.orderBy(productFeatureGroupAppls, UtilMisc.toList("sequenceNum"));

                                            for (GenericValue productFeatureGroupAppl : productFeatureGroupAppls) {
                                                GenericValue productFeature = productFeatureGroupAppl.getRelatedOne("ProductFeature");
                                                String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
                                                String key = SolrConstants.EXTRACT_PRODUCT_FEATURE_PREFIX + productFeatureTypeId;
                                                productFeatureTypeMap.put(key, productFeatureTypeId);
                                            }

                                            String productFeatureTypeIds = StringUtils.join(UtilMisc.toList(productFeatureTypeMap.keySet()), " ");

                                            facetGroupDocument.setProductCategoryFacetGroups(productFeatureTypeIds);
                                            documentList.add(facetGroupDocument);
                                        }
                                    }

                                    documentList.add(productDocument);
                                }

                            }
                        }
                    }

                }

            }

            if (UtilValidate.isNotEmpty(documentList)) {
                // Generate CSV File
                String[] columnNames = (String[]) headerColumns.toArray(new String[headerColumns.size()]);
                String filename = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe.properties", "solr-product-index-file"), context);

                Debug.log("solr-product-index-file=" + filename, module);

                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")));
                CsvBeanWriter cbw = new CsvBeanWriter(writer, CsvPreference.EXCEL_PREFERENCE);

                CellProcessor[] cp = (CellProcessor[]) cellProcessors.toArray(new CellProcessor[cellProcessors.size()]);
                for (ProductDocument doc : documentList) {
                    cbw.write(doc, columnNames, cp);
                }
                cbw.close();
                writer.flush();

                String solrServer = UtilProperties.getPropertyValue("osafe.properties", "solr-server");

                Debug.log("solrServer=" + solrServer, module);

                // Delete previous index using Http Client
                String deleteAllUrl = solrServer + "/update?stream.body=<delete><query>*:*</query></delete>&commit=true";
                HttpClient hc = new HttpClient(deleteAllUrl);
                String deleteResponse = hc.get();
                Debug.log(deleteResponse, module);

                // Import CSV file using Http Client
                int index = prodFeatureColNames.size();
                for (String prodFeatureType : prodFeatureColNames) {
                    columnNames[columnNames.length - index] = "productFeature_" + prodFeatureType;
                    index--;
                }
                String importUrl = solrServer + "/update/csv?stream.file=" + filename + "&stream.contentType=text/plain;charset=utf-8&header=false&commit=true&fieldnames=" + StringUtils.join(columnNames, ",");
                Debug.log(importUrl, module);
                hc = new HttpClient(importUrl);
                String importResponse = hc.get();
                Debug.log(importResponse, module);
            }
        } catch (Exception e) {
            Debug.logError(e, e.getMessage(), module);
        }

        if (documentList == null) {
            documentList = FastList.newInstance();
        }
        result.put("documentListCount", documentList.size());

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
    /**
     * service for update the schema.xml file in solr .service remove the field element of fields parent element.
     * For append the field element which treat as constant, 
     * service import these element from schema-ConstantField.xml from location same as schema.xml
     * Dynamic field element get by ProductFeatureType entity, 
     * value of attributes of dynamic field elements are fix 
     * and service retrive these keys, values from com.osafe.solr.SolrConstants java file
     */
    public static Map<String, Object> updateSolrSchemaXml(DispatchContext ctx, Map<String, ?> context) {

        Delegator delegator = ctx.getDelegator();
        Map<String, Object> resp = null;
        int addFeatureElementCount = 0;
        InputStream ins = null, insc = null;
        OutputStream os = null;
        Document xmlDocument = null;

        String schemaXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe.properties", "solr-schema-xml-file"), context);
        String schemaConstantXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe.properties", "solr-schema-constantField-xml-file"), context);

        if (UtilValidate.isNotEmpty(schemaXmlFilePath) && UtilValidate.isNotEmpty(schemaConstantXmlFilePath)) {

            URL schemaXmlFileUrl = UtilURL.fromFilename(schemaXmlFilePath);
            URL schemaConstantXmlFileUrl = UtilURL.fromFilename(schemaConstantXmlFilePath);

            try {
                if (schemaXmlFileUrl  != null) ins = schemaXmlFileUrl.openStream();
                if (schemaConstantXmlFileUrl  != null) insc = schemaConstantXmlFileUrl.openStream();

                if (ins != null && insc != null) {
                    xmlDocument = UtilXml.readXmlDocument(ins, schemaXmlFileUrl.toString());
                    // ##############################################
                    // remove the field element and copyField element
                    // ##############################################
                    Node parentFieldNode = removeNode(xmlDocument, "field");
                    Node parentCopyFieldNode = removeNode(xmlDocument, "copyField");
                    // ###################################################
                    // append constant field element and copyField element  
                    // which import from schema-ConstantField.xml
                    // ###################################################
                    Document xmlConstantDocument = UtilXml.readXmlDocument(insc, schemaConstantXmlFileUrl.toString());
                    importNode(xmlConstantDocument, parentFieldNode, "field");
                    importNode(xmlConstantDocument, parentCopyFieldNode, "copyField");

                    // ##################################################
                    // append dynamic field element and copyField element
                    // from ProductFeatureType entity
                    // ##################################################
                    if (parentFieldNode!= null && parentCopyFieldNode!= null) {
                        List<GenericValue>  productFeatureTypeIdList=delegator.findByAnd("ProductFeatureType", UtilMisc.toMap());
                        for (GenericValue productFeatureType: productFeatureTypeIdList) {
                            addFeatureElementCount++;
                            //create dynamic field element with their attributes
                            Element newFeatureElement = UtilXml.addChildElement((Element)parentFieldNode, "field", xmlDocument);
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_NAME_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_NAME_VALUE_PREFIX.concat(productFeatureType.get("productFeatureTypeId").toString()));
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_INDEXED_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_INDEXED_VALUE);
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_MULTIVALUED_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_MULTIVALUED_VALUE);
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_OMITNORMS_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_OMITNORMS_VALUE);
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_REQUIRED_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_REQUIRED_VALUE);
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_STORED_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_STORED_VALUE);
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_TYPE_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_TYPE_VALUE);

                            //create dynamic copyField element with their attributes
                            newFeatureElement = UtilXml.addChildElement((Element)parentCopyFieldNode, "copyField", xmlDocument);
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_SOURCE_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_SOURCE_VALUE_PREFIX.concat(productFeatureType.get("productFeatureTypeId").toString()));
                            newFeatureElement.setAttribute(SolrConstants.SCHEMA_PRODUCT_FEATURE_DEST_ATTR, SolrConstants.SCHEMA_PRODUCT_FEATURE_DEST_VALUE);
                        }
                    }
                }
                else {
                    resp = ServiceUtil.returnFailure();
                }
            } catch (IOException ioe) {
                Debug.logError(ioe, module);
                resp = ServiceUtil.returnFailure();
                xmlDocument = null;
            } catch (Exception exc) {
                Debug.logError(exc, module);
                resp = ServiceUtil.returnFailure();
                xmlDocument = null;
            }
            finally {
                try {
                    if (xmlDocument != null) {
                        // ################################################
                        // save the DOM document to schema.xml file 
                        // ################################################
                        xmlDocument.normalize();
                        os = new FileOutputStream(schemaXmlFileUrl.getPath());
                        UtilXml.writeXmlDocument(os, xmlDocument);
                    }
                    if (os != null) os.close();
                    if (ins != null) ins.close();
                    if (insc != null) insc.close();
                } catch (IOException ioe) {
                    Debug.logError(ioe, module);
                    resp = ServiceUtil.returnFailure();
                } catch (Exception exc) {
                    Debug.logError(exc, module);
                    resp = ServiceUtil.returnFailure();
                }
                os = null;
                ins = null;
                insc = null;
                if (resp == null) resp = ServiceUtil.returnSuccess();
            }
        }
        else {
            resp = ServiceUtil.returnFailure();
        }
        resp.put("addFeatureElementCount", addFeatureElementCount);
        return resp;
    }

    private static Node removeNode(Document docment, String removeNodeName) throws Exception {

        Node parentNode = null;
        if (UtilValidate.isNotEmpty(removeNodeName) && UtilValidate.isNotEmpty(docment)) {
            if (docment.getElementsByTagName(removeNodeName).getLength() > 0 ) {
                List<? extends Node> nodeList = UtilXml.childNodeList(docment.getElementsByTagName(removeNodeName).item(0));
                for (Node node: nodeList) {
                    if (node.getNodeName().equalsIgnoreCase(removeNodeName)) {
                        parentNode = node.getParentNode();
                        parentNode.removeChild(node);
                    }
                }
            }
        }
        return parentNode;
    }

    private static void importNode(Document importDocument, Node parentNode, String importNodeName) throws Exception {

        if (UtilValidate.isNotEmpty(importNodeName) && UtilValidate.isNotEmpty(importDocument) && UtilValidate.isNotEmpty(parentNode) ) {
            if (importDocument.getElementsByTagName(importNodeName).getLength() > 0 ) {
                List<? extends Node> nodeList = UtilXml.childNodeList(importDocument.getElementsByTagName(importNodeName).item(0));
                for (Node node: nodeList) {
                    if (node.getNodeName().equalsIgnoreCase(importNodeName)) {
                        Node importNode = parentNode.getOwnerDocument().importNode(node, true);
                        parentNode.appendChild(importNode);
                    }
                }
            }
        }
    }
}

package com.osafe.solr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.osafe.util.Util;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.StringUtil.StringWrapper;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;

import com.osafe.services.SolrIndexDocument;

public class RefinementsHelperSolr {

    private static final String module = RefinementsHelperSolr.class.getName();
    private CommandContext commandContext;
    private Delegator delegator = null;
    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());

    public RefinementsHelperSolr(CommandContext commandContext, Delegator delegator) {
        this.commandContext = commandContext;
        this.delegator = delegator;
    }

    /**
     * Creates a collection of GenericRefinements with embedded GenericRefinementValues from the facets in the solr search
     * results.
     *
     * @param facetResults
     * @return Collection of GenericRefinements
     */
    public Collection processRefinements(List<FacetField> facetResults) {

        // Create a new collection for the generic Refinements in the generic
        // wrapper
        Collection refinementCollection = new ArrayList();
        if (facetResults != null) {
            // Get the standard refinement options set
            Iterator<FacetField> facets = facetResults.iterator();

            // Loop through the facets
            while (facets.hasNext()) {

                // Get the Solr facet
                FacetField facet = (FacetField) facets.next();

                if (facet != null && facet.getValues() != null) {

                    // Create a generic refinement
                    GenericRefinement genericRefinement = new GenericRefinement();

                    // Populate the generic refinement with the details from the
                    // Solr facet
                    String facetType = facet.getName();

                    boolean isOnCategoryList = commandContext.isOnCategoryList();

                    if (isOnCategoryList) {
                        if (!SolrConstants.TYPE_PRODUCT_CATEGORY.equals(facetType)) {
                            // skip
                            //continue;
                        }

                    }
                    String productFeatureGroupId = null;
                    String productFeatureGroupFacetSort = null;
                    Map<String, String> filterGroupsIds = commandContext.getFilterGroupsIds();
                    Map<String, String> filterGroupsFacetSorts = commandContext.getFilterGroupsFacetSorts();

                    // Product Feature Group Id
                    if (filterGroupsIds.containsKey(facetType)) {
                        productFeatureGroupId = filterGroupsIds.get(facetType);
                    }
                    genericRefinement.setProductFeatureGroupId(productFeatureGroupId);

                    // Product Feature Group Facet Sorting rule
                    if (filterGroupsFacetSorts.containsKey(facetType)) {
                        productFeatureGroupFacetSort = filterGroupsFacetSorts.get(facetType);
                    }
                    if (UtilValidate.isEmpty(productFeatureGroupFacetSort)) {
                        productFeatureGroupFacetSort = SolrConstants.FACET_SORT_DB_SEQ;
                    }
                    genericRefinement.setProductFeatureGroupFacetSort(productFeatureGroupFacetSort);

                    String facetName = facetType;
                    genericRefinement.setType(facetType);

                    genericRefinement.setProductFeatureTypeId(facetType);

                    Map<String, String> filterGroupsDescriptions = commandContext.getFilterGroupsDescriptions();
                    if (filterGroupsDescriptions.containsKey(facetType)) {
                        facetName = filterGroupsDescriptions.get(facetType);
                    } else {
                        facetName = WordUtils.capitalizeFully(facetType);
                    }

                    genericRefinement.setName(facetName);

                    // Get a collection of the refinement values for this refinement
                    // may include merged common refinement values
                    Collection refinementValues = processRefinementValues(facet, genericRefinement);

                    // Add the refinement values to the refinement
                    genericRefinement.setRefinementValues(refinementValues);

                    // Add the new generic refinement to the collection - check
                    // errors elsewhere have not created a null refinement first
                    if (genericRefinement != null) {
                        refinementCollection.add(genericRefinement);
                    }
                }
            }
            refinementCollection = sortRefinementValues(refinementCollection);
        }
        return refinementCollection;
    }

    /**
     * Creates a collection of GenericRefinements with embedded GenericRefinementValues from the facets in the solr search
     * results.
     *
     * @param facetResults
     * @return Collection of GenericRefinements
     */
    public Collection processPriceRangeRefinements(Map facetResults) {
        // Create a new collection for the generic Refinements in the generic
        // wrapper
        Collection refinementCollection = new ArrayList();
        if (facetResults != null) {
            // Get the standard refinement options set
            Iterator facets = facetResults.keySet().iterator();

            // Create a generic refinement
            GenericRefinement genericRefinement = new GenericRefinement();

            // Populate the generic refinement with the details from the Solr facet
            genericRefinement.setType(SolrConstants.TYPE_PRICE);
            genericRefinement.setName(SolrConstants.DISPLAY_PRICE_NAME_KEY);

            List refinementValues = new ArrayList();

            // Loop through the facets
            while (facets.hasNext()) {

                // Get the Solr refinement value
                String value = (String) facets.next();
                Integer count = (Integer) facetResults.get(value);

                if (value.indexOf(SolrConstants.TYPE_PRICE) > -1) {
                    if (count != null && count.longValue() > 0) {
                        // Create a generic refinement value
                        GenericRefinementValue genericRefinementValue = convertRefinementValueToGeneric(value, count.longValue(), genericRefinement);

                        // Add the refinementValue to the collection
                        refinementValues.add(genericRefinementValue);
                    }
                }
            }

            if (!UtilValidate.isEmpty(refinementValues)) {
                // sort the price order
                Collections.sort(refinementValues);

                genericRefinement.setRefinementValues(refinementValues);
                refinementCollection.add(genericRefinement);
            }
        }
        return refinementCollection;
    }

    /**
     * Creates a collection of GenericRefinements with embedded GenericRefinementValues from the facets in the solr search
     * results.
     *
     * @param facetResults
     * @param list of the SolrIndexDocument
     * @return Collection of GenericRefinements
     */
    public Collection processPriceRangeRefinements(Map facetResults, List<SolrIndexDocument> results) {
        // Create a new collection for the generic Refinements in the generic
        // wrapper
        Collection refinementCollection = new ArrayList();
        if (facetResults != null) {
            // Get the standard refinement options set
            Iterator facets = facetResults.keySet().iterator();

            // Create a generic refinement
            GenericRefinement genericRefinement = new GenericRefinement();

            // Populate the generic refinement with the details from the Solr facet
            genericRefinement.setType(SolrConstants.TYPE_PRICE);
            genericRefinement.setName(SolrConstants.DISPLAY_PRICE_NAME_KEY);

            List refinementValues = new ArrayList();

            // Loop through the facets
            while (facets.hasNext()) {

                // Get the Solr refinement value
                String value = (String) facets.next();
                Integer count = (Integer) facetResults.get(value);
                if (value.indexOf(SolrConstants.TYPE_PRICE) > -1) {
                    if (count != null && count.longValue() > 0) {
                    	if(UtilValidate.isNotEmpty(results)) {
                    		List<SolrIndexDocument> duplicateProducts = getDuplicateProducts(results);
                    		if(UtilValidate.isNotEmpty(duplicateProducts)) {
	                            Iterator duplicateProductItr = duplicateProducts.iterator();
	                            while (duplicateProductItr.hasNext()) {
	                                SolrIndexDocument solrIndexDocument = (SolrIndexDocument)duplicateProductItr.next();
	                                double start = getPriceRangeArr(value)[0].doubleValue();
	                                double end = getPriceRangeArr(value)[1].doubleValue();
	                                double productPrice = solrIndexDocument.getPrice().doubleValue();
	                                if (Double.compare(start, productPrice) <= 0 && Double.compare(end, productPrice) > 0) {
	                            	    count = count - 1;
	                                }
	                            }
                    		}
                        }
                        // Create a generic refinement value
                        GenericRefinementValue genericRefinementValue = convertRefinementValueToGeneric(value, count.longValue(), genericRefinement);
                        // Add the refinementValue to the collection
                        
                        if (!refinementValues.contains(genericRefinementValue))
                        refinementValues.add(genericRefinementValue);
                    }
                }
            }

            if (!UtilValidate.isEmpty(refinementValues)) {
                // sort the price order
                Collections.sort(refinementValues);

                genericRefinement.setRefinementValues(refinementValues);
                refinementCollection.add(genericRefinement);
            }
        }
        return refinementCollection;
    }

    public Collection processCustomerRatingRefinements(Map facetResults) {

        // Create a new collection for the generic Refinements in the generic
        // wrapper
        Collection refinementCollection = new ArrayList();
        if (facetResults != null) {
            // Get the standard refinement options set
            Iterator facets = facetResults.keySet().iterator();

            // Create a generic refinement
            GenericRefinement genericRefinement = new GenericRefinement();

            // Populate the generic refinement with the details from the Solr facet
            genericRefinement.setType(SolrConstants.TYPE_CUSTOMER_RATING);
            genericRefinement.setName(SolrConstants.DISPLAY_CUSTOMER_RATING_NAME_KEY);

            List refinementValues = new ArrayList();

            // Loop through the facets
            while (facets.hasNext()) {

                // Get the Solr refinement value
                String value = (String) facets.next();
                Integer count = (Integer) facetResults.get(value);

                if (value.indexOf(SolrConstants.TYPE_CUSTOMER_RATING) > -1) {
                    if (count != null && count.longValue() > 0) {
                        // Create a generic refinement value
                        GenericRefinementValue genericRefinementValue = convertRefinementValueToGeneric(value, count.longValue(), genericRefinement);

                        // Add the refinementValue to the collection
                        refinementValues.add(genericRefinementValue);
                    }
                }
            }

            if (!UtilValidate.isEmpty(refinementValues)) {
                Collections.sort(refinementValues);

                genericRefinement.setRefinementValues(refinementValues);
                refinementCollection.add(genericRefinement);
            }
        }
        return refinementCollection;
    }
    

    /**
     * Sorts the facet values in the order as given in the COMREG entry SEARCH_SOLR_ATTRVALUE_SORT_ORDER
     *
     * @param refinementCollection
     * @return
     */
    private Collection sortRefinementValues(Collection refinementCollection) {
        Object[] arrayFacets = null;
        if (refinementCollection != null) {
            arrayFacets = refinementCollection.toArray();
            Collection sortedCollection = new ArrayList();
            String productFeatureGroupFacetSort = null;
            for (int i = 0; i < arrayFacets.length; i++) {
                GenericRefinement refinement = (GenericRefinement) arrayFacets[i];
                FacetValueSequenceComparator comparator = null;
                comparator = new FacetValueSequenceComparator(this.delegator, refinement.getProductFeatureGroupId());

                if (SolrConstants.TYPE_PRODUCT_CATEGORY.equals(refinement.getType()) || SolrConstants.TYPE_TOP_MOST_PRODUCT_CATEGORY.equals(refinement.getType())) {
                    comparator.setUseSequenceNum(true);
                } else {
                    productFeatureGroupFacetSort = refinement.getProductFeatureGroupFacetSort();
                    comparator.setProductFeatureGroupSorting(productFeatureGroupFacetSort);
                    if (SolrConstants.FACET_SORT_DB_SEQ.equals(productFeatureGroupFacetSort)) {
                        comparator.populateSortOrder();
                    }
                }

                Collection facetValues = (Collection) refinement.getRefinementValues();
                if (facetValues != null) {
                    Collections.sort((List) facetValues, comparator);
                }
            }
        }
        return refinementCollection;
    }

    /**
     * Creates a collection of generic refinement values, merging with common refinement values if required
     *
     * @param csnRefinement
     * @param genericRefinement
     * @return
     */
    private Collection processRefinementValues(FacetField facet, GenericRefinement genericRefinement) {

        Collection refinementValues = new ArrayList();

        // Get the associated values from the facet
        List values = facet.getValues();
        Iterator it = values.iterator();

        // Loop through the values
        while (it.hasNext()) {
            // Get the Solr refinement value

            FacetField.Count value = (FacetField.Count) it.next();
            GenericRefinementValue genericRefinementValue = null;
            // Create a generic refinement value
            try {
                genericRefinementValue = convertRefinementValueToGeneric(URLDecoder.decode(value.getName(), SolrConstants.DEFAULT_ENCODING), value.getCount(), genericRefinement);
            } catch (UnsupportedEncodingException e) {
                // Oops UTF-8?????
                genericRefinementValue = convertRefinementValueToGeneric(value.getName(), value.getCount(), genericRefinement);
            }

            // Add the refinementValue to the collection
            refinementValues.add(genericRefinementValue);
        }

        return refinementValues;
    }

    /**
     * Creates a generic refinement value from the solr facet value
     *
     * @param key
     * @param value
     * @param genericRefinement
     * @return
     */
    private GenericRefinementValue convertRefinementValueToGeneric(String key, long count, GenericRefinement genericRefinement) {

        GenericRefinementValue genericRefinementValue = new GenericRefinementValue();
        genericRefinementValue.setName(key);
        genericRefinementValue.setScalarCount(String.valueOf(count));
        boolean isPrice = genericRefinement.getType().equals(SolrConstants.TYPE_PRICE);
        boolean isCustomerRating = genericRefinement.getType().equals(SolrConstants.TYPE_CUSTOMER_RATING);
        boolean isProductCategory = genericRefinement.getType().equals(SolrConstants.TYPE_PRODUCT_CATEGORY);
        boolean isTopMostProductCategory = genericRefinement.getType().equals(SolrConstants.TYPE_TOP_MOST_PRODUCT_CATEGORY);
        boolean isOnCategoryList = commandContext.isOnCategoryList();

        boolean firstParamAdded = false;
        String requestName = commandContext.getRequestName();
        String url = requestName;
        url += "?";
        String ccSearchText = commandContext.getSearchText();
        String ccProductCategoryId = commandContext.getProductCategoryId();
        String ccTopMostProductCategoryId = commandContext.getTopMostProductCategoryId();
        if (UtilValidate.isNotEmpty(ccSearchText)) {
            url += "searchText" + "=" + ccSearchText;
            firstParamAdded = true;

            if (isTopMostProductCategory) 
            {
                if (firstParamAdded) 
                {
                    url += "&";
                }
                url += "topMostProductCategoryId" + "=" + key;
            } 
            else 
            {
              if (UtilValidate.isNotEmpty(ccTopMostProductCategoryId)) 
              {
                  if (firstParamAdded) 
                  {
                      url += "&";
                  }
                url += "topMostProductCategoryId" + "=" + ccTopMostProductCategoryId;
              }
            }
        }

        if (UtilValidate.isNotEmpty(ccProductCategoryId)) {
            if (firstParamAdded) {
                url += "&";
            }
            url += "productCategoryId" + "=" + ccProductCategoryId;
        }

        if (isProductCategory && UtilValidate.isEmpty(ccSearchText)) {
            if (!isOnCategoryList) {
                url += "&";
            }
            url += "productCategoryId" + "=" + key;
        }

        if (!isOnCategoryList) {
            url += "&" + "filterGroup" + "=";
            List<String> prevFilterGroups = commandContext.getFilterGroups();
            List<String> filterGroupList = FastList.newInstance();
            if (!isPrice && !isCustomerRating) {
                String encodedKey = null;
                try {
                    encodedKey = URLEncoder.encode(key, SolrConstants.DEFAULT_ENCODING);
                } catch (UnsupportedEncodingException e) {
                    encodedKey = key;
                }
                filterGroupList.add(genericRefinement.getType() + ":" + encodedKey);
            }
            filterGroupList.addAll(0, prevFilterGroups);
            String filterGroups = StringUtils.join(filterGroupList, "|");
            url += filterGroups;

            String priceFilterGroupValue = null;
            if (isPrice) {
                priceFilterGroupValue = key;
                priceFilterGroupValue = priceFilterGroupValue.replaceAll("price", "PRICE");

                if (filterGroupList.size() > 0) {
                    url += "|";
                }
                url += priceFilterGroupValue;
            }
            String customerRatingFilterGroupValue = null;
            if (isCustomerRating) {
                customerRatingFilterGroupValue = key;
                customerRatingFilterGroupValue = customerRatingFilterGroupValue.replaceAll("customerRating", "CUSTOMER_RATING");

                if (filterGroupList.size() > 0) {
                    url += "|";
                }
                url += customerRatingFilterGroupValue;
            }

            // Sorting parameter
            String sortParameterName = commandContext.getSortParameterName();
            if (UtilValidate.isNotEmpty(sortParameterName)) {
                String sortParameterValue = StringUtils.trimToEmpty(commandContext.getSortParameterValue());
                url += "&" + sortParameterName + "=" + sortParameterValue;
            }

            // Rows Shown parameter
            String numberOfRowsShown = commandContext.getNumberOfRowsShown();
            if (UtilValidate.isNotEmpty(numberOfRowsShown)) {
                url += "&rows=" + numberOfRowsShown;
            }
        }
        genericRefinementValue.setRefinementURL(url);

        // Set the value to display in the jsp. Different for price ranges
        if (isPrice) {
            String displayName = getPriceRangeStr(key);
            genericRefinementValue.setDisplayName(displayName);
            genericRefinementValue.setStart(getPriceRangeArr(key)[0].doubleValue());
        } else if (isCustomerRating) {

            // Check lower limit of each facet value that should give us the directory we should pull the image from
            String ratingNumber = getCustomerRatingLowRangeStr(key);

            genericRefinementValue.setStart(Double.parseDouble(ratingNumber));
            genericRefinementValue.setSortMethod("desc");

            String productReviewImagesPath = OSAFE_PROPS.getString("productReviewImagesPath");
            String iconName = OSAFE_PROPS.getString("productReviewFacetIconName");

            String displayImage = productReviewImagesPath + ratingNumber + "_0/" + iconName;

            genericRefinementValue.setDisplayImage(displayImage);

            String displayName = getCustomerRatingRangeStr(key);
            genericRefinementValue.setDisplayName(displayName);
        } else if (isTopMostProductCategory) {
            String displayName = getProductCategoryName(key);
            genericRefinementValue.setDisplayName(displayName);
            genericRefinementValue.setSequenceNum(getProductCategorySequenceNum(key));
        } else if (isProductCategory) {
            String displayName = getProductCategoryName(key);
            genericRefinementValue.setDisplayName(displayName);
            String supportingText = getProductCategorySupportingText(key);
            genericRefinementValue.setSupportingText(supportingText);
            String displayImage = getProductCategoryImage(key);
            genericRefinementValue.setDisplayImage(displayImage);
            genericRefinementValue.setSequenceNum(getProductCategorySequenceNum(key));
        } else {
            genericRefinementValue.setDisplayName(ProductFeatureEncoder.decodePlus(key));
        }

        return genericRefinementValue;
    }

    private Double[] getPriceRangeArr(String key) {

        Double[] ret = new Double[2];
        key = StringUtils.substring(key, key.indexOf(":") + 1);
        key = StringUtils.strip(key, "[]");
        String[] split = StringUtils.split(key, " ");

        split[0] = StringUtils.replace(split[0], "*", "0");
        split[1] = StringUtils.replace(split[1], "*", "9999");

        ret[0] = Double.valueOf(split[0]);
        ret[1] = Double.valueOf(split[1]);

        return ret;
    }

    private String getPriceRangeStr(String key) {

        key = StringUtils.substring(key, key.indexOf(":") + 1);
        key = StringUtils.strip(key, "[]");
        String[] split = StringUtils.split(key, " ");

        split[0] = StringUtils.replace(split[0], "*", "0");
        split[1] = StringUtils.replace(split[1], "*", "9999");

        String isoCode = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
        
        HttpServletRequest request = commandContext.getRequest();
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        String productStoreId = productStore.getString("productStoreId");

        int rounding = 0;
        String parmValue = null;
        
        parmValue = Util.getProductStoreParm(delegator, productStoreId, "FACET_PRICE_ROUND");
        
        if (UtilValidate.isNotEmpty(parmValue))
        {
            try {
                rounding = Integer.parseInt(parmValue);
            } catch(NumberFormatException nfe) {
                Debug.logError(nfe.getMessage(), module);
            }
        }

        String start = UtilFormatOut.formatCurrency(Double.valueOf(split[0]), isoCode, UtilHttp.getLocale(request), rounding);
        String end = UtilFormatOut.formatCurrency(Double.valueOf(split[1]), isoCode, UtilHttp.getLocale(request), rounding);
        return start + " to " + end;
    }

    private String getCustomerRatingLowRangeStr(String key) {

        String ratingNumber = "5";
        key = StringUtils.substring(key, key.indexOf(":") + 1);
        key = StringUtils.strip(key, "[]");
        String[] split = StringUtils.split(key, " ");

        split[0] = StringUtils.replace(split[0], "*", "0");

        ratingNumber = split[0];
        return ratingNumber;
    }

    private String getCustomerRatingRangeStr(String key) {
        String ratingNumber = getCustomerRatingLowRangeStr(key);
        return ratingNumber + ".0 &amp; up";
    }

    private String getProductCategoryName(String key) {

        GenericValue productCategory = null;
        String productCategoryName = key;
        try {
            productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", key), true);
            productCategoryName = productCategory.getString("categoryName");
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
        }
        return productCategoryName;
    }

    private String getProductCategorySupportingText(String key) {

        GenericValue productCategory = null;
        String supportingText = "";
        try {
            productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", key), true);
            String supportingTextWrapper = productCategory.getString("longDescription");
            if (supportingTextWrapper != null) {
                supportingText = supportingTextWrapper.toString();
            }

        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
        }
        return supportingText;
    }

    private String getProductCategoryImage(String key) {

        GenericValue productCategory = null;
        String productCategoryImage = null;
        try {
            productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", key), true);
            String categoryImageUrl = productCategory.getString("categoryImageUrl");
            if (UtilValidate.isNotEmpty(categoryImageUrl)) {
                productCategoryImage = categoryImageUrl.toString();
            }
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
        }
        return productCategoryImage;
    }

    private Long getProductCategorySequenceNum(String key) {

        Long sequenceNum = Long.valueOf(0);
        try {
            List<GenericValue> productCategoryRollups = delegator.findByAndCache("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", key));
            productCategoryRollups = EntityUtil.filterByDate(productCategoryRollups);
            if (UtilValidate.isNotEmpty(productCategoryRollups)) {
                GenericValue productCategoryRollup = EntityUtil.getFirst(productCategoryRollups);
                sequenceNum = productCategoryRollup.getLong("sequenceNum");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
        }
        return sequenceNum;
    }

    public static List<SolrIndexDocument> getDuplicateProducts(List<SolrIndexDocument> results) {
        Iterator itr = results.iterator();
        List subresult = FastList.newInstance();
        List dupresult = FastList.newInstance();
        try {
            while(itr.hasNext()) {
                SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itr.next();
                if (subresult.contains(solrIndexDocument.getProductId())) {
                    //results.remove(itr.next());
                	dupresult.add(solrIndexDocument);
                } else {
                    subresult.add(solrIndexDocument.getProductId());
                }
            }
        } catch (Exception e) {
            Debug.log(e.getMessage());
        }
        return dupresult;
    }
}
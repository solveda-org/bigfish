package com.osafe.events;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;

import com.osafe.services.SolrIndexDocument;
import com.osafe.solr.CommandContext;
import com.osafe.solr.RefinementsHelperSolr;
import com.osafe.solr.SolrConstants;
import com.osafe.util.Util;

public class SolrEvents {
    public static final String module = SolrEvents.class.getName();
    private static final ResourceBundle OSAFE_UI_LABELS = UtilProperties.getResourceBundle("OSafeUiLabels.xml", Locale.getDefault());
    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("osafe.properties", Locale.getDefault());

    public static String solrSearch(HttpServletRequest request, HttpServletResponse response) {
        try {

            Delegator delegator = (Delegator) request.getAttribute("delegator");
            // Select only products that are in the passed category
            String productCategoryId = request.getParameter("productCategoryId");
            if (UtilValidate.isEmpty(productCategoryId))
            {
                 productCategoryId = (String)request.getAttribute("productCategoryId");
                
            }
            String topMostProductCategoryId = request.getParameter("topMostProductCategoryId");
            if (UtilValidate.isEmpty(topMostProductCategoryId))
            {
                topMostProductCategoryId = (String)request.getAttribute("topMostProductCategoryId");
                
            }

            // Get text to use for a site search query
            String searchText = Util.stripHTML(request.getParameter("searchText"));
            
            if (UtilValidate.isEmpty(searchText))
            {
                searchText = Util.stripHTML((String)request.getAttribute("searchText"));
            }
            
            // Get text to use for a site search query
            String searchTextSpellCheck = Util.stripHTML((String)request.getAttribute("searchTextSpellCheck"));
            
            if (UtilValidate.isNotEmpty(searchTextSpellCheck))
            {
                searchText = searchTextSpellCheck;
            }
            
            GenericValue productStore = ProductStoreWorker.getProductStore(request);
            String productStoreId = productStore.getString("productStoreId");

            String catalogTopCategoryId = CatalogWorker.getCatalogTopCategoryId(request);
            
            String solrServer = OSAFE_PROPS.getString("solr-search-server");
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(solrServer);
            solr.setRequestWriter(new BinaryRequestWriter());

            String facetSearchCategoryLabel = OSAFE_UI_LABELS.getString("FacetSearchCategoryCaption");
            String facetProductCategoryLabel = OSAFE_UI_LABELS.getString("FacetProductCategoryCaption");

            Map<String, String> mProductStoreParms = FastMap.newInstance();
            List<String> facetGroups = FastList.newInstance();
            Map<String, String> facetGroupDescriptions = FastMap.newInstance();
            Map<String, String> facetGroupIds = FastMap.newInstance();
            Map<String, String> facetGroupFacetSorts = FastMap.newInstance();

            //Build product Store Parms
            List<GenericValue> productStoreParms = delegator.findByAndCache("XProductStoreParm",UtilMisc.toMap("productStoreId",productStoreId));
            if (UtilValidate.isNotEmpty(productStoreParms))
            {
                for (int i=0;i < productStoreParms.size();i++)
                {
                    GenericValue prodStoreParm = (GenericValue) productStoreParms.get(i);
                    mProductStoreParms.put(prodStoreParm.getString("parmKey"),prodStoreParm.getString("parmValue"));
                }
            }
            int solrPageSize = NumberUtils.toInt(mProductStoreParms.get("PLP_NUM_ITEMS_PER_PAGE"), 20);
            if (solrPageSize == 0) {
                solrPageSize = 20;
            }
            


            // If Criteria has been passed, this should redirect the user to the
            // "No Results" page
            if (UtilValidate.isEmpty(searchText) && UtilValidate.isEmpty(productCategoryId)) {
                request.setAttribute("emptySearch", "Y");
                return "error";
            }

            // Get facet groups for product category
            // Available Facets For "Product Category"
            if (UtilValidate.isNotEmpty(productCategoryId)) {
                String queryProductCategoryFacets = "rowType:facetGroup AND productCategoryId:" + productCategoryId;
                SolrQuery solrQueryProductCategoryFacets = new SolrQuery(queryProductCategoryFacets);
                solrQueryProductCategoryFacets.setRows(999);
                QueryResponse responseProductCategoryFacets = solr.query(solrQueryProductCategoryFacets);
                List<SolrIndexDocument> resultsProductCategoryFacets = responseProductCategoryFacets.getBeans(SolrIndexDocument.class);
                if (UtilValidate.isNotEmpty(resultsProductCategoryFacets)) {
                    for (SolrIndexDocument doc : resultsProductCategoryFacets) {

                        String id = doc.getProductFeatureGroupId();
                        String description = doc.getProductFeatureGroupDescription();
                        String facetSort = doc.getProductFeatureGroupFacetSort();
                        String key = doc.getProductCategoryFacetGroups();
                        facetGroups.add(key);
                        facetGroupDescriptions.put(key, description);
                        facetGroupIds.put(key, id);
                        facetGroupFacetSorts.put(key, facetSort);

                    }
                }
            } 

            String queryFacet = null;
            String filterPriceQuery = null;
            String filterCustomerRatingQuery = null;
            String filterGroup = request.getParameter("filterGroup");
            if (UtilValidate.isEmpty(filterGroup))
            {
                filterGroup = (String)request.getAttribute("filterGroup");
            }
            
            CommandContext cc = new CommandContext();
            String requestURI = request.getRequestURI();
            String[] requestURIParts = requestURI.split("/");
            cc.setRequest(request);
            cc.setRequestName(requestURIParts[requestURIParts.length - 1]);
            cc.setFilterGroupsDescriptions(facetGroupDescriptions);
            cc.setFilterGroupsIds(facetGroupIds);
            cc.setFilterGroupsFacetSorts(facetGroupFacetSorts);

            if (UtilValidate.isNotEmpty(searchText)) {
                queryFacet = "searchText:" + searchText;
                cc.setSearchText(searchText);

                if (UtilValidate.isEmpty(productCategoryId) && UtilValidate.isNotEmpty(topMostProductCategoryId)) {
                    queryFacet += " AND " + "topMostProductCategoryId:" + topMostProductCategoryId;
                    cc.setTopMostProductCategoryId(topMostProductCategoryId);
                    facetGroups.add(0, SolrConstants.TYPE_PRODUCT_CATEGORY);
                    facetGroupDescriptions.put(SolrConstants.TYPE_PRODUCT_CATEGORY, facetSearchCategoryLabel);
                } else if (UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isNotEmpty(topMostProductCategoryId)) {
                    cc.setTopMostProductCategoryId(topMostProductCategoryId);
                    cc.setProductCategoryId(productCategoryId);
                } else {
                    // Add the "Top Most Product Category" facet to the top
                    facetGroups.add(0, SolrConstants.TYPE_PRODUCT_CATEGORY);
                    facetGroupDescriptions.put(SolrConstants.TYPE_PRODUCT_CATEGORY, facetSearchCategoryLabel);
                }

            } else if (UtilValidate.isNotEmpty(productCategoryId)) {
                queryFacet = "productCategoryId:" + productCategoryId;
                cc.setProductCategoryId(productCategoryId);
            }

            
          //Add all feature groups to query facet, since on site search we don't know which feature group is associated.
            if (UtilValidate.isEmpty(productCategoryId)) {
            	List<GenericValue> productFeatureGroups = delegator.findList("ProductFeatureGroup", null, null, null, null, true);
            	if(UtilValidate.isNotEmpty(productFeatureGroups)) {
            		for(GenericValue productFeatureGroup : productFeatureGroups) {
            			String productFeatureGroupId = SolrConstants.EXTRACT_PRODUCT_FEATURE_PREFIX + productFeatureGroup.getString("productFeatureGroupId");
            			facetGroups.add(productFeatureGroupId);
            			facetGroupIds.put(productFeatureGroupId, productFeatureGroup.getString("productFeatureGroupId"));
            		}
            		cc.setFilterGroupsIds(facetGroupIds);
            	}
            }
            
            // Get filter groups that are being passed and exclude them from the
            // list of facets we are want to show
            Float priceLow = null;
            Float priceHigh = null;
            if (UtilValidate.isNotEmpty(filterGroup)) {
                String[] filterGroupArr = StringUtils.split(filterGroup, "|");

                for (int i = 0; i < filterGroupArr.length; i++) {
                    String filterGroupValue = filterGroupArr[i];
                    if (filterGroupValue.toLowerCase().contains("price") || filterGroupValue.toLowerCase().contains("customer_rating")) {
                        filterGroupValue = StringUtil.replaceString(filterGroupValue,"+", " ");
                    }
                    
                    String[] splitTemp = filterGroupValue.split(":");
                    String encodedValue = null;
                    try {
                        encodedValue = URLEncoder.encode(splitTemp[1], SolrConstants.DEFAULT_ENCODING);
                    } catch (UnsupportedEncodingException e) {
                        encodedValue = splitTemp[1];
                    }
                    filterGroupValue = splitTemp[0] + ":" + encodedValue;
                    cc.addFilterGroup(filterGroupValue);
                    // Price
                    if (filterGroupValue.toLowerCase().contains("price")) {
                        filterPriceQuery = filterGroupValue.replaceAll("productFeature_PRICE", SolrConstants.TYPE_PRICE);
                        //calculation for price range retrieve on price facet selection click
                        String[] priceRangeArr =  URLDecoder.decode(encodedValue, SolrConstants.DEFAULT_ENCODING).split(" ");
                        try {
                            priceLow = Float.parseFloat(priceRangeArr[0].substring(1)) +1;
                            priceHigh = Float.parseFloat(priceRangeArr[1].substring(0, priceRangeArr[1].length()-1)) - 1;
                        } catch (NumberFormatException nexc) {
                            priceLow = null;
                            priceHigh = null;
                        }
                        // skip
                        continue;
                    }

                    // Customer Rating
                    if (filterGroupValue.toLowerCase().contains("customer_rating")) {
                        filterCustomerRatingQuery = filterGroupValue.replaceAll("productFeature_CUSTOMER_RATING", SolrConstants.TYPE_CUSTOMER_RATING);
                        // skip
                        continue;
                    }
                    queryFacet += " AND " + filterGroupValue;

                    String[] filterGroupParts = filterGroupArr[i].split(":");
                    facetGroups.remove(filterGroupParts[0]);
                }
            }
            //queryFacet += " Red";
            // Add filter query (Price)
            if (UtilValidate.isNotEmpty(filterPriceQuery)) {
                try {
                    filterPriceQuery = URLDecoder.decode(filterPriceQuery, SolrConstants.DEFAULT_ENCODING);
                } catch (UnsupportedEncodingException e) {
                    Debug.logError(e, module);
                }
                queryFacet += " AND " + filterPriceQuery;
            }

            // Add filter query (Customer Rating)
            if (UtilValidate.isNotEmpty(filterCustomerRatingQuery)) {
                try {
                    filterCustomerRatingQuery = URLDecoder.decode(filterCustomerRatingQuery, SolrConstants.DEFAULT_ENCODING);
                } catch (UnsupportedEncodingException e) {
                    Debug.logError(e, module);
                }
                queryFacet += " AND " + filterCustomerRatingQuery;
            }

            // Check request name to decide how to query index
            String pathInfo = request.getPathInfo();
            String requestName = "";
            if (pathInfo != null) {
                requestName = pathInfo.substring(1);
            }
            
                queryFacet += " AND rowType:product";
                try {
                    String productParentCategoryId="";
                    List<GenericValue> productCategoryRollups = null;
                    List<String> parentProductCategoryIds = null;
                    
                    if (UtilValidate.isNotEmpty(productCategoryId)) {
                    	productCategoryRollups = EntityUtil.filterByDate(delegator.findByAndCache("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", productCategoryId), UtilMisc.toList("sequenceNum")));
                    	if(UtilValidate.isNotEmpty(productCategoryRollups)) {
                    	    parentProductCategoryIds = EntityUtil.getFieldListFromEntityList(productCategoryRollups, "parentProductCategoryId", true);
                    	}
                    }
                    if (UtilValidate.isNotEmpty(productCategoryRollups)) {
                        productParentCategoryId = EntityUtil.getFirst(productCategoryRollups).getString("parentProductCategoryId");
                    }

                    
                         List<String> facetCatGroups = FastList.newInstance();
                            Map<String, String> facetCatGroupDescriptions = FastMap.newInstance();

                            CommandContext ccCat = new CommandContext();
                            ccCat.setRequest(request);
                            ccCat.setFilterGroupsDescriptions(facetCatGroupDescriptions);
                            ccCat.setOnCategoryList(true);
                            ccCat.setRequestName("eCommerceProductList");
                            ccCat.setProductCategoryId(null);
                            String queryCatFacet = "";
                            if (UtilValidate.isNotEmpty(productParentCategoryId)) {
                            	if(parentProductCategoryIds.contains(catalogTopCategoryId)) {
                            		queryCatFacet = "topMostProductCategoryId:" + productCategoryId + " AND ";
                            	} else {
                            		queryCatFacet = "topMostProductCategoryId:" + productParentCategoryId + " AND ";
                            	}
                                facetCatGroups.add(0, SolrConstants.TYPE_PRODUCT_CATEGORY);
                                facetCatGroupDescriptions.put(SolrConstants.TYPE_PRODUCT_CATEGORY, facetProductCategoryLabel);
                            }
                            queryCatFacet += "rowType:product";

                            SolrQuery solrCatQueryFacet = new SolrQuery(queryCatFacet);
                            solrCatQueryFacet.setFacet(true);
                            solrCatQueryFacet.setFacetSort(FacetParams.FACET_SORT_INDEX);
                            solrCatQueryFacet.setFacetMinCount(1);
                            
                            // Add the facet groups to the query
                            if (UtilValidate.isNotEmpty(facetCatGroups)) {
                                for (String groupName : facetCatGroups) {
                                    solrCatQueryFacet.addFacetField(groupName);
                                }
                            }
                            
                            QueryResponse responseCatFacet = solr.query(solrCatQueryFacet);
                            List<FacetField> resultsFacet = responseCatFacet.getFacetFields();
                            SolrDocumentList sdl = responseCatFacet.getResults();
                            RefinementsHelperSolr rh = new RefinementsHelperSolr(ccCat, delegator);
                            List facetCatList = (List) rh.processRefinements(resultsFacet);
                            request.setAttribute("facetCatList", facetCatList);

                }
                 catch (Exception e) {
                     
                 }
     
            SolrQuery solrQueryFacet = new SolrQuery(queryFacet);
            solrQueryFacet.setFacet(true);
            solrQueryFacet.setFacetSort(FacetParams.FACET_SORT_INDEX);
            solrQueryFacet.setFacetMinCount(1);

                        
            // Pricing Facets
            if (UtilValidate.isEmpty(priceLow)) {
                String queryPriceRangeLow = queryFacet;
                queryPriceRangeLow += " AND _val_:" + "\"" + "max(price, 99999)" + "\"";
                SolrQuery solrQueryPriceRangeLow = new SolrQuery(queryPriceRangeLow);
                solrQueryPriceRangeLow.addSortField("price", ORDER.asc);
                solrQueryPriceRangeLow.setRows(Integer.valueOf(1));
                QueryResponse responsePriceRangeLow = solr.query(solrQueryPriceRangeLow);
                List<SolrIndexDocument> resultsPriceRangeLow = responsePriceRangeLow.getBeans(SolrIndexDocument.class);
                if (UtilValidate.isNotEmpty(resultsPriceRangeLow)) {
                    SolrIndexDocument solrIndexDocumentLow = resultsPriceRangeLow.get(0);
                    priceLow = solrIndexDocumentLow.getPrice();
                }
            }

            if (UtilValidate.isEmpty(priceHigh)) {
                String queryPriceRangeHigh = queryFacet;
                queryPriceRangeHigh += " AND _val_:" + "\"" + "max(price,0)" + "\"";
                SolrQuery solrQueryPriceRangeHigh = new SolrQuery(queryPriceRangeHigh);
                solrQueryPriceRangeHigh.addSortField("price", ORDER.desc);
                solrQueryPriceRangeHigh.setRows(Integer.valueOf(1));
                QueryResponse responsePriceRangeHigh = solr.query(solrQueryPriceRangeHigh);
                List<SolrIndexDocument> resultsPriceRangeHigh = responsePriceRangeHigh.getBeans(SolrIndexDocument.class);
                if (UtilValidate.isNotEmpty(resultsPriceRangeHigh)) {
                    SolrIndexDocument solrIndexDocumentHigh = resultsPriceRangeHigh.get(0);
                    priceHigh = solrIndexDocumentHigh.getPrice();
                }    
            }
            
			
            if (priceLow != null && priceHigh != null) {
                float priceHighVal = priceHigh.floatValue();
                float priceLowVal = priceLow.floatValue();

                priceHighVal = Math.round(priceHighVal + 1.0);
                priceLowVal = Math.max(0f, Math.round(priceLowVal - 1.0));

                float priceRange = priceHighVal - priceLowVal;
                float step = priceRange / 5;
                //incremental factor calculation as defined in BF Facet Price Calculator.xls
                if (step > 10000)
                {
                    if ((step%1000) > 0)
                    {
                        step+= (1000-(step%1000));
                    }
                }
                else if (step > 1000)
                {
                    if ((step%100) > 0)
                    {
                        step+= (100-(step%100));
                    }
                }
                else if (step > 100)
                {
                    if ((step%10) > 0)
                    {
                        step+= (10-(step%10));
                    }
                }
                else
                {
                    if ((step%1) > 0)
                    {
                        step+= (1-(step%1));
                    }
                }

                float rangeLowVal = priceLowVal;
                float rangeHighVal = priceLowVal + step;
                while (priceRange > 0) {
                    String priceRangeQuery = queryFacet;
                    priceRangeQuery += " AND price:[" + (rangeLowVal) + " " + rangeHighVal + "]";
                    SolrQuery solrPriceRangeQuery = new SolrQuery(priceRangeQuery);
                    QueryResponse responsePriceRangeQuery = solr.query(solrPriceRangeQuery);
                    List<SolrIndexDocument> resultsPriceRange = responsePriceRangeQuery.getBeans(SolrIndexDocument.class);
                    if (UtilValidate.isNotEmpty(resultsPriceRange))
                    {
                        float tempPriceRange =  priceRange - step;
                        float tempRangeLowVal = rangeHighVal + .01f;
                        float tempRangeHighVal = rangeHighVal + step;
                        while (tempPriceRange > 0) {
                            priceRangeQuery = queryFacet;
                            priceRangeQuery += " AND price:[" + (tempRangeLowVal) + " " + tempRangeHighVal + "]";
                            solrPriceRangeQuery = new SolrQuery(priceRangeQuery);
                            responsePriceRangeQuery = solr.query(solrPriceRangeQuery);
                            resultsPriceRange = responsePriceRangeQuery.getBeans(SolrIndexDocument.class);
                            if (UtilValidate.isNotEmpty(resultsPriceRange))
                            {
                                break;
                            }
                            else
                            {
                                tempPriceRange = tempPriceRange - step;
                                tempRangeHighVal = tempRangeHighVal + step;
                            }
                        
                        }
                        priceRange = tempPriceRange + step;
                        rangeHighVal = tempRangeHighVal - step;
                        solrQueryFacet.addFacetQuery("price:[" + (rangeLowVal) + " " + rangeHighVal + "]");
                        rangeLowVal = rangeHighVal + .01f;
                        priceRange = priceRange - step;
                        rangeHighVal = rangeHighVal + step;
                    }
                    else
                    {
                        priceRange = priceRange - step;
                        rangeHighVal = rangeHighVal + step;
                    }
                }
            }

            // Customer Rating Facets
            // This should always give us the same set of "Customer Rating" facets
            int ratingStart = NumberUtils.toInt(OSAFE_PROPS.getString("customer-rating-start"), 4);
            int ratingEnd = NumberUtils.toInt(OSAFE_PROPS.getString( "customer-rating-end"), 1);
            int ratingMax = NumberUtils.toInt(OSAFE_PROPS.getString("customer-rating-max"), 5);
            for (int i = ratingStart; i >= ratingEnd; i--) {
                solrQueryFacet.addFacetQuery("customerRating:[" + i + " " + ratingMax + "]");
            }

            // Paging and how many rows to display
            String rows = request.getParameter("rows");
            if (UtilValidate.isEmpty(rows))
            {
                rows = (String)request.getAttribute("rows");
                
            }
            String start = request.getParameter("start");
            if (UtilValidate.isEmpty(start))
            {
                start = (String)request.getAttribute("start");
                
            }
            int pageSize = Integer.valueOf(NumberUtils.toInt(rows, solrPageSize));
            if (pageSize != solrPageSize) {
                cc.setNumberOfRowsShown("" + pageSize);
            }

            solrQueryFacet.setRows(pageSize);
            solrQueryFacet.setStart(Integer.valueOf(NumberUtils.toInt(start, 0)));

            // Results Sorting
            String sortName = null;
            String sortDir = null;
            String defaultSort= null;
            String SORT_OPTIONS =null;
            String sortResults = request.getParameter("sortResults");
            if (UtilValidate.isEmpty(sortResults))
            {
                sortResults = (String)request.getAttribute("sortResults");
            }
            
            //Reads the system parameter to get all the Available sort options
            SORT_OPTIONS  = Util.getProductStoreParm(request, "PLP_AVAILABLE_SORT");
            List<String> SORT_OPTIONS_LIST = StringUtil.split(SORT_OPTIONS, ",");

            List<Map<String,String>> sortOptions = FastList.newInstance();
            if (UtilValidate.isNotEmpty(SORT_OPTIONS_LIST)) {
                defaultSort = Util.getProductStoreParm(request, "PLP_DEFAULT_SORT");
                
              //makes a list of Sort Options
                for (String sortOption: SORT_OPTIONS_LIST) {
                  sortOption = sortOption.trim().toUpperCase();
                  if(OSAFE_PROPS.containsKey(sortOption)){
                  String sortOptionTxt = OSAFE_PROPS.getString(sortOption);//UtilProperties.getPropertyValue("osafe.properties", sortOption);
                  if (UtilValidate.isNotEmpty(sortOptionTxt)){
                      List<String> sortOptionAttrList = StringUtil.split(sortOptionTxt, "|");
                      if ((UtilValidate.isNotEmpty(sortOptionAttrList)) && (sortOptionAttrList.size() > 1)){
                          Map<String,String> sortOptionMap = FastMap.newInstance();
                          sortOptionMap.put("SORT_OPTION",sortOption );
                          sortOptionMap.put("SOLR_VALUE", sortOptionAttrList.get(0));
                          sortOptionMap.put("SORT_OPTION_LABEL", sortOptionAttrList.get(1));
                          sortOptions.add(sortOptionMap);
                      }
                  }
                }
                }
               
            //Sets default value for PLP sort. 
            if(UtilValidate.isEmpty(sortResults)){
                for (Map sortOptionMap : sortOptions) {
                    if (UtilValidate.isNotEmpty(defaultSort)) {
                        String sortOption = (String)sortOptionMap.get("SORT_OPTION");
                        if(defaultSort.equalsIgnoreCase(sortOption)){
                            sortResults = (String)sortOptionMap.get("SOLR_VALUE");
                            break;
                        }
                    }
                }
                if(UtilValidate.isEmpty(sortResults) && (sortOptions.size() > 0)){
                	Map sortOptionMap = sortOptions.get(0);
                	sortResults = (String)sortOptionMap.get("SOLR_VALUE");
                }
           }
         }
            if (UtilValidate.isNotEmpty(sortResults)) {
                cc.setSortParameterName("sortResults");
                cc.setSortParameterValue(sortResults);
                String[] sortResultsParts = StringUtils.split(sortResults, "-");
                if (sortResultsParts.length > 1) {
                    sortName = sortResultsParts[0];
                    sortDir = sortResultsParts[1];
                    ORDER solrOrder = ORDER.asc;
                    if ("desc".equalsIgnoreCase(sortDir)) {
                        solrOrder = ORDER.desc;
                    }
                    solrQueryFacet.addSortField(sortName, solrOrder);
                }
            }
            else{
                solrQueryFacet.addSortField("customerRating", ORDER.desc);
            }
            
            // Add Category facets to the facetCatList
            if(UtilValidate.isNotEmpty(searchText)) 
            {
                if(facetGroups.contains(SolrConstants.TYPE_PRODUCT_CATEGORY)){
                    solrQueryFacet.addFacetField(SolrConstants.TYPE_PRODUCT_CATEGORY);
                }
            	QueryResponse responseCatFacet = solr.query(solrQueryFacet);
                List<FacetField> resultsCatFacet = responseCatFacet.getFacetFields();
                RefinementsHelperSolr rhCatFacet = new RefinementsHelperSolr(cc, delegator);
                List facetCatList = (List) rhCatFacet.processRefinements(resultsCatFacet);
                request.setAttribute("facetCatList", facetCatList);
                solrQueryFacet.removeFacetField(SolrConstants.TYPE_PRODUCT_CATEGORY);
                facetGroups.remove(SolrConstants.TYPE_PRODUCT_CATEGORY);
            }
            
            // Remove Category Facets while using site search, as we already added the category facets to the facetCatList.
            /*if(facetGroups.contains(SolrConstants.TYPE_PRODUCT_CATEGORY) && UtilValidate.isNotEmpty(searchText)){
        		facetGroups.remove(SolrConstants.TYPE_PRODUCT_CATEGORY);
        	}*/
            
            // Add the facet groups to the query
            if (UtilValidate.isNotEmpty(facetGroups)) {
                for (String groupName : facetGroups) {
                    solrQueryFacet.addFacetField(groupName);
                }
            }
            
            //used in resultsNavigation.ftl
            request.setAttribute("sortOptions", sortOptions);

            QueryResponse responseFacet = solr.query(solrQueryFacet);
            
            List<FacetField> resultsFacet = responseFacet.getFacetFields();
            List<SolrIndexDocument> results = responseFacet.getBeans(SolrIndexDocument.class);
            
            SolrDocumentList sdl = responseFacet.getResults();
            
            // Get the Complete Document List
            solrQueryFacet.setRows((int)sdl.getNumFound());
            solrQueryFacet.setStart(0);
            QueryResponse responseFacetComplete = solr.query(solrQueryFacet);
            List<SolrIndexDocument> resultsComplete = responseFacetComplete.getBeans(SolrIndexDocument.class);
            List newresultComplete = removeDuplicateEntry(resultsComplete);
            
            // resultsFacetQueries holds both price a customer rating facets
            Map<String, Integer> resultsFacetQueries = responseFacet.getFacetQuery();
            
            
            RefinementsHelperSolr rh = new RefinementsHelperSolr(cc, delegator);
            List facetList = (List) rh.processRefinements(resultsFacet);
            List facetListPriceRange = (List) rh.processPriceRangeRefinements(resultsFacetQueries, results);
            List facetListCustomerRating = (List) rh.processCustomerRatingRefinements(resultsFacetQueries);
            List newresult = removeDuplicateEntry(results);
            int startIndex = Integer.valueOf(NumberUtils.toInt(start, 0));
            if (newresultComplete.size() > (startIndex+pageSize)) {
                newresult = newresultComplete.subList(startIndex, (startIndex+pageSize));
            } else {
                newresult = newresultComplete.subList(startIndex, newresultComplete.size());
            }
            
            if (UtilValidate.isNotEmpty(searchText) && sdl.getNumFound() < 1) 
            {
                return "error";
            }
            
            request.setAttribute("numFound", newresultComplete.size());
            request.setAttribute("start", sdl.getStart());
            request.setAttribute("size", sdl.size());
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("solrPageSize", solrPageSize);
            request.setAttribute("facetList", facetList);
            request.setAttribute("facetListPriceRange", facetListPriceRange);
            request.setAttribute("facetListCustomerRating", facetListCustomerRating);
            request.setAttribute("documentList", newresult); //request.setAttribute("documentList", newresult);
            request.setAttribute("completeDocumentList", newresultComplete);
            request.setAttribute("sortResults",sortResults);

        } catch (MalformedURLException e) {
            Debug.logError(e.getMessage(), module);
        } catch (SolrServerException e) {
            Debug.logError(e.getMessage(), module);
        }catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
        return "success";
    }
    
    public static List<SolrIndexDocument> removeDuplicateEntry (List<SolrIndexDocument> results) {
        Iterator itr = results.iterator();
        List subresult = FastList.newInstance();
        List newresult = FastList.newInstance();
        try {
            while(itr.hasNext()) {
                SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itr.next();
                if (subresult.contains(solrIndexDocument.getProductId())) {
                    //results.remove(itr.next());
                } else {
                
                    newresult.add(solrIndexDocument);
                    subresult.add(solrIndexDocument.getProductId());
                }
            }
        } catch (Exception e) {
            Debug.log(e.getMessage());
        }
        return newresult;
    }
    
    public static String solrSpellCheck(HttpServletRequest request, HttpServletResponse response) 
    {
    	String solrServer = OSAFE_PROPS.getString("solr-search-server");
        
    	String searchText = Util.stripHTML(request.getParameter("searchText"));
        
        if (UtilValidate.isEmpty(searchText))
        {
            searchText = Util.stripHTML((String)request.getAttribute("searchText"));
        }
        try 
        {
        	CommonsHttpSolrServer solr = new CommonsHttpSolrServer(solrServer);
        	ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("qt", "/spell");
            params.set("q", searchText);
            QueryResponse responseSpellCheck = solr.query(params);
            if(responseSpellCheck.getSpellCheckResponse().isCorrectlySpelled()) 
            {
            	request.setAttribute("searchText", searchText);	
            } 
            else 
            {
            	request.setAttribute("searchTextSpellCheck", responseSpellCheck.getSpellCheckResponse().getCollatedResult());
            }
        } 
        catch (Exception e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        return "success";
    }
    
    public static String autoSuggestionList(HttpServletRequest request, HttpServletResponse response) 
    {
    	String solrServer = OSAFE_PROPS.getString("solr-search-server");
    	
    	List<String> autoSuggestionList = FastList.newInstance();
    	List<Long> autoSuggestionFreqList = FastList.newInstance();
        
    	String searchText = Util.stripHTML(request.getParameter("searchText"));
        
        if (UtilValidate.isEmpty(searchText))
        {
            searchText = Util.stripHTML((String)request.getAttribute("searchText"));
        }
        try 
        {
        	CommonsHttpSolrServer solr = new CommonsHttpSolrServer(solrServer);
        	ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("qt", "/terms");
            params.set("terms.fl", "autocomplete_text");
            params.set("omitHeader", "true");
            params.set("terms.sort", "index");
            params.set("terms.prefix", searchText);
            QueryResponse responseSpell = solr.query(params);
            String terms = responseSpell.getTermsResponse().getTerms("autocomplete_text").get(0).getTerm();
            
            List<Term> termsListParent = responseSpell.getTermsResponse().getTerms("autocomplete_text");
            
            for(Term termsListChild : termsListParent)
            {
                autoSuggestionList.add(termsListChild.getTerm());
            	autoSuggestionFreqList.add(termsListChild.getFrequency());
            }
            if(UtilValidate.isNotEmpty(autoSuggestionList))
            {
            	HashSet set = new HashSet(autoSuggestionList);
                autoSuggestionList.clear();
                autoSuggestionList.addAll(set);
            }
            
            request.setAttribute("autoSuggestionList", autoSuggestionList);
            request.setAttribute("response", "success");
        } 
        catch (Exception e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        return "success";
    }
    
}
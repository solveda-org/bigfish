package com.osafe.events;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;

import com.osafe.services.SolrIndexDocument;
import com.osafe.solr.CommandContext;
import com.osafe.solr.GenericRefinement;
import com.osafe.solr.RefinementsHelperSolr;
import com.osafe.solr.SolrConstants;
import com.osafe.util.Util;

@SuppressWarnings("deprecation")
public class SolrEvents 
{
    public static final String module = SolrEvents.class.getName();
    private static final ResourceBundle OSAFE_UI_LABELS = UtilProperties.getResourceBundle("OSafeUiLabels.xml", Locale.getDefault());
    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());
    private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
    
    public static String solrSearch(HttpServletRequest request, HttpServletResponse response) 
    {
        try 
        {
        	String nowDate = _sdf.format(UtilDateTime.nowDate());
        	
            Delegator delegator = (Delegator) request.getAttribute("delegator");
            String productCategoryId = request.getParameter("productCategoryId");
            String productSearchCategoryId = request.getParameter("productSearchCategoryId");
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

            //replace special characters with their solr encoded value
            if(UtilValidate.isNotEmpty(searchText))
            {
            	searchText = replaceSpecialChar(searchText);
            }

            //append "" to search text to perform exact search
            String searchType = (String)request.getAttribute("searchType");
            if (UtilValidate.isNotEmpty(searchText) && UtilValidate.isEmpty(searchType))
            {
                searchText = "\""+searchText+"\"";
            }

            // Get text after spell check to use for a site search query
            if(UtilValidate.isNotEmpty(searchType) && searchType.equalsIgnoreCase("SpellCheckSearch"))
            {
                String searchTextSpellCheck = Util.stripHTML((String)request.getAttribute("searchTextSpellCheck"));

                if (UtilValidate.isNotEmpty(searchTextSpellCheck))
                {
                    searchText = searchTextSpellCheck;
                }
            }


            GenericValue productStore = ProductStoreWorker.getProductStore(request);
            String productStoreId = productStore.getString("productStoreId");

            String catalogTopCategoryId = CatalogWorker.getCatalogTopCategoryId(request);

            String solrServer = OSAFE_PROPS.getString("solrSearchServer");
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
            if (solrPageSize == 0) 
            {
                solrPageSize = 20;
            }

            String facetLimitStr = mProductStoreParms.get("FACET_VALUE_MAX");
            int facetLimit = 0;
            if(UtilValidate.isNotEmpty(facetLimitStr))
            {
                try
                {
                    facetLimit = Integer.parseInt(facetLimitStr);
                }
                catch(NumberFormatException nfe)
                {
                    Debug.logError(nfe.getMessage(), module);
                }
            }

            // If Criteria has been passed, this should redirect the user to the
            // "No Results" page
            if (UtilValidate.isEmpty(searchText) && UtilValidate.isEmpty(productCategoryId)) 
            {
                request.setAttribute("emptySearch", "Y");
                return "error";
            }

            // Get facet groups for product category
            // Available Facets For "Product Category"
            if (UtilValidate.isNotEmpty(productCategoryId)) 
            {
                String queryProductCategoryFacets = "rowType:facetGroup AND productCategoryId:" + productCategoryId;
                SolrQuery solrQueryProductCategoryFacets = new SolrQuery(queryProductCategoryFacets);
                solrQueryProductCategoryFacets.setRows(999);
                QueryResponse responseProductCategoryFacets = solr.query(solrQueryProductCategoryFacets);
                List<SolrIndexDocument> resultsProductCategoryFacets = responseProductCategoryFacets.getBeans(SolrIndexDocument.class);
                if (UtilValidate.isNotEmpty(resultsProductCategoryFacets)) {
                    for (SolrIndexDocument doc : resultsProductCategoryFacets) 
                    {
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

            if (UtilValidate.isNotEmpty(searchText)) 
            {
                //Add wildcard character (*) to the search text
                if(UtilValidate.isNotEmpty(searchType) && searchType.equalsIgnoreCase("WildCardSearch"))
                {
                    queryFacet = "searchText: (" + searchText +"*)";
                }
                else
                {
                    queryFacet = "searchText: (" + searchText +")";
                }
                searchText = searchText.replace("\"", "");
                cc.setSearchText(searchText);

                if (UtilValidate.isEmpty(productCategoryId) && UtilValidate.isNotEmpty(topMostProductCategoryId)) 
                {
                    queryFacet += " AND " + "topMostProductCategoryId:" + topMostProductCategoryId;
                    cc.setTopMostProductCategoryId(topMostProductCategoryId);
                    facetGroups.add(0, SolrConstants.TYPE_PRODUCT_CATEGORY);
                    facetGroupDescriptions.put(SolrConstants.TYPE_PRODUCT_CATEGORY, facetSearchCategoryLabel);
                }
                else if (UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isNotEmpty(topMostProductCategoryId)) 
                {
                    cc.setTopMostProductCategoryId(topMostProductCategoryId);
                    cc.setProductCategoryId(productCategoryId);
                } 
                else 
                {
                    // Add the "Top Most Product Category" facet to the top
                    facetGroups.add(0, SolrConstants.TYPE_PRODUCT_CATEGORY);
                    facetGroupDescriptions.put(SolrConstants.TYPE_PRODUCT_CATEGORY, facetSearchCategoryLabel);
                }
                
                if (UtilValidate.isNotEmpty(productSearchCategoryId)) 
                {
                    queryFacet +=" AND " + "productCategoryId:" + productSearchCategoryId;
                    cc.setProductCategoryId(productSearchCategoryId);
                	
                }
                

            } 
            else if (UtilValidate.isNotEmpty(productCategoryId)) 
            {
                queryFacet = "productCategoryId:" + productCategoryId;
                cc.setProductCategoryId(productCategoryId);
            }


            //Add all feature groups to query facet, since on site search we don't know which feature group is associated.
            if (UtilValidate.isEmpty(productCategoryId)) 
            {
                List<GenericValue> productFeatureGroups = delegator.findList("ProductFeatureGroup", null, null, null, null, true);
                if(UtilValidate.isNotEmpty(productFeatureGroups)) 
                {
                    for(GenericValue productFeatureGroup : productFeatureGroups) 
                    {
                        String productFeatureGroupId = productFeatureGroup.getString("productFeatureGroupId");
                        facetGroups.add(productFeatureGroupId);
                        facetGroupIds.put(productFeatureGroupId, productFeatureGroup.getString("productFeatureGroupId"));
                    }
                    cc.setFilterGroupsIds(facetGroupIds);
                }
            }
            
            String facetValueStyle = Util.getProductStoreParm(request, "FACET_VALUE_STYLE");
            boolean facetMultiSelectTrue = false;
            if (UtilValidate.isNotEmpty(facetValueStyle) && (facetValueStyle.equalsIgnoreCase("CHECKBOX") || facetValueStyle.equalsIgnoreCase("DROPDOWN")))
            {
            	facetMultiSelectTrue = true;
            }
            
            // Get filter groups that are being passed and exclude them from the
            // list of facets we are want to show
            Float priceLow = null;
            Float priceHigh = null;
            Map<String, List> filterGroupParamMap = FastMap.newInstance();
            Map<String, String> filterGroupMap = FastMap.newInstance();
            String multiFacetInitialType = null;
            if (UtilValidate.isNotEmpty(filterGroup)) 
            {
                String[] filterGroupArr = StringUtils.split(filterGroup, "|");

                for (int i = 0; i < filterGroupArr.length; i++) 
                {
                    String filterGroupValue = filterGroupArr[i];
                    if (filterGroupValue.toLowerCase().contains("customer_rating")) 
                    {
                        filterGroupValue = StringUtil.replaceString(filterGroupValue,"+", " ");
                    }

                    String[] splitTemp = filterGroupValue.split(":");

                    if(UtilValidate.isEmpty(multiFacetInitialType))
                    {
                        multiFacetInitialType = splitTemp[0];
                    }

                    if(filterGroupParamMap.containsKey(splitTemp[0]))
                    {
                        List featureParamList = (List) filterGroupParamMap.get(splitTemp[0]);
                        featureParamList.add(URLDecoder.decode(splitTemp[1], SolrConstants.DEFAULT_ENCODING));
                        filterGroupParamMap.put(splitTemp[0], featureParamList);
                    }
                    else
                    {
                        filterGroupParamMap.put(splitTemp[0], UtilMisc.toList(URLDecoder.decode(splitTemp[1], SolrConstants.DEFAULT_ENCODING)));    
                    }
                    
                    String encodedValue = null;
                    
                    splitTemp[1] = replaceSpecialChar(splitTemp[1]);
                    /*splitTemp[1] = splitTemp[1].replace("/", "%2F");
                    splitTemp[1] = splitTemp[1].replace("\"", "%22");
                    splitTemp[1] = splitTemp[1].replace(":", "%3A");
                    splitTemp[1] = splitTemp[1].replace("&", "%26");*/
                    

                    try 
                    {
                        encodedValue = URLEncoder.encode(splitTemp[1], SolrConstants.DEFAULT_ENCODING);
                    } 
                    catch (UnsupportedEncodingException e) 
                    {
                        encodedValue = splitTemp[1];
                    }
                    filterGroupValue = splitTemp[0] + ":" + encodedValue;
                    cc.addFilterGroup(filterGroupValue);
                    
                    if (filterGroupValue.toLowerCase().contains("price")) 
                    {
                        // skip
                        continue;
                    }
                    // Customer Rating
                    if (filterGroupValue.toLowerCase().contains("customer_rating")) 
                    {
                        filterCustomerRatingQuery = filterGroupValue.replaceAll("CUSTOMER_RATING", SolrConstants.TYPE_CUSTOMER_RATING);
                        // skip
                        continue;
                    }
                    if(filterGroupValue.toLowerCase().contains("customer_rating"))
                    {
                        queryFacet += " AND " + filterGroupValue;
                    }
                    else
                    {
                        if(filterGroupMap.containsKey(splitTemp[0]))
                        {
                            filterGroupMap.put(splitTemp[0], filterGroupMap.get(splitTemp[0])+" "+splitTemp[1]);
                        }
                        else
                        {
                            filterGroupMap.put(splitTemp[0], splitTemp[1]);    
                        }
                    }

                    String[] filterGroupParts = filterGroupArr[i].split(":");
                    if(!facetMultiSelectTrue || filterGroupValue.toLowerCase().contains("productcategoryid"))
                    {
                        facetGroups.remove(filterGroupParts[0]);
                    }
                }
            }
            String queryFacetGroup = "";
            if(UtilValidate.isNotEmpty(filterGroupMap))
            {
                for (Map.Entry<String, String> entry : filterGroupMap.entrySet()) 
                {
                    queryFacetGroup += " AND " + entry.getKey()+": ("+entry.getValue()+")";
                }
            }
            queryFacet += " AND rowType:product AND -(-introductionDate:[* TO "+nowDate+"] AND introductionDate:[* TO *]) AND -(-salesDiscontinuationDate:["+nowDate+" TO *] AND salesDiscontinuationDate:[* TO *])";

            String queryFacetWithoutGroup = "";
            if(facetMultiSelectTrue)
            {
                queryFacetWithoutGroup = queryFacet; 
            }
            queryFacet+=queryFacetGroup;

            // Add filter query (Customer Rating)
            if (UtilValidate.isNotEmpty(filterCustomerRatingQuery)) 
            {
                try 
                {
                    filterCustomerRatingQuery = URLDecoder.decode(filterCustomerRatingQuery, SolrConstants.DEFAULT_ENCODING);
                } 
                catch (UnsupportedEncodingException e) 
                {
                    Debug.logError(e, module);
                }
                queryFacet += " AND " + filterCustomerRatingQuery;
            }

            List facetCatList = null;
            try 
            {
                String productParentCategoryId="";
                List<GenericValue> productCategoryRollups = null;
                List<String> parentProductCategoryIds = null;

                if (UtilValidate.isNotEmpty(productCategoryId)) 
                {
                    productCategoryRollups = EntityUtil.filterByDate(delegator.findByAndCache("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", productCategoryId), UtilMisc.toList("sequenceNum")));
                    if(UtilValidate.isNotEmpty(productCategoryRollups)) 
                    {
                        parentProductCategoryIds = EntityUtil.getFieldListFromEntityList(productCategoryRollups, "parentProductCategoryId", true);
                    }
                }
                if (UtilValidate.isNotEmpty(productCategoryRollups)) 
                {
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
                if (UtilValidate.isNotEmpty(productParentCategoryId)) 
                {
                    if(parentProductCategoryIds.contains(catalogTopCategoryId)) 
                    {
                        queryCatFacet = "topMostProductCategoryId:" + productCategoryId + " AND ";
                    } 
                    else 
                    {
                        queryCatFacet = "topMostProductCategoryId:" + productParentCategoryId + " AND ";
                    }
                    facetCatGroups.add(0, SolrConstants.TYPE_PRODUCT_CATEGORY);
                    facetCatGroupDescriptions.put(SolrConstants.TYPE_PRODUCT_CATEGORY, facetProductCategoryLabel);
                }
                queryCatFacet += "rowType:product AND -(-introductionDate:[* TO "+nowDate+"] AND introductionDate:[* TO *]) AND -(-salesDiscontinuationDate:["+nowDate+" TO *] AND salesDiscontinuationDate:[* TO *])";

                SolrQuery solrCatQueryFacet = new SolrQuery(queryCatFacet);
                solrCatQueryFacet.setFacet(true);
                solrCatQueryFacet.setFacetSort(FacetParams.FACET_SORT_INDEX);
                solrCatQueryFacet.setFacetMinCount(1);
                if(UtilValidate.isNotEmpty(facetLimit) && facetLimit > 0)
                {
                    solrCatQueryFacet.setFacetLimit(facetLimit);
                }

                // Add the facet groups to the query
                if (UtilValidate.isNotEmpty(facetCatGroups)) 
                {
                    for (String groupName : facetCatGroups) 
                    {
                        solrCatQueryFacet.addFacetField(groupName);
                    }
                }
                addProductFacilityFilter(request, solrCatQueryFacet);            
                QueryResponse responseCatFacet = solr.query(solrCatQueryFacet);
                List<FacetField> resultsFacet = responseCatFacet.getFacetFields();
                RefinementsHelperSolr rh = new RefinementsHelperSolr(ccCat, delegator);
                facetCatList = (List) rh.processRefinements(resultsFacet);
                request.setAttribute("facetCatList", facetCatList);
            }
            catch (Exception e) 
            {
                Debug.logError(e, module);    
            }

            SolrQuery solrQueryFacet = new SolrQuery(queryFacet);

            solrQueryFacet.setFacet(true);
            solrQueryFacet.setFacetSort(FacetParams.FACET_SORT_INDEX);
            solrQueryFacet.setFacetMinCount(1);    

            String priceQueryFacet = queryFacet;
            
            if(facetMultiSelectTrue)
            {
            	//TO DO
                priceLow = null;
                priceHigh = null;
                priceQueryFacet = queryFacetWithoutGroup; 
            }

            // Customer Rating Facets
            // This should always give us the same set of "Customer Rating" facets
            int ratingStart = NumberUtils.toInt(OSAFE_PROPS.getString("customerRatingStart"), 4);
            int ratingEnd = NumberUtils.toInt(OSAFE_PROPS.getString( "customerRatingEnd"), 1);
            int ratingMax = NumberUtils.toInt(OSAFE_PROPS.getString("customerRatingMax"), 5);
            for (int i = ratingStart; i >= ratingEnd; i--) 
            {
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
            if (pageSize != solrPageSize) 
            {
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
            if (UtilValidate.isNotEmpty(SORT_OPTIONS_LIST)) 
            {
                defaultSort = Util.getProductStoreParm(request, "PLP_DEFAULT_SORT");

                //makes a list of Sort Options
                for (String sortOption: SORT_OPTIONS_LIST) 
                {
                    sortOption = sortOption.trim().toUpperCase();
                    if(OSAFE_PROPS.containsKey(sortOption))
                    {
                        String sortOptionTxt = OSAFE_PROPS.getString(sortOption);
                        if (UtilValidate.isNotEmpty(sortOptionTxt))
                        {
                            List<String> sortOptionAttrList = StringUtil.split(sortOptionTxt, "|");
                            if ((UtilValidate.isNotEmpty(sortOptionAttrList)) && (sortOptionAttrList.size() > 1))
                            {
                                Map<String,String> sortOptionMap = FastMap.newInstance();
                                sortOptionMap.put("SORT_OPTION",sortOption);
                                sortOptionMap.put("SOLR_VALUE", sortOptionAttrList.get(0));
                                sortOptionMap.put("SORT_OPTION_LABEL", sortOptionAttrList.get(1));
                                sortOptions.add(sortOptionMap);
                            }
                        }
                    }
                }

                //Sets default value for PLP sort. 
                if(UtilValidate.isEmpty(sortResults))
                {
                    for (Map sortOptionMap : sortOptions) 
                    {
                        if (UtilValidate.isNotEmpty(defaultSort)) 
                        {
                            String sortOption = (String)sortOptionMap.get("SORT_OPTION");
                            if(defaultSort.equalsIgnoreCase(sortOption))
                            {
                                sortResults = (String)sortOptionMap.get("SOLR_VALUE");
                                break;
                            }
                        }
                    }
                    if(UtilValidate.isEmpty(sortResults) && (sortOptions.size() > 0))
                    {
                        Map sortOptionMap = sortOptions.get(0);
                        sortResults = (String)sortOptionMap.get("SOLR_VALUE");
                    }
                }
            }
            if (UtilValidate.isNotEmpty(sortResults)) 
            {
                cc.setSortParameterName("sortResults");
                cc.setSortParameterValue(sortResults);
                String[] sortResultsParts = StringUtils.split(sortResults, "-");
                if (sortResultsParts.length > 1) 
                {
                	sortName = sortResultsParts[0];
                	
                	//Special Case: if sortName is salesDiscontinuationDate then we need to add this sort field to set null values to the end
                	if(sortName.equalsIgnoreCase("salesDiscontinuationDate"))
                	{
                        solrQueryFacet.addSortField("salesDiscontinuationDateNullFlag", ORDER.desc);
                	}

                    sortDir = sortResultsParts[1];
                    ORDER solrOrder = ORDER.asc;
                    if ("desc".equalsIgnoreCase(sortDir)) 
                    {
                        solrOrder = ORDER.desc;
                    }
                    solrQueryFacet.addSortField(sortName, solrOrder);
                }
            }
            else
            {
                solrQueryFacet.addSortField("customerRating", ORDER.desc);
            }

            addProductFacilityFilter(request, solrQueryFacet);

            // Add Category facets to the facetCatList
            if(UtilValidate.isNotEmpty(searchText)) 
            {
                if(facetGroups.contains(SolrConstants.TYPE_PRODUCT_CATEGORY))
                {
                    solrQueryFacet.addFacetField(SolrConstants.TYPE_PRODUCT_CATEGORY);
                }
                QueryResponse responseCatFacet = solr.query(solrQueryFacet);
                List<FacetField> resultsCatFacet = responseCatFacet.getFacetFields();
                RefinementsHelperSolr rhCatFacet = new RefinementsHelperSolr(cc, delegator);
                facetCatList = (List) rhCatFacet.processRefinements(resultsCatFacet);
                request.setAttribute("facetCatList", facetCatList);
                solrQueryFacet.removeFacetField(SolrConstants.TYPE_PRODUCT_CATEGORY);
                facetGroups.remove(SolrConstants.TYPE_PRODUCT_CATEGORY);
            }

            // Remove Category Facets while using site search, as we already added the category facets to the facetCatList.
            /*if(facetGroups.contains(SolrConstants.TYPE_PRODUCT_CATEGORY) && UtilValidate.isNotEmpty(searchText)){
                facetGroups.remove(SolrConstants.TYPE_PRODUCT_CATEGORY);
            }*/

            // Add the facet groups to the query
            if (UtilValidate.isNotEmpty(facetGroups)) 
            {
                for (String groupName : facetGroups) 
                {
                    solrQueryFacet.addFacetField(groupName);
                }
            }

            List<SolrIndexDocument> resultsCompleteMultiSelect = FastList.newInstance();
            if(facetMultiSelectTrue)
            {
                solrQueryFacet.setParam("q", queryFacetWithoutGroup);
                QueryResponse responseFacetGroup = solr.query(solrQueryFacet);
                List<FacetField> resultsFacetGroup = responseFacetGroup.getFacetFields();
                RefinementsHelperSolr rhFacetGroup = new RefinementsHelperSolr(cc, delegator);
                
                SolrDocumentList sdlFacetGroup = responseFacetGroup.getResults();
                
                // Get the Complete Document List
                solrQueryFacet.setRows((int)sdlFacetGroup.getNumFound());
                solrQueryFacet.setStart(0);

                QueryResponse responseFacetCompleteFacetGroup = solr.query(solrQueryFacet);

                resultsCompleteMultiSelect = responseFacetCompleteFacetGroup.getBeans(SolrIndexDocument.class);
                
                Map multiResultsFacetQueriesPrice = (Map) rhFacetGroup.getResultFacetQueryPrice(request, resultsCompleteMultiSelect, null);
                
                List multiFacetGroup = (List) rhFacetGroup.processRefinements(resultsFacetGroup, responseFacetCompleteFacetGroup.getResults());
                List multiFacetPriceRange = (List) rhFacetGroup.processPriceRangeRefinements(multiResultsFacetQueriesPrice, resultsCompleteMultiSelect);
                List multiFacetCustomerRating = (List) rhFacetGroup.processCustomerRatingRefinements(responseFacetGroup.getFacetQuery(), resultsCompleteMultiSelect);
                request.setAttribute("multiFacetGroup", multiFacetGroup);
                request.setAttribute("multiFacetCustomerRating", multiFacetCustomerRating);
                request.setAttribute("multiFacetPriceRange", multiFacetPriceRange);
                request.setAttribute("multiFacetListAll", buildAllFacetList(delegator, productCategoryId, facetCatList, multiFacetPriceRange, multiFacetCustomerRating, multiFacetGroup));
            }
            solrQueryFacet.setParam("q", queryFacet);

            QueryResponse responseFacet = solr.query(solrQueryFacet);

            List<FacetField> resultsFacet = responseFacet.getFacetFields();
            RefinementsHelperSolr rh = new RefinementsHelperSolr(cc, delegator);

            List<SolrIndexDocument> results = responseFacet.getBeans(SolrIndexDocument.class);

            SolrDocumentList sdl = responseFacet.getResults();

            if (UtilValidate.isNotEmpty(searchText) && sdl.getNumFound() < 1) 
            {
                return "error";
            }

            // Get the Complete Document List
            
            solrQueryFacet.setRows((int)sdl.getNumFound());
            solrQueryFacet.setStart(0);

            QueryResponse responseFacetComplete = solr.query(solrQueryFacet);

            List<SolrIndexDocument> resultsComplete = responseFacetComplete.getBeans(SolrIndexDocument.class);

            // resultsFacetQueries holds both price a customer rating facets
            Map<String, Integer> resultsFacetQueries = responseFacet.getFacetQuery();

            List facetList = null;
            Map resultsFacetQueriesPrice = (Map) rh.getResultFacetQueryPrice(request, resultsComplete, resultsCompleteMultiSelect);
            List facetListPriceRange = (List) rh.processPriceRangeRefinements(resultsFacetQueriesPrice, resultsComplete);
            
            
            List facetListCustomerRating = (List) rh.processCustomerRatingRefinements(resultsFacetQueries, resultsComplete);
            
            List newresultCompleteUnique = removeDuplicateEntry(resultsComplete);
            List<SolrIndexDocument> newresultComplete = rh.filterResultsOnPriceRange(request, newresultCompleteUnique);
            
            List<SolrDocument> solrDocumentFiltered = getSolrDocumentFromIndexDoc(responseFacetComplete.getResults(), newresultComplete);
            
            facetList = (List) rh.processRefinements(resultsFacet, solrDocumentFiltered);

            List newresultUnique = removeDuplicateEntry(results);
            
            List newresult = rh.filterResultsOnPriceRange(request, newresultUnique);
            
            int startIndex = Integer.valueOf(NumberUtils.toInt(start, 0));

            if (newresultComplete.size() > (startIndex+pageSize)) 
            {
                newresult = newresultComplete.subList(startIndex, (startIndex+pageSize));
            } 
            else 
            {
                newresult = newresultComplete.subList(startIndex, newresultComplete.size());
            }

            
            //used in resultsNavigation.ftl
            request.setAttribute("sortOptions", sortOptions);
            request.setAttribute("numFound", newresultComplete.size());
            request.setAttribute("start", Integer.valueOf(NumberUtils.toInt(start, 0)));
            request.setAttribute("size", pageSize);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("solrPageSize", solrPageSize);
            request.setAttribute("facetListAll", buildAllFacetList(delegator, productCategoryId, facetCatList, facetListPriceRange, facetListCustomerRating, facetList));
            request.setAttribute("facetList", facetList);
            request.setAttribute("facetListPriceRange", facetListPriceRange);
            request.setAttribute("facetListCustomerRating", facetListCustomerRating);
            request.setAttribute("documentList", newresult);
            request.setAttribute("completeDocumentList", newresultComplete);
            request.getSession().setAttribute("productDocumentList", newresultComplete);
            request.setAttribute("sortResults",sortResults);
            request.setAttribute("filterGroup", filterGroup);
            request.setAttribute("filterGroupParamMap", filterGroupParamMap);
            if(facetMultiSelectTrue)
            {
                if (UtilValidate.isNotEmpty(filterPriceQuery) || UtilValidate.isNotEmpty(queryFacetGroup) || UtilValidate.isNotEmpty(filterCustomerRatingQuery)) 
                {
                    if (UtilValidate.isNotEmpty(filterGroupParamMap) && filterGroupParamMap.size() == 1) 
                    {
                        request.setAttribute("multiFacetInitialType", multiFacetInitialType);
                    }
                    request.setAttribute("multiFacetGroupRefined", facetList);
                    request.setAttribute("multiFacetPriceRangeRefined", facetListPriceRange);
                    request.setAttribute("multiFacetCustomerRatingRefined", facetListCustomerRating);
                }
            }

        } 
        catch (MalformedURLException e) 
        {
            Debug.logError(e.getMessage(), module);
        } 
        catch (SolrServerException e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        catch (Exception e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        return "success";
    }

    public static List<SolrIndexDocument> removeDuplicateEntry (List<SolrIndexDocument> results) 
    {
        Iterator itr = results.iterator();
        List subresult = FastList.newInstance();
        List newresult = FastList.newInstance();
        try 
        {
            while(itr.hasNext()) 
            {
                SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itr.next();
                if (subresult.contains(solrIndexDocument.getProductId())) 
                {
                    //results.remove(itr.next());
                }
                else 
                {
                    newresult.add(solrIndexDocument);
                    subresult.add(solrIndexDocument.getProductId());
                }
            }
        }
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        return newresult;
    }
    
    public static List<SolrDocument> getSolrDocumentFromIndexDoc(List<SolrDocument> results, List<SolrIndexDocument> resultsIndex) 
    {
        Iterator itr = results.iterator();
        List newresult = FastList.newInstance();
        try 
        {
            while(itr.hasNext()) 
            {
                SolrDocument solrDocument = (SolrDocument)itr.next();
                Iterator itrIndex = resultsIndex.iterator();
                while(itrIndex.hasNext()) 
                {
                	SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itrIndex.next();
                	if ((solrIndexDocument.getProductId()).toString().equals(solrDocument.getFieldValue("productId"))) 
                    {
                		newresult.add(solrDocument);
                        //results.remove(itr.next());
                    }
                }
            }
        }
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        return newresult;
    }
    
    public static String solrSpellCheck(HttpServletRequest request, HttpServletResponse response) 
    {
        String solrServer = OSAFE_PROPS.getString("solrSearchServer");

        String searchText = Util.stripHTML(request.getParameter("searchText"));

        if (UtilValidate.isEmpty(searchText))
        {
            searchText = Util.stripHTML((String)request.getAttribute("searchText"));
        }
        if(UtilValidate.isNotEmpty(searchText))
        {
            try 
            {
            	searchText = URLEncoder.encode(searchText, SolrConstants.DEFAULT_ENCODING);
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
                    request.setAttribute("searchType", "SpellCheckSearch");
                    request.setAttribute("searchTextSpellCheck", responseSpellCheck.getSpellCheckResponse().getCollatedResult());
                }
            } 
            catch (Exception e) 
            {
                Debug.logError(e.getMessage(), module);
            }
        }
        return "success";
    }

    public static String addWildCardSearch(HttpServletRequest request, HttpServletResponse response) 
    {
        request.setAttribute("searchType", "WildCardSearch");
        return "success";
    }

    public static String addAllWordSearch(HttpServletRequest request, HttpServletResponse response) 
    {
        request.setAttribute("searchType", "AllWordSearch");
        return "success";
    }

    public static String autoSuggestionList(HttpServletRequest request, HttpServletResponse response) 
    {
        String solrServer = OSAFE_PROPS.getString("solrSearchServer");

        List<String> autoSuggestionList = FastList.newInstance();
        List<Long> autoSuggestionFreqList = FastList.newInstance();

        String searchText = Util.stripHTML(request.getParameter("searchText"));

        if (UtilValidate.isEmpty(searchText))
        {
            searchText = Util.stripHTML((String)request.getAttribute("searchText"));
        }
        if(UtilValidate.isNotEmpty(searchText))
        {
            try 
            {
                CommonsHttpSolrServer solr = new CommonsHttpSolrServer(solrServer);
                ModifiableSolrParams params = new ModifiableSolrParams();
                params.set("qt", "/terms");
                params.set("terms.fl", "autocomplete_text");
                params.set("omitHeader", "true");
                params.set("terms.sort", "index");
                params.set("terms.regex", searchText+".*");
                params.set("terms.regex.flag", "case_insensitive");
                QueryResponse responseSpell = solr.query(params);

                List<Term> termsListParent = responseSpell.getTermsResponse().getTerms("autocomplete_text");

                for(Term termsListChild : termsListParent)
                {
                    autoSuggestionList.add(termsListChild.getTerm());
                    autoSuggestionFreqList.add(termsListChild.getFrequency());
                }

                request.setAttribute("autoSuggestionList", autoSuggestionList);
                if(autoSuggestionList.size() > 0)
                {
                    request.setAttribute("response", "success");    
                }
                else
                {
                    request.setAttribute("response", "error");
                }

            } 
            catch (Exception e) 
            {
                Debug.logError(e.getMessage(), module);
            }
        }
        return "success";
    }

    private static void addProductFacilityFilter(HttpServletRequest request, SolrQuery query)
    {
        if (checkUserAddress(request))
        {
            query.addFilterQuery(getProductFacilityFilterQuery(request));
        }
    }

    private static String[] getProductFacilityFilterQuery(HttpServletRequest request)
    {
        List<String> availableFacilities = getAvailableFacilities(request);
        String[] filterQuery = new String[1];
        String filterQueryStr = "";
        int count = 1;
        if (UtilValidate.isNotEmpty(availableFacilities)) 
        {
            for (String availableFacility: availableFacilities)
            {
                filterQueryStr = filterQueryStr + SolrConstants.EXTRACT_PRODUT_FACILTY + SolrConstants.SEARCH_TERM_EQUALS + availableFacility;
                if(count++ < availableFacilities.size())
                    filterQueryStr = filterQueryStr + SolrConstants.SEARCH_TERM_OR_OPERATOR;
            }
        }
        filterQuery[0] =  filterQueryStr;
        return filterQuery;
    }

    public static List<String> getAvailableFacilities(HttpServletRequest request)
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);
        List<String> availableFacilities = FastList.newInstance();

        //get available facilities from session if search address match with session stored address
        if (matchUserFromSession(request))
        {
            if (UtilValidate.isNotEmpty(request.getSession().getAttribute(SolrConstants.SEARCH_AVAILABLE_FACILITIES_SESSION_KEY)))
            {
                availableFacilities = (List<String>) request.getSession().getAttribute(SolrConstants.SEARCH_AVAILABLE_FACILITIES_SESSION_KEY);
                return availableFacilities;
            }
        }

        List<GenericValue> facilities = null;
        try 
        {
            //Get the all facility which are associated with productStoreId
            facilities = delegator.findByAndCache("Facility", UtilMisc.toMap("productStoreId", productStoreId));
        } 
        catch (GenericEntityException e) 
        {
            Debug.logError(e, "Unable to get facilities", module);
            availableFacilities.add("NOFACILITY");
            return availableFacilities;
        }

        // clone the list for concurrent modification
        List<GenericValue> returnFacilities = UtilMisc.makeListWritable(facilities);

        //Get set of Geo Id with reference of user
        Set userGeoRegion = getUserGeoRegion(request);
        if (UtilValidate.isEmpty(userGeoRegion)) 
        {
            availableFacilities.add("NOFACILITY");
            return availableFacilities;
        }

        if (UtilValidate.isNotEmpty(facilities))
        {
            for (GenericValue facility: facilities)
            {
                try
                {
                    //Get facility address
                    GenericValue facilityAddress = getShippingOriginContactMech(delegator, facility.getString("ownerPartyId"));
                    if (UtilValidate.isEmpty(facilityAddress))
                    {
                        // No address found for facility
                        returnFacilities.remove(facility);
                        //Debug.logInfo("Removed facility due to blank facility address", module);
                        continue;
                    }
                    if (!userGeoRegion.contains(facilityAddress.getString("stateProvinceGeoId")) &&
                            !userGeoRegion.contains(facilityAddress.getString("postalCodeGeoId")))
                    {
                        // not in geos
                        returnFacilities.remove(facility);
                        //Debug.logInfo("Removed facility due to being outside the user GEO", module);
                        continue;
                    }
                } 
                catch (Exception e)
                {
                    Debug.logError(e.getMessage(), module);
                }
            }

        }

        for (GenericValue facility: returnFacilities)
        {
            availableFacilities.add(facility.getString("facilityId"));
        }

        //if no facility found add NOFACILITY key string
        if(UtilValidate.isEmpty(availableFacilities))
        {
            availableFacilities.add("NOFACILITY");
        }

        request.getSession().setAttribute(SolrConstants.SEARCH_AVAILABLE_FACILITIES_SESSION_KEY, availableFacilities);
        return availableFacilities;
    }

    public static Set getUserGeoRegion(HttpServletRequest request)
    {
        //this method will be use for get user location and make region
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Set geoIdSet = FastSet.newInstance();
        try
        {
            GenericValue userAddress = getUserAddress(request);
            request.getSession().setAttribute(SolrConstants.SEARCH_USER_ADDRESS_SESSION_KEY, userAddress);

            if (UtilValidate.isNotEmpty(userAddress))
            {
                if (UtilValidate.isNotEmpty(userAddress.getString("countryGeoId")))
                {
                    geoIdSet.add(userAddress.getString("countryGeoId"));
                }
                if (UtilValidate.isNotEmpty(userAddress.getString("stateProvinceGeoId")))
                {
                    geoIdSet.add(userAddress.getString("stateProvinceGeoId"));
                }
                if (UtilValidate.isNotEmpty(userAddress.getString("countyGeoId")))
                {
                    geoIdSet.add(userAddress.getString("countyGeoId"));
                }
                //Get postal Geo id
                String postalCodeGeoId = getPostalAddressPostalCodeGeoId(userAddress, delegator);
                if (UtilValidate.isNotEmpty(postalCodeGeoId))
                {
                    geoIdSet.add(postalCodeGeoId);
                }
            } 
            else 
            {
                Debug.logWarning("userAddress is null, adding nothing to geoIdSet", module);
            }
            // get the most granular, or all available, geoIds and then find parents by GeoAssoc with geoAssocTypeId="REGIONS" and geoIdTo=<granular geoId> and find the GeoAssoc.geoId
            geoIdSet = expandGeoRegionDeep(geoIdSet, delegator);
        }
        catch (Exception e)
        {
            Debug.logError(e.getMessage(), module);
        }
        return geoIdSet;
    }

    public static boolean matchUserFromSession(HttpServletRequest request)
    {
        boolean isMatch = true;
        if (UtilValidate.isEmpty(request.getSession().getAttribute(SolrConstants.SEARCH_USER_ADDRESS_SESSION_KEY)))
        {
            return false;
        }
        GenericValue userAddressFromSession = (GenericValue) request.getSession().getAttribute(SolrConstants.SEARCH_USER_ADDRESS_SESSION_KEY);
        GenericValue userAddress = getUserAddress(request);

        //Match countryGeoId
        if (UtilValidate.isEmpty(userAddressFromSession.getString("countryGeoId")) && UtilValidate.isNotEmpty(userAddress.getString("countryGeoId")))
        {
            isMatch = false;
        }
        else if (UtilValidate.isNotEmpty(userAddressFromSession.getString("countryGeoId")) && UtilValidate.isNotEmpty(userAddress.getString("countryGeoId")))
        {
            if (!userAddressFromSession.getString("countryGeoId").equals(userAddress.getString("countryGeoId")))
            {
                isMatch = false;
            }
        }

        //Match stateProvinceGeoId
        if (UtilValidate.isEmpty(userAddressFromSession.getString("stateProvinceGeoId")) && UtilValidate.isNotEmpty(userAddress.getString("stateProvinceGeoId")))
        {
            isMatch = false;
        }
        else if (UtilValidate.isNotEmpty(userAddressFromSession.getString("stateProvinceGeoId")) && UtilValidate.isNotEmpty(userAddress.getString("stateProvinceGeoId")))
        {
            if (!userAddressFromSession.getString("stateProvinceGeoId").equals(userAddress.getString("stateProvinceGeoId")))
            {
                isMatch = false;
            }
        }

        //Match postalCode
        if (UtilValidate.isEmpty(userAddressFromSession.getString("postalCode")) && UtilValidate.isNotEmpty(userAddress.getString("postalCode")))
        {
            isMatch = false;
        }
        else if (UtilValidate.isNotEmpty(userAddressFromSession.getString("postalCode")) && UtilValidate.isNotEmpty(userAddress.getString("postalCode")))
        {
            if (!userAddressFromSession.getString("postalCode").equals(userAddress.getString("postalCode")))
            {
                isMatch = false;
            }
        }
        return isMatch;
    }

    public static GenericValue getUserAddress(HttpServletRequest request)
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userAddress = delegator.makeValue("PostalAddress");
        //set the postal code in generic value
        String postalCode = request.getParameter("postalCode");
        userAddress.put("postalCode", postalCode);

        String defaultCountry = Util.getProductStoreParm(request, "COUNTRY_DEFAULT");
        userAddress.put("countryGeoId", defaultCountry);
        //TODO we can set user's other information

        return userAddress;
    }

    public static boolean checkUserAddress(HttpServletRequest request)
    {
        boolean doFilterSearch = false;
        GenericValue userAddress = getUserAddress(request);
        if (UtilValidate.isNotEmpty(userAddress.getString("postalCode")))
        {
            doFilterSearch = true;
        }
        else
        {
            if (UtilValidate.isNotEmpty(request.getSession().getAttribute(SolrConstants.SEARCH_USER_ADDRESS_SESSION_KEY)) && (userAddress.getString("postalCode") == null))
            {
                doFilterSearch = true;
            }
            else
            {
                request.getSession().removeAttribute(SolrConstants.SEARCH_USER_ADDRESS_SESSION_KEY);
                request.getSession().removeAttribute(SolrConstants.SEARCH_AVAILABLE_FACILITIES_SESSION_KEY);
            }
        }
        return doFilterSearch;
    }

    /**
     * Attempts to get the facility's shipping origin address and failing that, the general location.
     */
    public static GenericValue getShippingOriginContactMech(Delegator delegator, String partyId) throws GeneralException 
    {
        List<EntityCondition> conditions = UtilMisc.toList(
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"),
                EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, UtilMisc.toList("SHIP_ORIG_LOCATION", "GENERAL_LOCATION")),
                EntityUtil.getFilterByDateExpr("contactFromDate", "contactThruDate"),
                EntityUtil.getFilterByDateExpr("purposeFromDate", "purposeThruDate")
        );
        EntityConditionList<EntityCondition> ecl = EntityCondition.makeCondition(conditions, EntityOperator.AND);

        List<GenericValue> addresses = delegator.findList("PartyContactWithPurpose", ecl, null, UtilMisc.toList("contactMechPurposeTypeId DESC"), null, true);

        GenericValue generalAddress = null;
        GenericValue originAddress = null;
        for (GenericValue address : addresses) 
        {
            if ("GENERAL_LOCATION".equals(address.get("contactMechPurposeTypeId")))
            {
                generalAddress = delegator.findByPrimaryKeyCache("PostalAddress", UtilMisc.toMap("contactMechId", address.getString("contactMechId")));
            }
            else if ("SHIP_ORIG_LOCATION".equals(address.get("contactMechPurposeTypeId")))
            {
                originAddress = delegator.findByPrimaryKeyCache("PostalAddress", UtilMisc.toMap("contactMechId", address.getString("contactMechId")));

            }
        }
        return originAddress != null ? originAddress : generalAddress;
    }

    public static String getPostalAddressPostalCodeGeoId(GenericValue postalAddress, Delegator delegator) throws GenericEntityException 
    {
        // if postalCodeGeoId not empty use that
        if (UtilValidate.isNotEmpty(postalAddress.getString("postalCodeGeoId")))
        {
            return postalAddress.getString("postalCodeGeoId");
        }

        // no postalCodeGeoId, see if there is a Geo record matching the countryGeoId and postalCode fields
        if (UtilValidate.isNotEmpty(postalAddress.getString("countryGeoId")) && UtilValidate.isNotEmpty(postalAddress.getString("postalCode")))
        {
            // first try the shortcut with the geoId convention for "{countryGeoId}-{postalCode}"
            GenericValue geo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", postalAddress.getString("countryGeoId") + "-" + postalAddress.getString("postalCode")));
            if (geo != null) 
            {
                // save the value to the postalAddress for quicker future reference
                postalAddress.set("postalCodeGeoId", geo.getString("geoId"));
                return geo.getString("geoId");
            }

            // no shortcut, try the longcut to see if there is something with a geoCode associated to the countryGeoId
            List<GenericValue> geoAssocAndGeoToList = delegator.findByAndCache("GeoAssocAndGeoTo",
                    UtilMisc.toMap("geoIdFrom", postalAddress.getString("countryGeoId"), "geoCode", postalAddress.getString("postalCode"), "geoAssocTypeId", "REGIONS"));
            GenericValue geoAssocAndGeoTo = EntityUtil.getFirst(geoAssocAndGeoToList);
            if (geoAssocAndGeoTo != null) 
            {
                // save the value to the postalAddress for quicker future reference
                postalAddress.set("postalCodeGeoId", geoAssocAndGeoTo.getString("geoId"));
                return geoAssocAndGeoTo.getString("geoId");
            }
        }

        // nothing found, return null
        return null;
    }

    public static Set<String> expandGeoRegionDeep(Set<String> geoIdSet, Delegator delegator) throws GenericEntityException 
    {
        if (UtilValidate.isEmpty(geoIdSet)) 
        {
            return geoIdSet;
        }
        Set<String> geoIdSetTemp = FastSet.newInstance();
        for (String curGeoId: geoIdSet) 
        {
            List<GenericValue> geoAssocList = delegator.findByAndCache("GeoAssoc", UtilMisc.toMap("geoIdTo", curGeoId, "geoAssocTypeId", "REGIONS"));
            for (GenericValue geoAssoc: geoAssocList) 
            {
                geoIdSetTemp.add(geoAssoc.getString("geoId"));
            }
        }
        geoIdSetTemp = expandGeoRegionDeep(geoIdSetTemp, delegator);
        Set<String> geoIdSetNew = FastSet.newInstance();
        geoIdSetNew.addAll(geoIdSet);
        geoIdSetNew.addAll(geoIdSetTemp);
        return geoIdSetNew;
    }
    
    
    public static String searchShoppingList(HttpServletRequest request, HttpServletResponse response) 
    {
        try 
        {
        	
        	String nowDate = _sdf.format(UtilDateTime.nowDate());
            Delegator delegator = (Delegator) request.getAttribute("delegator");

            
            String solrServer = OSAFE_PROPS.getString("solrSearchServer");
            CommonsHttpSolrServer solr = new CommonsHttpSolrServer(solrServer);
            
            
            GenericValue productStore = ProductStoreWorker.getProductStore(request);
            String productStoreId = productStore.getString("productStoreId");

            solr.setRequestWriter(new BinaryRequestWriter());

            Map<String, String> mProductStoreParms = FastMap.newInstance();
            
            Map<String, Object> params = UtilHttp.getCombinedMap(request);
            
            Map<String, String> searchTermsMap = new TreeMap<String, String>();
            
            List resultShoppingListSearch = FastList.newInstance();
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
            if (solrPageSize == 0) 
            {
                solrPageSize = 20;
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
            if (UtilValidate.isNotEmpty(SORT_OPTIONS_LIST)) 
            {
                defaultSort = Util.getProductStoreParm(request, "PLP_DEFAULT_SORT");

                //makes a list of Sort Options
                for (String sortOption: SORT_OPTIONS_LIST) 
                {
                    sortOption = sortOption.trim().toUpperCase();
                    if(OSAFE_PROPS.containsKey(sortOption))
                    {
                        String sortOptionTxt = OSAFE_PROPS.getString(sortOption);
                        if (UtilValidate.isNotEmpty(sortOptionTxt))
                        {
                            List<String> sortOptionAttrList = StringUtil.split(sortOptionTxt, "|");
                            if ((UtilValidate.isNotEmpty(sortOptionAttrList)) && (sortOptionAttrList.size() > 1))
                            {
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
                if(UtilValidate.isEmpty(sortResults))
                {
                    for (Map sortOptionMap : sortOptions) 
                    {
                        if (UtilValidate.isNotEmpty(defaultSort)) 
                        {
                            String sortOption = (String)sortOptionMap.get("SORT_OPTION");
                            if(defaultSort.equalsIgnoreCase(sortOption))
                            {
                                sortResults = (String)sortOptionMap.get("SOLR_VALUE");
                                break;
                            }
                        }
                    }
                    if(UtilValidate.isEmpty(sortResults) && (sortOptions.size() > 0))
                    {
                        Map sortOptionMap = sortOptions.get(0);
                        sortResults = (String)sortOptionMap.get("SOLR_VALUE");
                    }
                }
            }
            List<String> processedSearchItems = FastList.newInstance();
            for(Entry<String, Object> entry : params.entrySet()) 
            {
                String parameterName = entry.getKey();
            	if (parameterName.toUpperCase().startsWith("SEARCHITEM"))
            	{
            		// Get text to use for a site search query
                    String searchItem = request.getParameter(parameterName);
                    if (UtilValidate.isEmpty(searchItem))
                    {
                    	searchItem = Util.stripHTML((String)request.getAttribute(parameterName));
                    }
                    String searchItemCompleteResultText = "";
                    if (UtilValidate.isNotEmpty(searchItem)&& !processedSearchItems.contains(searchItem.toUpperCase()))
                    {
                    	
						searchItem = searchItem.toLowerCase();
                        
						searchItem = replaceSpecialChar(searchItem);
                        
                    	searchItemCompleteResultText = searchItem;
                    	
                        //PERFORM AND SEARCH
                        
                        String searchItemUsingAnd = "\""+searchItem+"\"";
                        
                        searchItemCompleteResultText = searchItemUsingAnd;
                        
                        QueryResponse responseFacet = getQueryResponse(searchItemUsingAnd, solr, nowDate);
                        
                        List<FacetField> resultsFacet = responseFacet.getFacetFields();

                        List<SolrIndexDocument> results = responseFacet.getBeans(SolrIndexDocument.class);
                        
                        SolrDocumentList sdl = responseFacet.getResults();
                        
                        //PERFORM SPELL CHECK
                        boolean spellCheck = false;
                        String searchItemSpellCheck = searchItem;
                        if (sdl.getNumFound() < 1) 
                        {
                        	ModifiableSolrParams solrParams = new ModifiableSolrParams();
                        	solrParams.set("qt", "/spell");
                        	solrParams.set("q", searchItem);
                            QueryResponse responseSpellCheck = solr.query(solrParams);
                            if(responseSpellCheck.getSpellCheckResponse()!= null)
                            {
                            	if(!responseSpellCheck.getSpellCheckResponse().isCorrectlySpelled()) 
                                {
                                	searchItemSpellCheck = responseSpellCheck.getSpellCheckResponse().getCollatedResult();
                                }
                            }
                            
                            if(UtilValidate.isNotEmpty(searchItemSpellCheck) && !processedSearchItems.contains(searchItemSpellCheck.toUpperCase()))
                            {
                            	
                            	spellCheck = true;
	                            responseFacet = getQueryResponse(searchItemSpellCheck, solr, nowDate);
	                        	
	                        	resultsFacet = responseFacet.getFacetFields();
	
	                            results = responseFacet.getBeans(SolrIndexDocument.class);
	                            
	                            sdl = responseFacet.getResults();
                            }
                             
                            searchItemCompleteResultText = searchItemSpellCheck;
                        }
                        
                        
                        String queryFacet = null;
                        queryFacet = "searchText: (" + searchItemCompleteResultText +")";
                        
                        
                        queryFacet += " AND rowType:product AND -(-introductionDate:[* TO "+nowDate+"] AND introductionDate:[* TO *]) AND -(-salesDiscontinuationDate:["+nowDate+" TO *] AND salesDiscontinuationDate:[* TO *])";
                        
                        SolrQuery solrQueryFacet = new SolrQuery(queryFacet);
                        
                        // Get the Complete Document List
                        solrQueryFacet.setRows((int)sdl.getNumFound());
                        
                        solrQueryFacet.setStart(0);

                        if (UtilValidate.isNotEmpty(sortResults)) 
                        {
                            String[] sortResultsParts = StringUtils.split(sortResults, "-");
                            if (sortResultsParts.length > 1) 
                            {
                                sortName = sortResultsParts[0];
                                sortDir = sortResultsParts[1];
                                ORDER solrOrder = ORDER.asc;
                                if ("desc".equalsIgnoreCase(sortDir)) 
                                {
                                    solrOrder = ORDER.desc;
                                }
                                solrQueryFacet.addSortField(sortName, solrOrder);
                            }
                        }
                        else
                        {
                            solrQueryFacet.addSortField("customerRating", ORDER.desc);
                        }
                        
                        QueryResponse responseFacetComplete = solr.query(solrQueryFacet);

                        List<SolrIndexDocument> resultsComplete = responseFacetComplete.getBeans(SolrIndexDocument.class);
                        
                        
                        List newresultComplete = removeDuplicateEntry(resultsComplete);

                        resultShoppingListSearch.addAll(newresultComplete);
                        if(!processedSearchItems.contains(searchItemSpellCheck.toUpperCase()))
                        {
                        	if(spellCheck)
                            {
                            	searchTermsMap.put(parameterName, URLDecoder.decode(searchItemSpellCheck, SolrConstants.DEFAULT_ENCODING));
                            }
                            else
                            {
                            	searchTermsMap.put(parameterName, URLDecoder.decode(searchItem, SolrConstants.DEFAULT_ENCODING));
                            }
                        	processedSearchItems.add(searchItemSpellCheck.toUpperCase());
                        }
                        
                    }
            	}
            }
            
            if (UtilValidate.isEmpty(resultShoppingListSearch)) 
            {
            	StringBuilder  shoppingListSearchText = new StringBuilder();
            	if(UtilValidate.isNotEmpty(searchTermsMap))
            	{
            		int currentItemNo = 0;
            		for (Map.Entry<String, String> entry : searchTermsMap.entrySet()) 
                    {
            			currentItemNo = currentItemNo + 1;
            			shoppingListSearchText.append(entry.getValue());
            			if(currentItemNo != searchTermsMap.size())
            			{
            				shoppingListSearchText.append(", ");
            			}
                    }
            	}
            	request.setAttribute("shoppingListSearchText", shoppingListSearchText.toString());
                return "error";
            }
            
            List resultShoppingListSearchPage = FastList.newInstance();
            
            int startIndex = Integer.valueOf(NumberUtils.toInt(start, 0));
            
            if (resultShoppingListSearch.size() > (startIndex+pageSize)) 
            {
            	resultShoppingListSearchPage = resultShoppingListSearch.subList(startIndex, (startIndex+pageSize));
            } 
            else 
            {
            	resultShoppingListSearchPage = resultShoppingListSearch.subList(startIndex, resultShoppingListSearch.size());
            }
            request.setAttribute("numFound", resultShoppingListSearch.size());
            request.setAttribute("documentList", resultShoppingListSearchPage);
            request.setAttribute("searchTermsMap", searchTermsMap);
            request.setAttribute("start", Integer.valueOf(NumberUtils.toInt(start, 0)));
            request.setAttribute("size", pageSize);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("solrPageSize", solrPageSize);
            request.setAttribute("sortOptions", sortOptions);
            request.setAttribute("sortResults",sortResults);
            
        }
        catch (Exception e) 
        {
			// TODO: handle exception
		}
        return "success";
    }
    
    
    public static QueryResponse getQueryResponse(String searchText, CommonsHttpSolrServer solr, String nowDate)
    {
    	String queryFacet = null;
        
        queryFacet = "searchText: (" + searchText +")";
        
        queryFacet += " AND rowType:product AND -(-introductionDate:[* "+nowDate+"] AND introductionDate:[* TO *]) AND -(-salesDiscontinuationDate:["+nowDate+" TO *] AND salesDiscontinuationDate:[* TO *])";
        
        SolrQuery solrQueryFacet = new SolrQuery(queryFacet);
        
        QueryResponse responseFacet = null;
		try 
		{
			responseFacet = solr.query(solrQueryFacet);
		} 
		catch (SolrServerException e) 
		{
			e.printStackTrace();
		}
        
        return responseFacet;
    }

    /**
     * Build the final facet list base on ProductFeatureCatGrpAppl entity
     * @param delegator String xml file path
     * @param productCategoryId String product category id
     * @param facetCatList List of category facet
     * @param facetListPriceRange List of price facet
     * @param facetListCustomerRating List of customer rating facet
     * @param facetList List of all other facet
     * @return a new collection of facet.
     */
    public static Collection<GenericRefinement> buildAllFacetList(Delegator delegator, String productCategoryId, List<GenericRefinement> facetCatList, List<GenericRefinement> facetListPriceRange, List<GenericRefinement> facetListCustomerRating, List<GenericRefinement> facetList)
    {
        Collection<GenericRefinement> returnFacetList = new ArrayList<GenericRefinement>();
        if (UtilValidate.isNotEmpty(productCategoryId))
        {
            List<GenericValue> productFeatureCatGrpApplList = null;
            try 
            {
                List orderBy = UtilMisc.toList("sequenceNum");
                productFeatureCatGrpApplList = delegator.findByAndCache("ProductFeatureCatGrpAppl", UtilMisc.toMap("productCategoryId", productCategoryId), orderBy);
                List<GenericValue> filteredProductFeatureCatGrpApplList = EntityUtil.filterByCondition(productFeatureCatGrpApplList, EntityCondition.makeCondition("productFeatureGroupId", EntityOperator.IN, UtilMisc.toList("FACET_GROUP_CATEGORY", "FACET_GROUP_PRICE", "FACET_GROUP_RATINGS")));
                if(UtilValidate.isEmpty(filteredProductFeatureCatGrpApplList))
                {
                    // Now Rearrange the seq for other
                    if(UtilValidate.isNotEmpty(productFeatureCatGrpApplList))
                    {
                        Long count = 4L;
                        for (GenericValue productFeatureCatGrpAppl : productFeatureCatGrpApplList)
                        {
                            productFeatureCatGrpAppl.put("sequenceNum", 10L * count);
                            count++;
                        }
                    }

                    GenericValue categoryFacetGv = delegator.makeValue("ProductFeatureCatGrpAppl");
                    categoryFacetGv.put("productCategoryId", productCategoryId);
                    categoryFacetGv.put("productFeatureGroupId", "FACET_GROUP_CATEGORY");
                    categoryFacetGv.put("sequenceNum", 10L);
                    productFeatureCatGrpApplList.add(categoryFacetGv);

                    GenericValue priceFacetGv = delegator.makeValue("ProductFeatureCatGrpAppl");
                    priceFacetGv.put("productCategoryId", productCategoryId);
                    priceFacetGv.put("productFeatureGroupId", "FACET_GROUP_PRICE");
                    priceFacetGv.put("sequenceNum", 20L);
                    productFeatureCatGrpApplList.add(priceFacetGv);

                    GenericValue ratingFacetGv = delegator.makeValue("ProductFeatureCatGrpAppl");
                    ratingFacetGv.put("productCategoryId", productCategoryId);
                    ratingFacetGv.put("productFeatureGroupId", "FACET_GROUP_RATINGS");
                    ratingFacetGv.put("sequenceNum", 30L);
                    productFeatureCatGrpApplList.add(ratingFacetGv);

                    productFeatureCatGrpApplList = EntityUtil.orderBy(productFeatureCatGrpApplList, orderBy);
                }
                productFeatureCatGrpApplList = EntityUtil.filterByDate(productFeatureCatGrpApplList);

            	if(UtilValidate.isNotEmpty(productFeatureCatGrpApplList))
                {
                    for (GenericValue productFeatureCatGrpAppl : productFeatureCatGrpApplList)
                    {
                        if ("FACET_GROUP_CATEGORY".equals(productFeatureCatGrpAppl.get("productFeatureGroupId")))
                        {
                            returnFacetList.addAll(facetCatList);
                        }
                        else if("FACET_GROUP_PRICE".equals(productFeatureCatGrpAppl.get("productFeatureGroupId")))
                        {
                            returnFacetList.addAll(facetListPriceRange);
                        }
                        else if("FACET_GROUP_RATINGS".equals(productFeatureCatGrpAppl.get("productFeatureGroupId")))
                        {
                            returnFacetList.addAll(facetListCustomerRating);
                        }
                        else
                        {
                        	for (GenericRefinement facet : facetList)
                        	{
                        		if (facet.getType().equals(productFeatureCatGrpAppl.get("productFeatureGroupId")))
                        		{
                        			returnFacetList.add(facet);
                        			break;
                        		}
                        	}
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Debug.logError(e.getMessage(), module);
                returnFacetList = new ArrayList<GenericRefinement>();
                returnFacetList.addAll(facetCatList);
                returnFacetList.addAll(facetListPriceRange);
                returnFacetList.addAll(facetListCustomerRating);
                returnFacetList.addAll(facetList);
            }
        }
        else
        {
            returnFacetList.addAll(facetCatList);
            returnFacetList.addAll(facetListPriceRange);
            returnFacetList.addAll(facetListCustomerRating);
            returnFacetList.addAll(facetList);
        }
        return returnFacetList;
    }
    
    private static String replaceSpecialChar(String str)
    {
    	if(UtilValidate.isNotEmpty(str))
    	{
    		str = str.replace("/", "%2F");
    		str = str.replace("\\", "%5C");
        	str = str.replace("\"", "%22");
        	str = str.replace(":", "%3A");
        	str = str.replace("&", "%26");
        	str = str.replace("!", "%21");
        	str = str.replace("^", "%5E");
    	}
    	else
    	{
    		return "";
    	}
    	return str;
    }
}
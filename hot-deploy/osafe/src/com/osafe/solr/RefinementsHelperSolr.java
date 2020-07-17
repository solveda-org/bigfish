package com.osafe.solr;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import com.osafe.util.Util;
import javax.servlet.http.HttpServletRequest;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.collections.primitives.adapters.CollectionByteCollection;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.LocalDispatcher;

import com.osafe.events.SolrEvents;
import com.osafe.services.SolrIndexDocument;
import org.apache.solr.common.SolrDocument;

public class RefinementsHelperSolr {

    private static final String module = RefinementsHelperSolr.class.getName();
    private CommandContext commandContext;
    private static Delegator delegator = null;
    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());

    public RefinementsHelperSolr(CommandContext commandContext, Delegator delegator) 
    {
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
    
    public Collection processRefinements(List<FacetField> facetResults) 
    {

        // Create a new collection for the generic Refinements in the generic wrapper
        Collection refinementCollection = new ArrayList();
        if (UtilValidate.isNotEmpty(facetResults)) 
        {
            // Get the standard refinement options set
            Iterator<FacetField> facets = facetResults.iterator();

            // Loop through the facets
            while (facets.hasNext()) 
            {
                // Get the Solr facet
                FacetField facet = (FacetField) facets.next();

                if (UtilValidate.isNotEmpty(facet) && UtilValidate.isNotEmpty(facet.getValues())) 
                {
                    // Create a generic refinement
                    GenericRefinement genericRefinement = new GenericRefinement();

                    // Populate the generic refinement with the details from the Solr facet
                    String facetType = facet.getName();

                    boolean isOnCategoryList = commandContext.isOnCategoryList();

                    if (isOnCategoryList) 
                    {
                        if (!SolrConstants.TYPE_PRODUCT_CATEGORY.equals(facetType)) 
                        {
                            // skip
                            //continue;
                        }
                    }
                    String productFeatureGroupId = null;
                    String productFeatureGroupFacetSort = null;
                    Map<String, String> filterGroupsIds = commandContext.getFilterGroupsIds();
                    Map<String, String> filterGroupsFacetSorts = commandContext.getFilterGroupsFacetSorts();

                    // Product Feature Group Id
                    if (filterGroupsIds.containsKey(facetType)) 
                    {
                        productFeatureGroupId = filterGroupsIds.get(facetType);
                    }
                    genericRefinement.setProductFeatureGroupId(productFeatureGroupId);

                    // Product Feature Group Facet Sorting rule
                    if (filterGroupsFacetSorts.containsKey(facetType)) 
                    {
                        productFeatureGroupFacetSort = filterGroupsFacetSorts.get(facetType);
                    }
                    if (UtilValidate.isEmpty(productFeatureGroupFacetSort)) 
                    {
                        productFeatureGroupFacetSort = SolrConstants.FACET_SORT_DB_SEQ;
                    }
                    genericRefinement.setProductFeatureGroupFacetSort(productFeatureGroupFacetSort);

                    String facetName = facetType;
                    genericRefinement.setType(facetType);

                    genericRefinement.setProductFeatureTypeId(facetType);

                    Map<String, String> filterGroupsDescriptions = commandContext.getFilterGroupsDescriptions();
                    if (filterGroupsDescriptions.containsKey(facetType)) 
                    {
                        facetName = filterGroupsDescriptions.get(facetType);
                    } 
                    else 
                    {
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
                    if (UtilValidate.isNotEmpty(genericRefinement)) 
                    {
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
     * @param results
     * @return Collection of GenericRefinements
     */
    public Collection processRefinements(List<FacetField> facetResults, List<SolrDocument> results) 
    {
        // Create a new collection for the generic Refinements in the generic wrapper
        Collection refinementCollection = new ArrayList();
        if (UtilValidate.isNotEmpty(facetResults)) 
        {
            // Get the standard refinement options set
            Iterator<FacetField> facets = facetResults.iterator();

            // Loop through the facets
            while (facets.hasNext()) 
            {
                // Get the Solr facet
                FacetField facet = (FacetField) facets.next();

                if (UtilValidate.isNotEmpty(facet) && UtilValidate.isNotEmpty(facet.getValues())) 
                {
                    // Create a generic refinement
                    GenericRefinement genericRefinement = new GenericRefinement();

                    // Populate the generic refinement with the details from the Solr facet
                    String facetType = facet.getName();

                    boolean isOnCategoryList = commandContext.isOnCategoryList();

                    if (isOnCategoryList) 
                    {
                        if (!SolrConstants.TYPE_PRODUCT_CATEGORY.equals(facetType)) 
                        {
                            // skip
                            //continue;
                        }
                    }
                    String productFeatureGroupId = null;
                    String productFeatureGroupFacetSort = null;
                    Map<String, String> filterGroupsIds = commandContext.getFilterGroupsIds();
                    Map<String, String> filterGroupsFacetSorts = commandContext.getFilterGroupsFacetSorts();

                    // Product Feature Group Id
                    if (filterGroupsIds.containsKey(facetType)) 
                    {
                        productFeatureGroupId = filterGroupsIds.get(facetType);
                    }
                    genericRefinement.setProductFeatureGroupId(productFeatureGroupId);

                    // Product Feature Group Facet Sorting rule
                    if (filterGroupsFacetSorts.containsKey(facetType)) 
                    {
                        productFeatureGroupFacetSort = filterGroupsFacetSorts.get(facetType);
                    }
                    if (UtilValidate.isEmpty(productFeatureGroupFacetSort)) 
                    {
                        productFeatureGroupFacetSort = SolrConstants.FACET_SORT_DB_SEQ;
                    }
                    genericRefinement.setProductFeatureGroupFacetSort(productFeatureGroupFacetSort);

                    String facetName = facetType;
                    genericRefinement.setType(facetType);

                    genericRefinement.setProductFeatureTypeId(facetType);

                    Map<String, String> filterGroupsDescriptions = commandContext.getFilterGroupsDescriptions();
                    if (filterGroupsDescriptions.containsKey(facetType)) 
                    {
                        facetName = filterGroupsDescriptions.get(facetType);
                    } 
                    else 
                    {
                        facetName = WordUtils.capitalizeFully(facetType);
                    }

                    genericRefinement.setName(facetName);

                    // Get a collection of the refinement values for this refinement
                    // may include merged common refinement values
                    Collection refinementValues = processRefinementValues(facet, genericRefinement, results);

                    // Add the refinement values to the refinement
                    genericRefinement.setRefinementValues(refinementValues);

                    // Add the new generic refinement to the collection - check
                    // errors elsewhere have not created a null refinement first
                    if (UtilValidate.isNotEmpty(genericRefinement)) 
                    {
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
    public Collection processPriceRangeRefinements(Map facetResults) 
    {
        // Create a new collection for the generic Refinements in the generic
        // wrapper
        Collection refinementCollection = new ArrayList();
        if (UtilValidate.isNotEmpty(facetResults)) 
        {
            // Get the standard refinement options set
            Iterator facets = facetResults.keySet().iterator();

            // Create a generic refinement
            GenericRefinement genericRefinement = new GenericRefinement();

            // Populate the generic refinement with the details from the Solr facet
            genericRefinement.setType(SolrConstants.TYPE_PRICE);
            genericRefinement.setName(SolrConstants.DISPLAY_PRICE_NAME_KEY);

            List refinementValues = new ArrayList();

            // Loop through the facets
            while (facets.hasNext()) 
            {
                // Get the Solr refinement value
                String value = (String) facets.next();
                Integer count = (Integer) facetResults.get(value);

                if (value.indexOf(SolrConstants.TYPE_PRICE) > -1) 
                {
                    if (UtilValidate.isNotEmpty(count) && count.longValue() > 0) 
                    {
                        // Create a generic refinement value
                        GenericRefinementValue genericRefinementValue = convertRefinementValueToGeneric(value, count.longValue(), genericRefinement);

                        // Add the refinementValue to the collection
                        refinementValues.add(genericRefinementValue);
                    }
                }
            }

            if (!UtilValidate.isEmpty(refinementValues)) 
            {
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
    public Collection processPriceRangeRefinements(Map facetResults, List<SolrIndexDocument> results) 
    {
        // Create a new collection for the generic Refinements in the generic wrapper
        Collection refinementCollection = new ArrayList();
        if (UtilValidate.isNotEmpty(facetResults)) 
        {
            // Get the standard refinement options set
            Iterator facets = facetResults.keySet().iterator();

            // Create a generic refinement
            GenericRefinement genericRefinement = new GenericRefinement();

            // Populate the generic refinement with the details from the Solr facet
            genericRefinement.setType(SolrConstants.TYPE_PRICE);
            genericRefinement.setName(SolrConstants.DISPLAY_PRICE_NAME_KEY);

            List refinementValues = new ArrayList();

            // Loop through the facets
            while (facets.hasNext()) 
            {
                // Get the Solr refinement value
                String value = (String) facets.next();
                Integer count = (Integer) facetResults.get(value);
                if (value.indexOf(SolrConstants.TYPE_PRICE) > -1) 
                {
                    if (UtilValidate.isNotEmpty(count) && count.longValue() > 0) 
                    {
                    	if(UtilValidate.isNotEmpty(results)) 
                    	{
                    		List<SolrIndexDocument> duplicateProducts = getDuplicateProducts(results);
                    		if(UtilValidate.isNotEmpty(duplicateProducts)) 
                    		{
	                            Iterator duplicateProductItr = duplicateProducts.iterator();
	                            while (duplicateProductItr.hasNext()) 
	                            {
	                                SolrIndexDocument solrIndexDocument = (SolrIndexDocument)duplicateProductItr.next();
	                                double start = getPriceRangeArr(value)[0].doubleValue();
	                                double end = getPriceRangeArr(value)[1].doubleValue();
	                                double productPrice = solrIndexDocument.getPrice().doubleValue();
	                                if (Double.compare(start, productPrice) <= 0 && Double.compare(end, productPrice) >= 0) 
	                                {
	                            	    count = count - 1;
	                                }
	                            }
                    		}
                        }
                        // Create a generic refinement value
                        GenericRefinementValue genericRefinementValue = convertRefinementValueToGeneric(value, count.longValue(), genericRefinement);
                        // Add the refinementValue to the collection
                        
                        if (!refinementValues.contains(genericRefinementValue))
                        {
                        	refinementValues.add(genericRefinementValue);
                        }
                    }
                }
            }

            if (!UtilValidate.isEmpty(refinementValues)) 
            {
                // sort the price order
                Collections.sort(refinementValues);

                genericRefinement.setRefinementValues(refinementValues);
                refinementCollection.add(genericRefinement);
            }
        }
        return refinementCollection;
    }
    
    
    public Map<String, Integer> getResultFacetQueryPrice(HttpServletRequest request, List<SolrIndexDocument> results, List<SolrIndexDocument> multiSelectResults) 
    {
    	List resultsDoc = updateSolrDocumentPrice(results, request);
    	String facetValueStyle = Util.getProductStoreParm(request, "FACET_VALUE_STYLE");
        boolean facetMultiSelectTrue = false;
        if (UtilValidate.isNotEmpty(facetValueStyle) && (facetValueStyle.equalsIgnoreCase("CHECKBOX") || facetValueStyle.equalsIgnoreCase("DROPDOWN")))
        {
        	facetMultiSelectTrue = true;
        }
    	Map LowHighPriceMap = FastMap.newInstance();
    	// multiSelectResults is used to show the all price facets
    	if (UtilValidate.isNotEmpty(multiSelectResults)) 
        {
        	List multiSelectResultsDoc = updateSolrDocumentPrice(multiSelectResults, request);
            LowHighPriceMap = getLowHighPriceMap(request, multiSelectResultsDoc);
        }
    	else
    	{
        	LowHighPriceMap = getLowHighPriceMap(request, resultsDoc);
    	}
    	Float priceLow = (Float) LowHighPriceMap.get("priceLow");
        Float priceHigh = (Float) LowHighPriceMap.get("priceHigh");
        List<Map<String, Integer>> resultsFacetQueriesMapList = FastList.newInstance();
        Map<String, Integer> resultsFacetQueriesMap = new HashMap();
        
        if (UtilValidate.isNotEmpty(priceLow) && UtilValidate.isNotEmpty(priceHigh)) 
        {
            float priceHighVal = priceHigh.floatValue();
            float priceLowVal = priceLow.floatValue();

            float priceRange = priceHighVal - priceLowVal;
            float step = priceRange / 5;

            float priceFacetRound=.01f;
            String sPriceFacetRound =Util.getProductStoreParm(request, "FACET_PRICE_ROUND");
            if (!UtilValidate.isEmpty(sPriceFacetRound))
            {
                int iPriceFacetRound=2;
                try 
                {
                    iPriceFacetRound = Integer.parseInt(sPriceFacetRound);
                }
                catch (Exception e)
                {
                    iPriceFacetRound=2; 
                }
                if (iPriceFacetRound == 0)
                {
                    priceFacetRound=1.0f;
                }
            }

            if (step > 10000)
            {
                step = ((float)Math.ceil((step/1000))*1000);
            }
            else if (step > 1000)
            {
                step = ((float)Math.ceil((step/100))*100);
            }
            else if (step > 100)
            {
                step = ((float)Math.ceil((step/10))*10);
            }
            else
            {
                step = ((float)Math.ceil((step/1))*1);
            }

            //incremental factor calculation as defined in BF Facet Price Calculator.xls
            //CEIL HIGH PRICE
            if (priceHighVal > 1000)
            {
                priceHighVal= ((float)Math.ceil((priceHighVal/100))*100);
            }
            else if (priceHighVal > 100)
            {
                priceHighVal= ((float)Math.ceil((priceHighVal/10))*10);
            }
            else
            {
                priceHighVal= ((float)Math.ceil((priceHighVal/1))*1);
            }

            //FLOOR LOW PRICE
            if (priceLowVal > 1000)
            {
                priceLowVal= ((float)Math.floor((priceLowVal/100))*100);
            }
            else if (priceLowVal > 100)
            {
                priceLowVal= ((float)Math.floor((priceLowVal/10))*10);
            }
            else
            {
                priceLowVal= ((float)Math.floor((priceLowVal/1))*1);
            }

            float rangeLowVal = priceLowVal;
            float rangeHighVal = priceLowVal + step;

            int rangeCount = 0;
            while (priceRange > 0) 
            {
            	String priceRangeMap =  "price:[" + (rangeLowVal) + " " + rangeHighVal + "]";
                int productCount = getProductCountsInRange(resultsDoc, rangeLowVal, rangeHighVal).get(priceRangeMap);
                // if facetMultiSelectTrue is true then show the count even product count is zero
                if (productCount > 0 || facetMultiSelectTrue)
                {
                    float tempPriceRange =  priceRange - step;
                    float tempRangeLowVal = rangeHighVal + priceFacetRound;
                    float tempRangeHighVal = rangeHighVal + step;
                    // if facetMultiSelectTrue is true then do not club the facets even product count is zero
                    while (tempPriceRange > 0 && !facetMultiSelectTrue) 
                    {
                    	String priceRangeQueryTemp = "price:[" + (tempRangeLowVal) + " " + tempRangeHighVal + "]";
                    	int productCounttemp = getProductCountsInRange(resultsDoc, tempRangeLowVal, tempRangeHighVal).get(priceRangeQueryTemp);
                        if (productCounttemp > 0)
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

                    priceRange = priceRange - step;

                    if(priceRange <= 0)
                    {
                    	resultsFacetQueriesMapList.add(getProductCountsInRange(resultsDoc, rangeLowVal, priceHighVal));
                    }
                    else
                    {
                        if(rangeCount == 0)
                        {
                        	resultsFacetQueriesMapList.add(getProductCountsInRange(resultsDoc, priceLowVal, rangeHighVal));
                        }
                        else
                        {
                        	resultsFacetQueriesMapList.add(getProductCountsInRange(resultsDoc, rangeLowVal, rangeHighVal));
                        }
                    }
                    rangeLowVal = rangeHighVal + priceFacetRound;

                    rangeHighVal = rangeHighVal + step;
                }
                else
                {
                    priceRange = priceRange - step;
                    rangeHighVal = rangeHighVal + step;
                }
                rangeCount++;
            }
            
            for(Map<String, Integer> resultsFacetQueries : resultsFacetQueriesMapList)
            {
            	for (Map.Entry<String, Integer> entry : resultsFacetQueries.entrySet())
            	{
            		resultsFacetQueriesMap.put(entry.getKey(), entry.getValue());
            	}
            }
        }
        return resultsFacetQueriesMap;
    }

    
    public Map getLowHighPriceMap(HttpServletRequest request, List<SolrIndexDocument> resultsDoc)
    {
    	String facetValueStyle = Util.getProductStoreParm(request, "FACET_VALUE_STYLE");
        boolean facetMultiSelectTrue = false;
        if (UtilValidate.isNotEmpty(facetValueStyle) && (facetValueStyle.equalsIgnoreCase("CHECKBOX") || facetValueStyle.equalsIgnoreCase("DROPDOWN")))
        {
        	facetMultiSelectTrue = true;
        }
    	Float priceLow = null;
        Float priceHigh = null;
        String multiFacetInitialType = null;
        Map<String, Object> lowHighPriceMap = new HashMap();
        
        //GET THE LOWPRICE AND HIGHPRICE FROM REQUEST PARAMETERS
        String filterGroup = request.getParameter("filterGroup");
        if (UtilValidate.isEmpty(filterGroup))
        {
            filterGroup = (String)request.getAttribute("filterGroup");
        }
        if (UtilValidate.isNotEmpty(filterGroup)) 
        {
            String[] filterGroupArr = StringUtils.split(filterGroup, "|");

            for (int i = 0; i < filterGroupArr.length; i++) 
            {
                String filterGroupValue = filterGroupArr[i];
                if (filterGroupValue.toLowerCase().contains("price")) 
                {
                    filterGroupValue = StringUtil.replaceString(filterGroupValue,"+", " ");
                }

                String[] splitTemp = filterGroupValue.split(":");

                if(UtilValidate.isEmpty(multiFacetInitialType))
                {
                    multiFacetInitialType = splitTemp[0];
                }

                String encodedValue = null;
                splitTemp[1] = splitTemp[1].replace("/", "%2F");
                splitTemp[1] = splitTemp[1].replace("\"", "%22");
                splitTemp[1] = splitTemp[1].replace(":", "%3A");
                splitTemp[1] = splitTemp[1].replace("&", "%26");

                try 
                {
                    encodedValue = URLEncoder.encode(splitTemp[1], SolrConstants.DEFAULT_ENCODING);
                } 
                catch (UnsupportedEncodingException e) 
                {
                    encodedValue = splitTemp[1];
                }
                
                filterGroupValue = splitTemp[0] + ":" + encodedValue;

                // Price
                if (filterGroupValue.toLowerCase().contains("price")) 
                {
                    
                    //calculation for price range retrieve on price facet selection click
                    String[] priceRangeArr = null;
					try {
						priceRangeArr = URLDecoder.decode(encodedValue, SolrConstants.DEFAULT_ENCODING).split(" ");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    try 
                    {
                        if(facetMultiSelectTrue)
                        {
                            //if there are multiple price groups then the lower price would be the minimum price and high price would be the maximum price.
                            //For Ex:- price:[100 150] and price: [90 120], then The LowPrice is 90 and HighPrice is 150.
                            if(UtilValidate.isEmpty(priceLow) || (UtilValidate.isNotEmpty(priceLow) && Float.parseFloat(priceRangeArr[0].substring(1)) < priceLow))
                            {
                                priceLow = Float.parseFloat(priceRangeArr[0].substring(1));
                            }
                            if(UtilValidate.isEmpty(priceHigh) || (UtilValidate.isNotEmpty(priceHigh) && Float.parseFloat(priceRangeArr[1].substring(0, priceRangeArr[1].length()-1)) > priceHigh))
                            {
                                priceHigh = Float.parseFloat(priceRangeArr[1].substring(0, priceRangeArr[1].length()-1));
                            }
                        }
                        else
                        {
                            //if there are multiple price groups then the lower price would be the maximum price and high price would be the minimum price.
                            //For Ex:- price:[100 150] and price: [90 120], then The LowPrice is 100 and HighPrice is 120.
                            if(UtilValidate.isEmpty(priceLow) || (UtilValidate.isNotEmpty(priceLow) && Float.parseFloat(priceRangeArr[0].substring(1)) > priceLow))
                            {
                                priceLow = Float.parseFloat(priceRangeArr[0].substring(1));
                            }
                            if(UtilValidate.isEmpty(priceHigh) || (UtilValidate.isNotEmpty(priceHigh) && Float.parseFloat(priceRangeArr[1].substring(0, priceRangeArr[1].length()-1)) < priceHigh))
                            {
                                priceHigh = Float.parseFloat(priceRangeArr[1].substring(0, priceRangeArr[1].length()-1));
                            }
                        }
                    } 
                    catch (NumberFormatException nexc) 
                    {
                        priceLow = null;
                        priceHigh = null;
                    }
                    // skip
                    continue;
                }
            }
        }
    	
        //GET THE LOWPRICE AND HIGHPRICE FROM RESULTS IF THE REQUEST PARAMETERS DOESN'T HAVE ANY
        if (UtilValidate.isEmpty(priceLow)) 
        {
        	Iterator itr = resultsDoc.iterator();
            List newresult = FastList.newInstance();
            try 
            {
            	BigDecimal lowPrice = null;
                while(itr.hasNext()) 
                {
                    SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itr.next();
                    if(UtilValidate.isEmpty(lowPrice))
                    {
                    	lowPrice = new BigDecimal(solrIndexDocument.getPrice());
                    }
                    BigDecimal price = new BigDecimal(solrIndexDocument.getPrice());
                    if(price.compareTo(lowPrice) < 0)
                    {
                    	lowPrice = price;
                    }
                }
                priceLow = lowPrice.floatValue();
                
            }
            catch(Exception e)
            {
            	
            }
        }
    	
        
        if (UtilValidate.isEmpty(priceHigh)) 
        {
        	Iterator itr = resultsDoc.iterator();
            List newresult = FastList.newInstance();
            try 
            {
            	BigDecimal highPrice = null;
                while(itr.hasNext()) 
                {
                    SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itr.next();
                    if(UtilValidate.isEmpty(highPrice))
                    {
                    	highPrice = new BigDecimal(solrIndexDocument.getPrice());
                    }
                    BigDecimal price = new BigDecimal(solrIndexDocument.getPrice());
                    if(price.compareTo(highPrice) > 0)
                    {
                    	highPrice = price;
                    }
                }
                priceHigh = highPrice.floatValue();
                
            }
            catch(Exception e)
            {
            	
            }
        }
        if(UtilValidate.isNotEmpty(priceLow) && UtilValidate.isNotEmpty(priceHigh))
        {
        	lowHighPriceMap.put("priceLow", priceLow);
            lowHighPriceMap.put("priceHigh", priceHigh);
        }
        return lowHighPriceMap;
        
    }
    
    public Collection processCustomerRatingRefinements(Map facetResults, List<SolrIndexDocument> results) 
    {
        // Create a new collection for the generic Refinements in the generic wrapper
        Collection refinementCollection = new ArrayList();
        if (UtilValidate.isNotEmpty(facetResults)) 
        {
            // Get the standard refinement options set
            Iterator facets = facetResults.keySet().iterator();

            // Create a generic refinement
            GenericRefinement genericRefinement = new GenericRefinement();

            // Populate the generic refinement with the details from the Solr facet
            genericRefinement.setType(SolrConstants.TYPE_CUSTOMER_RATING);
            genericRefinement.setName(SolrConstants.DISPLAY_CUSTOMER_RATING_NAME_KEY);

            List refinementValues = new ArrayList();

            // Loop through the facets
            while (facets.hasNext()) 
            {
                // Get the Solr refinement value
                String value = (String) facets.next();
                Integer count = (Integer) facetResults.get(value);

                if (value.indexOf(SolrConstants.TYPE_CUSTOMER_RATING) > -1) 
                {
                    if (UtilValidate.isNotEmpty(count) && count.longValue() > 0) 
                    {
                    	if(UtilValidate.isNotEmpty(results)) 
                    	{
                    		List<SolrIndexDocument> duplicateProducts = getDuplicateProducts(results);
                    		if(UtilValidate.isNotEmpty(duplicateProducts)) 
                    		{
	                            Iterator duplicateProductItr = duplicateProducts.iterator();
	                            while (duplicateProductItr.hasNext()) 
	                            {
	                                SolrIndexDocument solrIndexDocument = (SolrIndexDocument)duplicateProductItr.next();
	                                double customerRatingLowRange = Double.valueOf(getCustomerRatingLowRangeStr(value));
	                                double productCustomerRating = solrIndexDocument.getCustomerRating().doubleValue();
	                                if (Double.compare(productCustomerRating, customerRatingLowRange) >= 0) 
	                                {
	                            	    count = count - 1;
	                                }
	                            }
                    		}
                        }
                        // Create a generic refinement value
                        GenericRefinementValue genericRefinementValue = convertRefinementValueToGeneric(value, count.longValue(), genericRefinement);

                        // Add the refinementValue to the collection
                        refinementValues.add(genericRefinementValue);
                    }
                }
            }

            if (!UtilValidate.isEmpty(refinementValues)) 
            {
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
    private Collection sortRefinementValues(Collection refinementCollection) 
    {
        Object[] arrayFacets = null;
        if (UtilValidate.isNotEmpty(refinementCollection)) 
        {
            arrayFacets = refinementCollection.toArray();
            Collection sortedCollection = new ArrayList();
            String productFeatureGroupFacetSort = null;
            for (int i = 0; i < arrayFacets.length; i++) 
            {
                GenericRefinement refinement = (GenericRefinement) arrayFacets[i];
                FacetValueSequenceComparator comparator = null;
                comparator = new FacetValueSequenceComparator(this.delegator, refinement.getProductFeatureGroupId());

                if (SolrConstants.TYPE_PRODUCT_CATEGORY.equals(refinement.getType()) || SolrConstants.TYPE_TOP_MOST_PRODUCT_CATEGORY.equals(refinement.getType())) 
                {
                    comparator.setUseSequenceNum(true);
                } 
                else 
                {
                    productFeatureGroupFacetSort = refinement.getProductFeatureGroupFacetSort();
                    comparator.setProductFeatureGroupSorting(productFeatureGroupFacetSort);
                    if (SolrConstants.FACET_SORT_DB_SEQ.equals(productFeatureGroupFacetSort)) 
                    {
                        comparator.populateSortOrder();
                    }
                }

                Collection facetValues = (Collection) refinement.getRefinementValues();
                if (UtilValidate.isNotEmpty(facetValues)) 
                {
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
    
    private Collection processRefinementValues(FacetField facet, GenericRefinement genericRefinement) 
    {
        Collection refinementValues = new ArrayList();

        // Get the associated values from the facet
        List values = facet.getValues();
        Iterator it = values.iterator();

        // Loop through the values
        while (it.hasNext()) 
        {
            // Get the Solr refinement value

            FacetField.Count value = (FacetField.Count) it.next();
            
            GenericRefinementValue genericRefinementValue = null;
            // Create a generic refinement value
            try 
            {
                genericRefinementValue = convertRefinementValueToGeneric(URLDecoder.decode(value.getName(), SolrConstants.DEFAULT_ENCODING), value.getCount(), genericRefinement);
            } 
            catch (UnsupportedEncodingException e) 
            {
                // Oops UTF-8?????
                genericRefinementValue = convertRefinementValueToGeneric(value.getName(), value.getCount(), genericRefinement);
            }

            // Add the refinementValue to the collection
            refinementValues.add(genericRefinementValue);
        }

        return refinementValues;
    }

    
    private Collection processRefinementValues(FacetField facet, GenericRefinement genericRefinement, List<SolrDocument> results) 
    {
        Collection refinementValues = new ArrayList();

        // Get the associated values from the facet
        List values = facet.getValues();
        Iterator it = values.iterator();

        // Loop through the values
        while (it.hasNext()) 
        {
            // Get the Solr refinement value

            FacetField.Count value = (FacetField.Count) it.next();
            Integer count = (int)(long)value.getCount();
            if (UtilValidate.isNotEmpty(count) && count.longValue() > 0) 
            {
            	if(UtilValidate.isNotEmpty(results)) 
            	{
            		String facetFieldName = value.getFacetField().getName();
            		count = countFacetExistenceInResult(results, value);
            		List<SolrDocument> duplicateProducts = getDuplicateProductsSolrDoc(results);
            		if(UtilValidate.isNotEmpty(duplicateProducts)) 
            		{
                        Iterator duplicateProductItr = duplicateProducts.iterator();
                        while (duplicateProductItr.hasNext()) 
                        {
                        	SolrDocument solrDocument = (SolrDocument)duplicateProductItr.next();
                        	List<String> facetValueList = (List)solrDocument.getFieldValue(facetFieldName);
                            if(UtilValidate.isNotEmpty(facetValueList) && facetValueList.contains(value.getName()))
                            {
                            	count = count - 1;
                            }
                        }
            		}
                }
            }
            
            GenericRefinementValue genericRefinementValue = null;
            if(count > 0)
            {
	            // Create a generic refinement value
	            try 
	            {
	                genericRefinementValue = convertRefinementValueToGeneric(URLDecoder.decode(value.getName(), SolrConstants.DEFAULT_ENCODING), count.longValue(), genericRefinement);
	            } 
	            catch (UnsupportedEncodingException e) 
	            {
	                // Oops UTF-8?????
	                genericRefinementValue = convertRefinementValueToGeneric(value.getName(), count.longValue(), genericRefinement);
	            }
     	        // Add the refinementValue to the collection
	            refinementValues.add(genericRefinementValue);
            }
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
    private GenericRefinementValue convertRefinementValueToGeneric(String key, long count, GenericRefinement genericRefinement) 
    {

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
        if (UtilValidate.isNotEmpty(ccSearchText)) 
        {
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

        if (UtilValidate.isNotEmpty(ccProductCategoryId)) 
        {
            if (firstParamAdded) 
            {
                url += "&";
            }
            url += "productCategoryId" + "=" + ccProductCategoryId;
        }

        if (isProductCategory && UtilValidate.isEmpty(ccSearchText)) 
        {
            if (!isOnCategoryList) 
            {
                url += "&";
            }
            url += "productCategoryId" + "=" + key;
        }

        if (!isOnCategoryList) 
        {
            url += "&" + "filterGroup" + "=";
            List<String> prevFilterGroups = commandContext.getFilterGroups();
            List<String> filterGroupList = FastList.newInstance();
            if (!isPrice && !isCustomerRating) 
            {
                String encodedKey = null;
                try 
                {
                    encodedKey = URLEncoder.encode(key, SolrConstants.DEFAULT_ENCODING);
                } 
                catch (UnsupportedEncodingException e) 
                {
                    encodedKey = key;
                }
                filterGroupList.add(genericRefinement.getType() + ":" + encodedKey);
            }
            filterGroupList.addAll(0, prevFilterGroups);
            String filterGroups = StringUtils.join(filterGroupList, "|");
            url += filterGroups;

            String priceFilterGroupValue = null;
            if(isPrice) 
            {
                priceFilterGroupValue = key;
                priceFilterGroupValue = priceFilterGroupValue.replaceAll("price", "PRICE");

                if (filterGroupList.size() > 0) 
                {
                    url += "|";
                }
                url += priceFilterGroupValue;
            }
            String customerRatingFilterGroupValue = null;
            if (isCustomerRating) 
            {
                customerRatingFilterGroupValue = key;
                customerRatingFilterGroupValue = customerRatingFilterGroupValue.replaceAll("customerRating", "CUSTOMER_RATING");

                if (filterGroupList.size() > 0) 
                {
                    url += "|";
                }
                url += customerRatingFilterGroupValue;
            }

            // Sorting parameter
            String sortParameterName = commandContext.getSortParameterName();
            if (UtilValidate.isNotEmpty(sortParameterName)) 
            {
                String sortParameterValue = StringUtils.trimToEmpty(commandContext.getSortParameterValue());
                url += "&" + sortParameterName + "=" + sortParameterValue;
            }

            // Rows Shown parameter
            String numberOfRowsShown = commandContext.getNumberOfRowsShown();
            if (UtilValidate.isNotEmpty(numberOfRowsShown)) 
            {
                url += "&rows=" + numberOfRowsShown;
            }
        }
        genericRefinementValue.setRefinementURL(url);

        // Set the value to display in the jsp. Different for price ranges
        if (isPrice) 
        {
            String displayName = getPriceRangeStr(key);
            genericRefinementValue.setDisplayName(displayName);
            genericRefinementValue.setStart(getPriceRangeArr(key)[0].doubleValue());
        } 
        else if (isCustomerRating) 
        {
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
        } 
        else if (isTopMostProductCategory) 
        {
            String displayName = getProductCategoryName(key);
            genericRefinementValue.setDisplayName(displayName);
            genericRefinementValue.setSequenceNum(getProductCategorySequenceNum(key));
        } 
        else if (isProductCategory) 
        {
            String displayName = getProductCategoryName(key);
            genericRefinementValue.setDisplayName(displayName);
            String supportingText = getProductCategorySupportingText(key);
            genericRefinementValue.setSupportingText(supportingText);
            String displayImage = getProductCategoryImage(key);
            genericRefinementValue.setDisplayImage(displayImage);
            genericRefinementValue.setSequenceNum(getProductCategorySequenceNum(key));
        } 
        else 
        {
            genericRefinementValue.setDisplayName(ProductFeatureEncoder.decodePlus(key));
        }

        return genericRefinementValue;
    }

    private Double[] getPriceRangeArr(String key) 
    {
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

    private String getPriceRangeStr(String key) 
    {
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
            try 
            {
                rounding = Integer.parseInt(parmValue);
            } 
            catch(NumberFormatException nfe) 
            {
                Debug.logError(nfe.getMessage(), module);
            }
        }

        String start = UtilFormatOut.formatCurrency(Double.valueOf(split[0]), isoCode, UtilHttp.getLocale(request), rounding);
        String end = UtilFormatOut.formatCurrency(Double.valueOf(split[1]), isoCode, UtilHttp.getLocale(request), rounding);
        return start + " to " + end;
    }

    private String getCustomerRatingLowRangeStr(String key) 
    {

        String ratingNumber = "5";
        key = StringUtils.substring(key, key.indexOf(":") + 1);
        key = StringUtils.strip(key, "[]");
        String[] split = StringUtils.split(key, " ");

        split[0] = StringUtils.replace(split[0], "*", "0");

        ratingNumber = split[0];
        return ratingNumber;
    }

    private String getCustomerRatingRangeStr(String key) 
    {
        String ratingNumber = getCustomerRatingLowRangeStr(key);
        return ratingNumber + ".0 &amp; up";
    }

    private String getProductCategoryName(String key) 
    {
        GenericValue productCategory = null;
        String productCategoryName = key;
        try 
        {
            productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", key), true);
            productCategoryName = productCategory.getString("categoryName");
        } 
        catch (GenericEntityException e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        return productCategoryName;
    }

    private String getProductCategorySupportingText(String key) 
    {
        GenericValue productCategory = null;
        String supportingText = "";
        try 
        {
            productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", key), true);
            String supportingTextWrapper = productCategory.getString("longDescription");
            if (supportingTextWrapper != null) 
            {
                supportingText = supportingTextWrapper.toString();
            }
        } 
        catch (GenericEntityException e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        return supportingText;
    }

    private String getProductCategoryImage(String key) 
    {
        GenericValue productCategory = null;
        String productCategoryImage = null;
        try 
        {
            productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", key), true);
            String categoryImageUrl = productCategory.getString("categoryImageUrl");
            if (UtilValidate.isNotEmpty(categoryImageUrl)) 
            {
                productCategoryImage = categoryImageUrl.toString();
            }
        } 
        catch (GenericEntityException e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        return productCategoryImage;
    }

    private Long getProductCategorySequenceNum(String key) 
    {
        Long sequenceNum = Long.valueOf(0);
        try 
        {
            List<GenericValue> productCategoryRollups = delegator.findByAndCache("ProductCategoryRollup", UtilMisc.toMap("productCategoryId", key));
            productCategoryRollups = EntityUtil.filterByDate(productCategoryRollups);
            if (UtilValidate.isNotEmpty(productCategoryRollups)) 
            {
                GenericValue productCategoryRollup = EntityUtil.getFirst(productCategoryRollups);
                sequenceNum = productCategoryRollup.getLong("sequenceNum");
            }
        } 
        catch (GenericEntityException e) 
        {
            Debug.logError(e.getMessage(), module);
        }
        return sequenceNum;
    }

    public static List<SolrIndexDocument> getDuplicateProducts(List<SolrIndexDocument> results) 
    {
        Iterator itr = results.iterator();
        List subresult = FastList.newInstance();
        List dupresult = FastList.newInstance();
        try 
        {
            while(itr.hasNext()) 
            {
                SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itr.next();
                if (subresult.contains(solrIndexDocument.getProductId())) 
                {
                    //results.remove(itr.next());
                	dupresult.add(solrIndexDocument);
                } 
                else 
                {
                    subresult.add(solrIndexDocument.getProductId());
                }
            }
        } 
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        return dupresult;
    }
    
    public static List<SolrDocument> getDuplicateProductsSolrDoc(List<SolrDocument> results) 
    {
        Iterator itr = results.iterator();
        List subresult = FastList.newInstance();
        List dupresult = FastList.newInstance();
        try 
        {
            while(itr.hasNext()) 
            {
            	SolrDocument solrDocument = (SolrDocument)itr.next();
                if (subresult.contains(solrDocument.getFieldValue("productId"))) 
                {
                    //results.remove(itr.next());
                	dupresult.add(solrDocument);
                } 
                else 
                {
                    subresult.add(solrDocument.getFieldValue("productId"));
                }
            }
        } 
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        return dupresult;
    }
    
    public static int countFacetExistenceInResult(List<SolrDocument> results, FacetField.Count value) 
    {
        Iterator itr = results.iterator();
        List subresult = FastList.newInstance();
        List dupresult = FastList.newInstance();
        int count = 0;
        try 
        {
            while(itr.hasNext()) 
            {
            	SolrDocument solrDocument = (SolrDocument)itr.next();
            	List<String> facetValueList = (List)solrDocument.getFieldValue(value.getFacetField().getName());
            	if(UtilValidate.isNotEmpty(facetValueList) && facetValueList.size() > 0)
            	{
            		String facetValueStr = facetValueList.get(0);
                	String[] facetValList = facetValueStr.split(" ");
                	String valueName = value.getName().trim();
                    if(UtilValidate.isNotEmpty(facetValList))
                    {
                    	for(int i = 0; i < facetValList.length; i++)
                    	{
                    		String facetVal = facetValList[i].trim();
                    		if(valueName.equals(facetVal))
                    		{
                    			count = count + 1;
                    		}
                    	}
                    }
            	}
            }
        } 
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        return count;
    }
    
    public static Map<String, Integer> getProductCountsInRange(List<SolrIndexDocument> results, Float lowPrice, Float  highPrice) 
    {
        Iterator itr = results.iterator();
        Map<String, Integer> productCountMap = new HashMap();
        int count = 0;
        try 
        {
            while(itr.hasNext()) 
            {
            	SolrIndexDocument solrDocument = (SolrIndexDocument)itr.next();
            	if(checkProductInPriceRange(solrDocument, lowPrice, highPrice))
            	{
            		count++;
            	}
            }
        } 
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        productCountMap.put(SolrConstants.TYPE_PRICE+":["+ lowPrice + " " + highPrice +"]", count);
        return productCountMap;
    }
    
    
    public static boolean checkProductInPriceRange(SolrIndexDocument solrIndexDocument, Float lowPrice, Float highPrice)
    {
        try 
        {
            Float documentPrice = solrIndexDocument.getPrice();
            if(documentPrice.compareTo(lowPrice) >= 0 && documentPrice.compareTo(highPrice) <= 0)
            {
            	return true;
            }
        }
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        return false;
    }
    
    public List<SolrIndexDocument> getProductDocInPriceRange(List<SolrIndexDocument> results, Float lowPrice, Float highPrice)
    {
    	
    	Iterator itr = results.iterator();
        Map<String, Integer> productCountMap = new HashMap();
        List newresult = FastList.newInstance();
        int count = 0;
        try 
        {
            while(itr.hasNext()) 
            {
            	SolrIndexDocument solrDocument = (SolrIndexDocument)itr.next();
            	if(checkProductInPriceRange(solrDocument, lowPrice, highPrice))
            	{
            		newresult.add(solrDocument);
            	}
            }
        } 
        catch (Exception e) 
        {
            Debug.log(e.getMessage());
        }
        return newresult;
    }
    
    public List<SolrIndexDocument> filterResultsOnPriceRange(HttpServletRequest request, List<SolrIndexDocument> results)
    {
    	List resultsDoc = updateSolrDocumentPrice(results, request);
        Map LowHighPriceMap = getLowHighPriceMap(request, resultsDoc);
        Float priceLow = (Float) LowHighPriceMap.get("priceLow");
        Float priceHigh = (Float) LowHighPriceMap.get("priceHigh");
    	return getProductDocInPriceRange(resultsDoc, priceLow, priceHigh);
    }
    
    public static List<SolrIndexDocument> updateSolrDocumentPrice(List<SolrIndexDocument> results, HttpServletRequest request) 
    {
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
    	if(UtilValidate.isEmpty(userLogin))
    	{
    		return results;
    	}

		String partyId = (String)userLogin.getString("partyId");
		List<GenericValue> productPriceRules = FastList.newInstance();
		if(UtilValidate.isNotEmpty(partyId))
		{
			List<GenericValue> partyIdConds = FastList.newInstance();
			try 
			{
				partyIdConds = delegator.findByAndCache("ProductPriceCond", UtilMisc.toMap("inputParamEnumId", "PRIP_PARTY_ID", "condValue", partyId));
			} 
			catch (GenericEntityException e1) 
			{
				e1.printStackTrace();
			}
			if(UtilValidate.isNotEmpty(partyIdConds))
			{
				List<String> priceRuleIds = EntityUtil.getFieldListFromEntityList(partyIdConds, "productPriceRuleId", true);
				try 
				{
					productPriceRules = delegator.findList("ProductPriceRule", EntityCondition.makeCondition("productPriceRuleId", EntityOperator.IN, priceRuleIds), null, null, null, true);
				} 
				catch (GenericEntityException e) 
				{
					e.printStackTrace();
				}
				productPriceRules = EntityUtil.filterByDate(productPriceRules);
			}
		}
			
		if(UtilValidate.isNotEmpty(productPriceRules))
		{
    		ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        	String currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
        	String webSiteId = CatalogWorker.getWebSiteId(request);
        	GenericValue productStore = ProductStoreWorker.getProductStore(request);
        	String productStoreId = (String) productStore.get("productStoreId");
        	
        	Delegator delegator = (Delegator) request.getAttribute("delegator");
        	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        	
            Iterator itr = results.iterator();
            List newresult = FastList.newInstance();
            try 
            {
                while(itr.hasNext()) 
                {
                    SolrIndexDocument solrIndexDocument = (SolrIndexDocument)itr.next();
                    String productId = solrIndexDocument.getProductId();
                    GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
                    if (cart.isSalesOrder()) 
                    {
                    	Map priceContext = FastMap.newInstance();
                        // sales order: run the "calculateProductPrice" service
                    	priceContext.put("product", product);
                    	priceContext.put("prodCatalogId", currentCatalogId);
                    	priceContext.put("currencyUomId", cart.getCurrency());
						if(UtilValidate.isNotEmpty(userLogin))
    	                {
		                    priceContext.put("autoUserLogin", userLogin);
		                }
                    	
                    	priceContext.put("webSiteId", webSiteId);
                    	priceContext.put("productStoreId", productStoreId);
                    	priceContext.put("checkIncludeVat", "Y");
                    	priceContext.put("agreementId", cart.getAgreementId());
                    	priceContext.put("productPricePurposeId", "PURCHASE");
                    	if(UtilValidate.isNotEmpty(partyId))
                    	{
                    		priceContext.put("partyId", partyId);
                    	}
                        Map pdpPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
                        solrIndexDocument.setListPrice(((BigDecimal)pdpPriceMap.get("listPrice")).floatValue());
                        solrIndexDocument.setPrice(((BigDecimal)pdpPriceMap.get("price")).floatValue());
                    }
                    newresult.add(solrIndexDocument);
                }
            }
            catch (Exception e) 
            {
                Debug.log(e.getMessage());
				return results;
            }
            return newresult;
		}
        return results;
    }
}
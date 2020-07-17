package common;

import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import com.osafe.control.SeoUrlHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.party.content.PartyContentWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import com.osafe.util.Util;
import org.ofbiz.entity.condition.EntityCondition;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.entity.Delegator;
import com.osafe.services.InventoryServices;
import org.ofbiz.base.util.UtilProperties;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.product.catalog.CatalogWorker;

productId = request.getAttribute("plpItemId");
cart = session.getAttribute("shoppingCart");
plpPdpSelectMultiVariant="";
isPlpPdpInStoreOnly = "N";


//***************************************************VIRTUAL JS FUNCTIONS START*********************************************//
selectOne = UtilProperties.getMessage("OSafeUiLabels", "SelectOneLabel", locale);

//BUILDS A PRODUCT FEATURE TREE

//BUILD JS TO CREATE DROPDOWN FOR ALL SELECTABLE FEATURE EXCEPT FIRST ONE
String buildNext(Map map, List order, String current, String prefix) 
{
    def ct = 0;
    def featureType = null;
    def featureIndex = 0;
    def buf = new StringBuffer();
    buf.append("function listFT" + current + "_" + uiSequenceScreen+"_"+productId + prefix + "() { ");
    
    buf.append("var select = document.createElement('select');");
    
    buf.append("select.setAttribute(\"name\",\"FT"+current + "_" + uiSequenceScreen+"_"+productId+"\");");
    
    buf.append("select.options.length = 1;");
    
    buf.append("select.options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
    
    //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + current + "\"].options.length = 1;");
    
    //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + current + "\"].options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
    map.each { key, value ->
        def optValue = null;

        if (order.indexOf(current) == (order.size()-1)) 
		{
            optValue = value.iterator().next();
        } else {
            optValue = prefix + "_" + ct;
        }
        String selectedFeature = current+":"+key;
        if(parameters.productFeatureType && parameters.productFeatureType == selectedFeature) {
            featureType = current;
            featureIndex = ct;
        }
        buf.append("select.options[" + (ct + 1) + "] = new Option(\"" + OsafeAdminUtil.formatSimpleText(key) + "\",\"" + optValue + "\");");
        //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + current + "\"].options[" + (ct + 1) + "] = new Option(\"" + OsafeAdminUtil.formatSimpleText(key) + "\",\"" + optValue + "\");");
        ct++;
    }
    
    buf.append("var parentDiv = document.getElementById(\"" + uiSequenceScreen+"_"+productId + "\");");
    
    buf.append("var childSelect = jQuery('div#"+uiSequenceScreen+"_"+productId+" select.FT"+current+"_"+uiSequenceScreen+"_"+productId+"');");
    
    buf.append("jQuery(childSelect).html(select.innerHTML);");
    
    buf.append(" }");
    if(map.size()==1)
    {   
        //jsBufDefault.append("getListPlp(\"FT" + current + "\", \""+featureIndex+"\", 1);");
    }
        
    if (order.indexOf(current) < (order.size()-1)) 
	{
        ct = 0;
        map.each { key, value ->
            def nextOrder = order.get(order.indexOf(current)+1);
            def newPrefix = prefix + "_" + ct;
            buf.append(buildNext(value, order, nextOrder, newPrefix));
            ct++;
        }
    }
    return buf.toString();
}

//BUILD JS TO CREATE 'LI' FOR ALL SELECTABLE FEATURE EXCEPT FIRST ONE
String buildNextLi(Map map, List order, String current, String prefix, Map productVariantInventoryMap, Map productVariantProductAttributeMap) 
{
    def ct = 0;
    def featureType = null;
    def featureIndex = null;
    def productFeatureId = null;
    def productFeatureTypeId = null;
    def buf = new StringBuffer();
	
	//If pdpSelectMultiVariant is "NONE" or undefined, then we need to set the VARSTOCK values outside of the function defined under this "if statement" so that the values can be accessed 
	if(UtilValidate.isNotEmpty(map) && (UtilValidate.isNotEmpty(plpPdpSelectMultiVariant) && (plpPdpSelectMultiVariant.equals("QTY") || plpPdpSelectMultiVariant.equals("CHECKBOX"))))
    {
		Map productVariantStockMap = FastMap.newInstance();
	    map.each { key, value ->
		   def optValue = null;
		   def stockClass = "";
		   if (order.indexOf(current) == (order.size()-1))
		   {
			   optValue = value.iterator().next();
			   
			   inventoryLevelMap = productVariantInventoryMap.get(optValue);
			   
			   inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
			   inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
			   inventoryLevel = inventoryLevelMap.get("inventoryLevel");
			   
			   if(inventoryLevel <= inventoryOutOfStockTo)
			   {
				   stockClass = "outOfStock";
			   }
			   else
			   {
				   if(inventoryLevel >= inventoryInStockFrom)
				   {
					   stockClass = "inStock";
				   }
				   else
				   {
					   stockClass = "lowStock";
				   }
			   }
			   productVariantStockMap.put(optValue, stockClass);
		   }
	  }
	  context.productVariantStockMap = productVariantStockMap;
    }
    buf.append("function listLiFT" + current + "_" + uiSequenceScreen+"_"+productId + prefix + "() { ");
    buf.append("jQuery(\"#LiFT" + current + "_" + uiSequenceScreen+"_"+productId+ "\").html(\"\");");
    map.each { key, value ->
        def optValue = null;
        def stockClass = "";
        if (order.indexOf(current) == (order.size()-1)) 
        {
            optValue = value.iterator().next();
            
            inventoryLevelMap = productVariantInventoryMap.get(optValue);
            
            inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
            inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
            inventoryLevel = inventoryLevelMap.get("inventoryLevel");
            
            if(inventoryLevel <= inventoryOutOfStockTo)
            {
                stockClass = "outOfStock";
            }
            else
            {
                if(inventoryLevel >= inventoryInStockFrom)
                {
                    stockClass = "inStock";
                }
                else
                {
                    stockClass = "lowStock";
                }
            }
            buf.append("VARSTOCKPDPITEM['" + optValue + "'] = \"" + stockClass + "\";");
            def plpPdpInStoreOnly = "N";
            variantProductAttribute = productVariantProductAttributeMap.get(optValue);
	        if(UtilValidate.isNotEmpty(variantProductAttribute) && UtilValidate.isNotEmpty(variantProductAttribute.PDP_IN_STORE_ONLY))
	        {
	        	plpPdpInStoreOnly = variantProductAttribute.PDP_IN_STORE_ONLY;
			}
            buf.append("VARINSTOREPDPITEM['" + optValue + "'] = \"" + plpPdpInStoreOnly + "\";");
        } 
        else 
        {
            optValue = prefix + "_" + ct;
        }
        
        String selectedFeature = current+":"+key;
        def selectedClass = false;
        
        if(parameters.productFeatureType)
        {
            productFeatureTypeIdArr = parameters.productFeatureType.split(":");
            productFeatureTypeId = productFeatureTypeIdArr[0];
            if(parameters.productFeatureType == selectedFeature) 
            {
                featureType = current;
                featureIndex = ct;
                selectedClass = true;
            }
        }
        
        if(!parameters.productFeatureType || (UtilValidate.isNotEmpty(productFeatureTypeId) && current != productFeatureTypeId))
        {
            if(map.size()==1)
            {
                selectedClass = true;
            }
        }
        buf.append("var li = document.createElement('li');");
		filteredKey = Util.removeNonAlphaNumeric(key);
        if(selectedClass == true)
        {
            buf.append("li.setAttribute(\"class\",\"selected "+filteredKey+" "+stockClass+"\");");
        } 
        else 
        {
            buf.append("li.setAttribute(\"class\",\""+filteredKey+" "+stockClass+"\");");
        }
        
        liText = "<a href=javascript:void(0); onclick=getListPlp('FT" + current + "_" +uiSequenceScreen+"_"+productId + "','" + ct + "',1,'"+uiSequenceScreen+"_"+productId+"');>" + OsafeAdminUtil.formatSimpleText(key) + "</a>";
        buf.append("jQuery(\"#LiFT" + current + "_" + uiSequenceScreen+"_"+productId + "\").append(li);");
        buf.append("li.innerHTML = \"" + liText + "\";");
        ct++;
    }
    
    buf.append(" }");
    if (order.indexOf(current) < (order.size()-1)) 
	{
        ct = 0;
        map.each { key, value ->
            def nextOrder = order.get(order.indexOf(current)+1);
            def newPrefix = prefix + "_" + ct;
            buf.append(buildNextLi(value, order, nextOrder, newPrefix,productVariantInventoryMap, productVariantProductAttributeMap));
            ct++;
        }
    }
    return buf.toString();
}


Map makeGroup(Delegator delegator, Map<String, List<String>> featureList, List<String> items, List<String> order, int index, Map standardFeatureMap) throws IllegalArgumentException, IllegalStateException
{
        
        Map<String, List<String>> tempGroup = FastMap.newInstance();
        Map<String, Object> group = new LinkedHashMap<String, Object>();
        String orderKey = (String) order.get(index);

        if (featureList == null) 
        {
            throw new IllegalArgumentException("Cannot build feature tree: featureList is null");
        }

        if (index < 0) 
        {
            throw new IllegalArgumentException("Invalid index '" + index + "' min index '0'");
        }
        if (index + 1 > order.size()) 
        {
            throw new IllegalArgumentException("Invalid index '" + index + "' max index '" + (order.size() - 1) + "'");
        }

        // loop through items and make the lists
        for (String thisItem: items) 
        {
            List<GenericValue> features = null;
            features = standardFeatureMap.get(thisItem);
            
            for (GenericValue item: features) 
            {
                String itemKey = item.getString("description");

                if (tempGroup.containsKey(itemKey)) 
                {
                    List<String> itemList = tempGroup.get(itemKey);

                    if (!itemList.contains(thisItem))
                    {
                        itemList.add(thisItem);
                    }
                } 
                else 
                {
                    List<String> itemList = UtilMisc.toList(thisItem);
                    tempGroup.put(itemKey, itemList);
                }
            }
        }

        // Loop through the feature list and order the keys in the tempGroup
        List<String> orderFeatureList = featureList.get(orderKey);

        if (orderFeatureList == null) 
        {
            throw new IllegalArgumentException("Cannot build feature tree: orderFeatureList is null for orderKey=" + orderKey);
        }

        for (GenericValue featureStr: orderFeatureList) 
        {
            if (tempGroup.containsKey(featureStr.getString("description")))
                group.put(featureStr.getString("description"), tempGroup.get(featureStr.getString("description")));
        }

        // no groups; no tree
        if (group.size() == 0) 
        {
            return group;
            throw new IllegalStateException("Cannot create tree from group list; error on '" + orderKey + "'");
        }

        if (index + 1 == order.size()) 
        {
            return group;
        }

        // loop through the keysets and get the sub-groups
        for (String key: group.keySet()) 
        {
            List<String> itemList = UtilGenerics.checkList(group.get(key));

            if (UtilValidate.isNotEmpty(itemList)) 
            {
                Map<String, Object> subGroup = makeGroup(delegator, featureList, itemList, order, index + 1, standardFeatureMap);
                group.put(key, subGroup);
            } 
            else 
            {
                // do nothing, ie put nothing in the Map
                throw new IllegalStateException("Cannot create tree from an empty list; error on '" + key + "'");
            }
        }
        
        return group;
}


//BUILD JS TO CREATE FIRST VARIANT PRODUCT ID MAP
String buildVariantMapJS(Map firstVariantIdMap, Map variantStandardFeatureMap)
{
    def buf = new StringBuffer();
    def featureExist = [];
    buf.append("function getFormOptionVarMap" + uiSequenceScreen+"_"+productId + "() {");
    buf.append("var VARMAP = new Object();");
    firstVariantIdMap.each { key, value ->
        def productVariantStandardFeatures = variantStandardFeatureMap.get(value);
        if(UtilValidate.isNotEmpty(productVariantStandardFeatures))
        {
            for(GenericValue productVariantStandardFeatureAppl : productVariantStandardFeatures)
            {
                def mapKey = "FT"+productVariantStandardFeatureAppl.productFeatureTypeId+"_"+uiSequenceScreen+"_"+productId+"_"+productVariantStandardFeatureAppl.description;
                if(!featureExist.contains(mapKey))
                {
                    buf.append("VARMAP['" + mapKey + "'] = \"" + productVariantStandardFeatureAppl.productId + "\";");
                    featureExist.add(mapKey);
                }
            }
        }
    }
    buf.append("return VARMAP;");
    buf.append("}");
    return buf.toString();
}


String buildSelectableFeatureMapJS(List featureOrder, Map productFeatureAndApplSelectMap)
{
	def buf = new StringBuffer();
	def liBuf = new StringBuffer();
	featureOrder.eachWithIndex 
	{ 
		feature, i ->
		if(i >= 0)
		{
			productFeatureAndApplSelect = productFeatureAndApplSelectMap.get(feature);
			
		    buf.append("function listFT" + feature + "_" + uiSequenceScreen+"_"+productId + "() { ");
		    
		    liBuf.append("function listLiFT" + feature + "_" + uiSequenceScreen+"_"+productId + "() { ");
		    liBuf.append("jQuery(\"#LiFT" + feature + "_" + uiSequenceScreen+"_"+productId+ "\").html(\"\");");
		    
		    
		    buf.append("var select = document.createElement('select');");
		    
		    buf.append("select.setAttribute(\"name\",\"FT"+feature + "_" + uiSequenceScreen+"_"+productId+"\");");
		    
		    buf.append("select.options.length = 1;");
		    
		    buf.append("select.options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
		    
		    //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + feature + "\"].options.length = 1;");
		    
		    //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + feature + "\"].options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
		    def ct = 0;
		    for(GenericValue productFeatureAndAppl : productFeatureAndApplSelect)
		    {
		    	buf.append("select.options[" + (ct + 1) + "] = new Option(\"" + OsafeAdminUtil.formatSimpleText(productFeatureAndAppl.description) + "\",\"" + productFeatureAndAppl.productId + "\");");
		    	//buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + feature + "\"].options[" + (ct + 1) + "] = new Option(\"" + OsafeAdminUtil.formatSimpleText(productFeatureAndAppl.description) + "\",\"" + productFeatureAndAppl.productId + "\");");
		    	
		    	buf.append("select.options[" + (ct + 1) + "] = new Option(\"" + OsafeAdminUtil.formatSimpleText(productFeatureAndAppl.description) + "\",\"" + productFeatureAndAppl.productId + "\");");
		    	
		    	buf.append("select.options[" + (ct + 1) + "].setAttribute(\"disabled\",\"disabled\");");
		    	//buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + feature + "\"].options[" + (ct + 1) + "].setAttribute(\"disabled\",\"disabled\");");
		    	
		    	liBuf.append("var li = document.createElement('li');");
				filteredKey = Util.removeNonAlphaNumeric(OsafeAdminUtil.formatSimpleText(productFeatureAndAppl.description));
				liBuf.append("li.setAttribute(\"class\",\""+filteredKey+"\");");
		    	liText = "<a href=javascript:void(0); onclick=javascript:void(0);>" + OsafeAdminUtil.formatSimpleText(productFeatureAndAppl.description) + "</a>";
		    	liBuf.append("jQuery(\"#LiFT" + feature + "_" + uiSequenceScreen+"_"+productId + "\").append(li);");
		    	liBuf.append("li.innerHTML = \"" + liText + "\";");
		        
		    	ct++;
		    }
		    
		    buf.append("var parentDiv = document.getElementById(\"" + uiSequenceScreen+"_"+productId + "\");");
		    
		    buf.append("var childSelect = jQuery('div#"+uiSequenceScreen+"_"+productId+" select.FT"+feature+"_"+uiSequenceScreen+"_"+productId+"');");

		    buf.append("jQuery(childSelect).html(select.innerHTML);");
		    
		    liBuf.append(" }");
		    buf.append(" }");
		}
    }
	buf.append(liBuf);
	return buf.toString();
}


//BUILD JS TO CREATE DROPDOWN FOR FIRST SELECTABLE FEATURE AND CALL THE FUNCTION TO CREATE DROPDOWN FOR REST FEATURES
firstSelectableFeatureSize = 0;
String buildFeatureJS(List featureOrder, Map variantTree, Map productVariantInventoryMap, Map productVariantProductAttributeMap)
{
    def nextFeatureJsBuf = new StringBuffer();
    def buf = new StringBuffer();
    topLevelName = featureOrder[0];
    buf.append("var VARSTOCKPDPITEM = new Object();");
    buf.append("var VARINSTOREPDPITEM = new Object();");
    
    buf.append("function getFormOption" + uiSequenceScreen+"_"+productId + "() {");
    buf.append("var OPT = new Array(" + featureOrder.size() + ");");
    featureOrder.eachWithIndex { feature, i ->
        buf.append("OPT[" + i + "] = \"FT" + feature + "_" +uiSequenceScreen+"_"+productId + "\";");
    }
    buf.append("return OPT;");
    buf.append("}");
    buf.append("function list" + topLevelName + "_" +uiSequenceScreen+"_"+productId + "() {");
    
    buf.append("var select = document.createElement('select');");
    
    buf.append("select.setAttribute(\"name\",\"FT"+topLevelName + "_" + uiSequenceScreen+"_"+productId +"\");");
    
    buf.append("select.options.length = 1;");
    
    buf.append("select.options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");
    
    //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + topLevelName + "\"].options.length = 1;");
    //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + topLevelName + "\"].options[0] = new Option(\"" + selectOne + "\",\"\",true,true);");

    //COMMENTING OUT THIS CODE FOR NOW, WILL REMOVE WHEN MAKE SURE THAT IT'S NOT AFFECTING ANY FUNCTIONALITY.
    /* featureOrder.each { featureKey ->
            jsBuf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + featureKey + "\"].options.length = 1;");
    } */
                 
    def counter = 0;
    featureCnt = "";

    variantTree.each { key, value ->
        opt = null;
        if (featureOrder.size() == 1) 
        {
            opt = value.iterator().next();
            inventoryLevelMap = productVariantInventoryMap.get(opt);

            inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo");
            inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom");
            inventoryLevel = inventoryLevelMap.get("inventoryLevel");

            if(inventoryLevel <= inventoryOutOfStockTo)
            {
                stockClass = "outOfStock";
            }
            else
            {
                if(inventoryLevel >= inventoryInStockFrom)
                {
                    stockClass = "inStock";
                }
                else
                {
                    stockClass = "lowStock";
                }
            }
            //ADD STOCK LEVEL CLASSES TO THE MAP IF THERE IS ONLY ONE SELECTABLE FEATURE FOR THAT PRODUCT.
            buf.append("VARSTOCKPDPITEM['" + opt + "'] = \"" + stockClass + "\";");
            def plpPdpInStoreOnly = "N";
            variantProductAttribute = productVariantProductAttributeMap.get(opt);
	        if(UtilValidate.isNotEmpty(variantProductAttribute) && UtilValidate.isNotEmpty(variantProductAttribute.PDP_IN_STORE_ONLY))
	        {
	        	plpPdpInStoreOnly = variantProductAttribute.PDP_IN_STORE_ONLY;
			}
            buf.append("VARINSTOREPDPITEM['" + opt + "'] = \"" + plpPdpInStoreOnly + "\";");
        } 
        else 
        {
            opt = counter as String;
        }
        
        buf.append("select.options[" + (counter+1) + "] = new Option(\"" + OsafeAdminUtil.formatSimpleText(key) + "\",\"" + opt + "\");");
        //buf.append("document.forms[\""+uiSequenceScreen+"_"+productId+"\"].elements[\"FT" + topLevelName + "\"].options[" + (counter+1) + "] = new Option(\"" + OsafeAdminUtil.formatSimpleText(key) + "\",\"" + opt + "\");");
        
        productFeatureType = topLevelName+":"+key;
        if(parameters.productFeatureType)
        {
            
            if(parameters.productFeatureType == productFeatureType)
            {
                featureCnt = counter;
            }
        }
        
        //CALLS THE FUNCTION TO CREATE DROPDOWN AND LI FOR REST OF THE SELECTABLE FEATURES.
        varTree = value;
        firstSelectableFeatureSize = varTree.size();
        cnt = "" + counter;
        if (value instanceof Map) 
        {
            nextFeatureJsBuf.append(buildNext(varTree, featureOrder, featureOrder[1], cnt));
            nextFeatureJsBuf.append(buildNextLi(varTree, featureOrder, featureOrder[1], cnt, productVariantInventoryMap, productVariantProductAttributeMap));
        }
        counter++;
    }

    buf.append("var parentDiv = document.getElementById(\"" + uiSequenceScreen+"_"+productId + "\");");
    
    buf.append("var childSelect = jQuery('div#"+uiSequenceScreen+"_"+productId+" select.FT"+topLevelName+"_"+uiSequenceScreen+"_"+productId+"');");

    buf.append("jQuery(childSelect).html(select.innerHTML);");
    
    buf.append("}");

    buf.append(nextFeatureJsBuf);
    
    return buf.toString();
}


//*****************************************************VIRTUAL JS FUNCTIONS END***************************************//




currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
webSiteId = CatalogWorker.getWebSiteId(request);
productStore = ProductStoreWorker.getProductStore(request);
productStoreId = productStore.get("productStoreId");
priceContext = [:];
if (cart.isSalesOrder()) 
{
    // sales order: run the "calculateProductPrice" service
    priceContext = [prodCatalogId : currentCatalogId,
                currencyUomId : cart.getCurrency(), autoUserLogin : autoUserLogin];
    priceContext.webSiteId = webSiteId;
    priceContext.productStoreId = productStoreId;
    priceContext.checkIncludeVat = "Y";
    priceContext.agreementId = cart.getAgreementId();
    priceContext.productPricePurposeId = "PURCHASE";
    priceContext.partyId = cart.getPartyId();  // IMPORTANT: must put this in, or price will be calculated for the CSR instead of the customer
}


if(UtilValidate.isNotEmpty(productId)) 
{
  product = delegator.findOne("Product",UtilMisc.toMap("productId",productId), true);
  if (UtilValidate.isNotEmpty(product)) 
  {
	productName = product.productName;
    categoryId = "";
    productInternalName = product.internalName;
    productFriendlyUrl = "";
    pdpUrl = "";
    productImageUrl = "";
    productImageAltUrl= "";
    price = "";
    listPrice = "";
    recurringPrice = "";
    plpLabel = "";
    plpProductFeatureType = "";
    productLongDesc = "";
	bfTotalInventory = "";
    
    ProductContentWrapper productContentWrapper = new ProductContentWrapper(product, request);
    productStore = ProductStoreWorker.getProductStore(request);
	productStoreId = productStore.get("productStoreId");

	//REMOVE THE LAST PLP ITEM CONTENT FROM CONTEXT
	if (UtilValidate.isNotEmpty(context))
	{
        for (Map.Entry contextEntry : context.entrySet()) 
        {
		   if(contextEntry.getKey().startsWith("PLP_"))
		   {
			   context.remove(contextEntry.getKey());
		   }
        }
	}
	
    if (UtilValidate.isNotEmpty(productContentWrapper))
    {
        Map productContentIdMap = FastMap.newInstance();

        productContentList = product.getRelatedCache("ProductContent");
		productContentList = EntityUtil.filterByDate(productContentList,true);
		if (UtilValidate.isNotEmpty(productContentList))
		{
            for (GenericValue productContent: productContentList) 
            {
    		   productContentTypeId = productContent.productContentTypeId;
    		   //Prefix the Content by "PLP" to distinguish content when groovy file is used in PDP
    		   context.put("PLP_" + productContent.productContentTypeId,productContent.contentId);
               productContentIdMap.put(productContent.productContentTypeId,productContent.contentId);
            }
		}
		
		//GET PRODUCT ATTRIBUTE LIST AND SET INTO LOCAL MAP(productAttrMap)
		productAttr = product.getRelatedCache("ProductAttribute");
		productAttrMap = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(productAttr))
		{
			attrlIter = productAttr.iterator();
			while (attrlIter.hasNext()) 
			{
				attr = (GenericValue) attrlIter.next();
				productAttrMap.put(attr.getString("attrName"),attr.getString("attrValue"));
			}
            if (UtilValidate.isNotEmpty(productAttrMap.get("PDP_SELECT_MULTI_VARIANT")))
            {
            	plpPdpSelectMultiVariant = productAttrMap.get("PDP_SELECT_MULTI_VARIANT");
            }
			if (UtilValidate.isNotEmpty(productAttrMap.get("BF_INVENTORY_TOT")))
			{
				bfTotalInventory = productAttrMap.get("BF_INVENTORY_TOT");
			}
			if (UtilValidate.isNotEmpty(productAttrMap.get("PDP_IN_STORE_ONLY")))
		    {
		    	isPlpPdpInStoreOnly = productAttrMap.get("PDP_IN_STORE_ONLY");
		    }
		}
		context.plpProductAtrributeMap = productAttrMap;
		context.plpPdpSelectMultiVariant = plpPdpSelectMultiVariant;
		
		
		//stock
		inStock = true;
		inventoryMethod = Util.getProductStoreParm(request,"INVENTORY_METHOD");
		if (UtilValidate.isNotEmpty(inventoryMethod) && "BIGFISH".equals(inventoryMethod.toUpperCase()))
		{
			inventoryOutOfStockTo = Util.getProductStoreParm(request,"INVENTORY_OUT_OF_STOCK_TO");
			if (UtilValidate.isNotEmpty(bfTotalInventory) && UtilValidate.isNotEmpty(inventoryOutOfStockTo) && (bfTotalInventory > inventoryOutOfStockTo))
			{
				inStock = true;
			}
			else
			{
				inStock = false;
			}
		}
		context.inStock = inStock;
		
		productContentId = productContentIdMap.get("PRODUCT_NAME");
        if (UtilValidate.isNotEmpty(productContentId))
        {
    	    productName = productContentWrapper.get("PRODUCT_NAME");
        }
	    productContentId = productContentIdMap.get("SMALL_IMAGE_URL");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	productImageUrl = productContentWrapper.get("SMALL_IMAGE_URL");
        }
	    productContentId = productContentIdMap.get("SMALL_IMAGE_ALT_URL");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	productImageAltUrl = productContentWrapper.get("SMALL_IMAGE_ALT_URL");
        }
        productContentId = productContentIdMap.get("PLP_LABEL");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	plpLabel = productContentWrapper.get("PLP_LABEL");
        }
        productContentId = productContentIdMap.get("LONG_DESCRIPTION");
        if (UtilValidate.isNotEmpty(productContentId))
        {
        	productLongDesc = productContentWrapper.get("LONG_DESCRIPTION");
        	productLongDesc = StringEscapeUtils.unescapeHtml(productLongDesc.toString());
     	    productLongDesc = productLongDesc;
        }
        
        
    }
    
    priceContext.put("product", product);
    plpVirtualPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
    
    price = plpVirtualPriceMap.price;
    listPrice = plpVirtualPriceMap.listPrice;

    //Not useing the calculateProductPrice service on purpose.
    //Instead just pulling the pricing directly from DB for plp type item displays (Complement, Accessory and  Recently Viewed)
    /*virtualProductPrices = delegator.findByAndCache("ProductPrice", UtilMisc.toMap("productId", productId, "currencyUomId", cart.getCurrency(), "productStoreGroupId", "_NA_","productPricePurposeId","PURCHASE"), UtilMisc.toList("-fromDate"));
    virtualProductPrices = EntityUtil.filterByDate(virtualProductPrices, true);
    if (UtilValidate.isNotEmpty(virtualProductPrices))
    {
        for(GenericValue virtualProductPrice : virtualProductPrices)
        {
          if (virtualProductPrice.productPriceTypeId == "DEFAULT_PRICE")
          {
        	    price = plpVirtualPriceMap.price;
                continue;
          }
          if (virtualProductPrice.productPriceTypeId == "LIST_PRICE")
          {
        	    listPrice = plpVirtualPriceMap.listPrice;
                continue;
          }
        }
    	
    }*/

    //CHECK if the virtual product has RECURRING price.
    //Instead just pulling the pricing directly from DB for plp type item displays (Complement, Accessory and  Recently Viewed)
    recurringProductPrices = delegator.findByAndCache("ProductPrice", UtilMisc.toMap("productId", productId, "currencyUomId", cart.getCurrency(), "productStoreGroupId", "_NA_","productPricePurposeId","RECURRING_CHARGE"), UtilMisc.toList("-fromDate"));
    recurringProductPrices = EntityUtil.filterByDate(recurringProductPrices, true);
    if (UtilValidate.isNotEmpty(recurringProductPrices))
    {
    	
        for(GenericValue recurringProductPrice : recurringProductPrices)
        {
          if (recurringProductPrice.productPriceTypeId == "DEFAULT_PRICE")
          {
        	    recurringPrice = recurringProductPrice.price;
                break;
          }
        }
    	
    }

    //CHECK WE HAVE A DEFAULT PRODUCT CATEGORY THE PRODUCT IS MEMBER OF
    if (UtilValidate.isEmpty(categoryId))
    {
        productCategoryMemberList = product.getRelatedCache("ProductCategoryMember");
        productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
	    productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList,UtilMisc.toList("sequenceNum"));
        if(UtilValidate.isNotEmpty(productCategoryMemberList))
        {
            productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
            categoryId = productCategoryMember.productCategoryId; 
        }    
    }
    
    //GET PRODUCT RATINGS AND REVIEWS
    decimals=Integer.parseInt("1");
    rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
	reviewMethod = Util.getProductStoreParm(request, "REVIEW_METHOD");
	reviewSize=0;
	averageCustomerRating="";
	if(UtilValidate.isNotEmpty(reviewMethod))
	{
	    if(reviewMethod.equalsIgnoreCase("BIGFISH"))
	    {
	        // get the average rating
		    productCalculatedInfos = product.getRelatedCache("ProductCalculatedInfo");
		    if (UtilValidate.isNotEmpty(productCalculatedInfos))
		    {
				productCalculatedInfo = EntityUtil.getFirst(productCalculatedInfos);
		        averageRating= productCalculatedInfo.getBigDecimal("averageCustomerRating");
		        if (UtilValidate.isNotEmpty(averageRating) && averageRating > 0)
		        {
		 	       averageCustomerRating= averageRating.setScale(1,rounding);
		        }
		    }
	        
	        reviews = product.getRelatedCache("ProductReview");
	        if (UtilValidate.isNotEmpty(reviews))
	        {
	            reviews = EntityUtil.filterByAnd(reviews, UtilMisc.toMap("statusId", "PRR_APPROVED", "productStoreId", productStoreId));
	            reviewSize=reviews.size();
	        }
	        
	    }
	}

    //GET PRODUCT CONTENT LIST AND SET INTO CONTEXT
    Map productContentIdMap = FastMap.newInstance();
	productContentList = product.getRelatedCache("ProductContent");
	productContentList = EntityUtil.filterByDate(productContentList,true);
	if (UtilValidate.isNotEmpty(productContentList))
	{
        for (GenericValue productContent: productContentList) 
        {
		   productContentTypeId = productContent.productContentTypeId;
		    //Prefix the Content by "PLP" to distinguish content when groovy file is used in PDP
		   context.put("PLP_" + productContent.productContentTypeId,productContent.contentId);
           productContentIdMap.put(productContent.productContentTypeId,productContent.contentId);
        }
	}
    
    plpLabelContent = productContentIdMap.get("PLP_LABEL");
	if (UtilValidate.isNotEmpty(plpLabelContent))
	{
	    	plpLabel = productContentWrapper.get("PLP_LABEL");
	}
	

   
	productFriendlyUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
    pdpUrl = 'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId;
    
    // GET PRODUCT FEATURE AND APPLS: DISTINGUISHING FEATURES
    // Using findByAndCache Call since the ProductService(Service getProductVariantTree call) will make the same findByAndCache Call.
	//Issue 38934, 38916 - Check for duplicate feature descriptions
    productDistinguishingFeatures = FastList.newInstance();
    Map distinguishingFeatureExistsMap = FastMap.newInstance();
    distinguishingFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"), UtilMisc.toList("sequenceNum"));
    distinguishingFeatures = EntityUtil.filterByDate(distinguishingFeatures, true);
    
    for (GenericValue distinguishingFeature : distinguishingFeatures)
    {
        String featureDescription = distinguishingFeature.description;
        if (UtilValidate.isNotEmpty(featureDescription)) 
        {
        	featureDescription = featureDescription.toUpperCase();
            if (!distinguishingFeatureExistsMap.containsKey(featureDescription))
            {
            	productDistinguishingFeatures.add(distinguishingFeature);
            	distinguishingFeatureExistsMap.put(featureDescription,featureDescription);
            }
        }
    }

    // GET PRODUCT FEATURE AND APPLS : DISTINGUISHING FEATURES BY FEATURE TYPE
    productFeatureTypes = FastList.newInstance();
    productFeaturesByType = new LinkedHashMap();
    for (GenericValue feature: productDistinguishingFeatures) 
    {
        featureType = feature.getString("productFeatureTypeId");
        if (!productFeatureTypes.contains(featureType)) 
        {
            productFeatureTypes.add(featureType);
        }
        features = productFeaturesByType.get(featureType);
        if (UtilValidate.isEmpty(features)) 
        {
            features = FastList.newInstance();
            productFeaturesByType.put(featureType, features);
        }
        features.add(feature);
    }
    
    // GET PRODUCT FEATURE TYPE
    Map plpProductFeatureTypesMap = FastMap.newInstance();
    productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, true);
    if(UtilValidate.isNotEmpty(productFeatureTypesList))
    {
        for (GenericValue productFeatureType : productFeatureTypesList)
        {
        	plpProductFeatureTypesMap.put(productFeatureType.productFeatureTypeId,productFeatureType.description);
        }
        
    }

    List productSelectableFeatureAndAppl = FastList.newInstance();
    List productVariantFeatureList = FastList.newInstance();
    List productAssoc= FastList.newInstance();
    
    Map productVariantContentWrapperMap = FastMap.newInstance();
    Map productVariantProductContentIdMap = FastMap.newInstance();
    Map productVariantProductAttributeMap = FastMap.newInstance();
    Map productFeatureFirstVariantIdMap = FastMap.newInstance();
    Map productFeatureDataResourceMap = FastMap.newInstance();

    featureValueSelected = request.getAttribute("featureValueSelected");
    productFeatureSelectVariantId="";
    productFeatureSelectVariantProduct = "";
    
    if(UtilValidate.isNotEmpty((product).isVirtual) && ((product).isVirtual).toUpperCase()== "Y")
    {
   	
    	Map plpProductVariantPriceMap = FastMap.newInstance();
    	Map descpFeatureGroupDescMap = FastMap.newInstance();
        variantFirstFeatureIdExist = [];
        facetGroupVariantMatch = request.getAttribute("FACET_GROUP_VARIANT_MATCH");
        plpFacetGroupVariantSticky = request.getAttribute("PLP_FACET_GROUP_VARIANT_STICKY");
        plpFacetGroupVariantSwatch = request.getAttribute("PLP_FACET_GROUP_VARIANT_SWATCH");
    	plpFacetGroupVariantMatch = Util.getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_MATCH");
    	
        productAssoc = product.getRelatedCache("MainProductAssoc");
        productAssoc = EntityUtil.filterByDate(productAssoc,true);
        productAssoc = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId","PRODUCT_VARIANT"));
	    productAssoc = EntityUtil.orderBy(productAssoc,UtilMisc.toList("sequenceNum"));

        if (UtilValidate.isNotEmpty(productAssoc)) 
        {
        	PLP_FACET_GROUP_VARIANT_MATCH = Util.getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_MATCH");
        	
            for(GenericValue pAssoc : productAssoc)
            {
		      productIdTo = pAssoc.productIdTo;
              isSellableVariant = ProductWorker.isSellable(delegator, productIdTo);
	          if (isSellableVariant)
		      {
                assocVariantProduct = pAssoc.getRelatedOneCache("AssocProduct");
                variantProductFeatureAndAppls = assocVariantProduct.getRelatedCache("ProductFeatureAndAppl");
                variantProductFeatureAndAppls = EntityUtil.filterByDate(variantProductFeatureAndAppls,true);
  	            variantProductFeatureAndAppls = EntityUtil.orderBy(variantProductFeatureAndAppls,UtilMisc.toList("sequenceNum"));
  	            
  	            priceContext.put("product", assocVariantProduct);
	            
	            plpPriceMap = dispatcher.runSync("calculateProductPrice", priceContext);
  	            
                /*variantProductPrices = assocVariantProduct.getRelatedCache("ProductPrice");
                variantProductPrices = EntityUtil.filterByDate(variantProductPrices,true);*/
	            
                //BULLD PRODUCT CONTENT WRAPPER FOR EACH VARIANT TO PUT INTO CONTEXT
                varProductContentWrapper = new ProductContentWrapper(assocVariantProduct, request);
                productVariantContentWrapperMap.put(assocVariantProduct.productId, varProductContentWrapper);

                varBasePrice = plpPriceMap.price;
                plpProductVariantPriceMap.put(assocVariantProduct.productId, UtilMisc.toMap("basePrice", varBasePrice));
                
                varListPrice = plpPriceMap.listPrice;
                varPriceMap = plpProductVariantPriceMap.get(assocVariantProduct.productId);
                if (UtilValidate.isNotEmpty(varPriceMap)) 
                {
                    varPriceMap.put("listPrice", varListPrice)
                } 
                else 
                {
                	plpProductVariantPriceMap.put(assocVariantProduct.productId, UtilMisc.toMap("listPrice", varListPrice));
                }
                
                /*for(GenericValue variantProductPrice : variantProductPrices)
	            {
	              if (variantProductPrice.productPriceTypeId == "DEFAULT_PRICE")
	              {
	                	varBasePrice = variantProductPrice.price;
	                    varPriceMap = plpProductVariantPriceMap.get(variantProductPrice.productId);
	                    if (UtilValidate.isNotEmpty(varPriceMap)) 
	                    {
	                        varPriceMap.put("basePrice", varBasePrice)
	                    } 
	                    else 
	                    {
	                    	plpProductVariantPriceMap.put(variantProductPrice.productId, UtilMisc.toMap("basePrice", varBasePrice))
	                    }
	                    continue;
	              }
	              if (variantProductPrice.productPriceTypeId == "LIST_PRICE")
	              {
		            	varListPrice = variantProductPrice.price;
		                varPriceMap = plpProductVariantPriceMap.get(variantProductPrice.productId);
		                if (UtilValidate.isNotEmpty(varPriceMap)) 
		                {
		                    varPriceMap.put("listPrice", varListPrice)
		                } 
		                else 
		                {
		                	plpProductVariantPriceMap.put(variantProductPrice.productId, UtilMisc.toMap("listPrice", varListPrice))
		                }
		                continue;
	              }
	            }*/
                for (GenericValue variantProductFeatureAndAppl: variantProductFeatureAndAppls)
                {
		            if (UtilValidate.isNotEmpty(PLP_FACET_GROUP_VARIANT_MATCH)) 
		            {
	                   if (variantProductFeatureAndAppl.productFeatureTypeId == PLP_FACET_GROUP_VARIANT_MATCH && variantProductFeatureAndAppl.productFeatureApplTypeId == "DISTINGUISHING_FEAT")
	                    {
	            		          descpFeatureGroupDescMap.put(variantProductFeatureAndAppl.productId, variantProductFeatureAndAppl.description);
	            		          continue;
	                    }
	                }
	                
                    if (variantProductFeatureAndAppl.productFeatureApplTypeId == "STANDARD_FEATURE")
                    {
			             productFeatureDesc = "";
			             if(UtilValidate.isNotEmpty(variantProductFeatureAndAppl.description))
			             {
			                 productFeatureDesc = variantProductFeatureAndAppl.description;
			             }
			             productVariantFeatureMap = UtilMisc.toMap("productVariantId", productIdTo,"productVariant", assocVariantProduct, "productFeatureId", variantProductFeatureAndAppl.productFeatureId,"productFeatureDesc", productFeatureDesc,"productFeatureTypeId", variantProductFeatureAndAppl.productFeatureTypeId);
			
			             variantPriceMap = plpProductVariantPriceMap.get(productIdTo);
			             
			             if (UtilValidate.isNotEmpty(variantPriceMap))
			             {
							if (UtilValidate.isNotEmpty(variantPriceMap.get("basePrice")))
							{
								productVariantFeatureMap.put("basePrice", variantPriceMap.get("basePrice"));
							}
			             }
			             else 
			             {
			                 productVariantFeatureMap.put("basePrice", price);
			             }
			             
			             if (UtilValidate.isNotEmpty(variantPriceMap))
			             {
							if (UtilValidate.isNotEmpty(variantPriceMap.get("listPrice")))
							{
			                    productVariantFeatureMap.put("listPrice", variantPriceMap.get("listPrice"));
							}
			             }
			             else 
			             {
			                 productVariantFeatureMap.put("listPrice", listPrice);
			             }
			
			             descpFeatureGroupDesc = descpFeatureGroupDescMap.get(productIdTo);
			             if (UtilValidate.isNotEmpty(descpFeatureGroupDesc))
			             {
			                 productVariantFeatureMap.put("descriptiveFeatureGroupDesc", descpFeatureGroupDesc);
			             }
			             else 
			             {
			                 productVariantFeatureMap.put("descriptiveFeatureGroupDesc", "");
			             }
			
			             productVariantFeatureList.add(productVariantFeatureMap);

	    			    //CREATE A MAP FOR First PRODUCT FEATURE ID AND VARIANT PRODUCT ID
			            //This is only needed if swatches are displayed and is used in plpSwatch.ftl
			            //(featureId, variantIdMap)
			     	    if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
			     	    {
			        		if(!variantFirstFeatureIdExist.contains(variantProductFeatureAndAppl.productFeatureId))
	                        {
	                    		productVariantContentList = assocVariantProduct.getRelatedCache("ProductContent");
	                    		productVariantContentList = EntityUtil.filterByDate(productVariantContentList,true);
	                    		if (UtilValidate.isNotEmpty(productVariantContentList))
	                    		{
	                                Map variantProductContentMap = FastMap.newInstance();
	                                for (GenericValue productContent: productVariantContentList) 
	                                {
	                        		   productContentTypeId = productContent.productContentTypeId;
	                        		   variantProductContentMap.put(productContent.productContentTypeId,productContent.contentId);
	                                }
	                                productVariantProductContentIdMap.put(assocVariantProduct.productId,variantProductContentMap);
	                                
	                    		}
			        			
	                            productFeatureFirstVariantIdMap.put(variantProductFeatureAndAppl.productFeatureId, productVariantFeatureMap); 
	                            variantFirstFeatureIdExist.add(variantProductFeatureAndAppl.productFeatureId);
	                        }
			     	    }
			             
                    }
                    
				    if(UtilValidate.isNotEmpty(featureValueSelected) && UtilValidate.isNotEmpty(facetGroupVariantMatch))
				    {
			            if (UtilValidate.isEmpty(productFeatureSelectVariantId))
			            {
	                       if (variantProductFeatureAndAppl.productFeatureTypeId == facetGroupVariantMatch && variantProductFeatureAndAppl.description == featureValueSelected)
	                       {
				              for(Map productVariantFeatureMap : productVariantFeatureList)
				              {
				                if (productVariantFeatureMap.productVariantId == pAssoc.productIdTo && productVariantFeatureMap.productFeatureTypeId == plpFacetGroupVariantSticky)
				                {
							        productFeatureSelectVariantId = productVariantFeatureMap.productVariantId;
							        productFeatureSelectVariantProduct = productVariantFeatureMap.productVariant;
							        featureValueSelected=productVariantFeatureMap.productFeatureDesc;
				                    break;
				                }
				              }
	                       }
			            }
				    }
	            }
                
                //GET PRODUCT VARIANT ATTRIBUTE LIST AND SET INTO CONTEXT
                productVariantProductAttributeMap.put("virtualProduct", productAttrMap);
				assocProductAttr = assocVariantProduct.getRelatedCache("ProductAttribute");
				assocProductAttrMap = FastMap.newInstance();
				if (UtilValidate.isNotEmpty(assocProductAttr))
				{
					assocAttrlIter = assocProductAttr.iterator();
					while (assocAttrlIter.hasNext()) {
						assocAttr = (GenericValue) assocAttrlIter.next();
						assocProductAttrMap.put(assocAttr.getString("attrName"),assocAttr.getString("attrValue"));
					}
					productVariantProductAttributeMap.put(assocVariantProduct.productId,assocProductAttrMap);
				}
				
		      }
            }
        }
    
        
        
        
	    if (UtilValidate.isNotEmpty(plpFacetGroupVariantSwatch))
	    {
	      productSelectableFeatureAndAppl = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId",productId, "productFeatureTypeId", plpFacetGroupVariantSwatch, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), UtilMisc.toList("sequenceNum"));
	      productSelectableFeatureAndAppl = EntityUtil.filterByDate(productSelectableFeatureAndAppl,true);
	      
          //BUILD CONTEXT MAP FOR PRODUCT_FEATURE_DATA_RESOURCE (productFeatureId, objectInfo)
          productFeatureDataResourceList = delegator.findList("ProductFeatureDataResource", EntityCondition.makeCondition(["featureDataResourceTypeId" : "PLP_SWATCH_IMAGE_URL"]), null, ["productFeatureId"], null, true);
          if (UtilValidate.isNotEmpty(productFeatureDataResourceList))
          {
              for (GenericValue productFeatureDataResource : productFeatureDataResourceList)
              {
              	dataResource = productFeatureDataResource.getRelatedOneCache("DataResource");
                  if(UtilValidate.isNotEmpty(dataResource.objectInfo))
                  {
                  	productFeatureDataResourceMap.put(productFeatureDataResource.productFeatureId,dataResource.objectInfo);
                  }
              }
          	
          }
	      
	      if(UtilValidate.isEmpty(productFeatureSelectVariantId) && UtilValidate.isNotEmpty(productSelectableFeatureAndAppl))
	      {
		        if(UtilValidate.isNotEmpty(productVariantFeatureList) && UtilValidate.isNotEmpty(plpFacetGroupVariantSticky))
		        {
		          for(GenericValue productFeatureAppls : productSelectableFeatureAndAppl)
		          {
		              firstSelectableFeatureId = productFeatureAppls.productFeatureId;
		          
		              for(Map productVariantFeatureMap : productVariantFeatureList)
		              {
		                if (productVariantFeatureMap.productFeatureId == firstSelectableFeatureId && productVariantFeatureMap.productFeatureTypeId == plpFacetGroupVariantSticky)
		                {
					        productFeatureSelectVariantId = productVariantFeatureMap.productVariantId;
					        productFeatureSelectVariantProduct = productVariantFeatureMap.productVariant;
					        featureValueSelected=productVariantFeatureMap.productFeatureDesc;
		                    break;
		                }
		              }
		              if(UtilValidate.isNotEmpty(productFeatureSelectVariantId))
		              {
		                  break;
		              }
	              }
		        }
	      }
	    }
    
	  if(UtilValidate.isNotEmpty(productFeatureSelectVariantId))
	  {
	        productVariantSelectSmallURL = "";
	        productVariantSelectSmallAltURL = "";
	        productVariantSelectContentWrapper = ProductContentWrapper.makeProductContentWrapper(productFeatureSelectVariantProduct, request);
	        if (UtilValidate.isNotEmpty(productVariantSelectContentWrapper))
	        {
	            productContentList = delegator.findByAndCache("ProductContent", UtilMisc.toMap("productId",productFeatureSelectVariantId, "productContentTypeId", "SMALL_IMAGE_URL"));
	            productContentList = EntityUtil.filterByDate(productContentList,true);
        		if (UtilValidate.isNotEmpty(productContentList))
        		{
        			productContent = EntityUtil.getFirst(productContentList);
    		  	    productContentId = productContent.contentId;
    		        if (UtilValidate.isNotEmpty(productContentId))
    		        {
    			        productVariantSelectSmallURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_URL");

    		            productContentList = delegator.findByAndCache("ProductContent", UtilMisc.toMap("productId",productFeatureSelectVariantId, "productContentTypeId", "SMALL_IMAGE_ALT_URL"));
    		            productContentList = EntityUtil.filterByDate(productContentList,true);
    	        		if (UtilValidate.isNotEmpty(productContentList))
    	        		{
    	        			productContent = EntityUtil.getFirst(productContentList);
            		  	    productContentId = productContent.contentId;
        			        if (UtilValidate.isNotEmpty(productContentId))
        			        {
             			        productVariantSelectSmallAltURL = productVariantSelectContentWrapper.get("SMALL_IMAGE_ALT_URL");
        			        }
    	        			
    	        		}
    		        	
    		        }
        			
        		}
	        	
	        }
	        if(UtilValidate.isNotEmpty(featureValueSelected))
	        {
	   	        if (UtilValidate.isNotEmpty(plpFacetGroupVariantSticky))
	   	        {
	        	    plpProductFeatureType = plpFacetGroupVariantSticky;
	        	    productFriendlyUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId+'&productFeatureType='+plpFacetGroupVariantSticky+':'+featureValueSelected);
	   	        }
	   	        else
	   	        {
	   	        	productFriendlyUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId='+productId+'&productCategoryId='+categoryId);
	   	        }
	        }
	        if(UtilValidate.isNotEmpty(productVariantSelectSmallURL))
	        {
	        	productImageUrl = productVariantSelectSmallURL;
	        }
	        if(UtilValidate.isNotEmpty(productVariantSelectSmallAltURL))
	        {
	        	productImageAltUrl = productVariantSelectSmallAltURL;
	        }
	        
	        if(UtilValidate.isNotEmpty(productVariantFeatureList))
	        {
	            for(Map productVariantFeature : productVariantFeatureList)
	            {
	               if(productVariantFeature.productVariantId == productFeatureSelectVariantId)
	               {
	                   price = productVariantFeature.basePrice;
	                   listPrice = productVariantFeature.listPrice; 
	               }
	            }
	        }
	    }
	    context.plpProductVariantPriceMap = plpProductVariantPriceMap;
    }
	
	//check if it is a finished good
	isFinishedGood = false;
	if(UtilValidate.isNotEmpty(product.isVirtual) && "N".equals(product.isVirtual) && UtilValidate.isNotEmpty(product.isVariant) && "N".equals(product.isVariant))
	{
		isFinishedGood = true;
	}
	context.isFinishedGood = isFinishedGood;
	
	
	
	
//************************* STRAT CREATING PLP VARIANT VIRTUAL JS***********************************//
    
	context.plpVariantTree = null;
    plpFeatureOrder = [];
    // Special Variant Code
    List productFeatureAndApplSelectList = FastList.newInstance();
    Map plpProductFeatureAndApplSelectMap = FastMap.newInstance();
    Map plpProductVariantStandardFeatureMap = FastMap.newInstance();
    Map plpProductVariantProductMap = FastMap.newInstance();
    Map plpProductVariantInventoryMap = FastMap.newInstance();
    Map plpProductFeatureFirstVariantIdMap = FastMap.newInstance();
    jsBuf = new StringBuffer();
    jsBufDefault = new StringBuffer();
    context.plpVirtualJavaScript = jsBuf;
    context.plpVirtualDefaultJavaScript = jsBufDefault;
    context.plpProductVariantProductMap = plpProductVariantProductMap;
    context.plpProductVariantMapKeys = plpProductVariantProductMap.keySet();
    context.plpProductVariantInventoryMap = plpProductVariantInventoryMap;
    context.plpProductFeatureAndApplSelectMap = plpProductFeatureAndApplSelectMap;
    context.plpProductVariantStandardFeatureMap = plpProductVariantStandardFeatureMap;
    context.plpProductFeatureFirstVariantIdMap = plpProductFeatureFirstVariantIdMap;
    if (UtilValidate.isNotEmpty((product).isVirtual) && ((product).isVirtual).toUpperCase()== "Y") 
    {
        if ("VV_FEATURETREE".equals(product.getString("virtualVariantMethodEnum"))) 
        {
            context.plpFeatureLists = ProductWorker.getSelectableProductFeaturesByTypesAndSeq(product);
        } 
        else 
        {
            // GET PRODUCT FEATURE AND APPLS: SELECTABLE FEATURES
            // Using findByAndCache Call since the ProductService(Service getProductVariantTree call) will make the same findByAndCache Call.
        	//Issue 38934, 38916 - Check for duplicate feature descriptions
            productSelectableFeatures = FastList.newInstance();
            Map selectableFeatureExistsMap = FastMap.newInstance();
            selectableFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", product.productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), UtilMisc.toList("sequenceNum"));
            selectableFeatures = EntityUtil.filterByDate(selectableFeatures, true);
            for (GenericValue selectableFeature : selectableFeatures)
            {
                String featureDescription = selectableFeature.description;
                if (UtilValidate.isNotEmpty(featureDescription)) 
                {
                	featureDescription = featureDescription.toUpperCase();
	                if (!selectableFeatureExistsMap.containsKey(featureDescription))
	                {
		            	productSelectableFeatures.add(selectableFeature);
		            	selectableFeatureExistsMap.put(featureDescription,featureDescription);
	                }
                }
            }
        
            //BUILD PRODUCT FEATURE SET (SELECTABLE FEATURES BY FEATURE TYPE)
            plpFeatureSet = new LinkedHashSet();
            plpFeatureSetAll = new LinkedHashSet();
            if (UtilValidate.isNotEmpty(productSelectableFeatures))
            {
                for (GenericValue prodSelectableFeatureAndAppl : productSelectableFeatures)
                {
                	plpFeatureSet.add(prodSelectableFeatureAndAppl.productFeatureTypeId);
                }
            }
            orderBy = ["sequenceNum"];
            
            if (UtilValidate.isNotEmpty(plpFeatureSet)) 
            {
                context.plpFeatureSet = plpFeatureSet;
                for(String plpFeatureType : plpFeatureSet)
                {
                	plpFeatureSetAll.add(plpFeatureType);
                }
                String lastProductFeatureTypeId="";
                

                
                if (UtilValidate.isNotEmpty(categoryId)) 
                {
                	categoryId = StringUtil.replaceString(categoryId,"/","");
                    productCategory =  delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId",categoryId), true);
                }
                //IF PRODUCT CATEGORY BUILD THE FEATURE SET BASED ON THE PRODUCT_FEATURE_CAT_GRP_APPL (SEQUENCE)
                if(UtilValidate.isNotEmpty(productCategory))
                {
                    productFeatureCatGroupAppls = productCategory.getRelatedCache("ProductFeatureCatGrpAppl");
                    //Commenting out since the thru date might be set to hide from the facet group but we do NOT
                    //want to remove from PDP
                    //productFeatureCatGroupAppls = EntityUtil.filterByDate(productFeatureCatGroupAppls,true);
                    productFeatureCatGroupAppls = EntityUtil.orderBy(productFeatureCatGroupAppls,UtilMisc.toList("sequenceNum"));

                    if(UtilValidate.isNotEmpty(productFeatureCatGroupAppls))
                    {
                        featureCategorySet = new LinkedHashSet();
                        for (GenericValue productFeatureCatGroupAppl : productFeatureCatGroupAppls)
                        {
                            if (plpFeatureSet.contains(productFeatureCatGroupAppl.productFeatureGroupId))
                            {
                                 featureCategorySet.add(productFeatureCatGroupAppl.productFeatureGroupId);
                            }
                        }
                        if (UtilValidate.isNotEmpty(featureCategorySet))
                        {
                        	plpFeatureSet.clear();
                        	plpFeatureSet.addAll(featureCategorySet);
                        }
                    }
                    
                }

                for(String plpFeatureType : plpFeatureSetAll)
                {
                	if(!plpFeatureSet.contains(plpFeatureType))
                	{
                		plpFeatureSet.add(plpFeatureType);
                	}
                }
                //CREATE A MAP FOR CONTEXT OF PRODUCT FEATURE AND APPL: SELECTABLE FEATURES
                for(GenericValue productFeatureAndAppl : productSelectableFeatures) 
                {
                   String productFeatureTypeId = productFeatureAndAppl.productFeatureTypeId;
                   if (!plpProductFeatureAndApplSelectMap.containsKey(productFeatureTypeId))
                   {
                       productFeatureAndApplSelectList = FastList.newInstance();
                   }
                   else
                   {
                       productFeatureAndApplSelectList = plpProductFeatureAndApplSelectMap.get(productFeatureTypeId);
                   }
                   productFeatureAndApplSelectList.add(productFeatureAndAppl);
                   plpProductFeatureAndApplSelectMap.put(productFeatureTypeId,productFeatureAndApplSelectList);
                }

                

                //GET PRODUCT ASSOCIATE PRODUCT: PRODUCT_VARIANT
                productAssocVariant = EntityUtil.filterByAnd(productAssoc, UtilMisc.toMap("productAssocTypeId","PRODUCT_VARIANT"));
				context.plpVariantProductAssocList = productAssocVariant;
				
				
                List<String> items = FastList.newInstance();
                variantFeatureIdExist = [];

                if (UtilValidate.isNotEmpty(productAssocVariant)) 
                {
                    //BUILD PRODUCT VARIANT MAPS FOR CONTENT
                    for(GenericValue pAssoc : productAssocVariant) 
                    {
                        //GET ASSOCIATED PRODUCT (VARIANT) 
                        assocVariantProduct = pAssoc.getRelatedOneCache("AssocProduct");
                        
                        if(ProductWorker.isSellable(assocVariantProduct))
                        {
                            //BULLD PRODUCT MAP FOR EACH VARIANT TO PUT INTO CONTEXT
                        	plpProductVariantProductMap.put(assocVariantProduct.productId, assocVariantProduct);

                            //GET VARIANT INVENTORY MAP
                            inventoryLevelMap = InventoryServices.getProductInventoryLevel(assocVariantProduct.productId, request);
                            plpProductVariantInventoryMap.put(assocVariantProduct.productId, inventoryLevelMap);
                            
                            variantProductFeatureAndAppls = assocVariantProduct.getRelatedCache("ProductFeatureAndAppl");
                            variantProductFeatureAndAppls = EntityUtil.filterByDate(variantProductFeatureAndAppls,true);
                            variantProductFeatureAndAppls = EntityUtil.orderBy(variantProductFeatureAndAppls,UtilMisc.toList("sequenceNum"));
                            
                            // GET VARIANT PRODUCT FEATURE AND APPLS: DISTINGUISHING FEATURES
                            productVariantDistinguishingFeatures = EntityUtil.filterByAnd(variantProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "DISTINGUISHING_FEAT"));
                            // GET VARIANT PRODUCT FEATURE AND APPLS: STANDARD FEATURES
                            productVariantStandardFeatures = EntityUtil.filterByAnd(variantProductFeatureAndAppls, UtilMisc.toMap("productFeatureApplTypeId", "STANDARD_FEATURE"));
                            plpProductVariantStandardFeatureMap.put(assocVariantProduct.productId, productVariantStandardFeatures);

                            //CREATE A MAP FOR PRODUCT FEATURE ID AND FIRST SELECTED VARIANT PRODUCT ID
                            if (UtilValidate.isNotEmpty(productVariantStandardFeatures))
                            {
                                for(GenericValue productVariantStandardFeatureAppl : productVariantStandardFeatures)
                                {
                                    if(!variantFeatureIdExist.contains(productVariantStandardFeatureAppl.productFeatureId))
                                    {
                                    	plpProductFeatureFirstVariantIdMap.put(productVariantStandardFeatureAppl.productFeatureId, productVariantStandardFeatureAppl.productId); 
                                        variantFeatureIdExist.add(productVariantStandardFeatureAppl.productFeatureId);
                                    }
                                }    
                            }
                            //ADD VARIANT PRODUCTS ID TO ITEMS LIST THAT IS USED TO PREPARE THE VARIANT TREE.
                            items.add(pAssoc.getString("productIdTo"));
                            
                        }
                    
                    }
                    //PREPARING THE VARIANT TREE
                    plpFeatureOrder = UtilMisc.makeListWritable(UtilGenerics.checkCollection(plpFeatureSet));
                    
                    if (plpFeatureOrder) 
                    {
                        context.plpFeatureOrderFirst = plpFeatureOrder[0];
                    }
                    plpVariantTree = null;
                    try
                    {
                    	plpVariantTree = makeGroup(delegator, plpProductFeatureAndApplSelectMap, items, plpFeatureOrder, 0, plpProductVariantStandardFeatureMap);
                    }
                    catch(Exception e) 
                    {
                        Debug.logError("Exception in Creating Product Variant Tree" + e.getMessage(), "eCommerceProductDetail.groovy");
                    }
                    if (UtilValidate.isNotEmpty(plpVariantTree)) 
                    {
                        context.plpVariantTree = plpVariantTree;
                    }
                    
                    //START - CRAETING VIRTUAL JS
                    jsBuf = new StringBuffer();
                    jsBuf.append("<script language=\"JavaScript\" type=\"text/javascript\">");
                    
                    //CALL THE FUNCTION TO CREATE JS FOR PRODUCT FEATURE VARIANT MAP
                    if(UtilValidate.isNotEmpty(plpProductFeatureFirstVariantIdMap))
                    {
                        jsBuf.append(buildVariantMapJS(plpProductFeatureFirstVariantIdMap, plpProductVariantStandardFeatureMap));
                    }
                    
                    //CALL THE FUNCTION TO CREATE JS FOR ALL SELECTABLE FEATURES
                    if(UtilValidate.isNotEmpty(plpProductFeatureAndApplSelectMap))
                    {
                        jsBuf.append(buildSelectableFeatureMapJS(plpFeatureOrder, plpProductFeatureAndApplSelectMap));
                    }
                    
                    
                    // CALL THE FUNCTION TO CREATE DROPDOWN FOR ALL SELECTABLE FEATURE, LI FOR ALL SELECTABLE FEATURE EXCEPT FIRST.
                    if(UtilValidate.isNotEmpty(plpVariantTree))
                    {
                        jsBuf.append(buildFeatureJS(plpFeatureOrder, plpVariantTree, plpProductVariantInventoryMap, productVariantProductAttributeMap));
                    }
                    
                    jsBuf.append("</script>");

                    // ADD getListPlp() FUNCTION CALL TO DEFAULT JS BUFFER SO THAT WHEN THE PAGE IS LOAD THE FIRST VALUE IS DEFAULT SELECTED FOR THE FIRST FEATURE.
                    jsBufDefault = new StringBuffer();
                    jsBufDefault.append("<script language=\"JavaScript\" type=\"text/javascript\">jQuery(document).ready(function(){");
                    if(UtilValidate.isNotEmpty(plpVariantTree) && (UtilValidate.isEmpty(plpPdpSelectMultiVariant) || (!plpPdpSelectMultiVariant.equals("QTY") && !plpPdpSelectMultiVariant.equals("CHECKBOX"))))
                    {
                    	if(plpVariantTree.size() == 1 || featureCnt != "")
                    	{
                    		jsBufDefault.append("getListPlp(\"FT" + topLevelName + "_" + uiSequenceScreen+"_"+productId + "\", \""+featureCnt+"\", 1,'"+uiSequenceScreen+"_"+productId+"');");
                    	}
                    }
                    jsBufDefault.append("  });</script>");
                    //END - CRAETING VIRTUAL JS
                    
                    // PUT VARIANT MAPS INTO CONTEXT
                    context.plpVirtualJavaScript = jsBuf;
                    context.plpVirtualDefaultJavaScript = jsBufDefault;
                    context.plpProductVariantProductMap = plpProductVariantProductMap;
                    context.plpProductVariantMapKeys = plpProductVariantProductMap.keySet();
                    context.plpProductVariantInventoryMap = plpProductVariantInventoryMap;
                    context.plpProductFeatureAndApplSelectMap = plpProductFeatureAndApplSelectMap;
                    context.plpProductVariantStandardFeatureMap = plpProductVariantStandardFeatureMap;
                    context.plpProductFeatureFirstVariantIdMap = plpProductFeatureFirstVariantIdMap;
                }
            }
        }
    }
    context.plpFeatureOrder = plpFeatureOrder;
    //************************************ END CREATING PLP VARIANT VIRTUAL JS**********************************//
	
	
	
	
	plpItemQtyInCart = 0;
	//get current qty in cart
	if(UtilValidate.isNotEmpty(cart))
	{
		shoppingCartItems = cart.items();
		if(UtilValidate.isNotEmpty(shoppingCartItems))
		{
			for (ShoppingCartItem shoppingCartItem : shoppingCartItems)
			{
				cartLineProductId = shoppingCartItem.getProductId();
				if(cartLineProductId.equals(productId))
				{
					plpItemQtyInCart = shoppingCartItem.getQuantity();
				}
			}
		}
	}
    
	//Get Manufacturer info
	partyManufacturer=product.getRelatedOneCache("ManufacturerParty");
	if (UtilValidate.isNotEmpty(partyManufacturer))
	{
	  context.plpManufacturerPartyId = partyManufacturer.partyId;
	  PartyContentWrapper partyContentWrapper = new PartyContentWrapper(partyManufacturer, request);
	  context.plpManufacturerPartyContentWrapper = partyContentWrapper;
	  context.plpManufacturerDescription = partyContentWrapper.get("DESCRIPTION");
	  context.plpManufacturerProfileName = partyContentWrapper.get("PROFILE_NAME");
	  context.plpManufacturerProfileImageUrl = partyContentWrapper.get("PROFILE_IMAGE_URL");
	}
	

    context.plpProductName = productName;
    context.plpProductId = productId;
    context.plpCategoryId = categoryId;
    context.plpProductInternalName = productInternalName;
    context.plpProductImageUrl = productImageUrl;
    context.plpPrice=price;
    context.plpListPrice = listPrice;
    context.plpRecurringPrice = recurringPrice;
    context.plpProductContentWrapper = productContentWrapper;
	context.plpLabel = plpLabel;
    context.plpPdpUrl = pdpUrl;
    context.plpProductFriendlyUrl = productFriendlyUrl;
    context.plpProductImageAltUrl = productImageAltUrl;
    context.featureValueSelected = featureValueSelected;
    context.plpLongDescription = productLongDesc;

    //Ratings
    context.decimals = decimals;
    context.rounding = rounding;
    context.plpAverageStarRating = averageCustomerRating;
    context.plpReviewSize = reviewSize;

    context.plpProductFeatureType = plpProductFeatureType;
    context.plpProductFeatureDataResourceMap = productFeatureDataResourceMap;
    context.plpProductSelectableFeatureAndAppl = productSelectableFeatureAndAppl;
    context.plpProductVariantContentWrapperMap = productVariantContentWrapperMap;
    context.plpProductFeatureFirstVariantIdMap = productFeatureFirstVariantIdMap;
    context.plpProductVariantProductContentIdMap = productVariantProductContentIdMap;
    context.plpProductVariantProductAttributeMap = productVariantProductAttributeMap;
    context.plpDisFeatureTypesList = productFeatureTypes;
    context.plpDisFeatureByTypeMap = productFeaturesByType;
    context.plpProductFeatureTypesMap = plpProductFeatureTypesMap;
	context.plpItemQtyInCart = plpItemQtyInCart;
	context.plpProduct = product;
	context.isPlpPdpInStoreOnly = isPlpPdpInStoreOnly;
  }
}
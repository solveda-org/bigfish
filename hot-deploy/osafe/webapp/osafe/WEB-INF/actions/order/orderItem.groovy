package common;

import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastMap;
import javolution.util.FastList;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;
import com.osafe.util.Util;
import org.ofbiz.base.util.UtilMisc;
import com.osafe.control.SeoUrlHelper;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import com.osafe.services.InventoryServices;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import com.osafe.services.CategoryServices;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.apache.commons.lang.StringEscapeUtils;
import org.ofbiz.order.order.*;

shoppingCart = session.getAttribute("shoppingCart");
rowOrderItem = request.getAttribute("orderItem");
localOrderReadHelper = request.getAttribute("localOrderReadHelper");
orderHeader = request.getAttribute("orderHeader");
rowClass = request.getAttribute("rowClass");
lineIndex = request.getAttribute("lineIndex");
shipGroupAssoc = request.getAttribute("shipGroupAssoc");
shipGroup = request.getAttribute("shipGroup");

productStoreCatalogList  = CatalogWorker.getStoreCatalogs(request);
String topCategoryId = null;
autoUserLogin = request.getSession().getAttribute("autoUserLogin");
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
currencyUom = Util.getProductStoreParm(request,"CURRENCY_UOM_DEFAULT");
virtualProduct="";
productBuyable = "true";
shipDate="";
carrierPartyGroupName="";
shipMethodDescription="";
trackingURL="";
trackingNumber="";
BigDecimal price = BigDecimal.ZERO;
displayPrice="";
offerPrice="";
orderStatus="";
orderItemStatus="";
BigDecimal shipQty = BigDecimal.ZERO;
BigDecimal itemTotal = BigDecimal.ZERO;
	
	

if (UtilValidate.isNotEmpty(rowOrderItem))
{
	productId = rowOrderItem.productId;
	product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
	urlProductId = productId;
	if (UtilValidate.isEmpty(orderHeader))
	{
		orderHeader= delegator.findOne("OrderHeader", [orderId : rowOrderItem.orderId], true);
		localOrderReadHelper = new OrderReadHelper(orderHeader);
		currencyUom = localOrderReadHelper.getCurrency();
	}

	orderStatus = orderHeader.getRelatedOneCache("StatusItem");
	if (UtilValidate.isNotEmpty(rowOrderItem.statusId))
	{
		orderItemStatus = delegator.findOne("StatusItem",[statusId : rowOrderItem.statusId],true);
		
	}
	

	if (UtilValidate.isNotEmpty(productStoreCatalogList))
	{
		productStoreCatalogList  = EntityUtil.filterByDate(productStoreCatalogList, true);
		gvProductStoreCatalog = EntityUtil.getFirst(productStoreCatalogList);
		String prodCatalogId = gvProductStoreCatalog.prodCatalogId;
		topCategoryId = CatalogWorker.getCatalogTopCategoryId(request, prodCatalogId);
	}
	
	productCategoryId = rowOrderItem.productCategoryId;
	if(UtilValidate.isEmpty(productCategoryId))
	{
		productCategoryId = product.primaryProductCategoryId;
	}
	if(UtilValidate.isEmpty(productCategoryId))
	{
		productCategoryMemberList = product.getRelatedCache("ProductCategoryMember");
		productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
		productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList, UtilMisc.toList('sequenceNum'));
		if(UtilValidate.isNotEmpty(productCategoryMemberList))
		{
			productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
			productCategoryId = productCategoryMember.productCategoryId;
		}
	}
	if(UtilValidate.isNotEmpty(product.isVariant) && "Y".equals(product.isVariant))
	{
		virtualProduct = ProductWorker.getParentProduct(productId, delegator);
		urlProductId = virtualProduct.productId;
		if(UtilValidate.isEmpty(productCategoryId))
		{
			productCategoryMemberList = virtualProduct.getRelatedCache("ProductCategoryMember");
			productCategoryMemberList = EntityUtil.filterByDate(productCategoryMemberList,true);
			productCategoryMemberList = EntityUtil.orderBy(productCategoryMemberList, UtilMisc.toList('sequenceNum'));
			if(UtilValidate.isNotEmpty(productCategoryMemberList))
			{
				productCategoryMember = EntityUtil.getFirst(productCategoryMemberList);
				productCategoryId = productCategoryMember.productCategoryId;

			}
		}
	}

	//Product Image URL
	productImageUrl = ProductContentWrapper.getProductContentAsText(product, "SMALL_IMAGE_URL", locale, dispatcher);
	if(UtilValidate.isEmpty(productImageUrl) && UtilValidate.isNotEmpty(virtualProduct))
	{
		productImageUrl = ProductContentWrapper.getProductContentAsText(virtualProduct, "SMALL_IMAGE_URL", locale, dispatcher);
	}
	//If the string is a literal "null" make it an "" empty string then all normal logic can stay the same
	if(UtilValidate.isNotEmpty(productImageUrl) && "null".equals(productImageUrl))
	{
		productImageUrl = "";
	}

	//Product Name
	productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher);
	if(UtilValidate.isEmpty(productName) && UtilValidate.isNotEmpty(virtualProduct))
	{
		productName = ProductContentWrapper.getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher);
	}

	priceContext = [product : product, prodCatalogId : currentCatalogId,
	                currencyUomId : currencyUom, autoUserLogin : autoUserLogin];
	    priceContext.webSiteId = webSiteId;
	    priceContext.productStoreId = productStoreId;
	    priceContext.checkIncludeVat = "Y";
	    priceContext.agreementId = shoppingCart.getAgreementId();
	    priceContext.partyId = shoppingCart.getPartyId();  // IMPORTANT: must put this in, or price will be calculated for the CSR instead of the customer
	    //priceContext.findAllQuantityPrices="Y";
	    priceMap = dispatcher.runSync("calculateProductPrice", priceContext);

	currentPrice = priceMap.price;
	price = rowOrderItem.unitPrice;
    if(UtilValidate.isNotEmpty(shipGroupAssoc))
    {
        shipQty = shipGroupAssoc.quantity;
    	
    }
    orderItemAdjustment = localOrderReadHelper.getOrderItemAdjustmentsTotal(rowOrderItem);
    if (orderItemAdjustment < 0)
    {
      offerPrice = (rowOrderItem.unitPrice + (orderItemAdjustment/rowOrderItem.quantity));
	  itemTotal = offerPrice * shipQty;
    }
	else
	{
	  itemTotal = price * shipQty;
	}


	//BUILD CONTEXT MAP FOR PRODUCT_FEATURE_TYPE_ID and DESCRIPTION(EITHER FROM PRODUCT_FEATURE_GROUP OR PRODUCT_FEATURE_TYPE)
	Map productFeatureTypesMap = FastMap.newInstance();
	productFeatureTypesList = delegator.findList("ProductFeatureType", null, null, null, null, true);

	//get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
	productFeatureGroupList = delegator.findList("ProductFeatureGroup", null, null, null, null, true);
	productFeatureGroupAndApplList = delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, true);
	productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);

	if(UtilValidate.isNotEmpty(productFeatureTypesList))
	{
		for (GenericValue productFeatureType : productFeatureTypesList)
		{
			//filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
			productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.productFeatureTypeId));
			description = "";
			if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
			{
				productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
				productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.productFeatureGroupId));
				productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
				description = productFeatureGroup.description;
			}
			else
			{
				description = productFeatureType.description;
			}
			productFeatureTypesMap.put(productFeatureType.productFeatureTypeId,description);
		}
		
	}

	//product features
	//Issue 38934, 38916 - Check for duplicate feature descriptions
	productFeatureAndAppls = FastList.newInstance();
	Map standardFeatureExistsMap = FastMap.newInstance();
	standardFeatures = delegator.findByAndCache("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "STANDARD_FEATURE"), UtilMisc.toList("sequenceNum"));
	standardFeatures = EntityUtil.filterByDate(standardFeatures,true);
	standardFeatures = EntityUtil.orderBy(standardFeatures,UtilMisc.toList('sequenceNum'));

	for (GenericValue standardFeature : standardFeatures)
	{
	    String featureDescription = standardFeature.description;
	    if (UtilValidate.isNotEmpty(featureDescription)) 
	    {
	    	featureDescription = featureDescription.toUpperCase();
	        if (!standardFeatureExistsMap.containsKey(featureDescription))
	        {
	        	productFeatureAndAppls.add(standardFeature);
	        	standardFeatureExistsMap.put(featureDescription,featureDescription);
	        }
	    }
	}

	productFriendlyUrl = SeoUrlHelper.makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId='+urlProductId+'&productCategoryId='+productCategoryId+'');
	
	//Check Product Availability
	virtualProductId = ProductWorker.getVariantVirtualId(product);
	buyableProductId = productId;
	if (UtilValidate.isNotEmpty(virtualProductId))
	{
	    buyableProductId = virtualProductId;
	}
	//Checked that Product is available in Product Category Member
	productCategoryMembers = delegator.findByAndCache("ProductCategoryMember", UtilMisc.toMap("productId", buyableProductId));
	productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers);
	if(UtilValidate.isNotEmpty(productCategoryMembers))
	{
		//Checked that Product Category is Exist in Unexpired categories List.
		List<Map<String, Object>> allUnexpiredCategories = CategoryServices.getRelatedCategories(delegator, topCategoryId, null, true, false, true);
		boolean isCategoryAlive = false;
		for(GenericValue productCategoryMember : productCategoryMembers)
		{
			productCategory = productCategoryMember.getRelatedOneCache("ProductCategory");
			for (Map<String, Object> workingCategoryMap : allUnexpiredCategories) 
			{
				workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
				if((workingCategory.productCategoryId).equals(productCategory.productCategoryId))
				{
					isCategoryAlive = true;
					break;
				}
			}
		}
		if(isCategoryAlive)
		{
			//Checked the Product Into and Disco Date.
			if(ProductWorker.isSellable(delegator, productId))
			{
				//Checked that Product is availabe in Stock.
				inventoryLevelMap = InventoryServices.getProductInventoryLevel(productId, request);
				if(inventoryLevelMap.get("inventoryLevel") <= inventoryLevelMap.get("inventoryLevelOutOfStockTo"))
				{
					productBuyable = "false";
				}
			}
			else
			{
				productBuyable = "false";
			}	
		}
		else
		{
			productBuyable = "false";
		}
		
	}
	else
	{
		productBuyable = "false";
	}
	if(productBuyable == 'true')
	{
		request.setAttribute("displayReOrderItemButton", "true");
	}
	
	//SHIP GROUP INFORMATION
	  if (UtilValidate.isNotEmpty(shipGroup))
	  {
	      shipDate = "";
	      if(UtilValidate.isNotEmpty(orderHeader) && (orderHeader.statusId == "ORDER_COMPLETED" || rowOrderItem.statusId == "ITEM_COMPLETED"))
	      {
	          shipDate = shipGroup.estimatedShipDate;
	      }
	      trackingNumber = shipGroup.trackingNumber;
	      findCarrierShipmentMethodMap = UtilMisc.toMap("shipmentMethodTypeId", shipGroup.shipmentMethodTypeId, "partyId", shipGroup.carrierPartyId,"roleTypeId" ,"CARRIER");
	      carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap);
		  if (UtilValidate.isNotEmpty(carrierShipmentMethod))
		  {
			  shipmentMethodType = carrierShipmentMethod.getRelatedOneCache("ShipmentMethodType");
		      shipMethodDescription = shipmentMethodType.description;
		      carrierPartyGroupName = "";
		      if (shipGroup.carrierPartyId != "_NA_")
		      {
		          carrierParty = carrierShipmentMethod.getRelatedOneCache("Party");
		          carrierPartyGroup = carrierParty.getRelatedOneCache("PartyGroup");
		          carrierPartyGroupName = carrierPartyGroup.groupName;
		          trackingURLPartyContents = delegator.findByAndCache("PartyContent",UtilMisc.toMap("partyId", shipGroup.carrierPartyId, "partyContentTypeId", "TRACKING_URL"));
		          if (UtilValidate.isNotEmpty(trackingURLPartyContents))
		          {
		              trackingURLPartyContent = EntityUtil.getFirst(trackingURLPartyContents);
		              if (UtilValidate.isNotEmpty(trackingURLPartyContent))
		              {
		                  content = trackingURLPartyContent.getRelatedOneCache("Content");
		                  if (UtilValidate.isNotEmpty(content))
		                  {
		                      dataResource = content.getRelatedOneCache("DataResource");
		                      if (UtilValidate.isNotEmpty(dataResource))
		                      {
		                          electronicText = dataResource.getRelatedOneCache("ElectronicText");
		                          trackingURL = electronicText.textData;
		                          if (UtilValidate.isNotEmpty(trackingURL))
		                          {
		                              trackingURL = FlexibleStringExpander.expandString(trackingURL,UtilMisc.toMap("TRACKING_NUMBER", trackingNumber));
		                          }
		                      }
		                  }
		              }
		          }
		      }
		  }
	  }
	  
    pdpQtyMinAttributeValue = "";
    pdpQtyMaxAttributeValue = "";
    if(UtilValidate.isNotEmpty(productId))
    {
	    productAttrPdpQtyMin = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId",productId,"attrName","PDP_QTY_MIN"), true);
	    productAttrPdpQtyMax = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId",productId,"attrName","PDP_QTY_MAX"), true);
	    if(UtilValidate.isNotEmpty(productAttrPdpQtyMin) && UtilValidate.isNotEmpty(productAttrPdpQtyMax))
	    {
		    pdpQtyMinAttributeValue = productAttrPdpQtyMin.attrValue;
		    pdpQtyMaxAttributeValue = productAttrPdpQtyMax.attrValue;
	    }
    }

      
    context.orderHeader = orderHeader;
    context.orderItem = rowOrderItem;
  	context.localOrderReadHelper = localOrderReadHelper;
	context.productImageUrl = productImageUrl;
	context.IMG_SIZE_CART_H = Util.getProductStoreParm(request,"IMG_SIZE_CART_H");
	context.IMG_SIZE_CART_W = Util.getProductStoreParm(request,"IMG_SIZE_CART_W");
	context.productFriendlyUrl = productFriendlyUrl;
	context.urlProductId = urlProductId;
	context.productName = productName;
	context.wrappedProductName = StringUtil.wrapString(productName);
	context.productFeatureAndAppls = productFeatureAndAppls;
	context.productFeatureTypesMap = productFeatureTypesMap;
	context.currencyUom = currencyUom;
	context.quantityOrdered = rowOrderItem.quantity;
	context.lineIndex = lineIndex;
	context.rowClass = rowClass;
	context.orderDate = rowOrderItem.orderDate;
	context.priceMap = priceMap;
	context.price = price;
	context.offerPrice = offerPrice;
	context.displayPrice = price;
	context.productId = productId;
	context.productCategoryId = productCategoryId;
	context.shoppingCart = shoppingCart; 
	context.productBuyable = productBuyable;	
	context.shipDate = shipDate;
	context.carrierPartyGroupName = carrierPartyGroupName;
	context.shipMethodDescription = shipMethodDescription;
	context.trackingURL = trackingURL;
	context.trackingNumber = trackingNumber;
	context.orderStatus = orderStatus;
	context.orderItemStatus = orderItemStatus;
	context.productBuyable = productBuyable;
	context.pdpQtyMinAttributeValue = pdpQtyMinAttributeValue;
	context.pdpQtyMaxAttributeValue = pdpQtyMaxAttributeValue;
	context.shipQty=shipQty;
	context.itemTotal=itemTotal;
}






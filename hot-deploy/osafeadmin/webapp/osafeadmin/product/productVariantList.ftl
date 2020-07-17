<input type="hidden" name="productId" id="productId" maxlength="20" value="${product.productId!""}"/>
<#if resultList?exists && resultList?has_content>
<table class="osafe" cellspacing="0">
  <thead>
  <tr class="heading">
    <th class="actionColSmall firstCol">&nbsp;</th>
    <th class="idCol">${uiLabelMap.VariantProductIdLabel}</th>
    <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
    <th class="dateCol">${uiLabelMap.IntroDateLabel}</th>
    <th class="dateCol">${uiLabelMap.DiscoDateLabel}</th>
    <th class="dollarCol">${uiLabelMap.ListPriceLabel}</th>
    <th class="dollarCol">${uiLabelMap.SalePriceLabel}</th>
    <th class="actionCol"></th>
    <th class="qtyCol">${uiLabelMap.BFInventoryTotalLabel}</th>
    <th class="qtyCol">${uiLabelMap.BFInventoryWarehouseLabel}</th>
    <#list resultList as variantProduct>
      <#assign variantProdDetail = variantProduct.getRelatedOne("AssocProduct")>
        <#assign productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", {"productId" : (variantProduct.productIdTo)?if_exists, "productFeatureApplTypeId", "STANDARD_FEATURE"}, Static["org.ofbiz.base.util.UtilMisc"].toList("productFeatureTypeId"))/>
        <#if productFeatureAndAppls?exists && productFeatureAndAppls?has_content>
          <#list productFeatureAndAppls as productFeatureAndAppl>
            <#assign featureType = ""/>
            <#if productFeatureTypesMap?has_content>
              <#assign featureType = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
            </#if>
            <#assign curProductFeatureType = productFeatureAndAppl.getRelatedOne("ProductFeatureType")>
            <th class="statusCol">${featureType}</th>
          </#list>
        </#if>
      <#break>
    </#list>
  </tr>
  </thead>
  <tbody>
    <#assign rowClass = "1"/>
    <#assign productListPrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, product.productId, "LIST_PRICE")!/>
    <#assign productDefaultPrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, product.productId, "DEFAULT_PRICE")!/>
    <#list resultList as variantProduct>
      <#assign variantProdDetail = variantProduct.getRelatedOne("AssocProduct")>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="actionColSmall firstCol">
            <input type="checkbox" class="checkBoxEntry" name="variantProductId" id="variantProductId" value="${variantProduct.productIdTo?if_exists}"/>
        </td>
        <td class="idCol <#if !variantProduct_has_next?if_exists>lastRow</#if>" ><a href="<@ofbizUrl>variantProductDetail?productId=${variantProduct.productIdTo?if_exists}</@ofbizUrl>">${(variantProduct.productIdTo)?if_exists}</a></td>
        <td class="nameCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">${(variantProdDetail.internalName)?if_exists}</td>
        <td class="dateCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">${(variantProdDetail.introductionDate?string(preferredDateFormat))!""}</td>
        <td class="dateCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">${(variantProdDetail.salesDiscontinuationDate?string(preferredDateFormat))!""}</td>
        <#assign variantProductPriceInfo = "">
        <#assign productVariantListPrice =  Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, variantProdDetail.productId, "LIST_PRICE")!>
        <#if productVariantListPrice?has_content>
	    	<#assign listPrice = productVariantListPrice.price!"" />
	    	<#assign pricesMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("variantListPrice", globalContext.currencySymbol+""+listPrice, "productListPrice", globalContext.currencySymbol+""+productListPrice.price!"")>
	    	<#assign variantProductPriceInfo = variantProductPriceInfo + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","VariantOverridePriceInfo",pricesMap, locale )+"</p>">
	    <#else>
	    	<#assign listPrice = productListPrice.price!"" />
	    	<#assign pricesMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productListPrice", globalContext.currencySymbol+""+productListPrice.price!"")>
	    	<#assign variantProductPriceInfo = variantProductPriceInfo + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","VirtualListPriceInfo",pricesMap, locale )+"</p>">
	  	</#if>
        <td class="dollarCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">
        	<#if productListPrice?has_content>
	          <@ofbizCurrency amount=listPrice isoCode=productListPrice.currencyUomId rounding=globalContext.currencyRounding/>
	        </#if>
        </td>
        <#assign productVariantSalePrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, variantProdDetail.productId, "DEFAULT_PRICE")!>
        <#if productVariantSalePrice?has_content>
	    	<#assign defaultPrice = productVariantSalePrice.price!"" />
	    	<#assign pricesMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("variantSalePrice", globalContext.currencySymbol+""+defaultPrice, "virtualSalePrice", globalContext.currencySymbol+""+productDefaultPrice.price!)>
	    	<#assign variantProductPriceInfo = variantProductPriceInfo + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","VariantOverrideDefaultPriceInfo",pricesMap, locale )+"</p>">
	    <#else>
	    	<#assign defaultPrice = productDefaultPrice.price!"" />
	    	<#assign pricesMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("virtualSalePrice", globalContext.currencySymbol+""+productDefaultPrice.price)>
	    	<#assign variantProductPriceInfo = variantProductPriceInfo + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","VirtualDefaultPriceInfo",pricesMap, locale )+"</p>">
	  	</#if>
        <td class="dollarCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">
        	<#if productListPrice?has_content>
	          <@ofbizCurrency amount=defaultPrice isoCode=productListPrice.currencyUomId rounding=globalContext.currencyRounding/>
	        </#if>
        </td>
        <td class="actionCol">
        	<p onMouseover="showTooltip(event,'${variantProductPriceInfo}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></p>
        </td> 
        <#assign bfTotalInventoryProductAttribute = delegator.findOne("ProductAttribute", {"productId" : variantProduct.productIdTo, "attrName" : "BF_INVENTORY_TOT"}, false)?if_exists/> 
        <#if bfTotalInventoryProductAttribute?exists>
          <#assign bfTotalInventory = bfTotalInventoryProductAttribute.attrValue!>
        </#if>
        
        <#assign bfWHInventoryProductAttribute = delegator.findOne("ProductAttribute", {"productId" : variantProduct.productIdTo, "attrName" : "BF_INVENTORY_WHS"}, false)?if_exists/> 
        <#if bfWHInventoryProductAttribute?exists>
          <#assign bfWHInventory = bfWHInventoryProductAttribute.attrValue!>
        </#if>
        <td class="qtyCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">${bfTotalInventory!}</td>
        <td class="qtyCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">${bfWHInventory!}</td>
        <#assign productFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", {"productId" : (variantProduct.productIdTo)?if_exists, "productFeatureApplTypeId", "STANDARD_FEATURE"}, Static["org.ofbiz.base.util.UtilMisc"].toList("productFeatureTypeId"))/>
        <#if productFeatureAndAppls?exists && productFeatureAndAppls?has_content>
          <#list productFeatureAndAppls as productFeatureAndAppl>
            <td class="statusCol <#if !variantProduct_has_next?if_exists>lastRow</#if>">${(productFeatureAndAppl.get("description",locale))?if_exists}</td>
          </#list>
        </#if>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  </tbody>
</table>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>
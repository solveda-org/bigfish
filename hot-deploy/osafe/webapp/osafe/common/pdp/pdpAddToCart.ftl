<li class="${request.getAttribute("attributeClass")!}">
  <#if pdpSelectMultiVariant?exists && pdpSelectMultiVariant?has_content && ((pdpSelectMultiVariant.toUpperCase() == "QTY") || (pdpSelectMultiVariant.toUpperCase() == "CHECKBOX")) >
    <div class="multiVariant">
       <input type="hidden" name="add_category_id" value="${parameters.add_category_id!productCategoryId!}" /> 
       <a href="javascript:addMultiItemsToCart('${pdpSelectMultiVariant}');" class="standardBtn addToCart" id="addMultiToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
    </div>
  <#else>
    <#if (currentProduct.isVirtual?if_exists?upper_case == "N" && currentProduct.isVariant?if_exists?upper_case == "N")>
      <#if (inventoryLevel?number gt inventoryOutOfStockTo?number)>
        <input type="hidden" name="add_product_id" value="${currentProduct.productId!}" />
      </#if>
    </#if>
    <input type="hidden" name= "add_category_id" id="add_category_id" value="${parameters.add_category_id!productCategoryId!}" /> 
    <div id="js_addToCart_div">
      <#if inStock>
        <a href="javascript:void(0);" onClick="javascript:addItemToCart();" <#if (featureOrder?exists && featureOrder?size gt 0) || (isPdpInStoreOnly?exists && isPdpInStoreOnly == "Y")>class="standardBtn addToCart inactiveAddToCart"<#else>class="standardBtn addToCart"</#if> id="js_addToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
      <#elseif !isSellable>
        <a href="javascript:void(0);" class="standardBtn addToCart inactiveAddToCart" id="js_addToCart"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
      <#else>
        <span>${uiLabelMap.OutOfStockLabel}</span>
      </#if>
    </div>
  </#if>
  <#-- If product has a PDP_QTY_MIN or PDP_QTY_MAX to override system parameter, then use these values for validation -->
  <#if currentProduct.isVirtual?if_exists?upper_case == "Y">
	  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
	    <#list productVariantMapKeys as key>
	      <#assign productAttrPdpQtyMin = ""/>
	      <#assign productAttrPdpQtyMax = ""/>
	      <#if productVariantProductAttributeMap.get(key)?has_content>
	      	<#assign variantAttributeMap = productVariantProductAttributeMap.get(key)!""/>
	      </#if>
	      <#if variantAttributeMap?has_content>
	      	<#assign productAttrPdpQtyMin = variantAttributeMap.get("PDP_QTY_MIN")!""/>
	      	<#assign productAttrPdpQtyMax = variantAttributeMap.get("PDP_QTY_MAX")!""/>
	      	<#assign productAttrPdpQtyDefault = variantAttributeMap.get("PDP_QTY_DEFAULT")!""/>
	      </#if>
	      <#if productAttrPdpQtyMin?has_content && productAttrPdpQtyMax?has_content>
	        <input type="hidden" name="productAttrPdpQtyMin_${key}" id="js_pdpQtyMinAttributeValue_${key}" value="${productAttrPdpQtyMin!}"/>
	        <input type="hidden" name="productAttrPdpQtyMax_${key}" id="js_pdpQtyMaxAttributeValue_${key}" value="${productAttrPdpQtyMax!}"/>
	      </#if>
	      <#if productAttrPdpQtyDefault?has_content>
	      	<input type="hidden" name="productAttrPdpQtyDefault_${key}" id="js_pdpQtyDefaultAttributeValue_${key}" value="${productAttrPdpQtyDefault!}"/>
	      </#if>
	    </#list>
	  </#if>
  <#else>
	  <#if productId?has_content>
	    <#assign productAttrPdpQtyMin = productAtrributeMap.get('PDP_QTY_MIN')!/>
	    <#assign productAttrPdpQtyMax = productAtrributeMap.get('PDP_QTY_MAX')!/>
	    <#assign productAttrPdpQtyDefault = productAtrributeMap.get("PDP_QTY_DEFAULT")!""/>
	    <#if productAttrPdpQtyMin?has_content &&  productAttrPdpQtyMax?has_content>
	      <input type="hidden" name="productAttrPdpQtyMin_${productId}" id="js_pdpQtyMinAttributeValue_${productId}" value="${productAttrPdpQtyMin}"/>
	      <input type="hidden" name="productAttrPdpQtyMax_${productId}" id="js_pdpQtyMaxAttributeValue_${productId}" value="${productAttrPdpQtyMax}"/>
	    </#if>
	    <#if productAttrPdpQtyDefault?has_content>
	      <input type="hidden" name="productAttrPdpQtyDefault_${productId}" id="js_pdpQtyDefaultAttributeValue_${productId}" value="${productAttrPdpQtyDefault!}"/>
	    </#if>
	  </#if>
  </#if>
</li>


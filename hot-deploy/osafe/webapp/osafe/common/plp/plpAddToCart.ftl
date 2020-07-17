<li class="${request.getAttribute('attributeClass')!}">
    <#if plpProduct.isVirtual?if_exists?upper_case == "N" && plpProduct.isVariant?if_exists?upper_case == "N">
        <input type="hidden" name="${uiSequenceScreen}_${plpProduct.productId}_add_product_id" id="${uiSequenceScreen}_${plpProduct.productId}_add_product_id" value="${plpProduct.productId!}" />
    </#if>
    <input type="hidden" name="${uiSequenceScreen}_${plpProduct.productId}_add_category_id" id="${uiSequenceScreen}_${plpProduct.productId}_add_category_id" value="${parameters.plp_add_category_id!plpCategoryId!}" /> 
    <input type="hidden" name="${uiSequenceScreen}_${plpProduct.productId}_add_product_name" id="${uiSequenceScreen}_${plpProduct.productId}_add_product_name" value="${plpProductName!}" /> 
    <div id="js_plpAddtoCart_div_${uiSequenceScreen}_${plpProduct.productId}">
      <#if inStock>
          <label>${uiLabelMap.CartItemAddToCartButtonCaption}</label>
          <a href="javascript:void(0);" onClick="javascript:addItemPlpToCart('${uiSequenceScreen}_${plpProduct.productId}');" class="standardBtn addToCart <#if plpFeatureOrder?has_content && plpFeatureOrder?size gt 0 || (isPlpPdpInStoreOnly?exists && isPlpPdpInStoreOnly == "Y")>inactiveAddToCart</#if>" id="js_plpAddtoCart_${uiSequenceScreen}_${plpProduct.productId}"><span>${uiLabelMap.OrderAddToCartBtn}</span></a>
      <#else>
          <span>${uiLabelMap.OutOfStockLabel}</span>
      </#if>
    </div>
    <div class="js_plpPdpInStoreOnlyContent" id="js_plpPdpInStoreOnlyLabel_${uiSequenceScreen}_${plpProduct.productId}" <#if !(isPdpInStoreOnly?exists && isPdpInStoreOnly == "Y")>style="display:none;"</#if>>
        <span>${uiLabelMap.InStoreOnlyLabel}</span>
    </div>
</li>

<#-- If product has a PDP_QTY_MIN or PDP_QTY_MAX to override system parameter, then use these values for validation -->
<#if plpProduct.isVirtual?if_exists?upper_case == "Y">
    <#if plpProductVariantMapKeys?exists && plpProductVariantMapKeys?has_content>
	    <#list plpProductVariantMapKeys as key>
	        <#assign productAttrPdpQtyMin = ""/>
	        <#assign productAttrPdpQtyMax = ""/>
	        <#if plpProductVariantProductAttributeMap.get(key)?has_content>
	      	    <#assign variantAttributeMap = plpProductVariantProductAttributeMap.get(key)!""/>
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
    <#if plpProductId?has_content>
	    <#assign productAttrPdpQtyMin = plpProductAtrributeMap.get('PDP_QTY_MIN')!/>
	    <#assign productAttrPdpQtyMax = plpProductAtrributeMap.get('PDP_QTY_MAX')!/>
	    <#assign productAttrPdpQtyDefault = plpProductAtrributeMap.get('PDP_QTY_DEFAULT')!/>
	    <#if productAttrPdpQtyMin?has_content &&  productAttrPdpQtyMax?has_content>
	        <input type="hidden" name="productAttrPdpQtyMin_${plpProductId}" id="js_pdpQtyMinAttributeValue_${plpProductId}" value="${productAttrPdpQtyMin}"/>
	        <input type="hidden" name="productAttrPdpQtyMax_${plpProductId}" id="js_pdpQtyMaxAttributeValue_${plpProductId}" value="${productAttrPdpQtyMax}"/>
	    </#if>
	    <#if productAttrPdpQtyDefault?has_content>
	        <input type="hidden" name="productAttrPdpQtyDefault_${plpProductId}" id="js_pdpQtyDefaultAttributeValue_${plpProductId}" value="${productAttrPdpQtyDefault!}"/>
	    </#if>
	</#if>
</#if>   

<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<li class="${request.getAttribute("attributeClass")!}">
	<#if pdpPriceMap?exists && pdpPriceMap?has_content && pdpPriceMap.price?has_content>
	  <div class="pdpPriceOnline" id="js_pdpPriceOnline">
	    <label>${uiLabelMap.OnlinePriceCaption}</label>
	    <span><@ofbizCurrency amount=pdpPriceMap.price isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed rounding=globalContext.currencyRounding /></span>
	  </div>
	  <div class="pdpPriceOnline" id="js_pdpPriceOnline_Virtual" style="display:none">
	    <label>${uiLabelMap.OnlinePriceCaption}</label>
	    <span><@ofbizCurrency amount=pdpPriceMap.price isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed rounding=globalContext.currencyRounding /></span>
	  </div>
	</#if>
	
	<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
	  <#list productVariantMapKeys as key>
	    <#assign productPrice = productVariantPriceMap.get('${key}')!""/>
	    <#if productPrice?has_content>
	      <div class="pdpPriceOnline" id="js_pdpPriceOnline_${key}" style="display:none">
	        <label>${uiLabelMap.OnlinePriceCaption}</label>
	        <span><@ofbizCurrency amount=productPrice.basePrice isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed rounding=globalContext.currencyRounding /></span>
	      </div>
	    </#if>
	  </#list>
	</#if>
</li>
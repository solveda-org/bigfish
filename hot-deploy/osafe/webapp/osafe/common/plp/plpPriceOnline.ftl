<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<li class="${request.getAttribute("attributeClass")!}">
	<div class="js_plpPriceOnline">
	  <#if plpPrice?exists && plpPrice?has_content>
	    <label>${uiLabelMap.PlpPriceLabel}</label>
	    <span><@ofbizCurrency amount=plpPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
	  </#if>
	</div>
</li>   

<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<li class="${request.getAttribute("attributeClass")!}">
	<div class="js_plpPriceList">
	  <#if plpListPrice?has_content && plpListPrice gt plpPrice>
	    <label>${uiLabelMap.PlpListPriceLabel}</label>
	    <span><@ofbizCurrency amount=plpListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
	  </#if>
	</div>
</li>   

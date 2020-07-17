<#if plpRecurrencePrice?exists && plpRecurrencePrice?has_content && (plpPrice?exists && plpPrice?has_content) && plpRecurrencePrice !=0>
 <#if (plpPrice > plpRecurrencePrice)>
	<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
    <#assign recurrenceSavePercent = ((plpPrice - plpRecurrencePrice)/plpPrice) />
	<li class="${request.getAttribute("attributeClass")!}">
		<div>
		    <label>${uiLabelMap.PlpRecurrencePriceLabel}</label>
		    <span><@ofbizCurrency amount=plpRecurrencePrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
		    <label>${uiLabelMap.PlpRecurrencePriceSavingsLabel}</label>
	        <span>${recurrenceSavePercent?string("#0%")}</span>
		</div>
	</li>   
 </#if>
</#if>

 
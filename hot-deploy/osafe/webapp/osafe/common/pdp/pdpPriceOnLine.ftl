<#if pdpPriceMap?exists && pdpPriceMap?has_content>
<div class="pdpPriceOnLine">
 <label>${uiLabelMap.OnlinePriceCaption}</label>
 <span class="price"><@ofbizCurrency amount=pdpPriceMap.price isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed /></span>
</div>
</#if>
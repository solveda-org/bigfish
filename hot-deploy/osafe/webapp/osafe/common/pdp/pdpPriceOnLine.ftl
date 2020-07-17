<#if priceMap?exists && priceMap?has_content>
<div id="pdpPriceOnLine">
 <label>${uiLabelMap.OnlinePriceCaption}</label>
 <span class="price"><@ofbizCurrency amount=priceMap.price isoCode=CURRENCY_UOM_DEFAULT!priceMap.currencyUsed /></span>
</div>
</#if>
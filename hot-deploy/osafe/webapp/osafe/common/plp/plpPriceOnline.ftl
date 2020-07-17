<#if price?exists && price?has_content>
<div class="plpPriceOnline">
<p class="price">${uiLabelMap.PlpPriceLabel} <@ofbizCurrency amount=price isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
</div>
</#if>
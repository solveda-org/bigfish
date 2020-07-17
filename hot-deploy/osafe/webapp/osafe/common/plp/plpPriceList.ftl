<#if priceMap.listPrice gt price>
<div class="plpPriceList">
<p class="price">${uiLabelMap.PlpListPriceLabel} <@ofbizCurrency amount=priceMap.listPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
</div>
</#if>
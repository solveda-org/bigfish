<div class="plpPriceOnline">
  <#if price?exists && price?has_content>
    <p class="price">${uiLabelMap.PlpPriceLabel} <@ofbizCurrency amount=price isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
  </#if>
</div>
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpPriceOnline">
  <#if plpPrice?exists && plpPrice?has_content>
    <p class="price">${uiLabelMap.PlpPriceLabel} <@ofbizCurrency amount=plpPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
  </#if>
</div>
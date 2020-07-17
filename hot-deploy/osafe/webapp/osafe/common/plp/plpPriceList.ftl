<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpPriceList">
  <#if plpListPrice?has_content && plpListPrice gt plpPrice>
    <p class="price">${uiLabelMap.PlpListPriceLabel} <@ofbizCurrency amount=plpListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
  </#if>
</div>

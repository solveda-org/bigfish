<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpPriceList">
  <#if listPrice?has_content && listPrice gt price>
    <p class="price">${uiLabelMap.PlpListPriceLabel} <@ofbizCurrency amount=listPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
  </#if>
</div>

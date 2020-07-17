<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpPriceOnline">
  <#if plpPrice?exists && plpPrice?has_content>
    <label>${uiLabelMap.PlpPriceLabel}</label>
    <span class="price"><@ofbizCurrency amount=plpPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
  </#if>
</div>
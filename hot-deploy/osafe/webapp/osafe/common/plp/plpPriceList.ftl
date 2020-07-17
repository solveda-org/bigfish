<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpPriceList">
  <#if plpListPrice?has_content && plpListPrice gt plpPrice>
    <label>${uiLabelMap.PlpListPriceLabel}</label>
    <span class="price"><@ofbizCurrency amount=plpListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
  </#if>
</div>

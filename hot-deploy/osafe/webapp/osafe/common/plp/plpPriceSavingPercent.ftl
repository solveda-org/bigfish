<#-- Check Savings Percent -->
<#if priceMap.listPrice != 0>
  <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
  <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
  <#assign youSavePercent = ((priceMap.listPrice - priceMap.price)/priceMap.listPrice) />
  <div class="plpPriceSavingPercent">
    <#if youSavePercent gt showSavingPercentAbove?number>
      <p class="price">${uiLabelMap.YouSaveCaption}${youSavePercent?string("#0%")}</p>
    </#if>
  </div>
</#if>

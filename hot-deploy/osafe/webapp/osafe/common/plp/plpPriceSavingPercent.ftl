<#-- Check Savings Percent -->
<#assign showSavingPercentAbove = PDP_PCT_THRESHOLD!"0"/>
<#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
<#assign youSavePercent = ((priceMap.listPrice - priceMap.price)/priceMap.listPrice) />
<#if youSavePercent gt showSavingPercentAbove?number>
 <div class="plpPriceSavingPercent">
    <p class="price">${uiLabelMap.YouSaveCaption}${youSavePercent?string("#0%")}</p>
 </div>
</#if>

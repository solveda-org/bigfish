<#-- Check Savings Percent -->
<#if priceMap?exists && priceMap?has_content>
<#assign showSavingPercentAbove = PDP_PCT_THRESHOLD!"0"/>
<#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
<#assign youSavePercent = ((priceMap.listPrice - priceMap.price)/priceMap.listPrice) />
<#if youSavePercent gt showSavingPercentAbove?number>
 <div id="pdpPriceSavingPercent">
     <label>${uiLabelMap.YouSaveCaption}</label>
     <span class="savings">${youSavePercent?string("#0%")}</span>
 </div>
</#if>
</#if>
<#-- Check Savings Percent -->
<#assign PRODUCT_PCT_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_PCT_THRESHOLD")!"0"/>
<#if listPrice?has_content && listPrice != 0>
  <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
  <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
  <#assign youSavePercent = ((listPrice - price)/listPrice) />
  <div class="plpPriceSavingPercent">
    <#if youSavePercent gt showSavingPercentAbove?number>
      <p class="price">${uiLabelMap.YouSaveCaption}${youSavePercent?string("#0%")}</p>
    </#if>
  </div>
</#if>

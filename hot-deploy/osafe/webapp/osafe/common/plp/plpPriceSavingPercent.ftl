<#-- Check Savings Percent -->
<#assign PRODUCT_PCT_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_PCT_THRESHOLD")!"0"/>
<#if plpListPrice?has_content && plpListPrice != 0>
  <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
  <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
  <#assign youSavePercent = ((plpListPrice - plpPrice)/plpListPrice) />
  <div class="plpPriceSavingPercent">
    <#if youSavePercent gt showSavingPercentAbove?number>
      <label>${uiLabelMap.YouSaveCaption}</label>
      <span class="price">${youSavePercent?string("#0%")}</span>
    </#if>
  </div>
</#if>

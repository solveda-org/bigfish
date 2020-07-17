<#-- Check Savings Percent -->
<#if pdpPriceMap?exists && pdpPriceMap?has_content && pdpPriceMap.listPrice != 0>
  <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
  <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
  <#assign youSavePercent = ((pdpPriceMap.listPrice - pdpPriceMap.price)/pdpPriceMap.listPrice) />
  <div class="pdpPriceSavingPercent" id="pdpPriceSavingPercent">
    <#if youSavePercent gt showSavingPercentAbove?number>
      <label>${uiLabelMap.YouSaveCaption}</label>
      <span class="savings">${youSavePercent?string("#0%")}</span>
    </#if>
  </div>
</#if>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",key), true)/>
    <#assign productPrice = dispatcher.runSync("calculateProductPrice", Static["org.ofbiz.base.util.UtilMisc"].toMap("product", product, "userLogin", userLogin))/>
    <#if productPrice?has_content && productPrice.listPrice != 0>
      <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
      <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
      <#assign youSavePercent = ((productPrice.listPrice - productPrice.basePrice)/productPrice.listPrice) />
      <#if youSavePercent gt showSavingPercentAbove?number>
        <div class="pdpPriceSavingPercent" id="pdpPriceSavingPercent_${key}" style="display:none">
          <label>${uiLabelMap.YouSaveCaption}</label>
          <span class="savings">${youSavePercent?string("#0%")}</span>
        </div>
      </#if>
    </#if>
  </#list>
</#if>
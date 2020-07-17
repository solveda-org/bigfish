<#-- Check Savings Percent -->
<#assign PRODUCT_PCT_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_PCT_THRESHOLD")!"0"/>
<#if pdpPriceMap?exists && pdpPriceMap?has_content && pdpPriceMap.listPrice?has_content && pdpPriceMap.listPrice != 0>
  <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
  <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
  <#assign youSavePercent = ((pdpPriceMap.listPrice - pdpPriceMap.price)/pdpPriceMap.listPrice) />
  <li class="${request.getAttribute("attributeClass")!}">
    <div class="pdpPriceSavingPercent" id="js_pdpPriceSavingPercent">
      <#if youSavePercent gt showSavingPercentAbove?number>
        <label>${uiLabelMap.YouSaveCaption}</label>
        <span>${youSavePercent?string("#0%")}</span>
      </#if>
    </div>
    
    <div class="pdpPriceSavingPercent" id="js_pdpPriceSavingPercent_Virtual" style="display:none">
      <#if youSavePercent gt showSavingPercentAbove?number>
        <label>${uiLabelMap.YouSaveCaption}</label>
        <span>${youSavePercent?string("#0%")}</span>
      </#if>
    </div>
    
  </li>
</#if>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign productPrice = productVariantPriceMap.get('${key}')!""/>
    <#if productPrice?has_content && productPrice.listPrice?has_content && productPrice.listPrice != 0>
      <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
      <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
      <#assign youSavePercent = ((productPrice.listPrice - productPrice.basePrice)/productPrice.listPrice) />
      <#if youSavePercent gt showSavingPercentAbove?number>
        <div class="pdpPriceSavingPercent" id="js_pdpPriceSavingPercent_${key}" style="display:none">
          <label>${uiLabelMap.YouSaveCaption}</label>
          <span>${youSavePercent?string("#0%")}</span>
        </div>
      </#if>
    </#if>
  </#list>
</#if>
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="pdpPriceOnLine" id="pdpPriceOnLine">
  <#if pdpPriceMap?exists && pdpPriceMap?has_content>
    <label>${uiLabelMap.OnlinePriceCaption}</label>
    <span class="price"><@ofbizCurrency amount=pdpPriceMap.price isoCode=CURRENCY_UOM_DEFAULT!pdpPriceMap.currencyUsed rounding=globalContext.currencyRounding /></span>
  </#if>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign productPrice = productVariantPriceMap.get('${key}')/>
    <#if productPrice?has_content>
      <div class="pdpPriceOnLine" id="pdpPriceOnLine_${key}" style="display:none">
        <label>${uiLabelMap.OnlinePriceCaption}</label>
        <span class="price"><@ofbizCurrency amount=productPrice.basePrice isoCode=CURRENCY_UOM_DEFAULT!productPrice.currencyUsed rounding=globalContext.currencyRounding /></span>
      </div>
    </#if>
  </#list>
</#if>
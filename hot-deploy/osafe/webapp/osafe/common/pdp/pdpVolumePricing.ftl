<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="pdpVolumePricing" id="pdpVolumePricing">
  <#if volumePricingRule?has_content>
    <div id="volumePricing">
      <span class="pricingCaption">${uiLabelMap.VolumePricingLabel}</span>
      <#list volumePricingRule as priceRule>
        <#assign volumePrice = volumePricingRuleMap.get(priceRule.productPriceRuleId)/>
        <p><span class="priceRule">${priceRule.description!}&nbsp;</span><span class="price"><@ofbizCurrency amount=volumePrice isoCode=CURRENCY_UOM_DEFAULT rounding=globalContext.currencyRounding/></span></p>
      </#list>
    </div>
  </#if>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign volumePricingRule = variantVolumePricingRuleMap.get(key)/>
    <#assign volumePricingRuleMap = variantVolumePricingRuleMapMap.get(key)/>
    <#if volumePricingRule?has_content>
        <div class="pdpVolumePricing" id="pdpVolumePricing_${key}" style="display:none">
          <div id="volumePricing">
            <span class="pricingCaption">${uiLabelMap.VolumePricingLabel}</span>
            <#list volumePricingRule as priceRule>
              <#assign volumePrice = volumePricingRuleMap.get(priceRule.productPriceRuleId)/>
              <p><span class="priceRule">${priceRule.description!}&nbsp;</span><span class="price"><@ofbizCurrency amount=volumePrice isoCode=CURRENCY_UOM_DEFAULT rounding=globalContext.currencyRounding/></span></p>
            </#list>
          </div>
        </div>
    </#if>
  </#list>
</#if>
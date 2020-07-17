<#if volumePricingRule?has_content>
  <div class="pdpVolumePricing">
    <div id="volumePricing">
          <span class="pricingCaption">${uiLabelMap.VolumePricingLabel}</span>
            <#list volumePricingRule as priceRule>
              <#assign volumePrice = volumePricingRuleMap.get(priceRule.productPriceRuleId)/>
              <p><span class="priceRule">${priceRule.description!}&nbsp;</span><span class="price"><@ofbizCurrency amount=volumePrice isoCode=CURRENCY_UOM_DEFAULT/></span></p>
            </#list>
    </div>
  </div>
</#if>

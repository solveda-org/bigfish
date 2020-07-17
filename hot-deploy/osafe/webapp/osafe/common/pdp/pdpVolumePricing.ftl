<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>

<li class="${request.getAttribute("attributeClass")!}">
  <#if volumePricingRule?has_content>
    <div class="pdpVolumePricing" id="js_pdpVolumePricing">
     <div class="displayBox">
      <h3>${uiLabelMap.VolumePricingLabel}</h3>
      <ul class="displayList volumePriceRuleList">
        <#list volumePricingRule as priceRule>
          <#assign volumePrice = volumePricingRuleMap.get(priceRule.productPriceRuleId)/>
           <li>
              <label>${priceRule.description!}&nbsp;</label>
              <span><@ofbizCurrency amount=volumePrice isoCode=CURRENCY_UOM_DEFAULT rounding=globalContext.currencyRounding/></span>
           </li>
        </#list>
      </ul>
     </div>
    </div>
    
    <div class="pdpVolumePricing" id="js_pdpVolumePricing_Virtual" style="display:none">
     <div class="displayBox">
      <h3>${uiLabelMap.VolumePricingLabel}</h3>
      <ul class="displayList volumePriceRuleList">
        <#list volumePricingRule as priceRule>
          <#assign volumePrice = volumePricingRuleMap.get(priceRule.productPriceRuleId)/>
           <li>
              <label>${priceRule.description!}&nbsp;</label>
              <span><@ofbizCurrency amount=volumePrice isoCode=CURRENCY_UOM_DEFAULT rounding=globalContext.currencyRounding/></span>
           </li>
        </#list>
      </ul>
     </div>
    </div>
    
  </#if>

  <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
    <#list productVariantMapKeys as key>
      <#assign volumePricingRule = variantVolumePricingRuleMap.get(key)/>
      <#assign volumePricingRuleMap = variantVolumePricingRuleMapMap.get(key)/>
      <#if volumePricingRule?has_content>
          <div class="pdpVolumePricing" id="js_pdpVolumePricing_${key}" style="display:none">
		   <div class="displayBox">
		      <h3>${uiLabelMap.VolumePricingLabel}</h3>
		      <ul class="displayList volumePriceRuleList">
		        <#list volumePricingRule as priceRule>
		          <#assign volumePrice = volumePricingRuleMap.get(priceRule.productPriceRuleId)/>
		           <li>
		              <label>${priceRule.description!}&nbsp;</label>
		              <span><@ofbizCurrency amount=volumePrice isoCode=CURRENCY_UOM_DEFAULT rounding=globalContext.currencyRounding/></span>
		           </li>
		        </#list>
		      </ul>
		   </div>
          </div>
      </#if>
    </#list>
  </#if>
</li>
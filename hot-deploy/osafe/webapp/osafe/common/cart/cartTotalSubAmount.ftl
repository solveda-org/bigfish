<div class="cartTotalSubAmount">
  <div class="labelText">
    <label>${uiLabelMap.SubTotalLabel}</label>
  </div>
  <div class="labelValue">
  	<#if cartSubTotal?exists && cartSubTotal?has_content >
          <span><@ofbizCurrency amount=cartSubTotal! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
        </#if>
  </div>
</div>
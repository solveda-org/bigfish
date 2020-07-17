<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.SubTotalCaption}</label>
    <#if cartSubTotal?exists && cartSubTotal?has_content>
      <span><@ofbizCurrency amount=cartSubTotal! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
    </#if>
  </div>
</li>
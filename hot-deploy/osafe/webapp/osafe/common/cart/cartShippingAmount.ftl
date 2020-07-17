<#if shippingApplies?exists && shippingApplies>
  <li class="${request.getAttribute("attributeClass")!}">
    <div>
      <label>${cartShippingLabel!uiLabelMap.CartShippingAndHandlingCaption}</label>
      <span><@ofbizCurrency amount=orderShippingTotal! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
    </div>
  </li>
</#if>
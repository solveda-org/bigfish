<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.ShippingAndHandlingCaption}</label>
    <span><@ofbizCurrency amount=orderShippingTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></span>
  </div>
</li>
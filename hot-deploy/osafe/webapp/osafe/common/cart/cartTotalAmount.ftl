<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.CartTotalCaption}</label>
    <span><@ofbizCurrency amount=orderGrandTotal! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
  </div>
</li>

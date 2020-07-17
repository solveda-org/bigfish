<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.SubTotalCaption}</label>
    <span><@ofbizCurrency amount=orderSubTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></span>
  </div>
</li>
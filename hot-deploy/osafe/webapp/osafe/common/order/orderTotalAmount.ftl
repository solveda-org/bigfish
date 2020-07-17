<li class="${request.getAttribute("attributeClass")!}<#if lineIndex?exists && lineIndex== 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.TotalAmountCaption}</label>
    <span><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom rounding=globalContext.currencyRounding/></span>
  </div>
</li>
<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.CartItemPriceTotalCaption}</label>
    <span><@ofbizCurrency amount=itemSubTotal isoCode=currencyUom rounding=globalContext.currencyRounding/></span> 
  </div>
</li>


<div class="cartTotalAmount">
  <div class="labelText">
    <label>${uiLabelMap.CartTotalLabel}</label>
  </div>
  <div class="labelValue">
    <span><@ofbizCurrency amount=orderGrandTotal! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
  </div>
</div>
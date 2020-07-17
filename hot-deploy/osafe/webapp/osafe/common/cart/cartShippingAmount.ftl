<div class="cartShippingAmount">
  <div class="labelText">
    <label>${uiLabelMap.CartShippingAndHandlingLabel}</label>
  </div>
  <div class="labelValue">
    <span><@ofbizCurrency amount=orderShippingTotal! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
  </div>
</div>
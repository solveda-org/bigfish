<div class="cartTaxAmount">
  <div class="labelText">
    <label>${uiLabelMap.CartTaxLabel}</label>
  </div>
  <div class="labelValue">
    <span><@ofbizCurrency amount=orderTaxTotal! isoCode=currencyUom  rounding=globalContext.currencyRounding/></span>
  </div>
</div>

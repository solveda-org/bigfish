<div class="${request.getAttribute("attributeClass")!}">
    <label>${uiLabelMap.CartItemPriceTotalCaption}</label>
    <span><@ofbizCurrency amount=itemSubTotal isoCode=currencyUom rounding=globalContext.currencyRounding/></span> 
</div>


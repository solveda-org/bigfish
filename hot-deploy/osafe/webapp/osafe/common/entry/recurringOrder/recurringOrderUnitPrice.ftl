<div class="${request.getAttribute("attributeClass")!}">
    <label>${uiLabelMap.UnitPriceCaption}</label>
    <span><@ofbizCurrency amount=displayPrice isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
</div>

<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
    <div>
     <label>${uiLabelMap.CurrentPriceCaption}</label>
     <span><@ofbizCurrency amount=price isoCode=currencyUom!priceMap.currencyUsed rounding=globalContext.currencyRounding /></span>
    </div>
</li>
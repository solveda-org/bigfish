<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
        <div>
         <label>${uiLabelMap.CartItemPriceTotalCaption}</label>
         <span><@ofbizCurrency amount=itemTotal rounding=globalContext.currencyRounding isoCode=currencyUom/></span>
        </div>
</li>
<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
      <label>${uiLabelMap.CartItemOfferPriceCaption}</label>
      <#if offerPrice?exists && offerPrice?has_content>
	    <span><@ofbizCurrency amount=offerPrice isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
      </#if>
    </#if>
  </div>
</li>

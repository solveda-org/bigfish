<#if (offerPriceVisible?has_content) && offerPriceVisible == "Y" >
  <div class="cartItemOfferPrice">
    <#if offerPrice?exists && offerPrice?has_content>
      <div class="labelText">
        <label>${uiLabelMap.CartItemOfferPriceCaption}</label>
      </div>
      <div class="labelValue">
        <span class="price"><@ofbizCurrency amount=offerPrice isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
      </div>
    </#if>
  </div>
</#if>

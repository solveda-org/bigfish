<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.CartItemUpdateBtnCaption}</label>
    <#if !cartLine.getIsPromo()>
      <a class="standardBtn update" href="javascript:submitCheckoutForm(document.${formName!}, 'UC', '');" title="${uiLabelMap.UpdateBtn}"><span>${uiLabelMap.UpdateBtn}</span></a>
    </#if>
    <input type="hidden" name="productName${cartLineIndex}" id="js_productName_${cartLineIndex}" value="${wrappedProductName!}" /> 
  </div>
</li>
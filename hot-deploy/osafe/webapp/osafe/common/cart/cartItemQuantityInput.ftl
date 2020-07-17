<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.CartItemQuantityCaption}</label>
    <#if cartLine?exists && cartLine?has_content>
      <#if cartLine.getIsPromo()>
        <input size="6" class="qtyInCart_${cartLine.getProductId()}" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${quantity!}" maxlength="5" readonly="readonly"/>
      <#else>
        <input size="6" class="qtyInCart_${cartLine.getProductId()}" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${quantity!}" maxlength="5" onkeypress="javascript:setCheckoutFormAction(document.${formName!}, 'UC', '');"/>
      </#if>
    <#else>
      <input size="6" type="text" name="recurrenceQuantity" id="recurrenceQuantity" value="${quantity!}" maxlength="5"/>
    </#if>
  </div>
</li>

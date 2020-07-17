<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.CartItemQuantityCaption}</label>
    <input size="6" type="text" class="qtyInCart_${productId}" name="update_${wishListSeqId}" id="update_${wishListSeqId}" value="${quantity!}" maxlength="5" onkeypress="javascript:setCheckoutFormAction(document.${formName!}, 'UWL', '');"/>
  </div>
</li>

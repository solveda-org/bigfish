<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.CartItemUpdateBtnCaption}</label>
    <a class="standardBtn update" href="javascript:submitCheckoutForm(document.${formName!}, 'UWL', '');" title="${uiLabelMap.UpdateBtn}"><span>${uiLabelMap.UpdateBtn}</span></a>
  </div>
</li>
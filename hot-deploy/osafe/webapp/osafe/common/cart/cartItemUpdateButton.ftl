<div class="cartItemUpdateButton">
  <div class="labelText">
    <label>${uiLabelMap.CartItemUpdateBtnCaption}</label>
  </div>
  <div class="labelValue">
    <#if !cartLine.getIsPromo()>
      <a class="standardBtn update" href="javascript:submitCheckoutForm(document.${formName!}, 'UC', '');" title="${uiLabelMap.UpdateBtn}"><span>${uiLabelMap.UpdateBtn}</span></a>
    </#if>
  </div>
</div>
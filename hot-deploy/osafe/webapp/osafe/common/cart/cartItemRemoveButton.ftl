<div class="cartItemRemoveButton">
  <div class="labelText">
    <label>${uiLabelMap.CartItemRemoveBtnCaption}</label>
  </div>
  <div class="labelValue">
    <#if !cartLine.getIsPromo()>
      <a class="standardBtn delete" href="<@ofbizUrl>deleteFromCart?delete_${cartLineIndex}=${cartLineIndex}</@ofbizUrl>" title="${uiLabelMap.RemoveItemBtn}">
      <span>${uiLabelMap.RemoveItemBtn}</span>
      </a>
    </#if>
  </div>
</div>



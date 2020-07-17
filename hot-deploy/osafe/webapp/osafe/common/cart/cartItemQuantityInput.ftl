<div class="cartItemQuantity">
  <div class="labelText">
    <label>${uiLabelMap.CartItemQuantityCaption}</label>
  </div>
  <div class="labelValue">
    <#if cartLine?exists && cartLine?has_content && cartLine.getIsPromo()>
      <input size="6" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${quantity!}" maxlength="5" readonly="readonly"/>
    <#else>
      <input size="6" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${quantity!}" maxlength="5"/>
    </#if>
  </div>
</div>

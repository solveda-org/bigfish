<div class="cartItemQuantity">
  <#if cartLine.getIsPromo()>
    <input size="6" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" maxlength="5" readonly="readonly"/>
  <#else>
    <input size="6" type="text" name="update_${cartLineIndex}" id="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" maxlength="5"/><span class="action"><a class="standardBtn action" href="javascript:submitCheckoutForm(document.${formName!}, 'UC', '');">Update</a></span>
  </#if>
</div>
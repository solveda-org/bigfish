<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="${request.getAttribute("attributeClass")!}">
  <label>${uiLabelMap.CartItemQuantityCaption}</label>
  <div class="entryField">
    <input size="6" type="text" name="recurrenceQuantity" id="recurrenceQuantity" value="${parameters.recurrenceQuantity!quantity!""}" maxlength="5"/>
    <@fieldErrors fieldName="recurrenceQuantity"/>
  </div>
</div>

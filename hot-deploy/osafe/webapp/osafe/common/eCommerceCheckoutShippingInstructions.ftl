<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shippingInstructions = shoppingCart.getShippingInstructions()?if_exists>
</#if>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="entryForm shippingInstructions">
    <div class="entry">
      <label>${uiLabelMap.ShippingInstructionsLabel}</label>
        <div class="entryField">
		      <textarea class="largeArea characterLimit" name="shipping_instructions" id="shipping_instructions" maxlength="255">${parameters.shipping_instructions!shippingInstructions!""}</textarea>
		      <span class="js_textCounter textCounter"></span>
		      <span class="entryHelper">${uiLabelMap.ShippingInstructionsInfo}</span>
		      <@fieldErrors fieldName="shipping_instructions"/>
		</div>
    </div>
   </div>
</div>
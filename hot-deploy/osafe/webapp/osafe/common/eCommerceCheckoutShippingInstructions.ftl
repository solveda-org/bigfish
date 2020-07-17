<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shippingInstructions = shoppingCart.getShippingInstructions()?if_exists>
</#if>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="entry">
      <label>${uiLabelMap.ShippingInstructionsLabel}</label>
        <div class="entryField">
		      <textarea class="largeArea" name="shipping_instructions">${parameters.shipping_instructions!shippingInstructions!""}</textarea>
		      <span class="entryHelper">${uiLabelMap.ShippingInstructionsInfo}</span>
		      <@fieldErrors fieldName="shipping_instructions"/>
		</div>
    </div>
</div>
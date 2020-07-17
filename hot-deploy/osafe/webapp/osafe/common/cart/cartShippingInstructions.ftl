<#if shippingInstructions?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
		<div>
		  <label>${uiLabelMap.CartShippingInstructionsLabel}</label>
		  <span>${shippingInstructions!}</span>
		</div>
	</li>
</#if>

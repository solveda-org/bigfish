<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign attributeClass = request.getAttribute("attributeClass")!""/>
<#assign attributeStyle = request.getAttribute("attributeStyle")!""/>
<#assign attributeId = request.getAttribute("attributeId")!""/>

<div <#if attributeId?has_content> id="${attributeId!}"</#if><#if attributeClass?has_content> class="${attributeClass!}"</#if><#if attributeStyle?has_content> style="${attributeStyle!}"</#if> >
	<div class="entry">
		<label>${uiLabelMap.PayPalCaption}</label>
		<a href="javascript:submitCheckoutForm(document.${formName!}, 'PA', 'EXT_PAYPAL');">
			<span class="payPalCheckoutImage"><span>${uiLabelMap.PayPalHeading}</span></span>
		</a>
	</div>
</div>

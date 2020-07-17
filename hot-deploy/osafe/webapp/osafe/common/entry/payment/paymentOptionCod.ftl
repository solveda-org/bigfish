<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign attributeClass = request.getAttribute("attributeClass")!""/>
<#assign attributeStyle = request.getAttribute("attributeStyle")!""/>
<#assign attributeId = request.getAttribute("attributeId")!""/>
<#assign includeRadioOption = request.getAttribute("includeRadioOption")!"N"/>
<#if parameters.paymentOption?exists && parameters.paymentOption?has_content>
     <#assign selectedPaymentOption = parameters.paymentOption!""/>
</#if>
<div <#if attributeId?has_content> id="${attributeId!}"</#if><#if attributeClass?has_content> class="${attributeClass!}"</#if><#if attributeStyle?has_content> style="${attributeStyle!}"</#if> >
	<#if includeRadioOption == "Y">
		<div class="entry">
	    	<label class="radioOptionLabel"><input type="radio" id="codPayment" name="paymentOption" value="PAYOPT_COD" <#if (selectedPaymentOption?exists && selectedPaymentOption?string == "PAYOPT_COD")>checked="checked"</#if>/><span class="radioOptionText">${uiLabelMap.CODLabel}</span></label>
		</div>
	</#if>
</div>

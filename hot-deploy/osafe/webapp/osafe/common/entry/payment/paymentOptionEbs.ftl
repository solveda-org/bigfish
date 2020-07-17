<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign attributeClass = request.getAttribute("attributeClass")!""/>
<#assign attributeStyle = request.getAttribute("attributeStyle")!""/>
<#assign attributeId = request.getAttribute("attributeId")!""/>

<div <#if attributeId?has_content> id="${attributeId!}"</#if><#if attributeClass?has_content> class="${attributeClass!}"</#if><#if attributeStyle?has_content> style="${attributeStyle!}"</#if> >
	<div class="entry">
		<label>${uiLabelMap.EBSCaption}</label>
		<a href="javascript:submitCheckoutForm(document.${formName!}, 'EB', 'EXT_EBS');">
			<span class="ebsCheckoutImage"><span>${uiLabelMap.EBSHeading}</span></span>
		</a>
	</div>
</div>

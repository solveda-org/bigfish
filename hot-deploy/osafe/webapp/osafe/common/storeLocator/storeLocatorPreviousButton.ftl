<#if cancelButtonVisible?has_content && cancelButtonVisible == "Y">
	<div class="${request.getAttribute("attributeClass")!}">
		<a href="<@ofbizUrl>${cancelButtonUrl!}</@ofbizUrl>" class="${cancelButtonClass!}"><span>${cancelButtonDescription!}</span></a>
        <span class="entryHelper">${CancelStoreSelectInfo!}</span>
	</div>
</#if>	

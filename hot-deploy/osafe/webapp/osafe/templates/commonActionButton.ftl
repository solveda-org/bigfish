<#assign commonButtonUrl = commonButtonUrl!"">
<#assign commonJsAction = "javascript:submitCommonForm(document.${formName!}, 'DN', '');">
<#assign commonButtonClass = commonButtonClass!"standardBtn positive">
<#assign commonButtonLabel = commonButtonLabel!uiLabelMap.ContinueBtn>
<div class="${request.getAttribute("attributeClass")!}">
  	<#-- commonButton Url may be set in screen or in groovy -->
    <a class="${commonButtonClass!""}" href="<#if commonButtonUrl?exists && commonButtonUrl?has_content><@ofbizUrl>${commonButtonUrl!""}</@ofbizUrl><#else>${commonJsAction!}</#if>"><span>${commonButtonLabel!}</span></a>
</div>

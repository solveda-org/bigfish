<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <label>${uiLabelMap.StatusCaption}</label>
    <#if status?exists>
    	<#if status == "N">
    		<span>${uiLabelMap.CancelledCaption}</span>
    	<#else>
    		<span>${uiLabelMap.ActiveCaption}</span>
    	</#if>
    </#if>
  </div>
</li>
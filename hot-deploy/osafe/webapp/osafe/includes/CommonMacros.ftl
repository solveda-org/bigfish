
<#macro required>
<span class="required">*</span>
</#macro>

<#macro fieldErrors fieldName>
  <#if errorMessageList?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName, true, errorMessageList)>
    <#if fieldMessages?has_content>
	    <ul class="fieldErrorMessage">
	      <#list fieldMessages as errorMsg>
	        <li>${errorMsg}</li>
	      </#list>
	    </ul>
    </#if>
  </#if>
</#macro>
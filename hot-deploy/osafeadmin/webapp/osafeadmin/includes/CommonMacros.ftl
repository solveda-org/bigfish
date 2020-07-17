
<#macro required>
<span class="required">*</span>
</#macro>

<#macro fieldErrors fieldName>
  <#if errorMessageList?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName, true, errorMessageList)>
    <ul>
      <#list fieldMessages as errorMsg>
        <li class="fieldErrorMessage">${errorMsg}</li>
      </#list>
    </ul>
  </#if>
</#macro>
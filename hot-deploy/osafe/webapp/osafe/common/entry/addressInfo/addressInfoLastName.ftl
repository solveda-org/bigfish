<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#assign lastName = ""/>
<#if postalAddressData?has_content>
    <#assign toName = postalAddressData.toName!"" />
    <#if toName?has_content>
      <#assign fullName = toName.split(" ")!/>
      <#if fullName?has_content>
        <#assign fullNameSize = fullName?size/>
        <#if fullNameSize &gt; 1>
          <#assign lastName = fullName[fullNameSize-1]!/>
        </#if>
      </#if>
    </#if>
<#-- for Facebook login -->
<#elseif parameters.fbLast_name?has_content>
  <#assign lastName= parameters.fbLast_name!""/>
</#if>

<#assign lastNameFromSession = session.getAttribute(fieldPurpose+"_LAST_NAME")!/> 
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <!-- address last name -->
      <label for="${fieldPurpose?if_exists}_LAST_NAME"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.LastNameCaption}</label>
      <div class="entryField">
      	<input type="text" maxlength="100" class="addressLastName" name="${fieldPurpose?if_exists}_LAST_NAME" id="js_${fieldPurpose?if_exists}_LAST_NAME" value="${requestParameters.get(fieldPurpose+"_LAST_NAME")!lastNameFromSession!lastName!}"/>
      	<input type="hidden" id="${fieldPurpose?if_exists}_LAST_NAME_MANDATORY" name="${fieldPurpose?if_exists}_LAST_NAME_MANDATORY" value="${mandatory}"/>
      	<@fieldErrors fieldName="${fieldPurpose?if_exists}_LAST_NAME"/>
      </div>
</div>
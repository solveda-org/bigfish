<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#assign firstName = ""/>
<#if postalAddressData?has_content>
    <#assign toName = postalAddressData.toName!"" />
    <#if toName?has_content>
      <#assign fullName = toName.split(" ")!/>
      <#if fullName?has_content>
        <#assign firstName = fullName[0]!/>
      </#if>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <!-- address first name -->
      <label for="${fieldPurpose?if_exists}_FIRST_NAME"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.FirstNameCaption}</label>
      <div class="entryField">
      	<input type="text" maxlength="100" class="addressFirstName" name="${fieldPurpose?if_exists}_FIRST_NAME" id="js_${fieldPurpose?if_exists}_FIRST_NAME" value="${requestParameters.get(fieldPurpose+"_FIRST_NAME")!firstName!}" />
      	<input type="hidden" id="${fieldPurpose?if_exists}_FIRST_NAME_MANDATORY" name="${fieldPurpose?if_exists}_FIRST_NAME_MANDATORY" value="${mandatory}"/>
      	<@fieldErrors fieldName="${fieldPurpose?if_exists}_FIRST_NAME"/>
      </div>
</div>
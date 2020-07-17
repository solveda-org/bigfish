<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#assign lastName = ""/>
<#if postalAddressData?has_content>
    <#assign fullName = postalAddressData.toName!"" />
<#-- for Facebook login -->
<#elseif parameters.fbName?has_content>
  <#assign fullName= parameters.fbName!""/>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <!-- address full Name -->
      <label for="${fieldPurpose?if_exists}_FULL_NAME"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.AddressFullNameCaption}</label>
      <div class="entryField">
      	<input type="text" maxlength="100" class="addressFullName" name="${fieldPurpose?if_exists}_FULL_NAME" id="js_${fieldPurpose?if_exists}_FULL_NAME" value="${requestParameters.get(fieldPurpose+"_FULL_NAME")!fullName!}"/>
      	<input type="hidden" id="${fieldPurpose?if_exists}_FULL_NAME_MANDATORY" name="${fieldPurpose?if_exists}_FULL_NAME_MANDATORY" value="${mandatory}"/>
      	<@fieldErrors fieldName="${fieldPurpose?if_exists}_FULL_NAME"/>
      </div>
</div>







			
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign postalCode = postalAddressData.postalCode!"">
    <#if postalCode?has_content && postalCode == '_NA_'>
      <#assign postalCode = "">
    </#if>
<#-- for Facebook login -->
<#elseif parameters.fbLocationZip?has_content>
  <#assign postalCode= parameters.fbLocationZip!""/>
</#if>
<!-- address zip entry -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <label for="${fieldPurpose?if_exists}_POSTAL_CODE">
            <span><#if mandatory == "Y"><@required/></#if>${uiLabelMap.PostalCodeOrZipCaption}</span>
        </label>
        <div class="entryField">
        	<input type="text" maxlength="60" class="postalCode" name="${fieldPurpose?if_exists}_POSTAL_CODE" id="js_${fieldPurpose?if_exists}_POSTAL_CODE" value="${requestParameters.get(fieldPurpose+"_POSTAL_CODE")!postalCode!""}" />
        	<input type="hidden" id="${fieldPurpose?if_exists}_POSTAL_CODE_MANDATORY" name="${fieldPurpose?if_exists}_POSTAL_CODE_MANDATORY" value="${mandatory}"/>
        	<@fieldErrors fieldName="${fieldPurpose?if_exists}_POSTAL_CODE"/>
        </div>
</div>
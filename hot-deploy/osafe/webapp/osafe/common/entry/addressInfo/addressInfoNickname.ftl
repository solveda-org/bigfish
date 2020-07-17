<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign attnName = postalAddressData.attnName!"" />
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
        <!-- address nick name -->
          <label for="${fieldPurpose?if_exists}_ATTN_NAME"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.AddressNickNameCaption}</label>
          <div class="entryField">
          	<input type="text" maxlength="100" class="addressNickName" name="${fieldPurpose?if_exists}_ATTN_NAME" id="js_${fieldPurpose?if_exists}_ATTN_NAME" value="${requestParameters.get(fieldPurpose+"_ATTN_NAME")!attnName!addressAttnName!""}" />
          	<input type="hidden" id="${fieldPurpose?if_exists}_ATTN_NAME_MANDATORY" name="${fieldPurpose?if_exists}_ATTN_NAME_MANDATORY" value="${mandatory}"/>
          	<@fieldErrors fieldName="${fieldPurpose?if_exists}_ATTN_NAME"/>
          </div>
</div>
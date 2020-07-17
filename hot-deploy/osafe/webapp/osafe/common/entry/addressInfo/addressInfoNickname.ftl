<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign attnName = postalAddressData.attnName!"" />
</#if>

<div class="addressInfoNickname">
    <#if showAttnName?has_content && showAttnName == "Y">
        <!-- address nick name -->
        <div class="entry">
          <label for="${fieldPurpose?if_exists}_ATTN_NAME"><@required/>${uiLabelMap.AddressNickNameCaption}</label>
          <input type="text" maxlength="100" class="addressNickName" name="${fieldPurpose?if_exists}_ATTN_NAME" id="${fieldPurpose?if_exists}_ATTN_NAME" value="${requestParameters.get(fieldPurpose+"_ATTN_NAME")!attnName!shippingAttnName!""}" />
          <@fieldErrors fieldName="${fieldPurpose?if_exists}_ATTN_NAME"/>
        </div>
    </#if>
</div>
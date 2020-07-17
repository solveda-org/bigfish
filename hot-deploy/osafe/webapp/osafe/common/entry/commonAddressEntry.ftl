<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery('#${fieldPurpose?if_exists}_COUNTRY')) {
            if(!jQuery('#${fieldPurpose?if_exists}_STATE_LIST_FIELD').length) {
                getAssociatedStateList('${fieldPurpose?if_exists}_COUNTRY', '${fieldPurpose?if_exists}_STATE', 'advice-required-${fieldPurpose?if_exists}_STATE', '${fieldPurpose?if_exists}_STATES', '${fieldPurpose?if_exists}_STATE_TEXT');
            }
            getAddressFormat("${fieldPurpose?if_exists}");
            jQuery('#${fieldPurpose?if_exists}_COUNTRY').change(function(){
                getAssociatedStateList('${fieldPurpose?if_exists}_COUNTRY', '${fieldPurpose?if_exists}_STATE', 'advice-required-${fieldPurpose?if_exists}_STATE', '${fieldPurpose?if_exists}_STATES', '${fieldPurpose?if_exists}_STATE_TEXT');
                getAddressFormat("${fieldPurpose?if_exists}");
            });
        }
    });
</script>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign postalAddressContactMechId = postalAddressData.contactMechId!"" />
</#if>

<h3>${addressEntryBoxHeading!"Address"}</h3>
<#if addressEntryInfo?exists && addressEntryInfo?has_content>
   <p class="instructions">${addressEntryInfo!}</p>
</#if>
<#if addressInstructionsInfo?exists && addressInstructionsInfo?has_content>
     <p class="instructions">${StringUtil.wrapString(addressInstructionsInfo!"")}</p>
</#if>

<input type="hidden" id="${fieldPurpose?if_exists}AddressContactMechId" name="${fieldPurpose?if_exists}AddressContactMechId" value="${postalAddressContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}HomePhoneContactMechId" name="${fieldPurpose?if_exists}HomePhoneContactMechId" value="${telecomHomeNoContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}MobilePhoneContactMechId" name="${fieldPurpose?if_exists}MobilePhoneContactMechId" value="${telecomMobileNoContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}_ADDRESS_ALLOW_SOL" name="${fieldPurpose?if_exists}_ADDRESS_ALLOW_SOL" value="N"/>
<#if isShipping?has_content && isShipping == "Y">
    <#if (errorMessage?has_content || errorMessageList?has_content) && !requestParameters.isSameAsBilling?has_content>
        <#assign isSameAsBilling = "N" />
    </#if>
    <div class="entry">
        <label for="isSameAsBilling">${uiLabelMap.SameAsBillingCaption}</label>
        <input type="checkbox" class="checkbox" name="isSameAsBilling" id="isSameAsBilling" value="Y" <#if isSameAsBilling?has_content && isSameAsBilling == "Y">checked</#if> />
    </div>
    <div id="${fieldPurpose?if_exists}_AddressSection">
</#if>
<div>
    <@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS_ERROR"/>
</div>
<#include "component://osafe/webapp/osafe/common/entry/addressSelection.ftl"/>
<#include "component://osafe/webapp/osafe/common/entry/addressNameFieldEntry.ftl"/>
<#include "component://osafe/webapp/osafe/common/entry/addressCommonFieldEntry.ftl"/>
<#include "component://osafe/webapp/osafe/common/entry/addressContactFieldEntry.ftl"/>
<#if isShipping?has_content && isShipping == "Y">
    </div>
</#if>
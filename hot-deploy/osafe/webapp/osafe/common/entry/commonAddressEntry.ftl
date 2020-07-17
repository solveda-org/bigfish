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
<#if context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
  <#assign phoneNumberMap = context.get(fieldPurpose+"PhoneNumberMap") />
</#if>
<#if postalAddressData?has_content>
    <#assign postalAddressContactMechId = postalAddressData.contactMechId!"" />
    <#assign attnName = postalAddressData.attnName!"" />
    <#assign address1 = postalAddressData.address1!"">
    <#assign address2 = postalAddressData.address2!"">
    <#assign address3 = postalAddressData.address3!"">
    <#assign address_nickname = postalAddressData.attnName!"">
    <#assign city = postalAddressData.city!"">
    <#if city?has_content && city == '_NA_'>
      <#assign city = "">
    </#if>
    <#assign stateProvinceGeoId = postalAddressData.stateProvinceGeoId!"">
    <#assign postalCode = postalAddressData.postalCode!"">
    <#if postalCode?has_content && postalCode == '_NA_'>
      <#assign postalCode = "">
    </#if>
    <#assign countryGeoId = postalAddressData.countryGeoId!"">
</#if>
<#if phoneNumberMap?has_content>
    <#assign telecomHomeNumber = phoneNumberMap["PHONE_HOME"]!"" />
    <#assign telecomMobileNumber = phoneNumberMap["PHONE_MOBILE"]!"" />
</#if>
<#if telecomHomeNumber?exists && telecomHomeNumber?has_content>
    <#assign telecomHomeNoContactMechId = telecomHomeNumber.contactMechId!"" />
    <#assign areaCodeHome = telecomHomeNumber.areaCode?if_exists />
    <#assign contactNumberHome = telecomHomeNumber.contactNumber?if_exists />
    <#if (contactNumberHome?has_content) && (contactNumberHome?length gt 6)>
        <#assign contactNumber3Home = contactNumberHome.substring(0, 3) />
        <#assign contactNumber4Home = contactNumberHome.substring(3, 7) />
    </#if>
</#if>
<#if telecomMobileNumber?has_content>
    <#assign telecomMobileNoContactMechId = telecomMobileNumber.contactMechId!"" />
    <#assign areaCodeMobile = telecomMobileNumber.areaCode?if_exists />
    <#assign contactNumberMobile = telecomMobileNumber.contactNumber?if_exists />
    <#if (contactNumberMobile?has_content) && (contactNumberMobile?length gt 6)>
        <#assign contactNumber3Mobile = contactNumberMobile.substring(0, 3) />
        <#assign contactNumber4Mobile = contactNumberMobile.substring(3, 7) />
    </#if>
</#if>

<div class="displayBoxHeader">
    <span class="displayBoxHeaderCaption">${addressEntryBoxHeading!"Address"}</span>
    ${addressEntryInfo!}
</div>
<p class="instructions">${StringUtil.wrapString(addressInstructionsInfo!"")}</p>
<div>
    <@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS_ERROR"/>
</div>
<input type="hidden" id="${fieldPurpose?if_exists}AddressContactMechId" name="${fieldPurpose?if_exists}AddressContactMechId" value="${postalAddressContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}HomePhoneContactMechId" name="${fieldPurpose?if_exists}HomePhoneContactMechId" value="${telecomHomeNoContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}MobilePhoneContactMechId" name="${fieldPurpose?if_exists}MobilePhoneContactMechId" value="${telecomMobileNoContactMechId!""}"/>
<input type="hidden" id="${fieldPurpose?if_exists}_ADDRESS_ALLOW_SOL" name="${fieldPurpose?if_exists}_ADDRESS_ALLOW_SOL" value="N"/>
<#if isShipping?has_content && isShipping == "Y">
    <div class="entry">
        <input type="checkbox" class="checkbox" name="isSameAsBilling" id="isSameAsBilling" value="${requestParameters.isSameAsBilling!isSameAsBilling!"Y"}" <#if requestParameters.isSameAsBilling?has_content || isSameAsBilling?has_content>checked</#if> />
        <label for="isSameAsBilling">${uiLabelMap.SameAsBillingCaption}</label>
    </div>
    <div id="${fieldPurpose?if_exists}_AddressSection">
</#if>
<#include "component://osafe/webapp/osafe/common/entry/addressNameFieldEntry.ftl"/>
<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(PCA_ACTIVE_FLAG!"") && loadPca?has_content && loadPca == "Y">
    <#include "component://osafe/webapp/osafe/common/entry/capturePlusEntry.ftl"/>
    <#include "component://osafe/webapp/osafe/common/entry/js/capturePlusEntryJS.ftl"/>
</#if>
<#include "component://osafe/webapp/osafe/common/entry/addressCommonFieldEntry.ftl"/>
<#include "component://osafe/webapp/osafe/common/entry/addressContactFieldEntry.ftl"/>
<#if isShipping?has_content && isShipping == "Y">
    </div>
</#if>
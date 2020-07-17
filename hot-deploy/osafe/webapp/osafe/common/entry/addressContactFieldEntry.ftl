<!-- address email entry -->
<#if showEmailAddr?has_content && showEmailAddr == "Y">
    <div class="entry">
        <label for="${fieldPurpose?if_exists}_EMAIL_ADDR"><@required/>${uiLabelMap.EmailAddressCaption}</label>
        <input type="text" maxlength="100" name="${fieldPurpose?if_exists}_EMAIL_ADDR" id="${fieldPurpose?if_exists}_EMAIL_ADDR" value="${requestParameters.get(fieldPurpose+"_EMAIL_ADDR")!emailLogin!""}"/>
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_EMAIL_ADDR"/>
    </div>
</#if>

<!-- address home phone entry -->
<#if showHomePhone?has_content && showHomePhone == "Y">
    <div class="entry">
      <label for="${fieldPurpose?if_exists}_HOME_CONTACT">
          <#if homePhoneRequired?has_content && homePhoneRequired == "Y"><@required/></#if>${homePhoneCaption!}
      </label>
      <span class="${fieldPurpose?if_exists}_USA ${fieldPurpose?if_exists}_CAN">
          <input type="text" class="phone3" id="${fieldPurpose?if_exists}_HOME_AREA" name="${fieldPurpose?if_exists}_HOME_AREA" maxlength="3" value="${requestParameters.get(fieldPurpose+"_HOME_AREA")!areaCodeHome!""}" />
          <input type="hidden" id="${fieldPurpose?if_exists}_HOME_CONTACT" name="${fieldPurpose?if_exists}_HOME_CONTACT" value="${requestParameters.get(fieldPurpose+"_HOME_CONTACT")!contactNumberHome!""}"/>
          <input type="hidden" id="${fieldPurpose?if_exists}_HOME_REQUIRED" name="${fieldPurpose?if_exists}_HOME_REQUIRED" value="true"/>
          <input type="text" class="phone3" id="${fieldPurpose?if_exists}_HOME_CONTACT3" name="${fieldPurpose?if_exists}_HOME_CONTACT3" value="${requestParameters.get(fieldPurpose+"_HOME_CONTACT3")!contactNumber3Home!""}" maxlength="3" />
          <input type="text" class="phone4" id="${fieldPurpose?if_exists}_HOME_CONTACT4" name="${fieldPurpose?if_exists}_HOME_CONTACT4" value="${requestParameters.get(fieldPurpose+"_HOME_CONTACT4")!contactNumber4Home!""}" maxlength="4" />
      </span>
      <span style="display:none" class="${fieldPurpose?if_exists}_OTHER">
          <input type="text" class="address" id="${fieldPurpose?if_exists}_HOME_CONTACT_OTHER" name="${fieldPurpose?if_exists}_HOME_CONTACT_OTHER" value="${requestParameters.get(fieldPurpose+"_HOME_CONTACT_OTHER")!contactNumberHome!""}" />
      </span>
      ${addressHomePhoneInfo!""}
      <@fieldErrors fieldName="${fieldPurpose?if_exists}_HOME_AREA"/>
      <@fieldErrors fieldName="${fieldPurpose?if_exists}_HOME_CONTACT"/>
    </div>
</#if>

<!-- address mobile phone entry -->
<#if showMobilePhone?has_content && showMobilePhone == "Y">
    <div class="entry">
        <label for="${fieldPurpose?if_exists}_HOME_CONTACT">${mobilePhoneCaption!}</label>
        <span class="${fieldPurpose?if_exists}_USA ${fieldPurpose?if_exists}_CAN">
            <input type="text" class="phone3" id="${fieldPurpose?if_exists}_MOBILE_AREA" name="${fieldPurpose?if_exists}_MOBILE_AREA" maxlength="3" value="${requestParameters.get(fieldPurpose+"_MOBILE_AREA")!areaCodeMobile!""}" />
            <input type="hidden" id="${fieldPurpose?if_exists}_MOBILE_CONTACT" name="${fieldPurpose?if_exists}_MOBILE_CONTACT" value="${requestParameters.get(fieldPurpose+"_MOBILE_CONTACT")!contactNumberMobile!""}"/>
            <input type="text" class="phone3" id="${fieldPurpose?if_exists}_MOBILE_CONTACT3" name="${fieldPurpose?if_exists}_MOBILE_CONTACT3" value="${requestParameters.get(fieldPurpose+"_MOBILE_CONTACT3")!contactNumber3Mobile!""}" maxlength="3"/>
            <input type="text" class="phone4" id="${fieldPurpose?if_exists}_MOBILE_CONTACT4" name="${fieldPurpose?if_exists}_MOBILE_CONTACT4" value="${requestParameters.get(fieldPurpose+"_MOBILE_CONTACT4")!contactNumber4Mobile!""}" maxlength="4"/>
        </span>
        <span style="display:none" class="${fieldPurpose?if_exists}_OTHER">
            <input type="text" class="address" id="${fieldPurpose?if_exists}_MOBILE_CONTACT_OTHER" name="${fieldPurpose?if_exists}_MOBILE_CONTACT_OTHER" value="${requestParameters.get(fieldPurpose+"_MOBILE_CONTACT_OTHER")!contactNumberMobile!""}" />
        </span>
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_MOBILE_AREA"/>
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_MOBILE_CONTACT"/>
    </div>
</#if>
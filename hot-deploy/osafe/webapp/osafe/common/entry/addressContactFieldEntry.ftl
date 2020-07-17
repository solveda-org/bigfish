<!-- address email entry -->
<#if showEmailAddr?has_content && showEmailAddr == "Y">
    <div class="entry">
        <label for="${fieldPurpose?if_exists}_EMAIL_ADDR"><@required/>${uiLabelMap.EmailAddressCaption}</label>
        <input type="text" maxlength="100" name="${fieldPurpose?if_exists}_EMAIL_ADDR" id="${fieldPurpose?if_exists}_EMAIL_ADDR" value="${requestParameters.get(fieldPurpose+"_EMAIL_ADDR")!emailLogin!""}"/>
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_EMAIL_ADDR"/>
    </div>
</#if>

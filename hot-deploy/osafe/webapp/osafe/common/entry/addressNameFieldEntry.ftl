<#if showAttnName?has_content && showAttnName == "Y">
    <!-- address nick name -->
    <div class="entry">
      <label for="${fieldPurpose?if_exists}_ATTN_NAME"><@required/>${uiLabelMap.AddressNickNameCaption}</label>
      <input type="text" maxlength="100" class="addressNickName" name="${fieldPurpose?if_exists}_ATTN_NAME" id="${fieldPurpose?if_exists}_ATTN_NAME" value="${requestParameters.get(fieldPurpose+"_ATTN_NAME")!attnName!""}" />
      <@fieldErrors fieldName="${fieldPurpose?if_exists}_ATTN_NAME"/>
    </div>
</#if>

<#if showName?has_content && showName == "Y">
    <!-- address first name -->
    <div class="entry">
      <label for="${fieldPurpose?if_exists}_FIRST_NAME"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <input type="text" maxlength="100" class="firstName" name="${fieldPurpose?if_exists}_FIRST_NAME" id="${fieldPurpose?if_exists}_FIRST_NAME" value="${requestParameters.get(fieldPurpose+"_FIRST_NAME")!firstName!""}" />
      <@fieldErrors fieldName="${fieldPurpose?if_exists}_FIRST_NAME"/>
    </div>
    
    <!-- address last name -->
    <div class="entry">
      <label for="${fieldPurpose?if_exists}_LAST_NAME"><@required/>${uiLabelMap.LastNameCaption}</label>
      <input type="text" maxlength="100" class="lastName" name="${fieldPurpose?if_exists}_LAST_NAME" id="${fieldPurpose?if_exists}_LAST_NAME" value="${requestParameters.get(fieldPurpose+"_LAST_NAME")!lastName!""}" />
      <@fieldErrors fieldName="${fieldPurpose?if_exists}_LAST_NAME"/>
    </div>
</#if>
<#if postalAddress?has_content>
    <#assign attnName = postalAddress.attnName!postalAddress.address1!"" />
</#if>
<div class = "addressInfoNickname">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.AddressNickNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="addressNickName" name="${fieldPurpose?if_exists}_attnName" id="${fieldPurpose?if_exists}_attnName" value="${parameters.get("${fieldPurpose?if_exists}_attnName")!attnName!""}" />
            </div>
        </div>
    </div>
</div>
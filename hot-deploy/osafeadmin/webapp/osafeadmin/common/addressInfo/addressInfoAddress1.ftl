<#if postalAddress?has_content>
    <#assign address1 = postalAddress.address1!"">
</#if>
<!-- address Line1 entry -->
<div class = "addressInfoAddress1">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.Address1Caption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_address1" id="${fieldPurpose?if_exists}_address1" value="${parameters.get("${fieldPurpose?if_exists}_address1")!address1!""}" />
            </div>
        </div>
    </div>
</div>
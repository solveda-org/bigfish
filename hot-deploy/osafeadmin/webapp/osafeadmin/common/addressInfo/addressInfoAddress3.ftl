<#if postalAddress?has_content>
    <#assign address3 = postalAddress.address3!"">
</#if>
<!-- address Line3 entry -->
<div class = "addressInfoAddress3">
    <div id="${fieldPurpose?if_exists}_STATE_TEXT" class="entry" style="display:none">
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption">
                    <label>${uiLabelMap.AddressLine3Caption}</label>
                </div>
                <div class="infoValue">
                    <input type="text" maxlength="100" class="address" name="${fieldPurpose?if_exists}_address3" id="${fieldPurpose?if_exists}_address3" value="${parameters.get("${fieldPurpose?if_exists}_address3")!address3!""}" />
                </div>
            </div>
        </div>
    </div>
</div>
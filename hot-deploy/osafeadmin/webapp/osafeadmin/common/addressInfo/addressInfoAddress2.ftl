<#if postalAddress?has_content>
    <#assign address2 = postalAddress.address2!"">
</#if>
<!-- address Line2 entry -->
<div class = "addressInfoAddress2">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.Address2Caption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_address2" id="${fieldPurpose?if_exists}_address2" value="${parameters.get("${fieldPurpose?if_exists}_address2")!address2!""}" />
            </div>
        </div>
    </div>
</div>
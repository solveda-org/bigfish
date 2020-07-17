<#if postalAddress?has_content>
    <#assign address3 = postalAddress.address3!"">
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<!-- address Line3 entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.AddressLine3Caption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="address" name="${fieldPurpose?if_exists}_address3" id="${fieldPurpose?if_exists}_address3" value="${parameters.get("${fieldPurpose?if_exists}_address3")!address3!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_address3_mandatory" name="${fieldPurpose?if_exists}_address3_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>

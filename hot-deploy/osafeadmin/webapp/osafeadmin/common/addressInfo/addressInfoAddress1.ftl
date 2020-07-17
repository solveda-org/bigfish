<#if postalAddress?has_content>
    <#assign address1 = postalAddress.address1!"">
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<!-- address Line1 entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.Address1Caption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_address1" id="${fieldPurpose?if_exists}_address1" value="${parameters.get("${fieldPurpose?if_exists}_address1")!address1!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_address1_mandatory" name="${fieldPurpose?if_exists}_address1_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
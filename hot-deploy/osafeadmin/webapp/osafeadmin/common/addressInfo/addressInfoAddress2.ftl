<#if postalAddress?has_content>
    <#assign address2 = postalAddress.address2!"">
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<!-- address Line2 entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.Address2Caption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_address2" id="${fieldPurpose?if_exists}_address2" value="${parameters.get("${fieldPurpose?if_exists}_address2")!address2!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_address2_mandatory" name="${fieldPurpose?if_exists}_address2_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
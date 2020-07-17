<#assign fullName = ""/>
<#if postalAddress?has_content>
    <#assign fullName = postalAddress.toName!"" />
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.AddressFullNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="addressFullName" name="${fieldPurpose?if_exists}_fullName" id="${fieldPurpose?if_exists}_fullName" value="${parameters.get("${fieldPurpose?if_exists}_fullName")!fullName!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_fullName_mandatory" name="${fieldPurpose?if_exists}_fullName_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>

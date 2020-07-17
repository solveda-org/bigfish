<#if postalAddress?has_content>
    <#assign attnName = postalAddress.attnName!postalAddress.address1!"" />
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.AddressNickNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="addressNickName" name="${fieldPurpose?if_exists}_attnName" id="${fieldPurpose?if_exists}_attnName" value="${parameters.get("${fieldPurpose?if_exists}_attnName")!attnName!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_attnName_mandatory" name="${fieldPurpose?if_exists}_attnName_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
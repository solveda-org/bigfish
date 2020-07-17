<#assign firstName = ""/>
<#if postalAddress?has_content>
    <#assign toName = postalAddress.toName!"" />
    <#if toName?has_content>
      <#assign fullName = toName.split(" ")!/>
      <#if fullName?has_content>
        <#assign firstName = fullName[0]!/>
      </#if>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.FirstNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="addressFirstName" name="${fieldPurpose?if_exists}_firstName" id="${fieldPurpose?if_exists}_firstName" value="${parameters.get("${fieldPurpose?if_exists}_firstName")!firstName!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_firstName_mandatory" name="${fieldPurpose?if_exists}_firstName_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
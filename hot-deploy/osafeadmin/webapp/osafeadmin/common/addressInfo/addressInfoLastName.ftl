<#assign lastName = ""/>
<#if postalAddress?has_content>
    <#assign toName = postalAddress.toName!"" />
    <#if toName?has_content>
      <#assign fullName = toName.split(" ")!/>
      <#if fullName?has_content>
        <#assign fullNameSize = fullName?size/>
        <#if fullNameSize &gt; 1>
          <#assign lastName = fullName[fullNameSize-1]!/>
        </#if>
      </#if>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.LastNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="addressLastName" name="${fieldPurpose?if_exists}_lastName" id="${fieldPurpose?if_exists}_lastName" value="${parameters.get("${fieldPurpose?if_exists}_lastName")!lastName!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_lastName_mandatory" name="${fieldPurpose?if_exists}_lastName_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
<#if postalAddress?has_content>
    <#assign postalCode = postalAddress.postalCode!"">
    <#if postalCode?has_content && postalCode == '_NA_'>
      <#assign postalCode = "">
    </#if>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<!-- address zip entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
					<#if mandatory == "Y"><span class="required">*</span></#if>
                    <span>${uiLabelMap.ZipOrPostalCodeCaption}</span>
                </label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="60" class="postalCode" name="${fieldPurpose?if_exists}_postalCode" id="${fieldPurpose?if_exists}_postalCode" value="${parameters.get("${fieldPurpose?if_exists}_postalCode")!postalCode!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_postalCode_mandatory" name="${fieldPurpose?if_exists}_postalCode_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
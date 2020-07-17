<#if postalAddress?has_content>
    <#assign postalCode = postalAddress.postalCode!"">
    <#if postalCode?has_content && postalCode == '_NA_'>
      <#assign postalCode = "">
    </#if>
</#if>
<!-- address zip entry -->
<div class = "addressInfoZipPostcode">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
					<span class="required">*</span>
                    <span class="${fieldPurpose?if_exists}_USA">${uiLabelMap.ZipCodeCaption}</span>
                    <span class="${fieldPurpose?if_exists}_CAN ${fieldPurpose?if_exists}_OTHER">${uiLabelMap.PostalCodeCaption}</span>
                </label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="60" class="postalCode" name="${fieldPurpose?if_exists}_postalCode" id="${fieldPurpose?if_exists}_postalCode" value="${parameters.get("${fieldPurpose?if_exists}_postalCode")!postalCode!""}" />
            </div>
        </div>
    </div>
</div>
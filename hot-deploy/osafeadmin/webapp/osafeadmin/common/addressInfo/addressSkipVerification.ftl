<!-- address skip verification -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption"><label>&nbsp;</label></div>
            <div class="entry checkbox medium">
                 <input type="checkbox" id="${fieldPurpose?if_exists}_skipVerification" name="${fieldPurpose?if_exists}_skipVerification" value="Y" <#if requestParameters.get(fieldPurpose+"_skipVerification")?has_content && requestParameters.get(fieldPurpose+"_skipVerification") == "Y">checked</#if>/>${uiLabelMap.AddressSkipVerificationLabel}
            </div>
        </div>
    </div>
</div>
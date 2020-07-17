<#if postalAddress?has_content>
    <#assign city = postalAddress.city!"">
</#if>
<!-- address city entry -->
<div class = "addressInfoCityTown">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
				    <span class="required">*</span>
                    <span class="${fieldPurpose?if_exists}_USA ${fieldPurpose?if_exists}_CAN">${uiLabelMap.CityCaption}</span>
                    <span class="${fieldPurpose?if_exists}_OTHER">${uiLabelMap.TownOrCityCaption}</span>
                </label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="city" name="${fieldPurpose?if_exists}_city" id="${fieldPurpose?if_exists}_city" value="${parameters.get("${fieldPurpose?if_exists}_city")!city!""}" />
            </div>
        </div>
    </div>
</div>
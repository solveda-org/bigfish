<#if postalAddress?has_content>
    <#assign city = postalAddress.city!"">
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<!-- address city entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.TownOrCityCaption}
                </label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" class="city" name="${fieldPurpose?if_exists}_city" id="${fieldPurpose?if_exists}_city" value="${parameters.get("${fieldPurpose?if_exists}_city")!city!""}" />
                <input type="hidden" id="${fieldPurpose?if_exists}_city_mandatory" name="${fieldPurpose?if_exists}_city_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
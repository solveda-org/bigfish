<#if postalAddress?has_content>
    <#assign countryGeoId = postalAddress.countryGeoId!"">
</#if>
<#assign  selectedCountry = parameters.get("${fieldPurpose?if_exists}_country")!countryGeoId?if_exists/>
<#if !selectedCountry?has_content>
    <#if defaultCountryGeoMap?exists>
        <#assign selectedCountry = defaultCountryGeoMap.geoId/>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<!-- address country entry -->
<div class="${request.getAttribute("attributeClass")!}">
    <#if Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(COUNTRY_MULTI!"")>
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption">
                    <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.CountryCaption}</label>
                </div>
                <div class="infoValue">
                    <select name="${fieldPurpose?if_exists}_country" id="${fieldPurpose?if_exists}_country" class="dependentSelectMaster">
                        <#list countryList as country>
                            <option value='${country.geoId}' <#if selectedCountry = country.geoId >selected=selected</#if>>${country.get("geoName")?default(country.geoId)}</option>
                        </#list>
                    </select>
                </div>
            </div>
        </div>
    <#else>
        <input type="hidden" name="${fieldPurpose?if_exists}_country" id="${fieldPurpose?if_exists}_country" value="${selectedCountry}"/>
    </#if>
    <input type="hidden" id="${fieldPurpose?if_exists}_country_mandatory" name="${fieldPurpose?if_exists}_country_mandatory" value="${mandatory}"/>
</div>

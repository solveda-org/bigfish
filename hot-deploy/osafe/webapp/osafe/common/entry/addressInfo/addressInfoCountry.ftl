<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign countryGeoId = postalAddressData.countryGeoId!"">
</#if>
<#assign  selectedCountry = parameters.get(fieldPurpose+"_COUNTRY")!countryGeoId?if_exists/>
<#if !selectedCountry?has_content>
    <#if defaultCountryGeoMap?exists>
        <#assign selectedCountry = defaultCountryGeoMap.geoId/>
    </#if>
</#if>

<!-- address country entry -->
<div class="addressInfoCountry">
    <#if COUNTRY_MULTI?has_content && Static["com.osafe.util.Util"].isProductStoreParmTrue(COUNTRY_MULTI)>
        <div class="entry">
            <label for="${fieldPurpose?if_exists}_COUNTRY"><@required/>${uiLabelMap.CountryCaption}</label>
            <select name="${fieldPurpose?if_exists}_COUNTRY" id="${fieldPurpose?if_exists}_COUNTRY" class="dependentSelectMaster">
                <#list countryList as country>
                    <option value='${country.geoId}' <#if selectedCountry = country.geoId >selected=selected</#if>>${country.get("geoName")?default(country.geoId)}</option>
                </#list>
            </select>
        </div>
    <#else>
        <input type="hidden" name="${fieldPurpose?if_exists}_COUNTRY" id="${fieldPurpose?if_exists}_COUNTRY" value="${selectedCountry}"/>
    </#if>
</div>

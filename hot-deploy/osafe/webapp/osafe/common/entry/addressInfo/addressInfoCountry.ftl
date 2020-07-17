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
<#if !Static["com.osafe.util.Util"].isProductStoreParmTrue(COUNTRY_MULTI!"")>
	<#assign selectedCountry = defaultCountryGeoMap.geoId/>
</#if>

<#if parameters.fbLocationCountry?has_content> 
	<#assign fbUserCountryList = delegator.findByAndCache("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoName" , parameters.fbLocationCountry, "geoTypeId", "COUNTRY"))/>
	<#if fbUserCountryList?has_content>
		<#assign fbUserCountry = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(fbUserCountryList)/>  
		<#assign selectedCountry = fbUserCountry.geoId/>
	</#if>
</#if>


<!-- address country entry -->
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <label for="${fieldPurpose?if_exists}_COUNTRY"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.CountryCaption}</label>
    <div class="entryField">
    	<select name="${fieldPurpose?if_exists}_COUNTRY" id="js_${fieldPurpose?if_exists}_COUNTRY" class="dependentSelectMaster">
        	<#list countryList as country>
        		<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(COUNTRY_MULTI!"") || selectedCountry == country.geoId>
            		<option value='${country.geoId}' <#if selectedCountry == country.geoId >selected=selected</#if>>${country.get("geoName")?default(country.geoId)}</option>
            	</#if>
        	</#list>
    	</select>
    	<input type="hidden" id="${fieldPurpose?if_exists}_COUNTRY_MANDATORY" name="${fieldPurpose?if_exists}_COUNTRY_MANDATORY" value="${mandatory}"/>
    	<@fieldErrors fieldName="${fieldPurpose?if_exists}_COUNTRY"/>
    </div>
</div>

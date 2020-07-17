<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign stateProvinceGeoId = postalAddressData.stateProvinceGeoId!"">
    <#assign countryGeoId = postalAddressData.countryGeoId!"">
</#if>
<!-- address state entry -->
<#assign  selectedCountry = parameters.get(fieldPurpose+"_COUNTRY")!countryGeoId?if_exists/>
<#if !selectedCountry?has_content>
    <#if defaultCountryGeoMap?exists>
        <#assign selectedCountry = defaultCountryGeoMap.geoId/>
    </#if>
</#if>
<#if !Static["com.osafe.util.Util"].isProductStoreParmTrue(COUNTRY_MULTI!"")>
	<#assign selectedCountry = defaultCountryGeoMap.geoId/>
</#if>
<#assign  selectedState = parameters.get(fieldPurpose+"_STATE")!stateProvinceGeoId?if_exists/>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div id="${fieldPurpose?if_exists}_STATES" class="state">
        <label for="${fieldPurpose?if_exists}_STATE"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.StateOrProvinceCaption}
            <span id="advice-required-${fieldPurpose?if_exists}_STATE" style="display:none" class="errorMessage">(${uiLabelMap.CommonRequired})</span>
        </label>
       <div class="entryField">
        <select id="js_${fieldPurpose?if_exists}_STATE" name="${fieldPurpose?if_exists}_STATE" class="select ${fieldPurpose?if_exists}_COUNTRY">
            <#list countryList as country>
                <#if country.geoId == selectedCountry>
                  <#assign stateMap = dispatcher.runSync("getAssociatedStateList", Static["org.ofbiz.base.util.UtilMisc"].toMap("countryGeoId", country.geoId, "userLogin", userLogin, "listOrderBy", "geoCode"))/>
                  <#assign stateList = stateMap.stateList />
                  <#-- assign stateList = Static["org.ofbiz.common.CommonWorkers"].getAssociatedStateList(delegator, country.geoId) /-->
                  <#if stateList?has_content>
                      <#list stateList as state>
                          <option value="${state.geoId!}" <#if selectedState?exists && selectedState == state.geoId!>selected=selected</#if>>${state.geoName?default(state.geoId!)}</option>
                      </#list>
                  </#if>
                </#if>
            </#list>
        </select>
        <input type="hidden" id="${fieldPurpose?if_exists}_STATE_MANDATORY" name="${fieldPurpose?if_exists}_STATE_MANDATORY" value="${mandatory}"/>
        <#if stateList?has_content>
            <input type="hidden" id="${fieldPurpose?if_exists}_STATE_LIST_FIELD_MANDATORY" name="${fieldPurpose?if_exists}_STATE_LIST_FIELD_MANDATORY" value="${mandatory}"/>
            <input type="hidden" id="${fieldPurpose?if_exists}_STATE_LIST_FIELD" name="${fieldPurpose?if_exists}_STATE_LIST_FIELD" value=""/>
        </#if>
        <@fieldErrors fieldName="${fieldPurpose?if_exists}_STATE"/>
       </div>
    </div>
</div>
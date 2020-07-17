<#if postalAddress?has_content>
    <#assign stateProvinceGeoId = postalAddress.stateProvinceGeoId!"">
    <#assign countryGeoId = postalAddress.countryGeoId!"">
</#if>
<!-- address state entry -->
<#assign  selectedCountry = parameters.get("${fieldPurpose?if_exists}_country")!countryGeoId?if_exists/>
<#if !selectedCountry?has_content>
    <#if defaultCountryGeoMap?exists>
        <#assign selectedCountry = defaultCountryGeoMap.geoId/>
    </#if>
</#if>
<#assign  selectedState = parameters.get("${fieldPurpose?if_exists}_state")!stateProvinceGeoId?if_exists/>
<div class = "addressInfoStateProvince">
    <div class="infoRow" id="${fieldPurpose?if_exists}_STATES">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>
				    <span class="required">*</span>
                    <span class="${fieldPurpose?if_exists}_USA">${uiLabelMap.StateCaption}</span>
                    <span class="${fieldPurpose?if_exists}_CAN">${uiLabelMap.ProvinceCaption}</span>
                    <span class="${fieldPurpose?if_exists}_OTHER">${uiLabelMap.StateOrProvinceCaption}</span>
                </label>
            </div>
            <div class="infoValue">
                <select id="${fieldPurpose?if_exists}_state" name="${fieldPurpose?if_exists}_state" class="select ${fieldPurpose?if_exists}_country">
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
                <#if stateList?has_content>
                    <input type="hidden" name="${fieldPurpose?if_exists}_StateListExist" value="" id="${fieldPurpose?if_exists}_StateListExist"/>
                </#if>
            </div>
        </div>
    </div>
</div>
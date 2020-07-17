<!-- address country entry -->
<#assign  selectedCountry = parameters.get(fieldPurpose+"_COUNTRY")!countryGeoId?if_exists/>
<#assign  selectedState = parameters.get(fieldPurpose+"_STATE")!stateProvinceGeoId?if_exists/>
<#if !selectedCountry?has_content>
    <#if defaultCountryGeoMap?exists>
        <#assign selectedCountry = defaultCountryGeoMap.geoId/>
    </#if>
</#if>
<#if COUNTRY_MULTI?has_content && Static["com.osafe.util.Util"].isProductStoreParmTrue(COUNTRY_MULTI)>
    <div class="entry">
        <label for="${fieldPurpose?if_exists}_COUNTRY"><@required/>${uiLabelMap.CountryCaption}</label>
        <select name="${fieldPurpose?if_exists}_COUNTRY" id="${fieldPurpose?if_exists}_COUNTRY" class="dependentSelectMaster required">
            <#list countryList as country>
                <option value='${country.geoId}' <#if selectedCountry = country.geoId >selected=selected</#if>>${country.get("geoName")?default(country.geoId)}</option>
            </#list>
        </select>
    </div>
<#else>
    <input type="hidden" name="${fieldPurpose?if_exists}_COUNTRY" id="${fieldPurpose?if_exists}_COUNTRY" value="${selectedCountry}"/>
</#if>

<!-- address Line1 entry -->
<div class="entry">
  <label for="${fieldPurpose?if_exists}_ADDRESS1"><@required/>${uiLabelMap.AddressLine1Caption}</label>
  <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_ADDRESS1" id="${fieldPurpose?if_exists}_ADDRESS1" value="${requestParameters.get(fieldPurpose+"_ADDRESS1")!address1!""}" />
  <@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS1"/>
</div>

<!-- address Line2 entry -->
<div class="entry">
    <label for="${fieldPurpose?if_exists}_ADDRESS2">${uiLabelMap.AddressLine2Caption}</label>
    <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_ADDRESS2" id="${fieldPurpose?if_exists}_ADDRESS2" value="${requestParameters.get(fieldPurpose+"_ADDRESS2")!address2!""}" />
    <@fieldErrors fieldName="${fieldPurpose?if_exists}_ADDRESS2"/>
</div>

<!-- address Line3 entry -->
<div id="${fieldPurpose?if_exists}_STATE_TEXT" class="entry" style="display:none">
    <label for="${fieldPurpose?if_exists}_ADDRESS3"><span id="address3Label">${uiLabelMap.AddressLine3Caption}</span></label>
    <input type="text" maxlength="100" class="address" name="${fieldPurpose?if_exists}_ADDRESS3" id="${fieldPurpose?if_exists}_ADDRESS3" value="${requestParameters.get(fieldPurpose+"_ADDRESS3")!address3!""}" />
</div>

<!-- address city entry -->
<div id="city" class="entry">
    <label for="${fieldPurpose?if_exists}_CITY"><@required/>
        <span class="${fieldPurpose?if_exists}_USA ${fieldPurpose?if_exists}_CAN">${uiLabelMap.CityCaption}</span>
        <span class="${fieldPurpose?if_exists}_OTHER">${uiLabelMap.TownOrCityCaption}</span>
    </label>
    <input type="text" maxlength="100" class="city" name="${fieldPurpose?if_exists}_CITY" id="${fieldPurpose?if_exists}_CITY" value="${requestParameters.get(fieldPurpose+"_CITY")!city!""}" />
    <@fieldErrors fieldName="${fieldPurpose?if_exists}_CITY"/>
</div>

<!-- address state entry -->
<div id="${fieldPurpose?if_exists}_STATES" class="entry">
    <label for="${fieldPurpose?if_exists}_STATE">
        <span class="${fieldPurpose?if_exists}_USA">${uiLabelMap.StateCaption}</span>
        <span class="${fieldPurpose?if_exists}_CAN">${uiLabelMap.ProvinceCaption}</span>
        <span class="${fieldPurpose?if_exists}_OTHER">${uiLabelMap.StateOrProvinceCaption}</span>
        <span id="advice-required-${fieldPurpose?if_exists}_STATE" style="display:none" class="errorMessage">(${uiLabelMap.CommonRequired})</span>
    </label>
    <select id="${fieldPurpose?if_exists}_STATE" name="${fieldPurpose?if_exists}_STATE" class="select ${fieldPurpose?if_exists}_COUNTRY">
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
    <@fieldErrors fieldName="${fieldPurpose?if_exists}_STATE"/>
    <#if stateList?has_content>
        <input type="hidden" name="${fieldPurpose?if_exists}_STATE_LIST_FIELD" value="" id="${fieldPurpose?if_exists}_STATE_LIST_FIELD"/>
    </#if>
</div>

<!-- address zip entry -->
<div class="entry">
    <label for="${fieldPurpose?if_exists}_POSTAL_CODE"><@required/>
        <span class="${fieldPurpose?if_exists}_USA">${uiLabelMap.ZipCodeCaption}</span>
        <span class="${fieldPurpose?if_exists}_CAN ${fieldPurpose?if_exists}_OTHER">${uiLabelMap.PostalCodeCaption}</span>
    </label>
    <input type="text" maxlength="60" class="postalCode" name="${fieldPurpose?if_exists}_POSTAL_CODE" id="${fieldPurpose?if_exists}_POSTAL_CODE" value="${requestParameters.get(fieldPurpose+"_POSTAL_CODE")!postalCode!""}" />
    <@fieldErrors fieldName="${fieldPurpose?if_exists}_POSTAL_CODE"/>
</div>
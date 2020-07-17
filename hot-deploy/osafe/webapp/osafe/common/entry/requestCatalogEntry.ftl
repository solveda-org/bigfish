<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery('#REQ_CATALOG_COUNTRY')) {
            if(!jQuery('#REQ_CATALOG_STATE_LIST_FIELD').length) {
                getAssociatedStateList('REQ_CATALOG_COUNTRY', 'REQ_CATALOG_STATE', 'advice-required-REQ_CATALOG_STATE', 'REQ_CATALOG_STATES', 'REQ_CATALOG_STATE_TEXT');
            }
            getAddressFormat("REQ_CATALOG");
            jQuery('#REQ_CATALOG_COUNTRY').change(function(){
                getAssociatedStateList('REQ_CATALOG_COUNTRY', 'REQ_CATALOG_STATE', 'advice-required-REQ_CATALOG_STATE', 'REQ_CATALOG_STATES', 'REQ_CATALOG_STATE_TEXT');
                getAddressFormat("REQ_CATALOG");
            });
        }
    });
</script>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<input type="hidden" name="partyIdFrom" value="${(userLogin.partyId)?if_exists}" />
<input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
<input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS" />
<input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI" />
<input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
<input type="hidden" name="productStoreName"  value="${productStore.storeName}" />
<input type="hidden" name="emailType" value="REQCAT_NOTI_EMAIL" />
<input type="hidden" name="custRequestTypeId" value="${custRequestTypeId!""}" />
<input type="hidden" name="custRequestName" value="${custRequestName!""}" />
<input type="hidden" name="note" value="${Static["org.ofbiz.base.util.UtilHttp"].getFullRequestUrl(request).toString()}" />
<div id="REQ_CATALOG_ADDRESS_ENTRY" class="displayBox">
    <div>
        <@fieldErrors fieldName="REQ_CATALOG_ADDRESS_ERROR"/>
    </div>
    <div class="entry">
      <label for="REQ_CATALOG_FIRST_NAME"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <input type="text" maxlength="100" class="firstName" name="REQ_CATALOG_FIRST_NAME" id="REQ_CATALOG_FIRST_NAME" value="${requestParameters.get("REQ_CATALOG_FIRST_NAME")!firstName!""}" />
      <@fieldErrors fieldName="REQ_CATALOG_FIRST_NAME"/>
    </div>
    
    <div class="entry">
      <label for="REQ_CATALOG_LAST_NAME"><@required/>${uiLabelMap.LastNameCaption}</label>
      <input type="text" maxlength="100" class="lastName" name="REQ_CATALOG_LAST_NAME" id="REQ_CATALOG_LAST_NAME" value="${requestParameters.get("REQ_CATALOG_LAST_NAME")!lastName!""}" />
      <@fieldErrors fieldName="REQ_CATALOG_LAST_NAME"/>
    </div>

    <#assign  selectedCountry = parameters.get("REQ_CATALOG_COUNTRY")!countryGeoId?if_exists/>
    <#assign  selectedState = parameters.get("REQ_CATALOG_STATE")!stateProvinceGeoId?if_exists/>
    <#if !selectedCountry?has_content>
        <#if defaultCountryGeoMap?exists>
            <#assign selectedCountry = defaultCountryGeoMap.geoId/>
        </#if>
    </#if>
    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"COUNTRY_MULTI")>
        <div class="entry">
            <label for="REQ_CATALOG_COUNTRY"><@required/>${uiLabelMap.CountryCaption}</label>
            <select name="REQ_CATALOG_COUNTRY" id="REQ_CATALOG_COUNTRY" class="dependentSelectMaster">
                <#list countryList as country>
                    <option value='${country.geoId}' <#if selectedCountry = country.geoId >selected=selected</#if>>${country.get("geoName")?default(country.geoId)}</option>
                </#list>
            </select>
        </div>
    <#else>
        <input type="hidden" name="REQ_CATALOG_COUNTRY" id="REQ_CATALOG_COUNTRY" value="${selectedCountry}"/>
    </#if>
    
    <!-- address Line1 entry -->
    <div class="entry">
      <label for="REQ_CATALOG_ADDRESS1"><@required/>${uiLabelMap.AddressLine1Caption}</label>
      <input type="text" maxlength="255" class="address" name="REQ_CATALOG_ADDRESS1" id="REQ_CATALOG_ADDRESS1" value="${requestParameters.get("REQ_CATALOG_ADDRESS1")!address1!""}" />
      <@fieldErrors fieldName="REQ_CATALOG_ADDRESS1"/>
    </div>
    
    <!-- address Line2 entry -->
    <div class="entry">
        <label for="REQ_CATALOG_ADDRESS2">${uiLabelMap.AddressLine2Caption}</label>
        <input type="text" maxlength="255" class="address" name="REQ_CATALOG_ADDRESS2" id="REQ_CATALOG_ADDRESS2" value="${requestParameters.get("REQ_CATALOG_ADDRESS2")!address2!""}" />
        <@fieldErrors fieldName="REQ_CATALOG_ADDRESS2"/>
    </div>
    
    <!-- address Line3 entry -->
    <div id="REQ_CATALOG_STATE_TEXT" class="entry" style="display:none">
        <label for="REQ_CATALOG_ADDRESS3"><span id="address3Label">${uiLabelMap.AddressLine3Caption}</span></label>
        <input type="text" maxlength="100" class="address" name="REQ_CATALOG_ADDRESS3" id="REQ_CATALOG_ADDRESS3" value="${requestParameters.get("REQ_CATALOG_ADDRESS3")!address3!""}" />
    </div>
    
    <!-- address city entry -->
    <div id="city" class="entry">
        <label for="REQ_CATALOG_CITY"><@required/>
            <span class="REQ_CATALOG_USA REQ_CATALOG_CAN">${uiLabelMap.CityCaption}</span>
            <span class="REQ_CATALOG_OTHER">${uiLabelMap.TownOrCityCaption}</span>
        </label>
        <input type="text" maxlength="100" class="city" name="REQ_CATALOG_CITY" id="REQ_CATALOG_CITY" value="${requestParameters.get("REQ_CATALOG_CITY")!city!""}" />
        <@fieldErrors fieldName="REQ_CATALOG_CITY"/>
    </div>
    
    <!-- address state entry -->
    <div id="REQ_CATALOG_STATES" class="entry">
        <label for="REQ_CATALOG_STATE">
            <span class="REQ_CATALOG_USA"><@required/>${uiLabelMap.StateCaption}</span>
            <span class="REQ_CATALOG_CAN"><@required/>${uiLabelMap.ProvinceCaption}</span>
            <span class="REQ_CATALOG_OTHER">${uiLabelMap.StateOrProvinceCaption}</span>
            <span id="advice-required-REQ_CATALOG_STATE" style="display:none" class="errorMessage">(${uiLabelMap.CommonRequired})</span>
        </label>
        <select id="REQ_CATALOG_STATE" name="REQ_CATALOG_STATE" class="REQ_CATALOG_COUNTRY">
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
        <@fieldErrors fieldName="REQ_CATALOG_STATE"/>
        <#if stateList?has_content>
            <input type="hidden" name="REQ_CATALOG_STATE_LIST_FIELD" value="" id="REQ_CATALOG_STATE_LIST_FIELD"/>
        </#if>
    </div>
    
    <div class="entry">
        <label for="REQ_CATALOG_POSTAL_CODE"><@required/>
            <span class="REQ_CATALOG_USA">${uiLabelMap.ZipCodeCaption}</span>
            <span class="REQ_CATALOG_CAN REQ_CATALOG_OTHER">${uiLabelMap.PostalCodeCaption}</span>
        </label>
        <input type="text" maxlength="60" class="postalCode" name="REQ_CATALOG_POSTAL_CODE" id="REQ_CATALOG_POSTAL_CODE" value="${requestParameters.get("REQ_CATALOG_POSTAL_CODE")!postalCode!""}" />
        <@fieldErrors fieldName="REQ_CATALOG_POSTAL_CODE"/>
    </div>

    <div class="entry">
        <label for="REQ_CATALOG_EMAIL_ADDR"><@required/>${uiLabelMap.EmailAddressCaption}</label>
        <input type="text" maxlength="100" name="REQ_CATALOG_EMAIL_ADDR" id="REQ_CATALOG_EMAIL_ADDR" value="${requestParameters.get("REQ_CATALOG_EMAIL_ADDR")!emailLogin!""}"/>
        <@fieldErrors fieldName="REQ_CATALOG_EMAIL_ADDR"/>
    </div>

    <div class="entry">
        <label for="REQ_CATALOG_HOME_CONTACT">${uiLabelMap.ContactPhoneCaption}</label>
        <span class="REQ_CATALOG_USA REQ_CATALOG_CAN">
            <input type="text" class="phone3" id="REQ_CATALOG_HOME_AREA" name="REQ_CATALOG_HOME_AREA" maxlength="3" value="${requestParameters.get("REQ_CATALOG_HOME_AREA")!areaCodeHome!""}" />
            <input type="hidden" id="REQ_CATALOG_HOME_CONTACT" name="REQ_CATALOG_HOME_CONTACT" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT")!contactNumberHome!""}"/>
            <input type="hidden" id="REQ_CATALOG_HOME_REQUIRED" name="REQ_CATALOG_HOME_REQUIRED" value="true"/>
            <input type="text" class="phone3" id="REQ_CATALOG_HOME_CONTACT3" name="REQ_CATALOG_HOME_CONTACT3" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT3")!contactNumber3Home!""}" maxlength="3" />
            <input type="text" class="phone4" id="REQ_CATALOG_HOME_CONTACT4" name="REQ_CATALOG_HOME_CONTACT4" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT4")!contactNumber4Home!""}" maxlength="4" />
        </span>
        <span style="display:none" class="REQ_CATALOG_OTHER">
            <input type="text" class="address" id="REQ_CATALOG_HOME_CONTACT_OTHER" name="REQ_CATALOG_HOME_CONTACT_OTHER" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT_OTHER")!contactNumberHome!""}" />
        </span>
        <@fieldErrors fieldName="REQ_CATALOG_HOME_AREA"/>
        <@fieldErrors fieldName="REQ_CATALOG_HOME_CONTACT"/>
    </div>

    <div class="entry">
        <label for="content">${uiLabelMap.CommentCaption}</label>
        <textarea name="content" id="content" cols="50" rows="5" class="content">${parameters.content!""}</textarea>
        <div class="entry">
            <label for="content">&nbsp;</label>
            <span class="textCounter" id="textCounter"></span>
        </div>
        <@fieldErrors fieldName="content"/>
    </div>
</div>
<#if mode?has_content>
  <#if party?has_content>
    <#assign groupName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(party)/>
    <#assign partyId = party.partyId!"" />
    <#assign statusId = party.statusId!"" />

    <#if partyGroup?has_content>
      <#assign groupName = partyGroup.groupName!""/>
      <#assign groupNameLocal = partyGroup.groupNameLocal!""/>
    </#if>

    <#if partyGeneralContactMechValueMap?has_content>
      <#assign postalAddress = partyGeneralContactMechValueMap.postalAddress>
      <#assign addressContactMech = partyGeneralContactMechValueMap.contactMech>
      <#if addressContactMech?has_content>
        <#assign addressContactMechId = addressContactMech.contactMechId!""/>
      </#if>
      <#if postalAddress?has_content>
        <#assign address1 = postalAddress.address1!""/>
        <#assign address2 = postalAddress.address2!""/>
        <#assign address3 = postalAddress.address3!""/>
        <#assign city = postalAddress.city!""/>
        <#assign stateProvinceGeoId = postalAddress.stateProvinceGeoId!""/>
        <#assign postalCode = postalAddress.postalCode!""/>
        <#assign countryGeoId = postalAddress.countryGeoId!""/>
      </#if>
    </#if>

    <#if partyPrimaryPhoneContactMechValueMap?has_content>
      <#assign telecomNumber = partyPrimaryPhoneContactMechValueMap.telecomNumber>
      <#assign phoneContactMech = partyPrimaryPhoneContactMechValueMap.contactMech>
      <#if phoneContactMech?has_content>
          <#assign phoneContactMechId = phoneContactMech.contactMechId!""/>
      </#if>
      <#if telecomNumber?has_content>
        <#assign countryCode = telecomNumber.countryCode!""/>
        <#assign areaCode = telecomNumber.areaCode!""/>
        <#assign contactNumber = telecomNumber.contactNumber!""/>
        <#if contactNumber?has_content && contactNumber?length gt 6>
            <#assign contactNumber3 = contactNumber.substring(0, 3) />
            <#assign contactNumber4 = contactNumber.substring(3, 7) />
        </#if>
      </#if>
    </#if>

    <#if storeHoursContentId?has_content>
      <#assign storeHoursTextData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeHoursContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
      <#if storeHoursTextData?has_content && storeHoursTextData.equals("null")>
        <#assign storeHoursTextData = ""/>
      </#if>
    </#if>

    <#if storeNoticeContentId?has_content>
      <#assign storeNoticeTextData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeNoticeContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
      <#if storeNoticeTextData?has_content && storeNoticeTextData.equals("null")>
        <#assign storeNoticeTextData = ""/>
      </#if>
    </#if>
    
    <#if storeContentSpotContentId?has_content>
      <#assign storeContentSpotData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, storeContentSpotContentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
      <#if storeContentSpotData?has_content && storeContentSpotData.equals("null")>
        <#assign storeContentSpotData = ""/>
      </#if>
    </#if>

    <#if geoPoint?has_content>
      <#assign longitude = geoPoint.longitude!""/>
      <#assign latitude = geoPoint.latitude!""/>
    </#if>

  </#if> 
  <#assign statusId = statusId!"PARTY_ENABLED">
  <#assign countryList = Static["com.osafe.util.OsafeAdminUtil"].getCountryList(request)/>
  <#assign selectedCountry = parameters.countryGeoId!countryGeoId!COUNTRY_DEFAULT!""/>
  <#-- assign stateList = Static["org.ofbiz.common.CommonWorkers"].getAssociatedStateList(delegator, selectedCountry) / -->
  <#assign stateMap = dispatcher.runSync("getAssociatedStateList", Static["org.ofbiz.base.util.UtilMisc"].toMap("countryGeoId", selectedCountry, "userLogin", userLogin, "listOrderBy", "geoCode"))/>
  <#if stateMap?has_content && stateMap.stateList?has_content>
    <#assign stateList = stateMap.stateList />
  </#if>

  <#assign selectedState = parameters.stateProvinceGeoId!stateProvinceGeoId!""/>
  <input type="hidden" name="addressContactMechId" value="${parameters.addressContactMechId!addressContactMechId!""}" />
  <input type="hidden" name="phoneContactMechId" value="${parameters.phoneContactMechId!phoneContactMechId!""}" />
  <input type="hidden" name="partyId" value="${parameters.partyId!partyId!""}" />
  <input type="hidden" name="storeHoursDataResourceId" value="${parameters.storeHoursDataResourceId!storeHoursDataResourceId!""}" />
  <input type="hidden" name="storeNoticeDataResourceId" value="${parameters.storeNoticeDataResourceId!storeNoticeDataResourceId!""}" />
  <input type="hidden" name="storeContentSpotDataResourceId" value="${parameters.storeContentSpotDataResourceId!storeContentSpotDataResourceId!""}" />
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.StoreLocationCodeCaption}</label>
      </div>
      <div class="infoValue">
        <input class="medium" type="text" name="groupNameLocal" maxlength="100" id="groupNameLocal" value="${parameters.groupNameLocal!groupNameLocal!""}" />
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.StoreLocationNameCaption}</label>
      </div>
      <div class="infoValue">
        <input class="large" type="text" id="groupName" name="groupName" maxlength="100" value="${parameters.groupName!groupName!""}"/>
      </div>
    </div>
  </div>

  <#if COUNTRY_MULTI?has_content && Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(COUNTRY_MULTI)>
    <div class="infoRow row">
      <div class="infoEntry long">
        <div class="infoCaption">
          <label>${uiLabelMap.CountryCaption}</label>
        </div>
        <div class="infoValue">
          <select name="countryGeoId" id="countryGeoId"  title="countryGeoId">
            <#if countryList?has_content>
              <#list countryList as country>
                <option value='${country.geoId}' <#if selectedCountry = country.geoId >selected=selected</#if>>${country.get("geoName")?default(country.geoId)}</option>
              </#list>
            </#if>
          </select>
        </div>
      </div>
    </div>
  <#else>
    <input type="hidden" name="countryGeoId" id="countryGeoId" value="${selectedCountry}"/>
  </#if> 
 
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.Address1Caption}</label>
      </div>
      <div class="infoValue">
        <input class="large" type="text" id="address1" name="address1" maxlength="255" value="${parameters.address1!address1!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.Address2Caption}</label>
      </div>
      <div class="infoValue">
        <input class="large" type="text" id="address2" name="address2" maxlength="255" value="${parameters.address2!address2!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow row" id="divAddress3" <#if stateList?has_content>style="display:none"</#if>>
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.AddressLine3Caption}</label>
      </div>
      <div class="infoValue">
        <input class="large" type="text" id="address3" name="address3" maxlength="255" value="${parameters.address3!address3!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <span>${uiLabelMap.TownOrCityCaption}</span>
        </label>
      </div>
      <div class="infoValue">
        <input class="medium" type="text" id="city" name="city" maxlength="100" value="${parameters.city!city!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow row" id="divStateProvinceGeoId" <#if !stateList?has_content>style="display:none"</#if>>
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <span>${uiLabelMap.StateOrProvinceCaption}</span>
        </label>
      </div>
      <div class="infoValue">
        <select name="stateProvinceGeoId" id="stateProvinceGeoId"  title="stateProvinceGeoId">
          <#if stateList?has_content>
            <#list stateList as state>
              <option value='${state.geoId!}' <#if selectedState = state.geoId! >selected=selected</#if>>${state.geoName?default(state.geoId!)}</option>
            </#list>
          </#if>
        </select>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>
          <span>${uiLabelMap.ZipOrPostalCodeCaption}</span>
        </label>
      </div>
      <div class="infoValue">
        <input class="medium" type="text" id="postalCode" name="postalCode" maxlength="60" value="${parameters.postalCode!postalCode!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.PhoneCaption}</label>
      </div>
      <div class="infoValue USA CAN">
        <input class="phone3" type="text" maxlength="3" name="areaCode" value="${parameters.areaCode!areaCode!""}"/>
        <input class="phone3" type="text" maxlength="3" name="contactNumber3" value="${parameters.contactNumber3!contactNumber3!""}"/>
        <input class="phone4" type="text" maxlength="4" name="contactNumber4" value="${parameters.contactNumber4!contactNumber4!""}"/>
      </div>
      <div class="infoValue OTHER">
        <input class="medium" type="text" maxlength="15" name="contactNumberOther" value="${parameters.contactNumberOther!contactNumber!""}"/>
      </div>
    </div>
  </div>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.StoreWrkHrCaption}</label>
      </div>
      <div class="infoValue">
        <textarea class="smallArea" name="storeHoursTextData" cols="50" rows="5">${parameters.storeHoursTextData!storeHoursTextData!""}</textarea>
      </div>
    </div>
  </div>
  
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.StoreNoticeCaption}</label>
      </div>
      <div class="infoValue">
        <textarea class="shortArea" name="storeNoticeTextData" cols="50">${parameters.storeNoticeTextData!storeNoticeTextData!""}</textarea>
      </div>
    </div>
  </div>
  
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.StoreContentSpotCaption}</label>
      </div>
      <div class="infoValue">
        <textarea class="" name="storeContentSpotData" cols="50" rows="5">${parameters.storeContentSpotData!storeContentSpotData!""}</textarea>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.GeoCodeCaption}</label>
      </div>
      <div class="infoValue medium">
        <div class="geoCode">
          <label>${uiLabelMap.LongitudeLabel}</label>
          <input type="text"  class="textEntry textAlignRight" name="longitude" id="longitude" value='${parameters.longitude!longitude!""}'/>
        </div>
        <div class="geoCode">
          <label>${uiLabelMap.LatitudeLabel}</label>
          <input type="text"  class="textEntry textAlignRight" name="latitude" id="latitude" value='${parameters.latitude!latitude!""}'/>
        </div>
      </div>
      <div class="statusButtons">
        <a href="javascript:submitDetailForm(document.${detailFormName!""}, 'GP');" class="standardBtn secondary">${uiLabelMap.StoreGetGeoCodeBtn}</a>
      </div>
    </div>
  </div>

  <div class="infoRow">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.StoreLocationStatusCaption}</label>
      </div>
      <div class="infoValue medium">
        <#if (statusId?has_content && statusId=="PARTY_ENABLED")>
          <span id="storeStatus">${uiLabelMap.StoreLocationOpenedInfo}</span>
        <#else>
          <span id="storeStatus">${uiLabelMap.StoreLocationClosedInfo}</span>
        </#if>
      </div>
      <div class="statusButtons">
        <input type="hidden" name="statusId" id="statusId" value="${parameters.statusId!statusId!""}" />
        <script type="text/javascript">
          var statusArray = new Array();
          <#assign index = 0/>
            statusArray[${index}] = new Status('${uiLabelMap.StoreLocationMakeOpenBtn}', '${uiLabelMap.StoreLocationOpenedInfo}', '${uiLabelMap.StoreLocationMakeCloseBtn}', 'PARTY_ENABLED'); 
          <#assign index = index+1/>
            statusArray[${index}] = new Status('${uiLabelMap.StoreLocationMakeCloseBtn}', '${uiLabelMap.StoreLocationClosedInfo}', '${uiLabelMap.StoreLocationMakeOpenBtn}', 'PARTY_DISABLED');; 
        </script>
        <#if (statusId?has_content && statusId=="PARTY_ENABLED")>
          <input id="storeStatusBtn" type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.StoreLocationMakeCloseBtn}" onClick="updateStatus(statusArray, 'storeStatus', 'storeStatusBtn', 'statusId');"/>
        <#else>
          <input id="storeStatusBtn" type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.StoreLocationMakeOpenBtn}" onClick="updateStatus(statusArray, 'storeStatus', 'storeStatusBtn', 'statusId');"/>
        </#if>
      </div>
    </div>
  </div>
<#else>
      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>

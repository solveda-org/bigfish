<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery('#${fieldPurpose?if_exists}_country')) {
            if(!jQuery('#${fieldPurpose?if_exists}_StateListExist').length) {
                getAssociatedStateList('${fieldPurpose?if_exists}_country', '${fieldPurpose?if_exists}_state', '${fieldPurpose?if_exists}_STATES');
            }
            getAddressFormat("${fieldPurpose?if_exists}");
            jQuery('#${fieldPurpose?if_exists}_country').change(function(){
                getAssociatedStateList('${fieldPurpose?if_exists}_country', '${fieldPurpose?if_exists}_state', '${fieldPurpose?if_exists}_STATES');
                getAddressFormat("${fieldPurpose?if_exists}");
            });
        }
    });
</script>

<#assign firstName = ""/>
<#assign lastName = ""/>
<#if postalAddress?has_content>
	<#assign contactMechId = postalAddress.contactMechId!"">
    <#assign toName = postalAddress.toName!"" />
    <#if toName?has_content>
      <#assign fullName = toName.split(" ")!/>
      <#if fullName?has_content>
        <#assign firstName = fullName[0]!/>
        <#assign fullNameSize = fullName?size/>
        <#if fullNameSize &gt; 1>
          <#assign lastName = fullName[fullNameSize-1]!/>
        </#if>
      </#if>
    </#if>
    
    <#assign attnName = postalAddress.attnName!"" />
    <#assign countryGeoId = postalAddress.countryGeoId!"">
    <#assign address1 = postalAddress.address1!"">
    <#assign address2 = postalAddress.address2!"">
    <#assign city = postalAddress.city!"">
    <#assign stateProvinceGeoId = postalAddress.stateProvinceGeoId!"">
    <#assign postalCode = postalAddress.postalCode!"">
    <#if postalCode?has_content && postalCode == '_NA_'>
      <#assign postalCode = "">
    </#if>
</#if>

<#assign  selectedCountry = parameters.get("${fieldPurpose?if_exists}_country")!countryGeoId?if_exists/>
<#if !selectedCountry?has_content>
    <#if defaultCountryGeoMap?exists>
        <#assign selectedCountry = defaultCountryGeoMap.geoId/>
    </#if>
</#if>

<#assign  selectedCountry = parameters.get("${fieldPurpose?if_exists}_country")!countryGeoId?if_exists/>
<#if !selectedCountry?has_content>
    <#if defaultCountryGeoMap?exists>
        <#assign selectedCountry = defaultCountryGeoMap.geoId/>
    </#if>
</#if>
<#assign  selectedState = parameters.get("${fieldPurpose?if_exists}_state")!stateProvinceGeoId?if_exists/>

<#if mode?has_content && mode="edit">
    <input type="hidden" name="contactMechId" id="contactMechId" value="${parameters.contactMechId!contactMechId!""}"/>
</#if>

<#if !parameters.contactMechId?has_content && !contactMechId?has_content>
	<#if person?has_content>
	  <#assign personFirstName= person.firstName!""/>
	  <#if !firstName?has_content>
	  	<#assign firstName= personFirstName!""/>
	  </#if>
	  <#assign personLastName= person.lastName!""/>
	  <#if !lastName?has_content>
	  	<#assign lastName = personLastName!""/>
	  </#if>
	</#if>
</#if>

<#if purposeType?has_content && purposeType == "BILLING_LOCATION" || fieldPurpose == "billing">
  <input type="hidden" id="${fieldPurpose?if_exists}_fullName_mandatory" name="${fieldPurpose?if_exists}_fullName_mandatory" value="Y"/>
</#if>
<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label><#if purposeType?has_content && purposeType == "BILLING_LOCATION" || fieldPurpose == "billing"><span class="required">*</span></#if>${eCommerceUiLabel.FirstNameCaption}</label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="100" class="addressFirstName" name="${fieldPurpose?if_exists}_firstName" id="${fieldPurpose?if_exists}_firstName" value="${parameters.get("${fieldPurpose?if_exists}_firstName")!firstName!""}" />
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label><#if purposeType?has_content && purposeType == "BILLING_LOCATION" || fieldPurpose == "billing"><span class="required">*</span></#if>${eCommerceUiLabel.LastNameCaption}</label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="100" class="addressLastName" name="${fieldPurpose?if_exists}_lastName" id="${fieldPurpose?if_exists}_lastName" value="${parameters.get("${fieldPurpose?if_exists}_lastName")!lastName!""}" />
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label><span class="required">*</span>${eCommerceUiLabel.AddressNickNameCaption}</label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="100" class="addressNickName" name="${fieldPurpose?if_exists}_attnName" id="${fieldPurpose?if_exists}_attnName" value="${parameters.get("${fieldPurpose?if_exists}_attnName")!attnName!""}" />
        </div>
    </div>
</div>
<input type="hidden" id="${fieldPurpose?if_exists}_attnName_mandatory" name="${fieldPurpose?if_exists}_attnName_mandatory" value="Y"/>


<#if Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(COUNTRY_MULTI!"")>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${eCommerceUiLabel.CountryCaption}</label>
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
<input type="hidden" id="${fieldPurpose?if_exists}_country_mandatory" name="${fieldPurpose?if_exists}_country_mandatory" value="Y"/>


<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label><span class="required">*</span>${eCommerceUiLabel.AddressLine1Caption}</label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_address1" id="${fieldPurpose?if_exists}_address1" value="${parameters.get("${fieldPurpose?if_exists}_address1")!address1!""}" />
            <input type="hidden" id="${fieldPurpose?if_exists}_address1_mandatory" name="${fieldPurpose?if_exists}_address1_mandatory" value="Y"/>
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label>${eCommerceUiLabel.AddressLine2Caption}</label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="255" class="address" name="${fieldPurpose?if_exists}_address2" id="${fieldPurpose?if_exists}_address2" value="${parameters.get("${fieldPurpose?if_exists}_address2")!address2!""}" />
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label>${eCommerceUiLabel.AddressLine3Caption}</label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="100" class="address" name="${fieldPurpose?if_exists}_address3" id="${fieldPurpose?if_exists}_address3" value="${parameters.get("${fieldPurpose?if_exists}_address3")!address3!""}" />
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label><span class="required">*</span>${eCommerceUiLabel.TownOrCityCaption}
            </label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="100" class="city" name="${fieldPurpose?if_exists}_city" id="${fieldPurpose?if_exists}_city" value="${parameters.get("${fieldPurpose?if_exists}_city")!city!""}" />
            <input type="hidden" id="${fieldPurpose?if_exists}_city_mandatory" name="${fieldPurpose?if_exists}_city_mandatory" value="Y"/>
        </div>
    </div>
</div>

<div class="infoRow" id="${fieldPurpose?if_exists}_STATES">
    <div class="infoEntry">
        <div class="infoCaption">
            <label><span class="required">*</span>${eCommerceUiLabel.StateOrProvinceCaption}</label>
        </div>
        <div class="infoValue">
            <select id="${fieldPurpose?if_exists}_state" name="${fieldPurpose?if_exists}_state" class="select ${fieldPurpose?if_exists}_country">
                <#list countryList as country>
                    <#if country.geoId == selectedCountry>
                      <#assign stateMap = dispatcher.runSync("getAssociatedStateList", Static["org.ofbiz.base.util.UtilMisc"].toMap("countryGeoId", country.geoId, "userLogin", userLogin, "listOrderBy", "geoCode"))/>
                      <#assign stateList = stateMap.stateList />
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
            <input type="hidden" id="${fieldPurpose?if_exists}_state_mandatory" name="${fieldPurpose?if_exists}_state_mandatory" value="Y"/>
        </div>
    </div>
</div>

<div class="infoRow">
    <div class="infoEntry">
        <div class="infoCaption">
            <label><span class="required">*</span>${eCommerceUiLabel.PostalCodeOrZipCaption}</label>
        </div>
        <div class="infoValue">
            <input type="text" maxlength="60" class="postalCode" name="${fieldPurpose?if_exists}_postalCode" id="${fieldPurpose?if_exists}_postalCode" value="${parameters.get("${fieldPurpose?if_exists}_postalCode")!postalCode!""}" />
            <input type="hidden" id="${fieldPurpose?if_exists}_postalCode_mandatory" name="${fieldPurpose?if_exists}_postalCode_mandatory" value="Y"/>
        </div>
    </div>
</div>















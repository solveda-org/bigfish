<!-- start customerDetailBillingInfo.ftl -->
<#if partyBillingContactMechValueMap?has_content>
    <#assign contactMech = partyBillingContactMechValueMap.contactMech?if_exists />
    <#assign contactMechType = partyBillingContactMechValueMap.contactMechType?if_exists />
    <#assign partyContactMech = partyBillingContactMechValueMap.partyContactMech?if_exists />
    <#-- Start Addresses -->
    <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
	 <div class="infoRow">
	   <div class="infoEntry">
	     <div class="infoCaption">
	      <label>${uiLabelMap.CustomerIdCaption}</label>
	     </div>
	     <div class="infoValue">
	       ${partyId!""}
	     </div>
	   </div>
	  </div>
     <div class="infoRow">
	      <div class="infoEntry">
	         <div class="infoCaption">
	            <label>${uiLabelMap.AddressCaption}</label>
	         </div>
	         <div class="infoValue">
	            <#assign postalAddress = partyBillingContactMechValueMap.postalAddress>
	            <#if postalAddress?has_content>
	                <#if postalAddress.toName?has_content><p>${postalAddress.toName}</p></#if>
	                <p>${postalAddress.address1}</p>
	                <#if postalAddress.address2?has_content><p>${postalAddress.address2}</p></#if>
	                <p>${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId} </#if>
	                ${postalAddress.postalCode?if_exists}</p>
	                ${postalAddress.countryGeoId?if_exists}</p>
	            </#if>
	         </div>
	      </div> <#-- end infoEntry -->
	 </div> <#-- end infoRow -->
    <#-- End Addresses -->
    </#if>

    <#-- Customer Email-->
    <#-- Start Email Address -->
          <div class="infoRow">
           <div class="infoEntry">
             <div class="infoCaption">
                <label>${uiLabelMap.EmailAddressCaption}</label>
             </div>
             <div class="infoValue">
                 <#if partyPrimaryEmailContactMechValueMap?has_content>
                     <#assign partyPrimaryEmailContactMech = partyPrimaryEmailContactMechValueMap.contactMech>
                      <p>${partyPrimaryEmailContactMech.infoString}</p>
                 </#if>
             </div>
           </div> <#-- end infoEntry -->
          </div> <#-- end infoRow -->
    <#-- End Email Address -->
    
    <#-- Start Phone Number -->
     <#if partyPhoneHomeContactMechValueMap?has_content>
        <#assign partyHomePhone = partyPhoneHomeContactMechValueMap.telecomNumber>
     </#if>
     <#if partyPhoneMobileContactMechValueMap?has_content>
        <#assign partyMobilePhone = partyPhoneMobileContactMechValueMap.telecomNumber/>
     </#if>
    <#if partyHomePhone?has_content || partyMobilePhone?has_content>
      <div class="infoRow">
        <#if partyHomePhone?has_content>
          <#assign formattedPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyHomePhone.areaCode?if_exists, partyHomePhone.contactNumber?if_exists)/>
          <#if formattedPhone?has_content>
            <div class="infoEntry">
              <div class="infoCaption">
                <label>${uiLabelMap.HomePhoneCaption}</label>
              </div>
              <div class="infoValue">${formattedPhone}</div>
            </div>
          </#if>
        </#if>
        <#if partyMobilePhone?has_content>
          <#assign formattedMobilePhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyMobilePhone.areaCode?if_exists, partyMobilePhone.contactNumber?if_exists)/>
          <#if formattedMobilePhone?has_content>
            <div class="infoEntry">
              <div class="infoCaption">
                <label>${uiLabelMap.MobilePhoneCaption}</label>
              </div>
              <div class="infoValue">${formattedMobilePhone}</div>
            </div>
          </#if>
        </#if>
      </div>
    </#if>
    <#-- End Phone Number -->
<#else>
   <div class="infoRow">
   <div class="infoEntry">
     <div>
      <span class="noResultsText">${uiLabelMap.NoPostalAddressInfo}</span>
     </div>
   </div>
  </div>
</#if>

<!-- end customerDetailBillingInfo.ftl -->
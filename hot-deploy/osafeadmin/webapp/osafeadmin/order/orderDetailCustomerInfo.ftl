<!-- start orderDetailCustomerInfo.ftl -->
 <div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.CustomerIdCaption}</label>
     </div>
     <div class="infoValue">
       <a href="<@ofbizUrl>customerDetail?partyId=${partyId!""}</@ofbizUrl>">${partyId!""}</a>
     </div>
   </div>
 </div>
 <#if displayParty?has_content>
   <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "lastNameFirst","Y", "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
  <div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
      <label>${uiLabelMap.CustomerNameCaption}</label>
     </div>
     <div class="infoValue">
       ${displayPartyNameResult.fullName?default("[${uiLabelMap.PartyNameNotFoundInfo}]")}
     </div>
   </div>
  </div>
 </#if>
<#if orderContactMechValueMaps?has_content>

    <#list orderContactMechValueMaps as orderContactMechValueMap>
      <#assign contactMech = orderContactMechValueMap.contactMech>
      <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
      <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION" || (contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION" && (!isStorePickup?has_content || isStorePickup != "Y"))>
          <#-- Start Addresses -->
          <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
          <div class="infoRow">
           <div class="infoEntry">
             <div class="infoCaption">
                <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION">
                  <label>${uiLabelMap.BillingAddressCaption}</label>
                </#if>
                <#if contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION">
                  <label>${uiLabelMap.ShippingAddressCaption}</label>
                </#if>
             </div>
             <div class="infoValue">
                  <#assign postalAddress = orderContactMechValueMap.postalAddress />
                    <#if postalAddress?has_content>
                        <#if postalAddress.toName?has_content><p>${postalAddress.toName}</p></#if>
                        <p>${postalAddress.address1}</p>
                        <#if postalAddress.address2?has_content><p>${postalAddress.address2}</p></#if>
                        <p>${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content && postalAddress.stateProvinceGeoId != '_NA_'>, ${postalAddress.stateProvinceGeoId} </#if>
                        ${postalAddress.postalCode?if_exists}</p>
                        <p>${postalAddress.countryGeoId?if_exists}</p>
                    </#if>
             </div>
           </div> <#-- end infoEntry -->
          </div> <#-- end infoRow -->
              <#-- End Addresses -->
          </#if>
      </#if>
    </#list>

    <#-- Customer Email-->
    <#-- Start Email Address -->
    <#if userEmailAddress?exists && userEmailAddress?has_content>
          <div class="infoRow">
           <div class="infoEntry">
             <div class="infoCaption">
                <label>${uiLabelMap.EmailAddressCaption}</label>
             </div>
             <div class="infoValue">
                 <p> ${userEmailAddress!""}</p>
             </div>
           </div> 
          </div>
    </#if>
    <#-- End Email Address -->
    
    <#-- Start Phone Number -->
      <#if phoneHomeTelecomNumber?has_content>
        <#assign formattedHomePhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneHomeTelecomNumber.areaCode?if_exists, phoneHomeTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
      </#if>
      <#if phoneWorkTelecomNumber?has_content>
        <#assign formattedWorkPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneWorkTelecomNumber.areaCode?if_exists, phoneWorkTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        <#if phoneWorkTelecomNumber.extension?has_content>
          <#assign partyWorkPhoneExt = phoneWorkTelecomNumber.extension!/> 
        </#if>
      </#if>
      <#if phoneMobileTelecomNumber?has_content>
        <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneMobileTelecomNumber.areaCode?if_exists, phoneMobileTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
      </#if>
      
    <#if formattedHomePhone?has_content || formattedCellPhone?has_content || formattedWorkPhone?has_content>
	  <div class="infoRow">
	    <#if formattedHomePhone?has_content>
          <div class="infoEntry">
            <div class="infoCaption">
              <label>${uiLabelMap.HomePhoneCaption}</label>
            </div>
            <div class="infoValue">${formattedHomePhone!}</div>
          </div>
        </#if>
        <#if formattedCellPhone?has_content>
          <div class="infoEntry">
            <div class="infoCaption">
              <label>${uiLabelMap.CellPhoneCaption}</label>
            </div>
            <div class="infoValue">${formattedCellPhone!}</div>
          </div>
        </#if>
        <#if formattedWorkPhone?has_content>
          <div class="infoEntry">
            <div class="infoCaption">
              <label>${uiLabelMap.WorkPhoneCaption}</label>
            </div>
            <div class="infoValue">${formattedWorkPhone!}<#if partyWorkPhoneExt?has_content>&nbsp;x${partyWorkPhoneExt}</#if></div>
          </div>
        </#if>
      </div>
    </#if>
    <#-- End Phone Number -->
    
</#if>
<!-- end orderDetailCustomerInfo.ftl -->
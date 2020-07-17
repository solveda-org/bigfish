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
                        <#if postalAddress.attnName?has_content><p>${postalAddress.attnName}</p></#if>
                        <p>${postalAddress.address1}</p>
                        <#if postalAddress.address2?has_content><p>${postalAddress.address2}</p></#if>
                        <p>${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId} </#if>
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
    <#assign placingParty = orderReadHelper.getPlacingParty()/>
    <#assign billToEmailList = Static["org.ofbiz.party.contact.ContactHelper"].getContactMech(placingParty, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false)/>
    <#if billToEmailList?has_content>
         <#assign customerEmail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(billToEmailList)/>
         <#assign customerEmailAddress = customerEmail.infoString/>
    </#if>

    <#-- Start Email Address -->
          <div class="infoRow">
           <div class="infoEntry">
             <div class="infoCaption">
                <label>${uiLabelMap.EmailAddressCaption}</label>
             </div>
             <div class="infoValue">
                 <p> ${customerEmailAddress!""}</p>
             </div>
           </div> <#-- end infoEntry -->
          </div> <#-- end infoRow -->
    <#-- End Email Address -->
    
    <#-- Start Phone Number -->
    <#assign homePhones = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",partyId,"contactMechPurposeTypeId","PHONE_HOME"))/>
	 <#if homePhones?has_content>
        <#assign homePhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(homePhones, nowTimestamp, "fromDate", "thruDate", true)/>
	    <#assign partyHomePhone = delegator.findByPrimaryKey("TelecomNumber", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",homePhones[0].contactMechId))/>
	 </#if>
	 <#assign mobilePhones = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",partyId,"contactMechPurposeTypeId","PHONE_MOBILE"))/>
	 <#if mobilePhones?has_content>
        <#assign mobilePhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(mobilePhones, nowTimestamp, "fromDate", "thruDate", true)/>
	    <#assign partyMobilePhone = delegator.findByPrimaryKey("TelecomNumber", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",mobilePhones[0].contactMechId))/>
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
         <#assign formattedPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyHomePhone.areaCode?if_exists, partyHomePhone.contactNumber?if_exists)/>
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
    
</#if>
<!-- end orderDetailCustomerInfo.ftl -->
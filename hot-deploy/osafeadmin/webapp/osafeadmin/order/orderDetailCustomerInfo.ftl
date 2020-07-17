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
      <#assign partyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId))/>
        <#-- Home Phone -->
      <#if partyContactDetails?has_content>
        <#assign partyHomePhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_HOME"}) />
      </#if>
      <#if partyHomePhoneDetails?has_content>
        <#assign partyHomePhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyHomePhoneDetails?if_exists) />
        <#assign partyHomePhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyHomePhoneDetails?if_exists) />
        <#assign formattedHomePhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyHomePhoneDetail.areaCode?if_exists, partyHomePhoneDetail.contactNumber?if_exists)/>
      </#if>
      
        <#-- Work Phone -->
      <#if partyContactDetails?has_content>
        <#assign partyWorkPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_WORK"}) />
      </#if>
      <#if partyWorkPhoneDetails?has_content>
        <#assign partyWorkPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyWorkPhoneDetails?if_exists) />
        <#assign partyWorkPhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyWorkPhoneDetails?if_exists) />
        <#assign formattedWorkPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyWorkPhoneDetail.areaCode?if_exists, partyWorkPhoneDetail.contactNumber?if_exists)/>
        <#if partyWorkPhoneDetail?has_content && partyWorkPhoneDetail.extension?has_content>
          <#assign partyWorkPhoneExt = partyWorkPhoneDetail.extension!/> 
        </#if>
      </#if>
        
        <#-- Cell Phone --> 
      <#if partyContactDetails?has_content>
        <#assign partyCellPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_MOBILE"}) />
      </#if>
      <#if partyCellPhoneDetails?has_content>
        <#assign partyCellPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyCellPhoneDetails?if_exists) />
        <#assign partyCellPhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyCellPhoneDetails?if_exists) />
        <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyCellPhoneDetail.areaCode?if_exists, partyCellPhoneDetail.contactNumber?if_exists)/>
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
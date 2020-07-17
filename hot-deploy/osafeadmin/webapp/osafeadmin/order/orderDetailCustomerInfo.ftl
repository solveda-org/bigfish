<!-- start orderDetailCustomerInfo.ftl -->
<div class="infoRow column">
     <div class="infoEntry">
         <div class="infoCaption">
          <label>${uiLabelMap.CustomerIdCaption}</label>
         </div>
         <div class="infoValue">
           <a href="<@ofbizUrl>customerDetail?partyId=${partyId!""}</@ofbizUrl>">${partyId!""}</a>
         </div>
     </div>
    
     <#if displayParty?has_content>
       <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
       <div class="infoEntry">
         <div class="infoCaption">
           <label>${uiLabelMap.CustomerNameCaption}</label>
         </div>
         <div class="infoValue">
           <#if displayPartyNameResult?has_content && (displayPartyNameResult.firstName?has_content || displayPartyNameResult.middleName?has_content || displayPartyNameResult.lastName?has_content)>
             ${displayPartyNameResult.firstName!""} ${displayPartyNameResult.middleName!""} ${displayPartyNameResult.lastName!""}
           <#else>
             ${uiLabelMap.PartyNameNotFoundInfo}
           </#if>
         </div>
       </div>
     </#if>

    <#-- Customer Email-->
    <#-- Start Email Address -->
    <#if userEmailAddress?exists && userEmailAddress?has_content>
       <div class="infoEntry">
         <div class="infoCaption">
            <label>${uiLabelMap.EmailAddressCaption}</label>
         </div>
         <div class="infoValue">
             <p> ${userEmailAddress!""}</p>
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
        <#if partyPurposeWorkPhone?has_content>
            <#assign partyWorkPhoneExt = partyPurposeWorkPhone.extension?if_exists />
        </#if>
    </#if>
    <#if phoneMobileTelecomNumber?has_content>
        <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneMobileTelecomNumber.areaCode?if_exists, phoneMobileTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
    </#if>
      
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
    <#-- End Phone Number -->
</div>

<div class="infoRow column">
    <#if orderContactMechValueMaps?has_content>
        <#list orderContactMechValueMaps as orderContactMechValueMap>
          <#assign contactMech = orderContactMechValueMap.contactMech>
          <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
          <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION">
              <#-- Start Addresses -->
              <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
               <div class="infoEntry">
                 <div class="infoCaption">
                    <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION">
                      <label>${uiLabelMap.BillingAddressCaption}</label>
                    </#if>
                 </div>
                 <div class="infoValue">
                      <#assign postalAddress = orderContactMechValueMap.postalAddress />
                        <#if postalAddress?has_content>
                             ${setRequestAttribute("PostalAddress",postalAddress)}
                             ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
                        </#if>
                 </div>
               </div> <#-- end infoEntry -->
              </#if>
          </#if>
        </#list>
    </#if>

    <#if shipGroups?has_content && orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL">
      <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroups)/> 
      <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
        <div class="infoEntry">
       
         <#if isStorePickup?has_content && isStorePickup == "Y">
             <div class="infoCaption">
                 <label>${uiLabelMap.ShipMethodCaption}</label>
             </div>
             <div class="infoValue">
                 <p>${uiLabelMap.PickupInStoreLabel}</p>
             </div> 
         <#else>
             <div class="infoCaption">
                 <label>${uiLabelMap.ShipMethodCaption}</label>
             </div>
             <div class="infoValue">
             <#-- the setting of shipping method is only supported for sales orders at this time -->
               <#if shipGroupsSize == 1 >
                 <#if orderHeader.orderTypeId == "SALES_ORDER" && shipGroup.shipmentMethodTypeId?has_content>
                     <#if shipGroup.carrierPartyId?has_content || shipmentMethodType?has_content>
                         <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_REJECTED">
                             <p>
                                 <#if shipGroup.carrierPartyId?has_content>
                                     <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
                                     <#if carrier?has_content>
                                     ${carrier.groupName?default(carrier.partyId)!}&nbsp;
                                     <#else>
                                     ${shipGroup.carrierPartyId!}&nbsp;
                                     </#if>
                                 </#if>
                                 ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
                             </p>
                         </#if>
                     </#if>
                 </#if>
               <#else>
                   <p class="leftFloat">${uiLabelMap.MultipleShipmentsLabel}</p>
                   <a href="<@ofbizUrl>orderShippingDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="shipmentDetailIcon"></span></a>
               </#if>
             </div>
           </#if>
        </div>
    <#else>
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ShipMethodCaption}</label>
            </div>
            <div class="infoValue">
                ${uiLabelMap.NoShippingMethodInfo}
            </div>
        </div>
    </#if>

    <#if isStorePickup?has_content && isStorePickup == "Y">
        <div class="infoEntry">
          <div class="infoCaption">
            <label>${uiLabelMap.StoreAddressCaption}</label>
          </div>
          <div class="infoValue">
            <#if storeContactMechValueMap?has_content>
              <#assign postalAddress = storeContactMechValueMap.postalAddress />
              <#if postalAddress?has_content>
                  ${setRequestAttribute("PostalAddress",postalAddress)}
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
              </#if>
            </#if>
          </div>
        </div>
    <#else>
        <#if orderContactMechValueMaps?has_content>
            <#list orderContactMechValueMaps as orderContactMechValueMap>
              <#assign contactMech = orderContactMechValueMap.contactMech>
              <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
              <#if (contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION" && (!isStorePickup?has_content || isStorePickup != "Y"))>
                  <#-- Start Addresses -->
                  <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                   <div class="infoEntry">
                     <div class="infoCaption">
                        <#if contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION">
                          <label>${uiLabelMap.ShippingAddressCaption}</label>
                        </#if>
                     </div>
                     <div class="infoValue">
                          <#assign postalAddress = orderContactMechValueMap.postalAddress />
                            <#if postalAddress?has_content>
                                 ${setRequestAttribute("PostalAddress",postalAddress)}
                                 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
                            </#if>
                     </div>
                   </div> <#-- end infoEntry -->
                  </#if>
              </#if>
            </#list>
        </#if>
    </#if>
</div>
    
<!-- end orderDetailCustomerInfo.ftl -->
<!-- start orderDetailShippingInfo.ftl -->
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
<#if orderContactMechValueMaps?has_content>
    <#list orderContactMechValueMaps as orderContactMechValueMap>
      <#assign contactMech = orderContactMechValueMap.contactMech>
      <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
      <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION">
      <div class="infoRow">
       <div class="infoEntry">
         <div class="infoCaption">
            <#-- Using GenericEntity.get(String name, String resource, Locale locale) this references our props file default is PartyEntityLabels.xml  -->
            <label>${uiLabelMap.BillingAddressCaption}</label>
         </div>
          <#-- Start Addresses -->
          <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
             <div class="infoValue">
                    <#assign postalAddress = orderContactMechValueMap.postalAddress>
                    <#if postalAddress?has_content>
				          ${setRequestAttribute("PostalAddress",postalAddress)}
				          ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
                    </#if>
             </div>

          </#if>
       </div> <#-- end infoEntry -->
      </div> <#-- end infoRow -->
      </#if>
    </#list>
</#if>
<#if shipGroups?has_content && orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL">
<#list shipGroups as shipGroup>
  <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
  <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>

  <div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
     <label>${uiLabelMap.ShipMethodCaption}</label>
     </div>
     <div class="infoValue">
        <#-- the setting of shipping method is only supported for sales orders at this time -->
        <#if orderHeader.orderTypeId == "SALES_ORDER" && shipGroup.shipmentMethodTypeId?has_content>
            <#if shipGroup.carrierPartyId?has_content || shipmentMethodType?has_content>
                <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_REJECTED">
                    <p><#if shipGroup.carrierPartyId?has_content>
                        <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
                        <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)}&nbsp;</#if>
                       </#if>
                       ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
                    </p>
                </#if>
            </#if>
        </#if>

        <#-- tracking number -->
        <#if shipGroup.trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
              <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
              <#if shipGroup.trackingNumber?has_content>
                <p>${shipGroup.trackingNumber}</p>
              </#if>
        </#if>

     </div>
   </div>
  </div>
</#list>
 <#else>
  <div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
     <label>${uiLabelMap.ShipMethodCaption}</label>
     </div>
     <div class="infoValue">${uiLabelMap.NoShippingMethodInfo}
     </div>
   </div>
  </div>
</#if>

 <#if orderReadHelper?has_content>
     <#assign orderPayments = orderReadHelper.getPaymentPreferences()/>
     <#if orderPayments?has_content>
      <div class="infoRow">
       <div class="infoEntry">
         <div class="infoCaption">
         <label>${uiLabelMap.PaymentMethodCaption}</label>
         </div>
         <div class="infoValue">
           <#list orderPayments as orderPaymentPreference>
              <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
              <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
              <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
              <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
              <#if ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "CREDIT_CARD"))>
                    <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard")>
                    ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
              <#else>
                    ${paymentMethodType.get("description","OSafeAdminUiLabels",locale)?if_exists}
              </#if>
             <p><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/> - ${oppStatusItem.get("description","OSafeAdminUiLabels",locale)}</p>
              <#if gatewayResponses?has_content>
                  <#list gatewayResponses as gatewayResponse>
                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                    <p><b>${(transactionCode.get("description","OSafeAdminUiLabels",locale))?default("Unknown")}</b>&nbsp;: ${gatewayResponse.transactionDate?string(preferredDateFormat)}</p>
                    <p>${uiLabelMap.ReferenceCaption} ${gatewayResponse.referenceNum?if_exists}</p>
                  </#list>
              </#if>

           </#list>
         </div>
       </div>
      </div>
     <#else>
      <div class="infoRow">
       <div class="infoEntry">
         <div class="infoCaption">
         <label>${uiLabelMap.PaymentMethodCaption}</label>
         </div>
         <div class="infoValue">${uiLabelMap.NoPaymentMethodInfo}
         </div>
       </div>
      </div>
     </#if>
 </#if>
<!-- end orderDetailShippingInfo.ftl -->

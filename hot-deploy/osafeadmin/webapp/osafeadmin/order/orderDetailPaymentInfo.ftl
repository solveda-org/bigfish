<!-- start orderDetailPaymentInfo.ftl -->
<#assign currencyUomId = orderReadHelper.getCurrency()>
<#if shipGroups?has_content && orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL">
  <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroups)/> 
  <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
  <div class="infoRow">
   <div class="infoEntry">
     <div class="infoCaption">
       <label>${uiLabelMap.ShipMethodCaption}</label>
     </div>
     
        <#if isStorePickup?has_content && isStorePickup == "Y">
	        <div class="infoValue">
	            <p>${uiLabelMap.PickupInStoreLabel}</p>
	     	</div> 
        <#else>
	        <div class="infoValue">
		        <#-- the setting of shipping method is only supported for sales orders at this time -->
		        <#if orderHeader.orderTypeId == "SALES_ORDER" && shipGroup.shipmentMethodTypeId?has_content>
		            <#if shipGroup.carrierPartyId?has_content || shipmentMethodType?has_content>
		                <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_REJECTED">
		                    <p>
		                       <#if shipGroup.carrierPartyId?has_content>
		                        <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
		                        <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)!}&nbsp;</#if>
		                       </#if>
		                      ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
		                   </p>
		                </#if>
		            </#if>
		        </#if>
	     	</div>
	        <#-- tracking number -->
	        <#if shipGroup.trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
	          <div class="infoCaption">
	            <label>${uiLabelMap.TrackingNoCaption}</label>
	          </div>
	          <div class="infoValue">
	              <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
	              <#if shipGroup.trackingNumber?has_content >
	                <#if orderItems?has_content>
		                <#list orderItems as orderItem>
		                    <#if orderItem.statusId == "ITEM_COMPLETED">
                                <#assign orderShipmentIconVisible= "true"/>
                                <#break>
		                    </#if>
		                </#list>
		                <#-- Fetching the carrier tracking URL -->
                        <#if orderShipmentIconVisible?has_content && shipGroupsSize == 1>    
                            <#assign trackingURLPartyContents = delegator.findByAnd("PartyContent", {"partyId": carrier.partyId, "partyContentTypeId": "TRACKING_URL"})/>
                            <#if trackingURLPartyContents?has_content>
                                <#assign trackingURLPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(trackingURLPartyContents)/>
                                <#if trackingURLPartyContent?has_content>
                                    <#assign content = trackingURLPartyContent.getRelatedOne("Content")/>
                                    <#if content?has_content>
                                        <#assign dataResource = content.getRelatedOne("DataResource")!""/>
                                        <#if dataResource?has_content>
                                            <#assign electronicText = dataResource.getRelatedOne("ElectronicText")!""/>
                                            <#assign trackingURL = electronicText.textData!""/>
                                            <#if trackingURL?has_content>
                                                <#assign trackingURL = Static["org.ofbiz.base.util.string.FlexibleStringExpander"].expandString(trackingURL, {"TRACKING_NUMBER":shipGroup.trackingNumber})/>
                                            </#if>
                                        </#if>
                                    </#if>
                                </#if>
                            </#if>
                        </#if>
			        </#if>
	              	<#if shipGroupsSize == 1 >
	                	<p>${shipGroup.trackingNumber!}</p>
		                <#if showOrderShipmentIcon?has_content && showOrderShipmentIcon == "true" && orderShipmentIconVisible?has_content && orderShipmentIconVisible == "true" && trackingURL?has_content>
		                    <a href="JavaScript:newPopupWindow('${trackingURL!""}');"><span class="shipmentDetailIcon"></span></a>
		                </#if>
	                <#else>
	                	<p>${uiLabelMap.MultipleShipmentsLabel}</p>
	                	<#if showOrderShipmentIcon?has_content && showOrderShipmentIcon == "true" && orderShipmentIconVisible?has_content && orderShipmentIconVisible == "true">
		                    <a href="<@ofbizUrl>orderItemDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="shipmentDetailIcon"></span></a>
		                </#if>
	                </#if>
	              </#if>
	          </div>
	        </#if>
      </#if>
   </div>
  </div>
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

<#if isStorePickup?has_content && isStorePickup == "Y">
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.StoreAddressCaption}</label>
      </div>
      <div class="infoValue">
        <#if storeContactMechValueMap?has_content>
          <p>${storeInfo.groupName!}(${storeInfo.groupNameLocal!})</p>
          <#assign postalAddress = storeContactMechValueMap.postalAddress />
          <#if postalAddress?has_content>
            <#if postalAddress.toName?has_content><p>${postalAddress.toName}</p></#if>
            <#if postalAddress.attnName?has_content><p>${postalAddress.attnName}</p></#if>
            <p>${postalAddress.address1}</p>
            <#if postalAddress.address2?has_content><p>${postalAddress.address2}</p></#if>
            <p>${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId} </#if>
            ${postalAddress.postalCode?if_exists}</p>
            <p>${postalAddress.countryGeoId?if_exists}</p>
          </#if>
        </#if>
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
              <#assign orderPaymentPreferenceId = orderPaymentPreference.getString("orderPaymentPreferenceId")?if_exists>
              <#assign paymentMethodId = orderPaymentPreference.getString("paymentMethodId")?if_exists>
              <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
              <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
              <#if ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "CREDIT_CARD") && (orderPaymentPreference.getString("paymentMethodId")?has_content))>
                    <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard")>
                    <p class="leftFloat">${creditCard.get("cardType")?if_exists}</p>
                    <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>"><span class="paymentDetailIcon"></span></a>
              <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD") && isStorePickup?has_content && isStorePickup == "Y") >
                  <p>${uiLabelMap.PayInStoreInfo}</p>
              <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD")) >
                  <p>${uiLabelMap.CashOnDeliveryInfo}</p>
              <#else>
                  <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
                  <#if paymentMethod?has_content>
		              <#assign paymentMethodType = paymentMethod.getRelatedOne("PaymentMethodType")?if_exists>
		              <#if paymentMethodType?has_content>
		               <p class="leftFloat">${paymentMethodType.description?default(paymentMethodType.paymentMethodTypeId)}</p>
                       <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>"><span class="paymentDetailIcon"></span></a>
		              </#if>
                  </#if>
              </#if>
           </#list>
         </div>
         <#if ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "CREDIT_CARD"))>
         <div class="infoCaption">
         <label>${uiLabelMap.NumberCaption}</label>
         </div>
         <div class="infoValue">
                 <#assign cardNumber = creditCard.get("cardNumber")>
                 <#assign cardNumber = cardNumber?substring(cardNumber?length - 4)>
                 <#assign cardNumber = "*" + cardNumber>
                 <p>${cardNumber}</p>
         </div>
          <div class="infoCaption">
         <label>${uiLabelMap.ExpireDateCaption}</label>
         </div>
            <div class="infoValue">
                  <p>${creditCard.get("expireDate")?if_exists}</p>
            </div>
         </#if>
       </div>
      </div>
      <div class="infoRow">
       <div class="infoEntry">
         <div class="infoCaption">
         <label>${uiLabelMap.AmountCaption}</label>
         </div>
         <div class="infoValue">
           <#list orderPayments as orderPaymentPreference>
              <p><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/></p>
           </#list>
         </div>
         <div class="infoCaption">
         <label>${uiLabelMap.StatusCaption}</label>
         </div>
         <div class="infoValue">
           <#list orderPayments as orderPaymentPreference>
             <p> ${oppStatusItem.get("description","OSafeAdminUiLabels",locale)}</p>
           </#list>
         </div>
       </div>
      </div>
      <div class="infoRow">
       <div class="infoEntry">
         <div class="infoCaption">
         <label>${uiLabelMap.AuthorizedCaption}</label>
         </div>
         <div class="infoValue">
              <#if gatewayResponses?has_content>
                  <#list gatewayResponses as gatewayResponse>
                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                    <#assign enumCode = transactionCode.get("enumCode")>
                    <#if (enumCode == "AUTHORIZE")>
                     <p> ${gatewayResponse.transactionDate?string(preferredDateFormat)}</p>
                    </#if>
                  </#list>
              </#if>
         </div>
         <div class="infoCaption">
         <label>${uiLabelMap.AuthorizedRefCaption}</label>
         </div>
         <div class="infoValue">
              <#if gatewayResponses?has_content>
                  <#list gatewayResponses as gatewayResponse>
                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                    <#assign enumCode = transactionCode.get("enumCode")>
                    <#if (enumCode == "AUTHORIZE")>
                      <p>${gatewayResponse.referenceNum?if_exists}</p>
                    </#if>
                  </#list>
              </#if>
         </div>
         <div class="infoCaption">
         <label>${uiLabelMap.CaptureCaption}</label>
         </div>
         <div class="infoValue">
              <#if gatewayResponses?has_content>
                  <#list gatewayResponses as gatewayResponse>
                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                    <#assign enumCode = transactionCode.get("enumCode")>
                    <#if (enumCode == "CAPTURE")>
                        <p>${gatewayResponse.transactionDate?string(preferredDateFormat)}</p>
                    </#if>
                  </#list>
              </#if>
        </div>
         <div class="infoCaption">
         <label>${uiLabelMap.CaptureRefCaption}</label>
         </div>
         <div class="infoValue">
              <#if gatewayResponses?has_content>
                  <#list gatewayResponses as gatewayResponse>
                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                    <#assign enumCode = transactionCode.get("enumCode")>
                    <#if (enumCode == "CAPTURE")>
                        <p>${gatewayResponse.referenceNum?if_exists}</p>
                    </#if>
                  </#list>
              </#if>
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

<!-- start orderDetailPaymentInfo.ftl -->
<#assign currencyUomId = orderReadHelper.getCurrency()>
<#if shipGroups?has_content && orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL">
  <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroups)/> 
  <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
  <div class="infoRow">
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
             <label>${uiLabelMap.ShipDateCaption}</label>
         </div>
         <div class="infoValue">
             <#if shipGroupsSize == 1 >
                 ${(shipGroup.estimatedShipDate?string(preferredDateFormat))!""}
             <#else>
                 <p class="leftFloat">${uiLabelMap.MultipleShipmentsLabel}</p>
                 <a href="<@ofbizUrl>orderShippingDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="shipmentDetailIcon"></span></a>
             </#if>
         </div>
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
		   </#if>
	     </div>
	     
	     <#-- tracking number -->
         <div class="infoCaption">
             <label>${uiLabelMap.TrackingNoCaption}</label>
         </div>
         <div class="infoValue">
             <#if shipGroupsSize == 1 && shipGroup.trackingNumber?has_content >
                 <p class="leftFloat">${shipGroup.trackingNumber!}</p>
                 <a href="<@ofbizUrl>orderShippingDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="shipmentDetailIcon"></span></a>
              </#if>
          </div>
       </#if>
    </div>
  </div>
<#else>
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.ShipMethodCaption}</label>
            </div>
            <div class="infoValue">
                ${uiLabelMap.NoShippingMethodInfo}
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
          <#assign postalAddress = storeContactMechValueMap.postalAddress />
          <#if postalAddress?has_content>
              ${setRequestAttribute("PostalAddress",postalAddress)}
              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
          </#if>
        </#if>
      </div>
    </div>
  </div>
</#if>

<#if orderReadHelper?has_content>
  <#assign orderPayments = orderReadHelper.getPaymentPreferences()/>
  <#if orderPayments?has_content>
    <#list orderPayments as orderPaymentPreference>
     <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
     <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
     <#assign orderPaymentPreferenceId = orderPaymentPreference.getString("orderPaymentPreferenceId")?if_exists>
     <#assign paymentMethodId = orderPaymentPreference.getString("paymentMethodId")?if_exists>
     <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
     <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
      <#if orderPayments.size() == 1>
		  <div class="infoRow">
		   <div class="infoEntry">
		         <div class="infoCaption">
		             <label>${uiLabelMap.PaymentMethodCaption}</label>
		         </div>
		         <div class="infoValue">
	              <#if ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD") && isStorePickup?has_content && isStorePickup == "Y")>
	                  <p>${uiLabelMap.PayInStoreInfo}</p>
	              <#else>
	                 <p class="leftFloat">${paymentMethodType.description?if_exists}</p>
	                 <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>"><span class="paymentDetailIcon"></span></a>
	              </#if>
	             </div>
	       <#if ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "CREDIT_CARD"))>
            <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard")>
				  <div class="infoCaption">
				     <label>${uiLabelMap.CardTypeCaption}</label>
				  </div>
				  <div class="infoValue">
		             <p>${creditCard.get("cardType")?if_exists}</p>
				  </div>
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
	      <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "GIFT_CARD"))>
            <#assign giftCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("GiftCard")>
				  <div class="infoCaption">
				     <label>${uiLabelMap.NumberCaption}</label>
				  </div>
				  <div class="infoValue">
				    <p>${giftCard.cardNumber}</p>
				  </div>
				  <div class="infoCaption">
				     <label>${uiLabelMap.ExpireDateCaption}</label>
				  </div>
				  <div class="infoValue">
				     <p>${giftCard.get("expireDate")?if_exists}</p>
				  </div>
	     </#if>
		         <div class="infoCaption">
		           <label>${uiLabelMap.AmountCaption}</label>
		         </div>
		         <div class="infoValue">
		            <p><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/></p>
		         </div>
		         <div class="infoCaption">
		           <label>${uiLabelMap.StatusCaption}</label>
		         </div>
		         <div class="infoValue">
		             <p> ${oppStatusItem.get("description","OSafeAdminUiLabels",locale)}</p>
		         </div>
	       </div>
	      </div>
                <#if gatewayResponses?has_content>
				 <div class="infoRow">
				   <div class="infoEntry">
			         <div class="infoCaption">
			            <label>${uiLabelMap.AuthorizedCaption}</label>
			         </div>
			         <div class="infoValue">
			                  <#list gatewayResponses as gatewayResponse>
			                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
			                    <#assign enumCode = transactionCode.get("enumCode")>
			                    <#if (enumCode == "AUTHORIZE")>
			                     <p> ${gatewayResponse.transactionDate?string(preferredDateFormat)}</p>
			                    </#if>
			                  </#list>
			         </div>
			         <div class="infoCaption">
			           <label>${uiLabelMap.AuthorizedRefCaption}</label>
			         </div>
			         <div class="infoValue">
			                  <#list gatewayResponses as gatewayResponse>
			                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
			                    <#assign enumCode = transactionCode.get("enumCode")>
			                    <#if (enumCode == "AUTHORIZE")>
			                      <p>${gatewayResponse.referenceNum?if_exists}</p>
			                    </#if>
			                  </#list>
			         </div>
			         <div class="infoCaption">
			           <label>${uiLabelMap.CaptureCaption}</label>
			         </div>
			         <div class="infoValue">
			                  <#list gatewayResponses as gatewayResponse>
			                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
			                    <#assign enumCode = transactionCode.get("enumCode")>
			                    <#if (enumCode == "CAPTURE")>
			                        <p>${gatewayResponse.transactionDate?string(preferredDateFormat)}</p>
			                    </#if>
			                  </#list>
			        </div>
			         <div class="infoCaption">
			           <label>${uiLabelMap.CaptureRefCaption}</label>
			         </div>
			         <div class="infoValue">
			                  <#list gatewayResponses as gatewayResponse>
			                    <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
			                    <#assign enumCode = transactionCode.get("enumCode")>
			                    <#if (enumCode == "CAPTURE")>
			                        <p>${gatewayResponse.referenceNum?if_exists}</p>
			                    </#if>
			                  </#list>
			        </div>
			       </div>
			     </div>
			    </#if>
      <#else>
			 <div class="infoRow">
			   <div class="infoEntry">
			         <div class="infoCaption">
			         <label>${uiLabelMap.PaymentMethodCaption}</label>
			         </div>
			         <div class="infoValue">
		                 <p class="leftFloat">${paymentMethodType.description?if_exists}</p>
		                 <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>"><span class="paymentDetailIcon"></span></a>
		             </div>
			         <div class="infoCaption">
			           <label>${uiLabelMap.AmountCaption}</label>
			         </div>
			         <div class="infoValue">
			            <p><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/></p>
			         </div>
			         <div class="infoCaption">
			           <label>${uiLabelMap.StatusCaption}</label>
			         </div>
			         <div class="infoValue">
			             <p> ${oppStatusItem.get("description","OSafeAdminUiLabels",locale)}</p>
			         </div>
		       </div>
		     </div>
      </#if>
    </#list>
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

<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"<#if defaultFontFamily?has_content>font-family="${defaultFontFamily}"</#if>> 
    
    <#assign logoImageUrl = Static["com.osafe.util.Util"].getProductStoreParm(request, "EMAIL_CLNT_LOGO")/>
    <#assign companyEmail = Static["com.osafe.util.Util"].getProductStoreParm(request, "EMAIL_CLNT_REPLY_TO")/>
    <#assign companyPhone = Static["com.osafe.util.Util"].getProductStoreParm(request, "EMAIL_CLNT_TEL_NO")/>
    
    <#assign orderId=shipment.primaryOrderId!>
    <#assign orderHeader = delegator.findByPrimaryKey("OrderHeader",Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId",orderId))/>
    <#assign orderReadHelper = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
    <#assign productStore = orderReadHelper.getProductStoreFromOrder(delegator,orderId)/>
    <#assign payToPartyId = productStore.payToPartyId>
    <#assign partyGroup =   delegator.findByPrimaryKey("PartyGroup",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId))/>
    <#if partyGroup?has_content>
      <#assign companyName = partyGroup.groupName>
    </#if>
    
     <#-- Company Address -->
    <#assign companyAddresses = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId, "contactMechPurposeTypeId","GENERAL_LOCATION"))/>
    <#assign selAddresses = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(companyAddresses, nowTimestamp, "fromDate", "thruDate", true)/>
    <#if selAddresses?has_content>
     <#assign companyAddress = delegator.findByPrimaryKey("PostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",selAddresses[0].contactMechId))/>
     <#assign country = companyAddress.getRelatedOne("CountryGeo")/>
     <#if country?has_content>
       <#assign countryName = country.get("geoName", locale)/>
     </#if>
     <#assign stateProvince = companyAddress.getRelatedOne("StateProvinceGeo")/>
     <#if stateProvince?has_content>
       <#assign stateProvinceAbbr = stateProvince.abbreviation/>
     </#if>
    </#if>
    <#-- Customer -->
    <#assign formattedHomePhone = ''/>
    <#assign formattedWorkPhone = ''/>
    <#assign formattedCellPhone = ''/>
    <#assign partyWorkPhoneExt = ''/>
    <#assign placingParty = orderReadHelper.getPlacingParty()/>
    <#if placingParty?has_content>

         <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", placingParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
         <#assign partyContactMechPurpose = placingParty.getRelated("PartyContactMechPurpose")/>
         <#assign partyContactMechPurpose = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyContactMechPurpose,true)/>

        <#assign partyPurposeEmails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactMechPurpose, Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechPurposeTypeId", "PRIMARY_EMAIL"))/>
        <#assign partyPurposeEmails = Static["org.ofbiz.entity.util.EntityUtil"].getRelated("PartyContactMech", partyPurposeEmails)/>
        <#assign partyPurposeEmails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyPurposeEmails,true)/>
        <#assign partyPurposeEmails = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(partyPurposeEmails, Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate DESC"))/>
        <#if partyPurposeEmails?has_content> 
        	<#assign partyPurposeEmail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyPurposeEmails)/>
            <#assign customerEmail = partyPurposeEmail.getRelatedOne("ContactMech")/>
            <#assign customerEmailAddress = customerEmail.infoString/>
            <#assign customerEmailAllowSolicitation= partyPurposeEmail.allowSolicitation!""/>
        </#if>

        <#assign partyPurposeHomePhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactMechPurpose, Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechPurposeTypeId", "PHONE_HOME"))/>
        <#assign partyPurposeHomePhones = Static["org.ofbiz.entity.util.EntityUtil"].getRelated("PartyContactMech", partyPurposeHomePhones)/>
        <#assign partyPurposeHomePhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyPurposeHomePhones,true)/>
        <#assign partyPurposeHomePhones = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(partyPurposeHomePhones, Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate DESC"))/>
        <#if partyPurposeHomePhones?has_content> 
        	<#assign partyPurposePhone = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyPurposeHomePhones)/>
        	<#assign telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber")/>
            <#assign phoneHomeTelecomNumber =telecomNumber/>
            <#assign formattedHomePhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneHomeTelecomNumber.areaCode?if_exists, phoneHomeTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        </#if>

        <#assign partyPurposeWorkPhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactMechPurpose, Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechPurposeTypeId", "PHONE_WORK"))/>
        <#assign partyPurposeWorkPhones = Static["org.ofbiz.entity.util.EntityUtil"].getRelated("PartyContactMech", partyPurposeWorkPhones)/>
        <#assign partyPurposeWorkPhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyPurposeWorkPhones,true)/>
        <#assign partyPurposeWorkPhones = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(partyPurposeWorkPhones, Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate DESC"))/>
        <#if partyPurposeWorkPhones?has_content> 
        	<#assign partyPurposePhone = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyPurposeWorkPhones)/>
        	<#assign telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber")/>
            <#assign phoneWorkTelecomNumber =telecomNumber/>
	        <#assign formattedWorkPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneWorkTelecomNumber.areaCode?if_exists, phoneWorkTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
	        <#if partyPurposePhone.extension?has_content>
	          <#assign partyWorkPhoneExt = partyPurposePhone.extension!/> 
	        </#if>
        </#if>

        <#assign partyPurposeMobilePhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactMechPurpose, Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechPurposeTypeId", "PHONE_MOBILE"))/>
        <#assign partyPurposeMobilePhones = Static["org.ofbiz.entity.util.EntityUtil"].getRelated("PartyContactMech", partyPurposeMobilePhones)/>
        <#assign partyPurposeMobilePhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyPurposeMobilePhones,true)/>
        <#assign partyPurposeMobilePhones = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(partyPurposeMobilePhones, Static["org.ofbiz.base.util.UtilMisc"].toList("fromDate DESC"))/>
        <#if partyPurposeMobilePhones?has_content> 
        	<#assign partyPurposePhone = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyPurposeMobilePhones)/>
        	<#assign telecomNumber = partyPurposePhone.getRelatedOne("TelecomNumber")/>
            <#assign phoneMobileTelecomNumber =telecomNumber/>
            <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(phoneMobileTelecomNumber.areaCode?if_exists, phoneMobileTelecomNumber.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        </#if>

     </#if>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="main-page"
              page-width="9.5in" page-height="11in"
              margin-top="0.4in" margin-bottom="0.4in"
              margin-left="0.6in" margin-right="0.4in">
            <#-- main body -->
            <fo:region-body margin-top="1.5in" margin-bottom="0.4in"/>
            <#-- the header -->
            <fo:region-before extent="1.2in"/>
            <#-- the footer -->
            <fo:region-after extent="0.4in"/>
        </fo:simple-page-master>
        <fo:simple-page-master master-name="main-page-landscape"
              page-width="11in" page-height="8.5in"
              margin-top="0.4in" margin-bottom="0.4in"
              margin-left="0.6in" margin-right="0.4in">
            <#-- main body -->
            <fo:region-body margin-top="1.2in" margin-bottom="0.4in"/>
            <#-- the header -->
            <fo:region-before extent="1.2in"/>
            <#-- the footer -->
            <fo:region-after extent="0.4in"/>
        </fo:simple-page-master>
    </fo:layout-master-set>
    
    
    <#assign shipGroup = shipment.getRelatedOne("PrimaryOrderItemShipGroup")?if_exists>
	<#if shipmentPackages?has_content>
        <#assign shipmentPackage = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipmentPackages) />
    </#if>
    <#assign orderReadHelper = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
    <#assign orderPayments = orderReadHelper.getPaymentPreferences()/>
    <#assign shippingTotal = 0.00?number>
    <#assign shippingTotal = shippingTotal + orderReadHelper.getShippingTotal()/>
    <#assign paymentTypeCod = "false"/>
    <#list orderPayments as orderPaymentPreference>
		<#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
		<#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
		<#assign orderPaymentPreferenceId = orderPaymentPreference.getString("orderPaymentPreferenceId")?if_exists>
		<#assign paymentMethodId = orderPaymentPreference.getString("paymentMethodId")?if_exists>
		<#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
		<#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
		<#if ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD")) >
		    <#assign paymentTypeCod = "true"/>
		</#if>
	</#list>
	
	<#assign itemIssuances =  delegator.findByAnd('ItemIssuance', {"orderId" : orderHeader.orderId, "shipmentId": shipment.shipmentId!, "shipGroupSeqId", shipGroup.shipGroupSeqId})!"" />
	<#if itemIssuances?has_content>
		<#list itemIssuances as itemIssuance>
		    <#assign orderItemBillings = delegator.findByAnd('OrderItemBilling', {"orderId" : orderHeader.orderId, "orderItemSeqId": itemIssuance.orderItemSeqId!})!"" />
		    <#if orderItemBillings?has_content>
		        <#assign orderItemBilling = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderItemBillings) /> 
		        <#assign invoiceId = orderItemBilling.invoiceId!/>
		    </#if>
		<#break>     
		</#list>
    </#if>
    <#if invoiceId?has_content>
        <#assign invoice = delegator.findByPrimaryKey('Invoice', {"invoiceId" : invoiceId})!"" />
    </#if>
    <fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">
        <fo:static-content flow-name="xsl-region-before">

        </fo:static-content>
        <#-- the footer -->
        <fo:static-content flow-name="xsl-region-after">

        </fo:static-content>
        
        <fo:flow flow-name="xsl-region-body" >
       	<fo:table table-layout="fixed" width="100%" border-style="solid"  border-width="2px">
        <fo:table-column column-number="1" column-width="proportional-column-width(125)"/>
        <fo:table-body>
            <fo:table-row>
          	<fo:table-cell>
        	    <fo:table  border-bottom-style="solid" border-bottom-width="2px" border-top-width="2px">
                <fo:table-column column-number="1" column-width="proportional-column-width(40)"/>
                <fo:table-column column-number="2" column-width="proportional-column-width(30)"/>
                <fo:table-column column-number="3" column-width="proportional-column-width(30)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                                <fo:block text-align="left" text-indent="2mm">
                                    <#if logoImageUrl?has_content><fo:external-graphic src="${HTTP_HOST}${logoImageUrl!""}" overflow="hidden" content-height="90pt" content-width="180pt"/></#if>
                                </fo:block>
                         </fo:table-cell>       
                         <fo:table-cell>       
                                <fo:block font-size="8pt">
                                <fo:block font-size="12pt" font-weight="bold" text-indent="10mm">${companyName?upper_case}</fo:block>
                                <fo:block wrap-option="wrap" text-indent="10mm">
                                <#if companyAddress?exists>
                                    <#if companyAddress?has_content>
                                      ${setRequestAttribute("PostalAddress",companyAddress)}
                                      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddressPDF")}
                                    </#if>
                                </#if>
                                </fo:block>
                            
                                
                                <fo:list-block provisional-distance-between-starts=".3in">
                                    <#if companyPhone?exists>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block text-indent="10mm">${uiLabelMap.PhoneCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block text-indent="12mm">${companyPhone?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    </#if>
                                    <#if companyEmail?exists>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block text-indent="10mm">${uiLabelMap.EmailCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block wrap-option="wrap" text-indent="12mm">${companyEmail?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    </#if>
                                </fo:list-block>
                                <fo:list-block provisional-distance-between-starts=".3in">
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block text-indent="10mm" font-weight="bold">${uiLabelMap.TinCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block text-indent="10mm"></fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                </fo:list-block>
                                
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                        <fo:block font-size="8pt">
                                <fo:table table-layout="fixed"  border-start-style="solid" border-start-width="2px" >
                  					<fo:table-body>                  					
                    				<fo:table-row >
                    					<fo:table-cell text-align="start">
                    						<fo:block margin-top="2mm" text-indent="2mm" font-weight="bold">${uiLabelMap.InvoiceNoCaption} ${invoiceId!}</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row >
                    				<fo:table-row >
                    					<fo:table-cell text-align="start">
                    						<fo:block margin-top="2mm" text-indent="2mm" font-weight="bold">${uiLabelMap.InvoiceDateCaption} <#if invoice?has_content>${(invoice.invoiceDate?string(preferredDateFormat))!""}</#if></fo:block>
                    					</fo:table-cell>
                    				</fo:table-row >
                    				<fo:table-row >
                    				<fo:table-cell text-align="start">
                    						<fo:block margin-top="2mm" text-indent="2mm" font-weight="bold">${uiLabelMap.VatRegNoCaption}</fo:block>
                    				</fo:table-cell>
                    				</fo:table-row >
                    				<fo:table-row >
                    					<fo:table-cell text-align="start">
                    						<fo:block margin-top="2mm" text-indent="2mm" font-weight="bold">${uiLabelMap.CstRegNoCaption}</fo:block>
                    					</fo:table-cell>
                    				</fo:table-row >
                                    <fo:table-row >
                    					<fo:table-cell text-align="start">
                    						<fo:block margin-top="5mm"></fo:block>
                    					</fo:table-cell>
                    				</fo:table-row >                     				
                    				</fo:table-body>
                    			</fo:table>
                    			</fo:block>
                         </fo:table-cell>
                    </fo:table-row>
                  </fo:table-body>
            </fo:table>
            <fo:block space-after="0.2in"/>        
            <fo:table table-layout="fixed" width="100%"  >
                <fo:table-column column-number="1" column-width="proportional-column-width(25)"/>
                <fo:table-column column-number="2" column-width="proportional-column-width(30)"/>
                <fo:table-column column-number="3" column-width="proportional-column-width(40)"/>
                <fo:table-body>
                  <fo:table-row>
                    <fo:table-cell number-columns-spanned="2" margin-left="2pt">
                      <fo:table table-layout="fixed" >
                      <fo:table-column column-number="1" column-width="proportional-column-width(40)"/>
                	  <fo:table-column column-number="2" column-width="proportional-column-width(60)"/>
                    	<fo:table-body>                 
                  		 <fo:table-row >
                          <fo:table-cell>
                            <fo:block text-align="start" font-weight="bold" text-indent="0.001mm" text-decoration="underline"> ${uiLabelMap.DeliverToLabel}</fo:block>
                            <fo:block text-align="start" text-indent="5mm">
                     			<fo:block font-size="10pt" font-weight="bold" text-indent="0.001mm" wrap-option="wrap">${displayPartyNameResult.fullName?default("[${uiLabelMap.PartyNameNotFoundInfo}]")?upper_case}</fo:block>
                                   <#assign postalAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
                                   <fo:block font-size="10pt" font-weight="bold" wrap-option="wrap" text-indent="0.001mm">
                                    <#if postalAddress?has_content>
                                      ${setRequestAttribute("PostalAddress",postalAddress)}
                                      ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddressPDF")}
                                      <fo:block wrap-option="wrap" text-indent="0.1mm">
                                        <#assign bluedartDstarcd = (delegator.findOne("BlueDartPrepaid", {"pincode", postalCode?if_exists}, false))?if_exists />
                                        <#if bluedartDstarcd?has_content>
                                           - ${bluedartDstarcd.dstarcd!}
                                        </#if>
                                      </fo:block>
                                    </#if>
                                   </fo:block>
                            </fo:block>
                            <fo:block text-align="start" font-size="10pt" text-indent="0.001mm">
                                ${uiLabelMap.PhCaption}
                                <#assign writeComa = false/>
                                <#if formattedHomePhone?has_content>
                                    ${formattedHomePhone!}
                                    <#assign writeComa = true/>
                                </#if>
                                <#if formattedCellPhone?has_content>
                                    <#if writeComa> ,</#if>
                                    ${formattedCellPhone!}
                                    <#assign writeComa = true/>
                                </#if>
                                <#if formattedWorkPhone?has_content>
                                    <#if writeComa> ,</#if>
                                    ${formattedWorkPhone!}
                                    <#if partyWorkPhoneExt?has_content> x${partyWorkPhoneExt}</#if>
                                </#if>
                            </fo:block>
                          </fo:table-cell>
                          <fo:table-cell>
                             <fo:block text-align="center" font-size="10pt" font-weight="bold">${uiLabelMap.OrderIDPDFLabel}</fo:block>
                             <fo:block margin-top="2mm" text-align="center" font-family="Basawa 3 of 9 MHR" font-size="36pt">
                                 <#if shipment.primaryOrderId?has_content>*${shipment.primaryOrderId!}*</#if>
                              </fo:block>
                            </fo:table-cell>
                           </fo:table-row >
                               <#assign pieces = 0?number/>
                               <#assign totalAmountToBeCollected = 0.00?number>
			                   <#assign invoiceItems = delegator.findByAnd('InvoiceItem', {"invoiceId" : invoiceId!})!"" />
			                   <#list invoiceItems as invoiceItem>
			                       <#if invoiceItems?has_content>
			                           <#if invoiceItem.invoiceItemTypeId == 'INV_FPROD_ITEM'>
			                               <#assign pieces = pieces + invoiceItem.quantity?default(0)/>
			                           </#if>
			                           <#if invoiceItem.invoiceItemTypeId == 'INV_FPROD_ITEM' || invoiceItem.invoiceItemTypeId == 'ITM_SALES_TAX' || invoiceItem.invoiceItemTypeId == 'ITM_PROMOTION_ADJ'>
			                               <#assign totalAmountToBeCollected = totalAmountToBeCollected + (invoiceItem.amount)*(invoiceItem.quantity?default(1))!>
			                           </#if>
			                       </#if>
			                   </#list>
			               <#assign totalAmountToBeCollected = totalAmountToBeCollected + shippingTotal/>   
	                       <#if paymentTypeCod == "true">
	                           <fo:table-row height="2px">
	                                  <fo:table-cell number-columns-spanned="2">
	                                      <fo:block text-align="left" font-size="20pt" font-weight="bold" text-indent="5mm">${uiLabelMap.AmountToCollectLabel}</fo:block>
	                                  </fo:table-cell>
	                              </fo:table-row >
	                              <fo:table-row height="2px">
	                                  <fo:table-cell number-columns-spanned="2">
	                                      <fo:block text-align="center" font-size="15pt" font-weight="bold" text-indent="5mm">
	                                          <@ofbizCurrency amount=totalAmountToBeCollected?default(0.00) rounding=globalContext.currencyRounding isoCode=globalContext.defaultCurrencyUomId/>
	                                      </fo:block>
	                                  </fo:table-cell>
	                               </fo:table-row >
	                       </#if>
                           </fo:table-body>                 
                     	  </fo:table>
                                  
                        </fo:table-cell>
                        <fo:table-cell> 

                        	 <fo:table table-layout="fixed">
                        	 <fo:table-column column-number="1" column-width="15mm"/>
                	         <fo:table-column column-number="2" column-width="35mm"/>
                	         <fo:table-column column-number="3" column-width="15mm"/>
                  			    <fo:table-body>                  					
                    			  <fo:table-row >
                    			  <fo:table-cell >
                    			  </fo:table-cell >
                    				<fo:table-cell number-columns-spanned="2"> 
			                            <fo:block text-align="center" font-size="10pt" font-weight="bold">
			                                <#if paymentTypeCod == "true">
			                                    ${uiLabelMap.CashOnDeliveryPDFLabel}
			                                <#else>
			                                    ${uiLabelMap.PrepaidOrderLabel}
			                                </#if>
			                            </fo:block>
			                            <fo:block margin-top="2mm" text-align="center" font-family="Basawa 3 of 9 MHR" font-size="36pt">
			                               <#if shipGroup.trackingNumber?has_content>*${shipGroup.trackingNumber!}*</#if>
			                            </fo:block>
                            		</fo:table-cell>
                                 </fo:table-row>
                                 <fo:table-row>
                                 <fo:table-cell >
                    			 </fo:table-cell >
                                  <fo:table-cell font-size="10pt" text-align="right" font-weight="bold">
                                    <fo:block margin-top="2mm">${uiLabelMap.AwbNoCaption}</fo:block>
                                  </fo:table-cell >
                                  <fo:table-cell >
                                    <fo:block margin-top="2mm" font-size="10pt" text-indent="1mm">${shipGroup.trackingNumber!}</fo:block>
                                  </fo:table-cell >
                                 </fo:table-row>
                                 <fo:table-row> 
                                 <fo:table-cell >
                    			 </fo:table-cell >                                
                                 <fo:table-cell >
                                    <fo:block font-size="10pt" text-align="right" font-weight="bold">${uiLabelMap.WeightCaption}</fo:block>
                                 </fo:table-cell >
                                 <fo:table-cell >
                                    <fo:block font-size="10pt" text-indent="1mm">
									    ${shipmentPackage.weight!}
                                        <#assign weightUom = delegator.findByPrimaryKey('Uom', {"uomId" : shipmentPackage.weightUomId!})!"" />
                                        <#if weightUom?has_content>
                                          (${weightUom.abbreviation!})
                                        </#if>
									</fo:block>
                                 </fo:table-cell >
                                </fo:table-row>
                                <fo:table-row>
                                <fo:table-cell >
                    			</fo:table-cell >
                                <fo:table-cell >
								   <#assign dimensionUom = delegator.findByPrimaryKey('Uom', {"uomId" : shipmentPackage.dimensionUomId!})!"" />
                                   <fo:block font-size="10pt" text-align="right" font-weight="bold">${uiLabelMap.DimensionsCaption}<#if dimensionUom?has_content>(${dimensionUom.abbreviation!})</#if>:</fo:block>
                                </fo:table-cell >
                                <fo:table-cell >
                                  <fo:block font-size="10pt" text-indent="1mm" font-weight="bold">${shipmentPackage.boxLength!}x${shipmentPackage.boxHeight!}x${shipmentPackage.boxWidth!}</fo:block>						   
                                </fo:table-cell >
                               </fo:table-row>
                               <fo:table-row>
                               <fo:table-cell >
                    		   </fo:table-cell >
                                <fo:table-cell >
                                   <fo:block font-size="10pt" text-align="right" font-weight="bold">${uiLabelMap.OrderIdCaption}</fo:block>
                                </fo:table-cell >
                                <fo:table-cell >
                                   <fo:block font-size="10pt" text-indent="1mm">${shipment.primaryOrderId!}</fo:block>
                                </fo:table-cell >
                               </fo:table-row>
                               <fo:table-row>
                                <fo:table-cell >
                    		    </fo:table-cell >
                                <fo:table-cell >
                                   <fo:block font-size="10pt" text-align="right" font-weight="bold">${uiLabelMap.OrderDateCaption}</fo:block>
                                </fo:table-cell >
                                <fo:table-cell >
                                   <fo:block font-size="10pt" text-indent="1mm">${(orderHeader.entryDate?string(preferredDateFormat))!""}</fo:block>
                                </fo:table-cell >
                               </fo:table-row>
                               <fo:table-row>
                                <fo:table-cell >
                    			</fo:table-cell >
                                <fo:table-cell >
                                   <fo:block font-size="10pt" text-align="right" font-weight="bold">${uiLabelMap.PiecesCaption}</fo:block>
                                </fo:table-cell >
                                <fo:table-cell >
                                   <fo:block font-size="10pt" text-indent="1mm" font-weight="bold">${pieces!}</fo:block>
                                </fo:table-cell >
                               </fo:table-row>   
                          	 </fo:table-body>
                            </fo:table>                           
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
            <fo:block space-after="0.2in"/>
           	  <fo:table >
           		<fo:table-column column-number="1" column-width="20mm"/>
                <fo:table-column column-number="2" column-width="90mm"/>
                <fo:table-column column-number="3" column-width="10mm"/>
           		<fo:table-body>                 
                   <fo:table-row >
                          <fo:table-cell>
                              <fo:block text-align="left">
                              </fo:block>
                          </fo:table-cell>
                              
                          <fo:table-cell >
                          <fo:block text-align="center">
	            <fo:table  border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		            <fo:table-column column-width="20mm"/>
		            <fo:table-column column-width="25mm"/>
		            <fo:table-column column-width="25mm"/>
		            <fo:table-column column-width="25mm"/>
		            <fo:table-column column-width="25mm"/>
		            <fo:table-column column-width="25mm"/>
		            <fo:table-column column-width="25mm"/>
		            <fo:table-header font-size="10pt">
		                <fo:table-row >
		                	<fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid" >
		                        <fo:block >${uiLabelMap.SrNoLabel}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.ItemCodeLabel}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.ItemDescriptionLabel}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.QuantityLabel}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.ValueLabel}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.TaxLabel}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.TotalAmountLabel}</fo:block>
		                    </fo:table-cell>        
		                </fo:table-row>
		            </fo:table-header>
	                <fo:table-body border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid" font-size="10pt">
		              <#list packages as package>
		              <#assign grandTotal = 0.00?number>
	                  <#list package as line>
	                        <#assign orderShipments = delegator.findByAnd('OrderShipment', {"orderId" : orderHeader.orderId, "shipmentId": shipment.shipmentId!, "shipGroupSeqId", shipGroup.shipGroupSeqId, "shipmentItemSeqId", line.shipmentItemSeqId!})!"" />
	                        <#if orderShipments?has_content>
	                          <#assign orderShipment = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderShipments) />
	                          <#assign orderItemBillings = delegator.findByAnd('OrderItemBilling', {"orderId" : orderHeader.orderId,"orderItemSeqId", orderShipment.orderItemSeqId!, "invoiceId",invoiceId!})!"" />
	                          <#if orderItemBillings?has_content>
	                              <#assign orderItemBilling =  Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderItemBillings) />
	                              <#assign invoiceItem = delegator.findByPrimaryKey('InvoiceItem', {"invoiceId" : invoiceId,"invoiceItemSeqId", orderItemBilling.invoiceItemSeqId!})!"" />
	                          </#if>
	                        </#if>
	                        <#assign amount = 0.00?number/>
	                        <#if invoiceItem?has_content>
	                            <#assign amount = (invoiceItem.amount)*(invoiceItem.quantity?default(1))! />
	                        </#if>
	                        <#assign invoiceItemTaxAmounts =  delegator.findByAnd('InvoiceItem', {"invoiceId" : invoiceId,"invoiceItemTypeId", "ITM_SALES_TAX"!})!"" />
	                        <#if invoiceItemTaxAmounts?has_content>
	                            <#assign invoiceItemTaxAmount = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(invoiceItemTaxAmounts) />
	                        </#if>
	                        <#assign invoiceItemAdjustmentList = delegator.findByAnd('InvoiceItem', {"invoiceId" : invoiceId,"invoiceItemTypeId", "ITM_PROMOTION_ADJ"!, "productId", line.product.productId})!"" />
	                        
	                        <#if invoiceItemAdjustmentList?has_content>
	                            <#list invoiceItemAdjustmentList as invoiceItemAdjustment>
	                                <#assign amount = amount + invoiceItemAdjustment.amount />
	                            </#list>
	                        </#if>
	                        <#assign taxAmount = 0.00?number/>
	                        <#if invoiceItemTaxAmount?has_content>
	                            <#assign taxAmount = invoiceItemTaxAmount.amount! />
	                        </#if>
	                        <#assign totalAmount = amount + taxAmount />
	                        <#assign grandTotal = grandTotal + totalAmount>    
		                    <fo:table-row>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${line_index + 1}</fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${line.product.productId}</fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >
			                        <#assign productId = line.product.productId />
			                        <#assign virtualProductId = Static["org.ofbiz.product.product.ProductWorker"].getVariantVirtualId(line.product)!/>
			                        <#if virtualProductId?has_content>
			                            <#assign productId = virtualProductId/>
			                        </#if>
			                        <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",productId), false)/>
	                                <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
			                        ${productContentWrapper.get("PRODUCT_NAME")!""}
		                        </fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${line.quantityInPackage?default(0)}</fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${amount!}</fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${taxAmount!}</fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${totalAmount!}</fo:block>
		                      </fo:table-cell>
		                    </fo:table-row>
	                  </#list>
	                  </#list>  
	                 <#assign grandTotal = grandTotal + shippingTotal>
	                    <fo:table-row border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
	                         <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.ShippingLabel}</fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${shippingTotal!}</fo:block>
		                      </fo:table-cell>
	                    </fo:table-row>
	                    <fo:table-row border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
	                         <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block ></fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${uiLabelMap.TotalLabel}</fo:block>
		                      </fo:table-cell>
		                      <fo:table-cell border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
		                        <fo:block >${grandTotal!}</fo:block>
		                      </fo:table-cell>
	                    </fo:table-row>
	                    
	                    
	                  
                </fo:table-body>
              </fo:table>
              </fo:block>
              </fo:table-cell>
              <fo:table-cell>
              </fo:table-cell>
	         </fo:table-row>
	                  
                </fo:table-body>
              </fo:table>
              <fo:block margin-bottom="2mm"/>
          
           <fo:table >
               <fo:table-body>
	               <fo:table-row>
	                   <fo:table-cell>
	            	       <fo:block text-align="center" font-size="10pt" font-weight="bold">${uiLabelMap.MessageLabel}</fo:block>
	                   </fo:table-cell>
	               </fo:table-row>
	           </fo:table-body>
           </fo:table>
                 
      				</fo:table-cell>
                  </fo:table-row>
               </fo:table-body>
          </fo:table>
           
        <fo:block space-after="0.05in"/>
        <fo:table table-layout="fixed" width="100%" border-style="solid"  border-width="2px">
        <fo:table-column column-number="1" column-width="1.3in"/>
        <fo:table-column column-number="2" column-width="2in"/>
        <fo:table-column column-number="3" column-width="5.2in"/>
        <fo:table-body>
	        <fo:table-row>
	         <fo:table-cell >
           		<fo:block font-size="12pt" text-align="left" font-weight="bold">${uiLabelMap.ReturnAddressLabel}</fo:block>
            	</fo:table-cell>
                <fo:table-cell>            		 
                     <fo:block font-weight="bold" font-size="12pt">${companyName?upper_case}</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:table table-layout="fixed" width="100%">
                        <fo:table-column column-number="1" column-width="5in"/>
                        <fo:table-column column-number="2" column-width="1in"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block font-size="10pt">
			                             <#if companyAddress?exists>
                                            <#if companyAddress?has_content>
                                              ${setRequestAttribute("DISPLAY_FORMAT","SINGLE_LINE_FULL_ADDRESS")}
                                              ${setRequestAttribute("PostalAddress",companyAddress)}
                                              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddressPDF")}
                                            </#if>
			                             </#if>
                                     </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block font-size="10pt" font-weight="bold">
		                                <#if companyAddress.postalCode?has_content>
		                                    <#if paymentTypeCod == "true">
		                                        <#assign bluedartCodPin = (delegator.findOne("BlueDartCodpin", {"pincode", companyAddress.postalCode?if_exists}, false))?if_exists />
		                                        <#if bluedartCodPin?has_content>
		                                            ${bluedartCodPin.returnLoc!} <#if bluedartCodPin.retLoc?has_content>/${bluedartCodPin.retLoc!}</#if>
		                                        </#if>
		                                    <#else>
		                                        <#assign bluedartPrepaid = (delegator.findOne("BlueDartPrepaid", {"pincode", companyAddress.postalCode?if_exists}, false))?if_exists />
		                                        <#if bluedartPrepaid?has_content>
		                                            ${bluedartPrepaid.dstarcd!} / ${companyAddress.postalCode!}
		                                        </#if>
		                                    </#if>
		                                </#if>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>
                    </fo:table>
                      
                             
            	</fo:table-cell>
            	</fo:table-row>
            </fo:table-body>
            </fo:table>
      </fo:flow>
    </fo:page-sequence>
            
      </fo:root>
</#escape>

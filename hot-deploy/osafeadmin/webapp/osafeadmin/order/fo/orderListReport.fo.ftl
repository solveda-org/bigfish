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
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
   <#-- inheritance -->
    <#if defaultFontFamily?has_content>font-family="${defaultFontFamily}"</#if>
>
    <fo:layout-master-set>
        <fo:simple-page-master master-name="main-page"
              page-width="8.5in" page-height="11in"
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

  <#if ordersList?has_content>
   <#list ordersList as orderHeader>
    <#assign orderId=orderHeader.orderId>
    <#assign currentStatus=orderHeader.getRelatedOne("StatusItem")>
    <#assign orderReadHelper = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
    <#assign productStore = orderReadHelper.getProductStoreFromOrder(delegator,orderId)/>
    <#assign payToPartyId = productStore.payToPartyId>
    <#assign partyGroup =   delegator.findByPrimaryKey("PartyGroup",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId))/>
    <#if partyGroup?has_content>
      <#assign logoImageUrl = partyGroup.logoImageUrl/>
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
    
     <#-- Company Phone-->
    <#assign phones = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId,"contactMechPurposeTypeId","PRIMARY_PHONE"))/>
     <#if selPhones?has_content>
        <#assign selPhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(phones, nowTimestamp, "fromDate", "thruDate", true)/>
        <#assign companyPhone = delegator.findByPrimaryKey("TelecomNumber", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",selPhones[0].contactMechId))/>
     </#if>
     <#assign faxNumbers = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId,"contactMechPurposeTypeId","FAX_NUMBER"))/>
     <#if faxNumbers?has_content>  
        <#assign faxNumbers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(faxNumbers, nowTimestamp, null, null, true)/>
        <#assign companyFax = delegator.findOne("TelecomNumber", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",faxNumbers[0].contactMechId), false)/>
     </#if>
     <#-- Company Email -->
     <#assign emails = delegator.findByAnd("PartyContactMechPurpose",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId,"contactMechPurposeTypeId","PRIMARY_EMAIL"))/>
     <#if selEmails?has_content>
       <#assign selEmails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(emails, nowTimestamp, "fromDate", "thruDate", true)/>
       <#assign companyEmail = delegator.findByPrimaryKey("ContactMech",Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",selEmails[0].contactMechId))/>
     </#if>

     <#-- Company Website -->
     <#assign websiteUrls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(delegator.findByAnd("PartyContactMechPurpose",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId,"contactMechPurposeTypeId","PRIMARY_WEB_URL")))/>
     <#if websiteUrls?has_content> 
         <#assign websiteUrl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(websiteUrls)/>
         <#assign companyWebsite = delegator.findOne("ContactMech",Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", websiteUrl.contactMechId), false)/>
     </#if>
     
     <#-- Customer -->
    <#assign placingParty = orderReadHelper.getPlacingParty()/>
    <#assign billToEmailList = Static["org.ofbiz.party.contact.ContactHelper"].getContactMech(placingParty, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false)/>
    <#if billToEmailList?has_content>
         <#assign customerEmail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(billToEmailList)/>
         <#assign customerEmailAddress = customerEmail.infoString/>
    </#if>
     
     <#-- Customer Phone -->
      <#assign partyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", placingParty.partyId))/>
      <#assign formattedHomePhone = ''/>
      <#assign formattedWorkPhone = ''/>
      <#assign formattedCellPhone = ''/>
      <#assign partyWorkPhoneExt = ''/>
        <#-- Home Phone -->
      <#if partyContactDetails?has_content>
        <#assign partyHomePhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_HOME"}) />
      </#if>
      <#if partyHomePhoneDetails?has_content>
        <#assign partyHomePhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyHomePhoneDetails?if_exists) />
        <#assign partyHomePhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyHomePhoneDetails?if_exists) />
        <#assign formattedHomePhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyHomePhoneDetail.areaCode?if_exists, partyHomePhoneDetail.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
      </#if>
      
        <#-- Work Phone -->
      <#if partyContactDetails?has_content>
        <#assign partyWorkPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(partyContactDetails,{"contactMechPurposeTypeId" : "PHONE_WORK"}) />
      </#if>
      <#if partyWorkPhoneDetails?has_content>
        <#assign partyWorkPhoneDetails = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(partyWorkPhoneDetails?if_exists) />
        <#assign partyWorkPhoneDetail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyWorkPhoneDetails?if_exists) />
        <#assign formattedWorkPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyWorkPhoneDetail.areaCode?if_exists, partyWorkPhoneDetail.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
        <#if partyWorkPhoneDetail?has_content>
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
        <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyCellPhoneDetail.areaCode?if_exists, partyCellPhoneDetail.contactNumber?if_exists, globalContext.FORMAT_TELEPHONE_NO!)/>
      </#if>
    <fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">

        <#-- Header -->
        <#-- The elements it it are positioned using a table composed by one row
             composed by two cells (each 50% of the total table that is 100% of the page):
             in the left side cell the "topLeft" template is included
             in the right side cell the "topRight" template is included
        -->
        
        <fo:static-content flow-name="xsl-region-before">
            <fo:table table-layout="fixed" width="100%">
                <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
                <fo:table-column column-number="2" column-width="proportional-column-width(50)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                                <fo:block text-align="left">
                                    <#if logoImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${logoImageUrl}</@ofbizContentUrl>" overflow="hidden" height="40px" content-height="scale-to-fit"/></#if>
                                </fo:block>
                                
                                <fo:block font-size="8pt">
                                <fo:block>${companyName}</fo:block>
                                <#if companyAddress?exists>
                                    <#if companyAddress?has_content>
                                        <fo:block>${companyAddress.address1?if_exists}</fo:block>
                                        <#if companyAddress.address2?has_content><fo:block>${companyAddress.address2?if_exists}</fo:block></#if>
                                        <fo:block>${companyAddress.city?if_exists}, ${stateProvinceAbbr?if_exists} ${companyAddress.postalCode?if_exists}, ${countryName?if_exists}</fo:block>
                                    </#if>
                                <#else>
                                    <fo:block>${uiLabelMap.NoPostalAddressInfo}</fo:block>
                                    <fo:block>${uiLabelMap.ForCaption} ${companyName}</fo:block>
                                </#if>
                            
                                <#if companyPhone?exists || companyEmail?exists || companyWebsite?exists>
                                <fo:list-block provisional-distance-between-starts=".5in">
                                    <#if companyPhone?exists>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block>${uiLabelMap.TelephoneAbbrCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block><#if companyPhone.countryCode?exists>${companyPhone.countryCode}-</#if><#if companyPhone.areaCode?exists>(${companyPhone.areaCode})-</#if>${companyPhone.contactNumber?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    </#if>
                                    <#if companyEmail?exists>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block>${uiLabelMap.EmailCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>${companyEmail.infoString?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    </#if>
                                    <#if companyWebsite?exists>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block>${uiLabelMap.WebsiteCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>${companyWebsite.infoString?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    </#if>
                                    <#if eftAccount?exists>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block>${uiLabelMap.BankCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>${eftAccount.bankName?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block>${uiLabelMap.RoutingCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>${eftAccount.routingNumber?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    <fo:list-item>
                                        <fo:list-item-label>
                                            <fo:block>${uiLabelMap.BankAccntNrAbbrCaption}</fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>${eftAccount.accountNumber?if_exists}</fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                    </#if>
                                </fo:list-block>
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        
                        <fo:table-cell>
                  <fo:table>
                    <fo:table-column column-width="2.0in"/>
                    <fo:table-column column-width="2.0in"/>
                    <fo:table-body font-size="9pt">
                   
                    <fo:table-row>
                      <fo:table-cell text-align="end"><fo:block font-weight="bold">${uiLabelMap.OrderNoCaption} </fo:block></fo:table-cell>
                      <fo:table-cell><fo:block start-indent="10pt">${orderId}</fo:block></fo:table-cell>
                    </fo:table-row>

                     <fo:table-row >
                      <fo:table-cell text-align="end"><fo:block font-weight="bold">${uiLabelMap.OrderDateCaption}</fo:block></fo:table-cell>
                      <#assign dateFormat = Static["java.text.DateFormat"].LONG>
                      <#assign orderDate = (Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(orderHeader.orderDate, preferredDateTimeFormat).toLowerCase())?default("N/A")>
                      <fo:table-cell><fo:block start-indent="10pt">${orderDate}</fo:block></fo:table-cell>
                    </fo:table-row>
                    
                    <fo:table-row>
                      <fo:table-cell text-align="end"><fo:block font-weight="bold">${uiLabelMap.OrderStatusCaption}</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block start-indent="10pt">${currentStatus.get("description",locale)}</fo:block></fo:table-cell>
                    </fo:table-row>
                    
                    </fo:table-body>
                  </fo:table>
                        </fo:table-cell>
                    </fo:table-row>
                  </fo:table-body>
            </fo:table>
        </fo:static-content>
        
        <#-- the footer -->
        <fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="10pt" text-align="center" space-before="10pt">
                ${uiLabelMap.PageLabel} <fo:page-number/> ${uiLabelMap.OfLabel} ${ordersList.size()}
            </fo:block>
        </fo:static-content>
        
        <#assign isStorePickup = "N" />
        <#if storePickupMap?has_content>
            <#assign storePickupInfo = storePickupMap.get(orderId) />
            <#if storePickupInfo?has_content>
              <#assign isStorePickup = storePickupInfo.isStorePickup />
            </#if>
        </#if>
        <#-- the body -->
        <fo:flow flow-name="xsl-region-body">
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        <#-- order info -->
        <fo:table table-layout="fixed" width="100%">
        <fo:table-column column-number="1" column-width="proportional-column-width(125)"/>
        <fo:table-body>
            <fo:table-row>
          	<fo:table-cell>
                
                <fo:table table-layout="fixed" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
               
                <fo:table-body>
                
                 <fo:table-row height="20px">
                  <fo:table-cell number-columns-spanned="5">
                    <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${uiLabelMap.OrderDetailInfoHeading}</fo:block>
                  </fo:table-cell>
                 </fo:table-row>
                 
                 <fo:table-row height="20px">
                 
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.OrderNoCaption}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" start-indent="10pt">${orderHeader.orderId!""}</fo:block>
                  </fo:table-cell>
                  
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.OrderStatusCaption}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="start" number-columns-spanned="2">
                        <fo:block font-size="8pt" start-indent="10pt">
                         <#if orderHeader?has_content>
				            <#assign statusItem = orderHeader.getRelatedOne("StatusItem")>
				            ${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}
				        </#if>
                        </fo:block>
                  </fo:table-cell>
                  
                 </fo:table-row>
                 
                 
                 
                 
                 <fo:table-row height="20px">
                 
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.OrderDateCaption}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" start-indent="10pt">
                        <#if orderHeader?has_content>
				            ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(orderHeader.orderDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
				        </#if>
                        </fo:block>
                  </fo:table-cell>
                  
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.ExportStatusCaption}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="start" number-columns-spanned="2">
                        <fo:block font-size="8pt" start-indent="10pt">
                          <#if orderHeader?has_content>
					          <#assign orderAttribute = delegator.findOne("OrderAttribute", {"orderId" : orderHeader.orderId, "attrName" : "IS_DOWNLOADED"}, false)!"" />
					          <#if orderAttribute?has_content>
					            <#assign downloadStatus = orderAttribute.attrValue!"">
					          </#if>
					            <#--assign downloadStatus = orderHeader.isDownloaded!""-->
					            <#if downloadStatus?has_content && downloadStatus == "Y">
					               ${uiLabelMap.ExportStatusInfo}
					            <#else>
					               ${uiLabelMap.DownloadNewInfo}
					            </#if>
					        </#if>
                        </fo:block>
                  </fo:table-cell>
                  
                 </fo:table-row>
                 
        
                 
                 <#if orderHeader?has_content>
                        <#assign orderHeaderInfo = delegator.findOne("OrderHeader", {"orderId" : orderHeader.orderId}, false)!"" />
                 </#if>
                 
                 
                 <fo:table-row height="20px">
                 
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.OrderVisitIdCaption}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" start-indent="10pt">
                        <#if orderHeaderInfo?has_content>
				             ${orderHeaderInfo.visitId!}
				         </#if>
                        </fo:block>
                  </fo:table-cell>
                  
                  <fo:table-cell text-align="start" number-columns-spanned="1">
                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CreatedByCaption}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="start" number-columns-spanned="2">
                        <fo:block font-size="8pt" start-indent="10pt">
                          <#if orderHeaderInfo?has_content>
				            ${orderHeaderInfo.createdBy!}
				         </#if>
                        </fo:block>
                  </fo:table-cell>
                  
                 </fo:table-row>
                 
                 
                 
                 
                 
                
                 <fo:table-row>
                   <fo:table-cell number-columns-spanned="5">
                     <fo:block font-weight="bold" font-size="10pt" text-align="center"></fo:block>
                   </fo:table-cell>
                  </fo:table-row>  
                               
               </fo:table-body>
               </fo:table>
         
              </fo:table-cell>
          	</fo:table-row>
       </fo:table-body>
       </fo:table>
        
        
        <fo:block space-after="0.2in"/>
        <#-- end order info -->
        
        
        
        
        
        
        
        
        
        
        
        
        
        <fo:table table-layout="fixed" width="100%">
        <fo:table-column column-number="1" column-width="proportional-column-width(48)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(4)"/>
        <fo:table-column column-number="3" column-width="proportional-column-width(48)"/>
          <fo:table-body>
            <fo:table-row>
              <fo:table-cell>
                <fo:table table-layout="fixed" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
                <fo:table-body>
                 <fo:table-row height="20px">
                  <fo:table-cell number-columns-spanned="2">
                    <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${uiLabelMap.CustomerInfoHeading}</fo:block>
                  </fo:table-cell>
                 </fo:table-row>
                 <fo:table-row height="20px">
                  <fo:table-cell text-align="start" >
                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CustomerIdCaption}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="start">
                        <fo:block font-size="8pt" start-indent="10pt">${placingParty.partyId}</fo:block>
                  </fo:table-cell>
                 </fo:table-row>
                 <fo:table-row>
                   <fo:table-cell number-columns-spanned="2">
                     <fo:block font-weight="bold" font-size="10pt" text-align="center"></fo:block>
                   </fo:table-cell>
                  </fo:table-row>
                    <#assign orderContactMechs = Static["org.ofbiz.party.contact.ContactMechWorker"].getOrderContactMechValueMaps(delegator, orderHeader.get("orderId"))>
                    <#list orderContactMechs as orderContactMechValueMap>
                        <#assign contactMech = orderContactMechValueMap.contactMech>
                        <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
                            <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                            <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION" || (contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION" && (!isStorePickup?has_content || isStorePickup != "Y"))>
              <fo:table-row>
                            <#assign postalAddress = orderContactMechValueMap.postalAddress>
                       <fo:table-cell text-align="start">
                             <fo:block font-size="8pt" text-align="right" font-weight="bold" > <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION">${uiLabelMap.BillingAddressCaption}
                            </#if>
                        <#if contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION">${uiLabelMap.ShippingAddressCaption}
                        </#if>
                            </fo:block>
                       </fo:table-cell>
                       <fo:table-cell>
                            <fo:block font-size="7pt" start-indent="10pt">
                                <#if postalAddress?has_content>
                                    <#if postalAddress.toName?has_content><fo:block>${postalAddress.toName?if_exists}</fo:block></#if>
                                <fo:block>${postalAddress.address1?if_exists}</fo:block>
                                    <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
                                <fo:block>
                                        <#assign stateGeo = (delegator.findOne("Geo", {"geoId", postalAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
                                        ${postalAddress.city}<#if stateGeo?has_content && stateGeo.geoId != '_NA_'>, ${stateGeo.geoName?if_exists}</#if> ${postalAddress.postalCode?if_exists}
                                </fo:block>
                                <fo:block>
                                        <#assign countryGeo = (delegator.findOne("Geo", {"geoId", postalAddress.countryGeoId?if_exists}, false))?if_exists />
                                        <#if countryGeo?has_content>${countryGeo.geoName?if_exists}</#if>
                                </fo:block>
                                </#if>
                            </fo:block>
                       </fo:table-cell>
              </fo:table-row>
                        </#if>
                      </#if>
                        
                    </#list>

                    <#assign billToEmailList = Static["org.ofbiz.party.contact.ContactHelper"].getContactMech(placingParty, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false)/>
                                <#if billToEmailList?has_content>
                                     <#assign customerEmail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(billToEmailList)/>
                                     <#assign customerEmailAddress = customerEmail.infoString/>
                                </#if>
                                <fo:table-row height="20px">
                                  <fo:table-cell text-align="start" >
                                    <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.EmailAddressCaption}</fo:block>
                                  </fo:table-cell>
                                  <fo:table-cell>
                                    <fo:block font-size="7pt" start-indent="10pt">${customerEmailAddress!""}</fo:block>
                                  </fo:table-cell>
                                </fo:table-row>
                                 
                                 <#if formattedHomePhone?has_content>
                                 <fo:table-row>
                                   <fo:table-cell text-align="start" >
                                     <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.HomePhoneCaption}</fo:block>
                                   </fo:table-cell>
                                   <fo:table-cell>
                                     <fo:block font-size="7pt" start-indent="10pt">
                                       ${formattedHomePhone!}
                                     </fo:block>
                                   </fo:table-cell>
                                 </fo:table-row>
                                 </#if>
                                 
                                 <#if formattedCellPhone?has_content>
                                 <fo:table-row>
                                   <fo:table-cell text-align="start" >
                                     <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CellPhoneCaption}</fo:block>
                                   </fo:table-cell>
                                   <fo:table-cell>
                                     <fo:block font-size="7pt" start-indent="10pt">
                                       ${formattedCellPhone!}
                                     </fo:block>
                                   </fo:table-cell>
                                 </fo:table-row>
                                 </#if>
                                 
                                 <#if formattedWorkPhone?has_content>
                                 <fo:table-row>
                                   <fo:table-cell text-align="start" >
                                     <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.WorkPhoneCaption}</fo:block>
                                   </fo:table-cell>
                                   <fo:table-cell>
                                     <fo:block font-size="7pt" start-indent="10pt">
                                       ${formattedWorkPhone!}<#if partyWorkPhoneExt?has_content> x${partyWorkPhoneExt}</#if>
                                     </fo:block>
                                   </fo:table-cell>
                                 </fo:table-row>
                                 </#if>
                              
               </fo:table-body>
               </fo:table>
              </fo:table-cell>
              <fo:table-cell>
              </fo:table-cell>
              <fo:table-cell>
                <fo:table table-layout="fixed" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
                  <fo:table-body>
                    <fo:table-row height="20px">
                        <fo:table-cell number-columns-spanned="2">
                            <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${uiLabelMap.PaymentInfoHeading}</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell text-align="start" >
                            <fo:block font-size="8pt" font-weight="bold" text-align="right">${uiLabelMap.ShipMethodCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>

                        <#assign shipGroups = delegator.findByAnd("OrderItemShipGroup", {"orderId" : orderId})>
                        <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroups) />
                        <#if shipGroups?has_content>
                          <#if isStorePickup?has_content && isStorePickup == "Y">
                            <fo:block font-size="8pt" start-indent="10pt">${uiLabelMap.PickupInStoreLabel}</fo:block>
                          <#else>
                         
		                            <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
		                            <#assign shipGroupAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
		                            
		                            <#if orderHeader.orderTypeId == "SALES_ORDER" && shipGroup.shipmentMethodTypeId?has_content>
		                              <#if shipGroup.carrierPartyId?has_content || shipmentMethodType?has_content>
		                                <#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_REJECTED">
		                                
		                                  <fo:block font-size="8pt" start-indent="10pt">
		                                    <#if shipGroup.carrierPartyId?has_content>
		                                      <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
		                                        <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)}</#if>
		                                    </#if>
		                                    ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
		                                  </fo:block>
		                                  
		                                </#if>
		                              </#if>
		                            </#if>
                          
                          </#if>
                        <#else>
                          <fo:block font-size="8pt" start-indent="10pt">${uiLabelMap.NoShippingMethodInfo}</fo:block>
                        </#if>
                        </fo:table-cell>
                      </fo:table-row>
                      
                      <fo:table-row  height="20px">
                        <fo:table-cell text-align="start" >
                          <#if shipGroup.trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
                            <fo:block font-size="8pt" font-weight="bold" text-align="right">
                              ${uiLabelMap.TrackingNoCaption}
                            </fo:block>
                          </#if>
                        </fo:table-cell>
                        <fo:table-cell>
                          <#if shipGroup.trackingNumber?has_content>
                            <fo:block font-size="8pt" start-indent="10pt">
                              ${shipGroup.trackingNumber}
                            </fo:block>
                          </#if>
                        </fo:table-cell>
                      </fo:table-row>
                      
                      <#if isStorePickup?has_content && isStorePickup == "Y">
                        <#assign storeContactMechValueMap = storePickupInfo.storeContactMechValueMap! />
                        <#if storeContactMechValueMap?has_content>
                          <fo:table-row>
                            <fo:table-cell>
                              <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.StoreAddressCaption}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                              <#assign storeInfo = storePickupInfo.storeInfo! />
                              <#if storeInfo?has_content>
                                <fo:block font-size="7pt" start-indent="10pt">${storeInfo.groupName!}(${storeInfo.groupNameLocal!})</fo:block>
                              </#if>
                            </fo:table-cell>
                          </fo:table-row>

                          <#assign postalAddress = storeContactMechValueMap.postalAddress />
                          <#if postalAddress?has_content>
                              <fo:table-row>
                                <fo:table-cell>
                                  <fo:block font-size="8pt" font-weight="bold"  text-align="right"></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                  <fo:block font-size="7pt">
                                    <#if postalAddress.toName?has_content>
                                      <fo:block start-indent="10pt">${postalAddress.toName}</fo:block>
                                    </#if>
                                    <#if postalAddress.attnName?has_content>
                                      <fo:block start-indent="10pt">${postalAddress.attnName}</fo:block>
                                    </#if>
                                    <fo:block start-indent="10pt">${postalAddress.address1}</fo:block>
                                    <#if postalAddress.address2?has_content>
                                      <fo:block start-indent="10pt">${postalAddress.address2}</fo:block>
                                    </#if>
                                    <fo:block start-indent="10pt">
                                      ${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId} </#if>
                                      ${postalAddress.postalCode?if_exists}
                                    </fo:block>
                                    <fo:block start-indent="10pt">${postalAddress.countryGeoId?if_exists}</fo:block>
                                  </fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                              <fo:table-row height="10px">
                                <fo:table-cell>
                                  <fo:block font-size="8pt" font-weight="bold"  text-align="right"></fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                  <fo:block font-size="7pt"></fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                          </#if>
                        </#if>
                      </#if>

                      <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.PaymentMethodCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                             <#assign orderPayments = orderReadHelper.getPaymentPreferences()/>
                            <#if orderPayments?has_content>
                            <#list orderPayments as orderPaymentPreference>
                                <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
                                <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                                <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>          
                                <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                                <#if ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "CREDIT_CARD") && (orderPaymentPreference.getString("paymentMethodId")?has_content))>
                                    <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard")>
                                    <fo:block start-indent="10pt">${creditCard.get("cardType")?if_exists}</fo:block>
                                <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD") && isStorePickup?has_content && isStorePickup == "Y")>
                                    <fo:block start-indent="10pt">${uiLabelMap.PayInStoreInfo}</fo:block>
                                <#else>
                                    <#assign paymentMethodType = paymentMethod.getRelatedOne("PaymentMethodType")>
                                    <fo:block start-indent="10pt">${paymentMethodType.description?default(paymentMethodType.paymentMethodTypeId)}</fo:block>
                                </#if>
                            </#list>
                            <#else>
                          		<fo:block font-size="8pt" start-indent="10pt">${uiLabelMap.NoPaymentMethodInfo}</fo:block>
                            </#if>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      
                      
                      
                      
                   <#if ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "CREDIT_CARD"))>
                      <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.NumberCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                              <#assign cardNumber = creditCard.get("cardNumber") />
                              <fo:block start-indent="10pt">${cardNumber}</fo:block>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row height="20px">
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.ExpireDateCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                               <fo:block start-indent="10pt">${creditCard.get("expireDate")?if_exists}</fo:block>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                    </#if>
                    
                    

                      <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.AmountCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                            <#list orderPayments as orderPaymentPreference>
                                <fo:block start-indent="10pt"><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=globalContext.defaultCurrencyUomId/></fo:block>
                            </#list>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                     <fo:table-row height="20px">
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.StatusCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                            <#list orderPayments as orderPaymentPreference>
                                <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                                <fo:block start-indent="10pt">${oppStatusItem.get("description",locale)}</fo:block>
                            </#list>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                     <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.AuthorizedCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                            <#list orderPayments as orderPaymentPreference>
                                <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                                <#if (gatewayResponses?has_content)>
                               <#list gatewayResponses as gatewayResponse>
                                <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                                <#assign enumCode = transactionCode.get("enumCode")>
                                   <#if (enumCode == "AUTHORIZE")>
                                      <fo:block start-indent="10pt">${gatewayResponse.transactionDate?string(preferredDateFormat)}</fo:block>
                                   </#if>
                                </#list>
                                </#if>
                            </#list>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                     <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.AuthorizedRefCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                            <#list orderPayments as orderPaymentPreference>
                                <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                                <#if (gatewayResponses?has_content)>
                               <#list gatewayResponses as gatewayResponse>
                                <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                                <#assign enumCode = transactionCode.get("enumCode")>
                                   <#if (enumCode == "AUTHORIZE")>
                                      <fo:block start-indent="10pt">${gatewayResponse.referenceNum?if_exists}</fo:block>
                                   </#if>
                                </#list>
                                </#if>
                            </#list>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                     <fo:table-row>
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.CaptureCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                            <#list orderPayments as orderPaymentPreference>
                                <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                                <#if (gatewayResponses?has_content)>
                               <#list gatewayResponses as gatewayResponse>
                                <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                                <#assign enumCode = transactionCode.get("enumCode")>
                                   <#if (enumCode == "CAPTURE")>
                                      <fo:block start-indent="10pt">${gatewayResponse.transactionDate?string(preferredDateFormat)}</fo:block>
                                   </#if>
                                </#list>
                                </#if>
                            </#list>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                     <fo:table-row height="20px">
                        <fo:table-cell>
                            <fo:block font-size="8pt" font-weight="bold"  text-align="right">${uiLabelMap.CaptureRefCaption}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                           <fo:block font-size="7pt">
                            <#list orderPayments as orderPaymentPreference>
                                <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                                <#if (gatewayResponses?has_content)>
                               <#list gatewayResponses as gatewayResponse>
                                <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                                <#assign enumCode = transactionCode.get("enumCode")>
                                   <#if (enumCode == "CAPTURE")>
                                      <fo:block start-indent="10pt">${gatewayResponse.referenceNum?if_exists}</fo:block>
                                   </#if>
                                </#list>
                                </#if>
                            </#list>
                           </fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                

               </fo:table-body>
             </fo:table>
              </fo:table-cell>
            </fo:table-row>
          </fo:table-body>
        </fo:table>
        
               
             

    <fo:block space-after="0.2in"/>
    
<#-- Order Items -->

<#assign orderItems = orderReadHelper.getOrderItems()/>
<#if orderItems?exists && orderItems?has_content>

 
    <fo:table border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
        <fo:table-body>
            <fo:table-row>
            <fo:table-cell>
            <fo:table>
            <fo:table-column column-width=".6in"/>
            <fo:table-column column-width=".7in"/>
            <fo:table-column column-width="1.35in"/>
            <fo:table-column column-width="1.35in"/>
            <fo:table-column column-width=".7in"/>
            <fo:table-column column-width=".3in"/>
            <fo:table-column column-width=".68in"/>
            <fo:table-column column-width=".68in"/>
            <fo:table-column column-width=".5in"/>
            <fo:table-column column-width=".62in"/>
            <fo:table-header font-size="8pt" font-weight="bold" background-color="#EEEEEE">
                <fo:table-row >
                	<fo:table-cell >
                        <fo:block >${uiLabelMap.ItemSeqIdLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ProductNoLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ItemNoLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ProductNameLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ItemStatusLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.QtyLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block >${uiLabelMap.UnitPriceLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block >${uiLabelMap.OfferPriceLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>${uiLabelMap.AdjustAmountLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>${uiLabelMap.ItemTotalLabel}</fo:block>
                    </fo:table-cell>
                    
                    
                </fo:table-row>
            </fo:table-header>
            
            
            
             <fo:table-body font-size="8pt">
                <#assign orderItems = orderReadHelper.getOrderItems()/>
                <#assign orderAdjustments = orderReadHelper.getAdjustments()>
                <#assign orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments()>
                <#assign headerAdjustmentsToShow = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false)/>
                <#assign orderSubTotal = orderReadHelper.getOrderItemsSubTotal()>
                <#assign currencyUomId = orderReadHelper.getCurrency()>
                <#assign otherAdjAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false)>
                <#assign shippingAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true)>
                <#assign shippingAmount = shippingAmount.add(Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true))>
                <#assign taxAmount = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal>
                <#assign grandTotal = orderReadHelper.getOrderGrandTotal()>
                <#list orderItems as orderItem>
                    <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                    <#assign productId = orderItem.productId?if_exists>
                    <#assign itemProduct = orderItem.getRelatedOne("Product")/>
                    <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
                    <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                    <#assign itemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
                    <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(itemProduct,request)>
                    <#assign productName = productContentWrapper.get("PRODUCT_NAME")!itemProduct.productName!"">
                    <#if productName="">
	                	<#if itemProduct.isVariant?if_exists?upper_case == "Y">
	                       	<#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(productId, delegator)?if_exists>
	                   	</#if>
	                   	<#assign productName = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(virtualProduct, 'PRODUCT_NAME', request)?if_exists>
	                </#if>
                    <#assign product = orderItem.getRelatedOne("Product")?if_exists>
                    <#assign itemPromoAdjustment = (orderReadHelper.getOrderItemAdjustmentsTotal(orderItem, true, false, false)/orderItem.quantity)/>
                	<#assign offerPrice = orderItem.unitPrice + itemPromoAdjustment/>
                    <fo:table-row>
                    	<fo:table-cell>
                            <fo:block>
                                <#if orderItem?exists>
                                    ${orderItem.orderItemSeqId}
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <#if productId?exists>
                                    ${productId}
                                <#elseif orderItemType?exists>
                                    ${orderItemType.get("description",locale)} - ${orderItem.itemDescription?if_exists}
                                <#else>
                                    ${orderItem.itemDescription?if_exists}
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <#if product?has_content>
                                    ${product.internalName?if_exists}
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <#if orderItem.supplierProductId?has_content>
                                    ${orderItem.supplierProductId} - ${orderItem.itemDescription?if_exists}
                                <#elseif productId?exists>
                                    ${productName?if_exists}
                                <#elseif orderItemType?exists>
                                    ${orderItemType.get("description",locale)} - ${orderItem.itemDescription?if_exists}
                                <#else>
                                    ${orderItem.itemDescription?if_exists}
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell> 
                        <fo:block>
                            <#if itemStatus.description?has_content>${itemStatus.description}
                            </#if>
                        </fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block>${remainingQuantity}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block><@ofbizCurrency amount=orderItem.unitPrice isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block><#if (itemPromoAdjustment < 0)><@ofbizCurrency amount=offerPrice isoCode=currencyUomId/></#if></fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block><@ofbizCurrency amount=itemAdjustment isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block><@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <#if itemAdjustment != 0>
                        <fo:table-row>
                            <fo:table-cell>
                                <fo:block >
                                    <fo:inline font-style="italic">${uiLabelMap.AdjustmentsCaption}</fo:inline>
                                    <@ofbizCurrency amount=itemAdjustment isoCode=currencyUomId/>
                                </fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#if>
                </#list>
               </fo:table-body>
               </fo:table>
            </fo:table-cell>
            </fo:table-row>
            
            <fo:table-row>
              <fo:table-cell>
            <fo:table>
                <fo:table-body>
                <fo:table-row>
                <fo:table-cell>
                </fo:table-cell>
                <#-- summary of order amounts -->
                <fo:table-cell>
                <fo:table border-bottom-style="solid" border-end-style="solid" border-top-style="solid" border-start-style="solid">
              <fo:table-body font-size="8pt">
                  <fo:table-row>
                    <fo:table-cell></fo:table-cell>
                    <fo:table-cell >
                        <fo:block text-align="right">${uiLabelMap.SubtotalCaption}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="right"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <#list headerAdjustmentsToShow as orderHeaderAdjustment>
                      <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
                      <#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo")!"">
                      <#if productPromo?has_content>
                         <#assign promoText = productPromo.promoText?if_exists/>
                         <#assign proomoCodeText = "" />
                         <#assign productPromoCode = productPromo.getRelated("ProductPromoCode")>
                         <#assign promoCodesEntered = orderReadHelper.getProductPromoCodesEntered()!""/>
                         <#if promoCodesEntered?has_content>
                            <#list promoCodesEntered as promoCodeEntered>
                              <#if productPromoCode?has_content>
                                <#list productPromoCode as promoCode>
                                  <#assign promoCodeEnteredId = promoCodeEntered/>
                                  <#assign promoCodeId = promoCode.productPromoCodeId!""/>
                                  <#if promoCodeEnteredId?has_content>
                                      <#if promoCodeId == promoCodeEnteredId>
                                         <#assign promoCodeText = promoCode.productPromoCodeId?if_exists/>
                                      </#if>
                                  </#if>
                                </#list>
                              </#if>
                             </#list>
                         </#if>
                      </#if>
                      <fo:table-row>
                            <fo:table-cell number-columns-spanned="2">
                                <fo:block text-align="right"><#if promoText?has_content>${promoText}<#if promoCodeText?has_content> (${promoCodeText})</#if><#else>${adjustmentType.get("description",locale)?if_exists}</#if>:</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block text-align="right"><@ofbizCurrency amount=orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) isoCode=currencyUomId/></fo:block>
                            </fo:table-cell>
                      </fo:table-row>
                  </#list>
                  <fo:table-row>
                    <fo:table-cell></fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="right">${uiLabelMap.ShipHandleCaption}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="right"><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                    <#if (!Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(CHECKOUT_SUPPRESS_TAX_IF_ZERO!"")) || (taxAmount?has_content && (taxAmount &gt; 0))>
                        <fo:table-row>
                            <fo:table-cell></fo:table-cell>
                            <fo:table-cell>
                                <fo:block text-align="right"><#if (taxAmount?default(0)> 0)>${uiLabelMap.TaxTotalCaption}<#else>${uiLabelMap.SalesTaxCaption}</#if></fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block text-align="right"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId/></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </#if>
                    <fo:table-row>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell>
                            <fo:block font-weight="bold" text-align="right">${uiLabelMap.OrderTotalCaption}</fo:block>
                         </fo:table-cell>
                         <fo:table-cell>
                            <fo:block font-weight="bold" text-align="right"><@ofbizCurrency amount=grandTotal isoCode=currencyUomId/></fo:block>
                         </fo:table-cell>
                    </fo:table-row>
             </fo:table-body>
        </fo:table>
                </fo:table-cell>
               </fo:table-row>
               
               <fo:table-row>
                <fo:table-cell>
                  <fo:table>
                    <fo:table-body>
                     <fo:table-row height="3px">
                       <fo:table-cell>
                         <fo:block></fo:block>
                       </fo:table-cell>
                     </fo:table-row>
                     </fo:table-body>
                     </fo:table>
                   </fo:table-cell>
               </fo:table-row>
                   
               </fo:table-body>
               </fo:table>
               </fo:table-cell>
            </fo:table-row>
            
        </fo:table-body>
    </fo:table>
<#else>

  <fo:table border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
        <fo:table-body>
            <fo:table-row>
            <fo:table-cell>
            <fo:table>
            <fo:table-column column-width=".8in"/>
            <fo:table-column column-width=".72in"/>
            <fo:table-column column-width="1.4in"/>
            <fo:table-column column-width="1in"/>
            <fo:table-column column-width=".8in"/>
            <fo:table-column column-width=".8in"/>
            <fo:table-column column-width=".98in"/>
            <fo:table-column column-width=".98in"/>
            <fo:table-header font-size="8pt" font-weight="bold" background-color="#EEEEEE">
                <fo:table-row >
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ProductIdLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ItemNoLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ProductNameLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ItemStatusLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.QuantityLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block >${uiLabelMap.UnitListLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>${uiLabelMap.AdjustAmountLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>${uiLabelMap.SubTotalLabel}</fo:block>
                    </fo:table-cell>         
                </fo:table-row>
            </fo:table-header>   
             <fo:table-body font-size="8pt">
		<fo:table-row>
                <fo:table-cell>
                  <fo:table>
                    <fo:table-body>
                     <fo:table-row height="15px">
                       <fo:table-cell>
                         <fo:block></fo:block>
                       </fo:table-cell>
                     </fo:table-row>
                     </fo:table-body>
                     </fo:table>
                   </fo:table-cell>
               </fo:table-row>

			<fo:table-row>
                <fo:table-cell>
                  <fo:table>
                    <fo:table-body>
                     <fo:table-row height="3px">
                       <fo:table-cell>
                         <fo:block></fo:block>
                       </fo:table-cell>
                     </fo:table-row>
                     </fo:table-body>
                     </fo:table>
                   </fo:table-cell>
               </fo:table-row>
                   
               </fo:table-body>
               </fo:table>
               </fo:table-cell>
            </fo:table-row>
            
        </fo:table-body>
    </fo:table>
   
</#if>
        <#-- Order Items -->
        <#-- Order Notes Start-->
          <fo:block space-after="0.2in"/>
    
    
    <fo:table border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:table>
              <fo:table-column column-width="1in"/>
              <fo:table-column column-width="1in"/>
              <fo:table-column column-width="1in"/>
              <fo:table-column column-width="1in"/>
              <fo:table-column column-width="3.475in"/>
              <fo:table-header font-size="8pt" font-weight="bold" background-color="#EEEEEE">
                <fo:table-row >
                  <fo:table-cell >
                    <fo:block >${uiLabelMap.NoteNoLabel}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block >${uiLabelMap.ByLabel}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block text-align="center">${uiLabelMap.DateLabel}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block text-align="center">${uiLabelMap.TimeLabel}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block >${uiLabelMap.NoteLabel}</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-header>
              <fo:table-body font-size="8pt">
                  <#assign noteList = delegator.findByAnd("OrderHeaderNoteView", {"orderId" : orderId!})/>
                  <#if noteList?exists && noteList?has_content>
                    <#list noteList as note>
                    <fo:table-row>
                      <fo:table-cell>
                        <fo:block>${note.noteId?if_exists}</fo:block>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block>${note.noteParty?if_exists}</fo:block>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block text-align="center">
                          <#assign noteDateTime = (Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(note.noteDateTime, preferredDateTimeFormat).toLowerCase())!"N/A"/>
                          <#assign noteDateTime = noteDateTime?split(" ")/>
                          <#assign noteDate=noteDateTime[0]!"" />
                          <#assign noteTime=noteDateTime[1]!""/>
                          ${noteDate?if_exists}
                        </fo:block>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block text-align="center">${noteTime?if_exists}</fo:block>
                      </fo:table-cell>
                      <fo:table-cell>
                        <fo:block>${note.noteInfo?if_exists}</fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                    </#list>
                  <#else>
                    <fo:table-row>
                      <fo:table-cell number-columns-spanned="5">
                        <fo:block text-align="center">${uiLabelMap.NoDataAvailableInfo}</fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                  </#if>
                </fo:table-body>
              </fo:table>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
        <#--Order Notes End -->
        
        
        <#-- Order Attributes Start-->
          <fo:block space-after="0.2in"/>
    
    
    <fo:table border-bottom-style="solid" border-top-style="solid" border-end-style="solid" border-start-style="solid">
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:table>
              <fo:table-column column-width="3.735in"/>
              <fo:table-column column-width="3.735in"/>
              <fo:table-header font-size="8pt" font-weight="bold" background-color="#EEEEEE">
                <fo:table-row >
                  <fo:table-cell >
                    <fo:block >${uiLabelMap.AttributeNameLabel}</fo:block>
                  </fo:table-cell>
                  <fo:table-cell >
                    <fo:block >${uiLabelMap.AttributeValueLabel}</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-header>
              <fo:table-body font-size="8pt">
                  <#assign attributeList = delegator.findByAnd("OrderAttribute", {"orderId" : orderId!})/>
                  <#if attributeList?exists && attributeList?has_content>
                    <#list attributeList as attribute>
                    	<#if attribute.attrName = "DATETIME_DOWNLOADED">
				      		<#assign exportedDateTs = Static["java.sql.Timestamp"].valueOf(attribute.attrValue)/>
				      		<#assign attributeValue = (Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(exportedDateTs, preferredDateTimeFormat)) />
				        <#else>
				      		<#assign attributeValue = attribute.attrValue!/>
				        </#if>
	                    <fo:table-row>
	                      <fo:table-cell>
	                        <fo:block>${attribute.attrName?if_exists}</fo:block>
	                      </fo:table-cell>
	                      <fo:table-cell>
	                        <fo:block>${attributeValue?if_exists}</fo:block>
	                      </fo:table-cell>
	                    </fo:table-row>
                    </#list>
                  <#else>
                    <fo:table-row>
                      <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="center">${uiLabelMap.NoDataAvailableInfo}</fo:block>
                      </fo:table-cell>
                    </fo:table-row>
                  </#if>
                </fo:table-body>
              </fo:table>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
        <#--Order Attributes End -->
               
                
            <fo:block id="theEnd"/>  <#-- marks the end of the pages and used to identify page-number at the end -->
        </fo:flow>
    </fo:page-sequence>
   </#list>
 </#if>

</fo:root>
</#escape>
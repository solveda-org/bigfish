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
      <#assign companyName = partyGroup.groupName>
    </#if>
    <#assign logoImageUrl = Static["com.osafe.util.Util"].getProductStoreParm(request, "EMAIL_CLNT_LOGO")/>
    <#assign HTTP_HOST = Static["com.osafe.util.Util"].getProductStoreParm(request, "HTTP_HOST")/>
    
     <#-- Company Address -->
    <#assign companyAddresses = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId, "contactMechPurposeTypeId","GENERAL_LOCATION"))/>
    <#assign selAddresses = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(companyAddresses, nowTimestamp, "fromDate", "thruDate", true)/>
    <#if selAddresses?has_content>
     <#assign companyAddress = delegator.findByPrimaryKey("PostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",selAddresses[0].contactMechId))/>
    </#if>
    
     <#-- Company Phone-->
    <#assign phones = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId,"contactMechPurposeTypeId","PRIMARY_PHONE"))/>
     <#if selPhones?has_content>
        <#assign selPhones = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(phones, nowTimestamp, "fromDate", "thruDate", true)/>
        <#assign companyPhone = delegator.findByPrimaryKey("TelecomNumber", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",selPhones[0].contactMechId))/>
     </#if>
     <#assign faxNumbers = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",payToPartyId,"contactMechPurposeTypeId","FAX_NUMBER"))/>
     <#if faxNumbers?has_content>  
        <#assign faxNumbers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(faxNumbers, nowTimestamp,"fromDate", "thruDate", true)/>
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
      <#assign formattedHomePhone = ''/>
      <#assign formattedWorkPhone = ''/>
      <#assign formattedCellPhone = ''/>
      <#assign partyWorkPhoneExt = ''/>
     <#assign placingParty = orderReadHelper.getPlacingParty()/>
     <#if placingParty?has_content>

         <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", placingParty.partyId, "lastNameFirst","Y", "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
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
                                    <#if logoImageUrl?has_content><fo:external-graphic src="${HTTP_HOST}${logoImageUrl!""}" overflow="hidden" height="40px" content-height="scale-to-fit"/></#if>
                                </fo:block>
                                
                                <fo:block font-size="8pt">
                                <fo:block>${companyName}</fo:block>
                                <#if companyAddress?exists>
                                  ${setRequestAttribute("PostalAddress",companyAddress)}
                                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddressPDF")}
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
        <fo:table-column column-number="1" column-width="proportional-column-width(100)"/>
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

        <#assign shipGroups = delegator.findByAnd("OrderItemShipGroup", {"orderId" : orderId})>
        <#-- order customer info -->
        <fo:table table-layout="fixed" width="100%" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
        <fo:table-column column-number="1" column-width="proportional-column-width(40)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(60)"/>
        <fo:table-body>

            <fo:table-row height="20px">
              <fo:table-cell number-columns-spanned="2">
                <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${uiLabelMap.CustomerInfoHeading}</fo:block>
              </fo:table-cell>
            </fo:table-row>

            <fo:table-row>
              <fo:table-cell>
                <fo:table table-layout="fixed">
                <fo:table-body>
                  
                  <fo:table-row>
                    <fo:table-cell text-align="start" >
                      <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CustomerIdCaption}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="start">
                      <fo:block font-size="8pt" start-indent="10pt"><#if placingParty?has_content>${placingParty.partyId!""}</#if></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  
                  <fo:table-row>
                    <fo:table-cell text-align="start" >
                      <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CustomerNameCaption}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell text-align="start">
                      <fo:block font-size="8pt" start-indent="10pt">${displayPartyNameResult.fullName?default("[${uiLabelMap.PartyNameNotFoundInfo}]")}</fo:block>
                    </fo:table-cell>
                  </fo:table-row>

                 <fo:table-row>
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
                <fo:table table-layout="fixed">
                <fo:table-body>

                  <#assign orderContactMechs = Static["org.ofbiz.party.contact.ContactMechWorker"].getOrderContactMechValueMaps(delegator, orderHeader.get("orderId"))>
                  <#list orderContactMechs as orderContactMechValueMap>
                   <#assign contactMech = orderContactMechValueMap.contactMech>
                   <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
                   <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                    <#if contactMechPurpose.contactMechPurposeTypeId == "BILLING_LOCATION">
                     <fo:table-row>
                      <#assign postalAddress = orderContactMechValueMap.postalAddress>
                      <fo:table-cell text-align="start">
                       <fo:block font-size="8pt" text-align="right" font-weight="bold"> 
                        ${uiLabelMap.BillingAddressCaption}
                       </fo:block>
                      </fo:table-cell>
                      <fo:table-cell>
                       <fo:block font-size="7pt" start-indent="10pt">
                        <#if postalAddress?has_content>
                          ${setRequestAttribute("PostalAddress",postalAddress)}
                          ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddressPDF")}
                        </#if>
                       </fo:block>
                      </fo:table-cell>
                     </fo:table-row>
                    </#if>
                   </#if>
                  </#list>

                 <fo:table-row>
                   <fo:table-cell text-align="start" >
                     <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.ShipMethodCaption}</fo:block>
                   </fo:table-cell>
                       <fo:table-cell>
                         <fo:block font-size="7pt" start-indent="10pt">
                           <#if isStorePickup?has_content && isStorePickup == "Y">
                               ${uiLabelMap.PickupInStoreLabel}
                           <#elseif shipGroups?has_content>
                               <#if shipGroups.size() == 1>
                                   <#assign shipGroup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(shipGroups)/>
                                    <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
                                    <#if shipGroup.carrierPartyId?has_content>
                                        <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
                                        <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)!}</#if>
                                    </#if>
                                    <#if shipmentMethodType?has_content>
                                       ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
                                    </#if>
                               <#else>
                                   ${uiLabelMap.MultipleShipmentsLabel}
                               </#if>
                           <#else>
                               ${uiLabelMap.NoShippingMethodInfo}
                           </#if>
                         </fo:block>
                       </fo:table-cell>
                 </fo:table-row>

                 <#if isStorePickup?has_content && isStorePickup == "Y">
                     <#assign storeContactMechValueMap = storePickupInfo.storeContactMechValueMap! />
                     <#if storeContactMechValueMap?has_content>
                         <#assign postalAddress = storeContactMechValueMap.postalAddress />
                         <#if postalAddress?has_content>
                             <fo:table-row>
                               <fo:table-cell text-align="start" >
                                 <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.StoreAddressCaption}</fo:block>
                               </fo:table-cell>
                                   <fo:table-cell>
                                     <fo:block font-size="7pt" start-indent="10pt">
                                          ${setRequestAttribute("PostalAddress",postalAddress)}
                                          ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddressPDF")}
                                     </fo:block>
                                   </fo:table-cell>
                             </fo:table-row>
                         </#if>
                     </#if>
                 <#else>
                    <#list orderContactMechs as orderContactMechValueMap>
                       <#assign contactMech = orderContactMechValueMap.contactMech>
                       <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
                       <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
                        <#if contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION">
                         <fo:table-row>
                          <#assign postalAddress = orderContactMechValueMap.postalAddress>
                          <fo:table-cell text-align="start">
                           <fo:block font-size="8pt" text-align="right" font-weight="bold"> 
                            ${uiLabelMap.ShippingAddressCaption}
                           </fo:block>
                          </fo:table-cell>
                          <fo:table-cell>
                           <fo:block font-size="7pt" start-indent="10pt">
                            <#if postalAddress?has_content>
                              ${setRequestAttribute("PostalAddress",postalAddress)}
                              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddressPDF")}
                            </#if>
                           </fo:block>
                          </fo:table-cell>
                         </fo:table-row>
                        </#if>
                       </#if>
                    </#list>
                 </#if>

                </fo:table-body>
                </fo:table>
              </fo:table-cell>
            </fo:table-row>

       </fo:table-body>
       </fo:table>

       <#-- end order customer info -->
       <fo:block space-after="0.2in"/>

        <#-- order payment info -->
        <fo:table table-layout="fixed" width="100%" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
        <fo:table-column column-number="1" column-width="proportional-column-width(20)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(40)"/>
        <fo:table-column column-number="3" column-width="proportional-column-width(20)"/>
        <fo:table-column column-number="4" column-width="proportional-column-width(20)"/>
        <fo:table-header font-size="8pt" font-weight="bold" background-color="#F5F5F5">
            <fo:table-row>
                <fo:table-cell number-columns-spanned="4">
                    <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${uiLabelMap.PaymentInfoHeading}</fo:block>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row border-top-style="solid">
                <fo:table-cell>
                    <fo:block >${uiLabelMap.DateLabel}</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block >${uiLabelMap.PaymentTypeLabel}</fo:block>
                </fo:table-cell>
                <fo:table-cell >
                    <fo:block >${uiLabelMap.AmountLabel}</fo:block>
                </fo:table-cell>
                <fo:table-cell >
                    <fo:block >${uiLabelMap.StatusLabel}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body font-size="8pt">

                <#assign currencyUomId = orderReadHelper.getCurrency()>
                <#assign orderValidItems = orderReadHelper.getOrderItems()/>
                <#assign orderAdjustments = orderReadHelper.getAdjustments()>
                <#assign orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments()>
                <#assign otherOrderHeaderAdjustments = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false)/>
                <#assign orderSubTotal = orderReadHelper.getOrderItemsSubTotal(orderValidItems,orderAdjustments)/>
                <#assign otherOrderHeaderAdjustmentsList = Static["javolution.util.FastList"].newInstance()/>
                <#list otherOrderHeaderAdjustments as orderHeaderAdjustment>
                  <#if orderHeaderAdjustment.orderAdjustmentTypeId != "LOYALTY_POINTS" && !orderHeaderAdjustment.productPromoId?has_content>
        		    <#assign changed = otherOrderHeaderAdjustmentsList.add(orderHeaderAdjustment)/>
        		   </#if>
        		</#list>
                <#assign otherAdjustmentsAmount = orderReadHelper.calcOrderAdjustments(otherOrderHeaderAdjustmentsList, orderSubTotal, true, false, false)/>
                <#assign shippingAmount = orderReadHelper.calcOrderAdjustments(orderAdjustments, orderSubTotal, false, false, true)> 
                <#assign taxAmount = orderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal!"0.00"/>
                <#assign grandTotal = orderReadHelper.getOrderGrandTotal(orderValidItems, orderAdjustments)/>
                <#assign orderOpenAmount = grandTotal/>
                <#assign returnQuantityMap = orderReadHelper.getOrderItemReturnedQuantities()>

            <#assign orderPayments = orderReadHelper.getPaymentPreferences()/>
            <#if orderPayments?has_content>
                <#list orderPayments as orderPaymentPreference>
                    <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                    <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
                    <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>
                                ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(orderPaymentPreference.createdDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
                            </fo:block>
                        </fo:table-cell>
                        <#if ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "CREDIT_CARD"))>
                            <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard")>
                            <#assign cardNumber = creditCard.get("cardNumber")>
                            <#assign cardNumber = cardNumber?substring(cardNumber?length - 4)>
                            <#assign cardNumber = "*" + cardNumber>
                            <fo:table-cell>
                                <fo:block>
                                    ${paymentMethodType.description?if_exists} (${creditCard.get("cardType")?if_exists}) ${cardNumber} ${creditCard.get("expireDate")?if_exists}
                                </fo:block>
                            </fo:table-cell>
                        <#elseif ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "GIFT_CARD"))>
                            <#assign giftCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("GiftCard")>
                            <fo:table-cell>
                                <fo:block>
                                    ${paymentMethodType.description?if_exists} (${giftCard.cardNumber})
                                </fo:block>
                            </fo:table-cell>
                        <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD") && isStorePickup?has_content && isStorePickup == "Y")>
                            <fo:table-cell>
                                <fo:block>
                                    ${uiLabelMap.PayInStoreInfo}
                                </fo:block>
                            </fo:table-cell>
                        <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD"))>
                            <fo:table-cell>
                                <fo:block>
                                    ${uiLabelMap.CashOnDeliveryInfo}
                                </fo:block>
                            </fo:table-cell>
                        <#else>
                            <fo:table-cell>
                                <fo:block>
                                    ${paymentMethodType.description?if_exists}
                                </fo:block>
                            </fo:table-cell>
                        </#if>
                        <fo:table-cell>
                            <fo:block>
                                <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                ${oppStatusItem.get("description","OSafeAdminUiLabels",locale)}
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <#if orderPaymentPreference.statusId == "PAYMENT_SETTLED">
                      <#assign orderOpenAmount = orderOpenAmount - orderPaymentPreference.maxAmount/>
                    </#if>

                    <#assign payments = orderPaymentPreference.getRelated("Payment")?if_exists>
                    <#if payments?has_content>
                        <#list payments as payment>
                            <#assign paymentStatusItem = payment.getRelatedOne("StatusItem")>
                            <#assign paymentType = payment.getRelatedOne("PaymentType")>
                            <#if orderPaymentPreference.statusId == "PAYMENT_NOT_RECEIVED">
	                            <#if payment.statusId == "PMNT_RECEIVED">
	                                <#assign orderOpenAmount = orderOpenAmount - payment.amount/>
	                            </#if>
                            </#if>
                            <fo:table-row>
                                <fo:table-cell>
                                    <fo:block>
                                        ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(payment.effectiveDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        ${paymentType.description?if_exists} (${paymentMethodType.description?if_exists})
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        <@ofbizCurrency amount=payment.amount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>
                                        ${paymentStatusItem.get("description","OSafeAdminUiLabels",locale)}
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </#list>
                    </#if>
                </#list>
            </#if>
            
            <#if orderOpenAmount?has_content && orderOpenAmount &gt; 0 && isStorePickup?has_content && isStorePickup == "Y">
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            ${uiLabelMap.BalanceDueLabel}
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block font-weight="bold">
                            <@ofbizCurrency amount=orderOpenAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </#if>

       </fo:table-body>
       </fo:table>

       <#-- end order payment info -->
       <fo:block space-after="0.2in"/>

        <#-- order shipping info -->
        <fo:table table-layout="fixed" width="100%" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
        <fo:table-column column-number="1" column-width="proportional-column-width(20)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(40)"/>
        <fo:table-column column-number="3" column-width="proportional-column-width(20)"/>
        <fo:table-column column-number="4" column-width="proportional-column-width(20)"/>
        <fo:table-header font-size="8pt" font-weight="bold" background-color="#F5F5F5">
            <fo:table-row>
                <fo:table-cell number-columns-spanned="4">
                    <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${uiLabelMap.ShippingInfoHeading}</fo:block>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row border-top-style="solid">
                <fo:table-cell>
                    <fo:block >${uiLabelMap.ShipGroupLabel}</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block >${uiLabelMap.ShipDateLabel}</fo:block>
                </fo:table-cell>
                <fo:table-cell >
                    <fo:block >${uiLabelMap.ShipMethodLabel}</fo:block>
                </fo:table-cell>
                <fo:table-cell >
                    <fo:block >${uiLabelMap.ShipTrackingNumberLabel}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body font-size="8pt">
            <#if shipGroups?has_content>
                <#list shipGroups as shipGroup>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>
                                ${shipGroup.shipGroupSeqId!""}
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(shipGroup.estimatedShipDate, preferredDateFormat).toLowerCase())!"N/A"}
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
                                <#if shipGroup.carrierPartyId?has_content>
                                    <#assign carrier =  delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", shipGroup.carrierPartyId))?if_exists />
                                    <#if carrier?has_content>${carrier.groupName?default(carrier.partyId)!}</#if>
                                </#if>
                                <#if shipmentMethodType?has_content>
                                   ${shipmentMethodType.get("description","OSafeAdminUiLabels",locale)?default("")}
                                </#if>
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block>
                                ${shipGroup.trackingNumber!}
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </#list>
            </#if>

       </fo:table-body>
       </fo:table>

       <#-- end order shipping info -->
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
            <fo:table-column column-width=".6in"/>
            <fo:table-column column-width="1.05in"/>
            <fo:table-column column-width="1.30in"/>
            <fo:table-column column-width=".63in"/>
            <fo:table-column column-width=".3in"/>
            <fo:table-column column-width=".4in"/>
            <fo:table-column column-width=".4in"/>
            <fo:table-column column-width=".6in"/>
            <fo:table-column column-width=".5in"/>
            <fo:table-column column-width=".5in"/>
            <fo:table-column column-width=".6in"/>
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
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.ReturnQtyLabel}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell >
                        <fo:block >${uiLabelMap.CancelQtyLabel}</fo:block>
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
                        <fo:block text-align="right" margin-right="0.1in">${uiLabelMap.ItemTotalLabel}</fo:block>
                    </fo:table-cell>
                    
                    
                </fo:table-row>
            </fo:table-header>
            
            
            
             <fo:table-body font-size="8pt">

                <#list orderItems as orderItem>
                    <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                    <#assign productId = orderItem.productId?if_exists>
                    <#assign itemProduct = orderItem.getRelatedOne("Product")/>
                    <#assign itemStatus = orderItem.getRelatedOne("StatusItem")/>
                    <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                    <#assign returnedQty = returnQuantityMap.get(orderItem.orderItemSeqId)?default(0) />
                    <#assign itemAdjustment = orderReadHelper.getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
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
                            <fo:block>${orderItem.quantity?default(0)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block>${returnedQty}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block>${orderItem.cancelQuantity?default(0)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block><@ofbizCurrency amount=orderItem.unitPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block><#if (itemPromoAdjustment < 0)><@ofbizCurrency amount=offerPrice rounding=globalContext.currencyRounding isoCode=currencyUomId/></#if></fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                            <fo:block><@ofbizCurrency amount=itemAdjustment rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right" margin-right="0.1in">
                                    <@ofbizCurrency amount=orderReadHelper.getOrderItemSubTotal(orderItem, orderAdjustments) rounding=globalContext.currencyRounding isoCode=currencyUomId/>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
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
                        <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=orderSubTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  <#list otherOrderHeaderAdjustments as orderHeaderAdjustment>
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
                         <fo:table-row>
                            <fo:table-cell number-columns-spanned="2">
                                <fo:block text-align="right"><#if promoText?has_content>${promoText}<#if promoCodeText?has_content> (${promoCodeText})</#if><#else>${adjustmentType.get("description",locale)?if_exists}</#if>:</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment) rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                            </fo:table-cell>
                         </fo:table-row>
                      </#if>
                  </#list>
                  <#list otherOrderHeaderAdjustments as orderHeaderAdjustment>
		            <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
		            <#assign loyaltyOrderAdjustmentTypeId = adjustmentType.orderAdjustmentTypeId/>
                    <#if loyaltyOrderAdjustmentTypeId == "LOYALTY_POINTS">
	                  <#assign orderAdjTotal = orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)!0/>
	                  <fo:table-row>
                        <fo:table-cell number-columns-spanned="2">
                            <fo:block text-align="right">${adjustmentType.get("description",locale)?if_exists}:</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=orderAdjTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
		            </#if>
		          </#list>
                  <fo:table-row>
                    <fo:table-cell></fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="right">${uiLabelMap.ShipHandleCaption}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=shippingAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  
                  <#if taxDisplayMap?has_content>
                  	<#assign taxDisplayDetailMap = taxDisplayMap.get(orderId) />
                  	<#if taxDisplayDetailMap?has_content>
                  		<#assign appliedTaxList = taxDisplayDetailMap.appliedTaxList />
                  		<#assign totalTaxPercent = taxDisplayDetailMap.totalTaxPercent />
                  		<#assign shipGroupSalesTaxSame = taxDisplayDetailMap.shipGroupSalesTaxSame />
                  	</#if>
                  </#if>
                  
                  <#if (!checkoutSuppressTaxIfZero) || (taxAmount?has_content && (taxAmount &gt; 0))>
                    <#if !checkoutShowSalesTaxMulti>
                      <fo:table-row>
                         <fo:table-cell number-columns-spanned="2">
                         	 <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
					         <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
                             <fo:block text-align="right"><#if shipGroupSalesTaxSame>${salesTaxCaption!}<#else>${uiLabelMap.SummarySalesTaxShortCaption!}</#if></fo:block>
                         </fo:table-cell>
                         <fo:table-cell>
                             <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=taxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                         </fo:table-cell>
                      </fo:table-row>
                    <#else>
                      <#if appliedTaxList?exists && appliedTaxList?has_content && shipGroupSalesTaxSame>
				        <#list appliedTaxList as appliedTax >
				          <#assign sourcePercentage = appliedTax.sourcePercentage />
                          <fo:table-row>
			                <fo:table-cell number-columns-spanned="2">
			                  <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", appliedTax.sourcePercentage, "description", appliedTax.description)>
						      <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxMultiCaption",taxInfoStringMap, locale ) />
			                  <fo:block text-align="right">${salesTaxCaption}</fo:block>
			                </fo:table-cell>
			                <fo:table-cell>
			                  <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=appliedTax.amount rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
			                </fo:table-cell>
			              </fo:table-row>
				        </#list>
				      <#else>
                        <fo:table-row>
                          <fo:table-cell number-columns-spanned="2">
                            <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
					        <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
                            <fo:block text-align="right"><#if shipGroupSalesTaxSame>${salesTaxCaption!}<#else>${uiLabelMap.SummarySalesTaxShortCaption!}</#if></fo:block>
                          </fo:table-cell>
                          <fo:table-cell>
                            <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=taxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                          </fo:table-cell>
                        </fo:table-row>
				      </#if>
                    </#if>
                  </#if>
                  
                    <fo:table-row>
                          <fo:table-cell number-columns-spanned="2">
                              <fo:block text-align="right">${uiLabelMap.AdjustmentsCaption}</fo:block>
                          </fo:table-cell>
                          <fo:table-cell>
                              <fo:block text-align="right" margin-right="0.1in"><@ofbizCurrency amount=otherAdjustmentsAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
                          </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell>
                            <fo:block font-weight="bold" text-align="right">${uiLabelMap.OrderTotalCaption}</fo:block>
                         </fo:table-cell>
                         <fo:table-cell>
                            <fo:block font-weight="bold" text-align="right" margin-right="0.1in"><@ofbizCurrency amount=grandTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></fo:block>
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
                        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
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
                        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
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
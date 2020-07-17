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

  <#if customerList?has_content>
   <#list customerList as partyrow>
    <#assign partyId=partyrow.get("partyId")>
    <#assign party =   delegator.findByPrimaryKey("Party",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",partyId))/>
    <#assign partyName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator,partyId, true)!"">
    <#assign partyRoles = delegator.findByAnd("PartyRole", {"partyId", partyId})>
     <#if partyRoles?has_content>
      <#list partyRoles as partyRole>
         <#assign roleType = partyRole.getRelatedOne("RoleType")>
         <#if roleType.roleTypeId=="GUEST_CUSTOMER">
           <#assign partyRoleType = roleType.description />
           <#break>
         </#if>
         <#if roleType.roleTypeId=="CUSTOMER" || roleType.roleTypeId=="EMAIL_SUBSCRIBER">
               <#assign partyRoleType = roleType.description>
         </#if>
      </#list>
     <#else>
              <#assign partyRoleType = "">
     </#if>
    <#assign userLogins = party.getRelated("UserLogin")>
    <#if userLogins?has_content>
         <#assign userLoginId = userLogins.get(0).userLoginId>
    </#if>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "IS_DOWNLOADED"}, true)!"" />
    <#if partyAttribute?has_content>
      <#assign downloadStatus = partyAttribute.attrValue!"">
    </#if>
    <#assign partyContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator, partyId, false)/>
    <#if partyContactMechValueMaps?has_content>
       <#list partyContactMechValueMaps as partyContactMechValueMap>
          <#assign contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes/>
          <#list contactMechPurposes as contactMechPurpose>
            <#if contactMechPurpose.contactMechPurposeTypeId =="PRIMARY_EMAIL">
                <#assign partyPrimaryEmailContactMechValueMap = partyContactMechValueMap/>           
                <#assign partyPrimaryEmailContactMech = partyPrimaryEmailContactMechValueMap.contactMech>
                <#assign primaryEmailAddress = partyPrimaryEmailContactMech.infoString/>
                <#assign partyContactMech = partyPrimaryEmailContactMechValueMap.partyContactMech?if_exists />
                <#assign allowSolicitation = partyContactMech.allowSolicitation?if_exists/>
            </#if>
          </#list>
       </#list>
    </#if>
    <#assign shippingContactMechList = Static["org.ofbiz.party.contact.ContactHelper"].getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false)/>
    <#assign typeId = partyrow.get("partyTypeId")>
    <#if typeId?has_content && typeId=="PERSON">
      <#assign currentStatus=partyrow.get("statusId")>
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
        <#assign country = companyAddress.getRelatedOneCache("CountryGeo")/>
        <#if country?has_content>
          <#assign countryName = country.get("geoName", locale)/>
        </#if>
        <#assign stateProvince = companyAddress.getRelatedOneCache("StateProvinceGeo")/>
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
        <#assign companyFax = delegator.findOne("TelecomNumber", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",faxNumbers[0].contactMechId), false)/>;
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
      <#-- Customer Phone -->
      <#assign partyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyId))/>
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
        <#assign formattedCellPhone = Static["com.osafe.util.OsafeAdminUtil"].formatTelephone(partyCellPhoneDetail.areaCode?if_exists, partyCellPhoneDetail.contactNumber?if_exists)/>
      </#if>
        
    <fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">

    <fo:title>
      ${partyId}
    </fo:title>
    
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
                      <fo:table-cell text-align="end"><fo:block font-weight="bold">${uiLabelMap.CustomerNoCaption} </fo:block></fo:table-cell>
                      <fo:table-cell><fo:block start-indent="10pt">${partyId}</fo:block></fo:table-cell>
                    </fo:table-row>

                    <fo:table-row>
                      <fo:table-cell text-align="end"><fo:block font-weight="bold">${uiLabelMap.CustomerNameCaption}</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block start-indent="10pt">${partyName!""}</fo:block></fo:table-cell>
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
                ${uiLabelMap.PageLabel} <fo:page-number/> ${uiLabelMap.OfLabel} ${customerList.size()}
            </fo:block>
        </fo:static-content>
        
        <#-- the body -->
        <fo:flow flow-name="xsl-region-body">
        <fo:table table-layout="fixed" width="100%">
          <fo:table-body>
            <fo:table-row>
              <fo:table-cell>
	                <fo:table table-layout="fixed" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
		                <fo:table-body>
			                 <fo:table-row height="20px">
				                  <fo:table-cell number-columns-spanned="4">
				                    <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${uiLabelMap.CustomerInfoHeading}</fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 <fo:table-row height="20px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CustomerNoCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${partyId}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CustomerStatusCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">
									        <#if party?has_content>
									           <#assign statusItem = party.getString("statusId")>
									           <#if statusItem?has_content && statusItem=="PARTY_ENABLED">
									              ${uiLabelMap.CustomerEnabledInfo}
									           <#else>
									              ${uiLabelMap.CustomerDisabledInfo}
									           </#if>
									        </#if>
				                        </fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 <fo:table-row height="20px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CustomerNameCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${partyName!""}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CustomerRoleCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${partyRoleType!""}</fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 <fo:table-row height="20px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.UserLoginCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">
   								         <#if userLoginId?has_content>
									           ${userLoginId}
								        <#else>
									           ${uiLabelMap.NoUserLoginIdInfo}
								        </#if>
				                        </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.ExportStatusCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">
									     <#if downloadStatus?has_content>
									               ${uiLabelMap.ExportStatusInfo}
 							             <#else>
									               ${uiLabelMap.DownloadNewInfo}
									     </#if>
				                        </fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 <fo:table-row height="20px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.EmailAddressCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${primaryEmailAddress!""}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.OptInCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">
								            <#if allowSolicitation?has_content && allowSolicitation=='N'>
								               ${uiLabelMap.NoInfo}
								            <#else>
								               ${uiLabelMap.YesInfo}
								            </#if>
				                        </fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 
			                 <#if formattedHomePhone?has_content || formattedWorkPhone?has_content>
			                 <fo:table-row height="20px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold"><#if formattedHomePhone?has_content>${uiLabelMap.HomePhoneCaption}</#if></fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                    <fo:block font-size="8pt" start-indent="10pt">
									  ${formattedHomePhone!}
				                    </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold"><#if formattedWorkPhone?has_content>${uiLabelMap.WorkPhoneCaption}</#if></fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                    <fo:block font-size="8pt" start-indent="10pt">
									  ${formattedWorkPhone!}<#if partyWorkPhoneExt?has_content> x${partyWorkPhoneExt!}</#if>
				                    </fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 </#if>
			                 
			                 <#if formattedCellPhone?has_content>
			                 <fo:table-row height="20px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.CellPhoneCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                    <fo:block font-size="8pt" start-indent="10pt">
									  ${formattedCellPhone!}
				                    </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start" >
				                    <fo:block font-size="8pt" text-align="right" font-weight="bold"></fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                    <fo:block font-size="8pt" start-indent="10pt">
				                    </fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			               </#if>
		                </fo:table-body>
	                 </fo:table>
              </fo:table-cell>
             </fo:table-row>
             </fo:table-body>
           </fo:table>
        <fo:block space-after="0.2in"/>
        <#if shippingContactMechList?has_content>
          <#assign idx=0>
          <#assign rowIdx=0>
		        <fo:table table-layout="fixed" width="100%">
		        <fo:table-column column-number="1" column-width="proportional-column-width(68)"/>
		        <fo:table-column column-number="2" column-width="proportional-column-width(25)"/>
		        <fo:table-column column-number="3" column-width="proportional-column-width(68)"/>
		        <fo:table-column column-number="4" column-width="proportional-column-width(0)"/>
		          <fo:table-body>
		            <fo:table-row>
          <#list shippingContactMechList as shippingContactMech>
              <#if rowIdx ==2>
	                 <fo:table-row height="10px">
		                  <fo:table-cell text-align="start" number-columns-spanned="4">
		                  </fo:table-cell>
	                 </fo:table-row>
		            <fo:table-row>
                   <#assign rowIdx=0>
              </#if>
             <#assign rowIdx=rowIdx + 1>
             <#assign idx=idx + 1>
             <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
		              <fo:table-cell>
		                <fo:table table-layout="fixed" border-end-style="solid" border-bottom-style="solid" border-start-style="solid" border-top-style="solid">
		                <fo:table-body>
			                 <fo:table-row height="20px">
				                  <fo:table-cell number-columns-spanned="2">
				                    <fo:block font-weight="bold" font-size="10pt" text-align="center" background-color="#EEEEEE">${shippingAddress.attnName!shippingAddress.address1}</fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 <fo:table-row height="10px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold">${uiLabelMap.AddressCaption}</fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">
							                 <#if shippingAddress.toName?has_content>${shippingAddress.toName}</#if>
				                        </fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 <fo:table-row height="10px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold"> </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${shippingAddress.address1}</fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
        	                 <#if shippingAddress.address2?has_content>
			                 <fo:table-row height="10px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold"> </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${shippingAddress.address2}</fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 </#if>
        	                 <#if shippingAddress.address3?has_content>
			                 <fo:table-row height="10px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold"> </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${shippingAddress.address3}</fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 </#if>
			                 <fo:table-row height="10px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold"> </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">
	                                     <#if shippingAddress.city?has_content && shippingAddress.city != '_NA_'>${shippingAddress.city}</#if><#if shippingAddress.stateProvinceGeoId?has_content && shippingAddress.stateProvinceGeoId != '_NA_'>, ${shippingAddress.stateProvinceGeoId}</#if>
	                                     <#if shippingAddress.postalCode?has_content && shippingAddress.postalCode != '_NA_' > ${shippingAddress.postalCode}</#if>
				                        </fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
        	                 <#if shippingAddress.countryGeoId?has_content>
			                 <fo:table-row height="10px">
				                  <fo:table-cell text-align="start" >
				                        <fo:block font-size="8pt" text-align="right" font-weight="bold"> </fo:block>
				                  </fo:table-cell>
				                  <fo:table-cell text-align="start">
				                        <fo:block font-size="8pt" start-indent="10pt">${shippingAddress.countryGeoId}</fo:block>
				                  </fo:table-cell>
			                 </fo:table-row>
			                 </#if>
						    
		                </fo:table-body>
		               </fo:table>
		              </fo:table-cell>
		              <fo:table-cell>
		              </fo:table-cell>
				    <#if (rowIdx == 2) || (idx == shippingContactMechList.size())>
   		                </fo:table-row>
		            </#if>
         </#list>
		          </fo:table-body>
		        </fo:table>
       </#if>
<#-- Customer Notes Start-->
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
                  <#assign noteList = delegator.findByAnd("PartyNoteView", {"targetPartyId" : partyId!})/>
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
                          <#assign noteTime=noteDateTime[1]!"" />
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
        <#--Customer Notes End -->
        
    <fo:block space-after="0.2in"/>
            <fo:block id="theEnd"/>  <#-- marks the end of the pages and used to identify page-number at the end -->
        </fo:flow>
    </fo:page-sequence>
    </#if>
   </#list>
 </#if>

</fo:root>
</#escape>
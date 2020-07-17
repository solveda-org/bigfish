<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.CustNoLabel}</th>
                <th class="nameCol">${uiLabelMap.LastNameLabel}</th>
                <th class="nameCol">${uiLabelMap.FirstNameLabel}</th>
                <th class="descCol">${uiLabelMap.UserLoginLabel}</th>
                <th class="addrCol">${uiLabelMap.AddressLabel}</th>
                <th class="typeCol">${uiLabelMap.CustomerRoleLabel}</th>
                <th class="statusCol">${uiLabelMap.CustomerStatusLabel}</th>
                <th class="statusCol lastCol">${uiLabelMap.ExportStatusLabel}</th>
                <th class="actionCol">&nbsp;</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as partyRow>
                  <#assign hasNext = partyRow_has_next/>
                  <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                  	<#assign userLoginId = "">
                    <td class="idCol <#if !partyRow_has_next>lastRow</#if> firstCol" ><a href="<@ofbizUrl>${detailAction!}?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
                   <#assign person = delegator.findByPrimaryKey("Person", {"partyId", partyRow.partyId})/>
                   <#assign party = delegator.findByPrimaryKey("Party", {"partyId", partyRow.partyId})/>
                    <#assign downloadStatus = "">
                    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyRow.partyId, "attrName" : "IS_DOWNLOADED"}, false)!"" />
                    <#if partyAttribute?has_content>
                      <#assign downloadStatus = partyAttribute.attrValue!"">
                    </#if>
                    <td class="nameCol <#if !partyRow_has_next>lastRow</#if>"><#if person?has_content>${person.lastName!""}</#if></td>
                    <td class="nameCol <#if !partyRow_has_next>lastRow</#if>"><#if person?has_content>${person.firstName!""}</#if></td>
                    <#assign userLogins = partyRow.getRelated("UserLogin")>
                    <#if userLogins?has_content>
                      <#list userLogins as userLogin>
                          <#if userLogin.enabled?has_content && userLogin.enabled == 'Y'>
                              <#assign userLoginId = userLogin.userLoginId!"">
                              <#break>
                          </#if>
                      </#list>
                      <#if !userLoginId?has_content>
                          <#assign userLoginId = userLogins.get(0).userLoginId>
                      </#if>
                    <#else>
                      <#assign userLoginId = "">
                    </#if>
                    
                    <td class="descCol <#if !partyRow_has_next>lastRow</#if>">${userLoginId?if_exists}</td>
                    <#assign primaryEmail="">
                    <#assign partyContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator, partyRow.partyId, false)!""/>
                    <#if partyContactMechValueMaps?has_content>
	                    <#list partyContactMechValueMaps as partyContactMechValueMap>
	                        <#assign contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes>
	                        <#list contactMechPurposes as contactMechPurpose>
                                <#if contactMechPurpose.contactMechPurposeTypeId=="PRIMARY_EMAIL">
                                  <#assign partyPrimaryEmailContactMech = partyContactMechValueMap.contactMech>
                                  <#assign primaryEmail=partyPrimaryEmailContactMech.infoString!"">
                                </#if>
	                        </#list>
	                    </#list>
                    </#if>
                    <#assign postalAddress = ""/>
                    <#assign contactMechs = Static["org.ofbiz.party.contact.ContactHelper"].getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false)!""/>                    
                    <#assign contactMech = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(contactMechs)!""/>
                    <#if contactMech?has_content>
                       <#assign postalAddress=contactMech.getRelatedOne("PostalAddress")!"">
                    </#if>
                    <td class="addrCol <#if !partyRow_has_next>lastRow</#if>">
                    <#if postalAddress?has_content>
                          ${setRequestAttribute("PostalAddress",postalAddress)}
                          ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_STREET_CITY_STATE")}
                          ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
                    </#if>
                    </td>
                     
                    <#assign partyRoles = delegator.findByAnd("PartyRole", {"partyId", partyRow.partyId}) />
                    <#assign partyRoleTypeIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(partyRoles, "roleTypeId", true) />
                    
                    <#if partyRoles?has_content>
                        <#list partyRoles as partyRole>
                            <#if partyRole.roleTypeId == "GUEST_CUSTOMER">
                                <#if roleTypeIds?has_content && roleTypeIds.contains("GUEST_CUSTOMER")>
                                    <#assign partyRoleType = roleTypesDescMap.get("GUEST_CUSTOMER")! />
                                    <#break>
                                </#if>
                            <#else>
                                <#if (roleTypeIds?has_content && roleTypeIds.contains(partyRole.roleTypeId))>
                                    <#assign partyRoleType = roleTypesDescMap.get(partyRole.roleTypeId)! />
                                    <#break>
                                <#elseif !roleTypeIds?has_content>
                                    <#if partyRoleTypeIds?has_content && partyRoleTypeIds.contains("CUSTOMER")>
                                        <#assign partyRoleType = roleTypesDescMap.get("CUSTOMER")! />
                                    <#else>
                                        <#assign partyRoleType = roleTypesDescMap.get(partyRole.roleTypeId)! />
                                    </#if>
                                    <#break>
                                </#if>
                            </#if>
                        </#list>
                    <#else>
                        <#assign partyRoleType = "">
                    </#if> 
		              
                    <td class="typeCol <#if !partyRow_has_next>lastRow</#if>">${partyRoleType?if_exists}</td>
                    <td class="statusCol <#if !partyRow_has_next>lastRow</#if>">
                        <#if (partyRow.statusId?has_content && partyRow.statusId=="PARTY_ENABLED")>
                            ${uiLabelMap.CustomerEnabledInfo}
                        <#else>
                            ${uiLabelMap.CustomerDisabledInfo}
                        </#if>
                    </td>
                    <td class="statusCol <#if !partyRow_has_next>lastRow</#if> lastCol">
                        <#if (downloadStatus?has_content && (downloadStatus == "Y"))>
                            ${uiLabelMap.ExportStatusInfo}
                        <#else>
                            ${uiLabelMap.DownloadNewInfo}
                        </#if>
                    </td>
                    <td class="actionCol <#if !hasNext?if_exists>lastRow</#if> <#if !hasNext?if_exists>bottomActionIconRow</#if>">
                        <div class="actionIconMenu">
                            <a class="toolIcon" href="javascript:void(o);"></a>
                            <div class="actionIconBox" style="display:none">
                                <div class="actionIcon">
                                    <ul>
                                       <li><a href="<@ofbizUrl>${detailAction!}?partyId=${partyRow.partyId}</@ofbizUrl>"><span class="editIcon"></span>${uiLabelMap.EditCustomerTooltip}</a></li>
                                       <#assign partyOrders = party.getRelated("OrderRole",  {"roleTypeId" : "PLACING_CUSTOMER"}, [])?if_exists/>
                                       <li>
                                           <#if (partyOrders?has_content)>
                                               <a href="<@ofbizUrl>searchOrders?partyId=${partyRow.partyId}&preRetrieved=Y</@ofbizUrl>"><span class="orderIcon"></span>${uiLabelMap.CustomerOrderTooltip} [${partyOrders.size()!}]</a>
                                           <#else>
                                               <p><span class="orderIcon"></span> ${uiLabelMap.CustomerOrderTooltip} [0]</p>
                                           </#if>
                                       </li>
                                       <#assign partyNotes = party.getRelated("PartyNote")?if_exists/>
                                       <li>
                                           <#if (partyNotes?has_content)>
                                               <a href="<@ofbizUrl>customerNoteList?partyId=${partyRow.partyId}</@ofbizUrl>"><span class="noteIcon"></span>${uiLabelMap.CustomerNoteTooltip} [${partyNotes.size()!}]</a>
                                           <#else>
                                               <a href="<@ofbizUrl>customerNoteList?partyId=${partyRow.partyId}</@ofbizUrl>"><span class="noteIcon"></span>${uiLabelMap.CustomerNoteTooltip} [0]</a>
                                           </#if>
                                       </li>
                                       <#assign partyContactUs = delegator.findByAnd("CustRequest",  {"fromPartyId" : partyRow.partyId, "custRequestTypeId" : "RF_CONTACT_US"})?if_exists/>
                                       <li>
                                           <#if (partyContactUs?has_content)>
                                               <a href="<@ofbizUrl>custRequestContactUsSearch?partyId=${partyRow.partyId}&preRetrieved=Y</@ofbizUrl>"><span class="contactUsIcon"></span>${uiLabelMap.CustomerContactUsTooltip} [${partyContactUs.size()!}]</a>
                                           <#else>
                                               <p><span class="contactUsIcon"></span> ${uiLabelMap.CustomerContactUsTooltip} [0]</p>
                                           </#if>
                                       </li>
                                       <#assign partyCatalogReqs = delegator.findByAnd("CustRequest",  {"fromPartyId" : partyRow.partyId, "custRequestTypeId" : "RF_CATALOG"})?if_exists/>
                                       <li>
                                           <#if (partyCatalogReqs?has_content)>
                                               <a href="<@ofbizUrl>custRequestCatalogSearch?partyId=${partyRow.partyId}&preRetrieved=Y</@ofbizUrl>"><span class="catalogRequestIcon"></span>${uiLabelMap.CustomerCatalogRequestTooltip} [${partyCatalogReqs.size()!}]</a>
                                           <#else>
                                               <p><span class="catalogRequestIcon"></span> ${uiLabelMap.CustomerCatalogRequestTooltip} [0]</p>
                                           </#if>
                                       </li>
                                       <li><a href="<@ofbizUrl>customerActivityDetail?partyId=${partyRow.partyId}</@ofbizUrl>"><span class="customerActivityIcon"></span>${uiLabelMap.CustomerWebsiteActivityTooltip}</a></li>
                                       <li><a href="<@ofbizUrl>${ExportToPdfAction!}?partyId=${partyRow.partyId}</@ofbizUrl>"><span class="exportToPdfIcon"></span>${uiLabelMap.ExportToPdfTooltip}</a></li>
                                       <li><a href="<@ofbizUrl>${ExportToXMLAction!}?partyId=${partyRow.partyId}</@ofbizUrl>"><span class="exportToXmlIcon"></span>${uiLabelMap.ExportToXmlTooltip}</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </td>
                  </tr>
                  <#-- toggle the row color -->
                  <#if rowClass == "2">
                    <#assign rowClass = "1">
                  <#else>
                    <#assign rowClass = "2">
                  </#if>
            </#list>
        </#if>
<!-- end listBox -->
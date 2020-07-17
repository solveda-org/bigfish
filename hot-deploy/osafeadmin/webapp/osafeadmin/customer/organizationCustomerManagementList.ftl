<!-- start listBox -->
<table class="osafe" id="organizationCustomers">
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
           <#assign rowClass = "1">
           <#assign rowNo = 1/>
           <#assign minRowNo = 1/>
          <input type="hidden" name="rowNo" id="rowNo"/>
          <input type="hidden" name="organizationCustomerPartyId" id="organizationCustomerPartyId"/>
          <input type="hidden" name="organizationCustomerPartyTypeId" id="organizationCustomerPartyTypeId" onchange="addOrganizationCustomerRow('organizationCustomers');"/>
          <#if resultList?has_content>
		      <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!resultList?size}"/>
		  <#else>
		      <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!}"/>
		  </#if>
        <#if resultList?exists && resultList?has_content  && !parameters.totalRows?exists>
            <#list resultList as partyRow>
                  <#assign hasNext = partyRow_has_next/>
                  <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                  	<#assign userLoginId = "">
                    <td class="idCol <#if !partyRow_has_next>lastRow</#if> firstCol" ><a href="<@ofbizUrl>${detailAction!}?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
                    <input type="hidden" name="organizationEmployeeId_${rowNo}" id="organizationEmployeeId" value="${partyRow.partyId!}"/>
                    
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
			                        <#assign partyRoleTypeId = "GUEST_CUSTOMER" />
			                        <#break>
			                    </#if>
			                <#else> 
			                    <#if (roleTypeIds?has_content && roleTypeIds.contains(partyRole.roleTypeId))>
			                        <#assign partyRoleTypeId = partyRole.roleTypeId! />
			                        <#break>
			                    <#else>
			                        <#if partyRoleTypeIds?has_content && partyRoleTypeIds.contains("CUSTOMER")>
			                            <#assign partyRoleTypeId = "CUSTOMER" />
			                        <#else>
			                            <#assign partyRoleTypeId = partyRole.roleTypeId! />
			                        </#if>
			                        <#break>
			                    </#if>
			                </#if>
			            </#list>
			        <#else>
			            <#assign partyRoleTypeId = "">
			        </#if> 
			        <td class="typeCol">
			            <#if partyRoleTypeId?has_content>
			                <#assign roleType = delegator.findByPrimaryKey("RoleType", {"roleTypeId", partyRoleTypeId})/>
			            </#if>
			            <#if roleType?has_content>
			                ${roleType.description?if_exists}
			            </#if>
			        </td>
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
                    <td class="actionCol">
                        <a href="javascript:setRowNo('${rowNo}');javascript:deleteOrganizationCustomerRow('${partyRow.partyId}','${person.firstName} ${person.lastName}', '', '');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteOrganizationCustomerTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
                        <a href="<@ofbizUrl>${detailAction!}?partyId=${partyRow.partyId}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${uiLabelMap.EditCustomerTooltip}');" onMouseout="hideTooltip()"><span class="editIcon"></span></a>
                    </td>
                  </tr>
                  <#-- toggle the row color -->
                  <#if rowClass == "2">
                    <#assign rowClass = "1">
                  <#else>
                    <#assign rowClass = "2">
                  </#if>
                  <#assign rowNo = rowNo+1/>
            </#list>
        <#elseif parameters.totalRows?exists>
            <#assign minRow = parameters.totalRows?number/>
            <#if (minRow?exists && minRow &gt; 0) >
                <#list 1..minRow as x>
                    <#assign organizationEmployeeId = request.getParameter("organizationEmployeeId_${x}")!/>
                    <input type="hidden" name="organizationEmployeeId_${x}" id="organizationEmployeeId" value="${organizationEmployeeId!}"/>
                    <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
                        <#assign userLoginId = "">
				        <td class="idCol firstCol" ><a href="<@ofbizUrl>${detailAction!}?partyId=${organizationEmployeeId}</@ofbizUrl>">${organizationEmployeeId}</a></td>
				        <#assign person = delegator.findByPrimaryKey("Person", {"partyId", organizationEmployeeId})/>
				        <#assign party = delegator.findByPrimaryKey("Party", {"partyId", organizationEmployeeId})/>
				        <#assign downloadStatus = "">
				        <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : organizationEmployeeId, "attrName" : "IS_DOWNLOADED"}, false)!"" />
				        <#if partyAttribute?has_content>
				            <#assign downloadStatus = partyAttribute.attrValue!"">
				        </#if>
				        <td class="nameCol"><#if person?has_content>${person.lastName!""}</#if></td>
				        <td class="nameCol"><#if person?has_content>${person.firstName!""}</#if></td>
				        <#assign userLogins = party.getRelated("UserLogin")>
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
				                
				        <td class="descCol">${userLoginId?if_exists}</td>
				        <#assign primaryEmail="">
				        <#assign partyContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator, organizationEmployeeId, false)!""/>
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
				        <td class="addrCol">
				            <#if postalAddress?has_content>
				                ${setRequestAttribute("PostalAddress",postalAddress)}
				                ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_STREET_CITY_STATE")}
				                ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
				            </#if>
				        </td>
				                 
				        <#assign partyRoles = delegator.findByAnd("PartyRole", {"partyId", organizationEmployeeId}) />
				        <#assign partyRoleTypeIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(partyRoles, "roleTypeId", true) />
				                
				        <#if partyRoles?has_content>
				            <#list partyRoles as partyRole>
				                <#if partyRole.roleTypeId == "GUEST_CUSTOMER">
				                    <#if roleTypeIds?has_content && roleTypeIds.contains("GUEST_CUSTOMER")>
				                        <#assign partyRoleTypeId = "GUEST_CUSTOMER" />
				                        <#break>
				                    </#if>
				                <#else>
				                    <#if (roleTypeIds?has_content && roleTypeIds.contains(partyRole.roleTypeId))>
				                        <#assign partyRoleTypeId = partyRole.roleTypeId! />
				                        <#break>
				                    <#else>
				                        <#if partyRoleTypeIds?has_content && partyRoleTypeIds.contains("CUSTOMER")>
				                            <#assign partyRoleTypeId = "CUSTOMER" />
				                        <#else>
				                            <#assign partyRoleTypeId = partyRole.roleTypeId! />
				                        </#if>
				                        <#break>
				                    </#if>
				                </#if>
				            </#list>
				        <#else>
				            <#assign partyRoleTypeId = "">
				        </#if> 
					              
				        <td class="typeCol">
				            <#if partyRoleTypeId?has_content>
				                <#assign roleType = delegator.findByPrimaryKey("RoleType", {"roleTypeId", partyRoleTypeId})/>
				            </#if>
				            ${roleType.description?if_exists}
				        </td>
				        <td class="statusCol">
				            <#if (party.statusId?has_content && party.statusId=="PARTY_ENABLED")>
				                ${uiLabelMap.CustomerEnabledInfo}
				            <#else>
				                ${uiLabelMap.CustomerDisabledInfo}
				            </#if>
				        </td>
				        <td class="statusCol lastCol">
				            <#if (downloadStatus?has_content && (downloadStatus == "Y"))>
				                ${uiLabelMap.ExportStatusInfo}
				            <#else>
				                ${uiLabelMap.DownloadNewInfo}
				            </#if>
				        </td>
				        <td class="actionCol">
				            <a href="javascript:setRowNo('${x}');javascript:deleteOrganizationCustomerRow('${organizationEmployeeId}','${person.firstName} ${person.lastName}', '', '');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteOrganizationCustomerTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
				            <a href="<@ofbizUrl>${detailAction!}?partyId=${organizationEmployeeId}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${uiLabelMap.EditCustomerTooltip}');" onMouseout="hideTooltip()"><span class="editIcon"></span></a>
				        </td>
                    </tr>
                    <#assign minRowNo = minRowNo+1/>
                </#list>
            </#if>
        </#if>
        </table>
        <#if (!resultList?has_content) || (resultList?exists && resultList?has_content  && !parameters.totalRows?exists)>
	        <div class="entryButton footer addCustomerRow">
	            <a href="javascript:setRowNo('${rowNo}');javascript:openLookup(document.${detailFormName!}.organizationCustomerPartyId,document.${detailFormName!}.organizationCustomerPartyTypeId,'lookupCustomer','500','700','center','true');" class="buttontext standardBtn action">${addActionBtn2!"${uiLabelMap.AddBtn}"}</a>
	        </div>
        <#elseif parameters.totalRows?exists>
            <div class="entryButton footer addCustomerRow">
	            <a href="javascript:setRowNo('${minRowNo}');javascript:openLookup(document.${detailFormName!}.organizationCustomerPartyId,document.${detailFormName!}.organizationCustomerPartyTypeId,'lookupCustomer','500','700','center','true');" class="buttontext standardBtn action">${addActionBtn2!"${uiLabelMap.AddBtn}"}</a>
	        </div>
        </#if>
        
<!-- end listBox -->
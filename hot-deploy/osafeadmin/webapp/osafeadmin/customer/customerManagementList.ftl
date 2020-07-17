<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.CustomerNoLabel}</th>
                <th class="nameCol">${uiLabelMap.LastNameLabel}</th>
                <th class="nameCol">${uiLabelMap.FirstNameLabel}</th>
                <th class="descCol">${uiLabelMap.UserLoginLabel}</th>
                <th class="addrCol">${uiLabelMap.AddressLabel}</th>
                <th class="typeCol">${uiLabelMap.CustomerRoleLabel}</th>
                <th class="statusCol">${uiLabelMap.CustomerStatusLabel}</th>
                <th class="statusCol lastCol">${uiLabelMap.ExportStatusLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as partyRow>
                  <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                    <td class="idCol <#if !partyRow_has_next>lastRow</#if> firstCol" ><a href="<@ofbizUrl>customerDetail?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
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
                      <#assign userLoginId = userLogins.get(0).userLoginId>
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
                    <#assign contactMechs = Static["org.ofbiz.party.contact.ContactHelper"].getContactMech(party, "BILLING_LOCATION", "POSTAL_ADDRESS", false)!""/>                    
                    <#assign contactMech = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(contactMechs)!""/>
                    <#if contactMech?has_content>
                       <#assign postalAddress=contactMech.getRelatedOne("PostalAddress")!"">
                    </#if>
                    <td class="addrCol <#if !partyRow_has_next>lastRow</#if>">
                    <#if postalAddress?has_content>
		                ${postalAddress.address1}<br>${postalAddress.city!""} ${postalAddress.stateProvinceGeoId!""}
                    </#if>
                    </td> 
                     <#assign partyRoles = delegator.findByAnd("PartyRole", {"partyId", partyRow.partyId})>
                     <#if partyRoles?has_content>
                      <#list partyRoles as partyRole>
	                      <#assign roleType = partyRole.getRelatedOne("RoleType") />
	                      <#if roleType.roleTypeId=="GUEST_CUSTOMER">
	                        <#assign partyRoleType = roleType.description />
	                        <#break>
	                      </#if>
	                      <#if roleType.roleTypeId=="CUSTOMER" || roleType.roleTypeId=="EMAIL_SUBSCRIBER">
	                         <#assign partyRoleType = roleType.description />
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
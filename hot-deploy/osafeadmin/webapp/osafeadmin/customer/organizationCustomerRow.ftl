<#if parameters.organizationCustomerPartyId?has_content>
    <tr class="dataRow odd">
        <#assign userLoginId = "">
        <input type="hidden" name="organizationEmployeeId" id="organizationEmployeeId" value="${parameters.organizationCustomerPartyId!}"/>
        <td class="idCol firstCol" ><a href="<@ofbizUrl>${detailAction!}?partyId=${parameters.organizationCustomerPartyId}</@ofbizUrl>">${parameters.organizationCustomerPartyId}</a></td>
        <#assign person = delegator.findByPrimaryKey("Person", {"partyId", parameters.organizationCustomerPartyId})/>
        <#assign party = delegator.findByPrimaryKey("Party", {"partyId", parameters.organizationCustomerPartyId})/>
        <#assign downloadStatus = "">
        <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : parameters.organizationCustomerPartyId, "attrName" : "IS_DOWNLOADED"}, false)!"" />
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
        <#assign partyContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator, parameters.organizationCustomerPartyId, false)!""/>
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
                 
        <#assign partyRoles = delegator.findByAnd("PartyRole", {"partyId", parameters.organizationCustomerPartyId}) />
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
            <#assign partyRoleType = "">
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
            <a href="javascript:setRowNo('');javascript:deleteOrganizationCustomerRow('${parameters.organizationCustomerPartyId}','${person.firstName} ${person.lastName}', '', '');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteOrganizationCustomerTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
            <a href="<@ofbizUrl>${detailAction!}?partyId=${parameters.organizationCustomerPartyId}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${uiLabelMap.EditCustomerTooltip}');" onMouseout="hideTooltip()"><span class="editIcon"></span></a>
        </td>
    </tr>
</#if>
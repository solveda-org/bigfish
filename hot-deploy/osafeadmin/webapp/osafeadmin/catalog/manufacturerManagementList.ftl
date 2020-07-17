<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ManufacturerNoLabel}</th>
                <th class="nameCol">${uiLabelMap.NameLabel}</th>
                <th class="nameCol">${uiLabelMap.ShortDescriptionLabel}</th>
                <th class="actionCol">&nbsp;</th>
                <th class="addrCol">${uiLabelMap.AddressLabel}</th>
                <th class="actionCol">&nbsp;</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as partyRow>
                  <#assign hasNext = partyRow_has_next/>
                  <#assign party = delegator.findByPrimaryKey("Party", {"partyId", partyRow.partyId})/>
                  <#-- get contact mech Id -->
                  <#assign contactMechs = Static["org.ofbiz.party.contact.ContactHelper"].getContactMechByType(party, "POSTAL_ADDRESS", false)!""/>                    
                  <#assign contactMech = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(contactMechs)!""/>
                  <#if contactMech?has_content>
                    <#assign postalAddress = contactMech.getRelatedOne("PostalAddress")!"">
                  </#if>
                  <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                    <td class="idCol <#if !partyRow_has_next>lastRow</#if> firstCol" ><a href="<@ofbizUrl>manufacturerDetail?manufacturerPartyId=${partyRow.partyId}&contactMechId=${postalAddress.contactMechId!""}</@ofbizUrl>">${partyRow.partyId}</a></td>
                   <#assign partyContentWrapper = Static["org.ofbiz.party.content.PartyContentWrapper"].makePartyContentWrapper(party, request)!""/> 
                   <#if partyContentWrapper?has_content>
                     <#assign manufacturerProfileName = partyContentWrapper.get("PROFILE_NAME", false)/> 
                     <#assign manufacturerShortDescription = partyContentWrapper.get("DESCRIPTION", false)/> 
                     <#assign manufacturerLongDescription = partyContentWrapper.get("LONG_DESCRIPTION", false)/> 
                   </#if>
                    <td class="nameCol <#if !partyRow_has_next>lastRow</#if>"><#if manufacturerProfileName?exists && manufacturerProfileName?has_content>${manufacturerProfileName!""}</#if></td>
                    <td class="nameCol <#if !partyRow_has_next>lastRow</#if>"><#if manufacturerShortDescription?exists && manufacturerShortDescription?has_content>${manufacturerShortDescription!""}</#if></td>
                    <td class="actionCol">
                      <#if manufacturerLongDescription?exists && manufacturerLongDescription?has_content && manufacturerLongDescription !="">
                        <#assign manufacturerLongDescriptionToolTip = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(manufacturerLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
                      </#if>
                      <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${manufacturerLongDescriptionToolTip!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
                    </td>
                    <td class="addrCol <#if !partyRow_has_next>lastRow</#if>">
                    <#if postalAddress?has_content>
		                ${postalAddress.address1!""}<#if postalAddress.city?has_content>, ${postalAddress.city!""}</#if><#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId!""}</#if>
                    </#if>
                    </td>
                    <td class="actionCol <#if !hasNext?if_exists>lastRow</#if> <#if !hasNext?if_exists>bottomActionIconRow</#if>">
                      <#assign editManufacturerTooltip = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(uiLabelMap.EditManufacturerTooltip, ADM_TOOLTIP_MAX_CHAR!)/>
                      <a href="<@ofbizUrl>manufacturerDetail?manufacturerPartyId=${partyRow.partyId}</@ofbizUrl>" onMouseover="javascript:showTooltip(event,'${editManufacturerTooltip!""}');" onMouseout="hideTooltip()"><span class="detailIcon"></span></a>
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
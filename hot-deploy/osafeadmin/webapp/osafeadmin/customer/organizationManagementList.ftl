<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.OrgNoLabel}</th>
                <th class="nameCol">${uiLabelMap.NameLabel}</th>
                <th class="addrCol">${uiLabelMap.BillingAddressLabel}</th>
                <th class="statusCol">${uiLabelMap.StatusLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as partyRow>
                  <#assign hasNext = partyRow_has_next/>
                  <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                  	<#assign userLoginId = "">
                    <td class="idCol <#if !partyRow_has_next>lastRow</#if> firstCol" ><a href="<@ofbizUrl>organizationDetail?partyId=${partyRow.partyId}</@ofbizUrl>">${partyRow.partyId}</a></td>
                   <#assign partyGroup = delegator.findByPrimaryKey("PartyGroup", {"partyId", partyRow.partyId})/>
                   <#assign party = delegator.findByPrimaryKey("Party", {"partyId", partyRow.partyId})/>
                    
                    <td class="nameCol <#if !partyRow_has_next>lastRow</#if>"><#if partyGroup?has_content>${partyGroup.groupName!""}</#if></td>
                    
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
                     
                    <td class="statusCol <#if !partyRow_has_next>lastRow</#if>">
                        <#if (partyRow.statusId?has_content && partyRow.statusId=="PARTY_ENABLED")>
                            ${uiLabelMap.CustomerEnabledInfo}
                        <#else>
                            ${uiLabelMap.CustomerDisabledInfo}
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
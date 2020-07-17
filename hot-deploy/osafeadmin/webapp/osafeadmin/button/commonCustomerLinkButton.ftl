<#if party?has_content && party.partyTypeId == "PERSON">
    <div class="linkButton">
      <#if !showCustomerEditLink?has_content>
          <#assign showCustomerEditLink = "true"/>
      </#if>
      <#if !showCustomerOrderLink?has_content>
          <#assign showCustomerOrderLink = "true"/>
      </#if>
      <#if !showCustomerNoteLink?has_content>
          <#assign showCustomerNoteLink = "true"/>
      </#if>
      <#if !showCustomerContactUsLink?has_content>
          <#assign showCustomerContactUsLink = "true"/>
      </#if>
      <#if !showCustomerCatalogReqLink?has_content>
          <#assign showCustomerCatalogReqLink = "true"/>
      </#if>
      <#if !showCustomerActivityLink?has_content>
          <#assign showCustomerActivityLink = "true"/>
      </#if>
      <#if !showExportToPdfLink?has_content>
          <#assign showExportToPdfLink = "true"/>
      </#if>
      <#if !showExportToXmlLink?has_content>
          <#assign showExportToXmlLink = "true"/>
      </#if>
      
      <#if showCustomerEditLink == 'true'>
          <a href="<@ofbizUrl>customerDetail?partyId=${party.partyId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.EditCustomerTooltip}');" onMouseout="hideTooltip()"><span class="editIcon"></span></a>
      </#if>
      <#if showCustomerOrderLink == 'true'>
           <#assign partyOrders = party.getRelated("OrderRole",  {"roleTypeId" : "PLACING_CUSTOMER"}, [])?if_exists/>
           <#if (partyOrders?has_content)>
               <a href="<@ofbizUrl>searchOrders?partyId=${party.partyId}&preRetrieved=Y</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.CustomerOrderTooltip} [${partyOrders.size()!}]');" onMouseout="hideTooltip()"><span class="orderIcon"></span></a>
           <#else>
               <span class="orderIcon" onMouseover="showTooltip(event,'${uiLabelMap.CustomerOrderTooltip} [0]');" onMouseout="hideTooltip()"></span>
           </#if>
      </#if>
      <#if showCustomerNoteLink == 'true'>
           <#assign partyNotes = party.getRelated("PartyNote")?if_exists/>
           <#if (partyNotes?has_content)>
               <a href="<@ofbizUrl>customerNoteList?partyId=${party.partyId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.CustomerNoteTooltip} [${partyNotes.size()!}]');" onMouseout="hideTooltip()"><span class="noteIcon"></span></a>
           <#else>
               <a href="<@ofbizUrl>customerNoteList?partyId=${party.partyId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.CustomerNoteTooltip} [0]');" onMouseout="hideTooltip()"><span class="noteIcon"></span></a>
           </#if>
      </#if>
      <#if showCustomerContactUsLink == 'true'>
           <#assign partyContactUs = delegator.findByAnd("CustRequest",  {"fromPartyId" : party.partyId, "custRequestTypeId" : "RF_CONTACT_US"})?if_exists/>
           <#if (partyContactUs?has_content)>
               <a href="<@ofbizUrl>custRequestContactUsSearch?partyId=${party.partyId}&preRetrieved=Y&productStoreall=Y</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.CustomerContactUsTooltip} [${partyContactUs.size()!}]');" onMouseout="hideTooltip()"><span class="contactUsIcon"></span></a>
           <#else>
               <span class="contactUsIcon" onMouseover="showTooltip(event,'${uiLabelMap.CustomerContactUsTooltip} [0]');" onMouseout="hideTooltip()"></span>
           </#if>
      </#if>
      <#if showCustomerCatalogReqLink == 'true'>
           <#assign partyCatalogReqs = delegator.findByAnd("CustRequest",  {"fromPartyId" : party.partyId, "custRequestTypeId" : "RF_CATALOG"})?if_exists/>
           <#if (partyCatalogReqs?has_content)>
               <a href="<@ofbizUrl>custRequestCatalogSearch?partyId=${party.partyId}&preRetrieved=Y&productStoreall=Y</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.CustomerCatalogRequestTooltip} [${partyCatalogReqs.size()!}]');" onMouseout="hideTooltip()"><span class="catalogRequestIcon"></span></a>
           <#else>
               <span class="catalogRequestIcon" onMouseover="showTooltip(event,'${uiLabelMap.CustomerCatalogRequestTooltip} [0]');" onMouseout="hideTooltip()"></span>
           </#if>
      </#if>
      <#if showCustomerActivityLink == 'true'>
          <#assign userLogins = party.getRelated("UserLogin")>
          <#if userLogins?has_content>
              <#assign userLoginId = userLogins.get(0).userLoginId>
          </#if>
          <#if userLoginId?has_content>
              <a href="<@ofbizUrl>customerActivityDetail?partyId=${parameters.partyId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.CustomerWebsiteActivityTooltip}');" onMouseout="hideTooltip()"><span class="customerActivityIcon"></span></a>
          <#else>
               <span class="customerActivityIcon" onMouseover="showTooltip(event,'${uiLabelMap.CustomerWebsiteActivityTooltip} [0]');" onMouseout="hideTooltip()"></span>
          </#if>
      </#if>
      <#if showExportToPdfLink == 'true'>
          <a href="<@ofbizUrl>AdminCustomer.pdf?partyId=${party.partyId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ExportToPdfTooltip}');" onMouseout="hideTooltip()"><span class="exportToPdfIcon"></span></a>
      </#if>
      <#if showExportToXmlLink == 'true'>
          <a href="<@ofbizUrl>exportCustomerXML?partyId=${party.partyId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ExportToXmlTooltip}');" onMouseout="hideTooltip()"><span class="exportToXmlIcon"></span></a>
      </#if>
    </div>
</#if>

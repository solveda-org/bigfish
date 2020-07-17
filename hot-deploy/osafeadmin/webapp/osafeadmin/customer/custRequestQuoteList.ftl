<!-- start listBox -->
<thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.IdLabel}</th>
    <th class="idCol firstCol">${uiLabelMap.CustNoLabel}</th>
    <th class="nameCol">${uiLabelMap.LastNameLabel}</th>
    <th class="nameCol">${uiLabelMap.FirstNameLabel}</th>
    <th class="addrCol">${uiLabelMap.EmailAddressLabel}</th>
    <th class="dateCol">${uiLabelMap.RequestDateLabel}</th>
    <th class="statusCol">${uiLabelMap.ExportStatusLabel}</th>
    <th class="actionCol"></th>
  </tr>
</thead>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    
    <#list resultList as custRequestInfo>
      <#assign requestQuote = custRequestInfo.CustRequest!>
      <#assign custReqAttributeList = custRequestInfo.CustRequestAttributeList!>
      <#assign hasNext = custRequestInfo_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !custRequestInfo_has_next?if_exists>lastRow</#if> firstCol" >
            <a href="<@ofbizUrl>custRequestQuoteDetail?custReqId=${requestQuote.custRequestId?if_exists}<#if requestQuote.fromPartyId?has_content>&partyId=${requestQuote.fromPartyId?if_exists}</#if></@ofbizUrl>">${requestQuote.custRequestId?if_exists}</a>
        </td>
        <td class="idCol <#if !reqCatalog_has_next?if_exists>lastRow</#if> firstCol" >
            <#if requestQuote.fromPartyId?has_content>
                <a href="<@ofbizUrl>customerDetail?partyId=${requestQuote.fromPartyId?if_exists}</@ofbizUrl>">${requestQuote.fromPartyId?if_exists}</a>
            </#if>
        </td>
        <#assign comment =""/>
        <#assign partNumber =""/>
        <#if custReqAttributeList?exists && custReqAttributeList?has_content>
          <#list custReqAttributeList as custReqAttribute>
            <#if custReqAttribute.attrName == 'LAST_NAME'>
              <#assign lname = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'FIRST_NAME'>
              <#assign fname = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'EMAIL_ADDRESS'>
              <#assign email = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'IS_DOWNLOADED'>
              <#assign exported = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'COMMENT'>
              <#assign comment = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'SKU_OR_PART'>
              <#assign partNumber = custReqAttribute.attrValue!""/>
            </#if>
          </#list>
        </#if>
        <td class="nameCol">
          ${lname!}
        </td>
        <td class="nameCol">
          ${fname!}
        </td>
        <td class="addrCol">
          ${email!}
        </td>
        <td class="dateCol">
          ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(requestQuote.createdDate, preferredDateFormat).toLowerCase())!"N/A"}
        </td>
        <td class="statusCol">
          <#if exported == 'Y'>
            ${uiLabelMap.ExportStatusInfo}
          <#else>
            ${uiLabelMap.DownloadNewInfo}
          </#if>
        </td>
        <td class="actionCol">
          <#if partNumber != ''>
              <#assign partNumber = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(partNumber, ADM_TOOLTIP_MAX_CHAR!)/>
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${partNumber!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
          </#if>
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  </#if>
<!-- end listBox -->
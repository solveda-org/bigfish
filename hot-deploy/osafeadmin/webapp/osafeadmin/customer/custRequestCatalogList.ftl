<!-- start listBox -->
<thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.IdLabel}</th>
    <th class="idCol firstCol">${uiLabelMap.CustNoLabel}</th>
    <th class="nameCol">${uiLabelMap.LastNameLabel}</th>
    <th class="nameCol">${uiLabelMap.FirstNameLabel}</th>
    <th class="descCol">${uiLabelMap.AddressLabel}</th>
    <th class="dateCol">${uiLabelMap.RequestDateLabel}</th>
    <th class="statusCol">${uiLabelMap.ExportStatusLabel}</th>
    <th class="actionCol"></th>
  </tr>
</thead>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    
    <#list resultList as custRequestInfo>
      <#assign reqCatalog = custRequestInfo.CustRequest!>
      <#assign custReqAttributeList = custRequestInfo.CustRequestAttributeList!>
      <#assign hasNext = custRequestInfo_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !custRequestInfo_has_next?if_exists>lastRow</#if> firstCol" >
            <a href="<@ofbizUrl>custRequestCatalogDetail?custReqId=${reqCatalog.custRequestId?if_exists}<#if reqCatalog.fromPartyId?has_content>&partyId=${reqCatalog.fromPartyId?if_exists}</#if></@ofbizUrl>">${reqCatalog.custRequestId?if_exists}</a>
        </td>
        <td class="idCol <#if !custRequestInfo_has_next?if_exists>lastRow</#if> firstCol" >
            <#if reqCatalog.fromPartyId?has_content>
                <a href="<@ofbizUrl>customerDetail?partyId=${reqCatalog.fromPartyId?if_exists}</@ofbizUrl>">${reqCatalog.fromPartyId?if_exists}</a>
            </#if>
        </td>
        <#assign comment =""/>
        <#assign exported =""/>
        <#if custReqAttributeList?exists && custReqAttributeList?has_content>
          <#list custReqAttributeList as custReqAttribute>
            <#if custReqAttribute.attrName == 'LAST_NAME'>
              <#assign lname = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'FIRST_NAME'>
              <#assign fname = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'ADDRESS1'>
              <#assign address1 = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'CITY'>
              <#assign city = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'STATE_PROVINCE'>
              <#assign state = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'IS_DOWNLOADED'>
              <#assign exported = custReqAttribute.attrValue!""/>
            </#if>
            <#if custReqAttribute.attrName == 'COMMENT'>
              <#assign comment = custReqAttribute.attrValue!""/>
            </#if>
          </#list>
        </#if>
        <td class="nameCol">
          ${lname!}
        </td>
        <td class="nameCol">
          ${fname!}
        </td>
        <td class="descCol">
          ${address1!} ${city!} ${state!}
        </td>
        <td class="dateCol">
          ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(reqCatalog.createdDate, preferredDateFormat).toLowerCase())!"N/A"}
        </td>
        <td class="statusCol">
          <#if exported == 'Y'>
            ${uiLabelMap.ExportStatusInfo}
          <#else>
            ${uiLabelMap.DownloadNewInfo}
          </#if>
        </td>
        <td class="actionCol">
          <#if comment != ''>
            <#assign comment = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(comment, ADM_TOOLTIP_MAX_CHAR!)/>
            <a href="javascript:void(0);" onMouseover="showTooltip(event,'${comment!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
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
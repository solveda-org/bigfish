<!-- start listBox -->
<thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.StoreLocationCodeLabel}</th>
    <th class="nameCol">${uiLabelMap.StoreLocationNameLabel}</th>
    <th class="statusCol">${uiLabelMap.StoreLocationStatusLabel}</th>
    <th class="cityCol">${uiLabelMap.StoreLocationCityLabel}</th>
    <th class="stateCol">${uiLabelMap.StoreLocationStateLabel}</th>
    <th class="zipCol">${uiLabelMap.StoreLocationZipLabel}</th>
    <th class="geoCol">${uiLabelMap.StoreLocationGeoLabel}</th>
    <th class="tooltipCol lastCol"></th>
  </tr>
</thead>
<#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as partyRow>
      <#assign partyGroup = delegator.findOne("PartyGroup", {"partyId": partyRow.partyId}, false)/>
      <#if partyGroup?has_content>
        <#assign groupName = partyGroup.groupName!""/>
        <#assign groupNameLocal = partyGroup.groupNameLocal!""/>
      </#if>
      <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
        <td class="idCol <#if !partyRow_has_next>lastRow</#if> firstCol" ><a href="<@ofbizUrl>storeLocationDetail?storePartyId=${partyRow.partyId}&groupNameLocal=${groupNameLocal!""}</@ofbizUrl>">${groupNameLocal!""}</a></td>
        <td class="nameCol <#if !partyRow_has_next>lastRow</#if>">${groupName!""}</td>
        <td class="statusCol <#if !partyRow_has_next>lastRow</#if>">
          <#if (partyRow.statusId?has_content && partyRow.statusId=="PARTY_ENABLED")>
            ${uiLabelMap.StoreLocationOpenedInfo}
          <#else>
            ${uiLabelMap.StoreLocationClosedInfo}
          </#if>
        </td>
        <#assign postalAddress=""/>
        <#assign partyContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator, partyRow.partyId, false)!""/>
        <#if partyContactMechValueMaps?has_content>
          <#list partyContactMechValueMaps as partyContactMechValueMap>
            <#assign contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes/>
            <#list contactMechPurposes as contactMechPurpose>
              <#if contactMechPurpose.contactMechPurposeTypeId=="GENERAL_LOCATION">
                <#assign postalAddress = partyContactMechValueMap.postalAddress/>
              </#if>
            </#list>
          </#list>
        </#if>
        <td class="cityCol <#if !partyRow_has_next>lastRow</#if>">
          <#if postalAddress?has_content>
              ${setRequestAttribute("PostalAddress",postalAddress)}
              ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_CITY")}
              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
          </#if>
        </td>
        <td class="stateCol <#if !partyRow_has_next>lastRow</#if>">
          <#if postalAddress?has_content>
              ${setRequestAttribute("PostalAddress",postalAddress)}
              ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_STATE")}
              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
          </#if>
        </td>
        <td class="zipCol <#if !partyRow_has_next>lastRow</#if>">
          <#if postalAddress?has_content>
              ${setRequestAttribute("PostalAddress",postalAddress)}
              ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_ZIP")}
              ${screens.render("component://osafeadmin/widget/CommonScreens.xml#displayPostalAddress")}
          </#if>
        </td>
        <td class="geoCol <#if !partyRow_has_next>lastRow</#if>">
          <#assign partyGeoPointList = delegator.findByAnd("PartyGeoPoint", {"partyId": partyRow.partyId}) />
          <#if partyGeoPointList?has_content>
            <#assign partyGeoPoint = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(partyGeoPointList)/>
          </#if>
          <#if partyGeoPoint?has_content>
            ${uiLabelMap.StoreLocationGeoYesInfo}
          <#else>
            ${uiLabelMap.StoreLocationGeoNoInfo}
          </#if>
        </td>
        <td class="tooltipCol <#if !partyRow_has_next>lastRow</#if>lastCol">
          <#assign tooltipData = "<div class=tooltipData>"/>
          <#assign storeHoursPartyContents = delegator.findByAnd("PartyContent", {"partyId": partyRow.partyId, "partyContentTypeId": "STORE_HOURS"})/>
          <#if storeHoursPartyContents?has_content>
              <#assign storeHoursPartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(storeHoursPartyContents)/>
              <#if storeHoursPartyContent?has_content>
                <#assign content = storeHoursPartyContent.getRelatedOne("Content")/>
                <#if content?has_content>
                  <#assign storeHoursTextData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, content.contentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
                  <#if storeHoursTextData?has_content && storeHoursTextData != "null">
                    <#assign storeHoursTextData = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(storeHoursTextData, ADM_TOOLTIP_MAX_CHAR!)/>
                    <#assign tooltipData = tooltipData+"<div class=tooltipFirstSubheading>${uiLabelMap.StoreWrkHrCaption}</div>"/>
                    <#assign tooltipData = tooltipData+"<div>${storeHoursTextData}</div>"/>
                  </#if>
                </#if>
              </#if>
          </#if>
          <#assign storeNoticePartyContents = delegator.findByAnd("PartyContent", {"partyId": partyRow.partyId, "partyContentTypeId": "STORE_NOTICE"})/>
          <#if storeNoticePartyContents?has_content>
              <#assign storeNoticePartyContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(storeNoticePartyContents)/>
              <#if storeNoticePartyContent?has_content>
                <#assign content = storeNoticePartyContent.getRelatedOne("Content")/>
                <#if content?has_content>
                  <#assign storeNoticeTextData = Static["org.ofbiz.content.content.ContentWorker"].renderContentAsText(dispatcher, delegator, content.contentId, Static["javolution.util.FastMap"].newInstance(), locale, "", true)/>
                  <#if storeNoticeTextData?has_content && storeNoticeTextData != "null">
                    <#assign storeNoticeTextData = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(storeNoticeTextData, ADM_TOOLTIP_MAX_CHAR!)/>
                    <#assign tooltipData = tooltipData+"<div class=tooltipFirstSubheading>${uiLabelMap.StoreNoticeCaption}</div>"/>
                    <#assign tooltipData = tooltipData+"<div>${storeNoticeTextData}</div>"/>
                  </#if>
                </#if>
              </#if>
          </#if>
          <#assign tooltipData = tooltipData+"</div>"/>
          <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
        </td>
      </tr>
      <#-- toggle the row color -->
      <#if rowClass == "2">
        <#assign rowClass = "1"/>
      <#else>
        <#assign rowClass = "2"/>
      </#if>
    </#list>
</#if>
<!-- end listBox -->
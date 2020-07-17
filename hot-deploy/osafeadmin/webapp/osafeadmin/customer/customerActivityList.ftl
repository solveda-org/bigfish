  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.DateLabel}</th>
    <th class="dateCol">${uiLabelMap.TimeLabel}</th>
    <th class="nameCol">${uiLabelMap.ActivityLabel}</th>
    <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
    <th class="actionCol"></th>
  </tr>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as customerActivity>
      <#assign hasNext = customerActivity_has_next>
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !customerActivity_has_next?if_exists>lastRow</#if> firstCol">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(customerActivity.activityDate, preferredDateFormat).toLowerCase())!"N/A"}</td>
        <td class="dateCol <#if !customerActivity_has_next?if_exists>lastRow</#if>">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(customerActivity.activityDate, preferredTimeFormat).toLowerCase())!"N/A"}</td>
        <td class="nameCol <#if !customerActivity_has_next?if_exists>lastRow</#if>">
        <#if customerActivity.activityLink !=''>
          <a href="<@ofbizUrl>${customerActivity.activityLink}</@ofbizUrl>">${customerActivity.activity}</a>
        <#else>
          ${customerActivity.activity}
        </#if>
        </td>
        <td class="descCol <#if !customerActivity_has_next?if_exists>lastRow</#if>">${customerActivity.activityDescription!""}</td>
        <td class="actionCol <#if !customerActivity_has_next?if_exists>lastRow</#if>">
          <#if customerActivity.activityHoverText?has_content>
            <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${customerActivity.activityHoverText!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
          </#if>
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
  </#if>

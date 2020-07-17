<#if preRetrieved?has_content && preRetrieved != "N">
<tr class="heading">
    <th class="qtyCol firstCol">${uiLabelMap.MonthLabel}</th>
        <th class="qtyCol">${uiLabelMap.AnonVisitorLabel}</th>
        <th class="qtyCol">${uiLabelMap.AnonItemsVisitorLabel}</th>
        <th class="qtyCol">${uiLabelMap.RegVisitorLabel}</th>
        <th class="qtyCol">${uiLabelMap.RegItemsVisitorLabel}</th>
</tr>
    
<#assign now = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
<#if resultList?has_content>
    <#assign rowClass = "1">
    <#assign anonTotal = 0>
    <#assign anonItemsTotal = 0>
    <#assign regTotal = 0>
    <#assign regItemsTotal = 0>
    <#list resultList as visitorCountInfo>
    	<#assign anonTotal = anonTotal + visitorCountInfo.anonVisitorCount>
    	<#assign anonItemsTotal = anonItemsTotal + visitorCountInfo.anonItemsVisitorCount>
    	<#assign regTotal = regTotal + visitorCountInfo.regVisitorCount>
    	<#assign regItemsTotal = regItemsTotal + visitorCountInfo.regItemsVisitorCount>
        <#assign hasNext = visitorCountInfo_has_next>
        <#assign dateFrom = Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(visitorCountInfo.tempFromDate, entryDateTimeFormat)!>
        <#assign dateTo = Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(visitorCountInfo.tempToDate, entryDateTimeFormat)!>
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
             <td class="qtyCol firstCol" >${visitorCountInfo.month!""}</a></td>
             <td class="qtyCol">
	             <#if visitorCountInfo.anonVisitorCount != 0>
	             	<a href="abandonedCartList?isAnon=isAnonAll&isReg=isNoReg&dateFrom=${dateFrom}&dateTo=${dateTo}">${visitorCountInfo.anonVisitorCount!""}</a>
	             <#else>
	             	${visitorCountInfo.anonVisitorCount!""}
	             </#if>
             </td>
             <td class="qtyCol">
             	<#if visitorCountInfo.anonItemsVisitorCount != 0>
	             	<a href="abandonedCartList?isAnon=isAnonItems&isReg=isNoReg&dateFrom=${dateFrom}&dateTo=${dateTo}">${visitorCountInfo.anonItemsVisitorCount!""}</a>
	             <#else>
	             	${visitorCountInfo.anonItemsVisitorCount!""}
	             </#if>
             </td>
             <td class="qtyCol">
	             <#if visitorCountInfo.regVisitorCount != 0>
	             	<a href="abandonedCartList?isReg=isRegAll&isAnon=isNoAnon&dateFrom=${dateFrom}&dateTo=${dateTo}">${visitorCountInfo.regVisitorCount!""}</a>
	             <#else>
	             	${visitorCountInfo.regVisitorCount!""}
	             </#if>
             </td>
             <td class="qtyCol">
	             <#if visitorCountInfo.regItemsVisitorCount != 0>
	             	<a href="abandonedCartList?isReg=isRegItems&isAnon=isNoAnon&dateFrom=${dateFrom}&dateTo=${dateTo}">${visitorCountInfo.regItemsVisitorCount!""}</a>
	             <#else>
	             	${visitorCountInfo.regItemsVisitorCount!""}
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
    <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
         <td class="qtyCol firstCol total" >${uiLabelMap.AbandonedCartTotalLabel!""}</td>
         <td class="qtyCol lastRow total">${anonTotal!""}</td>
         <td class="qtyCol lastRow total">${anonItemsTotal!""}</td>
         <td class="qtyCol lastRow total">${regTotal!""}</td>
         <td class="qtyCol lastRow total">${regItemsTotal!""}</td>
    </tr>
</#if>
</#if>
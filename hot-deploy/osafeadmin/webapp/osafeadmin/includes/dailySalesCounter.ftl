<#assign maskedTodayOrderCount = "#"+todayOrderCount?string(""?right_pad(6, "0"))>
<#assign maskedTodayTotalRevenue = todayTotalRevenue?string(""?right_pad(7, "0"))>
<#assign tableWidth = maskedTodayOrderCount?length >
<div id="rowOrderCount">
    <#list 0..(tableWidth-1) as cellNumber>
        <span class="orderCount<#if !cellNumber_has_next> lastCol</#if>">${maskedTodayOrderCount[cellNumber]}</span>
    </#list>
</div>
<div id="rowTotalRevenue">
    <#list 0..(tableWidth-1) as cellNumber>
        <span class="totalRevenue<#if !cellNumber_has_next> lastCol</#if>"><#if cellNumber_index == 0>${StringUtil.wrapString(globalContext.currencySymbol)!}<#else>${maskedTodayTotalRevenue[cellNumber]}</#if></span>
    </#list>
</div>
<!-- start operationOrders.ftl -->
<div class="displayBox ordersRequiringWork">
    <div class="header"><h2>${uiLabelMap.OperationsHeading}</h2></div>
    <div class="subHeader"><h3>${uiLabelMap.OrdersRequiringWorkHeading}</h3></div>
    <div class="boxBody">
        <table class = "osafe">
        <#if ordersRequiringWork?has_content>

            <#assign rowClass = "1">
            <#list ordersRequiringWork as workRow>
                <#assign description = workRow.description!"">
                <#assign count = workRow.count!"">
                <#assign statusId = workRow.statusId!"">
                <tr class="<#if rowClass == "2">even</#if>">
                    <td class="boxCaption <#if !workRow_has_next>lastRow</#if>"><#if description?has_content>${description}:</#if></td>
                    <td class="boxNumber <#if !workRow_has_next>lastRow</#if> lastCol">
                        <#if count != 0><a href="<@ofbizUrl>orderManagement?statusId=${statusId}&initializedCB=Y&preRetrieved=Y</@ofbizUrl>">${count}</a><#else>${count}</#if>
                    </td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        <#else>
                <tr>
                    <td colspan="3" class="boxNumber">${uiLabelMap.NoDataAvailableInfo}</td>
                </tr>
        </#if>
        </table>
    </div>
</div>
<!-- end operationOrders.ftl -->
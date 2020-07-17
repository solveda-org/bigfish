<!-- start creditCardTypeList.ftl -->
<tr class="heading">
    <th class="idCol firstCol">${enumIdLabel!""}</th>
    <#if enumCodeLabel?exists && enumCodeLabel?has_content && enumTypeId == "RECURRENCE_FREQUENCY">
        <th class="nameCol">${enumCodeLabel!""}</th>  
    </#if>
    <th class="descCol">${enumDescLabel!""}</th>
    <#if enumCodeLabel?exists && enumCodeLabel?has_content && enumTypeId == "CREDIT_CARD_TYPE">
        <th class="nameCol">${enumCodeLabel!""}</th>  
    </#if>
    <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
    <th class="dateCol">${uiLabelMap.CreatedDateLabel}</th>
    <th class="dateCol">${uiLabelMap.UpdatedDateLabel}</th>
</tr>
    
<#assign now = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
<#if resultList?has_content>
    <#assign rowClass = "1">
    <#list resultList as thisEnum>
        <#assign hasNext = thisEnum_has_next>
        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
            <td class="idCol firstCol" ><a href="<@ofbizUrl>${detailPage}?enumId=${thisEnum.enumId}</@ofbizUrl>">${thisEnum.enumId!"N/A"}</a></td>
            <#if enumCodeLabel?exists && enumCodeLabel?has_content  && enumTypeId == "RECURRENCE_FREQUENCY">
                 <td class="nameCol <#if !hasNext>lastRow</#if>">${thisEnum.enumCode?if_exists}</td>
             </#if>
             <td class="descCol <#if !hasNext>lastRow</#if>">${thisEnum.description?if_exists}</td>
             <#if enumCodeLabel?exists && enumCodeLabel?has_content  && enumTypeId == "CREDIT_CARD_TYPE">
                 <td class="nameCol <#if !hasNext>lastRow</#if>">${thisEnum.enumCode?if_exists}</td>
             </#if>
             <td class="seqCol <#if !hasNext>lastRow</#if>">${thisEnum.sequenceId?if_exists}</td>
            <td class="dateCol <#if !hasNext>lastRow</#if>">
                ${(thisEnum.createdStamp?string(preferredDateFormat))!""}
            </td>
            <td class="dateCol <#if !hasNext>lastRow</#if> lastCol">
                ${(thisEnum.lastUpdatedStamp?string(preferredDateFormat))!""}
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
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>
<!-- end creditCardTypeList.ftl -->
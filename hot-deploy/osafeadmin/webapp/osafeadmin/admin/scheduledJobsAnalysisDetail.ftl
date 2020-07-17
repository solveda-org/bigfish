<#if jobStatusAnalysisList?has_content && parameters.showDetail?has_content && parameters.showDetail == 'true'>
    <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="nameCol firstCol">${uiLabelMap.JobStatusLabel}</th>
           <th class="seqCol">${uiLabelMap.EntityRowsLabel}
           <th class="actionCol"></th>
           <th class="actionCol"></th>
         </tr>
       </thead>
       <tbody>
             <input type="hidden" name="statusId" value="" id="statusId"/>
             <input type="hidden" name="serviceName" value="" id="serviceName"/>
             <input type="hidden" name="isOld" value="" id="isOld"/>
            <#assign rowClass = "1">
            <#list jobStatusAnalysisList as jobType>
                <#assign hasNext = jobType_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                         ${jobType.status.toUpperCase()!}
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                        <#assign statusDescription = "" />
                        <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : jobType.statusId!}, false)?if_exists/>
                        <#if statusItem?has_content && statusItem.description?has_content>
                            <#assign statusDescription = statusItem.description />
                        </#if>

                        <#assign finishDateTime = Static["org.ofbiz.base.util.UtilDateTime"].addDaysToTimestamp(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(), -30)>
                        <#assign initializedCB = "initializedCB" + "=" + "Y">
                        <#assign preRetrieved = "&preRetrieved" + "=" + "Y">
                        <#assign status = "&srch${statusDescription!}" + "=" + "Y">
                        <#assign scheduledJobParams = initializedCB + preRetrieved + status>
                        <#if jobType.status.equalsIgnoreCase(uiLabelMap.FinishedWithinLabel)>
                            <#assign scheduledJobParams = scheduledJobParams + "&srchEndDateFrom"  + "=" + "${StringUtil.wrapString(finishDateTime?if_exists?string(entryDateTimeFormat))}">
                        <#elseif jobType.status.equalsIgnoreCase(uiLabelMap.FinishedExclLabel)>
                            <#assign scheduledJobParams = scheduledJobParams + "&srchEndDateTo"  + "=" + "${StringUtil.wrapString(finishDateTime?if_exists?string(entryDateTimeFormat))}">
                        </#if>

                        <#if jobType.rowCount?has_content && jobType.rowCount?number != 0>
                            <a href="<@ofbizUrl>scheduledJob?${scheduledJobParams!}</@ofbizUrl>">${jobType.rowCount!}</a>
                        <#else>
                            ${'0'}
                        </#if>
                     </td>
                     <td class="actionCol <#if !hasNext>lastRow</#if>">
                         <#if jobType.status.equalsIgnoreCase(uiLabelMap.FinishedExclLabel)>
                             <div class="infoText">
                                 <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.JobFinishedExclInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                             </div>
                         </#if>  
                         <#if jobType.status.equalsIgnoreCase(uiLabelMap.FinishedWithinLabel)>
                             <div class="infoText">
                                 <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.JobFinishedWithinInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                             </div>
                         </#if> 
                     </td>
                     <td class="actionCol <#if !hasNext>lastRow</#if>"></td>
                </tr>
                <#if jobType.result?has_content>
                    <#list jobType.result as result>
                        <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                             <td class="nameCol">
                                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                 ${result.serviceName!}
                             </td>
                             <td class="seqCol">
                                 <#if result.count?has_content && result.count?number != 0>
                                     <a href="<@ofbizUrl>scheduledJob?${scheduledJobParams!}&srchServiceName=${result.serviceName!}</@ofbizUrl>">${result.count!}</a>
                                 <#else>
                                     ${'0'}
                                 </#if>
                             </td>
                             <td class="actionCol <#if !hasNext>lastRow</#if>"></td>
                             <td class="actionCol">
                                 <#assign deleteConfirmTxt = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "DeleteScheduledJobsConfirmText", [result.count, result.serviceName, jobType.status.toUpperCase()], locale)>
                                 <#if jobType.status.equalsIgnoreCase(uiLabelMap.FinishedExclLabel)>
                                     <a href="javascript:deleteConfirmTxt('${deleteConfirmTxt!}');" class="standardBtn" onclick="javascript:setScheduledJobDeleteParams('Y', '${result.serviceName!}', '${jobType.statusId!}');">${uiLabelMap.DeleteBtn}</a>
                                 <#elseif !jobType.statusId.equalsIgnoreCase("SERVICE_RUNNING") && !jobType.statusId.equalsIgnoreCase("SERVICE_QUEUED")>
                                     <a href="javascript:deleteConfirmTxt('${deleteConfirmTxt!}');" class="standardBtn" onclick="javascript:setScheduledJobDeleteParams('N', '${result.serviceName!}', '${jobType.statusId!}');">${uiLabelMap.DeleteBtn}</a>
                                 </#if> 
                             </td>
                        </tr>
                    </#list>
                </#if>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </tbody>
        </table>
    <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
    </#if>

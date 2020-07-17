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
            
            <#assign rowClass = "1">
            <#list jobStatusAnalysisList as jobStatus>
                <#assign hasNext = jobStatus_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                         ${jobStatus.status.toUpperCase()!}
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                         ${jobStatus.rowCount!}
                     </td>
                     <td class="actionCol <#if !hasNext>lastRow</#if>">
                         <#if jobStatus.status.equalsIgnoreCase(uiLabelMap.FinishedExclLabel)>
                             <div class="infoText">
                                 <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.JobFinishedExclInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                             </div>
                         </#if>  
                         <#if jobStatus.status.equalsIgnoreCase(uiLabelMap.FinishedWithinLabel)>
                             <div class="infoText">
                                 <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.JobFinishedWithinInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                             </div>
                         </#if> 
                     </td>
                     <td class="actionCol <#if !hasNext>lastRow</#if>">
                         <#if jobStatus.status.equalsIgnoreCase(uiLabelMap.FinishedExclLabel)>
                             <input type="hidden" name="isOld" value="" id="isOld"/>
                             <a href="javascript:deleteConfirmTxt('${uiLabelMap.FinishedExclLabel}');" class="standardBtn" onclick="void(document.getElementById('isOld').value='Y');">${uiLabelMap.DeleteBtn}</a>
                         </#if>
                         <#if jobStatus.status.equalsIgnoreCase(uiLabelMap.FinishedWithinLabel)>
                             <a href="javascript:deleteConfirmTxt('${uiLabelMap.FinishedWithinLabel}');" class="standardBtn" onclick="void(document.getElementById('isOld').value='N');">${uiLabelMap.DeleteBtn}</a>
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
        </tbody>
        </table>
    <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
    </#if>

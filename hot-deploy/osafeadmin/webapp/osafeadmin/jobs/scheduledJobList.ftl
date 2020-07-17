<!-- start listBox -->
            <thead>
            <tr class="heading">
            	<th class="idCol firstCol">${uiLabelMap.JobIdLabel}</th>
                <th class="nameCol">${uiLabelMap.JobNameLabel}</th>
                <th class="nameCol">${uiLabelMap.ServiceNameLabel}</th>
                <th class="statusCol">${uiLabelMap.StatusLabel}</th>
                <th class="dateCol">${uiLabelMap.RunDateLabel}</th>
                <th class="dateCol">${uiLabelMap.StartDateLabel}</th>
                <th class="dateCol">${uiLabelMap.FinishDateLabel}</th>
                <th class="dateCol">${uiLabelMap.CancelDateLabel}</th>
            </tr>
            </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as job>
              <#assign hasNext = job_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
                    <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>scheduledJobDetail?jobId=${job.jobId}&jobName=${job.jobName}</@ofbizUrl>">${job.jobId}</a></td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${job.jobName!""}</td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${job.serviceName!""}</td>
                    <#assign statusId=job.statusId >
                    <#assign statusId = statusId?split("_") />
                    <#assign statusId = statusId[1] />
                    <td class="statusCol <#if !hasNext>lastRow</#if>">${statusId!""}</td>
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.runTime?exists && job.runTime?has_content>${job.runTime?string(preferredDateTimeFormat)}</#if></td>
                    <!-- <td class="dateCol <#if !hasNext>lastRow</#if>">${job.runTime?string(preferredDateFormat)}</td> -->
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.startDateTime?exists && job.startDateTime?has_content>${job.startDateTime?string(preferredDateTimeFormat)}</#if></td>
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.finishDateTime?exists && job.finishDateTime?has_content>${job.finishDateTime?string(preferredDateTimeFormat)}</#if></td>
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.cancelDateTime?exists && job.cancelDateTime?has_content>${job.cancelDateTime?string(preferredDateTimeFormat)}</#if></td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </#if>
<!-- end listBox -->
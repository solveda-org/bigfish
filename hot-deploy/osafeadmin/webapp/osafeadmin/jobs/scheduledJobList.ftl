<!-- start listBox -->
            <thead>
            <tr class="heading">
            	<th class="idCol firstCol">${uiLabelMap.JobIdLabel}</th>
            	<th class="idCol">${uiLabelMap.ParentJobIdLabel}</th>
            	<th class="idCol">${uiLabelMap.PrevJobIdLabel}</th>
                <th class="nameCol">${uiLabelMap.JobNameLabel}</th>
                <th class="statusCol">${uiLabelMap.StatusLabel}</th>
                <th class="actionCol"></th>
                <th class="dateCol">${uiLabelMap.RunDateLabel}</th>
                <th class="dateCol">${uiLabelMap.StartDateLabel}</th>
                <th class="dateCol">${uiLabelMap.FinishDateLabel}</th>
                <th class="dateCol">${uiLabelMap.CancelDateLabel}</th>
                <th class="actionColSmall"></th>
            </tr>
            </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as job>
              <#assign hasNext = job_has_next/>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
                    <td class="idCol firstCol <#if !hasNext>lastRow</#if> " ><a href="<@ofbizUrl>scheduledJobDetail?jobId=${job.jobId!""}&jobName=${job.jobName!""}</@ofbizUrl>">${job.jobId!""}</a></td>
                    <#assign parentJobName=""/>
                    <#if job.parentJobId?has_content>
                    	<#assign parentJob = delegator.findOne("JobSandbox", {"jobId" : job.parentJobId}, false)?if_exists/> 
                    	<#assign parentJobName = parentJob.jobName!""/> 
                    </#if>
                    <td class="idCol <#if !hasNext>lastRow</#if>">
                        <#if parentJobName?has_content>
                            <a href="<@ofbizUrl>scheduledJobDetail?jobId=${job.parentJobId!""}&jobName=${parentJobName!""}</@ofbizUrl>">${job.parentJobId!""}</a>
                        <#else>
                            ${job.parentJobId!""}
                        </#if>
                    </td>
                    <#assign prevJobName=""/>
                    <#if job.previousJobId?has_content>
                    	<#assign prevJob = delegator.findOne("JobSandbox", {"jobId" : job.previousJobId}, false)?if_exists/> 
                    	<#assign prevJobName = prevJob.jobName!""/> 
                    </#if>
                    <td class="idCol <#if !hasNext>lastRow</#if>">
                        <#if prevJobName?has_content>
                            <a href="<@ofbizUrl>scheduledJobDetail?jobId=${job.previousJobId!""}&jobName=${prevJobName!""}</@ofbizUrl>">${job.previousJobId!""}</a>
                        <#else>
                            ${job.previousJobId!""}
                        </#if>
                    </td>
                    <td class="nameCol <#if !hasNext>lastRow</#if>">${job.jobName!""}</td>
                    <#assign statusId=job.statusId >
                    <#assign statusId = statusId?split("_") />
                    <#assign statusId = statusId[1] />
                    <td class="statusCol <#if !hasNext>lastRow</#if>">${statusId!""}</td>
                    
                    <#if job.recurrenceInfoId?exists && job.recurrenceInfoId?has_content>
	                    <#assign recurrenceInfo = delegator.findOne("RecurrenceInfo", {"recurrenceInfoId" : job.recurrenceInfoId}, false)?if_exists/> 
	                    <#if recurrenceInfo?exists && recurrenceInfo?has_content>
	                    	<#assign recurrenceRule = delegator.findOne("RecurrenceRule", {"recurrenceRuleId" : recurrenceInfo.recurrenceRuleId}, false)?if_exists/> 
	                    </#if>
                    </#if>
                    
                    <#if recurrenceRule?exists && recurrenceRule?has_content>
                    	<#assign timeUnit ="" >
                    	<#if recurrenceRule.frequency == "MINUTELY">
                    		<#if (recurrenceRule.intervalNumber &gt; 1)>
                    			<#assign timeUnit = uiLabelMap.Minutes >
                    		<#else>
                    			<#assign timeUnit = uiLabelMap.Minute >
                    		</#if>
                    	</#if>
                    	<#if recurrenceRule.frequency == "HOURLY">
                    		<#if (recurrenceRule.intervalNumber &gt; 1)>
                    			<#assign timeUnit = uiLabelMap.Hours >
                    		<#else>
                    			<#assign timeUnit = uiLabelMap.Hour >
                    		</#if>
                    	</#if>
                    	<#if recurrenceRule.frequency == "DAILY">
                    		<#if (recurrenceRule.intervalNumber &gt; 1)>
                    			<#assign timeUnit = uiLabelMap.Days >
                    		<#else>
                    			<#assign timeUnit = uiLabelMap.Day >
                    		</#if>
                    	</#if>
                    	<#if recurrenceRule.frequency == "WEEKLY">
                    		<#if (recurrenceRule.intervalNumber &gt; 1)>
                    			<#assign timeUnit = uiLabelMap.Weeks >
                    		<#else>
                    			<#assign timeUnit = uiLabelMap.Week >
                    		</#if>
                    	</#if>
                    	<#if recurrenceRule.frequency == "MONTHLY">
                    		<#if (recurrenceRule.intervalNumber &gt; 1)>
                    			<#assign timeUnit = uiLabelMap.Months >
                    		<#else>
                    			<#assign timeUnit = uiLabelMap.Month >
                    		</#if>
                    	</#if>
                    	<#if recurrenceRule.frequency == "YEARLY">
                    		<#if (recurrenceRule.intervalNumber &gt; 1)>
                    			<#assign timeUnit = uiLabelMap.Years >
                    		<#else>
                    			<#assign timeUnit = uiLabelMap.Year >
                    		</#if>
                    	</#if>
                    	
                    	<#if recurrenceRule.countNumber?exists && recurrenceRule.countNumber?has_content>
                    		<#assign runCountNumber ="" >
                    		<#if (recurrenceRule.countNumber &gt; 0)>
                    			<#assign runCountNumber = recurrenceRule.countNumber + " " + uiLabelMap.CommonTime >
                    		</#if>
                    		<#if (recurrenceRule.countNumber == -1)>
                    			<#assign runCountNumber = uiLabelMap.CommonForever >
                    		</#if>
                    	</#if>
                    </#if>
                    
                    <#if recurrenceRule?exists && recurrenceRule?has_content>
                    	<#assign recurrenceRuleInfoString = "">
                    	<#assign rulesMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("intervalNumber", recurrenceRule.intervalNumber, "timeUnit", timeUnit, "runCountNumber", runCountNumber, "serviceNamefromList", job.serviceName!)>
                    	<#assign recurrenceRuleInfoString = recurrenceRuleInfoString + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","ServiceNameInfo",rulesMap, locale )+"</p>">
                    	<#assign recurrenceRuleInfoString = recurrenceRuleInfoString + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","RecurrenceRuleRepeatInfo",rulesMap, locale )+"</p>">
                    	<#assign recurrenceRuleInfoString = recurrenceRuleInfoString + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","RecurrenceRuleCountInfo",rulesMap, locale )+"</p>">
                    <#else>
                    	<#assign recurrenceRuleInfoString = uiLabelMap.NAInfo>
                    </#if>
                    <td class="actionCol">
			        	<p onMouseover="showTooltip(event,'${recurrenceRuleInfoString!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></p>
			        </td> 
			        <#assign recurrenceRule = "" >
			        <#assign recurrenceRuleInfoString = "" >
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.runTime?exists && job.runTime?has_content>${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(job.runTime, preferredDateTimeFormat).toLowerCase())!"N/A"}</#if></td>
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.startDateTime?exists && job.startDateTime?has_content>${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(job.startDateTime, preferredDateTimeFormat).toLowerCase())!"N/A"}</#if></td>
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.finishDateTime?exists && job.finishDateTime?has_content>${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(job.finishDateTime, preferredDateTimeFormat).toLowerCase())!"N/A"}</#if></td>
                    <td class="dateCol <#if !hasNext>lastRow</#if>"><#if job.cancelDateTime?exists && job.cancelDateTime?has_content>${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(job.cancelDateTime, preferredDateTimeFormat).toLowerCase())!"N/A"}</#if></td>
                    <#assign deleteScheduledJobConfirmListText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "DeleteScheduledJobConfirmListText", Static["org.ofbiz.base.util.UtilMisc"].toMap("jobId", "${job.jobId!}"), locale)/>
                    <td class="actionColSmall <#if !hasNext>lastRow</#if>"><a href="javascript:setConfirmDialogContent('${job.jobId}','${deleteScheduledJobConfirmListText}','deleteScheduledJobFromList');javascript:submitDetailForm(document.${detailFormName!""}, 'CF');" onMouseover="showTooltip(event,'${uiLabelMap.ScheduledJobListDeleteInfo}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a></td> 
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
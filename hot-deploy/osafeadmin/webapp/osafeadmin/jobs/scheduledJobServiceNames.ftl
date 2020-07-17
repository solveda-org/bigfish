<!-- drop down for service names for scheduled jobs -->
<#if scheduledJobList?has_content>
	<#list scheduledJobList as scheduledJob>
		<option value='${scheduledJob.key!""}'<#if selectedService == '${scheduledJob.key!""}'>selected=selected</#if>>${scheduledJob.name!""}</option>
	</#list>
</#if>
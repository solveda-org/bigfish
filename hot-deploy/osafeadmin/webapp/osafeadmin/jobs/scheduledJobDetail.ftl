<#if mode?has_content>
	<#if schedJob?exists && schedJob?has_content>
		<#assign jobName = schedJob.jobName!"" />
		<#assign serviceName = schedJob.serviceName!"" />
		<!--remove 'SERVICE_' from beginning of statusId string-->
		<#assign statusId = schedJob.statusId?split("_") />
	    <#assign statusId = statusId[1] />
	    <#assign selectedService = schedJob.serviceName!"" />
	    <#assign selectedTime = runTime!"" />
	<#else>
		<#assign statusId = "PENDING" />
		<#assign selectedService = parameters.SERVICE_NAME!"" />
		<#assign selectedTime = parameters.SERVICE_TIME!"" />
	</#if>
	<#if mode != "add">		
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.JobIdCaption}</label>
	      </div>
	      <div class="infoValue">
	        	${parameters.jobId!jobId!""}
	         	<input name="jobId" type="hidden" id="jobId" value="${parameters.jobId!jobId!""}"/>
	         	<input name="currentId" type="hidden" id="currentId" value="${parameters.jobId!jobId!""}"/>
	      </div>
	    </div>
	    <div class="infoEntry">
	      <div class="infoRowIcon">
	        	<a href="runtimeDataParametersDetail?runtimeDataId=${schedJob.runtimeDataId}&jobId=${parameters.jobId}&jobName=${parameters.jobName}" onMouseover="javascript:showTooltip(event,'${uiLabelMap.ScheduledJobViewRuntimeParamsInfo!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
	      </div>
	    </div>
	  </div>
	</#if>  
	
	<#if mode != "add">		
	  <div class="infoRow column">
	  	<#if schedJob.parentJobId?has_content>
        	<#assign parentJob = delegator.findOne("JobSandbox", {"jobId" : schedJob.parentJobId}, false)?if_exists/> 
        </#if>
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.ParentJobIdCaption}</label>
	      </div>
	      <div class="infoValue">
	      	<#if parentJob?exists && parentJob?has_content>
	        	<a href="<@ofbizUrl>scheduledJobDetail?jobId=${schedJob.parentJobId!""}&jobName=${parentJob.jobName!""}</@ofbizUrl>">${schedJob.parentJobId!""}</a>
	        <#else>
	            ${schedJob.parentJobId!""}
	        </#if>
	      </div>
	    </div>
	  </div>
	</#if>  
	
	<#if mode != "add">		
	  <div class="infoRow column">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.MaxRetryCaption}</label>
	      </div>
	      <div class="infoValue">
	        	${schedJob.maxRetry!""}
	      </div>
	    </div>
	  </div>
	</#if> 
	
	<#if mode != "add">		
	  <div class="infoRow column">
	  	<#if schedJob.previousJobId?has_content>
        	<#assign prevJob = delegator.findOne("JobSandbox", {"jobId" : schedJob.previousJobId}, false)?if_exists/> 
        </#if>
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.PrevJobIdCaption}</label>
	      </div>
	      <div class="infoValue">
	      	<#if prevJob?exists && prevJob?has_content>
	        	<a href="<@ofbizUrl>scheduledJobDetail?jobId=${schedJob.previousJobId!""}&jobName=${prevJob.jobName!""}</@ofbizUrl>">${schedJob.previousJobId!""}</a>
            <#else>
                ${schedJob.previousJobId!""}
	        </#if>
	      </div>
	    </div>
	  </div>
	</#if> 
	
	<#if mode != "add">		
	  <div class="infoRow column">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.LoaderNameCaption}</label>
	      </div>
	      <div class="infoValue">
	        	${schedJob.loaderName!""}
	      </div>
	    </div>
	  </div>
	</#if> 
	
	  <div class="infoRow <#if mode != "add">column</#if>">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.JobNameCaption}</label>
	      </div>
	      <div class="infoValue">
	        	<input name="JOB_NAME" type="text" id="JOB_NAME" value="${jobName!parameters.JOB_NAME!""}"/>
	      </div>
	    </div>
	  </div>
	  
	<#if mode != "add">		
	  <div class="infoRow column">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.RunByInstanceCaption}</label>
	      </div>
	      <div class="infoValue">
	        	${schedJob.runByInstanceId!""}
	      </div>
	    </div>
	  </div>
	</#if> 
	
	  <div class="infoRow <#if mode != "add">column</#if>">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.ServiceNameCaption}</label>
	      </div>
	      <div class="infoValue">
	      		<#if mode == "add">
		      		<select name="SERVICE_NAME" id="SERVICE_NAME">
		        		${screens.render("component://osafeadmin/widget/CommonScreens.xml#scheduledJobServiceNames")}
		        	</select>
		        <#else>
		        	${serviceName!""}
	         		<input name="SERVICE_NAME" type="hidden" id="SERVICE_NAME" value="${serviceName!""}"/>
		        </#if>
	      </div>
	    </div>
	  </div>
	  
	<#if mode != "add">		
	  <div class="infoRow column">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.AuthUserCaption}</label>
	      </div>
	      <div class="infoValue">
	        	${schedJob.authUserLoginId!""}
	      </div>
	    </div>  
	  </div>
	</#if> 
	
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.StatusIdCaption}</label>
	      </div>
	      <div class="infoValue">
	        	${statusId!""}
	         	<input name="jobStatus" type="hidden" id="jobStatus" value="${statusId!""}"/>
	      </div>
	    </div>
	  </div>
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        <label>${uiLabelMap.RunDateTimeCaption}</label>
	      </div>
	      <div class="infoValue ">
	        	<#if statusId == "PENDING">
	        		<input class="dateEntry" type="text" id="SERVICE_DATE" name="SERVICE_DATE" value="${parameters.SERVICE_DATE!runDate!""}"/>
	        	<#else>
		        	${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(schedJob.runTime, preferredDateTimeFormat))!"N/A"}
			    </#if>  
	      </div>  
	    </div>
	  </div>
	  
	  <#if mode!="add">	
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        <label>${uiLabelMap.StartDateTimeCaption}</label>
	      </div>
	      <div class="infoValue ">
	        <div class="entryInput ">
				  ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(schedJob.startDateTime, preferredDateTimeFormat))!"N/A"}
	        </div>
	      </div>
	    </div>
	  </div>
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        <label>${uiLabelMap.FinishDateTimeCaption}</label>
	      </div>
	      <div class="infoValue">
	        <div class="entryInput ">
				   ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(schedJob.finishDateTime, preferredDateTimeFormat))!"N/A"} 
	        </div>
	      </div>
	    </div>
	  </div>
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        <label>${uiLabelMap.CancelDateTimeCaption}</label>
	      </div>
	      <div class="infoValue">
	        <div class="entryInput ">
				  ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(schedJob.cancelDateTime, preferredDateTimeFormat))!"N/A"}
	        </div>
	      </div>  
	    </div>
	  </div>
	 </#if> 
<#else>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>

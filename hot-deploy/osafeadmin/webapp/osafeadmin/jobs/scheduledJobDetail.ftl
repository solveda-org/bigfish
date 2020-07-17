<#if mode?has_content>
	<#if schedJob?exists && schedJob?has_content>
		<#assign jobName = schedJob.jobName!"" />
		<#assign serviceName = schedJob.serviceName!"" />
		<!--remove 'SERVICE_' from beginning of statusId string-->
		<#assign statusId = schedJob.statusId?split("_") />
	    <#assign statusId = statusId[1] />
	    <#assign selectedService = schedJob.serviceName!"" />
	<#else>
		<#assign statusId = "PENDING" />
		<#assign selectedService = parameters.SERVICE_NAME!"" />
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
	  </div>
	</#if>  
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.JobNameCaption}</label>
	      </div>
	      <div class="infoValue">
	        	<input name="JOB_NAME" type="text" id="JOB_NAME" value="${jobName!parameters.JOB_NAME!""}"/>
	      </div>
	    </div>
	  </div>
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.ServiceNameCaption}</label>
	      </div>
	      <div class="infoValue">
	      		<#if mode == "add">
		      		<select name="SERVICE_NAME" id="SERVICE_NAME">
		        	<!--<input name="SERVICE_NAME" type="text" id="SERVICE_NAME" value="${serviceName!parameters.SERVICE_NAME!""}"/>-->
		        	<#include "component://osafeadmin/webapp/osafeadmin/jobs/scheduledJobServiceNames.ftl"/>
		        	</select>
		        <#else>
		        	${serviceName!""}
	         		<input name="SERVICE_NAME" type="hidden" id="SERVICE_NAME" value="${serviceName!""}"/>
		        </#if>
	      </div>
	    </div>
	  </div>
	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.StatusIdCaption}</label>
	      </div>
	      <div class="infoValue">
	        	${statusId!""}
	         	<input name="statusId" type="hidden" id="statusId" value="${statusId!""}"/>
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
	        		<input class="dateEntry" type="text" id="SERVICE_DATE" name="SERVICE_DATE" value="${runDate!parameters.SERVICE_DATE!""}"/>
	        	<#else>
		        	${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(schedJob.runTime, preferredDateTimeFormat))!"N/A"}
			    </#if>  
	      </div>
		    <#if statusId == "PENDING">
		    	  <div>
			        	<label>${uiLabelMap.TimeCaption}</label>
			      </div>
			      <div class="infoValue">
			        <div class="entryInput"> 
			        	<#if statusId == "PENDING"> 
			        		<input class="textEntry" type="text" id="SERVICE_TIME" name="SERVICE_TIME" value="${runTime!parameters.SERVICE_TIME!""}"/>
			        		<#assign selectedAMPM = runTimeAMPM!parameters.SERVICE_AMPM!/>
							<select name="SERVICE_AMPM" id="SERVICE_AMPM">
								<option value='1'<#if selectedAMPM == 'AM'>selected=selected</#if>>${uiLabelMap.CommonAM}</option>
								<option value='2'<#if selectedAMPM == 'PM'>selected=selected</#if>>${uiLabelMap.CommonPM}</option>
						    </select>
					    </#if>               
			        </div>
			      </div>
		    </#if>        
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
  		${uiLabelMap.NoDataAvailableInfo}
</#if>

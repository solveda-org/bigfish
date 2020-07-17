<#if mode?has_content>
		<!-- other hidden fields -->
		<input type="hidden" id="POOL_NAME" name="POOL_NAME" value="pool"/>
		<input type="hidden" name="productStoreId" value="${globalContext.productStoreId!""}" />
		<input type="hidden" name="browseRootProductCategoryId" value="${globalContext.rootProductCategoryId!""}" />
		<input type="hidden" name="emailCount" value="${globalContext.EMAIL_ABANDON_NUM!""}" />
		<input type="hidden" name="intervalHours" value="${globalContext.EMAIL_ABANDON_HRS!""}" />
		<input type="hidden" name="FORMAT_DATE" value="${globalContext.FORMAT_DATE!""}" />
		<#if schedJob?exists && schedJob?has_content>
			<#assign jobName = schedJob.jobName!"" />
			<#assign serviceName = schedJob.serviceName!"" />
			<!--remove 'SERVICE_' from beginning of statusId string-->
			<#assign statusId = schedJob.statusId?split("_") />
            <#assign statusId = statusId[1] />
		<#else>
			<#assign statusId = "PENDING" />
		</#if>
<#if mode != "add">		
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        	<label>${uiLabelMap.JobIdCaption}</label>
      </div>
      <div class="infoValue">
        	${parameters.jobId!jobId!""}
         	<input name="jobId" type="hidden" id="jobId" maxlength="20" value="${parameters.jobId!jobId!""}"/>
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
        	<input name="JOB_NAME" type="text" id="JOB_NAME" maxlength="20" value="${jobName!parameters.JOB_NAME!""}"/>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        	<label>${uiLabelMap.ServiceNameCaption}</label>
      </div>
      <div class="infoValue">
        	<input name="SERVICE_NAME" type="text" id="SERVICE_NAME" maxlength="20" value="${serviceName!parameters.SERVICE_NAME!""}"/>
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
         	<input name="statusId" type="hidden" id="statusId" maxlength="20" value="${statusId!""}"/>
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
        		<input class="dateEntry" type="text" id="SERVICE_DATE" name="SERVICE_DATE" maxlength="40" value="${runDate!parameters.SERVICE_DATE!""}"/>
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
		        		<input class="textEntry" type="text" id="SERVICE_TIME" name="SERVICE_TIME" maxlength="40" value="${runTime!parameters.SERVICE_TIME!""}"/>
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

<#if mode?has_content>
  	<#if recurrenceRule?has_content>
	    <#assign frequency = recurrenceRule.frequency!"" />
	    <#assign intervalNumber = recurrenceRule.intervalNumber!"" />
	    <#assign countNumber = recurrenceRule.countNumber!"" />
	    
	    <#assign selectedFrequency = recurrenceRule.frequency!""/>
    <#else>
    	<#assign selectedFrequency = parameters.SERVICE_FREQUENCY!""/>
    	<#assign frequency = uiLabelMap.CommonNone />
 	</#if>
 	<#if schedJob?exists && schedJob?has_content>
		<#assign jobName = schedJob.jobName!"" />
		<#assign serviceName = schedJob.serviceName!"" />
		<!--remove 'SERVICE_' from beginning of statusId string-->
		<#assign statusId = schedJob.statusId?split("_") />
	    <#assign statusId = statusId[1] />
	<#else>
		<#assign statusId = "PENDING" />
	</#if>

  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.RepeatCaption}</label>
      </div>
      <div class="infoValue">
      	<#if statusId=="PENDING">
	        <select name="SERVICE_FREQUENCY" id="SERVICE_FREQUENCY" class="small intervalUnitSet">
	        	<option value='0' <#if selectedFrequency == "" >selected=selected</#if>>${uiLabelMap.CommonNone}</option>
	        	<option value='4' <#if selectedFrequency == "DAILY" >selected=selected</#if>>${uiLabelMap.CommonDaily}</option>
	        	<option value='5' <#if selectedFrequency == "WEEKLY" >selected=selected</#if>>${uiLabelMap.CommonWeekly}</option>
	        	<option value='6' <#if selectedFrequency == "MONTHLY" >selected=selected</#if>>${uiLabelMap.CommonMonthly}</option>
	        	<option value='7' <#if selectedFrequency == "YEARLY" >selected=selected</#if>>${uiLabelMap.CommonYearly}</option>
	        	<option value='3' <#if selectedFrequency == "HOURLY" >selected=selected</#if>>${uiLabelMap.CommonHourly}</option>
	        	<option value='2' <#if selectedFrequency == "MINUTELY" >selected=selected</#if>>${uiLabelMap.CommonMinutely}</option>
	        </select>
	    <#else>
	    		<span id="SERVICE_FREQUENCYspan">${frequency}</span>
	    		<input name="SERVICE_FREQUENCY" type="hidden" id="SERVICE_FREQUENCY" class="intervalUnitSet" value="${frequency!recurrenceRule.frequency!""}"/>
        </#if>
      </div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.RunEveryCaption}</label>
      </div>
      <div class="infoValue">
        <#if statusId=="PENDING">
        	<input name="SERVICE_INTERVAL" type="text" id="SERVICE_INTERVAL" maxlength="20" class="intervalUnitSet freqdetail" value="${parameters.SERVICE_INTERVAL!intervalNumber!""}"/>
        <#else>
          	${intervalNumber!""}
          	<input name="SERVICE_INTERVAL" type="hidden" id="SERVICE_INTERVAL" class="intervalUnitSet" value="${intervalNumber!""}"/>
        </#if>
      </div>
      <div id="intervalUnit" class="infoValue">${intervalUnit!""}</div>
    </div>
  </div>
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.FreqCountCaption}</label>
      </div>
      <div class="infoValue">
        <#if statusId=="PENDING">
        	<input name="SERVICE_COUNT" type="text" id="SERVICE_COUNT" maxlength="20" class="freqdetail" value="${parameters.SERVICE_COUNT!countNumber!""}"/>
        <#else>
          	${countNumber!""}
          	<input name="SERVICE_COUNT" type="hidden" id="SERVICE_COUNT" maxlength="20" class="freqdetail" value="${countNumber!""}"/>
        </#if>
      </div>
      <#if statusId=="PENDING">
      		<div class="infoValue"><a onMouseover="showTooltip(event,'${uiLabelMap.CountNumberHelperInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a></div>
      </#if>
    </div>
  </div>
    

<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>

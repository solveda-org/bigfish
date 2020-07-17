<#if mode?has_content>
  	<#if recurrenceRule?has_content>
	    <#assign frequencyDisplay = recurrenceRule.frequency!"" />
        <#if  frequencyDisplay = "MINUTELY">
            <#assign frequency = "2" />
            <#assign frequencyDisplay = uiLabelMap.CommonMinutely />
        <#elseif frequencyDisplay = "HOURLY">
            <#assign frequency = "3" />
            <#assign frequencyDisplay = uiLabelMap.CommonHourly />
        <#elseif frequencyDisplay = "DAILY">
            <#assign frequency = "4" />
            <#assign frequencyDisplay = uiLabelMap.CommonDaily />
        <#elseif frequencyDisplay = "WEEKLY">
            <#assign frequency = "5" />
            <#assign frequencyDisplay = uiLabelMap.CommonWeekly />
        <#elseif frequencyDisplay = "MONTHLY">
            <#assign frequency = "6" />
            <#assign frequencyDisplay = uiLabelMap.CommonMonthly />
        <#elseif frequencyDisplay = "YEARLY">
            <#assign frequency = "7" />
            <#assign frequencyDisplay = uiLabelMap.CommonYearly />
        </#if>
	    <#assign intervalNumber = recurrenceRule.intervalNumber!"" />
	    <#assign countNumber = recurrenceRule.countNumber!"" />
    <#else>
    	<#assign frequency = "0" />
        <#assign frequencyDisplay = uiLabelMap.CommonNone />
 	</#if>
    <#assign selectedFrequency = parameters.SERVICE_FREQUENCY!frequency!""/>
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
      <div class="header sub"><h2>${scheduledJobRuleBoxHeading!}</h2></div>
    </div>
	  <div class="infoRow row">
	    <div class="infoEntry long">
	      <div class="infoCaption">
	        <label>${uiLabelMap.RepeatCaption}</label>
	      </div>
	      <div class="infoValue">
	      	<#if statusId=="PENDING">
		        <select name="SERVICE_FREQUENCY" id="SERVICE_FREQUENCY" class="small intervalUnitSet">
		        	<option value='4' <#if selectedFrequency == "4" >selected=selected</#if>>${uiLabelMap.CommonDaily}</option>
		        	<option value='5' <#if selectedFrequency == "5" >selected=selected</#if>>${uiLabelMap.CommonWeekly}</option>
		        	<option value='6' <#if selectedFrequency == "6" >selected=selected</#if>>${uiLabelMap.CommonMonthly}</option>
		        	<option value='7' <#if selectedFrequency == "7" >selected=selected</#if>>${uiLabelMap.CommonYearly}</option>
		        	<option value='3' <#if selectedFrequency == "3" >selected=selected</#if>>${uiLabelMap.CommonHourly}</option>
		        	<option value='2' <#if selectedFrequency == "2" >selected=selected</#if>>${uiLabelMap.CommonMinutely}</option>
		        </select>
		    <#else>
		    		<span id="SERVICE_FREQUENCYspan">${frequencyDisplay!""}</span>
		    		<input name="SERVICE_FREQUENCY" type="hidden" id="SERVICE_FREQUENCY" class="intervalUnitSet" value="${frequency!""}"/>
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
	        	<input name="SERVICE_INTERVAL" type="text" id="SERVICE_INTERVAL" class="intervalUnitSet freqdetail" value="${parameters.SERVICE_INTERVAL!intervalNumber!""}"/>
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
	        	<input name="SERVICE_COUNT" type="text" id="SERVICE_COUNT" class="freqdetail" value="${parameters.SERVICE_COUNT!countNumber!""}"/>
	        <#else>
	          	${countNumber!""}
	          	<input name="SERVICE_COUNT" type="hidden" id="SERVICE_COUNT" class="freqdetail" value="${countNumber!""}"/>
	        </#if>
	      </div>
	      <#if statusId=="PENDING">
	      		<div class="infoValue"><a onMouseover="showTooltip(event,'${uiLabelMap.CountNumberHelperInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a></div>
	      </#if>
	    </div>
	  </div>
<#else>
   <div class="header sub"><h2>${scheduledJobRuleBoxHeading!}</h2></div>
   ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>

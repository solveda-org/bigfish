<script type="text/javascript">
//handle the display of the helper text for the Unit of the frequency interval 
	
//when values are changed, run this:		
jQuery(document).ready(function(){
		var servFreq = jQuery('#SERVICE_FREQUENCY').val();
		if(servFreq=="")
		{
			servFreq = jQuery('#SERVICE_FREQUENCYspan').text();
		}
		var servInter = jQuery('#SERVICE_INTERVAL').val();
		if(servInter=="")
		{
			servInter = jQuery('#SERVICE_INTERVALspan').text();
		}
		var intervalUnit = "";
		if(servFreq != "")
		{
			if(servFreq == "NONE")
			{//not set	
					jQuery("#SERVICE_INTERVAL").prop('disabled', true);	
					jQuery("#SERVICE_INTERVAL").val('');
					jQuery("#SERVICE_COUNT").prop('disabled', true);	
					jQuery("#SERVICE_COUNT").val('');
			}	
			if(servInter != "")
			{
				if(servFreq == "NONE")
				{//not set					
				}
				if(servFreq == "DAILY")
				{
					intervalUnit= "Days";
					if(servInter == "1")
					{
						intervalUnit= "Day";
					}
				}
				if(servFreq == "WEEKLY")
				{
					intervalUnit= "Weeks";
					if(servInter == "1")
					{
						intervalUnit= "Week";
					}
				}
				if(servFreq == "MONTHLY")
				{
					intervalUnit= "Months";
					if(servInter == "1")
					{
						intervalUnit= "Month";
					}
				}
				if(servFreq == "YEARLY")
				{
					intervalUnit= "Years";
					if(servInter == "1")
					{
						intervalUnit= "Year";
					}
				}
				if(servFreq == "HOURLY")
				{
					intervalUnit= "Hours";
					if(servInter == "1")
					{
						intervalUnit= "Hour";
					}
				}
				if(servFreq == "MINUTELY")
				{
					intervalUnit= "Minutes";
					if(servInter == "1")
					{
						intervalUnit= "Minute";
					}
				}
			}
		jQuery("#intervalUnit").text(intervalUnit);
		}	

	
		
	jQuery('.intervalUnitSet').change(function() {
		var servFreq = jQuery('#SERVICE_FREQUENCY').val();
		var servInter = jQuery('#SERVICE_INTERVAL').val();
		var intervalUnit = "";
		if(servFreq != "")
		{
			if(servFreq == "NONE")
			{//not set
					jQuery("#SERVICE_INTERVAL").prop('disabled', true);	
					jQuery("#SERVICE_INTERVAL").val('');
					jQuery("#SERVICE_COUNT").prop('disabled', true);	
					jQuery("#SERVICE_COUNT").val('');
			}	
			if(servFreq != "NONE")
			{//not set
					jQuery("#SERVICE_INTERVAL").prop('disabled', false);	
					jQuery("#SERVICE_COUNT").prop('disabled', false);
			}	
			if(servInter != "")
			{
				if(servFreq == "NONE")
				{//not set					
				}
				if(servFreq == "DAILY")
				{
					intervalUnit= "Days";
					if(servInter == "1")
					{
						intervalUnit= "Day";
					}
				}
				if(servFreq == "WEEKLY")
				{
					intervalUnit= "Weeks";
					if(servInter == "1")
					{
						intervalUnit= "Week";
					}
				}
				if(servFreq == "MONTHLY")
				{
					intervalUnit= "Months";
					if(servInter == "1")
					{
						intervalUnit= "Month";
					}
				}
				if(servFreq == "YEARLY")
				{
					intervalUnit= "Years";
					if(servInter == "1")
					{
						intervalUnit= "Year";
					}
				}
				if(servFreq == "HOURLY")
				{
					intervalUnit= "Hours";
					if(servInter == "1")
					{
						intervalUnit= "Hour";
					}
				}
				if(servFreq == "MINUTELY")
				{
					intervalUnit= "Minutes";
					if(servInter == "1")
					{
						intervalUnit= "Minute";
					}
				}
			}
		jQuery("#intervalUnit").text(intervalUnit);
		}	
	});
});
</script>
<#if mode?has_content>
  <#if recurrenceRule?has_content>
    <#assign frequency = recurrenceRule.frequency!"" />
    <#assign intervalNumber = recurrenceRule.intervalNumber!"" />
    <#assign countNumber = recurrenceRule.countNumber!"" />
    
    <#assign selectedFrequency = recurrenceRule.frequency!""/>
    <#else>
    	<#assign selectedFrequency = parameters.SERVICE_FREQUENCY!""/>
  </#if>

<#-- 
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.RepeatCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode="add">
        	<input name="frequency" type="text" id="frequency" maxlength="20" value="${parameters.frequency!frequency!""}"/>
        <#else>
          	<input name="frequency" type="text" id="frequency" maxlength="20" value="${frequency!""}"/>
        </#if>
      </div>
    </div>
  </div> 
-->  
<#--
//handle the display of the helper text for the Unit of the frequency interval !!CHANGE THIS TO FTL CODE!!!!
//when the page first loads, run this:
-->

  
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.RepeatCaption}</label>
      </div>
      <div class="infoValue">
      	<#if mode=="add" || schedJob.statusId=="SERVICE_PENDING">
	      	<#assign frequencyValues = "NONE,DAILY,WEEKLY,MONTHLY,YEARLY,HOURLY,MINUTELY"/>
	      	<#assign freqValues = frequencyValues?split(",")/>
	        <select name="SERVICE_FREQUENCY" id="SERVICE_FREQUENCY" class="small intervalUnitSet">
	        	<#list freqValues as freq>
	              <option value='${freq!}' <#if selectedFrequency == freq >selected=selected</#if>>${freq?default(parameters.SERVICE_FREQUENCY!freq!)}</option>
	            </#list>   
	        </select>
	    <#else>
	    	<#if recurrenceRule?exists && recurrenceRule?has_content>
	    		<span id="SERVICE_FREQUENCYspan">${recurrenceRule.frequency}</span>
	    		<input name="SERVICE_FREQUENCY" type="hidden" id="SERVICE_FREQUENCY" class="intervalUnitSet" value="${recurrenceRule.frequency!frequency!""}"/>
	    	<#else>
	    		<span>NONE</span>
	    		<input name="SERVICE_FREQUENCY" type="hidden" id="SERVICE_FREQUENCY" class="intervalUnitSet" value="${recurrenceRule.frequency!frequency!""}"/>
	    	</#if>
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
        <#if mode=="add" || schedJob.statusId=="SERVICE_PENDING">
        	<input name="SERVICE_INTERVAL" type="text" id="SERVICE_INTERVAL" maxlength="20" class="intervalUnitSet freqdetail" value="${parameters.SERVICE_INTERVAL!intervalNumber!""}"/>
        <#else>
          	<#if recurrenceRule?exists && recurrenceRule?has_content>
          		${recurrenceRule.intervalNumber!intervalNumber!""}
          		<input name="SERVICE_INTERVAL" type="hidden" id="SERVICE_INTERVAL" class="intervalUnitSet" value="${recurrenceRule.intervalNumber!intervalNumber!""}"/>
          	</#if>
        </#if>
      </div>
      <div id="intervalUnit" class="rightHelperText"> ${intervalUnit!""}</div>
    </div>
  </div>
  
  <div class="infoRow">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.FreqCountCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode=="add" || schedJob.statusId=="SERVICE_PENDING">
        	<input name="SERVICE_COUNT" type="text" id="SERVICE_COUNT" maxlength="20" class="freqdetail" value="${parameters.SERVICE_COUNT!countNumber!""}"/>
        <#else>
          	<#if recurrenceRule?exists && recurrenceRule?has_content>
          		${recurrenceRule.countNumber!countNumber!""}
          		<input name="SERVICE_COUNT" type="hidden" id="SERVICE_COUNT" maxlength="20" class="freqdetail" value="${recurrenceRule.countNumber!countNumber!""}"/>
          	</#if>
        </#if>
      </div>
      <div class="rightHelperText">${uiLabelMap.CountNumberHelperInfo}</div>
    </div>
  </div>
    

<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>

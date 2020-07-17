<!-- hidden input for delete function -->
<input type="hidden" class="confirmHiddenFields" name="currentId" id="currentId" value=""/>
<!-- start searchBox -->
     <div class="entryRow">
      <div class="entry medium">
          <label>${uiLabelMap.JobIdCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="srchJobId" name="srchJobId" maxlength="40" value="${parameters.srchJobId!""}"/>
          </div>
      </div>
      <div class="entry daterange">
          <label>${uiLabelMap.RunDateFromCaption}</label>
          <div class="entryInput from">
                <input class="dateEntry" type="text" name="srchRunDateFrom" maxlength="40" value="${parameters.srchRunDateFrom!srchRunDateFrom!""}"/>
          </div>
          <label class="tolabel">${uiLabelMap.ToCaption}</label>
          <div class="entryInput to">
                <input class="dateEntry" type="text" name="srchRunDateTo" maxlength="40" value="${parameters.srchRunDateTo!srchRunDateTo!""}"/>
          </div>
      </div>
     </div>
     
     
     <div class="entryRow">
      <div class="entry medium">
          <label>${uiLabelMap.JobNameCaption}</label>
          <div class="entryInput">
            <input class="largeTextEntry" type="text" id="srchJobName" name="srchJobName" maxlength="40" value="${parameters.srchJobName!" "}"/>
          </div>
      </div>
      <div class="entry daterange">
          <label>${uiLabelMap.StartDateFromCaption}</label>
          <div class="entryInput from">
                <input class="dateEntry" type="text" name="srchStartDateFrom" maxlength="40" value="${parameters.srchStartDateFrom!srchStartDateFrom!""}"/>
          </div>
          <label class="tolabel">${uiLabelMap.ToCaption}</label>
          <div class="entryInput to">
                <input class="dateEntry" type="text" name="srchStartDateTo" maxlength="40" value="${parameters.srchStartDateTo!srchStartDateTo!""}"/>
          </div>
      </div>
     </div>
     
     
     <div class="entryRow">
      <div class="entry medium">
          <label>${uiLabelMap.ServiceNameCaption}</label>
          <div class="entryInput">
            <input class="largeTextEntry" type="text" id="srchServiceName" name="srchServiceName" maxlength="40" value="${parameters.srchServiceName!""}"/>
          </div>
      </div>
      <div class="entry daterange">
          <label>${uiLabelMap.FinishDateFromCaption}</label>
          <div class="entryInput from">
                <input class="dateEntry" type="text" name="srchEndDateFrom" maxlength="40" value="${parameters.srchEndDateFrom!srchEndDateFrom!""}"/>
          </div>
          <label class="tolabel">${uiLabelMap.ToCaption}</label>
          <div class="entryInput to">
                <input class="dateEntry" type="text" name="srchEndDateTo" maxlength="40" value="${parameters.srchEndDateTo!srchEndDateTo!""}"/>
          </div>
      </div>
     </div>

     <div class="entryRow">
	      <div class="entry long">
	          <label>${uiLabelMap.JobStatusCaption} </label>
	          <#assign intiCb = "${initializedCB}"/>
	          <div class="entryInput checkbox">
	                    <input type="checkbox" class="checkBoxEntry" name="srchall" id="srchall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','srch')" <#if parameters.srchall?has_content>checked</#if> />${uiLabelMap.AllLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="srchCanceled" id="srchCanceled" value="Y" <#if parameters.srchCanceled?has_content || srchCanceled?has_content>checked</#if> />${uiLabelMap.CanceledLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="srchCrashed" id="srchCrashed" value="Y" <#if parameters.srchCrashed?has_content || srchCrashed?has_content>checked</#if> />${uiLabelMap.CrashedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="srchFailed" id="srchFailed" value="Y" <#if parameters.srchFailed?has_content || srchFailed?has_content>checked</#if> />${uiLabelMap.FailedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="srchFinished" id="srchFinished" value="Y" <#if parameters.srchFinished?has_content || srchFinished?has_content>checked</#if> />${uiLabelMap.FinishedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="srchPending" id="srchPending" value="Y" <#if parameters.srchPending?has_content || srchPending?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.PendingLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="srchQueued" id="srchQueued" value="Y" <#if parameters.srchQueued?has_content || srchQueued?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.QueuedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="srchRunning" id="srchRunning" value="Y" <#if parameters.srchRunning?has_content || srchRunning?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.RunningLabel}
	          </div>
	     </div>
     </div>
    
<!-- end searchBox -->


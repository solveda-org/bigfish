

<!-- start searchBox -->
     <div class="entryRow">
      <div class="entry short">
          <label>${uiLabelMap.CustomerNoCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="partyId" name="partyId" maxlength="40" value="${parameters.partyId!""}"/>
          </div>
      </div>
      <div class="entry short">
          <label>${uiLabelMap.UserLoginCaption}</label>
          <div class="entryInput">
            <input class="textEntry emailEntry long" type="text" id="partyUserLoginId" name="partyUserLoginId" maxlength="40" value="${parameters.partyUserLoginId!""}"/>
          </div> 
     </div>
     </div>
     <div class="entryRow">
      <div class="entry short">
          <label>${uiLabelMap.CustomerNameCaption}</label>
          <div class="entryInput">
                <input class="textEntry nameEntry" type="text" id="partyName" name="partyName" maxlength="50" value="${parameters.partyName!""}"/>
          </div>
      </div>
      <div class="entry short">
          <label>${uiLabelMap.EmailCaption}</label>
          <div class="entryInput">
                <input class="textEntry emailEntry long" type="text" id="partyEmail" name="partyEmail" maxlength="40" value="${parameters.partyEmail!""}"/>
          </div>
      </div>
     </div>
     <div class="entryRow">
	      <div class="entry.medium">
	          <label>${uiLabelMap.CustomerStatusCaption}</label>
	          <#assign intiCb = "${initializedCB}"/>
	          <div class="entryInput checkbox medium">
	                    <input type="checkbox" class="checkBoxEntry" name="statusall" id="statusall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','status')" <#if parameters.statusall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="statusEnabled" id="statusEnabled" value="Y" <#if parameters.statusEnabled?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.CustomerEnabledCaption}
	                    <input type="checkbox" class="checkBoxEntry" name="statusDisabled" id="statusDisabled" value="Y" <#if parameters.statusDisabled?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.CustomerDisabledCaption}
	          </div>
	     </div>
     </div>
     <div class="entryRow">
	      <div class="entry long">
	          <label>${uiLabelMap.CustomerRoleCaption}</label>
	          <div class="entryInput checkbox medium">
	              <input type="checkbox" class="checkBoxEntry" name="roleall" id="roleall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','role')" <#if parameters.roleall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
	              <#assign roleTypes = sessionAttributes.roleTypes?if_exists>
	              <#list roleTypes as roleType>
	                  <#if roleType.description?has_content>
	                      <#assign description = roleType.description?lower_case />
	                  </#if>
	                  <input type="checkbox" class="checkBoxEntry" name="role${description!}" id="role${description!}" value="Y" <#if parameters.get('role${description!}')?has_content || context.get('role${description!}')?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${roleType.description!}
	              </#list>
	              <#-- 
	                    
	                    <input type="checkbox" class="checkBoxEntry" name="roleCustomerId" id="roleCustomerId" value="Y" <#if parameters.roleCustomerId?has_content || roleCustomerId?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.CustomerCaption}
	                    <input type="checkbox" class="checkBoxEntry" name="roleEmailId" id="roleEmailId" value="Y" <#if parameters.roleEmailId?has_content || roleEmailId?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.EmailSubscriberCaption}
	                    <input type="checkbox" class="checkBoxEntry" name="roleGuestId" id="roleGuestId" value="Y" <#if parameters.roleGuestId?has_content || roleGuestId?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.GuestCaption}
	               -->
	          </div>
	     </div>
     </div>
     <div class="entryRow">
	      <div class="entry long">
	          <label>${uiLabelMap.ExportStatusCaption}</label>
	          <div class="entryInput checkbox medium">
	                    <input type="checkbox" class="checkBoxEntry" name="downloadall" id="downloadall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','download')" <#if parameters.downloadall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="downloadnew" id="downloadnew" value="Y" <#if parameters.downloadnew?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.NewLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="downloadloaded" id="downloadloaded" value="Y" <#if parameters.downloadloaded?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.ExportedLabel}
	          </div>
	     </div>
     </div>
<!-- end searchBox -->


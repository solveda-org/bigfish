<!-- start searchBox -->
	<#assign selectedGroup = parameters.searchGroupId!""/>
     <div class="entryRow">
          <label>${uiLabelMap.UserLoginIdCaption}</label>
          <div class="entryInput">
            <input class="textEntry nameEntry" type="text" id="srchUserLoginId" name="srchUserLoginId" value="${parameters.srchUserLoginId!""}"/>
          </div>
     </div>
     <div class="entryRow">
          <label>${uiLabelMap.SecurityGroupCaption}</label>
          <div class="entryInput">
                <select name="searchGroupId" id="searchGroupId" >
		          <#if securityGroups?has_content>
		          	<option value='' <#if selectedGroup == '' >selected</#if>></option>
		            <#list securityGroups as securityGroup>
		              <option value='${securityGroup.groupId!}' <#if selectedGroup == securityGroup.groupId >selected</#if>>${securityGroup.groupId!}</option>
		            </#list>
		          </#if>
		        </select>
          </div>
     </div>
     
<!-- end searchBox -->


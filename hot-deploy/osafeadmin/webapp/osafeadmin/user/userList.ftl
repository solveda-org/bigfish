<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.UserLoginIdLabel}</th>
                <th class="nameCol">${uiLabelMap.SystemLabel}</th>
                <th class="nameCol">${uiLabelMap.EnabledLabel}</th>
                <th class="nameCol">${uiLabelMap.PasswordChangeLabel}</th>
                <th class="dateCol">${uiLabelMap.DisabledDateLabel}</th>
                <th class="actionCol">${uiLabelMap.ActionsLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <!-- check if this user has been removed/expired from the selected securityGroup by checking the thruDate -->
            <#assign securityGroupExpired = "false">
            <#list resultList as userRow>
            	<#if searchGroupId?exists && searchGroupId?has_content>
            		<#assign userLoginSecurityGroups = delegator.findByAnd("UserLoginSecurityGroup", {"userLoginId" : userRow.userLoginId, "groupId" : searchGroupId})>
            		<#if userLoginSecurityGroup?has_content>
	            		<#list userLoginSecurityGroups as userLoginSecurityGroup>
	            			<#if userLoginSecurityGroup.thruDate?exists && userLoginSecurityGroup.thruDate?has_content >
		            			<#assign nowTimeStamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
			            		<#if nowTimeStamp.after(userRow.thruDate)>
			            			<#assign securityGroupExpired = "true">
			            		</#if>
	            			</#if>
	            		</#list>
            		</#if>
            	</#if>
            	<#if securityGroupExpired="false">
            		<!-- build string with all security groups to be used as toolTip text in action column -->
            		<#assign userGroups = delegator.findByAnd("UserLoginSecurityGroup", {"userLoginId" : userRow.userLoginId})>
            		<#assign groupIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(userGroups, "groupId", true)>
            		<#assign groupIdsString = "">
            		<#list groupIds as groupIdString>
            			<#assign hasNextGroup = groupIdString_has_next>
            			<#if hasNextGroup>
            				<#assign groupIdsString = groupIdsString + groupIdString + ", ">
            			<#else>
            				<#assign groupIdsString = groupIdsString + groupIdString>
            			</#if>
            		</#list>
            		
            		<#if groupIdsString == "">
            			<#assign groupIdsString = uiLabelMap.CommonNone>
            		</#if>
            		<#assign groupIdsStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("groupIdsString", groupIdsString)>
            		<#assign userLoginGroupToolTipText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","UserSecurityGroupHelpInfo",groupIdsStringMap, locale ) />

	            	<#assign hasNext = userRow_has_next>
	                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
		                <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>userDetail?userLoginId=${userRow.userLoginId}</@ofbizUrl>">${userRow.userLoginId}</a></td>
		                <td class="nameCol <#if !hasNext>lastRow</#if>">${userRow.isSystem!""}</td>
		                <td class="nameCol <#if !hasNext>lastRow</#if>">${userRow.enabled!""}</td>
		                <td class="nameCol <#if !hasNext>lastRow</#if>">${userRow.requirePasswordChange!""}</td>
		                <td class="dateCol <#if !hasNext>lastRow</#if>"><#if userRow.disabledDateTime?exists && userRow.disabledDateTime?has_content>${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(userRow.disabledDateTime, preferredDateFormat).toLowerCase())!""}</#if></td> 
		                <td class="actionCol">
		                 <a href="<@ofbizUrl>userSecurityGroupList?userLoginId=${userRow.userLoginId}</@ofbizUrl>" onMouseover="showTooltip(event,'${userLoginGroupToolTipText!}');" onMouseout="hideTooltip()"><span class="helperInfoIcon"></span></a>
		                </td> 		                
	                </tr>
                </#if>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </#if>
<!-- end listBox -->
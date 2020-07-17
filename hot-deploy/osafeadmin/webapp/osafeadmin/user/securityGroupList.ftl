<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.SecurityGroupIdLabel}</th>
                <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
                <th class="actionCol">${uiLabelMap.ActionsLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <!-- check if this user has been removed/expired from the selected securityGroup -->
            <#assign securityGroupExpired = "false">
            <#list resultList as secGroupRow>
	            	<#assign hasNext = secGroupRow_has_next>
	                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
		                <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="<@ofbizUrl>securityGroupDetail?groupId=${secGroupRow.groupId}</@ofbizUrl>">${secGroupRow.groupId}</a></td>
		                <td class="descCol <#if !hasNext>lastRow</#if>">${secGroupRow.description!""}</td>
		                <td class="actionCol">
				          <div class="actionIconMenu">
				            <a class="toolIcon" href="javascript:void(o);"></a>
				            <div class="actionIconBox" style="display:none">
				            <div class="actionIcon">
				            <ul>
				                <li><a href="<@ofbizUrl>userManagement?searchGroupId=${secGroupRow.groupId}&preRetrieved=Y</@ofbizUrl>"><span class="allUsersIcon"></span>${uiLabelMap.SecurityGroupShowUsersHelpInfo!}</a></li>
				                <li><a href="<@ofbizUrl>securityGroupPermissionList?groupId=${secGroupRow.groupId}</@ofbizUrl>"><span class="allPermissionsIcon"></span>${uiLabelMap.SecurityGroupShowPermissionsHelpInfo}</a></li>
					        </ul>
					       </div>
					       </div>
					      </div>             
		                </td> 
	                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </#if>
<!-- end listBox -->
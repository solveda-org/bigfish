<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.PermissionIdLabel}</th>
                <th class="nameCol">${uiLabelMap.DescriptionLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <!-- check if this user has been removed/expired from the selected securityGroup -->
            <#assign securityGroupExpired = "false">
            <#list resultList as userRow>
            	<#if groupId?exists && groupId?has_content>
            	<!-- TODO: userRow does not have thruDate .. fix this -->
	            	<#if userRow.thruDate?exists && userRow.thruDate?has_content >
	            		<#assign nowTimeStamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
	            		<#if nowTimeStamp.after(userRow.thruDate)>
	            			<#assign securityGroupExpired = "true">
	            		</#if>
	            	</#if>
            	</#if>
            	<#if securityGroupExpired="false">
            		<!-- TODO: build string with all security groups to be used as toolTip text in action column -->
	            	<#assign hasNext = userRow_has_next>
	                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
		                <td class="nameCol <#if !hasNext>lastRow</#if>">${userRow.permissionId!""}</td>
		                <td class="nameCol <#if !hasNext>lastRow</#if>">${userRow.description!""}</td>
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
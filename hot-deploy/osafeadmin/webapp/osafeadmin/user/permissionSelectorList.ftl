<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.PermissionIdLabel}</th>
                <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as permissionRow>
            		<!-- TODO: build string with all security groups to be used as toolTip text in action column -->
	            	<#assign hasNext = permissionRow_has_next>
	                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
		                <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="javascript:set_values('${permissionRow.permissionId?if_exists}', '${permissionRow.description?if_exists}')">${permissionRow.permissionId?if_exists}</a></td>
		                <td class="descCol <#if !hasNext>lastRow</#if>">${permissionRow.description!""}</td>
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
<#if displayAlerts?has_content && displayAlerts == "Y">
	<!-- start displayBox -->
	<div class="displayBox alerts">
	    <div class="header"><h2>${uiLabelMap.DashboardAlertsHeading}</h2></div>
	    <div class="boxBody">
	        <table class = "osafe">
	        <#if alertList?has_content>
	            <#assign rowClass = "1">
	            <#list alertList as alertMap>
	            	<#if alertMap.sequenceNum?exists && alertMap.sequenceNum &gt; 0>
		                <tr class="<#if rowClass == "2">even</#if>">
		                	<td class="nameCol <#if !alertMap_has_next?if_exists>lastRow</#if>"><span class="${alertMap.iconClass}"></span></td>
		                    <td class="nameCol <#if !alertMap_has_next?if_exists>lastRow</#if> firstCol" >
		                    	<p><span>${alertMap.infoMessage!""}</span></p></br>
		                    	<p><span>${alertMap.actionPath!""} <a href="<@ofbizUrl>${alertMap.action}</@ofbizUrl>">${alertMap.actionLabel!""}</a></span></p></br>
		                    	<p><span>${alertMap.actionInfo!""}</span></p>
		                    </td>
		                </tr>
		                <#-- toggle the row color -->
		                <#if rowClass == "2">
		                    <#assign rowClass = "1">
		                <#else>
		                    <#assign rowClass = "2">
		                </#if>
	                </#if>
	            </#list>      
	        </#if>
	        </table>
	    </div>
	</div>
	<!-- end displayBox -->
</#if>
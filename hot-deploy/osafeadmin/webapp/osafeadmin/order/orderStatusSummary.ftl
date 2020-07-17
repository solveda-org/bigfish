<form name="OrderStatusSummaryForm" >
	<input name="preRetrieved" type="hidden" value="Y"/>
	<input name="initializedCB" type="hidden" value="Y"/>
	<input name="viewIndex" type="hidden" value="1"/>
	<input name="showPagesLink" type="hidden" value="Y"/>
	<input name="statusId" type="hidden" class="confirmHiddenParamStatusId" value=""/>
	<input name="viewSize" type="hidden" class="confirmHiddenParamViewSize" value=""/>
</form>
          <thead>
            <tr class="heading">
                <th class="nameCol">${uiLabelMap.StatusLabel}</th>
                <th class="qtyCol">${uiLabelMap.OrderCountLabel}</th>
            </tr>
          </thead>
        <#if ordersRequiringWork?exists && ordersRequiringWork?has_content>
            <#assign rowClass = "1">
            <#list ordersRequiringWork as workRow>
	            <#assign description = workRow.description!"">
	            <#assign count = workRow.count!"">
	            <#assign statusId = workRow.statusId!"">
	            <#assign adm_DEF_LIST_ROWS = ADM_DEF_LIST_ROWS!"">
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
                    <td class="nameCol <#if !workRow_has_next>lastRow</#if>"><#if description?has_content>${description}</#if></td>
                    <td class="qtyCol <#if !workRow_has_next>lastRow</#if> lastCol">
                        <#if count != 0>
                        	<#if (count &gt; adm_DEF_LIST_ROWS?number)>
                        		<a href="javascript:setParamsForList('${statusId}','${count}');javascript:setConfirmDialogContent('','${uiLabelMap.ShowAllError}','orderManagement');javascript:submitDetailForm('showAllForm', 'CF');">${count}</a>
                        	<#else>
                        		<a href="<@ofbizUrl>orderManagement?statusId=${statusId}&initializedCB=Y&preRetrieved=Y</@ofbizUrl>">${count}</a>
                        	</#if>
                        <#else>
                        	${count}
                        </#if>
                    </td>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
            <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">                
                    <td class="nameCol lastRow total" >${uiLabelMap.CommonTotalLabel!""}</td>
                    <td class="qtyCol lastRow lastCol total">${total}</td>
             </tr>
        </#if>





<!-- start listBox -->
          <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.ManufacturerIdShortLabel}</th>
                <th class="descCol">${uiLabelMap.NameLabel}</th>
            </tr>
          </thead>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as manufacturerRow>
	            	<#assign hasNext = manufacturerRow_has_next>
	                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">    
		                <td class="idCol <#if !hasNext>lastRow</#if> firstCol" ><a href="javascript:set_values('${manufacturerRow.partyId?if_exists}', '${manufacturerRow.groupName?if_exists}')">${manufacturerRow.partyId?if_exists}</a></td> 
		                <td class="descCol <#if !hasNext>lastRow</#if>">${manufacturerRow.groupName!""}</td>
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
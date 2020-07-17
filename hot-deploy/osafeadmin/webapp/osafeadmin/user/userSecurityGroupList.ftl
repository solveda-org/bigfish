<table class="osafe" id="userSecurityGroup">
  <thead>
    <tr class="heading">
    	<th class="idCol firstCol">${uiLabelMap.SecurityGroupIdLabel}</th>
    	<th class="actionCol">${uiLabelMap.ActionsLabel}</th>
    </tr>
  </thead>
  <tbody>
    <#assign rowClass = "1"/>
    <#assign rowNo = 1/>
    <input type="hidden" name="userLoginId" id="userLoginId" value="${parameters.userLoginId!}"/>
    <input type="hidden" name="rowNo" id="rowNo"/>
    <#if resultList?exists && resultList?has_content>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!resultList?size}"/>
    <#else>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!}"/>
    </#if>
    <input type="hidden" name="addGroupId" id="addGroupId" onchange="addGroupRow('userSecurityGroup');"/>
    <input type="hidden" name="addGroupDescription" id="addGroupDescription" />
    <#if resultList?exists && resultList?has_content && !parameters.totalRows?exists>
      <#list resultList as secGroupRow>
        <#assign rowSeq = rowNo * 10>
         <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>"> 
           <input type="hidden" name="relatedGroupId_${rowNo}" id="relatedGroupId" value="${secGroupRow.groupId!}"/>    
           <td class="idCol firstCol">${secGroupRow.groupId!""}</td>
           <td class="actionCol">
             <a href="javascript:setRowNo('${rowNo}');javascript:deleteGroupTableRow('${secGroupRow.groupId!?if_exists}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.UserSecurityGroupDeleteHelpInfo}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo}');javascript:openLookup(document.${detailFormName!}.addGroupId,document.${detailFormName!}.addGroupDescription,'lookupSecurityGroup','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.UserSecurityGroupInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo+1}');javascript:openLookup(document.${detailFormName!}.addGroupId,document.${detailFormName!}.addGroupDescription,'lookupSecurityGroup','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.UserSecurityGroupInsertAfterHelpInfo}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>           </td> 
        </tr>
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
        <#assign rowNo = rowNo+1/>
      </#list>



    <#elseif parameters.totalRows?exists>
      <#assign minRow = parameters.totalRows?number/>
      <#list 1..minRow as x>
       <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>"> 
       		<#assign relatedGroupId = request.getParameter("relatedGroupId_${x}")!/>

           <input type="hidden" name="relatedGroupId_${x}" id="relatedGroupId" value="${relatedGroupId!}"/>
           <td class="idCol firstCol">${relatedGroupId!""}</td>           
           <td class="actionCol">
             <a href="javascript:setRowNo('${x}');javascript:deleteGroupTableRow('${relatedGroupId?if_exists}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.UserSecurityGroupDeleteHelpInfo}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${x}');javascript:openLookup(document.${detailFormName!}.addGroupId,document.${detailFormName!}.addGroupDescription,'lookupSecurityGroup','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.UserSecurityGroupInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${x+1}');javascript:openLookup(document.${detailFormName!}.addGroupId,document.${detailFormName!}.addGroupDescription,'lookupSecurityGroup','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.UserSecurityGroupInsertAfterHelpInfo}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td>
        </tr> 
        <#if rowClass == "2">
          <#assign rowClass = "1">
        <#else>
          <#assign rowClass = "2">
        </#if>
      </#list>
    </#if>
    
    <tr class="dataRow" id="addIconRow" <#if (resultList?exists && resultList?has_content) || (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
      <td colspan="1">&nbsp;</td>
      <td class="actionCol">
        <span class="noAction"></span>
        <a href="javascript:setRowNo('1');javascript:openLookup(document.${detailFormName!}.addGroupId,document.${detailFormName!}.addGroupDescription,'lookupSecurityGroup','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
        <span class="noAction"></span>      </td>
    </tr>
  </tbody>
</table>
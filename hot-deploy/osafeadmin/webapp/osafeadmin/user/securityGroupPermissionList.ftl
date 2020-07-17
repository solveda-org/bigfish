<table class="osafe" id="securityGroupPermission">
  <thead>
    <tr class="heading">
    	<th class="idCol firstCol">${uiLabelMap.PermissionIdLabel}</th>
        <th class="actionCol">${uiLabelMap.ActionsLabel}</th>
    </tr>
  </thead>
  <tbody>
    <#assign rowClass = "1"/>
    <#assign rowNo = 1/>
    <input type="hidden" name="groupId" id="groupId" value="${parameters.groupId!}"/>
    <input type="hidden" name="rowNo" id="rowNo"/>
    <#if resultList?exists && resultList?has_content>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!resultList?size}"/>
    <#else>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!}"/>
    </#if>
    <input type="hidden" name="addPermissionId" id="addPermissionId" onchange="addPermissionRow('securityGroupPermission');"/>
    <input type="hidden" name="addPermissionDescription" id="addPermissionDescription" />
    <#if resultList?exists && resultList?has_content && !parameters.totalRows?exists>
      <#list resultList as permission>
        <#assign rowSeq = rowNo * 10>
         <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>"> 
           <input type="hidden" name="relatedPermissionId_${rowNo}" id="relatedPermissionId" value="${permission.permissionId!}"/>    
           <td class="idCol firstCol">${permission.permissionId!""}</td>
           <td class="actionCol">
             <a href="javascript:setRowNo('${rowNo}');javascript:deletePermissionTableRow('${permission.permissionId!?if_exists}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionDeleteHelpInfo}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo}');javascript:openLookup(document.${detailFormName!}.addPermissionId,document.${detailFormName!}.addPermissionDescription,'lookupPermission','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo+1}');javascript:openLookup(document.${detailFormName!}.addPermissionId,document.${detailFormName!}.addPermissionDescription,'lookupPermission','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertAfterHelpInfo}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td> 
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
       		<#assign relatedPermissionId = request.getParameter("relatedPermissionId_${x}")!/>

           <input type="hidden" name="relatedPermissionId_${x}" id="relatedPermissionId" value="${relatedPermissionId!}"/>

           <td class="idCol firstCol">${relatedPermissionId!""}</td>           
   
           <td class="actionCol">
             <a href="javascript:setRowNo('${x}');javascript:deletePermissionTableRow('${relatedPermissionId?if_exists}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionDeleteHelpInfo}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${x}');javascript:openLookup(document.${detailFormName!}.addPermissionId,document.${detailFormName!}.addPermissionDescription,'lookupPermission','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${x+1}');javascript:openLookup(document.${detailFormName!}.addPermissionId,document.${detailFormName!}.addPermissionDescription,'lookupPermission','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertAfterHelpInfo}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
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
        <a href="javascript:setRowNo('1');javascript:openLookup(document.${detailFormName!}.addPermissionId,document.${detailFormName!}.addPermissionDescription,'lookupPermission','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
        <span class="noAction"></span>
      </td>
    </tr>
  </tbody>
</table>
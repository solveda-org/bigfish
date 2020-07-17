    <#if permissions?has_content>
         <tr class="dataRow">
           <input type="hidden" name="relatedPermissionId_${rowNo}" id="relatedPermissionId" value="${permissions.permissionId!}"/>    
           <td class="idCol firstCol">${permissions.permissionId!""}</td>
           <td class="actionCol">
             <a href="javascript:setRowNo('${rowNo}');javascript:deletePermissionTableRow('${permissions.permissionId!?if_exists}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionDeleteHelpInfo}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo}');javascript:openLookup(document.${detailFormName!}.addPermissionId,document.${detailFormName!}.addPermissionDescription,'lookupPermission','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('${rowNo+1}');javascript:openLookup(document.${detailFormName!}.addPermissionId,document.${detailFormName!}.addPermissionDescription,'lookupPermission','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertAfterHelpInfo}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td>
         </tr>
     </#if>

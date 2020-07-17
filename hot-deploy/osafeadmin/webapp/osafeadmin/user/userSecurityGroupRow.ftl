    <#if securityGroupInfo?has_content>
         <tr class="dataRow">
           <input type="hidden" name="relatedGroupId_" id="relatedGroupId" value="${securityGroupInfo.groupId!}"/>
           <td class="idCol firstCol" >${securityGroupInfo.groupId?if_exists}</a></td> 
           <td class="actionCol">
             <a href="javascript:setRowNo('');javascript:deleteGroupTableRow('${securityGroupInfo.groupId?if_exists}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionDeleteHelpInfo}');" onMouseout="hideTooltip()" ><span class="crossIcon"></span></a>
             <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.addGroupId,document.${detailFormName!}.addGroupDescription,'lookupSecurityGroup','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertBeforeHelpInfo}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
             <a href="javascript:setRowNo('');javascript:openLookup(document.${detailFormName!}.addGroupId,document.${detailFormName!}.addGroupDescription,'lookupSecurityGroup','500','700','center','true');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.SecurityGroupPermissionInsertAfterHelpInfo}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
           </td>
         </tr>
     </#if>

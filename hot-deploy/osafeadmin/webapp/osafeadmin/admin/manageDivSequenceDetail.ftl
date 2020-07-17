<#if resultScreenList?has_content>
    <input type="hidden" name="screenType" value=${parameters.screenType!screenType}></input>
    <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="nameCol firstCol">${uiLabelMap.KeyLabel}</th>
           <th class="descCol">${uiLabelMap.DescriptionLabel}</th>
           <th class="valueCol">${uiLabelMap.StyleLabel}</th>
           <th class="radioCol">${uiLabelMap.MandatoryLabel}
           <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.MandatoryHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
           </th>
           <th class="seqCol">${uiLabelMap.SeqNumberLabel}
           <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SeqIdHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
           </th> 
           <th class="numberCol">${uiLabelMap.GroupNumberLabel}
             <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.GroupHelperInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
           </th>
         </tr>
       </thead>
       <tbody>
            
            <#assign rowClass = "1">
            <#list resultScreenList  as screenList>
                <#assign hasNext = screenList_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                       <a href="<@ofbizUrl>manageDivSeqItemDetail?key=${screenList.key?if_exists}&amp;screenType=${screenList.screen?if_exists}</@ofbizUrl>">${screenList.key!}</a>
                       <input type="hidden" name="key_${screenList_index}" value="${screenList.key!}"></input>
                     </td>
                     <td class="descCol <#if !hasNext>lastRow</#if>">
                       ${screenList.description!}
                       <input type="hidden" name="description_${screenList_index}" value="${screenList.description!""}"></input>
                     </td>
                     <td class="valueCol <#if !hasNext>lastRow</#if>">
                       ${screenList.style!}
                       <input type="hidden" name="style_${screenList_index}" value="${screenList.style!""}"></input>
                     </td>
                     <td class="radioCol <#if !hasNext>lastRow</#if>">
                         <#if screenList.mandatory?has_content && (screenList.mandatory == "YES" || screenList.mandatory == "NO")>
                             <span class="radiobutton">
                                 <#assign mandatory = request.getParameter("mandatory_${screenList_index}")!screenList.mandatory!''/>
                                 <input type="radio" name="mandatory_${screenList_index}" value="YES" <#if mandatory == "YES">checked="checked"</#if>/>${uiLabelMap.YesLabel}
                                 <input type="radio" name="mandatory_${screenList_index}" value="NO" <#if mandatory == "NO">checked="checked"</#if>/>${uiLabelMap.NoLabel}
                             </span>
                         <#else>
                             ${screenList.mandatory!}
                             <input type="hidden" name="mandatory_${screenList_index}" value="${screenList.mandatory!""}"></input>
                         </#if>
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                         <input type="text" name="value_${screenList_index}" class="small" id="seqNo" value="${parameters.get("value_${screenList_index}")!screenList.value}" ></input>
                     </td>
                     <td class="numberCol <#if !hasNext>lastRow</#if>">
                         <input type="text" name="group_${screenList_index}" class="small" id="group_${screenList_index}" value="${parameters.get("group_${screenList_index}")!screenList.group!}" ></input>
                     </td>
                     <input type="hidden" name="screen_${screenList_index}" value="${screenList.screen!}"></input>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
        </tbody>
        </table>
        <input type="hidden" name="showDetail" value="false"/>
    <#else>
        ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
    </#if>

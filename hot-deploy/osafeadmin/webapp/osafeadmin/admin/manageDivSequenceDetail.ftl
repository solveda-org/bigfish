<#if resultScreenList?has_content>
    <input type="hidden" name="screenType" value=${parameters.screenType!screenType}></input>
    <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="nameCol firstCol">${uiLabelMap.DivTagLabel}</th>
           <th class="descCol">${uiLabelMap.DescLabel}</th>
           <th class="seqCol">${uiLabelMap.SeqNumberLabel}
           <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SeqIdHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
           </th> 
           <#if parameters.screenType?exists && PDPTabsScreenType?exists  && parameters.screenType == PDPTabsScreenType>
               <th class="numberCol">${uiLabelMap.GroupNumberLabel}
               <div class="infoIcon">
                 <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.PdpTabsGroupHelperInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
               </div>
               </th>
           </#if>
         </tr>
       </thead>
       <tbody>
            
            <#assign rowClass = "1">
            <#list resultScreenList  as screenList>
                <#assign hasNext = screenList_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                       <a href="<@ofbizUrl>manageDivSeqItemDetail?key=${screenList.key?if_exists}&amp;screen=${screenList.screen?if_exists}</@ofbizUrl>">${screenList.key!}</a>
                       <input type="hidden" name="key_${screenList_index}" value="${screenList.key!}"></input>
                     </td>
                     <td class="descCol <#if !hasNext>lastRow</#if>">
                       ${screenList.description!}
                       <input type="hidden" name="description_${screenList_index}" value="${screenList.description!""}"></input>
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                     <#if screenList.value?has_content>
                       <input type="text" name="value_${screenList_index}" class="small" id="seqNo" value="${parameters.get("value_${screenList_index}")!screenList.value}" ></input>
                     </#if>
                     </td>
                     <#if parameters.screenType?exists && PDPTabsScreenType?exists && parameters.screenType == PDPTabsScreenType>
                         <td class="numberCol <#if !hasNext>lastRow</#if>">
                             <#if screenList.group?has_content>
                                 <input type="text" name="group_${screenList_index}" class="small" id="group_${screenList_index}" value="${parameters.get("group_${screenList_index}")!screenList.group!}" ></input>
                             </#if>
                         </td>
                     </#if>
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
        ${uiLabelMap.NoDataAvailableInfo}
    </#if>

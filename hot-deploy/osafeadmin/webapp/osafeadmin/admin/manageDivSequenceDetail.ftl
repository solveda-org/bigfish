<#if resultScreenList?has_content>
    <input type="hidden" name="screenType" value=${parameters.screenType!screenType}></input>
    <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="nameCol firstCol">${uiLabelMap.DivTagLabel}</th>
           <th class="descCol">${uiLabelMap.DescLabel}</th>
           <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th> 
           <#if parameters.screenType?exists && parameters.screenType == 'PDPTabs'>
               <th class="numberCol">${uiLabelMap.GroupNumberLabel}</th>
           </#if>
         </tr>
       </thead>
       <tbody>
            
            <#assign rowClass = "1">
            <#assign rowNo = 1/>   
            <#assign seqNo = 1/> 
            <#list resultScreenList  as screenList>
                <#assign hasNext = screenList_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                       <a href="<@ofbizUrl>manageDivSeqItemDetail?key=${screenList.key?if_exists}&amp;screen=${screenList.screen?if_exists}</@ofbizUrl>">${screenList.key!}</a>
                       <input type="hidden" name="key_${rowNo}" value="${screenList.key!}"></input>
                     </td>
                     <td class="descCol <#if !hasNext>lastRow</#if>">
                       ${screenList.description!}
                       <input type="hidden" name="description_${rowNo}" value="${screenList.description!""}"></input>
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                     <#if screenList.value?has_content && screenList.value != 0?int>
                     <#assign rowSeq = seqNo * 10>
                     <#assign seqNo = seqNo+1/>
                       <input type="text" name="value_${rowNo}" class="small" id="seqNo" value="${parameters.get("value_${rowNo}")!rowSeq}" ></input>
                     <#else>
                       <input type="text" name="value_${rowNo}" class="small" id="seqNo" value="${parameters.get("value_${rowNo}")!"0"}" ></input>
                     </#if>
                     </td>
                     <#if parameters.screenType?exists && parameters.screenType == 'PDPTabs'>
                         <td class="numberCol <#if !hasNext>lastRow</#if>">
                             <input type="text" name="group_${rowNo}" class="small" id="group_${rowNo}" value="${parameters.get("group_${rowNo}")!screenList.group!}" ></input>
                         </td>
                     </#if>
                     <input type="hidden" name="screen_${rowNo}" value="${screenList.screen!}"></input>
                </tr>
                <#assign rowNo = rowNo+1/>
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

<#if resultList?has_content>
    <table class="osafe" cellspacing="0">
       <thead>
         <tr class="heading">
           <th class="nameCol firstCol">${uiLabelMap.AttrNameLabel}</th>
           <th class="seqCol">${uiLabelMap.SeqNoLabel}
             <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.SeqIdHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
           </th>
           <th class="nameCol">${uiLabelMap.CaptionLabel}</th> 
           <th class="nameCol">${uiLabelMap.TypeLabel}</th>
           <th class="nameCol">${uiLabelMap.ReqLabel}</th>
         </tr>
       </thead>
       <tbody>
            
            <#assign rowClass = "1">
            <#list resultList  as customPartyAttribute>
                <#assign hasNext = customPartyAttribute_has_next>
                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                     <td class="nameCol <#if !hasNext>lastRow</#if>" >
                       <a href="<@ofbizUrl>manageCustomPartyAttributeItemDetail?attrName=${customPartyAttribute.AttrName?if_exists}</@ofbizUrl>">${customPartyAttribute.AttrName!}</a>
                       <input type="hidden" name="attrName_${customPartyAttribute_index}" value="${parameters.get("attrName_${customPartyAttribute_index}")!customPartyAttribute.AttrName!}"></input>
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                       <input type="text" name="sequenceNum_${customPartyAttribute_index}" class="small" id="sequenceNum" value="${parameters.get("sequenceNum_${customPartyAttribute_index}")!customPartyAttribute.SequenceNum!}" maxlength="9"></input>
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                       <input type="hidden" name="caption_${customPartyAttribute_index}" value="${parameters.get("caption_${customPartyAttribute_index}")!customPartyAttribute.Caption!}"></input>
                       ${customPartyAttribute.Caption!}
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                       <input type="hidden" name="type_${customPartyAttribute_index}" value="${parameters.get("type_${customPartyAttribute_index}")!customPartyAttribute.Type!}"></input>
                       ${customPartyAttribute.Type!}
                     </td>
                     <td class="seqCol <#if !hasNext>lastRow</#if>">
                       <input type="hidden" name="mandatory_${customPartyAttribute_index}" value="${parameters.get("mandatory_${customPartyAttribute_index}")!customPartyAttribute.Mandatory!}"></input>
                       ${customPartyAttribute.Mandatory!}
                     </td>
                     <input type="hidden" name="entryFormat_${customPartyAttribute_index}" value="${parameters.get("entryFormat_${customPartyAttribute_index}")!customPartyAttribute.EntryFormat!}"></input>
                     <input type="hidden" name="maxLength_${customPartyAttribute_index}" value="${parameters.get("maxLength_${customPartyAttribute_index}")!customPartyAttribute.MaxLength!}"></input>
                     <input type="hidden" name="valueList_${customPartyAttribute_index}" value="${parameters.get("valueList_${customPartyAttribute_index}")!customPartyAttribute.ValueList!}"></input>
                     <input type="hidden" name="reqMessage_${customPartyAttribute_index}" value="${parameters.get("reqMessage_${customPartyAttribute_index}")!customPartyAttribute.ReqMessage!}"></input>
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

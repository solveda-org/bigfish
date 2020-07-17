    <table class="osafe" cellspacing="0" id="createFacetValueTable">
      <thead>
        <tr class="heading extraTr">
          <th class="idCol firstCol">${uiLabelMap.FacetIDLabel}</th>
          <th class="valueCol">${uiLabelMap.FacetValueLabel}</th>
          <th class="radioCol">${uiLabelMap.HideShowLabel}</th>
          <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
          <th class="actionCol">${uiLabelMap.ActionsLabel}</th>
        </tr>
      </thead>
      <tbody>
      <input type="hidden" name="productFeatureTypeId" value="${parameters.productFeatureGroupId!}"/>
      <input type="hidden" name="rowNo" id="rowNo"/>
      <#assign minInitializeRow = '5'>
      <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!minInitializeRow!}"/>
        <#assign rowClass = "1">
        <#assign minRow = parameters.totalRows!minInitializeRow!>
        <#assign minRow = minRow?number/>
        <#list 1..minRow as rowNo>
          <tr id="row_${rowNo}" class="<#if rowClass == "2">even<#else>odd</#if>">
            <td class="idCol firstCol">
              <#assign productFeatureIdParm = parameters.get("productFeatureId_${rowNo}")! />
              <input type="text" maxlength="20" name="productFeatureId_${rowNo}" id="productFeatureId" value="${productFeatureIdParm}"/>
            </td>
            <td class="valueCol">
              <#assign descriptionParm = parameters.get("description_${rowNo}")! />
              <input type="text" name="description_${rowNo}" id="description" value="${descriptionParm!}"/>
            </td>
            <td class="radioCol">
              <#assign yesterday=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1)/>
              <#assign thruDate= parameters.get("thruDate_${rowNo}")!/>
              <span class="radiobutton">
                <input type="radio" name="thruDate_${rowNo}" id="thruDate" value="${yesterday!}" <#if thruDate?has_content>checked="checked"</#if> />${uiLabelMap.HideLabel}
                <input type="radio" name="thruDate_${rowNo}" id="thruDate" value="" <#if !thruDate?has_content>checked="checked"</#if> />${uiLabelMap.ShowLabel}
              </span>
            </td>
            <td class="seqCol">
               <#assign sequenceNumParm = parameters.get("sequenceNum_${rowNo}")! />
               <input type="text" class="small" name="sequenceNum_${rowNo}" id="sequenceNum" value="${sequenceNumParm!}" maxlength="9"/>
            </td>
            <td class="actionCol">
                <a href="javascript:setNewRowNo('${rowNo}');javascript:removeRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteFeatureRowTooltip}');" onMouseout="hideTooltip()"></a>
                <a href="javascript:setNewRowNo('${rowNo}');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewFeatureRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                <a href="javascript:setNewRowNo('${rowNo+1}');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewFeatureRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
              </td>
            </td>
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if>
          </tr>
          <#assign rowNo = rowNo+1/>
        </#list>
        <tr id="addIconRow extraTr" <#if (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
          <td colspan="5">&nbsp;</td>
          <td class="actionCol">
            <span class="noAction"></span>
              <a href="javascript:setNewRowNo(jQuery('tr').length-2);javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewFeatureRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
            <span class="noAction"></span>      
          </td>
        </tr>
      </tbody>
    </table>
    <table style="display:none" id="newRow">
        <tr>
            <td class="idCol firstCol">
              <input type="text" maxlength="20" name="productFeatureId_" id="productFeatureId" value="" disabled="disabled"/>
            </td>
            <td class="valueCol">
              <input type="text" name="description_" id="description" value="" disabled="disabled"/>
            </td>
            <td class="radioCol">
              <span class="radiobutton">
                <input type="radio" id="thruDate" name="thruDate_" value="${yesterday!}" <#if thruDate?has_content>checked="checked"</#if> disabled="disabled"/>${uiLabelMap.HideLabel}
                <input type="radio" id="thruDate" name="thruDate_" value="" <#if !thruDate?has_content>checked="checked"</#if>  disabled="disabled"/>${uiLabelMap.ShowLabel}
              </span>
            </td>
            <td class="seqCol">
              <input type="text" class="small" name="sequenceNum_" id="sequenceNum" value="" disabled="disabled"/>
            </td>
            <td class="actionCol">
              <a href="javascript:setNewRowNo('');javascript:removeRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeleteFeatureRowTooltip}');" onMouseout="hideTooltip()"></a>
              <a href="javascript:setNewRowNo('');javascript:addNewRow('${detailEntryTable}');" ><span class="insertBeforeIcon" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewFeatureRowTooltip}');" onMouseout="hideTooltip()"></span></a>
              <a href="javascript:setNewRowNo('');javascript:addNewRow('${detailEntryTable}');" ><span class="insertAfterIcon" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewFeatureRowTooltip}');" onMouseout="hideTooltip()"></span></a>        </td>
            </td>
          </tr>
      </table>
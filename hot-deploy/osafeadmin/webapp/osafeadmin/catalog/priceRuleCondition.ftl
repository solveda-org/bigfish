
<input type="hidden" name="rowNo" id="rowNo"/>
<#if productPriceCondList?exists && productPriceCondList?has_content>
    <#assign productPriceCondListSize= productPriceCondList.size()/>
    <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!productPriceCondListSize!}"/>
<#else>
    <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!minInitializeRow!}"/>
</#if>
<input type="hidden" name="productName" id="productName" value=""/>
<input type="hidden" name="categoryName" id="categoryName" value=""/>
<input type="hidden" name="customerName" id="customerName" value=""/>
<input type="hidden" name="featureDescription" id="featureDescription" value=""/>
<input type="hidden" name="partyClassificationGroupDescription" id="partyClassificationGroupDescription" value=""/>

<table class="osafe" id="${detailEntryTable}">
  <thead>
    <tr class="heading extraTr">
      <th class="idCol firstCol">${uiLabelMap.InputLabel}</th>
      <th class="nameCol">${uiLabelMap.OperatorLabel}</th>
      <th class="nameCol">${uiLabelMap.ValueLabel}</th>
      <th class="actionCol lastCol"></th>
    </tr>
  </thead>
  <tbody>
      <#if productPriceCondList?has_content && !parameters.totalRows?exists>
          <#assign rowNo = 1/>
          <#list productPriceCondList as productPriceCond>
              <#assign inputParamEnumId = productPriceCond.inputParamEnumId!""/>
              <#assign operatorEnumId = productPriceCond.operatorEnumId!""/>
              <#assign condValue = productPriceCond.condValue!""/>
            <tr>
              <td>
                <select name="inputParamEnumId_${rowNo}" id="inputParamEnumId" class="small priceRuleInputParamEnumId" onChange="javascript:showLookupIcon(this);">
                  <#if inputParamEnums?has_content>
                    <#list inputParamEnums as inputParamEnum>
                      <option value='${inputParamEnum.enumId!}' <#if inputParamEnumId == inputParamEnum.enumId!"" >selected=selected</#if>>${inputParamEnum.description?default(inputParamEnum.enumId!)}</option>
                    </#list>
                  </#if>
                </select>
              </td>
              <td>
                <select name="operatorEnumId_${rowNo}" id="operatorEnumId" class="small">
                  <#if condOperEnums?has_content>
                    <#list condOperEnums as condOperEnum>
                      <option value='${condOperEnum.enumId!}' <#if operatorEnumId == condOperEnum.enumId!"" >selected=selected</#if>>${condOperEnum.description?default(condOperEnum.enumId!)}</option>
                    </#list>
                  </#if>
                </select>
              </td>
              <td>
                <input type="text" class="normal" name="condValue_${rowNo}" id="condValue" value="${condValue}"/>
              </td>
              <td class="actionCol">
                <a href="javascript:setNewRowNo('${rowNo}');javascript:removeRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeletePriceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
                <a href="javascript:setNewRowNo('${rowNo}');javascript:addNewRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                <a href="javascript:setNewRowNo('${rowNo+1}');javascript:addNewRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewPiceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
              </td>
            </tr>
            <#assign rowNo = rowNo+1/>
          </#list>
      <#else>
        <#assign minRow = parameters.totalRows!minInitializeRow!>
        <#assign minRow = minRow?number/>
        <#if  minRow &gt; 0>
            <#list 1..minRow as x>
              <#assign inputParamEnumId = request.getParameter("inputParamEnumId_${x}")!""/>
              <#assign operatorEnumId = request.getParameter("operatorEnumId_${x}")!""/>
              <#assign condValue = request.getParameter("condValue_${x}")!""/>
              <tr>
                  <td>
                    <select name="inputParamEnumId_${x}" id="inputParamEnumId" class="small priceRuleInputParamEnumId" onChange="javascript:showLookupIcon(this);">
                      <#if inputParamEnums?has_content>
                        <#list inputParamEnums as inputParamEnum>
                          <option value='${inputParamEnum.enumId!}' <#if inputParamEnumId == inputParamEnum.enumId!"" >selected=selected</#if>>${inputParamEnum.description?default(inputParamEnum.enumId!)}</option>
                        </#list>
                      </#if>
                    </select>
                  </td>
                  <td>
                    <select name="operatorEnumId_${x}" id="operatorEnumId" class="small">
                      <#if condOperEnums?has_content>
                        <#list condOperEnums as condOperEnum>
                          <option value='${condOperEnum.enumId!}' <#if operatorEnumId == condOperEnum.enumId!"" >selected=selected</#if>>${condOperEnum.description?default(condOperEnum.enumId!)}</option>
                        </#list>
                      </#if>
                    </select>
                  </td>
                  <td>
                    <input type="text" class="normal" name="condValue_${x}" id="condValue" value="${condValue}"/>
                  </td>
                <td class="actionCol">
                  <a href="javascript:setNewRowNo('${x}');javascript:removeRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeletePriceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
                  <a href="javascript:setNewRowNo('${x}');javascript:addNewRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                  <a href="javascript:setNewRowNo('${x+1}');javascript:addNewRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewPiceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
                </td>
              </tr>
            </#list>
        </#if>
      </#if>
      <tr id="addIconRow" class="extraTr"<#if (productPriceCondList?exists && productPriceCondList?has_content) || (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
        <td colspan="3">&nbsp;</td>
        <td class="actionCol">
            <span class="noAction"></span>
            <a href="javascript:setNewRowNo(jQuery('tr').length-2);javascript:addNewRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
            <span class="noAction"></span>
        </td>
      </tr>
  </tbody>
</table>

  <table style="display:none" id="newRow">
    <tr>
      <td>
        <select name="inputParamEnumId_" id="inputParamEnumId" class="small priceRuleInputParamEnumId" disabled="disabled" onChange="javascript:showLookupIcon(this);">
          <#if inputParamEnums?has_content>
            <#list inputParamEnums as inputParamEnum>
              <option value='${inputParamEnum.enumId!}'>${inputParamEnum.description?default(inputParamEnum.enumId!)}</option>
            </#list>
          </#if>
        </select>
      </td>
      <td>
        <select name="operatorEnumId_" id="operatorEnumId" class="small" disabled="disabled">
          <#if condOperEnums?has_content>
            <#list condOperEnums as condOperEnum>
              <option value='${condOperEnum.enumId!}'>${condOperEnum.description?default(condOperEnum.enumId!)}</option>
            </#list>
          </#if>
        </select>
      </td>
      <td>
        <input type="text" class="normal" name="condValue_" id="condValue" value="" disabled="disabled"/>
      </td>
        <td class="actionCol">
          <a href="javascript:setNewRowNo('');javascript:removeRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeletePriceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
          <a href="javascript:setNewRowNo('');javascript:addNewRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
          <a href="javascript:setNewRowNo('');javascript:addNewRow('${detailEntryTable}');javascript:showLookupIconPageLoad();" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewPiceRuleRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a></td>
      </tr>
  </table>
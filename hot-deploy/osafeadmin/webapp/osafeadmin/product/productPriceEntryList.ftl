<#assign priceType= parameters.PriceTypeRadio!""/>
  <#if (productPriceCondList?has_content && priceType=='') || (priceType == 'VolumePrice')>
    <div id="volumepricing" style="display:block">
  <#else>
    <div id="volumepricing" style="display:none">
  </#if>  
     
  <table id="volumePriceTable">
    <thead>
      <tr class="extraTr">
        <th>&nbsp;</th>
        <th class="qtyCol">${uiLabelMap.FromQtyLabel}</th>
        <th class="qtyCol">${uiLabelMap.ToQtyLabel}</th>
        <th class="qtyCol textAlignRight">${uiLabelMap.PriceLabel}</th>
        <th class="priceDescCol">${uiLabelMap.DescriptionLabel}</th>
        <th class="actionCol"></th>
      </tr>
    </thead>
    <tbody>
      <input type="hidden" name="rowNo" id="rowNo"/>
      <#if productPriceCondList?exists && productPriceCondList?has_content>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!productPriceCondListSize}"/>
      <#else>
        <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!minInitializeRow!}"/>
      </#if>
      <#if productPriceCondList?has_content && !parameters.totalRows?exists>
      <#assign rowNo = 1/>
        <#list productPriceCondList as priceCond>
          <#assign priceRule = priceCond.getRelatedOne("ProductPriceRule")?if_exists>
          <#assign qtyBreakIdCondList = delegator.findByAnd("ProductPriceCond",Static["org.ofbiz.base.util.UtilMisc"].toMap("inputParamEnumId", "PRIP_QUANTITY", "productPriceRuleId",priceRule.productPriceRuleId),Static["org.ofbiz.base.util.UtilMisc"].toList("condValue"))/>
          <#if qtyBreakIdCondList?has_content && qtyBreakIdCondList?exists>
            <#assign priceIdActionList= delegator.findByAnd("ProductPriceAction",Static["org.ofbiz.base.util.UtilMisc"].toMap("productPriceActionTypeId", "PRICE_FLAT", "productPriceRuleId",priceRule.productPriceRuleId),Static["org.ofbiz.base.util.UtilMisc"].toList("productPriceRuleId"))/>
            <#if priceIdActionList?has_content && priceIdActionList?exists>
              <#assign priceIdAction = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(priceIdActionList)/>
            </#if>
            <#assign toQty = ''/>
            <#list qtyBreakIdCondList as qtyBreaks>
              <#if qtyBreaks.operatorEnumId == 'PRC_GTE'>
                <#assign fromQty = request.getParameter("fromQty_${rowNo}")!qtyBreaks.condValue!/>
              </#if>
              <#if qtyBreaks.operatorEnumId == 'PRC_LTE'>
                <#assign toQty = request.getParameter("toQty_${rowNo}")!qtyBreaks.condValue!/>
              </#if>
            </#list>
            <#assign amount = request.getParameter("price_${rowNo}")!priceIdAction.amount!''/>
            <#assign description = request.getParameter("description_${rowNo}")!priceRule.description!''/>
            <input type="hidden" name="productPriceRuleId" id="productPriceRuleId" value="${priceCond.productPriceRuleId!}" />
            <tr>
              <td>&nbsp;</td>
              <td>
                <input type="text" class="textEntry" name="fromQty_${rowNo}" id="fromQty" value="${fromQty!}"/>
              </td>
              <td>
                <input type="text" class="textEntry" name="toQty_${rowNo}" id="toQty" value="${toQty!}"/>
              </td>
              <td>
                <input type="text" class="textEntry textAlignRight" name="price_${rowNo}" id="price" value="${amount?string("0.00")!}"/>
              </td>
              <td>
                <input type="text" class="normal" name="description_${rowNo}" id="description" value="${description!}"/>
              </td>
              <td class="actionCol">
                <a href="javascript:setNewRowNo('${rowNo}');javascript:removeRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeletePriceRowTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
                <a href="javascript:setNewRowNo('${rowNo}');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                <a href="javascript:setNewRowNo('${rowNo+1}');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewPiceRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
              </td>
            </tr>
            <#assign rowNo = rowNo+1/>
          </#if>
        </#list>
      <#else>
        <#assign minRow = parameters.totalRows!minInitializeRow!>
        <#assign minRow = minRow?number/>
        <#list 1..minRow as x>
          <#assign fromQty = request.getParameter("fromQty_${x}")!/>
          <#assign toQty = request.getParameter("toQty_${x}")!/>
          <#assign amount = request.getParameter("price_${x}")!/>
          <#assign description = request.getParameter("description_${x}")!/>
          <tr>
            <td>&nbsp;</td>
            <td>
              <input type="text" class="textEntry" name="fromQty_${x}" id="fromQty" value="${fromQty!}"/>
            </td>
            <td>
              <input type="text" class="textEntry" name="toQty_${x}" id="toQty" value="${toQty!}"/>
            </td>
            <td>
              <input type="text" class="textEntry textAlignRight" name="price_${x}" id="price" value="${amount!}"/>
            </td>
            <td>
              <input type="text" class="normal" name="description_${x}" id="description" value="${description!}"/>
            </td>
            <td class="actionCol">
              <a href="javascript:setNewRowNo('${x}');javascript:removeRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeletePriceRowTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
              <a href="javascript:setNewRowNo('${x}');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
              <a href="javascript:setNewRowNo('${x+1}');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewPiceRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>           </td>
          </tr>
        </#list>
      </#if>
      <tr id="addIconRow" class="extraTr" <#if (productPriceCondList?exists && productPriceCondList?has_content) || (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
      <td colspan="5">&nbsp;</td>
      <td class="actionCol">
        <span class="noAction"></span>
        <a href="javascript:setNewRowNo(jQuery('tr').length-2);javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
        <span class="noAction"></span>      </td>
    </tr>
    </tbody>
  </table>
  <table style="display:none" id="newRow">
    <tr>
        <td>&nbsp;</td>
        <td>
          <input type="text" class="textEntry" name="fromQty_" id="fromQty" value="" disabled="disabled"/>
        </td>
        <td>
          <input type="text" class="textEntry" name="toQty_" id="toQty" value="" disabled="disabled"/>
        </td>
        <td>
          <input type="text" class="textEntry textAlignRight" name="price_" id="price" value="" disabled="disabled"/>
        </td>
        <td>
          <input type="text" class="normal" name="description_" id="description" value="" disabled="disabled"/>
        </td>
        <td class="actionCol">
          <a href="javascript:setNewRowNo('');javascript:removeRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.DeletePriceRowTooltip}');" onMouseout="hideTooltip()"><span class="crossIcon"></span></a>
          <a href="javascript:setNewRowNo('');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewPiceRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
          <a href="javascript:setNewRowNo('');javascript:addNewRow('${detailEntryTable}');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewPiceRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
        </td>
      </tr>
  </table>

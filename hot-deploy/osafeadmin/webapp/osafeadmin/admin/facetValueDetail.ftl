
    <#assign now = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
    <#assign yesterday=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1)/>
    <input type="hidden" name="_useRowSubmit" value="Y" />
    <table class="osafe" cellspacing="1" id="createFacetValueTable">
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
        <#assign rowClass = "1">
        <#assign rowNo = 1/>
        <input type="hidden" name="rowNo" id="rowNo"/>
        <#if productFeatureGrpApplList?exists && productFeatureGrpApplList?has_content>
            <#assign productFeatureGrpApplListSize = productFeatureGrpApplList.size()/>
            <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!productFeatureGrpApplListSize}"/>
        <#else>
            <input type="hidden" name="totalRows" id="totalRows" value="${parameters.totalRows!minInitializeRow!}"/>
        </#if>
        <input type="hidden" name="productFeatureGroupId" value="${parameters.productFeatureGroupId!}"/>
        <#if productFeatureGrpApplList?has_content && !parameters.totalRows?exists>
          <#assign rowNo = 1/>
            <#list productFeatureGrpApplList as productFeatureGrpAppl>
              <tr id="row_${productFeatureGrpAppl.productFeatureGroupId!}" class="<#if rowClass == "2">even<#else>odd</#if>">
                <td class="idCol firstCol">
                  <input type="hidden" name="fromDate_${rowNo}"  id="fromDate" value="${productFeatureGrpAppl.fromDate!}"/>
                  <input type="hidden" name="productFeatureId_${rowNo}" id="productFeatureId" value="${productFeatureGrpAppl.productFeatureId!}"/>
                  ${productFeatureGrpAppl.productFeatureId!}
                </td>
                <td class="valueCol">
                  <#assign productFeature = delegator.findOne("ProductFeature", {"productFeatureId" : productFeatureGrpAppl.productFeatureId}, false) />
                  <#assign description = request.getParameter("description_${rowNo}")!productFeature.description!''/>
                  <input type="text" name="description_${rowNo}" id="description" value="${description!}"/>
                  <input type="hidden" name="productFeatureTypeId_${rowNo}" id="productFeatureTypeId value="${productFeature.productFeatureTypeId!}"/>
                </td>
                <td class="radioCol">
                  <#assign yesterday=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1)/>
                  <span class="radiobutton">
                    <#assign thruDate = request.getParameter("thruDate_${rowNo}")!productFeatureGrpAppl.thruDate!''/>
                    <input type="radio" name="thruDate_${rowNo}" id="thruDate" value="${yesterday!}" <#if (thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.HideLabel}
                    <input type="radio" name="thruDate_${rowNo}"  id="thruDate" value="" <#if !(thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.ShowLabel}
                  </span>
                </td>
                <td class="seqCol">
                   <#assign rowSeq = request.getParameter("sequenceNum_${rowNo}")!productFeatureGrpAppl.sequenceNum!''/>
                   <input type="text" class="small" name="sequenceNum_${rowNo}" id="sequenceNum" value="${rowSeq!}"/>
                </td>
                <td class="actionCol">
                    <a href="<@ofbizUrl>featureSwatchImage?productFeatureGroupId=${parameters.productFeatureGroupId!}&amp;productFeatureId=${productFeatureGrpAppl.productFeatureId?if_exists}</@ofbizUrl>" class="normalAnchor" onMouseover="showTooltip(event,'${uiLabelMap.ManageSwatchImagesTooltip}');" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
                    <a href="javascript:setNewRowNo('${rowNo}');javascript:addNewRow('createFacetValueTable');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewFeatureRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                    <a href="javascript:setNewRowNo('${rowNo+1}');javascript:addNewRow('createFacetValueTable');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewFeatureRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
                </td>
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
              </tr>
              <#assign rowNo = rowNo+1/>
            </#list>
        <#elseif parameters.totalRows?exists>
            <#assign minRow = parameters.totalRows!minInitializeRow!>
            <#assign minRow = minRow?number/>
            <#assign rowNo = 1/>
            <#list 1..minRow as x>
              <#assign productFeatureIdParm = parameters.get("productFeatureId_${x}")! />
              <#assign fromDate = parameters.get("fromDate_${x}")! />
              <#if !fromDate?has_content>
                  <#assign fromDate = now/>
              </#if>
              <#assign descriptionParm = parameters.get("description_${x}")! />
              <#assign thruDate= parameters.get("thruDate_${x}")!/>
              <#assign sequenceNumParm = parameters.get("sequenceNum_${x}")! />
              <tr id="row_${x}" class="<#if rowClass == "2">even<#else>odd</#if>">
                <td class="idCol firstCol">
                  
                  <input type="hidden" name="fromDate_${x}"  id="fromDate" value="${fromDate!}"/>
                  <#if (parameters.get("productFeatureId_${x}"))?has_content>
                      <input type="hidden" name="productFeatureId_${x}" id="productFeatureId" value="${productFeatureIdParm!}"/>
                      ${productFeatureIdParm!}
                  <#else>
                      <#assign newProductFeatureIdParm = parameters.get("newProductFeatureId_${x}")! />
                      <input type="text" name="newProductFeatureId_${x}" maxlength="20" id="newProductFeatureId" value="${newProductFeatureIdParm!}" />
                  </#if>
                </td>
                <td class="valueCol">
                  <#if (parameters.get("description_${x}"))?has_content>
                      <input type="text" name="description_${x}" id="description" value="${descriptionParm!}"/>
                  <#else>
                      <#assign newDescription = parameters.get("newDescription_${x}")! />
                      <input type="text" name="newDescription_${x}" id="newDescription" value="${newDescription!}" />
                  </#if>
                </td>
                <td class="radioCol">
                  <span class="radiobutton">
                    <input type="radio" name="thruDate_${x}" id="thruDate" value="${yesterday!}" <#if thruDate?has_content>checked="checked"</#if> />${uiLabelMap.HideLabel}
                    <input type="radio" name="thruDate_${x}" id="thruDate" value="" <#if !thruDate?has_content>checked="checked"</#if> />${uiLabelMap.ShowLabel}
                  </span>
                </td>
                <td class="seqCol">
                   <input type="text" class="small" name="sequenceNum_${x}" id="sequenceNum" value="${sequenceNumParm!}" maxlength="9"/>
                </td>
                <td class="actionCol">
                  <#if parameters.productFeatureGroupId?exists && parameters.productFeatureGroupId?has_content>
                      <a href="<@ofbizUrl>featureSwatchImage?productFeatureGroupId=${parameters.productFeatureGroupId!}&amp;productFeatureId=${productFeatureId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageSwatchImagesTooltip}');" class="normalAnchor" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
                  </#if>
                  <a href="javascript:setNewRowNo('${x}');javascript:addNewRow('createFacetValueTable');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewFeatureRowTooltip}');" onMouseout="hideTooltip()"><span class="insertBeforeIcon"></span></a>
                  <a href="javascript:setNewRowNo('${x+1}');javascript:addNewRow('createFacetValueTable');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewFeatureRowTooltip}');" onMouseout="hideTooltip()"><span class="insertAfterIcon"></span></a>
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
            </#if>
            <#if productFeatureGrpApplList?has_content>
            <tr class="footer extraTr">
                <th colspan="5">
                  <div class="entryInput checkbox">
                    <input type="checkbox" class="checkBoxEntry" name="updateProductFeatureAppls" id="updateProductFeatureAppls" value="Y"<#if parameters.updateProductFeatureAppls?has_content>checked</#if> />${uiLabelMap.BroadcastChangeToProductLabel}
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.BroadcastChangeToProductHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                  </div>
                </th>
            </tr>
            <#else>
            <tr id="addIconRow" class="extraTr" <#if (minRow?exists && minRow &gt; 0)> style="display:none"</#if>>
              <td colspan="4">&nbsp;</td>
              <td class="actionCol">
                <span class="noAction"></span>
                  <a href="javascript:setNewRowNo(jQuery('tr').length-jQuery('tr.extraTr').size());javascript:addNewRow('createFacetValueTable');" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewFeatureRowTooltip}');" onMouseout="hideTooltip()" onClick="jQuery('.footer').show();"><span class="insertBeforeIcon"></span></a>
                <span class="noAction"></span>      
              </td>
            </tr>
            </#if>
            
            <tr class="footer extraTr" style="display:none;">
                <th colspan="5">
                  <div class="entryInput checkbox">
                    <input type="checkbox" class="checkBoxEntry" name="updateProductFeatureAppls" id="updateProductFeatureAppls" value="Y"<#if parameters.updateProductFeatureAppls?has_content>checked</#if> />${uiLabelMap.BroadcastChangeToProductLabel}
                    <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.BroadcastChangeToProductHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                  </div>
                </th>
            </tr>
            
      </tbody>
    </table>
    <table style="display:none" id="newRow">
        <tr>
          <td class="idCol firstCol">
            <input type="hidden" name="fromDate_"  id="fromDate" value="${now!}" disabled="disabled"/>
            <input type="text" maxlength="20" name="newProductFeatureId_" id="newProductFeatureId" value="" disabled="disabled"/>
          </td>
          <td class="valueCol">
            <input type="text" name="newDescription_" id="newDescription" value="" disabled="disabled"/>
            <input type="hidden" name="productFeatureTypeId_" id="productFeatureTypeId" value="" disabled="disabled"/>
          </td>
          <td class="radioCol">
            <span class="radiobutton">
              <input type="radio" name="thruDate_" id="thruDate" value="${yesterday!}" disabled="disabled"/>${uiLabelMap.HideLabel}
              <input type="radio" name="thruDate_" id="thruDate" value=""  checked="checked" disabled="disabled"/>${uiLabelMap.ShowLabel}
            </span>
          </td>
          <td class="seqCol">
            <input type="text" class="small" name="sequenceNum_" id="sequenceNum" value="" disabled="disabled"/>
          </td>
          <td class="actionCol">
            <a href="<@ofbizUrl>featureSwatchImage?productFeatureGroupId=${parameters.productFeatureGroupId!}&amp;productFeatureId=""</@ofbizUrl>" class="normalAnchor" onMouseover="showTooltip(event,'${uiLabelMap.ManageSwatchImagesTooltip}');" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
            <a href="javascript:setNewRowNo('');javascript:addNewRow('createFacetValueTable');" ><span class="insertBeforeIcon" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertBeforeNewFeatureRowTooltip}');" onMouseout="hideTooltip()"></span></a>
            <a href="javascript:setNewRowNo('');javascript:addNewRow('createFacetValueTable');" ><span class="insertAfterIcon" onMouseover="javascript:showTooltip(event,'${uiLabelMap.InsertAfterNewFeatureRowTooltip}');" onMouseout="hideTooltip()"></span></a>        </td>
          </td>
        </tr>
    </table>

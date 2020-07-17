<#if productFeatureGrpApplList?has_content>
    <input type="hidden" name="_useRowSubmit" value="Y" />
    <table class="osafe" cellspacing="0">
      <thead>
        <tr class="heading">
          <th class="idCol firstCol">${uiLabelMap.FacetIDLabel}</th>
          <th class="valueCol">${uiLabelMap.FacetValueLabel}</th>
          <th class="radioCol">${uiLabelMap.HideShowLabel}</th>
          <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
          <th class="actionColSmall">${uiLabelMap.ActionsLabel}</th>
        </tr>
      </thead>
      <tbody>
        <#assign rowClass = "1">
        <#assign rowNo = 1/>
        <input type="hidden" name="productFeatureGroupId" value="${parameters.productFeatureGroupId!}"/>
        <#list productFeatureGrpApplList as productFeatureGrpAppl>
        <#assign rowSeq = rowNo * 10>
          <tr id="row_${productFeatureGrpAppl.productFeatureGroupId!}" class="<#if rowClass == "2">even<#else>odd</#if>">
            <td class="idCol firstCol">
              <input type="hidden" name="fromDate_${productFeatureGrpAppl_index}" value="${productFeatureGrpAppl.fromDate!}"/>
              <input type="hidden" name="productFeatureId_${productFeatureGrpAppl_index}" value="${productFeatureGrpAppl.productFeatureId!}"/>
              ${productFeatureGrpAppl.productFeatureId!}
            </td>
            <td class="valueCol">
              <#assign productFeature = delegator.findOne("ProductFeature", {"productFeatureId" : productFeatureGrpAppl.productFeatureId}, true) />
              <#assign description = request.getParameter("description_${productFeatureGrpAppl_index}")!productFeature.description!''/>
              <input type="text" name="description_${productFeatureGrpAppl_index}" value="${description!}"/>
              <input type="hidden" name="productFeatureTypeId_${productFeatureGrpAppl_index}" value="${productFeature.productFeatureTypeId!}"/>
            </td>
            <td class="radioCol">
              <#assign yesterday=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1)/>
              <span class="radiobutton">
                <#assign thruDate = request.getParameter("thruDate_${productFeatureGrpAppl_index}")!productFeatureGrpAppl.thruDate!''/>
                <input type="radio" name="thruDate_${productFeatureGrpAppl_index}" value="${yesterday!}" <#if (thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.HideLabel}
                <input type="radio" name="thruDate_${productFeatureGrpAppl_index}" value="" <#if !(thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.ShowLabel}
              </span>
            </td>
            <td class="seqCol">
               <#assign rowSeq = request.getParameter("sequenceNum_${productFeatureGrpAppl_index}")!rowSeq!''/>
               <input type="text" class="small" name="sequenceNum_${productFeatureGrpAppl_index}" value="${rowSeq!}"/>
            </td>
            <td class="actionColSmall">
               <a href="<@ofbizUrl>featureSwatchImage?productFeatureGroupId=${parameters.productFeatureGroupId!}&productFeatureId=${productFeatureGrpAppl.productFeatureId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageSwatchImagesTooltip}');" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
            </td>
            <#if rowClass == "2">
                <#assign rowClass = "1">
            <#else>
                <#assign rowClass = "2">
            </#if>
          </tr>
          <#assign rowNo = rowNo+1/>
        </#list>
        <tr class="footer">
            <th colspan="5">
            <div class="entryInput checkbox">
              <input type="checkbox" class="checkBoxEntry" name="updateProductFeatureAppls" id="updateProductFeatureAppls" value="Y"<#if parameters.updateProductFeatureAppls?has_content>checked</#if> />${uiLabelMap.BroadcastChangeToProductLabel}
              <a href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.BroadcastChangeToProductHelpInfo}');" onMouseout="hideTooltip()"><span class="helpIcon"></span></a>
            </div>
            </th>
        </tr>
      </tbody>
    </table>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>
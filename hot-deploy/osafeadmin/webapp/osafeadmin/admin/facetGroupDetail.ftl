<input type="hidden" name="productCategoryId" value="${parameters.productCategoryId!}"/>
<#if productFeatureCatGrpApplList?has_content>
    <input type="hidden" name="_useRowSubmit" value="Y" />
    <table class="osafe" cellspacing="0">
      <thead>
        <tr class="heading">
          <th class="idCol firstCol">${uiLabelMap.FacetGroupIdLabel}</th>
          <th class="idCol">${uiLabelMap.FacetDescriptionLabel}</th>
          <th class="actionCol"></th>
          <th class="radioCol">${uiLabelMap.HideShowLabel}</th>
          <th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
          <th class="valueCol">${uiLabelMap.MinDisplayLabel}</th>
          <th class="valueCol">${uiLabelMap.MaxDisplayLabel}</th>
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#assign rowNo = 1/>
        <#list productFeatureCatGrpApplList as productFeatureCatGrpAppl>
          <tr id="row_${productFeatureCatGrpAppl.productFeatureGroupId!}" <#if alt_row> class="alternate-row"</#if>>
            <td class="idCol firstCol">
              <a href="<@ofbizUrl>editFacetGroupDetail?productCategoryId=${parameters.productCategoryId!}&facetGroupId=${productFeatureCatGrpAppl.productFeatureGroupId!}</@ofbizUrl>">${productFeatureCatGrpAppl.productFeatureGroupId!}</a>
            </td>
            <td class="idCol">
              <input type="hidden" name="productCategoryId_${productFeatureCatGrpAppl_index}" value="${productFeatureCatGrpAppl.productCategoryId!}"/>
              <input type="hidden" name="fromDate_${productFeatureCatGrpAppl_index}" value="${productFeatureCatGrpAppl.fromDate!}"/>
              <input type="hidden" name="productFeatureGroupId_${productFeatureCatGrpAppl_index}" value="${productFeatureCatGrpAppl.productFeatureGroupId!}"/>
              <#assign productFeatureGroup = productFeatureCatGrpAppl.getRelatedOne("ProductFeatureGroup") />
              <#assign description = request.getParameter("description_${productFeatureCatGrpAppl_index}")!productFeatureGroup.description!''/>
              <input type="text" name="description_${productFeatureCatGrpAppl_index}" value="${description!}"/>
            </td>
            <td class="actionCol">
	        	<#if productFeatureCatGrpAppl.facetTooltip?has_content>
	        	    <#assign facetTooltipText = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productFeatureCatGrpAppl.facetTooltip, ADM_TOOLTIP_MAX_CHAR!)/>
	        	    <p onMouseover="showTooltip(event,'${facetTooltipText!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></p>
	        	</#if>
	        </td> 
            <td  class="radioCol">
              <#assign yesterday=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1)/>
              <span class="radiobutton">
                <#assign thruDate = request.getParameter("thruDate_${productFeatureCatGrpAppl_index}")!productFeatureCatGrpAppl.thruDate!''/>
                <input type="radio" name="thruDate_${productFeatureCatGrpAppl_index}" value="${yesterday!}" <#if (thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.HideLabel}
                <input type="radio" name="thruDate_${productFeatureCatGrpAppl_index}" value="" <#if !(thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.ShowLabel}
              </span>
            </td>
            <td class="seqCol">
              <#assign rowSeq = request.getParameter("sequenceNum_${productFeatureCatGrpAppl_index}")!productFeatureCatGrpAppl.sequenceNum!''/>
              <input type="text" class="infoValue small textAlignCenter" name="sequenceNum_${productFeatureCatGrpAppl_index}" value="${rowSeq!}" maxlength="9"/>
            </td>
            <td class="valueCol">
              <#assign facetValueMin = request.getParameter("facetValueMin_${productFeatureCatGrpAppl_index}")!productFeatureCatGrpAppl.facetValueMin!''/>
              <input type="text" class="infoValue small textAlignCenter" name="facetValueMin_${productFeatureCatGrpAppl_index}" value="${facetValueMin!}"/>
            </td>
            <td class="valueCol">
              <#assign facetValueMax = request.getParameter("facetValueMax_${productFeatureCatGrpAppl_index}")!productFeatureCatGrpAppl.facetValueMax!''/>
              <input type="text" class="infoValue small textAlignCenter" name="facetValueMax_${productFeatureCatGrpAppl_index}" value="${facetValueMax!}"/>
              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.MinMaxDisplayHelperIconInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </td>
          </tr>
          <#assign alt_row = !alt_row>
          <#assign rowNo = rowNo+1/>
        </#list>
      </tbody>
    </table>
<#else>
  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
</#if>
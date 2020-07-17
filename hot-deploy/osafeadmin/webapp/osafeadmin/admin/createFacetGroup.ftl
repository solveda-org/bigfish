<#if productFeatureCatGrpAppl?has_content && mode?exists && mode=="edit">
  <#assign productFeatureGroup = productFeatureCatGrpAppl.getRelatedOne("ProductFeatureGroup") />
  <#assign grpDescription = productFeatureGroup.description!/>
  <#assign facetTooltip = productFeatureCatGrpAppl.facetTooltip! />
  <#assign grpThruDate = productFeatureCatGrpAppl.thruDate! />
  <#assign grpFromDate = productFeatureCatGrpAppl.fromDate! />
  <#assign grpSequenceNum = productFeatureCatGrpAppl.sequenceNum! />
  <#assign facetValueMin = productFeatureCatGrpAppl.facetValueMin! />
  <#assign facetValueMax = productFeatureCatGrpAppl.facetValueMax! />
</#if>

<input type="hidden" name="productCategoryId" value="${parameters.productCategoryId!}"/>
<#assign today=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, 0)/>
<#if mode?exists && mode=="edit">
	<input type="hidden" name="grpFromDate" value="${grpFromDate!}"/>
<#else>
	<input type="hidden" name="grpFromDate" value="${today!}"/>
</#if>
<#assign readOnly = parameters.readOnly?if_exists />
<input type = "hidden" name="readOnly" value="${parameters.readOnly!readOnly!""}"/>
<div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.FacetGrpIdCaption}</label>
      </div>
      <div class="infoValue">
        <#if mode?exists && mode=="edit">
			<input type = "text" name="productFeatureGroupId" maxlength="20" readonly="readonly" value="${parameters.productFeatureGroupId!parameters.facetGroupId!""}"/>
		<#else>
			<#if readOnly?has_content && readOnly == "true">
	            <input type = "text" name="productFeatureGroupId" maxlength="20" readonly="readonly" value="${parameters.productFeatureGroupId!productFeatureGroupId!""}"/>
	        <#else>
	            <input type = "text" name="productFeatureGroupId" maxlength="20" value="${parameters.productFeatureGroupId!productFeatureGroupId!parameters.facetGroupId!""}"/>
	        </#if>
		</#if>
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.FacetDescCaption}</label>
      </div>
      <div class="infoValue">
        <#if readOnly?has_content && readOnly == "true">
            <input type = "text" name="grpDescription" readonly="readonly" value="${parameters.grpDescription!grpDescription!""}"/>
        <#else>
            <input type = "text" name="grpDescription" value="${parameters.grpDescription!grpDescription!""}"/>
        </#if>
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.FacetTooltipCaption}</label>
      </div>
      <div class="infoValue">
        
        <#if readOnly?has_content && readOnly == "true">
            <textarea class="smallArea" name="facetTooltip" cols="50" rows="5" readonly="readonly">${parameters.facetTooltip!facetTooltip!""}</textarea>
        <#else>
            <textarea class="smallArea" name="facetTooltip" cols="50" rows="5">${parameters.facetTooltip!facetTooltip!""}</textarea>
        </#if>
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.HideShowCaption}</label>
      </div>
      <div class="infoValue">
        <#assign yesterday=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1)/>
        <span class="radiobutton">
            <#assign thruDate = parameters.grpThruDate!grpThruDate!''/>
            <#if readOnly?has_content && readOnly == "true">
                <input type="radio" name="grpThruDate" value="${yesterday!}" <#if (thruDate?has_content)> checked="checked"</#if> disabled/>${uiLabelMap.HideLabel}
                <input type="radio" name="grpThruDate" value="" <#if !(thruDate?has_content)>checked="checked"</#if> disabled/>${uiLabelMap.ShowLabel}
                <#if (thruDate?has_content)>
                    <input type="hidden" name="grpThruDate" value="${yesterday!}"/>
                </#if>    
            <#else>
                <input type="radio" name="grpThruDate" value="${yesterday!}" <#if (thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.HideLabel}
                <input type="radio"  name="grpThruDate" value="" <#if !(thruDate?has_content)>checked="checked"</#if>/>${uiLabelMap.ShowLabel}
            </#if>
        </span>
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.SeqIdCaption}</label>
      </div>
      <div class="infoValue">
        <#if readOnly?has_content && readOnly == "true">
            <input type = "text" name="grpSequenceNum" readonly="readonly" value="${parameters.grpSequenceNum!grpSequenceNum!""}"/>
        <#else>
            <input type = "text" name="grpSequenceNum" value="${parameters.grpSequenceNum!grpSequenceNum!""}" maxlength="9"/>
        </#if>
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.MinDisplayCaption}</label>
      </div>
      <div class="infoValue">
      <#if readOnly?has_content && readOnly == "true">
          <input type = "text" name="facetValueMin" readonly="readonly" value="${parameters.facetValueMin!facetValueMin!""}"/>
      <#else>
          <input type = "text" name="facetValueMin"  value="${parameters.facetValueMin!facetValueMin!""}"/>
      </#if>
      </div>
      <div class="infoIcon">
          <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.MinMaxDisplayHelperIconInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
  <div class="infoRow row">
    <div class="infoEntry long">
      <div class="infoCaption">
        <label>${uiLabelMap.MaxDisplayCaption}</label>
      </div>
      <div class="infoValue">
        <#if readOnly?has_content && readOnly == "true">
            <input type = "text"  readonly="readonly" name="facetValueMax" value="${parameters.facetValueMax!facetValueMax!""}"/>
        <#else>
            <input type = "text"  name="facetValueMax" value="${parameters.facetValueMax!facetValueMax!""}"/>
        </#if>
      </div>
      <div class="infoIcon">
          <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.MinMaxDisplayHelperIconInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </div>
  </div>
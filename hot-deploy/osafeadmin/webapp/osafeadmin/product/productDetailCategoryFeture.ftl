<div id="productCategoryFetureDetail">
<#if parameters.productCategoryId?has_content>
    <#if parameters.productId?has_content>
        <#assign product = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", parameters.productId))?if_exists>
    </#if>
    <#assign productFeatureCatGrpApplList = delegator.findByAnd("ProductFeatureCatGrpAppl", {"productCategoryId" : parameters.productCategoryId}, ["sequenceNum", "productFeatureGroupId"])>
    <#assign productFeatureCatGrpAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureCatGrpApplList!)/>
      <table class="osafe">
        <thead>
	      <tr class="heading">
	        <th class="idCol firstCol"></th>
	        <th class="idCol">${uiLabelMap.FacetGroupIdLabel}</th>
	        <th class="descCol">${uiLabelMap.FacetDescLabel}</th>
	        <th class="radioCol">&nbsp;</th>
	      </tr>
        </thead>
      <#if productFeatureCatGrpAppls?has_content>
        
        <#list productFeatureCatGrpAppls as productFeatureCatGrpAppl>
          <tr class="dataRow">
            <td class="idCol firstCol">
	          <#if productFeatureCatGrpAppl_index == 0>
	            <div class="infoIcon">
	            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.FeatureHelperInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	            </div>
	          <#else>
	            &nbsp;
	          </#if>
	        </td>
	        <td class="idCol">
	          ${parameters.get("productFeatureGroupId_${productFeatureCatGrpAppl_index}")!productFeatureCatGrpAppl.productFeatureGroupId!""}
	          <input type="hidden" class="textEntry" name="productFeatureGroupId_${productFeatureCatGrpAppl_index}" id="productFeatureGroupId_${productFeatureCatGrpAppl_index}" value='${parameters.get("productFeatureGroupId_${productFeatureCatGrpAppl_index}")!productFeatureCatGrpAppl.productFeatureGroupId!""}' readOnly="true"/>
	        </td>
            <td class="descCol">
              <#assign productFeatureGroup = productFeatureCatGrpAppl.getRelatedOne("ProductFeatureGroup")!""/>
              ${productFeatureGroup.description!}
            </td>
            <td class="radioCol">
            <span class="radiobutton">
              <#assign productFeatureApplType = ""/>
              <#assign productFeatureId = ""/>
              <#assign productFeatureGroupAndAppls = delegator.findByAnd("ProdFeaGrpAppAndProdFeaApp", {"productId" : parameters.productId!"", "productFeatureGroupId" : productFeatureCatGrpAppl.productFeatureGroupId!""})>
              <#assign productFeatureGroupAndAppl = ""/>
              <#if productFeatureGroupAndAppls?has_content>
                <#assign productFeatureGroupAndAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureGroupAndAppls) />
                <#assign productFeatureApplType = productFeatureGroupAndAppl.productFeatureApplTypeId! />
                <#assign productFeatureId = productFeatureGroupAndAppl.productFeatureId! />
              </#if>
              <#if productFeatureGroupAndAppl?has_content && productFeatureGroupAndAppl.productFeatureApplTypeId == "SELECTABLE_FEATURE">
                <#assign productFeatureApplTypeId = request.getParameter("productFeatureApplTypeId_${productFeatureCatGrpAppl_index}")!productFeatureApplType!''/>
                <#if (!product?has_content) ||( product?has_content && (product.isVirtual = 'Y' || product.isVariant = 'Y'))>
                  <input type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}"  value="SELECTABLE_FEATURE" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "SELECTABLE_FEATURE">checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onclick="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.SelectableLabel}
                </#if>
                <input type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}" value="DISTINGUISHING_FEAT" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT">checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onclick="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.DescriptiveLabel}
                <input type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}" value="NA" <#if  productFeatureApplTypeId?exists && (productFeatureApplTypeId?string == "NA" || (productFeatureApplTypeId?string == "SELECTABLE_FEATURE" && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT"))>checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onclick="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.NALabel}
              <#else>
                <#assign productFeatureApplTypeId = request.getParameter("productFeatureApplTypeId_${productFeatureCatGrpAppl_index}")!productFeatureApplType!''/>
                <#if (!product?has_content) ||( product?has_content && (product.isVirtual = 'Y' || product.isVariant = 'Y'))>
                  <span class="selectableRadio" <#if parameters.isVirtual?has_content && parameters.isVirtual == 'N'>style="display:none"</#if>>
                    <input type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}"  value="SELECTABLE_FEATURE" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "SELECTABLE_FEATURE">checked="checked"</#if> onclick="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.SelectableLabel}
                  </span>
                </#if>
                <input type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}" value="DISTINGUISHING_FEAT" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT">checked="checked"</#if> onclick="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.DescriptiveLabel}
                <input type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}" value="NA" <#if  productFeatureApplTypeId?exists && (productFeatureApplTypeId?string == "NA" || (product?has_content && productFeatureApplTypeId?string != "SELECTABLE_FEATURE" && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT"))>checked="checked"</#if> onclick="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.NALabel}
              </#if>
            </span>
            &nbsp;                
              <#if (!product?has_content) ||( product?has_content && !(product.isVirtual = 'N' && product.isVariant = 'N'))>
                <span id = "selectedHelperIcon_${productFeatureCatGrpAppl_index}" style="display:none">
                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "SelectedFeatureHelperIconInfo",Static["org.ofbiz.base.util.UtilMisc"].toMap("featureType", "${productFeatureCatGrpAppl.productFeatureGroupId!}"), locale)/>
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </span>
                <span id = "notApplicableHelperIcon_${productFeatureCatGrpAppl_index}" style="display:none">
                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "NotApplicableFeatureHelperIconInfo", locale)/>
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </span>
              </#if>
                    
                    
              <#assign productFeatureGroupApplList = delegator.findByAnd("ProductFeatureGroupAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureCatGrpAppl.productFeatureGroupId!""), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
              <#assign productFeatureGroupAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureGroupApplList!)/>
              <#if productFeatureGroupAppls?has_content>
                <select id="productFeatureId_${productFeatureCatGrpAppl_index}" name="productFeatureId_${productFeatureCatGrpAppl_index}" class="short" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT">style="display:none"</#if>>
                  <option value="">Select</option>
                  <#assign selectedFeture = parameters.get("productFeatureId_${productFeatureCatGrpAppl_index}")!productFeatureId!"">
                  <#assign productFeatureList = Static["javolution.util.FastList"].newInstance()/>
                  <#list productFeatureGroupAppls as productFeatureGroupAppl>
                    <#assign productFeature = productFeatureGroupAppl.getRelatedOne("ProductFeature")/>
                    <#-- Prepared the list Product Feature to sort based on Description -->
                    <#assign changed = productFeatureList.add(productFeature)/>
                  </#list>
                  <#assign productFeatureList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productFeatureList,Static["org.ofbiz.base.util.UtilMisc"].toList("description"))/>
                  <#list productFeatureList as productFeature>
                    <#assign productFeatureName = productFeature.description?trim/>
                    <#assign optionValue = "${productFeature.productFeatureId!}">
                    <option value="${optionValue!}" <#if selectedFeture?has_content && selectedFeture.equals(optionValue)>selected</#if>><#if productFeatureName?has_content>${productFeatureName?if_exists}<#else>${productFeature.productFeatureId?if_exists}</#if></option>
                  </#list>
                </select>
              </#if>
                       
                       
              <#if (!product?has_content) ||( product?has_content && !(product.isVirtual = 'N' && product.isVariant = 'N'))>
                <span id = "descriptiveHelperIcon_${productFeatureCatGrpAppl_index}" style="display:none">
                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "DescriptiveFeatureHelperIconInfo", locale)/>
                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
                </span>
              </#if>
            </td>
          </tr>
        </#list>
      <#else>
        <tr class="dataRow">
            <td class="idCol firstCol" colspan="4">
	            <div class="infoIcon">
	            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.FeatureHelperInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	            </div>
	        </td>
	    </tr>
      </#if>
      
   </table>
    
</#if>
</div>
<div id="productCategoryFetureDetail">
<#if parameters.productCategoryId?has_content>
    <#if parameters.productId?has_content>
        <#assign product = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", parameters.productId))?if_exists>
    </#if>
    <#assign productFeatureCatGrpAppls = delegator.findByAnd("ProductFeatureCatGrpAppl", {"productCategoryId" : parameters.productCategoryId}, ["sequenceNum", "productFeatureGroupId"])>
    <#-- <#assign productFeatureCatGrpAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureCatGrpApplList!)/> -->
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
        <#assign alreadyShownProductFeatureGroupId = Static["javolution.util.FastList"].newInstance()/>
        <#list productFeatureCatGrpAppls as productFeatureCatGrpAppl>
          <#if !alreadyShownProductFeatureGroupId.contains(productFeatureCatGrpAppl.productFeatureGroupId)>
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
	              <#assign productFeatureGroupAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureGroupAndAppls!)/>
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
	                <span id = "selectedHelperIcon_${productFeatureCatGrpAppl_index}" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "SELECTABLE_FEATURE"><#else>style="display:none"</#if>>
	                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "SelectedFeatureHelperIconInfo",Static["org.ofbiz.base.util.UtilMisc"].toMap("featureType", "${productFeatureCatGrpAppl.productFeatureGroupId!}"), locale)/>
	                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	                </span>
	                <span id = "notApplicableHelperIcon_${productFeatureCatGrpAppl_index}" <#if  productFeatureApplTypeId?exists && (productFeatureApplTypeId?string == "NA" || (product?has_content && productFeatureApplTypeId?string != "SELECTABLE_FEATURE" && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT"))><#else>style="display:none"</#if>>
	                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "NotApplicableFeatureHelperIconInfo", locale)/>
	                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	                </span>
	              </#if>
	              
	              <#assign distinguishProductFeatureMultiValue = ""/>
	              <#if (productFeatureGroupAndAppls?has_content) && (productFeatureGroupAndAppls?size > 1) && (productFeatureApplTypeId?string == "DISTINGUISHING_FEAT")>
		              <#assign productFeatureGroupAndApplsDesc = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productFeatureGroupAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureApplTypeId","DISTINGUISHING_FEAT")) />
		              <#list productFeatureGroupAndApplsDesc as productFeatureGroupAndApplDesc>
			              <#assign distinguishProductFeatureMultiValue = distinguishProductFeatureMultiValue + "${productFeatureGroupAndApplDesc.productFeatureId}@DISTINGUISHING_FEAT"/>
			              <#if productFeatureGroupAndApplDesc_has_next>
			                  <#assign distinguishProductFeatureMultiValue = distinguishProductFeatureMultiValue + ","/>
			              </#if>
			          </#list>
			      </#if>
	              <#assign distinguishProductFeatureMultiValue = parameters.get("distinguishProductFeatureMulti_${productFeatureCatGrpAppl.productFeatureGroupId}")!distinguishProductFeatureMultiValue!"">
	                    
	              <#assign productFeatureGroupAppls = delegator.findByAnd("ProductFeatureGroupAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureCatGrpAppl.productFeatureGroupId!""), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
	              <#-- assign productFeatureGroupAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureGroupApplList!)/ -->
	              <#if productFeatureGroupAppls?has_content>
	                <span id="distinguishFeatureValue_${productFeatureCatGrpAppl.productFeatureGroupId}">
	                    <span id="multipleInfo_${productFeatureCatGrpAppl.productFeatureGroupId}" class="productFeatureId_${productFeatureCatGrpAppl_index}" <#if !distinguishProductFeatureMultiValue?has_content>style="display:none"</#if>>
	                        ${uiLabelMap.MultipleInfo}
	                    </span>
		                
		                <select id="productFeatureId_${productFeatureCatGrpAppl_index}" name="productFeatureId_${productFeatureCatGrpAppl_index}" class="short" <#if (productFeatureApplTypeId?exists && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT") || (distinguishProductFeatureMultiValue?has_content)>style="display:none"</#if>>
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
		                    <option value="${optionValue!}" <#if !distinguishProductFeatureMultiValue?has_content && selectedFeture?has_content && selectedFeture.equals(optionValue)>selected</#if>><#if productFeatureName?has_content>${productFeatureName?if_exists}<#else>${productFeature.productFeatureId?if_exists}</#if></option>
		                  </#list>
		                </select>
	                </span>
	                
	              </#if>
	              
	              <#if product?has_content>
	                  <#assign formName = "updateProduct"/>
	              <#else>
	                  <#assign formName = "createProduct"/>
	              </#if>
	              
	              <span id="descriptiveFeaturePickerIcon_${productFeatureCatGrpAppl_index}" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT">style="display:none"</#if>>
	                  <input type="hidden" name="distinguishProductFeatureMulti_${productFeatureCatGrpAppl.productFeatureGroupId}" id="distinguishProductFeatureMulti_${productFeatureCatGrpAppl_index}" value="${distinguishProductFeatureMultiValue!""}" onchange="javascript:setVirtualFeatureDisplay(this);"/>
	                  <input type="hidden" name="distinguishProductFeatureNameMulti_${productFeatureCatGrpAppl.productFeatureGroupId}" id="distinguishProductFeatureNameMulti_${productFeatureCatGrpAppl_index}" value=""/>
	                  <a href="javascript:openLookup(document.${formName}.distinguishProductFeatureMulti_${productFeatureCatGrpAppl.productFeatureGroupId},document.${formName}.distinguishProductFeatureNameMulti_${productFeatureCatGrpAppl.productFeatureGroupId},'lookupFeature?featureTypeId=${productFeatureCatGrpAppl.productFeatureGroupId}&featureIdValue=distinguishProductFeatureMulti_${productFeatureCatGrpAppl_index}','500','700','center','true');"><span class="previewIcon"></span></a>
	              </span>
	                       
	              <#if (!product?has_content) ||( product?has_content && !(product.isVirtual = 'N' && product.isVariant = 'N'))>
	                <span id = "descriptiveHelperIcon_${productFeatureCatGrpAppl_index}" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT"><#else>style="display:none"</#if>>
	                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "DescriptiveFeatureHelperIconInfo", locale)/>
	                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	                </span>
	              </#if>
	              
	              
	            </td>
	          </tr>
          </#if>
          <#if productFeatureCatGrpAppl.productFeatureGroupId?has_content>
              <#assign changed = alreadyShownProductFeatureGroupId.add(productFeatureCatGrpAppl.productFeatureGroupId)/>
          </#if>
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
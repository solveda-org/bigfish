<div id="productCategoryFetureDetail">
<#if parameters.productCategoryId?has_content>
    <#if parameters.productId?has_content>
        <#assign product = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", parameters.productId))?if_exists>
    </#if>
    <#assign productCategoryMembers = delegator.findByAnd("ProductCategoryMember", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", parameters.productId))?if_exists>
    <#assign productCategoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMembers!)/>
    
    <#assign productCategoryIdList = Static["javolution.util.FastList"].newInstance()/>
    
    <#if productCategoryMembers?has_content>
        <#list productCategoryMembers as productCategoryMember>
            <#assign changedProductCategoryId = productCategoryIdList.add(productCategoryMember.productCategoryId)/>
        </#list>
    <#else>
        <#assign changedProductCategoryId = productCategoryIdList.add(parameters.productCategoryId)/>    
    </#if>
    
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
    <#assign alreadyShownProductFeatureGroupId = Static["javolution.util.FastList"].newInstance()/>
    <#assign count = 0?number/>
    <#list productCategoryIdList as productCategoryId>

    <#assign productFeatureCatGrpAppls = delegator.findByAnd("ProductFeatureCatGrpAppl", {"productCategoryId" : productCategoryId}, ["sequenceNum", "productFeatureGroupId"]) />
      <#if productFeatureCatGrpAppls?has_content>
        <#list productFeatureCatGrpAppls as productFeatureCatGrpAppl>
          <#if !alreadyShownProductFeatureGroupId.contains(productFeatureCatGrpAppl.productFeatureGroupId)>
	          <tr class="dataRow">
	            <td class="idCol firstCol">
		          <#if count == 0>
		            <div class="infoIcon">
		            <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.FeatureHelperInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
		            </div>
		          <#else>
		            &nbsp;
		          </#if>
		        </td>
		        <td class="idCol">
		          ${parameters.get("productFeatureGroupId_${count}")!productFeatureCatGrpAppl.productFeatureGroupId!""}
		          <input type="hidden" class="textEntry" name="productFeatureGroupId_${count}" id="productFeatureGroupId_${count}" value='${parameters.get("productFeatureGroupId_${count}")!productFeatureCatGrpAppl.productFeatureGroupId!""}' readOnly="true"/>
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
	                <#assign productFeatureApplTypeId = request.getParameter("productFeatureApplTypeId_${count}")!productFeatureApplType!''/>
	                <#if (!product?has_content) ||( product?has_content && (product.isVirtual = 'Y' || product.isVariant = 'Y'))>
	                  <input type="radio" name="productFeatureApplTypeId_${count}"  value="SELECTABLE_FEATURE" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "SELECTABLE_FEATURE">checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onclick="showFeature(this, '${count}')"/>${uiLabelMap.SelectableLabel}
	                </#if>
	                <input type="radio" name="productFeatureApplTypeId_${count}" value="DISTINGUISHING_FEAT" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT">checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onclick="showFeature(this, '${count}')"/>${uiLabelMap.DescriptiveLabel}
	                <input type="radio" name="productFeatureApplTypeId_${count}" value="NA" <#if  productFeatureApplTypeId?exists && (productFeatureApplTypeId?string == "NA" || (productFeatureApplTypeId?string == "SELECTABLE_FEATURE" && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT"))>checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onclick="showFeature(this, '${count}')"/>${uiLabelMap.NALabel}
	              <#else>
	                <#assign productFeatureApplTypeId = request.getParameter("productFeatureApplTypeId_${count}")!productFeatureApplType!''/>
	                <#if (!product?has_content) ||( product?has_content && (product.isVirtual = 'Y' || product.isVariant = 'Y'))>
	                  <span class="selectableRadio" <#if parameters.isVirtual?has_content && parameters.isVirtual == 'N'>style="display:none"</#if>>
	                    <input type="radio" name="productFeatureApplTypeId_${count}"  value="SELECTABLE_FEATURE" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "SELECTABLE_FEATURE">checked="checked"</#if> onclick="showFeature(this, '${count}')"/>${uiLabelMap.SelectableLabel}
	                  </span>
	                </#if>
	                <input type="radio" name="productFeatureApplTypeId_${count}" value="DISTINGUISHING_FEAT" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT">checked="checked"</#if> onclick="showFeature(this, '${count}')"/>${uiLabelMap.DescriptiveLabel}
	                <input type="radio" name="productFeatureApplTypeId_${count}" value="NA" <#if  productFeatureApplTypeId?exists && (productFeatureApplTypeId?string == "NA" || (product?has_content && productFeatureApplTypeId?string != "SELECTABLE_FEATURE" && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT"))>checked="checked"</#if> onclick="showFeature(this, '${count}')"/>${uiLabelMap.NALabel}
	              </#if>
	            </span>
	            &nbsp;                
	              <#if (!product?has_content) ||( product?has_content && !(product.isVirtual = 'N' && product.isVariant = 'N'))>
	                <span id = "selectedHelperIcon_${count}" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "SELECTABLE_FEATURE"><#else>style="display:none"</#if>>
	                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "SelectedFeatureHelperIconInfo",Static["org.ofbiz.base.util.UtilMisc"].toMap("featureType", "${productFeatureCatGrpAppl.productFeatureGroupId!}"), locale)/>
	                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	                </span>
	                <span id = "notApplicableHelperIcon_${count}" <#if  productFeatureApplTypeId?exists && (productFeatureApplTypeId?string == "NA" || (product?has_content && productFeatureApplTypeId?string != "SELECTABLE_FEATURE" && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT"))><#else>style="display:none"</#if>>
	                  <#assign tooltipData = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels", "NotApplicableFeatureHelperIconInfo", locale)/>
	                  <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${tooltipData!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
	                </span>
	              </#if>
	              
	              <#assign distinguishProductFeatureMultiValue = ""/>
	              <#if (productFeatureGroupAndAppls?has_content) && (productFeatureGroupAndAppls?size > 1) && (productFeatureApplTypeId?string == "DISTINGUISHING_FEAT")>
		              <#assign productFeatureGroupAndApplsDesc = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productFeatureGroupAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureApplTypeId","DISTINGUISHING_FEAT")) />
		              <#assign alreadyAddedDistinguishProductFeatureId = Static["javolution.util.FastList"].newInstance()/>
		              <#list productFeatureGroupAndApplsDesc as productFeatureGroupAndApplDesc>
		                  <#if !alreadyAddedDistinguishProductFeatureId.contains(productFeatureGroupAndApplDesc.productFeatureId)>
			                  <#assign distinguishProductFeatureMultiValue = distinguishProductFeatureMultiValue + "${productFeatureGroupAndApplDesc.productFeatureId}@DISTINGUISHING_FEAT"/>
			                  <#assign changedProductFeatureId = alreadyAddedDistinguishProductFeatureId.add(productFeatureGroupAndApplDesc.productFeatureId)/>
			                  <#if productFeatureGroupAndApplDesc_has_next>
			                      <#assign distinguishProductFeatureMultiValue = distinguishProductFeatureMultiValue + ","/>
			                  </#if>
			              </#if>
			          </#list>
			      </#if>
	              <#assign distinguishProductFeatureMultiValue = parameters.get("distinguishProductFeatureMulti_${productFeatureCatGrpAppl.productFeatureGroupId}")!distinguishProductFeatureMultiValue!"">
	                    
	              <#assign productFeatureGroupAppls = delegator.findByAnd("ProductFeatureGroupAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureCatGrpAppl.productFeatureGroupId!""), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
	              <#-- assign productFeatureGroupAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureGroupApplList!)/ -->
	              <#if productFeatureGroupAppls?has_content>
	                <span id="distinguishFeatureValue_${productFeatureCatGrpAppl.productFeatureGroupId}">
	                    <span id="multipleInfo_${productFeatureCatGrpAppl.productFeatureGroupId}" class="productFeatureId_${count}" <#if !distinguishProductFeatureMultiValue?has_content>style="display:none"</#if>>
	                        ${uiLabelMap.MultipleInfo}
	                    </span>
		                
		                <select id="productFeatureId_${count}" name="productFeatureId_${count}" class="short" <#if (productFeatureApplTypeId?exists && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT") || (distinguishProductFeatureMultiValue?has_content)>style="display:none"</#if>>
		                  <option value="">Select</option>
		                  <#assign selectedFeture = parameters.get("productFeatureId_${count}")!productFeatureId!"">
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
	              
	              <span id="descriptiveFeaturePickerIcon_${count}" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT">style="display:none"</#if>>
	                  <input type="hidden" name="distinguishProductFeatureMulti_${productFeatureCatGrpAppl.productFeatureGroupId}" id="distinguishProductFeatureMulti_${count}" value="${distinguishProductFeatureMultiValue!""}" onchange="javascript:setVirtualFeatureDisplay(this);"/>
	                  <input type="hidden" name="distinguishProductFeatureNameMulti_${productFeatureCatGrpAppl.productFeatureGroupId}" id="distinguishProductFeatureNameMulti_${count}" value=""/>
	                  <a href="javascript:openLookup(document.${formName}.distinguishProductFeatureMulti_${productFeatureCatGrpAppl.productFeatureGroupId},document.${formName}.distinguishProductFeatureNameMulti_${productFeatureCatGrpAppl.productFeatureGroupId},'lookupFeature?featureTypeId=${productFeatureCatGrpAppl.productFeatureGroupId}&featureIdValue=distinguishProductFeatureMulti_${count}','500','700','center','true');"><span class="previewIcon"></span></a>
	              </span>
	                       
	              <#if (!product?has_content) ||( product?has_content && !(product.isVirtual = 'N' && product.isVariant = 'N'))>
	                <span id = "descriptiveHelperIcon_${count}" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT"><#else>style="display:none"</#if>>
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
          <#assign count = count + 1/>
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
      </#list>
   </table>
    
</#if>
</div>
<#if product?has_content>
  <#if selFeatureTypesList?has_content>
    <#list selFeatureTypesList as selFeatureType>
      <#assign productFeatureList = delegator.findByAnd("ProductFeature", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId" , selFeatureType), Static["org.ofbiz.base.util.UtilMisc"].toList("description"))/>
      <#if productFeatureList?has_content>
        <div class="infoRow column">
          <div class="infoEntry">
            <div class="infoCaption">
              <label>
                <#assign productFeatureTypeLabel = ""/>
                <#if productFeatureTypesMap?has_content>
                  <#assign productFeatureTypeLabel = productFeatureTypesMap.get(selFeatureType)!"" />
                </#if>
                ${productFeatureTypeLabel!}:
              </label>
            </div>
            <div class="infoValue">
              <select id="selectableProductFeature_${selFeatureType}" name="selectableProductFeature_${selFeatureType}" class="short">
                <option value="">Select</option>
                
                <#assign selectedFetureParm = Static["org.ofbiz.base.util.StringUtil"].wrapString(parameters.get("selectableProductFeature_${selFeatureType}"))!"" />
                <#list productFeatureList as productFeature>
                  <#assign selectedFetureDb = "" />
                    
                    <#if variantProductStandardFeatureAndAppls?has_content>
                      <#assign variantProductFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(variantProductStandardFeatureAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toMap('productFeatureId',productFeature.productFeatureId!, 'productFeatureTypeId', selFeatureType))/>
                      <#if variantProductFeatureAndAppls?has_content>
                        <#assign variantProductFeatureAndAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(variantProductFeatureAndAppls) />
                        <#if !selectedFetureParm?has_content>
                          <#assign selectedFetureDb = "${variantProductFeatureAndAppl.productFeatureId!}@SELECTABLE_FEATURE" />
                        </#if>
                      </#if>
                    </#if>
                    
                    <#assign productFeatureName = productFeature.description?trim />
                    <#assign optionValue = "${productFeature.productFeatureId!}@SELECTABLE_FEATURE" />
                    <option value="${optionValue!}" <#if selectedFetureParm?has_content && selectedFetureParm == optionValue>selected<#elseif selectedFetureDb?has_content && selectedFetureDb == optionValue>selected</#if>><#if productFeatureName?has_content>${productFeatureName?if_exists}<#else>${productFeature.productFeatureId?if_exists}</#if></option>
                </#list>
              </select>
            </div>
          </div>
        </div>
      </#if>
    </#list>
  </#if>
  
  <div class="infoRow row">
    <div class="header"><h2>${uiLabelMap.ProductDistinguishingFeaturesHeading}</h2></div>
  </div>
  
  <#if descFeatureTypesList?has_content>
    <#list descFeatureTypesList as descFeatureType>
      <#assign productFeatureList = delegator.findByAnd("ProductFeature", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureTypeId" , descFeatureType), Static["org.ofbiz.base.util.UtilMisc"].toList("description"))/>
      <#if productFeatureList?has_content>
        <div class="infoRow column">
          <div class="infoEntry">
            <div class="infoCaption">
              <label>
                <#assign productFeatureTypeLabel = ""/>
                <#if productFeatureTypesMap?has_content>
                  <#assign productFeatureTypeLabel = productFeatureTypesMap.get(descFeatureType)!"" />
                </#if>
                ${productFeatureTypeLabel!}:
              </label>
            </div>
            
            <div class="infoValue" id="distinguishFeatureValue_${descFeatureType}">
              <#assign distinguishProductFeatureMultiValue = ""/>
              <#if variantProductDescFeatureAndAppls?has_content>
                  <#assign variantProductDescFeatureAndApplsByType = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(variantProductDescFeatureAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toMap('productFeatureTypeId', descFeatureType))/>
              </#if>
              <#if (variantProductDescFeatureAndApplsByType?has_content) && (variantProductDescFeatureAndApplsByType?size > 1)>
                  <#assign alreadyAddedDistinguishProductFeatureId = Static["javolution.util.FastList"].newInstance()/>
	              <#list variantProductDescFeatureAndApplsByType as variantProductDescFeatureAndApplByType>
	                  <#if !alreadyAddedDistinguishProductFeatureId.contains(variantProductDescFeatureAndApplByType.productFeatureId)>
	                      <#assign distinguishProductFeatureMultiValue = distinguishProductFeatureMultiValue + "${variantProductDescFeatureAndApplByType.productFeatureId}@DISTINGUISHING_FEAT"/>
	                      <#assign changedProductFeatureId = alreadyAddedDistinguishProductFeatureId.add(variantProductDescFeatureAndApplByType.productFeatureId)/>
			              <#if variantProductDescFeatureAndApplByType_has_next>
			                  <#assign distinguishProductFeatureMultiValue = distinguishProductFeatureMultiValue + ","/>
			              </#if>
	                  </#if>
		          </#list>
		      </#if>
              <#assign distinguishProductFeatureMultiValue = parameters.get("distinguishProductFeatureMulti_${descFeatureType}")!distinguishProductFeatureMultiValue!"">
            
              <input type="hidden" name="distinguishProductFeatureMulti_${descFeatureType}" id="distinguishProductFeatureMulti_${descFeatureType}" value="${distinguishProductFeatureMultiValue!""}" onchange="javascript:setFeatureDisplay(this);"/>
            <input type="hidden" name="distinguishProductFeatureNameMulti_${descFeatureType}" id="distinguishProductFeatureNameMulti_${descFeatureType}" value=""/>
              
              <span id="multipleInfo_${descFeatureType}" <#if !distinguishProductFeatureMultiValue?has_content>style="display:none"</#if>>
                  ${uiLabelMap.MultipleInfo}
              </span>
              
              
              <select id="distinguishProductFeature_${descFeatureType}" name="distinguishProductFeature_${descFeatureType}" class="short" <#if distinguishProductFeatureMultiValue?has_content>style="display:none"</#if> >
                <option value="">Select</option>
                <#assign descFetureParm = Static["org.ofbiz.base.util.StringUtil"].wrapString(parameters.get("distinguishProductFeature_${descFeatureType}"))!"" />
                <#-- assign selectedFeture = selectedFeture.substring(0, selectedFeture.indexOf('@')) / -->
                <#list productFeatureList as productFeature>
                  <#assign descFetureDb = "" />
                  
                    <#if variantProductDescFeatureAndAppls?has_content>
                      <#assign variantProductFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(variantProductDescFeatureAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toMap('productFeatureId',productFeature.productFeatureId!, 'productFeatureTypeId', descFeatureType))/>
                      <#if variantProductFeatureAndAppls?has_content>
                        <#assign variantProductFeatureAndAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(variantProductFeatureAndAppls) />
                        <#if !descFetureParm?has_content>
                          <#assign descFetureDb = "${variantProductFeatureAndAppl.productFeatureId}@DISTINGUISHING_FEAT" />
                        </#if>
                      </#if>
                    </#if>
                  
                    <#assign productFeatureName = productFeature.description?trim/>
                    <#assign optionValue = "${productFeature.productFeatureId}@DISTINGUISHING_FEAT" />
                    <#--<#assign optValue = productFeature.productFeatureId />-->
                    <option value="${optionValue!}" <#if !distinguishProductFeatureMultiValue?has_content && descFetureParm?has_content && descFetureParm == optionValue>selected<#elseif !distinguishProductFeatureMultiValue?has_content && descFetureDb?has_content && descFetureDb == optionValue>selected</#if>><#if productFeatureName?has_content>${productFeatureName?if_exists}<#else>${productFeature.productFeatureId?if_exists}</#if></option>
                </#list>
              </select>
            </div>
            
            <div class="infoIcon">
                <a href="javascript:openLookup(document.${detailFormName!}.distinguishProductFeatureMulti_${descFeatureType},document.${detailFormName!}.distinguishProductFeatureNameMulti_${descFeatureType},'lookupFeature?featureTypeId=${descFeatureType}&featureIdValue=distinguishProductFeatureMulti_${descFeatureType}','500','700','center','true');"><span class="previewIcon"></span></a>
            </div>
            
            <div class="infoIcon">
                <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.AddDescriptiveFeatureHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
          </div>
        </div>
      </#if>
    </#list>
  </#if>
</#if>
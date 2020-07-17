<div id="productCategoryFetureDetail">
<#if parameters.productCategoryId?has_content>
    <#if parameters.productId?has_content>
        <#assign product = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", parameters.productId))?if_exists>
    </#if>
    <#assign productFeatureCatGrpApplList = delegator.findByAnd("ProductFeatureCatGrpAppl", {"productCategoryId" : parameters.productCategoryId}, ["sequenceNum", "productFeatureGroupId"])>
    <#assign productFeatureCatGrpAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureCatGrpApplList!)/>
    <#if productFeatureCatGrpAppls?has_content>
        <div class="infoRow row">
            <div class="header"><h2>${uiLabelMap.ProductFeatureHeading}</h2></div>
        </div>
        <#list productFeatureCatGrpAppls as productFeatureCatGrpAppl>
            <div class="infoRow">
                <div class="infoEntry long">
                    <div class="infoCaption">
                        <label>${uiLabelMap.FeatureCaption}</label>
                    </div>
                    <div class="infoValue small">
                        <input type="text" class="textEntry" name="productFeatureGroupId_${productFeatureCatGrpAppl_index}" id="productFeatureGroupId_${productFeatureCatGrpAppl_index}" value='${parameters.get("productFeatureGroupId_${productFeatureCatGrpAppl_index}")!productFeatureCatGrpAppl.productFeatureGroupId!""}' readOnly="true"/>
                    </div>
                </div>
                <div class="infoEntry long">
                    <div class="infoCaption">&nbsp;</div>
                    <div class="infoValue small">
                        <div class="entry checkbox medium">
                            <#assign productFeatureApplType = ""/>
                            <#assign productFeatureId = ""/>
                            <#assign productFeatureGroupAndAppls = delegator.findByAnd("ProdFeaGrpAppAndProdFeaApp", {"productId" : parameters.productId!"", "productFeatureGroupId" : productFeatureCatGrpAppl.productFeatureGroupId!""})>
                            <#if productFeatureGroupAndAppls?has_content>
                                <#assign productFeatureGroupAndAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureGroupAndAppls) />
                                <#assign productFeatureApplType = productFeatureGroupAndAppl.productFeatureApplTypeId! />
                                <#assign productFeatureId = productFeatureGroupAndAppl.productFeatureId! />
                            </#if>
                            <#assign productFeatureApplTypeId = request.getParameter("productFeatureApplTypeId_${productFeatureCatGrpAppl_index}")!productFeatureApplType!''/>
                            <input class="checkBoxEntry" type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}"  value="SELECTABLE_FEATURE" <#if productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "SELECTABLE_FEATURE">checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onchange="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.SelectableLabel}
                            <input class="checkBoxEntry" type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}" value="DISTINGUISHING_FEAT" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "DISTINGUISHING_FEAT">checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onchange="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.DescriptiveLabel}
                            <input class="checkBoxEntry" type="radio" name="productFeatureApplTypeId_${productFeatureCatGrpAppl_index}" value="NA" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string == "NA">checked="checked"</#if> <#if product?has_content>disabled="disabled"</#if> onchange="showFeature(this, '${productFeatureCatGrpAppl_index}')"/>${uiLabelMap.NALabel}
                        </div>
                    </div>
               </div>
               <div class="infoEntry long">
                   <div class="infoCaption">&nbsp;</div>
                   <div class="infoValue small">
                       <#assign productFeatureGroupApplList = delegator.findByAnd("ProductFeatureGroupAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureGroupId" , productFeatureCatGrpAppl.productFeatureGroupId!""), Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum"))/>
                       <#assign productFeatureGroupAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureGroupApplList!)/>
                       <#if productFeatureGroupAppls?has_content>
                           <select id="productFeatureId_${productFeatureCatGrpAppl_index}" name="productFeatureId_${productFeatureCatGrpAppl_index}" class="short" <#if  productFeatureApplTypeId?exists && productFeatureApplTypeId?string != "DISTINGUISHING_FEAT">style="display:none"</#if> <#if product?has_content>disabled="disabled"</#if>>
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
                   </div>
               </div>
           </div>
        </#list>
    </#if>
</#if>
</div>
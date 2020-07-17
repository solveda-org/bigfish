<#if (mode?has_content && mode == "add") && (!virtualProduct?has_content)>
    <#if rootProductCategoryId?has_content>
      <#assign topLevelList = delegator.findByAnd("ProductCategoryRollupAndChild", {"parentProductCategoryId" : rootProductCategoryId}, Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum')) />
      <#assign topLevelList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(topLevelList) />
    </#if>
    <div class="infoRow row">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.CategoryCaption}</label>
            </div>
            <div class="infoValue">
                <select id="productCategoryId" name="productCategoryId">
                    <option value="" <#if (parameters.productCategoryId!"") == "">selected</#if>>${uiLabelMap.SelectOneLabel}</option>
                    <#if topLevelList?exists && topLevelList?has_content>
                        <#list topLevelList as category>
                            <option value="${category.productCategoryId?if_exists}" <#if (parameters.productCategoryId!"") == "${category.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;${category.categoryName?if_exists}</option>
                            <#assign subCatList = delegator.findByAnd("ProductCategoryRollupAndChild", {"parentProductCategoryId" : category.getString("productCategoryId")}, Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum')) />
                            <#assign subCatList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(subCatList) />
                            <#if subCatList?exists && subCatList?has_content>
                                <#list subCatList as subCategory>
                                    <option value="${subCategory.productCategoryId?if_exists}" <#if (parameters.productCategoryId!"") == "${subCategory.productCategoryId?if_exists}">selected</#if>>&nbsp;&nbsp;&nbsp;&nbsp;${subCategory.categoryName?if_exists}</option>
                                </#list>
                            </#if>
                        </#list>
                    </#if>
                </select>
            </div>
        </div>
    </div>
<#elseif (mode?has_content && mode == "edit") && (isVirtual = 'Y' || isFinished = 'Y')>
    <#if prodCatList?exists>
        <#list prodCatList as productCategoryAndMember>
        	<#assign hasNext = productCategoryAndMember_has_next>	
            <#assign primaryProdCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategoryAndMember.primaryParentCategoryId?if_exists), false)/>
            <#if primaryProdCategory?exists>
                <div class="infoRow column">
                    <div class="infoEntry">
                        <div class="infoCaption">
                            <label>${uiLabelMap.NavBarCaption}</label>
                        </div>
                        <div class="infoValue">
                            ${primaryProdCategory.categoryName!""}
                            <#if productCategoryAndMember_index == 0>
                                <input type="hidden" name="primaryParentCategoryId" id="primaryParentCategoryId" value="${primaryProdCategory.productCategoryId!""}"/>
                            </#if>
                        </div>
                    </div>
                </div>
            </#if>

            <div class="infoRow column">
                <div class="infoEntry">
                    <div class="infoCaption">
                      <label>${uiLabelMap.SubItemCaption}</label>
                    </div>
                    <div class="infoValue">
                      ${productCategoryAndMember.categoryName!""}
                      <#if productCategoryAndMember_index == 0>
                          <input type="hidden" name="productCategoryId" id="productCategoryId" value="${productCategoryAndMember.productCategoryId!""}"/>
                      </#if>
                    </div>
                    <#if !hasNext>
	                    <div class="infoIcon">
	                      <a href="<@ofbizUrl>productCategoryMembershipDetail?productId=${parameters.productId!currentProduct.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductcategoryMembershipTooltip}');" onMouseout="hideTooltip()"><span class="membershipIcon"></span></a>
	                    </div>
                    </#if>
                </div>
            </div>
        </#list>
    </#if>
</#if>



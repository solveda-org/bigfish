<#if (requestAttributes.facetCatList)?exists><#assign facetCatList = requestAttributes.facetCatList></#if>
<#if (currentProductCategoryContentWrapper)?exists>
  <#assign categoryName = currentProductCategoryContentWrapper.get("CATEGORY_NAME")!currentProductCategory.categoryName!"">
  <#assign longDescription = currentProductCategoryContentWrapper.get("LONG_DESCRIPTION")!currentProductCategory.longDescription!"">
  <#assign categoryImageUrl = currentProductCategoryContentWrapper.get("CATEGORY_IMAGE_URL")!"">
  <#assign categoryId = currentProductCategoryContentWrapper.get("PRODUCT_CATEGORY_ID")!"">
	<#assign pageTopProdCatContentTypeId = 'PLP_ESPOT_PAGE_TOP'/>
   <#if pageTopProdCatContentTypeId?exists && pageTopProdCatContentTypeId?has_content>
  <#assign pageTopProductCategoryContentList = delegator.findByAnd("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , categoryId?string, "prodCatContentTypeId" , pageTopProdCatContentTypeId?if_exists)) />
  
  <#if pageTopProductCategoryContentList?has_content>
    <#assign pageTopProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(pageTopProductCategoryContentList?if_exists) />
    <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(pageTopProductCategoryContentList) />
    <#assign pageTopContentId = prodCategoryContent.contentId?if_exists />
  </#if>
  <#if pageTopContentId?exists >
    <#assign pageTopPlpEspotContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", pageTopContentId), true) />
  </#if>
</#if>
</#if>
<h1>${categoryName!pageTitle!""}</h1>

  <#if pageTopPlpEspotContent?has_content>
    <#if ((pageTopPlpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
      <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
        <@renderContentAsText contentId="${pageTopContentId}" ignoreTemplate="true"/>
      </div>
    </#if>
  </#if>

 <div class="resultCategoryListContainer">
    <#if facetCatList?has_content>
        <div id="eCommerceCategoryList">
        <#assign facet = facetCatList[0] >
        <#if facet.refinementValues?has_content>
            <#list facet.refinementValues as category>
               ${setRequestAttribute("clpItem",category)}
                <!-- DIV for Displaying Product Categories STARTS here -->
                <div class="eCommerceListItem categoryListItem">
                        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#clpDivSequence")}
                </div>
                 <!-- DIV for Displaying Product Categories ENDS here -->
            </#list>
        </#if>
        </div>
   </#if>
</div>
<#assign pageEndProdCatContentTypeId = 'PLP_ESPOT_PAGE_END'/>
<#if pageEndProdCatContentTypeId?exists && pageEndProdCatContentTypeId?has_content>
  <#assign pageEndProductCategoryContentList = delegator.findByAnd("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , categoryId?string, "prodCatContentTypeId" , pageEndProdCatContentTypeId?if_exists)) />
  
  <#if pageEndProductCategoryContentList?has_content>
    <#assign pageEndProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(pageEndProductCategoryContentList?if_exists) />
    <#assign pageEndProdCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(pageEndProductCategoryContentList) />
    <#assign pageEndContentId = pageEndProdCategoryContent.contentId?if_exists />
  </#if>
  <#if pageEndContentId?exists >
    <#assign pageEndPlpEspotContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", pageEndContentId), true) />
  </#if>
</#if>

<#if pageEndPlpEspotContent?has_content>
  <#if ((pageEndPlpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
    <div id="eCommercePlpEspot_${categoryId}" class="plpEspot endContent">
      <@renderContentAsText contentId="${pageEndContentId}" ignoreTemplate="true"/>
    </div>
  </#if>
</#if>
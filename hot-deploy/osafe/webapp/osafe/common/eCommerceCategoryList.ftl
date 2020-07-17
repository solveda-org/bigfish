<#if (requestAttributes.facetCatList)?exists>
  <#assign facetCatList = requestAttributes.facetCatList>
</#if>
<#if currentProductCategory?has_content>
  <#assign categoryName = currentProductCategory.categoryName!"">
  <#assign longDescription = currentProductCategory.longDescription!""> 
  <#assign categoryImageUrl = currentProductCategory.categoryImageUrl!"">
  <#assign categoryId = currentProductCategory.productCategoryId!"">
  <#assign categoryContentList = currentProductCategory.getRelatedCache("ProductCategoryContent")/>
  <#assign categoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(categoryContentList,true) />
  <#if categoryContentList?has_content>
    <#assign pageTopCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(categoryContentList,Static["org.ofbiz.base.util.UtilMisc"].toMap("prodCatContentTypeId","PLP_ESPOT_PAGE_TOP")) />
    <#if pageTopCategoryContentList?has_content>
      <#assign pageTopCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(pageTopCategoryContentList) />
      <#assign pageTopContent = pageTopCategoryContent.getRelatedOneCache("Content")/>
      <#if pageTopContent.statusId?has_content>
        <#if (pageTopContent.statusId == "CTNT_PUBLISHED")>
          <#assign pageTopContentId = pageTopContent.contentId/>
        </#if>
      </#if>
    </#if>
    <#assign pageEndCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(categoryContentList,Static["org.ofbiz.base.util.UtilMisc"].toMap("prodCatContentTypeId","PLP_ESPOT_PAGE_END")) />
    <#if pageEndCategoryContentList?has_content>
      <#assign pageEndCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(pageEndCategoryContentList) />
      <#assign pageEndContent = pageEndCategoryContent.getRelatedOneCache("Content")/>
      <#if pageEndContent.statusId?has_content>
        <#if (pageEndContent.statusId == "CTNT_PUBLISHED")>
          <#assign pageEndContentId = pageEndContent.contentId/>
        </#if>
      </#if>
    </#if>
  </#if>
</#if>  
<h1>${categoryName!pageTitle!""}</h1>
<#if pageTopContentId?has_content>
  <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
    <@renderContentAsText contentId="${pageTopContentId}" ignoreTemplate="true"/>
  </div>
</#if>

  <#if facetCatList?has_content>
    <div id="eCommerceCategoryList">
     <div class="boxList categoryList">
      <#assign facet = facetCatList[0] >
      <#if facet.refinementValues?has_content>
        <#list facet.refinementValues as category>
         ${setRequestAttribute("clpItem",category)}
          <!-- DIV for Displaying Product Categories STARTS here -->
            ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#clpDivSequence")}
          <!-- DIV for Displaying Product Categories ENDS here -->
        </#list>
      </#if>
    </div>
  </#if>

<#if pageEndContentId?has_content>
  <div id="eCommercePlpEspot_${categoryId}" class="plpEspot endContent">
    <@renderContentAsText contentId="${pageEndContentId}" ignoreTemplate="true"/>
  </div>
</#if>

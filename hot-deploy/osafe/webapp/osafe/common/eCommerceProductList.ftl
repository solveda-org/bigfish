<#-- variable setup and worker calls -->
<#assign categoryId = ""/>
<#if (currentProductCategoryContentWrapper)?exists>
    <#assign categoryName = currentProductCategoryContentWrapper.get("CATEGORY_NAME")!currentProductCategory.categoryName!"">
    <#assign longDescription = currentProductCategoryContentWrapper.get("LONG_DESCRIPTION")!currentProductCategory.longDescription!"">
    <#assign categoryImageUrl = currentProductCategoryContentWrapper.get("CATEGORY_IMAGE_URL")!"">
    <#assign categoryId = currentProductCategoryContentWrapper.get("PRODUCT_CATEGORY_ID")!"">
</#if>
<#if !categoryId?has_content>
  <#assign categoryId = parameters.productCategoryId?if_exists />
</#if>
<#if (requestAttributes.documentList)?exists><#assign documentList = requestAttributes.documentList></#if>
<#if (requestAttributes.pageSize)?exists><#assign pageSize = requestAttributes.pageSize!10></#if>
<#if (requestAttributes.numFound)?exists><#assign numFound = requestAttributes.numFound!></#if>

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
<h1>${StringUtil.wrapString(categoryName!pageTitle!"")}</h1>

  <#if pageTopPlpEspotContent?has_content>
    <#if ((pageTopPlpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
      <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
        <@renderContentAsText contentId="${pageTopContentId}" ignoreTemplate="true"/>
      </div>
    </#if>
  </#if>

<div class="resultListContainer">
  <#if !pageSize?exists>
        <#assign pageSize =10/>
  </#if>
  
  <#if (numFound?has_content && (numFound gt 1))>
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#plpPagingControlsTop")}
  </#if>
  <!-- List of products  -->
       <#if documentList?has_content>
       <#assign PRODUCT_STORE_PARM_FACET = PLP_FACET_GROUP_VARIANT_SWATCH_IMG!""/>
        ${setRequestAttribute("PRODUCT_STORE_PARM_FACET",PRODUCT_STORE_PARM_FACET)}
       <#if PRODUCT_STORE_PARM_FACET?has_content>
          <#assign PRODUCT_STORE_PARM_FACET=PRODUCT_STORE_PARM_FACET.toUpperCase()/>
           ${setRequestAttribute("PRODUCT_STORE_PARM_FACET",PRODUCT_STORE_PARM_FACET)}
       </#if>
       <#assign featureValueSelected=""/>
       ${setRequestAttribute("featureValueSelected",featureValueSelected)}
       <#if facetGroups?has_content && PRODUCT_STORE_PARM_FACET?has_content>
          <#list facetGroups as facet>
            <#if PRODUCT_STORE_PARM_FACET == facet.facet>
                <#assign featureValueSelected=facet.facetValue!""/>
                 ${setRequestAttribute("featureValueSelected",featureValueSelected)}
            </#if>
          </#list>
       </#if>
       <#if searchTextGroups?has_content && PRODUCT_STORE_PARM_FACET?has_content>
          <#list searchTextGroups as facet>
            <#if PRODUCT_STORE_PARM_FACET == facet.facet>
                <#assign featureValueSelected=facet.facetValue!""/>
                ${setRequestAttribute("featureValueSelected",featureValueSelected)}
            </#if>
          </#list>
       </#if>
       
       <!-- PLP page parent DIV STARTS-->
         <div id="eCommerceProductList">
            <#list documentList as document>
              ${setRequestAttribute("plpItem",document)}
                      <#assign productId = document.productId!"">
                 <!-- DIV for Displaying PLP item STARTS here -->
                      <div class="eCommerceListItem productListItem">
                        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#plpDivSequence")}
                      </div>
                 <!-- DIV for Displaying PLP item ENDS here -->     
            </#list>
            <div class="spacer"></div>
         </div>
       <!-- PLP page parent DIV ENDS -->
        </#if>
        <#if (numFound?has_content && (numFound gt 1))>
          ${screens.render("component://osafe/widget/EcommerceScreens.xml#plpPagingControlsBottom")}
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
    <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
      <@renderContentAsText contentId="${pageEndContentId}" ignoreTemplate="true"/>
    </div>
  </#if>
</#if>
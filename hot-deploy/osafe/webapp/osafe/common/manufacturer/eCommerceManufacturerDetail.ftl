<#-- These hidden inputs are used when using addToCart and addToWishlist -->
<form method="post" name="${formName!'productListForm'}" id="productListForm">
  <input type="hidden" name="plp_qty" id="plp_qty" value=""/>
  <input type="hidden" name="plp_add_product_id" id="plp_add_product_id" value=""/>
  <input type="hidden" name="plp_add_category_id" id="plp_add_category_id" value=""/> 
  <input type="hidden" name="manufacturer_party_id" id="last_viewed_manufacturer_party_id" value="${manufacturerPartyId!""}"/> 
  <input type="hidden" name="productListFormSearchText" id="productListFormSearchText" value="${productListFormSearchText!""}"/> 
  <#-- flag to display success message -->
  <input type="hidden" name="showSuccess" id="showSuccess" value="Y"/> 
</form>

<#-- variable setup and worker calls -->
<#if (requestAttributes.pageSize)?exists><#assign pageSize = requestAttributes.pageSize!10></#if>
<#if (requestAttributes.numFound)?exists><#assign numFound = requestAttributes.numFound!></#if>

<#assign categoryId = ""/>
<#if currentProductCategory?exists>
    <#assign categoryName = currentProductCategory.categoryName!"">
    <#assign longDescription = currentProductCategory.longDescription!"">
    <#assign categoryImageUrl = currentProductCategory.categoryImageUrl!"">
    <#assign categoryId = currentProductCategory.productCategoryId!"">
</#if>
<#if !categoryId?has_content>
  <#assign categoryId = parameters.productCategoryId?if_exists />
</#if>

<#if currentProductCategory?has_content>
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


  <#if pageTopContentId?has_content>
      <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
        <@renderContentAsText contentId="${pageTopContentId}" ignoreTemplate="true"/>
      </div>
  </#if>

  <!-- List of products  -->
   <#if manufacturerProductList?has_content>
       <#assign plpFacetGroupVariantSwatch = Static["com.osafe.util.Util"].getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_SWATCH_IMG")!""/>
       <#assign plpFacetGroupVariantSticky =  Static["com.osafe.util.Util"].getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_PDP_MATCH")!""/>
       <#assign facetGroupMatch = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_GROUP_VARIANT_MATCH")!""/>
       
       ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_SWATCH",plpFacetGroupVariantSwatch)}
       <#if plpFacetGroupVariantSwatch?has_content>
          <#assign plpFacetGroupVariantSwatch=plpFacetGroupVariantSwatch.toUpperCase()/>
           ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_SWATCH",plpFacetGroupVariantSwatch)}
       </#if>
       
       <#if plpFacetGroupVariantSticky?has_content>
          <#assign plpFacetGroupVariantSticky=plpFacetGroupVariantSticky.toUpperCase()/>
           ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_STICKY",plpFacetGroupVariantSticky)}
       </#if>
       
       <#assign featureValueSelected=""/>
       ${setRequestAttribute("featureValueSelected",featureValueSelected)}

       <#if facetGroupMatch?has_content>
          <#assign facetGroupMatch=facetGroupMatch.toUpperCase()/>
           ${setRequestAttribute("FACET_GROUP_VARIANT_MATCH",facetGroupMatch)}
       </#if>
       
       <#if facetGroups?has_content && facetGroupMatch?has_content>
          <#list facetGroups as facet>
            <#if facetGroupMatch == facet.facet>
                <#assign featureValueSelected=facet.facetValue!""/>
                 ${setRequestAttribute("featureValueSelected",featureValueSelected)}
                 <#break>
            </#if>
          </#list>
       </#if>
       
       <#if searchTextGroups?has_content && facetGroupMatch?has_content>
          <#list searchTextGroups as facet>
            <#if facetGroupMatch == facet.facet>
                <#assign featureValueSelected=facet.facetValue!""/>
                ${setRequestAttribute("featureValueSelected",featureValueSelected)}
                 <#break>
            </#if>
          </#list>
       </#if>
       <div class="manufacturerDetail">
  			${screens.render("component://osafe/widget/EcommerceDivScreens.xml#manufacturerDivSequence")}
	   </div>  	   
   </#if>

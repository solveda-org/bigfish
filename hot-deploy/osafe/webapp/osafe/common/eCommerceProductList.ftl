<#-- These hidden inputs are used when using addToCart and addToWishlist from PLP -->
<form method="post" name="${formName!'productListForm'}" id="productListForm">
  <input type="hidden" name="plp_qty" id="plp_qty" value=""/>
  <input type="hidden" name="plp_add_product_id" id="plp_add_product_id" value=""/>
  <input type="hidden" name="plp_add_category_id" id="plp_add_category_id" value=""/> 
  <input type="hidden" name="productListFormSearchText" id="productListFormSearchText" value="${productListFormSearchText!""}"/>
  
  <#if searchTermsMap?has_content>
      <#list searchTermsMap.entrySet() as entry>
          <#if entry.value?has_content>
	          <input type="hidden" name="${entry.key}" id="${entry.key}" value="${entry.value!""}"/>
	      </#if>
      </#list>
  </#if> 
  <#-- flag to display success message -->
  <input type="hidden" name="showSuccess" id="showSuccess" value="Y"/> 
</form>
  
<#-- variable setup and worker calls -->
<#if (requestAttributes.documentList)?exists><#assign documentList = requestAttributes.documentList></#if>
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


<h1>${StringUtil.wrapString(categoryName!pageTitle!"")}</h1>
<#if pageSubTitle?exists && pageSubTitle?has_content>
 <h3>${StringUtil.wrapString(pageSubTitle!"")}</h3>
</#if>

  <#if pageTopContentId?has_content>
      <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
        <@renderContentAsText contentId="${pageTopContentId}" ignoreTemplate="true"/>
      </div>
  </#if>


  <#if !pageSize?exists>
        <#assign pageSize =10/>
  </#if>
  
  <#if (numFound?has_content && (numFound gt 1))>
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#plpPagingControlsTop")}
  </#if>
  <!-- List of products  -->
       <#if documentList?has_content>
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
       
       <#if searchTextGroups?has_content && facetGroupMatch?has_content>
          <#list searchTextGroups as facet>
            <#if facetGroupMatch == facet.facet>
                <#assign featureValueSelected=facet.facetValue!""/>
                ${setRequestAttribute("featureValueSelected",featureValueSelected)}
            </#if>
          </#list>
       </#if>
       
       <!-- PLP page parent DIV STARTS-->
         <div id="eCommerceProductList">
          <div class="boxList productList">
            <#list documentList as document>
                ${setRequestAttribute("plpItem",document)}
                <#assign productId = document.productId!"">
                ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#plpDivSequence")}
                <#if isPlpPdpInStoreOnly?exists>
                    <script language="JavaScript" type="text/javascript">eval(checkProductInStorePlp('${isPlpPdpInStoreOnly}', '${uiSequenceScreen}_${plpProduct.productId}'));</script>
                </#if>
                ${virtualJavaScript?if_exists}
                ${virtualDefaultJavaScript?if_exists}
                <#-- Prefill first select box (virtual products only) -->
                <#if plpVariantTree?exists && 0 < plpVariantTree.size()>
                  <#assign rowNo = 0/>
                  <#list plpFeatureOrder as feature>
                      <#if rowNo == 0>
                          <script language="JavaScript" type="text/javascript">eval("list" + "${StringUtil.wrapString(feature)}_${uiSequenceScreen}_${plpProduct.productId}" + "()");</script>
                      <#else>
                          <script language="JavaScript" type="text/javascript">eval("listFT" + "${StringUtil.wrapString(feature)}_${uiSequenceScreen}_${plpProduct.productId}" + "()");</script>
                          <script language="JavaScript" type="text/javascript">eval("listLiFT" + "${StringUtil.wrapString(feature)}_${uiSequenceScreen}_${plpProduct.productId}" + "()");</script>
                      </#if>
                  <#assign rowNo = rowNo + 1/>
                  </#list> 
                </#if>
            </#list>
          </div>
        </div>
       <!-- PLP page parent DIV ENDS -->
        </#if>
        <#if (numFound?has_content && (numFound gt 1))>
          ${screens.render("component://osafe/widget/EcommerceScreens.xml#plpPagingControlsBottom")}
        </#if>
<!-- The section is displayed when there are no products for a particular filter. -->
<#if (facetGroups?has_content && (facetGroups.size() gt 0) && !(documentList?has_content))>
    <div class="facetNavNoResultMsg">
      ${uiLabelMap.FacetNavNoResultsInfo}
    </div>
    <div class="facetNavNoResultSuggestionMsg">
      ${uiLabelMap.FacetNavNoResultsSuggestionInfo} 
    </div>
</#if>
<#if pageEndContentId?has_content>
	<div id="eCommercePlpEspot_${categoryId}" class="plpEspot endContent">
	  <@renderContentAsText contentId="${pageEndContentId}" ignoreTemplate="true"/>
	</div>
</#if>

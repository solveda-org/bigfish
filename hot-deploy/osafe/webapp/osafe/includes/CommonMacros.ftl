
<#-- REQUIRED LABELS ---------------------------------------------------------------------- -->
<#macro required>
<span class="required">*</span>
</#macro>
<#-- ---------------------------------------------------------------------- -->

<#-- FIELD ERRORS ---------------------------------------------------------------------- -->
<#macro fieldErrors fieldName>
  <#if errorMessageList?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName, true, errorMessageList)>
    <#if fieldMessages?has_content>
	    <ul class="fieldErrorMessage">
	      <#list fieldMessages as errorMsg>
	        <li>${errorMsg}</li>
	      </#list>
	    </ul>
    </#if>
  </#if>
</#macro>
<#-- ---------------------------------------------------------------------- -->

<#--Facet Navigation  --------------------------------------------------------------------- -->

<#macro facetLine facet facetType refinementValueName refinementValue multiFacetRefinedExist multiFacetRefined multiFacetInitialType facetGroupParamList allSelected showItemCount>

    <#assign facetShowItemCount = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"FACET_SHOW_ITEM_CNT")/>
    <#if showItemCount?has_content && showItemCount == "N">
    	<#assign facetShowItemCount = false />
    </#if>
    <#assign filterGroupParamMap = parameters.filterGroupParamMap!requestAttributes.filterGroupParamMap! />
    <#assign categoryId = ""/>
    <#if currentProductCategory?exists>
        <#assign categoryId = currentProductCategory.productCategoryId!"">
    </#if>
    <#if !categoryId?has_content>
        <#assign categoryId = parameters.productCategoryId?if_exists />
    </#if>
    <#assign catOrSearchText = ""/>
    <#if parameters.searchText?has_content>
        <#assign catOrSearchText = "eCommerceProductList?searchText=" + parameters.searchText/>
    <#else>
        <#assign catOrSearchText = "siteSearch?productCategoryId=" + categoryId/>
    </#if>
    <#assign refinementValueDisplayName = "">
    <#assign refinementURL = "">
    <#if refinementValue?has_content>
    	<#assign refinementValueDisplayName = refinementValue.displayName/>
    	<#assign refinementURL = refinementValue.refinementURL/>
    </#if>
    <#assign productCategoryUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${refinementURL}')/>
    
    <#-- Determine count -->
    <#if refinementValue?has_content>
	    <#assign scalarCount = refinementValue.scalarCount/>
	    <#assign useDisable = true/>
	    <#if multiFacetInitialType?has_content && multiFacetInitialType.equalsIgnoreCase(facetType)>
	        <#assign useDisable = false/>
	    </#if>
	    <#if useDisable>
	        <#assign disabled = true/>
	    <#else>
	        <#assign disabled = false/>
	    </#if>
	    <#if multiFacetRefinedExist>
	        <#if multiFacetRefined?has_content>
	            <#list multiFacetRefined as facetResultRefined>
	                <#if facetResultRefined.refinementValues?has_content>
	                    <#list facetResultRefined.refinementValues as refinementValueRefined>
	                        <#if refinementValueRefined.name == refinementValue.name && facet.type == facetResultRefined.type>
	                            <#assign disabled = false/>
	                            <#assign scalarCount = refinementValueRefined.scalarCount/>
	                        </#if>
	                    </#list>
	                </#if>
	            </#list>
	        </#if>
	    <#else>
	        <#assign disabled = false/>
	    </#if>
	    <#if disabled>
	        <#assign scalarCount = 0/>
	    </#if>
    </#if>
    
    <#if FACET_VALUE_STYLE == "CHECKBOX">
    	<#-- CHECKBOX -->
        <#assign removeUrl = catOrSearchText/>
        <#assign removeFilterGroupValue = ""/>
        <#if filterGroupParamMap?has_content>
              <#list filterGroupParamMap.entrySet() as entry>
                  <#list entry.value as value>
                      <#if !(refinementValueName.equals(value))>
                          <#assign removeFilterGroupValue = removeFilterGroupValue+entry.key+":"+StringUtil.wrapString(value)+"|"/>
                      </#if>
                  </#list>
              </#list>
              <#if removeFilterGroupValue?has_content>
                  <#assign removeUrl = removeUrl+"&filterGroup=${removeFilterGroupValue}"/>
              </#if>
        </#if>
        <#assign removeUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${removeUrl}')/>
		<span class="facetValue">
            <input type="checkbox" name="facetValue_${refinementValueName?html}" id="facetValue_${refinementValueName?html}" value="Y" onclick="solrSearch(this, '${productCategoryUrl!}', '${removeUrl!}', 'checkbox')" <#if facetGroupParamList.contains(refinementValueName)>checked</#if> <#if disabled>disabled='true'</#if>/>
            <span class="facetValueName <#if disabled>disabled</#if>">
                <#if facetType?exists && facetType == "CUSTOMER_RATING">
                    <img alt="${refinementValueDisplayName}" src="/osafe_theme/images/user_content/images/rating_facet_bar_${refinementValue.start}.gif" class="ratingFacetBar">
                </#if>
                ${refinementValueDisplayName} <#if facetShowItemCount>(${scalarCount!})</#if>
            </span>
        </span>
     	<#-- END CHECKBOX -->
    <#elseif FACET_VALUE_STYLE == "DROPDOWN">  
    	<#-- DROPDOWN -->   
    	<#assign newUrl = catOrSearchText/>
        <#assign newFilterGroupValue = ""/>
        <#if filterGroupParamMap?has_content>
        	  <#assign updatedRefinementValueInUrl = false />
              <#list filterGroupParamMap.entrySet() as entry>
                  <#if facetType != entry.key?string>
                      	<#list entry.value as value>
                      		<#assign newFilterGroupValue = newFilterGroupValue+entry.key+":"+StringUtil.wrapString(value)+"|"/>
                      	</#list>
                  <#else>
                  	<#if allSelected != "Y">
                  		<#assign newFilterGroupValue = newFilterGroupValue+entry.key+":"+StringUtil.wrapString(refinementValueName)+"|"/>
                  		<#assign updatedRefinementValueInUrl = true />
                  	</#if>
                  </#if>
              </#list>
              <#if allSelected != "Y" && !updatedRefinementValueInUrl>
              		<#assign newFilterGroupValue = newFilterGroupValue+facetType+":"+StringUtil.wrapString(refinementValueName)+"|"/>
              </#if>
        </#if>
        
        <#if newFilterGroupValue?has_content>
	        	<#assign newUrl = newUrl+"&filterGroup=${newFilterGroupValue}"/>
	          	<#assign newUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${newUrl}')/>
	    <#elseif allSelected == "Y">
	    		<#assign newUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${newUrl}')/>
	    <#else>
	      		<#assign newUrl = productCategoryUrl/>
	    </#if>
        
    	<#assign isSelected = false/>
    	<#if facetGroupParamList?has_content && facetGroupParamList.contains(refinementValueName)>
    		<#assign isSelected = true/>
    	</#if>
    	<#if allSelected != "Y">
    		<#if scalarCount?number &gt; 0>
    			<option value='${newUrl!}'<#if isSelected>selected=selected</#if>>${refinementValueDisplayName} <#if facetShowItemCount>(${scalarCount})</#if></option>
    		</#if>
    	<#else>
    		<option class="js_allOptionDD" value='${newUrl!}'>${uiLabelMap.CommonAll}</option>
    	</#if>
    	<#-- END DROPDOWN -->  
    <#else>
    	<#-- DEFAULT -->  
    	<span class="facetValue">
            <#if facet.refinementValues?size == 1>
                <#if facetType?exists && facetType == "CUSTOMER_RATING">
                    <img alt="${refinementValueDisplayName}" src="/osafe_theme/images/user_content/images/rating_facet_bar_${refinementValue.start}.gif" class="ratingFacetBar">
                </#if>
                ${refinementValueDisplayName} <#if facetShowItemCount>(${refinementValue.scalarCount})</#if>
            <#else>
                <a class="facetValueLink" title="${refinementValueName?html}" href="${productCategoryUrl}">
                    <#if facetType?exists && facetType == "CUSTOMER_RATING">
                        <img alt="${refinementValueDisplayName}" src="/osafe_theme/images/user_content/images/rating_facet_bar_${refinementValue.start}.gif" class="ratingFacetBar">
                    </#if>
                    ${refinementValueDisplayName} <#if facetShowItemCount>(${refinementValue.scalarCount})</#if>
                </a>
            </#if>
        </span>
        <#-- END DEFAULT -->  
    </#if>
</#macro>
<#-- ---------------------------------------------------------------------- -->
<#-- SITE NAVIGATION ---------------------------------------------------------------------- -->
<#macro navBar parentCategory category levelUrl levelValue listIndex listSize>
 
  <#-- Value is from the Product Category entity-->
  <#assign categoryName = category.categoryName!>
  <#-- Value is from the Product Category entity-->
  <#assign categoryDescription = category.description!>

  <#if listIndex =1>
    <#assign itemIndexClass="navfirstitem">
  <#else>
      <#if listIndex = listSize>
        <#assign itemIndexClass="navlastitem">
      <#else>
        <#assign itemIndexClass="">
      </#if>
  </#if>

  <#assign megaMenuContentId = "" />
  <#local macroLevelUrl = levelUrl>
  <#assign levelClass = "">
  <#if levelValue?has_content && levelValue="1">
      <#assign levelClass = "topLevel">
	  <#assign megaMenuProductCategoryContentList = delegator.findByAndCache("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , category.productCategoryId?string, "prodCatContentTypeId" , "PLP_ESPOT_MEGA_MENU")) />
	  <#if megaMenuProductCategoryContentList?has_content>
	   <#assign megaMenuProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(megaMenuProductCategoryContentList,true) />
	   <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(megaMenuProductCategoryContentList) />
	   <#assign megaMenuContent = prodCategoryContent.getRelatedOneCache("Content")/>
	   <#if megaMenuContent.statusId?has_content>
		   <#if (megaMenuContent.statusId == "CTNT_PUBLISHED")>
		        <#assign megaMenuContentId = megaMenuContent.contentId/>
		   <#elseif previewContentId?has_content>
               <#if (previewContentId == megaMenuContent.contentId)>
                    <#assign megaMenuContentId = megaMenuContent.contentId/>
               </#if>
		   </#if>
	   </#if>
	  </#if>
	  
	  <#assign productCategoryMemberList = delegator.findByAndCache("ProductCategoryMember", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , category.productCategoryId?string)) />
	  <#if productCategoryMemberList?has_content>
	     <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMemberList,true) />
	     <#if productCategoryMemberList?has_content>
	         <#local macroLevelUrl = "eCommerceProductList">
	     </#if>
	  </#if>
	  
  <#elseif levelValue?has_content && levelValue="2">
      <#assign levelClass = "subLevel">
  </#if>
    <#local subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
    <#if !(subCatList?has_content)>
        <#assign subNoListSize=subCatList.size()/>
        <#if subNoListSize == 0>
            <#local macroLevelUrl = "eCommerceProductList">
        </#if>
    </#if>
  <#local macroLevelUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${macroLevelUrl}?productCategoryId=${category.productCategoryId}')>
  
    <li class="${levelClass} ${itemIndexClass} ${category.productCategoryId!}">
        <a class="${levelClass}" href="${macroLevelUrl}">
         <span>
          <#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if>
         </span>
        </a>
        <#if megaMenuContentId?has_content>
          <ul class="ecommerceMegaMenu ${categoryName}">
              <@renderContentAsText contentId="${megaMenuContentId}" ignoreTemplate="true"/>
          </ul>
        </#if>
        <#if subCatList?has_content>
          <ul<#if megaMenuContentId?has_content> class="ecommerceMegaMenuAlt ${categoryName}"</#if>>
          <#assign idx=1/>
          <#assign subListSize=subCatList.size()/>
          <#list subCatList as subCat>
            <#assign subCategoryRollups = subCat.getRelatedCache("CurrentProductCategoryRollup")/>
            <#assign subCategoryRollups = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(subCategoryRollups)/>
            <#if subCategoryRollups?has_content>
               <#assign subCategoryRollup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(subCategoryRollups) />
            </#if>
            <#if (subCategoryRollup?has_content && (subCategoryRollup.sequenceNum?has_content && subCategoryRollup.sequenceNum > 0)) >
               <@navBar parentCategory=category category=subCat levelUrl="eCommerceProductList" levelValue="2" listIndex=idx listSize=subListSize/>
               <#assign idx= idx + 1/>
            </#if>
          </#list>
          </ul>
        </#if>
      
      </li>
    <#if levelValue?has_content && levelValue="1">
        <li class="navSpacer"></li>
    </#if>
</#macro>
<#-- ---------------------------------------------------------------------- -->

<#-- BREAD CRUMBS ---------------------------------------------------------------------- -->
<#macro topCategoryList category>
    <#if curCategoryId?exists && curCategoryId == category.productCategoryId>
        <@renderCrumb category=category/>
    <#else>
       <#local subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
        <#if subCatList?exists>
          <#list subCatList as subCat>
           <#if curCategoryId?exists && curCategoryId == subCat.productCategoryId >
            <@renderCrumb category=category />
            <@renderCrumb category=subCat/>
           </#if>
          </#list>
        </#if>
    </#if>
</#macro>

<#macro renderCrumb category>
      <#assign productCategoryMembers = category.getRelatedCache("ProductCategoryMember")!"" />
      <#assign productCategoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMembers)/>
      <#if (productCategoryMembers?size > 0)>
        <#assign categoryUrl = "eCommerceProductList"/>
      <#else>
        <#assign categoryUrl = "eCommerceCategoryList" />
      </#if>
      <li>
        <#if category.categoryName?has_content>
          <#assign catName = category.categoryName!/>
        <#elseif category.description?has_content>
          <#assign catName = category.description!/>
        </#if>
        <#if (curCategoryId?exists && curCategoryId == category.productCategoryId) && !facetGroups?has_content && !product_id?has_content>
           <span>${catName}</span>
        <#else>
           <#assign catalogFriendlyUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'${categoryUrl}?productCategoryId=${category.productCategoryId!""}')/>
           <a href="${catalogFriendlyUrl}"><span>${catName}</span></a>
        </#if>
      </li>
</#macro>

<#macro renderProductCrumb pdpProductName>
  <li>
    <span>${pdpProductName}</span>
  </li>
</#macro>

<#macro renderFacetCrumb facet listSize index>
     <#assign facetParts = facet.split(":")/>
     <#assign facetPart1 = facetParts[0]/>
     <#assign facetPart2 = facetParts[1]/>
     <#assign facetPart2Desc = facetPart2!""/>
     

    <#-- Look up the "Product Category" name -->
    <#if productCategoryIdFacet?has_content && productCategoryIdFacet=="Y">
        <#if facetPart1?has_content && facetPart1=="productCategoryId">
            <#assign productCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", facetPart2), true)>
            <#assign catName = productCategory.categoryName!/>
            <#assign facetPart2Desc = catName/>
        </#if>
    </#if>
    <#if topMostProductCategoryIdFacet?has_content && topMostProductCategoryIdFacet=="Y">
        <#if facetPart1?has_content && facetPart1=="topMostProductCategoryId">
            <#assign productCategory = delegator.findOne("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", facetPart2), true)>
            <#assign catName = productCategory.categoryName!/>
            <#assign facetPart2Desc = catName/>
        </#if>
    </#if>

    <#if facetPart1?lower_case?contains("price")>
        <#assign facetPart2Desc = StringUtil.wrapString(facetPart2)/>
        <#assign facetPart2Desc = facetPart2Desc?replace("[", "")/>
        <#assign facetPart2Desc = facetPart2Desc?replace("]", "")/>
        <#assign facetPart2Desc = facetPart2Desc?replace("+", " ")/>
        <#assign facetPart2Prices = facetPart2Desc?split(" ")/>

        <#-- Using special ftl syntax so we can call the ofbizCurrency macro -->
        <#assign start><@ofbizCurrency amount=facetPart2Prices[0]?number isoCode=CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() rounding=globalContext.currencyRounding/></#assign>
        <#assign end><@ofbizCurrency amount=facetPart2Prices[1]?number isoCode=CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() rounding=globalContext.currencyRounding/></#assign>

        <#assign facetPart2Desc = start + " to " + end/>
    </#if>

    <#if facetPart1?lower_case?contains("customer rating")>
        <#assign facetPart2Desc = StringUtil.wrapString(facetPart2)/>
        <#assign facetPart2Desc = facetPart2Desc?replace("[", "")/>
        <#assign facetPart2Desc = facetPart2Desc?replace("]", "")/>
        <#assign facetPart2Desc = facetPart2Desc?replace("+", " ")/>
        <#assign facetPart2Ratings = facetPart2Desc?split(" ")/>

        <#assign start>${facetPart2Ratings[0]}.0</#assign>
        <#assign facetPart2Desc = start + " &amp; up " />
    </#if>

      <li>
         
           <#assign catOrSearchText = ""/>
           <#assign removeFilterGroupValue = removeFilterGroupValues[index-1]?if_exists/>
           <#assign filterGroupValue = filterGroupValues[index]?if_exists/>
            <#if searchText?has_content>
                <#assign catOrSearchText = "searchText=" + searchText/>
                <#-- Add "Top Most Product Category" to url -->
                <#if topMostProductCategoryIdFacet?has_content && topMostProductCategoryIdFacet=="Y">
                    <#if facetPart2?has_content && facetPart2==curTopMostCategoryId>
                        <#assign catOrSearchText = catOrSearchText + "&topMostProductCategoryId=" + curTopMostCategoryId/>
                    </#if>
                </#if>

                <#-- Add "Top Most Product Category" and "Product Category" to url -->
                <#if productCategoryIdFacet?has_content && productCategoryIdFacet=="Y">
                    <#if facetPart2?has_content && facetPart2==curCategoryId>
                        <#assign catOrSearchText = catOrSearchText + "&topMostProductCategoryId=" + curTopMostCategoryId/>
                        <#assign catOrSearchText = catOrSearchText + "&productCategoryId=" + curCategoryId/>
                    </#if>
                </#if>
            <#else>
                <#assign catOrSearchText = "productCategoryId=" + curCategoryId/>
            </#if>
           <#if index==listSize>
              <#if facetPart2Desc?has_content>
                  <#assign facetPart2Desc = Static["org.ofbiz.base.util.StringUtil"].wrapString(facetPart2Desc)!/>
              </#if>
              <span>${facetPart2Desc}</span>
            <#else>
             <a href="<@ofbizUrl><#if !searchText?has_content>eCommerceProductList<#else>siteSearch</#if>?${catOrSearchText}&filterGroup=${filterGroupValue?if_exists}</@ofbizUrl>">${facetPart2Desc}</a>
           </#if>
           <a class="removeFacet" href="<@ofbizUrl><#if !searchText?has_content>eCommerceProductList<#else>siteSearch</#if>?${catOrSearchText}<#if removeFilterGroupValue?has_content>&filterGroup=${removeFilterGroupValue?if_exists}</#if></@ofbizUrl>" title="${uiLabelMap.RemoveLabel} ${facetPart2Desc}"><span class="removeFacetIcon"></span></a>
      </li>
</#macro>
<#-- ---------------------------------------------------------------------- -->



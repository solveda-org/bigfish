
<#-- variable setup and worker calls -->
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>

<#-- looping macro -->
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
  <#local macroLevelUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${macroLevelUrl}?productCategoryId=${category.productCategoryId}')>
  
    <li class="${levelClass} ${itemIndexClass}">
        <a class="${levelClass}" href="${macroLevelUrl}">
          <#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if>
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

<#--
    Current nav bar is genrated as a single level menu
    http://htmldog.com/articles/suckerfish/dropdowns/
    -->
<#if topLevelList?has_content>
<div id="eCommerceNavBarWidget">
    <a href="javascript:void(0);" class="showNavWidget"><span>${uiLabelMap.ShowNavWidgetLabel}</span></a>
	<a href="javascript:void(0);" class="hideNavWidget" style="display:none"><span>${uiLabelMap.HideNavWidgetLabel}</span></a>
</div>
<ul id="eCommerceNavBarMenu">
    <#assign parentIdx=1/>
    <#assign listSize=topLevelList.size()/>
    <#list topLevelList as category>
        <#assign categoryRollups = delegator.findByAndCache("ProductCategoryRollup", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , category.productCategoryId, "parentProductCategoryId", topCategoryId?if_exists)) />
        <#assign categoryRollups = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(categoryRollups)/>
        <#if categoryRollups?has_content>
          <#assign categoryRollup = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(categoryRollups) />
        </#if>
        <#if (categoryRollup?has_content) && (categoryRollup.sequenceNum?has_content && categoryRollup.sequenceNum > 0) >
            <@navBar parentCategory="" category=category levelUrl="eCommerceCategoryList" levelValue="1" listIndex=parentIdx listSize=listSize/>
            <#assign parentIdx= parentIdx + 1/>
        </#if>
    </#list>
</ul>
</#if>

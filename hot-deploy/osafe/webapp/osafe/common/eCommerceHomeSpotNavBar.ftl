
<#-- variable setup and worker calls -->
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>

<#-- looping macro -->
<#macro navBar parentCategory category levelUrl levelValue listIndex listSize>
  
    <#assign categoryName = category.categoryName?if_exists>
    <#assign categoryDescription = category.description?if_exists>

  <#assign megaMenuContentId = "" />
  <#assign megaMenuProdCatContentTypeId = 'PLP_ESPOT_MEGA_MENU'/>
  <#if megaMenuProdCatContentTypeId?exists && megaMenuProdCatContentTypeId?has_content>
    <#assign megaMenuProductCategoryContentList = delegator.findByAndCache("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , category.productCategoryId?string, "prodCatContentTypeId" , megaMenuProdCatContentTypeId?if_exists)) />
    <#if megaMenuProductCategoryContentList?has_content>
      <#assign megaMenuProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(megaMenuProductCategoryContentList?if_exists) />
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(megaMenuProductCategoryContentList) />
      <#assign megaMenuContentId = prodCategoryContent.contentId?if_exists />
    </#if>
  </#if>

  <#if listIndex =1>
    <#assign itemIndexClass="homeSpotNavfirstitem">
  <#else>
      <#if listIndex = listSize>
        <#assign itemIndexClass="homeSpotNavlastitem">
      <#else>
        <#assign itemIndexClass="">
      </#if>
  </#if>

  <#local macroLevelUrl = levelUrl>
  <#assign levelClass = "">
  <#if levelValue?has_content && levelValue="1">
      <#assign levelClass = "topLevel">
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
    <li class="${levelClass} ${itemIndexClass}"><a class="${levelClass}" href="${macroLevelUrl}"><#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if></a>
        <#assign megaMenuActiveContentId = "" />
        <#if megaMenuContentId?has_content>
          <#assign megaMenuContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", megaMenuContentId), true) />
            <#if ((megaMenuContent.statusId)?if_exists == "CTNT_PUBLISHED")>
                <#assign megaMenuActiveContentId = "${megaMenuContentId}" />
            </#if>
        </#if>        
       <#if megaMenuActiveContentId?has_content>
          <ul class="ecommerceHomeSpotMegaMenu ${categoryName}">
              <@renderContentAsText contentId="${megaMenuContentId}" ignoreTemplate="true"/>
          </ul>
       <#else>
            <#if subCatList?has_content>
              <ul class="subHomeSpotNavBar">
              <#assign idx=1/>
              <#assign subListSize=subCatList.size()/>
              <#list subCatList as subCat>
                <#if subCat_index &lt; 5><#-- TODO: work for this condition, get from property -->
                  <@navBar parentCategory=category category=subCat levelUrl="eCommerceProductList" levelValue="2" listIndex=idx listSize=subListSize/>
                  <#assign idx= idx + 1/>
                </#if>
              </#list>
              </ul>
            </#if>
        </#if>
    </li>
    <#if levelValue?has_content && levelValue="1">
        <li class="navHomeSpotSpacer"></li>
    </#if>
</#macro>

<#--
    Current nav bar is genrated as a single level menu
    http://htmldog.com/articles/suckerfish/dropdowns/
    -->
<#if topLevelList?has_content>
<ul>
    <#assign parentIdx=1?int/>
    <#assign listSize=topLevelList.size()/>
    <#list topLevelList as category>
       <#if category_index &lt; 3><#-- TODO: work for this condition, get from property -->
            <@navBar parentCategory="" category=category levelUrl="eCommerceCategoryList" levelValue="1" listIndex=parentIdx listSize=listSize/>
            <#assign parentIdx= parentIdx + 1/>
       </#if>
    </#list>
</ul>
</#if>

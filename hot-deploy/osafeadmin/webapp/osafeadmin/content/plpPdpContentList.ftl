<#-- variable setup and worker calls -->

<#-- looping macro -->
<#macro categories parentCategory category levelUrl levelValue listIndex listSize rowClass>
  <#if catContentWrappers?exists && catContentWrappers[category.productCategoryId]?exists && catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")?exists>
    <#-- Value is from the related Content record -->
    <#assign categoryName = catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")>
  <#else>
    <#-- Value is from the Product Category entity-->
    <#assign categoryName = category.categoryName?if_exists>
  </#if>
  <#if catContentWrappers?exists && catContentWrappers[category.productCategoryId]?exists && catContentWrappers[category.productCategoryId].get("DESCRIPTION")?exists>
    <#-- Value is from the related Content record -->
    <#assign categoryDescription = catContentWrappers[category.productCategoryId].get("DESCRIPTION")>
  <#else>
    <#-- Value is from the Product Category entity-->
    <#assign categoryDescription = category.description?if_exists>
  </#if>
    
  <#if listIndex =1>
    <#assign itemIndexClass="firstitem">
  <#else>
      <#if listIndex = listSize>
        <#assign itemIndexClass="lastitem">
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
    <#if subCatRollUpMap?has_content>
    <#assign subCatList = subCatRollUpMap.get(category.productCategoryId)!/>
  </#if>
  <#if !(subCatList?has_content)>
    <#assign subListSize=0/>
    <#if subListSize == 0>
      <#local macroLevelUrl = "categoryDetail">
    </#if>
  </#if>
    
  <#if levelValue?has_content && levelValue="1">
    <#assign megaMenuProdCatContentTypeId = "PLP_ESPOT_MEGA_MENU"/>
    <#assign megaMenuMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : megaMenuProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
     
    <#if prodCategoryContentList?has_content>
      <#assign megaMenuMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign megaMenuContentId = prodCategoryContent.contentId?if_exists />
    </#if>
    <#assign pageTopProdCatContentTypeId = "PLP_ESPOT_PAGE_TOP"/>
    <#assign pageTopMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : pageTopProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
     
    <#if prodCategoryContentList?has_content>
      <#assign pageTopMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign pageTopContentId = prodCategoryContent.contentId?if_exists />
    </#if>
    
    <#assign pageEndProdCatContentTypeId = "PLP_ESPOT_PAGE_END"/>
    <#assign pageEndMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : pageEndProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
     
    <#if prodCategoryContentList?has_content>
      <#assign pageEndMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign pageEndContentId = prodCategoryContent.contentId?if_exists />
    </#if>
    
    <#assign facetTopProdCatContentTypeId = "PLP_ESPOT_FACET_TOP"/>
    <#assign facetTopMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : facetTopProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
     
    <#if prodCategoryContentList?has_content>
      <#assign facetTopMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign facetTopContentId = prodCategoryContent.contentId?if_exists />
    </#if>
    
    <#assign facetEndProdCatContentTypeId = "PLP_ESPOT_FACET_END"/>
    <#assign facetEndMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : facetEndProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
     
    <#if prodCategoryContentList?has_content>
      <#assign facetEndMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign facetEndContentId = prodCategoryContent.contentId?if_exists />
    </#if>
    <tr class="dataRow ${levelClass} ${itemIndexClass} <#if rowClass == "2">even<#else>odd</#if>">
      <td class="seqCol firstCol">${category.sequenceNum?if_exists}</td>
      <td class="descCol">
          <#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if>
      </td>
      <td class="seqCol">&nbsp;</td>
      <td class="descCol" colspan="0">&nbsp;</td>
      <td class="actionPromoCol">
        <#if megaMenuContentId?has_content && (megaMenuMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${megaMenuContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(megaMenuProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td>&nbsp;</td>
      <td class="actionPromoCol">
        <#if pageTopContentId?has_content && (pageTopMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageTopContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(pageTopProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td class="actionPromoCol">
        <#if pageEndContentId?has_content && (pageEndMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageEndContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(pageEndProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td class="actionPromoCol">
        <#if facetTopContentId?has_content && (facetTopMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetTopContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(facetTopProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td class="actionPromoCol">
        <#if facetEndContentId?has_content && (facetEndMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetEndContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(facetEndProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
    </tr>
 </#if> 
  <#if levelValue?has_content && levelValue="2">
    <#assign PDPAddlContentTypeId = "PDP_ADDITIONAL"/>
    <#assign PDPAddlMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : PDPAddlContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
    <#if prodCategoryContentList?has_content>
      <#assign PDPAddlMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign PDPAddlSubContentId = prodCategoryContent.contentId?if_exists />
    <#else>
      <#assign PDPAddlSubContentId = "" />
    </#if>
    
    <#assign pageTopProdCatContentTypeId = "PLP_ESPOT_PAGE_TOP"/>
    <#assign pageTopMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : pageTopProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
    
    <#if prodCategoryContentList?has_content>
      <#assign pageTopMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign pageTopSubContentId = prodCategoryContent.contentId?if_exists />
    <#else>
      <#assign pageTopSubContentId = "" />
    </#if>

    <#assign pageEndProdCatContentTypeId = "PLP_ESPOT_PAGE_END"/>
    <#assign pageEndMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : pageEndProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
    
    <#if prodCategoryContentList?has_content>
      <#assign pageEndMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign pageEndSubContentId = prodCategoryContent.contentId?if_exists />
    <#else>
      <#assign pageEndSubContentId = "" />
    </#if>

    <#assign facetTopProdCatContentTypeId = "PLP_ESPOT_FACET_TOP"/>
    <#assign facetTopMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : facetTopProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
    
    <#if prodCategoryContentList?has_content>
      <#assign facetTopMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign facetTopSubContentId = prodCategoryContent.contentId?if_exists />
    <#else>
      <#assign facetTopSubContentId = "" />
    </#if>

    <#assign facetEndProdCatContentTypeId = "PLP_ESPOT_FACET_END"/>
    <#assign facetEndMode = "Add"/>
    <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : facetEndProdCatContentTypeId?if_exists})>
    <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
    
    <#if prodCategoryContentList?has_content>
      <#assign facetEndMode = "Edit"/>
      <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
      <#assign facetEndSubContentId = prodCategoryContent.contentId?if_exists />
    <#else>
      <#assign facetEndSubContentId = "" />
    </#if>
 <tr class="dataRow ${levelClass} ${itemIndexClass} <#if rowClass == "2">even<#else>odd</#if>">
      <td class="seqCol firstCol">&nbsp;</td>
      <td class="descCol">&nbsp;</td>
      <td class="seqCol">${category.sequenceNum?if_exists}</td>
      <td class="descCol" colspan="0">
          <#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if>
      </td>
      <td class="actionPromoCol">&nbsp;</td>  
      <td class="actionPromoCol">
        <#if PDPAddlSubContentId?has_content && (PDPAddlMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${PDPAddlSubContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(PDPAddlContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>   
      <td class="actionPromoCol">
        <#if pageTopSubContentId?has_content && (pageTopMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageTopSubContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(pageTopProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td class="actionPromoCol">
        <#if pageEndSubContentId?has_content && (pageEndMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageEndSubContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(pageEndProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td class="actionPromoCol">
        <#if facetTopSubContentId?has_content && (facetTopMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetTopSubContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(facetTopProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td class="actionPromoCol">
        <#if facetEndSubContentId?has_content && (facetEndMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetEndSubContentId?if_exists}&productCategoryId=${category.productCategoryId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(facetEndProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
 </tr>
 </#if>
  <#if subCatList?has_content>
    <#assign idx=1/>
    <#assign subListSize=subCatList.size()/>
    <#list subCatList as subCat>
      <@categories parentCategory=category category=subCat levelUrl="eCommerceProductList" levelValue="2" listIndex=idx listSize=subListSize rowClass=rowClass/>
      <#assign idx= idx + 1/>
    </#list>
  </#if>
</#macro>

  <tr class="heading">
    <th class="seqCol firstCol">${uiLabelMap.SeqNumberLabel}</th>
    <th class="descCol">${uiLabelMap.TopNavigationLabel}</th>
   	<th class="seqCol">${uiLabelMap.SeqNumberLabel}</th>
    <th class="descCol">${uiLabelMap.SubItemLabel}</th>
    <#if contentListName?exists && contentListName == 'PLPContentList'>
      <th class="actionPromoCol">${uiLabelMap.MegaMenuLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.PDPAddlLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.PageTopLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.PageEndLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.FacetTopLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.FacetEndLabel}</th>
    </#if>
  </tr>
  <#if resultList?has_content>
    <#assign rowClass = "1">
    <#assign parentIdx=1/>
    <#assign listSize=resultList.size()/>
    <#list resultList as category>
      <@categories parentCategory="" category=category levelUrl="categoryDetail" levelValue="1" listIndex=parentIdx listSize=listSize rowClass=rowClass/>
      <#assign parentIdx= parentIdx + 1/>
      <#-- toggle the row color -->
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  <#else>
     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoResult")}
  </#if>

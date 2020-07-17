<#-- variable setup and worker calls -->
<#if resultList?exists && resultList?has_content><#assign topLevelList = resultList></#if>

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
  <#if contentListName?exists && contentListName == 'PLPContentList'>
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
  </#if>
    <#if contentListName?exists && contentListName == 'PDPContentList'>
      <#if prodCatContentTypeId?exists && prodCatContentTypeId?has_content>
      <#assign mode = "Add"/>
      <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : prodCatContentTypeId?if_exists})>
      <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
     
      <#if prodCategoryContentList?has_content>
        <#assign mode = "Edit"/>
        <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
        <#assign contentId = prodCategoryContent.contentId?if_exists />
      <#else>
        <#assign contentId = "" />
      </#if>
      </#if>
    </#if>
    <tr class="dataRow ${levelClass} ${itemIndexClass} <#if rowClass == "2">even<#else>odd</#if>">
      <td class="seqCol firstCol">${category.sequenceNum?if_exists}</td>
      <td class="descCol">
        <#if contentListName?exists && contentListName == 'PLPContentList'>
          <#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if>
        </#if>
        <#if contentListName?exists && contentListName == 'PDPContentList'>
          <#if prodCatContentTypeId?exists && prodCatContentTypeId?has_content><a class="${levelClass}" href="<@ofbizUrl><#if mode != "Add">${contentDetailTarget?if_exists}?contentId=${contentId?if_exists}<#else>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId}&amp;prodCatContentTypeId=${prodCatContentTypeId?if_exists}</#if></@ofbizUrl>"><#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if></a></#if>
        </#if>
      </td>
      <td class="seqCol">&nbsp;</td>
      <td class="descCol" colspan="0">&nbsp;</td>
      <#if contentListName?exists && contentListName == 'PLPContentList'>
      <td class="actionPromoCol">
        <#if megaMenuContentId?has_content && (megaMenuMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${megaMenuContentId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(megaMenuProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      <td class="actionPromoCol">
        <#if pageTopContentId?has_content && (pageTopMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageTopContentId?if_exists}</@ofbizUrl>">
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
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageEndContentId?if_exists}</@ofbizUrl>">
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
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetTopContentId?if_exists}</@ofbizUrl>">
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
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetEndContentId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(facetEndProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
       </#if>
    <#if contentListName?exists && contentListName == 'PDPContentList'>
      <#if contentId?has_content && (mode != "Add")>
        <#assign contentDetail = delegator.findOne("Content", {"contentId" : contentId}, true) />
        <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : contentDetail.statusId}, false)>
        <td class="statusCol"><#if (statusItem.statusId)?has_content && (statusItem.statusId != "CTNT_IN_PROGRESS")>${statusItem.description!statusItem.get("description",locale)!statusItem.statusId}</#if></td>
        <td class="dateCol">
          <#assign lastModifiedDate = "" />
          <#if (statusItem.statusId)?has_content && (statusItem.statusId == "CTNT_PUBLISHED")>
            <#if contentDetail.lastModifiedDate?has_content>
              ${contentDetail.lastModifiedDate?string(preferredDateFormat)}
            </#if>
          </#if>
        </td>
        <td class="dateCol">${(contentDetail.createdDate?string(preferredDateFormat))!""}</td>
      <#else>
        <td colspan="3"></td>
      </#if>
      <td class="actionColSmall lastCol">
        <#if previewAction?exists && previewAction?has_content && (contentId?has_content || subContentId?has_content)>
          <a href="<@ofbizUrl>${previewAction}?contentId=${contentId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.PreviewContentTooltip}');" onMouseout="hideTooltip()" class="previewIcon" target="_new"></a>
          <#assign contentId = "" />
          <#assign subContentId = "" />
        </#if>
      </td>
    </#if>
    </tr>
  </#if>
  
  <#if levelValue?has_content && levelValue="2">
  <#if contentListName?exists && contentListName == 'PLPContentList'>
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
  </#if>
  <#if contentListName?exists && contentListName == 'PDPContentList'>
    <#if prodCatContentTypeId?exists && prodCatContentTypeId?has_content>
      <#assign mode = "Add"/>
        <#assign productCategoryContentList = delegator.findByAnd("ProductCategoryContent", {"productCategoryId" : category.getString("productCategoryId"), "prodCatContentTypeId" : prodCatContentTypeId?if_exists})>
        <#assign prodCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryContentList?if_exists) />
    
        <#if prodCategoryContentList?has_content>
          <#assign mode = "Edit"/>
          <#assign prodCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(prodCategoryContentList) />
          <#assign subContentId = prodCategoryContent.contentId?if_exists />
        <#else>
          <#assign subContentId = "" />
        </#if>
    </#if>
  </#if>
    <tr class="dataRow ${levelClass} ${itemIndexClass} <#if rowClass == "2">even<#else>odd</#if>">
      <td class="seqCol firstCol">&nbsp;</td>
      <td class="descCol">&nbsp;</td>
      <td class="seqCol">${category.sequenceNum?if_exists}</td>
      <td class="descCol" colspan="0">
        <#if contentListName?exists && contentListName == 'PLPContentList'>
          <#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if>
        </#if>
        <#if contentListName?exists && contentListName == 'PDPContentList'>
          <#if prodCatContentTypeId?exists && prodCatContentTypeId?has_content><a class="${levelClass}" href="<@ofbizUrl><#if mode != "Add">${contentDetailTarget?if_exists}?contentId=${subContentId?if_exists}<#else>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(prodCatContentTypeId)?if_exists}</#if></@ofbizUrl>"><#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if></a></#if>
        </#if>
      </td>
      <#if contentListName?exists && contentListName == 'PLPContentList'>
      <td class="actionPromoCol">&nbsp;</td>     
      <td class="actionPromoCol">
        <#if pageTopSubContentId?has_content && (pageTopMode != "Add")>
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageTopSubContentId?if_exists}</@ofbizUrl>">
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
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${pageEndSubContentId?if_exists}</@ofbizUrl>">
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
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetTopSubContentId?if_exists}</@ofbizUrl>">
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
          <a class="${levelClass}" href="<@ofbizUrl>${contentDetailTarget?if_exists}?contentId=${facetEndSubContentId?if_exists}</@ofbizUrl>">
            <span class="previewIcon"></span>
          </a>
        <#else>
          <a class="${levelClass}" href="<@ofbizUrl>${addProdCatContentTarget?if_exists}?productCategoryId=${category.productCategoryId?if_exists}&amp;prodCatContentTypeId=${(facetEndProdCatContentTypeId)?if_exists}</@ofbizUrl>">
            <span class="plusIcon"></span>
          </a>
        </#if>
      </td>
      </#if>
      <#if contentListName?exists && contentListName == 'PDPContentList'>
      <#if subContentId?has_content && (mode != "Add")>
        <#assign contentDetailSub = delegator.findOne("Content", {"contentId" : subContentId}, true) />
        <#assign statusItemSub = delegator.findOne("StatusItem", {"statusId" : contentDetailSub.statusId}, false)>
        <td class="statusCol"><#if (statusItemSub.statusId)?has_content && (statusItemSub.statusId != "CTNT_IN_PROGRESS")>${statusItemSub.description!statusItemSub.get("description",locale)!statusItemSub.statusId}</#if></td>
        <td class="dateCol">
          <#assign lastModifiedDate = "" />
          <#if (statusItemSub.statusId)?has_content && (statusItemSub.statusId == "CTNT_PUBLISHED") >
            <#if contentDetailSub.lastModifiedDate?has_content>
              ${contentDetailSub.lastModifiedDate?string(preferredDateFormat)}
            </#if>
          </#if>
        </td>
        <td class="dateCol">${(contentDetailSub.createdDate?string(preferredDateFormat))!""}</td>
      <#else>
        <td colspan="3"></td>
      </#if>
      <td class="actionColSmall lastCol">
        <#if previewAction?exists && previewAction?has_content && (contentId?has_content || subContentId?has_content)>
          <a href="<@ofbizUrl>${previewAction}?contentId=<#if contentId?has_content>${contentId?if_exists}<#elseif subContentId?has_content>${subContentId?if_exists}</#if></@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.PreviewContentTooltip}');" onMouseout="hideTooltip()" class="previewIcon" target="_new"></a>
          <#assign contentId = "" />
          <#assign subContentId = "" />
        </#if>
      </td>
    </#if>
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
      <th class="actionPromoCol">${uiLabelMap.PageTopLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.PageEndLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.FacetTopLabel}</th>
      <th class="actionPromoCol">${uiLabelMap.FacetEndLabel}</th>
    </#if>
    <#if contentListName?exists && contentListName == 'PDPContentList'>
      <th class="statusCol">${uiLabelMap.StatusLabel}</th>
      <th class="dateCol">${uiLabelMap.ActiveDateLabel}</th>
      <th class="dateCol">${uiLabelMap.CreatedDateLabel}</th>
      <th class="actionColSmall"></th>
    </#if>
  </tr>
  <#if topLevelList?has_content>
    <#assign rowClass = "1">
    <#assign parentIdx=1/>
    <#assign listSize=topLevelList.size()/>
    <#list topLevelList as category>
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
    <tr><td class="boxNumber" colspan="5">${uiLabelMap.NoMatchingDataInfo}</td></tr>
  </#if>

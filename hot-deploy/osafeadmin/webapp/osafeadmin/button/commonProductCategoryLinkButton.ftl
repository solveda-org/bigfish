<div class="linkButton">
  <#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request) />
  <#assign rootProductCategoryId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogTopCategoryId(request, currentCatalogId) />
  
  <#assign categoryMembers = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", productCategory.productCategoryId!))>
  <#assign categoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(categoryMembers)>
  
  
  <#if !showCategoryImageLink?has_content>
      <#assign showCategoryImageLink = "true"/>
  </#if>

  <#if !showProductLink?has_content>
      <#assign showProductLink = "true"/>
  </#if>
  <#if !showPlpSequenceLink?has_content>
	  <#assign showPlpSequenceLink = "true"/>
  </#if>
  <#if !showMetaTagLink?has_content>
	  <#assign showMetaTagLink = "true"/>
  </#if>

  <#if showCategoryImageLink == 'true'>
    <#if productCategory.primaryParentCategoryId ?exists && productCategory.primaryParentCategoryId != rootProductCategoryId>
      <a href="<@ofbizUrl>categoryImageDetail?productCategoryId=${productCategory.productCategoryId?if_exists}</@ofbizUrl>" onMouseover="<#if productCategory.categoryImageUrl?has_content>showTooltipImage(event,'${uiLabelMap.CategoryImageTooltip}','${productCategory.categoryImageUrl!}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.CategoryImageTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
    </#if>
  </#if>

  <#if showProductLink == 'true'>
    <#if categoryMembers?has_content> 
      <a href="<@ofbizUrl>productManagement?categoryId=${productCategory.productCategoryId!}&preRetrieved='Y'</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductCategoryTooltip}');" onMouseout="hideTooltip()"><span class="productIcon"></span></a>
    </#if>
  </#if>
  
  <#if showPlpSequenceLink == 'true'>
    <#if categoryMembers?has_content> 
      <a href="<@ofbizUrl>plpSequence?productCategoryId=${productCategory.productCategoryId!}&preRetrieved='Y'</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductSequenceTooltip}');" onMouseout="hideTooltip()"><span class="sequenceIcon"></span></a>
    </#if>
  </#if>
  
  <#if showMetaTagLink == 'true'>
    <a href="<@ofbizUrl>categoryMetatag?productCategoryId=${productCategory.productCategoryId!}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
  </#if>
</div>

<#assign categoryId = ""/>
<#if (currentProductCategoryContentWrapper)?exists>
    <#assign categoryId = currentProductCategoryContentWrapper.get("PRODUCT_CATEGORY_ID")!"">
</#if>
<#if !categoryId?has_content>
  <#assign categoryId = parameters.productCategoryId?if_exists />
</#if>
<#if (requestAttributes.facetList)?exists><#assign facetList = requestAttributes.facetList></#if>
<#assign facetTopProdCatContentTypeId = 'PLP_ESPOT_FACET_TOP'/>
<#if facetTopProdCatContentTypeId?exists && facetTopProdCatContentTypeId?has_content>
  <#assign facetTopProductCategoryContentList = delegator.findByAnd("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , categoryId?string, "prodCatContentTypeId" , facetTopProdCatContentTypeId?if_exists)) />
  
  <#if facetTopProductCategoryContentList?has_content>
    <#assign facetTopProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(facetTopProductCategoryContentList?if_exists) />
    <#assign facetTopProdCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(facetTopProductCategoryContentList) />
    <#assign facetTopContentId = facetTopProdCategoryContent.contentId?if_exists />
  </#if>
  <#if facetTopContentId?exists >
    <#assign facetTopPlpEspotContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", facetTopContentId), true) />
  </#if>
</#if>

<#if facetTopPlpEspotContent?has_content>
  <#if ((facetTopPlpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
    <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
      <@renderContentAsText contentId="${facetTopContentId}" ignoreTemplate="true"/>
    </div>
  </#if>
</#if>
<h3 class="CategoryFacetTitle">${uiLabelMap.FacetNarrowResultsCaption}</h3>

    <ul class="eCommerceFacetCategories">
         <#if facetList?has_content>
            <#if FACET_VALUE_MAX?has_content>
              <#assign valueCnt = FACET_VALUE_MAX?if_exists?number />
            <#else>
              <#assign valueCnt = facetShowValueCnt!99 />
            </#if>
            
            <#list facetList as facet>
              <li>
                <h3 class="facetGroup">${facet.name}</h3>
                <#if facet.refinementValues?has_content>
                   <#assign indx=0/>
                    <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
                        <#assign valueSize = facet.refinementValues.size()/>
                        <#list facet.refinementValues as refinementValue>
                        <#assign indx = indx + 1/>
                        <#assign hideClass="showThem"/>
                         <#if (indx > valueCnt)>
                            <#assign hideClass="hideThem"/>
                         </#if>
                        <li class="facetValue ${hideClass}">
                        <#assign refinementValueName = refinementValue.displayName>
                        <#assign code = refinementValue.name>
                        <#assign refinementURL = refinementValue.refinementURL>
                       <#assign productCategoryUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${refinementURL}')/>
                            <a class="facetValueLink" title="${refinementValueName}" href="${productCategoryUrl}">${refinementValueName} (${refinementValue.scalarCount})</a>
                        </li>
                         <#if (indx > valueCnt) && indx == valueSize>
                           <li class="facetValue">
                             <a class="seeMoreLink" href="#">See more...</a>
                           </li>
                         </#if>
                        </#list>
                    </ul>
                </#if>
            </li>
            </#list>
        </#if>
    </ul>
<#assign facetEndProdCatContentTypeId = 'PLP_ESPOT_FACET_END'/>
<#if facetEndProdCatContentTypeId?exists && facetEndProdCatContentTypeId?has_content>
  <#assign facetEndProductCategoryContentList = delegator.findByAnd("ProductCategoryContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId" , categoryId?string, "prodCatContentTypeId" , facetEndProdCatContentTypeId?if_exists)) />
  
  <#if facetEndProductCategoryContentList?has_content>
    <#assign facetEndProductCategoryContentList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(facetEndProductCategoryContentList?if_exists) />
    <#assign facetEndProdCategoryContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(facetEndProductCategoryContentList) />
    <#assign facetEndContentId = facetEndProdCategoryContent.contentId?if_exists />
  </#if>
  <#if facetEndContentId?exists >
    <#assign facetEndPlpEspotContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", facetEndContentId), true) />
  </#if>
</#if>

<#if facetEndPlpEspotContent?has_content>
  <#if ((facetEndPlpEspotContent.statusId)?if_exists == "CTNT_PUBLISHED")>
    <div id="eCommercePlpEspot_${categoryId}" class="plpEspot">
      <@renderContentAsText contentId="${facetEndContentId}" ignoreTemplate="true"/>
    </div>
  </#if>
</#if>
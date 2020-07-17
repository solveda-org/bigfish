<#assign categoryId = ""/>
<#if (currentProductCategoryContentWrapper)?exists>
  <#assign categoryId = currentProductCategoryContentWrapper.get("PRODUCT_CATEGORY_ID")!"">
</#if>
<#if !categoryId?has_content>
  <#assign categoryId = parameters.productCategoryId?if_exists />
</#if>
<#if (requestAttributes.facetList)?exists><#assign facetList = requestAttributes.facetList></#if>
<#if (requestAttributes.facetListPriceRange)?exists><#assign facetListPriceRange = requestAttributes.facetListPriceRange></#if>
<#if (requestAttributes.facetListCustomerRating)?exists><#assign facetListCustomerRating = requestAttributes.facetListCustomerRating></#if>
<#if (requestAttributes.facetCatList)?exists><#assign facetCatList = requestAttributes.facetCatList></#if>
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
 
<h3 class="CategoryFacetTitle">${CategoryFacetTitle}</h3>
<#assign facetMinValue = FACET_VALUE_MIN?if_exists?number />
<#assign facetMaxValue = FACET_VALUE_MAX?if_exists?number />
<input type="hidden" name="facetShowItemCnt" id="facetShowItemCnt" value="${FACET_SHOW_ITEM_CNT!}" />
<ul>
  <#if facetCatList?has_content>
    <#if FACET_VALUE_MIN?has_content>
      <#assign valueCnt = FACET_VALUE_MIN?if_exists?number />
    <#else>
      <#assign valueCnt = facetShowValueCnt!5 />
    </#if>
    <#if parameters._CURRENT_VIEW_ == 'eCommerceCategoryList' || Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_CAT_ON_PLP)>
    <#list facetCatList as facet>
      <li>
        <h3 class="facetGroup">${facet.name}</h3>
        <#if facet.refinementValues?has_content>
          <#assign indx=0/>
          <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
            <#assign valueSize = facet.refinementValues.size()/>
            <#list facet.refinementValues as refinementValue>
              <#assign indx = indx + 1/>
              <#assign hideClass="showThem"/>
              <#if (indx > facetMinValue)>
                <#assign hideClass="hideThem"/>
              </#if>
              <li class="facetValue ${hideClass}">
                <#assign refinementValueName = refinementValue.displayName>
                <#assign code = refinementValue.name>
                <#assign refinementURL = refinementValue.refinementURL>
                <#assign productCategoryUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${refinementURL}')/>
                <a class="facetValueLink" title="${refinementValueName}" href="${productCategoryUrl}">${refinementValueName} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${refinementValue.scalarCount})</#if></a>
              </li>
              <#if valueSize! lt facetMaxValue>
                <#assign facetMaxValue = valueSize>
              </#if>
              <#if (indx > facetMinValue) && indx == valueSize>
                <li class="facetValue" id="facet_${facet.name!}">
                  <#assign remaining = facetMaxValue?number - facetMinValue?number />
                  <input type="hidden" id="less_${facet.name}" value="${facetMinValue!}" />
                  <input type="hidden" id="remaining_${facet.name!}" value="${remaining!}" />
                  <a class="seeMoreLink" href="javascript:void(0);">${uiLabelMap.FacetSeeMoreLinkCaption} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${remaining!})</#if></a>
                  <a class="seeLessLink" href="javascript:void(0);">${uiLabelMap.FacetSeeLessLinkCaption}</a>
                </li>
              </#if>
            </#list>
          </ul>
        </#if>
      </li>
    </#list>
    </#if>
  </#if>

  <#if facetListPriceRange?has_content>
    <#list facetListPriceRange as facet>
      <li>
        <h3 class="facetGroup">${facet.name}</h3>
        <#if facet.refinementValues?has_content>
          <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
            <#list facet.refinementValues as refinementValue>
              <li class="facetValue">
                <#assign refinementValueName = refinementValue.displayName>
                <#assign code = refinementValue.name>
                <#assign refinementURL = refinementValue.refinementURL>
                <#if facet.refinementValues?size == 1>
                  ${refinementValueName} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${refinementValue.scalarCount})</#if>
                <#else>
                  <#assign productCategoryUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${refinementURL}')/>
                  <a class="facetValueLink" title="${refinementValueName}" href="${productCategoryUrl}">${refinementValueName} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${refinementValue.scalarCount})</#if></a>  
                </#if>
              </li>
            </#list>
          </ul>
        </#if>
      </li>
    </#list>
  </#if>
  
  <#if facetListCustomerRating?has_content>
    <#list facetListCustomerRating as facet>
      <li>
        <h3 class="facetGroup">${facet.name}</h3>
        <#if facet.refinementValues?has_content>
          <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
            <#list facet.refinementValues as refinementValue>
              <li class="facetValue">
                <#assign refinementValueImage = refinementValue.displayImage>
                <#assign refinementValueName = refinementValue.displayName>
                <#assign code = refinementValue.name>
                <#assign refinementURL = refinementValue.refinementURL>
                <#assign productCategoryUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${refinementURL}')/>
                <a class="facetValueLink" title="${refinementValueName}" href="${productCategoryUrl}">
                  <img alt="${refinementValueName}" src="/osafe_theme/images/user_content/images/rating_facet_bar_${refinementValue.start}.gif" class="ratingFacetBar">
                  ${refinementValueName}<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${refinementValue.scalarCount})</#if>
                </a>
              </li>
            </#list>
          </ul>
        </#if>
      </li>
    </#list>
  </#if>
  
  <#assign includedSearchFacetGroup = Static["javolution.util.FastList"].newInstance()!""/>
  <#if SEARCH_FACET_GROUP_INCLUDE?has_content>
    <#assign includedSearchFacetGroupList = SEARCH_FACET_GROUP_INCLUDE?split(",") />
  </#if>
  <#if includedSearchFacetGroupList?has_content>
	  <#list includedSearchFacetGroupList as searchFacetGroup>
	    <#assign newSearchFacetGroup = includedSearchFacetGroup.add(searchFacetGroup?trim?upper_case)/>
	  </#list>
  </#if>
    
  <#if facetList?has_content>
    <#list facetList as facet>
      <#if parameters.searchText?has_content && !parameters.productCategoryId?has_content>
      <#if includedSearchFacetGroup?has_content && includedSearchFacetGroup.contains(facet.productFeatureGroupId?upper_case!)>
        <li>
          <h3 class="facetGroup">${facet.name}</h3>
          <#if facet.refinementValues?has_content>
            <#assign facetMinValue = FACET_VALUE_MIN?if_exists?number />
            <#assign facetMaxValue = FACET_VALUE_MAX?if_exists?number />
            <#assign indx=0/>
            <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
              <#assign valueSize = facet.refinementValues.size()/>
              <#list facet.refinementValues as refinementValue>
                <#assign indx = indx + 1/>
                <#assign hideClass="showThem"/>
                <#if (indx > facetMinValue)>
                  <#assign hideClass="hideThem"/>
                </#if>
                <li class="facetValue ${hideClass}">
                  <#assign refinementValueName = refinementValue.displayName>
                  <#assign code = refinementValue.name>
                  <#assign refinementURL = refinementValue.refinementURL>
                  <#assign productCategoryUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${refinementURL}')/>
                  <a class="facetValueLink" title="${refinementValueName}" href="${productCategoryUrl}">${refinementValueName} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${refinementValue.scalarCount})</#if></a>
                </li>
                <#if valueSize! lt facetMaxValue>
                  <#assign facetMaxValue = valueSize>
                </#if>
                <#if (indx > facetMinValue) && indx == facetMaxValue>
                  <li class="facetValue" id="facet_${facet.productFeatureGroupId!}">
                    <#assign remaining = facetMaxValue?number - facetMinValue?number />
                    <input type="hidden" id="less_${facet.productFeatureGroupId!}" value="${facetMinValue!}" />
                    <input type="hidden" id="remaining_${facet.productFeatureGroupId!}" value="${remaining!}" />
                    <a class="seeMoreLink" href="javascript:void(0);">${uiLabelMap.FacetSeeMoreLinkCaption} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${remaining!})</#if></a>
                    <a class="seeLessLink" href="javascript:void(0);">${uiLabelMap.FacetSeeLessLinkCaption}</a>
                  </li>
                </#if>
                <#if indx == facetMaxValue><#break></#if>
              </#list>
            </ul>
          </#if>
        </li>
      </#if>
      </#if>
      
      <#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNum")/>
      <#assign productFeatureCatGrpApplList=""/> 
      <#if parameters.productCategoryId?has_content>
        <#assign productFeatureCatGrpApplList = delegator.findByAnd("ProductFeatureCatGrpAppl", {"productCategoryId" : parameters.productCategoryId, "productFeatureGroupId" : facet.productFeatureGroupId!})>
        <#assign productFeatureCatGrpAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureCatGrpApplList!)/>
      </#if>
      <#if productFeatureCatGrpAppls?has_content>
        <#assign productFeatureCatGrpAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureCatGrpAppls) />
        <#assign facetMinValue = productFeatureCatGrpAppl.facetValueMin?default(FACET_VALUE_MIN?if_exists?number)/>
        <#assign facetMaxValue = productFeatureCatGrpAppl.facetValueMax?default(FACET_VALUE_MAX?if_exists?number)/>
        <#assign fromDate = productFeatureCatGrpAppl.fromDate!/>
        <#assign thruDate = productFeatureCatGrpAppl.thruDate!/>
        <#if ((productFeatureCatGrpAppl.getTimestamp("fromDate"))?exists && (!nowTimestamp.before(productFeatureCatGrpAppl.getTimestamp("fromDate")))) && (!productFeatureCatGrpAppl.getTimestamp("thruDate")?has_content || (!nowTimestamp.after(productFeatureCatGrpAppl.getTimestamp("thruDate")!)))>
          <li>
            <h3 class="facetGroup">${facet.name}</h3>
            <#if facet.refinementValues?has_content>
              <#assign indx=0/>
              <ul id="${facet.name?lower_case?replace(" ","_")}" class="facetGroup">
                <#assign valueSize = facet.refinementValues.size()/>
                <#list facet.refinementValues as refinementValue>
                  <#assign indx = indx + 1/>
                  <#assign hideClass="showThem"/>
                  <#if (indx > facetMinValue)>
                    <#assign hideClass="hideThem"/>
                  </#if>
                  <li class="facetValue ${hideClass}">
                    <#assign refinementValueName = refinementValue.displayName>
                    <#assign code = refinementValue.name>
                    <#assign refinementURL = refinementValue.refinementURL>
                    <#assign productCategoryUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${refinementURL}')/>
                    <a class="facetValueLink" title="${refinementValueName}" href="${productCategoryUrl}">${refinementValueName} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${refinementValue.scalarCount})</#if></a>
                  </li>
                  <#if valueSize! lt facetMaxValue>
                    <#assign facetMaxValue = valueSize>
                  </#if>
                  <#if (indx > facetMinValue) && indx == facetMaxValue>
                    <li class="facetValue" id="facet_${facet.productFeatureGroupId}">
                      <#assign remaining = facetMaxValue?number - facetMinValue?number />
                      <input type="hidden" id="less_${facet.productFeatureGroupId}" value="${facetMinValue!}" />
                      <input type="hidden" id="remaining_${facet.productFeatureGroupId}" value="${remaining!}" />
                      <a class="seeMoreLink" href="javascript:void(0);">${uiLabelMap.FacetSeeMoreLinkCaption} <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(FACET_SHOW_ITEM_CNT)>(${remaining!})</#if></a>
                      <a class="seeLessLink" href="javascript:void(0);">${uiLabelMap.FacetSeeLessLinkCaption}</a>
                    </li>
                  </#if>
                  <#if indx == facetMaxValue><#break></#if>
                </#list>
              </ul>
            </#if>
          </li>
        </#if>
      </#if>
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
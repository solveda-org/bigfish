<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#-- variable setup and worker calls -->
<#assign topLevelList = requestAttributes.topLevelList?if_exists>
<#assign curCategoryId = requestAttributes.curCategoryId?if_exists>
<#assign curTopMostCategoryId = requestAttributes.curTopMostCategoryId?if_exists>
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>

<#-- looping macro -->
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
           <#assign catalogFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'${categoryUrl}?productCategoryId=${category.productCategoryId!""}')/>
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
              <span>${facetPart2Desc}</span>
            <#else>
             <a href="<@ofbizUrl><#if !searchText?has_content>eCommerceProductList<#else>siteSearch</#if>?${catOrSearchText}&filterGroup=${filterGroupValue?if_exists}</@ofbizUrl>">${facetPart2Desc}</a>
           </#if>
           <a class="removeFacet" href="<@ofbizUrl><#if !searchText?has_content>eCommerceProductList<#else>siteSearch</#if>?${catOrSearchText}<#if removeFilterGroupValue?has_content>&filterGroup=${removeFilterGroupValue?if_exists}</#if></@ofbizUrl>" title="${uiLabelMap.RemoveLabel} ${facetPart2Desc}"><span class="removeFacetIcon"></span></a>
      </li>
</#macro>

<div class="breadcrumbs">
  <ul id="breadcrumb">
    <li class="first">
      <a href="<@ofbizUrl>main</@ofbizUrl>"><span>${uiLabelMap.Home}</span></a>
    </li>
    <#if searchText?has_content && filterGroupValues?has_content>
      <li>
        <a href="<@ofbizUrl>siteSearch?searchText=${searchText}</@ofbizUrl>"><span>${searchText}</span></a>
      </li>
    <#else>
	     <#if searchText?has_content>
	        <li><span>${searchText}</span></li>
	     </#if>
    </#if>
    <#-- Show the category branch -->
    <#if productCategoryIdFacet?has_content && productCategoryIdFacet=="N">
        <#list topLevelList as category>
          <@topCategoryList category=category/>
        </#list>
    </#if>
    <#if pdpProductName?has_content>
        <@renderProductCrumb pdpProductName=pdpProductName/>
    </#if>
    <#if facetGroups?has_content>
        <#assign facetIdx =0/>
        <#assign facetSize =facetGroups.size()/>
        <#list facetGroups as facet>
          <#assign facetIdx = facetIdx +1/>
          <@renderFacetCrumb facet=facet listSize=facetSize index=facetIdx/>
        </#list>
    </#if>
  </ul>
</div>

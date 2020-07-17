<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#-- variable setup and worker calls -->
<#assign topLevelList = requestAttributes.topLevelList?if_exists>
<#assign curCategoryId = requestAttributes.curCategoryId?if_exists>
<#assign curTopMostCategoryId = requestAttributes.curTopMostCategoryId?if_exists>
<#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<#assign FACET_VALUE_STYLE = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_VALUE_STYLE")!"DEFAULT"/>
<#if !FACET_VALUE_STYLE?has_content>
	<#assign FACET_VALUE_STYLE = "DEFAULT"/>
<#else>
	<#assign FACET_VALUE_STYLE = FACET_VALUE_STYLE?string?upper_case/>
</#if>


<div class="breadcrumbs">
  <ul id="breadcrumb">
    <li class="first">
      <a href="<@ofbizUrl>main</@ofbizUrl>"><span>${uiLabelMap.Home}</span></a>
    </li>
    <#if searchText?has_content && filterGroupValues?has_content>
      <li>
        <#assign searchText = Static["org.ofbiz.base.util.StringUtil"].wrapString(searchText)!/>
        <a href="<@ofbizUrl>siteSearch?searchText=${searchText}</@ofbizUrl>">${searchText}</span></a>
      </li>
    <#else>
	     <#if searchText?has_content>
	        <#assign searchText = Static["org.ofbiz.base.util.StringUtil"].wrapString(searchText)!/>
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
    <#if facetGroups?has_content && FACET_VALUE_STYLE != "CHECKBOX">
        <#assign facetIdx =0/>
        <#assign facetSize =facetGroups.size()/>
        <#list facetGroups as facet>
          <#assign facetIdx = facetIdx +1/>
          <@renderFacetCrumb facet=facet listSize=facetSize index=facetIdx/>
        </#list>
    </#if>
  </ul>
</div>

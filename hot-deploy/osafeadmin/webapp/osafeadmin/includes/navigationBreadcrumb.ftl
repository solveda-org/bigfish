<#if navigationCategoryText?exists && navigationCategoryText?has_content>
  <ul id="navigationBreadcrumb">
    <li class="breadcrumb first">
      <a href="${navigationCategoryHref!}">
      	 <span>${uiLabelMap.get(navigationCategoryText)!}</span>
      </a>
    </li>
    <#if subSubMenuItemText?exists && subSubMenuItemText?has_content>
      <li class="breadcrumb">
        <a href="${subSubMenuItemHref!}">
        	<span>${uiLabelMap.get(subSubMenuItemText)!}</span>
        </a>
      </li>
    </#if>
    <#if breadcrumbTitle?exists && breadcrumbTitle?has_content>
  	  <li class="breadcrumb">
  		<span>${breadcrumbTitle!}</span>
  	  </li>
    </#if>
  </ul>
</#if>








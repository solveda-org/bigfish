<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>

<#-- variable setup and worker calls -->
<#if (requestAttributes.topLevelList)?exists>
 <#assign topLevelList = requestAttributes.topLevelList>
</#if>

<div class="${request.getAttribute("attributeClass")!}" id="eCommerceNavBar">
<#--
    Current nav bar is genrated as a single level menu
    http://htmldog.com/articles/suckerfish/dropdowns/
    -->
    
<#assign superMegaMenuContentXrefs = delegator.findByAndCache("XContentXref", Static["org.ofbiz.base.util.UtilMisc"].toMap("productStoreId" , productStoreId, "bfContentId", "SI_SUPER_MEGA_MENU")) />
<#if superMegaMenuContentXrefs?has_content>
	<#assign superMegaMenuContentXref = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(superMegaMenuContentXrefs) />
	<#assign superMegaMenuContents= delegator.findByAndCache("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId" , superMegaMenuContentXref.contentId)) />
	<#if superMegaMenuContents?has_content>
		<#assign superMegaMenuContent = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(superMegaMenuContents) />
		<#assign superMegaMenuContentStatusId = superMegaMenuContent.statusId!/>
		<#if previewContentId?has_content>
			<#if (previewContentId == superMegaMenuContent.contentId)>
				<#assign superMegaMenuContentStatusId = "CTNT_PUBLISHED"/>
			</#if>
		</#if>
	</#if>
</#if>

<#if superMegaMenuContentStatusId?exists && superMegaMenuContentStatusId?has_content && superMegaMenuContentStatusId=="CTNT_PUBLISHED">
  <div id="eCommerceNavBarWidget">
    <a href="javascript:void(0);" class="showNavWidget"><span>${uiLabelMap.ShowNavWidgetLabel}</span></a>
	<a href="javascript:void(0);" class="hideNavWidget" style="display:none"><span>${uiLabelMap.HideNavWidgetLabel}</span></a>
  </div>
  <#-- By default, nav menu will initially be hidden on small screen -->
  <ul id="eCommerceNavBarMenu" class="hideNavBarMenu">
  	${screens.render("component://osafe/widget/EcommerceContentScreens.xml#SI_SUPER_MEGA_MENU")}
  </ul>
<#elseif topLevelList?has_content>
	<div id="eCommerceNavBarWidget">
	    <a href="javascript:void(0);" class="showNavWidget"><span>${uiLabelMap.ShowNavWidgetLabel}</span></a>
		<a href="javascript:void(0);" class="hideNavWidget" style="display:none"><span>${uiLabelMap.HideNavWidgetLabel}</span></a>
	</div>
	<#-- By default, nav menu will initially be hidden on small screen -->
	<ul id="eCommerceNavBarMenu" class="hideNavBarMenu">
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

</div>
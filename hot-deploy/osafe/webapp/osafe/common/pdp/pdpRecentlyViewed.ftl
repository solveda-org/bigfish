<#-- variable setup and worker calls -->
<#assign maxRecentlyViewedProducts = pdpRecentViewedMax/>
<#if sessionAttributes.lastViewedProducts?exists && sessionAttributes.lastViewedProducts?has_content>
 <div class="pdpRecentlyViewed">
  <#assign PRODUCT_STORE_PARM_FACET = PLP_FACET_GROUP_VARIANT_SWATCH_IMG!""/>
  ${setRequestAttribute("PRODUCT_STORE_PARM_FACET",PRODUCT_STORE_PARM_FACET)}
  <#if PRODUCT_STORE_PARM_FACET?has_content>
    <#assign PRODUCT_STORE_PARM_FACET=PRODUCT_STORE_PARM_FACET.toUpperCase()/>
    ${setRequestAttribute("PRODUCT_STORE_PARM_FACET",PRODUCT_STORE_PARM_FACET)}
  </#if>
  <#assign featureValueSelected=""/>
  ${setRequestAttribute("featureValueSelected",featureValueSelected)}
  <#if facetGroups?has_content && PRODUCT_STORE_PARM_FACET?has_content>
    <#list facetGroups as facet>
      <#if PRODUCT_STORE_PARM_FACET == facet.facet>
        <#assign featureValueSelected=facet.facetValue!""/>
        ${setRequestAttribute("featureValueSelected",featureValueSelected)}
      </#if>
    </#list>
  </#if>
  <#if searchTextGroups?has_content && PRODUCT_STORE_PARM_FACET?has_content>
    <#list searchTextGroups as facet>
      <#if PRODUCT_STORE_PARM_FACET == facet.facet>
        <#assign featureValueSelected=facet.facetValue!""/>
        ${setRequestAttribute("featureValueSelected",featureValueSelected)}
      </#if>
    </#list>
  </#if>
  <#assign count = 1/>
  <h2>${uiLabelMap.RecentlyViewedProductHeading}</h2>
  <#list sessionAttributes.lastViewedProducts as productId>
    ${setRequestAttribute("plpItemId",productId)}
    <!-- DIV for Displaying Recommended productss STARTS here -->
    <div class="eCommerceListItem eCommerceRecentlyViewedProduct">
      ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#PDPRecentDivSequence")}
    </div>
    <#if count == maxRecentlyViewedProducts?number>
       <#break>
    </#if>
    <#assign count = count+1/>
    <!-- DIV for Displaying PLP item ENDS here -->     
  </#list>
 </div>
</#if>

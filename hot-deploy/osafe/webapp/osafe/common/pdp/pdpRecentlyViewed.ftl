<#-- variable setup and worker calls -->
<#assign maxRecentlyViewedProducts = pdpRecentViewedMax/>
<#if sessionAttributes.lastViewedProducts?exists && sessionAttributes.lastViewedProducts?has_content>
 <div class="pdpRecentlyViewed">
  <#assign PLP_FACET_GROUP_VARIANT_SWATCH = PLP_FACET_GROUP_VARIANT_SWATCH_IMG!""/>
  ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_SWATCH",PLP_FACET_GROUP_VARIANT_SWATCH)}
  <#if PLP_FACET_GROUP_VARIANT_SWATCH?has_content>
    <#assign PLP_FACET_GROUP_VARIANT_SWATCH=PLP_FACET_GROUP_VARIANT_SWATCH.toUpperCase()/>
    ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_SWATCH",PLP_FACET_GROUP_VARIANT_SWATCH)}
  </#if>
   <#if PLP_FACET_GROUP_VARIANT_PDP_MATCH?has_content>
      <#assign PLP_FACET_GROUP_VARIANT_STICKY=PLP_FACET_GROUP_VARIANT_PDP_MATCH.toUpperCase()/>
       ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_STICKY",PLP_FACET_GROUP_VARIANT_STICKY)}
   </#if>
   <#assign featureValueSelected=""/>
   ${setRequestAttribute("featureValueSelected",featureValueSelected)}
   <#assign facetGroupMatch = FACET_GROUP_VARIANT_MATCH!""/>
   <#if facetGroupMatch?has_content>
      <#assign facetGroupMatch=facetGroupMatch.toUpperCase()/>
      ${setRequestAttribute("FACET_GROUP_VARIANT_MATCH",facetGroupMatch)}
   </#if>
   <#if facetGroups?has_content && facetGroupMatch?has_content>
      <#list facetGroups as facet>
        <#if facetGroupMatch == facet.facet>
            <#assign featureValueSelected=facet.facetValue!""/>
             ${setRequestAttribute("featureValueSelected",featureValueSelected)}
        </#if>
      </#list>
   </#if>
   <#if searchTextGroups?has_content && facetGroupMatchhas_content>
      <#list searchTextGroups as facet>
        <#if facetGroupMatch == facet.facet>
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

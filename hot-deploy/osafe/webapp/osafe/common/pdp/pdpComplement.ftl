<#-- variable setup and worker calls -->
<#assign productName = productContentWrapper.get("PRODUCT_NAME")!currentProduct.productName!"">

<#if recommendProducts?has_content>
  <div class="pdpComplement">
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
       <h2>${uiLabelMap.ComplementProductHeading}</h2>
            <#list recommendProducts as complementProduct>
             ${setRequestAttribute("plpItemId",complementProduct.productId)}
                 <!-- DIV for Displaying Recommended productss STARTS here -->
                      <div class="eCommerceListItem eCommerceComplementProduct">
                        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#PDPComplementDivSequence")}
                      </div>
                 <!-- DIV for Displaying PLP item ENDS here -->     
            </#list>
  </div>
</#if>


<li class="${request.getAttribute("attributeClass")!}">
  <#-- variable setup and worker calls -->
  <#if accessoryProducts?has_content>
    <div class="js_pdpAccessory">
       <#assign plpFacetGroupVariantSwatch = Static["com.osafe.util.Util"].getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_SWATCH_IMG")!""/>
       <#assign plpFacetGroupVariantSticky =  Static["com.osafe.util.Util"].getProductStoreParm(request,"PLP_FACET_GROUP_VARIANT_PDP_MATCH")!""/>
       <#assign facetGroupMatch = Static["com.osafe.util.Util"].getProductStoreParm(request,"FACET_GROUP_VARIANT_MATCH")!""/>
       
       ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_SWATCH",plpFacetGroupVariantSwatch)}
       <#if plpFacetGroupVariantSwatch?has_content>
          <#assign plpFacetGroupVariantSwatch=plpFacetGroupVariantSwatch.toUpperCase()/>
           ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_SWATCH",plpFacetGroupVariantSwatch)}
       </#if>
       
       <#if plpFacetGroupVariantSticky?has_content>
          <#assign plpFacetGroupVariantSticky=plpFacetGroupVariantSticky.toUpperCase()/>
           ${setRequestAttribute("PLP_FACET_GROUP_VARIANT_STICKY",plpFacetGroupVariantSticky)}
       </#if>
       
       <#assign featureValueSelected=""/>
       ${setRequestAttribute("featureValueSelected",featureValueSelected)}

       <#if facetGroupMatch?has_content>
          <#assign facetGroupMatch=facetGroupMatch.toUpperCase()/>
           ${setRequestAttribute("FACET_GROUP_VARIANT_MATCH",facetGroupMatch)}
       </#if>
       
       <#if facetGroups?has_content && facetGroupMatch?has_content>
          <#list facetGroups as facet>
            <#if facetGroupMatch == facet.facet>
                <#assign featureValueSelected=facet.facetValue!""/>
                 ${setRequestAttribute("featureValueSelected",featureValueSelected)}
                 <#break>
            </#if>
          </#list>
       </#if>
       
       <#if searchTextGroups?has_content && facetGroupMatch?has_content>
          <#list searchTextGroups as facet>
            <#if facetGroupMatch == facet.facet>
                <#assign featureValueSelected=facet.facetValue!""/>
                ${setRequestAttribute("featureValueSelected",featureValueSelected)}
                 <#break>
            </#if>
          </#list>
       </#if>
       
       <h2>${uiLabelMap.AccessoryProductHeading}</h2>
       <div class="boxList productList">
        <#list accessoryProducts as accessoryProduct>
            ${setRequestAttribute("plpItemId",accessoryProduct.productIdTo)}
            <!-- DIV for Displaying Accessory products STARTS here -->
            ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#PDPAccessoryDivSequence")}
            <!-- DIV for Displaying Accessory products ENDS here -->     
        </#list>
      </div>
    </div>
  </#if>
</li>


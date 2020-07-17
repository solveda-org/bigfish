<#-- variable setup and worker calls -->
<#assign productName = productContentWrapper.get("PRODUCT_NAME")!currentProduct.productName!"">

<#if recommendProducts?has_content>
  <div id="pdpComplement">
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
       <h2>${uiLabelMap.ComplementProductHeading}</h2>
            <#list recommendProducts as complementProduct>
             ${setRequestAttribute("plpItemId",complementProduct.productId)}
                 <!-- DIV for Displaying Recommended productss STARTS here -->
                      <div class="eCommerceComplementProduct eCommerceListItem">
                        ${screens.render("component://osafe/widget/EcommerceScreens.xml#plpDivSequence")}
                      </div>
                 <!-- DIV for Displaying PLP item ENDS here -->     
            </#list>
  </div>
</#if>


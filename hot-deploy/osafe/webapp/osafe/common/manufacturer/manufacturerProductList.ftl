<div class="resultsNavigation">
  ${screens.render("component://osafe/widget/EcommerceScreens.xml#eCommercePagingControls")}
</div>
<div class="manufacturerProductList"> 
  <#list eCommerceResultList as product>		
    ${setRequestAttribute("plpItem",product)}
	<#assign categoryId = product.primaryProductCategoryId!"">
	${setRequestAttribute("productCategoryId",categoryId)}
	<#assign productId = product.productId!"">
    <!-- DIV for Displaying PLP item STARTS here -->
	<div class="eCommerceListItem manufacturerListItem">
	  ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#manufacturerProductListDivSequence")}
	</div>
    <!-- DIV for Displaying PLP item ENDS here -->     
   </#list>
   <div class="spacer"></div>
</div>


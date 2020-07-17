<div class="${request.getAttribute("attributeClass")!}">
	<div class="resultsNavigation">
	  ${screens.render("component://osafe/widget/EcommerceScreens.xml#eCommercePagingControls")}
	</div>
	<div class="boxList productList">
	  <#list eCommerceResultList as product>		
	    ${setRequestAttribute("plpItem",product)}
		<#assign categoryId = product.primaryProductCategoryId!"">
		${setRequestAttribute("productCategoryId",categoryId)}
		<#assign productId = product.productId!"">
	    <!-- DIV for Displaying PLP item STARTS here -->
		  ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#manufacturerProductListDivSequence")}
	    <!-- DIV for Displaying PLP item ENDS here -->     
	   </#list>
	</div>
</div>


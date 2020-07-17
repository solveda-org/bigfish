<div class="cartOrderItems">

 <#if (shoppingCartSize > 0)>
  <div class="showCartItems">
  <#assign offerPriceVisible= "N"/>
  <#list shoppingCart.items() as cartLine>
    <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
    <#if (cartItemAdjustment &lt; 0) >
      <#assign offerPriceVisible= "Y" />
      <#break>
    </#if>
  </#list>
  <div id="cart_wrap">
    <input type="hidden" name="removeSelected" value="false"/>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
        <input type="hidden" name="guest" value="guest"/>
    </#if>
    
    <#assign itemsFromList = false>
        <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
        <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />

    <#list shoppingCart.items() as cartLine>
      <#--
      ${setRequestAttribute("cartLine",cartLine)}
      <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine)>
      ${setRequestAttribute("cartLineIndex",cartLineIndex)}
          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures()>
          ${setRequestAttribute("lineOptionalFeatures",lineOptionalFeatures)}
          <#assign product = cartLine.getProduct()>
          <#assign urlProductId = cartLine.getProductId()>
          <#assign productCategoryId = product.primaryProductCategoryId!""/>
          <#assign productCategoryId = cartLine.getProductCategoryId()?if_exists>
          <#if !productCategoryId?has_content>
             <#assign productCategoryMemberList = product.getRelatedCache("ProductCategoryMember") />
             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMemberList,true)/>
             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productCategoryMemberList,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
              <#if productCategoryMemberList?has_content>
    	          <#assign productCategoryMember = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMemberList)/>
                  <#assign productCategoryId = productCategoryMember.productCategoryId!"">
              </#if>
          </#if>
          <#if product.isVariant?if_exists?upper_case == "Y">
             <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(cartLine.getProductId(), delegator)?if_exists>
             <#assign urlProductId=virtualProduct.productId>
             <#if !productCategoryId?has_content>
	             <#assign productCategoryMemberList = virtualProduct.getRelatedCache("ProductCategoryMember") />
	             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productCategoryMemberList,true)/>
	             <#assign productCategoryMemberList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productCategoryMemberList,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
	              <#if productCategoryMemberList?has_content>
	    	          <#assign productCategoryMember =Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productCategoryMemberList)/>
	                  <#assign productCategoryId = productCategoryMember.productCategoryId!"">
	              </#if>
              </#if>
          </#if>
          <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${urlProductId}&productCategoryId=${productCategoryId!""}')/>
          
          
          -->
          
          
          
      ${setRequestAttribute("cartLine", cartLine)}
      ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#lightCartOrderItemsDivSequence")}
    </#list>
    
    
    
    
    
    
    
    
    
    
    
    

  </div>
</div>
<#else>
  <div class="showCartItems">
    <div class="displayBox">
      <p class="instructions">${uiLabelMap.YourShoppingCartIsEmptyInfo}</p>
    </div>
  </div>
 </#if>


</div>
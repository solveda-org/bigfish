 <#if (shoppingCartSize > 0)>
  <#assign offerPriceVisible= "N"/>
  <#list shoppingCart.items() as cartLine>
    <#assign cartItemAdjustment = cartLine.getOtherAdjustments()/>
    <#if (cartItemAdjustment &lt; 0) >
      <#assign offerPriceVisible= "Y" />
      <#break>
    </#if>
  </#list>
    <input type="hidden" name="removeSelected" value="false"/>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
        <input type="hidden" name="guest" value="guest"/>
    </#if>
    <#assign itemsFromList = false>
    <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
    <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
    
   <div class="${request.getAttribute("attributeClass")!}">
    <div class="boxList cartList">
	    <#list shoppingCart.items() as cartLine>
	      ${setRequestAttribute("cartLine", cartLine)}
	      ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#showCartOrderItemsDivSequence")}
          <#-- If product has a PDP_QTY_MIN or MAX to override system parameter, then use these values for validation -->
          <#if pdpQtyMinAttributeValue?exists && pdpQtyMinAttributeValue?has_content && pdpQtyMaxAttributeValue?exists && pdpQtyMaxAttributeValue?has_content>
              <input type="hidden" name="pdpQtyMinAttributeValue_${cartLineIndex!}" id="js_pdpQtyMinAttributeValue_${cartLine.getProductId()!}" value="${pdpQtyMinAttributeValue!}"/>
              <input type="hidden" name="pdpQtyMaxAttributeValue_${cartLineIndex!}" id="js_pdpQtyMaxAttributeValue_${cartLine.getProductId()!}" value="${pdpQtyMaxAttributeValue!}"/>
          </#if>
	    </#list>
	</div>
   </div>
 <#else>
 <div class="${request.getAttribute("attributeClass")!}">
  <div class="displayBox">
    <p class="instructions">${uiLabelMap.YourShoppingCartIsEmptyInfo}</p>
  </div>
 </div>
 </#if>

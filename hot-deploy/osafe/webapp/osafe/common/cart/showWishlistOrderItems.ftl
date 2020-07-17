 <#if (wishListSize > 0)>
  <#assign offerPriceVisible= "N"/>
    <input type="hidden" name="removeSelected" value="false"/>
    <input type="hidden" name="add_item_id" id="js_add_item_id" value=""/>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
        <input type="hidden" name="guest" value="guest"/>
    </#if>
    <#assign itemsFromList = false>
    <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
    <#assign currencyUom = CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency() />
    
    <#assign rowNo = 0/>
    <div class="boxList cartList">
	    <#list wishList as wishListItem>
	        ${setRequestAttribute("rowNo", rowNo)}
	        ${setRequestAttribute("wishListItem", wishListItem)}
	        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#showWishlistOrderItemsDivSequence")}
	        <#assign rowNo = rowNo+1/>
            <input type="hidden" name="product_name_${lineIndex}" id="js_productName_${wishListSeqId!}" value="${wrappedProductName!}" /> 
            <#-- If product has a PDP_QTY_MIN or MAX to override system parameter, then use these values for validation -->
            <#if pdpQtyMinAttributeValue?exists && pdpQtyMinAttributeValue?has_content && pdpQtyMaxAttributeValue?exists && pdpQtyMaxAttributeValue?has_content>
              <input type="hidden" name="pdpQtyMinAttributeValue_${cartLineIndex}" id="js_pdpQtyMinAttributeValue_${productId}" value="${pdpQtyMinAttributeValue!}"/>
              <input type="hidden" name="pdpQtyMaxAttributeValue_${cartLineIndex}" id="js_pdpQtyMaxAttributeValue_${productId}" value="${pdpQtyMaxAttributeValue!}"/>
            </#if>
	    </#list>
    </div>
 <#else>
  <div class="displayBox">
    <p class="instructions">${uiLabelMap.YourWishListIsEmptyInfo}</p>
  </div>
 </#if>

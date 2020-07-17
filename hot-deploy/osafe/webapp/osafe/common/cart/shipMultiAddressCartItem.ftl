<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="boxListItemTabular shipItem multiAddressShipItem">
  <div class="multiAddressItems group group1">
   <ul class="displayList shipItemList multiAddressItems">
	<li class="image itemImage multiAddressItemImage<#if lineIndex == 0> firstRow</#if>">
	  <div>
	      <img alt="${wrappedProductName}" src="${productImageUrl}" class="productCartListImage" height="${IMG_SIZE_CART_H!""}" width="${IMG_SIZE_CART_W!""}" <#if productImageAltUrl?has_content && productImageAltUrl != ''> onmouseover="src='${productImageAltUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${productImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');">     
	  </div>
	</li>
   </ul>
  </div>           
  <div class="multiAddressItems group group2">
   <ul class="displayList shipItemList multiAddressItems">
	<li class="string itemName multiAddressItemName<#if lineIndex == 0> firstRow</#if>">
	  <div>
          <label>${uiLabelMap.CartItemNameCaption}</label>
		  <span>${wrappedProductName}</span>
     	  <input type="hidden" name="productName_${lineIndex}" value="${wrappedProductName!}"/>
     	  <input type="hidden" name="productId_${lineIndex}" value="${cartLine.getProductId()!}"/>
     	  <input type="hidden" name="parentProductId_${lineIndex}" value="${cartLine.getParentProductId()!}"/>
     	  <input type="hidden" name="productCategoryId_${lineIndex}" value="${cartLine.getProductCategoryId()!}"/>
     	  <input type="hidden" name="prodCatelogId_${lineIndex}" value="${cartLine.getProdCatalogId()!}"/>
     	  <input type="hidden" name="unitPrice_${lineIndex}" value="${cartLine.getBasePrice()!}"/>
     	  <input type="hidden" name="cartLineIndex_${lineIndex}" value="${cartLineIndex!}"/>
	  </div>
	</li>			
	<li class="string itemDescription multiAddressItemDescription<#if lineIndex == 0> firstRow</#if>">
	  <div>
	    <label>${uiLabelMap.CartItemDescriptionCaption}</label>
	    <span>
	      <ul class="displayList productFeature">
			<li class="string productFeature">
			 <div>
			  <#if productFeatureAndAppls?has_content>
			      <#list productFeatureAndAppls as productFeatureAndAppl>
				    <#assign productFeatureTypeLabel = ""/>
				    <#if productFeatureTypesMap?has_content>
				      <#assign productFeatureTypeLabel = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
				    </#if>
				    <span>${productFeatureTypeLabel!}:${productFeatureAndAppl.description!}</span>
			      </#list>
			  </#if>
	         </div>
	       </li>
	      </ul>
	    </span>
	  </div>
	</li>   
    <li class="number itemQty multiAddressItemQty<#if lineIndex == 0> firstRow</#if>">
      <#assign qtyInput= parameters.get("qtyInCart_${lineIndex}")!1/>
	  <div>
	    <label>${uiLabelMap.CartItemQuantityCaption}</label>
	      <input size="6" type="text" name="qtyInCart_${lineIndex}" id="qtyInCart_${lineIndex}" value="${qtyInput}" maxlength="5"/>
	      
	  </div>
	</li>
    <#if lineItemGiftMessage?has_content && lineItemGiftMessage == "Y">
    <li class="string giftMessage multiAddressGiftMessage<#if lineIndex == 0> firstRow</#if>">
	  <div>
        <#if lineItemGiftFrom?has_content>
	        <span>${uiLabelMap.FromCaption} ${lineItemGiftFrom!}</span>
      	    <input type="hidden" name="lineItemGiftFrom_${lineIndex}" value="${lineItemGiftFrom!}"/>
        </#if>
        <#if lineItemGiftTo?has_content>
	        <span>${uiLabelMap.ToCaption} ${lineItemGiftTo!}</span>
      	    <input type="hidden" name="lineItemGiftTo_${lineIndex}" value="${lineItemGiftTo!}"/>
        </#if>
        <#if lineItemGiftMsg?has_content>
	        <span>${uiLabelMap.GiftMessageCaption} ${lineItemGiftMsg!}</span>
      	    <input type="hidden" name="lineItemGiftMsg_${lineIndex}" value="${lineItemGiftMsg!}"/>
        </#if>
	  </div>
	</li>
   </#if>
   </ul>
  </div>
  <div class="multiAddressItems group group3">
   <ul class="displayList shipItemList multiAddressItems">
    <li class="container itemAddress multiAddressItemAddress firstRow">
	  <div>
        <label>${uiLabelMap.ChooseShipAddressLabel}</label>
        <#if shippingContactMechList?has_content>
	         <#assign paramChosenShippingContactMechId= parameters.get("shippingContactMechId_${lineIndex}")!""/>
	         <#list shippingContactMechList as shippingContactMech>
		        <#assign postalAddress = shippingContactMech.getRelatedOneCache("PostalAddress")>
		        <#if paramChosenShippingContactMechId?has_content>
	                <#assign chosenShippingContactMechId= paramChosenShippingContactMechId/>
	            <#else>
	  	           <#if !chosenShippingContactMechId?has_content>
	                <#assign chosenShippingContactMechId= postalAddress.contactMechId/>
	               </#if>
		        </#if>
		        <div class="entry radioOption">
		        <label class="radioOptionLabel">
		         <input type="radio" name="shippingContactMechId_${lineIndex}" value="${postalAddress.contactMechId}" <#if (chosenShippingContactMechId == postalAddress.contactMechId)> checked</#if> />
                 <span class="radioOptionText">
				     ${setRequestAttribute("PostalAddress", postalAddress)}
				     ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_STREET_CITY")}
				     ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
				 </span>
				 </label>
		        </div>
		     </#list>
		  </#if>
      </div>
    </li>
   </ul>
  </div>
  
</div>


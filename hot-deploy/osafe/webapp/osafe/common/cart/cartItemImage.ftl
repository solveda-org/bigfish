<div class="cartItemImage">

TEST IMAGE DIV
  <#--
<#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
  <#if !productImageUrl?has_content && virtualProduct?has_content>
   <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
  </#if>
  //////// If the string is a literal "null" make it an "" empty string then all normal logic can stay the same ///////
  <#if (productImageUrl?string?has_content && (productImageUrl == "null"))>
   <#assign productImageUrl = "">
  </#if>
  <td class="image firstCol <#if !cartLine_has_next>lastRow</#if>" scope="row">
    <a href="${productFriendlyUrl}" id="image_${urlProductId}">
      <#assign IMG_SIZE_CART_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_CART_H")!""/>
      <#assign IMG_SIZE_CART_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_CART_W")!""/>
      <img alt="${StringUtil.wrapString(productName)}" src="${productImageUrl}" class="productCartListImage" height="${IMG_SIZE_CART_H!""}" width="${IMG_SIZE_CART_W!""}">
    </a>
  </td>
-->
</div>

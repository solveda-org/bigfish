<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
    <a href="${productFriendlyUrl}" id="image_${urlProductId}">
      <img alt="${wrappedProductName}" src="${productImageUrl}" class="productCartListImage" height="${IMG_SIZE_CART_H!""}" width="${IMG_SIZE_CART_W!""}" <#if productImageAltUrl?has_content && productImageAltUrl != ''> onmouseover="src='${productImageAltUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});" onmouseout="src='${productImageUrl!""}'; jQuery(this).error(function(){onImgError(this, 'PLP-Thumb');});"</#if> onerror="onImgError(this, 'PLP-Thumb');">     
    </a>
  </div>
</li>
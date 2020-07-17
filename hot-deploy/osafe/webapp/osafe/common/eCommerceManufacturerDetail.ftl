 <div id="eCommerceManufacturerDetail" class="eCommerceManufacturer">
 <#assign description = partyContentWrapper.get("LONG_DESCRIPTION")!"">
 <#assign profileImageUrl = partyContentWrapper.get("PROFILE_IMAGE_URL")!"">
 <#assign profileName = partyContentWrapper.get("PROFILE_NAME")!"">
 <#assign profileFbLikeUrl = partyContentWrapper.get("PROFILE_NAME")!"">
 <#assign profileTweetUrl = partyContentWrapper.get("PROFILE_TWEET_URL")!"">
     <img alt="${profileName}" src="${profileImageUrl!""}" class="profileImage" height="${IMG_SIZE_PROF_MFG_H!""}" width="${IMG_SIZE_PROF_MFG_W!""}">
      <p class="profileName">${profileName!""}</p>
      <p class="profileDescription">${description!""}</p>
 </div>

<#if manufacturerProductList?has_content>
 <div id="eCommerceManufacturerProductList">
   <h2>${uiLabelMap.ManufacturerProfileCollectionHeading}</h2>
    <#list manufacturerProductList as product>
              <#assign productId = product.productId!"">
              <#assign categoryId = product.primaryProductCategoryId!"">
              <#assign productInternalName = product.internalName!"">
              <#assign averageCustomerRating = Static["org.ofbiz.product.product.ProductWorker"].getAverageProductRating(delegator,productId)>
              <#assign productPrice = dispatcher.runSync("calculateProductPrice", Static["org.ofbiz.base.util.UtilMisc"].toMap("product", product, "userLogin", userLogin))/>
              <#assign plpLabel = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(product, 'PLP_LABEL', request)?if_exists>
              <#assign productName = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(product, 'PRODUCT_NAME', request)?if_exists>
              <#assign productImageUrl = Static['org.ofbiz.product.product.ProductContentWrapper'].getProductContentAsText(product, 'SMALL_IMAGE_URL', request)?if_exists>

              <div class="eCommerceListItem productListItem">
                   <div class="eCommerceThumbNailHolder">
                    <a title="${productName}" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&productCategoryId=${categoryId!""}</@ofbizUrl>">
                        <img alt="${productName}" src="${productImageUrl}" class="productThumbnailImage" height="${IMG_SIZE_PLP_H!""}" width="${IMG_SIZE_PLP_W!""}">
                    </a>
                   </div>
                    <a class="eCommerceProductLink" title="${productName}" href="<@ofbizUrl>eCommerceProductDetail?productId=${productId}&productCategoryId=${categoryId!""}</@ofbizUrl>">
                        ${productName}
                    </a>
                    <#if averageCustomerRating?has_content && (averageCustomerRating > 0)>
                        <#assign ratePercentage= ((averageCustomerRating / 5) * 100)>
                        <div class="rating_bar"><div style="width:${ratePercentage}%"></div></div>
                    </#if>
                    <p class="price"><@ofbizCurrency amount=productPrice.listPrice isoCode=productPrice.currencyUsed /></p>
              </div>
              <#assign customerRating=""/>
    </#list>
    <div class="spacer"></div>
 </div> 
</#if>

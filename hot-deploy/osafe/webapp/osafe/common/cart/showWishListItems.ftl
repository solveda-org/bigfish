<#if (wishListSize > 0)>
  <div class="showCartItems">
  <div id="cart_wrap">
    <input type="hidden" name="removeSelected" value="false"/>
    <input type="hidden" name="add_item_id" id="add_item_id" value=""/>
    <table cellspacing="0" cellpadding="0" id="cart_display">
        <thead>
            <tr class="cart_headers">
                <th class="product firstCol" scope="col" colspan="2">${uiLabelMap.Product}</th>
                <th class="quantity" scope="col">${uiLabelMap.QuantityLabel}</th>
                <th class="stockCol" scope="col">${uiLabelMap.AvailabilityLabel}</th>
                <th class="priceCol numberCol" scope="col">${uiLabelMap.PriceLabel}</th>
                <th class="total numberCol" scope="col">${uiLabelMap.TotalLabel}</th>
                <th class="actions" scope="col">&nbsp;</th>
                <th class="actions lastCol" scope="col">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
        <#assign rowNo = 0/>
        <#assign currencyUom = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
        <#list wishList as wishListItem>
          <#assign urlProductId = wishListItem.productId>
          <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",urlProductId), true)/>
          <#assign productPrice = dispatcher.runSync("calculateProductPrice", Static["org.ofbiz.base.util.UtilMisc"].toMap("product", product, "userLogin", userLogin))/>
          <#assign totalPrice = (productPrice.basePrice)*(wishListItem.quantity)>

          <#assign inventoryLevelMap = Static["com.osafe.services.InventoryServices"].getProductInventoryLevel(urlProductId, request)/>
          <#assign inventoryLevel = inventoryLevelMap.get("inventoryLevel")/>
          <#assign inventoryInStockFrom = inventoryLevelMap.get("inventoryLevelInStockFrom")/>
          <#assign inventoryOutOfStockTo = inventoryLevelMap.get("inventoryLevelOutOfStockTo")/>
          <#if (inventoryLevel?number <= inventoryOutOfStockTo?number)>
             <#assign stockInfo = uiLabelMap.OutOfStockLabel/>
             <#assign inStock = false />
          <#else>
            <#assign inStock = true />
            <#if (inventoryLevel?number >= inventoryInStockFrom?number)>
              <#assign stockInfo = uiLabelMap.InStockLabel/>
            <#else>
              <#assign stockInfo = uiLabelMap.LowStockLabel/>
            </#if>
          </#if>

          <#assign productCategoryId = product.primaryProductCategoryId!""/>
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
             <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(urlProductId, delegator)?if_exists>
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

          <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
          <#if !productImageUrl?has_content && virtualProduct?has_content>
               <#assign productImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "SMALL_IMAGE_URL", locale, dispatcher)?if_exists>
          </#if>

          <#-- If the string is a literal "null" make it an "" empty string then all normal logic can stay the same -->
          <#if (productImageUrl?string?has_content && (productImageUrl == "null"))>
               <#assign productImageUrl = "">
          </#if>
          <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "PRODUCT_NAME", locale, dispatcher)?if_exists>
          <#if !productName?has_content && virtualProduct?has_content>
               <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(virtualProduct, "PRODUCT_NAME", locale, dispatcher)?if_exists>
          </#if>

         <#assign productFriendlyUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request,'eCommerceProductDetail?productId=${urlProductId}&productCategoryId=${productCategoryId!""}')/>

            <tr class="cart_contents">
                <td class="image firstCol <#if !wishListItem_has_next>lastRow</#if>" scope="row">

                    <a href="${productFriendlyUrl}" id="image_${urlProductId}">
                      <#assign IMG_SIZE_WISHLIST_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_WISHLIST_H")!""/>
                      <#assign IMG_SIZE_WISHLIST_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_WISHLIST_W")!""/>
                        <img alt="${StringUtil.wrapString(productName)}" src="${productImageUrl}" class="productWishListImage" height="${IMG_SIZE_WISHLIST_H!""}" width="${IMG_SIZE_WISHLIST_W!""}">
                    </a>
                </td>
                <td class="description <#if !wishListItem_has_next>lastRow</#if>">
                    <dl>
                        <dt>${uiLabelMap.ProductDescriptionAttributesInfo}</dt>
                        <dd class="description">
                          <a href="${productFriendlyUrl}">${StringUtil.wrapString(productName!)}</a>
                        </dd>
                         <#assign productFeatureAndAppls = product.getRelatedCache("ProductFeatureAndAppl") />
                         <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(productFeatureAndAppls,true)/>
                         <#assign productFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(productFeatureAndAppls,Static["org.ofbiz.base.util.UtilMisc"].toList('sequenceNum'))/>
                        <#if productFeatureAndAppls?has_content>
                          <#list productFeatureAndAppls as productFeatureAndAppl>
                            <#assign productFeatureTypeLabel = ""/>
                            <#if productFeatureTypesMap?has_content>
                              <#assign productFeatureTypeLabel = productFeatureTypesMap.get(productFeatureAndAppl.productFeatureTypeId)!"" />
                            </#if>
                            <dd>${productFeatureTypeLabel!}:${productFeatureAndAppl.description!}</dd>
                          </#list>
                        </#if>
                    </dl>
                </td>
                <td class="quantity <#if !wishListItem_has_next>lastRow</#if>">
                    <input size="6" type="text" name="update_${wishListItem.shoppingListItemSeqId}" id="update_${rowNo}" value="${wishListItem.quantity?string.number}" maxlength="5"/><span class="action"><a class="standardBtn action" href="javascript:submitCheckoutForm(document.${formName!}, 'UWL', '');">Update</a></span>
                </td>
                <td class="stockCol <#if !wishListItem_has_next>lastRow</#if>">
                    <ul>
                        <li>
                             <span class="stock">${stockInfo!""}</span>
                        </li>
                    </ul>
                </td>
                <td class="priceCol numberCol <#if !wishListItem_has_next>lastRow</#if>">
                    <ul title="Price Information">
                        <li>
                        <div id="priceelement">
                            <ul>
                                <li>
                                    <span class="price"><@ofbizCurrency amount=productPrice.basePrice rounding=2 isoCode=currencyUom/></span>
                                </li>
                            </ul>
                        </div>

                       </li>
                    </ul>
                </td>
                <td class="total numberCol <#if !wishListItem_has_next>lastRow</#if>">
                    <ul>
                        <li>
                            <span class="price"><@ofbizCurrency amount=totalPrice rounding=2 isoCode=currencyUom/></span>
                        </li>
                    </ul>
                </td>
                <td class="actions  <#if !wishListItem_has_next>lastRow</#if>">
                    <ul>
                        <li class="remove">
                            <span class="action">
                                    <a class="standardBtn action" href="<@ofbizUrl>${deleteFromWishListAction!}?delete_${wishListItem.shoppingListItemSeqId}=${wishListItem.shoppingListItemSeqId}</@ofbizUrl>" title="Remove Item">
                                    <span>Remove Item</span>
                                    </a>
                            </span>
                        </li>

                    </ul>
                </td>
                <td class="actions lastCol <#if !wishListItem_has_next>lastRow</#if>">
                    <ul>
                        <li class="addToCart">
                            <#if inStock>
                                <span class="action">
                                    <a class="standardBtn action" href="javascript:submitCheckoutForm(document.${formName!},'ACW','${wishListItem.shoppingListItemSeqId}');" title="Add to Cart">
                                    <span>${uiLabelMap.OrderAddToCartBtn}</span>
                                    </a>
                                </span>
                            </#if>
                        </li>
                    </ul>
                </td>
            </tr>
            <#assign rowNo = rowNo+1/>
        </#list>
      </tbody>
    </table>

  </div>
</div>
<#else>
  <div class="showCartItems">
    <div class="displayBox">
      <p class="instructions">${uiLabelMap.YourWishListIsEmptyInfo}</p>
    </div>
  </div>
</#if>

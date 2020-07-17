<#if product?has_content>
<div class="linkButton">
  <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
  <#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request)! />
  <#if !showDetailLink?has_content>
      <#assign showDetailLink = "true"/>
  </#if>
  <#if !showImageLink?has_content>
      <#assign showImageLink = "true"/>
  </#if>
  <#if !showAttachLink?has_content>
      <#assign showAttachLink = "true"/>
  </#if>
  <#if !showProductFeatureLink?has_content>
      <#assign showProductFeatureLink = "true"/>
  </#if>
  <#if !showVariantLink?has_content>
      <#assign showVariantLink = "true"/>
  </#if>
  <#if !showMetaTagLink?has_content>
      <#assign showMetaTagLink = "true"/>
  </#if>
  <#if !showPricingLink?has_content>
      <#assign showPricingLink = "true"/>
  </#if>
  <#if !showRelatedLink?has_content>
      <#assign showRelatedLink = "true"/>
  </#if>
  <#if !showCategoryMemberLink?has_content>
      <#assign showCategoryMemberLink = "true"/>
  </#if>
  <#if !showVideoLink?has_content>
      <#assign showVideoLink = "true"/>
  </#if>
  <#if !showCartLink?has_content>
      <#assign showCartLink = "true"/>
  </#if>
  <#if !showProductContentLink?has_content>
      <#assign showProductContentLink = "true"/>
  </#if>
  <#if !showProductAttributeLink?has_content>
      <#assign showProductAttributeLink = "true"/>
  </#if>
  
  <#if showDetailLink == 'true'>
    <#if product.isVirtual?if_exists == 'Y'>
      <a href="<@ofbizUrl>virtualProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ShowVirtualDetailTooltip}');" onMouseout="hideTooltip()"><span class="detailIcon"></span></a>
    <#elseif (product.productTypeId?if_exists == 'FINISHED_GOOD') && (product.isVirtual?if_exists == 'N') && (product.isVariant?if_exists == 'N')>
      <a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ShowFinishedDetailTooltip}');" onMouseout="hideTooltip()"><span class="detailIcon"></span></a>
    <#else>
      <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
      <a href="<@ofbizUrl>virtualProductDetail?productId=${virtualProduct.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ShowVirtualDetailTooltip}');" onMouseout="hideTooltip()"><span class="detailIcon"></span></a>
    </#if>
  </#if>
  
  <#if productContentWrapper?exists>
      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
  </#if>
  
  <#if showImageLink == 'true'>
    <a href="<@ofbizUrl>productImages?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'${uiLabelMap.ProductImagesTooltip}','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
  </#if>
  
  <#if showAttachLink == 'true'>
    <a href="<@ofbizUrl>productAttachments?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductAttachmentsTooltip}');" onMouseout="hideTooltip()"><span class="attachIcon"></span></a>
  </#if>
  
  <#if showProductFeatureLink == 'true'>
   <#if (product.isVariant?if_exists == 'N')>
     <a href="<@ofbizUrl>productFeatures?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductFeaturesTooltip}');" onMouseout="hideTooltip()"><span class="featureIcon"></span></a>
   <#else>
     <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
     <a href="<@ofbizUrl>productFeatures?productId=${virtualProduct.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductFeaturesTooltip}');" onMouseout="hideTooltip()"><span class="featureIcon"></span></a>
   </#if>
  </#if>
  
  <#assign productAssocs = product.getRelated("MainProductAssoc")!""/>
  <#if showVariantLink == 'true'>
    <#if (product.isVirtual?if_exists == 'Y') && (product.isVariant?if_exists == 'N')>
      <#assign prodVariants = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_VARIANT'))/>
      <#assign prodVariantCount = prodVariants.size()!0/>
      <a href="<@ofbizUrl>productVariants?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductVariantsTooltip} [${prodVariantCount!}]');" onMouseout="hideTooltip()"><span class="variantIcon"></span></a>
    <#elseif (product.isVariant?if_exists == 'Y')>
      <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
      <#assign productAssocs = virtualProduct.getRelated("MainProductAssoc")!""/>
      <#assign prodVariants = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_VARIANT'))/>
      <#assign prodVariantCount = prodVariants.size()!0/>
      <a href="<@ofbizUrl>productVariants?productId=${virtualProduct.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductVariantsTooltip} [${prodVariantCount!}]');" onMouseout="hideTooltip()"><span class="variantIcon"></span></a>
    <#else>
      <a href="javascript:void(0)" onMouseover="showTooltip(event,'${uiLabelMap.FinishedGoodNoVarsTooltip}');" onMouseout="hideTooltip()"><span class="variantIcon"></span></a>
    </#if>
  </#if>
  
  <#if showMetaTagLink == 'true'>
    <#if (product.isVariant?if_exists == 'N')>
      <a href="<@ofbizUrl>productMetatag?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
    <#else>
      <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
      <a href="<@ofbizUrl>productMetatag?productId=${virtualProduct.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
    </#if>
  </#if>
  
  <#if showPricingLink == 'true'>
    <a href="<@ofbizUrl>productPrice?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductPricingTooltip}');" onMouseout="hideTooltip()"><span class="priceIcon"></span></a>
  </#if>
  
  <#if showRelatedLink == 'true'>
    <#assign prodRelatedComplement = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_COMPLEMENT'))/>
    <#assign prodRelatedComplement = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(prodRelatedComplement)>
    <#assign prodRelatedComplementCount = prodRelatedComplement.size()!0/>
                   
    <#assign prodRelatedAccessory = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_ACCESSORY'))/>
    <#assign prodRelatedAccessory = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(prodRelatedAccessory)>
    <#assign prodRelatedAccessoryCount = prodRelatedAccessory.size()!0/>
    <#assign prodRelatedCount = prodRelatedComplementCount + prodRelatedAccessoryCount>
    <#if !(product.isVariant?if_exists == 'Y') >
        <a href="<@ofbizUrl>productAssociationDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductAssociationsTooltip} [${prodRelatedCount!}]');" onMouseout="hideTooltip()"><span class="relatedIcon"></span></a>
    <#else>
        <a href="javascript:void(0);javascript:alert('${uiLabelMap.VariantProductAssocError}');" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductAssociationsTooltip} [${prodRelatedCount!}]');" onMouseout="hideTooltip()"><span class="relatedIcon"></span></a>
    </#if>
  </#if>
  
  <#if showCategoryMemberLink == 'true'>
    <#if product.isVariant?if_exists == 'N'>
      <#assign categoryMembers = product.getRelated("ProductCategoryMember")!""/>
      <#assign categoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(categoryMembers)>
      <#assign prodCatMembershipCount = categoryMembers.size()!0/>
      <a href="<@ofbizUrl>productCategoryMembershipDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductcategoryMembershipTooltip} [${prodCatMembershipCount!}]');" onMouseout="hideTooltip()"><span class="membershipIcon"></span></a>
    <#else>
      <#assign virtualProduct = Static["org.ofbiz.product.product.ProductWorker"].getParentProduct(product.productId, delegator)?if_exists>
      <#assign categoryMembers = virtualProduct.getRelated("ProductCategoryMember")!""/>
      <#assign categoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(categoryMembers)>
      <#assign prodCatMembershipCount = categoryMembers.size()!0/>
      <a href="<@ofbizUrl>productCategoryMembershipDetail?productId=${virtualProduct.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductcategoryMembershipTooltip} [${prodCatMembershipCount!}]');" onMouseout="hideTooltip()"><span class="membershipIcon"></span></a>
    </#if>
  </#if>
  
  <#if showVideoLink == 'true'>
    <a href="<@ofbizUrl>productVideo?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductVideoTooltip}');" onMouseout="hideTooltip()"><span class="videoIcon"></span></a>
  </#if>
  <#if showCartLink == 'true'>
    <#if (product.productTypeId?if_exists == 'FINISHED_GOOD') && (product.isVirtual?if_exists == 'N')>
      <#assign inventoryMethod = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"INVENTORY_METHOD")!/>
      <#assign notIntroduced = false />
      <#if product.introductionDate?has_content>
        <#assign introDate = product.introductionDate! />
        <#assign introductionDate = (introDate)?string(entryDateTimeFormat)>
        <#if nowTimestamp.before(introDate)><#assign notIntroduced = true /></#if>
      </#if>
      <#assign hasExpired = false />
      <#if product.salesDiscontinuationDate?has_content>
        <#assign discDate = product.salesDiscontinuationDate! />
        <#assign salesDiscontinuationDate = (discDate)?string(entryDateTimeFormat)>
        <#if nowTimestamp.after(discDate)><#assign hasExpired = true /></#if>
      </#if>
      <#if notIntroduced == true || hasExpired == true>
        <#if notIntroduced?has_content && notIntroduced == true>
          <a href="javascript:void(0);javascript:alert('${uiLabelMap.ProductNotIntroducedError} ${introductionDate}');" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()" ><span class="adminAddCartIcon"></span></a>
        <#elseif hasExpired?has_content && hasExpired == true>
          <a href="javascript:void(0);javascript:alert('${uiLabelMap.ProductExpiredError} ${salesDiscontinuationDate}');" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()"><span class="adminAddCartIcon"></span></a>
        </#if>
      <#else>
        <!-- If inventory method is BigFish then check how many items are in stock -->
        <#if inventoryMethod?has_content && inventoryMethod.toUpperCase() == "BIGFISH" >
          <#assign inventoryOutOfStockTo = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"INVENTORY_OUT_OF_STOCK_TO")!/>
          <#assign bfProductAllAttributes = product.getRelated("ProductAttribute") />
          <#if bfProductAllAttributes?has_content>
            <#assign bfTotalInventoryProductAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','BF_INVENTORY_TOT'))/> 
            <#assign bfWHInventoryProductAttributes = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(bfProductAllAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap('attrName','BF_INVENTORY_WHS'))/>
          </#if>
          <#if bfTotalInventoryProductAttributes?has_content>
            <#assign bfTotalInventoryProductAttribute = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(bfTotalInventoryProductAttributes) />
            <#assign bfTotalInventory = bfTotalInventoryProductAttribute.attrValue!>
          </#if>
          <#if bfTotalInventory?has_content && inventoryOutOfStockTo?has_content && (bfTotalInventory?number > inventoryOutOfStockTo?number)>
            <#assign pdpMaxQty = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MAX")!"99"/>
            <#assign pdpMaxQtyNum = pdpMaxQty?number />
            <#assign qtyInCart = 0?number />
            <#if shoppingCart?has_content>
              <#list shoppingCart.items() as cartLine>
                  <#if cartLine.getProductId() == product.productId>
                      <#assign qtyInCart = cartLine.getQuantity() />
                  </#if>
              </#list>
            </#if>
            <#if ((qtyInCart+1) > pdpMaxQtyNum)>
              <a href="javascript:void(0);javascript:alert('${eCommerceUiLabel.PDPMaxQtyError}');" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()"><span class="adminAddCartIcon"></span></a>
            <#else>
              <a href="<@ofbizUrl>${addToCartAction}?productId=${product.productId?if_exists}&add_product_id=${product.productId?if_exists}&addToCartFrom=${addToCartFrom?if_exists}<#if (product.isVariant?if_exists == 'Y') >&prod_type=Variant<#elseif (product.isVariant?if_exists == 'N')>&prod_type=FinishedGood</#if></@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()"><span class="adminAddCartIcon"></span></a>
            </#if> 
          <#else>
            <a href="javascript:void(0);javascript:alert('${uiLabelMap.OutOfStockAddToCartError}');" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()"><span class="adminAddCartIcon"></span></a>
          </#if>
        <#else>
          <#assign pdpMaxQty = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MAX")!"99"/>
          <#assign pdpMaxQtyNum = pdpMaxQty?number />
          <#assign qtyInCart = 0?number />
          <#if shoppingCart?has_content>
              <#list shoppingCart.items() as cartLine>
                  <#if cartLine.getProductId() == product.productId>
                      <#assign qtyInCart = cartLine.getQuantity() />
                  </#if>
              </#list>
          </#if>
          <#if ((qtyInCart+1) > pdpMaxQtyNum)>
              <a href="javascript:void(0);javascript:alert('${eCommerceUiLabel.PDPMaxQtyError}');" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()"><span class="adminAddCartIcon"></span></a>
          <#else>
              <a href="<@ofbizUrl>${addToCartAction}?productId=${product.productId?if_exists}&add_product_id=${product.productId?if_exists}&addToCartFrom=${addToCartFrom?if_exists}<#if (product.isVariant?if_exists == 'Y') >&prod_type=Variant<#elseif (product.isVariant?if_exists == 'N')>&prod_type=FinishedGood</#if></@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()"><span class="adminAddCartIcon"></span></a>
          </#if>
        </#if>
      </#if>
    <#else>
      <a href="javascript:void(0);javascript:alert('${uiLabelMap.VirtualProductAddToCartError}');" onMouseover="showTooltip(event,'${uiLabelMap.AddToCartTooltip}');" onMouseout="hideTooltip()"><span class="adminAddCartIcon"></span></a>
    </#if>
  </#if>
  
  <#if showProductContentLink == 'true'>
    <a href="<@ofbizUrl>productContentSpotList?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductContentSpotTooltip}');" onMouseout="hideTooltip()"><span class="contentSpotIcon"></span></a>
  </#if>
  
  <#if showProductAttributeLink == 'true'>
    <a href="<@ofbizUrl>productAttributeDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductAttributeDetailTooltip}');" onMouseout="hideTooltip()"><span class="productAttributeIcon"></span></a>
  </#if>
</div>
</#if>

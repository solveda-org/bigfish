<!-- start listBox -->
  <thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
    <th class="nameCol">${uiLabelMap.ItemNoLabel}</th>
    <th class="nameCol">${uiLabelMap.NameLabel}</th>
    <th class="actionCol"></th>
    <th class="statusCol">${uiLabelMap.VirtualLabel}</th>
    <th class="statusCol">${uiLabelMap.VariantLabel}</th>
    <th class="dateCol">${uiLabelMap.IntroDateLabel}</th>
    <th class="dateCol">${uiLabelMap.DiscoDateLabel}</th>
    <th class="dollarCol">${uiLabelMap.ListPriceLabel}</th>
    <th class="dollarCol">${uiLabelMap.SalePriceLabel}</th>
    <th class="actionCol">${uiLabelMap.ActionsLabel}</th>
  </tr>
  </thead>
  <#if resultList?exists && resultList?has_content>
    <#assign rowClass = "1"/>
    <#list resultList as result>
      <#assign hasNext = result_has_next/>
      <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",result.productId), false)/>
      <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
      <#assign productInStoreOnlyAttribute = delegator.findOne("ProductAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("attrName" , "PDP_IN_STORE_ONLY", "productId" , result.productId!),false)?if_exists/>
      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !hasNext?if_exists>lastRow</#if> firstCol" >
          <#if product.isVirtual == 'Y'>
            <a href="<@ofbizUrl>virtualProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${product.productId?if_exists}</a>
          <#elseif product.isVirtual == 'N' && product.isVariant == 'N'>
            <a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${product.productId?if_exists}</a>
          </#if>
          <#if productInStoreOnlyAttribute?has_content && productInStoreOnlyAttribute.attrValue?upper_case == 'Y'>
              <#assign toolTipData = uiLabelMap.StoreOnlyProductInfo>
              <a onMouseover="showTooltip(event,'${toolTipData!}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></a>
          </#if>
        </td>
        <td class="nameCol <#if !hasNext?if_exists>lastRow</#if>">${product.internalName?if_exists}</td>
        <td class="nameCol">
          ${productContentWrapper.get("PRODUCT_NAME")!""}
        </td>
        <td class="actionCol">
        <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
        <#if productLongDescription?has_content && productLongDescription !="">
          <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
          <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
        </#if>
        </td>
        <td class="statusCol <#if !hasNext?if_exists>lastRow</#if>">${product.isVirtual!}</td>
        <td class="statusCol <#if !hasNext?if_exists>lastRow</#if>">${product.isVariant!}</td>
        <td class="dateCol <#if !hasNext?if_exists>lastRow</#if>">${(product.introductionDate?string(preferredDateFormat))!""}</td>
        <td class="dateCol <#if !hasNext?if_exists>lastRow</#if>">${(product.salesDiscontinuationDate?string(preferredDateFormat))!""}</td>
        <#assign productListPrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, product.productId, "LIST_PRICE")!/>
        <td class="dollarCol <#if !hasNext?if_exists>lastRow</#if>">
        <#if productListPrice?has_content>
          <@ofbizCurrency amount=productListPrice.price isoCode=productListPrice.currencyUomId rounding=globalContext.currencyRounding/>
        </#if>
        </td>
        <#assign productDefaultPrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, product.productId, "DEFAULT_PRICE")!/>
        <td class="dollarCol <#if !hasNext?if_exists>lastRow</#if>">
          <#if productDefaultPrice?has_content>
            <@ofbizCurrency amount=productDefaultPrice.price isoCode=productDefaultPrice.currencyUomId rounding=globalContext.currencyRounding/>
          </#if>
        </td>
        <td class="actionCol <#if !hasNext?if_exists>lastRow</#if> <#if !hasNext?if_exists>bottomActionIconRow</#if>">
          <div class="actionIconMenu">
            <a class="toolIcon" href="javascript:void(o);"></a>
            <div class="actionIconBox" style="display:none">
              <div class="actionIcon">
                <#if productLargeImageUrl?has_content>
                  <img class="actionIconMenuImage" src="<@ofbizContentUrl>${productLargeImageUrl}</@ofbizContentUrl>" alt="${productLargeImageUrl}"/>
                </#if>
                <ul>
                 <#if product.isVirtual?if_exists == 'Y'>
                   <li><a href="<@ofbizUrl>virtualProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="detailIcon"></span>${uiLabelMap.ShowVirtualDetailTooltip}</a></li>
                 <#elseif (product.productTypeId?if_exists == 'FINISHED_GOOD') && (product.isVirtual?if_exists == 'N') && (product.isVariant?if_exists == 'N')>
                   <li><a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="detailIcon"></span>${uiLabelMap.ShowFinishedDetailTooltip}</a></li>
                  </#if>
                  <li><a href="<@ofbizUrl>productImages?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="imageIcon"></span>${uiLabelMap.ProductImagesTooltip}</a></li>
                  <li><a href="<@ofbizUrl>productAttachments?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="attachIcon"></span>${uiLabelMap.ProductAttachmentsTooltip}</a></li>
                  <li><a href="<@ofbizUrl>productPrice?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="priceIcon"></span>${uiLabelMap.ProductPricingTooltip}</a></li>
                  <li><a href="<@ofbizUrl>productMetatag?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="metatagIcon"></span>${uiLabelMap.HtmlMetatagTooltip}</a></li>
                  <#if (product.isVariant?if_exists == 'N')>
                    <li><a href="<@ofbizUrl>productFeatures?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="featureIcon"></span>${uiLabelMap.ProductFeaturesTooltip}</a></li>
                  </#if>
                  <#assign productAssocs = product.getRelated("MainProductAssoc")!""/>
                  <#if (product.isVirtual?if_exists == 'Y')>
                    <#assign prodVariants = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_VARIANT'))/>
                    <#assign prodVariantCount = prodVariants.size()!0/>
                    <li><a href="<@ofbizUrl>productVariants?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="variantIcon"></span>${uiLabelMap.ProductVariantsTooltip} [${prodVariantCount!}]</a></li>
                  <#else>
			        <li><a href="javascript:void(0)"><span class="variantIcon"></span>${uiLabelMap.FinishedGoodNoVarsTooltip}</a></li>
			      </#if>
                  <#if (product.isVariant?if_exists == 'N')>
                    <#assign prodRelatedComplement = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_COMPLEMENT'))/>
                    <#assign prodRelatedComplement = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(prodRelatedComplement) />
                    <#assign prodRelatedComplementCount = prodRelatedComplement.size()!0/>
                    <#assign prodRelatedAccessory = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_ACCESSORY'))/>
                    <#assign prodRelatedAccessory = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(prodRelatedAccessory) />
                    <#assign prodRelatedAccessoryCount = prodRelatedAccessory.size()!0/>
                    <#assign prodRelatedCount = prodRelatedComplementCount + prodRelatedAccessoryCount />
                    <li><a href="<@ofbizUrl>productAssociationDetail?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="relatedIcon"></span>${uiLabelMap.ManageProductAssociationsTooltip} [${prodRelatedCount!}]</a></li>
                  </#if>
                  <#if (product.isVariant?if_exists == 'N')>
                    <#assign categoryMembers = product.getRelated("ProductCategoryMember")!""/>
                    <#assign categoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(categoryMembers) />
                    <#assign prodCatMembershipCount = categoryMembers.size()!0/>
                    <li><a href="<@ofbizUrl>productCategoryMembershipDetail?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="membershipIcon"></span>${uiLabelMap.ManageProductcategoryMembershipTooltip} [${prodCatMembershipCount!}]</a></li>
                  </#if>
                  <li><a href="<@ofbizUrl>productVideo?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="videoIcon"></span>${uiLabelMap.ManageProductVideoTooltip}</a></li>          
                  <#if (product.productTypeId?if_exists == 'FINISHED_GOOD') && (product.isVirtual?if_exists == 'N')>
                    <#assign inventoryMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"INVENTORY_METHOD")!/>
                    <#assign notIntroduced = false />
                    <#if product.introductionDate?has_content>
                      <#assign introDate = product.introductionDate! />
                      <#assign introductionDate = (introDate)?string(preferredDateFormat)>
                      <#if nowTimestamp.before(introDate)><#assign notIntroduced = true /></#if>
                    </#if>
                    <#assign hasExpired = false />
                    <#if product.salesDiscontinuationDate?has_content>
                      <#assign discDate = product.salesDiscontinuationDate! />
                      <#assign salesDiscontinuationDate = (discDate)?string(preferredDateFormat)>
                      <#if nowTimestamp.after(discDate)><#assign hasExpired = true /></#if>
                    </#if>
                    <#if notIntroduced == true || hasExpired == true>
                      <li>
                        <#if notIntroduced?has_content && notIntroduced == true>
                          <a href="javascript:void(0);javascript:alert('${uiLabelMap.ProductNotIntroducedError} ${introductionDate}');" ><span class="adminAddCartIcon"></span>${uiLabelMap.AddToCartTooltip}</a>
                        <#elseif hasExpired?has_content && hasExpired == true>
                          <a href="javascript:void(0);javascript:alert('${uiLabelMap.ProductExpiredError} ${salesDiscontinuationDate}');"><span class="adminAddCartIcon"></span>${uiLabelMap.AddToCartTooltip}</a>
                        </#if>
                      </li>
                      <!-- If inventory method is BigFish then check how many items are in stock -->
                    <#else>
                      <#if inventoryMethod?has_content && inventoryMethod.toUpperCase() == "BIGFISH" >
                        <#assign inventoryOutOfStockTo = Static["com.osafe.util.Util"].getProductStoreParm(request,"INVENTORY_OUT_OF_STOCK_TO")!/>
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
                          <li><a href="<@ofbizUrl>${addToCartAction}?add_product_id=${product.productId?if_exists}&addToCartFrom=${addToCartFrom?if_exists}<#if (product.isVariant?if_exists == 'Y') >&prod_type=Variant<#elseif (product.isVariant?if_exists == 'N')>&prod_type=FinishedGood</#if></@ofbizUrl>"><span class="adminAddCartIcon"></span>${uiLabelMap.AddToCartTooltip}</a></li>
                        <#else>
                          <li><a href="javascript:void(0);javascript:alert('${uiLabelMap.OutOfStockAddToCartError}');" ><span class="adminAddCartIcon"></span>${uiLabelMap.AddToCartTooltip}</a></li>
                        </#if>
                      <#else>
                        <li><a href="<@ofbizUrl>${addToCartAction}?add_product_id=${product.productId?if_exists}&addToCartFrom=${addToCartFrom?if_exists}<#if (product.isVariant?if_exists == 'Y') >&prod_type=Variant<#elseif (product.isVariant?if_exists == 'N')>&prod_type=FinishedGood</#if></@ofbizUrl>"><span class="adminAddCartIcon"></span>${uiLabelMap.AddToCartTooltip}</a></li>
                      </#if>
                    </#if>
                  <#else>
                    <li><a href="javascript:void(0);javascript:alert('${uiLabelMap.VirtualProductAddToCartError}');" ><span class="adminAddCartIcon"></span>${uiLabelMap.AddToCartTooltip}</a></li>
                  </#if>
                  <li><a href="<@ofbizUrl>${productContentSpotListAction}?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="contentSpotIcon"></span>${uiLabelMap.ProductContentSpotTooltip}</a></li>
                  <li><a href="<@ofbizUrl>productAttributeDetail?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="productAttributeIcon"></span>${uiLabelMap.ProductAttributeDetailTooltip}</a></li>    
                </ul>
              </div>
            </div>
          </div>
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1" />
      <#else>
        <#assign rowClass = "2" />
      </#if>
    </#list>
  </#if>
<!-- end listBox -->
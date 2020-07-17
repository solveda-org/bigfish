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
      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !hasNext?if_exists>lastRow</#if> firstCol" >
          <#if product.isVirtual == 'Y'>
            <a href="<@ofbizUrl>virtualProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${product.productId?if_exists}</a>
          <#elseif product.isVirtual == 'N' && product.isVariant == 'N'>
            <a href="<@ofbizUrl>finishedProductDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${product.productId?if_exists}</a>
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
          <@ofbizCurrency amount=productListPrice.price isoCode=productListPrice.currencyUomId />
        </#if>
        </td>
        <#assign productDefaultPrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, product.productId, "DEFAULT_PRICE")!/>
          <td class="dollarCol <#if !hasNext?if_exists>lastRow</#if>">
          <#if productDefaultPrice?has_content>
            <@ofbizCurrency amount=productDefaultPrice.price isoCode=productDefaultPrice.currencyUomId />
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
	           <li><a href="<@ofbizUrl>productImages?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="imageIcon"></span>${uiLabelMap.ProductImagesTooltip}</a></li>
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
	           </#if>
	           <#if (product.isVariant?if_exists == 'N')>
			       <#assign prodRelatedComplement = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_COMPLEMENT'))/>
			       <#assign prodRelatedComplement = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(prodRelatedComplement)>
                   <#assign prodRelatedComplementCount = prodRelatedComplement.size()!0/>
                   
                   <#assign prodRelatedAccessory = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAssocs,Static["org.ofbiz.base.util.UtilMisc"].toMap('productAssocTypeId','PRODUCT_ACCESSORY'))/>
			       <#assign prodRelatedAccessory = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(prodRelatedAccessory)>
                   <#assign prodRelatedAccessoryCount = prodRelatedAccessory.size()!0/>
                   <#assign prodRelatedCount = prodRelatedComplementCount + prodRelatedAccessoryCount>
         	        <li><a href="<@ofbizUrl>productAssociationDetail?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="relatedIcon"></span>${uiLabelMap.ManageProductAssociationsTooltip} [${prodRelatedCount!}]</a></li>
	           </#if>
	           <#if (product.isVariant?if_exists == 'N')>
                   <#assign categoryMembers = product.getRelated("ProductCategoryMember")!""/>
                   <#assign categoryMembers = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(categoryMembers)>
                   <#assign prodCatMembershipCount = categoryMembers.size()!0/>
	               <li><a href="<@ofbizUrl>productCategoryMembershipDetail?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="membershipIcon"></span>${uiLabelMap.ManageProductcategoryMembershipTooltip} [${prodCatMembershipCount!}]</a></li>
	           </#if>
	           <li><a href="<@ofbizUrl>productVideo?productId=${product.productId?if_exists}</@ofbizUrl>"><span class="videoIcon"></span>${uiLabelMap.ManageProductVideoTooltip}</a></li>
	           <#if (product.productTypeId?if_exists == 'FINISHED_GOOD') && (product.isVirtual?if_exists == 'N')>
	           		<li><a href="<@ofbizUrl>${addToCartAction}?add_product_id=${product.productId?if_exists}<#if (product.isVariant?if_exists == 'Y') >&prod_type=Variant<#elseif (product.isVariant?if_exists == 'N')>&prod_type=FinishedGood</#if></@ofbizUrl>"><span class="adminAddCartIcon"></span>${uiLabelMap.AddToCartTooltip}</a></li>
	           </#if>
	        </ul>
	       </div>
	       </div>
	      </div>
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
  </#if>
<!-- end listBox -->
<!-- start listBox -->
  <thead>
  <tr class="heading">
    <th class="idCol firstCol">${uiLabelMap.ProductNoLabel}</th>
    <th class="descCol">${uiLabelMap.ItemNoLabel}</th>
    <th class="descCol">${uiLabelMap.NameLabel}</th>
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
    <#--if numFound?if_exists gt pageSize>
      ${screens.render("component://osafe/widget/EcommerceScreens.xml#plpPagingControlsTop")}
    </#if-->
    <#list resultList as result>
      <#assign product = delegator.findOne("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",result.productId), true)/>
      <#assign hasNext = result_has_next>
      <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)!""/>
      <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")!"">
      <tr class="dataRow <#if rowClass?if_exists == "2">even<#else>odd</#if>">
        <td class="idCol <#if !result_has_next?if_exists>lastRow</#if> firstCol" ><a href="<@ofbizUrl>productDetail?productId=${product.productId?if_exists}</@ofbizUrl>">${product.productId?if_exists}</a></td>
        <td class="descCol <#if !result_has_next?if_exists>lastRow</#if>">${product.internalName?if_exists}</td>
        <td class="descCol">
          ${productContentWrapper.get("PRODUCT_NAME")?html!""}
        </td>
        <td class="actionCol">
        <#assign productLongDescription = productContentWrapper.get("LONG_DESCRIPTION")!""/>
        <#if productLongDescription?has_content && productLongDescription !="">
          <#assign productLongDescription = Static["com.osafe.util.OsafeAdminUtil"].formatToolTipText(productLongDescription, ADM_TOOLTIP_MAX_CHAR!)/>
          <a href="javascript:void(0);" onMouseover="javascript:showTooltip(event,'${productLongDescription!""}');" onMouseout="hideTooltip()"><span class="descIcon"></span></a>
        </#if>
        </td>
        <td class="statusCol <#if !result_has_next?if_exists>lastRow</#if>">${product.isVirtual!}</td>
        <td class="statusCol <#if !result_has_next?if_exists>lastRow</#if>">${product.isVariant!}</td>
        <td class="dateCol <#if !result_has_next?if_exists>lastRow</#if>">${(product.introductionDate?string(preferredDateFormat))!""}</td>
        <td class="dateCol <#if !result_has_next?if_exists>lastRow</#if>">${(product.salesDiscontinuationDate?string(preferredDateFormat))!""}</td>
        <#assign productListPrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, product.productId, "LIST_PRICE")!/>
        <td class="dollarCol">
        <#if productListPrice?has_content>
          <@ofbizCurrency amount=productListPrice.price isoCode=productListPrice.currencyUomId />
        </#if>
        </td>
        <#assign productDefaultPrice = Static["com.osafe.util.OsafeAdminUtil"].getProductPrice(request, product.productId, "DEFAULT_PRICE")!/>
          <td class="dollarCol">
          <#if productDefaultPrice?has_content>
            <@ofbizCurrency amount=productDefaultPrice.price isoCode=productDefaultPrice.currencyUomId />
          </#if>
          </td>
        <td class="actionCol">
           <a href="<@ofbizUrl>productImages?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="<#if productLargeImageUrl?has_content>showTooltipImage(event,'${uiLabelMap.ProductImagesTooltip}','${productLargeImageUrl}?${nowTimestamp!}');<#else>showTooltip(event,'${uiLabelMap.ProductImagesTooltip}');</#if>" onMouseout="hideTooltip()"><span class="imageIcon"></span></a>
           <a href="<@ofbizUrl>productPrice?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductPricingTooltip}');" onMouseout="hideTooltip()"><span class="priceIcon"></span></a>
           <a href="<@ofbizUrl>productMetatag?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.HtmlMetatagTooltip}');" onMouseout="hideTooltip()"><span class="metatagIcon"></span></a>
           <#if (product.isVariant?if_exists == 'N')>
             <#assign features = product.getRelated("ProductFeatureAppl")/>
             <#if features?exists && features?has_content>
               <a href="<@ofbizUrl>productFeatures?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductFeaturesTooltip}');" onMouseout="hideTooltip()"><span class="featureIcon"></span></a>
             <#else>
               <span class="noAction"></span>
             </#if>
           <#else>
               <span class="noAction"></span>
           </#if>
           <#if (product.isVirtual?if_exists == 'Y') && (product.isVariant?if_exists == 'N')>
             <#assign variants = delegator.findByAnd("ProductAssoc", {"productId" : product.productId, "productAssocTypeId" : "PRODUCT_VARIANT"})/>
             <#if variants?exists && variants?has_content>
               <a href="<@ofbizUrl>productVariants?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ProductVariantsTooltip}');" onMouseout="hideTooltip()"><span class="variantIcon"></span></a>
             <#else>
               <span class="noAction"></span>
             </#if>
           <#else>
               <span class="noAction"></span>
           </#if>
           <a href="<@ofbizUrl>relatedProductsDetail?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageRelatedProductsTooltip}');" onMouseout="hideTooltip()"><span class="relatedIcon"></span></a>
           <a href="<@ofbizUrl>productVideo?productId=${product.productId?if_exists}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ManageProductVideoTooltip}');" onMouseout="hideTooltip()"><span class="videoIcon"></span></a>
        </td>
      </tr>
      <#if rowClass == "2">
        <#assign rowClass = "1">
      <#else>
        <#assign rowClass = "2">
      </#if>
    </#list>
    <#--if numFound?if_exists gt pageSize>
      ${screens.render("component://osafe/widget/EcommerceScreens.xml#plpPagingControlsTop")}
    </#if-->
  </#if>
<!-- end listBox -->
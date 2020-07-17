<#if productSelectableFeatureAndAppl?has_content>
 <#assign plpSwatchImageHeight = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_SWATCH_H")!""/>
 <#assign plpSwatchImageWidth = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_SWATCH_W")!""/>
 <#assign IMG_SIZE_PLP_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_H")!""/>
 <#assign IMG_SIZE_PLP_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_W")!""/>
 <#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
 <#assign PRODUCT_PCT_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_PCT_THRESHOLD")!"0"/>
 <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<div class="plpSwatchImage">
  <div class="swatch">
    <#list productSelectableFeatureAndAppl as productFeatureAppls>
      <#assign productFeatureId=productFeatureAppls.productFeatureId/>
      <#assign productFeatureTypeId=productFeatureAppls.productFeatureTypeId/>
      <#assign productFeatureDescription=productFeatureAppls.description!""/>

      <#assign productFeatureVariantId=""/>
      <#list productVariantFeatureList as productVariantFeatureListInfo>
        <#if productVariantFeatureListInfo.productFeatureId==productFeatureId && !productFeatureVariantId?has_content>
          <#assign productFeatureVariantId=productVariantFeatureListInfo.productVariantId/>
          <#assign productFeatureVariantProduct=productVariantFeatureListInfo.productVariant/>
          <#assign descriptiveFeatureGroupDesc = productVariantFeatureListInfo.descriptiveFeatureGroupDesc />
          <#assign variantListPrice = productVariantFeatureListInfo.listPrice!""/>
          <#assign variantOnlinePrice = productVariantFeatureListInfo.basePrice!""/>
        </#if>
      </#list>
      
      <#if productFeatureVariantId?has_content>

        <#assign variantProductUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request, StringUtil.wrapString(pdpUrl) + "&productFeatureType=${productFeatureTypeId!}:${productFeatureDescription!}") />
        <input type = "hidden" id="${productId}${productFeatureTypeId!}:${productFeatureDescription!}" value="${variantProductUrl!}"/>
        <input type = "hidden" class="featureGroup" value="${descriptiveFeatureGroupDesc!}"/>
        
        <#assign productVariantContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(productFeatureVariantProduct, request)!""/>
        <#assign productVariantSmallURL = productVariantContentWrapper.get("SMALL_IMAGE_URL")!"">
        <#assign productVariantSmallAltURL = productVariantContentWrapper.get("SMALL_IMAGE_ALT_URL")!"">
        <#assign productVariantPlpSwatchURL = productVariantContentWrapper.get("PLP_SWATCH_IMAGE_URL")!"">
        <#if productVariantPlpSwatchURL?has_content>
          <img src="<@ofbizContentUrl>${productVariantPlpSwatchURL}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
        <#else>
	      <#assign productFeatureDataResources = delegator.findByAndCache("ProductFeatureDataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId",productFeatureId,"featureDataResourceTypeId","PLP_SWATCH_IMAGE_URL"))/>
	      <#if productFeatureDataResources?has_content>
            <#assign productFeatureDataResource = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productFeatureDataResources) />
            <#assign dataResource = productFeatureDataResource.getRelatedOneCache("DataResource")/>
            <#assign productFeatureUrl = dataResource.objectInfo!""/>
	      </#if>
          <#if productFeatureUrl?has_content>
            <img src="<@ofbizContentUrl>${productFeatureUrl}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
          </#if>
        </#if>
        <div class="swatchVariant" style="display:none">
          <a class="pdpUrl" title="${productName}" href="${productFriendlyUrl}">
            <img alt="${productName}" title="${productName}" src="${productVariantSmallURL}" class="productThumbnailImage" <#if IMG_SIZE_PLP_H?has_content> height="${thumbImageHeight!""}"</#if> <#if IMG_SIZE_PLP_W?has_content> width="${thumbImageWidth!""}"</#if> <#if productVariantSmallAltURL?string?has_content>onmouseover="src='${productVariantSmallAltURL}'"</#if> onmouseout="src='${productVariantSmallURL}'" onerror="onImgError(this, 'PLP-Thumb');"/>
          </a>
        </div>

        <div class="swatchVariantOnlinePrice" style="display:none">
          <p class="price">${uiLabelMap.PlpPriceLabel} <@ofbizCurrency amount=variantOnlinePrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
        </div>
        
        <div class="swatchVariantListPrice" style="display:none">
          <#if variantListPrice?has_content && variantListPrice gt variantOnlinePrice>
            <p class="price">${uiLabelMap.PlpListPriceLabel} <@ofbizCurrency amount=variantListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
          </#if>
        </div>
        
        <div class="swatchVariantSaveMoney" style="display:none">
          <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
          <#if variantListPrice?has_content && variantOnlinePrice?has_content>
            <#assign youSaveMoney = (variantListPrice - variantOnlinePrice)/>
            <#if (youSaveMoney?has_content) && (youSaveMoney gt showSavingMoneyAbove?number)>  
              <p class="price">${uiLabelMap.YouSaveCaption}<@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" /></p>
            </#if>
          </#if>
        </div>
        
        <div class="swatchVariantSavingPercent" style="display:none">
          <#if variantListPrice?has_content && variantListPrice != 0>
            <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
            <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
            <#assign youSavePercent = ((variantListPrice - variantOnlinePrice)/variantListPrice) />
            <#if youSavePercent gt showSavingPercentAbove?number>  
              <p class="price">${uiLabelMap.YouSaveCaption}${youSavePercent?string("#0%")}</p>
            </#if>
          </#if>
        </div>
      </#if>
    </#list>
  </div>
</div>
</#if>
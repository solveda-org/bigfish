<#if productSelectableFeatureAndAppl?has_content && productSelectableFeatureAndAppl.size() gt 1>
<div class="plpSwatchImage">
  <div class="swatch">
    <#list productSelectableFeatureAndAppl as productFeatureAppls>
      <#assign plpSwatchImageHeight= IMG_SIZE_PLP_SWATCH_H!""/>
      <#assign plpSwatchImageWidth= IMG_SIZE_PLP_SWATCH_W!""/>
      <#assign productFeatureId=productFeatureAppls.productFeatureId/>
      <#assign productFeatureTypeId=productFeatureAppls.productFeatureTypeId/>
      <#assign productFeatureDescription=productFeatureAppls.description!""/>
      <#assign productFeatureDataResources = delegator.findByAnd("ProductFeatureDataResource", Static["org.ofbiz.base.util.UtilMisc"].toMap("productFeatureId",productFeatureId,"prodFeatureDataResourceTypeId","PLP_SWATCH_IMAGE_URL"))/>
      <#if productFeatureDataResources?has_content>
        <#list productFeatureDataResources as productFeatureDataResource>
          <#assign dataResource = productFeatureDataResource.getRelatedOne("DataResource")/>
          <#assign productFeatureUrl = dataResource.objectInfo!""/>
        </#list>
      </#if>

      <#assign productFeatureVariantId=""/>
      <#list productVariantFeatureList as productVariantFeatureListInfo>
        <#if productVariantFeatureListInfo.productFeatureId==productFeatureId && !productFeatureVariantId?has_content>
          <#assign productFeatureVariantId=productVariantFeatureListInfo.productVariantId/>
          <#assign productFeatureVariantProduct=productVariantFeatureListInfo.productVariant/>
        </#if>
      </#list>
      <#if productFeatureVariantId?has_content>
        
        <#if PLP_FACET_GROUP_VARIANT_MATCH?has_content>
          <#assign descriptiveProductFeatureAndAppls = delegator.findByAnd("ProductFeatureAndAppl", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId" ,productFeatureVariantId, 'productFeatureTypeId', PLP_FACET_GROUP_VARIANT_MATCH, 'productFeatureApplTypeId','DISTINGUISHING_FEAT')) />
		  <#assign descriptiveProductFeatureAndAppls = Static["org.ofbiz.entity.util.EntityUtil"].filterByDate(descriptiveProductFeatureAndAppls?if_exists)/>
		  <#if descriptiveProductFeatureAndAppls?has_content>
		    <#assign descriptiveProductFeatureAndAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(descriptiveProductFeatureAndAppls?if_exists)/>
		    <#assign descriptiveFeatureGroupDesc = descriptiveProductFeatureAndAppl.description! />
		  </#if>
        </#if>
        
        <#assign variantProductUrl = Static["com.osafe.services.CatalogUrlServlet"].makeCatalogFriendlyUrl(request, StringUtil.wrapString(pdpUrl) + "&productFeatureType=${productFeatureTypeId!}:${productFeatureDescription!}") />
        <input type = "hidden" id="${productId}${productFeatureTypeId!}:${productFeatureDescription!}" value="${variantProductUrl!}"/>
        <input type = "hidden" class="featureGroup" value="${descriptiveFeatureGroupDesc!}"/>
        
        <#assign productVariantContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(productFeatureVariantProduct, request)!""/>
        <#assign productVariantSmallURL = productVariantContentWrapper.get("SMALL_IMAGE_URL")!"">
        <#assign productVariantSmallAltURL = productVariantContentWrapper.get("SMALL_IMAGE_ALT_URL")!"">
        <#assign productVariantPlpSwatchURL = productVariantContentWrapper.get("PLP_SWATCH_IMAGE_URL")!"">
        <#if (productVariantPlpSwatchURL?string?has_content)>
          <img src="<@ofbizContentUrl>${productVariantPlpSwatchURL}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
        <#else>
          <#if productFeatureUrl?has_content>
            <img src="<@ofbizContentUrl>${productFeatureUrl}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
          </#if>
        </#if>
        <div class="swatchVariant" style="display:none">
          <a class="pdpUrl" title="${productName}" href="${productFriendlyUrl}">
            <img alt="${productName}" title="${productName}" src="${productVariantSmallURL}" class="productThumbnailImage" <#if IMG_SIZE_PLP_H?has_content> height="${thumbImageHeight!""}"</#if> <#if IMG_SIZE_PLP_W?has_content> width="${thumbImageWidth!""}"</#if> <#if productVariantSmallAltURL?string?has_content>onmouseover="src='${productVariantSmallAltURL}'"</#if> onmouseout="src='${productVariantSmallURL}'" onerror="onImgError(this, 'PLP-Thumb');"/>
          </a>
        </div>
      <#else>
        <#if productFeatureUrl?has_content>
          <img src="<@ofbizContentUrl>${productFeatureUrl}</@ofbizContentUrl>" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if>" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureId!""}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
        </#if>
      </#if>
    </#list>
  </div>
</div>
</#if>
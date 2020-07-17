
<#if productFeatureAndAppl?has_content && productFeatureAndAppl.size() gt 1>
<div class="plpSwatchImage">
<input type="hidden" id="pdpUrl_${productId!}" value="${productUrl}"/>
 <div class="swatch">
      <#list productFeatureAndAppl as productFeatureAppls>
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
           <#assign productVariantContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(productFeatureVariantProduct, request)!""/>
           <#assign productVariantSmallURL = productVariantContentWrapper.get("SMALL_IMAGE_URL")!"">
           <#assign productVariantSmallAltURL = productVariantContentWrapper.get("SMALL_IMAGE_ALT_URL")!"">
           <#assign productVariantPlpSwatchURL = productVariantContentWrapper.get("PLP_SWATCH_IMAGE_URL")!"">
           <#if (productVariantPlpSwatchURL?string?has_content)>
             <img src="<@ofbizContentUrl>${productVariantPlpSwatchURL}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
           <#else>
             <#if productFeatureUrl?has_content>
               <img src="<@ofbizContentUrl>${productFeatureUrl}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${productId!}" class="plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight != '0' && plpSwatchImageHeight != ''>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth != '0' && plpSwatchImageWidth != ''>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
             </#if>
           </#if>
           <div class="swatchVariant" style="display:none">
               <a title="${productName}" href="${productUrl}">
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
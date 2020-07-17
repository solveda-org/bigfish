<div id="pdpAlternateImage">
<div id="eCommerceProductAddImage">
  <#if !maxAltImages?exists>
   <#assign maxAltImages=10/>
  </#if>
  <#assign maxAltImages = maxAltImages?number/>
  <#list 1..maxAltImages as altImgNum>
    <#assign productAddImageUrl = context.get("productAddImageUrl${altImgNum}")!""/>
    <#if (productAddImageUrl?string?has_content)>
      <#assign altThumbImageExist = 'true'/>
      <#break>
    </#if>
  </#list>
  <#if altThumbImageExist?exists && altThumbImageExist =='true'>
  <ul id="altImageThumbnails">
  <#if (productThumbImageUrl?exists && productThumbImageUrl?string?has_content) && (altThumbImageExist?exists && altThumbImageExist =='true')>
    <li><a href="javascript:void(0);" id="mainAddImageLink" onclick="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><img src="<@ofbizContentUrl><#if productThumbImageUrl != ''>${productThumbImageUrl}<#else>${productLargeImageUrl!}</#if></@ofbizContentUrl>" id="mainAddImage" name="mainAddImage" id="mainAddImage" vspace="5" hspace="5" alt="" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
  </#if>
  <#list 1..maxAltImages as altImgNum>
    <#assign productAddImageUrl = context.get("productAddImageUrl${altImgNum}")!""/>
    <#assign productXtraAddLargeImageUrl = context.get("productXtraAddLargeImageUrl${altImgNum}")!""/>
    <#assign productXtraAddDetailImageUrl = context.get("productXtraAddImageUrl${altImgNum}")!""/>
    <#if productAddImageUrl?string?has_content>
      <li><a href="javascript:void(0);" id="addImage${altImgNum}Link" onclick="javascript:replaceDetailImage('<#if productXtraAddLargeImageUrl!=''>${productXtraAddLargeImageUrl}<#else>${productAddImageUrl}</#if>','${productXtraAddDetailImageUrl!}');"><img src="<@ofbizContentUrl>${productAddImageUrl?if_exists}</@ofbizContentUrl>" name="addImage${altImgNum}" id="addImage${altImgNum}" vspace="5" hspace="5" alt="" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
    </#if>
  </#list>
  </ul>
  </#if>
</div>
</div>
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantMap.get('${key}')/>
    <#assign productLargeImageUrl = variantProdCtntWrapper.get("LARGE_IMAGE_URL")!""/>
    <#assign productDetailImageUrl = variantProdCtntWrapper.get("DETAIL_IMAGE_URL")!""/>
    <#assign productThumbImageUrl = variantProdCtntWrapper.get("THUMBNAIL_IMAGE_URL")!""/>
    
    <#if productLargeImageUrl?has_content && productLargeImageUrl!=''>
    <div id="altImage_${key}" style="display:none">
      <#assign altThumbImageExist = ''/>
      <#list 1..maxAltImages as altImgNum>
        <#assign productAddImageUrl = variantProdCtntWrapper.get("ADDITIONAL_IMAGE_${altImgNum}")!"">
        <#if (productAddImageUrl?string?has_content)>
          <#assign altThumbImageExist = 'true'/>
          <#break>
        </#if>
      </#list>
      <#if altThumbImageExist?exists && altThumbImageExist =='true'>
        <ul>
      <#if (productThumbImageUrl?string?has_content) && (altThumbImageExist?exists && altThumbImageExist =='true')>
      <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
        <li><a href="javascript:void(0);" id="mainAddImageLink" onclick="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><img src="<@ofbizContentUrl><#if productThumbImageUrl != ''>${productThumbImageUrl}<#else>${productLargeImageUrl!}</#if></@ofbizContentUrl>" id="mainAddImage" name="mainAddImage" id="mainAddImage" vspace="5" hspace="5" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
      </#if>
      <#list 1..maxAltImages as altImgNum>
        <#assign productAddImageUrl = variantProdCtntWrapper.get("ADDITIONAL_IMAGE_${altImgNum}")!"">
        <#assign productXtraAddLargeImageUrl = variantProdCtntWrapper.get("XTRA_IMG_${altImgNum}_LARGE")!"">
        <#assign productXtraAddDetailImageUrl = variantProdCtntWrapper.get("XTRA_IMG_${altImgNum}_DETAIL")!"">
        <#if productAddImageUrl?string?has_content>
        <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
          <li><a href="javascript:void(0);" id='addImage${altImgNum}Link' onclick="javascript:replaceDetailImage('<#if productXtraAddLargeImageUrl!=''>${productXtraAddLargeImageUrl}<#else>${productAddImageUrl}</#if>','${productXtraAddDetailImageUrl!}');"><img src="<@ofbizContentUrl>${productAddImageUrl?if_exists}</@ofbizContentUrl>" name="addImage${altImgNum}" id="addImage${altImgNum}" vspace="5" hspace="5" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
        </#if>
      </#list>
      </ul>
      </#if>
    </div>
    </#if>  
  </#list>
</#if>

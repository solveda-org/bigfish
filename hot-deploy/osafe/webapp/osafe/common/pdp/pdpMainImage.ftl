<div class="pdpMainImage">
<#assign activeZoom = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_IMG_ZOOM_ACTIVE_FLAG") />
<#assign IMG_SIZE_PDP_REG_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_REG_H")!""/>
<#assign IMG_SIZE_PDP_REG_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_REG_W")!""/>
<div id="productDetailsImageContainer">
  <#if activeZoom><a href="<@ofbizContentUrl>${productDetailImageUrl!}</@ofbizContentUrl>" <#if productDetailImageUrl?has_content> class="innerZoom"</#if>></#if>
  <img src="<@ofbizContentUrl>${productLargeImageUrl!}</@ofbizContentUrl>" id="mainImage" name="mainImage" class="productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if> onerror="onImgError(this, 'PDP-Large');"/>
  <#if activeZoom></a></#if>
</div>
</div>
<div id="mainImageDiv" style="display:none">
  <#if activeZoom><a href="<@ofbizContentUrl>${productDetailImageUrl!}</@ofbizContentUrl>"></#if>
  <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
  <img src="<@ofbizContentUrl>${productLargeImageUrl!}</@ofbizContentUrl>" name="mainImage" class="productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if> onerror="onImgError(this, 'PDP-Large');"/>
  <#if activeZoom></a></#if>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantMap.get('${key}')/>
    <#assign productLargeImageUrl = variantProdCtntWrapper.get("LARGE_IMAGE_URL")!""/>
    <#assign productDetailImageUrl = variantProdCtntWrapper.get("DETAIL_IMAGE_URL")!""/>
    <#assign productThumbImageUrl = variantProdCtntWrapper.get("THUMBNAIL_IMAGE_URL")!""/>
    <#if productLargeImageUrl?has_content && productLargeImageUrl!=''>
    <div id="mainImage_${key}" style="display:none">
      <#if activeZoom && productDetailImageUrl?has_content && productDetailImageUrl !=''><a href="<@ofbizContentUrl>${productDetailImageUrl!}</@ofbizContentUrl>"></#if>
      <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
      <img src="<@ofbizContentUrl>${productLargeImageUrl!}</@ofbizContentUrl>" name="mainImage" class="productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if> onerror="onImgError(this, 'PDP-Large');"/>
      <#if activeZoom && productDetailImageUrl?has_content && productDetailImageUrl !=''></a></#if>
    </div>
    </#if>  
  </#list>
</#if>

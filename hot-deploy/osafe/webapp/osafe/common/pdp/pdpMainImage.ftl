<li class="${request.getAttribute("attributeClass")!}">
  <div class="pdpMainImage">
    <#assign activeZoom = Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"PDP_IMG_ZOOM_ACTIVE_FLAG") />
    <div id="js_productDetailsImageContainer">
      <#if activeZoom && productDetailImageUrl?has_content>
        <a href="<@ofbizContentUrl>${productDetailImageUrl!}</@ofbizContentUrl>" <#if productDetailImageUrl?has_content> class="innerZoom"</#if>>
      </#if>
      <img src="<@ofbizContentUrl>${productLargeImageUrl!}</@ofbizContentUrl>" id="js_mainImage" name="mainImage" class="js_productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if> onerror="onImgError(this, 'PDP-Large');"/>
      <#if activeZoom && productDetailImageUrl?has_content>
        </a>
      </#if>
    </div>
  </div>
  <div id="js_mainImageDiv" style="display:none">
    <#if activeZoom && productDetailImageUrl?has_content>
      <a href="<@ofbizContentUrl>${productDetailImageUrl!}</@ofbizContentUrl>">
    </#if>
    <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
    <img src="<@ofbizContentUrl>${productLargeImageUrl!}</@ofbizContentUrl>" name="mainImage" class="js_productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if> onerror="onImgError(this, 'PDP-Large');"/>
    <#if activeZoom && productDetailImageUrl?has_content>
      </a>
    </#if>
  </div>


	<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
	  <#list productVariantMapKeys as key>
	    <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')!/>
	    <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
	    <#assign productLargeImageUrl = context.get("productLargeImageUrl")!""/>
	    <#assign productDetailImageUrl = context.get("productDetailImageUrl")!""/>
	    <#assign productThumbImageUrl = context.get("productThumbImageUrl")!""/>
	    <#if variantContentIdMap?has_content && variantProdCtntWrapper?has_content >
	    	<#assign variantContentId = variantContentIdMap.get("LARGE_IMAGE_URL")!""/>
	        <#if variantContentId?has_content>
	           <#assign productLargeImageUrl = variantProdCtntWrapper.get("LARGE_IMAGE_URL")!""/>
	        </#if>
	    	<#assign variantContentId = variantContentIdMap.get("DETAIL_IMAGE_URL")!""/>
	        <#if variantContentId?has_content>
	           <#assign productDetailImageUrl = variantProdCtntWrapper.get("DETAIL_IMAGE_URL")!""/>
	        </#if>
	    	<#assign variantContentId = variantContentIdMap.get("THUMBNAIL_IMAGE_URL")!""/>
	        <#if variantContentId?has_content>
	           <#assign productThumbImageUrl = variantProdCtntWrapper.get("THUMBNAIL_IMAGE_URL")!""/>
	        </#if>
	    </#if>
	    <#if productLargeImageUrl?has_content && productLargeImageUrl!=''>
	    <div id="js_mainImage_${key}" style="display:none">
	      <#if activeZoom && productDetailImageUrl?has_content && productDetailImageUrl !=''><a href="<@ofbizContentUrl>${productDetailImageUrl!}</@ofbizContentUrl>"></#if>
	      <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
	      <img src="<@ofbizContentUrl>${productLargeImageUrl!}</@ofbizContentUrl>" name="mainImage" class="js_productLargeImage<#if !IMG_SIZE_PDP_REG_W?has_content> productLargeImageDefaultWidth</#if>" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if> onerror="onImgError(this, 'PDP-Large');"/>
	      <#if activeZoom && productDetailImageUrl?has_content && productDetailImageUrl !=''></a></#if>
	    </div>
	    </#if>  
	  </#list>
	</#if>
</li>

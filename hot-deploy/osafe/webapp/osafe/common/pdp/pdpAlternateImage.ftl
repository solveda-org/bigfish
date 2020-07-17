<script language="JavaScript" type="text/javascript">

jQuery(document).ready(function(){
    activateScroller();
    }); 

</script>
<li class="${request.getAttribute("attributeClass")!}">
  <div class="pdpAlternateImage">
    <div id="js_eCommerceProductAddImage">
      <#assign IMG_SIZE_PDP_THUMB_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_THUMB_H")!""/>
      <#assign IMG_SIZE_PDP_THUMB_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_THUMB_W")!""/>
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
	  <ul id="js_altImageThumbnails">
		  <#if (productThumbImageUrl?exists && productThumbImageUrl?string?has_content)>
		   <li><a href="javascript:void(0);" id="mainAddImageLink" onclick="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><img src="<@ofbizContentUrl><#if productThumbImageUrl != ''>${productThumbImageUrl}<#else>${productLargeImageUrl!}</#if></@ofbizContentUrl>" id="mainAddImage" name="mainAddImage" id="mainAddImage" vspace="5" hspace="5" alt="" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
		  </#if>
	      <#if altThumbImageExist?exists && altThumbImageExist =='true'>
			  <#list 1..maxAltImages as altImgNum>
			    <#assign productAddImageUrl = context.get("productAddImageUrl${altImgNum}")!""/>
			    <#assign productXtraAddLargeImageUrl = context.get("productXtraAddLargeImageUrl${altImgNum}")!""/>
			    <#assign productXtraAddDetailImageUrl = context.get("productXtraAddImageUrl${altImgNum}")!""/>
			    <#if productAddImageUrl?string?has_content>
			      <li><a href="javascript:void(0);" id="addImage${altImgNum}Link" onclick="javascript:replaceDetailImage('<#if productXtraAddLargeImageUrl!=''>${productXtraAddLargeImageUrl}<#else>${productAddImageUrl}</#if>','${productXtraAddDetailImageUrl!}');"><img src="<@ofbizContentUrl>${productAddImageUrl?if_exists}</@ofbizContentUrl>" name="addImage${altImgNum}" id="addImage${altImgNum}" vspace="5" hspace="5" alt="" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
			    </#if>
			  </#list>
	      </#if>
	  </ul>
    </div>
  </div>


 <#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')!""/>
    <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
    <#assign productLargeImageUrl = context.get("productLargeImageUrl")!""/>
    <#assign productDetailImageUrl = context.get("productDetailImageUrl")!""/>
    <#assign productThumbImageUrl = context.get("productThumbImageUrl")!""/>
    
    <#if variantContentIdMap?has_content>
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
    <div id="js_altImage_${key}" style="display:none">
      <#assign altThumbImageExist = 'false'/>
      <#assign altThumbImageExistVariant = 'false'/>
      <#list 1..maxAltImages as altImgNum>
       <#if variantContentIdMap?has_content>
	    	<#assign variantContentId = variantContentIdMap.get("ADDITIONAL_IMAGE_${altImgNum}")!""/>
            <#if variantContentId?has_content>
		        <#assign productAddImageUrl = variantProdCtntWrapper.get("ADDITIONAL_IMAGE_${altImgNum}")!"">
		        <#if (productAddImageUrl?string?has_content)>
		          <#assign altThumbImageExist = 'true'/>
		          <#assign altThumbImageExistVariant = 'true'/>
		          <#break>
		        </#if>
		    </#if>
	   </#if>
      </#list>
      <#list 1..maxAltImages as altImgNum>
      <#if altThumbImageExist =='false'>
        <#assign productAddImageUrl = context.get("productAddImageUrl${altImgNum}")!""/>
        <#if (productAddImageUrl?string?has_content)>
           <#assign altThumbImageExist = 'true'/>
           <#break>
        </#if>
      </#if>
      </#list>
        <ul>
      <#if (productThumbImageUrl?string?has_content)>
      <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
        <li><a href="javascript:void(0);" id="mainAddImageLink" onclick="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><img src="<@ofbizContentUrl><#if productThumbImageUrl != ''>${productThumbImageUrl}<#else>${productLargeImageUrl!}</#if></@ofbizContentUrl>" id="mainAddImage" name="mainAddImage" id="mainAddImage" vspace="5" hspace="5" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
      </#if>
        <#if altThumbImageExist?exists && altThumbImageExist =='true'>
		      <#list 1..maxAltImages as altImgNum>
		         <#assign productAddImageUrl = context.get("productAddImageUrl${altImgNum}")!""/>
		         <#assign productXtraAddLargeImageUrl = context.get("productXtraAddLargeImageUrl${altImgNum}")!""/>
		         <#assign productXtraAddDetailImageUrl = context.get("productXtraAddImageUrl${altImgNum}")!""/>
		         <#if variantContentIdMap?has_content>
			    	<#assign variantContentId = variantContentIdMap.get("ADDITIONAL_IMAGE_${altImgNum}")!""/>
			        <#if variantContentId?has_content>
			          <#assign productAddImageUrl = variantProdCtntWrapper.get("ADDITIONAL_IMAGE_${altImgNum}")!"">
			        </#if>
			    	<#assign variantContentId = variantContentIdMap.get("XTRA_IMG_${altImgNum}_LARGE")!""/>
			        <#if variantContentId?has_content>
			          <#assign productXtraAddLargeImageUrl = variantProdCtntWrapper.get("XTRA_IMG_${altImgNum}_LARGE")!"">
			        </#if>
			    	<#assign variantContentId = variantContentIdMap.get("XTRA_IMG_${altImgNum}_DETAIL")!""/>
			        <#if variantContentId?has_content>
			            <#assign productXtraAddDetailImageUrl = variantProdCtntWrapper.get("XTRA_IMG_${altImgNum}_DETAIL")!"">
			        </#if>
			     </#if>
		        <#if altThumbImageExistVariant == 'false'>
			        <#if productAddImageUrl==''>
			          <#assign productAddImageUrl = context.get("productAddImageUrl${altImgNum}")!""/>
			        </#if>
			        <#if productXtraAddLargeImageUrl==''>
			          <#assign productXtraAddLargeImageUrl = context.get("productXtraAddLargeImageUrl${altImgNum}")!""/>
			        </#if>
			        <#if productXtraAddDetailImageUrl==''>
			          <#assign productXtraAddDetailImageUrl = context.get("productXtraAddImageUrl${altImgNum}")!""/>
			        </#if>
		        </#if>
		        <#if productAddImageUrl?string?has_content>
		        <#-- image path added in alt attribute, on user action it will set in image src, in this way it will not effect page loading time -->
		          <li><a href="javascript:void(0);" id='addImage${altImgNum}Link' onclick="javascript:replaceDetailImage('<#if productXtraAddLargeImageUrl!=''>${productXtraAddLargeImageUrl}<#else>${productAddImageUrl}</#if>','${productXtraAddDetailImageUrl!}');"><img src="<@ofbizContentUrl>${productAddImageUrl?if_exists}</@ofbizContentUrl>" name="addImage${altImgNum}" id="addImage${altImgNum}" vspace="5" hspace="5" class="productAdditionalImage" <#if IMG_SIZE_PDP_THUMB_H?has_content> height="${IMG_SIZE_PDP_THUMB_H!""}"</#if> <#if IMG_SIZE_PDP_THUMB_W?has_content> width="${IMG_SIZE_PDP_THUMB_W!""}"</#if> onerror="onImgError(this, 'PDP-Alt');"/></a></li>
		        </#if>
		      </#list>
          </#if>
      </ul>
    </div>
    </#if>  
  </#list>
 </#if>
</li>

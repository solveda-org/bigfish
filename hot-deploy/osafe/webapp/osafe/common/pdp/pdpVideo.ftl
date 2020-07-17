<div class="pdpVideo">
  <div id="productVideoLink">
    <#if pdpVideoUrl?has_content && pdpVideoUrl!=''>
      <a href="javascript:void(0);" id="pdpShowVideo" onclick="javascript:showProductVideo('productVideo')"><span>${uiLabelMap.PdpVideoLabel}</span></a>
    </#if>
  </div>
  <div class="productVideo" id="productVideo" style="display:none">
    <#if pdpVideoUrl?has_content && pdpVideoUrl!=''>
      <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
	    <param name="movie" value="${pdpVideoUrl!}">
	    <param name="wmode" value="transparent">
	    <embed wmode="transparent" src="${pdpVideoUrl!}" <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>></embed>
	  </object>
	</#if>
  </div>
</div>
  
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantMap.get('${key}')/>
    <#assign pdpVideoUrl = variantProdCtntWrapper.get("PDP_VIDEO_URL")!""/>
    <#if pdpVideoUrl?has_content && pdpVideoUrl!=''>
      <div id="productVideoLink_${key}" style="display:none">
         <a href="javascript:void(0);" id="pdpShowVideo_${key}" onclick="javascript:showProductVideo('productVideo')"><span>${uiLabelMap.PdpVideoLabel}</span></a>
      </div>
      <div id="productVideo_${key}" style="display:none">
	    <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
		  <param name="movie" value="${pdpVideoUrl}">
		  <param name="wmode" value="transparent">
		  <embed wmode="transparent" src="${pdpVideoUrl}" <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>></embed>
	    </object>
	  </div>
    </#if>  
  </#list>
</#if>
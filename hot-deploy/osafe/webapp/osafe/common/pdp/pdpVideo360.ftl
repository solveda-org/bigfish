<div class="pdpVideo360">
<#assign IMG_SIZE_PDP_VIDEO_360_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_VIDEO_360_H")!""/>
<#assign IMG_SIZE_PDP_VIDEO_360_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_VIDEO_360_W")!""/>
  <div id="productVideo360Link">
    <#if pdpVideo360Url?has_content && pdpVideo360Url!=''>
      <a href="javascript:void(0);" id="pdpShowVideo360" onclick="javascript:showProductVideo('productVideo360')"><span>${uiLabelMap.PdpVideo360Label}</span></a>
    </#if>
  </div>
  <div class="productVideo360" id="productVideo360" style="display:none">
    <#if pdpVideo360Url?has_content && pdpVideo360Url!=''>
	  <object <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>>		
	    <param name="movie" value="${pdpVideo360Url!}">
	    <param name="wmode" value="transparent">
	    <embed wmode="transparent" src="${pdpVideo360Url!}" <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>></embed>
	  </object>
	</#if>
  </div>
</div>

<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')/>
    <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
    <#assign pdpVideo360Url = ""/>
    <#if variantContentIdMap?has_content>
    	<#assign variantContentId = variantContentIdMap.get("PDP_VIDEO_360_URL")!""/>
        <#if variantContentId?has_content>
           <#assign pdpVideo360Url = variantProdCtntWrapper.get("PDP_VIDEO_360_URL")!""/>
        </#if>
    </#if>
    <#if pdpVideo360Url?has_content && pdpVideo360Url!=''>
      <div id="productVideo360Link_${key}" style="display:none">
         <a href="javascript:void(0);" id="pdpShowVideo360_${key}" onclick="javascript:showProductVideo('productVideo360')"><span>${uiLabelMap.PdpVideo360Label}</span></a>
      </div>
      <div id="productVideo360_${key}" style="display:none">
	    <object <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>>		
		  <param name="movie" value="${pdpVideo360Url}">
		  <param name="wmode" value="transparent">
		  <embed wmode="transparent" src="${pdpVideo360Url}" <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>></embed>
	    </object>
	  </div>
    </#if>  
  </#list>
</#if>
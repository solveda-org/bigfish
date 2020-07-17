<#if pdpVideo360Url?has_content && pdpVideo360Url!=''>
 <li class="${request.getAttribute("attributeClass")!}">
  <div class="pdpVideo360">
  <#assign IMG_SIZE_PDP_VIDEO_360_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_VIDEO_360_H")!""/>
  <#assign IMG_SIZE_PDP_VIDEO_360_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_VIDEO_360_W")!""/>
    <div id="js_productVideo360Link">
      <a href="javascript:void(0);" id="pdpShowVideo360" onclick="javascript:showProductVideo('js_productVideo360')"><span>${uiLabelMap.PdpVideo360Label}</span></a>
    </div>
    <div class="js_productVideo360" id="js_productVideo360" style="display:none">
    	<object <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>>		
    	  <param name="movie" value="${pdpVideo360Url!}"></param>
          <param name="allowFullScreen" value="true"></param>
		  <param name="wmode" value="transparent"></param>
          <param name="allowscriptaccess" value="always"></param>
	      <embed wmode="transparent" src="${pdpVideo360Url!}" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>></embed>
	    </object>
    </div>
  </div>
	
	<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
	  <#list productVariantMapKeys as key>
	    <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')/>
	    <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
	    <#assign pdpVideo360UrlVariant = ""/>
	    <#if variantContentIdMap?has_content>
	    	<#assign variantContentId = variantContentIdMap.get("PDP_VIDEO_360_URL")!""/>
	        <#if variantContentId?has_content>
	           <#assign pdpVideo360UrlVariant = variantProdCtntWrapper.get("PDP_VIDEO_360_URL")!""/>
	        </#if>
	    </#if>
	    
	    <#if pdpVideo360UrlVariant == "" && pdpVideo360Url !="">
	        <#assign pdpVideo360UrlVariant = pdpVideo360Url/>
	    </#if>
	    
	    <#if pdpVideo360UrlVariant?has_content && pdpVideo360UrlVariant!=''>
	      <div id="js_productVideo360Link_${key}" style="display:none">
	         <a href="javascript:void(0);" id="pdpShowVideo360_${key}" onclick="javascript:showProductVideo('js_productVideo360')"><span>${uiLabelMap.PdpVideo360Label}</span></a>
	      </div>
	      <div id="js_productVideo360_${key}" style="display:none">
		    <object <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>>		
			  <param name="movie" value="${pdpVideo360UrlVariant}"></param>
	          <param name="allowFullScreen" value="true"></param>
			  <param name="wmode" value="transparent"></param>
	          <param name="allowscriptaccess" value="always"></param>
			  <embed wmode="transparent" src="${pdpVideo360UrlVariant}" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>></embed>
		    </object>
		  </div>
	    </#if>  
	  </#list>
	</#if>
</li>
</#if>
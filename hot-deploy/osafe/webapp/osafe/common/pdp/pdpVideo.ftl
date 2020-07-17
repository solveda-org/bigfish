<#if pdpVideoUrl?has_content && pdpVideoUrl!=''>
  <li class="${request.getAttribute("attributeClass")!}">
    <div class="pdpVideo">
      <#assign IMG_SIZE_PDP_VIDEO_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_VIDEO_H")!""/>
      <#assign IMG_SIZE_PDP_VIDEO_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_VIDEO_W")!""/>
      <div id="js_productVideoLink">
        <a href="javascript:void(0);" id="pdpShowVideo" onclick="javascript:showProductVideo('js_productVideo')"><span>${uiLabelMap.PdpVideoLabel}</span></a>
      </div>
      <div class="js_productVideo" id="js_productVideo" style="display:none">
        <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
	      <param name="movie" value="${pdpVideoUrl!}"></param>
          <param name="allowFullScreen" value="true"></param>
	      <param name="wmode" value="transparent"></param>
          <param name="allowscriptaccess" value="always"></param>
	      <embed wmode="transparent" src="${pdpVideoUrl!}" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>></embed>
	    </object>
      </div>
    </div>
  
	<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
	  <#list productVariantMapKeys as key>
	    <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')/>
	    <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
	    <#assign pdpVideoUrlVariant = ""/>
	    <#if variantContentIdMap?has_content>
	    	<#assign variantContentId = variantContentIdMap.get("PDP_VIDEO_URL")!""/>
	        <#if variantContentId?has_content>
	           <#assign pdpVideoUrlVariant = variantProdCtntWrapper.get("PDP_VIDEO_URL")!""/>
	        </#if>
	    </#if>
	    
	    <#if pdpVideoUrlVariant == "" && pdpVideoUrl !="">
	        <#assign pdpVideoUrlVariant = pdpVideoUrl/>
	    </#if>
	    <#if pdpVideoUrlVariant?has_content && pdpVideoUrlVariant!=''>
	      <div id="js_productVideoLink_${key}" style="display:none">
	         <a href="javascript:void(0);" id="pdpShowVideo_${key}" onclick="javascript:showProductVideo('js_productVideo')"><span>${uiLabelMap.PdpVideoLabel}</span></a>
	      </div>
	      <div id="js_productVideo_${key}" style="display:none">
		    <object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
			  <param name="movie" value="${pdpVideoUrlVariant}"></param>
	          <param name="allowFullScreen" value="true"></param>
			  <param name="wmode" value="transparent"></param>
	          <param name="allowscriptaccess" value="always"></param>
			  <embed wmode="transparent" src="${pdpVideoUrlVariant}" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>></embed>
		    </object>
		  </div>
	    </#if>  
	  </#list>
	</#if>
 </li>
</#if>
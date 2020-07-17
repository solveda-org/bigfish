<#if pdpVideo360Url?has_content>
  <div id="pdpVideo360">
		<a href="#" id="pdpShowVideo360" onclick="javascript:showProductVideo('pdpVideo360')"><span>${uiLabelMap.PdpVideo360Label}</span></a>
        <div class="pdpVideo360" style="display:none">
		<object <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>>		
		<param name="movie" value="${pdpVideo360Url}">
		<param name="wmode" value="transparent">
		<embed wmode="transparent" src="${pdpVideo360Url}" <#if IMG_SIZE_PDP_VIDEO_360_H?has_content> height="${IMG_SIZE_PDP_VIDEO_360_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_360_W?has_content> width="${IMG_SIZE_PDP_VIDEO_360_W}"</#if>></embed>
		</object>
		</div>
  </div>
</#if>

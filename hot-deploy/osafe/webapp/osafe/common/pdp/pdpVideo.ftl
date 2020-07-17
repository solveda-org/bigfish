<#if pdpVideoUrl?has_content>
  <div id="pdpVideo">
		<a href="javascript:void(0);" id="pdpShowVideo" onclick="javascript:showProductVideo('pdpVideo')"><span>${uiLabelMap.PdpVideoLabel}</span></a>
        <div class="pdpVideo" style="display:none">
		<object <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>>		
		<param name="movie" value="${pdpVideoUrl}">
		<param name="wmode" value="transparent">
		<embed wmode="transparent" src="${pdpVideoUrl}" <#if IMG_SIZE_PDP_VIDEO_H?has_content> height="${IMG_SIZE_PDP_VIDEO_H}"</#if> <#if IMG_SIZE_PDP_VIDEO_W?has_content> width="${IMG_SIZE_PDP_VIDEO_W}"</#if>></embed>
		</object>
		</div>
  </div>
</#if>

<#if plpManufacturerProfileImageUrl?exists &&  plpManufacturerProfileImageUrl?has_content>
	<li class="${request.getAttribute("attributeClass")!}">
	 <div>
	  <#assign IMG_SIZE_PLP_MFG_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_MFG_H")!""/>
	  <#assign IMG_SIZE_PLP_MFG_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_MFG_W")!""/>
	  <img alt="${plpManufacturerProfileName!""}" src="${plpManufacturerProfileImageUrl!""}" class="manufacturerImage" height="${IMG_SIZE_PLP_MFG_H!""}" width="${IMG_SIZE_PLP_MFG_W!""}" onerror="onImgError(this, 'MANU-Image');">
	 </div>
	</li>  
</#if> 

<div class="pdpManufacturerImage">
   <#assign IMG_SIZE_PDP_MFG_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_MFG_H")!""/>
   <#assign IMG_SIZE_PDP_MFG_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_MFG_W")!""/>
    <img alt="${manufacturerProfileName!""}" src="${manufacturerProfileImageUrl!""}" class="manufacturerImage" height="${IMG_SIZE_PDP_MFG_H!""}" width="${IMG_SIZE_PDP_MFG_W!""}" onerror="onImgError(this, 'MANU-Image');">
</div>

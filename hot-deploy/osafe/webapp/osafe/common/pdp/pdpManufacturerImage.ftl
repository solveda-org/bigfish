<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <#if pdpManufacturerProfileImageUrl?has_content>
      <#assign IMG_SIZE_PDP_MFG_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_MFG_H")!""/>
      <#assign IMG_SIZE_PDP_MFG_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_MFG_W")!""/>
      <a href="<@ofbizUrl>eCommerceManufacturerDetail?manufacturerPartyId=${manufacturerPartyId}</@ofbizUrl>"><img alt="${pdpManufacturerProfileName!""}" src="${pdpManufacturerProfileImageUrl!""}" height="${IMG_SIZE_PDP_MFG_H!""}" width="${IMG_SIZE_PDP_MFG_W!""}" onerror="onImgError(this, 'MANU-Image');"></a>
    </#if>    
  </div>
</li>

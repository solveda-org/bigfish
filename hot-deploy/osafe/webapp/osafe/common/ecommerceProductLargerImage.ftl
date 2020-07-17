<div>
  <#assign productDetailImageUrl = request.getAttribute('detailImageUrl')!"" />
  <#assign IMG_SIZE_PDP_POPUP_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_POPUP_H")!""/>
  <#assign IMG_SIZE_PDP_POPUP_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_POPUP_W")!""/>
  <#if productDetailImageUrl == "">
      <#assign productDetailImageUrl = "/osafe_theme/images/user_content/images/NotFoundImagePDPDetail.jpg">
  </#if>
  <div><img id="js_largeImage" name="largeImage" src="<@ofbizContentUrl>${productDetailImageUrl!}</@ofbizContentUrl>" <#if IMG_SIZE_PDP_POPUP_H?has_content> height="${IMG_SIZE_PDP_POPUP_H!""}"</#if> <#if IMG_SIZE_PDP_POPUP_W?has_content> width="${IMG_SIZE_PDP_POPUP_W!""}"</#if> onerror="onImgError(this, 'PDP-Detail');"/></div>
</div>
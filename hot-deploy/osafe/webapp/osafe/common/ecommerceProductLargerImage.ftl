<div>
  <#assign productLargeImageUrl = request.getAttribute('largeImageUrl')!"" />
  <#if productLargeImageUrl == "">
      <#assign productLargeImageUrl = "/osafe_theme/images/user_content/images/NotFoundImagePDPDetail.jpg">
  </#if>
  <div><img id="largeImage" name="largeImage" src="<@ofbizContentUrl>${productLargeImageUrl!}</@ofbizContentUrl>" <#if IMG_SIZE_PDP_POPUP_H?has_content> height="${IMG_SIZE_PDP_POPUP_H!""}"</#if> <#if IMG_SIZE_PDP_POPUP_W?has_content> width="${IMG_SIZE_PDP_POPUP_W!""}"</#if> onerror="onImgError(this, 'PDP-Detail');"/></div>
</div>
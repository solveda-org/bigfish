<div class="clpThumbImage">
<div class="eCommerceThumbNail">
   <a title="${categoryName}" href="${productCategoryUrl}">
      <#assign IMG_SIZE_PLP_CAT_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_CAT_H")!""/>
      <#assign IMG_SIZE_PLP_CAT_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PLP_CAT_W")!""/>
      <#if categoryImageUrl?has_content>
        <img alt="${categoryName}" src="${categoryImageUrl}" class="categoryImage" height="${IMG_SIZE_PLP_CAT_H!""}" width="${IMG_SIZE_PLP_CAT_W!""}" onerror="onImgError(this, 'CLP-Thumb');">
      </#if>
   </a>
</div>
</div>

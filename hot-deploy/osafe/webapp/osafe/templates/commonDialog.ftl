<div id="${dialogPurpose!}dialog" class="dialogOverlay"></div>
<div id="${dialogPurpose!}displayDialog" style="display:none" class="${dialogBoxStyle!""}">
  <#assign IMG_SIZE_PDP_REG_H = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_REG_H")!""/>
  <#assign IMG_SIZE_PDP_REG_W = Static["com.osafe.util.Util"].getProductStoreParm(request,"IMG_SIZE_PDP_REG_W")!""/>
  <input type="hidden" name="${dialogPurpose!}dialogBoxTitle" id="${dialogPurpose!}dialogBoxTitle" value="${dialogBoxTitle!}"/>
  <#if dialogPurpose?has_content>
    <#assign dialogBoxSection = "${dialogPurpose!}DialogBox" />
    ${sections.render(dialogBoxSection!)}
  <#elseif largeImageUrl?exists && largeImageUrl?has_content>
    <img src="<@ofbizContentUrl>${largeImageUrl!}</@ofbizContentUrl>" id="js_mainImage" name="mainImage" class="js_productLargeImage" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if>/>
  </#if>
</div>

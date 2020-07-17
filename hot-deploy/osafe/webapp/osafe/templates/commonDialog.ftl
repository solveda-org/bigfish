<div id="${dialogPurpose!}dialog" class="dialogOverlay"></div>
<div id="${dialogPurpose!}displayDialog" style="display:none" class="${dialogBoxStyle!""}">
  <input type="hidden" name="${dialogPurpose!}dialogBoxTitle" id="${dialogPurpose!}dialogBoxTitle" value="${dialogBoxTitle!}"/>
  <#if dialogPurpose?has_content>
    <#assign dialogBoxSection = "${dialogPurpose!}DialogBox" />
    ${sections.render(dialogBoxSection!)}
  <#elseif largeImageUrl?exists && largeImageUrl?has_content>
    <img src="<@ofbizContentUrl>${largeImageUrl!}</@ofbizContentUrl>" id="mainImage" name="mainImage" class="productLargeImage" <#if IMG_SIZE_PDP_REG_H?has_content> height="${IMG_SIZE_PDP_REG_H!""}"</#if> <#if IMG_SIZE_PDP_REG_W?has_content> width="${IMG_SIZE_PDP_REG_W!""}"</#if>/>
  </#if>
</div>

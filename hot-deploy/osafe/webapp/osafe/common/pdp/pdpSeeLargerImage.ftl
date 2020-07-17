<div class="pdpSeeLargerImage">
<div id="seeLargerImage" class="seeLargerImage">
  <#if productLargeImageUrl?has_content && productLargeImageUrl!=''>
    <a name="mainImageLink" id="mainImageLink" href="javascript:displayDialogBox('largeImage_');"><span>${uiLabelMap.ViewLargerImageLabel}</span></a>
  </#if>
</div>
</div>
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantMap.get('${key}')/>
    <#assign productLargeImageUrl = variantProdCtntWrapper.get("LARGE_IMAGE_URL")!""/>
    <#assign productDetailImageUrl = variantProdCtntWrapper.get("DETAIL_IMAGE_URL")!""/>
    <#if productLargeImageUrl?has_content && productLargeImageUrl!=''>
      <div id="largeImageUrl_${key}" style="display:none">
        <a name="mainImageLink" class="mainImageLink" href="javascript:setDetailImage('${productDetailImageUrl!productLargeImageUrl!}');displayDialogBox('largeImage_');"><span>${uiLabelMap.ViewLargerImageLabel}</span></a>
      </div>
    </#if>  
  </#list>
</#if>

${screens.render("component://osafe/widget/DialogScreens.xml#viewLargeImageDialog")}
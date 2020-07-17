<div class="pdpSeeLargerImage">
<div id="seeLargerImage" class="seeLargerImage">
  <#if productDetailImageUrl?has_content && productDetailImageUrl!=''>
    <a name="mainImageLink" id="mainImageLink" href="javascript:displayDialogBox('largeImage_');"><span>${uiLabelMap.ViewLargerImageLabel}</span></a>
  </#if>
</div>
</div>
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')!/>
    <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
    <#assign productDetailImageUrl = context.get("productDetailImageUrl")!""/>
    <#if variantContentIdMap?has_content>
    	<#assign variantContentId = variantContentIdMap.get("DETAIL_IMAGE_URL")!""/>
        <#if variantContentId?has_content>
           <#assign productDetailImageUrl = variantProdCtntWrapper.get("DETAIL_IMAGE_URL")!""/>
        </#if>
    </#if>
    <#if (!variantProductDetailImageUrl?has_content || variantProductDetailImageUrl == '')  && (productDetailImageUrl?has_content && productDetailImageUrl != '')>
      <#assign variantProductDetailImageUrl = productDetailImageUrl/>
    </#if>
    <#if (variantProductDetailImageUrl?has_content && variantProductDetailImageUrl != '')>
      <div id="largeImageUrl_${key}" style="display:none">
        <a name="mainImageLink" class="mainImageLink" href="javascript:setDetailImage('<#if (variantProductDetailImageUrl?has_content && variantProductDetailImageUrl !='')>${variantProductDetailImageUrl!}</#if>');displayDialogBox('largeImage_');"><span>${uiLabelMap.ViewLargerImageLabel}</span></a>
      </div>
    </#if>  
  </#list>
</#if>

${screens.render("component://osafe/widget/DialogScreens.xml#viewLargeImageDialog")}
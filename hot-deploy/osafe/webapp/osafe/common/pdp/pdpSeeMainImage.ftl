<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <div id="js_seeMainImage">
      <a href="javascript:void(0);" onclick="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><span>${uiLabelMap.SeeMainImageLabel}</span></a>
    </div>
  </div>
</li>
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantContentWrapperMap.get('${key}')/>
    <#assign variantContentIdMap = productVariantProductContentIdMap.get('${key}')!""/>
    <#assign productLargeImageUrl = context.get("productLargeImageUrl")!""/>
    <#assign productDetailImageUrl = context.get("productDetailImageUrl")!""/>
    <#if variantContentIdMap?has_content>
    	<#assign variantContentId = variantContentIdMap.get("LARGE_IMAGE_URL")!""/>
        <#if variantContentId?has_content>
           <#assign productLargeImageUrl = variantProdCtntWrapper.get("LARGE_IMAGE_URL")!""/>
        </#if>
    	<#assign variantContentId = variantContentIdMap.get("DETAIL_IMAGE_URL")!""/>
        <#if variantContentId?has_content>
           <#assign productDetailImageUrl = variantProdCtntWrapper.get("DETAIL_IMAGE_URL")!""/>
        </#if>
    </#if>
    <#if productLargeImageUrl?has_content && productLargeImageUrl!=''>
      <div id="js_seeMainImage_${key}" style="display:none">
        <a href="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><span>${uiLabelMap.SeeMainImageLabel}</span></a>
      </div>
    </#if>  
  </#list>
</#if>
 
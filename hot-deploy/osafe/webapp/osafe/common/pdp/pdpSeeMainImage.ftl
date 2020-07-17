<div class="pdpSeeMainImage">
<div id="seeMainImage">
    <a href="javascript:void(0);" onclick="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><span>${uiLabelMap.SeeMainImageLabel}</span></a>
</div>
</div>
<#if productVariantMapKeys?exists && productVariantMapKeys?has_content>
  <#list productVariantMapKeys as key>
    <#assign variantProdCtntWrapper = productVariantMap.get('${key}')/>
    <#assign productLargeImageUrl = variantProdCtntWrapper.get("LARGE_IMAGE_URL")!""/>
    <#assign productDetailImageUrl = variantProdCtntWrapper.get("DETAIL_IMAGE_URL")!""/>
    <#if productLargeImageUrl?has_content && productLargeImageUrl!=''>
      <div id="seeMainImage_${key}" style="display:none">
        <a href="javascript:replaceDetailImage('${productLargeImageUrl?if_exists}','${productDetailImageUrl!""}');"><span>${uiLabelMap.SeeMainImageLabel}</span></a>
      </div>
    </#if>  
  </#list>
</#if>
 
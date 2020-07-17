<#-- This ftl is used on plpQuickLook so that it doesn't uses any context parameters(except featureValueSelected) of eCommercePLPItem.groovy -->
<#assign pdpProductFriendlyUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}')/>
<#if productFeatureType?has_content && featureValueSelected?has_content>
  <#assign pdpProductFriendlyUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}&productFeatureType=${productFeatureType!""}:${featureValueSelected!""}')/>
</#if>
<#if parameters.productFeatureType?has_content>
  <#assign pdpProductFriendlyUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request,'eCommerceProductDetail?productId=${productId!""}&productCategoryId=${categoryId!productCategoryId!""}&productFeatureType=${StringUtil.wrapString(parameters.productFeatureType!)}')/>
</#if>

<div class="pdpSeeItemDetails">
  <a class="js_seeItemDetail pdpUrl" title="${productName!""}" href="${pdpProductFriendlyUrl!""}"><span>${uiLabelMap.SeeItemDetailsLabel}</span></a>
</div>
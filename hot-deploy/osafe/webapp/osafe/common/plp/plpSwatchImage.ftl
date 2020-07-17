<#if plpProductSelectableFeatureAndAppl?has_content>
 <#assign PRODUCT_MONEY_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_MONEY_THRESHOLD")!"0"/>
 <#assign PRODUCT_PCT_THRESHOLD = Static["com.osafe.util.Util"].getProductStoreParm(request,"PRODUCT_PCT_THRESHOLD")!"0"/>
 <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>
<li class="${request.getAttribute("attributeClass")!}">
  <div class="swatch">
    <#list plpProductSelectableFeatureAndAppl as productFeatureAppls>
      <#assign productFeatureId=productFeatureAppls.productFeatureId/>
      <#assign productFeatureTypeId=productFeatureAppls.productFeatureTypeId/>
      <#assign productFeatureDescription=productFeatureAppls.description!""/>

      <#assign productFeatureVariantId=""/>
      <#assign productVariantFeatureMap = plpProductFeatureFirstVariantIdMap.get(productFeatureId)!"">
      <#if productVariantFeatureMap?has_content>
          <#assign productFeatureVariantId=productVariantFeatureMap.get("productVariantId")!""/>
          <#assign productFeatureVariantProduct=productVariantFeatureMap.get("productVariant")!""/>
          <#assign descriptiveFeatureGroupDesc = productVariantFeatureMap.get("descriptiveFeatureGroupDesc")!"" />
          <#assign variantListPrice = productVariantFeatureMap.get("listPrice")!""/>
          <#assign variantOnlinePrice = productVariantFeatureMap.get("basePrice")!""/>
      </#if>
      <#if productFeatureVariantId?has_content>
        <#assign variantProductUrl = Static["com.osafe.control.SeoUrlHelper"].makeSeoFriendlyUrl(request, StringUtil.wrapString(plpPdpUrl) + "&productFeatureType=${productFeatureTypeId!}:${productFeatureDescription!}") />
        <input type = "hidden" id="${plpProductId}${productFeatureTypeId!}:${productFeatureDescription!}" value="${variantProductUrl!}"/>
        <input type = "hidden" class="js_featureGroup" value="${descriptiveFeatureGroupDesc!}"/>
        
        <#assign productVariantContentWrapper = plpProductVariantContentWrapperMap.get('${productFeatureVariantId!}')/>
        <#assign variantContentIdMap = plpProductVariantProductContentIdMap.get('${productFeatureVariantId}')!""/>
        <#assign productVariantSmallURL = "">
        <#assign productVariantSmallAltURL = "">
        <#assign productVariantPlpSwatchURL = "">
 	    <#if variantContentIdMap?has_content>
	    	<#assign variantContentId = variantContentIdMap.get("SMALL_IMAGE_URL")!""/>
	        <#if variantContentId?has_content>
                <#assign productVariantSmallURL = productVariantContentWrapper.get("SMALL_IMAGE_URL")!"">
            <#else>
                <#assign productVariantSmallURL = plpProductContentWrapper.get("SMALL_IMAGE_URL")!"">
	        </#if>
	    	<#assign variantContentId = variantContentIdMap.get("SMALL_IMAGE_ALT_URL")!""/>
	        <#if variantContentId?has_content>
               <#assign productVariantSmallAltURL = productVariantContentWrapper.get("SMALL_IMAGE_ALT_URL")!"">
            <#else>
                <#assign productVariantSmallAltURL = plpProductContentWrapper.get("SMALL_IMAGE_ALT_URL")!"">
	        </#if>
	    	<#assign variantContentId = variantContentIdMap.get("PLP_SWATCH_IMAGE_URL")!""/>
	        <#if variantContentId?has_content>
                <#assign productVariantPlpSwatchURL = productVariantContentWrapper.get("PLP_SWATCH_IMAGE_URL")!"">
	        </#if>
	    </#if>
        <#if productVariantPlpSwatchURL?string?has_content>
          <img src="<@ofbizContentUrl>${productVariantPlpSwatchURL}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${plpProductId!}" class="js_plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight?has_content && plpSwatchImageHeight != '0'>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth?has_content && plpSwatchImageWidth != '0'>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
        <#else>
          <#assign productFeatureUrl = ""/>
          <#if plpProductFeatureDataResourceMap?has_content>
           <#assign productFeatureResourceUrl = plpProductFeatureDataResourceMap.get(productFeatureId)!""/>
           <#if productFeatureResourceUrl?has_content>
             <#assign productFeatureUrl=productFeatureResourceUrl/>
           </#if>
          </#if>
          <#if productFeatureUrl?has_content>
            <img src="<@ofbizContentUrl>${productFeatureUrl}</@ofbizContentUrl>" id="${productFeatureTypeId!}:${productFeatureDescription!}|${plpProductId!}" class="js_plpFeatureSwatchImage <#if featureValueSelected==productFeatureDescription>selected</#if> ${productFeatureDescription!""} ${descriptiveFeatureGroupDesc!""}" title="${productFeatureDescription!""}" alt="${productFeatureDescription!""}" name="${productFeatureVariantId!""}" <#if plpSwatchImageHeight?has_content && plpSwatchImageHeight != '0'>height = "${plpSwatchImageHeight}"</#if> <#if plpSwatchImageWidth?has_content && plpSwatchImageWidth != '0'>width = "${plpSwatchImageWidth}"</#if> onerror="onImgError(this, 'PLP-Swatch');"/>
          </#if>
        </#if>
        <div class="js_swatchVariant" style="display:none">
          <a class="pdpUrl" title="${plpProductName!}" href="${plpProductFriendlyUrl}">
            <img alt="${plpProductName!}" title="${plpProductName!}" src="${productVariantSmallURL!}" class="productThumbnailImage" <#if thumbImageHeight?has_content> height="${thumbImageHeight!""}"</#if> <#if thumbImageWidth?has_content> width="${thumbImageWidth!""}"</#if> <#if productVariantSmallAltURL?string?has_content>onmouseover="src='${productVariantSmallAltURL}'"</#if> onmouseout="src='${productVariantSmallURL}'" onerror="onImgError(this, 'PLP-Thumb');"/>
          </a>
        </div>


        <div class="js_swatchVariantOnlinePrice" style="display:none">
          <label>${uiLabelMap.PlpPriceLabel}</label>
          <span><@ofbizCurrency amount=variantOnlinePrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
        </div>
     
  
        <div class="js_swatchVariantListPrice" style="display:none">
          <#if variantListPrice?has_content && variantListPrice gt variantOnlinePrice>
            <label>${uiLabelMap.PlpListPriceLabel}</label> 
            <span><@ofbizCurrency amount=variantListPrice isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
          </#if>
        </div>
        
        <div class="js_swatchVariantSaveMoney" style="display:none">
          <#assign showSavingMoneyAbove = PRODUCT_MONEY_THRESHOLD!"0"/>
          <#if variantListPrice?has_content && variantOnlinePrice?has_content>
            <#assign youSaveMoney = (variantListPrice - variantOnlinePrice)/>
            <#if (youSaveMoney?has_content) && (youSaveMoney gt showSavingMoneyAbove?number)>  
              <label>${uiLabelMap.YouSaveCaption}</label>
              <span><@ofbizCurrency amount=youSaveMoney isoCode=CURRENCY_UOM_DEFAULT!productStore.defaultCurrencyUomId!"" rounding=globalContext.currencyRounding/></span>
            </#if>
          </#if>
        </div>
        
        <div class="js_swatchVariantSavingPercent" style="display:none">
          <#if variantListPrice?has_content && variantListPrice != 0>
            <#assign showSavingPercentAbove = PRODUCT_PCT_THRESHOLD!"0"/>
            <#assign showSavingPercentAbove = (showSavingPercentAbove?number)/100.0 />
            <#assign youSavePercent = ((variantListPrice - variantOnlinePrice)/variantListPrice) />
            <#if youSavePercent gt showSavingPercentAbove?number>  
              <label>${uiLabelMap.YouSaveCaption}</label>
              <span>${youSavePercent?string("#0%")}</span>
            </#if>
          </#if>
        </div>
      </#if>
    </#list>
  </div>
</li>
</#if>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0)>
    <#assign productPromoCodesEntered = shoppingCart.getProductPromoCodesEntered().clone()/>
    <#assign productPromoUseInfoList = Static["javolution.util.FastList"].newInstance()/>
    <#list shoppingCart.getProductPromoUseInfoIter() as productPromoUsed>
        <#assign removed = productPromoCodesEntered.remove(productPromoUsed.productPromoCodeId)/>
        <#assign productPromoUseMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productPromoCodeId", productPromoUsed.productPromoCodeId, "productPromoId", productPromoUsed.productPromoId, "totalDiscountAmount", productPromoUsed.totalDiscountAmount, "quantityLeftInActions", productPromoUsed.quantityLeftInActions)/>
        <#assign changed = true/>
        <#list productPromoUseInfoList as productPromoUseInfo>
          <#if productPromoUseInfo.productPromoCodeId?has_content && productPromoUsed.productPromoCodeId?has_content>
            <#if productPromoUseInfo.productPromoCodeId == productPromoUsed.productPromoCodeId && productPromoUseInfo.productPromoId == productPromoUsed.productPromoId >
                <#assign changed = false/>
            </#if>
          </#if>
        </#list>
        <#if changed>
            <#assign changed = productPromoUseInfoList.add(productPromoUseMap)/>
        </#if>
    </#list>
    <#list productPromoCodesEntered.iterator() as productPromoCodeEntered>
        <#assign productPromoCode = delegator.findByPrimaryKeyCache("ProductPromoCode", {"productPromoCodeId" : productPromoCodeEntered})/>
        <#assign productPromoUseMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productPromoCodeId", productPromoCodeEntered, "productPromoId", productPromoCode.productPromoId!, "totalDiscountAmount", 0, "quantityLeftInActions", null)/>
        <#assign changed = productPromoUseInfoList.add(productPromoUseMap)/>
    </#list>

    <#list productPromoUseInfoList as productPromoUseInfo>
      <div class="ecommercePromocodeDetail">
        <div class="ecommercePromocodeName">
            <#if productPromoUseInfo.productPromoCodeId?has_content>
                ${productPromoUseInfo.productPromoCodeId!""}
            <#elseif productPromoUseInfo.productPromoId?has_content>
                <#assign productPromo = delegator.findByPrimaryKeyCache("ProductPromo", {"productPromoId" : productPromoUseInfo.productPromoId})/>
                <#if productPromo?has_content>
                    ${productPromo.promoName!""}
                </#if>
            </#if>
        </div>
        <div class="ecommercePromocodeDesc">
            <#if productPromoUseInfo.productPromoId?has_content>
                <#assign productPromo = delegator.findByPrimaryKeyCache("ProductPromo", {"productPromoId" : productPromoUseInfo.productPromoId})/>
                <#if productPromo?has_content>
                    ${productPromo.promoText!""}
                </#if>
            </#if>
        </div>
     <div class="ecommercePromocodeStatus">
         <#if (productPromoUseInfo.quantityLeftInActions?string == null) || (productPromoUseInfo.quantityLeftInActions?double > 0)>
             ${uiLabelMap.PromoCodeAddedOnlyInfo}
         <#else>
             ${uiLabelMap.PromoCodeAppliedInfo}
         </#if>
     </div>
     <div class="ecommercePromocodeAction">
         <#if productPromoUseInfo.productPromoCodeId?has_content>
             <a href="javascript:removePromoCode('${productPromoUseInfo.productPromoCodeId}');">${uiLabelMap.RemoveOfferLabel}</a>
         </#if>
     </div>
     </div>
    </#list>
</#if>

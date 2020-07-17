<table class="osafe">
  <thead>
    <tr class="heading">
      <th class="idCol firstCol">${uiLabelMap.PromoCodeLabel}</th>
      <th class="descCol">${uiLabelMap.PromoDescLabel}</th>
      <th class="statusCol">${uiLabelMap.StatusLabel}</th>
      <th class="actionCol"></th>
    </tr>
  </thead>
  <#-- Get List of promotions. Note that shoppingCart is needed here because this screen is loaded (without any groovy) when shipping method is changed. -->
  <#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request) />
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
      <#assign productPromoCode = delegator.findByPrimaryKey("ProductPromoCode", {"productPromoCodeId" : productPromoCodeEntered})/>
      <#assign productPromoUseMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productPromoCodeId", productPromoCodeEntered, "productPromoId", productPromoCode.productPromoId!, "totalDiscountAmount", 0, "quantityLeftInActions", null)/>
      <#assign changed = productPromoUseInfoList.add(productPromoUseMap)/>
    </#list>
	
    <tbody>
      <#list productPromoUseInfoList as productPromoUseInfo>
        <tr>
          <td class="idCol firstCol">
            <#if productPromoUseInfo.productPromoCodeId?has_content>
              ${productPromoUseInfo.productPromoCodeId!""}
            <#elseif productPromoUseInfo.productPromoId?has_content>
              <#assign productPromo = delegator.findByPrimaryKey("ProductPromo", {"productPromoId" : productPromoUseInfo.productPromoId})/>
              <#if productPromo?has_content>
                ${productPromo.promoName!""}
              </#if>
            </#if>
          </td>
          <td class="descCol">
            <#if productPromoUseInfo.productPromoId?has_content>
              <#assign productPromo = delegator.findByPrimaryKey("ProductPromo", {"productPromoId" : productPromoUseInfo.productPromoId})/>
                <#if productPromo?has_content>
                  ${productPromo.promoText!""}
                </#if>
            </#if>
          </td>
          <td class="statusCol">
            <#if productPromoUseInfo.quantityLeftInActions?has_content >
              <#assign qtyLeftInActions = productPromoUseInfo.quantityLeftInActions.toString() />
            </#if>
            <#assign is_Float = Static["com.osafe.util.OsafeAdminUtil"].isFloat(qtyLeftInActions!"") />
            <!-- When Promo Code is Applied, qtyLeftInActions returns a number. When it is not Applied, it will return a null value. -->
            <#if !(productPromoUseInfo.quantityLeftInActions?has_content) || !(is_Float)>
              ${uiLabelMap.PromoCodeAddedOnlyInfo}
            <#else>
              ${uiLabelMap.PromoCodeAppliedInfo}
            </#if> 
          </td>
          <td class="actionCol">
            <#if productPromoUseInfo.productPromoCodeId?has_content>
              <a href="javascript:removePromoCode('${productPromoUseInfo.productPromoCodeId}');"><span class="crossIcon"></span></a>
            </#if>
          </td>
        </tr>
      </#list>
    </tbody>
  </#if>
</table>


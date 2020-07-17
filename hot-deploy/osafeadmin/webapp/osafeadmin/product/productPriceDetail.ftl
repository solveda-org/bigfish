<#if product?has_content>
<#assign isVariant = product.isVariant!"" />
<#assign priceType= parameters.PriceTypeRadio!""/>
  <div id="errorMessageInsert" class="fieldErrorMessage" style="display:none">${uiLabelMap.NoRowSelectedInsertError}</div>
  <div id="errorMessageDelete" class="fieldErrorMessage" style="display:none">${uiLabelMap.NoRowSelectedDeleteError}</div>
<input type="hidden" name="productId" value="${product.productId?if_exists}" />
<input type="hidden" name="isVariant" id="isVariant" value="${product.isVariant!""}"/>
  <div class="infoRow">
    <div>
      <input type="radio" name="PriceTypeRadio" value="SimplePrice" <#if (!productPriceCondList?has_content && priceType=='') || (priceType=='SimplePrice')>checked="checked"</#if> onclick="javascript:hideVolumePricing('volumepricing');"/>
      ${uiLabelMap.SimplePricingLabel}
    </div>
  </div>
  <div class="infoRow">
    <div>
      <input type="radio" name="PriceTypeRadio" value="VolumePrice" <#if (productPriceCondList?has_content && priceType=='') || (priceType=='VolumePrice')>checked="checked"</#if> onclick="javascript:showVolumePricing('volumepricing');"/>
      ${uiLabelMap.VolumePricingLabel}
    </div>
  </div>
  <div class="infoRow column">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ListPriceCaption}</label>
      </div>
      <div class="infoValue">
        <#if isVariant == 'Y'>
          <@ofbizCurrency amount=parameters.listPrice!productListPrice.price! isoCode=productListPrice.currencyUomId!/>
        <#else>
          <input type="text" class="textEntry textAlignRight" name="listPrice" id="listPrice" value="${parameters.listPrice!productListPrice.price?string("0.00")!}"/>
        </#if>
      </div>
    </div>
  </div>
  <input type="hidden" id="Sale_Price" name="Sale_Price" value="${uiLabelMap.SalePriceCaption}"/>
  <input type="hidden" id="Default_Sale_Price" name="Default_Sale_Price" value="${uiLabelMap.DefaultSalePriceCaption}"/>
  <div class="infoRow column">
    <div class="infoEntry">
      <div class="infoCaption">
        <label id="default_price">
          <#if (productPriceCondList?has_content && priceType=='') || (priceType == 'VolumePrice')>
            ${uiLabelMap.DefaultSalePriceCaption}
          <#else>
            ${uiLabelMap.SalePriceCaption}
          </#if>
        </label>
      </div>
      <div class="infoValue">
        <#if isVariant == 'Y'>
          <@ofbizCurrency amount=parameters.defaultPrice!productDefaultPrice.price! isoCode=productDefaultPrice.currencyUomId!/>
        <#else>
          <input type="text"  class="textEntry textAlignRight" name="defaultPrice" id="defaultPrice" value="${parameters.defaultPrice!productDefaultPrice.price?string("0.00")!}"/>
        </#if>
      </div>
    </div>
  </div>
</#if>

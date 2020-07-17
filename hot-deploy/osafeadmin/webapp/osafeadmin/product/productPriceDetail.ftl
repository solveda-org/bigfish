<#if product?has_content>
<#assign isVariant = product.isVariant!"" />
<#assign isVirtual = product.isVirtual!"" />
<#assign priceType= parameters.PriceTypeRadio!""/>
  <div id="errorMessageInsert" class="fieldErrorMessage" style="display:none">${uiLabelMap.NoRowSelectedInsertError}</div>
  <div id="errorMessageDelete" class="fieldErrorMessage" style="display:none">${uiLabelMap.NoRowSelectedDeleteError}</div>
  <input type="hidden" name="productId" value="${product.productId?if_exists}" />
  <input type="hidden" name="isVariant" id="isVariant" value="${product.isVariant!""}"/>
  <#assign currencyUomId = CURRENCY_UOM_DEFAULT!currencyUomId />
  <input type="hidden" name="currencyUomId" value="${parameters.currencyUomId!currencyUomId!}" />
  <#if isVirtual=='Y' || isVariant=='Y'>
    <#if isVariant == 'Y'>
      <#assign simplePricingHelperInfo = uiLabelMap.VariantSimplePricingHelpInfo! />
      <#assign volumePricingHelperInfo = uiLabelMap.VariantVolumePricingHelpInfo! />
    <#else>
      <#assign simplePricingHelperInfo = uiLabelMap.VirtualSimplePricingHelpInfo! />
      <#assign volumePricingHelperInfo = uiLabelMap.VirtualVolumePricingHelpInfo! />
    </#if>
  </#if>
  <div class="infoRow">
    <div class="infoValue">
      <input type="radio" name="PriceTypeRadio" value="SimplePrice" <#if (!productPriceCondList?has_content && priceType=='') || (priceType=='SimplePrice')>checked="checked"</#if> onclick="javascript:hideVolumePricing('volumepricing');"/>
    </div>
    <div class="infoCaption">  
      ${uiLabelMap.SimplePricingLabel}
    </div>
    <#if isVirtual=='Y' || isVariant=='Y'>
      <div class="infoIcon">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${simplePricingHelperInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </#if>
  </div>
  <div class="infoRow">
    <div class="infoValue">
      <input type="radio" name="PriceTypeRadio" value="VolumePrice" <#if (productPriceCondList?has_content && priceType=='') || (priceType=='VolumePrice')>checked="checked"</#if> onclick="javascript:showVolumePricing('volumepricing');"/>
    </div>
    <div class="infoCaption"> 
      ${uiLabelMap.VolumePricingLabel}
    </div>
    <#if isVirtual=='Y' || isVariant=='Y'>
      <div class="infoIcon">
        <a href="javascript:void(0);" onMouseover="showTooltip(event,'${volumePricingHelperInfo!""}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
      </div>
    </#if>
  </div>
  <div class="infoRow column">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.ListPriceCaption}</label>
      </div>
      <div class="infoValue">
        <#if isVariant == 'Y'>
          <input type="text" class="textEntry textAlignRight" name="variantListPrice" id="variantListPrice" value="<#if parameters.variantListPrice?has_content || productVariantListPrice?has_content>${parameters.variantListPrice!productVariantListPrice.price?string("0.00")!}</#if>"/>
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
          <input type="text"  class="textEntry textAlignRight" name="variantSalePrice" id="variantSalePrice" value="<#if parameters.variantSalePrice?has_content || productVariantSalePrice?has_content>${parameters.variantSalePrice!productVariantSalePrice.price?string("0.00")!}</#if>"/>
        <#else>
          <input type="text"  class="textEntry textAlignRight" name="defaultPrice" id="defaultPrice" value="${parameters.defaultPrice!productDefaultPrice.price?string("0.00")!}"/>
        </#if>
      </div>
    </div>
  </div>

  <#if isVariant != 'Y'>
  <div class="infoRow column">
    <div class="infoEntry">
      <div class="infoCaption">
        <label>${uiLabelMap.RecurringPriceCaption}</label>
      </div>
      <div class="infoValue">
        <#if isVariant == 'Y'>
 	      <#assign variantRecurringPrice = "" />
		  <#if productVariantRecurringPrice?has_content>
		    <#assign variantRecurringPrice = productVariantRecurringPrice.price!"" />
		  </#if>
          <input type="text" class="textEntry textAlignRight" name="variantRecurringPrice" id="variantRecurringPrice" value="<#if parameters.variantRecurringPrice?has_content || productVariantRecurringPrice?has_content>${parameters.variantRecurringPrice!variantRecurringPrice!}</#if>"/>
        <#else>
   	      <#assign recurringPrice = "" />
		  <#if productRecurringPrice?has_content>
		    <#assign recurringPrice = productRecurringPrice.price!"" />
		  </#if>
          <input type="text" class="textEntry textAlignRight" name="recurringPrice" id="recurringPrice" value="${parameters.recurringPrice!recurringPrice!}"/>
        </#if>
      </div>
    </div>
  </div>
 </#if>
  
</#if>

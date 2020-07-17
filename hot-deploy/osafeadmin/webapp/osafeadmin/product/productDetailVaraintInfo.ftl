<#if virtualProduct?has_content>
    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VirtualProductIdCaption}</label>
            </div>
            <div class="infoValue">
                <#-- #if !parameters.productIdTo?has_content>
                    <#assign productIdToSeqId = delegator.getNextSeqId("Product")/>
                </#if> -->
                <#-- input type="hidden" name="productId" id="productId" maxlength="20" value="${virtualProduct.productId!parameters.productId!""}"/> -->
                <#-- input type="hidden" name="productIdTo" id="productIdTo" maxlength="20" value="${parameters.productIdTo!productIdToSeqId!""}"/> -->
                ${virtualProduct.productId!""}
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VirtualItemNoCaption}</label>
            </div>
            <div class="infoValue">
                <#assign internalName = virtualProduct.internalName!"" />${internalName!}
                <!--<input type="text" name="internalName" id="internalName" value="${parameters.internalName!internalName!""}" />-->
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VirtualListPriceCaption}</label>
            </div>
            <div class="infoValue">
                <#if productListPrice?has_content>
                    <@ofbizCurrency amount=productListPrice.price! isoCode=productListPrice.currencyUomId! rounding=globalContext.currencyRounding/>
                </#if>
            </div>
        </div>
    </div>

    <div class="infoRow column">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.VirtualSalePriceCaption}</label>
            </div>
            <div class="infoValue">
                <#if productDefaultPrice?has_content>
                    <@ofbizCurrency amount=productDefaultPrice.price! isoCode=productDefaultPrice.currencyUomId! rounding=globalContext.currencyRounding/>
                </#if>
                <#if virtualProductPriceCondList?has_content><span class="pricingInfo">${uiLabelMap.PricingRulesApplyInfo}</span></#if>
            </div>
         </div>
     </div>

    <#if descFeatureTypesList?has_content>
      <#list descFeatureTypesList as descFeatureType>
        <#if descFeatureByTypeMap?has_content>
          <#assign descFeatureAndApplList = descFeatureByTypeMap[descFeatureType]![]>
          <#if descFeatureAndApplList?has_content>
            <#assign descFeatureAndAppl = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(descFeatureAndApplList) />
            <div class="infoRow column">
              <div class="infoEntry">
                <div class="infoCaption">
                  <label>
                    <#assign productFeatureType = descFeatureAndAppl.getRelatedOne("ProductFeatureType")!"" />
                    ${productFeatureType.description!}:</label>
                </div>
                <div class="infoValue">
                  ${descFeatureAndAppl.description!}
                </div>
              </div>
            </div>
          </#if>
        </#if>
      </#list>
    </#if>
</#if>
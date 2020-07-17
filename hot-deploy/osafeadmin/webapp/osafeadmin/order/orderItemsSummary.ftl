<!-- start orderItemsSummary.ftl -->

<table class="osafe">
  <tr>
    <td colspan="10">
      <table class="osafe orderSummary">
        <tr>
          <td class="totalCaption"><label>${uiLabelMap.SubtotalCaption}</label></td>
          <td class="totalValue"><@ofbizCurrency amount=orderSubTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
        </tr>
        <#if appliedPromoList?exists && appliedPromoList?has_content>
          <#list appliedPromoList as appliedPromo >
            <tr>
              <#assign orderAdjTotal = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(appliedPromo.cartAdjustment, shoppingCartSubTotal)!0/>
              <td class="totalCaption"><label><#if appliedPromo.promoText?has_content>${appliedPromo.promoText!}<#if appliedPromo.promoCodeText?has_content><a href="<@ofbizUrl>promotionCodeDetail?productPromoCodeId=${appliedPromo.promoCodeText!}</@ofbizUrl>">(${appliedPromo.promoCodeText!})</a></#if><#else>${appliedPromo.adjustmentTypeDesc!}</#if>:</label></td>
              <td class="totalValue"><@ofbizCurrency amount=orderAdjTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
            </tr>
          </#list>
        </#if>
        <#if appliedLoyaltyPointsList?exists && appliedLoyaltyPointsList?has_content>
          <#list appliedLoyaltyPointsList as appliedLoyaltyPoints >
            <tr>
              <#assign orderAdjTotal = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(appliedLoyaltyPoints.cartAdjustment, shoppingCartSubTotal)!0/>
              <td class="totalCaption"><label>${appliedLoyaltyPoints.adjustmentTypeDesc!}:</label></td>
              <td class="totalValue"><@ofbizCurrency amount=orderAdjTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
            </tr>
          </#list>
        </#if>
        <tr>
          <td class="totalCaption"><label>${uiLabelMap.ShipHandleCaption}</label></td>
          <td class="totalValue"><@ofbizCurrency amount=shippingAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
        </tr>
        <#if (!Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO")) || (taxAmount?has_content && (taxAmount &gt; 0))>
          <#if !Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_SHOW_SALES_TAX_MULTI")>
            <tr>
	      	  <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
	          <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
              <td class="totalCaption"><label><#if shipGroupSalesTaxSame>${salesTaxCaption!}<#else>${uiLabelMap.SummarySalesTaxShortCaption!}</#if></label></td>
              <td class="totalValue"><@ofbizCurrency amount=taxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
            </tr>
          <#else>
            <#if appliedTaxList?exists && appliedTaxList?has_content && shipGroupSalesTaxSame>
		      <#list appliedTaxList as appliedTax >  
		        <tr>
		          <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", appliedTax.sourcePercentage, "description", appliedTax.description)>
		             <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxMultiCaption",taxInfoStringMap, locale ) />
                  <td class="totalCaption"><label>${salesTaxCaption!}</label></td>
                  <td class="totalValue"><@ofbizCurrency amount=appliedTax.amount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
                </tr>
		      </#list>
		    <#else>
		      <tr>
	      	    <#assign taxInfoStringMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("taxPercent", totalTaxPercent)>
	            <#assign salesTaxCaption = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","SummarySalesTaxCaption",taxInfoStringMap, locale ) />
                <td class="totalCaption"><label><#if shipGroupSalesTaxSame>${salesTaxCaption!}<#else>${uiLabelMap.SummarySalesTaxShortCaption!}</#if></label></td>
                <td class="totalValue"><@ofbizCurrency amount=taxAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
              </tr>
		    </#if>
          </#if>
        </#if>      
        <tr>
          <td class="totalCaption"><label>${uiLabelMap.AdjustmentsCaption}</label></td>
          <td class="totalValue"><@ofbizCurrency amount=otherAdjustmentsAmount rounding=globalContext.currencyRounding isoCode=currencyUomId/></td>
        </tr>
        <tr>
          <td class="totalCaption total"><label>${uiLabelMap.OrderTotalCaption}</label></td>
          <td class="totalValue total">
            <@ofbizCurrency amount=grandTotal rounding=globalContext.currencyRounding isoCode=currencyUomId/>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<!-- end orderItemsSummary.ftl -->



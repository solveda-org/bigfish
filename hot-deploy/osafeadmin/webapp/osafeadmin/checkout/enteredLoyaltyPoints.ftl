<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0)>
  <#assign orderAdjustmentAttributeList = sessionAttributes.orderAdjustmentAttributeList!/>
  <#assign showLoyaltyPointsAdjustedWarning = sessionAttributes.showLoyaltyPointsAdjustedWarning!/>
  <#if !showLoyaltyPointsAdjustedWarning?has_content>
    <#assign showLoyaltyPointsAdjustedWarning = parameters.showLoyaltyPointsAdjustedWarning!/>
  </#if>
  <#if showLoyaltyPointsAdjustedWarning?has_content && showLoyaltyPointsAdjustedWarning == "Y">
    <div id="lpWarningMessText" class="warningMessText">
      <span>${uiLabelMap.LoyaltyPointsExceedCartBalanceWarning}</span>
    </div>
  </#if>
  <div class="boxList loyaltyPointList">
   <table class="osafe">
     <thead>
       <tr class="heading">
         <th class="numberCol firstCol">${uiLabelMap.PointsAvailableLabel}</th>
         <th class="actionCol"></th>
         <th class="numberCol">${uiLabelMap.AmountAvailableLabel}</th>
         <th class="dateCol">${uiLabelMap.ExpiresLabel}</th>
         <th class="numberCol">${uiLabelMap.PointsRedeemedLabel}</th>
         <th class="currencyCol">${uiLabelMap.AmountRedeemedLabel}</th>
         <th class="actionCol"></th>
       </tr>
     </thead> 
     <#if (orderAdjustmentAttributeList?has_content)>
      <#list orderAdjustmentAttributeList as orderAdjustmentAttributeInfoMap>
	    <#-- get Loyalty Points info if there is one set in session -->
	    <#assign indexOfAdj = orderAdjustmentAttributeInfoMap.INDEX!""/>
	    <#assign loyaltyPoints = orderAdjustmentAttributeInfoMap.ADJUST_POINTS!""/>
	    <#assign loyaltyPointsId = orderAdjustmentAttributeInfoMap.MEMBER_ID!""/>
	    <#assign checkoutLoyaltyConversion = orderAdjustmentAttributeInfoMap.CONVERSION_FACTOR!""/>
	    <#assign expDate = orderAdjustmentAttributeInfoMap.EXP_DATE!""/>
	    <#assign loyaltyPointsAmount = orderAdjustmentAttributeInfoMap.CURRENCY_AMOUNT!""/>
	    <#assign totalLoyaltyPoints = orderAdjustmentAttributeInfoMap.TOTAL_POINTS!""/>
	    <#assign totalLoyaltyPointsAmount = orderAdjustmentAttributeInfoMap.TOTAL_AMOUNT!""/>
	    <#assign loyaltyPointsIncludeInTax = orderAdjustmentAttributeInfoMap.INCLUDE_IN_TAX!""/>
	    <#-- Get currency -->
	    <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>  
	    <#assign currencyUom = shoppingCart.getCurrency()!""/>
	    <#if !currencyUom?has_content>
	      <#assign currencyUom = CURRENCY_UOM_DEFAULT!""/>
	    </#if>
	    <#if loyaltyPoints?has_content>  
          <tr>
            <#if totalLoyaltyPoints?has_content>
              <td class="numberCol firstCol">
                ${totalLoyaltyPoints!}
                <input type="hidden" name="loyaltyPointsAvailable" value="${totalLoyaltyPoints!""}"/>
              </td>
            </#if>
            <#assign adjustmentInfoMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("includeInTax", loyaltyPointsIncludeInTax)>
            <#assign adjustmentInfo = "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","AdjustmentIncludeInTaxInfo",adjustmentInfoMap, locale )+"</p>">
	    	<#assign adjustmentInfo = adjustmentInfo + "<p>"+Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeAdminUiLabels","AdjustmentIncludeInShippingInfo",adjustmentInfoMap, locale )+"</p>">
            <td class="actionCol">
	        	<p onMouseover="showTooltip(event,'${adjustmentInfo!""}');" onMouseout="hideTooltip()"><span class="informationIcon"></span></p>
	        </td> 
            <#if totalLoyaltyPointsAmount?has_content>
              <td class="currencyCol">
                <@ofbizCurrency amount=totalLoyaltyPointsAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
              </td>
            </#if>
            <#if expDate?has_content>
              <td class="dateCol">
                ${expDate!""}
              </td>
            </#if>
            <#if loyaltyPoints?has_content>
              <td class="numberCol">
                <input type="text" id="js_loyaltyPointsAmount" name="update_loyaltyPointsAmount" value="${loyaltyPoints!""}" maxlength="20"/>
                <a id="js_updateLoyaltyPointsAmount" href="javascript:updateLoyaltyPoints();"><span class="refreshIcon"></span></a>
              </td>
            </#if>
            <#if loyaltyPointsAmount?has_content>
              <td class="dateCol">
                <@ofbizCurrency amount=loyaltyPointsAmount isoCode=currencyUom rounding=globalContext.currencyRounding/>
              </td>
            </#if>
            <td class="actionCol">
              <a id="js_removeLoyaltyCard" href="javascript:removeLoyaltyPoints();"><span class="crossIcon"></span></a>
            </td>   
          </tr>
        </#if>
      </#list>
    </#if>
    </tbody>
  </table>
 </div>
</#if>


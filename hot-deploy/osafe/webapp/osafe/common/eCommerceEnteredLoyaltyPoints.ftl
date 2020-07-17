<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
<#if (shoppingCart.size() > 0)>
  <#assign orderAdjustmentAttributeList = sessionAttributes.orderAdjustmentAttributeList!/>
  <#assign showLoyaltyPointsAdjustedWarning = sessionAttributes.showLoyaltyPointsAdjustedWarning!/>
  <#if !showLoyaltyPointsAdjustedWarning?has_content>
    <#assign showLoyaltyPointsAdjustedWarning = parameters.showLoyaltyPointsAdjustedWarning!/>
  </#if>
    <#if (orderAdjustmentAttributeList?has_content)>
     <div class="boxList loyaltyPointList">
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
	    <#-- Get currency -->
	    <#assign CURRENCY_UOM_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"CURRENCY_UOM_DEFAULT")!""/>  
	    <#assign currencyUom = shoppingCart.getCurrency()!""/>
	    <#if !currencyUom?has_content>
	      <#assign currencyUom = CURRENCY_UOM_DEFAULT!""/>
	    </#if>
	    
	    <#if loyaltyPoints?has_content>
	      <input type="hidden" name="loyaltyPointsIndex" value="${indexOfAdj!""}"/>
    	  <div class="boxListItemTabular actionResultItem loyaltyPointItem">
		    <ul class="displayList">
		      <#if totalLoyaltyPoints?has_content>
		       <li class="number loyalty">
		        <div>
		         <label>${uiLabelMap.PointsAvailableCaption}</label>
		         <span>${totalLoyaltyPoints!""}</span>
		         <input type="hidden" name="loyaltyPointsAvailable" value="${totalLoyaltyPoints!""}"/>
		        </div>
		       </li>
              </#if>
              <#if totalLoyaltyPointsAmount?has_content>
		       <li class="currency loyalty">
		        <div>
		         <label>${uiLabelMap.AmountAvailableCaption}</label>
		         <span><@ofbizCurrency amount=totalLoyaltyPointsAmount isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
		        </div>
		       </li>
              </#if>
              <#if expDate?has_content>
		       <li class="date expiration">
		        <div>
		         <label>${uiLabelMap.ExpirationDateCaption}</label>
		         <span>${expDate!""}</span>
		        </div>
		       </li>
              </#if>
              <#if loyaltyPoints?has_content>
		       <li class="input loyalty">
		        <div>
		         <label>${uiLabelMap.PointsRedeemedCaption}</label>
		         <input type="text" id="js_loyaltyPointsAmount" name="update_loyaltyPointsAmount" value="${loyaltyPoints!""}" maxlength="20" onkeypress="javascript:setCheckoutFormAction(document.${formName!}, 'ULP', '');"/>
		         <a class="standardBtn update" id="js_updateLoyaltyPointsAmount" href="javascript:updateLoyaltyPoints();" title="${uiLabelMap.UpdateLoyaltyPointsBtn}">
		           <span>${uiLabelMap.UpdateLoyaltyPointsBtn}</span>
		         </a>
		         <@fieldErrors fieldName="update_loyaltyPointsAmount"/>
		        </div>
		       </li>
              </#if>
              <#if loyaltyPointsAmount?has_content>
		       <li class="currency loyalty">
		        <div>
		         <label>${uiLabelMap.AmountRedeemedCaption}</label>
		         <span><@ofbizCurrency amount=loyaltyPointsAmount isoCode=currencyUom rounding=globalContext.currencyRounding/></span>
		        </div>
		       </li>
              </#if>
		      <li class="action remove">
	           <div>
			    <a class="standardBtn delete" id="js_removeLoyaltyCard" href="javascript:removeLoyaltyPoints();" title="${uiLabelMap.RemoveLoyaltyCardBtn}">
		          <span>${uiLabelMap.RemoveLoyaltyCardBtn}</span>
		        </a>
		       </div>
		      </li>
		      <#if totalLoyaltyPointsAmount?has_content && (totalLoyaltyPointsAmount?number &lt; 1)>
		        <li class="string">
	             <div>
			       <span class="warningMessText">${uiLabelMap.LoyaltyPointsLowBalanceError}</span>
		         </div>
		        </li>
		      </#if>
		      <#if showLoyaltyPointsAdjustedWarning?has_content && showLoyaltyPointsAdjustedWarning == "Y">
		        <li class="string">
	             <div>
			       <span class="warningMessText">${uiLabelMap.LoyaltyPointsExceedCartBalanceWarning}</span>
		         </div>
		        </li>
		      </#if>
		    </ul>
		  </div>
	    </#if>
      </#list>
     </div>
     </#if>
</#if>

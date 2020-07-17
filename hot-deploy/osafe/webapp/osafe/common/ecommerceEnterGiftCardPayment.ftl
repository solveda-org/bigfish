<#assign giftCardMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_GIFTCARD_METHOD")!""/>
<#if giftCardMethod?has_content && (giftCardMethod.toUpperCase() != "NONE") >
 <#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
 <#assign shoppingCart = sessionAttributes.shoppingCart?if_exists />
 <#if (shoppingCart.size() > 0)>
   <#assign remainingPayment = shoppingCart.getGrandTotal().subtract(shoppingCart.getPaymentTotal())! />
   <div class="${request.getAttribute("attributeClass")!}">
    <div class="displayBox">
        <h3>${uiLabelMap.GiftCardHeading}</h3>
        <ul class="displayActionList ${request.getAttribute("attributeClass")!}">
          <@fieldErrors fieldName="giftCardNumber"/>
	      <li>
	       <div>
            <label>${uiLabelMap.EnterGiftCardLabel}</label>
            <input type="text" id="js_giftCardNumber" name="giftCardNumber" value="" maxlength="60" autocomplete="off"/>
            <#if (remainingPayment &gt; 0)>
              <a class="standardBtn action" href="javascript:addGiftCardNumber();"><span>${uiLabelMap.ApplyGiftCardBtn}</span></a>
            </#if>
	       </div>
	      </li>
	    </ul>
        ${screens.render("component://osafe/widget/EcommerceCheckoutScreens.xml#eCommerceEnteredGiftCardPayment")}
    </div>
   </div>
 </#if>
</#if>


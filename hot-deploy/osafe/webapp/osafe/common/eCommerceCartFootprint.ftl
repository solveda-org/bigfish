 <#if (shoppingCart?has_content && (shoppingCart.size() > 0)) || (showCartFootprint?has_content && showCartFootprint == "Y" )>
    <div class="eCommerceCartFootPrint">
    	<#-- Shipping Method will not be displayed when no shipping Applies to cart -->
        <#if selectedStep?has_content && selectedStep =="cart">
         <ul class="cartFootPrint cartStep">
            <li id="cart" class="current first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="off next"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="off"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="off"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="off last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
         </ul>
        </#if>
        <#if selectedStep?has_content && selectedStep =="shipping">
         <ul class="cartFootPrint addressStep">
            <li id="cart" class="on first"><a href="javascript:submitCheckoutForm(document.${formName!}, 'SCBK', '');"><span>${uiLabelMap.ShoppingCartFPLabel}</span></a></li>
            <li id="shippingAddress" class="current"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="off next"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="<#if shippingApplies?exists && shippingApplies>off<#else>next</#if>"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="off last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
         </ul>
        </#if>
        
        <#if selectedStep?has_content && selectedStep =="shippingMethod">
         <ul class="cartFootPrint shippingMethodStep">
            <li id="cart" class="on first"><a href="javascript:submitCheckoutForm(document.${formName!}, 'SCBK', '');"><span>${uiLabelMap.ShoppingCartFPLabel}</span></a></li>
            <li id="shippingAddress" class="on"><a href="javascript:submitCheckoutForm(document.${formName!}, 'CABK', '');"><span>${uiLabelMap.ShippingAddressFPLabel}</span></a></li>
            <li id="shippingMethod" class="current"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            <li id="payment" class="off next"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="off last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
         </ul>
        </#if>
        
        <#if selectedStep?has_content && selectedStep =="payment">
         <ul class="cartFootPrint paymentStep">
            <li id="cart" class="on first"><a href="javascript:submitCheckoutForm(document.${formName!}, 'SCBK', '');"><span>${uiLabelMap.ShoppingCartFPLabel}</span></a></li>
            <li id="shippingAddress" class="on"><a href="javascript:submitCheckoutForm(document.${formName!}, 'CABK', '');"><span>${uiLabelMap.ShippingAddressFPLabel}</span></a></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="on"><a href="javascript:submitCheckoutForm(document.${formName!}, 'SOBK', '');"><span>${uiLabelMap.ShippingMethodFPLabel}</span></a></li>
            </#if>
            <li id="payment" class="current"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="off next last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
         </ul>
        </#if>
        <#if selectedStep?has_content && selectedStep =="confirmation">
         <ul class="cartFootPrint confirmationStep">
            <li id="cart" class="on first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="on"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="on"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="on"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="current last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
         </ul>
        </#if>
        <#if selectedStep?has_content && selectedStep =="onePage">
        	<#-- TODO: Spec out footprint for one page chaeckout -->
         <ul class="cartFootPrint onePageStep">
            <li id="cart" class="on first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="on"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="on"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="current"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="off next last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
         </ul>
        </#if>
    </div>
</#if>
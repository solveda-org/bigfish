 <#if (shoppingCart?has_content && (shoppingCart.size() > 0)) || (showCartFootprint?has_content && showCartFootprint == "Y" )>
    <ul>
        <#if selectedStep?has_content && selectedStep =="cart">
            <li id="cart" class="first">${uiLabelMap.ShoppingCartFPLabel}</li>
            <li id="shippingAddress" class="next">${uiLabelMap.ShippingAddressFPLabel}</li>
            <li id="shippingMethod" class="off">${uiLabelMap.ShippingMethodFPLabel}</li>
            <li id="payment" class="off">${uiLabelMap.PaymentFPLabel}</li>
            <li id="confirmation" class="last">${uiLabelMap.OrderConfirmationFPLabel}</li>
        </#if>
        <#if selectedStep?has_content && selectedStep =="shipping">
            <li id="cart" class="first">${uiLabelMap.ShoppingCartFPLabel}</li>
            <li id="shippingAddress" class="current">${uiLabelMap.ShippingAddressFPLabel}</li>
            <li id="shippingMethod" class="next">${uiLabelMap.ShippingMethodFPLabel}</li>
            <li id="payment" class="off">${uiLabelMap.PaymentFPLabel}</li>
            <li id="confirmation" class="last">${uiLabelMap.OrderConfirmationFPLabel}</li>
        </#if>
        <#if selectedStep?has_content && selectedStep =="shippingMethod">
            <li id="cart" class="first">${uiLabelMap.ShoppingCartFPLabel}</li>
            <li id="shippingAddress" class="on">${uiLabelMap.ShippingAddressFPLabel}</li>
            <li id="shippingMethod" class="current">${uiLabelMap.ShippingMethodFPLabel}</li>
            <li id="payment" class="next">${uiLabelMap.PaymentFPLabel}</li>
            <li id="confirmation" class="last">${uiLabelMap.OrderConfirmationFPLabel}</li>
        </#if>
        <#if selectedStep?has_content && selectedStep =="payment">
            <li id="cart" class="first">${uiLabelMap.ShoppingCartFPLabel}</li>
            <li id="shippingAddress" class="on">${uiLabelMap.ShippingAddressFPLabel}</li>
            <li id="shippingMethod" class="on">${uiLabelMap.ShippingMethodFPLabel}</li>
            <li id="payment" class="current">${uiLabelMap.PaymentFPLabel}</li>
            <li id="confirmation" class="next">${uiLabelMap.OrderConfirmationFPLabel}</li>
        </#if>
        <#if selectedStep?has_content && selectedStep =="confirmation">
            <li id="cart" class="first">${uiLabelMap.ShoppingCartFPLabel}</li>
            <li id="shippingAddress" class="on">${uiLabelMap.ShippingAddressFPLabel}</li>
            <li id="shippingMethod" class="on">${uiLabelMap.ShippingMethodFPLabel}</li>
            <li id="payment" class="on">${uiLabelMap.PaymentFPLabel}</li>
            <li id="confirmation" class="current">${uiLabelMap.OrderConfirmationFPLabel}</li>
        </#if>
    </ul>
</#if>
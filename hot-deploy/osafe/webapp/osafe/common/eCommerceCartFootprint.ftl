 <#if (shoppingCart?has_content && (shoppingCart.size() > 0)) || (showCartFootprint?has_content && showCartFootprint == "Y" )>
    <ul>
    	<#-- Shipping Method will not be displayed when no shipping Applies to cart -->
        <#if selectedStep?has_content && selectedStep =="cart">
            <li id="cart" class="first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="next"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="off"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="off"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
        </#if>
        <#if selectedStep?has_content && selectedStep =="shipping">
            <li id="cart" class="first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="current"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="next"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="<#if shippingApplies?exists && shippingApplies>off<#else>next</#if>"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
        </#if>
        
        <#if selectedStep?has_content && selectedStep =="shippingMethod">
            <li id="cart" class="first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="on"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <li id="shippingMethod" class="current"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            <li id="payment" class="next"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
        </#if>
        
        <#if selectedStep?has_content && selectedStep =="payment">
            <li id="cart" class="first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="on"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="on"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="current"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="next last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
        </#if>
        <#if selectedStep?has_content && selectedStep =="confirmation">
            <li id="cart" class="first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="on"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="on"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="on"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="current last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
        </#if>
        <#if selectedStep?has_content && selectedStep =="onePage">
        	<#-- TODO: Spec out footprint for one page chaeckout -->
            <li id="cart" class="first"><span>${uiLabelMap.ShoppingCartFPLabel}</span></li>
            <li id="shippingAddress" class="on"><span>${uiLabelMap.ShippingAddressFPLabel}</span></li>
            <#if shippingApplies?exists && shippingApplies>
              <li id="shippingMethod" class="on"><span>${uiLabelMap.ShippingMethodFPLabel}</span></li>
            </#if>
            <li id="payment" class="current"><span>${uiLabelMap.PaymentFPLabel}</span></li>
            <li id="confirmation" class="next last"><span>${uiLabelMap.OrderConfirmationFPLabel}</span></li>
        </#if>
    </ul>
</#if>
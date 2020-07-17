<div id="Customerpersonalinfo" class="displayBox" >
    <h3>${uiLabelMap.CustomerPersonalHeading?if_exists}</h3>
     <p class="instructions">${uiLabelMap.CustomerPersonalInfo}</p>
    <p><a id="Customerpersonalinfo" href="<@ofbizUrl>eCommerceEditCustomerInfo</@ofbizUrl>">${uiLabelMap.ClickPersonalDetailsInfo}</a></p>
</div>
<div id="Customerlogininfo" class="displayBox" >
    <h3>${uiLabelMap.CustomerLoginHeading?if_exists}</h3>
     <p class="instructions">${uiLabelMap.CustomerLoginInfo}</p>
    <p><a id="Customerlogininfo" href="<@ofbizUrl>eCommerceEditLoginInfo</@ofbizUrl>">${uiLabelMap.ClickLoginDetailsInfo}</a></p>
</div>
<div id="CustomerAddressBook" class="displayBox">
    <h3>${uiLabelMap.CustomerAddressBookHeading?if_exists}</h3>
    <p class="instructions">${uiLabelMap.CustomerAddressBookInfo}</p>
    <p><a id="CustomerAddressBook" href="<@ofbizUrl>eCommerceEditAddressBook</@ofbizUrl>">${uiLabelMap.ClickAddressBookInfo}</a></p>
</div>
<#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"CHECKOUT_KEEP_PAYMENT_METHODS") && (userLogin?has_content) && !(userLogin.userLoginId == "anonymous")>
    <div id="CustomerPaymentMethodInfo" class="displayBox">
        <h3>${uiLabelMap.PaymentMethodsHeading?if_exists}</h3>
        <p class="instructions">${uiLabelMap.PaymentMethodsInfo}</p>
        <p><a id="CustomerOrderStatus" href="<@ofbizUrl>eCommercePaymentMethodInfo</@ofbizUrl>">${uiLabelMap.ClickViewPaymentMethodInfo}</a></p>
    </div>
</#if>
<div id="CustomerOrderStatus" class="displayBox">
    <h3>${uiLabelMap.CustomerOrderStatusHeading?if_exists}</h3>
    <p class="instructions">${uiLabelMap.CustomerOrderStatusInfo}</p>
    <p><a id="CustomerOrderStatus" href="<@ofbizUrl>eCommerceOrderHistory</@ofbizUrl>">${uiLabelMap.ClickViewOrdersInfo}</a></p>
</div>


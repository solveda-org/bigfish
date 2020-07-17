<!-- TODO : Need formatting -->
<script type="text/javascript">
//<![CDATA[
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; checkout
        form.action="<@ofbizUrl>${doneAction!"multiPageCheckoutOptions"}</@ofbizUrl>";
        form.submit();
    } else if (mode == "BK") {
        // Previous Page
        form.action="<@ofbizUrl>${backAction!"multiPageShippingOptions"}?action=previous</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>updateCheckoutOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "GC") {
        // edit gift card
        form.action="<@ofbizUrl>updateCheckoutOptions/editgiftcard?paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    }else if(mode = "EG")
    //edit gift card
        form.action="<@ofbizUrl>updateCheckoutOptions/editgiftcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
}
//]]>
</script>

<#assign cart = shoppingCart?if_exists />
<script language="JavaScript" type="text/javascript">
<!--
    var clicked = 0;
    function submitOrder() {
        if (clicked == 0) {
            clicked++;
            //window.location.replace("<@ofbizUrl>processorder</@ofbizUrl>");
            document.submitOrderForm.submitOrderBtn.value="${uiLabelMap.SubmittingOrderBtn}";
            document.submitOrderForm.submitOrderBtn.disabled=true;
            document.editcreditcardform.submit();
        } else {
            alert("You order is being processed, this may take a moment.");
        }
    }
    function selectPaymentMethod(paymentType) {
        if (paymentType) {
            window.location="#paymentMethod";
            var ddCardType = document.getElementById("cardType");
            if (ddCardType) {
			    ddCardType.selectedIndex = 0;
				for (var i=0; i < ddCardType.options.length; i++) {
					if (ddCardType.options[i].value == paymentType) {
					      ddCardType.selectedIndex = i;
					      document.getElementById("cardNumber").focus();
					}
				}            
            }
        } else {
            return;
        }
    }

// -->
</script>
<#if cart?exists && 0 &lt; cart.size()>
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#entryFormJS")}
 <!-- DIV for Displaying Order Summary STARTS here -->
    <div class="orderSummary">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderSummaryDivSequence")}
    </div>
<!-- DIV for Displaying Order Summary ENDS here -->  
<#else>
    <h3>${uiLabelMap.OrderErrorShoppingCartEmpty}.</h3>
</#if>

    <form method="post" id="checkoutInfoForm" name="checkoutInfoForm" action="">
    <input type="hidden" id="checkoutpage" name="checkoutpage" value="payment" />
    <input type="hidden" id="BACK_PAGE" name="BACK_PAGE" value="checkoutoptions" />
        <#-- Required So Previous button works -->
    </form>
    <form method="post" id="${formName!"entryForm"}" name="${formName!"entryForm"}">
    </form>

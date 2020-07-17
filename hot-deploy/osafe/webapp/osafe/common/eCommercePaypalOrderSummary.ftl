
<#assign cart = shoppingCart?if_exists />
<script language="JavaScript" type="text/javascript">
<!--
    var clicked = 0;
    function submitOrder() {
        if (clicked == 0) {
            clicked++;
            document.submitOrderForm.submitOrderBtn.value="${uiLabelMap.SubmittingOrderBtn}";
            document.submitOrderForm.submitOrderBtn.disabled=true;
            document.checkoutInfoForm.submit();
        } else {
            alert("You order is being processed, this may take a moment.");
        }
    }
// -->
</script>

    <#if cart?exists && 0 < cart.size()>
    <!-- DIV for Displaying Order Summary STARTS here -->
    <div class="orderSummaryPaypal">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#orderSummaryPayPalDivSequence")}
    </div>
    <!-- DIV for Displaying Order Summary ENDS here -->  
    <#else>
      <h3>${uiLabelMap.OrderErrorShoppingCartEmpty}.</h3>
    </#if>

    <form method="post" id="checkoutInfoForm" name="checkoutInfoForm" action="<@ofbizUrl>processCartAttribute</@ofbizUrl>">
    <input type="hidden" id="checkoutpage" name="checkoutpage" value="payment" />
    <input type="hidden" id="BACK_PAGE" name="BACK_PAGE" value="checkoutoptions" />
        <#-- Required So Previous button works -->
    </form>


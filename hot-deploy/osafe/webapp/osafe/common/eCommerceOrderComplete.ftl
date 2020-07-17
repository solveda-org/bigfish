<#if orderHeader?has_content>
    <div id="checkoutOrderComplete">

     <!-- DIV for Displaying ORDER COMPLETE info STARTS here -->
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xmlcheckoutOrderCompleteDivSequence")}
    <#if showThankYouStatus?exists && showThankYouStatus == "Y">
        <p class="instructions">${OrderCompleteInfoMapped!""}</p>
    </#if>
   ${screens.render("component://osafe/widget/EcommerceContentScreens.xml#PTS_ORDER_DETAIL")}

   <#-- Order Items List -->
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#orderItems")}
    
    <#assign orderAttrPickupStore = orderHeader.getRelatedByAnd("OrderAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("attrName", "STORE_LOCATION"))>
    <#if orderAttrPickupStore?has_content>
        ${screens.render("component://osafe/widget/EcommerceScreens.xml#storeDetail")}
    </#if>

    <#-- Shipping Box -->
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#shippingAddress")}

    <#-- Payment Information -->
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#paymentInformation")}
    </div>

<#else>
  <h3>${uiLabelMap.OrderSpecifiedNotFound}.</h3>
</#if>

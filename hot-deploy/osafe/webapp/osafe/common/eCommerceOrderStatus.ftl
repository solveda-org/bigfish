<#if orderHeader?has_content>
    <div id="orderStatusBlock">
    <#assign FORMAT_DATE = Static["com.osafe.util.Util"].getProductStoreParm(request,"FORMAT_DATE")!""/>


    <#if showThankYouStatus?exists && showThankYouStatus == "Y">
        <p class="instructions">${OrderCompleteInfoMapped!""}</p>
    </#if>

    <#if showThankYouStatus?exists && showThankYouStatus == "N">
        <table id="orderStatusGrid">
            <tr>
                <td id="orderNumberCap" class="caption">${uiLabelMap.OrderOrderNumber}</td>
                <td id="orderNumber">${orderHeader.orderId}</td>
                <td  id="customerCap"class="caption">${uiLabelMap.OrderCustomer}</td>
                <td id="customer">
                <#if localOrderReadHelper?exists && orderHeader?has_content>
                  <#assign displayParty = localOrderReadHelper.getPlacingParty()?if_exists/>
                  <#if displayParty?has_content>
                    <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                  </#if>

                    ${(displayPartyNameResult.fullName)?default("[Name Not Found]")}
                </#if>
                </td>
            </tr>
            <tr>
                <td id="orderStatusCap"class="caption">${uiLabelMap.OrderStatusLabel}</td>
                <td id="orderStatus" >
                <#if orderHeader?has_content>
                <span>${localOrderReadHelper.getCurrentStatusString()}</span>
                <#else>
                <span>${uiLabelMap.OrderNotYetOrdered}</span>
                </#if>
                </td>
                <td id="orderDateCap"class="caption">${uiLabelMap.OrderOrder} ${uiLabelMap.CommonDate}</td>
                <td id="orderDate" >
                    <#if orderHeader?has_content>
                        ${(Static["com.osafe.util.Util"].convertDateTimeFormat(orderHeader.orderDate, FORMAT_DATE))!"N/A"}
                    </#if>
                </td>
            </tr>
        </table>
    </#if>
    </div>

   <#-- Order Items List -->
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#orderItems")}
    
    <#assign orderAttrPickupStore = orderHeader.getRelatedByAnd("OrderAttribute", Static["org.ofbiz.base.util.UtilMisc"].toMap("attrName", "STORE_LOCATION"))>
    <#if orderAttrPickupStore?has_content>
        ${screens.render("component://osafe/widget/EcommerceScreens.xml#storeDetail")}
    </#if>
	
	<#if shippingApplies?exists && shippingApplies>
      <#-- Shipping Box -->
      ${screens.render("component://osafe/widget/EcommerceScreens.xml#shippingAddress")}
	</#if>
	
    <#-- Payment Information -->
    ${screens.render("component://osafe/widget/EcommerceScreens.xml#paymentInformation")}

<#else>
  <h3>${uiLabelMap.OrderSpecifiedNotFound}.</h3>
</#if>

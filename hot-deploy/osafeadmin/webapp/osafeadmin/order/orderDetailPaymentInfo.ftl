<!-- start listBox -->
    <table class="osafe">
        <tr class="heading">
            <th class="dateCol firstCol">${uiLabelMap.DateLabel}</th>
            <th class="nameCol">${uiLabelMap.PaymentTypeLabel}</th>
            <th class="dollarCol">${uiLabelMap.AmountLabel}</th>
            <th class="statusCol lastCol">${uiLabelMap.StatusLabel}</th>
        </tr>
        <#assign currencyUomId = orderReadHelper.getCurrency()>
        <#assign rowClass = "1"/>
        <#if orderReadHelper?has_content>
            <#assign orderHeader = orderReadHelper.getOrderHeader()/>
            <#assign orderHeaderStatusId = orderHeader.statusId />
            <#assign orderItems = orderReadHelper.getOrderItems()/>
            <#assign orderAdjustments = orderReadHelper.getAdjustments()/>
            <#assign orderOpenAmount = orderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments)/>
            <#assign orderPayments = orderReadHelper.getPaymentPreferences()/>
            <#if orderPayments?has_content>
                <tbody>
                    <#list orderPayments as orderPaymentPreference>
                        <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                            <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
                            <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
                            <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
                            <td class="dateCol firstCol">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(orderPaymentPreference.createdDate, preferredDateTimeFormat).toLowerCase())!"N/A"}</td>
                            <#if ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "CREDIT_CARD"))>
                                <#assign creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard")>
                                <#assign cardNumber = creditCard.get("cardNumber")>
                                <#assign cardNumber = cardNumber?substring(cardNumber?length - 4)>
                                <#assign cardNumber = "*" + cardNumber>
                                <td class="nameCol">${paymentMethodType.description?if_exists} (${creditCard.get("cardType")?if_exists}) ${cardNumber} ${creditCard.get("expireDate")?if_exists} <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ViewPaymentDetailsTooltip!}');" onMouseout="hideTooltip()"><span class="paymentDetailIcon"></span></a></td>
                            <#elseif ((paymentMethod?has_content) && (paymentMethod.paymentMethodTypeId == "GIFT_CARD"))>
                                <#assign giftCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("GiftCard")>
                                <td class="nameCol">${paymentMethodType.description?if_exists} (${giftCard.cardNumber}) <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ViewPaymentDetailsTooltip!}');" onMouseout="hideTooltip()"><span class="paymentDetailIcon"></span></a></td>
                            <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD") && isStorePickup?has_content && isStorePickup == "Y")>
                                <td class="nameCol">${uiLabelMap.PayInStoreInfo} <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ViewPaymentDetailsTooltip!}');" onMouseout="hideTooltip()"><span class="paymentDetailIcon"></span></a></td>
                            <#elseif ((orderPaymentPreference?has_content) && (orderPaymentPreference.getString("paymentMethodTypeId") == "EXT_COD"))>
                                <td class="nameCol">${uiLabelMap.CashOnDeliveryInfo} <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ViewPaymentDetailsTooltip!}');" onMouseout="hideTooltip()"><span class="paymentDetailIcon"></span></a></td>
                            <#else>
                                <td class="nameCol">${paymentMethodType.description?if_exists} <a href="<@ofbizUrl>orderPaymentDetail?orderPaymentPreferenceId=${orderPaymentPreferenceId!""}&orderId=${parameters.orderId}</@ofbizUrl>" onMouseover="showTooltip(event,'${uiLabelMap.ViewPaymentDetailsTooltip!}');" onMouseout="hideTooltip()"><span class="paymentDetailIcon"></span></a></td>
                            </#if>
                            <td class="dollarCol"><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                            <td class="statusCol lastCol">${oppStatusItem.get("description","OSafeAdminUiLabels",locale)}</td>
                            <#if orderPaymentPreference.statusId == "PAYMENT_SETTLED">
                              <#assign orderOpenAmount = orderOpenAmount - orderPaymentPreference.maxAmount/>
                            </#if>
                        </tr>

                        <#-- toggle the row color -->
                        <#if rowClass == "2">
                            <#assign rowClass = "1">
                        <#else>
                            <#assign rowClass = "2">
                        </#if>

                        <#assign payments = orderPaymentPreference.getRelated("Payment")?if_exists>
                        <#if payments?has_content>
                            <#list payments as payment>
                                <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                                    <#assign paymentStatusItem = payment.getRelatedOne("StatusItem")>
                                    <#assign paymentType = payment.getRelatedOne("PaymentType")>
                                    <#assign paymentMethodType = payment.getRelatedOne("PaymentMethodType")?if_exists>
                                    <td class="dateCol firstCol">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(payment.effectiveDate, preferredDateTimeFormat).toLowerCase())!"N/A"}</td>
                                    <td class="nameCol">${paymentType.description?if_exists} (${paymentMethodType.description?if_exists}<#if payment.paymentRefNum?has_content> ${payment.paymentRefNum!}</#if>)</td>
                                    <td class="dollarCol"><@ofbizCurrency amount=payment.amount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                                    <td class="statusCol lastCol">${paymentStatusItem.get("description","OSafeAdminUiLabels",locale)}</td>
                                    <#if orderPaymentPreference.statusId == "PAYMENT_NOT_RECEIVED">
	                                    <#if payment.statusId == "PMNT_RECEIVED">
	                                        <#assign orderOpenAmount = orderOpenAmount - payment.amount/>
	                                    </#if>
                                    </#if>
                                </tr>

                                <#-- toggle the row color -->
                                <#if rowClass == "2">
                                    <#assign rowClass = "1">
                                <#else>
                                    <#assign rowClass = "2">
                                </#if>
                            </#list>
                        </#if>
                    </#list>
                    <#if (orderOpenAmount?has_content && orderOpenAmount &gt; 0)>
                        <tr class="dataRow <#if rowClass == "2">even<#else>odd></#if>">
                            <td class="dateCol firstCol">&nbsp;</td>
                            <td class="nameCol total">${uiLabelMap.BalanceDueLabel}</td>
                            <td class="dollarCol total"><@ofbizCurrency amount=orderOpenAmount?default(0.00) isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                            <td class="statusCol lastCol total"><a href="<@ofbizUrl>addPaymentDetail?orderId=${parameters.orderId}</@ofbizUrl>"><span class="paymentIcon"></span></a></td>
                        </tr>
                    </#if>
                </tbody>
            <#else>
                <tbody>
                     ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
                </tbody>
            </#if>
        </#if>
    </table>

<!-- end listBox -->
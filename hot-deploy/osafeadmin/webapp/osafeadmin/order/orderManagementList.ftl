<!-- start listBox -->
            <#assign totOrders = 0/>
            <#assign crrcyUomId = ""/>
            <thead>
            <tr class="heading">
                <th class="idCol firstCol">${uiLabelMap.OrderNoLabel}</th>
                <th class="dateCol">${uiLabelMap.OrderDateLabel}</th>
                <th class="idCol">${uiLabelMap.CustNoLabel}</th>
                <th class="nameCol">${uiLabelMap.CustomerNameLabel}</th>
                <th class="nameCol">${uiLabelMap.EmailAddressLabel}</th>
                <th class="statusCol">${uiLabelMap.OrderStatusLabel}</th>
                <th class="statusCol">${uiLabelMap.ExportStatusLabel}</th>
                <th class="dollarCol">${uiLabelMap.ItemDollarLabel}</th>
                <th class="dollarCol">${uiLabelMap.AdjDollarLabel}</th>
                <th class="dollarCol">${uiLabelMap.ShipDollarLabel}</th>
                <th class="dollarCol">${uiLabelMap.TaxDollarLabel}</th>
                <th class="dollarCol lastCol">${uiLabelMap.OrderDollarLabel}</th>
            </tr>
            </thead>
            <#assign totalItemAmount = 0/>
            <#assign totalShipAmount = 0/>
            <#assign totalTaxAmount = 0/>
            <#assign totalAdjAmount = 0/>
        <#if resultList?exists && resultList?has_content>
            <#assign rowClass = "1">
            <#list resultList as orderHeader>
                <#assign orHeader = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderHeader(delegator,orderHeader.orderId)>
                <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orHeader)>
                <#assign displayParty = orh.getPlacingParty()?if_exists>
                <#assign statusItem = orHeader.getRelatedOneCache("StatusItem")>
                <#assign downloadStatus = "N">
                <#assign orderDownloadAttribute = delegator.findOne("OrderAttribute", {"orderId" : orderHeader.orderId, "attrName" : "IS_DOWNLOADED"}, false)!"" />
                <#if orderDownloadAttribute?has_content>
                  <#assign downloadStatus = orderDownloadAttribute.attrValue!"">
                </#if>
                <#assign orderDeliveryOptionAttr = delegator.findOne("OrderAttribute", {"orderId" : orderHeader.orderId, "attrName" : "DELIVERY_OPTION"}, false)!"" />
                <#assign isStorePickup = "N">
                <#if orderDeliveryOptionAttr?has_content && orderDeliveryOptionAttr.attrValue == "STORE_PICKUP">
                  <#assign isStorePickup = "Y">
                </#if>
                
                <#assign orderAdjustments = orh.getAdjustments()>
                <#assign orderValidItems = orh.getOrderItems()/>
                <#assign orderHeaderAdjustments = orh.getOrderHeaderAdjustments()>
                <#assign orderSubTotal = orh.getOrderItemsSubTotal(orderValidItems,orderAdjustments)/>
                <#assign totalItemAmount = totalItemAmount + orderSubTotal />
                <#assign currencyUomId = orh.getCurrency()>
                <#assign orderAdjustmentAmount = orh.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, true, false, false)> 
                <#assign totalAdjAmount = totalAdjAmount + orderAdjustmentAmount />
                <#assign shippingAmount = orh.calcOrderAdjustments(orderAdjustments, orderSubTotal, false, false, true)> 
                <#assign totalShipAmount = totalShipAmount + shippingAmount />
                <#assign taxAmount = orh.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal!"0.00">
                <#assign totalTaxAmount = totalTaxAmount + taxAmount />
                <#assign grandTotal = orh.getOrderGrandTotal(orderValidItems, orderAdjustments)>
                <#assign orderItemAmount = orderSubTotal/>


                <#assign orderEmail=""/>
				<#if orderSearchEmail?exists && orderSearchEmail?has_content>
					<#assign orderEmail=orderSearchEmail/>
				<#else>
	                <#assign orderEmailContactMechs = orh.getOrderContactMechs("ORDER_EMAIL")!""/>
	                <#if orderEmailContactMechs?has_content>
	                  <#assign orderEmail = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(orderEmailContactMechs)/>
	                  <#assign orderEmailContactMech = orderEmail.getRelatedOne("ContactMech")/>
				      <#assign orderEmail=orderEmailContactMech.infoString!""/>
				    </#if>
				</#if>

                <tr class="dataRow <#if rowClass == "2">even<#else>odd</#if>">
                    <td class="idCol <#if !orderHeader_has_next>lastRow</#if> firstCol" >
                        <a href="<@ofbizUrl>orderDetail?orderId=${orderHeader.orderId}</@ofbizUrl>">${orderHeader.orderId}</a>
                        <#if isStorePickup?has_content  && isStorePickup == "Y">
                            <span class="pickupStoreIndicator"></span>
                        </#if>
                    </td>
                    <td class="dateCol <#if !orderHeader_has_next>lastRow</#if>">${orderHeader.getTimestamp("orderDate")?string(preferredDateFormat)}</td>
                    <td class="idCol <#if !orderHeader_has_next>lastRow</#if>">
                        <#if displayParty?has_content>
                            <a href="<@ofbizUrl>customerDetail?partyId=${displayParty.partyId}</@ofbizUrl>">${displayParty.partyId}</a>
                        </#if>
                    </td>
                    <td class="nameCol <#if !orderHeader_has_next>lastRow</#if>">
                        <#if displayParty?has_content>
                            <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "lastNameFirst","Y", "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                            ${displayPartyNameResult.fullName?default("[${uiLabelMap.PartyNameNotFoundInfo}]")}
                        <#else>
                            ${uiLabelMap.NAInfo}
                        </#if>
                    </td>
                    <td class="nameCol <#if !orderHeader_has_next>lastRow</#if>">
                        <#if orderEmail?has_content>
                            ${orderEmail!}
                        <#else>
                            ${uiLabelMap.NAInfo}
                        </#if>
                    </td>
                    <td class="statusCol <#if !orderHeader_has_next>lastRow</#if>">${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}</td>
                    <td class="statusCol <#if !orderHeader_has_next>lastRow</#if>">
                      <#if downloadStatus?has_content  && downloadStatus == "Y">
                        ${uiLabelMap.ExportStatusInfo}
                      <#else>
                        ${uiLabelMap.DownloadNewInfo}
                      </#if>
                    </td>
                    <td class="dollarCol">
                            <@ofbizCurrency amount=orderItemAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                    </td>
                    <td class="dollarCol"><#if (orderAdjustmentAmount !=0)><@ofbizCurrency amount=orderAdjustmentAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/></#if></td>
                    <td class="dollarCol"><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                    <td class="dollarCol"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/></td>
                    <#assign totOrders = totOrders + grandTotal />
                    <#assign crrcyUomId = orh.getCurrency() />
                    <td class="dollarCol <#if !orderHeader_has_next>lastRow</#if> lastCol"><@ofbizCurrency amount=grandTotal isoCode=orh.getCurrency() rounding=globalContext.currencyRounding/></td>
                </tr>

                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
            </#list>
            <tfoot>
            <tr class="ListTotal">
              <td colspan="13">
                <table class="osafe orderListTotal autoWidth">
                    <tr>
                      <td class="totalCaption total"><label>${uiLabelMap.ItemsTotalDollarCaption}</label></td>
                      <td class="totalValue total">
                        <@ofbizCurrency amount=totalItemAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                      </td>
                    </tr>
                    <tr>
                      <td class="totalCaption total"><label>${uiLabelMap.AdjTotalDollarCaption}</label></td>
                      <td class="totalValue total">
                        <@ofbizCurrency amount=totalAdjAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                      </td>
                    </tr>
                    <tr>
                      <td class="totalCaption total"><label>${uiLabelMap.ShipTotalDollarCaption}</label></td>
                      <td class="totalValue total">
                        <@ofbizCurrency amount=totalShipAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                      </td>
                    </tr>
                    <#if (!Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmTrue(request,"CHECKOUT_SUPPRESS_TAX_IF_ZERO")) || (totalTaxAmount?has_content && (totalTaxAmount &gt; 0))>
                      <tr>
                        <td class="totalCaption total"><label>${uiLabelMap.TaxTotalDollarCaption}</label></td>
                        <td class="totalValue total">
                          <@ofbizCurrency amount=totalTaxAmount isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                        </td>
                      </tr>
                    </#if>
                    <tr>
                      <td class="totalCaption total"><label>${uiLabelMap.ListTotalCaption}</label></td>
                      <td class="totalValue total">
                        <@ofbizCurrency amount=totOrders isoCode=currencyUomId rounding=globalContext.currencyRounding/>
                      </td>
                    </tr>
                </table>
              </td>
           </tr>
           </tfoot>
        </#if>
<!-- end listBox -->
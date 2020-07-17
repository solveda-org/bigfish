

    <table id="orderSalesHistory" class="standardTable" summary="This table display order sales history.">
      <thead>
        <tr>
          <th class="orderNumber firstCol">${uiLabelMap.OrderOrderNumber}</th>
          <th class="orderDate">${uiLabelMap.OrderDate}</th>
          <th class="orderStatus">${uiLabelMap.CommonStatus}</th>
          <th class="totalPrice lastCol">${uiLabelMap.CommonTotalPrice}</th>
        </tr>
      </thead>
      <tbody>
        <#if orderHeaderList?has_content>
          <#list orderHeaderList as orderHeader>
            <#assign status = orderHeader.getRelatedOneCache("StatusItem") />
            <tr>
              <td class="orderNumber firstCol">
                    <a href="<@ofbizUrl>eCommerceOrderStatus?orderId=${orderHeader.orderId}</@ofbizUrl>">${orderHeader.orderId}</a>
              </td>
              <td class="orderDate">${(Static["com.osafe.util.Util"].convertDateTimeFormat(orderHeader.orderDate, FORMAT_DATE))!"N/A"}</td>
              <td class="orderStatus">${status.get("description",locale)}</td>
              <td class="totalPrice total lastCol"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom /></td>
            </tr>
          </#list>
        <#else>
          <tr><td colspan="4">${uiLabelMap.OrderNoOrderFound}</td></tr>
        </#if>
      </tbody>
    </table>

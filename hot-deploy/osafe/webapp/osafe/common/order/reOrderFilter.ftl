<div class="${request.getAttribute("attributeClass")!}">
  <form id="reOrderItemSearchForm" name="reOrderItemSearchForm" action="/online/control/" method="post">
	<div class="displayBox reOrderFilter">
	    <ul class="displayActionList reOrderFilter">
	      <li>
	       <div>
	        <label>${uiLabelMap.FilterByOrderCaption}</label>
	        <select name="orderId" id="orderId" onChange="javascript:findOrderItems(this);">
             <option value="">${uiLabelMap.AllOrdersLabel}</option>
             <#if customerOrderList?has_content>
              <#list customerOrderList as customerOrder>
                <option value = "${customerOrder.orderId}" <#if parameters.orderId?exists && parameters.orderId == customerOrder.orderId>Selected</#if>>
                  ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels", "OrderPlacedOnDateLabel", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", customerOrder.orderId, "orderDate", Static["com.osafe.util.Util"].convertDateTimeFormat(customerOrder.orderDate, preferredDateFormat)), locale)!}
                </option>
              </#list>
             </#if>
           </select>
	       </div>
	      </li>
	    </ul>
	</div>
  </form>
</div>

<#if shippingApplies?exists && shippingApplies>
 <#if (!deliveryOption?has_content || deliveryOption != "SHIP_TO_MULTI")>
  <li class="${request.getAttribute("attributeClass")!}">
	<div>
	  <label>${uiLabelMap.CartShippingMethodLabel}</label>
	  <span>${chosenShippingMethodDescription!}</span>
	</div>
  </li>
 </#if>
</#if>
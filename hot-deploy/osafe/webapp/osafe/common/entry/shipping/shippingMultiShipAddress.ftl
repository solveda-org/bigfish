<#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request)! />  
<#if shoppingCart?has_content >
  <#assign totalQty = shoppingCart.getTotalQuantity()!"0" />
	<#if (userLogin?has_content && userLogin.userLoginId == "anonymous") && (totalQty > 1)>
	 <#if multiAddressUrl?exists && multiAddressUrl?has_content>
		<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
		<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
		<div class="${attributeClass!request.getAttribute("attributeClass")!}">
		        <label>&nbsp;</label>
		        <div class="entryField">
		          <a href="<@ofbizUrl>${multiAddressUrl?if_exists}</@ofbizUrl>" title="${uiLabelMap.ShipToMultiAddressLabel}"><span>${uiLabelMap.ShipToMultiAddressLabel}</span></a>
		        </div>
		</div>
	 </#if>
	</#if>
</#if>	
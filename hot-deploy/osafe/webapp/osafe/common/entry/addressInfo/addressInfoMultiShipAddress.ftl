<#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request)! />  
<#if shoppingCart?has_content >
  <#assign totalQty = shoppingCart.getTotalQuantity()!"0" />
	<#if (userLogin?has_content && userLogin.userLoginId != "anonymous") && (totalQty > 1)>
	 <#if multiAddressUrl?exists && multiAddressUrl?has_content>
		<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
		<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
		<div class="${request.getAttribute("attributeClass")!}">
		        <label>&nbsp;</label>
		        <div class="entryField">
                 <#if multiAddressAction?exists && multiAddressAction?has_content>
    	              <a href="javascript:submitCheckoutForm(document.${formName!}, 'MAU', '')" title="${uiLabelMap.ShipToMultiAddressLabel}"><span>${uiLabelMap.ShipToMultiAddressLabel}</span></a>
   	             <#else>
		          <a href="<@ofbizUrl>${multiAddressUrl?if_exists}</@ofbizUrl>" title="${uiLabelMap.ShipToMultiAddressLabel}"><span>${uiLabelMap.ShipToMultiAddressLabel}</span></a>
		         </#if>
		        </div>
		</div>
	 </#if>
	</#if>
</#if>	
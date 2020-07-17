<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<#if fieldPurpose?has_content && context.get(fieldPurpose+"PostalAddress")?has_content>
  <#assign postalAddressData = context.get(fieldPurpose+"PostalAddress") />
</#if>
<#if postalAddressData?has_content>
    <#assign postalAddressContactMechId = postalAddressData.contactMechId!"" />
</#if>
<div class="${request.getAttribute("attributeClass")!}">
	<#if showAddressSelection?has_content && showAddressSelection == "Y">
	    <#if fieldPurpose?has_content && context.get(fieldPurpose+"ContactMechList")?has_content>
	      <#assign contactMechList = context.get(fieldPurpose+"ContactMechList") />
	    </#if>
	    <#if contactMechList?has_content>
	        <div class="entry radioOption">
	          <label for="${fieldPurpose?if_exists}_ADDRESSES">${uiLabelMap.SelectAddressCaption}</label>
	          <#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request) />
	          <#assign  selectedAddress = parameters.get(fieldPurpose+"_SELECT_ADDRESS")!postalAddressContactMechId!""/>
	          <#list contactMechList as contactMech>
	              <#if contactMech.contactMechTypeId?if_exists = "POSTAL_ADDRESS">
	                  <#assign postalAddress=contactMech.getRelatedOneCache("PostalAddress")!"">
	                  <#if postalAddress?has_content>
	                      <label class="radioOptionLabel">
	                      <input type="radio" id="js_${fieldPurpose?if_exists}_SELECT_ADDRESS" class="${fieldPurpose?if_exists}_SELECT_ADDRESS" name="${fieldPurpose?if_exists}_SELECT_ADDRESS" value="${postalAddress.contactMechId!}" onchange="javascript:getPostalAddress('${postalAddress.contactMechId!}', '${fieldPurpose?if_exists}');"<#if selectedAddress == postalAddress.contactMechId >checked="checked"</#if>/>
                          <span class="radioOptionText">
						    ${setRequestAttribute("PostalAddress", postalAddress)}
						    ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_NICKNAME")}
						    ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
						  </span>
						  </label>
	                  </#if>
	              </#if>
	          </#list>
	          <a href="javascript:submitCheckoutForm(document.${formName!}, 'NA', '${fieldPurpose?if_exists}_LOCATION');" class="standardBtn action">${uiLabelMap.AddAddressBtn}</a>
	        </div>
	    <#else>
	        <div class="entry addressSelection">
	          <label for="${fieldPurpose?if_exists}_ADDRESSES">${uiLabelMap.SelectAddressCaption}</label>
	          <a href="javascript:submitCheckoutForm(document.${formName!}, 'NA', '${fieldPurpose?if_exists}_LOCATION');" class="standardBtn action">${uiLabelMap.AddAddressBtn}</a>
	        </div>
	    </#if>
	    <@fieldErrors fieldName="${fieldPurpose?if_exists}_SELECT_ADDRESS"/>
	</#if>
</div>

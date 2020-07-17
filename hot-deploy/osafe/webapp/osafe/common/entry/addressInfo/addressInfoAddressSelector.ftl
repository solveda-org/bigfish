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
             <#assign  selectedAddress = parameters.get(fieldPurpose+"_SELECT_ADDRESS")!postalAddressContactMechId!""/>
             <#assign checkoutAddressStyle = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_ADDRESS_STYLE")!"RADIOBUTTON"/>
	         <#if "DROPDOWN" == checkoutAddressStyle.toUpperCase()>
		         <div class="entry selectOption">
			       <label for="${fieldPurpose?if_exists}_ADDRESSES">${uiLabelMap.SelectAddressCaption}</label>
	               <select name="${fieldPurpose?if_exists}_SELECT_ADDRESS" onchange="javascript:getPostalAddress(this.options[this.selectedIndex].value,'${fieldPurpose?if_exists}');">
			         <#list contactMechList as contactMech>
				        <#assign postalAddress = contactMech.getRelatedOneCache("PostalAddress")>
				        <#if selectedAddress?has_content>
			                <#assign chosenShippingContactMechId= selectedAddress/>
			            <#else>
			  	           <#if !chosenShippingContactMechId?has_content>
			                <#assign chosenShippingContactMechId= postalAddress.contactMechId/>
			               </#if>
				        </#if>
					    ${setRequestAttribute("PostalAddress", postalAddress)}
					    ${setRequestAttribute("DISPLAY_FORMAT", "SINGLE_LINE_NICKNAME")}
	                     <option value="${postalAddress.contactMechId}" <#if chosenShippingContactMechId == postalAddress.contactMechId> selected </#if>>${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}</option>
				     </#list>
				   </select>
		           <a href="javascript:submitCheckoutForm(document.${formName!}, 'NA', '${fieldPurpose?if_exists}_LOCATION');" class="standardBtn action"><span>${uiLabelMap.AddAddressBtn}</span></a>
	             </div>
	         <#else>
			        <div class="entry radioOption">
			          <label for="${fieldPurpose?if_exists}_ADDRESSES">${uiLabelMap.SelectAddressCaption}</label>
			          <#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request) />
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
			          <a href="javascript:submitCheckoutForm(document.${formName!}, 'NA', '${fieldPurpose?if_exists}_LOCATION');" class="standardBtn action"><span>${uiLabelMap.AddAddressBtn}</span></a>
			        </div>
	         </#if>
	    <#else>
	        <div class="entry addressSelection">
	          <label for="${fieldPurpose?if_exists}_ADDRESSES">${uiLabelMap.SelectAddressCaption}</label>
	          <a href="javascript:submitCheckoutForm(document.${formName!}, 'NA', '${fieldPurpose?if_exists}_LOCATION');" class="standardBtn action"><span>${uiLabelMap.AddAddressBtn}</span></a>
	        </div>
	    </#if>
	    <@fieldErrors fieldName="${fieldPurpose?if_exists}_SELECT_ADDRESS"/>
	</#if>
</div>

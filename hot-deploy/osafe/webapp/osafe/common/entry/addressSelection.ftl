<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign contactMechList = request.getAttribute("contactMechList")!""/>
<#assign addressDisplayFormat=request.getAttribute("DISPLAY_FORMAT")!""/>
<#assign addressSelectionInputName=request.getAttribute("addressSelectionInputName")!""/>
<#assign addressSelectionCaption=request.getAttribute("addressSelectionCaption")!""/>
<#assign addAddressAction=request.getAttribute("addAddressAction")!""/>
<#assign paymentMethodId=request.getAttribute("paymentMethodId")!""/>
<#assign selectedContactMechId=request.getAttribute("selectedContactMechId")!""/>
<#assign addressFieldPurpose=request.getAttribute("addressFieldPurpose")!""/>
<#if contactMechList?has_content>
     <#assign  selectedAddress = parameters.get("contactMechId")!selectedContactMechId!""/>
     <#assign checkoutAddressStyle = Static["com.osafe.util.Util"].getProductStoreParm(request,"CHECKOUT_ADDRESS_STYLE")!"RADIOBUTTON"/>
     <#if "DROPDOWN" == checkoutAddressStyle.toUpperCase()>
         <div class="entry selectOption">
          <#if addressSelectionCaption?has_content>
	       <label for="${addressSelectionInputName!""}">${addressSelectionCaption!}</label>
	      </#if>
           <select name="${addressSelectionInputName!""}">
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
			    ${setRequestAttribute("DISPLAY_FORMAT", addressDisplayFormat)}
                 <option value="${postalAddress.contactMechId}" <#if chosenShippingContactMechId == postalAddress.contactMechId> selected </#if>>${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}</option>
		     </#list>
		   </select>
	          <#if addAddressAction?has_content>
                 <a href="<@ofbizUrl>${addAddressAction!""}?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=${addressFieldPurpose?if_exists}_LOCATION&DONE_PAGE=${addAddressDonePage!""}<#if paymentMethodId?has_content>&paymentMethodId=${paymentMethodId}</#if></@ofbizUrl>" class="standardBtn action"><span>${uiLabelMap.AddAddressBtn}</span></a>
              </#if>
         </div>
     <#else>
	        <div class="entry radioOption">
              <#if addressSelectionCaption?has_content>
	              <label for="${addressSelectionInputName!""}">${addressSelectionCaption!}</label>
	          </#if>
	          <#list contactMechList as contactMech>
	              <#if contactMech.contactMechTypeId?if_exists = "POSTAL_ADDRESS">
	                  <#assign postalAddress=contactMech.getRelatedOneCache("PostalAddress")!"">
				      <#if selectedAddress?has_content>
			                <#assign chosenShippingContactMechId= selectedAddress/>
			            <#else>
			  	           <#if !chosenShippingContactMechId?has_content>
			                <#assign chosenShippingContactMechId= postalAddress.contactMechId/>
			               </#if>
				      </#if>
	                  <#if postalAddress?has_content>
	                      <label class="radioOptionLabel">
	                      <input type="radio" class="${addressFieldPurpose?if_exists}_SELECT_ADDRESS" name="${addressSelectionInputName!""}" value="${postalAddress.contactMechId!}" <#if chosenShippingContactMechId == postalAddress.contactMechId >checked="checked"</#if>/>
                          <span class="radioOptionText">
						    ${setRequestAttribute("PostalAddress", postalAddress)}
						    ${setRequestAttribute("DISPLAY_FORMAT", addressDisplayFormat)}
						    ${screens.render("component://osafe/widget/CommonScreens.xml#displayPostalAddress")}
						  </span>
						  </label>
	                  </#if>
	              </#if>
	          </#list>
	          <#if addAddressAction?has_content>
                 <a href="<@ofbizUrl>${addAddressAction!""}?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=${addressFieldPurpose?if_exists}_LOCATION&DONE_PAGE=${addAddressDonePage!""}<#if paymentMethodId?has_content>&paymentMethodId=${paymentMethodId}</#if></@ofbizUrl>" class="standardBtn action"><span>${uiLabelMap.AddAddressBtn}</span></a>
	          </#if>
	        </div>
     </#if>
<#else>
    <#if addAddressAction?has_content>
	    <div class="entry addressSelection">
          <#if addressSelectionCaption?has_content>
	              <label for="${addressSelectionInputName!""}">${addressSelectionCaption!}</label>
           </#if>
	        <a href="<@ofbizUrl>${addAddressAction!""}?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=${addressFieldPurpose?if_exists}_LOCATION&DONE_PAGE=${addAddressDonePage!""}<#if paymentMethodId?has_content>&paymentMethodId=${paymentMethodId}</#if></@ofbizUrl>" class="standardBtn action"><span>${uiLabelMap.AddAddressBtn}</span></a>
	    </div>
    </#if>
</#if>

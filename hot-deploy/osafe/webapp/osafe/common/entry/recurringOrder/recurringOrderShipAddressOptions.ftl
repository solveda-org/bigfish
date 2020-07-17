<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign selectedContactMechId = ""/>
<#if parameters.shippingContactMech?exists && parameters.shippingContactMech?has_content>
  <#assign selectedContactMechId = parameters.shippingContactMech/>
<#else>
  <#if contactMechId?exists && contactMechId?has_content>
    <#assign selectedContactMechId = contactMechId/>
  </#if>
</#if>
<div class="${request.getAttribute("attributeClass")!}">
  <label>${uiLabelMap.CartItemShipAddressCaption}</label>
  <div class="entryField">
    <select name="shippingContactMech" id="shippingContactMech" class="shippingContactMech">
     <#if selectedContactMechId?has_content>
       <#assign postalAddress = delegator.findByPrimaryKeyCache("PostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", selectedContactMechId))?if_exists /> 
       <#if postalAddress?has_content>
       	<option value="${postalAddress.contactMechId!}" selected=selected>${postalAddress.address1!}</option>
       </#if>
     </#if>
     <#list partyShippingLocations as partyShippingLocation>
     	<#assign address = partyShippingLocation.getRelatedOneCache("PostalAddress")/>
        <option value="${address.contactMechId!}">${address.address1!}</option>
     </#list>
    </select>
    <@fieldErrors fieldName="shippingContactMech"/>
  </div>
</div>

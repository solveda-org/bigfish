<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>

<#if contactMech?has_content>
    <#assign contactMechId=contactMech.contactMechId!"">
    <input type="hidden" name="contactMechId" value="${contactMechId!""}"/>
</#if>
<#if postalAddressData?has_content>
    <#assign attnName=postalAddressData.attnName!"">
</#if>
<input type="hidden" name="formRequestName" value="${formRequestName!requestParameters.formRequestName!}"/>
<input type="hidden" name="contactMechTypeId" value="${requestParameters.contactMechTypeId!"POSTAL_ADDRESS"}" />
<input type="hidden" name="contactMechPurposeTypeId" value="${requestParameters.contactMechPurposeTypeId!"SHIPPING_LOCATION"}"/>
<input type="hidden" name="DONE_PAGE" value="${requestParameters.DONE_PAGE!}"/>
<input type="hidden" name="checkoutpage" value="${requestParameters.checkoutpage!}"/>
<input type="hidden" name="paymentMethodId" value="${requestParameters.paymentMethodId!}"/>
<#if requestParameters.shoppingListId?exists && requestParameters.shoppingListId?has_content>
	<input type="hidden" name="shoppingListId" value="${requestParameters.shoppingListId!}"/>
</#if>
<div id="js_${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
</div>
<div class="container button">
    <a class="standardBtn negative" href="<@ofbizUrl>${donePage!}</@ofbizUrl>">${uiLabelMap.CommonBack}</a>
    <a class="standardBtn action" href="javascript:$('${formName!"entryForm"}').submit()">${formContinueButton!uiLabelMap.SaveBtn}</a>
</div>


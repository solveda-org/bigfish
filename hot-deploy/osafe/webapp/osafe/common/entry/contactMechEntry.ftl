<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<input type="hidden" name="formRequestName" value="${formRequestName!requestParameters.formRequestName}"/>
<input type="hidden" name="contactMechTypeId" value='POSTAL_ADDRESS' />
<input type="hidden" name="contactMechPurposeTypeId" value="SHIPPING_LOCATION"/>
<input type="hidden" name="DONE_PAGE" value="${requestParameters.DONE_PAGE}"/>
<input type="hidden" name="checkoutpage" value="${requestParameters.checkoutpage}"/>
<#if contactMech?exists>
    <input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
</#if>
<div id="${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
</div>
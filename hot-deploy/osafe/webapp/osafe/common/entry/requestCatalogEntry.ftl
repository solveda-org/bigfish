<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<input type="hidden" name="partyIdFrom" value="${(userLogin.partyId)?if_exists}" />
<input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
<input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS" />
<input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI" />
<input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
<input type="hidden" name="productStoreName"  value="${productStore.storeName}" />
<input type="hidden" name="emailType" value="REQCAT_NOTI_EMAIL" />
<input type="hidden" name="custRequestTypeId" value="${custRequestTypeId!""}" />
<input type="hidden" name="custRequestName" value="${custRequestName!""}" />
<input type="hidden" name="note" value="${Static["org.ofbiz.base.util.UtilHttp"].getFullRequestUrl(request).toString()}" />
<div id="${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
    <div class="entry">
        <label for="content">${uiLabelMap.CommentCaption}</label>
        <textarea name="content" id="content" cols="50" rows="5" class="content">${parameters.content!""}</textarea>
        <div class="entry">
            <label for="content">&nbsp;</label>
            <span class="textCounter" id="textCounter"></span>
        </div>
        <@fieldErrors fieldName="content"/>
    </div>
</div>
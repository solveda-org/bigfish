<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery('#REQ_CATALOG_COUNTRY')) {
            if(!jQuery('#REQ_CATALOG_STATE_LIST_FIELD').length) {
                getAssociatedStateList('REQ_CATALOG_COUNTRY', 'REQ_CATALOG_STATE', 'advice-required-REQ_CATALOG_STATE', 'REQ_CATALOG_STATES', 'REQ_CATALOG_STATE_TEXT');
            }
            getAddressFormat("REQ_CATALOG");
            jQuery('#REQ_CATALOG_COUNTRY').change(function(){
                getAssociatedStateList('REQ_CATALOG_COUNTRY', 'REQ_CATALOG_STATE', 'advice-required-REQ_CATALOG_STATE', 'REQ_CATALOG_STATES', 'REQ_CATALOG_STATE_TEXT');
                getAddressFormat("REQ_CATALOG");
            });
        }
    });
</script>
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
<div id="REQ_CATALOG_ADDRESS_ENTRY" class="displayBox">

    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>

    <div class="entry">
        <label for="REQ_CATALOG_HOME_CONTACT">${uiLabelMap.ContactPhoneCaption}</label>
        <span class="REQ_CATALOG_USA REQ_CATALOG_CAN">
            <input type="text" class="phone3" id="REQ_CATALOG_HOME_AREA" name="REQ_CATALOG_HOME_AREA" maxlength="3" value="${requestParameters.get("REQ_CATALOG_HOME_AREA")!areaCodeHome!""}" />
            <input type="hidden" id="REQ_CATALOG_HOME_CONTACT" name="REQ_CATALOG_HOME_CONTACT" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT")!contactNumberHome!""}"/>
            <input type="hidden" id="REQ_CATALOG_HOME_REQUIRED" name="REQ_CATALOG_HOME_REQUIRED" value="true"/>
            <input type="text" class="phone3" id="REQ_CATALOG_HOME_CONTACT3" name="REQ_CATALOG_HOME_CONTACT3" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT3")!contactNumber3Home!""}" maxlength="3" />
            <input type="text" class="phone4" id="REQ_CATALOG_HOME_CONTACT4" name="REQ_CATALOG_HOME_CONTACT4" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT4")!contactNumber4Home!""}" maxlength="4" />
        </span>
        <span style="display:none" class="REQ_CATALOG_OTHER">
            <input type="text" class="address" id="REQ_CATALOG_HOME_CONTACT_OTHER" name="REQ_CATALOG_HOME_CONTACT_OTHER" value="${requestParameters.get("REQ_CATALOG_HOME_CONTACT_OTHER")!contactNumberHome!""}" />
        </span>
        <@fieldErrors fieldName="REQ_CATALOG_HOME_AREA"/>
        <@fieldErrors fieldName="REQ_CATALOG_HOME_CONTACT"/>
    </div>

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
<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery('#js_REQ_CATALOG_COUNTRY')) {
            if(!jQuery('#REQ_CATALOG_STATE_LIST_FIELD').length) {
                getAssociatedStateList('js_REQ_CATALOG_COUNTRY', 'js_REQ_CATALOG_STATE', 'advice-required-REQ_CATALOG_STATE', 'REQ_CATALOG_STATES');
            }
            getAddressFormat("REQ_CATALOG");
            jQuery('#js_REQ_CATALOG_COUNTRY').change(function(){
                getAssociatedStateList('js_REQ_CATALOG_COUNTRY', 'js_REQ_CATALOG_STATE', 'advice-required-REQ_CATALOG_STATE', 'REQ_CATALOG_STATES');
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
<input type="hidden" name="productStoreName"  value="${productStore.storeName}" />
<input type="hidden" name="emailType" value="REQCAT_NOTI_EMAIL" />
<input type="hidden" name="custRequestTypeId" value="${custRequestTypeId!""}" />
<input type="hidden" name="custRequestName" value="${custRequestName!""}" />
<input type="hidden" name="note" value="${Static["org.ofbiz.base.util.UtilHttp"].getFullRequestUrl(request).toString()}" />
<div id="js_REQ_CATALOG_ADDRESS_ENTRY" class="displayBox">

  ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#requestCatalogDivSequence")}

</div>
<script type="text/javascript">
    jQuery(document).ready(function () {
        if (jQuery('#${fieldPurpose?if_exists}_country')) {
            if(!jQuery('#${fieldPurpose?if_exists}_StateListExist').length) {
                getAssociatedStateList('${fieldPurpose?if_exists}_country', '${fieldPurpose?if_exists}_state', '${fieldPurpose?if_exists}_STATES', '${fieldPurpose?if_exists}_STATE_TEXT');
            }
            getAddressFormat("${fieldPurpose?if_exists}");
            jQuery('#${fieldPurpose?if_exists}_country').change(function(){
                getAssociatedStateList('${fieldPurpose?if_exists}_country', '${fieldPurpose?if_exists}_state', '${fieldPurpose?if_exists}_STATES', '${fieldPurpose?if_exists}_STATE_TEXT');
                getAddressFormat("${fieldPurpose?if_exists}");
            });
        }
        if (jQuery('#billing_addressEntry').length && jQuery('#shipping_addressEntry').length && jQuery('#isSameAsBilling').length && jQuery('#shipping_addressSection').length) {
          copyAddress('billing', jQuery('#billing_addressEntry'), 'shipping', jQuery('#shipping_addressSection'), jQuery('#isSameAsBilling'), true);
        }
    });
</script>
<#if postalAddress?has_content>
    <#assign contactMechId = postalAddress.contactMechId!"">
</#if>
<input type="hidden" name="contactMechId" id="contactMechId" value="${parameters.contactMechId!contactMechId!""}"/>
${screens.render("component://osafeadmin/widget/AdminDivScreens.xml#addressInfoDivSequence")}
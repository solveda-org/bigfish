
<script language="javascript" type="text/javascript">
//<![CDATA[
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; checkout
        form.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "BK") {
        // Previous Page
        form.action="<@ofbizUrl>eCommerceShippingAddress?action=previous</@ofbizUrl>";
        form.submit();
    }else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>updateCheckoutOptions/eCommerceShowcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutshippingaddress&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutoptions&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    }
}

//]]>
</script>

<form method="post" name="checkoutInfoForm">
<#include "component://osafe/webapp/osafe/common/eCommerceCheckoutShippingOptions.ftl"/>
<#--    <input type="hidden" name="checkoutpage" value="shippingoptions"/>
    <div class="shippingOptions">
            <div id="shippingOptionDisplay" class="displayBox">
             <div class="displayBoxHeader">
                <span class="displayBoxHeaderCaption">${uiLabelMap.ShippingOptionHeading}</span>
             </div>
             <#if chosenShippingMethod?has_content && chosenShippingMethod.equals("NO_SHIPPING@_NA_")>
                 <#assign chosenShippingMethod = "">
             </#if>
                <div class="shippingMethodsContainer">
                  <#list carrierShipmentMethodList as carrierMethod>
                     <#assign shippingMethod = carrierMethod.shipmentMethodTypeId + "@" + carrierMethod.partyId>
                     <#assign findCarrierShipmentMethodMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("shipmentMethodTypeId", carrierMethod.shipmentMethodTypeId, "partyId", carrierMethod.partyId,"roleTypeId" ,"CARRIER")>
                     <#assign carrierShipmentMethod = delegator.findByPrimaryKeyCache("CarrierShipmentMethod", findCarrierShipmentMethodMap)>
                        <div>
                            <fieldset class="col">
                            <div class="entry radioOption">
                                <input type="radio" name="shipping_method" value="${shippingMethod}" <#if (StringUtil.wrapString(shippingMethod) == StringUtil.wrapString(chosenShippingMethod!"")) || (!chosenShippingMethod?has_content && carrierMethod_index == 0)>checked="checked"</#if> />
                                <#if shoppingCart.getShippingContactMechId()?exists>
                                    <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierMethod)?default(-1)>
                                </#if>
                                <span class="radioOptionText"> <#-- use margin left -->
                                <#--    <#if carrierMethod.partyId != "_NA_">
                                     <#assign carrierParty = carrierShipmentMethod.getRelatedOne("Party")/>
                                     <#assign carrierPartyGroup = carrierParty.getRelatedOne("PartyGroup")/>
                                    ${carrierPartyGroup.groupName?if_exists}&nbsp;</#if>${carrierMethod.description?if_exists}

                                    <#if carrierShipmentMethod.optionalMessage?has_content> - ${carrierShipmentMethod.optionalMessage}</#if>
                                </span>
                                <span class="radioOptionTextAdditional"><#if shippingEst?has_content> <#if (shippingEst > -1)><@ofbizCurrency amount=shippingEst isoCode=CURRENCY_UOM_DEFAULT!shoppingCart.getCurrency()/><#else>${uiLabelMap.OrderCalculatedOffline}</#if></#if></span>
                            </div>
                            </fieldset>
                        </div>
                  </#list>
                </div>
            </div>
    </div>
    <#-- Fileds that were on the original page, giving default values -->
 <#--   <input type="hidden" name="may_split" value="N"/>
    <input type="hidden" name="shipping_instructions" value=""/>
    <input type="hidden" name="correspondingPoId" value=""/>
    <input type="hidden" name="is_gift" value=""/>
    <input type="hidden" name="gift_message" value=""/>
    <input type="hidden" name="order_additional_emails" value=""/>-->


</form>
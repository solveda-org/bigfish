<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if person?has_content>
    <#assign partyId= person.partyId!""/>
</#if>
<script type="text/javascript">
    jQuery(document).ready(function () {
            getAddressFormat("USER");
    });
</script>

<div id="aboutYouEntry" class="displayBox">
<div class="displayBoxHeader">
<span class="displayBoxHeaderCaption"><#if isCheckoutPage?exists && isCheckoutPage! == "true">${uiLabelMap.CustomerPersonalHeading}<#else>${uiLabelMap.AboutYouHeading}</#if></span>
</div>
<p class="instructions">${StringUtil.wrapString(uiLabelMap.EditCustomerInstructionsInfo)}</p>
  <fieldset class="col">

     <input type="hidden" name="partyId" value="${partyId!""}"/>
     <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
     <input type="hidden" name="USER_COUNTRY" id="USER_COUNTRY" value="${COUNTRY_DEFAULT!}"/>
     <input type="hidden" id="USER_USE_PHONE_CONTACT" name="USER_USE_PHONE_CONTACT" value="true"/>
     <input type="hidden" name="USER_MIDDLE_NAME" value=""/>
     <#if contactMech?has_content>
       <#assign contactMechId=contactMech.contactMechId!"">
       <input type="hidden" name="contactMechId" value="${contactMechId!""}"/>
     </#if>
     <#if postalAddressData?has_content>
       <#assign attnName=postalAddressData.attnName!"">
     </#if>
     
     <!-- DIV for Displaying Person info STARTS here -->
    <div class="personInfo">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#personInfoDivSequence")}
    </div>
    <!-- DIV for Displaying Person Info ENDS here -->  
   
  </fieldset>
</div>

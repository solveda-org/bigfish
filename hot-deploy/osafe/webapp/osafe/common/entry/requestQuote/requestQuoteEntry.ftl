<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="requestQuoteEntry" class="displayBox">
<p class="instructions">${uiLabelMap.RequestQuoteInstructionsInfo}</p>
<p class="instructions">${uiLabelMap.RequestQuoteEnterMessageInfo}</p>
    <input type="hidden" name="partyIdFrom" value="${(userLogin.partyId)?if_exists}" />
    <input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
    <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS" />
    <input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI" />
    <input type="hidden" name="emailType" value="${emailType!""}" />
    <input type="hidden" name="custRequestTypeId" value="${custRequestTypeId!""}" />
    <input type="hidden" name="custRequestName" value="${custRequestName!""}" />
    <#assign COUNTRY_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"COUNTRY_DEFAULT")!"USA"/>
    <input type="hidden" name="CONTACT_US_COUNTRY" id="CONTACT_US_COUNTRY" value="${COUNTRY_DEFAULT!}"/>
    <#if userLogin?has_content>
       <#assign emailLogin=userLogin.userLoginId>
       <#assign person = userLogin.getRelatedOneCache("Person")!"" >
       <#if person?has_content>
         <#assign firstName=person.firstName>
         <#assign lastName=person.lastName>
       </#if>
    </#if>

  ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#requestQuoteDivSequence")}
</div>
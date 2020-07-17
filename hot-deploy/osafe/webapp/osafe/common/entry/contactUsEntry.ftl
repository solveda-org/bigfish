<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="contactUsEntry" class="displayBox">
<p class="instructions">${uiLabelMap.ContactUsInstructionsInfo}</p>
<p class="instructions">${uiLabelMap.ContactUsEnterMessageInfo}</p>
  <fieldset class="col">
    <input type="hidden" name="partyIdFrom" value="${(userLogin.partyId)?if_exists}" />
    <input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
    <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS" />
    <input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI" />
    <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
    <input type="hidden" name="emailType" value="CONT_NOTI_EMAIL" />
    <input type="hidden" name="custRequestTypeId" value="${custRequestTypeId!""}" />
    <input type="hidden" name="custRequestName" value="${custRequestName!""}" />
    <#if userLogin?has_content>
       <#assign emailLogin=userLogin.userLoginId>
       <#assign person = userLogin.getRelatedOneCache("Person")!"" >
       <#if person?has_content>
         <#assign firstName=person.firstName>
         <#assign lastName=person.lastName>
       </#if>
    </#if>
    <div class="entry">
      <label><@required/>${uiLabelMap.ReasonForContactCaption}</label>
      <select name="contactReason" id="contactReason">
          ${screens.render("component://osafe/widget/CommonScreens.xml#contactReasonType")}
      </select>
      <@fieldErrors fieldName="contactReason"/>
    </div>
    <div class="entry">
      <label for="firstName"><@required/>${uiLabelMap.FirstNameCaption}</label>
      <input type="text" maxlength="100" name="firstName" id="firstName" value="${parameters.firstName!firstName!""}"/>
      <@fieldErrors fieldName="firstName"/>
    </div>
    <div class="entry">
      <label for="lastName"><@required/>${uiLabelMap.LastNameCaption}</label>
      <input type="text"  maxlength="100" name="lastName" id="lastName" value="${parameters.lastName!lastName!""}"/>
      <@fieldErrors fieldName="lastName"/>
    </div>
    <div class="entry">
      <label for="emailAddress"><@required/>${uiLabelMap.EmailAddressCaption}</label>
      <input type="email"  maxlength="100" name="emailAddress" id="emailAddress" value="${parameters.emailAddress!emailLogin!""}"/>
      <@fieldErrors fieldName="emailAddress"/>
    </div>
    <#assign countryDefault=COUNTRY_DEFAULT!""/>
    <#if countryDefault?has_content>
     <#assign countryDefault = countryDefault.toUpperCase()/> 
    </#if>
    <#if countryDefault?has_content && (countryDefault =="USA" || countryDefault="CAN")>
    <div class="entry">
      <label for="contactPhoneContact">${uiLabelMap.ContactPhoneCaption}</label>
      <input type="number" class="phone3" name="contactPhoneArea" size="5" value="${parameters.contactPhoneArea!""}" /></td>
      <input type="hidden" id="contactPhoneContact" name="contactPhoneContact" value="${parameters.contactPhoneContact!""}"/>
      <input type="number" class="phone3" id="contactPhoneContact3" name="contactPhoneContact3" value="${parameters.contactPhoneContact3!""}" maxlength="3" /></td>
      <input type="number" class="phone4" id="contactPhoneContact4" name="contactPhoneContact4" value="${parameters.contactPhoneContact4!""}" maxlength="255"/></td>
    </div>
    <#else>
    <div class="entry">
      <label for="contactPhoneContact">${uiLabelMap.ContactPhoneCaption}</label>
      <input type="number" class="phone" name="contactPhoneNumber" maxlength="100" value="${parameters.contactPhoneNumber!""}" /></td>
    </div>
    </#if>
    <div class="entry">
      <label for="orderIdNumber">${uiLabelMap.OrderNumberCaption}</label>
      <input type="text"  maxlength="20" name="orderIdNumber" id="orderIdNumber" value="${parameters.orderIdNumber!""}"/>
    </div>
    <div class="entry">
      <label for="content"><@required/>${uiLabelMap.CommentCaption}</label>
      <textarea name="content" id="content" class="content" cols="50" rows="5">${parameters.content!""}</textarea>
      <@fieldErrors fieldName="content"/>
      <div class="entry">
        <label for="content">&nbsp;</label>
        <span class="textCounter" id="textCounter"></span>
      </div>
    </div>
  </fieldset>
</div>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
  <#-- Enter Credit Card Box -->
  <div id="paymentMethods" class="displayBox">
    <div class="displayBoxHeader">
      <span class="displayBoxHeaderCaption">${uiLabelMap.PaymentInformationHeading}</span>
    </div>
    <#assign donePage = "donePage">
    <div>
        <#if creditCard?exists && creditCard?has_content>
          <input type="hidden" name="paymentMethodId" value="${paymentMethodId}" />
        </#if>
        <input type="hidden" name="fieldLevelErrors" value="Y" />

        <#-- Added to deal with payment being declined -->
        <input type="hidden" id="checkoutpage" name="checkoutpage" value="payment" />
        <input type="hidden" id="BACK_PAGE" name="BACK_PAGE" value="checkoutoptions" />

        <#-- Added so on successful redirect to "Order Complete" page we know to show the "Thank You" message  -->
        <#-- <input type="hidden" id="showThankYouStatus" name="showThankYouStatus" value="Y" /> -->

        <#if !creditCard?has_content>
            <#assign creditCard = requestParameters>
        </#if>

        <#if !paymentMethod?has_content>
            <#assign paymentMethod = requestParameters>
        </#if>
        <fieldset class="col">
          <input type="hidden" name="companyNameOnCard" id="companyNameOnCard" value="" />
          <input type="hidden" name="titleOnCard" id="titleOnCard" value="" />

          <#-- Place holder for fields that are ootb but not currently on our screen -->
          <input type="hidden" name="firstNameOnCard" id="firstNameOnCard" value="${billingPersonFirstName?if_exists}" />
          <input type="hidden" name="middleNameOnCard" id="middleNameOnCard" value="" />
          <input type="hidden" name="lastNameOnCard" id="lastNameOnCard" value="${billingPersonLastName?if_exists}" />
          <input type="hidden" name="suffixOnCard" id="suffixOnCard" value="" />

          <input type="hidden" name="cardSecurityCode" id="cardSecurityCode" value="" />
          <input type="hidden" name="description" id="cardSecurityCode" value="" />
          <#if billingContactMechId?has_content>
            <input type="hidden" name="contactMechId" id="contactMechId" value="${billingContactMechId!""}" />
          </#if>

          <input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="CREDIT_CARD" />
            <div class="entry paypal">
            <h3>${uiLabelMap.PayPalHeading}</h3>
              <label>${uiLabelMap.PayPalCaption}</label>
              <a href="javaScript:void(0);" onclick="setExternalCheckout('EXT_PAYPAL');">
                    <img class="paymentMethodImg" alt="PayPal Checkout" src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif">
              </a>
            </div>

            <div class="entry">
               <h3>${uiLabelMap.CreditCardHeading}</h3>
              <label for="cardType"><@required/>${uiLabelMap.CardTypeCaption}</label>
                <select id="cardType" name="cardType" class="cardType">
                    <#if creditCard.cardType?exists>
                        <option>${creditCard.cardType}</option>

                        <option value="${creditCard.cardType}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.SelectOneLabel}</option>
                    ${screens.render("component://common/widget/CommonScreens.xml#cctypes")}
              </select>
              <@fieldErrors fieldName="cardType"/>
            </div>
            <div class="entry">
              <label for="cardNumber"><@required/>${uiLabelMap.CardNumberCaption}</label>
               <input type="text" class="cardNumber" maxlength="30" id="cardNumber"  name="cardNumber" value="${creditCard.cardNumber!requestParameters.cardNumber!""}"/>
              <@fieldErrors fieldName="cardNumber"/>
            </div>
              <#assign expMonth = "">
              <#assign expYear = "">
              <#if creditCard?exists && creditCard.expireDate?exists>
                <#assign expDate = creditCard.expireDate>
                <#if (expDate?exists && expDate.indexOf("/") > 0)>
                  <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
                  <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
                </#if>
              </#if>
            <div class="entry">
              <label for="expMonth"><@required/>${uiLabelMap.ExpirationMonthCaption}</label>
              <select id="expMonth" name="expMonth" class="expMonth">
                <#if creditCard?has_content && expMonth?has_content>
                  <#assign ccExprMonth = expMonth>
                <#else>
                  <#assign ccExprMonth = requestParameters.expMonth?if_exists>
                </#if>
                <#if ccExprMonth?has_content>
                  <option value="${ccExprMonth?if_exists}">${ccExprMonth?if_exists}</option>
                </#if>
                <option value="">${uiLabelMap.SelectOneLabel}</option>
                ${screens.render("component://common/widget/CommonScreens.xml#ccmonths")}
              </select>
               <@fieldErrors fieldName="expMonth"/>
            </div>
            <div class="entry">
              <label for="expYear"><@required/>${uiLabelMap.ExpirationYearCaption}</label>
              <select id="expYear" name="expYear" class="expYear">
                <#if creditCard?has_content && expYear?has_content>
                  <#assign ccExprYear = expYear>
                <#else>
                  <#assign ccExprYear = requestParameters.expYear?if_exists>
                </#if>
                <#if ccExprYear?has_content>
                  <option value="${ccExprYear?if_exists}">${ccExprYear?if_exists}</option>
                </#if>
                <option value="">${uiLabelMap.SelectOneLabel}</option>
                ${screens.render("component://common/widget/CommonScreens.xml#ccyears")}
              </select>
              <@fieldErrors fieldName="expYear"/>
            </div>

        </fieldset>

           </div>
        <a name="paymentMethod"></a>

    </div>


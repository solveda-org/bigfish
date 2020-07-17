<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="emailAddress"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.EmailAddressCaption}</label>
      <div class="entryField">
          <input class="medium" type="email"  maxlength="255" name="fromEmailAddress" id="fromEmailAddress" value="${parameters.fromEmailAddress!userEmailAddress!""}"/>
          <input type="hidden" name="fromEmailAddress_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="fromEmailAddress"/>
      </div>
</div>
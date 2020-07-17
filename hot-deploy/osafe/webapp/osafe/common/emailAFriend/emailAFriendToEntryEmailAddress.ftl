<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="emailAddress"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.EmailAddressCaption}</label>
      <div class="entryField">
          <input class="medium" type="email"  maxlength="255" name="toEmailAddress" id="toEmailAddress" value="${parameters.toEmailAddress!""}"/>
          <input type="hidden" name="toEmailAddress_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="toEmailAddress"/>
      </div>
</div>
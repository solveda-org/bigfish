<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="firstName"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.FirstNameCaption}</label>
      <div class="entryField">
          <input class="medium" type="text" maxlength="255" name="toFirstName" id="toFirstName" value="${parameters.toFirstName!""}"/>
          <input type="hidden" name="toFirstName_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="toFirstName"/>
      </div>
</div>
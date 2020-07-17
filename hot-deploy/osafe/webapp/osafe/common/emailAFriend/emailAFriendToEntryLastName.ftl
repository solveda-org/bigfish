<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="lastName"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.LastNameCaption}</label>
      <div class="entryField">
          <input class="medium" type="text"  maxlength="255" name="toLastName" id="toLastName" value="${parameters.toLastName!""}"/>
          <input type="hidden" name="toLastName_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="toLastName"/>
      </div>
</div>
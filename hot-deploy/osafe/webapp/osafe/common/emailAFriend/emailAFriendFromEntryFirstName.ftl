<#if person?has_content>
  <#assign fromFirstName = person.firstName!""/>
</#if>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="firstName"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.FirstNameCaption}</label>
      <div class="entryField">
          <input class="medium" type="text" maxlength="255" name="fromFirstName" id="fromFirstName" value="${parameters.fromFirstName!fromFirstName!""}"/>
          <input type="hidden" name="fromFirstName_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="fromFirstName"/>
      </div>
</div>
<#if person?has_content>
  <#assign fromLastName = person.lastName!""/>
</#if>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="lastName"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.LastNameCaption}</label>
      <div class="entryField">
          <input class="medium" type="text"  maxlength="255" name="fromLastName" id="fromLastName" value="${parameters.fromLastName!fromLastName!""}"/>
          <input type="hidden" name="fromLastName_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="fromLastName"/>
      </div>
</div>
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="content"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.SubjectCaption}</label>
      <div class="entryField">
          <input class="medium" type="text" maxlength="255" name="subject" id="subject" value="${parameters.subject!subject!""}"/>
          <input type="hidden" name="subject_MANDATORY" value="${mandatory}"/>
          <@fieldErrors fieldName="subject"/>
      </div>
</div>
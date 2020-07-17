<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="${request.getAttribute("attributeClass")!}">
  <label>${uiLabelMap.CartItemRecurrenceFreqCaption}</label>
  <div class="entryField">
    <select name="recurrenceFreq" id="recurrenceFreq" class="recurrenceFreq">
      <#if recurrenceFreq?exists && recurrenceFreq?has_content>
        <#assign recurrenceFreqEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumCode" , recurrenceFreq))/>  
        <#if recurrenceFreqEnums?has_content>
          <#assign recurrenceFreqEnum = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(recurrenceFreqEnums) />  
          <#if recurrenceFreqEnum?has_content>
            <option value="${recurrenceFreq!}">${recurrenceFreqEnum.description!}</option>
          </#if>
        </#if>
      </#if>
      ${screens.render("component://osafe/widget/CommonScreens.xml#recurrenceFreqTypes")}
    </select>
    <@fieldErrors fieldName="recurrenceFreq"/>
  </div>
</div>


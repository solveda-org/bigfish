<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#if userLogin?has_content>
    <#assign partyId = userLogin.partyId!"">
</#if>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "TITLE"}, true)?if_exists />
    <#if partyAttribute?has_content>
      <#assign USER_TITLE = partyAttribute.attrValue!"">
    </#if>
</#if>
<#assign  selectedUserTitle = parameters.get("USER_TITLE")!USER_TITLE?if_exists/>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
      <label for="USER_TITLE"><#if mandatory == "Y"><@required/></#if>${uiLabelMap.TitleCaption}</label>
      <div class="entryField">
	      <select name="USER_TITLE" id="USER_TITLE">
	          <#if selectedUserTitle?has_content>
	            <option value="${selectedUserTitle!}">${selectedUserTitle!}</option>
	          </#if>
	          <option value="">${uiLabelMap.SelectOneLabel}</option>
	          ${screens.render("component://osafe/widget/CommonScreens.xml#titleTypes")}
	      </select>
	      <input type="hidden" name="USER_TITLE_MANDATORY" value="${mandatory}"/>
	      <@fieldErrors fieldName="USER_TITLE"/>
      </div>
</div>

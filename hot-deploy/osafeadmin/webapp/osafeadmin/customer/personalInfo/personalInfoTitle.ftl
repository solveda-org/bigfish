<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "TITLE"}, false)?if_exists />
    <#if partyAttribute?has_content>
      <#assign personalTitle = partyAttribute.attrValue!"">
    </#if>
</#if>
<#assign  selectedUserTitle = parameters.personalTitle!personalTitle?if_exists/>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label for="title"><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.TitleCaption}</label>
            </div>
            <div class="infoValue">
                <select name="personalTitle" id="personalTitle">
                    <#if selectedUserTitle?has_content>
                        <option value="${selectedUserTitle!}">${selectedUserTitle!}</option>
                    </#if>
                    <option value="">${uiLabelMap.SelectOneLabel}</option>
                    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#titleTypes")}
                 </select>
                 <input type="hidden" name="personalTitle_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
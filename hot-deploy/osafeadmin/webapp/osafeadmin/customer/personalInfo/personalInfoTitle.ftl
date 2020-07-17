<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "TITLE"}, true)?if_exists />
    <#if partyAttribute?has_content>
      <#assign personalTitle = partyAttribute.attrValue!"">
    </#if>
</#if>
<#assign  selectedUserTitle = parameters.personalTitle!personalTitle?if_exists/>
<div class = "personalInfoTitle">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label for="title"><span class="required">*</span>${uiLabelMap.TitleCaption}</label>
            </div>
            <div class="infoValue">
              <select name="personalTitle" id="personalTitle">
                  <#if selectedUserTitle?has_content>
                    <option value="${selectedUserTitle!}">${selectedUserTitle!}</option>
                  </#if>
                  <option value="">${uiLabelMap.SelectOneLabel}</option>
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#titleTypes")}
              </select>
            </div>
        </div>
    </div>
</div>
<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "GENDER"}, true)?if_exists />
    <#if partyAttribute?has_content>
      <#assign gender = partyAttribute.attrValue!"">
    </#if>
</#if>

<div class ="personalInfoGender">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.GenderCaption}</label>
            </div>
            <div class="infoValue">
              <select name="gender" id="gender">
                <option value="">${uiLabelMap.SelectOneLabel}</option>
                <option value="M" <#if ((parameters.gender?exists && parameters.gender?string == "M") || (!parameters.gender?exists && gender?exists && gender == "M"))>selected</#if>>${uiLabelMap.GenderMale}</option>
                <option value="F" <#if ((parameters.gender?exists && parameters.gender?string == "F") || (!parameters.gender?exists && gender?exists && gender == "F"))>selected</#if>>${uiLabelMap.GenderFemale}</option>
              </select>
            </div>
        </div>
    </div>
</div>

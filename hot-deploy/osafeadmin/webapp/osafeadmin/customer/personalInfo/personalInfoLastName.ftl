<#if person?has_content>
  <#assign lastName= person.lastName!""/>
</#if>
<div class ="personalInfoLastName">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.LastNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" name="lastName" id="lastName" value="${parameters.lastName!lastName!""}" />
            </div>
        </div>
    </div>
</div>

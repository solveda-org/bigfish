<#if person?has_content>
  <#assign firstName= person.firstName!""/>
</#if>
<div class ="personalInfoFirstName">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><span class="required">*</span>${uiLabelMap.FirstNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" name="firstName" id="firstName" value="${parameters.firstName!firstName!""}" />
            </div>
        </div>
    </div>
</div>
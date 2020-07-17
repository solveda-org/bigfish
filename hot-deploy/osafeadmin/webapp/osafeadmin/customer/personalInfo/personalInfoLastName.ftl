<#if person?has_content>
  <#assign lastName= person.lastName!""/>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.LastNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" name="lastName" id="lastName" value="${parameters.lastName!lastName!""}" />
                <input type="hidden" name="lastName_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
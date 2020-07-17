<#if person?has_content>
  <#assign firstName= person.firstName!""/>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.FirstNameCaption}</label>
            </div>
            <div class="infoValue">
                <input type="text" maxlength="100" name="firstName" id="firstName" value="${parameters.firstName!firstName!""}" />
                <input type="hidden" name="firstName_mandatory" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
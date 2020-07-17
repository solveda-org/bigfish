<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "DOB_DDMMYYYY"}, false)?if_exists />
    <#if partyAttribute?has_content>
      <#assign DOB_DDMMYYYY = partyAttribute.attrValue!"">
      <#if DOB_DDMMYYYY?has_content && (DOB_DDMMYYYY?length gt 9)>
          <#assign dobDay = DOB_DDMMYYYY.substring(0, 2) />
          <#assign dobMonth = DOB_DDMMYYYY.substring(3,5) />
          <#assign dobYear = DOB_DDMMYYYY.substring(6) />
      </#if>
    </#if>
</#if>

<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label for="dobLongDayUk"><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.DOB_Caption}</label>
            </div>
            <div class="infoValue">
              <select id="dobLongDayUk" name="dobLongDayUk" class="dobDay">
                  <#assign dobDay = parameters.dobLongDayUk!dobDay!"">
                  <#if dobDay?has_content && (dobDay?length gt 1)>
                      <option value="${dobDay?if_exists}">${dobDay?if_exists}</option>
                  </#if>
                  <option value="">${uiLabelMap.DOB_Day}</option>
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ddDays")}
              </select>
              <select id="dobLongMonthUk" name="dobLongMonthUk" class="dobMonth">
                  <#assign dobMonth = parameters.dobLongMonthUk!dobMonth!"">
                  <#if dobMonth?has_content && (dobMonth?length gt 1)>
                      <option value="${dobMonth?if_exists}">${dobMonth?if_exists}</option>
                  </#if>
                  <option value="">${uiLabelMap.DOB_Month}</option>
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ddMonths")}
              </select>
              <select id="dobLongYearUk" name="dobLongYearUk" class="dobYear">
                  <#assign dobYear = parameters.dobLongYearUk!dobYear!"">
                  <#if dobYear?has_content && (dobYear?length gt 1)>
                      <option value="${dobYear?if_exists}">${dobYear?if_exists}</option>
                  </#if>
                  <option value="">${uiLabelMap.DOB_Year}</option>
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ddYears")}
              </select>
              <input type="hidden" name="DOB_DDMMYYYY_MANDATORY" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
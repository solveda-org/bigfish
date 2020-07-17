<#if partyId?exists && partyId?has_content>
    <#assign partyAttribute = delegator.findOne("PartyAttribute", {"partyId" : partyId, "attrName" : "DOB_MMDD"}, false)?if_exists />
    <#if partyAttribute?has_content>
          <#assign DOB_MMDD = partyAttribute.attrValue!"">
      <#if DOB_MMDD?has_content && DOB_MMDD?length gt 4>
          <#assign dobMonth= DOB_MMDD.substring(0, 2) />
          <#assign dobDay = DOB_MMDD.substring(3,5) />
      </#if>
    </#if>
</#if>
<#assign mandatory= request.getAttribute("attributeMandatory")!"N"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label for="dobShortMonthUs"><#if mandatory == "Y"><span class="required">*</span></#if>${uiLabelMap.DOB_Caption}</label>
            </div>
            <div class="infoValue">
              <select id="dobShortMonthUs" name="dobShortMonthUs" class="dobMonth">
                  <#assign dobMonth = parameters.dobShortMonthUs!dobMonth!"">
                  <#if dobMonth?has_content && (dobMonth?length gt 1)>
                      <option value="${dobMonth?if_exists}">${dobMonth?if_exists}</option>
                  </#if>
                  <option value="">${uiLabelMap.DOB_Month}</option>
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ddMonths")}
              </select>
              <select id="dobShortDayUs" name="dobShortDayUs" class="dobDay">
                  <#assign dobDay = parameters.dobShortDayUs!dobDay!"">
                  <#if dobDay?has_content && (dobDay?length gt 1)>
                      <option value="${dobDay?if_exists}">${dobDay?if_exists}</option>
                  </#if>
                  <option value="">${uiLabelMap.DOB_Day}</option>
                  ${screens.render("component://osafeadmin/widget/CommonScreens.xml#ddDays")}
              </select>
              <input type="hidden" name="DOB_MMDD_MANDATORY" value="${mandatory}"/>
            </div>
        </div>
    </div>
</div>
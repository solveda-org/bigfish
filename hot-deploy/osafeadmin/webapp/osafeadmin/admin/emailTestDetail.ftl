  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="emailType" value="${parameters.emailType?default(emailType!"")}" />
      <div class="infoRow">
        <div class="infoEntry">
        	<div class="infoCaption">
                <label>${uiLabelMap.EMAIL_ADDRESS_Caption}</label>
            </div>
             <div class="infoValue">
                 <input type="text" class="large" name="testEmailAddress" id="testEmailAddress" maxlength="255" value="${parameters.testEmailAddress!""}"/>
             </div>
        </div>
      </div>

      <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TextCaption!""}</label>
            </div>
            <div class="infoValue">
                <textarea class="smallArea" name="testEmailText" cols="50" rows="5">${parameters.testEmailText!""}</textarea>
            </div>
        </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>
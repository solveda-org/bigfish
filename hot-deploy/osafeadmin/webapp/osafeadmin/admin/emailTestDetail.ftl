  <#if detailScreenName?exists && detailScreenName?has_content>
      <input type="hidden" name="detailScreen" value="${parameters.detailScreen?default(detailScreen!"")}" />
      <input type="hidden" name="emailType" value="${parameters.emailType?default(emailType!"")}" />
      <div class="infoRow">
        <div class="infoEntry">
        	<div class="infoCaption">
                <label>${uiLabelMap.FROM_EMAIL_ADDRESS_Caption}</label>
            </div>
             <div class="infoValue">
                 <input type="text" class="large" name="fromAddress" id="fromAddress" maxlength="255" value="${parameters.fromAddress!EMAIL_CLNT_REPLY_TO!""}"/>
             </div>
        </div>
      </div>
      <div class="infoRow">
        <div class="infoEntry">
        	<div class="infoCaption">
                <label>${uiLabelMap.TO_EMAIL_ADDRESS_Caption}</label>
            </div>
             <div class="infoValue">
                 <input type="text" class="large" name="toAddress" id="toAddress" maxlength="255" value="${parameters.toAddress!""}"/>
             </div>
        </div>
      </div>
      <div class="infoRow">
        <div class="infoEntry">
        	<div class="infoCaption">
                <label>${uiLabelMap.EMAIL_SUBJECT_Caption}</label>
            </div>
             <div class="infoValue">
                 <#assign emailSubject = "${EMAIL_CLNT_NAME !}: Email Test">
                 <input type="text" class="large" name="emailSubject" id="emailSubject" maxlength="255" value="${parameters.emailSubject!emailSubject!""}"/>
             </div>
        </div>
      </div>

      <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption">
                <label>${uiLabelMap.TextCaption!""}</label>
            </div>
            <div class="infoValue">
                <textarea class="smallArea" name="testEmailText" cols="50" rows="5">${parameters.testEmailText!"This is a test email"}</textarea>
            </div>
        </div>
      </div>
  <#else>
      ${uiLabelMap.NoDataAvailableInfo}
  </#if>
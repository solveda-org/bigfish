<#if emailConfiguration?has_content>
      <input type="hidden" name="emailType" id="emailType" value="${emailConfiguration.emailType!""}"/>
      <input type="hidden" name="bodyScreenLocation" id="bodyScreenLocation" value="${emailConfiguration.bodyScreenLocation!""}"/>
      
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption"><label>${uiLabelMap.EmailTypeCaption}</label></div>
            <div class="infoValue">${emailConfiguration.emailType!""}</div>
          </div>
        </div>
      
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption"><label>${uiLabelMap.DescriptionCaption}</label></div>
            <div class="infoValue">
            <#assign enumDetail = emailConfiguration.getRelatedOne("Enumeration")/>
              <textarea class="smallArea characterLimit" maxlength="255" name="description" cols="50" rows="1">${parameters.description!enumDetail.description!""}</textarea>
              <span class="textCounter"></span>
             </div>
          </div>
        </div>
      
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption"><label>${uiLabelMap.SubjectCaption}</label></div>
            <div class="infoValue">
              <input type="text" class="large" name="subject" id="subject" value="${parameters.subject!emailConfiguration.subject!""}"/>
            </div>
          </div>
        </div>
      
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption"><label>${uiLabelMap.FromCaption}</label></div>
            <div class="infoValue">
              <input type="text" class="medium" name="fromAddress" id="fromAddress" value="${parameters.fromAddress!emailConfiguration.fromAddress!""}"/>
            </div>
          </div>
        </div>
      
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption"><label>${uiLabelMap.CCCaption}</label></div>
            <div class="infoValue">
              <input type="text" class="medium" name="ccAddress" id="ccAddress" value="${parameters.ccAddress!emailConfiguration.ccAddress!""}"/>
            </div>
          </div>
        </div>
     
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption"><label>${uiLabelMap.BCCCaption}</label></div>
            <div class="infoValue">
              <input type="text" class="medium" name="bccAddress" id="bccAddress" value="${parameters.bccAddress!emailConfiguration.bccAddress!""}"/>
            </div>
          </div>
        </div>
<#else>
    ${uiLabelMap.NoDataAvailableInfo}
</#if>

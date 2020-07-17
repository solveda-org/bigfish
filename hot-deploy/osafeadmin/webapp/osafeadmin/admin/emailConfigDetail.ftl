<#if emailConfiguration?has_content>
      <#assign enumDetail = emailConfiguration.getRelatedOne("Enumeration")/>
      <#assign statusId = emailConfiguration.statusId!"CTNT_DEACTIVATED" />
      <#if statusId != "CTNT_PUBLISHED">
          <#assign statusId = "CTNT_DEACTIVATED">
      </#if>
      <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : statusId}, false)>
      <#assign statusDesc = statusItem.description!statusItem.get("description",locale)!statusItem.statusId>
      <#assign createdDate = emailConfiguration.createdStamp!"" />
      <#assign lastModifiedDate = emailConfiguration.lastUpdatedStamp!"" />

      <input type="hidden" name="emailType" id="emailType" value="${emailConfiguration.emailType!""}"/>
      <input type="hidden" name="bodyScreenLocation" id="bodyScreenLocation" value="${emailConfiguration.bodyScreenLocation!""}"/>
      <input type="hidden" name="statusId" id="statusId" value="${statusId!""}" />

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
            <div class="infoIcon">
              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.EmailSeparatorHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
          </div>
        </div>
     
      <div class="infoRow">
        <div class="infoEntry">
          <div class="infoCaption"><label>${uiLabelMap.BCCCaption}</label></div>
            <div class="infoValue">
              <input type="text" class="medium" name="bccAddress" id="bccAddress" value="${parameters.bccAddress!emailConfiguration.bccAddress!""}"/>
            </div>
            <div class="infoIcon">
              <a class="helper" href="javascript:void(0);" onMouseover="showTooltip(event,'${uiLabelMap.EmailSeparatorHelpInfo}');" onMouseout="hideTooltip()"><span class="helperIcon"></span></a>
            </div>
          </div>
        </div>
        <#-- ====== Created Date ==== -->
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption"><label>${uiLabelMap.CreatedDateCaption}</label></div>
                <div class="infoValue">${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(createdDate, preferredDateTimeFormat).toLowerCase())!"N/A"}</div>
            </div>
        </div>
        <#-- ===== Status Buttons ====== -->
        <div class="infoRow">
            <div class="infoEntry long">
                <div class="infoCaption">
                    <label>${uiLabelMap.StatusCaption}</label>
                </div>
                <div class="infoValue statusItem">
                    <span id="contentStatus">${statusDesc!""}</span>
                </div>
                <div class="statusButtons">
                    <#if statusId != "CTNT_PUBLISHED">
                        <input id="contentStatusBtn" type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ContentMakeActive}" idValue="CTNT_PUBLISHED" onClick="updateStatusBtn('${uiLabelMap.ContentMakeActive}', '${uiLabelMap.ContentSetInactive}', document.${detailFormName!""}, 'contentStatus', 'contentStatusBtn');"/>
                    <#else>
                        <input id="contentStatusBtn" type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ContentSetInactive}" onClick="updateStatusBtn('${uiLabelMap.ContentMakeActive}', '${uiLabelMap.ContentSetInactive}', document.${detailFormName!""}, 'contentStatus', 'contentStatusBtn');"/>
                    </#if>
               </div>
            </div>
        </div>

        <#-- ====== Active Date ==== -->
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoCaption">
                    <label>${uiLabelMap.ActiveDateCaption}</label>
                </div>
                <div class="infoValue">
                    <#if statusId == "CTNT_PUBLISHED" >
                        <#if lastModifiedDate?has_content>
                            ${(Static["com.osafe.util.OsafeAdminUtil"].convertDateTimeFormat(lastModifiedDate, preferredDateTimeFormat).toLowerCase())!"N/A"}
                        </#if>
                    </#if>
                   
                </div>
            </div>
        </div>
<#else>
	${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>

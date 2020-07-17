<!-- start promotionsSearch.ftl -->
     <div class="entryRow">
      <div class="entry">
          <label>${uiLabelMap.ReviewIdCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="srchReviewId" name="srchReviewId" maxlength="40" value="${parameters.srchReviewId!srchReviewId!""}"/>
          </div>
      </div>
      <div class="entry medium">
          <label>${uiLabelMap.StatusCaption}</label>
          <#assign intiCb = "${parameters.initializedCB!initializedCB}"/>
          <div class="entryInput checkbox medium">
             <input type="checkbox" class="checkBoxEntry" name="srchall" id="srchall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','srch')" <#if parameters.srchall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.CommonAll}
             <input class="srchReviewPend" type="checkbox" id="srchReviewPend" name="srchReviewPend" value="Y" <#if parameters.srchReviewPend?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.PendingLabel}
             <input class="srchReviewApprove" type="checkbox" id="srchReviewApprove" name="srchReviewApprove" value="Y" <#if parameters.srchReviewApprove?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.ApprovedLabel}
             <input class="srchReviewReject" type="checkbox" id="srchReviewReject" name="srchReviewReject" value="Y" <#if parameters.srchReviewReject?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.RejectedLabel}
          </div>
     </div>
    </div>
     <div class="entryRow">
      <div class="entry">
          <label>${uiLabelMap.ProductNoCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="srchProductId" name="srchProductId" maxlength="40" value="${parameters.srchProductId!srchProductId!""}"/>
          </div>
      </div>
      <div class="entry">
            <label>${uiLabelMap.DaysCaption}</label>
            <div class="entryInput select">
                <select id="srchDays" name="srchDays">
                    <option value="" <#if (parameters.srchDays!"") == "">selected</#if>>${uiLabelMap.ChooseOneLabel}</option>
                    <option value="oneToFive" <#if (parameters.srchDays!"") == "oneToFive">selected</#if>>${uiLabelMap.OneToFiveDaysLabel}</option>
                    <option value="fiveToTen" <#if (parameters.srchDays!"") == "fiveToTen">selected</#if>>${uiLabelMap.FiveToTenDaysLabel}</option>
                    <option value="tenPlus" <#if (parameters.srchDays!"") == "tenPlus">selected</#if>>${uiLabelMap.TenPlusDayLabel}</option>
                </select>
            </div>
      </div>
      <div class="entry">
          <label>${uiLabelMap.ReviewerCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="srchReviewer" name="srchReviewer" maxlength="40" value="${parameters.srchReviewer!srchReviewer!""}"/>
          </div>
      </div>
     </div>
<!-- end promotionsSearch.ftl -->


<!-- start promotionsSearch.ftl -->
    <#assign nowTimestamp=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
    <#assign defaultFromDate=Static["com.osafe.util.OsafeAdminUtil"].getMonthBackTimeStamp(1)>
    <div class="entryRow">
      <div class="entry">
        <label>${uiLabelMap.FromDateCaption}</label>
        <div class="entryInput from">
          <input class="dateEntry" type="text" name="from" maxlength="40" value="${parameters.from!from!defaultFromDate?string(entryDateTimeFormat)!""}"/>
        </div>
      </div> 
      <div class="entry medium">
        <label>${uiLabelMap.ToCaption}</label>
        <div class="entryInput to">
          <input class="dateEntry" type="text" name="to" maxlength="40" value="${parameters.to!to!nowTimestamp?string(entryDateTimeFormat)!""}"/>
        </div>
      </div> 
    </div>
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
          <#assign status = parameters.status!/>
          <#if status?has_content>
              <#assign intiCb = "Y"/>
          </#if>
          <div class="entryInput checkbox medium">
             <input type="checkbox" class="checkBoxEntry" name="srchall" id="srchall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','srch')" <#if parameters.srchall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.CommonAll}
             <input class="srchReviewPend" type="checkbox" id="srchReviewPend" name="srchReviewPend" value="Y" <#if parameters.srchReviewPend?has_content || ((intiCb?exists) && (intiCb == "N")) || (status == "PRR_PENDING" )>checked</#if>/>${uiLabelMap.PendingLabel}
             <input class="srchReviewApprove" type="checkbox" id="srchReviewApprove" name="srchReviewApprove" value="Y" <#if parameters.srchReviewApprove?has_content || ((intiCb?exists) && (intiCb == "N")) || (status == "PRR_APPROVED" )>checked</#if>/>${uiLabelMap.ApprovedLabel}
             <input class="srchReviewReject" type="checkbox" id="srchReviewReject" name="srchReviewReject" value="Y" <#if parameters.srchReviewReject?has_content || ((intiCb?exists) && (intiCb == "N")) || (status == "PRR_DELETED" )>checked</#if>/>${uiLabelMap.DeletedLabel}
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
      <div class="entry medium">
          <label>${uiLabelMap.StarsCaption}</label>
          <#assign intiCb = "${parameters.initializedCB!initializedCB}"/>
          <#if parameters.status?has_content>
              <#assign intiCb = "Y"/>
          </#if>
          <div class="entryInput checkbox medium">
             <input type="checkbox" class="checkBoxEntry" name="searchall" id="searchall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','search')" <#if parameters.searchall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.CommonAll}
             <input class="searchOneStar" type="checkbox" id="searchOneStar" name="searchOneStar" value="1" <#if parameters.searchOneStar?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.OneStarLabel}
             <input class="searchTwoStars" type="checkbox" id="searchTwoStars" name="searchTwoStars" value="2" <#if parameters.searchTwoStars?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.TwoStarLabel}
             <input class="searchThreeStars" type="checkbox" id="searchThreeStars" name="searchThreeStars" value="3" <#if parameters.searchThreeStars?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.ThreeStarLabel}
             <input class="searchFourStars" type="checkbox" id="searchFourStars" name="searchFourStars" value="4" <#if parameters.searchFourStars?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.FourStarLabel}
             <input class="searchFiveStars" type="checkbox" id="searchFiveStars" name="searchFiveStars" value="5" <#if parameters.searchFiveStars?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.FiveStarLabel}
          </div>
     </div>
     </div>
<!-- end promotionsSearch.ftl -->


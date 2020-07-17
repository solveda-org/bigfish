<!-- start productReviewDetail.ftl -->
<#if review?has_content>

    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.NickNameCaption}</label>
         </div>
         <div class="infoValue">
            <input type="text" class="medium" name="reviewNickName" id="reviewNickName" size="32" maxlength="100" value="${parameters.reviewNickName!review.reviewNickName!""}"/>
         </div>
       </div>
    </div>

    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.LocationCaption}</label>
         </div>
         <div class="infoValue">
            <input type="text" class="medium" name="reviewLocation" id="reviewLocation" size="32" maxlength="100" value="${parameters.reviewLocation!review.reviewLocation!""}"/>
         </div>
       </div>
    </div>

    <#assign  selectedReviewAge = parameters.reviewAge!review.reviewAge!""/>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.AgeCaption}</label>
         </div>
         <div class="infoValue">
             <select name="reviewAge" id="reviewAge">
                  <#if selectedReviewAge?has_content>
                    <option value="${selectedReviewAge!}">${selectedReviewAge!}</option>
                  </#if>
                  <option value="">${uiLabelMap.SelectOneLabel}</option>
                 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#reviewAges")}
             </select> 
         </div>
       </div>
    </div>

    <#assign  selectedReviewGender = parameters.reviewGender!review.reviewGender!""/>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.GenderCaption}</label>
         </div>
         <div class="infoValue">
         <select name="reviewGender" id="reviewGender">
             <option value=""> ${uiLabelMap.SelectOneLabel}</option>
             <option value="M" <#if ((selectedReviewGender?exists && selectedReviewGender?string == "M"))>selected</#if>>${uiLabelMap.GenderMale}</option>
             <option value="F" <#if ((selectedReviewGender?exists && selectedReviewGender?string == "F"))>selected</#if>>${uiLabelMap.GenderFemale}</option>
         </select>
         </div>
       </div>
    </div>
    
    <#assign  selectedReviewCustom01 = parameters.reviewCustom01!review.reviewCustom01!""/>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.Custom01Caption}</label>
         </div>
         <div class="infoValue">
             <select name="reviewCustom01" id="reviewCustom01">
                  <#if selectedReviewCustom01?has_content>
                    <option value="${selectedReviewCustom01!}">${selectedReviewCustom01!}</option>
                  </#if>
                  <option value="">${uiLabelMap.SelectOneLabel}</option>
                 ${screens.render("component://osafeadmin/widget/CommonScreens.xml#reviewCustom01")}
             </select> 
         </div>
       </div>
    </div>

</#if>
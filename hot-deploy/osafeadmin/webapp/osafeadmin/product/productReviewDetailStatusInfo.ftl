<#if review?has_content>
    <#assign statusItem = review.getRelatedOne("StatusItem")!>
    <div class="infoRow">
       <div class="infoEntry long">
         <div class="infoCaption">
          <label>${uiLabelMap.StatusCaption}</label>
         </div>
         <div class="infoValue statusItem">
           <span id="reviewStatus">${(statusItem.get("description",locale)?default(statusItem.statusId?default("N/A")))!}</span>
         </div>
        <div class="statusButtons">
            <div class="PRR_PENDING">
                <input type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ApproveBtn}" onClick="updateReview('PRR_APPROVED');"/>
                <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.DeleteBtn}"  onClick="updateReview('PRR_DELETED');"/>
            </div>
            <div class="PRR_APPROVED">
                <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.DeleteBtn}"  onClick="updateReview('PRR_DELETED');"/>
                <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.PendingBtn}"  onClick="updateReview('PRR_PENDING');"/>
            </div>
            <div class="PRR_DELETED">
                <input type="button" class="standardBtn secondary" name="approveBtn" value="${uiLabelMap.ApproveBtn}" onClick="updateReview('PRR_APPROVED');"/>
                <input type="button" class="standardBtn secondary" name="rejectBtn" value="${uiLabelMap.PendingBtn}"  onClick="updateReview('PRR_PENDING');"/>
            </div>
        </div>
       </div>
    </div>
</#if>
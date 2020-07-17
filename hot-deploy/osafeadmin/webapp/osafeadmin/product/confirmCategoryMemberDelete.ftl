<div class="displayListBox">
    <div class="header"><h2>${commonConfirmDialogTitle!""}</h2></div>
    <div class="boxBody">
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoValue confirmTxt" id='confirmDeleteTxt'>${commonConfirmDialogText!""} <#if confirmDeleteId?exists && confirmDeleteId?has_content> ${confirmDeleteId!""}</#if></div>
            </div>
        </div>
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoValue confirmBtn">
                    <input type="button" class="buttontext standardBtn action" name="yesBtn" value='${commonConfirmDialogYesBtn!""}' onClick="javascript:removeCategoryMemberRow('productCategoryMemberTable');"/>
                    <input type="button" class="buttontext standardBtn action" name="noBtn" value='${commonConfirmDialogNoBtn!""}'  onClick="javascript:confirmDialogResult('N');"/>
                </div>
            </div>
        </div>
    </div>
</div>

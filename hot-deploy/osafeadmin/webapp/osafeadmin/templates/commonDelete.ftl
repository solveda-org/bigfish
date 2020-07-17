<div class="displayListBox">
    <div class="header"><h2>${commonConfirmDialogTitle!""}</h2></div>
    <div class="boxBody">
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoValue confirmDeleteTxt">${StringUtil.wrapString(deleteDialogText!"")} <#if confirmDeleteId?exists && confirmDeleteId?has_content> ${confirmDeleteId!""}</#if></div>
            </div>
        </div>
        <div class="infoRow">
            <div class="infoEntry">
                <div class="infoValue confirmBtn">
                    <input type="button" class="buttontext standardBtn action" name="yesDeleteBtn" value='${commonDeleteDialogYesBtn!""}' onClick="javascript:deleteDialogResult('Y');"/>
                    <input type="button" class="buttontext standardBtn action" name="noBtn" value='${commonDeleteDialogNoBtn!""}'  onClick="javascript:deleteDialogResult('N');"/>
                </div>
            </div>
        </div>
    </div>
</div>

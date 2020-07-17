    <div class="infoRow">
        <div class="infoEntry">
            <div class="infoCaption"><label>${uiLabelMap.GiftCardCaption}</label></div>
            <div class="infoValue">
               <input type="text" id="giftCardNumber" name="giftCardNumber" value="" maxlength="60" autocomplete="off"/>
               <a href="javascript:addGiftCardNumber();"><span class="refreshIcon"></span></a>
            </div>
        </div>
    </div>
    <div class="infoRow">
    ${screens.render("component://osafeadmin/widget/AdminCheckoutScreens.xml#enteredGiftCard")}
    </div>
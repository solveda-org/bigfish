<div id="pincodeChecker" class="pincodeChecker">
  <form method="post" class="${dialogPurpose!}Form" action="<@ofbizUrl secure="${request.isSecure()?string}">${dialogBoxFormAction}${previousParams?if_exists}</@ofbizUrl>" name="pincodeSearchForm">
      <p class="instructions">
          ${uiLabelMap.PinCodeSearchTxtInfo}
      </p>
      <div class="entry">
          <label>${uiLabelMap.PinCodeCaption}</label>
          <input type="text" maxlength="255" name="pincode" id="pincode" value="${parameters.pincode!""}"/>
      </div>
      <div class="entryButtons">
          <input type="submit" value="${uiLabelMap.CheckPinCodeBtn}" class="standardBtn action"/>
      </div>
  </form>
</div>

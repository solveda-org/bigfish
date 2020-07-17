<form method="post" class="${dialogPurpose!}Form" action="<@ofbizUrl>${searchStoreFormAction!""}${previousParams?if_exists}</@ofbizUrl>" id="${searchStoreFormName!"searchForm"}" name="${searchStoreFormName!"searchForm"}">
  <p class="instructions">
    ${uiLabelMap.StoreLocatorInstructionsInfo}
  </p>
  <div class="entry">
      <label>${uiLabelMap.StoreLocatorEnterMessageInfo}</label>
      <input type="text" maxlength="255" name="address" id="address" value="${parameters.address!""}"/>
      <input type="submit" value="${uiLabelMap.SearchBtn}" class="standardBtn action"/>
  </div>
</form>
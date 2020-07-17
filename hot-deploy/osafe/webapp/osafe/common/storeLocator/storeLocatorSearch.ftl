<div class="${request.getAttribute("attributeClass")!}">
<form method="post" class="${dialogPurpose!}Form" action="<@ofbizUrl>${searchStoreFormAction!""}${previousParams?if_exists}</@ofbizUrl>" id="${searchStoreFormName!"searchForm"}" name="${searchStoreFormName!"searchForm"}">
  <p class="instructions">
    ${uiLabelMap.StoreLocatorInstructionsInfo}
  </p>
      <#-- Hidden fields for address used when user search not found. -->
      <input type="hidden" name="notFoundLatitude" id="notFoundLatitude" value="${parameters.notFoundLatitude!""}"/>
      <input type="hidden" name="notFoundLongitude" id="notFoundLongitude" value="${parameters.notFoundLongitude!""}"/>
      <input type="hidden" name="notFoundAddress" id="notFoundAddress" value="${notFoundAddress!""}"/>
      <input type="hidden" name="latitude" id="latitude" value="${parameters.latitude!""}"/>
      <input type="hidden" name="longitude" id="longitude" value="${parameters.longitude!""}"/>
      <#-- End Hidden fields for address used when user search not found. -->
   <ul class="displayActionList ${request.getAttribute("attributeClass")!}">
    <li>
     <div>
      <label>${uiLabelMap.StoreLocatorEnterMessageInfo}</label>
      <input type="text" maxlength="255" name="address" id="address" value="${parameters.address!""}"/>
      <input type="submit" value="${uiLabelMap.SearchBtn}" class="standardBtn action"/>
     </div>
    </li>
   </ul>
</form>
</div>

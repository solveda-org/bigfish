<!-- start searchBox -->
  <div class="entryRow">
    <div class="entry short">
      <label>${uiLabelMap.StoreLocationCodeCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="partyGroupNameLocal" name="partyGroupNameLocal" maxlength="100" value="${parameters.partyGroupNameLocal!""}"/>
      </div>
    </div>
    <div class="entry short">
      <label>${uiLabelMap.StoreLocationNameCaption}</label>
      <div class="entryInput">
        <input class="textEntry nameEntry" type="text" id="partyGroupName" name="partyGroupName" maxlength="100" value="${parameters.partyGroupName!""}"/>
      </div>
    </div>
  </div>
  <div class="entryRow">
    <div class="entry.short">
      <label>${uiLabelMap.StoreLocationStatusCaption}</label>
      <#assign intiCb = "${initializedCB}"/>
      <div class="entryInput checkbox small">
        <input type="checkbox" class="checkBoxEntry" name="statusall" id="statusall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','status')" <#if parameters.statusall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
        <input type="checkbox" class="checkBoxEntry" name="statusEnabled" id="statusEnabled" value="Y" <#if parameters.statusEnabled?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.StoreLocationOpenedLabel}
        <input type="checkbox" class="checkBoxEntry" name="statusDisabled" id="statusDisabled" value="Y" <#if parameters.statusDisabled?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.StoreLocationClosedLabel}
      </div>
    </div>
  </div>
<!-- end searchBox -->


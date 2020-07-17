<!-- start searchBox -->
<div class="entryRow">
  <div class="entry medium">
    <label>${uiLabelMap.LastNameCaption}</label>
    <div class="entryInput">
      <input class="largeTextEntry" type="text" id="lastName" name="lastName" value="${parameters.lastName!""}"/>
    </div>
  </div>
</div>
<div class="entryRow">
  <div class="entry daterange">
    <label>${uiLabelMap.FromDateCaption}</label>
    <div class="entryInput from">
      <input class="dateEntry" type="text" name="contactUsDateFrom" maxlength="40" value="${parameters.contactUsDateFrom!periodFrom!""}"/>
    </div>
    <label class="tolabel">${uiLabelMap.ToCaption}</label>
    <div class="entryInput to">
      <input class="dateEntry" type="text" name="contactUsDateTo" maxlength="40" value="${parameters.contactUsDateTo!periodTo!""}"/>
    </div>
  </div>
</div>
<div class="entryRow">
  <div class="entry short">
    <label>${uiLabelMap.ExportStatusCaption}</label>
    <#assign intiCb = "${initializedCB}"/>
    <div class="entryInput checkbox small">
      <input type="checkbox" class="checkBoxEntry" name="downloadall" id="downloadall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','download')" <#if parameters.downloadall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
      <input type="checkbox" class="checkBoxEntry" name="downloadnew" id="downloadnew" value="Y" <#if parameters.downloadnew?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.NewLabel}
      <input type="checkbox" class="checkBoxEntry" name="downloadloaded" name="downloadloaded" value="Y" <#if parameters.downloadloaded?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportedLabel}
    </div>
  </div>
</div>
<!-- end searchBox -->
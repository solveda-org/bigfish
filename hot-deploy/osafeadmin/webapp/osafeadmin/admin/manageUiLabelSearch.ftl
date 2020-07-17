
<!-- start searchBox -->
  <div class="entryRow">
    <div class="entry">
      <label>${uiLabelMap.SearchForCaption}</label>
      <div class="entryInput">
        <input class="textEntry" type="text" id="searchString" name="searchString" maxlength="40" value="${parameters.searchString!""}"/>
      </div>
    </div>
  </div>
  <div class="entryRow">
    <div class="entry long">
      <label>${uiLabelMap.SearchInCaption}</label>
      <#assign intiCb = "${initializedCB}"/>
      <div class="entryInput checkbox">
        <input type="checkbox" class="checkBoxEntry" name="srchall" id="srchall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','srch')" <#if parameters.srchall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
        <input type="checkbox" class="srchByName" name="srchByName" id="srchByName" value="Y" <#if parameters.srchByName?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.LabelLabel}
        <input type="checkbox" class="srchByCategory" name="srchByCategory" id="srchByCategory" value="Y" <#if parameters.srchByCategory?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.CategoryLabel}
        <input type="checkbox" class="srchByDescription" name="srchByDescription" id="srchByDescription" value="Y" <#if parameters.srchByDescription?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.DescriptionLabel}
        <input type="checkbox" class="srchByValue" name="srchByValue" id="srchByValue" value="Y" <#if parameters.srchByValue?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.CaptionLabel}
      </div>
    </div>
  </div>
<!-- end searchBox -->


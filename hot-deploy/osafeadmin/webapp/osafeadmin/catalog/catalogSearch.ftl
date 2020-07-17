<!-- start searchBox -->
<div class="entryRow">
  <div class="entry daterange">
    <label>${uiLabelMap.AsOfDateCaption}</label>
    <div class="entryInput from">
      <input class="dateEntry" type="text" name="catalogActiveDate" maxlength="40" value="${catalogActiveDate!parameters.catalogActiveDate!""}"/>
    </div>
  </div>
  <div class="entry">
    <#assign intiCb = "${initializedCB!}"/>
    <div class="entryInput checkbox short">
      <input class="checkBoxEntry" type="checkbox" id="showAll" name="showAll" value="Y" <#if parameters.showAll?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if>/>${uiLabelMap.ShowAllLabel}
    </div>
  </div>
</div>  
<!-- end searchBox -->
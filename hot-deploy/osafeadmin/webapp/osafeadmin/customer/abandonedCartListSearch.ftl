<!-- start searchBox -->
<#assign isAnon = parameters.isAnon!isAnon!"" >

<#assign isReg = parameters.isReg!isReg!"" >

<div class="entryRow">
  <div class="entry daterange">
    <label>${uiLabelMap.FromDateCaption}</label>
    <div class="entryInput from">
      <input class="dateEntry" type="text" name="dateFrom" maxlength="40" value="${parameters.dateFrom!dateFrom!""}"/>
    </div>
    <label class="tolabel">${uiLabelMap.ToCaption}</label>
    <div class="entryInput to">
      <input class="dateEntry" type="text" name="dateTo" maxlength="40" value="${parameters.dateTo!dateTo!""}"/>
    </div>
  </div>
</div>
<div class="entryRow">
  <div class="entry medium">
    <label>${uiLabelMap.AnonUsersCaption}</label>
    <div class="entryInput checkbox extraSmall">
      <input type="radio" class="radiobutton" name="isAnon" id="isAnon" value="isAnonItems" <#if isAnon?has_content && isAnon == "isAnonItems">checked</#if> />${uiLabelMap.CartsWithItemsLabel}
      <input type="radio" class="radiobutton" name="isAnon" id="isAnon" value="isAnonAll" <#if isAnon?has_content && isAnon == "isAnonAll">checked</#if> />${uiLabelMap.AllLabel}
      <input type="radio" class="radiobutton" name="isAnon" id="isAnon" value="isNoAnon" <#if isAnon?has_content && isAnon == "isNoAnon">checked</#if> />${uiLabelMap.NoneLabel}
    </div>
  </div>
</div>
<div class="entryRow">
  <div class="entry medium">
    <label>${uiLabelMap.RegUsersCaption}</label>
    <div class="entryInput checkbox extraSmall">
      <input type="radio" class="radiobutton" name="isReg" id="isReg" value="isRegItems" <#if isReg?has_content && isReg == "isRegItems">checked</#if> />${uiLabelMap.CartsWithItemsLabel}
      <input type="radio" class="radiobutton" name="isReg" id="isReg" value="isRegAll" <#if isReg?has_content && isReg == "isRegAll">checked</#if> />${uiLabelMap.AllLabel}
      <input type="radio" class="radiobutton" name="isReg" id="isReg" value="isNoReg" <#if isReg?has_content && isReg == "isNoReg">checked</#if> />${uiLabelMap.NoneLabel}
    </div>
  </div>
</div>
<!-- end searchBox -->
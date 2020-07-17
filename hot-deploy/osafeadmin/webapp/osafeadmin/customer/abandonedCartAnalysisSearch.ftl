<#assign nowTimestamp=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
<#assign defaultFromdate=Static["com.osafe.util.OsafeAdminUtil"].getMonthBackTimeStamp(1,preferredDateFormat)>
<!-- start searchBox -->
<div class="entryRow">
  <div class="entry daterange">
    <label>${uiLabelMap.FromDateCaption}</label>
    <div class="entryInput from">
      <input class="dateEntry" type="text" name="dateFrom" maxlength="40" value="${parameters.dateFrom!dateFrom!defaultFromdate?string(preferredDateFormat)!""}"/>
    </div>
    <label class="tolabel">${uiLabelMap.ToCaption}</label>
    <div class="entryInput to">
      <input class="dateEntry" type="text" name="dateTo" maxlength="40" value="${parameters.dateTo!dateTo!nowTimestamp?string(preferredDateFormat)!""}"/>
    </div>
  </div>
</div>
<!-- end searchBox -->
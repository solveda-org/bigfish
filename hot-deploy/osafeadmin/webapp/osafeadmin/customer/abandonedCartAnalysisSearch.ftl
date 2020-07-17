<#assign monthBackStamp=Static["com.osafe.util.OsafeAdminUtil"].getMonthBackTimeStamp(1)>
<#assign defaultTodate=Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(monthBackStamp,timeZone,locale)>
<#assign monthBackStamp=Static["com.osafe.util.OsafeAdminUtil"].getMonthBackTimeStamp(3)>
<#assign defaultFromdate=Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(monthBackStamp,timeZone,locale)>
<!-- start searchBox -->
<div class="entryRow">
  <div class="entry daterange">
    <label>${uiLabelMap.FromDateCaption}</label>
    <div class="entryInput from">
      <input class="dateEntry" type="text" name="dateFrom" maxlength="40" value="${parameters.dateFrom!dateFrom!defaultFromdate?string(entryDateTimeFormat)!""}"/>
    </div>
    <label class="tolabel">${uiLabelMap.ToCaption}</label>
    <div class="entryInput to">
      <input class="dateEntry" type="text" name="dateTo" maxlength="40" value="${parameters.dateTo!dateTo!defaultTodate?string(entryDateTimeFormat)!""}"/>
    </div>
  </div>
</div>
<!-- end searchBox -->
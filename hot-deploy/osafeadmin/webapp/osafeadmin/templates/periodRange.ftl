<script type="text/javascript">
    function setDateRange(dateFrom,dateTo) {
    var form = document.updatePeriodForm;
    form.elements['periodFrom'].value=dateFrom;
    form.elements['periodTo'].value=dateTo;
    form.submit();
    
    }
    function setCheckboxes(formName,checkBoxName) {
        // This would be clearer with camelCase variable names
        var allCheckbox = document.forms[formName].elements[checkBoxName + "all"];
        for(i = 0;i < document.forms[formName].elements.length;i++) {
            var elem = document.forms[formName].elements[i];
            if (elem.name.indexOf(checkBoxName) == 0 && elem.name.indexOf("_") < 0 && elem.type == "checkbox") {
                elem.checked = allCheckbox.checked;
            }
        }
    }
</script>

<!-- start displayBox -->
<div class="periodBox">
    <div class="boxBody period">
        <form action="<@ofbizUrl>${periodRequest}</@ofbizUrl>" method="post" name="updatePeriodForm">
         ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonFormHiddenFields")}
         <div class="entryRow">
            <div class="entry daterange">
             <label>${uiLabelMap.PeriodCaption}</label>
             <div class="entryInput from">
                    <input class="dateEntry" type="text" id="" name="periodFrom" maxlength="40" value="${periodFrom!request.getParameter('periodFrom')!""}"/>
             </div>
             <label class="tolabel">${uiLabelMap.ToLabel}</label>
             <div class="entryInput to">
                    <input class="dateEntry" type="text" name="periodTo" maxlength="40" value="${periodTo!request.getParameter('periodTo')!""}"/>
             </div>
            </div>
            <div class="entryButtons">
                <input type="submit" class="standardBtn action" name="periodBtn" value="${uiLabelMap.SearchBtn}" />
                <#if periodFromTs?exists && periodFromTs?has_content && periodToTs?exists && periodToTs?has_content >
                  <#assign subtractFromDay=Static["org.ofbiz.base.util.UtilDateTime"].addDaysToTimestamp(periodFromTs?if_exists,-1?int)>
                  <#assign subtractToDay=Static["org.ofbiz.base.util.UtilDateTime"].addDaysToTimestamp(periodToTs?if_exists,-1?int)>
                  <#assign addFromDay=Static["org.ofbiz.base.util.UtilDateTime"].addDaysToTimestamp(periodFromTs,1?int)>
                  <#assign addToDay=Static["org.ofbiz.base.util.UtilDateTime"].addDaysToTimestamp(periodToTs,1?int)>
	            </#if>
                <#assign nowTimestamp=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
                <#assign todayStart=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp)/>
                <#assign yesterdayStart=Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp, -1?int)/>
                <#assign yesterdayEnd=Static["org.ofbiz.base.util.UtilDateTime"].getDayEnd(nowTimestamp, -1?int)/>
                <#assign weekStart=Static["org.ofbiz.base.util.UtilDateTime"].getWeekStart(nowTimestamp)>
                <#assign weekStart=Static["org.ofbiz.base.util.UtilDateTime"].addDaysToTimestamp(weekStart,-1?int)>
                <#assign monthStart=Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)>
                <#assign yearStart=Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(nowTimestamp)>
                <#assign productionDate=Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue('osafeAdmin','production-date')>
                <#if productionDate?has_content>
                   <#assign productDateStart=Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp(productionDate)>
                <#else>
                   <#assign productDateStart=Static["org.ofbiz.base.util.UtilDateTime"].toTimestamp('06/02/2011 00:00:00')>
                </#if>
            </div>
	            <div class="dateSelectButtons">
	            	<input type="button" class="standardBtn dateSelect" name="SubtractDayBtn" value="${uiLabelMap.SubtractDayBtn}" <#if periodFromTs?exists && periodFromTs?has_content && periodToTs?exists && periodToTs?has_content > onClick="setDateRange('${subtractFromDay?if_exists?string(entryDateTimeFormat)}','${subtractToDay?if_exists?string(entryDateTimeFormat)}');" </#if>/>
	            	<input type="button" class="standardBtn dateSelect" name="AddDayBtn" value="${uiLabelMap.AddDayBtn}" <#if periodFromTs?exists && periodFromTs?has_content && periodToTs?exists && periodToTs?has_content > onClick="setDateRange('${addFromDay?if_exists?string(entryDateTimeFormat)}','${addToDay?if_exists?string(entryDateTimeFormat)}');" </#if>/>
	                <input type="button" class="standardBtn dateSelect" name="TodayBtn" value="${uiLabelMap.TodayBtn}" onClick="setDateRange('${todayStart?string(entryDateTimeFormat)}','${nowTimestamp?string(entryDateTimeFormat)}');"/>
	                <input type="button" class="standardBtn dateSelect" name="YesterdayBtn" value="${uiLabelMap.YesterdayBtn}" onClick="setDateRange('${yesterdayStart?string(entryDateTimeFormat)}','${yesterdayEnd?string(entryDateTimeFormat)}');"/>
					<input type="button" class="standardBtn dateSelect" name="ThisWeekBtn" value="${uiLabelMap.ThisWeekBtn}" onClick="setDateRange('${weekStart?string(entryDateTimeFormat)}','${nowTimestamp?string(entryDateTimeFormat)}');"/>
	                <input type="button" class="standardBtn dateSelect" name="MonthToDateBtn" value="${uiLabelMap.MonthToDateBtn}" onClick="setDateRange('${monthStart?string(entryDateTimeFormat)}','${nowTimestamp?string(entryDateTimeFormat)}');"/>
	                <input type="button" class="standardBtn dateSelect" name="YearToDateBtn" value="${uiLabelMap.YearToDateBtn}" onClick="setDateRange('${yearStart?string(entryDateTimeFormat)}','${nowTimestamp?string(entryDateTimeFormat)}');"/>
	                <input type="button" class="standardBtn dateSelect" name="DateAllBtn" value="${uiLabelMap.DateAllBtn}" onClick="setDateRange('${productDateStart?string(entryDateTimeFormat)}','${nowTimestamp?string(entryDateTimeFormat)}');"/>
	            </div>
        <#if showDeliveryOption?has_content && showDeliveryOption =="Y">
             <div class="entryRow">
             <div class="entry medium">
                 <label class="smallLabel">${uiLabelMap.DeliveryOptionCaption}</label>
                 <div class="entryInput checkbox medium">
                     <input type="checkbox" class="checkBoxEntry" name="srchall" id="srchall" value="Y" onclick="javascript:setCheckboxes('updatePeriodForm','srch')" <#if parameters.srchall?has_content>checked</#if> />${uiLabelMap.AllLabel}
                     <input type="checkbox" class="checkBoxEntry" name="srchShipTo" id="srchShipTo" value="Y" <#if parameters.srchShipTo?has_content>checked</#if>/>${uiLabelMap.ShipToDeliveryLabel}
                     <input type="checkbox" class="checkBoxEntry" name="srchStorePickup" id="srchStorePickup" value="Y" <#if parameters.srchStorePickup?has_content>checked</#if>/>${uiLabelMap.StorePickupDeliveryLabel}
                 </div>
             </div>
             </div>
        </#if> 
         </div>
        </form>
    </div>
</div>
<!-- end displayBox -->

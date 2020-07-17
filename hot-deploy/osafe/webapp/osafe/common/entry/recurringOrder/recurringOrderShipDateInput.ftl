<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<#assign shipDate = parameters.shipDate!shipDate!/>
<div class="${request.getAttribute("attributeClass")!}">
    <label>${uiLabelMap.ShipDateCaption}</label>
    <div class="entryField">
    	<input class="dateEntry" type="text" name="shipDate" maxlength="40" value="<#if shipDateIsValid?exists && shipDateIsValid?has_content && shipDateIsValid == "N">${shipDate!""}<#else><#if shipDate?has_content>${(Static["com.osafe.util.Util"].convertDateTimeFormat(shipDate, FORMAT_DATE))!""}</#if></#if>"/>
    	<@fieldErrors fieldName="shipDate"/>
    </div>
</div>

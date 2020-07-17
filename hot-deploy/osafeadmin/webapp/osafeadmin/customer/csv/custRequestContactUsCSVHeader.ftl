<#if custRequestList?exists && custRequestList?has_content>
  ${uiLabelMap.IdLabel},${uiLabelMap.DateContactLabel},${uiLabelMap.LastNameLabel},${uiLabelMap.FirstNameLabel}, ${uiLabelMap.ContactReasonLabel},${uiLabelMap.EmailLabel},${uiLabelMap.ContactPhoneLabel},${StringUtil.wrapString(uiLabelMap.OrderNoLabel)},${uiLabelMap.CommentLabel}, ${uiLabelMap.IsDownloadedLabel}, ${uiLabelMap.DateDownloadedLabel}
</#if>

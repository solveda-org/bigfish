${sections.render('orderFinancialSummaryBoxBody')}
<#if statusId == "ORDER_CANCELLED">
    ${sections.render('orderCancelFinancialAdjustmentBoxBody')!}
</#if>
<#if statusId == "PRODUCT_RETURN">
    ${sections.render('productReturnFinancialAdjustmentBoxBody')!}
</#if>

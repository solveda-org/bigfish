<#if paraOrderPaymentPreferenceId?has_content && paraOrderPaymentPreferenceId == orderPaymentPreferenceId>
    <#assign sliding = "open"/>
<#else>
    <#assign sliding = "close"/>
</#if>
<div class="displayListBox detailInfo <#if sliding = 'close'>slidingClose<#else>slidingOpen</#if>">
    <div class="header"><h2>${StringUtil.wrapString(detailInfoBoxHeading!)}</h2></div>
    <#if paymentMethodInfoHeading?exists && paymentMethodInfoHeading?has_content && !( methodType.equals("CASH")|| methodType.equals("COMPANY_CHECK") || methodType.equals("EXT_COD")) >
        <div class="boxBody">
            <div class="heading">${paymentMethodInfoHeading!}</div>
            ${sections.render('paymentMethodInfoBoxBody')!}
        </div>
    </#if>
    <#if orderPaymentPreferenceHeading?exists && orderPaymentPreferenceHeading?has_content >
        <div class="boxBody">
            <div class="heading">${orderPaymentPreferenceHeading!}</div>
            ${sections.render('orderPaymentPreferenceBoxBody')!}
        </div>
    </#if>
    <#if paymentInfoHeading?exists && paymentInfoHeading?has_content && !( methodType.equals("CASH")|| methodType.equals("COMPANY_CHECK") || methodType.equals("EXT_COD"))>
        <div class="boxBody">
            <div class="heading">${paymentInfoHeading!}</div>
            ${sections.render('paymentInfoBoxBody')!}
        </div>
    </#if>
    <#if paymentGatewayResponseHeading?exists && paymentGatewayResponseHeading?has_content && !( methodType.equals("CASH")|| methodType.equals("COMPANY_CHECK") || methodType.equals("EXT_COD"))>
        <#if gatewayResponseInfoList?has_content>
            <#list gatewayResponseInfoList as gatwayInfo>
                ${setRequestAttribute("gatewayResponseInfo",gatwayInfo)}
                <div class="boxBody">
                    <div class="heading">${paymentGatewayResponseHeading!}</div>
                    ${sections.render('paymentGatewayResponseBoxBody')!}
                </div>
            </#list>
        </#if> 
    </#if>
</div>
   
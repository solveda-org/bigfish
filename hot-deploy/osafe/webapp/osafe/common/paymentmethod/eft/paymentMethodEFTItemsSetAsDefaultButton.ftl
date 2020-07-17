<li class="${request.getAttribute("attributeClass")!}">
    <div>
        <#if !(partyProfileDefault?has_content && partyProfileDefault.defaultPayMeth?has_content && partyProfileDefault.defaultPayMeth == savedEftAccount.paymentMethodId)>
            <a class="standardBtn action" href="<@ofbizUrl>updateDefaultEftPmtMethod?paymentMethodId=${savedEftAccount.paymentMethodId!""}</@ofbizUrl>"><span>${uiLabelMap.SetAsDefaultLabel}</span></a>
        </#if>
    </div>
</li>
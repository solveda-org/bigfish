<li class="${request.getAttribute("attributeClass")!}">
  <div>
    <label>${uiLabelMap.BankAcctNoCaption}</label>
    <#assign accountNumber=savedEftAccount.accountNumber?if_exists/>
    <#assign accountNumberDisplay = "">
    <#assign size = accountNumber?length - 4>
    <#if (size > 0)>
        <#list 0 .. size-1 as charno>
           <#assign accountNumberDisplay = accountNumberDisplay + "*">
        </#list>
        <#assign accountNumberDisplay = accountNumberDisplay + "-">
        <#assign accountNumberDisplay = accountNumberDisplay + accountNumber[size .. size + 3]>
    <#else>
        <#assign accountNumberDisplay = accountNumber>
    </#if>
    <a href="<@ofbizUrl>eCommerceEditEftAccountInfo?paymentMethodId=${savedEftAccount.paymentMethodId!""}&amp;mode=edit</@ofbizUrl>"><span>${accountNumberDisplay}</span></a>
  </div>
</li>
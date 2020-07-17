<li class="${request.getAttribute("attributeClass")!}">
  <div>
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
    <a class="standardBtn delete" href="javascript:setDeleteId('${savedEftAccount.paymentMethodId}','js_paymentMethodId');deleteConfirm('${accountNumberDisplay}');"><span>${uiLabelMap.DeleteLabel}</span></a>
  </div>
</li>





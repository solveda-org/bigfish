<div class="entryButtons<#if isCheckoutPage?exists && isCheckoutPage! == "true"> submitOrder</#if>">
  <#if isCheckoutPage?exists && isCheckoutPage! == "true">
    <a class="standardBtn action" href="javascript:$('${formName!"entryForm"}').submit()">${uiLabelMap.SubmitOrderBtn} -></a>
  <#else>
    <a class="standardBtn action" href="javascript:$('${formName!"entryForm"}').submit()">${uiLabelMap.ContinueBtn}</a>
  </#if>
</div>


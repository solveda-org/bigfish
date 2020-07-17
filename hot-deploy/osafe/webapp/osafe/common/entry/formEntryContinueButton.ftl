<div class="container button<#if isCheckoutPage?exists && isCheckoutPage! == "true"> submitOrder</#if>">
  <#if isCheckoutPage?exists && isCheckoutPage! == "true">
    <a class="standardBtn submitOrder" href="javascript:$('${formName!"entryForm"}').submit()">${uiLabelMap.SubmitOrderBtn} -></a>
  <#else>
    <a class="standardBtn action" href="javascript:$('${formName!"entryForm"}').submit()">${formContinueButton!uiLabelMap.ContinueBtn}</a>
  </#if>
</div>


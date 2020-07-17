<div class="container button">
    <#if backAction?exists && backAction?has_content>
        <a class="standardBtn negative" href="<@ofbizUrl>${backAction!}</@ofbizUrl>">${uiLabelMap.CommonBack}</a>
    </#if>
    <a class="standardBtn action" href="javascript:$('${formName!"entryForm"}').submit()">${formButton!uiLabelMap.ContinueBtn}</a>
</div>


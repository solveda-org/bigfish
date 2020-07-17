<div class="${request.getAttribute("attributeClass")!}">
  <#if backAction?exists && backAction?has_content>
      <a class="standardBtn negative" href="<@ofbizUrl>${backAction!}</@ofbizUrl>"><span>${uiLabelMap.CommonBack}</span></a>
  </#if>
</div>

<div class="container button">
  <#if cancelButtonVisible?has_content && cancelButtonVisible == "Y">
    <a href="<@ofbizUrl>${cancelButtonUrl!}</@ofbizUrl>" class="${cancelButtonClass!}">${cancelButtonDescription!}</a>
    <span>${CancelStoreSelectInfo!}</span>
  </#if>
</div>
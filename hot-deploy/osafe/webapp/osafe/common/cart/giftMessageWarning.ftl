<#if totalQuantityWithGiftMess?exists && totalQuantityWithGiftMess?has_content && totalQuantityAllowGiftMess?exists && totalQuantityAllowGiftMess?has_content>
  <#assign totalQuantityWithoutGiftMess = totalQuantityAllowGiftMess - totalQuantityWithGiftMess>
  <#if totalQuantityAllowGiftMess &gt; totalQuantityWithGiftMess>
    <#assign giftMessageWarningMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("totalItems", totalQuantityAllowGiftMess, "itemsWithoutGiftMessage", totalQuantityWithoutGiftMess)>
    <#assign giftMessageWarningText = Static["org.ofbiz.base.util.UtilProperties"].getMessage("OSafeUiLabels","GiftMessageWarning",giftMessageWarningMap, locale) />
  <div class="${request.getAttribute("attributeClass")!}">
      <div class="displayBox">
        <span>${giftMessageWarningText!}</span>
      </div>
  </div>    
  </#if>
</#if>

<!-- Pixel Tracking -->
<#if pixelTrackingList?has_content>
  <#list pixelTrackingList as trackingListItem>
    <#assign pixelContent = trackingListItem.getRelatedOne("Content")!/>
    <#if pixelContent?has_content && ((pixelContent.statusId)?if_exists == "CTNT_PUBLISHED")>
      <#assign pixelScope = trackingListItem.pixelScope/>
      <#if orderConfirmed?has_content && orderConfirmed == "Y">
        <#if pixelScope == "ORDER_CONFIRM">
          <@renderContentAsText contentId="${trackingListItem.contentId}" ignoreTemplate="true"/>
        </#if>
      <#else>
        <#if pixelScope == "ALL_EXCEPT_ORDER_CONFIRM">
          <@renderContentAsText contentId="${trackingListItem.contentId}" ignoreTemplate="true"/>
        </#if>
      </#if>
      <#if pixelScope =="ALL">
        <@renderContentAsText contentId="${trackingListItem.contentId}" ignoreTemplate="true"/>
      </#if>
    </#if>
  </#list>
</#if>
<!-- Pixel Tracking -->
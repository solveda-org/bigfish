
<!-- Pixel Tracking -->
<#if pixelTrackingList?has_content>
  <#list pixelTrackingList as trackingListItem>
    <#assign pixelContent = trackingListItem.getRelatedOneCache("Content")!/>
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
	  <#if showCartPageTagging?has_content && showCartPageTagging == "Y">
	    <#if pixelScope == "SHOW_CART">
	      <@renderContentAsText contentId="${trackingListItem.contentId}" ignoreTemplate="true"/>
	    </#if>
	  </#if>
	  <#if (newAccountCreated?has_content && newAccountCreated =="Y") && (createAccountPageTagging?has_content && createAccountPageTagging == "Y")>
	    <#if pixelScope == "CREATE_ACCOUNT_SUCCESS">
	      <@renderContentAsText contentId="${trackingListItem.contentId}" ignoreTemplate="true"/>
	    </#if>
	  </#if>
	  <#if emailSubscriberPageTagging?has_content && emailSubscriberPageTagging == "Y">
	    <#if pixelScope == "SUBSCRIBE_NEWSLETTER_SUCCESS">
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


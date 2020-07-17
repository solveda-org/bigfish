
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
      <#if pixelScope =="SPECIFIC_URL">
          <#assign pixelUrl = trackingListItem.pixelUrl!"" />
          <#if pixelUrl?has_content>
              <#assign currentRequest = parameters.get("_SERVER_ROOT_URL_")!"">
              <#if parameters.get("javax.servlet.forward.request_uri")?has_content>
                  <#assign currentRequest = currentRequest.concat(parameters.get("javax.servlet.forward.request_uri")?if_exists) >
              <#else>
                  <#assign currentRequest = currentRequest.concat(request.getRequestURI()?if_exists) >
              </#if>
              <#if (currentRequest.indexOf(";jsessionid") > 0) >
                  <#assign currentRequest = currentRequest.substring(0, currentRequest.indexOf(";jsessionid"))>
              </#if>
              <#if (currentRequest.indexOf("?") > 0) >
                  <#assign currentRequest = currentRequest.substring(0, currentRequest.indexOf("?"))>
              </#if>
              <#if currentRequest?has_content && currentRequest.equals(pixelUrl)>
                  <@renderContentAsText contentId="${trackingListItem.contentId}" ignoreTemplate="true"/>
              </#if>
          </#if>
      </#if>
    </#if>
  </#list>
</#if>
<!-- Pixel Tracking -->


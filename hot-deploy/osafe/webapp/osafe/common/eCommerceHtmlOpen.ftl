<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<#assign initialLocale = locale.toString()>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta content="all,index,follow" name="robots"/>
	<#assign titleWithOutHtml = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(metaTitle!title!productStore.title!"")!"") />
	<#assign seoTitle = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(globalContext.SEO_STORE_TITLE!productStore.storeName!"")!"") />
	<#assign seoTitlePosition = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(globalContext.SEO_STORE_TITLE_POSITION!"")!"") />
	<#if seoTitlePosition == "SUFFIX">
		<title><#if titleWithOutHtml?has_content>${titleWithOutHtml!""}</#if> ${(seoTitle)?if_exists}  </title>
	<#else>
		<title>${(seoTitle)?if_exists} <#if titleWithOutHtml?has_content> ${titleWithOutHtml!""}</#if> </title>
	</#if>
  <#if layoutSettings.VT_SHORTCUT_ICON?has_content>
    <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
  <#elseif layoutSettings.shortcutIcon?has_content>
    <#assign shortcutIcon = layoutSettings.shortcutIcon/>
  </#if>
  <#if shortcutIcon?has_content>
    <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
  </#if>
  <#if layoutSettings.javaScripts?has_content>
    <#--layoutSettings.javaScripts is a list of java scripts. -->
    <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
    <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
    <#list layoutSettings.javaScripts as javaScript>
      <#if javaScriptsSet.contains(javaScript)>
        <#assign nothing = javaScriptsSet.remove(javaScript)/>
        <script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
      </#if>
    </#list>
  </#if>
  <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
      <script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
    </#list>
  </#if>
  <#if layoutSettings.styleSheets?has_content>
    <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.styleSheets as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#if layoutSettings.VT_STYLESHEET?has_content>
    <#list layoutSettings.VT_STYLESHEET as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
   <#assign googleSiteVer =Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("osafe", "google-site-verification")/>
   <meta name="google-site-verification" content="${GA_SITE_VERIFICATION?default(googleSiteVer)}"/>

   <#if GA_SITE_VERIFY_V1?exists?string?has_content>
      <meta name="verify-v1" content="${GA_SITE_VERIFY_V1!""}"/>
   </#if>

   <#assign strippedMetaDescription = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(metaDescription!productStore.subtitle!"",SEO_META_DESC_LEN!"")!"") />
   <meta name="description" content="${strippedMetaDescription!""}"/>

    <#assign strippedMetaKeywords = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(metaKeywords!productStore.subtitle!"",SEO_META_KEY_LEN!"")!"") />
    <meta name="keywords" content="${strippedMetaKeywords!""}"/>

    <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(PCA_ACTIVE_FLAG!"") && loadPca?has_content && loadPca == "Y">
        <#assign osafeCapturePlus = Static["com.osafe.captureplus.OsafeCapturePlus"].getInstance(globalContext.productStoreId!) />
        <#if osafeCapturePlus.isNotEmpty()>
            ${setRequestAttribute("osafeCapturePlus",osafeCapturePlus)}
        </#if>
    </#if>
</head>
<body>
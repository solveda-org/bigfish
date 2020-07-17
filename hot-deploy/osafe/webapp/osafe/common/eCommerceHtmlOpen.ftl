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
    <!-- page Tag at Head Start -->
    
    <#if pageTrackingList?has_content && pageTrackingList.size() gt 0>
        ${setRequestAttribute("pixelPagePosition","HEAD_START")}
        ${screens.render("component://osafe/widget/CommonScreens.xml#pixelTracking")}
    </#if> 
    <meta content="all,index,follow" name="robots"/>

	<#assign titleWithOutHtml = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(metaTitle!title!productStore.title!"")!"") />
	<#assign SEO_TITLE = Static["com.osafe.util.Util"].getProductStoreParm(request,"SEO_STORE_TITLE")!""/>
	<#assign seoTitle = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(SEO_TITLE!productStore.storeName!"")!"") />
	<#assign SEO_TITLE_POSITION = Static["com.osafe.util.Util"].getProductStoreParm(request,"SEO_STORE_TITLE_POSITION")!""/>
    <#assign seoTitlePosition = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(SEO_TITLE_POSITION!"")!"") />
	<#assign GA_SITE_VERIFICATION = Static["com.osafe.util.Util"].getProductStoreParm(request,"GA_SITE_VERIFICATION")!""/>
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

  <#-- STYLE SHEETS -->
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
   
  <#-- META DATA -->
    <#assign strippedMetaDescription = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(metaDescription!productStore.subtitle!"",Static["com.osafe.util.Util"].getProductStoreParm(request,"SEO_META_DESC_LEN")!)!"")/>
    <meta name="description" content="${strippedMetaDescription!""}"/>
    <#assign strippedMetaKeywords = StringUtil.wrapString(Static["com.osafe.util.Util"].stripHTMLInLength(metaKeywords!productStore.subtitle!"",Static["com.osafe.util.Util"].getProductStoreParm(request,"SEO_META_KEY_LEN")!)!"")/>
    <meta name="keywords" content="${strippedMetaKeywords!""}"/>
    <#if GA_SITE_VERIFICATION?has_content>
       <meta name="google-site-verification" content="${GA_SITE_VERIFICATION!}"/>
    </#if>
    
    <#if canonicalUrl?has_content>
        <link rel="canonical" href="${StringUtil.wrapString(canonicalUrl)}"/>
    </#if>
    <#if plpPagingLinkPrev?has_content>
        <link rel="prev" href="${StringUtil.wrapString(plpPagingLinkPrev)}"/>
    </#if>
    <#if plpPagingLinkNext?has_content>
        <link rel="next" href="${StringUtil.wrapString(plpPagingLinkNext)}"/>
    </#if>
  <#-- BF CUSTOM HEADER TAG CONTENT (SI_HEAD_TAG)- typically used for adding the mobile css-->
    ${screens.render("component://osafe/widget/EcommerceContentScreens.xml#SI_HEAD_TAG")}    

  <#-- JAVA SCRIPTS -->
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
  
  
 
  <#-- BF CUSTOM JAVA SCRIPTS -->
     <#-- CAPTURE PLUS -->
    <#assign addressVerificationMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"ADDRESS_VERIFICATION_METHOD")!""/>
    <#if addressVerificationMethod?has_content && loadPca?has_content && loadPca == "Y">
        <#if addressVerificationMethod.toUpperCase() == "PCA">
            <#assign osafeCapturePlus = Static["com.osafe.captureplus.OsafeCapturePlus"].getInstance(globalContext.productStoreId!) />
            <#if osafeCapturePlus.isNotEmpty()>
                ${setRequestAttribute("osafeCapturePlus",osafeCapturePlus)}
            </#if>
        </#if>
    </#if>
     <#-- REVOO -->
    <#assign reviewMethod = Static["com.osafe.util.Util"].getProductStoreParm(request,"REVIEW_METHOD")!""/>
    <#if reviewMethod?has_content >
	    <#if reviewMethod.toUpperCase() == "REEVOO" >
	        <#assign reevooJsurl = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_JS_URL")!"">
	        <#assign reevooTrkref = Static["com.osafe.util.Util"].getProductStoreParm(request,"REEVOO_TRKREF")!"">
	        <#assign reevooJsurl = reevooJsurl.concat("/").concat(reevooTrkref).concat(".js?async=true")>
	        <script id="reevoomark-loader">
	            (function() {
	                var myscript = document.createElement('script');
	                myscript.type = 'text/javascript';
	                myscript.src=('${StringUtil.wrapString(reevooJsurl)}');
	                var s = document.getElementById('reevoomark-loader');
	                s.parentNode.insertBefore(myscript, s);
	            })();
	        </script>
	    </#if>
    </#if>
    <!-- page Tag at Head End -->
    <#if pageTrackingList?has_content && pageTrackingList.size() gt 0>
        ${setRequestAttribute("pixelPagePosition","HEAD_END")}
        ${screens.render("component://osafe/widget/CommonScreens.xml#pixelTracking")}
    </#if>
  </head>
<body>
<!-- page Tag at Body Start -->
<#if pageTrackingList?has_content && pageTrackingList.size() gt 0>
    ${setRequestAttribute("pixelPagePosition","BODY_START")}
    ${screens.render("component://osafe/widget/CommonScreens.xml#pixelTracking")}
</#if>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
            <meta http-equiv="content-type" content="text/html; charset=utf-8" />
            <meta http-equiv="imagetoolbar" content="no" />
            <#assign productStoreId = session.getAttribute("productStoreId")?if_exists>
            <#if productStore?has_content>
	            <#if pageTitle?has_content>
	               <#assign titleOfPage = Static["com.osafe.util.OsafeAdminUtil"].stripHTML(pageTitle)>
	            </#if>
	            <title>${productStore.storeName!""}&nbsp;:&nbsp;${uiLabelMap.eCommerceAdminModuleTitle}
	            <#if pageTitle?has_content>, ${StringUtil.wrapString(titleOfPage)}</#if>
                </title>
            <#else>
               <title>${uiLabelMap.eCommerceAdminModuleTitle}
                 <#if pageTitle?has_content>, ${StringUtil.wrapString(pageTitle)}</#if>
               </title>
            </#if>
            <#if layoutSettings.VT_SHORTCUT_ICON?has_content>
              <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
            <#elseif layoutSettings.shortcutIcon?has_content>
              <#assign shortcutIcon = layoutSettings.shortcutIcon/>
            </#if>
            <#if shortcutIcon?has_content>
              <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
            </#if>
            <meta name="description" content="${uiLabelMap.eCommerceAdminModuleTitle}" />
            <meta name="keywords" content="" />
            <meta name="language" content="en" />
            <meta name="robots" content="noindex, nofollow" />
            <#if (layoutSettings.styleSheets)?has_content>
                <#list layoutSettings.styleSheets as styleSheet>
                     <link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" />
                </#list>
            </#if>
            <#if layoutSettings.VT_STYLESHEET?has_content>
              <#list layoutSettings.VT_STYLESHEET as styleSheet>
                <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
              </#list>
            </#if>

        <#if (layoutSettings.javaScripts)?has_content>
            <#list layoutSettings.javaScripts as javaScript>
                <script type="text/javascript" src="<@ofbizContentUrl>${javaScript}</@ofbizContentUrl>"></script>
            </#list>
        </#if>

        <#assign addressVerificationMethod = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"ADDRESS_VERIFICATION_METHOD")!""/>
        <#if addressVerificationMethod?has_content && loadPca?has_content && loadPca == "Y">
            <#if addressVerificationMethod.toUpperCase() == "PCA">
                <#assign osafeCapturePlus = Static["com.osafe.captureplus.OsafeCapturePlus"].getInstance(globalContext.productStoreId!) />
                <#if osafeCapturePlus.isNotEmpty()>
                    ${setRequestAttribute("osafeCapturePlus",osafeCapturePlus)}
                </#if>
            </#if>
        </#if>
    </head>
    <body class="all">
        <div id="mainContainer">
            <div id="bodyContainer">
                <div id="header">
                    ${sections.render('siteLogo')}
                    <#if !Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmFalse(request,"ADM_SHOW_DAILY_COUNTER")>  
                    	${sections.render('dailySalesCounter')}
                    </#if>
                    ${sections.render('siteInfo')}
                    ${sections.render('navigationBar')}
                </div>
                <div id="pageContainer">
                	${sections.render('navigationBarBreadcrumb')}
                    ${sections.render('pageHeading')}
                    <#if showLastOrder?has_content && showLastOrder =="Y">
                    	<#if !Static["com.osafe.util.OsafeAdminUtil"].isProductStoreParmFalse(request,"ADM_SHOW_DASHBOARD")> 
                        	${sections.render('lastOrder')}
                        </#if>
                    </#if>
                    <#if hideMainPageMessages?has_content && hideMainPageMessages =="Y">
                    <#else>
                     ${sections.render('messages')}
                    </#if>
                     ${sections.render('commonJquery')}
                     ${sections.render('body')}
                </div>
            </div>
        </div>
    </body>
</html>
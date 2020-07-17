<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
            <meta http-equiv="content-type" content="text/html; charset=utf-8" />
            <meta http-equiv="imagetoolbar" content="no" />
            <#assign productStoreId = session.getAttribute("productStoreId")?if_exists>
            <#if productStore?has_content>
               <title>${productStore.storeName!""}&nbsp;:&nbsp;${uiLabelMap.eCommerceAdminModuleTitle}</title>
            <#else>
               <title>${uiLabelMap.eCommerceAdminModuleTitle}</title>
            </#if>
            <title>${uiLabelMap.eCommerceAdminModuleTitle}</title>
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
            <!--[if IE 8]>
                <link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>/html/admin/css/ie8.css</@ofbizContentUrl>" />
            <![endif]-->

        <#if (layoutSettings.javaScripts)?has_content>
            <#list layoutSettings.javaScripts as javaScript>
                <script type="text/javascript" src="<@ofbizContentUrl>${javaScript}</@ofbizContentUrl>"></script>
            </#list>
        </#if>
    </head>
    <body class="all">
        <div id="mainContainer">
            <div id="bodyContainer">
                <div id="header">
                    ${sections.render('siteLogo')}
                    ${sections.render('dailySalesCounter')}
                    ${sections.render('siteInfo')}
                    ${sections.render('navigationBar')}
                </div>
                <div id="pageContainer">
                     ${sections.render('pageHeading')}
                    <#if showLastOrder?has_content && showLastOrder =="Y">
                        ${sections.render('lastOrder')}
                    </#if>
                     ${sections.render('messages')}
                    <#if showPeriod?has_content && showPeriod =="Y">
                        ${sections.render('periodRange')}
                    </#if>
                     ${sections.render('commonJquery')}
                     ${sections.render('body')}
                </div>
            </div>
        </div>
    </body>
</html>
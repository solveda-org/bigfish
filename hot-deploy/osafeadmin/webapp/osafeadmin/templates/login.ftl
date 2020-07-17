<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=utf-8" />
        <meta http-equiv="imagetoolbar" content="no" />

        <title>${uiLabelMap.eCommerceAdminApplicationTitle}</title>

        <meta name="description" content="${uiLabelMap.eCommerceAdminApplicationTitle}" />
        <meta name="keywords" content="" />
        <meta name="language" content="en" />
        <meta name="robots" content="noindex, nofollow" />

          <link rel="shortcut icon" type="image/x-icon" href="<@ofbizContentUrl>/osafe_admin_theme/images/admin/header_image.gif</@ofbizContentUrl>" />

        <#if (layoutSettings.styleSheets)?has_content>
            <#list layoutSettings.styleSheets as styleSheet>
                <link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" />
            </#list>
        </#if>

        <#if (layoutSettings.javaScripts)?has_content>
            <#list layoutSettings.javaScripts as javaScript>
                <script type="text/javascript" src="<@ofbizContentUrl>${javaScript}</@ofbizContentUrl>"></script>
            </#list>
        </#if>
    </head>
    <body>
        <div id="loginNavigation">
            <div class="statement">
                ${sections.render('messages')}
            </div>
            ${sections.render('body')}
        </div>
    </body>
</html>


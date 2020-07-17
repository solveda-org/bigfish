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

    <#if (layoutSettings.VT_STYLESHEET)?has_content>
      <#list layoutSettings.VT_STYLESHEET as styleSheet>
        <link rel="stylesheet" type="text/css" href="<@ofbizContentUrl>${styleSheet!}</@ofbizContentUrl>" />
      </#list>
    </#if>

  </head>
  <body>
    <div id="previewContentBody">
      ${sections.render('body')}
    </div>
  </body>
</html>


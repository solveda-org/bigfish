<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        ${sections.render('emailHead')}
        ${sections.render('emailHeadStyles')}
    </head>
    <body ${emailBodyStyleAttribute!""}>
       ${sections.render('emailBodyStyles')}
      <div class="emailBodyHeader">
          ${sections.render('emailBodyHeader')}
      </div>
      <div class="emailBody" ${StringUtil.wrapString(emailBodyBodyStyleAttribute!"")}>
          ${sections.render('emailBody')}
      </div>
      <div class="emailFooter">
          ${sections.render('emailBodyFooter')}
      </div>
    </body>
</html>
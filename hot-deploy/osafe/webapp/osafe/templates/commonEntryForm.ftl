${sections.render('entryFormJS')}
<form method="post" action="<@ofbizUrl>${formAction!""}${previousParams?if_exists}</@ofbizUrl>" id="${formName!"entryForm"}" name="${formName!"entryForm"}">
  ${sections.render('entryForm')}
  ${sections.render('entryFormButtons')}
  ${sections.render('capturePlusJs')?if_exists}
</form>

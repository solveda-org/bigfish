${sections.render('entryFormJS')?if_exists}
${sections.render('checkoutJS')?if_exists}
<form method="post" action="<@ofbizUrl>${formAction!""}${previousParams?if_exists}</@ofbizUrl>" id="${formName!""}" name="${formName!""}">
    <input type="hidden" id="checkoutpage" name="checkoutpage" value="${checkoutPage!}"/>
    ${sections.render('pageMessages')?if_exists}
    ${sections.render('pageTopContent')?if_exists}
    ${sections.render('cartBody')?if_exists}
    ${sections.render('pageEndContent')?if_exists}
</form>
${sections.render('commonDialog')?if_exists}
${sections.render('capturePlusJs')?if_exists}

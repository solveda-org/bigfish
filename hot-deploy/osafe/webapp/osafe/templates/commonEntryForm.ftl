${sections.render('entryFormJS')}
<form method="post" <#if encTypeMulti?exists && encTypeMulti?has_content && encTypeMulti == "Y">enctype="multipart/form-data"</#if> action="<@ofbizUrl>${formAction!""}${previousParams?if_exists}</@ofbizUrl>" id="${formName!"entryForm"}" name="${formName!"entryForm"}">
    <#if userLogin?has_content>
        <#assign partyId = userLogin.partyId!"">
    </#if>
    <input type="hidden" name="partyId" value="${partyId!""}"/>
    <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
    ${sections.render('entryForm')}
    ${sections.render('entryFormButtons')}
    ${sections.render('capturePlusJs')?if_exists}
</form>

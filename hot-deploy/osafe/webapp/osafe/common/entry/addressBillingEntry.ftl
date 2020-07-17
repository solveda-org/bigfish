<div class="${request.getAttribute("attributeClass")!}">
<div id="js_${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <input type="hidden" id="emailProductStoreId" name="emailProductStoreId" value="${productStoreId!""}"/>
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
</div>
</div>
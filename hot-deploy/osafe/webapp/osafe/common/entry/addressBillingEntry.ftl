<div id="${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <input type="hidden" name="emailProductStoreId" value="${productStoreId!""}"/>
    <input type="hidden" name="${fieldPurpose?if_exists}_ATTN_NAME" value="Billing Address"/>
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
</div>
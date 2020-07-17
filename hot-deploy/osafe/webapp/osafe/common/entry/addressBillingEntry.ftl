<div id="${fieldPurpose?if_exists}_ADDRESS_ENTRY" class="displayBox">
    <input type="hidden" id="emailProductStoreId" name="emailProductStoreId" value="${productStoreId!""}"/>
    <input type="hidden" id="${fieldPurpose?if_exists}_ATTN_NAME" name="${fieldPurpose?if_exists}_ATTN_NAME" value="Billing Address"/>
    <#include "component://osafe/webapp/osafe/common/entry/commonAddressEntry.ftl"/>
</div>
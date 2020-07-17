<div class="displayBox">
<div id="storeLocator" class="storeLocator">
    <div class="storeSearch">
        ${sections.render('searchBoxBody')?if_exists}
    </div>
    <div class="storeMap">
        ${sections.render('commonGeoLocationChart')?if_exists}
    </div>
    <div class="storeList">
        ${sections.render('listBoxBody')?if_exists}
    </div>
</div>
<#if pickupStoreButtonVisible?has_content && pickupStoreButtonVisible == "Y">
  ${sections.render('ecommerceCommonButtons')?if_exists}
</#if>
</div>
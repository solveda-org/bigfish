<!-- start customerDetailBillingInfo.ftl -->
<div id="${fieldPurpose?if_exists}_addressEntry">
    <div class="infoRow row">
        <div class="header"><h2>${uiLabelMap.BillingAddressHeading}</h2></div>
    </div>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonAddressEntry")}
</div>
<!-- end customerDetailBillingInfo.ftl -->
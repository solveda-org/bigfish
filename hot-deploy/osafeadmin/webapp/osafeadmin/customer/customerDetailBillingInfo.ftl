<!-- start customerDetailBillingInfo.ftl -->
<div id="${fieldPurpose?if_exists}_addressEntry">
     <div class="heading">
       <h3>${uiLabelMap.BillingAddressHeading}</h3>
     </div>
    ${screens.render("component://osafeadmin/widget/CommonScreens.xml#commonAddressEntry")}
</div>
<!-- end customerDetailBillingInfo.ftl -->
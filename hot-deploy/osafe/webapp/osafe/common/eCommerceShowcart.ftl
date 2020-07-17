 <!-- DIV for Displaying Cart content STARTS here -->
${screens.render("component://osafe/widget/EcommerceScreens.xml#entryFormJS")}
    <div class="showcart">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#showCartDivSequence")}
    </div>
<!-- DIV for Displaying Cart content ENDS here -->  
<form method="post" id="${formName!"entryForm"}" name="${formName!"entryForm"}">
</form>
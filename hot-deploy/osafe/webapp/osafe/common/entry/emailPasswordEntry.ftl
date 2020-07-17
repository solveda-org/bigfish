<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="${request.getAttribute("attributeClass")!}">
    <div id="emailPasswordEntry" class="displayBox">
        <h3>${uiLabelMap.EmailAddressHeading}</h3>
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#createAccountDivSequence")}
    </div>
</div>

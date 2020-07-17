<#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="createAccountEntry" class="displayBox">
<h3>${uiLabelMap.CreateAnAccountHeading}</h3>
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#createAccountDivSequence")}
</div>
</#if>
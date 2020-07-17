<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div class="${request.getAttribute("attributeClass")!}">
  <#if !parameters.isFBLogin?has_content || (parameters.isFBLogin?has_content && parameters.isFBLogin !="Y")>
    <div id="emailPasswordEntry" class="displayBox">
        <h3>${uiLabelMap.EmailAddressHeading}</h3>
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#createAccountDivSequence")}
    </div>
  </#if>
</div>

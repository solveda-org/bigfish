<div class="lightCart">
  <form id="${formNametemp!}" name="${formNametemp!}" action="/online/control/" method="post">
    <input type="hidden" name="removeSelected" value="false"/>
    <#if !userLogin?has_content || userLogin.userLoginId == "anonymous">
        <input type="hidden" name="guest" value="guest"/>
    </#if>
    ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#showLightCartDivSequence")}
  </form>
</div>

<#if parameters.productId?has_content>
    <input type="hidden" name="productId" value="${parameters.productId!}"/>
    <div id="js_emailAFriendEntry" class="displayBox">
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#emailAFriendDivSequence")}
    </div>
</#if>
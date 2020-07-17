<!-- page Tag at Body End -->
<#if pageTrackingList?has_content && pageTrackingList.size() gt 0>
    ${setRequestAttribute("pixelPagePosition","BODY_END")}
    ${screens.render("component://osafe/widget/CommonScreens.xml#pixelTracking")}
</#if>
</body>
</html>
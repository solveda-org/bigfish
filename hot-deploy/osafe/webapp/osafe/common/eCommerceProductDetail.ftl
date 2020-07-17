<#-- virtual product javascript -->
<#if currentProduct?exists && currentProduct?has_content>
${virtualJavaScript?if_exists}
<form method="post" action="" name="addform"  style="margin: 0;">
<#if uiSequenceSearchList?has_content>
   <#list uiSequenceSearchList as pdpDiv>
      <#assign sequenceNum = pdpDiv.value!/>
      <#if sequenceNum?has_content && sequenceNum?number !=0>
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#${pdpDiv.div}")}
      </#if>
   </#list>
</#if>
</form>
${virtualDefaultJavaScript?if_exists}
</#if>
<#-- virtual product javascript -->
${virtualJavaScript?if_exists}
<form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="addform"  style="margin: 0;" onSubmit="return addItem();">
<#if uiSequenceSearchList?has_content>
   <#list uiSequenceSearchList as pdpDiv>
      <#assign sequenceNum = pdpDiv.value!/>
      <#if sequenceNum?has_content && sequenceNum?number !=0>
        ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#${pdpDiv.key}")}
      </#if>
   </#list>
</#if>
</form>
${virtualDefaultJavaScript?if_exists}

<#if divSequenceList?has_content>
   <#list divSequenceList as divSequenceItem>
      <#assign sequenceNum = divSequenceItem.value!/>
      <#if sequenceNum?has_content && sequenceNum?number !=0>
        ${screens.render("component://osafe/widget/EcommerceScreens.xml#${divSequenceItem.key}")}
      </#if>
   </#list>
</#if>

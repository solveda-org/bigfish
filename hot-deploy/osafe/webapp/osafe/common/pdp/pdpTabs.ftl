<#if uiPdpTabSequenceSearchList?has_content>
 <div class="pdpTabs">
   <ul>
   <#assign idx=1/>
   <#list uiPdpTabSequenceSearchList as pdpTabDiv>
      <#assign sequenceNum = pdpTabDiv.value!/>
      <#if sequenceNum?has_content && sequenceNum?number !=0>
        <#assign tabLabel = uiLabelMap.get("PdpTabLabel" + idx)/>
        <li><a href="#${pdpTabDiv.key}">${tabLabel}</a></li>
        <#assign idx= idx + 1/>
      </#if>
   </#list>
   </ul>
   <#list uiPdpTabSequenceSearchList as pdpTabDiv>
      <#assign sequenceNum = pdpTabDiv.value!/>
      <#if sequenceNum?has_content && sequenceNum?number !=0>
        ${screens.render("component://osafe/widget/EcommerceScreens.xml#${pdpTabDiv.key}")}
      </#if>
   </#list>
 </div>
</#if>

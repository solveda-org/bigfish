<#if uiPdpTabSequenceGroupMaps?has_content>
 <div class="pdpTabs">
   <ul>
   <#assign idx=1/>
   <#list uiPdpTabSequenceGroupMaps.entrySet() as entry>
        <#assign tabLabel = uiLabelMap.get("PdpTabLabel" + idx)/>
        <li><a href="#pdpTabsGroup_${idx}">${tabLabel}</a></li>
        <#assign idx= idx + 1/>
   </#list>
   </ul>

   <#assign idx=1/>
   <#list uiPdpTabSequenceGroupMaps.entrySet() as entry>
      <div id = "pdpTabsGroup_${idx}">
          <#assign uiPdpTabSequenceScreenList = (entry.value)?default("")>
          <#list uiPdpTabSequenceScreenList as pdpTabDiv>
              <#assign sequenceNum = pdpTabDiv.value!/>
              <#if sequenceNum?has_content && sequenceNum?number !=0>
                ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#${pdpTabDiv.div}")}
              </#if>
          </#list>
      </div>
      <#assign idx= idx + 1/>
   </#list>
 </div>
</#if>
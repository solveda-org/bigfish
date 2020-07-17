<li class="${request.getAttribute("attributeClass")!}">
	<#if uiPdpTabSequenceGroupMaps?has_content>
	    <div class="js_pdpTabs">
	      <ul>
	        <#assign idx=1/>
	        <#list uiPdpTabSequenceGroupMaps.entrySet() as entry>
	            <#assign tabLabel = uiLabelMap.get("PdpTabLabel" + idx)/>
	            <li><a href="#PDPTabs_Group_${idx}">${tabLabel}</a></li>
	            <#assign idx= idx + 1/>
	        </#list>
	      </ul>
	
	      <#assign idx=1/>
	      <#list uiPdpTabSequenceGroupMaps.entrySet() as entry>
	        <div id = "PDPTabs_Group_${idx}">
	          <#assign uiPdpTabSequenceScreenList = (entry.value)?default("")>
	          <#list uiPdpTabSequenceScreenList as pdpTabDiv>
	            <#assign sequenceNum = pdpTabDiv.value!/>
	            <#if sequenceNum?has_content && sequenceNum?number !=0>
                    <#assign attributeClass = pdpTabDiv.key!""/>
                    <#assign attributeMandatory = pdpTabDiv.mandatory!"N"/>
                    <#if attributeMandatory =="NA">
                          <#assign attributeMandatory = "N"/>
                    <#elseif attributeMandatory =="SYS_YES">
                          <#assign attributeMandatory = "Y"/>
                    <#elseif attributeMandatory =="SYS_NO">
                          <#assign attributeMandatory = "N"/>
                    <#elseif attributeMandatory =="NO">
                          <#assign attributeMandatory = "N"/>
                    <#elseif attributeMandatory =="YES">
                          <#assign attributeMandatory = "Y"/>
                    </#if>
                    <#assign uiSequenceScreen = pdpTabDiv.screen!/>
                    <#if uiSequenceScreen?has_content>
                      <#assign uiSequenceScreenLength=uiSequenceScreen.length()/>
                      <#assign uiScreenSequenceComponent = attributeClass.substring(uiSequenceScreenLength)/>
                      <#assign uiFirstChar = uiScreenSequenceComponent.substring(0,1).toLowerCase()/>
                      <#assign uiScreenSequenceComponent = uiFirstChar + uiScreenSequenceComponent.substring(1)/>
                      <#assign attributeClass = uiScreenSequenceComponent + " " + attributeClass/>
                    </#if>
                    <#if pdpTabDiv.style?exists && pdpTabDiv.style?has_content>
                      <#assign attributeClass = pdpTabDiv.style + " " + attributeClass/>
                    </#if>
                    ${setRequestAttribute("attributeClass",attributeClass)}
                    ${setRequestAttribute("attributeMandatory",attributeMandatory)}
	              <ul>
	                ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#${pdpTabDiv.key}")}
	              </ul>
	            </#if>
	          </#list>
	        </div>
	        <#assign idx= idx + 1/>
	      </#list>
	    </div>
	</#if>
</li>


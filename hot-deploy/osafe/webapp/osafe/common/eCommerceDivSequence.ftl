<#if divSequenceGroupMaps?has_content>
    <#assign uiBoxListItemGridName=uiBoxListItemGridName!/>
    <#assign uiBoxListItemTabName=uiBoxListItemTabName!/>
    <#assign uiBoxListItemGridName=uiBoxListItemGridName!/>
    <#assign uiSequenceScreen=uiSequenceScreen!/>
    <#assign uiDisplayListName=uiDisplayListName!/>
    <#assign uiSequenceScreenPrefix=uiSequenceScreenPrefix!/>

    <#assign divSequenceGroupSize=divSequenceGroupMaps.size()/>
    <#if uiBoxListItemGridName?exists && uiBoxListItemGridName?has_content>
     <div class="boxListItemGrid ${uiBoxListItemGridName!} ${uiSequenceScreen!}">
    </#if>
    <#if uiBoxListItemTabName?exists && uiBoxListItemTabName?has_content>
     <div class="boxListItemTabular ${uiBoxListItemTabName!} ${uiSequenceScreen!}">
    </#if>
    <#list divSequenceGroupMaps.entrySet() as entry>
     <#if (!(divSequenceGroupSize > 1 && entry.key ==0))>
        <#if (entry.key !=0)>
          <div class="${uiSequenceScreen!} group group${entry.key!}">
        </#if>
        <#if uiEntryFormName?exists && uiEntryFormName?has_content>
         <div class="entryForm ${uiEntryFormName!} ${uiSequenceScreen!}">
        </#if>
        <#if uiDisplayListName?exists && uiDisplayListName?has_content>
         <ul class="displayList ${uiDisplayListName!} ${uiSequenceScreen!}">
        </#if>
	        <#assign divSequenceList = (entry.value)?default("")>
	        <#list divSequenceList as divSequenceItem>
	          <#assign sequenceNum = divSequenceItem.value!/>
	          <#if sequenceNum?has_content && sequenceNum?number !=0>
	            <#assign attributeClass = divSequenceItem.key!""/>
	            <#assign attributeMandatory = divSequenceItem.mandatory!"N"/>
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
	            <#if uiSequenceScreen?has_content>
                  <#assign uiSequenceScreenLength=uiSequenceScreen.length()/>
                  <#assign uiScreenSequenceComponent = attributeClass.substring(uiSequenceScreenLength)/>
                  <#assign uiFirstChar = uiScreenSequenceComponent.substring(0,1).toLowerCase()/>
                  <#assign uiScreenSequenceComponent = uiFirstChar + uiScreenSequenceComponent.substring(1)/>
  	              <#assign attributeClass = uiScreenSequenceComponent + " " + attributeClass/>
	            </#if>
	            <#if divSequenceItem.style?exists && divSequenceItem.style?has_content>
	              <#assign attributeClass = divSequenceItem.style + " " + attributeClass/>
	            </#if>
	            ${setRequestAttribute("attributeClass",attributeClass)}
	            ${setRequestAttribute("attributeMandatory",attributeMandatory)}
	            ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#${uiSequenceScreenPrefix!}${divSequenceItem.key!}")}
	          </#if>
	        </#list>
        <#if uiDisplayListName?exists && uiDisplayListName?has_content>
         </ul>
        </#if>
        <#if uiEntryFormName?exists && uiEntryFormName?has_content>
         </div>
        </#if>
        <#if (entry.key !=0)>
          </div>
        </#if>
     </#if>
    </#list>
    <#if uiBoxListItemGridName?exists && uiBoxListItemGridName?has_content>
     </div>
    </#if>
    <#if uiBoxListItemTabName?exists && uiBoxListItemTabName?has_content>
     </div>
    </#if>
</#if>



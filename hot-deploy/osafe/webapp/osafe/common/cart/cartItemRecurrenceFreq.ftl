<#if (recurrenceItem?has_content) && recurrenceItem == "Y" >
   <li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
     <div>
       <label>${uiLabelMap.CartItemRecurrenceFreqCaption}</label>
       <span>
       <#if recurrenceFreq?exists && recurrenceFreq?has_content>
	       <#assign recurrenceFreqEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumCode" , recurrenceFreq))/>  
	       <#if recurrenceFreqEnums?has_content>
	           <#assign recurrenceFreqEnum = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(recurrenceFreqEnums) />  
	           <#if recurrenceFreqEnum?has_content>
	           	${recurrenceFreqEnum.description!}
	           </#if>
	       </#if>
       </#if>
       </span>
     </div>
   </li>
</#if>

<#if (recurrenceItem?has_content) && recurrenceItem == "Y" >
   <li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
     <div>
       <label>${uiLabelMap.CartItemRecurrenceFreqCaption}</label>
       <select name="recurrenceFreq_${cartLineIndex!}" id="recurrenceFreq_${cartLineIndex!}" class="js_recurrenceFreq" <#if cartLine.getIsPromo()> DISABLED</#if>>
         <#if recurrenceFreq?exists && recurrenceFreq?has_content>
           <#assign recurrenceFreqEnums = delegator.findByAndCache("Enumeration", Static["org.ofbiz.base.util.UtilMisc"].toMap("enumCode" , recurrenceFreq))/>  
           <#if recurrenceFreqEnums?has_content>
	           <#assign recurrenceFreqEnum = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(recurrenceFreqEnums) />  
	           <#if recurrenceFreqEnum?has_content>
	           	<option value="${recurrenceFreq!}">${recurrenceFreqEnum.description!}</option>
	           </#if>
           </#if>
         </#if>
         ${screens.render("component://osafe/widget/CommonScreens.xml#recurrenceFreqTypes")}
       </select>
     </div>
   </li>
</#if>

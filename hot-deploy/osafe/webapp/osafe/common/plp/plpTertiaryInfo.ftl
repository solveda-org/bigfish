 <#if plpLabel?has_content>
	 <div class="plpTertiaryInfo">
	   <p class="tertiaryInformation">${plpLabel!""}</p>
	 </div>
 <#else>
	  <div class="plpTertiaryInfo">
	     <#if productInternalName?has_content>
	       <p class="tertiaryInformation">${uiLabelMap.InternalNameLabel}&nbsp;${productInternalName!""}</p>
	     </#if>
	  </div>
</#if>

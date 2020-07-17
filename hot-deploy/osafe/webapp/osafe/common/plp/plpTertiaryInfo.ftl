 <#if plpLabel?has_content>
	 <div class="plpTertiaryInfo">
	   <p class="tertiaryInformation">${plpLabel!""}</p>
	 </div>
 <#else>
	  <div class="plpTertiaryInfo">
	     <#if plpProductInternalName?has_content>
	       <p class="tertiaryInformation">${uiLabelMap.InternalNameLabel}&nbsp;${plpProductInternalName!""}</p>
	     </#if>
	  </div>
</#if>

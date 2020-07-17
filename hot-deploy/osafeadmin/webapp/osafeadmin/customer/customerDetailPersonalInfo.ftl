<!-- start customerDetailPersonalInfo.ftl -->
<#if gender?has_content>
	  <#assign gender = gender.attrValue!"" >
</#if>

<#if dob_MMDD?has_content>
	  <#assign dob_MMDD = dob_MMDD.attrValue!"" >
</#if>

<#if dob_MMDDYYYY?has_content>
	  <#assign dob_MMDDYYYY = dob_MMDDYYYY.attrValue!"" >
</#if>

	<div class="displayBox personalInfo">
	  <div class="header"><h2>${personalInfoBoxHeading!} <span class="headingHelperText">${uiLabelMap.CustomerDetailPersonalInfoHeadingHelperInfo}</span></h2></div>
	  <div class="boxBody">

		<#if gender?has_content>
			<div class="infoRow">
			   <div class="infoEntry">
			     <div class="infoCaption">
			      <label>${uiLabelMap.GenderCaption}</label>
			     </div>
			     <div class="infoValue medium">
			       ${gender!""}
			     </div>
			   </div>
			</div>
		</#if>

		<#if dob_MMDD?has_content>					
			<div class="infoRow">
			   <div class="infoEntry">
			     <div class="infoCaption">
			      <label>${uiLabelMap.DOBCaption}</label>
			     </div>
			     <div class="infoValue medium">
			       ${dob_MMDD!""}
			     </div>
			   </div>
			</div>
		</#if>

		<#if dob_MMDDYYYY?has_content>					
			<div class="infoRow">
			   <div class="infoEntry">
			     <div class="infoCaption">
			      <label>${uiLabelMap.DOBCaption}</label>
			     </div>
			     <div class="infoValue medium">
			       ${dob_MMDDYYYY!""}
			     </div>
			   </div>
			</div>
		</#if>
		
		</div>
    </div>
    
    

<!-- end customerDetailPersonalInfo.ftl -->

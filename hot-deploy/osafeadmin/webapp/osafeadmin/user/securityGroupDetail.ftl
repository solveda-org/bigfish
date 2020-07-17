<#if mode?has_content>
	<#if securityGroupInfo?exists && securityGroupInfo?has_content>
		<#assign description = securityGroupInfo.description!"" />		
	</#if>

	  <div class="infoRow">
	    <div class="infoEntry">
	      <div class="infoCaption">
	        	<label>${uiLabelMap.SecurityGoupIdCaption}</label>
	      </div>
	      <div class="infoValue">
	      		<#if mode="add">
	        		<input name="groupId" type="text" id="groupId" value="${parameters.groupId!""}"/>
	        	<#else>
	        		${parameters.groupId!""}
	        		<input name="groupId" type="hidden" id="groupId" value="${parameters.groupId!""}"/>
	        	</#if>
	      </div>
	    </div>
	  </div>

	  
	  <div class="infoRow row">
		  <div class="infoEntry long">
		    <div class="infoCaption">
		      <label>${uiLabelMap.DescriptionCaption}</label>
		    </div>
		    <div class="infoValue">
		      <textarea name="description" class="shortArea" cols="50" rows="5">${parameters.description!description!""}</textarea>
		    </div>
		  </div>
	  </div>
	  


	 
	 
<#else>
  	${screens.render("component://osafeadmin/widget/CommonScreens.xml#ListNoDataResult")}
</#if>

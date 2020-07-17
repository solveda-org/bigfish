<!-- start error messages -->
<#if errorMessageList?has_content>
  	<div id="error">
		<img src="<@ofbizContentUrl>/html/img/rc/trackOrder/error.top.gif</@ofbizContentUrl>" alt="" />
		<div class="content">
		  	
	  		<#if errorMessageList?has_content>
	  			<font color="red" face="arial,helvetica" size="3">Following errors occurred:</font>
		  		<font color="red" face="arial,helvetica" size="2">
			  		<ul>
						<#list errorMessageList as errorMsg>
							<#if errorMsg.indexOf?exists>
								<#assign callIndex = errorMsg.indexOf("calling service")>
							   	<#if callIndex != -1>
									<li>${errorMsg.substring(0, callIndex)}</li>
								<#else>
									<li>${errorMsg}</li>
								</#if>
							<#else>
								<li>${errorMsg}</li>
							</#if>
						</#list>
					</ul>
				</font>
			</#if>
		</div>
	</div>
</#if>
<!-- end error messages -->

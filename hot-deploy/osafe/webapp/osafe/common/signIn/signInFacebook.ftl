<div class="${request.getAttribute("attributeClass")!}<#if className?exists && className?has_content> ${className}</#if>">
  <div class="displayBox">
    <h3>${uiLabelMap.FacebookLoginHeading}</h3>
    <ul class="displayActionList">
		      <li>
			       <div>
			       		<#--
						  Below we include the Login Button social plugin. This button uses the JavaScript SDK to
						  present a graphical Login button that triggers the FB.login() function when clicked. 
						-->
			       		 <#-- <fb:login-button show-faces="false" width="400" max-rows="1"></fb:login-button> 
			       		 <input type="submit" id="js_fbLoginButton" name="fbLoginButton" value="Facebook Login" />
			       		 -->
			       		
			       		<div class="fb-login-button" data-size="large" data-show-faces="false" data-auto-logout-link="false" onlogin="fbSetFields()" scope="public_profile,email"></div>
			       		
			       </div>
			  </li>
	</ul>
	<#-- Set hidden fields when facebook successfully logs in -->
    <form method="post" action="" id="fbLoginForm" name="fbLoginForm" onsubmit="submitForm(this)">
          <input type="hidden" id="js_fbBirthday" name="fbBirthday" value=""/>
          <input type="hidden" id="js_fbEmail" name="fbEmail" value=""/>
          <input type="hidden" id="js_fbFirstName" name="fbFirst_name" value=""/>
          <input type="hidden" id="js_fbLastName" name="fbLast_name" value=""/>
          <input type="hidden" id="js_fbFullName" name="fbName" value=""/>
          <input type="hidden" id="js_fbGender" name="fbGender" value=""/>
          <input type="hidden" id="js_fbId" name="fbId" value=""/>
          <input type="hidden" id="js_fbLocationCountry" name="fbLocationCountry" value=""/>
          <input type="hidden" id="js_fbLocationCity" name="fbLocationCity" value=""/>
          <input type="hidden" id="js_fbLocationState" name="fbLocationState" value=""/>
          <input type="hidden" id="js_fbLocationZip" name="fbLocationZip" value=""/>
          <input type="hidden" id="js_fbIsFBLogin" name="isFBLogin" value=""/>
          <input type="hidden" name="fbNextAction" value="${parameters.fbDoneAction!}"/>

    </form>
  </div>
</div>
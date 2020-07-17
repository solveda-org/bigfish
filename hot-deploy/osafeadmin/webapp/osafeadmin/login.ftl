
<div class="logo"><span class="loginSiteLogoImage"></span></div>

<#assign previousParams = sessionAttributes._PREVIOUS_PARAMS_?if_exists>
<#if previousParams?has_content>
    <#assign previousParams = '?' + previousParams>
</#if>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(''))>

<!-- start box -->
<div class="displayBox">
    <div class="header">${uiLabelMap.LoginHeading}</div>
    <div class="boxBody">
        <form method="post" action="<@ofbizUrl>login${previousParams?if_exists}</@ofbizUrl>" name="loginform">
			<div class="loginUserName">
				<label>${uiLabelMap.UserNameLabel}</label>
				<input type="text" name="USERNAME" value="${username?html}" class="field">
			</div>
			<div class="loginPassword">
				<label>${uiLabelMap.PasswordLabel}</label>
				<input class="field" type="password" name="PASSWORD"/>
			</div>
            <div class="loginButton">
                <input type="submit" class="standardBtn action" name="loginBtn" value="${uiLabelMap.LoginBtn}" />
            </div>
        </form>
    </div>
</div>
<!-- end box -->

<script language="JavaScript" type="text/javascript">
// <![CDATA[

    var f = document.loginform;
    if (f.USERNAME.value == '') {
        f.USERNAME.focus();
    } else if (f.PASSWORD.value == '') {
        f.PASSWORD.focus();
    }

// ]]>
</script>



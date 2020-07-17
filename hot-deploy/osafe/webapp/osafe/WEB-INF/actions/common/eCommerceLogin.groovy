package common;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import com.osafe.util.Util;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;

context.autoUserLogin = session.getAttribute("autoUserLogin");

previousParams = session.getAttribute("_PREVIOUS_PARAMS_");
if (UtilValidate.isNotEmpty(previousParams)) 
{
    previousParams = UtilHttp.stripNamedParamsFromQueryString(previousParams, ["USERNAME", "PASSWORD"]);
    previousParams = "?" + previousParams;
} else 
{
    previousParams = "";
}
context.previousParams = previousParams;

if (UtilValidate.isNotEmpty(parameters.review) && "review".equals(parameters.review)) 
{
    context.infoMessage = UtilProperties.getMessage("OSafeUiLabels","ReviewLoginInfo", locale );
}

loginToLowerCase="false";
securityProperties = UtilProperties.getResourceBundleMap("security.properties", locale);
userNameLowerCase= UtilProperties.getPropertyValue("security", "username.lowercase");
if (UtilValidate.isNotEmpty(loginToLowerCase)) 
{
    loginToLowerCase=userNameLowerCase;
}
context.loginToLowerCase=loginToLowerCase;

checkoutAsGuest = Util.isProductStoreParmTrue(request,"CHECKOUT_AS_GUEST");
guestUser = parameters.guest;
showGuestLogin = "N";
shoppingCart = ShoppingCartEvents.getCartObject(request);
shoppingCartSize = 0;
className = ""
if (UtilValidate.isNotEmpty(shoppingCart)) 
{
	shoppingCartSize = shoppingCart.size();
	if(shoppingCartSize > 0)
	{
		if(checkoutAsGuest && UtilValidate.isNotEmpty(guestUser) && "guest".equalsIgnoreCase(guestUser))
		{
			className = "withGuestCheckoutOption";
			showGuestLogin = "Y";
		}
	}
}

context.shoppingCartSize = shoppingCartSize;
context.className = className;
context.showGuestLogin = showGuestLogin;
package navigation;

import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import com.osafe.services.OsafeManageXml;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;


adminTopMenuXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "admin-navigation-def-xml-file"), context);
adminTopMenuList = OsafeManageXml.getMapListFromXmlFile(adminTopMenuXmlFilePath);


navigationCategory = context.navigationCategory;
activeTopMenuItem = context.activeTopMenuItem;
activeSubMenuItem = context.activeSubMenuItem;
breadcrumbTitle = "";
navigationCategoryText = "";
subSubMenuItemText = "";
navigationCategoryHref = "";
subSubMenuItemHref = "";
if (UtilValidate.isNotEmpty(navigationCategory) || UtilValidate.isNotEmpty(activeTopMenuItem)) 
{
    requestParameters = UtilHttp.getParameterMap(request);
	if (UtilValidate.isEmpty(navigationCategory))
	{
		navigationCategory = requestParameters.navigationCategory;
	}
	adminTopMenuList.each { adminTopMenuItem ->
		subMenuItemList = adminTopMenuItem.child;
		subMenuItemList.each { subMenuItem ->
			if (navigationCategory.equals(subMenuItem.navigationCategory) || activeTopMenuItem.equals(subMenuItem.name))
			{
				navigationCategoryText = subMenuItem.text;
				navigationCategoryHref = subMenuItem.href;
				subSubMenuItemList = subMenuItem.child;
				subSubMenuItemList.each { subSubMenuItem ->
					if (activeSubMenuItem.equals(subSubMenuItem.name))
					{
						subSubMenuItemText = subSubMenuItem.text;
						subSubMenuItemHref = subSubMenuItem.href;
					}
				}
			}
		}	
	}
}

context.navigationCategoryText = navigationCategoryText;
context.subSubMenuItemText = subSubMenuItemText;
context.navigationCategoryHref = navigationCategoryHref;
context.subSubMenuItemHref = subSubMenuItemHref;
if (UtilValidate.isNotEmpty(context.title))
{
	breadcrumbTitle = (context.title).toString();	
}
context.breadcrumbTitle = StringUtil.wrapString(breadcrumbTitle);



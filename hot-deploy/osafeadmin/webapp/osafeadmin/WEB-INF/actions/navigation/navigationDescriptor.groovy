package navigation;

import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilHttp;
import com.osafe.services.OsafeManageXml;

adminTopMenuXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "admin-navigation-def-xml-file"), context);
adminTopMenuList = OsafeManageXml.getMapListFromXmlFile(adminTopMenuXmlFilePath);

navigationCategory = context.navigationCategory;
if (UtilValidate.isEmpty(navigationCategory)) 
{
    requestParameters = UtilHttp.getParameterMap(request);
    navigationCategory = requestParameters.navigationCategory;
}

adminTopMenuList.each { adminTopMenuItem ->
    if (UtilValidate.isInteger(adminTopMenuItem.sequenceNumber))
    {
        if (UtilValidate.isNotEmpty(adminTopMenuItem.sequenceNumber)) 
        {
            adminTopMenuItem.sequenceNumber = Integer.parseInt(adminTopMenuItem.sequenceNumber);
        } else 
        {
            adminTopMenuItem.sequenceNumber = 0;
        }
    }
    subMenuItemList = adminTopMenuItem.child;
    subMenuItemList.each { subMenuItem ->

       if (UtilValidate.isNotEmpty(navigationCategory)) 
       {
        if (subMenuItem.navigationCategory.equals(navigationCategory)) 
        {
            context.topMenuItem = subMenuItem;
        }
       }
       if (context.activeSubMenuItem && UtilValidate.isNotEmpty(activeSubMenuItem) && subMenuItem.name.equals(activeSubMenuItem)) 
       {
            subMenuItem.className = subMenuItem.className + " active";
       }
       if (UtilValidate.isInteger(subMenuItem.sequenceNumber))
       {
            if (UtilValidate.isNotEmpty(subMenuItem.sequenceNumber)) 
            {
                subMenuItem.sequenceNumber = Integer.parseInt(subMenuItem.sequenceNumber);
            } 
            else 
            {
                subMenuItem.sequenceNumber = 0;
            }
        }
    }
    if (context.activeTopMenuItem && UtilValidate.isNotEmpty(activeTopMenuItem) && adminTopMenuItem.name.equals(activeTopMenuItem)) 
    {
        adminTopMenuItem.className = adminTopMenuItem.className + " active";
    }
    
}

context.adminTopMenuList = adminTopMenuList;


package common;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.control.*;

/*
 if NOTHING is being included set mainPanelStyle=mainPanel
 if ONLY left panel is being included set mainPanelStyle=leftPanel
 if ONLY right panel is being included set mainPanelStyle=rightPanel
 if left AND right panel is being included set mainPanelStyle=leftRightPanel
 */


if (UtilValidate.isEmpty(context.mainPanelStyle))
{
    context.mainPanelStyle="mainPanel";
    context.contentPanelStyle="mainPanel";
}

if (UtilValidate.isNotEmpty(context.contentPanelStyle))
{
    context.contentPanelStyle=context.contentPanelStyle;
}

String eCommerceLeftPanel = context.eCommerceLeftPanel;
String eCommerceRightPanel = context.eCommerceRightPanel;
if (UtilValidate.isNotEmpty(eCommerceLeftPanel) && UtilValidate.isNotEmpty(eCommerceRightPanel))
{
    context.mainPanelStyle="leftRightPanel";
}
else if (UtilValidate.isNotEmpty(eCommerceLeftPanel))
{
    context.mainPanelStyle="leftPanel";
}
else if (UtilValidate.isNotEmpty(eCommerceRightPanel))
{
    context.mainPanelStyle="rightPanel";
}

//Placed on the highest level container in decorators.
//PageClassIdentifier are defined at the screen defintion level for each page 

if (UtilValidate.isNotEmpty(context.pageClassIdentifier))
{
	context.pageClassIdentifier = context.pageClassIdentifier + "PageIdentifier";
}

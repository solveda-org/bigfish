package common;

import org.ofbiz.base.util.UtilValidate;

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

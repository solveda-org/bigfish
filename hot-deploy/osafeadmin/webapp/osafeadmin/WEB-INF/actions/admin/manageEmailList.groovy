package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//email templates
adminToolsList = FastList.newInstance();
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.AdminEmailHeading);
adminTool.put("toolDesc", uiLabelMap.AdminEmailInfo);
adminTool.put("toolDetail", "emailSpotList");
adminToolsList.add(adminTool);

//email config
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.AdminEmailConfigHeading);
adminTool.put("toolDesc", uiLabelMap.AdminEmailConfigInfo);
adminTool.put("toolDetail", "emailConfigList");
adminToolsList.add(adminTool);

context.resultList = adminToolsList;
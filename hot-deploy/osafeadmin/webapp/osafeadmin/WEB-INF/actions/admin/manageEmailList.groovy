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

//txt message templates
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.AdminTxtMsgHeading);
adminTool.put("toolDesc", uiLabelMap.AdminTxtMsgInfo);
adminTool.put("toolDetail", "txtMsgSpotList");
adminToolsList.add(adminTool);

//email parameter
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.AdminEmailParameterHeading);
adminTool.put("toolDesc", uiLabelMap.AdminEmailParameterInfo);
adminTool.put("toolDetail", "manageBigfishParameter?parameterFileName=parameters_email_styles.xml&showDetail=true&backAction=manageEmailList");
adminToolsList.add(adminTool);

context.resultList = adminToolsList;
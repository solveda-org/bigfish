package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//clear label cache
adminToolsList = FastList.newInstance();
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ClrLblCacheLabel);
adminTool.put("toolDesc", uiLabelMap.ClrLblCacheInfo);
adminTool.put("toolDetail", "clearLabelCacheDetail");
adminToolsList.add(adminTool);

//solr re-index
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SolrReIndxLabel);
adminTool.put("toolDesc", uiLabelMap.SolrReIndxInfo);
adminTool.put("toolDetail", "solrReIndexDetail");
adminToolsList.add(adminTool);

//generate SiteMap.xml
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SiteMapLabel);
adminTool.put("toolDesc", uiLabelMap.SiteMapInfo);
adminTool.put("toolDetail", "siteMapDetail");
adminToolsList.add(adminTool);

//generate OsafeSeoUrlMap.xml
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SeoUrlMapLabel);
adminTool.put("toolDesc", uiLabelMap.SeoUrlMapInfo);
adminTool.put("toolDetail", "seoUrlMapDetail");
adminToolsList.add(adminTool);

//compare tool: labels and captions
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareLabelFileLabel);
adminTool.put("toolDesc", uiLabelMap.CompareLabelFileInfo);
adminTool.put("toolDetail", "compareLabelFileDetail");
adminToolsList.add(adminTool);

//edit ecommerce css style file
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.EditEcommerceStyleLabel);
adminTool.put("toolDesc", uiLabelMap.EditEcommerceStyleInfo);
adminTool.put("toolDetail", "editEcommerceStyleDetail");
adminToolsList.add(adminTool);

//manage ecommerce css style file
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageEcommerceStyleLabel);
adminTool.put("toolDesc", uiLabelMap.ManageEcommerceStyleInfo);
adminTool.put("toolDetail", "manageEcommerceStyleDetail");
adminToolsList.add(adminTool);


//edit admin css style file
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.EditAdminStyleLabel);
adminTool.put("toolDesc", uiLabelMap.EditAdminStyleInfo);
adminTool.put("toolDetail", "editAdminStyleDetail");
adminToolsList.add(adminTool);

//manage admin css style file
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageAdminStyleLabel);
adminTool.put("toolDesc", uiLabelMap.ManageAdminStyleInfo);
adminTool.put("toolDetail", "manageAdminStyleDetail");
adminToolsList.add(adminTool);

//compare tool: System Parameters
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareParametersLabel);
adminTool.put("toolDesc", uiLabelMap.CompareParametersInfo);
adminTool.put("toolDetail", "compareParameters");
adminToolsList.add(adminTool);

//manage <DIV> sequence
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.manageDivSequenceLabel);
adminTool.put("toolDesc", uiLabelMap.manageDivSequenceInfo);
adminTool.put("toolDetail", "manageDivSequenceDetail");
adminToolsList.add(adminTool);

context.resultList = UtilMisc.sortMaps(adminToolsList, UtilMisc.toList("toolType"));
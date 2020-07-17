package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//clear label cache
adminToolsList = FastList.newInstance();
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ClrLblCacheLabel);
adminTool.put("toolDesc", uiLabelMap.ClrCacheInfo);
adminTool.put("toolDetail", "clearLabelCacheDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.ClrLblCacheLabel).toUpperCase());
adminToolsList.add(adminTool);

//solr re-index
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SolrReIndxLabel);
adminTool.put("toolDesc", uiLabelMap.SolrReIndxInfo);
adminTool.put("toolDetail", "solrReIndexDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.SolrReIndxLabel).toUpperCase());
adminToolsList.add(adminTool);

//generate SiteMap.xml
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SiteMapLabel);
adminTool.put("toolDesc", uiLabelMap.SiteMapInfo);
adminTool.put("toolDetail", "siteMapDetail");
adminTool.put("toolTypeUpperCase",(uiLabelMap.SiteMapLabel).toUpperCase());
adminToolsList.add(adminTool);

//generate OsafeSeoUrlMap.xml
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SeoUrlMapLabel);
adminTool.put("toolDesc", uiLabelMap.SeoUrlMapInfo);
adminTool.put("toolDetail", "seoUrlMapDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.SeoUrlMapLabel).toUpperCase());
adminToolsList.add(adminTool);

//compare tool: labels and captions
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareLabelFileLabel);
adminTool.put("toolDesc", uiLabelMap.CompareLabelFileInfo);
adminTool.put("toolDetail", "compareLabelFileDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CompareLabelFileLabel).toUpperCase());
adminToolsList.add(adminTool);

//compare tool: Div Sequence
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareDivSequenceFileLabel);
adminTool.put("toolDesc", uiLabelMap.CompareDivSequenceFileInfo);
adminTool.put("toolDetail", "compareDivSequenceFileDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CompareDivSequenceFileLabel).toUpperCase());
adminToolsList.add(adminTool);

//compare tool: System Parameters
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareParametersLabel);
adminTool.put("toolDesc", uiLabelMap.CompareParametersInfo);
adminTool.put("toolDetail", "compareParameters");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CompareParametersLabel).toUpperCase());
adminToolsList.add(adminTool);

//manage <DIV> sequence
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.manageDivSequenceLabel);
adminTool.put("toolDesc", uiLabelMap.manageDivSequenceInfo);
adminTool.put("toolDetail", "manageDivSequenceDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.manageDivSequenceLabel).toUpperCase());
adminToolsList.add(adminTool);

//system configuration files
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.AdminSysConfigLabel);
adminTool.put("toolDesc", uiLabelMap.AdminSysConfigInfo);
adminTool.put("toolDetail", "sysConfigFileList");
adminTool.put("toolTypeUpperCase", (uiLabelMap.AdminSysConfigLabel).toUpperCase());
adminToolsList.add(adminTool);

//email test
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.EmailTestLabel);
adminTool.put("toolDesc", uiLabelMap.EmailTestInfo);
adminTool.put("toolDetail", "emailTestDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.EmailTestLabel).toUpperCase());
adminToolsList.add(adminTool);


//bigfish xml export
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.BFXmlExportLabel);
adminTool.put("toolDesc", uiLabelMap.BFXmlExportInfo);
adminTool.put("toolDetail", "exportBigfishContentDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.BFXmlExportLabel).toUpperCase());
adminToolsList.add(adminTool);


//catalog asset checker
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CatalogAssetCheckerLabel);
adminTool.put("toolDesc", uiLabelMap.CatalogAssetCheckerInfo);
adminTool.put("toolDetail", "catalogAssetCheckerDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CatalogAssetCheckerLabel).toUpperCase());
adminToolsList.add(adminTool);

//system health check
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SysHealthCheckLabel);
adminTool.put("toolDesc", uiLabelMap.SysHealthCheckInfo);
adminTool.put("toolDetail", "sysHealthCheckList");
adminTool.put("toolTypeUpperCase", (uiLabelMap.SysHealthCheckLabel).toUpperCase());
adminToolsList.add(adminTool);

//CSS management tool
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CSSManagementToolLabel);
adminTool.put("toolDesc", uiLabelMap.CSSManagementToolInfo);
adminTool.put("toolDetail", "manageCssDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CSSManagementToolLabel).toUpperCase());
adminToolsList.add(adminTool);

context.resultList = UtilMisc.sortMaps(adminToolsList, UtilMisc.toList("toolTypeUpperCase"));
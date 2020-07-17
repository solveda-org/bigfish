package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//ADMIN TOOLS
adminToolsList = FastList.newInstance();

//BigFish Parameters
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.BigfishParameterLabel);
adminTool.put("toolDesc", uiLabelMap.BigfishParameterInfo);
adminTool.put("toolDetail", "manageBigfishParameter");
adminTool.put("toolTypeUpperCase", (uiLabelMap.BigfishParameterLabel).toUpperCase());
adminToolsList.add(adminTool);

//bigfish xml export
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.BFXmlExportLabel);
adminTool.put("toolDesc", uiLabelMap.BFXmlExportInfo);
adminTool.put("toolDetail", "exportBigfishContentDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.BFXmlExportLabel).toUpperCase());
adminToolsList.add(adminTool);

//clear BigFish cache
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ClrBigfishCacheLabel);
adminTool.put("toolDesc", uiLabelMap.ClrBigFishCacheInfo);
adminTool.put("toolDetail", "clearCacheDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.ClrBigfishCacheLabel).toUpperCase());
adminToolsList.add(adminTool);

//CSS management tool
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CSSManagementToolLabel);
adminTool.put("toolDesc", uiLabelMap.CSSManagementToolInfo);
adminTool.put("toolDetail", "manageCssDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CSSManagementToolLabel).toUpperCase());
adminToolsList.add(adminTool);

//manage Custom Party Attribute
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.manageCustomPartyAttributesLabel);
adminTool.put("toolDesc", uiLabelMap.manageCustomPartyAttributesInfo);
adminTool.put("toolDetail", "manageCustomPartyAttributesDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.manageCustomPartyAttributesLabel).toUpperCase());
adminToolsList.add(adminTool);

//manage <DIV> sequence
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.manageDivSequenceLabel);
adminTool.put("toolDesc", uiLabelMap.manageDivSequenceInfo);
adminTool.put("toolDetail", "manageDivSequenceDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.manageDivSequenceLabel).toUpperCase());
adminToolsList.add(adminTool);

//solr re-index
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SolrReIndxLabel);
adminTool.put("toolDesc", uiLabelMap.SolrReIndxInfo);
adminTool.put("toolDetail", "solrReIndexDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.SolrReIndxLabel).toUpperCase());
adminToolsList.add(adminTool);

//Product Delete Tool
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ProductDeleteToolLabel);
adminTool.put("toolDesc", uiLabelMap.ProductDeleteToolInfo);
adminTool.put("toolDetail", "productDeleteToolDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.ProductDeleteToolLabel).toUpperCase());
adminToolsList.add(adminTool);

//entity Sync 
/*adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.EntitySyncLabel);
adminTool.put("toolDesc", uiLabelMap.EntitySyncInfo);
adminTool.put("toolDetail", "entitySyncDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.EntitySyncLabel).toUpperCase());
adminToolsList.add(adminTool);*/

//Admin Labels and Captions
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.AdminManageUiLabelLabel);
adminTool.put("toolDesc", uiLabelMap.AdminManageUiLabelInfo);
adminTool.put("toolDetail", "manageAdminUiLabel");
adminTool.put("toolTypeUpperCase", (uiLabelMap.AdminManageUiLabel).toUpperCase());
adminToolsList.add(adminTool);

context.adminToolsList = UtilMisc.sortMaps(adminToolsList, UtilMisc.toList("toolTypeUpperCase"));
// END OF ADMIN TOOLS

//COMPARE TOOLS
compareToolsList = FastList.newInstance();

//compare tool: Div Sequence
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareDivSequenceFileLabel);
adminTool.put("toolDesc", uiLabelMap.CompareDivSequenceFileInfo);
adminTool.put("toolDetail", "compareDivSequenceFileDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CompareDivSequenceFileLabel).toUpperCase());
compareToolsList.add(adminTool);

//compare tool: labels and captions
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareLabelFileLabel);
adminTool.put("toolDesc", uiLabelMap.CompareLabelFileInfo);
adminTool.put("toolDetail", "compareLabelFileDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CompareLabelFileLabel).toUpperCase());
compareToolsList.add(adminTool);

//compare tool: System Parameters
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CompareParametersLabel);
adminTool.put("toolDesc", uiLabelMap.CompareParametersInfo);
adminTool.put("toolDetail", "compareParameters");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CompareParametersLabel).toUpperCase());
compareToolsList.add(adminTool);

context.compareToolsList = UtilMisc.sortMaps(compareToolsList, UtilMisc.toList("toolTypeUpperCase"));
//END OF COMPARE TOOLS

//SEO TOOLS
seoToolsList = FastList.newInstance();

//seo friendly url: generate OsafeSeoUrlMap.xml
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SeoUrlMapLabel);
adminTool.put("toolDesc", uiLabelMap.SeoUrlMapInfo);
adminTool.put("toolDetail", "seoUrlMapDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.SeoUrlMapLabel).toUpperCase());
seoToolsList.add(adminTool);

//sitemap xml generator: generate SiteMap.xml
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SiteMapLabel);
adminTool.put("toolDesc", uiLabelMap.SiteMapInfo);
adminTool.put("toolDetail", "siteMapDetail");
adminTool.put("toolTypeUpperCase",(uiLabelMap.SiteMapLabel).toUpperCase());
seoToolsList.add(adminTool);

context.seoToolsList = UtilMisc.sortMaps(seoToolsList, UtilMisc.toList("toolTypeUpperCase"));
//END OF SEO TOOLS

//UTILITIES
utilitiesList = FastList.newInstance();

//catalog asset checker
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.CatalogAssetCheckerLabel);
adminTool.put("toolDesc", uiLabelMap.CatalogAssetCheckerInfo);
adminTool.put("toolDetail", "catalogAssetCheckerDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.CatalogAssetCheckerLabel).toUpperCase());
utilitiesList.add(adminTool);

//email test
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.EmailTestLabel);
adminTool.put("toolDesc", uiLabelMap.EmailTestInfo);
adminTool.put("toolDetail", "emailTestDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.EmailTestLabel).toUpperCase());
utilitiesList.add(adminTool);

//Text Message test
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.TxtMsgTestLabel);
adminTool.put("toolDesc", uiLabelMap.TxtMsgTestInfo);
adminTool.put("toolDetail", "txtMsgTestDetail");
adminTool.put("toolTypeUpperCase", (uiLabelMap.TxtMsgTestLabel).toUpperCase());
utilitiesList.add(adminTool);

//system configuration files
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.AdminSysConfigLabel);
adminTool.put("toolDesc", uiLabelMap.AdminSysConfigInfo);
adminTool.put("toolDetail", "sysConfigFileList");
adminTool.put("toolTypeUpperCase", (uiLabelMap.AdminSysConfigLabel).toUpperCase());
utilitiesList.add(adminTool);

//system health check
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.SysHealthCheckLabel);
adminTool.put("toolDesc", uiLabelMap.SysHealthCheckInfo);
adminTool.put("toolDetail", "sysHealthCheckList");
adminTool.put("toolTypeUpperCase", (uiLabelMap.SysHealthCheckLabel).toUpperCase());
utilitiesList.add(adminTool);

context.utilitiesList = UtilMisc.sortMaps(utilitiesList, UtilMisc.toList("toolTypeUpperCase"));
//END OF UTILITIES


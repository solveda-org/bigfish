package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

feedExportList = FastList.newInstance();


//Customers Export
feedsExport = FastMap.newInstance();
feedsExport.put("toolSeq","1");
feedsExport.put("toolType", uiLabelMap.CustomersExportLabel);
feedsExport.put("toolDesc", uiLabelMap.CustomersExportInfo);
feedsExport.put("toolDetail", "customersExport");
feedExportList.add(feedsExport);

//Orders Export
feedsExport = FastMap.newInstance();
feedsExport.put("toolSeq","1");
feedsExport.put("toolType", uiLabelMap.OrdersExportLabel);
feedsExport.put("toolDesc", uiLabelMap.OrdersExportInfo);
feedsExport.put("toolDetail", "ordersExport");
feedExportList.add(feedsExport);

//Contact Us Export
feedsExport = FastMap.newInstance();
feedsExport.put("toolSeq","1");
feedsExport.put("toolType", uiLabelMap.ContactUsExportLabel);
feedsExport.put("toolDesc", uiLabelMap.ContactUsExportInfo);
feedsExport.put("toolDetail", "contactUsExport");
feedExportList.add(feedsExport);

//Request Catalog Export
feedsExport = FastMap.newInstance();
feedsExport.put("toolSeq","1");
feedsExport.put("toolType", uiLabelMap.RequestCatalogExportLabel);
feedsExport.put("toolDesc", uiLabelMap.RequestCatalogExportInfo);
feedsExport.put("toolDetail", "requestCatalogExport");
feedExportList.add(feedsExport);

context.resultList = UtilMisc.sortMaps(feedExportList, UtilMisc.toList("toolSeq"));
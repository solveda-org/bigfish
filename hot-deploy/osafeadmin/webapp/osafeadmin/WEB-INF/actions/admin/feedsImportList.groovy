package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

feedImportList = FastList.newInstance();


//Order Status Change Imports
feedsImport = FastMap.newInstance();
feedsImport.put("toolSeq","1");
feedsImport.put("toolType", uiLabelMap.OrderStatusChangesLabel);
feedsImport.put("toolDesc", uiLabelMap.OrderStatusChangesInfo);
feedsImport.put("toolDetail", "orderStatusChangesImport");
feedImportList.add(feedsImport);

//Product Rating Imports
feedsImport = FastMap.newInstance();
feedsImport.put("toolSeq","1");
feedsImport.put("toolType", uiLabelMap.ProductRatingsLabel);
feedsImport.put("toolDesc", uiLabelMap.ProductRatingsInfo);
feedsImport.put("toolDetail", "productRatingsImport");
feedImportList.add(feedsImport);

//Stores Imports
feedsImport = FastMap.newInstance();
feedsImport.put("toolSeq","1");
feedsImport.put("toolType", uiLabelMap.StoresLabel);
feedsImport.put("toolDesc", uiLabelMap.StoresInfo);
feedsImport.put("toolDetail", "storesImport");
feedImportList.add(feedsImport);

context.resultList = UtilMisc.sortMaps(feedImportList, UtilMisc.toList("toolSeq"));
package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

List bluedartFeedsImportList = FastList.newInstance();


//Customer Imports
bluedartFeedsImport = FastMap.newInstance();
bluedartFeedsImport.put("toolSeq","1");
bluedartFeedsImport.put("toolType", uiLabelMap.PrepaidFileFeedLabel);
bluedartFeedsImport.put("toolDesc", uiLabelMap.PrepaidFileFeedInfo);
bluedartFeedsImport.put("toolDetail", "bluedartPrepaidFileFeedImport");
bluedartFeedsImportList.add(bluedartFeedsImport);

//Order Status Change Imports
bluedartFeedsImport = FastMap.newInstance();
bluedartFeedsImport.put("toolSeq","1");
bluedartFeedsImport.put("toolType", uiLabelMap.CoDFileFeedLabel);
bluedartFeedsImport.put("toolDesc", uiLabelMap.CoDFileFeedInfo);
bluedartFeedsImport.put("toolDetail", "blueDartCodFileFeedImport");
bluedartFeedsImportList.add(bluedartFeedsImport);

context.resultList = UtilMisc.sortMaps(bluedartFeedsImportList, UtilMisc.toList("toolSeq"));
package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

productLoadersList = FastList.newInstance();


//Load bIg fish xls file
productLoader = FastMap.newInstance();
productLoader.put("toolSeq","1");
productLoader.put("toolType", uiLabelMap.LoadBigFishFormatLabel);
productLoader.put("toolDesc", uiLabelMap.LoadBigFishFormatInfo);
productLoader.put("toolDetail", "bigFishXlsLoader");
productLoadersList.add(productLoader);

//Download Big Fish Sample xls file
productLoader = FastMap.newInstance();
productLoader.put("toolSeq","2");
productLoader.put("toolType", uiLabelMap.DownloadBigFishSampleFileLabel);
productLoader.put("toolDesc", uiLabelMap.DownloadBigFishSampleFileInfo);
productLoader.put("toolDetail", "bigFishXlsSampleDetail");
productLoadersList.add(productLoader);

//Create Big Fish xls from DB
productLoader = FastMap.newInstance();
productLoader.put("toolSeq","3");
productLoader.put("toolType", uiLabelMap.CreateBigFishFromDBLabel);
productLoader.put("toolDesc", uiLabelMap.CreateBigFishFromDBInfo);
productLoader.put("toolDetail", "bigFishXlsFromDBDetail");
productLoadersList.add(productLoader);

//Create Big Fish xls from ebay file
productLoader = FastMap.newInstance();
productLoader.put("toolSeq","4");
productLoader.put("toolType", uiLabelMap.CreateBigFishFromEbayLabel);
productLoader.put("toolDesc", uiLabelMap.CreateBigFishFromEbayInfo);
productLoader.put("toolDetail", "bigFishXlsFromEbayDetail");
productLoadersList.add(productLoader);

//Update Image Location Preference file
productLoader = FastMap.newInstance();
productLoader.put("toolSeq","5");
productLoader.put("toolType", uiLabelMap.ImageLocationPreferencesLabel);
productLoader.put("toolDesc", uiLabelMap.ImageLocationPreferencesInfo);
productLoader.put("toolDetail", "imageLocationPreferencesDetail");
productLoadersList.add(productLoader);


context.resultList = UtilMisc.sortMaps(productLoadersList, UtilMisc.toList("toolSeq"));
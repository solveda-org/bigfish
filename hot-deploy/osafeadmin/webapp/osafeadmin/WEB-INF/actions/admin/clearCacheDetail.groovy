package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//cache list to clear
//put | between two or more cache
cacheList = FastList.newInstance();

//Label & captions Cache
clearCache = FastMap.newInstance();
clearCache.put("seqNum", 1);
clearCache.put("cacheType", uiLabelMap.LabelCaptionCacheLabel);
clearCache.put("cacheStore", "properties.UtilPropertiesBundleCache");
clearCache.put("cacheToClear", "properties.UtilPropertiesBundleCache");//OSafeUiLabels
cacheList.add(clearCache);

//SEO Friendly Url Cache
clearCache = FastMap.newInstance();
clearCache.put("seqNum", 2);
clearCache.put("cacheType", uiLabelMap.SEOFriendlyUrlCacheLabel);
clearCache.put("cacheStore", "properties.UtilPropertiesBundleCache");
clearCache.put("cacheToClear", "properties.UtilPropertiesBundleCache");//OSafeSeoUrlMap
cacheList.add(clearCache);

//Div Sequencing Cache
clearCache = FastMap.newInstance();
clearCache.put("seqNum", 3);
clearCache.put("cacheType", uiLabelMap.DivSequencingCacheLabel);
clearCache.put("cacheStore", "osafe.ManageXmlUrlCache");
clearCache.put("cacheToClear", "osafe.ManageXmlUrlCache");
cacheList.add(clearCache);

//Custom Party Attribute Cache
clearCache = FastMap.newInstance();
clearCache.put("seqNum", 4);
clearCache.put("cacheType", uiLabelMap.CustomPartyAttributesCacheLabel);
clearCache.put("cacheStore", "osafe.ManageXmlUrlCache");
clearCache.put("cacheToClear", "osafe.ManageXmlUrlCache");
cacheList.add(clearCache);

//Pixcel Tracking Cache
clearCache = FastMap.newInstance();
clearCache.put("seqNum", 5);
clearCache.put("cacheType", uiLabelMap.PixelTrackingCacheLabel);
clearCache.put("cacheStore", "entitycache.entity-list.default.XPixelTracking");
clearCache.put("cacheToClear", "entitycache.entity-list.default.XPixelTracking");
cacheList.add(clearCache);

//System Parameter Cache
clearCache = FastMap.newInstance();
clearCache.put("seqNum", 6);
clearCache.put("cacheType", uiLabelMap.SystemParameterCacheLabel);
clearCache.put("cacheStore", "entitycache.entity.default.XProductStoreParm"+System.getProperty("line.separator")+"entitycache.entity-list.default.XProductStoreParm");
clearCache.put("cacheToClear", "entitycache.entity.default.XProductStoreParm|entitycache.entity-list.default.XProductStoreParm");
cacheList.add(clearCache);

//BigFish Parameter Cache
clearCache = FastMap.newInstance();
clearCache.put("seqNum", 7);
clearCache.put("cacheType", uiLabelMap.BigFishParameterCacheLabel);
clearCache.put("cacheStore", "properties.UtilPropertiesBundleCache");
clearCache.put("cacheToClear", "properties.UtilPropertiesBundleCache");
cacheList.add(clearCache);


context.cacheList = UtilMisc.sortMaps(cacheList, UtilMisc.toList("seqNum"));
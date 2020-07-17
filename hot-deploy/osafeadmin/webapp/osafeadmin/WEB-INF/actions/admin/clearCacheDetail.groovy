package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//cache list to clear
//put | between two or more cache
cacheList = FastList.newInstance();

//Label & captions Cache
clearCache = FastMap.newInstance();
clearCache.put("cacheType", uiLabelMap.LabelCaptionCacheLabel);
clearCache.put("cacheStore", "properties.UtilPropertiesBundleCache");
clearCache.put("cacheToClear", "properties.UtilPropertiesBundleCache");//OSafeUiLabels
cacheList.add(clearCache);

//SEO Friendly Url Cache
clearCache = FastMap.newInstance();
clearCache.put("cacheType", uiLabelMap.SEOFriendlyUrlCacheLabel);
clearCache.put("cacheStore", "properties.UtilPropertiesBundleCache");
clearCache.put("cacheToClear", "properties.UtilPropertiesBundleCache");//OSafeSeoUrlMap
cacheList.add(clearCache);

//Div Sequencing Cache
clearCache = FastMap.newInstance();
clearCache.put("cacheType", uiLabelMap.DivSequencingCacheLabel);
clearCache.put("cacheStore", "osafe.ManageXmlUrlCache");
clearCache.put("cacheToClear", "osafe.ManageXmlUrlCache");
cacheList.add(clearCache);

//Custom Party Attribute Cache
clearCache = FastMap.newInstance();
clearCache.put("cacheType", uiLabelMap.CustomPartyAttributesCacheLabel);
clearCache.put("cacheStore", "osafe.ManageXmlUrlCache");
clearCache.put("cacheToClear", "osafe.ManageXmlUrlCache");
cacheList.add(clearCache);

//Pixcel Tracking Cache
clearCache = FastMap.newInstance();
clearCache.put("cacheType", uiLabelMap.PixcelTrackingCacheLabel);
clearCache.put("cacheStore", "entitycache.entity-list.default.XPixelTracking");
clearCache.put("cacheToClear", "entitycache.entity-list.default.XPixelTracking");
cacheList.add(clearCache);

//System Parameter Cache
clearCache = FastMap.newInstance();
clearCache.put("cacheType", uiLabelMap.SystemParameterCacheLabel);
clearCache.put("cacheStore", "entitycache.entity.default.XProductStoreParm"+System.getProperty("line.separator")+"entitycache.entity-list.default.XProductStoreParm");
clearCache.put("cacheToClear", "entitycache.entity.default.XProductStoreParm|entitycache.entity-list.default.XProductStoreParm");
cacheList.add(clearCache);

context.cacheList = UtilMisc.sortMaps(cacheList, UtilMisc.toList("cacheType"));
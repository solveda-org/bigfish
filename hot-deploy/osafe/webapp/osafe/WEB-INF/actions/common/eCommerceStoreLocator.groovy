package common;

import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.common.geo.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.contact.ContactMechWorker;
import com.osafe.util.Util;
import com.osafe.geo.OsafeGeo;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;

address = parameters.address;
latitude = parameters.latitude;
longitude = parameters.longitude;
parameters.latitude = "";
parameters.longitude = "";
showMap = parameters.showMap;
searchOsafeGeo = "";
context.searchedAddress = address;

notFoundAddress = Util.getProductStoreParm(request,"GMAP_NOT_FOUND_ADDRESS");
isStoreNotFoundAddress = parameters.isStoreNotFoundAddress;

if (UtilValidate.isNotEmpty(isStoreNotFoundAddress) && "Y".equals(isStoreNotFoundAddress))
{
	address = notFoundAddress;
}

storePartyList = FastList.newInstance();
session = context.session;

//Check if we are showing a single store detail or a list of available stores
if (UtilValidate.isNotEmpty(context.storeDetail) && "Y".equals(context.storeDetail)) 
{
	//Check if a sepcific store id was passed
	storeId = parameters.storeId;
	if (UtilValidate.isEmpty(storeId)) 
	{
	    shoppingCart = session.getAttribute("shoppingCart");
	    if (UtilValidate.isNotEmpty(shoppingCart))
	    {
	        storeId = shoppingCart.getOrderAttribute("STORE_LOCATION");
	        context.shoppingCart = shoppingCart;
	    }

	    orderId = parameters.orderId;
	    if (UtilValidate.isNotEmpty(orderId)) 
	    {
	        orderAttrPickupStore = delegator.findOne("OrderAttribute", ["orderId" : orderId, "attrName" : "STORE_LOCATION"], true);
	        if (UtilValidate.isNotEmpty(orderAttrPickupStore)) 
	        {
	            storeId = orderAttrPickupStore.attrValue;
	        }
	    }
	}
	if (UtilValidate.isNotEmpty(storeId))
	{
	    party = delegator.findByPrimaryKeyCache("Party", [partyId : storeId]);
	    if (UtilValidate.isNotEmpty(party)) 
	    {
	    	storePartyList = FastList.newInstance();
	    	storePartyList.add(party);
	        partyGroup = party.getRelatedOneCache("PartyGroup");
	        if (UtilValidate.isNotEmpty(partyGroup)) 
	        {
	            context.storeInfo = partyGroup;
	        }
	    	
	    }
	}
}
else
{
	if(UtilValidate.isNotEmpty(showMap) && "Y".equals(showMap)) 
	{
		storeList = delegator.findList("ProductStoreRole", EntityCondition.makeCondition([roleTypeId : "STORE_LOCATION"]), null, null, null, true);
		storeListIds = EntityUtil.getFieldListFromEntityList(storeList, "partyId", true);
		storePartyList = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition("partyId", EntityOperator.IN, storeListIds), null, null, null, true);
	    storePartyList = EntityUtil.filterByAnd(storePartyList, UtilMisc.toMap("statusId","PARTY_ENABLED"));
	 	searchOsafeGeo = new OsafeGeo(latitude, longitude);
	}
	
}





//Initial GMAP settings
width = "600px"; 
height = "300px"; 
zoom = "4";
uom = "Miles"; 
redius = 20; 
numDiplay = 10; 
gmapUrl ="";

geoPoints = FastList.newInstance();
partyDetailList = FastList.newInstance();
partyDetailExistsList = FastList.newInstance();
if(UtilValidate.isNotEmpty(storePartyList)) 
{
	mapWidth = Util.getProductStoreParm(request,"GMAP_MAP_IMG_W");
	mapHeight = Util.getProductStoreParm(request,"GMAP_MAP_IMG_H");
	mapZoom = Util.getProductStoreParm(request,"GMAP_MAP_ZOOM");
	mapUom = Util.getProductStoreParm(request,"GMAP_UOM");
	mapRadius = Util.getProductStoreParm(request,"GMAP_RADIUS");
	mapNumDisplay = Util.getProductStoreParm(request,"GMAP_NUM_TO_DISPLAY");
	mapApiUrl = Util.getProductStoreParm(request,"GMAP_JS_API_URL");
	mapApiKey = Util.getProductStoreParm(request,"GMAP_JS_API_KEY");
    if (Util.isNumber(mapWidth)) 
    {
        width = mapWidth +"px";
    }
    if (Util.isNumber(mapHeight)) 
    {
        height = mapHeight + "px";
    }
    if (Util.isNumber(mapZoom)) 
    {
        zoom = mapZoom;
    }
    if (UtilValidate.isNotEmpty(mapUom) && (mapUom.equalsIgnoreCase("Kilometers") || mapUom.equalsIgnoreCase("Miles"))) 
    {
        uom = mapUom;
    }
    if (Util.isNumber(mapRadius)) 
    {
        redius = Integer.parseInt(mapRadius);
    }
    if (Util.isNumber(mapNumDisplay)) 
    {
        numDiplay = Integer.parseInt(mapNumDisplay);
    }
    if (UtilValidate.isNotEmpty(mapApiUrl)) 
    {
        gmapUrl = mapApiUrl;
    }
    if (UtilValidate.isNotEmpty(mapApiKey)) 
    {
        gmapUrl = mapApiUrl+mapApiKey;
    }
    for(partyRow in storePartyList) 
    {
      partyId = partyRow.partyId;
      if (!partyDetailExistsList.contains(partyId))
      {
    	  partyDetailExistsList.add(partyId);
          party = delegator.findByPrimaryKeyCache("Party", [partyId : partyId]);
          if (UtilValidate.isNotEmpty(party)) 
          {        
            latestPartyGeoPoint = GeoWorker.findLatestGeoPoint(delegator, "PartyGeoPoint", "partyId", partyId, null, null);

            if(UtilValidate.isNotEmpty(latestPartyGeoPoint)) 
            {
                latestGeoPoint = delegator.findByPrimaryKeyCache("GeoPoint", [geoPointId : latestPartyGeoPoint.geoPointId]);
                latestOsafeGeo = new OsafeGeo(latestGeoPoint.latitude.toString(), latestGeoPoint.longitude.toString());
                distance = Math.round(Util.distFrom(searchOsafeGeo, latestOsafeGeo, uom) * 10) / 10;
                if (latestGeoPoint.dataSourceId == dataSourceId && distance <= redius) 
                {

                    groupName = ""; 
                    groupNameLocal = "";
                    
                    storeAddress = ""; 
                    context.storeAddress =storeAddress;
                    address1 = ""; 
                    address2 = "";
                    address3 = ""; 
                    city = ""; 
                    stateProvinceGeoId = ""; 
                    postalCode = "";
                    countryGeoId = ""; 
                    countryName = "";
                    
                    storePhone = ""; 
                    context.storePhone =storePhone;
                    areaCode = ""; 
                    contactNumber = ""; 
                    contactNumber3 = ""; 
                    contactNumber4 = "";
                    
                    openingHoursContentId = ""; 
                    context.storeHoursContentId = openingHoursContentId;
              	    storeHoursDataResourceId = "";
                    context.storeHoursDataResourceId = storeHoursDataResourceId;
                    
                    storeNoticeContentId = "";
                    context.storeNoticeContentId = storeNoticeContentId;
             	    storeNoticeDataResourceId = "";
                    context.storeNoticeDataResourceId = storeNoticeDataResourceId;

                    storeContentSpotContentId = "";
                 	context.storeContentSpotContentId = storeContentSpotContentId;
              	    storeContentSpotDataResourceId = "";
         		    context.storeContentSpotDataResourceId = storeContentSpotDataResourceId;

                    partyGroup = party.getRelatedOneCache("PartyGroup");
                    if (UtilValidate.isNotEmpty(partyGroup)) 
                    {
                        groupName = partyGroup.groupName;
                        groupNameLocal = partyGroup.groupNameLocal;
                    }

                    partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
                    partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
                    partyContactMechPurpose = EntityUtil.orderBy(partyContactMechPurpose,UtilMisc.toList("-fromDate"));
                    
                    storeLocationLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "GENERAL_LOCATION"));
                    storeLocationLocations = EntityUtil.getRelatedCache("PartyContactMech", storeLocationLocations);
                    storeLocationLocations = EntityUtil.filterByDate(storeLocationLocations,true);
                    storeLocationLocations = EntityUtil.orderBy(storeLocationLocations, UtilMisc.toList("fromDate DESC"));
                    if (UtilValidate.isNotEmpty(storeLocationLocations)) 
                    {
                    	storeLocationLocation = EntityUtil.getFirst(storeLocationLocations);
                    	storeAddress = storeLocationLocation.getRelatedOneCache("PostalAddress");
                        context.storeAddress =storeAddress;
                        address1 = storeAddress.address1;
                        address2 = storeAddress.address2;
                        address3 = storeAddress.address3;
                        city = storeAddress.city;
                        stateProvinceGeoId = storeAddress.stateProvinceGeoId;
                        postalCode = storeAddress.postalCode;
                        countryGeoId = storeAddress.countryGeoId;
                        GenericValue countryGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", storeAddress.countryGeoId), true);
                        if (UtilValidate.isNotEmpty(countryGeo)) 
                        {
                            countryName = countryGeo.geoName;
                        }
                    }
                    
                    storeTelephoneLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_PHONE"));
                    storeTelephoneLocations = EntityUtil.getRelatedCache("PartyContactMech", storeTelephoneLocations);
                    storeTelephoneLocations = EntityUtil.filterByDate(storeTelephoneLocations,true);
                    storeTelephoneLocations = EntityUtil.orderBy(storeTelephoneLocations, UtilMisc.toList("fromDate DESC"));
                    if (UtilValidate.isNotEmpty(storeTelephoneLocations)) 
                    {
                    	storeTelephoneLocation = EntityUtil.getFirst(storeTelephoneLocations);
                    	storePhone = storeTelephoneLocation.getRelatedOneCache("TelecomNumber");
                        context.storePhone =storePhone;
                        areaCode = storePhone.areaCode;
                        contactNumber = storePhone.contactNumber;
                        if (contactNumber.length() >= 7) 
                        {
                            contactNumber3 = contactNumber.substring(0, 3);
                            contactNumber4 = contactNumber.substring(3, 7);
                        }
                    }
                    
                    partyContent = party.getRelatedCache("PartyContent");
                    partyContent = EntityUtil.filterByDate(partyContent,true);
                    partyContent = EntityUtil.orderBy(partyContent,UtilMisc.toList("-fromDate"));

                    storeHours = EntityUtil.filterByAnd(partyContent, UtilMisc.toMap("partyContentTypeId", "STORE_HOURS"));
                    if (UtilValidate.isNotEmpty(storeHours)) 
                    {
                    	storeHour = EntityUtil.getFirst(storeHours);
                        content = storeHour.getRelatedOneCache("Content");
                        if (UtilValidate.isNotEmpty(content))
                        {
                           openingHoursContentId = content.contentId;
                           context.storeHoursContentId = openingHoursContentId;
                           dataResource = content.getRelatedOneCache("DataResource");
                           if (UtilValidate.isNotEmpty(dataResource))
                           {
                        	   storeHoursDataResourceId = dataResource.dataResourceId;
                               context.storeHoursDataResourceId = storeHoursDataResourceId;
                           }
                        }
                    }
                    
                    storeNotices = EntityUtil.filterByAnd(partyContent, UtilMisc.toMap("partyContentTypeId", "STORE_NOTICE"));
                    if (UtilValidate.isNotEmpty(storeNotices)) 
                    {
                    	storeNotice = EntityUtil.getFirst(storeNotices);
                        content = storeNotice.getRelatedOneCache("Content");
                        if (UtilValidate.isNotEmpty(content))
                        {
                           storeNoticeContentId = content.contentId;
                           context.storeNoticeContentId = storeNoticeContentId;
                           dataResource = content.getRelatedOneCache("DataResource");
                           if (UtilValidate.isNotEmpty(dataResource))
                           {
                        	   storeNoticeDataResourceId = dataResource.dataResourceId;
                               context.storeNoticeDataResourceId = storeNoticeDataResourceId;
                           }
                        }
                    }
                    storeContentSpots = EntityUtil.filterByAnd(partyContent, UtilMisc.toMap("partyContentTypeId", "STORE_CONTENT_SPOT"));
                    if (UtilValidate.isNotEmpty(storeContentSpots)) 
                    {
                    	storeContentSpot = EntityUtil.getFirst(storeContentSpots);
                        content = storeContentSpot.getRelatedOneCache("Content");
                        if (UtilValidate.isNotEmpty(content))
                        {
                     	   storeContentSpotContentId = content.contentId;
                     	   context.storeContentSpotContentId = storeContentSpotContentId;
                           dataResource = content.getRelatedOneCache("DataResource");
                           if (UtilValidate.isNotEmpty(dataResource))
                           {
                        	  storeContentSpotDataResourceId = dataResource.dataResourceId;
                     		  context.storeContentSpotDataResourceId = storeContentSpotDataResourceId;
                           }
                        }
                    }
                    
                    data = groupName+" ("+groupNameLocal+")";

                    distanceValue = distance;
                    if (uom.equalsIgnoreCase("Kilometers")) 
                    {
                        distance = distance+" Kilometers";
                    } else if (uom.equalsIgnoreCase("Miles")) 
                    {
                        distance = distance+" Miles";
                    } else 
                    {
                        distance = distance+" "+uom;
                    }

                    partyDetailMap = UtilMisc.toMap("partyId", partyId, "storeCode", groupNameLocal, "storeName", groupName, "storeAddress", storeAddress,
						    "address1", address1, "address2", address2,  "address3", address3, "city", city, "stateProvinceGeoId", stateProvinceGeoId,
                            "postalCode", postalCode,"countryGeoId", countryGeoId,"countryName", countryName, "areaCode", areaCode, "contactNumber", contactNumber,
                            "contactNumber3", contactNumber3, "contactNumber4", contactNumber4, "openingHoursContentId", openingHoursContentId, "storeNoticeContentId", storeNoticeContentId, "storeContentSpotContentId", storeContentSpotContentId, "distance", distance, "distanceValue", distanceValue,
                            "latitude", latestGeoPoint.latitude, "longitude", latestGeoPoint.longitude, "searchAddress", address, "searchlatitude", latitude, "searchlongitude", longitude);
                    
                    geoPoints.add(UtilMisc.toMap("lat", latestGeoPoint.latitude, "lon", latestGeoPoint.longitude, "userLocation", "N", "closures", UtilMisc.toMap("data", data, "storeDetail", partyDetailMap), "distanceValue", distanceValue));
                    partyDetailList.add(partyDetailMap);
                }
            }
          }    	  
      }
    }
}
if (UtilValidate.isNotEmpty(searchOsafeGeo) && searchOsafeGeo.isNotEmpty()) 
{
    geoPoints.add(UtilMisc.toMap("lat", Double.valueOf(searchOsafeGeo.latitude()), "lon", Double.valueOf(searchOsafeGeo.longitude()), "userLocation", "Y", "closures", UtilMisc.toMap("data", address), "distanceValue", new BigDecimal(0)));
    context.userLocation = "Y";
}

geoPoints = UtilMisc.sortMaps(geoPoints, UtilMisc.toList("distanceValue"));
partyDetailList = UtilMisc.sortMaps(partyDetailList, UtilMisc.toList("distanceValue"));
if (UtilValidate.isNotEmpty(geoPoints) && (geoPoints.size() > (numDiplay+1)))
{
    // clone the list for concurrent modification
    cloneGeoPoints = UtilMisc.makeListWritable(geoPoints);
    geoPoints = geoPoints.subList(0, numDiplay+1);
}
if (UtilValidate.isNotEmpty(partyDetailList) && (partyDetailList.size() > numDiplay))
{
    // clone the list for concurrent modification
    clonePartyDetailList = UtilMisc.makeListWritable(partyDetailList);
    partyDetailList = partyDetailList.subList(0, numDiplay);
}

Map geoChart = UtilMisc.toMap("GeoMapRequestUrl", gmapUrl, "width", width, "height", height, "zoom", zoom, "controlUI" , "small", "dataSourceId", dataSourceId, "uom", uom, "points", geoPoints);
context.geoChart = geoChart;
context.storeDetailList = partyDetailList;
context.gmapNumDisplay = numDiplay;

context.notFoundAddress = notFoundAddress;
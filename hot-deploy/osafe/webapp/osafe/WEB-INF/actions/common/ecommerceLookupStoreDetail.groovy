package common;

import javolution.util.FastMap;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.party.contact.ContactMechWorker;
import com.osafe.util.Util;

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

} else 
{
    party = delegator.findByPrimaryKeyCache("Party", [partyId : storeId]);
    if (UtilValidate.isNotEmpty(party)) 
    {
        partyGroup = party.getRelatedOneCache("PartyGroup");
        if (UtilValidate.isNotEmpty(partyGroup)) 
        {
            context.storeInfo = partyGroup;
        }

        partyContactMechPurpose = party.getRelatedCache("PartyContactMechPurpose");
        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
        partyContactMechPurpose = EntityUtil.orderBy(partyContactMechPurpose,UtilMisc.toList("-fromDate"));
        
        storeLocationLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "GENERAL_LOCATION"));
        if (UtilValidate.isNotEmpty(storeLocationLocations)) 
        {
        	storeLocationLocation = EntityUtil.getFirst(storeLocationLocations);
        	storeAddress = storeLocationLocation.getRelatedOneCache("PostalAddress");
            context.storeAddress =storeAddress;
        }
        
        storeTelephoneLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "PRIMARY_PHONE"));
        if (UtilValidate.isNotEmpty(storeTelephoneLocations)) 
        {
        	storeTelephoneLocation = EntityUtil.getFirst(storeTelephoneLocations);
        	storePhone = storeTelephoneLocation.getRelatedOneCache("TelecomNumber");
            context.storePhone =storePhone;
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
               context.storeHoursContentId = content.contentId;
               dataResource = content.getRelatedOneCache("DataResource");
               if (UtilValidate.isNotEmpty(dataResource))
               {
                   context.storeHoursDataResourceId = dataResource.dataResourceId;
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
                context.storeNoticeContentId = content.contentId;
               dataResource = content.getRelatedOneCache("DataResource");
               if (UtilValidate.isNotEmpty(dataResource))
               {
                   context.storeNoticeDataResourceId = dataResource.dataResourceId;
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
         	   context.storeContentSpotContentId = content.contentId;
               dataResource = content.getRelatedOneCache("DataResource");
               if (UtilValidate.isNotEmpty(dataResource))
               {
         		  context.storeContentSpotDataResourceId = dataResource.dataResourceId;
               }
            }
        }

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

mapWith = Util.getProductStoreParm(request,"GMAP_MAP_IMG_W");
mapHeight = Util.getProductStoreParm(request,"GMAP_MAP_IMG_H");
mapZoom = Util.getProductStoreParm(request,"GMAP_MAP_ZOOM");
mapUom = Util.getProductStoreParm(request,"GMAP_UOM");
mapRadius = Util.getProductStoreParm(request,"GMAP_RADIUS");
mapNumDisplay = Util.getProductStoreParm(request,"GMAP_NUM_TO_DISPLAY");
mapApiUrl = Util.getProductStoreParm(request,"GMAP_JS_API_URL");
mapApiKey = Util.getProductStoreParm(request,"GMAP_JS_API_KEY");
    if (Util.isNumber(mapWidth)) 
    {
        width = mapWith +"px";
    }
    if (Util.isNumber(mapHeight)) 
    {
        height = mapHeight + "px";
    }
    if (Util.isNumber(mapZoom) 
    {
        zoom = productStoreParmMap.GMAP_MAP_ZOOM;
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


geoPoints = FastList.newInstance();
partyDetailList = FastList.newInstance();

partyId = storeId;
if(UtilValidate.isNotEmpty(partyId)) 
{
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

                groupName = ""; groupNameLocal = ""; address1 = ""; address2 = "";
                address3 = ""; city = ""; stateProvinceGeoId = ""; postalCode = "";
                countryGeoId = ""; countryName = ""; areaCode = ""; contactNumber = ""; 
                contactNumber3 = ""; contactNumber4 = ""; openingHoursContentId = ""; storeNoticeContentId = "";

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
                if (UtilValidate.isNotEmpty(storeLocationLocations)) 
                {
                	storeLocationLocation = EntityUtil.getFirst(storeLocationLocations);
                	storeAddress = storeLocationLocation.getRelatedOneCache("PostalAddress");
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
                if (UtilValidate.isNotEmpty(storeTelephoneLocations)) 
                {
                	storeTelephoneLocation = EntityUtil.getFirst(storeTelephoneLocations);
                	storePhone = storeTelephoneLocation.getRelatedOneCache("TelecomNumber");
                    partyTelecomNumberContactMech = storePhone.telecomNumber;
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
                    }
                }
                
                storeNotices = EntityUtil.filterByAnd(partyContent, UtilMisc.toMap("partyContentTypeId", "STORE_NOTICE"));
                if (UtilValidate.isNotEmpty(storeNotices)) 
                {
                	storeNotice = EntityUtil.getFirst(storeNotices);
                    content = storeNotice.getRelatedOneCache("Content");
                    if (UtilValidate.isNotEmpty(content))
                    {
                        context.storeNoticeContentId = content.contentId;
                       dataResource = content.getRelatedOneCache("DataResource");
                       if (UtilValidate.isNotEmpty(content))
                       {
                          storeNoticeContentId = content.contentId;
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

                partyDetailMap = UtilMisc.toMap("partyId", partyId, "storeCode", groupNameLocal, "storeName", groupName, "address1", address1,
                                                "address2", address2,  "address3", address3, "city", city, "stateProvinceGeoId", stateProvinceGeoId,
                                                "postalCode", postalCode,"countryGeoId", countryGeoId,"countryName", countryName, "areaCode", areaCode, "contactNumber", contactNumber,
                                                "contactNumber3", contactNumber3, "contactNumber4", contactNumber4, "openingHoursContentId", openingHoursContentId, "storeNoticeContentId", storeNoticeContentId,"distance", distance, "distanceValue", distanceValue,
                                                "latitude", latestGeoPoint.latitude, "longitude", latestGeoPoint.longitude, "searchAddress", address, "searchlatitude", latitude, "searchlongitude", longitude);
                geoPoints.add(UtilMisc.toMap("lat", latestGeoPoint.latitude, "lon", latestGeoPoint.longitude, "userLocation", "N", "closures", UtilMisc.toMap("data", data, "storeDetail", partyDetailMap)));
                partyDetailList.add(partyDetailMap);
            }
        }
    }
}
Map geoChart = UtilMisc.toMap("GeoMapRequestUrl", gmapUrl, "width", width, "height", height, "zoom", zoom, "controlUI" , "small", "dataSourceId", dataSourceId, "uom", uom, "points", geoPoints);
context.geoChart = geoChart;










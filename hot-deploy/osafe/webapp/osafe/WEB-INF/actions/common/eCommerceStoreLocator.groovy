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

address = StringUtils.trimToEmpty(parameters.address);
latitude = StringUtils.trimToEmpty(parameters.latitude);
longitude = StringUtils.trimToEmpty(parameters.longitude);
searchOsafeGeo = new OsafeGeo(latitude, longitude);

session = context.session;
Map<String, Object> svcCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");
svcCtx.put("lookupFlag", "Y");
svcCtx.put("showAll", "N");
svcCtx.put("statusId", "ANY");
svcCtx.put("extInfo", "N");
svcCtx.put("partyTypeId", "PARTY_GROUP");
svcCtx.put("roleTypeId", "STORE_LOCATION");
svcCtx.put("statusId", "PARTY_ENABLED");
svcRes = dispatcher.runSync("findParty", svcCtx);
partyList = svcRes.get("completePartyList");

width = "600px"; height = "300px"; zoom = "4";
uom = "Miles"; redius = 20; numDiplay = 10; gmapUrl ="";

productStoreParmMap = Util.getProductStoreParmMap(request);
if(UtilValidate.isNotEmpty(productStoreParmMap)) {
    if (Util.isNumber(productStoreParmMap.GMAP_MAP_IMG_W)) {
        width = productStoreParmMap.GMAP_MAP_IMG_W+"px";
    }
    if (Util.isNumber(productStoreParmMap.GMAP_MAP_IMG_H)) {
        height = productStoreParmMap.GMAP_MAP_IMG_H+"px";
    }
    if (Util.isNumber(productStoreParmMap.GMAP_MAP_ZOOM)) {
        zoom = productStoreParmMap.GMAP_MAP_ZOOM;
    }
    if (UtilValidate.isNotEmpty(productStoreParmMap.GMAP_UOM) && (productStoreParmMap.GMAP_UOM.equalsIgnoreCase("Kilometers") || productStoreParmMap.GMAP_UOM.equalsIgnoreCase("Miles"))) {
        uom = productStoreParmMap.GMAP_UOM;
    }
    if (Util.isNumber(productStoreParmMap.GMAP_RADIUS)) {
        redius = Integer.parseInt(productStoreParmMap.GMAP_RADIUS);
    }
    if (Util.isNumber(productStoreParmMap.GMAP_NUM_TO_DISPLAY)) {
        numDiplay = Integer.parseInt(productStoreParmMap.GMAP_NUM_TO_DISPLAY);
    }
    if (UtilValidate.isNotEmpty(productStoreParmMap.GMAP_JS_API_URL)) {
        gmapUrl = productStoreParmMap.GMAP_JS_API_URL;
    }
    if (UtilValidate.isNotEmpty(productStoreParmMap.GMAP_JS_API_KEY)) {
        gmapUrl = gmapUrl+productStoreParmMap.GMAP_JS_API_KEY;
    }
}

geoPoints = FastList.newInstance();
partyDetailList = FastList.newInstance();
if(UtilValidate.isNotEmpty(partyList)) {
    for(partyRow in partyList) {
        partyId = partyRow.partyId;
        latestPartyGeoPoint = GeoWorker.findLatestGeoPoint(delegator, "PartyGeoPoint", "partyId", partyId, null, null);

        if(UtilValidate.isNotEmpty(latestPartyGeoPoint)) {
            latestGeoPoint = delegator.findByPrimaryKey("GeoPoint", [geoPointId : latestPartyGeoPoint.geoPointId]);
            latestOsafeGeo = new OsafeGeo(latestGeoPoint.latitude.toString(), latestGeoPoint.longitude.toString());
            distance = Math.round(Util.distFrom(searchOsafeGeo, latestOsafeGeo, uom) * 10) / 10;
            if (latestGeoPoint.dataSourceId == dataSourceId && geoPoints.size() < numDiplay && distance <= redius) {

                groupName = ""; groupNameLocal = ""; address1 = ""; address2 = "";
                address3 = ""; city = ""; stateProvinceGeoId = ""; postalCode = "";
                countryGeoId = ""; countryName = ""; areaCode = ""; contactNumber = ""; 
                contactNumber3 = ""; contactNumber4 = ""; openingHoursContentId = ""; storeNoticeContentId = "";

                partyGroup = delegator.findOne("PartyGroup", [partyId : partyId], true);
                if (UtilValidate.isNotEmpty(partyGroup)) {
                    groupName = partyGroup.groupName;
                    groupNameLocal = partyGroup.groupNameLocal;
                }

                partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
                if (partyContactMechValueMaps) {
                    partyContactMechValueMaps.each { partyContactMechValueMap ->
                        contactMechPurposes = partyContactMechValueMap.partyContactMechPurposes;
                        contactMechPurposes.each { contactMechPurpose ->
                            if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
                                partyPostalAddressContactMech = partyContactMechValueMap.postalAddress;
                                address1 = partyPostalAddressContactMech.address1;
                                address2 = partyPostalAddressContactMech.address2;
                                address3 = partyPostalAddressContactMech.address3;
                                city = partyPostalAddressContactMech.city;
                                stateProvinceGeoId = partyPostalAddressContactMech.stateProvinceGeoId;
                                postalCode = partyPostalAddressContactMech.postalCode;
                                countryGeoId = partyPostalAddressContactMech.countryGeoId;
                                GenericValue countryGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", partyPostalAddressContactMech.countryGeoId), false);
                                if (UtilValidate.isNotEmpty(countryGeo)) {
                                    countryName = countryGeo.geoName;
                                }
                            } else if (contactMechPurpose.contactMechPurposeTypeId.equals("PRIMARY_PHONE")) {
                                partyTelecomNumberContactMech = partyContactMechValueMap.telecomNumber;
                                areaCode = partyTelecomNumberContactMech.areaCode;
                                contactNumber = partyTelecomNumberContactMech.contactNumber;
                                if (contactNumber.length() >= 7) {
                                    contactNumber3 = contactNumber.substring(0, 3);
                                    contactNumber4 = contactNumber.substring(3, 7);
                                }
                            }
                        }
                    }
                }

                partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "STORE_HOURS"]))
                if (UtilValidate.isNotEmpty(partyContent)) {
                    content = partyContent.getRelatedOne("Content");
                    if (UtilValidate.isNotEmpty(content))
                    {
                       openingHoursContentId = content.contentId;
                    }
                }
                partyContent = EntityUtil.getFirst(delegator.findByAnd("PartyContent", [partyId : partyId, 	partyContentTypeId : "STORE_NOTICE"]))
                if (UtilValidate.isNotEmpty(partyContent)) {
                    content = partyContent.getRelatedOne("Content");
                    if (UtilValidate.isNotEmpty(content))
                    {
                       storeNoticeContentId = content.contentId;
                    }
                }
                data = groupName+" ("+groupNameLocal+")";

                distanceValue = distance;
                if (uom.equalsIgnoreCase("Kilometers")) {
                    distance = distance+" Kilometers";
                } else if (uom.equalsIgnoreCase("Miles")) {
                    distance = distance+" Miles";
                } else {
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
if (searchOsafeGeo.isNotEmpty()) {
    geoPoints.add(UtilMisc.toMap("lat", Double.valueOf(searchOsafeGeo.latitude()), "lon", Double.valueOf(searchOsafeGeo.longitude()), "userLocation", "Y", "closures", UtilMisc.toMap("data", address)));
    context.userLocation = "Y";
}
Map geoChart = UtilMisc.toMap("GeoMapRequestUrl", gmapUrl, "width", width, "height", height, "zoom", zoom, "controlUI" , "small", "dataSourceId", dataSourceId, "uom", uom, "points", geoPoints);
context.geoChart = geoChart;
partyDetailList = UtilMisc.sortMaps(partyDetailList, UtilMisc.toList("distanceValue"));
context.storeDetailList = partyDetailList;
package com.osafe.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.osafe.geo.OsafeGeo;


public class GeoServices {

    public static final String module = GeoServices.class.getName();

    public static Map<String, ?> genPartyGeoPoint(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> resp = null;
        OsafeGeo osafeGeo = OsafeGeo.EmptyOsafeGeo;

        String productStoreId = (String) context.get("productStoreId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        String roleTypeId = (String) context.get("roleTypeId");
        String contactMechPurposeTypeId = (String) context.get("contactMechPurposeTypeId");

        Map conditions = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(partyId)) {
            conditions.put("partyId", partyId);
        }
        if (UtilValidate.isNotEmpty(roleTypeId)) {
            conditions.put("roleTypeId", roleTypeId);
        } else {
            conditions.put("roleTypeId", "STORE_LOCATION");
        }
        try {
            List<GenericValue> partyRoleList = delegator.findByAnd("PartyRole", conditions);
            for (GenericValue partyRole : partyRoleList) {
                try {
                    GenericValue party = partyRole.getRelatedOneCache("Party");
                    partyId = party.getString("partyId");

                    Collection<GenericValue> contactMechList = null;
                    if (UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
                    	contactMechList = ContactHelper.getContactMechByPurpose(party, contactMechPurposeTypeId, false);
                    }
                    if (UtilValidate.isEmpty(contactMechList)) {
                        contactMechList = ContactHelper.getContactMechByType(party, "POSTAL_ADDRESS", false);
                    }
                    if (UtilValidate.isNotEmpty(contactMechList)) {
                        GenericValue contactMech  = EntityUtil.getFirst((List<GenericValue>) contactMechList);
                        GenericValue postalAddress  = contactMech.getRelatedOneCache("PostalAddress");
                        StringBuilder  address = new StringBuilder();
                        if (UtilValidate.isNotEmpty(postalAddress.getString("address1"))) {
                            address.append(postalAddress.getString("address1")+ ", ");
                        }
                        if (UtilValidate.isNotEmpty(postalAddress.getString("address2"))) {
                            address.append(postalAddress.getString("address2")+", ");
                        }
                        if (UtilValidate.isNotEmpty(postalAddress.getString("city"))) {
                            address.append(postalAddress.getString("city")+", ");
                        }
                        // Google Map api give appropriate result with GeoName
                        if (UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId"))) {
                            GenericValue stateGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", postalAddress.getString("stateProvinceGeoId")), true);
                            if (UtilValidate.isNotEmpty(stateGeo.getString("geoName"))) {
                                address.append(stateGeo.getString("geoName")+", ");
                            }
                        } else if (UtilValidate.isNotEmpty(postalAddress.getString("address3"))) {
                            address.append(postalAddress.getString("address3")+", ");
                        }
                        if (UtilValidate.isNotEmpty(postalAddress.getString("countryGeoId"))) {
                            GenericValue countryGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", postalAddress.getString("countryGeoId")), true);
                            if (UtilValidate.isNotEmpty(countryGeo.getString("geoName"))) {
                                address.append(countryGeo.getString("geoName"));
                            }
                        }
                        Debug.logInfo(address.toString(), module);
                        osafeGeo = OsafeGeo.fromAddress(address.toString(),productStoreId);
                    }

                    List<GenericValue> partyGeoPointList = delegator.findByAndCache("PartyGeoPoint", UtilMisc.toMap("partyId", partyId));
                    partyGeoPointList = EntityUtil.filterByDate(partyGeoPointList, true);
                    if (UtilValidate.isNotEmpty(partyGeoPointList) && osafeGeo.isNotEmpty()) {
                        GenericValue partyGeoPoint  = EntityUtil.getFirst((List<GenericValue>) partyGeoPointList);
                        GenericValue geoPoint  = partyGeoPoint.getRelatedOneCache("GeoPoint");
                        OsafeGeo preOsafeGeo = new OsafeGeo(geoPoint.getString("latitude"), geoPoint.getString("longitude"));
                        if (preOsafeGeo.isEmpty() || !preOsafeGeo.equals(osafeGeo)) {
                            Map updateGeoPointParams = UtilMisc.toMap("geoPointId", geoPoint.getString("geoPointId"),
                                                                      "dataSourceId","GEOPT_GOOGLE",
                                                                      "latitude", osafeGeo.latitude(),
                                                                      "longitude", osafeGeo.longitude(),
                                                                      "userLogin", userLogin);
                            Map result = dispatcher.runSync("updateGeoPoint", updateGeoPointParams);
                        }
                        continue;
                    }
                    if (osafeGeo.isNotEmpty()) {
                        Map createGeoPointParams = UtilMisc.toMap("dataSourceId","GEOPT_GOOGLE",
                                                                  "latitude", osafeGeo.latitude(),
                                                                  "longitude", osafeGeo.longitude(),
                                                                  "userLogin", userLogin);
                        Map result = dispatcher.runSync("createGeoPoint", createGeoPointParams);
                        String geoPointId = (String)result.get("geoPointId");

                        Map createPartyGeoPointParams = UtilMisc.toMap("partyId", partyId,
                                                                       "geoPointId", geoPointId,
                                                                       "userLogin", userLogin);
                        result = dispatcher.runSync("createPartyGeoPoint", createPartyGeoPointParams);
                    }

                } catch (Exception exc) {
                    Debug.logError(exc, module);
                }
            }
        } catch (Exception exc) {
            Debug.logError(exc, module);
            resp = ServiceUtil.returnError(exc.getMessage());
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }
}

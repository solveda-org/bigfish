package com.osafe.services;

import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.osafe.services.AddressDocument;
import com.osafe.services.AddressVerificationResponse;
import com.osafe.services.MelissaDataHelper;

public class MelissaDataServices {

    public static final String module = MelissaDataServices.class.getName();

    public static Map<String, ?> addressChecker(DispatchContext ctx, Map<String, ?> context) {
        Map<String, Object> responseMap = ServiceUtil.returnSuccess();
        String productStoreId = (String) context.get("productStoreId");
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String address3 = (String) context.get("address3");
        String city = (String) context.get("city");
        String state = (String) context.get("stateProvinceGeoId");
        String county = (String) context.get("countyGeoId");
        String postalCode = (String) context.get("postalCode");
        String postalCodeExt = (String) context.get("postalCodeExt");
        String country = (String) context.get("countryGeoId");

        AddressVerificationResponse avResponse = new AddressVerificationResponse();
        AddressDocument queryAddressdata = new AddressDocument();
        queryAddressdata.setAddress1(address1);
        queryAddressdata.setAddress2(address2);
        queryAddressdata.setAddress3(address3);
        queryAddressdata.setCity(city);
        queryAddressdata.setStateProvinceGeoId(state);
        queryAddressdata.setCountyGeoId(county);
        queryAddressdata.setPostalCode(postalCode);
        queryAddressdata.setPostalCodeExt(postalCodeExt);
        queryAddressdata.setCountryGeoId(country);
        
        MelissaDataHelper addressDataHelper = MelissaDataHelper.getInstance(productStoreId);
        if (UtilValidate.isNotEmpty(addressDataHelper))
        {
            try 
            {
                avResponse = addressDataHelper.verifyAddress(queryAddressdata);
            }
            catch (Exception e)
            {
                Debug.logError(e, "Error Verifying Melissa Address", module);
            }
        }
        responseMap.put("addressVerificationResponse",avResponse);
        return responseMap;
    }
}

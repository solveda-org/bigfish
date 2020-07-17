package com.osafe.services;

import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.osafe.services.AddressVerificationResponse;


import com.osafe.util.Util;

public class AddressServices {

    public static final String module = AddressServices.class.getName();

    public static Map<String, Object> extendedAddressValidation(DispatchContext dctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
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
        String addressValidationMethod = Util.getProductStoreParm(productStoreId, "ADDRESS_VERIFICATION_METHOD");
    	if (UtilValidate.isEmpty(addressValidationMethod))
    	{
    		addressValidationMethod="NONE";
    	}

    	if(addressValidationMethod.equalsIgnoreCase("MELISSA_DATA"))
    	{
            Map melissaAddressParams = UtilMisc.toMap("productStoreId",productStoreId);
            melissaAddressParams.put("address1",address1);
            melissaAddressParams.put("address2",address2);
            melissaAddressParams.put("address3",address3);
            melissaAddressParams.put("city",city);
            melissaAddressParams.put("stateProvinceGeoId",state);
            melissaAddressParams.put("countyGeoId",county);
            melissaAddressParams.put("postalCode",postalCode);
            melissaAddressParams.put("postalCodeExt",postalCodeExt);
            melissaAddressParams.put("countryGeoId",country);
            melissaAddressParams.put("userLogin", userLogin);
            
            try
            {
                Map result = dispatcher.runSync("addressValidationMelissaData", melissaAddressParams);
                if (!ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE)))
                {
                	avResponse = (AddressVerificationResponse) result.get("addressVerificationResponse");
                }
            }
            catch (Exception e)
            {
            	
            }
    	}
		avResponse.setVerificationMethod(addressValidationMethod);
        responseMap.put("addressVerificationResponse",avResponse);
        return responseMap;
    }

}

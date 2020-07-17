package com.osafe.services;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
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
        List suggestionList = FastList.newInstance();
        List<AddressDocument> alternateAddresses = avResponse.getAlternateAddresses();
        if (alternateAddresses != null )
        {
        	for (AddressDocument alternateAddress : alternateAddresses)
        	{
        		suggestionList.add(UtilMisc.toMap("address1",alternateAddress.getAddress1(),
        			"address2",alternateAddress.getAddress2(),"address3",alternateAddress.getAddress3(),
        			"city",alternateAddress.getCity(), "stateProvinceGeoId",alternateAddress.getStateProvinceGeoId(),
        			"countyGeoId",alternateAddress.getCountyGeoId(), "postalCode",alternateAddress.getPostalCode(),
        			"postalCodeExt",alternateAddress.getPostalCodeExt(), "countryGeoId",alternateAddress.getCountryGeoId()));
        	}
        }
        AddressDocument changedAddressdata = avResponse.getChangedAddressdata();
        if (changedAddressdata != null )
        {
            responseMap.put("address1",changedAddressdata.getAddress1());
            responseMap.put("address2",changedAddressdata.getAddress2());
            responseMap.put("address3",changedAddressdata.getAddress3());
            responseMap.put("city",changedAddressdata.getCity());
            responseMap.put("stateProvinceGeoId",changedAddressdata.getStateProvinceGeoId());
            responseMap.put("countyGeoId",changedAddressdata.getCountyGeoId());
            responseMap.put("postalCode",changedAddressdata.getPostalCode());
            responseMap.put("postalCodeExt",changedAddressdata.getPostalCodeExt());
            responseMap.put("countryGeoId",changedAddressdata.getCountryGeoId());
        }
        else
        {
            responseMap.put("address1",address1);
            responseMap.put("address2",address2);
            responseMap.put("address3",address3);
            responseMap.put("city",city);
            responseMap.put("stateProvinceGeoId",state);
            responseMap.put("countyGeoId",county);
            responseMap.put("postalCode",postalCode);
            responseMap.put("postalCodeExt",postalCodeExt);
            responseMap.put("countryGeoId",country);
        }
        responseMap.put("responseCode",avResponse.getResponseCode());
        responseMap.put("suggestionList",suggestionList);
        return responseMap;
    }
}

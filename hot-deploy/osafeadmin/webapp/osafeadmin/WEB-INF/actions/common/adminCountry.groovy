import javolution.util.FastList;
import org.ofbiz.base.util.StringUtil;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.Debug

defaultCountry = OsafeAdminUtil.getProductStoreParm(request,"COUNTRY_DEFAULT");
countryDropDown = OsafeAdminUtil.getProductStoreParm(request,"COUNTRY_DROPDOWN");
defaultCountryGeoMap = [:];
List countryList = FastList.newInstance();

if (UtilValidate.isNotEmpty(defaultCountry))
 {
  defaultCountryGeoMap = OsafeAdminUtil.getCountryGeoInfo(delegator, defaultCountry.trim());
  if (UtilValidate.isNotEmpty(defaultCountryGeoMap))
   {
      context.defaultCountryGeoMap = defaultCountryGeoMap;
   }
 }

if(OsafeAdminUtil.isProductStoreParmTrue(request,"COUNTRY_MULTI")) 
{
    if(("ALL").equalsIgnoreCase(countryDropDown)) 
    {
		countryList = CommonWorkers.getCountryList(delegator);
    }
    else
    {
	  countryGeoCodeList = StringUtil.split(countryDropDown,",")
	  for (String geoId: countryGeoCodeList) 
	   {
          countryGeoId = geoId;
          countryGeoMap = OsafeAdminUtil.getCountryGeoInfo(delegator, countryGeoId.trim());
          if(UtilValidate.isNotEmpty(countryGeoMap))
          {
            countryList.add(countryGeoMap);
          }
	   }
     }
}

if (UtilValidate.isEmpty(countryList))
 {
    countryList = CommonWorkers.getCountryList(delegator);
 }
if (UtilValidate.isEmpty(defaultCountryGeoMap))
{
   defaultCountryGeoMap.put("geoId","USA");
   context.defaultCountryGeoMap = defaultCountryGeoMap;
}
context.countryList = countryList;
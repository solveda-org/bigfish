package user;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import com.osafe.util.OsafeAdminUtil;


//get entity for UserLogin --> userInfo
userLoginId = StringUtils.trimToEmpty(parameters.userLoginId);
context.userLoginId = userLoginId;
if (UtilValidate.isNotEmpty(userLoginId))
{
    userInfo = delegator.findByPrimaryKey("UserLogin", [userLoginId : userLoginId]);
    context.userInfo = userInfo;	
	
	if ((UtilValidate.isNotEmpty(userInfo)) && (UtilValidate.isNotEmpty(userInfo.disabledDateTime)))
	{
		disDate = OsafeAdminUtil.convertDateTimeFormat(userInfo.disabledDateTime, preferredDateFormat);
		disHour = UtilDateTime.getHour(UtilDateTime.toTimestamp(userInfo.disabledDateTime), timeZone, locale);
		disMinute = UtilDateTime.getMinute(UtilDateTime.toTimestamp(userInfo.disabledDateTime), timeZone, locale);
		disTimeAMPM = ((disHour/12) < 1)?1:2;// 1 for AM and 2 for PM
		disHour = ((disHour%12) == 0)?12:(disHour%12);
		context.disDate = disDate;
		context.disHour = Integer.toString(disHour);
		context.disMinute = Integer.toString(disMinute);
		context.disTimeAMPM = Integer.toString(disTimeAMPM);
	}
}










package user;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.UtilDateTime;  

//get entity for UserLogin --> userInfo
userLoginId = StringUtils.trimToEmpty(parameters.userLoginId);
context.userLoginId = userLoginId;
if (UtilValidate.isNotEmpty(userLoginId))
{
    userInfo = delegator.findByPrimaryKey("UserLogin", [userLoginId : userLoginId]);
    context.userInfo = userInfo;	
	
	if ((UtilValidate.isNotEmpty(userInfo)) && (UtilValidate.isNotEmpty(userInfo.disabledDateTime)))
	{
		disDate = OsafeAdminUtil.convertDateTimeFormat(userInfo.disabledDateTime, entryDateTimeFormat);
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
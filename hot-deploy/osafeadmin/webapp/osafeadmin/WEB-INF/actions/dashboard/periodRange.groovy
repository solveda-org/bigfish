
package  dashboard;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.ObjectType;

import java.util.Locale;
import java.util.TimeZone;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.product.ProductWorker;

import com.ibm.icu.util.Calendar;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression.Context;

import com.osafe.util.OsafeAdminUtil;

nowTs = UtilDateTime.nowTimestamp();

context.periodFrom ="";
context.periodTo ="";
String periodFrom = request.getParameter("periodFrom");
String periodTo = request.getParameter("periodTo");
Timestamp periodFromTs = null;
Timestamp periodToTs = null;
Boolean isValidDate = true;
if(UtilValidate.isEmpty(periodFrom))
{
    String periodFromSess = session.getAttribute("periodFrom");
    periodFrom = periodFromSess;
    if(UtilValidate.isEmpty(periodFrom))
    {
        periodFromTs = UtilDateTime.getDayStart(nowTs);
        periodFrom = UtilDateTime.timeStampToString(periodFromTs, entryDateTimeFormat, timeZone, locale);
    }
}

if(UtilValidate.isEmpty(periodTo))
{
    String periodToSess = session.getAttribute("periodTo");
    periodTo = periodToSess;
    if(UtilValidate.isEmpty(periodTo))
    {
        periodToTs = nowTs;
        periodTo = UtilDateTime.timeStampToString(periodToTs, entryDateTimeFormat, timeZone, locale);
    }
}

if(UtilValidate.isNotEmpty(periodFrom))
{
    if(OsafeAdminUtil.isDateTime(periodFrom, entryDateTimeFormat))
    {
        periodFromTs =ObjectType.simpleTypeConvert(periodFrom, "Timestamp", entryDateTimeFormat, locale);
        context.periodFrom = periodFrom;
        context.periodFromTs = periodFromTs;
        session.setAttribute("periodFrom", periodFrom);
    }
    else
    {
        isValidDate = false;
    }
}

if(UtilValidate.isNotEmpty(periodTo)) 
{
    if(OsafeAdminUtil.isDateTime(periodTo, entryDateTimeFormat))
    {
        periodToTs =ObjectType.simpleTypeConvert(periodTo, "Timestamp", entryDateTimeFormat, locale);
        context.periodTo = periodTo;
        context.periodToTs = periodToTs;
        session.setAttribute("periodTo", periodTo);
    }
    else
    {
        isValidDate = false;
    }
    
}
context.isValidDate = isValidDate;
context.periodFrom = periodFrom;
context.periodTo = periodTo;


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
import com.osafe.util.Util;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression.Context;

import com.osafe.util.Util;
import com.osafe.util.OsafeAdminUtil;

nowTs = UtilDateTime.nowTimestamp();

preferredDateFormat = Util.getProductStoreParm(request, "FORMAT_DATE");
preferredDateFormat = OsafeAdminUtil.isValidDateFormat(preferredDateFormat)?preferredDateFormat:"MM/dd/yy";
String entryDateFormat = preferredDateFormat;
context.periodFrom ="";
context.periodTo ="";
String periodFrom = request.getParameter("periodFrom");
String periodTo = request.getParameter("periodTo");
Timestamp periodFromTs = null;
Timestamp periodToTs = null;
if(UtilValidate.isEmpty(periodFrom)){
    String periodFromSess = session.getAttribute("periodFrom");
    periodFrom = periodFromSess;
    if(UtilValidate.isEmpty(periodFrom)){
        periodFromTs = UtilDateTime.getDayStart(nowTs);
        periodFrom = UtilDateTime.timeStampToString(periodFromTs, entryDateFormat, timeZone, locale);
    }
}

if(UtilValidate.isEmpty(periodTo)){
    String periodToSess = session.getAttribute("periodTo");
    periodTo = periodToSess;
    if(UtilValidate.isEmpty(periodTo)){
        periodToTs = UtilDateTime.getDayEnd(nowTs);
        periodTo = UtilDateTime.timeStampToString(periodToTs, entryDateFormat, timeZone, locale);
    }
}

if(UtilValidate.isNotEmpty(periodFrom) && Util.isDateTime(periodFrom)) {
    periodFromTs =ObjectType.simpleTypeConvert(periodFrom, "Timestamp", entryDateFormat, locale);
    periodFromTs = UtilDateTime.getDayStart(periodFromTs);
    context.periodFrom = periodFrom;
    context.periodFromTs = periodFromTs;
    session.setAttribute("periodFrom", periodFrom);
}

if(UtilValidate.isNotEmpty(periodTo) && Util.isDateTime(periodTo)) {
    periodToTs =ObjectType.simpleTypeConvert(periodTo, "Timestamp", entryDateFormat, locale);
    periodToTs = UtilDateTime.getDayEnd(periodToTs);
    context.periodTo = periodTo;
    context.periodToTs = periodToTs;
    session.setAttribute("periodTo", periodTo);
}

String respString = Util.checkDateRange(periodFrom, periodTo, entryDateFormat);
if(!"success".equals(respString)) {
    session.setAttribute("periodTo", null);
    session.setAttribute("periodFrom", null);
    if("invalidFormat".equals(respString)){
        context.periodFrom = periodFrom;
        context.periodTo = periodTo;
    }
}

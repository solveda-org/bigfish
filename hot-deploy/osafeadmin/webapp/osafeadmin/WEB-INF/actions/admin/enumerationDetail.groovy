package admin;
import org.ofbiz.base.util.UtilHttp;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Date;
import java.sql.Timestamp;
userLogin = session.getAttribute("userLogin");
requestParams = UtilHttp.getParameterMap(request);
context.userLogin=userLogin;
enumeration="";
enumId="";

enumId = requestParams.get("enumId");
if (!enumId) 
{
    enumId = parameters.enumId;
}

if(UtilValidate.isNotEmpty(enumId))
{
    enumeration = delegator.findOne("Enumeration",UtilMisc.toMap("enumId", enumId), false);
}
context.enum = enumeration;
context.enumId = enumId;
 
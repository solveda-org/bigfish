package content;
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
content="";
contentId="";

contentId = requestParams.get("contentId");
if (!contentId) 
  {
    contentId = parameters.contentId;
  }

if(UtilValidate.isNotEmpty(contentId))
{
    content = delegator.findOne("Content",UtilMisc.toMap("contentId", contentId), false);
}
if (UtilValidate.isNotEmpty(content))
 {
    dataResource = content.getRelatedOne("DataResource");
    if (UtilValidate.isNotEmpty(dataResource))
     {
        electronicText = dataResource.getRelatedOne("ElectronicText");
        context.eText = electronicText.textData;
     }
 }
context.content = content;
context.contentId = contentId;
 
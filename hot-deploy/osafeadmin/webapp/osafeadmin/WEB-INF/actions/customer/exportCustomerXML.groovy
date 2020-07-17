import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;

List customerList = new ArrayList();
List<GenericValue> customerXMLList = null;
if(parameters.partyId || parameters.exportId)
{
    if(parameters.partyId)
    {
        customerList.add(parameters.partyId);
    }
    if(parameters.exportId)
    {
        customerList.add(parameters.exportId);
    }
    context.exportId = parameters.partyId;
}
else
{
    session = context.session;
    svcCtx = session.getAttribute("customerPDFMap");
    if (UtilValidate.isEmpty(svcCtx)) {
        Map<String, Object> svcCtx = FastMap.newInstance();
    }

    if (UtilValidate.isNotEmpty(svcCtx)) 
    {
	    svcCtx.put("VIEW_SIZE", "10000");
	    svcCtx.put("lookupFlag", "Y");
	    svcCtx.put("showAll", "N");
	    svcCtx.put("roleTypeId", "ANY");
	    svcCtx.put("partyTypeId", "ANY");
	    svcCtx.put("statusId", "ANY");
	    svcCtx.put("extInfo", "N");
	    svcCtx.put("partyTypeId", "PERSON");

        Map<String, Object> svcRes;
        svcRes = dispatcher.runSync("findParty", svcCtx);
        customerXMLList =  UtilGenerics.checkList(svcRes.get("completePartyList"), GenericValue.class);
        customerXMLList = EntityUtil.orderBy(customerXMLList, ["partyId"]);
        for(GenericValue party : customerXMLList)
        {
            customerList.add(party.partyId);
        }
    }
}
if (UtilValidate.isNotEmpty(customerList)) 
{
    context.exportIdList = customerList
}

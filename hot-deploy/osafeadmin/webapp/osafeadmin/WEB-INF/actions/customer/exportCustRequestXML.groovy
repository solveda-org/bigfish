package customer;

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastMap;
import org.ofbiz.product.store.ProductStoreWorker;

userLogin = session.getAttribute("userLogin");
custRequestCond = session.getAttribute("custRequestCond");
custRequestList = delegator.findList("CustRequest",custRequestCond, null, null, null, false);

custRequest = EntityUtil.getFirst(custRequestList);;

List custRequestIdList = new ArrayList();
if (UtilValidate.isNotEmpty(custRequestList)) 
{
    for(GenericValue custRequest : custRequestList)
    {
        custRequestId = custRequest.custRequestId;
        Map<String, Object> updateCustReqAttrCtx = FastMap.newInstance();
        updateCustReqAttrCtx.put("userLogin",userLogin);
        updateCustReqAttrCtx.put("custRequestId",custRequestId);
        updateCustReqAttrCtx.put("attrName","IS_DOWNLOADED");
        updateCustReqAttrCtx.put("attrValue","Y");
        custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","IS_DOWNLOADED"));
        if(UtilValidate.isNotEmpty(custReqAttribute))
        {
            dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
        }
        else
        {
            dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
        }
        
        custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","DATETIME_DOWNLOADED"));
        updateCustReqAttrCtx.put("attrName","DATETIME_DOWNLOADED");
        updateCustReqAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
        if(UtilValidate.isNotEmpty(custReqAttribute))
        {
            dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
        }
        else
        {
            dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
        }
    }
    
    custRequestIdList = EntityUtil.getFieldListFromEntityList(custRequestList, "custRequestId", true);
    
    Map<String, Object> exportCustRequestCtx = FastMap.newInstance();

    productStore = ProductStoreWorker.getProductStore(request);
    exportCustRequestCtx.put("userLogin", userLogin);
    exportCustRequestCtx.put("custRequestIdList",custRequestIdList);
    exportCustRequestCtx.put("productStoreId",productStore.productStoreId);
    exportResult = null;
    if(custRequest.custRequestTypeId == 'RF_CONTACT_US') {
        exportResult = dispatcher.runSync("exportCustRequestContactUsXML", exportCustRequestCtx);
    } else if(custRequest.custRequestTypeId == 'RF_CATALOG') {
        exportResult = dispatcher.runSync("exportCustRequestCatalogXML", exportCustRequestCtx);
    }
    
    custRequestXMLName = exportResult.feedsFileName;
    response.setContentType("text/xml");
    response.setHeader("Content-Disposition","attachment; filename=\"" + custRequestXMLName + "\";");
        
    String downloadTempDir = exportResult.feedsDirectoryPath;
    String filePath = downloadTempDir + custRequestXMLName; 
    InputStream inputStr = new FileInputStream(filePath);
    OutputStream out = response.getOutputStream();
    byte[] bytes = new byte[102400];
    int bytesRead;
    while ((bytesRead = inputStr.read(bytes)) != -1)
    {
        out.write(bytes, 0, bytesRead);
    }
    out.flush();
    out.close();
    inputStr.close();
    
    
    
}

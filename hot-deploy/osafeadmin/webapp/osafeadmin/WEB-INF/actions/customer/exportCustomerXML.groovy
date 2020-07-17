import org.ofbiz.base.util.UtilValidate;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.base.util.UtilDateTime;
import com.osafe.util.OsafeAdminUtil;

List customerList = new ArrayList();
List<GenericValue> customerXMLList = null;
if(parameters.partyId)
{
    customerList.add(parameters.partyId);
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
        for(String partyId : customerList)
        {
            person = delegator.findOne("Person",["partyId":partyId], true);
            
            partyAttrIsDownload = delegator.findOne("PartyAttribute", ["partyId" : partyId, "attrName" : "IS_DOWNLOADED"], true);
            Map<String, Object> isDownloadedPartyAttrCtx = FastMap.newInstance();
            isDownloadedPartyAttrCtx.put("partyId", partyId);
            isDownloadedPartyAttrCtx.put("userLogin",userLogin);
            isDownloadedPartyAttrCtx.put("attrName","IS_DOWNLOADED");
            isDownloadedPartyAttrCtx.put("attrValue","Y");
            Map<String, Object> isDownloadedPartyAttrMap = null;
            if (UtilValidate.isNotEmpty(partyAttrIsDownload)) {
                isDownloadedPartyAttrMap = dispatcher.runSync("updatePartyAttribute", isDownloadedPartyAttrCtx);
            } else {
                isDownloadedPartyAttrMap = dispatcher.runSync("createPartyAttribute", isDownloadedPartyAttrCtx);
            }
            
            partyAttrDateTimeDownload = delegator.findOne("PartyAttribute", ["partyId" : partyId, "attrName" : "DATETIME_DOWNLOADED"], true);
            Map<String, Object> dateTimeDownloadedPartyAttrCtx = FastMap.newInstance();
            dateTimeDownloadedPartyAttrCtx.put("partyId", partyId);
            dateTimeDownloadedPartyAttrCtx.put("userLogin",userLogin);
            dateTimeDownloadedPartyAttrCtx.put("attrName","DATETIME_DOWNLOADED");
            dateTimeDownloadedPartyAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
            Map<String, Object> dateTimeDownloadedPartyAttrMap = null;
            if (UtilValidate.isNotEmpty(partyAttrDateTimeDownload)) {
                dateTimeDownloadedPartyAttrMap = dispatcher.runSync("updatePartyAttribute", dateTimeDownloadedPartyAttrCtx);
            } else {
                dateTimeDownloadedPartyAttrMap = dispatcher.runSync("createPartyAttribute", dateTimeDownloadedPartyAttrCtx);
            }
 
        }
        
    }

Map<String, Object> exportCustomerCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");

productStore = ProductStoreWorker.getProductStore(request);
exportCustomerCtx.put("userLogin", userLogin);
exportCustomerCtx.put("customerList",customerList);
exportCustomerCtx.put("productStoreId",productStore.productStoreId);

exportResult = dispatcher.runSync("exportCustomerXML", exportCustomerCtx);
    /*Send Customer XML for browser.*/
    response.setContentType("text/xml");
    customerXMLName = exportResult.feedsFileName;
    response.setHeader("Content-Disposition","attachment; filename=\"" + customerXMLName + "\";");
    String downloadTempDir = exportResult.feedsDirectoryPath;
    String filePath = downloadTempDir + customerXMLName; 
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

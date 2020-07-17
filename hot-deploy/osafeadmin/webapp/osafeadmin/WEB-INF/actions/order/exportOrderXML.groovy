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
import com.osafe.util.OsafeAdminUtil;

List orderList = new ArrayList();
if(parameters.orderId)
{
    orderList.add(parameters.orderId);
}
else
{
    session = context.session;
    svcCtx = session.getAttribute("orderPDFMap");
    if (UtilValidate.isEmpty(svcCtx)) {
        Map<String, Object> svcCtx = FastMap.newInstance();
    }
    if (UtilValidate.isNotEmpty(svcCtx)) {
        svcCtx.put("viewSize",  Integer.valueOf("1000"));
        Map<String, Object> svcRes = dispatcher.runSync("searchOrders", svcCtx);
        List<GenericValue> orderXMLList = UtilGenerics.checkList(svcRes.get("completeOrderList"), GenericValue.class);
        for(GenericValue order : orderXMLList)
        {
            orderList.add(order.orderId);
        }
    }
}
Map<String, Object> exportOrderCtx = FastMap.newInstance();
userLogin = session.getAttribute("userLogin");

productStore = ProductStoreWorker.getProductStore(request);
exportOrderCtx.put("userLogin", userLogin);
exportOrderCtx.put("orderList",orderList);
exportOrderCtx.put("productStoreId",productStore.productStoreId);

exportResult = dispatcher.runSync("exportOrderXML", exportOrderCtx);

    /*Send sitemap for browser.*/
    response.setContentType("text/xml");
    orderXMLName = exportResult.feedsFileName;
    
    response.setHeader("Content-Disposition","attachment; filename=\"" + orderXMLName + "\";");
    String downloadTempDir = exportResult.feedsDirectoryPath;
    String filePath = downloadTempDir + orderXMLName; 
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

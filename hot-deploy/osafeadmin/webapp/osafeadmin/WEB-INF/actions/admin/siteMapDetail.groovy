import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;

svcCtx = [:];
    /*Prepare map for service that will generate the sitemap.*/
    productStoreId = ProductStoreWorker.getProductStoreId(request);
    svcCtx.productStoreId = productStoreId;
    currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
    rootProductCategoryId = CatalogWorker.getCatalogTopCategoryId(request, currentCatalogId);
    svcCtx.browseRootProductCategoryId = rootProductCategoryId;
    svcCtx.userLogin = userLogin;
    svcRes = dispatcher.runSync("genSiteMap", svcCtx);
    
    /*Send sitemap for browser.*/
    response.setContentType("text/xml");
    response.setHeader("Content-Disposition","inline; filename=" + svcRes.siteMapFile);
    InputStream inputStr = new FileInputStream(svcRes.siteMapFile);
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

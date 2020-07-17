import org.ofbiz.base.util.UtilValidate;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*;


    /*Send sitemap for browser.*/
    response.setContentType("text/xls");
    response.setHeader("Content-Disposition","attachment; filename=ClientProductImport.xls");
    tempDir = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-import-data-path"), context);
    String filePath = tempDir + "/clientProductImport.xls"; 
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

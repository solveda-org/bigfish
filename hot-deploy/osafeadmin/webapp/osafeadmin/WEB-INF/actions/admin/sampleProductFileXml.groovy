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


    /*Send sitemap for browser.*/
    response.setContentType("text/xml");
    response.setHeader("Content-Disposition","attachment; filename=sampleClientProductImport.xml");
    tempDir = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-import-data-path"), context);
    String filePath = tempDir + "/sampleClientProductImport.xml"; 
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

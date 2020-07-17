import org.ofbiz.base.util.UtilValidate;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import org.apache.commons.io.FileUtils;
import com.osafe.services.OsafeAdminFeedServices;

convertedFilePath = OsafeAdminFeedServices.getConvertedBigfishXMLFilePath();
if(UtilValidate.isNotEmpty(convertedFilePath) && UtilValidate.isNotEmpty(parameters.convertedFileName))
{
    response.setContentType("text/xml");
    response.setHeader("Content-Disposition","inline; filename=" + parameters.convertedFileName);
    InputStream inputStr = new FileInputStream(new File(convertedFilePath,parameters.convertedFileName));
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
    try
    {
    	new File(convertedFilePath,parameters.convertedFileName).delete();
    }
    catch(Exception e)
    {
    }
    return "success";
}
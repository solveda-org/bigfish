package content;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import java.io.File;
import com.osafe.util.OsafeAdminUtil;
import java.awt.*;
import java.awt.image.PixelGrabber;
import org.ofbiz.base.util.string.FlexibleStringExpander;

String mediaType = StringUtils.trimToEmpty(parameters.mediaType);
String mediaName = StringUtils.trimToEmpty(parameters.mediaName);
initializedCB = StringUtils.trimToEmpty(parameters.initializedCB);
preRetrieved = StringUtils.trimToEmpty(parameters.preRetrieved);
if (UtilValidate.isNotEmpty(preRetrieved))
{
   context.preRetrieved=preRetrieved;
}
else
{
  preRetrieved = context.preRetrieved;
}

if (UtilValidate.isNotEmpty(initializedCB))
{
   context.initializedCB=initializedCB;
}
fileListMap = FastMap.newInstance();
fileList = FastList.newInstance();
    fileArray = OsafeAdminUtil.getUserContent(mediaType);
    osafeThemeServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "osafe.theme.server"), context);
    userContentImagePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "user-content.image-path"), context);
    Toolkit tool = Toolkit.getDefaultToolkit();
    if(UtilValidate.isNotEmpty(preRetrieved) && preRetrieved != "N") 
    {
        for (File file: fileArray) {
            try {
                parentPath = file.getParent();
                File parentDirPath = new File(parentPath);
                parentDirName = parentDirPath.getName()
                attrMap = FastMap.newInstance();
                String fileName = file.getName();
                double filesize = file.length();
                double filesizeInKB = filesize / 1024;
                filesizeInKB = Math.round(filesizeInKB*100)/100.0d;
                userContentPath = osafeThemeServerPath + userContentImagePath + parentDirName+"/";
                Image image = tool.getImage(userContentPath+fileName);
                PixelGrabber grabber = new PixelGrabber(image,0, 0, -1, -1, false);
                grabber.grabPixels();
                int height = grabber.getHeight();
                int width = grabber.getWidth();
                
                attrMap.put("fileSize",String.valueOf(filesizeInKB));
                attrMap.put("height",height);
                attrMap.put("width",width);
                imagePath = userContentImagePath + parentDirName + "/" + fileName;
                attrMap.put("imagePath",imagePath);
                attrMap.put("parentDirName",parentDirName);
                fileListMap.put(fileName, attrMap);
                if(UtilValidate.isNotEmpty(mediaName))
                {
                    //Search By Media Name
                    filNameUpper = fileName.toUpperCase();
                    mediaNameUpper = mediaName.toUpperCase();
                    if(filNameUpper.contains(mediaNameUpper)) 
                    {
                        fileList.add(fileName);
                    }
                }
                else
                {
                    fileList.add(fileName);
                }
            } catch (IOException ioe) {
                Debug.logError(ioe, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }
    }
    if(UtilValidate.isNotEmpty(fileListMap)) {
        context.fileListMap = fileListMap;
    }
    Collections.sort(fileList);
    pagingListSize = fileList.size();
    context.pagingListSize = pagingListSize;
    context.pagingList = fileList;
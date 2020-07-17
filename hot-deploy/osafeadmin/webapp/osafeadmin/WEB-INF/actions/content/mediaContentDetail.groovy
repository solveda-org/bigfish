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
import java.util.List;

import com.osafe.util.OsafeAdminUtil;
import java.awt.*;
import java.awt.image.PixelGrabber;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import java.io.FileInputStream;

fileAttrMap = FastMap.newInstance();
osafeProperties = UtilProperties.getResourceBundleMap("OsafeProperties.xml", locale);
if(UtilValidate.isNotEmpty(parameters.currentMediaName))
{
    mediaName = parameters.currentMediaName;
}
else
{
    mediaName = parameters.mediaName;
}

if(UtilValidate.isNotEmpty(mediaName))
{
    
    if(UtilValidate.isNotEmpty(parameters.currentMediaType))
    {
        mediaType = parameters.currentMediaType;
    }
    else
    {
        mediaType = parameters.mediaType;
    }
    osafeThemeServerPath = FlexibleStringExpander.expandString(osafeProperties.osafeThemeServer, context);
    userContentImagePath = FlexibleStringExpander.expandString(osafeProperties.userContentImagePath, context);
    
    userContentPath = osafeThemeServerPath + userContentImagePath + mediaType + "/";
    imagePath = userContentImagePath + mediaType + "/" + mediaName;
    File file = new File(userContentPath + mediaName);
    fileAttrMap.put("imagePath", imagePath);
    int height = -1;
    int width = -1;
    int originalWidth = 0; 
    
    //get the size of file
    double filesize = file.length();
    double filesizeInKB = filesize / 1024;
    filesizeInKB = Math.round(filesizeInKB*100)/100.0d;
    fileAttrMap.put("fileSize", filesizeInKB);
	
	Image image = null;
	FileInputStream fis = null;
	try
	{
	    //get the dimension of file
	    Toolkit tool = Toolkit.getDefaultToolkit();
		fis = new FileInputStream(userContentPath + mediaName);
	    image = tool.createImage(UtilObject.getBytes(fis));
	    PixelGrabber grabber = new PixelGrabber(image,0, 0, -1, -1, false);
	    grabber.grabPixels();
	    height = grabber.getHeight();
	    width = grabber.getWidth();
	    if(height > 0 && width > 0)
	    {
	        originalWidth = width;
	        if(width > 3500)
	        {
	            width = (width*10/100);
	        }
	        else if ((width < 3500) && (width >= 2800))
	        {
	            width = (width*20/100);
	        }
	        else if ((width < 2800) && (width >= 2100))
	        {
	            width = (width*30/100);
	        }
	        else if ((width < 2100) && (width >= 1400))
	        {
	            width = (width*40/100);
	        }
	        else if ((width < 1400) && (width >= 700))
	        {
	            width = (width*50/100);
	        }
	    }
    } 
	catch (Exception exc)
	{
        Debug.logError(exc, "MediaConetnt");
    }
	finally
	{
		if(UtilValidate.isNotEmpty(image))
		{
		    image.flush();
		}
		if(UtilValidate.isNotEmpty(fis))
		{
		    fis.close();
		}
	}
    
    fileAttrMap.put("height", height);
    fileAttrMap.put("width", width);
    fileAttrMap.put("originalWidth", originalWidth);
    
    messageMap=[:];
    messageMap.put("height", height);
    messageMap.put("width", width);
    messageMap.put("originalWidth", originalWidth);
    
    context.optionalResizeNoteInfo = UtilProperties.getMessage("OSafeAdminUiLabels","OptionalResizeNoteInfo",messageMap, locale );
    
    context.mediaName = mediaName;
    context.currentMediaType = mediaType;
    context.fileAttrMap = fileAttrMap;
}

//get List of user_content directories
List<File> fileList = OsafeAdminUtil.getUserContentDirectories();
List<String> directoryNameList = FastList.newInstance();
if(UtilValidate.isNotEmpty(fileList))
{
	for(File file : fileList) {
		directoryNameList.add(file.getName());
	}
}

context.directoryNameList = directoryNameList;






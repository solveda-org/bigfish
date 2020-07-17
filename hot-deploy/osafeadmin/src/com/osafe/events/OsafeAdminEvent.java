package com.osafe.events;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.HttpRequestFileUpload;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import com.osafe.util.OsafeAdminUtil;


public class OsafeAdminEvent {
    
    public static String validateMediaContent(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = UtilHttp.getLocale(request);
        if (locale == null)
            locale = Locale.getDefault();
        String mediaType = request.getParameter("mediaType");
        Map<String, Object> serviceContext = FastMap.newInstance();
        
        String osafeThemeServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "osafe.theme.server"), serviceContext);
        String userContentImagePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "user-content.image-path"), serviceContext);
        
        String contentTargetPath =  osafeThemeServerPath + userContentImagePath + mediaType+"/";
        String contentTempPath =  osafeThemeServerPath+"/osafe_theme/images/temp_user_content/";
        HttpRequestFileUpload uploadTempObject = new HttpRequestFileUpload();
        
        uploadTempObject.setSavePath(contentTempPath);
        try {
            uploadTempObject.doUpload(request);
        } catch(Exception e) {
            String errMsg = UtilProperties.getMessage("OSafeAdminUiLabels", "ValidMediaFileError", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        String mediaFileName = uploadTempObject.getFilename();
        List<File> fileList = OsafeAdminUtil.getUserContent(mediaType);
        List<String> fileNameList = FastList.newInstance();
        for(File file : fileList) {
            fileNameList.add(file.getName());
        }
        if(fileNameList.contains(mediaFileName)) {
            String errMsg = UtilProperties.getMessage("OSafeAdminUiLabels", "SameNameMediaFileError", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        } else {
            request.setAttribute("contentTempPath", contentTempPath);
            request.setAttribute("contentTargetPath", contentTargetPath);
            request.setAttribute("mediaFileName", mediaFileName);
        }
       return "success";
    }
}
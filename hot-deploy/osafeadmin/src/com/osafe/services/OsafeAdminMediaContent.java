package com.osafe.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import com.osafe.util.OsafeAdminUtil;

public class OsafeAdminMediaContent {
    public static final String module = OsafeAdminMediaContent.class.getName();
    public static Map<String, Object> serviceContext = FastMap.newInstance();
    public static String osafeThemeServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "osafe.theme.server"), serviceContext);
    public static String userContentImagePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "user-content.image-path"), serviceContext);
    
    
    public static Map<String, ?> updateMediaContent(DispatchContext dctx, Map<String, ?> context) throws IOException, JDOMException {
        Locale locale = (Locale) context.get("locale");
        List<String> error_list = new ArrayList<String>();
        
        String mediaType = (String) context.get("mediaType");
        String currentMediaName = (String) context.get("currentMediaName");
        String currentMediaType = (String) context.get("currentMediaType");
        ByteBuffer uploadedMediaFile = (ByteBuffer) context.get("uploadedMediaFile");
        String uploadedMediaFileName = (String) context.get("_uploadedMediaFile_fileName");
        
        if (UtilValidate.isEmpty(uploadedMediaFileName)) {
        	if(currentMediaType.equals(mediaType)) {
        		return ServiceUtil.returnSuccess();
        	} else {
        		List<File> fileList = OsafeAdminUtil.getUserContent(mediaType);
                List<String> fileNameList = FastList.newInstance();
                for(File file : fileList) {
                    fileNameList.add(file.getName());
                }
                if(fileNameList.contains(currentMediaName)) {
                    error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "SameNameMediaFileError", locale));
                    return ServiceUtil.returnError(error_list);
                }
        	}
        } else {
            int uploadedFileExtIndex = uploadedMediaFileName.lastIndexOf(".");
            String uploadedFileExt = uploadedMediaFileName.substring(uploadedFileExtIndex+1,uploadedMediaFileName.length());
            int currentFileExtIndex = currentMediaName.lastIndexOf(".");
            String currentFileExt = currentMediaName.substring(currentFileExtIndex+1,currentMediaName.length());
            if(!currentFileExt.equalsIgnoreCase(uploadedFileExt)){
                error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "FileTypeMismatchError", UtilMisc.toMap("currentFileExt", currentFileExt.toUpperCase(),"uploadedFileExt", uploadedFileExt.toUpperCase()), locale));
                return ServiceUtil.returnError(error_list);
            }
        }
        
        if (UtilValidate.isNotEmpty(uploadedMediaFileName)) {
        	
                File file = new File(osafeThemeServerPath + userContentImagePath + mediaType + "/" + currentMediaName);
                try {
                    RandomAccessFile out = new RandomAccessFile(file, "rw");
                    out.write(uploadedMediaFile.array());
                    out.close();
                } catch (FileNotFoundException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to open file for writing: " + file.getAbsolutePath());
                } catch (IOException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to write binary data to: " + file.getAbsolutePath());
                }
                if(!currentMediaType.equals(mediaType)) {
                	File currentFile = new File(osafeThemeServerPath + userContentImagePath + currentMediaType + "/" + currentMediaName);
                	try {
                        //delete the source file. 
                        FileUtils.forceDelete(currentFile);
                    } catch(Exception e) {
                        Debug.logError (e, module);
                    }
            	}   
            
        } else {
            String contentSourcePath =  osafeThemeServerPath + userContentImagePath + currentMediaType+"/";
            String contentTargetPath =  osafeThemeServerPath + userContentImagePath + mediaType+"/";
            OsafeAdminUtil.moveContent(contentSourcePath, contentTargetPath, currentMediaName);
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, ?> addMediaContent(DispatchContext dctx, Map<String, ?> context) {
        String contentTempPath = (String) context.get("contentTempPath");
        String contentTargetPath = (String) context.get("contentTargetPath");
        String mediaFileName = (String) context.get("mediaFileName");
        
        OsafeAdminUtil.moveContent(contentTempPath, contentTargetPath, mediaFileName);
        try {
            File tempDir = new File(contentTempPath);
            FileUtils.deleteDirectory(tempDir);
        } catch(Exception e) {
        	Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, ?> deleteMediaContent(DispatchContext dctx, Map<String, ?> context) {
        Locale locale = (Locale) context.get("locale");
        List<String> successMessageList = new ArrayList<String>();
        String currentMediaType = (String) context.get("currentMediaType");
        String currentMediaName = (String) context.get("currentMediaName");
        String mediaFilePath =  osafeThemeServerPath + userContentImagePath + currentMediaType+"/";
        try {
            File deleteFile = new File(mediaFilePath+currentMediaName);
            deleteFile.delete();
        } catch(Exception e) {
        	Debug.logError(e, module);
        }
        String deletedSuccessMessage = UtilProperties.getMessage("OSafeAdminUiLabels", "DeletedSuccess", locale);
        successMessageList.add(deletedSuccessMessage);
        return ServiceUtil.returnSuccess(successMessageList);
    }
}
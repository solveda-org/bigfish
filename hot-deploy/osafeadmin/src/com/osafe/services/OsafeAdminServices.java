package com.osafe.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.osafe.util.OsafeAdminUtil;

public class OsafeAdminServices {
    public static final String module = OsafeAdminServices.class.getName();
    
    public static Map<String, Object> loadProductDataToDB(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productLoadImagesDir = (String) context.get("productLoadImagesDir");
        String imageUrl = (String) context.get("imageUrl");
        String xlsFileName = (String)context.get("xlsFileName");
        String processingOpt = (String)context.get("processingOpt");
        String errorExists = (String)context.get("errorExists");
        
        List<String> error_list = new ArrayList<String>();
        if (UtilValidate.isEmpty(processingOpt)) {
            error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankProcessingOptionError", locale));
        }
        if(errorExists.equals("yes")) {
            error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductLoaderError", locale));
        }
        if(error_list.size() > 0) {
            return ServiceUtil.returnError(error_list);
        }
        if (UtilValidate.isNotEmpty(xlsFileName)) {
        
            String tempDir = (String)context.get("xlsFilePath");
            String filenameToUse = xlsFileName;
            File file = new File(tempDir + filenameToUse);
            String xlsDataFile = tempDir + filenameToUse;
            String xmlDataDir = tempDir;
            Boolean removeAll = false;
            if(processingOpt.equals("reload")){
                removeAll = true;
            }
            if(processingOpt.equals("update")){
                removeAll = false;
            }
            Boolean autoLoad = true;
            Map<String, Object> importClientProductTemplateCtx = null;
            
            try {
            	Map result  = FastMap.newInstance();
            	if(xlsDataFile.endsWith(".xls")) {
            		importClientProductTemplateCtx = UtilMisc.toMap("xlsDataFile", xlsDataFile, "xmlDataDir", xmlDataDir,"productLoadImagesDir", productLoadImagesDir, "imageUrl", imageUrl, "removeAll",removeAll,"autoLoad",autoLoad,"userLogin",userLogin);
                    result = dispatcher.runSync("importClientProductTemplate", importClientProductTemplateCtx);
            	}
            	if(xlsDataFile.endsWith(".xml")) {
            		importClientProductTemplateCtx = UtilMisc.toMap("xmlDataFile", xlsDataFile, "xmlDataDir", xmlDataDir,"productLoadImagesDir", productLoadImagesDir, "imageUrl", imageUrl, "removeAll",removeAll,"autoLoad",autoLoad,"userLogin",userLogin);
                    result = dispatcher.runSync("importClientProductXMLTemplate", importClientProductTemplateCtx);
            	}
                List<String> serviceMsg = (List)result.get("messages");
                if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) {
                	try {
                	    FileUtils.deleteDirectory(new File(tempDir));
                	} catch (IOException e) {
                		Debug.logWarning(e, module);
					}
                }
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
            }
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> uploadFile(DispatchContext dctx, Map<String, ? extends Object> context)throws IOException, JDOMException 
    {
        
        Locale locale = (Locale) context.get("locale");
        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
        String xlsFileName = (String)context.get("_uploadedFile_fileName");
        List<String> error_list = new ArrayList<String>();
        Map result = ServiceUtil.returnSuccess();
        if (UtilValidate.isNotEmpty(xlsFileName))
        {
            String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                
                if (!new File(uploadTempDir).exists()) 
                {
                	new File(uploadTempDir).mkdirs();
        	    }
                
                String filenameToUse = xlsFileName;
                
                File file = new File(uploadTempDir + filenameToUse);
                
                if(file.exists()) {
                	file.delete();
                }
                
                try {
                    RandomAccessFile out = new RandomAccessFile(file, "rw");
                    out.write(uploadBytes.array());
                    out.close();
                    result.put("uploadFileName",xlsFileName);
                    result.put("uploadFilePath",uploadTempDir);
                } catch (FileNotFoundException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to open file for writing: " + file.getAbsolutePath());
                } catch (IOException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to write binary data to: " + file.getAbsolutePath());
                }
        } 
        else 
        {
            error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankUploadFileError", locale));
            return ServiceUtil.returnError(error_list);
        }
        
        
        return result;
    }
    
}
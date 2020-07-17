package com.osafe.services;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import javolution.util.FastList;
import javolution.util.FastMap;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.calendar.RecurrenceInfo;
import org.ofbiz.service.job.JobManager;
import org.ofbiz.service.job.JobPoller;

import com.osafe.feeds.FeedsUtil;
import com.osafe.feeds.osafefeeds.*;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.MessageString;

public class OsafeAdminFeedServices 
{
    public static final String module = OsafeAdminFeedServices.class.getName();
    private static final String resource = "OSafeAdminUiLabels";
    
    public static Map<String, Object> clientProductRatingUpdate(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsInRatingDir = (String)context.get("feedsInRatingDir");
        String feedsInSuccessSubDir = (String)context.get("feedsInSuccessSubDir");
        String feedsInErrorSubDir = (String)context.get("feedsInErrorSubDir");

        // Check passed params
        if (UtilValidate.isEmpty(feedsInRatingDir)) 
        {
        	feedsInRatingDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_RATING_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsInSuccessSubDir)) 
        {
        	feedsInSuccessSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_SUCCESS_SUB_DIR");
        }
        if (UtilValidate.isEmpty(feedsInErrorSubDir)) 
        {
        	feedsInErrorSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_ERROR_SUB_DIR");
        }
        
        String currentDateTimeString = UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        
        String processedDir = "_"+currentDateTimeString;
        if(UtilValidate.isNotEmpty(feedsInSuccessSubDir)) 
        {
        	processedDir = feedsInSuccessSubDir + processedDir; 
        }
        String errorDir = "_"+currentDateTimeString;
        if(UtilValidate.isNotEmpty(feedsInErrorSubDir)) 
        {
        	errorDir = feedsInErrorSubDir + errorDir; 
        }
        
    	String jobName = getJobName(dispatcher, "clientProductRatingUpdate");
    	String serviceName = "clientProductRatingUpdate";
        
        if (UtilValidate.isNotEmpty(feedsInRatingDir)) 
        {
            long pauseLong = 0;
            File baseDir = new File(feedsInRatingDir);

            if (baseDir.isDirectory() && baseDir.canRead()) 
            {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) 
                {
                    if (file.getName().toUpperCase().endsWith("XML")) 
                    {
                        files.add(file);
                    }
                }
                int passes=0;
                int lastUnprocessedFilesCount = 0;
                
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 && files.size() != lastUnprocessedFilesCount) 
                {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) 
                    {
                    	
                    	File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        String rowString = new String();
                        try
                        {
                        	if(files.size()>0)
                        	{
                        		String fileNameWithoutExt = FilenameUtils.removeExtension(f.getName());
                        		fOutFile = new File(feedsInRatingDir, fileNameWithoutExt+".log");
                                if (fOutFile.createNewFile()) 
                                {
                                	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                                }
                        	}
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                    	
                    	String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                    	
                    	List<String> errorMessageList = FastList.newInstance();
                    	List<String> processedProductIdList = FastList.newInstance();
                    	
                    	String productRatingCount = "";
                    	
                    	try 
                    	{
                    	    FileUtils.copyFileToDirectory(f, new File(uploadTempDir));
                    	} 
                    	catch (IOException e) 
                    	{
                    		Debug.log("Can not copy file " + f.getName() + " to Directory " +uploadTempDir);
						}
                    	
                        Map<String, Object> importClientProductRatingXMLTemplateCtx = UtilMisc.toMap("xmlDataDir", uploadTempDir,
                                "autoLoad", Boolean.TRUE,
                                "userLogin", userLogin);
                        try 
                        {
                        	
                        	StringBuilder jobInfoStr = new StringBuilder();
            				jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
            				jobInfoStr.append("   Job Name: "+jobName);
            				jobInfoStr.append("   ServiceName: "+serviceName);
            				jobInfoStr.append("   SCHEDULED");
            				jobInfoStr.append("   STARTED");
            				
            				writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
            				
	                        String xmlDataFile = uploadTempDir + f.getName();
                    		List productRatingDataList = FastList.newInstance();
                    		List<String> dataErrorMessageList = FastList.newInstance();
                    		
                    		List<String> serviceLogValidateMessageList = FastList.newInstance();
                    		List<String> serviceLogWarningMessageList = FastList.newInstance();
                    		
                    		List<String> serviceErrorMessageList = FastList.newInstance();
                    		
                    		if (UtilValidate.isNotEmpty(uploadTempDir) && UtilValidate.isNotEmpty(f.getName())) 
                    		{
                    			Map<String, Object> productRatingDataListSvcCtx = FastMap.newInstance();
                    			productRatingDataListSvcCtx.put("productRatingFilePath", uploadTempDir);
                    			productRatingDataListSvcCtx.put("productRatingFileName", f.getName());
                    			
                    			Map productRatingDataListSvcRes = dispatcher.runSync("getProductRatingDataListFromFile", productRatingDataListSvcCtx);
                    			productRatingDataList = UtilGenerics.checkList(productRatingDataListSvcRes.get("productRatingDataList"), Map.class);
                    			dataErrorMessageList = UtilGenerics.checkList(productRatingDataListSvcRes.get("errorMessageList"), String.class);
                    			productRatingCount = (String)productRatingDataListSvcRes.get("productRatingCount");
                    		}
                    		
                    		if(dataErrorMessageList.size() > 0)
                    		{
                    		}
                    		else
                    		{
                    			Map<String, Object> svcCtx = FastMap.newInstance();
    	                        svcCtx.put("productRatingDataList", productRatingDataList);

    	                        Map svcRes = dispatcher.runSync("validateProductRatingData", svcCtx);

    	                        serviceLogValidateMessageList = UtilGenerics.checkList(svcRes.get("serviceLogValidateMessageList"), String.class);
    	                        serviceLogWarningMessageList = UtilGenerics.checkList(svcRes.get("serviceLogWarningMessageList"), String.class);
    	                        serviceErrorMessageList = UtilGenerics.checkList(svcRes.get("errorMessageList"), String.class);
    	                        processedProductIdList = UtilGenerics.checkList(svcRes.get("processedProductIdList"), String.class);
    	                        
                    		}
                    		if(UtilValidate.isNotEmpty(productRatingCount))
                    		{
                    			if((Integer.parseInt(productRatingCount)) !=  productRatingDataList.size())
                        		{
                    				serviceErrorMessageList.add(UtilProperties.getMessage(resource, "FeedCountMismatchError", UtilMisc.toMap("count", productRatingCount, "feedName", "ProductRating", "noOfEntries", Integer.toString(productRatingDataList.size())), locale));
                        		}
                    		}
                            
                    		serviceErrorMessageList.addAll(dataErrorMessageList);
	                        errorMessageList.addAll(serviceErrorMessageList);
                    		
	                        if(errorMessageList.size() > 0)
	                        {
	                        }
	                        else
	                        {
	                            importClientProductRatingXMLTemplateCtx.put("xmlDataFile", xmlDataFile);
	                            importClientProductRatingXMLTemplateCtx.put("processedProductIdList", processedProductIdList);
	                            Map result  = dispatcher.runSync("importClientProductRatingXMLTemplate", importClientProductRatingXMLTemplateCtx);
	                            List<String> serviceMsg = (List)result.get("messages");
	                            if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) 
	                            {
	                            } 
	                            else 
	                            {
	                            	errorMessageList.add(result.get("errorMessage").toString());
	                            }
	                        }
	                        
	                        
	                        if(errorMessageList.size() > 0)
                            {
                            	for(String errorMessage : errorMessageList)
                    			{
                    				rowString = "*** ERROR *** " + errorMessage;
                    				writeFeedLogMessage(bwOutFile, rowString);
                    			}
                            	try 
                            	{
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInRatingDir , errorDir));
                        	        f.delete();
                        	    }
                            	catch (IOException e) 
                            	{
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +errorDir);
    						    }
                            }
                            else
                            {
                            	if(serviceLogValidateMessageList.size() > 0)
                            	{
                            		for(String serviceLogValidateMessage : serviceLogValidateMessageList)
                            		{
                        				writeFeedLogMessage(bwOutFile, serviceLogValidateMessage);
                            		}
                            	}
                            	if(serviceLogWarningMessageList.size() > 0)
                            	{
                            		for(String serviceLogWarningMessage : serviceLogWarningMessageList)
                            		{
                        				writeFeedLogMessage(bwOutFile, serviceLogWarningMessage);
                            		}
                            	}
                            	try 
                                {
                            		if(processedProductIdList.size() > 0)
                            		{
                            			FileUtils.copyFileToDirectory(f, new File(feedsInRatingDir , processedDir));
                            		}
                            		else
                            		{
                            			FileUtils.copyFileToDirectory(f, new File(feedsInRatingDir , errorDir));
                            		}
                        	        f.delete();
                        	    } 
                                catch (IOException e) 
                                {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +processedDir);
    						    }
                                
                                writeInFeedSummary(bwOutFile, productRatingCount, Integer.toString(productRatingDataList.size()), Integer.toString(processedProductIdList.size()), "PRODUCTRATING");
                                
                            }
                        } 
                        catch (Exception e) 
                        {
                            unprocessedFiles.add(f);
                            Debug.log("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) 
                        {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try 
                            {
                                Thread.sleep((pauseLong * 1000));
                            } 
                            catch (InterruptedException ie) 
                            {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                        
                        
                        StringBuilder jobInfoStr = new StringBuilder();
        				jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobInfoStr.append("   ServiceName: "+serviceName);
        				jobInfoStr.append("   SCHEDULED");
        				jobInfoStr.append("   FINISHED - Job Status is ");
        				if(processedProductIdList.size() > 0)
                		{
        					jobInfoStr.append("SUCCESS");
                		}
        				else
        				{
        					jobInfoStr.append("FAILURE");
        				}
        				writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
        				
                        try
                        {
                            bwOutFile.flush();
                            if(errorMessageList.size() > 0 || processedProductIdList.size() == 0)
                            {
                   	    	    //place the log file in error directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInRatingDir , errorDir));
                            } 
                   	        else
                   	        {
                      	        //place the log file in success directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInRatingDir , processedDir));
                   	        }
                        }
                        catch (Exception e) 
                        {
              	        }
                        finally 
                        {
                            try {
                                if (bwOutFile != null) 
                                {
                                	bwOutFile.close();
                           	        fOutFile.delete();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                }
                
                lastUnprocessedFilesCount=unprocessedFiles.size();
                
            } 
            else 
            {
            	Debug.log("path not found or can't be read");
            }
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> clientProductUpdate(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsInProductDir = (String)context.get("feedsInProductDir");
        String feedsInSuccessSubDir = (String)context.get("feedsInSuccessSubDir");
        String feedsInErrorSubDir = (String)context.get("feedsInErrorSubDir");
        
        String currentDateTimeString = UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        // Check passed params
        if (UtilValidate.isEmpty(feedsInProductDir)) 
        {
        	feedsInProductDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_PRODUCT_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsInSuccessSubDir)) 
        {
        	feedsInSuccessSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_SUCCESS_SUB_DIR");
        }
        if (UtilValidate.isEmpty(feedsInErrorSubDir)) 
        {
        	feedsInErrorSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_ERROR_SUB_DIR");
        }

        String processedDir = "_"+currentDateTimeString;
        if(UtilValidate.isNotEmpty(feedsInSuccessSubDir)) 
        {
        	processedDir = feedsInSuccessSubDir + processedDir; 
        }
        String errorDir = "_"+currentDateTimeString;
        if(UtilValidate.isNotEmpty(feedsInErrorSubDir)) 
        {
        	errorDir = feedsInErrorSubDir + errorDir; 
        }
        
    	String jobName = getJobName(dispatcher, "clientProductUpdate");
    	String serviceName = "clientProductUpdate";
        
        if (UtilValidate.isNotEmpty(feedsInProductDir)) 
        {
            long pauseLong = 0;
            File baseDir = new File(feedsInProductDir);

            if (baseDir.isDirectory() && baseDir.canRead()) 
            {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) 
                {
                    if (file.getName().toUpperCase().endsWith("XML")) 
                    {
                        files.add(file);
                    }
                }
                int passes=0;
                int lastUnprocessedFilesCount = 0;
                
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 && files.size() != lastUnprocessedFilesCount) 
                {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) 
                    {
                    	
                    	File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        String rowString = new String();
                        try
                        {
                        	if(files.size()>0)
                        	{
                        		String fileNameWithoutExt = FilenameUtils.removeExtension(f.getName());
                        		fOutFile = new File(feedsInProductDir, fileNameWithoutExt+".log");
                                if (fOutFile.createNewFile()) 
                                {
                                	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                                }
                        	}
                        }
                        catch(Exception e)
                        {
                        	
                        }
                    	
                    	String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                    	try 
                    	{
                    	    FileUtils.copyFileToDirectory(f, new File(uploadTempDir));
                    	} 
                    	catch (IOException e) 
                    	{
                    		Debug.log("Can not copy file " + f.getName() + " to Directory " +uploadTempDir);
						}
                    	
                    	List<String> prodCatErrorList = FastList.newInstance();
                        List<String> prodCatWarningList = FastList.newInstance();
                        List<String> productErrorList = FastList.newInstance();
                        List<String> productWarningList = FastList.newInstance();
                        List<String> productAssocErrorList = FastList.newInstance();
                        List<String> productAssocWarningList = FastList.newInstance();
                        List<String> productFacetGroupErrorList = FastList.newInstance();
                        List<String> productFacetGroupWarningList = FastList.newInstance();
                        List<String> productFacetValueErrorList = FastList.newInstance();
                        List<String> productFacetValueWarningList = FastList.newInstance();
                        List<String> productManufacturerErrorList = FastList.newInstance();
                        List<String> productManufacturerWarningList = FastList.newInstance();
                        List<String> errorMessageList = FastList.newInstance();
                        
                        List<String> serviceLogProdCatMessageList = FastList.newInstance();
                        List<String> serviceLogProductMessageList = FastList.newInstance();
                        List<String> serviceLogProductAssocMessageList = FastList.newInstance();
                        List<String> serviceLogProductFacetGroupMessageList = FastList.newInstance();
                        List<String> serviceLogProductFacetValueMessageList = FastList.newInstance();
                        List<String> serviceLogProductManufacturerMessageList = FastList.newInstance();
                    	
                        Map<String, Object> importClientProductTemplateCtx = UtilMisc.toMap("xmlDataDir", uploadTempDir,"removeAll",Boolean.FALSE,"autoLoad",Boolean.TRUE,"userLogin",userLogin,"productStoreId",productStoreId);
                        try 
                        {
                        	StringBuilder jobInfoStr = new StringBuilder();
            				jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
            				jobInfoStr.append("   Job Name: "+jobName);
            				jobInfoStr.append("   ServiceName: "+serviceName);
            				jobInfoStr.append("   SCHEDULED");
            				jobInfoStr.append("   STARTED");
            				
            				writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        	
                        	String xmlDataFile = uploadTempDir + f.getName();
                        	importClientProductTemplateCtx.put("xmlDataFile", xmlDataFile);
                        	
                        	List productCatDataList = FastList.newInstance();
                        	List productDataList = FastList.newInstance();
                        	List productAssocDataList = FastList.newInstance();
                        	List productFacetGroupDataList = FastList.newInstance();
                        	List productFacetValueDataList = FastList.newInstance();
                        	List manufacturerDataList = FastList.newInstance();
                        		
                        	if (UtilValidate.isNotEmpty(uploadTempDir) && UtilValidate.isNotEmpty(f.getName())) 
                        	{
                        		Map<String, Object> productDataListSvcCtx = FastMap.newInstance();
                        		productDataListSvcCtx.put("productFilePath", uploadTempDir);
                        		productDataListSvcCtx.put("productFileName", f.getName());
                        			
                        		Map productDataListSvcRes = dispatcher.runSync("getProductDataListFromFile", productDataListSvcCtx);
                        			
                        		productCatDataList = UtilGenerics.checkList(productDataListSvcRes.get("productCatDataList"), Map.class);
                        		productDataList = UtilGenerics.checkList(productDataListSvcRes.get("productDataList"), Map.class);
                        		productAssocDataList = UtilGenerics.checkList(productDataListSvcRes.get("productAssocDataList"), Map.class);
                        		productFacetGroupDataList = UtilGenerics.checkList(productDataListSvcRes.get("productFacetGroupDataList"), Map.class);
                        		productFacetValueDataList = UtilGenerics.checkList(productDataListSvcRes.get("productFacetValueDataList"), Map.class);
                        		manufacturerDataList = UtilGenerics.checkList(productDataListSvcRes.get("manufacturerDataList"), Map.class);
                        		errorMessageList = UtilGenerics.checkList(productDataListSvcRes.get("errorMessageList"), String.class);
                        	}
                        	if(errorMessageList.size() > 0)
                        	{
                        		// Log the Errors and Warnings into Log File
                        	}
                        	else
                        	{
	                            Map<String, Object> svcCtx = FastMap.newInstance();
		                        svcCtx.put("productCatDataList", productCatDataList);
		                        svcCtx.put("productDataList", productDataList);
		                        svcCtx.put("productAssocDataList", productAssocDataList);
		                        svcCtx.put("productFacetGroupDataList", productFacetGroupDataList);
		                        svcCtx.put("productFacetValueDataList", productFacetValueDataList);
		                        svcCtx.put("manufacturerDataList", manufacturerDataList);

		                        Map svcRes = dispatcher.runSync("validateProductData", svcCtx);
		                        prodCatErrorList = UtilGenerics.checkList(svcRes.get("prodCatErrorList"), String.class);
		                        prodCatWarningList = UtilGenerics.checkList(svcRes.get("prodCatWarningList"), String.class);
		                        productErrorList = UtilGenerics.checkList(svcRes.get("productErrorList"), String.class);
		                        productWarningList = UtilGenerics.checkList(svcRes.get("productWarningList"), String.class);
		                        productAssocErrorList = UtilGenerics.checkList(svcRes.get("productAssocErrorList"), String.class);
		                        productAssocWarningList = UtilGenerics.checkList(svcRes.get("productAssocWarningList"), String.class);
		                        
		                        productFacetGroupErrorList = UtilGenerics.checkList(svcRes.get("productFacetGroupErrorList"), String.class);
		                        productFacetValueErrorList = UtilGenerics.checkList(svcRes.get("productFacetValueErrorList"), String.class);
		                        productFacetGroupWarningList = UtilGenerics.checkList(svcRes.get("productFacetGroupWarningList"), String.class);
		                        productFacetValueWarningList = UtilGenerics.checkList(svcRes.get("productFacetValueWarningList"), String.class);
		                        productManufacturerErrorList = UtilGenerics.checkList(svcRes.get("productManufacturerErrorList"), String.class);
		                        productManufacturerWarningList = UtilGenerics.checkList(svcRes.get("productManufacturerWarningList"), String.class);
		                        errorMessageList = UtilGenerics.checkList(svcRes.get("errorMessageList"), String.class);
		                        
		                        serviceLogProdCatMessageList = UtilGenerics.checkList(svcRes.get("serviceLogProdCatMessageList"), String.class);
		                        serviceLogProductMessageList = UtilGenerics.checkList(svcRes.get("serviceLogProductMessageList"), String.class);
		                        serviceLogProductAssocMessageList = UtilGenerics.checkList(svcRes.get("serviceLogProductAssocMessageList"), String.class);
		                        serviceLogProductFacetGroupMessageList = UtilGenerics.checkList(svcRes.get("serviceLogProductFacetGroupMessageList"), String.class);
		                        serviceLogProductFacetValueMessageList = UtilGenerics.checkList(svcRes.get("serviceLogProductFacetValueMessageList"), String.class);
		                        serviceLogProductManufacturerMessageList = UtilGenerics.checkList(svcRes.get("serviceLogProductManufacturerMessageList"), String.class);
                        	}
                        	if(errorMessageList.size() > 0 || prodCatErrorList.size() > 0 || productErrorList.size()>0 || productAssocErrorList.size()>0 || productFacetGroupErrorList.size()>0 || productFacetValueErrorList.size()>0 || productManufacturerErrorList.size()>0)
                        	{
                        		// Log the Errors and Warnings into Log File
                        	}
                        	else
                        	{
                        		Map result  = dispatcher.runSync("importClientProductXMLTemplate", importClientProductTemplateCtx);
                                List<String> serviceMsg = (List)result.get("messages");
                                if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) 
                                {
                                } 
                                else 
                                {
                                	errorMessageList.add(result.get("errorMessage").toString());
                                }
                        	}
                        	for(String serviceLogMessage : serviceLogProdCatMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, serviceLogMessage);
                			}
                        	for(String serviceLogMessage : serviceLogProductMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, serviceLogMessage);
                			}
                        	for(String serviceLogMessage : serviceLogProductAssocMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, serviceLogMessage);
                			}
                        	for(String serviceLogMessage : serviceLogProductFacetGroupMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, serviceLogMessage);
                			}
                        	for(String serviceLogMessage : serviceLogProductFacetValueMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, serviceLogMessage);
                			}
                        	for(String serviceLogMessage : serviceLogProductManufacturerMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, serviceLogMessage);
                			}
                        	if(errorMessageList.size() > 0 || prodCatErrorList.size() > 0 || productErrorList.size()>0 || productAssocErrorList.size()>0 || productFacetGroupErrorList.size()>0 || productFacetValueErrorList.size()>0 || productManufacturerErrorList.size()>0)
                            {
                            	for(String errorMessage : errorMessageList)
                    			{
                    				rowString = "*** ERROR *** " + errorMessage;
                    				writeFeedLogMessage(bwOutFile, rowString);
                    			}
                            	try 
                            	{
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInProductDir , errorDir));
                        	        f.delete();
                        	    } 
                            	catch (IOException e) 
                            	{
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +errorDir);
    						    }
                            }
                            else
                            {
                            	try 
                                {
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInProductDir , processedDir));
                        	        f.delete();
                        	    } 
                                catch (IOException e) 
                                {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +processedDir);
    						    }
                            }
                            
	                        StringBuilder jobEndInfoStr = new StringBuilder();
                            jobEndInfoStr.append(UtilDateTime.nowTimestamp().toString());
            				if(UtilValidate.isNotEmpty(jobName))
            				{
            					jobEndInfoStr.append("   Job Name: "+jobName);
            				}
            				else
            				{
            					jobEndInfoStr.append("   Job Name: "+serviceName);
            				}
            				jobEndInfoStr.append("   ServiceName: "+serviceName);
            				jobEndInfoStr.append("   SCHEDULED");
            				jobEndInfoStr.append("   FINISHED - Job Status is ");
            				if(errorMessageList.size() > 0 || prodCatErrorList.size() > 0 || productErrorList.size()>0 || productAssocErrorList.size()>0 || productFacetGroupErrorList.size()>0 || productFacetValueErrorList.size() > 0 || productManufacturerErrorList.size()>0)
                    		{
            					jobEndInfoStr.append("FAILURE");
                    		}
            				else
            				{
            					jobEndInfoStr.append("SUCCESS");
            				}
            				writeFeedLogMessage(bwOutFile, jobEndInfoStr.toString());
                        } 
                        catch (Exception e) 
                        {
                            unprocessedFiles.add(f);
                            Debug.log("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) 
                        {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try 
                            {
                                Thread.sleep((pauseLong * 1000));
                            } 
                            catch (InterruptedException ie) 
                            {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                        
                        try
                        {
                            bwOutFile.flush();
                            
                            if(errorMessageList.size() > 0 || prodCatErrorList.size() > 0 || productErrorList.size()>0 || productAssocErrorList.size()>0 || productFacetGroupErrorList.size()>0 || productFacetValueErrorList.size() > 0 || productManufacturerErrorList.size()>0)
                            {
                   	    	    //place the log file in error directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInProductDir , errorDir));
                            } 
                   	        else
                   	        {
                      	        //place the log file in success directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInProductDir , processedDir));
                   	        }
                        }
                        catch (Exception e) 
                        {
              	        }
                        finally 
                        {
                            try 
                            {
                            	if (bwOutFile != null) 
                                {
                                	bwOutFile.close();
                           	        fOutFile.delete();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
                        
                    }
                    files = unprocessedFiles;
                    passes++;
                }
                
                lastUnprocessedFilesCount=unprocessedFiles.size();
                
            } 
            else 
            {
            	Debug.log("path not found or can't be read");
            }
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> clientStoreUpdate(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsInStoreDir = (String)context.get("feedsInStoreDir");
        String feedsInSuccessSubDir = (String)context.get("feedsInSuccessSubDir");
        String feedsInErrorSubDir = (String)context.get("feedsInErrorSubDir");

        // Check passed params
        if (UtilValidate.isEmpty(feedsInStoreDir)) 
        {
        	feedsInStoreDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_STORE_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsInSuccessSubDir)) 
        {
        	feedsInSuccessSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_SUCCESS_SUB_DIR");
        }
        if (UtilValidate.isEmpty(feedsInErrorSubDir)) 
        {
        	feedsInErrorSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_ERROR_SUB_DIR");
        }

        String processedDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInSuccessSubDir)) {
        	processedDir = feedsInSuccessSubDir + processedDir; 
        }
        String errorDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInErrorSubDir)) 
        {
        	errorDir = feedsInErrorSubDir + errorDir; 
        }
        
    	String jobName = getJobName(dispatcher, "clientStoreUpdate");
    	String serviceName = "clientStoreUpdate";
        
        if (UtilValidate.isNotEmpty(feedsInStoreDir)) 
        {
            long pauseLong = 0;
            File baseDir = new File(feedsInStoreDir);

            if (baseDir.isDirectory() && baseDir.canRead()) 
            {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) 
                {
                    if (file.getName().toUpperCase().endsWith("XML")) 
                    {
                        files.add(file);
                    }
                }
                int passes=0;
                int lastUnprocessedFilesCount = 0;
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 && files.size() != lastUnprocessedFilesCount) 
                {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) 
                    {
                    	
                    	File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        String rowString = new String();
                        try
                        {
                        	if(files.size()>0)
                        	{
                        		String fileNameWithoutExt = FilenameUtils.removeExtension(f.getName());
                        		fOutFile = new File(feedsInStoreDir, fileNameWithoutExt+".log");
                                if (fOutFile.createNewFile()) 
                                {
                                	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                                }
                        	}
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                        
                    	String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                    	String storeCount = "";
                    	List storeDataList = FastList.newInstance();
                    	List processedStoreCodeList = FastList.newInstance();
                    	List<String> errorMessageList = FastList.newInstance();
                    	List<String> serviceMsg = FastList.newInstance();
                    	
                    	try 
                    	{
                    	    FileUtils.copyFileToDirectory(f, new File(uploadTempDir));
                    	} 
                    	catch (IOException e) 
                    	{
                    		Debug.log("Can not copy file " + f.getName() + " to Directory " +uploadTempDir);
						}
                    	
                        Map<String, Object> importClientStoreXMLTemplateCtx = UtilMisc.toMap("xmlDataDir", uploadTempDir,"autoLoad",Boolean.TRUE,"userLogin",userLogin);
                        try 
                        {
                        	
                        	StringBuilder jobInfoStr = new StringBuilder();
            				jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
            				jobInfoStr.append("   Job Name: "+jobName);
            				jobInfoStr.append("   ServiceName: "+serviceName);
            				jobInfoStr.append("   SCHEDULED");
            				jobInfoStr.append("   STARTED");
            				
            				writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        	
                        	String xmlDataFile = uploadTempDir + f.getName();
                        	
                        	List<String> dataErrorMessageList = FastList.newInstance();
                    		List<String> serviceLogValidateMessageList = FastList.newInstance();
                    		List<String> serviceLogWarningMessageList = FastList.newInstance();
                    		
                    		List serviceErrorMessageList = FastList.newInstance();
                    		if (UtilValidate.isNotEmpty(uploadTempDir) && UtilValidate.isNotEmpty(f.getName())) 
                    		{
                    			Map<String, Object> storeDataListSvcCtx = FastMap.newInstance();
                    			storeDataListSvcCtx.put("storeFilePath", uploadTempDir);
                    			storeDataListSvcCtx.put("storeFileName", f.getName());
                    			
                    			Map storeDataListSvcRes = dispatcher.runSync("getStoreDataListFromFile", storeDataListSvcCtx);
                    			storeDataList = UtilGenerics.checkList(storeDataListSvcRes.get("storeDataList"), Map.class);
                    			dataErrorMessageList = UtilGenerics.checkList(storeDataListSvcRes.get("errorMessageList"), String.class);
                    			storeCount = (String)storeDataListSvcRes.get("storeCount");
                    		}
                        	
                    		if(dataErrorMessageList.size() > 0)
                    		{
                    		}
                    		else
                    		{
                    			Map<String, Object> svcCtx = FastMap.newInstance();
    	                        svcCtx.put("storeDataList", storeDataList);
    	                        svcCtx.put("productStoreId", productStoreId);

    	                        Map svcRes = dispatcher.runSync("validateStoreData", svcCtx);

    	                        serviceLogValidateMessageList = UtilGenerics.checkList(svcRes.get("serviceLogValidateMessageList"), String.class);
    	                        serviceLogWarningMessageList = UtilGenerics.checkList(svcRes.get("serviceLogWarningMessageList"), String.class);
    	                        serviceErrorMessageList = UtilGenerics.checkList(svcRes.get("errorMessageList"), String.class);
    	                        processedStoreCodeList = UtilGenerics.checkList(svcRes.get("processedStoreCodeList"), String.class);
                    		}
                            
                    		if(UtilValidate.isNotEmpty(storeCount))
                    		{
                    			if((Integer.parseInt(storeCount)) !=  storeDataList.size())
                        		{
                    				serviceErrorMessageList.add(UtilProperties.getMessage(resource, "FeedCountMismatchError", UtilMisc.toMap("count", storeCount, "feedName", "Store", "noOfEntries", Integer.toString(storeDataList.size())), locale));
                        		}
                    		}
                    		
                    		serviceErrorMessageList.addAll(dataErrorMessageList);
	                        errorMessageList.addAll(serviceErrorMessageList);
                    		
	                        
	                        if(errorMessageList.size() > 0)
	                        {
	                        }
	                        else
	                        {
	                        	importClientStoreXMLTemplateCtx.put("xmlDataFile", xmlDataFile);
	                        	importClientStoreXMLTemplateCtx.put("processedStoreCodeList", processedStoreCodeList);
	                            Map result  = dispatcher.runSync("importClientStoreXMLTemplate", importClientStoreXMLTemplateCtx);
	                            serviceMsg = (List)result.get("messages");
	                            if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) 
	                            {
	                            } 
	                            else 
	                            {
	                            	if(UtilValidate.isNotEmpty(result.get("errorMessage")))
	                            	{
	                            		errorMessageList.add(result.get("errorMessage").toString());
	                            	}
	                            }
	                        }
	                        
	                        if(errorMessageList.size() > 0)
                            {
                            	for(String errorMessage : errorMessageList)
                    			{
                    				rowString = "*** ERROR *** " + errorMessage;
                    				writeFeedLogMessage(bwOutFile, rowString);
                    			}
                            	try 
                            	{
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInStoreDir , errorDir));
                        	        f.delete();
                        	    }
                            	catch (IOException e) 
                            	{
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +errorDir);
    						    }
                            }
                            else
                            {
                            	if(serviceLogValidateMessageList.size() > 0)
                            	{
                            		for(String serviceLogValidateMessage : serviceLogValidateMessageList)
                            		{
                        				writeFeedLogMessage(bwOutFile, serviceLogValidateMessage);
                            		}
                            	}
                            	if(serviceLogWarningMessageList.size() > 0)
                            	{
                            		for(String serviceLogWarningMessage : serviceLogWarningMessageList)
                            		{
                        				writeFeedLogMessage(bwOutFile, serviceLogWarningMessage);
                            		}
                            	}
                            	try 
                                {
                            		if(processedStoreCodeList.size() > 0)
                            		{
                            			FileUtils.copyFileToDirectory(f, new File(feedsInStoreDir , processedDir));
                            		}
                            		else
                            		{
                            			FileUtils.copyFileToDirectory(f, new File(feedsInStoreDir , errorDir));
                            		}
                        	        f.delete();
                        	    } 
                                catch (IOException e) 
                                {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +processedDir);
    						    }
                                
                                writeInFeedSummary(bwOutFile, storeCount, Integer.toString(storeDataList.size()), Integer.toString(processedStoreCodeList.size()), "STORE");
                                
                            }
                            
                        } 
                        catch (Exception e) 
                        {
                            unprocessedFiles.add(f);
                            Debug.log("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) 
                        {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try 
                            {
                                Thread.sleep((pauseLong * 1000));
                            } 
                            catch (InterruptedException ie) 
                            {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                        
                        StringBuilder jobInfoStr = new StringBuilder();
        				jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobInfoStr.append("   ServiceName: "+serviceName);
        				jobInfoStr.append("   SCHEDULED");
        				jobInfoStr.append("   FINISHED - Job Status is ");
        				if(processedStoreCodeList.size() > 0)
                		{
        					jobInfoStr.append("SUCCESS");
                		}
        				else
        				{
        					jobInfoStr.append("FAILURE");
        				}
        				writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
        				
                        
                        try
                        {
                            bwOutFile.flush();
                            if(errorMessageList.size() > 0 || processedStoreCodeList.size() == 0)
                            {
                   	    	    //place the log file in error directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInStoreDir , errorDir));
                            } 
                   	        else
                   	        {
                      	        //place the log file in success directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInStoreDir , processedDir));
                   	        }
                        }
                        catch (Exception e) 
                        {
                        	Debug.log("Can not flush the file "+e);
              	        }
                        finally 
                        {
                            try 
                            {
                                if (bwOutFile != null) 
                                {
                           	        bwOutFile.close();
                           	        fOutFile.delete();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                }
                lastUnprocessedFilesCount=unprocessedFiles.size();
                
            } 
            else 
            {
            	Debug.log("path not found or can't be read");
            }
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> clientOrderStatusUpdate(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsInOrderStatusDir = (String)context.get("feedsInOrderStatusDir");
        String feedsInSuccessSubDir = (String)context.get("feedsInSuccessSubDir");
        String feedsInErrorSubDir = (String)context.get("feedsInErrorSubDir");

        // Check passed params
        if (UtilValidate.isEmpty(feedsInOrderStatusDir)) 
        {
        	feedsInOrderStatusDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_ORDER_STATUS_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsInSuccessSubDir)) 
        {
        	feedsInSuccessSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_SUCCESS_SUB_DIR");
        }
        if (UtilValidate.isEmpty(feedsInErrorSubDir)) 
        {
        	feedsInErrorSubDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_IN_ERROR_SUB_DIR");
        }
        
        String currentDateTimeString = UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        
        String processedDir = "_"+currentDateTimeString;
        if(UtilValidate.isNotEmpty(feedsInSuccessSubDir)) 
        {
        	processedDir = feedsInSuccessSubDir + processedDir; 
        }
        String errorDir = "_"+currentDateTimeString;
        if(UtilValidate.isNotEmpty(feedsInErrorSubDir)) 
        {
        	errorDir = feedsInErrorSubDir + errorDir; 
        }
        
        
        String jobName = getJobName(dispatcher, "clientOrderStatusUpdate");
    	
    	String serviceName = "clientOrderStatusUpdate";
        
        if (UtilValidate.isNotEmpty(feedsInOrderStatusDir)) 
        {
            long pauseLong = 0;
            File baseDir = new File(feedsInOrderStatusDir);

            if (baseDir.isDirectory() && baseDir.canRead()) 
            {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) 
                {
                    if (file.getName().toUpperCase().endsWith("XML")) 
                    {
                        files.add(file);
                    }
                }
                int passes=0;
                int lastUnprocessedFilesCount = 0;
                
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 && files.size() != lastUnprocessedFilesCount) 
                {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) 
                    {
                    	
                    	File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        String rowString = new String();
                        try
                        {
                        	if(files.size()>0)
                        	{
                        		String fileNameWithoutExt = FilenameUtils.removeExtension(f.getName());
                        		fOutFile = new File(feedsInOrderStatusDir, fileNameWithoutExt+".log");
                                if (fOutFile.createNewFile()) 
                                {
                                	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                                }
                        	}
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                    	
                    	String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                    	String orderStatusCount = "";
                    	List orderStatusDataList = FastList.newInstance();
                    	List processedOrderIdList = FastList.newInstance();
                    	List<String> errorMessageList = FastList.newInstance();
                    	List<String> serviceMsg = FastList.newInstance();
                    	try 
                    	{
                    	    FileUtils.copyFileToDirectory(f, new File(uploadTempDir));
                    	} 
                    	catch (IOException e) 
                    	{
                    		Debug.log("Can not copy file " + f.getName() + " to Directory " +uploadTempDir);
						}
                    	
                        Map<String, Object> importClientOrderStatusXMLTemplateCtx = UtilMisc.toMap("xmlDataDir", uploadTempDir,"autoLoad",Boolean.TRUE,"userLogin",userLogin);
                        try 
                        {
                        	
                        	StringBuilder jobInfoStr = new StringBuilder();
            				jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
            				jobInfoStr.append("   Job Name: "+jobName);
            				jobInfoStr.append("   ServiceName: "+serviceName);
            				jobInfoStr.append("   SCHEDULED");
            				jobInfoStr.append("   STARTED");
            				
            				writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
            				
                        	String xmlDataFile = uploadTempDir + f.getName();
                        	
                        	
                    		List<String> dataErrorMessageList = FastList.newInstance();
                    		List<String> serviceLogValidateMessageList = FastList.newInstance();
                    		List<String> serviceLogWarningMessageList = FastList.newInstance();
                    		
                    		List serviceErrorMessageList = FastList.newInstance();
                    		if (UtilValidate.isNotEmpty(uploadTempDir) && UtilValidate.isNotEmpty(f.getName())) 
                    		{
                    			Map<String, Object> orderStatusDataListSvcCtx = FastMap.newInstance();
                    			orderStatusDataListSvcCtx.put("orderStatusFilePath", uploadTempDir);
                    			orderStatusDataListSvcCtx.put("orderStatusFileName", f.getName());
                    			
                    			Map orderStatusDataListSvcRes = dispatcher.runSync("getOrderStatusDataListFromFile", orderStatusDataListSvcCtx);
                    			orderStatusDataList = UtilGenerics.checkList(orderStatusDataListSvcRes.get("orderStatusDataList"), Map.class);
                    			dataErrorMessageList = UtilGenerics.checkList(orderStatusDataListSvcRes.get("errorMessageList"), String.class);
                    			orderStatusCount = (String)orderStatusDataListSvcRes.get("orderStatusCount");
                    		}
                    		
                    		if(dataErrorMessageList.size() > 0)
                    		{
                    		}
                    		else
                    		{
                    			Map<String, Object> svcCtx = FastMap.newInstance();
    	                        svcCtx.put("orderStatusDataList", orderStatusDataList);
    	                        svcCtx.put("productStoreId", productStoreId);

    	                        Map svcRes = dispatcher.runSync("validateOrderStatusData", svcCtx);

    	                        serviceLogValidateMessageList = UtilGenerics.checkList(svcRes.get("serviceLogValidateMessageList"), String.class);
    	                        serviceLogWarningMessageList = UtilGenerics.checkList(svcRes.get("serviceLogWarningMessageList"), String.class);
    	                        serviceErrorMessageList = UtilGenerics.checkList(svcRes.get("errorMessageList"), String.class);
    	                        processedOrderIdList = UtilGenerics.checkList(svcRes.get("processedOrderIdList"), String.class);
                    		}
                            
                    		if(UtilValidate.isNotEmpty(orderStatusCount))
                    		{
                    			if((Integer.parseInt(orderStatusCount)) !=  orderStatusDataList.size())
                        		{
                    				serviceErrorMessageList.add(UtilProperties.getMessage(resource, "FeedCountMismatchError", UtilMisc.toMap("count", orderStatusCount, "feedName", "Order", "noOfEntries", Integer.toString(orderStatusDataList.size())), locale));
                        		}
                    		}
                    		
                    		serviceErrorMessageList.addAll(dataErrorMessageList);
	                        errorMessageList.addAll(serviceErrorMessageList);
	                        
	                        if(errorMessageList.size() > 0)
	                        {
	                        }
	                        else
	                        {
	                        	importClientOrderStatusXMLTemplateCtx.put("xmlDataFile", xmlDataFile);
	                        	importClientOrderStatusXMLTemplateCtx.put("processedOrderIdList", processedOrderIdList);
	                            Map result  = dispatcher.runSync("importClientOrderStatusXMLTemplate", importClientOrderStatusXMLTemplateCtx);
	                            serviceMsg = (List)result.get("messages");
	                            if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) 
	                            {
	                            } 
	                            else 
	                            {
	                            	if(UtilValidate.isNotEmpty(result.get("errorMessage")))
	                            	{
	                            		errorMessageList.add(result.get("errorMessage").toString());
	                            	}
	                            }
	                        }
	                        if(errorMessageList.size() > 0)
                            {
                            	for(String errorMessage : errorMessageList)
                    			{
                    				rowString = "*** ERROR *** " + errorMessage;
                    				writeFeedLogMessage(bwOutFile, rowString);
                    			}
                            	try 
                            	{
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInOrderStatusDir , errorDir));
                        	        f.delete();
                        	    }
                            	catch (IOException e) 
                            	{
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +errorDir);
    						    }
                            }
                            else
                            {
                            	if(serviceLogValidateMessageList.size() > 0)
                            	{
                            		for(String serviceLogValidateMessage : serviceLogValidateMessageList)
                            		{
                        				writeFeedLogMessage(bwOutFile, serviceLogValidateMessage);
                            		}
                            	}
                            	if(serviceLogWarningMessageList.size() > 0)
                            	{
                            		for(String serviceLogWarningMessage : serviceLogWarningMessageList)
                            		{
                        				writeFeedLogMessage(bwOutFile, serviceLogWarningMessage);
                            		}
                            	}
                            	try 
                                {
                            		if(processedOrderIdList.size() > 0)
                            		{
                            			FileUtils.copyFileToDirectory(f, new File(feedsInOrderStatusDir , processedDir));
                            		}
                            		else
                            		{
                            			FileUtils.copyFileToDirectory(f, new File(feedsInOrderStatusDir , errorDir));
                            		}
                        	        f.delete();
                        	    } 
                                catch (IOException e) 
                                {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +processedDir);
    						    }
                                
                                writeInFeedSummary(bwOutFile, orderStatusCount, Integer.toString(orderStatusDataList.size()), Integer.toString(processedOrderIdList.size()), "ORDER");
                                
                            }
                        } 
                        catch (Exception e) 
                        {
                            unprocessedFiles.add(f);
                            Debug.log("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) 
                        {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try 
                            {
                                Thread.sleep((pauseLong * 1000));
                            } 
                            catch (InterruptedException ie) 
                            {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                        
                        StringBuilder jobInfoStr = new StringBuilder();
        				jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobInfoStr.append("   ServiceName: "+serviceName);
        				jobInfoStr.append("   SCHEDULED");
        				jobInfoStr.append("   FINISHED - Job Status is ");
        				if(processedOrderIdList.size() > 0)
                		{
        					jobInfoStr.append("SUCCESS");
                		}
        				else
        				{
        					jobInfoStr.append("FAILURE");
        				}
        				writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        
                        try
                        {
                            bwOutFile.flush();
                            if(errorMessageList.size() > 0 || processedOrderIdList.size() == 0)
                            {
                   	    	    //place the log file in error directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInOrderStatusDir , errorDir));
                            } 
                   	        else
                   	        {
                      	        //place the log file in success directory
                   	        	FileUtils.copyFileToDirectory(fOutFile, new File(feedsInOrderStatusDir , processedDir));
                   	        }
                        }
                        catch (Exception e) 
                        {
                        	Debug.log("Can not flush the file "+e);
              	        }
                        finally 
                        {
                            try 
                            {
                                if (bwOutFile != null) 
                                {
                           	        bwOutFile.close();
                           	        fOutFile.delete();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                }
                
                lastUnprocessedFilesCount=unprocessedFiles.size();
                
            } 
            else 
            {
            	Debug.log("path not found or can't be read");
            }
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> bigFishCustomerFeed(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsOutCustomerDir = (String)context.get("feedsOutCustomerDir");
        String feedsOutCustomerPrefix = (String)context.get("feedsOutCustomerPrefix");

        // Check passed params
        if (UtilValidate.isEmpty(feedsOutCustomerDir)) 
        {
        	feedsOutCustomerDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_CUSTOMER_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsOutCustomerPrefix)) 
        {
        	feedsOutCustomerPrefix = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_CUSTOMER_PREFIX");
        }
        
    	String jobName = getJobName(dispatcher, "bigFishCustomerFeed");
        
        String serviceName = "bigFishCustomerFeed";

    	StringBuilder jobInfoStr = new StringBuilder();
		jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
		jobInfoStr.append("   Job Name: "+jobName);
		jobInfoStr.append("   ServiceName: "+serviceName);
		jobInfoStr.append("   SCHEDULED");
		jobInfoStr.append("   STARTED");
		
        if (UtilValidate.isNotEmpty(feedsOutCustomerDir)) 
        {
        	Map<String, Object> findPartyCtx = UtilMisc.toMap("lookupFlag", "Y",
                    "showAll", "N","extInfo", "N", "statusId", "ANY",
                    "userLogin", userLogin);
        	findPartyCtx.put("roleTypeId", "CUSTOMER");
        	findPartyCtx.put("partyTypeId", "PERSON");
        	findPartyCtx.put("isDownloaded", "N");
        	
        	Map results;
        	List<GenericValue> completePartyList = FastList.newInstance();
			try 
			{
				results = dispatcher.runSync("findParty", findPartyCtx);
				completePartyList = (List<GenericValue>) results.get("completePartyList");
			} 
			catch (GenericServiceException e1) 
			{
				e1.printStackTrace();
			}
        	
        	List<String> partyList = FastList.newInstance();
        	if(UtilValidate.isNotEmpty(completePartyList)) 
        	{
        		for(GenericValue party : completePartyList) 
        		{
        			partyList.add(party.getString("partyId"));
        		}
        	}
        	if(UtilValidate.isNotEmpty(partyList)) 
        	{
        		Map<String, Object> exportCustomerXMLCtx = UtilMisc.toMap("customerList", partyList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try 
				{
					exportResults = dispatcher.runSync("exportCustomerXML", exportCustomerXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		List<String> exportMessageList = UtilGenerics.checkList(exportResults.get("exportMessageList"), String.class);
	        		List<String> feedsExportedIdList = (List)exportResults.get("feedsExportedIdList");
	        		
	        		//Set the IS_DOWNLOADED Attribute to 'Y'
        	        if(UtilValidate.isNotEmpty(feedsExportedIdList)) 
        	        {
        	        	Map<String, Object> createUpdateDownloadedArrtibuteCtx = UtilMisc.toMap("feedsExportedIdList", feedsExportedIdList,
	                            "entityName", "PartyAttribute", "entityPrimaryColumnName", "partyId",
	                            "userLogin", userLogin);
        	        	dispatcher.runSync("createUpdateDownloadedArrtibute", createUpdateDownloadedArrtibuteCtx);
        	        }
        	        
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutCustomerPrefix)) 
	                {
	                	exportedFileName = feedsOutCustomerPrefix + exportedFileName; 
	                }
	        		try 
	        		{
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutCustomerDir, exportedFileName));
	        	        
	        	        File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        try
                        {
                        	fOutFile = new File(feedsOutCustomerDir, exportedFileName.replace(".xml", ".log"));
                            if (fOutFile.createNewFile()) 
                            {
                            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                            }
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                        
                        writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        
	        	        if(UtilValidate.isNotEmpty(exportMessageList))
	        	        {
	        	        	for(String exportMessage : exportMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, exportMessage);
                			}
	        	        }
	        	        writeOutFeedSummary(bwOutFile, Integer.toString(partyList.size()), Integer.toString(feedsExportedIdList.size()), "CUSTOMER");
	        	        
	        	        StringBuilder jobEndInfoStr = new StringBuilder();
	        	        jobEndInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobEndInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobEndInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobEndInfoStr.append("   ServiceName: "+serviceName);
        				jobEndInfoStr.append("   SCHEDULED");
        				jobEndInfoStr.append("   FINISHED - Job Status is SUCCESS");
        				
        				writeFeedLogMessage(bwOutFile, jobEndInfoStr.toString());
        				
	        	        try
                        {
                            bwOutFile.flush();
                        }
                        catch (Exception e) 
                        {
                        	Debug.log("Can not flush the file "+e);
              	        }
                        finally 
                        {
                            try 
                            {
                                if (bwOutFile != null) 
                                {
                           	        bwOutFile.close();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
                        
	        	        exportedFileSrc.delete();
	        	        
	        	    } 
	        		catch (IOException e) 
	        		{
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutCustomerDir);
				    }
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
        		
        	}
            
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> bigFishOrderFeed(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        List lProductStoreId = FastList.newInstance();
        if(UtilValidate.isNotEmpty(productStoreId)) 
        {
        	lProductStoreId.add(productStoreId);
        }
        String feedsOutOrderDir = (String)context.get("feedsOutOrderDir");
        String feedsOutOrderPrefix = (String)context.get("feedsOutOrderPrefix");
        // Check passed params
        if (UtilValidate.isEmpty(feedsOutOrderDir)) 
        {
        	feedsOutOrderDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_ORDER_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsOutOrderPrefix)) 
        {
        	feedsOutOrderPrefix = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_ORDER_PREFIX");
        }
        
    	String jobName = getJobName(dispatcher, "bigFishOrderFeed");
    	
    	String serviceName = "bigFishOrderFeed";

    	StringBuilder jobInfoStr = new StringBuilder();
		jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
		jobInfoStr.append("   Job Name: "+jobName);
		jobInfoStr.append("   ServiceName: "+serviceName);
		jobInfoStr.append("   SCHEDULED");
		jobInfoStr.append("   STARTED");
		
        Integer viewIndex = 1;
        Integer viewSize = 10000;
        if (UtilValidate.isNotEmpty(feedsOutOrderDir)) 
        {
        	Map<String, Object> searchOrdersCtx = UtilMisc.toMap("showAll", "N", "userLogin", userLogin);
        	searchOrdersCtx.put("viewIndex", viewIndex);
        	searchOrdersCtx.put("viewSize", viewSize);
        	searchOrdersCtx.put("productStoreId", lProductStoreId);
        	searchOrdersCtx.put("isDownloaded", "N");
        	Map results;
        	List<GenericValue> completeOrderList = FastList.newInstance();
			try 
			{
				results = dispatcher.runSync("searchOrders", searchOrdersCtx);
				completeOrderList = (List<GenericValue>) results.get("completeOrderList");
			} 
			catch (GenericServiceException e1) 
			{
				e1.printStackTrace();
			}
        	
			String includeExportOrderStatus = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "ORDER_STATUS_INC_EXPORT");
            List<String> exportedOrderStatusList = FastList.newInstance();
            if(UtilValidate.isNotEmpty(includeExportOrderStatus)) 
            {
            	List<String> includeExportOrderStatusList = StringUtil.split(includeExportOrderStatus, ",");
            	if(UtilValidate.isNotEmpty(includeExportOrderStatusList)) 
            	{
            		for(String orderStatus : includeExportOrderStatusList) 
            		{
            			exportedOrderStatusList.add(orderStatus.trim());
            		}
            	}
            }
			
        	List<String> orderList = FastList.newInstance();
        	if(UtilValidate.isNotEmpty(completeOrderList)) 
        	{
        		for(GenericValue order : completeOrderList) 
        		{
        			if(UtilValidate.isNotEmpty(order.getString("statusId")) && UtilValidate.isNotEmpty(exportedOrderStatusList) && !exportedOrderStatusList.contains(order.getString("statusId"))) 
    	  	    	{
    	  	    		continue;
    	  	    	}
        			else
        			{
        				orderList.add(order.getString("orderId"));
        			}
        		}
        	}
        	
        	
        	if(UtilValidate.isNotEmpty(orderList)) 
        	{
        		Map<String, Object> exportOrderXMLCtx = UtilMisc.toMap("orderList", orderList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try 
				{
					exportResults = dispatcher.runSync("exportOrderXML", exportOrderXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		List<String> exportMessageList = UtilGenerics.checkList(exportResults.get("exportMessageList"), String.class); 
	        		List<String> feedsExportedIdList =  (List) exportResults.get("feedsExportedIdList");
	        		
	        		
	        		//Set the IS_DOWNLOADED Attribute to 'Y'
        	        if(UtilValidate.isNotEmpty(feedsExportedIdList)) 
        	        {
        	        	Map<String, Object> createUpdateDownloadedArrtibuteCtx = UtilMisc.toMap("feedsExportedIdList", feedsExportedIdList,
	                            "entityName", "OrderAttribute", "entityPrimaryColumnName", "orderId",
	                            "userLogin", userLogin);
        	        	dispatcher.runSync("createUpdateDownloadedArrtibute", createUpdateDownloadedArrtibuteCtx);
        	        }
	        		
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutOrderPrefix)) 
	                {
	                	exportedFileName = feedsOutOrderPrefix + exportedFileName; 
	                }
	        		try 
	        		{
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutOrderDir, exportedFileName));
	        	        
	        	        File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        try
                        {
                        	fOutFile = new File(feedsOutOrderDir, exportedFileName.replace(".xml", ".log"));
                            if (fOutFile.createNewFile()) 
                            {
                            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                            }
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                        
                        writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        
	        	        if(UtilValidate.isNotEmpty(exportMessageList))
	        	        {
	        	        	for(String exportMessage : exportMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, exportMessage);
                			}
	        	        }
	        	        writeOutFeedSummary(bwOutFile, Integer.toString(orderList.size()), Integer.toString(feedsExportedIdList.size()), "ORDER");
	        	        
	        	        StringBuilder jobEndInfoStr = new StringBuilder();
	        	        jobEndInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobEndInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobEndInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobEndInfoStr.append("   ServiceName: "+serviceName);
        				jobEndInfoStr.append("   SCHEDULED");
        				jobEndInfoStr.append("   FINISHED - Job Status is SUCCESS");
        				
        				writeFeedLogMessage(bwOutFile, jobEndInfoStr.toString());
	        	        
	        	        try
                        {
                            bwOutFile.flush();
                        }
                        catch (Exception e) 
                        {
                        	Debug.log("Can not flush the file "+e);
              	        }
                        finally 
                        {
                            try 
                            {
                                if (bwOutFile != null) 
                                {
                           	        bwOutFile.close();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
	        	        	
	        	        exportedFileSrc.delete();
        				
	        	    } catch (IOException e) {
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutOrderDir);
				    }
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
        		
        	}
            
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
	public static Map<String, Object> bigFishContactUsFeed(DispatchContext dctx, Map<String, ? extends Object> context) 
	{
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsOutContactUsDir = (String)context.get("feedsOutContactUsDir");
        String feedsOutContactUsPrefix = (String)context.get("feedsOutContactUsPrefix");

        // Check passed params
        if (UtilValidate.isEmpty(feedsOutContactUsDir)) 
        {
        	feedsOutContactUsDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_CONTACT_US_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsOutContactUsPrefix)) 
        {
        	feedsOutContactUsPrefix = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_CONTACT_US_PREFIX");
        }

    	String jobName = getJobName(dispatcher, "bigFishContactUsFeed");
        String serviceName = "bigFishContactUsFeed";

    	StringBuilder jobInfoStr = new StringBuilder();
		jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
		jobInfoStr.append("   Job Name: "+jobName);
		jobInfoStr.append("   ServiceName: "+serviceName);
		jobInfoStr.append("   SCHEDULED");
		jobInfoStr.append("   STARTED");
        
        if (UtilValidate.isNotEmpty(feedsOutContactUsDir)) 
        {
        	List<String> custRequestIdList = FastList.newInstance();
			try 
			{
				List custRequestAttrIdList = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("CustRequestAttribute", UtilMisc.toMap("attrName","IS_DOWNLOADED", "attrValue","N")), "custRequestId", true);
				if(UtilValidate.isNotEmpty(custRequestAttrIdList))
				{
					List<EntityExpr> custRequestExpr = FastList.newInstance();
					custRequestExpr.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestAttrIdList));
					custRequestExpr.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "RF_CONTACT_US"));
					custRequestExpr.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
					custRequestIdList = EntityUtil.getFieldListFromEntityList(delegator.findList("CustRequest", EntityCondition.makeCondition(custRequestExpr, EntityOperator.AND), null, null, null, false),"custRequestId",true);
				}
			} 
			catch (GenericEntityException e2) 
			{
				e2.printStackTrace();
			}
        	
        	if(UtilValidate.isNotEmpty(custRequestIdList)) 
        	{
        		Map<String, Object> exportCustRequestContactUsXMLCtx = UtilMisc.toMap("custRequestIdList", custRequestIdList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try 
				{
					exportResults = dispatcher.runSync("exportCustRequestContactUsXML", exportCustRequestContactUsXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		List<String> feedsExportedIdList =  (List) exportResults.get("feedsExportedIdList");
	        		
	        		//Set the IS_DOWNLOADED Attribute to 'Y'
        	        if(UtilValidate.isNotEmpty(feedsExportedIdList)) 
        	        {
        	        	Map<String, Object> createUpdateDownloadedArrtibuteCtx = UtilMisc.toMap("feedsExportedIdList", feedsExportedIdList,
	                            "entityName", "CustRequestAttribute", "entityPrimaryColumnName", "custRequestId",
	                            "userLogin", userLogin);
        	        	dispatcher.runSync("createUpdateDownloadedArrtibute", createUpdateDownloadedArrtibuteCtx);
        	        }
	        		
	        		List<String> exportMessageList = UtilGenerics.checkList(exportResults.get("exportMessageList"), String.class);
	        		
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutContactUsPrefix)) 
	                {
	                	exportedFileName = feedsOutContactUsPrefix + exportedFileName; 
	                }
	        		try 
	        		{
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutContactUsDir, exportedFileName));
	        	        
	        	        File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        try
                        {
                        	fOutFile = new File(feedsOutContactUsDir, exportedFileName.replace(".xml", ".log"));
                            if (fOutFile.createNewFile()) 
                            {
                            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                            }
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                        
                        
                        writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        
                        if(UtilValidate.isNotEmpty(exportMessageList))
	        	        {
	        	        	for(String exportMessage : exportMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, exportMessage);
                			}
	        	        }
	        	        writeOutFeedSummary(bwOutFile, Integer.toString(custRequestIdList.size()), Integer.toString(feedsExportedIdList.size()), "CONTACT US");
                        
	        	        StringBuilder jobEndInfoStr = new StringBuilder();
	        	        jobEndInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobEndInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobEndInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobEndInfoStr.append("   ServiceName: "+serviceName);
        				jobEndInfoStr.append("   SCHEDULED");
        				jobEndInfoStr.append("   FINISHED - Job Status is SUCCESS");
        				
        				writeFeedLogMessage(bwOutFile, jobEndInfoStr.toString());
	        	        
	        	        try
                        {
                            bwOutFile.flush();
                        }
                        catch (Exception e) 
                        {
                        	Debug.log("Can not flush the file "+e);
              	        }
                        finally 
                        {
                            try 
                            {
                                if (bwOutFile != null) 
                                {
                           	        bwOutFile.close();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
                        
	        	        exportedFileSrc.delete();
	        	        
	        	    } 
	        		catch (IOException e) 
	        		{
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutContactUsDir);
				    }
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
        	}
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> bigFishRequestCatalogFeed(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsOutRequestCatalogDir = (String)context.get("feedsOutRequestCatalogDir");
        String feedsOutRequestCatalogPrefix = (String)context.get("feedsOutRequestCatalogPrefix");

        // Check passed params
        if (UtilValidate.isEmpty(feedsOutRequestCatalogDir)) 
        {
        	feedsOutRequestCatalogDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_REQUEST_CATALOG_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsOutRequestCatalogPrefix)) 
        {
        	feedsOutRequestCatalogPrefix = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_REQUEST_CATALOG_PREFIX");
        }

        String jobName = getJobName(dispatcher, "bigFishRequestCatalogFeed");
        
        String serviceName = "bigFishRequestCatalogFeed";

    	StringBuilder jobInfoStr = new StringBuilder();
		jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
		jobInfoStr.append("   Job Name: "+jobName);
		jobInfoStr.append("   ServiceName: "+serviceName);
		jobInfoStr.append("   SCHEDULED");
		jobInfoStr.append("   STARTED");
        
        if (UtilValidate.isNotEmpty(feedsOutRequestCatalogDir)) 
        {
        	List<String> custRequestIdList = FastList.newInstance();
			try 
			{
				List custRequestAttrIdList = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("CustRequestAttribute", UtilMisc.toMap("attrName","IS_DOWNLOADED", "attrValue","N")), "custRequestId", true);
				if(UtilValidate.isNotEmpty(custRequestAttrIdList))
				{
					List<EntityExpr> custRequestExpr = FastList.newInstance();
					custRequestExpr.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestAttrIdList));
					custRequestExpr.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "RF_CATALOG"));
					custRequestExpr.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
					custRequestIdList = EntityUtil.getFieldListFromEntityList(delegator.findList("CustRequest", EntityCondition.makeCondition(custRequestExpr, EntityOperator.AND), null, null, null, false),"custRequestId",true);
				}
			} 
			catch (GenericEntityException e2) 
			{
				e2.printStackTrace();
			}
        	
        	if(UtilValidate.isNotEmpty(custRequestIdList)) 
        	{
        		Map<String, Object> exportCustRequestCatalogXMLCtx = UtilMisc.toMap("custRequestIdList", custRequestIdList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try 
				{
					exportResults = dispatcher.runSync("exportCustRequestCatalogXML", exportCustRequestCatalogXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		List<String> feedsExportedIdList =  (List) exportResults.get("feedsExportedIdList");
	        		
	        		//Set the IS_DOWNLOADED Attribute to 'Y'
        	        if(UtilValidate.isNotEmpty(feedsExportedIdList)) 
        	        {
        	        	Map<String, Object> createUpdateDownloadedArrtibuteCtx = UtilMisc.toMap("feedsExportedIdList", feedsExportedIdList,
	                            "entityName", "CustRequestAttribute", "entityPrimaryColumnName", "custRequestId",
	                            "userLogin", userLogin);
        	        	dispatcher.runSync("createUpdateDownloadedArrtibute", createUpdateDownloadedArrtibuteCtx);
        	        }
	        		
	        		List<String> exportMessageList = UtilGenerics.checkList(exportResults.get("exportMessageList"), String.class);
	        		
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutRequestCatalogPrefix)) 
	                {
	                	exportedFileName = feedsOutRequestCatalogPrefix + exportedFileName; 
	                }
	        		try 
	        		{
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutRequestCatalogDir, exportedFileName));
	        	        
	        	        File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        try
                        {
                        	fOutFile = new File(feedsOutRequestCatalogDir, exportedFileName.replace(".xml", ".log"));
                            if (fOutFile.createNewFile()) 
                            {
                            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                            }
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                        
                        writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        
	        	        if(UtilValidate.isNotEmpty(exportMessageList))
	        	        {
	        	        	for(String exportMessage : exportMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, exportMessage);
                			}
	        	        }
	        	        writeOutFeedSummary(bwOutFile, Integer.toString(custRequestIdList.size()), Integer.toString(feedsExportedIdList.size()), "REQUEST CATALOG");
	        	        
	        	        StringBuilder jobEndInfoStr = new StringBuilder();
	        	        jobEndInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobEndInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobEndInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobEndInfoStr.append("   ServiceName: "+serviceName);
        				jobEndInfoStr.append("   SCHEDULED");
        				jobEndInfoStr.append("   FINISHED - Job Status is SUCCESS");
        				
        				writeFeedLogMessage(bwOutFile, jobEndInfoStr.toString());
        				
	        	        try
                        {
                            bwOutFile.flush();
                        }
                        catch (Exception e) 
                        {
                        	Debug.log("Can not flush the file "+e);
              	        }
                        finally 
                        {
                            try 
                            {
                                if (bwOutFile != null) 
                                {
                           	        bwOutFile.close();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
	        	        
	        	        exportedFileSrc.delete();
	        	      
	        	    } 
	        		catch (IOException e) 
	        		{
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutRequestCatalogDir);
				    }
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
        		
        	}
            
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> importBluedartPrepaid(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String uploadedFileName = (String)context.get("_uploadedFile_fileName");
        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
        List<String> error_list = new ArrayList<String>();
        if(UtilValidate.isEmpty(uploadedFileName)) 
        {
        	error_list.add(UtilProperties.getMessage(resource, "BlankFeedFileError", locale));
        }
        Map result = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(uploadedFileName)) 
        {
        	if(!((uploadedFileName.toUpperCase()).endsWith("XLS") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
        	{
        		error_list.add(UtilProperties.getMessage(resource, "BlueDartFeedFileNotXlsError", locale));	
        	} 
        	else 
        	{
        		Map<String, Object> uploadFileCtx = FastMap.newInstance();
                uploadFileCtx.put("userLogin",userLogin);
                uploadFileCtx.put("uploadedFile",uploadBytes);
                uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
                
                try 
                {
        			result = dispatcher.runSync("uploadFile", uploadFileCtx);
        		} 
                catch (GenericServiceException e) 
                {
        			e.printStackTrace();
        		}
        	}
        }
        
        if(UtilValidate.isNotEmpty(result.get("uploadFilePath")) && UtilValidate.isNotEmpty(result.get("uploadFileName"))) 
        {
        	try 
        	{
        		File inputWorkbook = new File((String)result.get("uploadFilePath") + (String)result.get("uploadFileName"));
        		if (inputWorkbook != null) 
                {
        			WorkbookSettings ws = new WorkbookSettings();
                    ws.setLocale(new Locale("en", "EN"));
                    Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                    for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                    {
                        BufferedWriter bw = null; 
                        try 
                        {
                            Sheet s = wb.getSheet(sheet);
                            String sTabName=s.getName();
                            
                            if (sheet == 0)
                            {
                            	List dataRows = BlueDartImportServices.buildDataRows(BlueDartImportServices.buildBlueDartPrepaidHeader(),s);
                            	List<String> duplicates = new ArrayList();
                                HashSet uniques = new HashSet();
                            	for (int i=0 ; i < dataRows.size() ; i++) 
                                {
                            		Map<String, String> mRow = (Map<String, String>)dataRows.get(i);
                            		String pinCode = (String)mRow.get("pincode");
                                     if (uniques.contains(pinCode))
                                     {
                                    	 duplicates.add(pinCode);
                                     }
                                     else
                                     {
                                         uniques.add(pinCode);
                                     }
                                }
                            	for(String pincode : duplicates)
                            	{
                            		error_list.add(UtilProperties.getMessage(resource, "PinCodeUniqueError",  UtilMisc.toMap("pincode", pincode), locale));
                            	}
                            }
                        } 
                        catch (Exception exc) 
                        {
                            Debug.logError(exc, module);
                        } 
                    }
                }
        	}
        	catch(Exception e)
        	{
        		
        	}
        }
        
        if(error_list.size() > 0) 
        {
            return ServiceUtil.returnError(error_list);
        }
        
		
		Map<String, Object> importBlueDartPrepaidTemplateCtx = FastMap.newInstance();
		importBlueDartPrepaidTemplateCtx.put("userLogin",userLogin);
		importBlueDartPrepaidTemplateCtx.put("xlsDataFile",(String)result.get("uploadFilePath") + (String)result.get("uploadFileName"));
		importBlueDartPrepaidTemplateCtx.put("xmlDataDir",(String)result.get("uploadFilePath"));
		importBlueDartPrepaidTemplateCtx.put("autoLoad",Boolean.TRUE);
		try 
		{
			dispatcher.runSync("importBlueDartPrepaidTemplate", importBlueDartPrepaidTemplateCtx);
		} 
		catch (GenericServiceException e) 
		{
			e.printStackTrace();
		}
        return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> importBluedartCoD(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String uploadedFileName = (String)context.get("_uploadedFile_fileName");
        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
        List<String> error_list = new ArrayList<String>();
        if(UtilValidate.isEmpty(uploadedFileName)) 
        {
        	error_list.add(UtilProperties.getMessage(resource, "BlueDartFeedFileNotXlsError", locale));
        }
        Map result = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(uploadedFileName)) 
        {
        	if(!((uploadedFileName.toUpperCase()).endsWith("XLS") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
        	{
        		error_list.add(UtilProperties.getMessage(resource, "FeedFileNotXlsError", locale));	
        	} 
        	else 
        	{
        		Map<String, Object> uploadFileCtx = FastMap.newInstance();
                uploadFileCtx.put("userLogin",userLogin);
                uploadFileCtx.put("uploadedFile",uploadBytes);
                uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
                
                try 
                {
        			result = dispatcher.runSync("uploadFile", uploadFileCtx);
        		} 
                catch (GenericServiceException e) 
                {
        			e.printStackTrace();
        		}
        	}
        }
        
        if(UtilValidate.isNotEmpty(result.get("uploadFilePath")) && UtilValidate.isNotEmpty(result.get("uploadFileName"))) 
        {
        	try 
        	{
        		File inputWorkbook = new File((String)result.get("uploadFilePath") + (String)result.get("uploadFileName"));
        		if (inputWorkbook != null) 
                {
        			WorkbookSettings ws = new WorkbookSettings();
                    ws.setLocale(new Locale("en", "EN"));
                    Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                    for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                    {
                        BufferedWriter bw = null; 
                        try 
                        {
                            Sheet s = wb.getSheet(sheet);
                            String sTabName=s.getName();
                            
                            if (sheet == 0)
                            {
                            	List dataRows = BlueDartImportServices.buildDataRows(BlueDartImportServices.buildBlueDartCoDHeader(),s);
                            	List<String> duplicates = new ArrayList();
                                HashSet uniques = new HashSet();
                            	for (int i=0 ; i < dataRows.size() ; i++) 
                                {
                            		Map<String, String> mRow = (Map<String, String>)dataRows.get(i);
                            		String pinCode = (String)mRow.get("pincode");
                                     if (uniques.contains(pinCode))
                                     {
                                    	 duplicates.add(pinCode);
                                     }
                                     else
                                     {
                                         uniques.add(pinCode);
                                     }
                                }
                            	for(String pincode : duplicates)
                            	{
                            		error_list.add(UtilProperties.getMessage(resource, "PinCodeUniqueError",  UtilMisc.toMap("pincode", pincode), locale));
                            	}
                            }
                        } 
                        catch (Exception exc) 
                        {
                            Debug.logError(exc, module);
                        } 
                    }
                }
        	}
        	catch(Exception e)
        	{
        		
        	}
        }
        
        if(error_list.size() > 0) 
        {
            return ServiceUtil.returnError(error_list);
        }
        
		
		Map<String, Object> importBlueDartCoDTemplateCtx = FastMap.newInstance();
		importBlueDartCoDTemplateCtx.put("userLogin",userLogin);
		importBlueDartCoDTemplateCtx.put("xlsDataFile",(String)result.get("uploadFilePath") + (String)result.get("uploadFileName"));
		importBlueDartCoDTemplateCtx.put("xmlDataDir",(String)result.get("uploadFilePath"));
		importBlueDartCoDTemplateCtx.put("autoLoad",Boolean.TRUE);
		try 
		{
			dispatcher.runSync("importBlueDartCoDTemplate", importBlueDartCoDTemplateCtx);
		} 
		catch (GenericServiceException e) 
		{
			e.printStackTrace();
		}
        return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> convertProductRatings(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String uploadedFileName = (String)context.get("_uploadedFile_fileName");
        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
        
        String productStoreId = (String)context.get("productStoreId");
        
        List<String> error_list = new ArrayList<String>();
        
        String bigfishXmlFile = "";
        
        String importDataPath = getConvertedBigfishXMLFilePath();
        
        if (!new File(importDataPath).exists()) 
        {
            new File(importDataPath).mkdirs();
        }
        
        if(UtilValidate.isEmpty(uploadedFileName)) 
        {
        	error_list.add(UtilProperties.getMessage(resource, "BlankXLSFileError", locale));
        }
        Map result = FastMap.newInstance();
        Map resultUploadSvc = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(uploadedFileName)) 
        {
        	if(!((uploadedFileName.toUpperCase()).endsWith("XLS") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
        	{
        		error_list.add(UtilProperties.getMessage(resource, "FeedFileNotXlsError", locale));	
        	} 
        	else 
        	{
        		Map<String, Object> uploadFileCtx = FastMap.newInstance();
                uploadFileCtx.put("userLogin",userLogin);
                uploadFileCtx.put("uploadedFile",uploadBytes);
                uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
                
                try 
                {
                	resultUploadSvc = dispatcher.runSync("uploadFile", uploadFileCtx);
        		} 
                catch (GenericServiceException e) 
                {
        			e.printStackTrace();
        		}
        	}
        }
        
        if(UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFilePath")) && UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFileName"))) 
        {
        	try 
        	{
        		bigfishXmlFile = UtilDateTime.nowDateString("yyyyMMddHHmmss")+".xml";
        		File inputWorkbook = new File((String)resultUploadSvc.get("uploadFilePath") + (String)resultUploadSvc.get("uploadFileName"));
        		if (inputWorkbook != null) 
                {
        			WorkbookSettings ws = new WorkbookSettings();
                    ws.setLocale(new Locale("en", "EN"));
                    Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                    
                    ObjectFactory factory = new ObjectFactory();
                    
                    BigFishProductRatingFeedType bfProductRatingFeedType = factory.createBigFishProductRatingFeedType();
                    
                    File tempFile = new File(importDataPath, "temp" + bigfishXmlFile);
                    
                    for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                    {
                        BufferedWriter bw = null; 
                        try 
                        {
                            Sheet s = wb.getSheet(sheet);
                            String sTabName=s.getName();
                            
                            if (sheet == 0)
                            {
                            	List dataRows = ImportServices.buildDataRows(ImportServices.buildProductRatingHeader(),s);
                            	List productRatingList =  bfProductRatingFeedType.getProductRating();
                            	ImportServices.createProductRatingXmlFromXls(factory, productRatingList, dataRows, productStoreId);
                            }
                        } 
                        catch (Exception exc) 
                        {
                            Debug.logError(exc, module);
                        } 
                    }
                    
                    FeedsUtil.marshalObject(new JAXBElement<BigFishProductRatingFeedType>(new QName("", "BigFishProductRatingFeed"), BigFishProductRatingFeedType.class, null, bfProductRatingFeedType), tempFile);
              	    
              	    new File(importDataPath, bigfishXmlFile).delete();
                    File renameFile =new File(importDataPath, bigfishXmlFile);
                    RandomAccessFile out = new RandomAccessFile(renameFile, "rw");
                    InputStream inputStr = new FileInputStream(tempFile);
                    byte[] bytes = new byte[102400];
                    int bytesRead;
                    while ((bytesRead = inputStr.read(bytes)) != -1)
                    {
                        out.write(bytes, 0, bytesRead);
                    }
                    out.close();
                    inputStr.close();
                    tempFile.delete();
                }
        	}
        	catch(Exception e)
        	{
        		
        	}
        }
        
        if(error_list.size() > 0) 
        {
            return ServiceUtil.returnError(error_list);
        }
        result.put("convertedFileName", bigfishXmlFile);
        return result;
    }
    
    public static Map<String, Object> convertStores(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String uploadedFileName = (String)context.get("_uploadedFile_fileName");
        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
        
        String productStoreId = (String)context.get("productStoreId");
        
        List<String> error_list = new ArrayList<String>();
        
        String bigfishXmlFile = "";
        
        String importDataPath = getConvertedBigfishXMLFilePath();
        
        if (!new File(importDataPath).exists()) 
        {
            new File(importDataPath).mkdirs();
        }
        
        if(UtilValidate.isEmpty(uploadedFileName)) 
        {
        	error_list.add(UtilProperties.getMessage(resource, "BlankXLSFileError", locale));
        }
        Map result = FastMap.newInstance();
        Map resultUploadSvc = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(uploadedFileName)) 
        {
        	if(!((uploadedFileName.toUpperCase()).endsWith("XLS") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
        	{
        		error_list.add(UtilProperties.getMessage(resource, "FeedFileNotXlsError", locale));	
        	} 
        	else 
        	{
        		Map<String, Object> uploadFileCtx = FastMap.newInstance();
                uploadFileCtx.put("userLogin",userLogin);
                uploadFileCtx.put("uploadedFile",uploadBytes);
                uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
                
                try 
                {
                	resultUploadSvc = dispatcher.runSync("uploadFile", uploadFileCtx);
        		} 
                catch (GenericServiceException e) 
                {
        			e.printStackTrace();
        		}
        	}
        }
        
        if(UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFilePath")) && UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFileName"))) 
        {
        	try 
        	{
        		bigfishXmlFile = UtilDateTime.nowDateString("yyyyMMddHHmmss")+".xml";
        		File inputWorkbook = new File((String)resultUploadSvc.get("uploadFilePath") + (String)resultUploadSvc.get("uploadFileName"));
        		if (inputWorkbook != null) 
                {
        			WorkbookSettings ws = new WorkbookSettings();
                    ws.setLocale(new Locale("en", "EN"));
                    Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                    
                    ObjectFactory factory = new ObjectFactory();
                    
                    BigFishStoreFeedType bfStoreFeedType = factory.createBigFishStoreFeedType();
                    
                    File tempFile = new File(importDataPath, "temp" + bigfishXmlFile);
                    
                    for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                    {
                        BufferedWriter bw = null; 
                        try 
                        {
                            Sheet s = wb.getSheet(sheet);
                            String sTabName=s.getName();
                            
                            if (sheet == 0)
                            {
                            	List dataRows = ImportServices.buildDataRows(ImportServices.buildStoreHeader(),s);
                            	List storeList =  bfStoreFeedType.getStore();
                            	ImportServices.createStoreXmlFromXls(factory, storeList, dataRows, productStoreId);
                            }
                        } 
                        catch (Exception exc) 
                        {
                            Debug.logError(exc, module);
                        } 
                    }
                    
                    FeedsUtil.marshalObject(new JAXBElement<BigFishStoreFeedType>(new QName("", "BigFishStoreFeed"), BigFishStoreFeedType.class, null, bfStoreFeedType), tempFile);
              	    
              	    new File(importDataPath, bigfishXmlFile).delete();
                    File renameFile =new File(importDataPath, bigfishXmlFile);
                    RandomAccessFile out = new RandomAccessFile(renameFile, "rw");
                    InputStream inputStr = new FileInputStream(tempFile);
                    byte[] bytes = new byte[102400];
                    int bytesRead;
                    while ((bytesRead = inputStr.read(bytes)) != -1)
                    {
                        out.write(bytes, 0, bytesRead);
                    }
                    out.close();
                    inputStr.close();
                    tempFile.delete();
                }
        	}
        	catch(Exception e)
        	{
        		
        	}
        }
        
        if(error_list.size() > 0) 
        {
            return ServiceUtil.returnError(error_list);
        }
        result.put("convertedFileName", bigfishXmlFile);
        return result;
    }
    
    public static Map<String, Object> convertOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String uploadedFileName = (String)context.get("_uploadedFile_fileName");
        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
        
        String productStoreId = (String)context.get("productStoreId");
        
        List<String> error_list = new ArrayList<String>();
        
        String bigfishXmlFile = "";
        
        String importDataPath = getConvertedBigfishXMLFilePath();
        
        if (!new File(importDataPath).exists()) 
        {
            new File(importDataPath).mkdirs();
        }
        
        if(UtilValidate.isEmpty(uploadedFileName)) 
        {
        	error_list.add(UtilProperties.getMessage(resource, "BlankXLSFileError", locale));
        }
        Map result = FastMap.newInstance();
        Map resultUploadSvc = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(uploadedFileName)) 
        {
        	if(!((uploadedFileName.toUpperCase()).endsWith("XLS") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
        	{
        		error_list.add(UtilProperties.getMessage(resource, "FeedFileNotXlsError", locale));	
        	} 
        	else 
        	{
        		Map<String, Object> uploadFileCtx = FastMap.newInstance();
                uploadFileCtx.put("userLogin",userLogin);
                uploadFileCtx.put("uploadedFile",uploadBytes);
                uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
                
                try 
                {
                	resultUploadSvc = dispatcher.runSync("uploadFile", uploadFileCtx);
        		} 
                catch (GenericServiceException e) 
                {
        			e.printStackTrace();
        		}
        	}
        }
        
        if(UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFilePath")) && UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFileName"))) 
        {
        	try 
        	{
        		bigfishXmlFile = UtilDateTime.nowDateString("yyyyMMddHHmmss")+".xml";
        		File inputWorkbook = new File((String)resultUploadSvc.get("uploadFilePath") + (String)resultUploadSvc.get("uploadFileName"));
        		if (inputWorkbook != null) 
                {
        			WorkbookSettings ws = new WorkbookSettings();
                    ws.setLocale(new Locale("en", "EN"));
                    Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                    
                    ObjectFactory factory = new ObjectFactory();
                    
                    BigFishOrderStatusUpdateFeedType bfOrderStatusUpdateFeedType = factory.createBigFishOrderStatusUpdateFeedType();
                    
                    File tempFile = new File(importDataPath, "temp" + bigfishXmlFile);
                    
                    for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                    {
                        BufferedWriter bw = null; 
                        try 
                        {
                            Sheet s = wb.getSheet(sheet);
                            String sTabName=s.getName();
                            
                            if (sheet == 0)
                            {
                            	List dataRows = ImportServices.buildDataRows(ImportServices.buildOrderStatusUpdateHeader(),s);
                            	List orderStatusUpdateList =  bfOrderStatusUpdateFeedType.getOrder();
                            	ImportServices.createOrderStatusUpdateXmlFromXls(factory, orderStatusUpdateList, dataRows, productStoreId);
                            }
                        } 
                        catch (Exception exc) 
                        {
                            Debug.logError(exc, module);
                        } 
                    }
                    
                    FeedsUtil.marshalObject(new JAXBElement<BigFishOrderStatusUpdateFeedType>(new QName("", "BigFishOrderStatusUpdateFeed"), BigFishOrderStatusUpdateFeedType.class, null, bfOrderStatusUpdateFeedType), tempFile);
              	    
              	    new File(importDataPath, bigfishXmlFile).delete();
                    File renameFile =new File(importDataPath, bigfishXmlFile);
                    RandomAccessFile out = new RandomAccessFile(renameFile, "rw");
                    InputStream inputStr = new FileInputStream(tempFile);
                    byte[] bytes = new byte[102400];
                    int bytesRead;
                    while ((bytesRead = inputStr.read(bytes)) != -1)
                    {
                        out.write(bytes, 0, bytesRead);
                    }
                    out.close();
                    inputStr.close();
                    tempFile.delete();
                }
        	}
        	catch(Exception e)
        	{
        		
        	}
        }
        
        if(error_list.size() > 0) 
        {
            return ServiceUtil.returnError(error_list);
        }
        result.put("convertedFileName", bigfishXmlFile);
        return result;
    }
    
    public static Map<String, Object> convertProductLoadFile(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String uploadedFileName = (String)context.get("_uploadedFile_fileName");
        ByteBuffer uploadBytes = (ByteBuffer) context.get("uploadedFile");
        
        String productStoreId = (String)context.get("productStoreId");
        
        List<String> error_list = new ArrayList<String>();
        
        String bigfishXmlFile = "";
        
        String importDataPath = getConvertedBigfishXMLFilePath();
        
        if (!new File(importDataPath).exists()) 
        {
            new File(importDataPath).mkdirs();
        }
        
        if(UtilValidate.isEmpty(uploadedFileName)) 
        {
        	error_list.add(UtilProperties.getMessage(resource, "BlankXLSFileError", locale));
        }
        Map result = FastMap.newInstance();
        Map resultUploadSvc = FastMap.newInstance();
        if(UtilValidate.isNotEmpty(uploadedFileName)) 
        {
        	if(!((uploadedFileName.toUpperCase()).endsWith("XLS") || (uploadedFileName.toUpperCase()).endsWith("XLSX"))) 
        	{
        		error_list.add(UtilProperties.getMessage(resource, "FeedFileNotXlsError", locale));	
        	} 
        	else 
        	{
        		Map<String, Object> uploadFileCtx = FastMap.newInstance();
                uploadFileCtx.put("userLogin",userLogin);
                uploadFileCtx.put("uploadedFile",uploadBytes);
                uploadFileCtx.put("_uploadedFile_fileName",uploadedFileName);
                
                try 
                {
                	resultUploadSvc = dispatcher.runSync("uploadFile", uploadFileCtx);
        		} 
                catch (GenericServiceException e) 
                {
        			e.printStackTrace();
        		}
        	}
        }
        
        if(UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFilePath")) && UtilValidate.isNotEmpty(resultUploadSvc.get("uploadFileName"))) 
        {
        	try 
        	{
        		bigfishXmlFile = UtilDateTime.nowDateString("yyyyMMddHHmmss")+".xml";
        		File inputWorkbook = new File((String)resultUploadSvc.get("uploadFilePath") + (String)resultUploadSvc.get("uploadFileName"));
        		if (inputWorkbook != null) 
                {
        			WorkbookSettings ws = new WorkbookSettings();
                    ws.setLocale(new Locale("en", "EN"));
                    Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                    
                    ObjectFactory factory = new ObjectFactory();
                    
                    BigFishProductFeedType bfProductFeedType = factory.createBigFishProductFeedType();
                    
                    File tempFile = new File(importDataPath, "temp" + bigfishXmlFile);
                    
                    for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                    {
                        BufferedWriter bw = null; 
                        try 
                        {
                            Sheet s = wb.getSheet(sheet);
                            String sTabName=s.getName();
                            
                            if (sheet == 1)
                            {
                                ProductCategoryType productCategoryType = factory.createProductCategoryType();
                    	        List productCategoryList =  productCategoryType.getCategory();
                                List<Map<String, Object>> dataRows = ImportServices.buildProductCategoryDataRows(s);
                                ImportServices.generateProductCategoryXML(factory, productCategoryList,  dataRows);
                    	  	    bfProductFeedType.setProductCategory(productCategoryType);
                            }
                            if (sheet == 2)
                            {
                                ProductsType productsType = factory.createProductsType();
                    	  	    List productList = productsType.getProduct();
                            	List<Map<String, Object>>  dataRows = ImportServices.buildProductDataRows(s);
                    	  	    ImportServices.generateProductXML(factory, productList, dataRows);
                    	  	    bfProductFeedType.setProducts(productsType);
                            }
                            if (sheet == 3)
                            {
                                ProductAssociationType productAssociationType = factory.createProductAssociationType();
                    	  	    List productAssocList = productAssociationType.getAssociation();
                            	List<Map<String, Object>>  dataRows = ImportServices.buildProductAssocDataRows(s);
                    	  	    ImportServices.generateProductAssocXML(factory, productAssocList, dataRows);
                    	  	    bfProductFeedType.setProductAssociation(productAssociationType);
                            }
                            if (sheet == 4)
                            {
                                ProductFacetCatGroupType productFacetCatGroupType = factory.createProductFacetCatGroupType();
                    	  	    List facetGroupList = productFacetCatGroupType.getFacetCatGroup();
                            	List<Map<String, Object>>  dataRows = ImportServices.buildFacetGroupDataRows(s);
                    	  	    ImportServices.generateFacetGroupXML(factory, facetGroupList, dataRows);
                    	  	    bfProductFeedType.setProductFacetGroup(productFacetCatGroupType);
                            
                            }
                            if (sheet == 5)
                            {
                                ProductFacetValueType productFacetValueType = factory.createProductFacetValueType();
                    	  	    List facetValueList = productFacetValueType.getFacetValue();
                            	List<Map<String, Object>>  dataRows = ImportServices.buildFacetValueDataRows(s);
                    	  	    ImportServices.generateFacetValueXML(factory, facetValueList, dataRows);
                    	  	    bfProductFeedType.setProductFacetValue(productFacetValueType);
                            }
                            
                            if (sheet == 6)
                            {
                                ProductManufacturerType productManufacturerType = factory.createProductManufacturerType();
                    	  	    List manufacturerList = productManufacturerType.getManufacturer();
                            	List<Map<String, Object>>  dataRows = ImportServices.buildManufacturerDataRows(s);
                    	  	    ImportServices.generateManufacturerXML(factory, manufacturerList, dataRows);
                    	  	    bfProductFeedType.setProductManufacturer(productManufacturerType);
                            }
                        } 
                        catch (Exception exc) 
                        {
                            Debug.logError(exc, module);
                        } 
                    }
                    
                    FeedsUtil.marshalObject(new JAXBElement<BigFishProductFeedType>(new QName("", "BigFishProductFeed"), BigFishProductFeedType.class, null, bfProductFeedType), tempFile);
              	    
              	    new File(importDataPath, bigfishXmlFile).delete();
                    File renameFile =new File(importDataPath, bigfishXmlFile);
                    RandomAccessFile out = new RandomAccessFile(renameFile, "rw");
                    InputStream inputStr = new FileInputStream(tempFile);
                    byte[] bytes = new byte[102400];
                    int bytesRead;
                    while ((bytesRead = inputStr.read(bytes)) != -1)
                    {
                        out.write(bytes, 0, bytesRead);
                    }
                    out.close();
                    inputStr.close();
                    tempFile.delete();
                }
        	}
        	catch(Exception e)
        	{
        		
        	}
        }
        
        if(error_list.size() > 0) 
        {
            return ServiceUtil.returnError(error_list);
        }
        result.put("convertedFileName", bigfishXmlFile);
        return result;
    }
    
    public static String getConvertedBigfishXMLFilePath()
    {
    	return System.getProperty("ofbiz.home") + "/runtime/tmp/upload/bigfishXmlFile/";
    }
    
    private static void writeFeedLogMessage(BufferedWriter bfOutFile, String message) 
    {
    	try 
    	{
    		bfOutFile.newLine();
    		bfOutFile.write(message);
    		bfOutFile.newLine();
            bfOutFile.flush();
    	}
    	catch (Exception e)
    	{
    	}
    }
    
    private static void writeInFeedSummary(BufferedWriter bwOutFile, String headerCount, String totalRecords, String totalProcessedRecords, String feedName) 
    {
    	writeFeedLogMessage(bwOutFile, "*******************************************************");
        writeFeedLogMessage(bwOutFile, "SUMMARY OF FEED PROCESSING");
    	writeFeedLogMessage(bwOutFile, "COUNT SUPPLIED IN FILE HEADER: "+headerCount);
        writeFeedLogMessage(bwOutFile, "TOTAL <"+feedName+"> RECORDS IN FILE: "+totalRecords);
        writeFeedLogMessage(bwOutFile, "TOTAL <"+feedName+"> RECORDS SUCCESSFULLY PROCESSED: "+totalProcessedRecords);
        try
        {
        	writeFeedLogMessage(bwOutFile, "TOTAL <"+feedName+"> RECORDS IN ERROR: "+Integer.toString((Integer.parseInt(totalRecords) - Integer.parseInt(totalProcessedRecords))));
        }
        catch (NumberFormatException nfe) 
        {
        	writeFeedLogMessage(bwOutFile, "TOTAL <"+feedName+"> RECORDS IN ERROR: COULD NOT IDENTIFY THE ERROR RECORDS "+nfe.getMessage());
		}
        writeFeedLogMessage(bwOutFile, "*******************************************************");
    }
    
    private static void writeOutFeedSummary(BufferedWriter bwOutFile, String totalRecords, String totalProcessedRecords, String feedName) 
    {
    	writeFeedLogMessage(bwOutFile, "*******************************************************");
        writeFeedLogMessage(bwOutFile, "SUMMARY OF FEED PROCESSING");
    	writeFeedLogMessage(bwOutFile, "TOTAL <"+feedName+"> RECORDS EXTRACTED FROM DATABASE: "+totalRecords);
        writeFeedLogMessage(bwOutFile, "TOTAL <"+feedName+"> RECORDS IN FILE: "+totalProcessedRecords);
        writeFeedLogMessage(bwOutFile, "COUNT SUPPLIED IN FILE HEADER: "+totalProcessedRecords);
        writeFeedLogMessage(bwOutFile, "*******************************************************");
    }
    
    private static String getJobName(LocalDispatcher dispatcher,String serviceName)
    {
    	String jobName = "";
        List<Map<String, Object>> processList = dispatcher.getJobManager().processList();
    	
    	for(Map<String, Object> process : processList)
    	{
    		if(UtilValidate.isNotEmpty(process.get("serviceName")))
    		{
    			String jobInfoServiceName = (String) process.get("serviceName");
    			if(jobInfoServiceName.equals(serviceName))
        		{
    				jobName = (String) process.get("jobName");
    				break;
        		}
    		}
    	}
    	if(UtilValidate.isEmpty(jobName))
    	{
    		jobName = serviceName;
    	}
    	return jobName;
    }

    public static Map<String, Object> googleProductFeed(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        String browseRootProductCategoryId = (String)context.get("browseRootProductCategoryId");
        
        String feedsOutGoogleProductDir = (String)context.get("feedsOutGoogleProductDir");
        String feedsOutGoogleProductPrefix = (String)context.get("feedsOutGoogleProductPrefix");

        // Check passed params
        if (UtilValidate.isEmpty(feedsOutGoogleProductDir)) 
        {
        	feedsOutGoogleProductDir = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_GOOGLE_PRODUCT_URL_DIR");
        }
        if (UtilValidate.isEmpty(feedsOutGoogleProductPrefix)) 
        {
        	feedsOutGoogleProductPrefix = OsafeAdminUtil.getProductStoreParm(delegator, productStoreId, "FEEDS_OUT_GOOGLE_PRODUCT_PREFIX");
        }

        String jobName = getJobName(dispatcher, "googleProductFeed");
        
        String serviceName = "googleProductFeed";

    	StringBuilder jobInfoStr = new StringBuilder();
		jobInfoStr.append(UtilDateTime.nowTimestamp().toString());
		jobInfoStr.append("   Job Name: "+jobName);
		jobInfoStr.append("   ServiceName: "+serviceName);
		jobInfoStr.append("   SCHEDULED");
		jobInfoStr.append("   STARTED");
        
        if (UtilValidate.isNotEmpty(feedsOutGoogleProductDir)) 
        {
        		Map<String, Object> exportGoogleProductXmlCtx = UtilMisc.toMap("browseRootProductCategoryId", browseRootProductCategoryId,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try 
				{
					exportResults = dispatcher.runSync("exportGoogleProductXml", exportGoogleProductXmlCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		List<String> feedsExportedIdList =  (List) exportResults.get("feedsExportedIdList");
	        		
	        		List<String> exportMessageList = UtilGenerics.checkList(exportResults.get("exportMessageList"), String.class);
	        		
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutGoogleProductPrefix)) 
	                {
	                	exportedFileName = feedsOutGoogleProductPrefix + exportedFileName; 
	                }
	        		try 
	        		{
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutGoogleProductDir, exportedFileName));
	        	        
	        	        File fOutFile =null;
                        BufferedWriter bwOutFile = null;
                        try
                        {
                        	fOutFile = new File(feedsOutGoogleProductDir, exportedFileName.replace(".xml", ".log"));
                            if (fOutFile.createNewFile()) 
                            {
                            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                            }
                        }
                        catch(Exception e)
                        {
                        	Debug.log("can not create a file "+e);
                        }
                        
                        writeFeedLogMessage(bwOutFile, jobInfoStr.toString());
                        
	        	        if(UtilValidate.isNotEmpty(exportMessageList))
	        	        {
	        	        	for(String exportMessage : exportMessageList)
                			{
                				writeFeedLogMessage(bwOutFile, exportMessage);
                			}
	        	        }
	        	        writeOutFeedSummary(bwOutFile, Integer.toString(feedsExportedIdList.size()), Integer.toString(feedsExportedIdList.size()), "GOOGLE ITEM");
	        	        
	        	        StringBuilder jobEndInfoStr = new StringBuilder();
	        	        jobEndInfoStr.append(UtilDateTime.nowTimestamp().toString());
        				if(UtilValidate.isNotEmpty(jobName))
        				{
        					jobEndInfoStr.append("   Job Name: "+jobName);
        				}
        				else
        				{
        					jobEndInfoStr.append("   Job Name: "+serviceName);
        				}
        				jobEndInfoStr.append("   ServiceName: "+serviceName);
        				jobEndInfoStr.append("   SCHEDULED");
        				jobEndInfoStr.append("   FINISHED - Job Status is SUCCESS");
        				
        				writeFeedLogMessage(bwOutFile, jobEndInfoStr.toString());
        				
	        	        try
                        {
                            bwOutFile.flush();
                        }
                        catch (Exception e) 
                        {
                        	Debug.log("Can not flush the file "+e);
              	        }
                        finally 
                        {
                            try 
                            {
                                if (bwOutFile != null) 
                                {
                           	        bwOutFile.close();
                                }
                            }  
                            catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            }
                        }
	        	        
	        	        exportedFileSrc.delete();
	        	      
	        	    } 
	        		catch (IOException e) 
	        		{
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutGoogleProductDir);
				    }
				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();
				}
        } 
        else 
        {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
}
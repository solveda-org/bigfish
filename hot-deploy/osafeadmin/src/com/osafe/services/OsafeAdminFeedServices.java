package com.osafe.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.io.FileUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.service.ServiceUtil;

public class OsafeAdminFeedServices {
    public static final String module = OsafeAdminFeedServices.class.getName();
    
    public static Map<String, Object> clientProductRatingUpdates(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsInRatingDir = (String)context.get("feedsInRatingDir");
        String feedsInSuccessSubDir = (String)context.get("feedsInSuccessSubDir");
        String feedsInErrorSubDir = (String)context.get("feedsInErrorSubDir");
        
        String processedDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInSuccessSubDir)) {
        	processedDir = feedsInSuccessSubDir + processedDir; 
        }
        String errorDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInErrorSubDir)) {
        	errorDir = feedsInErrorSubDir + errorDir; 
        }
        
        if (UtilValidate.isNotEmpty(feedsInRatingDir)) {
            long pauseLong = 0;
            File baseDir = new File(feedsInRatingDir);

            if (baseDir.isDirectory() && baseDir.canRead()) {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) {
                    if (file.getName().toUpperCase().endsWith("XML")) {
                        files.add(file);
                    }
                }
                int passes=0;
                int lastUnprocessedFilesCount = 0;
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 &&
                        files.size() != lastUnprocessedFilesCount) {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) {
                    	String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                    	try {
                    	    FileUtils.copyFileToDirectory(f, new File(uploadTempDir));
                    	} catch (IOException e) {
                    		Debug.log("Can not copy file " + f.getName() + " to Directory " +uploadTempDir);
						}
                    	
                        Map<String, Object> importClientProductRatingXMLTemplateCtx = UtilMisc.toMap("xmlDataDir", uploadTempDir,
                                "autoLoad", Boolean.TRUE,
                                "userLogin", userLogin);

                        try {
                        	String xmlDataFile = uploadTempDir + f.getName();
                            importClientProductRatingXMLTemplateCtx.put("xmlDataFile", xmlDataFile);
                            Map result  = dispatcher.runSync("importClientProductRatingXMLTemplate", importClientProductRatingXMLTemplateCtx);
                            List<String> serviceMsg = (List)result.get("messages");
                            if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) {
                                try {
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInRatingDir , processedDir));
                        	        f.delete();
                        	    } catch (IOException e) {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +processedDir);
    						    }
                            } else {
                            	try {
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInRatingDir , errorDir));
                        	        f.delete();
                        	    } catch (IOException e) {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +errorDir);
    						    }
                            }
                            
                        } catch (Exception e) {
                            unprocessedFiles.add(f);
                            Debug.log("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try {
                                Thread.sleep((pauseLong * 1000));
                            } catch (InterruptedException ie) {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                }
                lastUnprocessedFilesCount=unprocessedFiles.size();
                
            } else {
            	Debug.log("path not found or can't be read");
            }
        } else {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> clientProductUpdates(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsInProductDir = (String)context.get("feedsInProductDir");
        String feedsInSuccessSubDir = (String)context.get("feedsInSuccessSubDir");
        String feedsInErrorSubDir = (String)context.get("feedsInErrorSubDir");
        
        String processedDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInSuccessSubDir)) {
        	processedDir = feedsInSuccessSubDir + processedDir; 
        }
        String errorDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInErrorSubDir)) {
        	errorDir = feedsInErrorSubDir + errorDir; 
        }
        
        if (UtilValidate.isNotEmpty(feedsInProductDir)) {
            long pauseLong = 0;
            File baseDir = new File(feedsInProductDir);

            if (baseDir.isDirectory() && baseDir.canRead()) {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) {
                    if (file.getName().toUpperCase().endsWith("XML")) {
                        files.add(file);
                    }
                }
                int passes=0;
                int lastUnprocessedFilesCount = 0;
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 &&
                        files.size() != lastUnprocessedFilesCount) {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) {
                    	String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                    	try {
                    	    FileUtils.copyFileToDirectory(f, new File(uploadTempDir));
                    	} catch (IOException e) {
                    		Debug.log("Can not copy file " + f.getName() + " to Directory " +uploadTempDir);
						}
                    	
                        Map<String, Object> importClientProductTemplateCtx = UtilMisc.toMap("xmlDataDir", uploadTempDir,"removeAll",Boolean.FALSE,"autoLoad",Boolean.TRUE,"userLogin",userLogin);
                        try {
                        	String xmlDataFile = uploadTempDir + f.getName();
                        	importClientProductTemplateCtx.put("xmlDataFile", xmlDataFile);
                            Map result  = dispatcher.runSync("importClientProductXMLTemplate", importClientProductTemplateCtx);
                            List<String> serviceMsg = (List)result.get("messages");
                            if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) {
                                try {
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInProductDir , processedDir));
                        	        f.delete();
                        	    } catch (IOException e) {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +processedDir);
    						    }
                            } else {
                            	try {
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInProductDir , errorDir));
                        	        f.delete();
                        	    } catch (IOException e) {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +errorDir);
    						    }
                            }
                            
                        } catch (Exception e) {
                            unprocessedFiles.add(f);
                            Debug.log("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try {
                                Thread.sleep((pauseLong * 1000));
                            } catch (InterruptedException ie) {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                }
                lastUnprocessedFilesCount=unprocessedFiles.size();
                
            } else {
            	Debug.log("path not found or can't be read");
            }
        } else {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> clientStoreUpdates(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsInStoreDir = (String)context.get("feedsInStoreDir");
        String feedsInSuccessSubDir = (String)context.get("feedsInSuccessSubDir");
        String feedsInErrorSubDir = (String)context.get("feedsInErrorSubDir");
        
        String processedDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInSuccessSubDir)) {
        	processedDir = feedsInSuccessSubDir + processedDir; 
        }
        String errorDir = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss");
        if(UtilValidate.isNotEmpty(feedsInErrorSubDir)) {
        	errorDir = feedsInErrorSubDir + errorDir; 
        }
        
        if (UtilValidate.isNotEmpty(feedsInStoreDir)) {
            long pauseLong = 0;
            File baseDir = new File(feedsInStoreDir);

            if (baseDir.isDirectory() && baseDir.canRead()) {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) {
                    if (file.getName().toUpperCase().endsWith("XML")) {
                        files.add(file);
                    }
                }
                int passes=0;
                int lastUnprocessedFilesCount = 0;
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 &&
                        files.size() != lastUnprocessedFilesCount) {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) {
                    	String uploadTempDir = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/";
                    	try {
                    	    FileUtils.copyFileToDirectory(f, new File(uploadTempDir));
                    	} catch (IOException e) {
                    		Debug.log("Can not copy file " + f.getName() + " to Directory " +uploadTempDir);
						}
                    	
                        Map<String, Object> importClientStoreXMLTemplateCtx = UtilMisc.toMap("xmlDataDir", uploadTempDir,"autoLoad",Boolean.TRUE,"userLogin",userLogin);
                        try {
                        	String xmlDataFile = uploadTempDir + f.getName();
                        	importClientStoreXMLTemplateCtx.put("xmlDataFile", xmlDataFile);
                            Map result  = dispatcher.runSync("importClientStoreXMLTemplate", importClientStoreXMLTemplateCtx);
                            List<String> serviceMsg = (List)result.get("messages");
                            if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) {
                                try {
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInStoreDir , processedDir));
                        	        f.delete();
                        	    } catch (IOException e) {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +processedDir);
    						    }
                            } else {
                            	try {
                        	        FileUtils.copyFileToDirectory(f, new File(feedsInStoreDir , errorDir));
                        	        f.delete();
                        	    } catch (IOException e) {
                        		    Debug.log("Can not copy file " + f.getName() + " to Directory " +errorDir);
    						    }
                            }
                            
                        } catch (Exception e) {
                            unprocessedFiles.add(f);
                            Debug.log("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try {
                                Thread.sleep((pauseLong * 1000));
                            } catch (InterruptedException ie) {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                }
                lastUnprocessedFilesCount=unprocessedFiles.size();
                
            } else {
            	Debug.log("path not found or can't be read");
            }
        } else {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> bigFishCustomerFeed(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsOutCustomerDir = (String)context.get("feedsOutCustomerDir");
        String feedsOutCustomerPrefix = (String)context.get("feedsOutCustomerPrefix");
        
        if (UtilValidate.isNotEmpty(feedsOutCustomerDir)) {
        	Map<String, Object> findPartyCtx = UtilMisc.toMap("lookupFlag", "Y",
                    "showAll", "N","extInfo", "N", "statusId", "ANY",
                    "userLogin", userLogin);
        	findPartyCtx.put("roleTypeId", "CUSTOMER");
        	findPartyCtx.put("partyTypeId", "PERSON");
        	findPartyCtx.put("isDownloaded", "N");
        	
        	Map results;
        	List<GenericValue> completePartyList = FastList.newInstance();
			try {
				results = dispatcher.runSync("findParty", findPartyCtx);
				completePartyList = (List<GenericValue>) results.get("completePartyList");
			} catch (GenericServiceException e1) {
				e1.printStackTrace();
			}
        	
        	List<String> partyList = FastList.newInstance();
        	if(UtilValidate.isNotEmpty(completePartyList)) {
        		for(GenericValue party : completePartyList) {
        			partyList.add(party.getString("partyId"));
        		}
        	}
        	if(UtilValidate.isNotEmpty(partyList)) {
        		Map<String, Object> exportCustomerXMLCtx = UtilMisc.toMap("customerList", partyList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try {
					exportResults = dispatcher.runSync("exportCustomerXML", exportCustomerXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutCustomerPrefix)) {
	                	exportedFileName = feedsOutCustomerPrefix + exportedFileName; 
	                }
	        		try {
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutCustomerDir, exportedFileName));
	        	        exportedFileSrc.delete();
	        	        //Set the IS_DOWNLOADED Attribute to 'Y'
	        	        for(String partyId : partyList)
	        	        {
	        	            
	        	            GenericValue partyAttrIsDownload = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId",partyId, "attrName","IS_DOWNLOADED"), true);
	        	            Map<String, Object> isDownloadedPartyAttrCtx = FastMap.newInstance();
	        	            isDownloadedPartyAttrCtx.put("partyId", partyId);
	        	            isDownloadedPartyAttrCtx.put("userLogin",userLogin);
	        	            isDownloadedPartyAttrCtx.put("attrName","IS_DOWNLOADED");
	        	            isDownloadedPartyAttrCtx.put("attrValue","Y");
	        	            Map<String, Object> isDownloadedPartyAttrMap = null;
	        	            if (UtilValidate.isNotEmpty(partyAttrIsDownload)) {
	        	                isDownloadedPartyAttrMap = dispatcher.runSync("updatePartyAttribute", isDownloadedPartyAttrCtx);
	        	            } else {
	        	                isDownloadedPartyAttrMap = dispatcher.runSync("createPartyAttribute", isDownloadedPartyAttrCtx);
	        	            }
	        	            
	        	            GenericValue partyAttrDateTimeDownload = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId",partyId, "attrName","DATETIME_DOWNLOADED"), true);
	        	            Map<String, Object> dateTimeDownloadedPartyAttrCtx = FastMap.newInstance();
	        	            dateTimeDownloadedPartyAttrCtx.put("partyId", partyId);
	        	            dateTimeDownloadedPartyAttrCtx.put("userLogin",userLogin);
	        	            dateTimeDownloadedPartyAttrCtx.put("attrName","DATETIME_DOWNLOADED");
	        	            dateTimeDownloadedPartyAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
	        	            Map<String, Object> dateTimeDownloadedPartyAttrMap = null;
	        	            if (UtilValidate.isNotEmpty(partyAttrDateTimeDownload)) {
	        	                dateTimeDownloadedPartyAttrMap = dispatcher.runSync("updatePartyAttribute", dateTimeDownloadedPartyAttrCtx);
	        	            } else {
	        	                dateTimeDownloadedPartyAttrMap = dispatcher.runSync("createPartyAttribute", dateTimeDownloadedPartyAttrCtx);
	        	            }
	        	 
	        	        }
	        	    } catch (IOException e) {
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutCustomerDir);
				    }
				} catch (Exception e1) {
					e1.printStackTrace();
				}
        		
        	}
            
        } else {
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
        if(UtilValidate.isNotEmpty(productStoreId)) {
        	lProductStoreId.add(productStoreId);
        }
        String feedsOutOrderDir = (String)context.get("feedsOutOrderDir");
        String feedsOutOrderPrefix = (String)context.get("feedsOutOrderPrefix");
        Integer viewIndex = 1;
        Integer viewSize = 10000;
        if (UtilValidate.isNotEmpty(feedsOutOrderDir)) {
        	Map<String, Object> searchOrdersCtx = UtilMisc.toMap("showAll", "N", "userLogin", userLogin);
        	searchOrdersCtx.put("viewIndex", viewIndex);
        	searchOrdersCtx.put("viewSize", viewSize);
        	searchOrdersCtx.put("productStoreId", lProductStoreId);
        	searchOrdersCtx.put("isDownloaded", "N");
        	Map results;
        	List<GenericValue> completeOrderList = FastList.newInstance();
			try {
				results = dispatcher.runSync("searchOrders", searchOrdersCtx);
				completeOrderList = (List<GenericValue>) results.get("completeOrderList");
			} catch (GenericServiceException e1) {
				e1.printStackTrace();
			}
        	
        	List<String> orderList = FastList.newInstance();
        	if(UtilValidate.isNotEmpty(completeOrderList)) {
        		for(GenericValue order : completeOrderList) {
        			orderList.add(order.getString("orderId"));
        		}
        	}
        	if(UtilValidate.isNotEmpty(orderList)) {
        		Map<String, Object> exportOrderXMLCtx = UtilMisc.toMap("orderList", orderList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try {
					exportResults = dispatcher.runSync("exportOrderXML", exportOrderXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutOrderPrefix)) {
	                	exportedFileName = feedsOutOrderPrefix + exportedFileName; 
	                }
	        		try {
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutOrderDir, exportedFileName));
	        	        exportedFileSrc.delete();
	        	        for(String orderId : orderList) {
	        	            
	        	            GenericValue orderAttrIsDownloaded = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", orderId, "attrName", "IS_DOWNLOADED"), true);
	        	        
	        	            Map<String, Object> isDownloadedOrderAttrCtx = FastMap.newInstance();
	        	            isDownloadedOrderAttrCtx.put("orderId", orderId);
	        	            isDownloadedOrderAttrCtx.put("userLogin",userLogin);
	        	            isDownloadedOrderAttrCtx.put("attrName","IS_DOWNLOADED");
	        	            isDownloadedOrderAttrCtx.put("attrValue","Y");
	        	            Map<String, Object> isDownloadOrderAttrMap = null;
	        	            if (UtilValidate.isNotEmpty(orderAttrIsDownloaded)) {
	        	                isDownloadOrderAttrMap = dispatcher.runSync("updateOrderAttribute", isDownloadedOrderAttrCtx);
	        	            } else {
	        	               isDownloadOrderAttrMap = dispatcher.runSync("createOrderAttribute", isDownloadedOrderAttrCtx);
	        	            }
	        	        
	        	            GenericValue orderAttrDateTimeDownloaded = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", orderId, "attrName", "DATETIME_DOWNLOADED"), true);
	        	            Map<String, Object> dateTimeDownloadedOrderAttrCtx = FastMap.newInstance();
	        	            dateTimeDownloadedOrderAttrCtx.put("orderId", orderId);
	        	            dateTimeDownloadedOrderAttrCtx.put("userLogin",userLogin);
	        	            dateTimeDownloadedOrderAttrCtx.put("attrName","DATETIME_DOWNLOADED");
	        	            dateTimeDownloadedOrderAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
	        	            Map<String, Object> dateTimeDownloadedOrderAttrMap = null;
	        	            if (UtilValidate.isNotEmpty(orderAttrDateTimeDownloaded)) {
	        	                dateTimeDownloadedOrderAttrMap = dispatcher.runSync("updateOrderAttribute", dateTimeDownloadedOrderAttrCtx);
	        	            } else {
	        	                dateTimeDownloadedOrderAttrMap = dispatcher.runSync("createOrderAttribute", dateTimeDownloadedOrderAttrCtx);
	        	            }
	        	        }
	        	    } catch (IOException e) {
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutOrderDir);
				    }
				} catch (Exception e1) {
					e1.printStackTrace();
				}
        		
        	}
            
        } else {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
    
	public static Map<String, Object> bigFishContactUsFeed(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productStoreId = (String)context.get("productStoreId");
        
        String feedsOutContactUsDir = (String)context.get("feedsOutContactUsDir");
        String feedsOutContactUsPrefix = (String)context.get("feedsOutContactUsPrefix");

        if (UtilValidate.isNotEmpty(feedsOutContactUsDir)) {
        	List<String> custRequestIdList = FastList.newInstance();
			try {
				List custRequestAttrIdList = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("CustRequestAttribute", UtilMisc.toMap("attrName","IS_DOWNLOADED", "attrValue","N")), "custRequestId", true);
				if(UtilValidate.isNotEmpty(custRequestAttrIdList)){
					List<EntityExpr> custRequestExpr = FastList.newInstance();
					custRequestExpr.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestAttrIdList));
					custRequestExpr.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "RF_CONTACT_US"));
					custRequestExpr.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
					custRequestIdList = EntityUtil.getFieldListFromEntityList(delegator.findList("CustRequest", EntityCondition.makeCondition(custRequestExpr, EntityOperator.AND), null, null, null, false),"custRequestId",true);
				}
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
			}
        	
        	if(UtilValidate.isNotEmpty(custRequestIdList)) {
        		Map<String, Object> exportCustRequestContactUsXMLCtx = UtilMisc.toMap("custRequestIdList", custRequestIdList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try {
					exportResults = dispatcher.runSync("exportCustRequestContactUsXML", exportCustRequestContactUsXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutContactUsPrefix)) {
	                	exportedFileName = feedsOutContactUsPrefix + exportedFileName; 
	                }
	        		try {
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutContactUsDir, exportedFileName));
	        	        exportedFileSrc.delete();
	        	        for(String custRequestId : custRequestIdList)
	        	        {
	        	            Map<String, Object> updateCustReqAttrCtx = FastMap.newInstance();
	        	            updateCustReqAttrCtx.put("userLogin",userLogin);
	        	            updateCustReqAttrCtx.put("custRequestId",custRequestId);
	        	            updateCustReqAttrCtx.put("attrName","IS_DOWNLOADED");
	        	            updateCustReqAttrCtx.put("attrValue","Y");
	        	            GenericValue custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","IS_DOWNLOADED"));
	        	            if(UtilValidate.isNotEmpty(custReqAttribute))
	        	            {
	        	                dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	            else
	        	            {
	        	                dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	            
	        	            custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","DATETIME_DOWNLOADED"));
	        	            updateCustReqAttrCtx.put("attrName","DATETIME_DOWNLOADED");
	        	            updateCustReqAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
	        	            if(UtilValidate.isNotEmpty(custReqAttribute))
	        	            {
	        	                dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	            else
	        	            {
	        	                dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	        }
	        	    } catch (IOException e) {
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutContactUsDir);
				    }
				} catch (Exception e1) {
					e1.printStackTrace();
				}
        		
        	}
            
        } else {
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

        if (UtilValidate.isNotEmpty(feedsOutRequestCatalogDir)) {
        	List<String> custRequestIdList = FastList.newInstance();
			try {
				List custRequestAttrIdList = EntityUtil.getFieldListFromEntityList(delegator.findByAnd("CustRequestAttribute", UtilMisc.toMap("attrName","IS_DOWNLOADED", "attrValue","N")), "custRequestId", true);
				if(UtilValidate.isNotEmpty(custRequestAttrIdList)){
					List<EntityExpr> custRequestExpr = FastList.newInstance();
					custRequestExpr.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestAttrIdList));
					custRequestExpr.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, "RF_CATALOG"));
					custRequestExpr.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
					custRequestIdList = EntityUtil.getFieldListFromEntityList(delegator.findList("CustRequest", EntityCondition.makeCondition(custRequestExpr, EntityOperator.AND), null, null, null, false),"custRequestId",true);
				}
			} catch (GenericEntityException e2) {
				e2.printStackTrace();
			}
        	
        	if(UtilValidate.isNotEmpty(custRequestIdList)) {
        		Map<String, Object> exportCustRequestCatalogXMLCtx = UtilMisc.toMap("custRequestIdList", custRequestIdList,
                        "productStoreId", productStoreId,
                        "userLogin", userLogin);
        		Map exportResults;
				try {
					exportResults = dispatcher.runSync("exportCustRequestCatalogXML", exportCustRequestCatalogXMLCtx);
					String feedsDirectoryPath = (String)exportResults.get("feedsDirectoryPath");
	        		String feedsFileName = (String)exportResults.get("feedsFileName");
	        		File exportedFileSrc = new File(feedsDirectoryPath, feedsFileName);
	        		String exportedFileName = "_"+UtilDateTime.nowDateString("yyyyMMdd")+"_"+UtilDateTime.nowDateString("HHmmss")+".xml";
	                if(UtilValidate.isNotEmpty(feedsOutRequestCatalogPrefix)) {
	                	exportedFileName = feedsOutRequestCatalogPrefix + exportedFileName; 
	                }
	        		try {
	        	        FileUtils.copyFile(exportedFileSrc, new File(feedsOutRequestCatalogDir, exportedFileName));
	        	        exportedFileSrc.delete();
	        	        for(String custRequestId : custRequestIdList)
	        	        {
	        	            Map<String, Object> updateCustReqAttrCtx = FastMap.newInstance();
	        	            updateCustReqAttrCtx.put("userLogin",userLogin);
	        	            updateCustReqAttrCtx.put("custRequestId",custRequestId);
	        	            updateCustReqAttrCtx.put("attrName","IS_DOWNLOADED");
	        	            updateCustReqAttrCtx.put("attrValue","Y");
	        	            GenericValue custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","IS_DOWNLOADED"));
	        	            if(UtilValidate.isNotEmpty(custReqAttribute))
	        	            {
	        	                dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	            else
	        	            {
	        	                dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	            
	        	            custReqAttribute = delegator.findByPrimaryKey("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId,"attrName","DATETIME_DOWNLOADED"));
	        	            updateCustReqAttrCtx.put("attrName","DATETIME_DOWNLOADED");
	        	            updateCustReqAttrCtx.put("attrValue",UtilDateTime.nowTimestamp().toString());
	        	            if(UtilValidate.isNotEmpty(custReqAttribute))
	        	            {
	        	                dispatcher.runSync("updateCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	            else
	        	            {
	        	                dispatcher.runSync("createCustRequestAttribute", updateCustReqAttrCtx);
	        	            }
	        	        }
	        	    } catch (IOException e) {
	        		    Debug.log("Can not copy file " + exportedFileSrc.getName() + " to Directory " +feedsOutRequestCatalogDir);
				    }
				} catch (Exception e1) {
					e1.printStackTrace();
				}
        		
        	}
            
        } else {
        	Debug.log("No path specified, doing nothing.");
        }
        
        return ServiceUtil.returnSuccess();
    }
}
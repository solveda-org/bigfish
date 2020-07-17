package com.osafe.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transaction;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.MessageString;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class OsafeAdminServices {
    public static final String module = OsafeAdminServices.class.getName();
    private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static Map<String, Object> loadProductDataToDB(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String productLoadImagesDir = (String) context.get("productLoadImagesDir");
        String imageUrl = (String) context.get("imageUrl");
        String xlsFileName = (String)context.get("xlsFileName");
        String errorExists = (String)context.get("errorExists");
        String productStoreId = (String)context.get("productStoreId");
        
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString tmp = null;
        if(errorExists.equals("yes")) 
        {
            tmp = new MessageString(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductLoaderError", locale),productStoreId,true);
            error_list.add(tmp);
        }
        if(error_list.size() > 0) 
        {
            return ServiceUtil.returnError(error_list);
        }
        
        if (UtilValidate.isNotEmpty(xlsFileName)) 
        {
        
            String tempDir = (String)context.get("xlsFilePath");
            String filenameToUse = xlsFileName;
            File file = new File(tempDir + filenameToUse);
            String xlsDataFile = tempDir + filenameToUse;
            String xmlDataDir = tempDir;
            Boolean removeAll = false;
            Boolean autoLoad = true;
            Map<String, Object> importClientProductTemplateCtx = null;
            
            try 
            {
                Map result  = FastMap.newInstance();
                if(xlsDataFile.endsWith(".xls")) 
                {
                    importClientProductTemplateCtx = UtilMisc.toMap("xlsDataFile", xlsDataFile, "xmlDataDir", xmlDataDir,"productLoadImagesDir", productLoadImagesDir, "imageUrl", imageUrl, "removeAll",removeAll,"autoLoad",autoLoad,"userLogin",userLogin, "productStoreId", productStoreId);
                    result = dispatcher.runSync("importClientProductTemplate", importClientProductTemplateCtx);
                }
                if(xlsDataFile.endsWith(".xml")) 
                {
                    importClientProductTemplateCtx = UtilMisc.toMap("xmlDataFile", xlsDataFile, "xmlDataDir", xmlDataDir,"productLoadImagesDir", productLoadImagesDir, "imageUrl", imageUrl, "removeAll",removeAll,"autoLoad",autoLoad,"userLogin",userLogin, "productStoreId", productStoreId);
                    result = dispatcher.runSync("importClientProductXMLTemplate", importClientProductTemplateCtx);
                }
                if(UtilValidate.isNotEmpty(result.get("responseMessage")) && result.get("responseMessage").equals("error"))
                {
                    error_list.add(new MessageString(result.get("errorMessage").toString(), productStoreId ,true));
                    return ServiceUtil.returnError(error_list);
                }
                List<String> serviceMsg = (List)result.get("messages");
                if(serviceMsg.size() > 0 && serviceMsg.contains("SUCCESS")) 
                {
                    try 
                    {
                        FileUtils.deleteDirectory(new File(tempDir));
                    } 
                    catch (IOException e) 
                    {
                        Debug.logWarning(e, module);
                    }
                }
            } 
            catch (GenericServiceException e) 
            {
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
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString tmp = null;
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
                
                if(file.exists()) 
                {
                    file.delete();
                }
                
                try 
                {
                    RandomAccessFile out = new RandomAccessFile(file, "rw");
                    out.write(uploadBytes.array());
                    out.close();
                    result.put("uploadFileName",xlsFileName);
                    result.put("uploadFilePath",uploadTempDir);
                } 
                catch (FileNotFoundException e) 
                {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to open file for writing: " + file.getAbsolutePath());
                } catch (IOException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError("Unable to write binary data to: " + file.getAbsolutePath());
                }
        } 
        else 
        {
            //error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankUploadFileError", locale));
            tmp = new MessageString(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankUploadFileError", locale),"_uploadedFile_fileName",true);
            error_list.add(tmp);
            return ServiceUtil.returnError(error_list);
        }
        
        
        return result;
    }
    public static Map<String, Object> deleteEntityRows(DispatchContext dctx, Map<String, ? extends Object> context)throws IOException, JDOMException 
    {
        String nowDateTime = _sdf.format(UtilDateTime.nowTimestamp());
        String dayBackDateTime = (UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), -1)).toString();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString tmp = null;
        Delegator delegator = dctx.getDelegator();
        String entityName = (String)context.get("entityDBName");
        SQLProcessor sqlP = null;
        int deleteRowCount =0;
        
        if (UtilValidate.isNotEmpty(entityName)) {
                try {
                    String sql = null;
                    if (entityName.equalsIgnoreCase("Visit")){
                        List<String> relatedEntitiesList = FastList.newInstance();
                        relatedEntitiesList.add("CartAbandonedLine");
                        relatedEntitiesList.add("InventoryItemTempRes");
                        relatedEntitiesList.add("PartyDataSource");
                        relatedEntitiesList.add("PartyNeed");
                        relatedEntitiesList.add("ProductSearchResult");
                        relatedEntitiesList.add("ServerHit");
                        relatedEntitiesList.add("TrackingCodeVisit");
                        relatedEntitiesList.add("WorkEffortSearchResult");
                        UtilProperties.setPropertyValueInMemory("serverstats.properties", "stats.persist.REQUEST.hit", "false");
                        for(String relatedEntity : relatedEntitiesList){
                            GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(delegator.getEntityGroupName(relatedEntity));
                            DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperInfo.getHelperBaseName());
                            String relatedEntityName = delegator.getModelEntity(relatedEntity).getTableName(datasourceInfo);
                            //Preparing DELETE query
                            sqlP = new SQLProcessor(helperInfo);
                            sql = "DELETE FROM " + relatedEntityName;
                            sqlP.prepareStatement(sql);
                            deleteRowCount = sqlP.executeUpdate();
                        }
                        GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(delegator.getEntityGroupName(entityName));
                        DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperInfo.getHelperBaseName());
                        String entity = delegator.getModelEntity(entityName).getTableName(datasourceInfo);
                      //Preparing DELETE query for Vist entity
                        sqlP = new SQLProcessor(helperInfo);
                        sql = "DELETE FROM " + entity;
                        sql += " WHERE LAST_UPDATED_STAMP < '"+dayBackDateTime+"'";
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                        UtilProperties.setPropertyValueInMemory("serverstats.properties", "stats.persist.REQUEST.hit", "true");
                    }
                    else {
                        GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(delegator.getEntityGroupName(entityName));
                        DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperInfo.getHelperBaseName());
                        String tableName = delegator.getModelEntity(entityName).getTableName(datasourceInfo);
                        //Preparing DELETE query
                        sqlP = new SQLProcessor(helperInfo);
                        sql = "DELETE FROM " + tableName;
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                    }
                } catch (Exception e) {
                    Debug.logInfo("An error occurred executing query"+e, module);
                    //error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", e.getMessage(), locale));
                    tmp = new MessageString(UtilProperties.getMessage("OSafeAdminUiLabels", e.getMessage(), locale),"",true);
                    error_list.add(tmp);
                    
                    return ServiceUtil.returnError(error_list);
                } finally {
                    try {
                        sqlP.close();
                    } catch (GenericDataSourceException e) {
                        Debug.logInfo("An error occurred in closing SQLProcessor"+e, module);
                        //error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", e.getMessage(), locale));
                        tmp = new MessageString(UtilProperties.getMessage("OSafeAdminUiLabels", e.getMessage(), locale),"",true);
                        error_list.add(tmp);
                        return ServiceUtil.returnError(error_list);
                    } catch (Exception e) {
                        Debug.logInfo("An error occurred in closing SQLProcessor"+e, module);
                        tmp = new MessageString(UtilProperties.getMessage("OSafeAdminUiLabels", e.getMessage(), locale),"",true);
                        error_list.add(tmp);
                        //error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", e.getMessage(), locale));
                        return ServiceUtil.returnError(error_list);
                    } 
                }
        }
        else {
            //error_list.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankEntityName", locale));
            tmp = new MessageString(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankEntityName", locale),"entityDBName",true);
            error_list.add(tmp);
            return ServiceUtil.returnError(error_list);
        }
        // send the notification
        List<String> successMessageList = UtilMisc.toList(deleteRowCount+" rows were successfully deleted from the "+entityName+" entity");
        return ServiceUtil.returnSuccess(successMessageList);
    }

    public static Map<String, Object> deleteScheduledJobs(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        Delegator delegator = dctx.getDelegator();
        String statusId = (String)context.get("statusId");
        String serviceName = (String)context.get("serviceName");
        String isOld = (String)context.get("isOld");
        Locale locale = (Locale) context.get("locale");
        int daysToKeep = 30;
        int deleteRowCount =0;

        Timestamp now = UtilDateTime.nowTimestamp();
        Timestamp deleteTime = UtilDateTime.addDaysToTimestamp(now, daysToKeep * -1);

        // list of conditions
        List conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition("statusId", statusId));
        conditions.add(EntityCondition.makeCondition("serviceName", serviceName));

        if (UtilValidate.isNotEmpty(statusId) && statusId.equalsIgnoreCase("SERVICE_FINISHED"))
        {
            if (UtilValidate.isNotEmpty(isOld) && isOld.equalsIgnoreCase("Y"))
            {
                conditions.add(EntityCondition.makeCondition("finishDateTime", EntityOperator.LESS_THAN, deleteTime));
            }
            else
            {
                conditions.add(EntityCondition.makeCondition("finishDateTime", EntityOperator.GREATER_THAN, deleteTime));
            }
        }

        EntityCondition mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);

        // configure the find options
        EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setResultSetType(EntityFindOptions.TYPE_SCROLL_INSENSITIVE);
        findOptions.setMaxRows(1000);

        // always suspend the current transaction; use the one internally
        Transaction parent = null;
        try {
            if (TransactionUtil.getStatus() != TransactionUtil.STATUS_NO_TRANSACTION)
            {
                parent = TransactionUtil.suspend();
            }

            // lookup the jobs - looping 1000 at a time to avoid problems with cursors
            // also, using unique transaction to delete as many as possible even with errors
            boolean noMoreResults = false;
            boolean beganTx1 = false;
            while (!noMoreResults) 
            {
                // current list of records
                List<GenericValue> curList = null;
                try
                {
                    // begin this transaction
                    beganTx1 = TransactionUtil.begin();

                    EntityListIterator foundJobs = delegator.find("JobSandbox", mainCond, null, UtilMisc.toSet("jobId"), null, findOptions);
                    try 
                    {
                        curList = foundJobs.getPartialList(1, 1000);
                    }
                    finally
                    {
                        foundJobs.close();
                    }
                }
                catch (GenericEntityException e)
                {
                    Debug.logError(e, "Cannot obtain job data from datasource", module);
                    try
                    {
                        TransactionUtil.rollback(beganTx1, e.getMessage(), e);
                    }
                    catch (GenericTransactionException e1)
                    {
                        Debug.logWarning(e1, module);
                    }
                    return ServiceUtil.returnError(e.getMessage());
                }
                finally
                {
                    try
                    {
                        TransactionUtil.commit(beganTx1);
                    }
                    catch (GenericTransactionException e)
                    {
                        Debug.logWarning(e, module);
                    }
                }
                // remove list in its own transaction
                if (UtilValidate.isNotEmpty(curList))
                {
                    boolean beganTx2 = false;
                    try 
                    {
                        beganTx2 = TransactionUtil.begin();
                        delegator.removeAll(curList);
                    }
                    catch (GenericEntityException e)
                    {
                        Debug.logInfo("Cannot remove job", module);
                        try
                        {
                            TransactionUtil.rollback(beganTx2, e.getMessage(), e);
                        }
                        catch (GenericTransactionException e1)
                        {
                            Debug.logWarning(e1, module);
                        }
                    }
                    finally
                    {
                        try
                        {
                            TransactionUtil.commit(beganTx2);
                            deleteRowCount += curList.size();
                        }
                        catch (GenericTransactionException e)
                        {
                            Debug.logWarning(e, module);
                        }
                    }
                }
                else
                {
                    noMoreResults = true;
                }
            }
        }
        catch (GenericTransactionException e)
        {
            Debug.logError(e, "Unable to suspend transaction; cannot delete jobs!", module);
            return ServiceUtil.returnError(e.getMessage());
        }
        finally
        {
            if (parent != null)
            {
                try
                {
                    TransactionUtil.resume(parent);
                }
                catch (GenericTransactionException e)
                {
                    Debug.logWarning(e, module);
                }
            }
        }
        
        // send the notification
        String successMessage = UtilProperties.getMessage("OSafeAdminUiLabels", "DeletedJobsSuccessMessage", UtilMisc.toList(deleteRowCount), locale);
        return ServiceUtil.returnSuccess(UtilMisc.toList(successMessage));
    }
}
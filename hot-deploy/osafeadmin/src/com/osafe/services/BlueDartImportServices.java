package com.osafe.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javolution.util.FastList;
import javolution.util.FastMap;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import com.osafe.constants.Constants;

public class BlueDartImportServices 
{
	public static final String module = ImportServices.class.getName();

    public static final WritableFont cellFont = new WritableFont(WritableFont.TIMES, 10);
    public static final WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
    
	public static Map<String, Object> importBlueDartPrePaidXls(DispatchContext ctx, Map<String, ?> context) 
	{
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();

        String xlsDataFilePath = (String)context.get("xlsDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        //String xmlDataDirPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("import", "import.dir"), context);
        //Boolean removeAll = (Boolean) context.get("removeAll");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        // if (removeAll == null) removeAll = Boolean.FALSE;
        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        File baseDataDir = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xlsDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) 
        {
            try 
            {
                URL xlsDataFileUrl = UtilURL.fromFilename(xlsDataFilePath);
                InputStream ins = xlsDataFileUrl.openStream();

                if (ins != null && (xlsDataFilePath.toUpperCase().endsWith("XLS") || xlsDataFilePath.toUpperCase().endsWith("XLSX"))) 
                {
                    baseDataDir = new File(xmlDataDirPath);
                    if (baseDataDir.isDirectory() && baseDataDir.canWrite()) 
                    {

                    	try {
                            inputWorkbook = new File(baseDataDir,  UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xlsDataFilePath));
                            if (inputWorkbook.createNewFile()) {
                                Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
                            }
                            } catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            } catch (Exception exc) {
                                Debug.logError(exc, module);
                            }
                    }
                    else 
                    {
                        messages.add("xml data dir path not found or can't be write");
                    }
                }
                else 
                {
                    messages.add(" path specified for Excel sheet file is wrong , doing nothing.");
                }

            } 
            catch (IOException ioe) 
            {
                Debug.logError(ioe, module);
            } 
            catch (Exception exc) 
            {
                Debug.logError(exc, module);
            }
        }
        else 
        {
            messages.add("No path specified for Excel sheet file or xml data direcotry, doing nothing.");
        }

        // ######################################
        //read the temp xls file and generate csv 
        // ######################################
        if (inputWorkbook != null && baseDataDir  != null) 
        {
            try 
            {

                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("en", "EN"));
                Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                // Gets the sheets from workbook
                for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                {
                    BufferedWriter bw = null; 
                    try 
                    {
                        Sheet s = wb.getSheet(sheet);
                        String sTabName=s.getName();
                        
                        if (sheet == 0)
                        {
                        	List dataRows = buildDataRows(buildBlueDartPrepaidHeader(),s);
                            buildBlueDartPrepaidData(dataRows, xmlDataDirPath);
                        }

                        //File to store data in form of CSV
                    } 
                    catch (Exception exc) 
                    {
                        Debug.logError(exc, module);
                    } 
                    finally 
                    {
                        try 
                        {
                            if (fOutProduct != null) 
                            {
                            	fOutProduct.close();
                            }
                        } 
                        catch (IOException ioe) 
                        {
                            Debug.logError(ioe, module);
                        }
                    }
                }

                // ############################################
                // call the service for remove entity data 
                // if removeAll and autoLoad parameter are true 
                // ############################################
                /*if (removeAll) {
                    Map importRemoveEntityDataParams = UtilMisc.toMap();
                    try {
                    
                        Map result = dispatcher.runSync("importRemoveEntityData", importRemoveEntityDataParams);
                    
                        List<String> serviceMsg = (List)result.get("messages");
                        for (String msg: serviceMsg) {
                            messages.add(msg);
                        }
                    } catch (Exception exc) {
                        Debug.logError(exc, module);
                        autoLoad = Boolean.FALSE;
                    }
                }*/

                // ##############################################
                // move the generated xml files in done directory
                // ##############################################
                File doneXmlDir = new File(baseDataDir, Constants.DONE_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
                File[] fileArray = baseDataDir.listFiles();
                for (File file: fileArray) 
                {
                    try 
                    {
                        if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("XML")) 
                        {
                            FileUtils.copyFileToDirectory(file, doneXmlDir);
                            file.delete();
                        }
                    } 
                    catch (IOException ioe) 
                    {
                        Debug.logError(ioe, module);
                    } 
                    catch (Exception exc) 
                    {
                        Debug.logError(exc, module);
                    }
                }

                // ######################################################################
                // call service for insert row in database  from generated xml data files 
                // by calling service entityImportDir if autoLoad parameter is true
                // ######################################################################
                if (autoLoad) 
                {
                    //Debug.logInfo("=====657========="+doneXmlDir.getPath()+"=========================", module);
                    Map entityImportDirParams = UtilMisc.toMap("path", doneXmlDir.getPath(), 
                                                             "userLogin", context.get("userLogin"));
                     try 
                     {
                         Map result = dispatcher.runSync("entityImportDir", entityImportDirParams);
                         List<String> serviceMsg = (List)result.get("messages");
                         for (String msg: serviceMsg) 
                         {
                             messages.add(msg);
                         }
                     } 
                     catch (Exception exc) 
                     {
                         Debug.logError(exc, module);
                     }
                }

            } 
            catch (BiffException be) 
            {
                Debug.logError(be, module);
            } 
            catch (Exception exc) 
            {
                Debug.logError(exc, module);
            }
            finally 
            {
                inputWorkbook.delete();
            }
        }
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
    } 
	
	public static Map<String, Object> importBlueDartCoDXls(DispatchContext ctx, Map<String, ?> context) 
	{
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();

        String xlsDataFilePath = (String)context.get("xlsDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        //String xmlDataDirPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("import", "import.dir"), context);
        //Boolean removeAll = (Boolean) context.get("removeAll");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        // if (removeAll == null) removeAll = Boolean.FALSE;
        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        File baseDataDir = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xlsDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) 
        {
            try 
            {
                URL xlsDataFileUrl = UtilURL.fromFilename(xlsDataFilePath);
                InputStream ins = xlsDataFileUrl.openStream();

                if (ins != null && (xlsDataFilePath.toUpperCase().endsWith("XLS") || xlsDataFilePath.toUpperCase().endsWith("XLSX"))) 
                {
                    baseDataDir = new File(xmlDataDirPath);
                    if (baseDataDir.isDirectory() && baseDataDir.canWrite()) 
                    {

                    	try {
                            inputWorkbook = new File(baseDataDir,  UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xlsDataFilePath));
                            if (inputWorkbook.createNewFile()) {
                                Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
                            }
                            } catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            } catch (Exception exc) {
                                Debug.logError(exc, module);
                            }
                    }
                    else 
                    {
                        messages.add("xml data dir path not found or can't be write");
                    }
                }
                else 
                {
                    messages.add(" path specified for Excel sheet file is wrong , doing nothing.");
                }

            } 
            catch (IOException ioe) 
            {
                Debug.logError(ioe, module);
            } 
            catch (Exception exc) 
            {
                Debug.logError(exc, module);
            }
        }
        else 
        {
            messages.add("No path specified for Excel sheet file or xml data direcotry, doing nothing.");
        }

        // ######################################
        //read the temp xls file and generate csv 
        // ######################################
        if (inputWorkbook != null && baseDataDir  != null) 
        {
            try 
            {

                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("en", "EN"));
                Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                // Gets the sheets from workbook
                for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                {
                    BufferedWriter bw = null; 
                    try 
                    {
                        Sheet s = wb.getSheet(sheet);
                        String sTabName=s.getName();
                        
                        if (sheet == 0)
                        {
                        	List dataRows = buildDataRows(buildBlueDartCoDHeader(),s);
                            buildBlueDartCoDData(dataRows, xmlDataDirPath);
                        }

                        //File to store data in form of CSV
                    } 
                    catch (Exception exc) 
                    {
                        Debug.logError(exc, module);
                    } 
                    finally 
                    {
                        try 
                        {
                            if (fOutProduct != null) 
                            {
                            	fOutProduct.close();
                            }
                        } 
                        catch (IOException ioe) 
                        {
                            Debug.logError(ioe, module);
                        }
                    }
                }

                // ############################################
                // call the service for remove entity data 
                // if removeAll and autoLoad parameter are true 
                // ############################################
                /*if (removeAll) {
                    Map importRemoveEntityDataParams = UtilMisc.toMap();
                    try {
                    
                        Map result = dispatcher.runSync("importRemoveEntityData", importRemoveEntityDataParams);
                    
                        List<String> serviceMsg = (List)result.get("messages");
                        for (String msg: serviceMsg) {
                            messages.add(msg);
                        }
                    } catch (Exception exc) {
                        Debug.logError(exc, module);
                        autoLoad = Boolean.FALSE;
                    }
                }*/

                // ##############################################
                // move the generated xml files in done directory
                // ##############################################
                File doneXmlDir = new File(baseDataDir, Constants.DONE_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
                File[] fileArray = baseDataDir.listFiles();
                for (File file: fileArray) 
                {
                    try 
                    {
                        if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("XML")) 
                        {
                            FileUtils.copyFileToDirectory(file, doneXmlDir);
                            file.delete();
                        }
                    } 
                    catch (IOException ioe) 
                    {
                        Debug.logError(ioe, module);
                    } 
                    catch (Exception exc) 
                    {
                        Debug.logError(exc, module);
                    }
                }

                // ######################################################################
                // call service for insert row in database  from generated xml data files 
                // by calling service entityImportDir if autoLoad parameter is true
                // ######################################################################
                if (autoLoad) 
                {
                    //Debug.logInfo("=====657========="+doneXmlDir.getPath()+"=========================", module);
                    Map entityImportDirParams = UtilMisc.toMap("path", doneXmlDir.getPath(), 
                                                             "userLogin", context.get("userLogin"));
                     try 
                     {
                         Map result = dispatcher.runSync("entityImportDir", entityImportDirParams);
                         List<String> serviceMsg = (List)result.get("messages");
                         for (String msg: serviceMsg) 
                         {
                             messages.add(msg);
                         }
                     } 
                     catch (Exception exc) 
                     {
                         Debug.logError(exc, module);
                     }
                }

            } 
            catch (BiffException be) 
            {
                Debug.logError(be, module);
            } 
            catch (Exception exc) 
            {
                Debug.logError(exc, module);
            }
            finally 
            {
                inputWorkbook.delete();
            }
        }
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
    }
	
	
	private static void buildBlueDartPrepaidData(List<Map<String, String>> dataRows, String xmlDataDirPath) 
	{

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
		try 
		{
	        fOutFile = new File(xmlDataDirPath, "000-BlueDartPrepaid.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                    StringBuilder  rowString = new StringBuilder();
                    rowString.append("<" + "BlueDartPrepaid" + " ");
	            	Map<String, String> mRow = (Map<String, String>)dataRows.get(i);
	            	rowString.append("pincode" + "=\"" + mRow.get("pincode") + "\" ");
	            	rowString.append("carea" + "=\"" + mRow.get("carea") + "\" ");
	            	rowString.append("cscrcd" + "=\"" + mRow.get("cscrcd") + "\" ");
	            	rowString.append("careadesc" + "=\"" + mRow.get("careadesc") + "\" ");
	            	rowString.append("city" + "=\"" + mRow.get("city") + "\" ");
	            	rowString.append("bdelLoc" + "=\"" + mRow.get("bdelLoc") + "\" ");
	            	rowString.append("state" + "=\"" + mRow.get("state") + "\" ");
	            	rowString.append("region" + "=\"" + mRow.get("region") + "\" ");
	            	rowString.append("zone" + "=\"" + mRow.get("zone") + "\" ");
	            	rowString.append("cloctype" + "=\"" + mRow.get("cloctype") + "\" ");
	            	rowString.append("bembargo" + "=\"" + mRow.get("bembargo") + "\" ");
	            	rowString.append("domestic" + "=\"" + mRow.get("domestic") + "\" ");
	            	rowString.append("apex" + "=\"" + mRow.get("apex") + "\" ");
	            	rowString.append("surface" + "=\"" + mRow.get("surface") + "\" ");
	            	rowString.append("cod" + "=\"" + mRow.get("cod") + "\" ");
	            	rowString.append("creditcard" + "=\"" + mRow.get("creditcard") + "\" ");
	            	rowString.append("tdd" + "=\"" + mRow.get("tdd") + "\" ");
	            	rowString.append("tddapx1200" + "=\"" + mRow.get("tddapx1200") + "\" ");
	            	rowString.append("dstarcd" + "=\"" + mRow.get("dstarcd") + "\" ");
	            	rowString.append("ccrcrdscr" + "=\"" + mRow.get("ccrcrdscr") + "\" ");
	            	rowString.append("csfczone" + "=\"" + mRow.get("csfczone") + "\" ");
	            	rowString.append("sfzonedesc" + "=\"" + mRow.get("sfzonedesc") + "\" ");
	            	rowString.append("subregion" + "=\"" + mRow.get("subregion") + "\" ");
	            	rowString.append("cservflag" + "=\"" + mRow.get("cservflag") + "\" ");
	            	rowString.append("ctel" + "=\"" + mRow.get("ctel") + "\" ");
	            	rowString.append("cnt" + "=\"" + mRow.get("cnt") + "\" ");
	            	rowString.append("newzone" + "=\"" + mRow.get("newzone") + "\" ");
	            	rowString.append("dodApex" + "=\"" + mRow.get("dodApex") + "\" ");
	            	rowString.append("dodSfc" + "=\"" + mRow.get("dodSfc") + "\" ");
	            	rowString.append("fodApex" + "=\"" + mRow.get("fodApex") + "\" ");
	            	rowString.append("fodSfc" + "=\"" + mRow.get("fodSfc") + "\" ");
	            	rowString.append("blueDartlimit" + "=\"" + mRow.get("blueDartlimit") + "\" ");
	            	
                    rowString.append("/>");
                    bwOutFile.write(rowString.toString());
                    bwOutFile.newLine();
 	            	
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
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
                 }
             } 
             catch (IOException ioe) 
             {
                 Debug.logError(ioe, module);
             }
         }
      	 
    }
	
	private static void buildBlueDartCoDData(List<Map<String, String>> dataRows, String xmlDataDirPath) 
	{

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
		try 
		{
	        fOutFile = new File(xmlDataDirPath, "000-BlueDartCoD.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                    StringBuilder  rowString = new StringBuilder();
                    rowString.append("<" + "BlueDartCodpin" + " ");
	            	Map<String, String> mRow = (Map<String, String>)dataRows.get(i);
	            	rowString.append("pincode" + "=\"" + mRow.get("pincode") + "\" ");
	            	rowString.append("carea" + "=\"" + mRow.get("carea") + "\" ");
	            	rowString.append("cscrcd" + "=\"" + mRow.get("cscrcd") + "\" ");
	            	rowString.append("city" + "=\"" + mRow.get("city") + "\" ");
	            	rowString.append("bdelLoc" + "=\"" + mRow.get("bdelLoc") + "\" ");
	            	rowString.append("state" + "=\"" + mRow.get("state") + "\" ");
	            	rowString.append("region" + "=\"" + mRow.get("region") + "\" ");
	            	rowString.append("zone" + "=\"" + mRow.get("zone") + "\" ");
	            	rowString.append("cloctype" + "=\"" + mRow.get("cloctype") + "\" ");
	            	rowString.append("bembargo" + "=\"" + mRow.get("bembargo") + "\" ");
	            	rowString.append("domestic" + "=\"" + mRow.get("domestic") + "\" ");
	            	rowString.append("apex" + "=\"" + mRow.get("apex") + "\" ");
	            	rowString.append("surface" + "=\"" + mRow.get("surface") + "\" ");
	            	rowString.append("cod" + "=\"" + mRow.get("cod") + "\" ");
	            	rowString.append("tdd" + "=\"" + mRow.get("tdd") + "\" ");
	            	rowString.append("dstarcd" + "=\"" + mRow.get("dstarcd") + "\" ");
	            	rowString.append("subregion" + "=\"" + mRow.get("subregion") + "\" ");
	            	rowString.append("returnLoc" + "=\"" + mRow.get("returnLoc") + "\" ");
	            	rowString.append("retLoc" + "=\"" + mRow.get("retLoc") + "\" ");
	            	rowString.append("newzone" + "=\"" + mRow.get("newzone") + "\" ");
	            	rowString.append("blueDartlimit" + "=\"" + mRow.get("blueDartlimit") + "\" ");
	            	
                    rowString.append("/>");
                    bwOutFile.write(rowString.toString());
                    bwOutFile.newLine();
 	            	
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
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
                 }
             } 
             catch (IOException ioe) 
             {
                 Debug.logError(ioe, module);
             }
         }
      	 
    }
	
	
	public static List<String> buildBlueDartPrepaidHeader() 
	{
        List<String> headerCols = FastList.newInstance();
   	    headerCols.add("carea");
   	    headerCols.add("cscrcd");
   	    headerCols.add("pincode");
   	    headerCols.add("careadesc");
   	    headerCols.add("city");
   	    headerCols.add("bdelLoc");
   	    headerCols.add("state");
   	    headerCols.add("region");
   	    headerCols.add("zone");
   	    headerCols.add("cloctype");
   	    headerCols.add("bembargo");
   	    headerCols.add("domestic");
   	    headerCols.add("apex");
	    headerCols.add("surface");
	    headerCols.add("cod");
   	    headerCols.add("creditcard");
   	    headerCols.add("tdd");
	    headerCols.add("tddapx1200");
	    headerCols.add("dstarcd");
	    headerCols.add("ccrcrdscr");
	    headerCols.add("csfczone");
   	    headerCols.add("sfzonedesc");
   	    headerCols.add("subregion");
	    headerCols.add("cservflag");
	    headerCols.add("ctel");
	    headerCols.add("cnt");
	    headerCols.add("newzone");
   	    headerCols.add("dodApex");
   	    headerCols.add("dodSfc");
	    headerCols.add("fodApex");
	    headerCols.add("fodSfc");
	    headerCols.add("blueDartlimit");
   	    
   	    return headerCols;
    }
	
	public static List<String> buildBlueDartCoDHeader() 
	{
        List<String> headerCols = FastList.newInstance();
   	    headerCols.add("carea");
   	    headerCols.add("cscrcd");
   	    headerCols.add("pincode");
   	    headerCols.add("city");
   	    headerCols.add("bdelLoc");
   	    headerCols.add("state");
   	    headerCols.add("region");
   	    headerCols.add("zone");
   	    headerCols.add("cloctype");
   	    headerCols.add("bembargo");
   	    headerCols.add("domestic");
   	    headerCols.add("apex");
	    headerCols.add("surface");
	    headerCols.add("cod");
   	    headerCols.add("tdd");
	    headerCols.add("dstarcd");
   	    headerCols.add("subregion");
	    headerCols.add("returnLoc");
	    headerCols.add("retLoc");
	    headerCols.add("newzone");
	    headerCols.add("blueDartlimit");
   	    
   	    return headerCols;
    }
	
	private static void writeXmlHeader(BufferedWriter bfOutFile) 
	{
    	try 
    	{
    		bfOutFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    		bfOutFile.newLine();
    		bfOutFile.write("<entity-engine-xml>");
    		bfOutFile.newLine();
            bfOutFile.flush();
    	}
    	catch (Exception e)
    	{
    	}
    }
	
    private static void writeXmlFooter(BufferedWriter bfOutFile) 
    {
    	try 
    	{
    		bfOutFile.write("</entity-engine-xml>");
            bfOutFile.flush();
            bfOutFile.close();
    	}
    	catch (Exception e)
    	{
    	}
    }
    
    public static List buildDataRows(List headerCols,Sheet s) 
    {
		List dataRows = FastList.newInstance();
		try 
		{
            for (int rowCount = 1 ; rowCount < s.getRows() ; rowCount++) 
            {
            	Cell[] row = s.getRow(rowCount);
                if (row.length > 0) 
                {
            	    Map mRows = FastMap.newInstance();
                    for (int colCount = 0; colCount < headerCols.size(); colCount++) 
                    {
                	    String colContent=null;
                	    try 
                	    {
                		    colContent=row[colCount].getContents();
                	    }
                	    catch (Exception e) 
                	    {
                		    colContent="";
                   	    }
                        mRows.put(headerCols.get(colCount), StringUtil.replaceString(colContent,"\"","'"));
                    }
                    mRows = formatProductXLSData(mRows);
                    dataRows.add(mRows);
                }
            }
    	}
      	catch (Exception e) 
      	{
   	    }
      	return dataRows;
    }
    
    private static Map<String, String> formatProductXLSData(Map<String, String> dataMap) 
    {
    	Map<String, String> formattedDataMap = new HashMap<String, String>();
    	for (Map.Entry<String, String> entry : dataMap.entrySet()) 
    	{
    		String value = entry.getValue();
    		if(UtilValidate.isNotEmpty(value)) 
    		{
    			value = StringUtil.replaceString(value, "&", "&amp");
    			value = StringUtil.replaceString(value, ";", "&#59;");
    	    	value = StringUtil.replaceString(value, "&amp", "&amp;");
    		}
    		formattedDataMap.put(entry.getKey(), value);
    	}
    	return formattedDataMap;
    }
}

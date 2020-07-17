package com.osafe.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilIO;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.datafile.DataFile;
import org.ofbiz.datafile.DataFile2EntityXml;
import org.ofbiz.datafile.DataFileException;
import org.ofbiz.datafile.ModelDataFile;
import org.ofbiz.datafile.ModelDataFileReader;
import org.ofbiz.datafile.Record;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.config.DatasourceInfo;
import org.ofbiz.entity.config.EntityConfigUtil;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.common.login.LoginServices;

import com.osafe.constants.Constants;
import com.osafe.feeds.FeedsUtil;
import com.osafe.feeds.osafefeeds.*;

import com.osafe.util.OsafeAdminUtil;
import com.osafe.util.OsafeProductLoaderHelper;


public class ImportServices {

    public static final String module = ImportServices.class.getName();
    private static final ResourceBundle OSAFE_PROP = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());
    private static final ResourceBundle OSAFE_ADMIN_PROP = UtilProperties.getResourceBundle("osafeAdmin", Locale.getDefault());
    private static final Long FEATURED_PRODUCTS_CATEGORY = Long.valueOf(10054);
    private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat _df = new DecimalFormat("##.00");
    private static Delegator _delegator = null;
    private static LocalDispatcher _dispatcher = null;
    private static Locale _locale =null;
    private static String localeString = "";
    
    public static final int scale = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    
    public static final WritableFont cellFont = new WritableFont(WritableFont.TIMES, 10);
    public static final WritableCellFormat cellFormat = new WritableCellFormat(cellFont,NumberFormats.TEXT);
    
    private static Map<String, ?> context = FastMap.newInstance();
	
	private static String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
	
	public static List<Map<Object, Object>> imageLocationPrefList = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
	
	private static String schemaLocation = FlexibleStringExpander.expandString("${sys:getProperty('ofbiz.home')}/hot-deploy/osafeadmin/dtd/feeds/bigfishfeed.xsd", context);
	
	private static final String resource = "OSafeAdminUiLabels";
	
    public static Map<String, Object> importProcess(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        try {
            // dispatcher.runSync("import[ClientServices", context);
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return result;
    }

    private static DataFile getImportDataFile(Map<String, ?> context) {
        DataFile importDataFile = null;
        String definitionFileName = (String) context.get("definitionFileName");
        String definitionName = (String) context.get("definitionName");
        String dataFileName = (String) context.get("dataFileName");

        String importDir = OSAFE_PROP.getString("importDir");
        URL definitionUrl = null;
        String fullDefinitionFilePath = importDir;
        fullDefinitionFilePath += "definition" + File.separator;
        fullDefinitionFilePath += definitionFileName + ".xml";

        URL dataFileUrl = null;
        String fullDataFilePath = importDir;
        fullDataFilePath += "data" + File.separator + "csv" + File.separator;
        fullDataFilePath += dataFileName + ".csv";

        definitionUrl = UtilURL.fromFilename(fullDefinitionFilePath);
        dataFileUrl = UtilURL.fromFilename(fullDataFilePath);

        try {

            importDataFile = DataFile.readFile(dataFileUrl, definitionUrl, definitionName);
        } catch (DataFileException e) {
            Debug.logError(e, "Error Loading File", module);
        }
        return importDataFile;
    }

    public static Map<String, Object> importDataFileCsvToXml(DispatchContext ctx, Map<String, ?> context) {
        Map<String, Object> result = FastMap.newInstance();
        String sImpSize = "0";

        String exportXmlFileName = (String) context.get("exportXmlFileName");

        String exportDir = OSAFE_PROP.getString("exportDir");
        String fullExportPath = exportDir;
        fullExportPath += exportXmlFileName + "_" + UtilDateTime.nowDateString() + ".xml";

        DataFile importDataFile = getImportDataFile(context);

        if (importDataFile != null) {

            List<Record> records = importDataFile.getRecords();

            sImpSize = "" + records.size();
            try {
                DataFile2EntityXml.writeToEntityXml(fullExportPath, importDataFile);
            } catch (DataFileException e) {
                Debug.logError(e, "Error Saving File", module);
            }

        }

        result.put("recordSize", sImpSize);
        return result;

    }

	public static Map<String, Object> importCsvToXml(DispatchContext ctx, Map context){
        /*
        * This Service is used to generate XML file from CSV file using
        * entity definition
        */
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String sourceCsvFileLoc = (String)context.get("sourceCsvFileLoc");
        String definitionFileLoc = (String)context.get("definitionFileLoc");
        String targetXmlFileLoc = (String)context.get("targetXmlFileLoc");

        Collection definitionFileNames = null;
        File outXmlDir = null;
        URL definitionUrl= null;
        definitionUrl = UtilURL.fromFilename(definitionFileLoc);

        if (definitionUrl != null) {
            try {
                ModelDataFileReader reader = ModelDataFileReader.getModelDataFileReader(definitionUrl);
                if (reader != null) {
                    definitionFileNames = (Collection)reader.getDataFileNames();
                    context.put("definitionFileNames", definitionFileNames);
                }
            } catch(Exception ex) {
                Debug.logError(ex.getMessage(), module);
            }
        }
        if (targetXmlFileLoc != null) {
            outXmlDir = new File(targetXmlFileLoc);
            if (!outXmlDir.exists()) {
                outXmlDir.mkdir();
            }
        }
        if (sourceCsvFileLoc != null) {
            File inCsvDir = new File(sourceCsvFileLoc);
            if (!inCsvDir.exists()) {
                inCsvDir.mkdir();
                }
            if (inCsvDir.isDirectory() && inCsvDir.canRead() && outXmlDir.isDirectory() && outXmlDir.canWrite()) {
                File[] fileArray = inCsvDir.listFiles();
                URL dataFileUrl = null;
                for (File file: fileArray) {
                    if(file.getName().toUpperCase().endsWith("CSV")) {
                        String fileNameWithoutExt = FilenameUtils.removeExtension(file.getName());
                        String definationName = OSAFE_PROP.getString(Constants.IMPOERT_XLS_ENTITY_PROPERTY_MAPPING_PREFIX+UtilValidate.stripWhitespace(fileNameWithoutExt));
                        if(definitionFileNames.contains(definationName)) {
                            dataFileUrl = UtilURL.fromFilename(file.getPath());
                            DataFile dataFile = null;
                            if(dataFileUrl != null && definitionUrl != null && definitionFileNames != null) {
                                try {
                                    dataFile = DataFile.readFile(dataFileUrl, definitionUrl, definationName);
                                    context.put("dataFile", dataFile);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if(dataFile != null) {
                                ModelDataFile modelDataFile = (ModelDataFile)dataFile.getModelDataFile();
                                context.put("modelDataFile", modelDataFile);
                            }
                            if (dataFile != null && definationName != null) {
                                try {
                                    DataFile2EntityXml.writeToEntityXml(new File(outXmlDir, definationName +".xml").getPath(), dataFile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            definitionFileNames.remove(definationName);
                        }
                        else {
                            Debug.log("======csv file name which not according to import defination file================="+file.getName()+"====");
                        }
                    }
                }
            }
        }
        return result;
    }
     /**
     * service for generating the xml data files from xls data file using import entity defination 
     * take the xls file location path, output xml data files directory path and import entity defination xml file location
     * working first upload the xls data file ,generate csv files from xls data file
     * using service importCsvToXml generate xml data files. 
     * this service support only 2003 Excel sheet format
     */
    public static Map<String, Object> importXLSFileAndGenerateXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        InputStream ins = null;
        File inputWorkbook = null, baseDataDir = null, csvDir = null;

        String definitionFileLoc = (String)context.get("definitionFileLoc");
        String xlsDataFilePath = (String)context.get("xlsDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");

        if (UtilValidate.isNotEmpty(xlsDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
            try {
                // ######################################
                // make the input stram for xls data file
                // ######################################
                URL xlsDataFileUrl = UtilURL.fromFilename(xlsDataFilePath);
                ins = xlsDataFileUrl.openStream();

                if (ins != null && (xlsDataFilePath.toUpperCase().endsWith("XLS"))) {
                    baseDataDir = new File(xmlDataDirPath);
                    if (baseDataDir.isDirectory() && baseDataDir.canWrite()) {

                        // ############################################
                        // move the existing xml files in dump directory
                        // ############################################
                        File dumpXmlDir = null;
                        File[] fileArray = baseDataDir.listFiles();
                        for (File file: fileArray) {
                            try {
                                if (file.getName().toUpperCase().endsWith("XML")) {
                                    if (dumpXmlDir == null) {
                                        dumpXmlDir = new File(baseDataDir, Constants.DUMP_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
                                    }
                                    FileUtils.copyFileToDirectory(file, dumpXmlDir);
                                    file.delete();
                                }
                            } catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            } catch (Exception exc) {
                                Debug.logError(exc, module);
                            }
                        }
                        // ######################################
                        //save the temp xls data file on server 
                        // ######################################
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
                    else {
                        messages.add("xml data dir path not found or can't be write");
                    }
                }
                else {
                    messages.add(" path specified for Excel sheet file is wrong , doing nothing.");
                }

            } catch (IOException ioe) {
                Debug.logError(ioe, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }
        else {
            messages.add("No path specified for Excel sheet file or xml data direcotry, doing nothing.");
        }

        // ######################################
        //read the temp xls file and generate csv 
        // ######################################
        if (inputWorkbook != null && baseDataDir  != null) {
            try {
                csvDir = new File(baseDataDir,  UtilDateTime.nowDateString()+"_CSV_"+UtilDateTime.nowAsString());
                if (!csvDir.exists() ) {
                    csvDir.mkdirs();
                }

                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("en", "EN"));
                Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                // Gets the sheets from workbook
                for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) {
                    BufferedWriter bw = null; 
                    try {
                        Sheet s = wb.getSheet(sheet);

                        //File to store data in form of CSV
                        File csvFile = new File(csvDir, s.getName().trim()+".csv");
                        if (csvFile.createNewFile()) {
                            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"));

                            Cell[] row = null;
                            //loop start from 1 because of discard the header row
                            for (int rowCount = 1 ; rowCount < s.getRows() ; rowCount++) {
                                StringBuilder  rowString = new StringBuilder();
                                row = s.getRow(rowCount);
                                if (row.length > 0) {
                                    rowString.append(row[0].getContents());
                                    for (int colCount = 1; colCount < row.length; colCount++) {
                                        rowString.append(",");
                                        rowString.append(row[colCount].getContents());
                                     }
                                     if(UtilValidate.isNotEmpty(StringUtil.replaceString(rowString.toString(), ",", "").trim())) {
                                         bw.write(rowString.toString());
                                         bw.newLine();
                                     }
                                }
                            }
                        }
                    } catch (IOException ioe) {
                        Debug.logError(ioe, module);
                    } catch (Exception exc) {
                        Debug.logError(exc, module);
                    } 
                    finally {
                        try {
                            if (bw != null) {
                                bw.flush();
                                bw.close();
                            }
                        } catch (IOException ioe) {
                            Debug.logError(ioe, module);
                        }
                        bw = null;
                    }
                }

            } catch (BiffException be) {
                Debug.logError(be, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }

        // ####################################################################################################################
        //Generate xml files from csv directory using importCsvToXml service 
        //Delete temp xls file and csv directory
        // ####################################################################################################################
        if(csvDir != null) {

            // call service for generate xml files from csv directory
            Map importCsvToXmlParams = UtilMisc.toMap("sourceCsvFileLoc", csvDir.getPath(),
                                                      "definitionFileLoc", definitionFileLoc,
                                                      "targetXmlFileLoc", baseDataDir.getPath());
            try {
                Map result = dispatcher.runSync("importCsvToXml", importCsvToXmlParams);

            } catch(Exception exc) {
                Debug.logError(exc, module);
            }

            //Delete temp xls file and csv directory 
            try {
                inputWorkbook.delete();
                FileUtils.deleteDirectory(csvDir);

            } catch (IOException ioe) {
                Debug.logError(ioe, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
            
            messages.add("file saved in xml base dir.");
        }
        else {
            messages.add("input parameter is wrong , doing nothing.");
        }

        // send the notification
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
    }
    /**
     * service for generating the xml data files and execute the insert script in database 
     * from xls data file using import entity defination 
     * take the xls file location path, output xml data files directory path and import entity defination xml file location
     * working first upload the xls data file ,generate csv files from xls data file
     * using service importCsvToXml generate xml data files.
     * save the all generated xml in new directory using service  importXLSFileAndGenerateXML
     */
    public static Map<String, Object> importXLSFileAndRunInsertInDataBase(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();

        String definitionFileLoc = (String)context.get("definitionFileLoc");
        String xlsDataFilePath = (String)context.get("xlsDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");

        if (UtilValidate.isNotEmpty(xlsDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {

            File baseDataDir = new File(xmlDataDirPath);
            if (baseDataDir.isDirectory() && baseDataDir.canWrite()) {
                // ######################################################
                // call service for generate xml files from Excel File
                // ######################################################
                Map importXLSFileAndGenerateXMLParams = UtilMisc.toMap("definitionFileLoc", definitionFileLoc,
                                                                       "xlsDataFile", xlsDataFilePath,
                                                                       "xmlDataDir", xmlDataDirPath);
                try {
                    
                    Map result = dispatcher.runSync("importXLSFileAndGenerateXML", importXLSFileAndGenerateXMLParams);
                    
                    List<String> serviceMsg = (List)result.get("messages");
                    for (String msg: serviceMsg) {
                        messages.add(msg);
                    }
                } catch (Exception exc) {
                    Debug.logError(exc, module);
                }

                // ########################################################################################################
                // call service for insert row in database  from generated xml data files by calling service entityImportDir
                // ########################################################################################################
                 Map entityImportDirParams = UtilMisc.toMap("path", xmlDataDirPath, 
                                                             "userLogin", context.get("userLogin"));
                 try {
                     Map result = dispatcher.runSync("entityImportDir", entityImportDirParams);
                     
                     List<String> serviceMsg = (List)result.get("messages");
                     for (String msg: serviceMsg) {
                         messages.add(msg);
                     }
                 } catch (Exception exc) {
                     Debug.logError(exc, module);
                 }

                 // ##############################################
                 // move the generated xml files in done directory
                 // ##############################################
                 File doneXmlDir = null;
                 File[] fileArray = baseDataDir.listFiles();
                 for (File file: fileArray) {
                     try {
                         if (file.getName().toUpperCase().endsWith("XML")) {
                             if (doneXmlDir == null) {
                                 doneXmlDir = new File(baseDataDir, Constants.DONE_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
                             }
                             FileUtils.copyFileToDirectory(file, doneXmlDir);
                             file.delete();
                         }
                     } catch (IOException ioe) {
                         Debug.logError(ioe, module);
                     } catch (Exception exc) {
                         Debug.logError(exc, module);
                     }
                 }
            }
            else {
                messages.add("xml data dir path not found or can't be write");
            }
        }
        else {
            messages.add("No path specified for Excel sheet file or xml data direcotry, doing nothing.");
        }
        // send the notification
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
    }
    
    public static Map<String, Object> importProductXls(DispatchContext ctx, Map<String, ?> context) 
    {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String xlsDataFilePath = (String)context.get("xlsDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        String loadImagesDirPath=(String)context.get("productLoadImagesDir");
        String imageUrl = (String)context.get("imageUrl");
        Boolean removeAll = (Boolean) context.get("removeAll");
        Boolean autoLoad = (Boolean) context.get("autoLoad");
        String productStoreId = (String) context.get("productStoreId");
        
        if (removeAll == null) removeAll = Boolean.FALSE;
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

                if (ins != null && (xlsDataFilePath.toUpperCase().endsWith("XLS"))) 
                {
                    baseDataDir = new File(xmlDataDirPath);
                    if (baseDataDir.isDirectory() && baseDataDir.canWrite()) 
                    {
                        
                        // ######################################
                        //save the temp xls data file on server 
                        // ######################################
                        try 
                        {
                            inputWorkbook = new File(baseDataDir,  UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xlsDataFilePath));
                            if (inputWorkbook.createNewFile()) 
                            {
                                Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
                            }
                            } 
                        catch (IOException ioe) 
                        {
                                Debug.logError(ioe, module);
                        } catch (Exception exc) 
                            {
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
        
        String bigfishXmlFile = UtilDateTime.nowAsString()+".xml";
            
        String importDataPath = System.getProperty("ofbiz.home") + "/runtime/tmp/upload/bigfishXmlFile/";
        
        if (!new File(importDataPath).exists()) 
        {
            new File(importDataPath).mkdirs();
        }
        
        File tempFile = new File(importDataPath, "temp" + bigfishXmlFile);
        
        
        // ######################################
        //read the temp xls file and generate Bigfish xml 
        // ######################################
        if (inputWorkbook != null && baseDataDir  != null) 
        {
            try 
            {

                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("en", "EN"));
                Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                
                ObjectFactory factory = new ObjectFactory();
                
                BigFishProductFeedType bfProductFeedType = factory.createBigFishProductFeedType();
                
                // Gets the sheets from workbook
                for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
                {
                    BufferedWriter bw = null; 
                    try 
                    {
                        Sheet s = wb.getSheet(sheet);
                        String sTabName=s.getName();
                        
                        if (sheet == 1)
                        {
                            List dataRows = buildDataRows(buildCategoryHeader(),s);
                            ProductCategoryType productCategoryType = factory.createProductCategoryType();
                	        List productCategoryList =  productCategoryType.getCategory();
                	        createProductCategoryXmlFromXls(factory, productCategoryList, dataRows);
                	  	    bfProductFeedType.setProductCategory(productCategoryType);
                        }
                        if (sheet == 2)
                        {
                            List dataRows = buildDataRows(buildProductHeader(),s);
                            ProductsType productsType = factory.createProductsType();
                	  	    List productList = productsType.getProduct();
                	  	    createProductXmlFromXls(factory, productList, dataRows);
                	  	    bfProductFeedType.setProducts(productsType);
                        }
                        if (sheet == 3)
                        {
                            List dataRows = buildDataRows(buildProductAssocHeader(),s);
                            ProductAssociationType productAssociationType = factory.createProductAssociationType();
                	  	    List productAssocList = productAssociationType.getAssociation();
                	  	    createProductAssocXmlFromXls(factory, productAssocList, dataRows);
                	  	    bfProductFeedType.setProductAssociation(productAssociationType);
                        }
                        if (sheet == 4)
                        {
                            List dataRows = buildDataRows(buildProductFeatureSwatchHeader(),s);
                            ProductFeatureSwatchType productFeatureSwatchType = factory.createProductFeatureSwatchType();
                	  	    List featureList = productFeatureSwatchType.getFeature();
                	  	    createProductFeatureSwatchXmlFromXls(factory, featureList, dataRows);
                	  	    bfProductFeedType.setProductFeatureSwatch(productFeatureSwatchType);
                        }
                        if (sheet == 5)
                        {
                            List dataRows = buildDataRows(buildManufacturerHeader(),s);
                            ProductManufacturerType productManufacturerType = factory.createProductManufacturerType();
                	  	    List manufacturerList = productManufacturerType.getManufacturer();
                	  	    createProductManufacturerXmlFromXls(factory, manufacturerList, dataRows);
                	  	    bfProductFeedType.setProductManufacturer(productManufacturerType);
                        }

                        //File to store data in form of CSV
                    } catch (Exception exc) {
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
                
                Map<String, Object> importClientProductTemplateCtx = null;
                Map result  = FastMap.newInstance();
                importClientProductTemplateCtx = UtilMisc.toMap("xmlDataFile", renameFile.toString(), "xmlDataDir", xmlDataDirPath,"productLoadImagesDir", loadImagesDirPath, "imageUrl", imageUrl, "removeAll",removeAll,"autoLoad",autoLoad,"userLogin",userLogin,"productStoreId",productStoreId);
                result = dispatcher.runSync("importClientProductXMLTemplate", importClientProductTemplateCtx);
                if(UtilValidate.isNotEmpty(result.get("responseMessage")) && result.get("responseMessage").equals("error"))
                {
               	    return ServiceUtil.returnError(result.get("errorMessage").toString());
                }
                messages = (List)result.get("messages");

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
    
    public static Map<String, Object> exportProductXls(DispatchContext ctx, Map<String, ?> context) {
        _delegator = ctx.getDelegator();
        _dispatcher = ctx.getDispatcher();
        _locale = (Locale) context.get("locale");
        List<String> messages = FastList.newInstance();

        String productStoreId = (String) context.get("productStoreId");
        String browseRootProductCategoryId = (String) context.get("browseRootProductCategoryId");
        String isSampleFile = (String) context.get("sampleFile");
        String fileName="clientProductImport.xls";

        WritableWorkbook workbook = null;
        
       try {
    	        if (UtilValidate.isNotEmpty(isSampleFile) && isSampleFile.equals("Y"))
    	        {
    	        	fileName="sampleClientProductImport.xls";
    	        }
                String importDataPath = FlexibleStringExpander.expandString(OSAFE_ADMIN_PROP.getString("ecommerce-import-data-path"),context);
                File file = new File(importDataPath, "temp" + fileName);
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));
                workbook = Workbook.createWorkbook(file, wbSettings);
                int iRows=0;
                Map mWorkBookHeadCaptions = createWorkBookHeaderCaptions();

                WritableSheet excelSheetModHistory = createWorkBookSheet(workbook,"Mod History", 0);
            	createWorkBookHeaderRow(excelSheetModHistory, buildModHistoryHeader(),mWorkBookHeadCaptions);
            	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 1);
            	createWorkBookRow(excelSheetModHistory, "system", 1, 1);
            	createWorkBookRow(excelSheetModHistory, "Auto Generated Product Import Template", 2, 1);
            	
            	WritableSheet excelSheetCategory = createWorkBookSheet(workbook,"Category", 1);
            	createWorkBookHeaderRow(excelSheetCategory, buildCategoryHeader(),mWorkBookHeadCaptions);
            	
                WritableSheet excelSheetProduct = createWorkBookSheet(workbook,"Product", 2);
            	createWorkBookHeaderRow(excelSheetProduct, buildProductHeader(),mWorkBookHeadCaptions);

                WritableSheet excelSheetProductAssoc = createWorkBookSheet(workbook,"Product Association", 3);
            	createWorkBookHeaderRow(excelSheetProductAssoc, buildProductAssocHeader(),mWorkBookHeadCaptions);
            	
                WritableSheet excelSheetFeatureSwatches = createWorkBookSheet(workbook,"Feature Swatches", 4);
            	createWorkBookHeaderRow(excelSheetFeatureSwatches, buildProductFeatureSwatchHeader(),mWorkBookHeadCaptions);

            	WritableSheet excelSheetManufacturer = createWorkBookSheet(workbook,"Manufacturer", 5);
            	createWorkBookHeaderRow(excelSheetManufacturer, buildManufacturerHeader(),mWorkBookHeadCaptions);

    	        if (UtilValidate.isNotEmpty(isSampleFile) && isSampleFile.equals("Y"))
    	        {
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 2);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 2);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Product Categories Generated", 2, 2);
    	        	
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 3);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 3);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Products Generated", 2, 3);
                	
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 4);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 4);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Product Associations Generated", 2, 4);
                	
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 5);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 5);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Feature Swatches Generated", 2, 5);

                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 6);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 6);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Manufacturers Generated", 2, 6);
                	
    	        }
    	        else
    	        {
                	iRows = createProductCategoryWorkSheet(excelSheetCategory, browseRootProductCategoryId);
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 2);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 2);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Product Categories Generated", 2, 2);
    	        	
                	iRows = createProductWorkSheet(excelSheetProduct, browseRootProductCategoryId);
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 3);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 3);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Products Generated", 2, 3);

                	iRows = createProductAssocWorkSheet(excelSheetProductAssoc, browseRootProductCategoryId);
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 4);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 4);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Product Associations Generated", 2, 4);

                	iRows = createFeatureSwatchesWorkSheet(excelSheetFeatureSwatches, browseRootProductCategoryId);
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 5);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 5);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Feature Swatches Generated", 2, 5);

                	iRows = createManufacturerWorkSheet(excelSheetManufacturer, browseRootProductCategoryId);
                	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 6);
                	createWorkBookRow(excelSheetModHistory, "system", 1, 6);
                	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Manufacturers Generated", 2, 6);
    	        }
    	        
            	workbook.write();
                workbook.close();
                
                new File(importDataPath, fileName).delete();
                File renameFile =new File(importDataPath, fileName);
                RandomAccessFile out = new RandomAccessFile(renameFile, "rw");
		        InputStream inputStr = new FileInputStream(file);
		        byte[] bytes = new byte[102400];
		        int bytesRead;
		        while ((bytesRead = inputStr.read(bytes)) != -1)
		        {
		            out.write(bytes, 0, bytesRead);
		        }
		        out.close();
		        inputStr.close();
                
                // Gets the sheets from workbook

       }catch (Exception exc) 
        {
                Debug.logError(exc, module);
        }
        finally 
        {
            if (workbook != null) 
            {
                try {
                    workbook.close();
                } catch (Exception exc) {
                    //Debug.warning();
                }
            }
        }
      
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
        
    }   

    private static void writeXmlHeader(BufferedWriter bfOutFile) {
    	try {
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
    private static void writeXmlFooter(BufferedWriter bfOutFile) {
    	try {
    		bfOutFile.write("</entity-engine-xml>");
            bfOutFile.flush();
            bfOutFile.close();
    		
    	}
    	 catch (Exception e)
    	 {
    	 }
    }

    private static List createWorkBookHeaderRow(WritableSheet excelSheet,List headerCols,Map headerCaptions) {
    	
    	try {
            CellView cv = new CellView();
            cv.setAutosize(true);
            WritableFont headerFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont,NumberFormats.TEXT);
            headerFormat.setBackground(Colour.DARK_BLUE);
            int row=0;
            WritableCellFormat textFormat = new WritableCellFormat(NumberFormats.TEXT);
            cv.setFormat(textFormat);
            for (int colCount = 0; colCount < headerCols.size(); colCount++) 
            {
              String headerCaption = (String)headerCaptions.get(headerCols.get(colCount));
              if (UtilValidate.isEmpty(headerCaption))
              {
            	  headerCaption = headerCols.get(colCount).toString();
              }
              Label label  = new Label(colCount, row,headerCaption, headerFormat);
              excelSheet.addCell(label);
              cv.setSize(headerCaption.length());
              excelSheet.setColumnView(colCount, cv);
            }
            
    		
    		
    	} catch (Exception e) {
            Debug.logError(e, module);

    	}
   	    return headerCols;
        
       }
    
    private static void createWorkBookRow(WritableSheet excelSheet,Object rowValue,int colIdx,int rowIdx) {
    	String rowContent="";
        CellView cv = new CellView();
        cv.setAutosize(true);
       	try 
        {
    		if (UtilValidate.isNotEmpty(rowValue) && !(rowValue.toString().equals("null")))
    		{
    			rowContent = rowValue.toString();
    		}
    		else
    		{
    			rowContent="";
    		}
            
            Label label  = new Label(colIdx, rowIdx, rowContent, cellFormat);
            excelSheet.addCell(label);
            cv.setSize(rowContent.length());
            excelSheet.setColumnView(colIdx, cv);
    	}
       	catch (Exception e) 
        {
            Debug.logError(e, module);

    	}
        
       }
    private static WritableSheet createWorkBookSheet(WritableWorkbook workbook,String sheetName,int sheetIdx) {

    	WritableSheet excelSheet=null;    	
    	try 
    	{
    		
            workbook.createSheet(sheetName,sheetIdx);
            excelSheet=workbook.getSheet(sheetIdx);
    		
    	} 
    	catch (Exception e) 
    	{
            Debug.logError(e, module);

    	}
   	    return excelSheet;
        
       }
    
    private static Map createWorkBookHeaderCaptions() {
        Map headerCols = FastMap.newInstance();
   	    headerCols.put("masterProductId","Master Product ID");
   	    headerCols.put("productId","Product ID");
   	    headerCols.put("productCategoryId","Category ID");
   	    headerCols.put("parentCategoryId","Parent Category Id");
   	    headerCols.put("categoryName","Category Name");
   	    headerCols.put("description","Description");
   	    headerCols.put("plpImageName","PLP Image Name");
   	    headerCols.put("plpText","Additional PLP Text");
   	    headerCols.put("pdpText","Additional PDP Text");
   	    headerCols.put("productIdTo","Product Id To");
   	    headerCols.put("productAssocType","Product Association Type");   	    
   	    headerCols.put("internalName","Internal Name");
   	    headerCols.put("productName","Product Name");
   	    headerCols.put("salesPitch","Sales Pitch");
   	    headerCols.put("longDescription","Long Description");
   	    headerCols.put("specialInstructions","Special Instr");
   	    headerCols.put("deliveryInfo","Delivery Info");
   	    headerCols.put("directions","Directions");
   	    headerCols.put("termsConditions","Terms & Cond");
   	    headerCols.put("ingredients","Ingredients");
   	    headerCols.put("warnings","Warnings");
   	    headerCols.put("plpLabel","PLP Label");
   	    headerCols.put("pdpLabel","PDP Label");
   	    headerCols.put("listPrice","List Price");
   	    headerCols.put("defaultPrice","Sales Price");
   	    headerCols.put("selectabeFeature_1","Selectable Features #1");
   	    headerCols.put("plpSwatchImage","Product Swatch Image for PLP [SWATCH_IMAGE]");
   	    headerCols.put("pdpSwatchImage","Product Swatch Image for PDP [SWATCH_IMAGE]");
   	    headerCols.put("selectabeFeature_2","Selectable Features #2");
   	    headerCols.put("selectabeFeature_3","Selectable Features #3");
   	    headerCols.put("selectabeFeature_4","Selectable Features #4");
	    headerCols.put("selectabeFeature_5","Selectable Features #5");
   	    headerCols.put("descriptiveFeature_1","Descriptive Features #1");
   	    headerCols.put("descriptiveFeature_2","Descriptive Features #2");
   	    headerCols.put("descriptiveFeature_3","Descriptive Features #3");
   	    headerCols.put("descriptiveFeature_4","Descriptive Features #4");
	    headerCols.put("descriptiveFeature_5","Descriptive Features #5");
   	    headerCols.put("smallImage","PLP Image");
   	    headerCols.put("smallImageAlt","PLP Image Alt");
   	    headerCols.put("thumbImage","PDP Thumbnail Image");
   	    headerCols.put("largeImage","PDP Regular Image");
   	    headerCols.put("detailImage","PDP Large Image");
   	    headerCols.put("addImage1","PDP Alt-1 Thumbnail Image");
   	    headerCols.put("xtraLargeImage1","PDP Alt-1 Regular Image");
   	    headerCols.put("xtraDetailImage1","PDP Alt-1 Large Image");
   	    headerCols.put("addImage2","PDP Alt-2 Thumbnail Image");
   	    headerCols.put("xtraLargeImage2","PDP Alt-2 Regular Image");
   	    headerCols.put("xtraDetailImage2","PDP Alt-2 Large Image");
   	    headerCols.put("addImage3","PDP Alt-3 Thumbnail Image");
   	    headerCols.put("xtraLargeImage3","PDP Alt-3 Regular Image");
   	    headerCols.put("xtraDetailImage3","PDP Alt-3 Large Image");
   	    headerCols.put("addImage4","PDP Alt-4 Thumbnail Image");
   	    headerCols.put("xtraLargeImage4","PDP Alt-4 Regular Image");
   	    headerCols.put("xtraDetailImage4","PDP Alt-4 Large Image");
   	    headerCols.put("addImage5","PDP Alt-5 Thumbnail Image");
   	    headerCols.put("xtraLargeImage5","PDP Alt-5 Regular Image");
   	    headerCols.put("xtraDetailImage5","PDP Alt-5 Large Image");
   	    headerCols.put("addImage6","PDP Alt-6 Thumbnail Image");
   	    headerCols.put("xtraLargeImage6","PDP Alt-6 Regular Image");
   	    headerCols.put("xtraDetailImage6","PDP Alt-6 Large Image");
   	    headerCols.put("addImage7","PDP Alt-7 Thumbnail Image");
   	    headerCols.put("xtraLargeImage7","PDP Alt-7 Regular Image");
   	    headerCols.put("xtraDetailImage7","PDP Alt-7 Large Image");
   	    headerCols.put("addImage8","PDP Alt-8 Thumbnail Image");
   	    headerCols.put("xtraLargeImage8","PDP Alt-8 Regular Image");
   	    headerCols.put("xtraDetailImage8","PDP Alt-8 Large Image");
   	    headerCols.put("addImage9","PDP Alt-9 Thumbnail Image");
   	    headerCols.put("xtraLargeImage9","PDP Alt-9 Regular Image");
   	    headerCols.put("xtraDetailImage9","PDP Alt-9 Large Image");
   	    headerCols.put("addImage10","PDP Alt-10 Thumbnail Image");
   	    headerCols.put("xtraLargeImage10","PDP Alt-10 Regular Image");
   	    headerCols.put("xtraDetailImage10","PDP Alt-10 Large Image");
   	    headerCols.put("productHeight","Product Height");
   	    headerCols.put("productWidth","Product Width");
   	    headerCols.put("productDepth","Product Depth");
   	    headerCols.put("returnable","Returnable");
   	    headerCols.put("taxable","Taxable");
   	    headerCols.put("chargeShipping","Charge Shipping");
   	    headerCols.put("introDate","Introduction Date");
   	    headerCols.put("discoDate","Discontinued Date");
   	    headerCols.put("manufacturerId","Manufacturer ID");
   	    headerCols.put("partyId","Manufacturer ID");
   	    headerCols.put("date","Date");
   	    headerCols.put("who","Who");
   	    headerCols.put("changes","Changes");
   	    headerCols.put("manufacturerName","Name");
   	    headerCols.put("address1","Address");
   	    headerCols.put("city","City/Town");
   	    headerCols.put("state","State/Province");
   	    headerCols.put("zip","ZipPostCode");
   	    headerCols.put("country","Country");
   	    headerCols.put("shortDescription","Short Description");
   	    headerCols.put("manufacturerImage","Manufacturer Image");
   	    headerCols.put("featureId","Feature");
   	    headerCols.put("plpSwatchImage","PLP Swatch Image");
   	    headerCols.put("pdpSwatchImage","PDP Swatch Image");
   	    headerCols.put("goodIdentificationSkuId","SKU#");
   	    headerCols.put("goodIdentificationGoogleId","Google-ID");
   	    headerCols.put("goodIdentificationIsbnId","ISBN");
   	    headerCols.put("goodIdentificationManufacturerId","Manufacturer Number");
   	    headerCols.put("pdpVideoUrl","Product Video");
   	    headerCols.put("pdpVideo360Url","Product 360 Video");
   	    headerCols.put("sequenceNum","Sequence Number");
	    headerCols.put("bfInventoryTot","BF Inventory Total");
	    headerCols.put("bfInventoryWhs","BF Inventory Warehouse");
	    headerCols.put("multiVariant","PDP Select Multi Variant");
	    headerCols.put("weight","Product Weight");
	    headerCols.put("giftMessage","Check Out Gift Message");
	    headerCols.put("pdpQtyMin","PDP Min Quantity");
	    headerCols.put("pdpQtyMax","PDP Max Quantity");
	    headerCols.put("pdpQtyDefault","PDP Default Quantity");
   	    return headerCols;
    }
    
    private static int createProductCategoryWorkSheet(WritableSheet excelSheet,String browseRootProductCategoryId) {
        int iRowIdx=1;
    	try {
    		
    		List<GenericValue> topLavelCategoryList =  _delegator.findByAnd("ProductCategoryRollupAndChild", UtilMisc.toMap("parentProductCategoryId",browseRootProductCategoryId),UtilMisc.toList("sequenceNum"));
    		//topLavelCategoryList = EntityUtil.filterByDate(topLavelCategoryList);
            GenericValue workingCategory = null;
            GenericValue workingCategoryRollup = null;
            String productCategoryIdPath = null;
            int iColIdx=0;
            String contentValue=null;
            Timestamp tsstamp=null;
            List<String> pathElements=null;
            String categoryImageURL=null;
            for (GenericValue topLavelCategory : topLavelCategoryList) 
            {
            	iColIdx=0;
                if ("CATALOG_CATEGORY".equals(topLavelCategory.getString("productCategoryTypeId"))) 
                {
                    String productCategoryId = (String) topLavelCategory.getString("productCategoryId");
        	        List<GenericValue> lCategoryContent = _delegator.findByAnd("ProductCategoryContent", UtilMisc.toMap("productCategoryId",productCategoryId),UtilMisc.toList("-fromDate"));
        	        lCategoryContent=EntityUtil.filterByDate(lCategoryContent, UtilDateTime.nowTimestamp());
                    createWorkBookRow(excelSheet,topLavelCategory.getString("productCategoryId"), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,topLavelCategory.getString("parentProductCategoryId"),iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,topLavelCategory.getString("categoryName"),iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,topLavelCategory.getString("description"),iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,topLavelCategory.getString("longDescription"),iColIdx++, iRowIdx);
                    categoryImageURL =topLavelCategory.getString("categoryImageUrl");
                    
                    if (UtilValidate.isNotEmpty(categoryImageURL))
                    {
                    	if (!UtilValidate.isUrl(categoryImageURL))
                    	{
                    		String categoryImagePath = getOsafeImagePath("CATEGORY_IMAGE_URL");
                    		pathElements = StringUtil.split(categoryImageURL, "/");
                            createWorkBookRow(excelSheet,categoryImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
                    	}
                    	else
                    	{
                    		createWorkBookRow(excelSheet,categoryImageURL, iColIdx++, iRowIdx);
                    	}
                        
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
                    createWorkBookRow(excelSheet, getProductCategoryContent(productCategoryId,"PLP_ESPOT_CONTENT",lCategoryContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductCategoryContent(productCategoryId,"PDP_ADDITIONAL",lCategoryContent), iColIdx++, iRowIdx);
                    tsstamp = topLavelCategory.getTimestamp("fromDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                        createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                    }
                    tsstamp = topLavelCategory.getTimestamp("thruDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                        createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                    }
                    iRowIdx++;
                    List<GenericValue> subLavelCategoryList =  _delegator.findByAnd("ProductCategoryRollupAndChild", UtilMisc.toMap("parentProductCategoryId",productCategoryId),UtilMisc.toList("sequenceNum"));
                    //subLavelCategoryList = EntityUtil.filterByDate(subLavelCategoryList);
                    for (GenericValue subLavelCategory : subLavelCategoryList) 
                    {
                    	iColIdx=0;
                        if ("CATALOG_CATEGORY".equals(subLavelCategory.getString("productCategoryTypeId"))) 
                        {
                            productCategoryId = (String) subLavelCategory.getString("productCategoryId");
                	        lCategoryContent = _delegator.findByAnd("ProductCategoryContent", UtilMisc.toMap("productCategoryId",productCategoryId),UtilMisc.toList("-fromDate"));
                	        lCategoryContent=EntityUtil.filterByDate(lCategoryContent, UtilDateTime.nowTimestamp());
                            createWorkBookRow(excelSheet,subLavelCategory.getString("productCategoryId"), iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,subLavelCategory.getString("parentProductCategoryId"),iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,subLavelCategory.getString("categoryName"),iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,subLavelCategory.getString("description"),iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,subLavelCategory.getString("longDescription"),iColIdx++, iRowIdx);
                            categoryImageURL =subLavelCategory.getString("categoryImageUrl");
                            
                            if (UtilValidate.isNotEmpty(categoryImageURL))
                            {
                            	if (!UtilValidate.isUrl(categoryImageURL))
                            	{
                            		String categoryImagePath = getOsafeImagePath("CATEGORY_IMAGE_URL");
                            		pathElements = StringUtil.split(categoryImageURL, "/");
                                    createWorkBookRow(excelSheet,categoryImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
                            	}
                            	else
                            	{
                            		createWorkBookRow(excelSheet,categoryImageURL, iColIdx++, iRowIdx);
                            	}
                                
                            }
                            else
                            {
                                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                            	
                            }
                            createWorkBookRow(excelSheet, getProductCategoryContent(productCategoryId,"PLP_ESPOT_CONTENT",lCategoryContent), iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet, getProductCategoryContent(productCategoryId,"PDP_ADDITIONAL",lCategoryContent), iColIdx++, iRowIdx);
                            tsstamp = subLavelCategory.getTimestamp("fromDate");
                            if (UtilValidate.isNotEmpty(tsstamp))
                            {
                                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                            }
                            else
                            {
                                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                            }
                            tsstamp = subLavelCategory.getTimestamp("thruDate");
                            if (UtilValidate.isNotEmpty(tsstamp))
                            {
                                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                            }
                            else
                            {
                                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                            }
                            iRowIdx++;
                        }
                  
                    }
                }
            }
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    	return (iRowIdx -1);
    }
    
    
    private static int createProductCategoryWorkSheetFromEbay(WritableSheet excelSheet,String browseRootProductCategoryId,List dataRows) {
        int iRowIdx=1;
    	try {
    		int iColIdx=0;
            HashMap productCategoryExists = new HashMap();
            for (int i=0 ; i < dataRows.size() ; i++) 
            {
            	Map mRow = (Map)dataRows.get(i);
            	iColIdx=0;
                String productCategoryId = (String)mRow.get("ebayCategoryList");
                String productCategoryDescription = (String)mRow.get("attribute7Value");
                if (UtilValidate.isNotEmpty(productCategoryId) && !productCategoryExists.containsKey(productCategoryId) && !productCategoryExists.containsValue(productCategoryDescription))
                {
                    productCategoryExists.put(productCategoryId, productCategoryDescription);
                    createWorkBookRow(excelSheet,productCategoryId, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,browseRootProductCategoryId,iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,productCategoryDescription,iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,productCategoryDescription,iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,productCategoryDescription,iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                    iRowIdx++;
                }
            }
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    	return (iRowIdx -1);
    }

    private static int createProductWorkSheet(WritableSheet excelSheet,String browseRootProductCategoryId) {
        int iRowIdx=1;
    	try {
    		
            List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, true, false, true);
            GenericValue workingCategory = null;
            String productCategoryIdPath = null;
            int categoryLevel = 0;
            List<String> categoryTrail = null;
            int iColIdx=0;
            String contentValue=null;
            Timestamp tsstamp=null;
            List<String> pathElements=null;
            String imageURL=null;
            String productPrice="";
            String productId="";
            HashMap productExists = new HashMap();
            
            
            Map productFeatureTypesMap = FastMap.newInstance();
            List<GenericValue> productFeatureTypesList = _delegator.findList("ProductFeatureType", null, null, null, null, false);
            
            //get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
            List productFeatureGroupList = _delegator.findList("ProductFeatureGroup", null, null, null, null, false);
            List productFeatureGroupAndApplList = _delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, false);
            productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);
            
            if(UtilValidate.isNotEmpty(productFeatureTypesList))
            {
                for (GenericValue productFeatureType : productFeatureTypesList)
                {
                    //filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
                	List productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.getString("productFeatureTypeId")));
                    String description = "";
                    if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
                    {
                        GenericValue productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
                        List productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.getString("productFeatureGroupId")));
                        GenericValue productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
                        description = productFeatureGroup.getString("description");
                    }
                    else
                    {
                        description = productFeatureType.getString("description");
                    }
                    productFeatureTypesMap.put(productFeatureType.getString("productFeatureTypeId"),description);
                }
                
            }
            
            for (Map<String, Object> workingCategoryMap : productCategories) 
            {
                workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
                List<GenericValue> productCategoryMembers = workingCategory.getRelated("ProductCategoryMember");
                // Remove any expired
                productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                for (GenericValue productCategoryMember : productCategoryMembers) 
                {
                	iColIdx=0;
                    GenericValue product = productCategoryMember.getRelatedOne("Product");
                    productId = product.getString("productId");
                    
                    if (UtilValidate.isNotEmpty(product) && !productExists.containsKey(productId))
                    {
                        productExists.put(productId, productId);
                    	String isVariant = product.getString("isVariant");
                        if (UtilValidate.isEmpty(isVariant)) {
                            isVariant = "N";
                        }
                        if ("N".equals(isVariant)) 
                        {
                	        List<GenericValue> productAssocitations = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"),UtilMisc.toList("sequenceNum"));
                	        if (UtilValidate.isNotEmpty(productAssocitations))
                	        {
                	        	boolean bFirstVariant = false;
                	        	addWorkSheetProductRow(excelSheet,product,iRowIdx, productFeatureTypesMap);
                	        	iRowIdx++;
                                for (GenericValue productAssoc : productAssocitations) 
                                {
                                    GenericValue variantProduct = productAssoc.getRelatedOne("AssocProduct");
                                	if (!bFirstVariant)
                                	{
                                		addWorkSheetProductVariantRow(excelSheet,variantProduct,productId,iRowIdx,bFirstVariant, productFeatureTypesMap);
                                		iRowIdx++;
                                	}
                                }
                	        	
                	        }
                	        else
                	        {
                	        	addWorkSheetProductRow(excelSheet,product,iRowIdx, productFeatureTypesMap);
                	        	iRowIdx++;
                	        }
                        }
                    	
                    }               
                }
            }
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    	return (iRowIdx -1);
    }
    

    private static void addWorkSheetProductVariantRow(WritableSheet excelSheet,GenericValue variantProduct, String productId, int iRowIdx, boolean bFirstVariant, Map productFeatureTypesMap) 
    {
    	int iColIdx=0;
    	List<String> pathElements=null;
    	String imageURL=null;
    	try 
    	{
    		String variantProductId=variantProduct.getString("productId");
        	createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
        	createWorkBookRow(excelSheet,variantProductId,iColIdx++, iRowIdx);
        	if (bFirstVariant)
        	{
        		iColIdx=iColIdx + 1;
        	}
        	else
        	{
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        	}
        	
        	String internalName=variantProduct.getString("internalName");
            if(UtilValidate.isNotEmpty(internalName))
            {
                createWorkBookRow(excelSheet,internalName, iColIdx++, iRowIdx);
    	    }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
        	
    		String variantProductName=variantProduct.getString("productName");
            if(UtilValidate.isNotEmpty(variantProductName))
            {
                createWorkBookRow(excelSheet,variantProductName, iColIdx++, iRowIdx);
    	    }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }

            List<GenericValue> lProductContent = _delegator.findByAnd("ProductContent", UtilMisc.toMap("productId",variantProductId),UtilMisc.toList("-fromDate"));
            lProductContent=EntityUtil.filterByDate(lProductContent, UtilDateTime.nowTimestamp());
            
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"SHORT_SALES_PITCH",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"LONG_DESCRIPTION",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"SPECIALINSTRUCTIONS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"DELIVERY_INFO",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"DIRECTIONS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"TERMS_AND_CONDS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"INGREDIENTS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"WARNINGS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"PLP_LABEL",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(variantProductId,"PDP_LABEL",lProductContent), iColIdx++, iRowIdx);
            
            String productPrice = "";
            List productPriceList = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", variantProductId, "productPriceTypeId", "LIST_PRICE"));
            if(UtilValidate.isNotEmpty(productPriceList))
            {
            	productPriceList = EntityUtil.filterByDate(productPriceList);
    	        if(UtilValidate.isNotEmpty(productPriceList))
    	        {
    	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
    	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
    	        }
    	    }
            createWorkBookRow(excelSheet,productPrice, iColIdx++, iRowIdx);
            productPrice="";
            productPriceList = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",variantProductId, "productPriceTypeId", "DEFAULT_PRICE"));
            if(UtilValidate.isNotEmpty(productPriceList))
            {
            	productPriceList = EntityUtil.filterByDate(productPriceList);
    	        if(UtilValidate.isNotEmpty(productPriceList))
    	        {
    	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
    	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
    	        }
    	    }
            createWorkBookRow(excelSheet,productPrice, iColIdx++, iRowIdx);
            
            List<GenericValue> productSelectableFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", variantProductId, "productFeatureApplTypeId", "STANDARD_FEATURE"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
            productSelectableFeatures = EntityUtil.filterByDate(productSelectableFeatures);
            
            int iSelectFeatureIdx=1;
            Map mSelectFeature = FastMap.newInstance();
            for (GenericValue productSelectableFeature : productSelectableFeatures) 
            {
            	if (iSelectFeatureIdx < 6)
            	{
            		String productFeatureTypeDesc = (String) productFeatureTypesMap.get(productSelectableFeature.getString("productFeatureTypeId"));
            		if(UtilValidate.isEmpty(productFeatureTypeDesc))
            		{
            			productFeatureTypeDesc = productSelectableFeature.getString("productFeatureTypeId");
            		}
            		mSelectFeature.put("selectFeature_" + iSelectFeatureIdx, productFeatureTypeDesc + ":" + productSelectableFeature.getString("description"));
            	}
            	iSelectFeatureIdx++;
            }
            
            for(int i = 1; i < 2; i++) 
            {
            	String selectFeature = (String)mSelectFeature.get("selectFeature_"+i);
                if (UtilValidate.isNotEmpty(selectFeature))
                {
                    createWorkBookRow(excelSheet,selectFeature, iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                }
            }
            
            imageURL =getProductContent(variantProductId,"PLP_SWATCH_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String plpSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,plpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(variantProductId,"PDP_SWATCH_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,pdpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            for(int i = 2; i < 6; i++) 
            {
            	String selectFeature = (String)mSelectFeature.get("selectFeature_"+i);
                if (UtilValidate.isNotEmpty(selectFeature))
                {
                    createWorkBookRow(excelSheet,selectFeature, iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                }
            }
            
            List<GenericValue> productDistinguishFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", variantProductId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
            productDistinguishFeatures = EntityUtil.filterByDate(productDistinguishFeatures);
            
            
            Map<String, String> mDescriptiveFeatureTypeMap = FastMap.newInstance();
	        for (GenericValue productDistinguishFeature : productDistinguishFeatures) 
            {
	        	if(mDescriptiveFeatureTypeMap.containsKey(productDistinguishFeature.getString("productFeatureTypeId")))
            	{
	        		mDescriptiveFeatureTypeMap.put(productDistinguishFeature.getString("productFeatureTypeId"), mDescriptiveFeatureTypeMap.get(productDistinguishFeature.getString("productFeatureTypeId"))+ ", " +productDistinguishFeature.getString("description"));
            	}
            	else
            	{
            		mDescriptiveFeatureTypeMap.put(productDistinguishFeature.getString("productFeatureTypeId"), productDistinguishFeature.getString("description"));	
            	}
            }
	        int iDescriptiveFeatureIdx=1;
            Map<String, String> mDescriptiveFeature = FastMap.newInstance();
	        if(UtilValidate.isNotEmpty(mDescriptiveFeatureTypeMap))
            {
            	for (Map.Entry<String, String> entry : mDescriptiveFeatureTypeMap.entrySet()) 
            	{
            		if (iDescriptiveFeatureIdx < 6)
                	{
            			String productFeatureTypeDesc = (String) productFeatureTypesMap.get(entry.getKey());
                		if(UtilValidate.isEmpty(productFeatureTypeDesc))
                		{
                			productFeatureTypeDesc = entry.getKey();
                		}
                		mDescriptiveFeature.put("descriptiveFeature_" + iDescriptiveFeatureIdx, productFeatureTypeDesc + ":" + entry.getValue());
                	}
                	iDescriptiveFeatureIdx++;
            	}
            }
            
            for(int i = 1; i < 6; i++) 
            {
            	String descriptiveFeature = (String)mDescriptiveFeature.get("descriptiveFeature_"+i);
                if (UtilValidate.isNotEmpty(descriptiveFeature))
                {
                    createWorkBookRow(excelSheet,descriptiveFeature, iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                }
            }
            
            imageURL =getProductContent(variantProductId,"SMALL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String smallImagePath = getOsafeImagePath("SMALL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,smallImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(variantProductId,"SMALL_IMAGE_ALT_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String smallAltImagePath = getOsafeImagePath("SMALL_IMAGE_ALT_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,smallAltImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(variantProductId,"THUMBNAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String thumbnailImagePath = getOsafeImagePath("THUMBNAIL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,thumbnailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(variantProductId,"LARGE_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String largeImagePath = getOsafeImagePath("LARGE_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,largeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(variantProductId,"DETAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String detailImagePath = getOsafeImagePath("DETAIL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,detailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            for (int i=1; i < 11; i++)
            {
                imageURL =getProductContent(variantProductId,"ADDITIONAL_IMAGE_" + i,lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalImagePath = getOsafeImagePath("ADDITIONAL_IMAGE_" + i);
                		pathElements = StringUtil.split(imageURL, "/");
                        createWorkBookRow(excelSheet,additionalImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
                	}
                	else
                	{
                		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
                	}
                    
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(variantProductId,"XTRA_IMG_" + i +"_LARGE",lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalLargeImagePath = getOsafeImagePath("XTRA_IMG_" + i +"_LARGE");
                		pathElements = StringUtil.split(imageURL, "/");
                        createWorkBookRow(excelSheet,additionalLargeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);                		
                	}
                	else
                	{
                		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
                	}
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(variantProductId,"XTRA_IMG_" + i + "_DETAIL",lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalDetailImagePath = getOsafeImagePath("XTRA_IMG_" + i + "_DETAIL");
                		pathElements = StringUtil.split(imageURL, "/");
                        createWorkBookRow(excelSheet,additionalDetailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
                	}
                	else
                	{
                		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
                	}
                    
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
            	
            }
            
            BigDecimal productHeight =variantProduct.getBigDecimal("productHeight");
            if (UtilValidate.isNotEmpty(productHeight))
            {
            	
                createWorkBookRow(excelSheet,_df.format(productHeight) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
            }
            
            BigDecimal productWidth =variantProduct.getBigDecimal("productWidth");
            if (UtilValidate.isNotEmpty(productWidth))
            {
            	
                createWorkBookRow(excelSheet,_df.format(productWidth) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
            }
            
            BigDecimal productDepth =variantProduct.getBigDecimal("productDepth");
            if (UtilValidate.isNotEmpty(productDepth))
            {
            	
                createWorkBookRow(excelSheet,_df.format(productDepth) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
            }
            
            if (bFirstVariant)
        	{
        		iColIdx=iColIdx +3;
        	} else {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        	}
            
            Timestamp tsstamp = variantProduct.getTimestamp("introductionDate");
            if (UtilValidate.isNotEmpty(tsstamp))
            {
            	
                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
            }
            tsstamp = variantProduct.getTimestamp("salesDiscontinuationDate");
            if (UtilValidate.isNotEmpty(tsstamp))
            {
            	
                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
            }
            
            if (bFirstVariant)
        	{
        		iColIdx=iColIdx + 1;
        	} else {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        	}
            
            List<GenericValue> productGoodIdentifications = _delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", variantProductId),UtilMisc.toList("goodIdentificationTypeId"));
            Map mGoodIdentifications = FastMap.newInstance();
            for (GenericValue productGoodIdentification : productGoodIdentifications) 
            {
            	mGoodIdentifications.put(productGoodIdentification.getString("goodIdentificationTypeId"), productGoodIdentification.getString("idValue"));
            }
            
            String goodIdentification = (String)mGoodIdentifications.get("SKU");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
        	
            goodIdentification = (String)mGoodIdentifications.get("GOOGLE_ID");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            goodIdentification = (String)mGoodIdentifications.get("ISBN");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            goodIdentification = (String)mGoodIdentifications.get("MANUFACTURER_ID_NO");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            imageURL =getProductContent(variantProductId,"PDP_VIDEO_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideoUrlPath = getOsafeImagePath("PDP_VIDEO_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,pdpVideoUrlPath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            
            imageURL =getProductContent(variantProductId,"PDP_VIDEO_360_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideo360UrlPath = getOsafeImagePath("PDP_VIDEO_360_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,pdpVideo360UrlPath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            
            
        	if (bFirstVariant)
        	{
        		iColIdx=iColIdx + 1;
        	} else {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        	}
            GenericValue productAttributeTot = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "BF_INVENTORY_TOT"));
            
            if (UtilValidate.isNotEmpty(productAttributeTot))
            {
                createWorkBookRow(excelSheet,(String)productAttributeTot.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributeWhs = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "BF_INVENTORY_WHS"));
            
            if (UtilValidate.isNotEmpty(productAttributeWhs))
            {
                createWorkBookRow(excelSheet,(String)productAttributeWhs.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributeMulti = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_SELECT_MULTI_VARIANT"));
            
            if (UtilValidate.isNotEmpty(productAttributeMulti))
            {
            	createWorkBookRow(excelSheet,(String)productAttributeMulti.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            BigDecimal weight =variantProduct.getBigDecimal("weight");
            if (UtilValidate.isNotEmpty(weight))
            {
            	
                createWorkBookRow(excelSheet,_df.format(weight) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
            }
            
            GenericValue productAttributeGiftMessage = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "CHECKOUT_GIFT_MESSAGE"));
            
            if (UtilValidate.isNotEmpty(productAttributeGiftMessage))
            {
            	createWorkBookRow(excelSheet,(String)productAttributeGiftMessage.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributePdpQtyMin = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_QTY_MIN"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMin))
            {
            	createWorkBookRow(excelSheet,(String)productAttributePdpQtyMin.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributePdpQtyMax = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_QTY_MAX"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMax))
            {
            	createWorkBookRow(excelSheet,(String)productAttributePdpQtyMax.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributePdpQtyDefault = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_QTY_DEFAULT"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyDefault))
            {
            	createWorkBookRow(excelSheet,(String)productAttributePdpQtyDefault.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
    	}
    	catch (Exception e) 
    	{
            Debug.logError(e, module);
    	}

    }
    
    private static void addWorkSheetProductRow(WritableSheet excelSheet,GenericValue product,int iRowIdx, Map productFeatureTypesMap) {
    	int iColIdx=0;
    	List<String> pathElements=null;
    	String imageURL=null;
    	
    	try {
    		String productId = product.getString("productId");
        	createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
        	
        	if(product.getString("isVirtual").equals("Y")) 
        	{
        		createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);	
        	}
        	else
        	{
        		createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        	}
            List<GenericValue> categoryMembers = product.getRelated("ProductCategoryMember");
            if(UtilValidate.isNotEmpty(categoryMembers))
            {
                categoryMembers = EntityUtil.filterByDate(categoryMembers, true);
            }
            if(UtilValidate.isNotEmpty(categoryMembers))
            {
            	StringBuffer catMembers =new StringBuffer();
                for (GenericValue categoryMember : categoryMembers) 
                {
                	catMembers.append(categoryMember.getString("productCategoryId") + ",");
                }
                catMembers.setLength(catMembers.length() - 1);
                createWorkBookRow(excelSheet,catMembers.toString(), iColIdx++, iRowIdx);
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            List<GenericValue> lProductContent = _delegator.findByAnd("ProductContent", UtilMisc.toMap("productId",productId),UtilMisc.toList("-fromDate"));
            lProductContent=EntityUtil.filterByDate(lProductContent, UtilDateTime.nowTimestamp());
            
        	createWorkBookRow(excelSheet,product.getString("internalName"),iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"PRODUCT_NAME",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"SHORT_SALES_PITCH",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"LONG_DESCRIPTION",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"SPECIALINSTRUCTIONS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"DELIVERY_INFO",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"DIRECTIONS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"TERMS_AND_CONDS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"INGREDIENTS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"WARNINGS",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"PLP_LABEL",lProductContent), iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet, getProductContent(productId,"PDP_LABEL",lProductContent), iColIdx++, iRowIdx);
            String productPrice="";
            List productPriceList = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", "LIST_PRICE"));
            if(UtilValidate.isNotEmpty(productPriceList))
            {
            	productPriceList = EntityUtil.filterByDate(productPriceList);
    	        if(UtilValidate.isNotEmpty(productPriceList))
    	        {
    	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
    	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
    	        }
    	    }
            createWorkBookRow(excelSheet,productPrice, iColIdx++, iRowIdx);
            productPrice="";
            productPriceList = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",productId, "productPriceTypeId", "DEFAULT_PRICE"));
            if(UtilValidate.isNotEmpty(productPriceList))
            {
            	productPriceList = EntityUtil.filterByDate(productPriceList);
    	        if(UtilValidate.isNotEmpty(productPriceList))
    	        {
    	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
    	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
    	        }
    	    }
            createWorkBookRow(excelSheet,productPrice, iColIdx++, iRowIdx);

            String featureProductId = productId;
            //If any virtual have variants then reterive the Features from first variant
            List<GenericValue> productAssocitations = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"),UtilMisc.toList("sequenceNum"));
            GenericValue variantProduct = null;
            
	        if (UtilValidate.isNotEmpty(productAssocitations))
	        {
	        	GenericValue productAssoc = EntityUtil.getFirst(productAssocitations);
	        	variantProduct = productAssoc.getRelatedOne("AssocProduct");
	        	featureProductId = variantProduct.getString("productId");
	        }
            
	        /*List<GenericValue> productSelectableFeatures = FastList.newInstance();
	        if(UtilValidate.isNotEmpty(featureProductId)) 
	        {
	        	productSelectableFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", featureProductId, "productFeatureApplTypeId", "STANDARD_FEATURE"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
	            productSelectableFeatures = EntityUtil.filterByDate(productSelectableFeatures);	
	        }
            
            int iSelectFeatureIdx=1;
            Map mSelectFeature = FastMap.newInstance();
            for (GenericValue productSelectableFeature : productSelectableFeatures) 
            {
            	if (iSelectFeatureIdx < 6)
            	{
            		String productFeatureTypeDesc = (String) productFeatureTypesMap.get(productSelectableFeature.getString("productFeatureTypeId"));
            		if(UtilValidate.isEmpty(productFeatureTypeDesc))
            		{
            			productFeatureTypeDesc = productSelectableFeature.getString("productFeatureTypeId");
            		}
            		mSelectFeature.put("selectFeature_" + iSelectFeatureIdx, productFeatureTypeDesc + ":" + productSelectableFeature.getString("description"));
            	}
            	iSelectFeatureIdx++;
            }
            
            String selectFeature = (String)mSelectFeature.get("selectFeature_1");
            if (UtilValidate.isNotEmpty(selectFeature))
            {
                createWorkBookRow(excelSheet,selectFeature, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }*/
            
            createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            imageURL = getProductContent(productId,"PLP_SWATCH_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String plpSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,plpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(productId,"PDP_SWATCH_IMAGE_URL",lProductContent);
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,pdpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
                    createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            
            /*selectFeature = (String)mSelectFeature.get("selectFeature_2");
            if (UtilValidate.isNotEmpty(selectFeature))
            {
                createWorkBookRow(excelSheet,selectFeature, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            
            selectFeature = (String)mSelectFeature.get("selectFeature_3");
            if (UtilValidate.isNotEmpty(selectFeature))
            {
                createWorkBookRow(excelSheet,selectFeature, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            
            selectFeature = (String)mSelectFeature.get("selectFeature_4");
            if (UtilValidate.isNotEmpty(selectFeature))
            {
                createWorkBookRow(excelSheet,selectFeature, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            
            selectFeature = (String)mSelectFeature.get("selectFeature_5");
            if (UtilValidate.isNotEmpty(selectFeature))
            {
                createWorkBookRow(excelSheet,selectFeature, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }*/

            List<GenericValue> productDistinguishFeatures = FastList.newInstance();
	        if(UtilValidate.isNotEmpty(productId)) 
	        {
	        	productDistinguishFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
	            productDistinguishFeatures = EntityUtil.filterByDate(productDistinguishFeatures);
	        }
	        Map<String, String> mDescriptiveFeatureTypeMap = FastMap.newInstance();
	        for (GenericValue productDistinguishFeature : productDistinguishFeatures) 
            {
	        	
	        	if(mDescriptiveFeatureTypeMap.containsKey(productDistinguishFeature.getString("productFeatureTypeId")))
            	{
	        		mDescriptiveFeatureTypeMap.put(productDistinguishFeature.getString("productFeatureTypeId"), mDescriptiveFeatureTypeMap.get(productDistinguishFeature.getString("productFeatureTypeId"))+ ", " +productDistinguishFeature.getString("description"));
            	}
            	else
            	{
            		mDescriptiveFeatureTypeMap.put(productDistinguishFeature.getString("productFeatureTypeId"), productDistinguishFeature.getString("description"));	
            	}
	        	
            }
	 
	        int iDescriptiveFeatureIdx=1;
            Map<String, String> mDescriptiveFeature = FastMap.newInstance();
	        if(UtilValidate.isNotEmpty(mDescriptiveFeatureTypeMap))
            {
            	for (Map.Entry<String, String> entry : mDescriptiveFeatureTypeMap.entrySet()) 
            	{
            		if (iDescriptiveFeatureIdx < 6)
                	{
            			String productFeatureTypeDesc = (String) productFeatureTypesMap.get(entry.getKey());
                		if(UtilValidate.isEmpty(productFeatureTypeDesc))
                		{
                			productFeatureTypeDesc = entry.getKey();
                		}
                		mDescriptiveFeature.put("descriptiveFeature_" + iDescriptiveFeatureIdx, productFeatureTypeDesc + ":" + entry.getValue());
                	}
                	iDescriptiveFeatureIdx++;
            	}
            }
	        
            for(int i = 1; i < 6; i++) 
            {
            	String descriptiveFeature = (String)mDescriptiveFeature.get("descriptiveFeature_"+i);
                if (UtilValidate.isNotEmpty(descriptiveFeature))
                {
                    createWorkBookRow(excelSheet,descriptiveFeature, iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                }
            }
            
            
            //iColIdx=createWorkBookProductFeatures(excelSheet,productDistinguishFeatures,iColIdx,iRowIdx);
            
            imageURL =getProductContent(productId,"SMALL_IMAGE_URL",lProductContent);
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String smallImagePath = getOsafeImagePath("SMALL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,smallImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(productId,"SMALL_IMAGE_ALT_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{ 
            		String smallAltImagePath = getOsafeImagePath("SMALL_IMAGE_ALT_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,smallAltImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(productId,"THUMBNAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{ 
            		String thumbnailImagePath = getOsafeImagePath("THUMBNAIL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,thumbnailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(productId,"LARGE_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String largeImagePath = getOsafeImagePath("LARGE_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,largeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(productId,"DETAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String detailImagePath = getOsafeImagePath("DETAIL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,detailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            for (int i=1; i < 11; i++)
            {
                imageURL =getProductContent(productId,"ADDITIONAL_IMAGE_" + i,lProductContent);
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalImagePath = getOsafeImagePath("ADDITIONAL_IMAGE_" + i);
                		pathElements = StringUtil.split(imageURL, "/");
                        createWorkBookRow(excelSheet,additionalImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                	}
                	else
                	{
                		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
                	}
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(productId,"XTRA_IMG_" + i +"_LARGE",lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalLargeImagePath = getOsafeImagePath("XTRA_IMG_" + i +"_LARGE");
                		pathElements = StringUtil.split(imageURL, "/");
                        createWorkBookRow(excelSheet,additionalLargeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                	}
                	else
                	{
                		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
                	}
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(productId,"XTRA_IMG_" + i + "_DETAIL",lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalDetailImagePath = getOsafeImagePath("XTRA_IMG_" + i + "_DETAIL");
                		pathElements = StringUtil.split(imageURL, "/");
                        createWorkBookRow(excelSheet,additionalDetailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                	}
                	else
                	{
                		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
                	}
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
            	
            }
            BigDecimal productHeight = product.getBigDecimal("productHeight");
            if(UtilValidate.isNotEmpty(productHeight))
            {
            	createWorkBookRow(excelSheet,_df.format(productHeight),iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"",iColIdx++, iRowIdx);
            }
        	
            BigDecimal productWidth = product.getBigDecimal("productWidth");
            if(UtilValidate.isNotEmpty(productWidth))
            {
            	createWorkBookRow(excelSheet,_df.format(productWidth),iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"",iColIdx++, iRowIdx);
            }
            
            BigDecimal productDepth = product.getBigDecimal("productDepth");
            if(UtilValidate.isNotEmpty(productDepth))
            {
            	createWorkBookRow(excelSheet,_df.format(productDepth),iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"",iColIdx++, iRowIdx);
            }
        	createWorkBookRow(excelSheet,product.getString("returnable"),iColIdx++, iRowIdx);
        	createWorkBookRow(excelSheet,product.getString("taxable"),iColIdx++, iRowIdx);
        	createWorkBookRow(excelSheet,product.getString("chargeShipping"),iColIdx++, iRowIdx);
        	Timestamp tsstamp = product.getTimestamp("introductionDate");
            if (UtilValidate.isNotEmpty(tsstamp))
            {
            	
                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            tsstamp = product.getTimestamp("salesDiscontinuationDate");
            if (UtilValidate.isNotEmpty(tsstamp))
            {
            	
                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
          
        	createWorkBookRow(excelSheet,product.getString("manufacturerPartyId"),iColIdx++, iRowIdx);

            List<GenericValue> productGoodIdentifications = _delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId),UtilMisc.toList("goodIdentificationTypeId"));
            Map mGoodIdentifications = FastMap.newInstance();
            for (GenericValue productGoodIdentification : productGoodIdentifications) 
            {
            	mGoodIdentifications.put(productGoodIdentification.getString("goodIdentificationTypeId"), productGoodIdentification.getString("idValue"));
            }
            
            String goodIdentification = (String)mGoodIdentifications.get("SKU");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
        	
            goodIdentification = (String)mGoodIdentifications.get("GOOGLE_ID");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            goodIdentification = (String)mGoodIdentifications.get("ISBN");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            goodIdentification = (String)mGoodIdentifications.get("MANUFACTURER_ID_NO");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                createWorkBookRow(excelSheet,goodIdentification, iColIdx++, iRowIdx);
            	
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
        	
        	
            imageURL =getProductContent(productId,"PDP_VIDEO_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideoUrlPath = getOsafeImagePath("PDP_VIDEO_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,pdpVideoUrlPath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            
            imageURL =getProductContent(productId,"PDP_VIDEO_360_URL",lProductContent);
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideo360UrlPath = getOsafeImagePath("PDP_VIDEO_360_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,pdpVideo360UrlPath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);	
            	}
            	else
            	{
            		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
            	}
                
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            if(UtilValidate.isNotEmpty(categoryMembers))
            {
            	GenericValue categoryMember = EntityUtil.getFirst(categoryMembers); 
                createWorkBookRow(excelSheet,categoryMember.getString("sequenceNum"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            GenericValue productAttributeTot = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "BF_INVENTORY_TOT"));
            
            if (UtilValidate.isNotEmpty(productAttributeTot))
            {
                createWorkBookRow(excelSheet,(String)productAttributeTot.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributeWhs = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "BF_INVENTORY_WHS"));
            
            if (UtilValidate.isNotEmpty(productAttributeWhs))
            {
                createWorkBookRow(excelSheet,(String)productAttributeWhs.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributeMulti = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_SELECT_MULTI_VARIANT"));
            
            if (UtilValidate.isNotEmpty(productAttributeMulti))
            {
            	createWorkBookRow(excelSheet,(String)productAttributeMulti.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            BigDecimal weight = product.getBigDecimal("weight");
            if(UtilValidate.isNotEmpty(weight))
            {
            	createWorkBookRow(excelSheet,_df.format(weight),iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"",iColIdx++, iRowIdx);
            }
            GenericValue productAttributeGift = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "CHECKOUT_GIFT_MESSAGE"));
            
            if (UtilValidate.isNotEmpty(productAttributeGift))
            {
            	createWorkBookRow(excelSheet,(String)productAttributeGift.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributePdpQtyMin = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_QTY_MIN"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMin))
            {
            	createWorkBookRow(excelSheet,(String)productAttributePdpQtyMin.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributePdpQtyMax = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_QTY_MAX"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMax))
            {
            	createWorkBookRow(excelSheet,(String)productAttributePdpQtyMax.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
            
            GenericValue productAttributePdpQtyDefault = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_QTY_DEFAULT"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyDefault))
            {
            	createWorkBookRow(excelSheet,(String)productAttributePdpQtyDefault.get("attrValue"), iColIdx++, iRowIdx);
            }
            else
            {
            	createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            }
    	}
    	catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    }

    private static int createProductWorkSheetFromEbay(WritableSheet excelSheet,String browseRootProductCategoryId,List dataRows) {
        int iRowIdx=1;
    	try {
    		int iColIdx=0;
            HashMap productCategoryExists = new HashMap();
            HashMap productParent = new HashMap();
            List<String> pathElements=null;
            Map productVariants= FastMap.newInstance();
            List productRows= FastList.newInstance();
            for (int i=0 ; i < dataRows.size() ; i++) 
            {
            	Map mRow = (Map)dataRows.get(i);
                String productCategoryId = (String)mRow.get("ebayCategoryList");
                String productCategoryDescription = (String)mRow.get("attribute7Value");
                if (UtilValidate.isNotEmpty(productCategoryId) && !productCategoryExists.containsKey(productCategoryId) && !productCategoryExists.containsValue(productCategoryDescription))
                {
                    productCategoryExists.put(productCategoryId, productCategoryDescription);
                }
            	String productId=(String)mRow.get("inventoryNumber");
            	productId=makeOfbizId(productId);
            	String parentProductId="";
                String parent=(String)mRow.get("variantParentSku");
                if (UtilValidate.isNotEmpty(parent) && "PARENT".equals(parent.toUpperCase()))
                {
                	productRows = FastList.newInstance();
                	parentProductId=productId;
                }
                else
                {
                    productRows.add(mRow);
                }
                productVariants.put(parentProductId, productRows);
                
            }
    		
            for (int i=0 ; i < dataRows.size() ; i++) 
            {
            	Map mRow = (Map)dataRows.get(i);
            	String productId=(String)mRow.get("inventoryNumber");
            	productId=makeOfbizId(productId);
            	if (UtilValidate.isNotEmpty(productId))
            	{
                  String parent=(String)mRow.get("variantParentSku");
                  if (UtilValidate.isNotEmpty(parent) && "PARENT".equals(parent.toUpperCase()))
                  {
                		
              		iColIdx=0;
                  	createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
                	String productCategoryId=(String)mRow.get("attribute7Value");
            		Iterator prodCatIter = productCategoryExists.keySet().iterator();
                	while (prodCatIter.hasNext())
                	{
                		String catKey =(String) prodCatIter.next();
                		String catValue =(String)productCategoryExists.get(catKey);
                		if (catValue.equals(productCategoryId))
                		{
                			productCategoryId=catKey;
                			break;
                		}

                	}
                    createWorkBookRow(excelSheet,productCategoryId, iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,(String)mRow.get("inventoryNumber"),iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,(String)mRow.get("auctionTitle"),iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,(String)mRow.get("description"), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, null, iColIdx++, iRowIdx);
    /*              createWorkBookRow(excelSheet, getProductContent(productId,"SPECIALINSTRUCTIONS",lProductContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductContent(productId,"DELIVERY_INFO",lProductContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductContent(productId,"DIRECTIONS",lProductContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductContent(productId,"TERMS_AND_CONDS",lProductContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductContent(productId,"INGREDIENTS",lProductContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductContent(productId,"WARNINGS",lProductContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductContent(productId,"PLP_LABEL",lProductContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductContent(productId,"PDP_LABEL",lProductContent), iColIdx++, iRowIdx);
    */                
                    createWorkBookRow(excelSheet,(String)mRow.get("buyItNowPrice"), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,(String)mRow.get("retailPrice"), iColIdx++, iRowIdx);
                    List prodVariants = (List)productVariants.get(productId);
                    if (prodVariants.isEmpty())
                    {
                    	prodVariants.add(mRow);
                    	iColIdx = createWorkBookProductFeaturesFromEbay(excelSheet,prodVariants,iColIdx,iRowIdx);
                    }
                    else
                    {
                    	iColIdx = createWorkBookProductFeaturesFromEbay(excelSheet, prodVariants, iColIdx, iRowIdx);
                    }
                    String pictureUrls =(String)mRow.get("pictureUrls");
                    String[] imageURLs = pictureUrls.split(",");
//                    imageURL =getProductContent(productId,"SMALL_IMAGE_URL",lProductContent);
                    if (UtilValidate.isNotEmpty(imageURLs[0]))
                    {
                        pathElements = StringUtil.split(imageURLs[0], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"THUMBNAIL_IMAGE_URL",lProductContent);
                    if (UtilValidate.isNotEmpty(imageURLs[0]))
                    {
                        pathElements = StringUtil.split(imageURLs[0], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"LARGE_IMAGE_URL",lProductContent);
                    if (UtilValidate.isNotEmpty(imageURLs[0]))
                    {
                        pathElements = StringUtil.split(imageURLs[0], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"DETAIL_IMAGE_URL",lProductContent);
                    if (UtilValidate.isNotEmpty(imageURLs[0]))
                    {
                        pathElements = StringUtil.split(imageURLs[0], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"ADDITIONAL_IMAGE_1",lProductContent);
                    if (imageURLs.length > 1 && UtilValidate.isNotEmpty(imageURLs[1]))
                    {
                        pathElements = StringUtil.split(imageURLs[1], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_1_LARGE",lProductContent);
                    if (imageURLs.length > 1 && UtilValidate.isNotEmpty(imageURLs[1]))
                    {
                        pathElements = StringUtil.split(imageURLs[1], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_1_DETAIL",lProductContent);
                    if (imageURLs.length > 1 && UtilValidate.isNotEmpty(imageURLs[1]))
                    {
                        pathElements = StringUtil.split(imageURLs[1], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"ADDITIONAL_IMAGE_2",lProductContent);
                    if (imageURLs.length > 2 && UtilValidate.isNotEmpty(imageURLs[2]))
                    {
                        pathElements = StringUtil.split(imageURLs[2], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_2_LARGE",lProductContent);
                    if (imageURLs.length > 2 && UtilValidate.isNotEmpty(imageURLs[2]))
                    {
                        pathElements = StringUtil.split(imageURLs[2], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_2_DETAIL",lProductContent);
                    if (imageURLs.length > 2 && UtilValidate.isNotEmpty(imageURLs[2]))
                    {
                        pathElements = StringUtil.split(imageURLs[2], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"ADDITIONAL_IMAGE_3",lProductContent);
                    if (imageURLs.length > 3 && UtilValidate.isNotEmpty(imageURLs[3]))
                    {
                        pathElements = StringUtil.split(imageURLs[3], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_3_LARGE",lProductContent);
                    if (imageURLs.length > 3 && UtilValidate.isNotEmpty(imageURLs[3]))
                    {
                        pathElements = StringUtil.split(imageURLs[3], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_3_DETAIL",lProductContent);
                    if (imageURLs.length > 3 && UtilValidate.isNotEmpty(imageURLs[3]))
                    {
                        pathElements = StringUtil.split(imageURLs[3], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"ADDITIONAL_IMAGE_4",lProductContent);
                    if (imageURLs.length > 4 && UtilValidate.isNotEmpty(imageURLs[4]))
                    {
                        pathElements = StringUtil.split(imageURLs[4], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_4_LARGE",lProductContent);
                    if (imageURLs.length > 4 && UtilValidate.isNotEmpty(imageURLs[4]))
                    {
                        pathElements = StringUtil.split(imageURLs[4], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
//                    imageURL =getProductContent(productId,"XTRA_IMG_4_DETAIL",lProductContent);
                    if (imageURLs.length > 4 && UtilValidate.isNotEmpty(imageURLs[4]))
                    {
                        pathElements = StringUtil.split(imageURLs[4], "/");
                        createWorkBookRow(excelSheet,pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }

                	createWorkBookRow(excelSheet,null,iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,(String)mRow.get("width"),iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,(String)mRow.get("length"),iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,(String)mRow.get("returnable"),iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,null,iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,null,iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                	createWorkBookRow(excelSheet,null,iColIdx++, iRowIdx);
                    
                    iRowIdx++;
                  }
            	}
            }
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    	return (iRowIdx -1);
    }
    
    private static int createWorkBookProductFeatures(WritableSheet excelSheet,List<GenericValue> productFeatures,int iColIdx,int iRowIdx) {
    	 
    	try {
        	StringBuffer selFeatures =new StringBuffer();
        	int listSize=productFeatures.size();
        	int iListIdx=0;
        	int iFeatCnt=0;
    		if (UtilValidate.isNotEmpty(productFeatures))
    		{
            	String lastProductFeatureTypeId=productFeatures.get(0).getString("productFeatureTypeId");
        		selFeatures.append(lastProductFeatureTypeId + ":");
                for (GenericValue productFeatureAndAppl : productFeatures) 
                {
                	iListIdx++;
    	        	String productFeatureTypeId=productFeatureAndAppl.getString("productFeatureTypeId");
                	if (!lastProductFeatureTypeId.equals(productFeatureTypeId))
                	{
                        selFeatures.setLength(selFeatures.length() - 1);
                        createWorkBookRow(excelSheet,selFeatures.toString(), iColIdx++, iRowIdx);
                        selFeatures.setLength(0);
                    	iFeatCnt++;
                    	lastProductFeatureTypeId=productFeatureTypeId;
                		selFeatures.append(productFeatureTypeId + ":");
                	}
            		selFeatures.append(productFeatureAndAppl.getString("description") + ",");
                	if (iListIdx == listSize)
                	{
                        selFeatures.setLength(selFeatures.length() - 1);
                        createWorkBookRow(excelSheet,selFeatures.toString(), iColIdx++, iRowIdx);
                    	iFeatCnt++;
                	}
                }
    			
    		}
        	for (int i=iFeatCnt;i < 5;i++)
        	{
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        		
        	}
            
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);

    	}
   	    return iColIdx;
        
       }
    
    private static int createWorkBookProductFeaturesFromEbay(WritableSheet excelSheet,List<Map> productFeatures,int iColIdx,int iRowIdx) {
   	 
    	try {
        	StringBuffer selFeatures =new StringBuffer();
        	int listSize=productFeatures.size();
        	int iListIdx=0;
        	int iSelFeatCnt=0;
        	int iDesFeatCnt=0;
        	String sLastFeatureValue="";
    		if (UtilValidate.isNotEmpty(productFeatures))
    		{
                for (Map productFeatureAndAppl : productFeatures) 
                {
    	        	String productFeatureTypeId=(String)productFeatureAndAppl.get("attribute2Name");
    	        	if (UtilValidate.isNotEmpty(productFeatureTypeId))
    	        	{
    	        		productFeatureTypeId=productFeatureTypeId.trim();
    	        	}
                	if (UtilValidate.isNotEmpty(productFeatureTypeId) && iListIdx ==0)
                	{
                		selFeatures.append(productFeatureTypeId + ":");
                	}
    	        	String productFeatureValue=(String)productFeatureAndAppl.get("attribute2Value");
    	        	if (UtilValidate.isNotEmpty(productFeatureValue))
    	        	{
    	        		productFeatureValue=productFeatureValue.trim();
    	        	}
    	        	
    	        	if (UtilValidate.isNotEmpty(productFeatureValue) && !productFeatureValue.equals(sLastFeatureValue))
    	        	{
                    	sLastFeatureValue=productFeatureValue;
                		selFeatures.append(productFeatureValue + ",");
                    	iListIdx++;
    	        		
    	        	}
                }
    			
            	if (iListIdx > 0)
            	{
                    selFeatures.setLength(selFeatures.length() - 1);
                    createWorkBookRow(excelSheet,selFeatures.toString(), iColIdx++, iRowIdx);
                	iSelFeatCnt++;
            	}
            	iListIdx=0;
            	selFeatures.setLength(0);
                for (Map productFeatureAndAppl : productFeatures) 
                {
    	        	String productFeatureTypeId=(String)productFeatureAndAppl.get("attribute5Name");
    	        	if (UtilValidate.isNotEmpty(productFeatureTypeId))
    	        	{
    	        		productFeatureTypeId=productFeatureTypeId.trim();
    	        	}
                	if (UtilValidate.isNotEmpty(productFeatureTypeId) && iListIdx ==0)
                	{
                		selFeatures.append(productFeatureTypeId + ":");
                	}
    	        	String productFeatureValue=(String)productFeatureAndAppl.get("attribute5Value");
    	        	if (UtilValidate.isNotEmpty(productFeatureValue))
    	        	{
    	        		productFeatureValue=productFeatureValue.trim();
    	        	}
    	        	if (UtilValidate.isNotEmpty(productFeatureValue) && !productFeatureValue.equals(sLastFeatureValue))
    	        	{
                    	sLastFeatureValue=productFeatureValue;
                		selFeatures.append(productFeatureValue + ",");
                    	iListIdx++;
    	        		
    	        	}
                }
                
            	if (iListIdx > 0)
            	{
                    selFeatures.setLength(selFeatures.length() - 1);
                    createWorkBookRow(excelSheet,selFeatures.toString(), iColIdx++, iRowIdx);
                	iSelFeatCnt++;
            	}
                for (int i=iSelFeatCnt;i < 3;i++)
                {
                	createWorkBookRow(excelSheet,"",iColIdx++,iRowIdx);
                }
                
            	iListIdx=0;
            	selFeatures.setLength(0);
                for (Map productFeatureAndAppl : productFeatures) 
                {
    	        	String productFeatureTypeId=(String)productFeatureAndAppl.get("attribute6Name");
    	        	if (UtilValidate.isNotEmpty(productFeatureTypeId))
    	        	{
    	        		productFeatureTypeId=productFeatureTypeId.trim();
    	        	}
                	if (UtilValidate.isNotEmpty(productFeatureTypeId) && iListIdx ==0)
                	{
                		selFeatures.append(productFeatureTypeId + ":");
                	}
    	        	String productFeatureValue=(String)productFeatureAndAppl.get("attribute6Value");
    	        	if (UtilValidate.isNotEmpty(productFeatureValue))
    	        	{
    	        		productFeatureValue=productFeatureValue.trim();
    	        	}
    	        	if (UtilValidate.isNotEmpty(productFeatureValue) && !productFeatureValue.equals(sLastFeatureValue))
    	        	{
                    	sLastFeatureValue=productFeatureValue;
                		selFeatures.append(productFeatureValue + ",");
                    	iListIdx++;
    	        		
    	        	}
                }
                
            	if (iListIdx > 0)
            	{
                    selFeatures.setLength(selFeatures.length() - 1);
                    createWorkBookRow(excelSheet,selFeatures.toString(), iColIdx++, iRowIdx);
                	iDesFeatCnt++;
            	}
            	iListIdx=0;
            	selFeatures.setLength(0);
                for (Map productFeatureAndAppl : productFeatures) 
                {
    	        	String productFeatureTypeId=(String)productFeatureAndAppl.get("attribute10Name");
    	        	if (UtilValidate.isNotEmpty(productFeatureTypeId))
    	        	{
    	        		productFeatureTypeId=productFeatureTypeId.trim();
    	        	}
                	if (UtilValidate.isNotEmpty(productFeatureTypeId) && iListIdx ==0)
                	{
                		selFeatures.append(productFeatureTypeId + ":");
                	}
    	        	String productFeatureValue=(String)productFeatureAndAppl.get("attribute10Value");
    	        	if (UtilValidate.isNotEmpty(productFeatureValue))
    	        	{
    	        		productFeatureValue=productFeatureValue.trim();
    	        	}
    	        	if (UtilValidate.isNotEmpty(productFeatureValue) && !productFeatureValue.equals(sLastFeatureValue))
    	        	{
                    	sLastFeatureValue=productFeatureValue;
                		selFeatures.append(productFeatureValue + ",");
                    	iListIdx++;
    	        		
    	        	}
                }
                
            	if (iListIdx > 0)
            	{
                    selFeatures.setLength(selFeatures.length() - 1);
                    createWorkBookRow(excelSheet,selFeatures.toString(), iColIdx++, iRowIdx);
                	iDesFeatCnt++;
            	}

            	iListIdx=0;
            	selFeatures.setLength(0);
                for (Map productFeatureAndAppl : productFeatures) 
                {
    	        	String productFeatureTypeId=(String)productFeatureAndAppl.get("attribute11Name");
    	        	if (UtilValidate.isNotEmpty(productFeatureTypeId))
    	        	{
    	        		productFeatureTypeId=productFeatureTypeId.trim();
    	        	}
                	if (UtilValidate.isNotEmpty(productFeatureTypeId) && iListIdx ==0)
                	{
                		selFeatures.append(productFeatureTypeId + ":");
                	}
    	        	String productFeatureValue=(String)productFeatureAndAppl.get("attribute11Value");
    	        	if (UtilValidate.isNotEmpty(productFeatureValue))
    	        	{
    	        		productFeatureValue=productFeatureValue.trim();
    	        	}
    	        	if (UtilValidate.isNotEmpty(productFeatureValue) && !productFeatureValue.equals(sLastFeatureValue))
    	        	{
                    	sLastFeatureValue=productFeatureValue;
                		selFeatures.append(productFeatureValue + ",");
                    	iListIdx++;
    	        		
    	        	}
                }
                
            	if (iListIdx > 0)
            	{
                    selFeatures.setLength(selFeatures.length() - 1);
                    createWorkBookRow(excelSheet,selFeatures.toString(), iColIdx++, iRowIdx);
                	iDesFeatCnt++;
            	}
            	
    		}
           
        	for (int i=iDesFeatCnt;i < 3;i++)
        	{
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        		
        	}
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);

    	}
   	    return iColIdx;
        
       }

    private static int createProductAssocWorkSheet(WritableSheet excelSheet,String browseRootProductCategoryId) {
        int iRowIdx=1;
    	try {
    		
            List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, true, false, true);
            GenericValue workingCategory = null;
            int iColIdx=0;
            Timestamp tsstamp=null;
            String productId="";
            HashMap productExists = new HashMap();
            for (Map<String, Object> workingCategoryMap : productCategories) 
            {
                workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
                List<GenericValue> productCategoryMembers = workingCategory.getRelated("ProductCategoryMember");
                // Remove any expired
                productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                for (GenericValue productCategoryMember : productCategoryMembers) 
                {
                    GenericValue product = productCategoryMember.getRelatedOne("Product");
                    productId = product.getString("productId");
                    if (UtilValidate.isNotEmpty(product) && !productExists.containsKey(productId))
                    {
                    	productExists.put(productId, productId);
                    	List<GenericValue> productAssocitations = product.getRelated("MainProductAssoc");
            	        if(UtilValidate.isNotEmpty(productAssocitations)) {
            	            productAssocitations = EntityUtil.filterByDate(productAssocitations, true);
            	        }
            	        List<GenericValue> complementProductAssoc = FastList.newInstance();
            	        if(UtilValidate.isNotEmpty(productAssocitations)) {
            	        	complementProductAssoc = EntityUtil.filterByAnd(productAssocitations, UtilMisc.toMap("productAssocTypeId", "PRODUCT_COMPLEMENT"));
            	        	complementProductAssoc = EntityUtil.orderBy(complementProductAssoc,UtilMisc.toList("sequenceNum"));
            	        }
                        for (GenericValue productAssoc : complementProductAssoc) 
                        {
                        	iColIdx=0;
                            createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,productAssoc.getString("productIdTo"),iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,"COMPLEMENT",iColIdx++, iRowIdx);
                        	tsstamp = productAssoc.getTimestamp("fromDate");
                            if (UtilValidate.isNotEmpty(tsstamp))
                            {
                            	
                                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                            }
                            else
                            {
                                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                            	
                            }
                            tsstamp = productAssoc.getTimestamp("thruDate");
                            if (UtilValidate.isNotEmpty(tsstamp))
                            {
                            	
                                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                            }
                            else
                            {
                                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                            	
                            }
                            iRowIdx++;
                        }
                        List<GenericValue> accessoryProductAssoc = FastList.newInstance();
            	        if(UtilValidate.isNotEmpty(productAssocitations)) {
            	        	accessoryProductAssoc = EntityUtil.filterByAnd(productAssocitations, UtilMisc.toMap("productAssocTypeId", "PRODUCT_ACCESSORY"));
            	        	accessoryProductAssoc = EntityUtil.orderBy(accessoryProductAssoc,UtilMisc.toList("sequenceNum"));
            	        }
                        for (GenericValue productAssoc : accessoryProductAssoc) 
                        {
                        	iColIdx=0;
                            createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,productAssoc.getString("productIdTo"),iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,"ACCESSORY",iColIdx++, iRowIdx);
                        	tsstamp = productAssoc.getTimestamp("fromDate");
                            if (UtilValidate.isNotEmpty(tsstamp))
                            {
                            	
                                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                            }
                            else
                            {
                                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                            	
                            }
                            tsstamp = productAssoc.getTimestamp("thruDate");
                            if (UtilValidate.isNotEmpty(tsstamp))
                            {
                            	
                                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                            }
                            else
                            {
                                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                            	
                            }
                            iRowIdx++;
                        }
                    }
                }
            }
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    	return (iRowIdx -1);
    }

    private static int createManufacturerWorkSheet(WritableSheet excelSheet,String browseRootProductCategoryId) {
        int iRowIdx=1;
    	try {
    		
	        List<GenericValue> partyManufacturers = _delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId","MANUFACTURER"),UtilMisc.toList("partyId"));
            GenericValue party = null;
            String partyId=null;
            GenericValue partyGroup = null;
            GenericValue partyContactMechPurpose = null;
            String imageURL=null;
            List<String> pathElements=null;
            int iColIdx=0;
            for (GenericValue partyManufacturer : partyManufacturers) 
            {
            	iColIdx=0;
            	party = (GenericValue) partyManufacturer.getRelatedOne("Party");
            	partyId=party.getString("partyId");
    	        List<GenericValue> lPartyContent = _delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",partyId),UtilMisc.toList("-fromDate"));
    	        lPartyContent=EntityUtil.filterByDate(lPartyContent,UtilDateTime.nowTimestamp());
                createWorkBookRow(excelSheet,partyId,iColIdx++, iRowIdx);
                String profileName = getPartyContent(partyId,"PROFILE_NAME",lPartyContent);
                createWorkBookRow(excelSheet, profileName, iColIdx++, iRowIdx);
                
                Collection<GenericValue> partyContactMechPurposes = ContactHelper.getContactMechByPurpose(party,"GENERAL_LOCATION",false);
                Iterator<GenericValue> partyContactMechPurposesIterator = partyContactMechPurposes.iterator();
                while (partyContactMechPurposesIterator.hasNext()) 
                {
                	partyContactMechPurpose = (GenericValue) partyContactMechPurposesIterator.next();
                }
                if (UtilValidate.isNotEmpty(partyContactMechPurpose))
                {
                	GenericValue postalAddress = partyContactMechPurpose.getRelatedOne("PostalAddress");
                	String address=postalAddress.getString("address1");
                	String city=postalAddress.getString("city");
                	String state=postalAddress.getString("stateProvinceGeoId");
                	String zip=postalAddress.getString("postalCode");
                    if (UtilValidate.isNotEmpty(address))
                    {
                        createWorkBookRow(excelSheet, address, iColIdx++, iRowIdx);
                    	
                    }
                    else
                    {
                        createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                    	
                    }
                    if (UtilValidate.isNotEmpty(city))
                    {
                        createWorkBookRow(excelSheet, city, iColIdx++, iRowIdx);
                    	
                    }
                    else
                    {
                        createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                    	
                    }
                    if (UtilValidate.isNotEmpty(state))
                    {
                        createWorkBookRow(excelSheet, state, iColIdx++, iRowIdx);
                    	
                    }
                    else
                    {
                        createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                    	
                    }
                    if (UtilValidate.isNotEmpty(zip))
                    {
                        createWorkBookRow(excelSheet, zip, iColIdx++, iRowIdx);
                    	
                    }
                    else
                    {
                        createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                    	
                    }
                }
                else
                {
                    createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, "", iColIdx++, iRowIdx);
                }
                createWorkBookRow(excelSheet, getPartyContent(partyId,"DESCRIPTION",lPartyContent), iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet, getPartyContent(partyId,"LONG_DESCRIPTION",lPartyContent), iColIdx++, iRowIdx);
                imageURL =getPartyContent(partyId,"PROFILE_IMAGE_URL",lPartyContent);
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String profileImagePath = getOsafeImagePath("PROFILE_IMAGE_URL");
                		pathElements = StringUtil.split(imageURL, "/");
                        createWorkBookRow(excelSheet,profileImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                	}
                	else
                	{
                		createWorkBookRow(excelSheet,imageURL, iColIdx++, iRowIdx);
                	}
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                
                iRowIdx++;
            }
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    	return (iRowIdx -1);
    }

    private static int createFeatureSwatchesWorkSheet(WritableSheet excelSheet,String browseRootProductCategoryId) {
        int iRowIdx=1;
        int iFeatureSwatchCnt=0;
        
    	try 
    	{
    		
    		Map productFeatureTypesMap = FastMap.newInstance();
            List<GenericValue> productFeatureTypesList = _delegator.findList("ProductFeatureType", null, null, null, null, false);
            
            //get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
            List productFeatureGroupList = _delegator.findList("ProductFeatureGroup", null, null, null, null, false);
            List productFeatureGroupAndApplList = _delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, false);
            productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);
            
            if(UtilValidate.isNotEmpty(productFeatureTypesList))
            {
                for (GenericValue productFeatureType : productFeatureTypesList)
                {
                    //filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
                	List productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.getString("productFeatureTypeId")));
                    String description = "";
                    if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
                    {
                        GenericValue productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
                        List productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.getString("productFeatureGroupId")));
                        GenericValue productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
                        description = productFeatureGroup.getString("description");
                    }
                    else
                    {
                        description = productFeatureType.getString("description");
                    }
                    productFeatureTypesMap.put(productFeatureType.getString("productFeatureTypeId"),description);
                }
                
            }	
    		
    		
	        List<GenericValue> lProductFeatureDataResource = _delegator.findByAnd("ProductFeatureDataResource", UtilMisc.toMap("featureDataResourceTypeId","PLP_SWATCH_IMAGE_URL"),UtilMisc.toList("dataResourceId"));
	        
	        Map mFeatureRow = FastMap.newInstance();
            for (GenericValue productFeatureDataResource : lProductFeatureDataResource) 
            {
            	GenericValue productFeature = (GenericValue) productFeatureDataResource.getRelatedOne("ProductFeature");
            	GenericValue dataResource  = (GenericValue) productFeatureDataResource.getRelatedOne("DataResource");
            	String productFeatureId = productFeature.getString("productFeatureId");
            	String productFeatureTypeDesc = (String) productFeatureTypesMap.get(productFeature.getString("productFeatureTypeId"));
        		if(UtilValidate.isEmpty(productFeatureTypeDesc))
        		{
        			productFeatureTypeDesc = productFeature.getString("productFeatureTypeId");
        		}
            	String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
            	String productFeatureDescription = productFeature.getString("description");
            	String dataResourceName = dataResource.getString("dataResourceName"); 
                mFeatureRow.put(productFeatureId,"" + iRowIdx);
                createWorkBookRow(excelSheet,productFeatureTypeDesc + ":" + productFeatureDescription,0, iRowIdx);
                if (!UtilValidate.isUrl(dataResourceName))
            	{
                	String featurePLPSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
                	createWorkBookRow(excelSheet,featurePLPSwatchImagePath + dataResourceName,1, iRowIdx);
            	}
                else
                {
                	createWorkBookRow(excelSheet,dataResourceName,1, iRowIdx);
                }
                iRowIdx++;
                iFeatureSwatchCnt++;
            }
	        lProductFeatureDataResource = _delegator.findByAnd("ProductFeatureDataResource", UtilMisc.toMap("featureDataResourceTypeId","PDP_SWATCH_IMAGE_URL"),UtilMisc.toList("dataResourceId"));
	        
	        iRowIdx=1;
            for (GenericValue productFeatureDataResource : lProductFeatureDataResource) 
            {
            	GenericValue productFeature = (GenericValue) productFeatureDataResource.getRelatedOne("ProductFeature");
            	GenericValue dataResource  = (GenericValue) productFeatureDataResource.getRelatedOne("DataResource");
            	String productFeatureId = productFeature.getString("productFeatureId");
            	String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
            	String productFeatureDescription = productFeature.getString("description");
            	String dataResourceName = dataResource.getString("dataResourceName");
            	String sRowIdx = (String) mFeatureRow.get(productFeatureId);
            	if (UtilValidate.isNotEmpty(sRowIdx))
            	{
            		if (!UtilValidate.isUrl(dataResourceName))
                	{
            			createWorkBookRow(excelSheet,dataResourceName,2, Integer.parseInt(sRowIdx));
                	}
                    else
                    {
                    	createWorkBookRow(excelSheet,dataResourceName,1, iRowIdx);
                    }
            	}
            	else
            	{
                    createWorkBookRow(excelSheet,productFeatureTypeId + ":" + productFeatureDescription,0, iRowIdx);
                    if (!UtilValidate.isUrl(dataResourceName))
                	{
                    	createWorkBookRow(excelSheet,dataResourceName,2, iRowIdx);
                	}
                    else
                    {
                    	createWorkBookRow(excelSheet,dataResourceName,1, iRowIdx);
                    }
                    iRowIdx++;
                    iFeatureSwatchCnt++;
            	}
            }
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}
    	return (iFeatureSwatchCnt);
    }
    
    
    public static List buildCategoryHeader() {
        List headerCols = FastList.newInstance();
   	    headerCols.add("productCategoryId");
   	    headerCols.add("parentCategoryId");
   	    headerCols.add("categoryName");
   	    headerCols.add("description");
   	    headerCols.add("longDescription");
   	    headerCols.add("plpImageName");
   	    headerCols.add("plpText");
   	    headerCols.add("pdpText");
   	    headerCols.add("fromDate");
   	    headerCols.add("thruDate");
   	    
   	    return headerCols;
        
       }
    public static List buildProductHeader() {
        List headerCols = FastList.newInstance();
   	    headerCols.add("masterProductId");
   	    headerCols.add("productId");
   	    headerCols.add("productCategoryId");
   	    headerCols.add("internalName");
   	    headerCols.add("productName");
   	    headerCols.add("salesPitch");
   	    headerCols.add("longDescription");
   	    headerCols.add("specialInstructions");
   	    headerCols.add("deliveryInfo");
   	    headerCols.add("directions");
   	    headerCols.add("termsConditions");
   	    headerCols.add("ingredients");
   	    headerCols.add("warnings");
   	    headerCols.add("plpLabel");
   	    headerCols.add("pdpLabel");
   	    headerCols.add("listPrice");
   	    headerCols.add("defaultPrice");
   	    headerCols.add("selectabeFeature_1");
   	    headerCols.add("plpSwatchImage");
   	    headerCols.add("pdpSwatchImage");
   	    headerCols.add("selectabeFeature_2");
   	    headerCols.add("selectabeFeature_3");
   	    headerCols.add("selectabeFeature_4");
	    headerCols.add("selectabeFeature_5");
   	    headerCols.add("descriptiveFeature_1");
   	    headerCols.add("descriptiveFeature_2");
   	    headerCols.add("descriptiveFeature_3");
   	    headerCols.add("descriptiveFeature_4");
	    headerCols.add("descriptiveFeature_5");
   	    headerCols.add("smallImage");
   	    headerCols.add("smallImageAlt");
   	    headerCols.add("thumbImage");
   	    headerCols.add("largeImage");
   	    headerCols.add("detailImage");
   	    headerCols.add("addImage1");
   	    headerCols.add("xtraLargeImage1");
   	    headerCols.add("xtraDetailImage1");
   	    headerCols.add("addImage2");
   	    headerCols.add("xtraLargeImage2");
   	    headerCols.add("xtraDetailImage2");
   	    headerCols.add("addImage3");
   	    headerCols.add("xtraLargeImage3");
   	    headerCols.add("xtraDetailImage3");
   	    headerCols.add("addImage4");
   	    headerCols.add("xtraLargeImage4");
   	    headerCols.add("xtraDetailImage4");
   	    headerCols.add("addImage5");
   	    headerCols.add("xtraLargeImage5");
   	    headerCols.add("xtraDetailImage5");
   	    headerCols.add("addImage6");
   	    headerCols.add("xtraLargeImage6");
   	    headerCols.add("xtraDetailImage6");
   	    headerCols.add("addImage7");
   	    headerCols.add("xtraLargeImage7");
   	    headerCols.add("xtraDetailImage7");
   	    headerCols.add("addImage8");
   	    headerCols.add("xtraLargeImage8");
   	    headerCols.add("xtraDetailImage8");
   	    headerCols.add("addImage9");
   	    headerCols.add("xtraLargeImage9");
   	    headerCols.add("xtraDetailImage9");
   	    headerCols.add("addImage10");
   	    headerCols.add("xtraLargeImage10");
   	    headerCols.add("xtraDetailImage10");
   	    headerCols.add("productHeight");
   	    headerCols.add("productWidth");
   	    headerCols.add("productDepth");
   	    headerCols.add("returnable");
   	    headerCols.add("taxable");
   	    headerCols.add("chargeShipping");
   	    headerCols.add("introDate");
   	    headerCols.add("discoDate");
   	    headerCols.add("manufacturerId");
   	    headerCols.add("goodIdentificationSkuId");
   	    headerCols.add("goodIdentificationGoogleId");
   	    headerCols.add("goodIdentificationIsbnId");
   	    headerCols.add("goodIdentificationManufacturerId");
   	    headerCols.add("pdpVideoUrl");
   	    headerCols.add("pdpVideo360Url");
   	    headerCols.add("sequenceNum");
	    headerCols.add("bfInventoryTot");
	    headerCols.add("bfInventoryWhs");
   	    headerCols.add("multiVariant");
   	    headerCols.add("weight");
   	    headerCols.add("giftMessage");
   	    headerCols.add("pdpQtyMin");
   	    headerCols.add("pdpQtyMax");
   	    headerCols.add("pdpQtyDefault");
   	    return headerCols;
    }

    public static List buildManufacturerHeader() 
    {
        List headerCols = FastList.newInstance();
   	    headerCols.add("partyId");
   	    headerCols.add("manufacturerName");
   	    headerCols.add("address1");
   	    headerCols.add("city");
   	    headerCols.add("state");
   	    headerCols.add("zip");
   	    headerCols.add("country");
   	    headerCols.add("shortDescription");
   	    headerCols.add("longDescription");
   	    headerCols.add("manufacturerImage");
   	    return headerCols;
    }

    public static List buildProductFeatureSwatchHeader() {
        List headerCols = FastList.newInstance();
   	    headerCols.add("featureId");
   	    headerCols.add("plpSwatchImage");
   	    headerCols.add("pdpSwatchImage");
   	    return headerCols;
        
       }

    public static List buildProductAssocHeader() {
        List headerCols = FastList.newInstance();
   	    headerCols.add("productId");
   	    headerCols.add("productIdTo");
   	    headerCols.add("productAssocType");
   	    headerCols.add("fromDate");
   	    headerCols.add("thruDate");
   	    return headerCols;
        
       }

    private static List buildModHistoryHeader() {
        List headerCols = FastList.newInstance();
   	    headerCols.add("date");
   	    headerCols.add("who");
   	    headerCols.add("changes");
   	    return headerCols;
        
       }
    
    public static List buildEbayProductHeader() {
        List headerCols = FastList.newInstance();
   	    headerCols.add("auctionTitle");
   	    headerCols.add("inventoryNumber");
   	    headerCols.add("quantityUpdateType");
   	    headerCols.add("quantity");
   	    headerCols.add("startingBid");
   	    headerCols.add("reserve");
   	    headerCols.add("weight");
   	    headerCols.add("isbn");
   	    headerCols.add("upc");
   	    headerCols.add("ean");
   	    headerCols.add("asin");
   	    headerCols.add("mpn");
   	    headerCols.add("shortDescription");
   	    headerCols.add("description");
   	    headerCols.add("manufacturer");
   	    headerCols.add("brand");
   	    headerCols.add("condition");
   	    headerCols.add("warranty");
   	    headerCols.add("sellerCost");
   	    headerCols.add("profitMargin");
   	    headerCols.add("buyItNowPrice");
   	    headerCols.add("retailPrice");
   	    headerCols.add("secondChanceOfferPrice");
   	    headerCols.add("pictureUrls");
   	    headerCols.add("taxProduct");
   	    headerCols.add("supplierCode");
   	    headerCols.add("supplierPo");
   	    headerCols.add("warehouseLocation");
   	    headerCols.add("receivedInventory");
   	    headerCols.add("inventorySubtitle");
   	    headerCols.add("relationshipName");
   	    headerCols.add("variantParentSku");
   	    headerCols.add("adTemplateName");
   	    headerCols.add("postingTemplateName");
   	    headerCols.add("scheduleName");
   	    headerCols.add("ebayCategoryList");
   	    headerCols.add("ebayStoreCategoryName");
   	    headerCols.add("labels");
   	    headerCols.add("dcCode");
   	    headerCols.add("doNotConsolidate");
   	    headerCols.add("channelAdvisorStoreTitle");
   	    headerCols.add("channelAdvisorStoreDescription");
   	    headerCols.add("storeMetaDescription");
   	    headerCols.add("channelAdvisorStorePrice");
   	    headerCols.add("channelAdvisorStoreCategoryId");
   	    headerCols.add("classification");
   	    headerCols.add("attribute1Name");
   	    headerCols.add("attribute1Value");
   	    headerCols.add("attribute2Name");
   	    headerCols.add("attribute2Value");
   	    headerCols.add("attribute3Name");
   	    headerCols.add("attribute3Value");
   	    headerCols.add("attribute4Name");
   	    headerCols.add("attribute4Value");
   	    headerCols.add("attribute5Name");
   	    headerCols.add("attribute5Value");
   	    headerCols.add("attribute6Name");
   	    headerCols.add("attribute6Value");
   	    headerCols.add("attribute7Name");
   	    headerCols.add("attribute7Value");
   	    headerCols.add("attribute8Name");
   	    headerCols.add("attribute8Value");
   	    headerCols.add("attribute9Name");
   	    headerCols.add("attribute9Value");
   	    headerCols.add("attribute10Name");
   	    headerCols.add("attribute10Value");
   	    headerCols.add("attribute11Name");
   	    headerCols.add("attribute11Value");
   	    headerCols.add("attribute12Name");
   	    headerCols.add("attribute12Value");
   	    headerCols.add("attribute13Name");
   	    headerCols.add("attribute13Value");
   	    headerCols.add("attribute14Name");
   	    headerCols.add("attribute14Value");
   	    headerCols.add("attribute15Name");
   	    headerCols.add("attribute15Value");
   	    headerCols.add("attribute16Name");
   	    headerCols.add("attribute16Value");
   	    headerCols.add("attribute17Name");
   	    headerCols.add("attribute17Value");
   	    headerCols.add("attribute18Name");
   	    headerCols.add("attribute18Value");
   	    headerCols.add("attribute19Name");
   	    headerCols.add("attribute19Value");
   	    headerCols.add("attribute20Name");
   	    headerCols.add("attribute20Value");
   	    headerCols.add("attribute21Name");
   	    headerCols.add("attribute21Value");
   	    headerCols.add("attribute22Name");
   	    headerCols.add("attribute22Value");
   	    headerCols.add("attribute23Name");
   	    headerCols.add("attribute23Value");
   	    headerCols.add("attribute24Name");
   	    headerCols.add("attribute24Value");
   	    headerCols.add("attribute25Name");
   	    headerCols.add("attribute25Value");
   	    headerCols.add("attribute26Name");
   	    headerCols.add("attribute26Value");
   	    headerCols.add("attribute27Name");
   	    headerCols.add("attribute27Value");
   	    headerCols.add("attribute28Name");
   	    headerCols.add("attribute28Value");
   	    headerCols.add("attribute29Name");
   	    headerCols.add("attribute29Value");
   	    headerCols.add("attribute30Name");
   	    headerCols.add("attribute30Value");
   	    headerCols.add("attribute31Name");
   	    headerCols.add("attribute31Value");
   	    headerCols.add("attribute32Name");
   	    headerCols.add("attribute32Value");
   	    headerCols.add("attribute33Name");
   	    headerCols.add("attribute33Value");
   	    headerCols.add("attribute34Name");
   	    headerCols.add("attribute34Value");
   	    headerCols.add("attribute35Name");
   	    headerCols.add("attribute35Value");
   	    headerCols.add("attribute36Name");
   	    headerCols.add("attribute36Value");
   	    headerCols.add("attribute37Name");
   	    headerCols.add("attribute37Value");
   	    headerCols.add("attribute38Name");
   	    headerCols.add("attribute39Value");
   	    headerCols.add("attribute40Name");
   	    headerCols.add("attribute40Value");
   	    headerCols.add("harmonizedCode");
   	    headerCols.add("height");
   	    headerCols.add("length");
   	    headerCols.add("width");
   	    headerCols.add("shipZoneName");
   	    headerCols.add("shipCarrierCode");
   	    headerCols.add("shipClassCode");
   	    headerCols.add("shipRateFirstItem");
   	    headerCols.add("shipHandlingFirstItem");
   	    headerCols.add("shipRateAdditionalItem");
   	    headerCols.add("shipHandlingAdditionalItem");
   	    headerCols.add("recommendedBrowseNode");
   	    
   	    return headerCols;
        
       }
    
    public static List buildProductRatingHeader() 
    {
        List headerCols = FastList.newInstance();
   	    headerCols.add("productId");
   	    headerCols.add("ratingScore");
   	    
   	    return headerCols;
        
    }
    
    public static List buildStoreHeader() 
    {
	    List headerCols = FastList.newInstance();
	   	headerCols.add("storeId");
	   	headerCols.add("storeCode");
	   	headerCols.add("storeName");
	   	headerCols.add("country");
	   	headerCols.add("address1");
	   	headerCols.add("address2");
	   	headerCols.add("address3");
	   	headerCols.add("cityOrTown");
	   	headerCols.add("stateOrProvince");
	   	headerCols.add("zipOrPostcode");
	   	headerCols.add("telephoneNumber");
	   	headerCols.add("status");
	   	headerCols.add("openingHours");
	   	headerCols.add("storeNotice");
	   	headerCols.add("contentSpot");
	   	headerCols.add("geoCodeLong");
	   	headerCols.add("geoCodeLat");
   	
   	    return headerCols;
    }
    
    public static List buildOrderStatusUpdateHeader() 
    {
	    List headerCols = FastList.newInstance();
	   	headerCols.add("orderId");
	   	headerCols.add("orderStatus");
	   	headerCols.add("orderShipDate");
	   	headerCols.add("orderShipCarrier");
	   	headerCols.add("orderShipMethod");
	   	headerCols.add("orderTrackingNumber");
	   	headerCols.add("orderNote");
	   	
   	    return headerCols;
    }
    
    public static List buildDataRows(List headerCols,Sheet s) {
		List dataRows = FastList.newInstance();

		try {

            for (int rowCount = 1 ; rowCount < s.getRows() ; rowCount++) 
            {
            	Cell[] row = s.getRow(rowCount);
             if (row.length > 0) 
             {
            	Map mRows = FastMap.newInstance();
                for (int colCount = 0; colCount < headerCols.size(); colCount++) {
                	String colContent=null;
                
                	 try {
                		 colContent=row[colCount].getContents().toString();
                	 }
                	   catch (Exception e) {
                		   colContent="";
                		   
                	   }
                  mRows.put(headerCols.get(colCount),colContent);
                }
                //mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
            }
			
    		
    
    	}
      	 catch (Exception e) {
   	         }
      	return dataRows;
       }

    private static void buildProductCategory(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        String categoryImageName=null;
		try {
			
	        fOutFile = new File(xmlDataDirPath, "000-ProductCategory.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
                    StringBuilder  rowString = new StringBuilder();
                    rowString.append("<" + "ProductCategory" + " ");
	            	 Map mRow = (Map)dataRows.get(i);
                     rowString.append("productCategoryId" + "=\"" + mRow.get("productCategoryId") + "\" ");
                     rowString.append("productCategoryTypeId" + "=\"" + "CATALOG_CATEGORY" + "\" ");
                     rowString.append("primaryParentCategoryId" + "=\"" + mRow.get("parentCategoryId") + "\" ");
                     rowString.append("categoryName" + "=\"" + (String)mRow.get("categoryName") + "\" ");
                     if(mRow.get("description") != null) {
                    	 rowString.append("description" + "=\"" + (String)mRow.get("description") + "\" "); 
                     }
                     if(mRow.get("longDescription") != null) {
                    	 rowString.append("longDescription" + "=\"" + (String)mRow.get("longDescription") + "\" "); 
                     }
                     
                     categoryImageName=(String)mRow.get("plpImageName");
     	             
                     if (UtilValidate.isNotEmpty(categoryImageName))
                     {
                    	 if (!UtilValidate.isUrl(categoryImageName)) 
                  		 {
                    		 Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
                        	 
                         	 for(Map<Object, Object> imageLocationPref : imageLocationPrefList) 
                         	 {
                         		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
                         	 }
                         	 String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
                         	 if(UtilValidate.isNotEmpty(defaultImageDirectory)) 
                         	 {
                         		categoryImageName = defaultImageDirectory + categoryImageName;
                         	 } 
                  		 }
                         
                         rowString.append("categoryImageUrl" + "=\"" + categoryImageName + "\" ");
                     }
                     else
                     {
                         rowString.append("categoryImageUrl" + "=\"" + "" + "\" ");
                     }
                     rowString.append("linkOneImageUrl" + "=\"" + "" + "\" ");
                     rowString.append("linkTwoImageUrl" + "=\"" + "" + "\" ");
                     rowString.append("detailScreen" + "=\"" + "" + "\" ");
                     rowString.append("/>");
                    bwOutFile.write(rowString.toString());
                    bwOutFile.newLine();
                    try
                    {
                    String fromDate = _sdf.format(UtilDateTime.nowTimestamp());
                    if (UtilValidate.isEmpty(mRow.get("fromDate")))
                    {
                    	List<GenericValue> productCategoryRollups = _delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap("productCategoryId",mRow.get("productCategoryId"),"parentProductCategoryId",mRow.get("parentCategoryId")),UtilMisc.toList("-fromDate"));
	                    if(UtilValidate.isNotEmpty(productCategoryRollups)) {
	                    	productCategoryRollups = EntityUtil.filterByDate(productCategoryRollups);
	                    	if(UtilValidate.isNotEmpty(productCategoryRollups)){
	                    	    GenericValue productCategoryRollup = EntityUtil.getFirst(productCategoryRollups);
	                    	    fromDate = _sdf.format(new Date(productCategoryRollup.getTimestamp("fromDate").getTime()));
	                    	}
	                    }
                    } else {
                    	String sFromDate=(String)mRow.get("fromDate");
                   	 	java.util.Date formattedFromDate=OsafeAdminUtil.validDate(sFromDate);
                   	 	fromDate =_sdf.format(formattedFromDate);
                    }
                    
                    rowString.setLength(0);
                    rowString.append("<" + "ProductCategoryRollup" + " ");
                    rowString.append("productCategoryId" + "=\"" + mRow.get("productCategoryId") + "\" ");
                    rowString.append("parentProductCategoryId" + "=\"" + mRow.get("parentCategoryId") + "\" ");
                    rowString.append("fromDate" + "=\"" + fromDate + "\" ");
                    String thruDate=(String)mRow.get("thruDate");
                    if(UtilValidate.isNotEmpty(thruDate)) 
                    {
                    	java.util.Date formattedThuDate=OsafeAdminUtil.validDate(thruDate);
                    	String sThruDate =_sdf.format(formattedThuDate);
                    	rowString.append("thruDate" + "=\"" + sThruDate + "\" ");	
                    }
                    rowString.append("sequenceNum" + "=\"" + ((i +1) *10) + "\" ");
                    rowString.append("/>");
                   bwOutFile.write(rowString.toString());
                   bwOutFile.newLine();
                   }
                   catch(Exception ex)
                   {
                       Debug.logError(ex, module);
                   }
                   addCategoryContentRow(rowString, mRow, bwOutFile, "text", "PLP_ESPOT_CONTENT", "plpText");
                   addCategoryContentRow(rowString, mRow, bwOutFile, "text", "PDP_ADDITIONAL", "pdpText");
 	            	
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
    	}
      	 catch (Exception e) {
   	         }
         finally {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
      	 
       }
    
    private static void buildManufacturer(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl, String productStoreId) {
    	if (UtilValidate.isNotEmpty(productStoreId))
	   	{
	        File fOutFile =null;
	        BufferedWriter bwOutFile=null;
        
			try {
				
		        fOutFile = new File(xmlDataDirPath, "020-Manufacturer.xml");
	            if (fOutFile.createNewFile()) {
	            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
	
	                writeXmlHeader(bwOutFile);
	                
	                for (int i=0 ; i < dataRows.size() ; i++) {
	                     StringBuilder  rowString = new StringBuilder();
		            	 Map mRow = (Map)dataRows.get(i);
		            	 String partyId=(String) mRow.get("partyId");
		            	 if (UtilValidate.isNotEmpty(partyId))
		            	 {
		            		 rowString.append("<" + "Party" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("partyTypeId" + "=\"" + "PARTY_GROUP" + "\" ");
		                     rowString.append("statusId" + "=\"" + "PARTY_ENABLED" + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyRole" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("roleTypeId" + "=\"" + "MANUFACTURER" + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
	
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyGroup" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("groupName" + "=\"" + (String)mRow.get("manufacturerName") + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     List<GenericValue> productStoreRoles = _delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId",partyId,"roleTypeId","MANUFACTURER","productStoreId",productStoreId),UtilMisc.toList("-fromDate"));
		 	                 if(UtilValidate.isNotEmpty(productStoreRoles)) 
		 	                 {
		 	                	productStoreRoles = EntityUtil.filterByDate(productStoreRoles);
		 	                 }
		 	                 if(UtilValidate.isEmpty(productStoreRoles))
			                 {
		 	                	rowString.setLength(0);
			                     rowString.append("<" + "ProductStoreRole" + " ");
			                     rowString.append("partyId" + "=\"" + partyId + "\" ");
			                     rowString.append("roleTypeId" + "=\"" + "MANUFACTURER" + "\" ");
			                     rowString.append("productStoreId" + "=\"" + productStoreId + "\" ");
			                     rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
			                     rowString.append("/>");
			                     bwOutFile.write(rowString.toString());
			                     bwOutFile.newLine();
			                 }
		                     
		         			 String contactMechId=_delegator.getNextSeqId("ContactMech");
		                     rowString.setLength(0);
		                     rowString.append("<" + "ContactMech" + " ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("contactMechTypeId" + "=\"" + "POSTAL_ADDRESS" + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
	
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyContactMech" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyContactMechPurpose" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("contactMechPurposeTypeId" + "=\"" + "GENERAL_LOCATION" + "\" ");
		                     rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     rowString.setLength(0);
		                     rowString.append("<" + "PostalAddress" + " ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("toName" + "=\"" + (String)mRow.get("manufacturerName") + "\" ");
		                     if(mRow.get("address1") != null) {
		                    	 rowString.append("address1" + "=\"" +  (String)mRow.get("address1") + "\" "); 
		                     }
		                     if(mRow.get("city") != null) {
		                    	 rowString.append("city" + "=\"" +  (String)mRow.get("city") + "\" ");
		                     }
		                     if(mRow.get("state") != null) {
		                    	 rowString.append("stateProvinceGeoId" + "=\"" +  mRow.get("state") + "\" ");
		                     }
		                     if(mRow.get("zip") != null) {
		                    	 rowString.append("postalCode" + "=\"" +  mRow.get("zip") + "\" ");
		                     }
		                     if(mRow.get("country") != null) {
		                    	 rowString.append("countryGeoId" + "=\"" +  (String)mRow.get("country") + "\" ");
		                     }
		                     
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     addPartyContentRow(rowString, mRow, bwOutFile, "text", "DESCRIPTION", "shortDescription",loadImagesDirPath,imageUrl,"shortDescriptionThruDate");
		                     addPartyContentRow(rowString, mRow, bwOutFile, "text", "LONG_DESCRIPTION", "longDescription",loadImagesDirPath,imageUrl,"longDescriptionThruDate");
		                     addPartyContentRow(rowString, mRow, bwOutFile, "image", "PROFILE_IMAGE_URL", "manufacturerImage",loadImagesDirPath,imageUrl, "manufacturerImageThruDate");
		                     addPartyContentRow(rowString, mRow, bwOutFile, "text", "PROFILE_NAME", "manufacturerName",loadImagesDirPath,imageUrl,"manufacturerNameThruDate");
		            	 }
	                     
	 	            	
		            }
	                bwOutFile.flush();
	         	    writeXmlFooter(bwOutFile);
	            }
	            
				
	    
	    	}
	      	 catch (Exception e) {
	   	         }
	         finally {
	             try {
	                 if (bwOutFile != null) {
	                	 bwOutFile.close();
	                 }
	             } catch (IOException ioe) {
	                 Debug.logError(ioe, module);
	             }
	         }
	    }
      	 
    }
    private static void buildProduct(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        String productId=null;
        
		try {

	        fOutFile = new File(xmlDataDirPath, "030-Product.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
            	String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
            	String priceFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 masterProductId = (String)mRow.get("masterProductId");
	            	 productId = (String)mRow.get("productId");
	            	 String[] productCategoryIds = null;
	            	 if ((UtilValidate.isEmpty(productId)) || (UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId)))
	            	 {
	                     rowString.setLength(0);
	                     rowString.append("<" + "Product" + " ");
		            	 rowString.append("productId" + "=\"" + masterProductId + "\" ");
	                     rowString.append("productTypeId" + "=\"" + "FINISHED_GOOD" + "\" ");
	                     String productCategoryId = (String)mRow.get("productCategoryId");
	                     if(UtilValidate.isNotEmpty(productCategoryId)) {
	                    	 productCategoryIds = productCategoryId.split(",");
	                         String primaryProductCategoryId =productCategoryIds[0].trim();
	                         rowString.append("primaryProductCategoryId" + "=\"" + primaryProductCategoryId + "\" ");
	                     }
	                     if(mRow.get("manufacturerId") != null) {
	                    	 rowString.append("manufacturerPartyId" + "=\"" + mRow.get("manufacturerId") + "\" ");
	                     }
	                     if(mRow.get("internalName") != null) {
	                    	 rowString.append("internalName" + "=\"" + (String)mRow.get("internalName") + "\" ");
	                     }
	                     rowString.append("brandName" + "=\"" + "" + "\" ");
	                     
	                     try
	                     {
	                    	 String fromDate=(String)mRow.get("introDate");
	                    	 if (UtilValidate.isNotEmpty(fromDate))
	                    	 {
	                    		 java.util.Date formattedFromDate=OsafeAdminUtil.validDate(fromDate);
		                    	 String sFromDate =_sdf.format(formattedFromDate);
	                    		 rowString.append("introductionDate" + "=\"" + sFromDate + "\" ");
	                    	 }
	                    	 else
	                    	 {
	                    		 rowString.append("introductionDate" + "=\"" + "" + "\" ");
	                    	 }
		                     rowString.append("productName" + "=\"" + "" + "\" ");
		                     String thruDate=(String)mRow.get("discoDate");
		        			 if (UtilValidate.isNotEmpty(thruDate))
		        			 {
		        				 java.util.Date formattedThuDate=OsafeAdminUtil.validDate(thruDate);
		                    	 String sThruDate =_sdf.format(formattedThuDate);
		                         rowString.append("salesDiscontinuationDate" + "=\"" + sThruDate + "\" ");
		        			 }
		        			 else
		        			 {
		                         rowString.append("salesDiscontinuationDate" + "=\"" + "" + "\" ");
		        			 }
		                 }
	                     catch(Exception ex)
	                     {
	                         Debug.logError(ex, module);
	                     }
	                     rowString.append("requireInventory" + "=\"" + "N"+ "\" ");
	                     if(mRow.get("returnable") != null) {
	                         rowString.append("returnable" + "=\"" + mRow.get("returnable") + "\" ");
	                     }
	                     if(mRow.get("taxable") != null) {
	                         rowString.append("taxable" + "=\"" + mRow.get("taxable") + "\" ");
	                     }
	                     if(mRow.get("chargeShipping") != null) {
	                         rowString.append("chargeShipping" + "=\"" + mRow.get("chargeShipping") + "\" ");
	                     }
	                     if(mRow.get("productHeight") != null) {
	                         rowString.append("productHeight" + "=\"" + mRow.get("productHeight") + "\" ");
	                     }
	                     if(mRow.get("productWidth") != null) {
	                         rowString.append("productWidth" + "=\"" + mRow.get("productWidth") + "\" ");
	                     }
	                     if(mRow.get("productDepth") != null) {
	                         rowString.append("productDepth" + "=\"" + mRow.get("productDepth") + "\" ");
	                     }
	                     if(mRow.get("weight") != null) {
	                         rowString.append("weight" + "=\"" + mRow.get("weight") + "\" ");
	                     }
	                     String isVirtual="N";
	                     
	                     if(UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId)) {
	                    	 isVirtual="Y";
	                     }
	 	            	 
	                     rowString.append("isVirtual" + "=\"" + isVirtual + "\" ");
	                     rowString.append("isVariant" + "=\"" + "N" + "\" ");
	                     rowString.append("/>");
	                     bwOutFile.write(rowString.toString());
	                     bwOutFile.newLine();
	                     if(UtilValidate.isNotEmpty(productCategoryIds)) {
	                    	 for (int j=0;j < productCategoryIds.length;j++)
		                     {
		                    	 String sequenceNum = (String)mRow.get("sequenceNum");
		                    	 String productCategoryFromDate = _sdf.format(UtilDateTime.nowTimestamp());
			         			 
			         			 if(UtilValidate.isEmpty(sequenceNum)) 
			         			 {
			         				sequenceNum = "10";
			         			 }
							     if(UtilValidate.isNotEmpty(productCategoryIds[j].trim())) 
							     {
							     
							     if(UtilValidate.isNotEmpty(mRow.get(productCategoryIds[j].trim()+"_sequenceNum"))) 
							     {
							         sequenceNum =  (String) mRow.get(productCategoryIds[j].trim()+"_sequenceNum");
							     }
							     if(UtilValidate.isEmpty(mRow.get(productCategoryIds[j].trim()+"_fromDate"))) 
							     {
							    	 List<GenericValue> productCategoryMembers = _delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productCategoryId",productCategoryIds[j].trim(),"productId",masterProductId),UtilMisc.toList("-fromDate"));
					                 if(UtilValidate.isNotEmpty(productCategoryMembers))
					                 {
					                	 productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers);
					                	 if(UtilValidate.isNotEmpty(productCategoryMembers))
					                	 {
					                    	GenericValue productCategoryMember = EntityUtil.getFirst(productCategoryMembers);
					                    	productCategoryFromDate = _sdf.format(new Date(productCategoryMember.getTimestamp("fromDate").getTime()));
					                	 }
					                 }
							     } 
							     else 
							     {
							    	 productCategoryFromDate = (String) mRow.get(productCategoryIds[j].trim()+"_fromDate");
					      			 java.util.Date formattedFromDate=OsafeAdminUtil.validDate(productCategoryFromDate);
					      			 productCategoryFromDate =_sdf.format(formattedFromDate);
							     }
							     
		                         rowString.setLength(0);
		                         rowString.append("<" + "ProductCategoryMember" + " ");
		                         rowString.append("productCategoryId" + "=\"" + productCategoryIds[j].trim()+ "\" ");
		                         rowString.append("productId" + "=\"" + masterProductId+ "\" ");
		            			 rowString.append("fromDate" + "=\"" + productCategoryFromDate + "\" ");
		            			 if (UtilValidate.isNotEmpty(mRow.get(productCategoryIds[j].trim()+"_thruDate")))
		            			 {
		            				 String productCategoryThruDate = (String) mRow.get(productCategoryIds[j].trim()+"_thruDate");
					      			 java.util.Date formattedFromDate=OsafeAdminUtil.validDate(productCategoryThruDate);
					      			 productCategoryThruDate =_sdf.format(formattedFromDate);
		                             rowString.append("thruDate" + "=\"" + productCategoryThruDate + "\" ");
		            			 }
		                         rowString.append("comments" + "=\"" + "" + "\" ");
		                         rowString.append("sequenceNum" + "=\"" + sequenceNum + "\" ");
		                         rowString.append("quantity" + "=\"" + "" + "\" ");
		                         rowString.append("/>");
		                         bwOutFile.write(rowString.toString());
		                         bwOutFile.newLine();
								 }
		                     	
		                     }
	                     }
	                     
	                    if(UtilValidate.isNotEmpty(mRow.get("listPriceCurrency"))) 
	                    {
		                   	currencyUomId = (String) mRow.get("listPriceCurrency");
		                }
	                    
	                    if(UtilValidate.isEmpty(mRow.get("listPriceFromDate"))) 
	                    {
		                    List<GenericValue> productListPrices = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",masterProductId,"productPriceTypeId","LIST_PRICE", "productPricePurposeId","PURCHASE", "currencyUomId", currencyUomId, "productStoreGroupId", "_NA_"),UtilMisc.toList("-fromDate"));
		                    if(UtilValidate.isNotEmpty(productListPrices))
		                    {
		                    	productListPrices = EntityUtil.filterByDate(productListPrices);
		                    	if(UtilValidate.isNotEmpty(productListPrices)) 
		                    	{
		                    	    GenericValue productListPrice = EntityUtil.getFirst(productListPrices);
		                    	    priceFromDate = _sdf.format(new Date(productListPrice.getTimestamp("fromDate").getTime()));
		                    	}
		                    }
		                } 
	                    else 
		                {
		                	priceFromDate = (String) mRow.get("listPriceFromDate");
		                    java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceFromDate);
		                  	priceFromDate =_sdf.format(formattedFromDate);
		                }
	                    if(mRow.get("listPrice") != null) {
		                    rowString.setLength(0);
		                    rowString.append("<" + "ProductPrice" + " ");
		                    rowString.append("productId" + "=\"" + masterProductId+ "\" ");
		                    rowString.append("productPriceTypeId" + "=\"" + "LIST_PRICE" + "\" ");
		                    rowString.append("productPricePurposeId" + "=\"" + "PURCHASE" + "\" ");
		                    rowString.append("currencyUomId" + "=\"" + currencyUomId + "\" ");
		                    rowString.append("productStoreGroupId" + "=\"" + "_NA_" + "\" ");
		                    rowString.append("price" + "=\"" + mRow.get("listPrice") + "\" ");
		                    rowString.append("fromDate" + "=\"" + priceFromDate + "\" ");
		                    if(UtilValidate.isNotEmpty(mRow.get("listPriceThruDate"))) {
		                    	String priceThruDate = (String) mRow.get("listPriceThruDate");
		                        java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceThruDate);
		                        priceThruDate =_sdf.format(formattedFromDate);
		                    	rowString.append("thruDate" + "=\"" + priceThruDate + "\" ");
		                    }
		                    rowString.append("/>");
		                    bwOutFile.write(rowString.toString());
		                    bwOutFile.newLine();
	                    }
	                    if(UtilValidate.isNotEmpty(mRow.get("defaultPriceCurrency"))) {
	                    	currencyUomId = (String) mRow.get("defaultPriceCurrency");
	                    }
	                    if(UtilValidate.isEmpty(mRow.get("defaultPriceFromDate"))) {
		                    List<GenericValue> productDefaultPrices = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",masterProductId,"productPriceTypeId","DEFAULT_PRICE", "productPricePurposeId","PURCHASE", "currencyUomId", currencyUomId, "productStoreGroupId", "_NA_"),UtilMisc.toList("-fromDate"));
		                    if(UtilValidate.isNotEmpty(productDefaultPrices)){
		                    	productDefaultPrices = EntityUtil.filterByDate(productDefaultPrices);
		                    	if(UtilValidate.isNotEmpty(productDefaultPrices)) {
		                    	    GenericValue productDefaultPrice = EntityUtil.getFirst(productDefaultPrices);
		                    	    priceFromDate = _sdf.format(new Date(productDefaultPrice.getTimestamp("fromDate").getTime()));
		                    	}
		                    }
		                } else {
		                	priceFromDate = (String) mRow.get("defaultPriceFromDate");
		                   	java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceFromDate);
		                   	priceFromDate =_sdf.format(formattedFromDate);
		                }
	                    if(mRow.get("defaultPrice") != null) {
		                    rowString.setLength(0);
		                    rowString.append("<" + "ProductPrice" + " ");
		                    rowString.append("productId" + "=\"" + masterProductId+ "\" ");
		                    rowString.append("productPriceTypeId" + "=\"" + "DEFAULT_PRICE" + "\" ");
		                    rowString.append("productPricePurposeId" + "=\"" + "PURCHASE" + "\" ");
		                    rowString.append("currencyUomId" + "=\"" + currencyUomId + "\" ");
		                    rowString.append("productStoreGroupId" + "=\"" + "_NA_" + "\" ");
		                    rowString.append("price" + "=\"" + mRow.get("defaultPrice") + "\" ");
		                    rowString.append("fromDate" + "=\"" + priceFromDate + "\" ");
		                    if(UtilValidate.isNotEmpty(mRow.get("defaultPriceThruDate"))) {
		                    	String priceThruDate = (String) mRow.get("defaultPriceThruDate");
		                        java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceThruDate);
		                        priceThruDate =_sdf.format(formattedFromDate);
		                    	rowString.append("thruDate" + "=\"" + priceThruDate + "\" ");
		                    }
		                    rowString.append("/>");
		                    bwOutFile.write(rowString.toString());
		                    bwOutFile.newLine();
	                    }
	            	 }
                    
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
            
			
    
    	}
      	 catch (Exception e) {
      		e.printStackTrace();
   	     }
         finally {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
      	 
       }
    private static void buildProductVariant(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl, Boolean removeAll) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
        
        StringBuilder  rowString = new StringBuilder();
        
		try {
			
	        fOutFile = new File(xmlDataDirPath, "040-ProductVariant.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
              	    Map mRow = (Map)dataRows.get(i);
              	    String productId=(String)mRow.get("masterProductId");
      			    String featureProductId=(String)mRow.get("productId");
      			    String fromDate=(String)mRow.get("introDate");
      			    String sFromDate = "";
      			    if(UtilValidate.isNotEmpty(fromDate))
      			    {
      			        java.util.Date formattedFromDate=OsafeAdminUtil.validDate(fromDate);
      			        sFromDate =_sdf.format(formattedFromDate);
      			    }	
      			    String thruDate=(String)mRow.get("discoDate");
      			    String sThruDate = "";
      			    if(UtilValidate.isNotEmpty(thruDate))
    			    {
      				    java.util.Date formattedThuDate=OsafeAdminUtil.validDate(thruDate);
      			        sThruDate =_sdf.format(formattedThuDate);  
    			    }
      	            
              	    mFeatureTypeMap.clear();
              	    int iSeq = 0;
              	    
              	    //not a variant product
              	    if (UtilValidate.isEmpty(featureProductId) || productId.equals(featureProductId))
              	    {
              	    	continue;
              	    }
              	    
              	    addProductVariantRow(rowString, bwOutFile, mRow, loadImagesDirPath,imageUrl,productId, featureProductId,sFromDate,sThruDate, iSeq, removeAll);
              	      
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
    	}
      	 catch (Exception e) {
   	         }
         finally {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
      	 
       }
    
    private static void buildProductGoodIdentification(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        String productId=null;
        
		try {

	        fOutFile = new File(xmlDataDirPath, "045-ProductGoodIdentification.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 masterProductId=(String)mRow.get("masterProductId");
	            	 productId = (String)mRow.get("productId");
	            	 if ((UtilValidate.isEmpty(productId)) || (UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId)))
	            	 {
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationSkuId","SKU");
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationGoogleId", "GOOGLE_ID");
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationIsbnId", "ISBN");
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationManufacturerId", "MANUFACTURER_ID_NO");
	            	 }
	            	 else
	            	 {
	            		 //Add Variant Product Good Identification
	            		 if (UtilValidate.isNotEmpty(productId) && !(masterProductId.equals(productId)))
	            		 {
	        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, productId,"goodIdentificationSkuId","SKU");
	        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, productId,"goodIdentificationGoogleId", "GOOGLE_ID");
	        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, productId,"goodIdentificationIsbnId", "ISBN");
	        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, productId,"goodIdentificationManufacturerId", "MANUFACTURER_ID_NO");
	            			 
	            		 }
	            				 
	            	 }
                    
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
            
			
            
    	}
      	 catch (Exception e) {
   	         }
         finally {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
      	 
       }
    
    private static void buildProductAttribute(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        String productId=null;
        String masterProductId=null;
        
		try {

	        fOutFile = new File(xmlDataDirPath, "075-ProductAttribute.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 masterProductId = (String)mRow.get("masterProductId");
	            	 productId =(String)mRow.get("productId");
	            	 if ((UtilValidate.isEmpty(productId)) || (UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId)))
	            	 {
	            		 addProductAttributeRow(rowString, mRow, bwOutFile, masterProductId,"bfInventoryTot","BF_INVENTORY_TOT");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, masterProductId,"bfInventoryWhs","BF_INVENTORY_WHS");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, masterProductId,"multiVariant","PDP_SELECT_MULTI_VARIANT");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, masterProductId,"giftMessage","CHECKOUT_GIFT_MESSAGE");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, masterProductId,"pdpQtyMin","PDP_QTY_MIN");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, masterProductId,"pdpQtyMax","PDP_QTY_MAX");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, masterProductId,"pdpQtyDefault","PDP_QTY_DEFAULT");
	            	 }
	            	 if (UtilValidate.isNotEmpty(productId) && !masterProductId.equals(productId))
	            	 {
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"bfInventoryTot","BF_INVENTORY_TOT");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"bfInventoryWhs","BF_INVENTORY_WHS");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"multiVariant","PDP_SELECT_MULTI_VARIANT");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"giftMessage","CHECKOUT_GIFT_MESSAGE");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"pdpQtyMin","PDP_QTY_MIN");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"pdpQtyMax","PDP_QTY_MAX");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"pdpQtyDefault","PDP_QTY_DEFAULT");
	            	 }
                    
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
            
			
            
    	}
      	 catch (Exception e) {
   	         }
         finally {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
      	 
       }
    

    private static void addProductVariantRow(StringBuilder rowString,BufferedWriter bwOutFile,Map mRow,String loadImagesDirPath, String imageUrl, String masterProductId,String featureProductId,String sFromDate,String sThruDate,int iSeq, Boolean removeAll) {
    	String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
    	String priceFromDate = _sdf.format(UtilDateTime.nowTimestamp());
    	try 
    	{
    		
		   rowString.setLength(0);
           rowString.append("<" + "Product" + " ");
           rowString.append("productId" + "=\"" + featureProductId + "\" ");
           rowString.append("productTypeId" + "=\"" + "FINISHED_GOOD" + "\" ");
           rowString.append("isVirtual" + "=\"" + "N" + "\" ");
           rowString.append("isVariant" + "=\"" + "Y" + "\" ");
	      if (UtilValidate.isNotEmpty(sFromDate))
		  {
             rowString.append("introductionDate" + "=\"" + sFromDate + "\" ");
		  }
		  else
		  {
             rowString.append("introductionDate" + "=\"" + "" + "\" ");
		  }
		  if (UtilValidate.isNotEmpty(sThruDate))
		  {
                 rowString.append("salesDiscontinuationDate" + "=\"" + sThruDate + "\" ");
		  }
		  else
		  {
                 rowString.append("salesDiscontinuationDate" + "=\"" + "" + "\" ");
		  }
       
          if(mRow.get("manufacturerId") != null) 
          {
         	 rowString.append("manufacturerPartyId" + "=\"" + mRow.get("manufacturerId") + "\" ");
          }
          if(mRow.get("internalName") != null) 
          {
         	 rowString.append("internalName" + "=\"" + (String)mRow.get("internalName") + "\" ");
          }
          rowString.append("brandName" + "=\"" + "" + "\" ");
          if(mRow.get("productName") != null) 
          {
         	 rowString.append("productName" + "=\"" + (String)mRow.get("productName") + "\" ");
          }
          else
          {
              rowString.append("productName" + "=\"" + "" + "\" ");
          }
          if(mRow.get("returnable") != null) 
          {
              rowString.append("returnable" + "=\"" + mRow.get("returnable") + "\" ");
          }
          if(mRow.get("taxable") != null) 
          {
              rowString.append("taxable" + "=\"" + mRow.get("taxable") + "\" ");
          }
          if(mRow.get("chargeShipping") != null) 
          {
              rowString.append("chargeShipping" + "=\"" + mRow.get("chargeShipping") + "\" ");
          }
          if(mRow.get("productHeight") != null) 
          {
              rowString.append("productHeight" + "=\"" + mRow.get("productHeight") + "\" ");
          }
          if(mRow.get("productWidth") != null) 
          {
              rowString.append("productWidth" + "=\"" + mRow.get("productWidth") + "\" ");
          }
          if(mRow.get("productDepth") != null) 
          {
              rowString.append("productDepth" + "=\"" + mRow.get("productDepth") + "\" ");
          }
          if(mRow.get("weight") != null) 
          {
              rowString.append("weight" + "=\"" + mRow.get("weight") + "\" ");
          }

           rowString.append("/>");
           bwOutFile.write(rowString.toString());
           bwOutFile.newLine();
           
           List<GenericValue> productAssocList = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", masterProductId, "productIdTo", featureProductId, "productAssocTypeId", "PRODUCT_VARIANT"));
           if(UtilValidate.isNotEmpty(productAssocList)) 
           {
        	   productAssocList = EntityUtil.filterByDate(productAssocList, true);
           }
           if(UtilValidate.isEmpty(productAssocList) || removeAll) 
           {
               rowString.setLength(0);
               rowString.append("<" + "ProductAssoc" + " ");
               rowString.append("productId" + "=\"" + masterProductId+ "\" ");
               rowString.append("productIdTo" + "=\"" + featureProductId + "\" ");
               rowString.append("productAssocTypeId" + "=\"" + "PRODUCT_VARIANT" + "\" ");
               rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
               rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
               rowString.append("/>");
               bwOutFile.write(rowString.toString());
               bwOutFile.newLine();
           }
           
           String sPrice =(String)mRow.get("listPrice");
           
           if (UtilValidate.isNotEmpty(sPrice))
           {
        	   if(UtilValidate.isNotEmpty(mRow.get("listPriceCurrency"))) 
        	   {
                   currencyUomId = (String) mRow.get("listPriceCurrency");
               }
        	   if(UtilValidate.isEmpty(mRow.get("listPriceFromDate"))) 
        	   {
                   List<GenericValue> productListPrices = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",featureProductId,"productPriceTypeId","LIST_PRICE", "productPricePurposeId","PURCHASE", "currencyUomId", currencyUomId, "productStoreGroupId", "_NA_"),UtilMisc.toList("-fromDate"));
                   if(UtilValidate.isNotEmpty(productListPrices)) 
                   {
                	   productListPrices = EntityUtil.filterByDate(productListPrices);
                   	   if(UtilValidate.isNotEmpty(productListPrices)) 
                   	   {
                   	       GenericValue productListPrice = EntityUtil.getFirst(productListPrices);
                   	       priceFromDate = _sdf.format(new Date(productListPrice.getTimestamp("fromDate").getTime()));
                       }
                   }
               } 
        	   else 
               {
               	 priceFromDate = (String) mRow.get("listPriceFromDate");
                 java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceFromDate);
               	 priceFromDate =_sdf.format(formattedFromDate);
               }
        	   
               rowString.setLength(0);
               rowString.append("<" + "ProductPrice" + " ");
               rowString.append("productId" + "=\"" + featureProductId+ "\" ");
               rowString.append("productPriceTypeId" + "=\"" + "LIST_PRICE" + "\" ");
               rowString.append("productPricePurposeId" + "=\"" + "PURCHASE" + "\" ");
               rowString.append("currencyUomId" + "=\"" + currencyUomId + "\" ");
               rowString.append("productStoreGroupId" + "=\"" + "_NA_" + "\" ");
               rowString.append("price" + "=\"" +  sPrice + "\" ");
               rowString.append("fromDate" + "=\"" + priceFromDate + "\" ");
               if(UtilValidate.isNotEmpty(mRow.get("listPriceThruDate"))) 
               {
            	   String priceThruDate = (String) mRow.get("listPriceThruDate");
                   java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceThruDate);
                   priceThruDate =_sdf.format(formattedFromDate);
            	   rowString.append("thruDate" + "=\"" + priceThruDate + "\" ");
               }
               rowString.append("/>");
               bwOutFile.write(rowString.toString());
               bwOutFile.newLine();
        	   
           }
           
           sPrice =(String)mRow.get("defaultPrice");
           if (UtilValidate.isNotEmpty(sPrice))
           {
        	   if(UtilValidate.isNotEmpty(mRow.get("defaultPriceCurrency"))) 
        	   {
                   currencyUomId = (String) mRow.get("defaultPriceCurrency");
               }
        	   if(UtilValidate.isEmpty(mRow.get("defaultPriceFromDate"))) 
        	   {
                   List<GenericValue> productDefaultPrices = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",featureProductId,"productPriceTypeId","DEFAULT_PRICE", "productPricePurposeId","PURCHASE", "currencyUomId", currencyUomId, "productStoreGroupId", "_NA_"),UtilMisc.toList("-fromDate"));
                   if(UtilValidate.isNotEmpty(productDefaultPrices))
                   {
                       productDefaultPrices = EntityUtil.filterByDate(productDefaultPrices);
                       if(UtilValidate.isNotEmpty(productDefaultPrices))
                       {
                   	       GenericValue productDefaultPrice = EntityUtil.getFirst(productDefaultPrices);
                   	       priceFromDate = _sdf.format(new Date(productDefaultPrice.getTimestamp("fromDate").getTime()));
                       }
               	   }
               } 
        	   else 
        	   {
               	   priceFromDate = (String) mRow.get("defaultPriceFromDate");
               	   java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceFromDate);
               	   priceFromDate =_sdf.format(formattedFromDate);
               }
               rowString.setLength(0);
               rowString.append("<" + "ProductPrice" + " ");
               rowString.append("productId" + "=\"" + featureProductId+ "\" ");
               rowString.append("productPriceTypeId" + "=\"" + "DEFAULT_PRICE" + "\" ");
               rowString.append("productPricePurposeId" + "=\"" + "PURCHASE" + "\" ");
               rowString.append("currencyUomId" + "=\"" + currencyUomId + "\" ");
               rowString.append("productStoreGroupId" + "=\"" + "_NA_" + "\" ");
               rowString.append("price" + "=\"" + sPrice+ "\" ");
               rowString.append("fromDate" + "=\"" + priceFromDate + "\" ");
               if(UtilValidate.isNotEmpty(mRow.get("defaultPriceThruDate"))) 
               {
            	   String priceThruDate = (String) mRow.get("defaultPriceThruDate");
                   java.util.Date formattedFromDate=OsafeAdminUtil.validDate(priceThruDate);
                   priceThruDate =_sdf.format(formattedFromDate);
            	   rowString.append("thruDate" + "=\"" + priceThruDate + "\" ");
               }
               rowString.append("/>");
               bwOutFile.write(rowString.toString());
               bwOutFile.newLine();
        	   
           }
           
    	}
    	 catch (Exception e) {
    		 
    	 }
    }

    private static Map addProductFeatureImageRow(StringBuilder rowString,BufferedWriter bwOutFile,Map mFeatureTypeMap,Map mFeatureIdImageExists,String featureImage,String colName,String featureDataResourceTypeId,String loadImagesDirPath, String imageUrl) {
    	
    	try {
        		Set featureTypeSet = mFeatureTypeMap.keySet();
        		Iterator iterFeatureType = featureTypeSet.iterator();
        		while (iterFeatureType.hasNext())
        		{
        			String featureType =(String)iterFeatureType.next();
        			String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
        			if (featureTypeId.length() > 20)
        			{
        				featureTypeId=featureTypeId.substring(0,20);
        			}
        			FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
            		Set featureSet = mFeatureMap.keySet();
            		Iterator iterFeature = featureSet.iterator();
            		while (iterFeature.hasNext())
            		{
            			String featureId =(String)iterFeature.next();
            			/*String featureId =StringUtil.removeSpaces(feature).toUpperCase();
            			featureId =StringUtil.replaceString(featureId, "&", "");
            			featureId=featureTypeId+"_"+featureId;
            			if (featureId.length() > 20)
            			{
            				featureId=featureId.substring(0,20);
            			}*/
            			if (!mFeatureIdImageExists.containsKey(featureId))
            			{
            				String dataResourceId = "";
            				List<GenericValue> lProductFeatureDataResource = _delegator.findByAnd("ProductFeatureDataResource", UtilMisc.toMap("productFeatureId", featureId, "featureDataResourceTypeId", featureDataResourceTypeId), UtilMisc.toList("-lastUpdatedStamp"));
            				if(UtilValidate.isNotEmpty(lProductFeatureDataResource))
            				{
            					GenericValue productFeatureDataResource = EntityUtil.getFirst(lProductFeatureDataResource);
            					dataResourceId = productFeatureDataResource.getString("dataResourceId");
            				}
            				else
            				{
            					dataResourceId = _delegator.getNextSeqId("DataResource");	
            				}
                            mFeatureIdImageExists.put(featureId,featureImage);
            				
            	            rowString.setLength(0);
            	            rowString.append("<" + "DataResource" + " ");
            	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
            	            rowString.append("dataResourceTypeId" + "=\"" + "SHORT_TEXT" + "\" ");
            	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
            	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
            	            rowString.append("dataResourceName" + "=\"" + featureImage + "\" ");
            	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
            	            
            	            if (!UtilValidate.isUrl(featureImage)) 
                     		{
            	            	Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
                            	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
                            		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
                            	}
                            	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
                            	String defaultSwatchImagePath = (String)imageLocationMap.get(featureDataResourceTypeId);
                            	if(UtilValidate.isNotEmpty(defaultImageDirectory) && UtilValidate.isNotEmpty(defaultSwatchImagePath)) 
                            	{
                            		featureImage = defaultImageDirectory + defaultSwatchImagePath + featureImage;
                            	}
                     		}
            	            
            	            
            	            rowString.append("objectInfo" + "=\"" + featureImage.trim() + "\" ");
            	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
            	            rowString.append("/>");
            	            bwOutFile.write(rowString.toString());
            	            bwOutFile.newLine();
                		
                			rowString.setLength(0);
       	                    rowString.append("<" + "ProductFeatureDataResource" + " ");
    	                    rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
    	                    rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
    	                    rowString.append("featureDataResourceTypeId" + "=\"" + featureDataResourceTypeId + "\" ");
                            rowString.append("/>");
                            bwOutFile.write(rowString.toString());
                            bwOutFile.newLine();
                            
            				
            			}

            		}
        		}
    	}
   	 catch (Exception e) 
   	  {
		 
	  }
   	 return mFeatureIdImageExists;
    }
    
    private static void buildProductContent(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl) {
        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        String masterProductId=null;
        String productId=null;
		try {

	        fOutFile = new File(xmlDataDirPath, "050-ProductContent.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                    StringBuilder  rowString = new StringBuilder();
	            	 Map mRow = (Map)dataRows.get(i);
	            	 masterProductId=(String)mRow.get("masterProductId");
	            	 productId=(String)mRow.get("productId");
	            	 if ((UtilValidate.isEmpty(productId)) || (UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId)))
	            	 {
	              		 addProductContent(rowString, mRow, bwOutFile, masterProductId,loadImagesDirPath, imageUrl);
	            	 }
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
    	}
      	 catch (Exception e) 
      	 {
   	     }
         finally {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
    }

    private static void buildProductVariantContent(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl) {
        File fOutFile =null;
        BufferedWriter bwOutFile=null;
		try {

	        fOutFile = new File(xmlDataDirPath, "055-ProductVariantContent.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
                    StringBuilder  rowString = new StringBuilder();
	            	Map mRow = (Map)dataRows.get(i);
	              	 
	              	String masterProductId=(String)mRow.get("masterProductId");
	            	String productId=(String)mRow.get("productId");
	              	if(UtilValidate.isNotEmpty(productId) && !productId.equals(masterProductId)) {
	              		addProductContent(rowString, mRow, bwOutFile, productId,loadImagesDirPath, imageUrl);
	              	}
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
            
			
    
    	}
      	 catch (Exception e) {
   	         }
         finally {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
    }
    private static void addProductContent(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile, String productId,String loadImagesDirPath,String imageUrl) {
    	
    	try 
    	{
			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","SMALL_IMAGE_URL", "smallImage", loadImagesDirPath, imageUrl,"smallImageThruDate");
             addProductContentRow(rowString, mRow, bwOutFile, productId,"image","SMALL_IMAGE_ALT_URL", "smallImageAlt", loadImagesDirPath, imageUrl,"smallImageAltThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PLP_SWATCH_IMAGE_URL", "plpSwatchImage", loadImagesDirPath, imageUrl,"plpSwatchImageThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PDP_SWATCH_IMAGE_URL", "pdpSwatchImage", loadImagesDirPath, imageUrl,"pdpSwatchImageThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","THUMBNAIL_IMAGE_URL", "thumbImage", loadImagesDirPath, imageUrl,"thumbImageThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","LARGE_IMAGE_URL", "largeImage", loadImagesDirPath, imageUrl,"largeImageThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","DETAIL_IMAGE_URL", "detailImage", loadImagesDirPath, imageUrl,"detailImageThruDate");

 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_1", "addImage1", loadImagesDirPath, imageUrl,"addImage1ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_1_LARGE", "xtraLargeImage1", loadImagesDirPath, imageUrl,"xtraLargeImage1ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_1_DETAIL", "xtraDetailImage1", loadImagesDirPath, imageUrl,"xtraDetailImage1ThruDate");
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_2", "addImage2", loadImagesDirPath, imageUrl,"addImage2ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_2_LARGE", "xtraLargeImage2", loadImagesDirPath, imageUrl,"xtraLargeImage2ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_2_DETAIL", "xtraDetailImage2", loadImagesDirPath, imageUrl,"xtraDetailImage2ThruDate");
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_3", "addImage3", loadImagesDirPath, imageUrl,"addImage3ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_3_LARGE", "xtraLargeImage3", loadImagesDirPath, imageUrl,"xtraLargeImage3ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_3_DETAIL", "xtraDetailImage3", loadImagesDirPath, imageUrl,"xtraDetailImage3ThruDate");
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_4", "addImage4", loadImagesDirPath, imageUrl,"addImage4ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_4_LARGE", "xtraLargeImage4", loadImagesDirPath, imageUrl,"xtraLargeImage4ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_4_DETAIL", "xtraDetailImage4", loadImagesDirPath, imageUrl,"xtraDetailImage4ThruDate");
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_5", "addImage5", loadImagesDirPath, imageUrl,"addImage5ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_5_LARGE", "xtraLargeImage5", loadImagesDirPath, imageUrl,"xtraLargeImage5ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_5_DETAIL", "xtraDetailImage5", loadImagesDirPath, imageUrl,"xtraDetailImage5ThruDate");

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_6", "addImage6", loadImagesDirPath, imageUrl,"addImage6ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_6_LARGE", "xtraLargeImage6", loadImagesDirPath, imageUrl,"xtraLargeImage6ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_6_DETAIL", "xtraDetailImage6", loadImagesDirPath, imageUrl,"xtraDetailImage6ThruDate");

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_7", "addImage7", loadImagesDirPath, imageUrl,"addImage7ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_7_LARGE", "xtraLargeImage7", loadImagesDirPath, imageUrl,"xtraLargeImage7ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_7_DETAIL", "xtraDetailImage7", loadImagesDirPath, imageUrl,"xtraDetailImage7ThruDate");

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_8", "addImage8", loadImagesDirPath, imageUrl,"addImage8ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_8_LARGE", "xtraLargeImage8", loadImagesDirPath, imageUrl,"xtraLargeImage8ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_8_DETAIL", "xtraDetailImage8", loadImagesDirPath, imageUrl,"xtraDetailImage8ThruDate");

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_9", "addImage9", loadImagesDirPath, imageUrl,"addImage9ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_9_LARGE", "xtraLargeImage9", loadImagesDirPath, imageUrl,"xtraLargeImage9ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_9_DETAIL", "xtraDetailImage9", loadImagesDirPath, imageUrl,"xtraDetailImage9ThruDate");

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_10", "addImage10", loadImagesDirPath, imageUrl,"addImage10ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_10_LARGE", "xtraLargeImage10", loadImagesDirPath, imageUrl,"xtraLargeImag10ThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_10_DETAIL", "xtraDetailImage10", loadImagesDirPath, imageUrl,"xtraDetailImage10ThruDate");

 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","PRODUCT_NAME", "productName", loadImagesDirPath, imageUrl,"productNameThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","SHORT_SALES_PITCH", "salesPitch", loadImagesDirPath, imageUrl,"salesPitchThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","LONG_DESCRIPTION", "longDescription", loadImagesDirPath, imageUrl,"longDescriptionThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","SPECIALINSTRUCTIONS", "specialInstructions", loadImagesDirPath, imageUrl,"specialInstructionsThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","DELIVERY_INFO", "deliveryInfo", loadImagesDirPath, imageUrl,"deliveryInfoThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","DIRECTIONS", "directions", loadImagesDirPath, imageUrl,"smallImageAltThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","TERMS_AND_CONDS", "termsConditions", loadImagesDirPath, imageUrl,"smallImageAltThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","INGREDIENTS", "ingredients", loadImagesDirPath, imageUrl,"termsConditionsThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","WARNINGS", "warnings", loadImagesDirPath, imageUrl,"warningsThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","PLP_LABEL", "plpLabel", loadImagesDirPath, imageUrl,"plpLabelThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","PDP_LABEL", "pdpLabel", loadImagesDirPath, imageUrl,"pdpLabelThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PDP_VIDEO_URL", "pdpVideoUrl", loadImagesDirPath, imageUrl,"pdpVideoUrlThruDate");
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PDP_VIDEO_360_URL", "pdpVideo360Url", loadImagesDirPath, imageUrl,"pdpVideo360UrlThruDate");
    		
    	}
    	 catch (Exception e)
    	 {
    		 
    	 }
    }
    private static void addProductContentRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String productId,String contentType,String productContentTypeId,String colName, String productImagesDirPath, String imageUrl,String colNameThruDate) {

		String contentId=null;
		String dataResourceId=null;
		Timestamp contentTimestamp=null;
    	try {
    		
			String contentValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(contentValue) && UtilValidate.isEmpty(contentValue.trim()))
			{
				return;
			}
			String contentValueThruDate=(String)mRow.get(colNameThruDate);
			List<GenericValue> lProductContent = _delegator.findByAnd("ProductContent", UtilMisc.toMap("productId",productId,"productContentTypeId",productContentTypeId),UtilMisc.toList("-fromDate"));
			if (UtilValidate.isNotEmpty(lProductContent))
			{
				GenericValue productContent = EntityUtil.getFirst(lProductContent);
				GenericValue content=productContent.getRelatedOne("Content");
				contentId=content.getString("contentId");
				dataResourceId=content.getString("dataResourceId");
				contentTimestamp =productContent.getTimestamp("fromDate");
			}
			else
			{
				contentId=_delegator.getNextSeqId("Content");
				dataResourceId=_delegator.getNextSeqId("DataResource");
				contentTimestamp =UtilDateTime.nowTimestamp();
			}

			if ("text".equals(contentType))
			{
	            rowString.setLength(0);
	            rowString.append("<" + "DataResource" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("dataResourceTypeId" + "=\"" + "ELECTRONIC_TEXT" + "\" ");
	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
	            rowString.append("dataResourceName" + "=\"" + colName + "\" ");
	            if(UtilValidate.isNotEmpty(localeString))
	            {
	            	rowString.append("localeString" + "=\"" + localeString + "\" ");
	            }
	            rowString.append("mimeTypeId" + "=\"" + "application/octet-stream" + "\" ");
	            rowString.append("objectInfo" + "=\"" + "" + "\" ");
	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();

	            rowString.setLength(0);
	            rowString.append("<" + "ElectronicText" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	        
	            rowString.append("textData" + "=\"" + contentValue + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();
	            
	            
			}
			else
			{
	            rowString.setLength(0);
	            rowString.append("<" + "DataResource" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("dataResourceTypeId" + "=\"" + "SHORT_TEXT" + "\" ");
	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
	            rowString.append("dataResourceName" + "=\"" + contentValue + "\" ");
	            if(UtilValidate.isNotEmpty(localeString))
	            {
	            	rowString.append("localeString" + "=\"" + localeString + "\" ");
	            }
	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
	            
	            if (!UtilValidate.isUrl(contentValue)) 
         		{
	            	Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
	            	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
	            		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
	            	}
	            	
	            	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
	            	if(UtilValidate.isNotEmpty(defaultImageDirectory)) 
	            	{
	            		contentValue = defaultImageDirectory + contentValue;	
	            	}
         		}
	           
	            rowString.append("objectInfo" + "=\"" + contentValue.trim() + "\" ");
	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();
			}

            rowString.setLength(0);
            rowString.append("<" + "Content" + " ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("contentTypeId" + "=\"" + "DOCUMENT" + "\" ");
            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
            rowString.append("contentName" + "=\"" + colName + "\" ");
            if(UtilValidate.isNotEmpty(localeString))
            {
            	rowString.append("localeString" + "=\"" + localeString + "\" ");
            }
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
			
            rowString.setLength(0);
            rowString.append("<" + "ProductContent" + " ");
            rowString.append("productId" + "=\"" + productId + "\" ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("productContentTypeId" + "=\"" + productContentTypeId + "\" ");
            rowString.append("fromDate" + "=\"" + _sdf.format(contentTimestamp) + "\" ");
			if (UtilValidate.isNotEmpty(contentValueThruDate))
			{
				java.util.Date formattedThuDate=OsafeAdminUtil.validDate(contentValueThruDate);
           	    contentValueThruDate =_sdf.format(formattedThuDate);
	            rowString.append("thruDate" + "=\"" + contentValueThruDate + "\" ");
			}
			else
			{
	            rowString.append("thruDate" + "=\"" + null + "\" ");
			}
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
    		
    	}
     	 catch (Exception e) {
	         }

     	 return;
    	
    }
    
    private static void addCategoryContentRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String contentType,String categoryContentType,String colName) {

		String objectImagePath = OSAFE_PROP.getString("productCategoryImagesPath");
		String contentId=null;
		String productCategoryId=null;
		String dataResourceId=null;
    	try {
    		
			String contentValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(contentValue) && UtilValidate.isEmpty(contentValue.trim()))
			{
				return;
			}
			productCategoryId=(String)mRow.get("productCategoryId");
			
	        List<GenericValue> lCategoryContent = _delegator.findByAnd("ProductCategoryContent", UtilMisc.toMap("productCategoryId",productCategoryId,"prodCatContentTypeId",categoryContentType),UtilMisc.toList("-fromDate"));
			if (UtilValidate.isNotEmpty(lCategoryContent))
			{
				GenericValue categoryContent = EntityUtil.getFirst(lCategoryContent);
				GenericValue content=categoryContent.getRelatedOne("Content");
				contentId=content.getString("contentId");
				dataResourceId=content.getString("dataResourceId");
			}
			else
			{
				contentId=_delegator.getNextSeqId("Content");
				dataResourceId=_delegator.getNextSeqId("DataResource");
				
			}
    		

			if ("text".equals(contentType))
			{
	            rowString.setLength(0);
	            rowString.append("<" + "DataResource" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("dataResourceTypeId" + "=\"" + "ELECTRONIC_TEXT" + "\" ");
	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
	            rowString.append("dataResourceName" + "=\"" + colName + "\" ");
	            if(UtilValidate.isNotEmpty(localeString))
	            {
	            	rowString.append("localeString" + "=\"" + localeString + "\" ");
	            }
	            rowString.append("mimeTypeId" + "=\"" + "application/octet-stream" + "\" ");
	            rowString.append("objectInfo" + "=\"" + "" + "\" ");
	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();

	            rowString.setLength(0);
	            rowString.append("<" + "ElectronicText" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("textData" + "=\"" +contentValue + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();
	            
	            
			}
			else
			{
	            rowString.setLength(0);
	            rowString.append("<" + "DataResource" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("dataResourceTypeId" + "=\"" + "SHORT_TEXT" + "\" ");
	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
	            rowString.append("dataResourceName" + "=\"" + contentValue + "\" ");
	            if(UtilValidate.isNotEmpty(localeString))
	            {
	            	rowString.append("localeString" + "=\"" + localeString + "\" ");
	            }
	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
	            
	            if (!UtilValidate.isUrl(contentValue)) 
         	    {
	            	Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
	            	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) 
	            	{
	            		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
	            	}
	            	
	            	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
	            	if(UtilValidate.isNotEmpty(defaultImageDirectory)) 
	            	{
		                contentValue = defaultImageDirectory + contentValue;
	            	}	
         		}
	            
	            rowString.append("objectInfo" + "=\"" + contentValue.trim() + "\" ");
	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();
			}

            rowString.setLength(0);
            rowString.append("<" + "Content" + " ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("contentTypeId" + "=\"" + "DOCUMENT" + "\" ");
            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
            rowString.append("contentName" + "=\"" + colName + "\" ");
            if(UtilValidate.isNotEmpty(localeString))
            {
            	rowString.append("localeString" + "=\"" + localeString + "\" ");
            }
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
			String sFromDate = (String)mRow.get("fromDate");
			if (UtilValidate.isEmpty(sFromDate))
			{
				sFromDate=_sdf.format(UtilDateTime.nowTimestamp());
			}
            rowString.setLength(0);
            rowString.append("<" + "ProductCategoryContent" + " ");
            rowString.append("productCategoryId" + "=\"" + productCategoryId + "\" ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("prodCatContentTypeId" + "=\"" + categoryContentType + "\" ");
            rowString.append("fromDate" + "=\"" + sFromDate + "\" ");
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
    		
    	}
     	 catch (Exception e) {
	         }

     	 return;
    	
    }
    private static void addProductGoodIdentificationRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String productId,String colName,String goodIdentificationTypeId) 
    {
    	try {
			String idValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(idValue))
			{
				return;
			}
            rowString.setLength(0);
            rowString.append("<" + "GoodIdentification" + " ");
       	    rowString.append("productId" + "=\"" + productId + "\" ");
            rowString.append("goodIdentificationTypeId" + "=\"" + goodIdentificationTypeId + "\" ");
            rowString.append("idValue" + "=\"" + idValue + "\" ");
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
    		
    	}
    	catch (Exception e)
    	{
    		
    	}
    }
    
    private static void addProductAttributeRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String productId,String colName,String attrName) 
    {
    	try {
			String attrValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(attrValue))
			{
				return;
			}
            rowString.setLength(0);
            rowString.append("<" + "ProductAttribute" + " ");
       	    rowString.append("productId" + "=\"" + productId + "\" ");
            rowString.append("attrName" + "=\"" + attrName + "\" ");
            rowString.append("attrValue" + "=\"" + attrValue + "\" ");
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
    		
    	}
    	catch (Exception e)
    	{
    		
    	}
    }

    private static String getProductCategoryContent(String productCategoryId ,String productcategoryContentTypeId,List lproductCategoryContent) {
		String contentText=null;

    	try {
    		List<GenericValue> lContent = EntityUtil.filterByCondition(lproductCategoryContent, EntityCondition.makeCondition("prodCatContentTypeId", EntityOperator.EQUALS, productcategoryContentTypeId));
			if (UtilValidate.isNotEmpty(lContent))
			{
                   return getContent(lContent);
            }
    		
    	}
     	 catch (Exception e) {
             Debug.logError(e, module);
	    }

     	 return contentText;
    	
    }
    
    private static String getProductContent(String productId ,String productContentTypeId,List lproductContent) {
		String contentText=null;

    	try {
    		
    		List<GenericValue> lContent = EntityUtil.filterByCondition(lproductContent, EntityCondition.makeCondition("productContentTypeId", EntityOperator.EQUALS, productContentTypeId));
			if (UtilValidate.isNotEmpty(lContent))
			{
                   return getContent(lContent);
            }
    	}
     	 catch (Exception e) {
             Debug.logError(e, module);
	    }

     	 return contentText;
    	
    }
    
    private static String getProductContentThruDate(String productId ,String productContentTypeId,List lproductContent) {
		String contentText=null;

    	try {
    		
    		List<GenericValue> lContent = EntityUtil.filterByCondition(lproductContent, EntityCondition.makeCondition("productContentTypeId", EntityOperator.EQUALS, productContentTypeId));
			if (UtilValidate.isNotEmpty(lContent))
			{
				GenericValue contentContent = EntityUtil.getFirst(lContent);
				
				Timestamp tsstamp = contentContent.getTimestamp("thruDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	return _sdf.format(new Date(tsstamp.getTime()));
                }
                else
                {
                	return "";
                }
                
            }
    	}
     	 catch (Exception e) {
             Debug.logError(e, module);
	    }

     	 return contentText;
    	
    }

    private static String getPartyContent(String partyId ,String partyContentTypeId,List lpartyContent) {
		String contentText=null;

    	try {
    		
    		List<GenericValue> lContent = EntityUtil.filterByCondition(lpartyContent, EntityCondition.makeCondition("partyContentTypeId", EntityOperator.EQUALS, partyContentTypeId));
			if (UtilValidate.isNotEmpty(lContent))
			{
                   return getContent(lContent);
            }
    		
    	}
     	 catch (Exception e) {
             Debug.logError(e, module);
	    }

     	 return contentText;
    }
    
    private static String getPartyContentThruDate(String partyId ,String partyContentTypeId,List lpartyContent) {
		String contentText=null;

    	try {
    		
    		List<GenericValue> lContent = EntityUtil.filterByCondition(lpartyContent, EntityCondition.makeCondition("partyContentTypeId", EntityOperator.EQUALS, partyContentTypeId));
			if (UtilValidate.isNotEmpty(lContent))
			{
				GenericValue contentContent = EntityUtil.getFirst(lContent);
				
				Timestamp tsstamp = contentContent.getTimestamp("thruDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	return _sdf.format(new Date(tsstamp.getTime()));
                }
                else
                {
                	return "";
                }
                
            }
    	}
     	 catch (Exception e) {
             Debug.logError(e, module);
	    }

     	 return contentText;
    	
    }

    private static String getContent(List lContent) {
		String contentText=null;

    	try {
    		
				GenericValue contentContent = EntityUtil.getFirst(lContent);
				GenericValue content=contentContent.getRelatedOne("Content");
				GenericValue dataResource=content.getRelatedOne("DataResource");
				String dataResourceTypeId=dataResource.getString("dataResourceTypeId");
				if ("ELECTRONIC_TEXT".equals(dataResourceTypeId))
				{
					GenericValue electronicText=dataResource.getRelatedOne("ElectronicText");
					return electronicText.getString("textData");
					
				}
				else if ("SHORT_TEXT".equals(dataResourceTypeId))
				{
					return dataResource.getString("objectInfo");
				}
    		
    	}
     	 catch (Exception e) {
             Debug.logError(e, module);
	    }

     	 return contentText;
    	
    }

    private static void addPartyContentRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String contentType,String partyContentType,String colName,String imagesDirPath, String imageUrl, String colNameThruDate) {

		String contentId=null;
		String partyId=null;
		String dataResourceId=null;
		Timestamp contentTimestamp = null;
    	try {
    		
			String contentValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(contentValue) && UtilValidate.isEmpty(contentValue.trim()))
			{
				return;
			}
			partyId=(String)mRow.get("partyId");
			String contentValueThruDate=(String)mRow.get(colNameThruDate);
	        List<GenericValue> lPartyContent = _delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",partyId,"partyContentTypeId",partyContentType),UtilMisc.toList("-fromDate"));
			if (UtilValidate.isNotEmpty(lPartyContent))
			{
				GenericValue partyContent = EntityUtil.getFirst(lPartyContent);
				GenericValue content=partyContent.getRelatedOne("Content");
				contentId=content.getString("contentId");
				dataResourceId=content.getString("dataResourceId");
				contentTimestamp =partyContent.getTimestamp("fromDate");
			}
			else
			{
				contentId=_delegator.getNextSeqId("Content");
				dataResourceId=_delegator.getNextSeqId("DataResource");
				contentTimestamp =UtilDateTime.nowTimestamp();
			}

			if ("text".equals(contentType))
			{
	            rowString.setLength(0);
	            rowString.append("<" + "DataResource" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("dataResourceTypeId" + "=\"" + "ELECTRONIC_TEXT" + "\" ");
	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
	            rowString.append("dataResourceName" + "=\"" + colName + "\" ");
	            if(UtilValidate.isNotEmpty(localeString))
	            {
	            	rowString.append("localeString" + "=\"" + localeString + "\" ");
	            }
	            rowString.append("mimeTypeId" + "=\"" + "application/octet-stream" + "\" ");
	            rowString.append("objectInfo" + "=\"" + "" + "\" ");
	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();

	            rowString.setLength(0);
	            rowString.append("<" + "ElectronicText" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("textData" + "=\"" + contentValue + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();
			}
			else
			{
	            rowString.setLength(0);
	            rowString.append("<" + "DataResource" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("dataResourceTypeId" + "=\"" + "SHORT_TEXT" + "\" ");
	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
	            rowString.append("dataResourceName" + "=\"" + contentValue + "\" ");
	            if(UtilValidate.isNotEmpty(localeString))
	            {
	            	rowString.append("localeString" + "=\"" + localeString + "\" ");
	            }
	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
	            
	            if (!UtilValidate.isUrl(contentValue)) 
         	    {
	            	Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
	            	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) 
	            	{
	            		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
	            	}
	            	
	            	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
	            	if(UtilValidate.isNotEmpty(defaultImageDirectory)) 
	            	{
		                contentValue = defaultImageDirectory + contentValue;
	            	}	
         	    }
	            
	            
	            rowString.append("objectInfo" + "=\"" + contentValue.trim() + "\" ");
	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();
			}

            rowString.setLength(0);
            rowString.append("<" + "Content" + " ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("contentTypeId" + "=\"" + "DOCUMENT" + "\" ");
            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
            rowString.append("contentName" + "=\"" + colName + "\" ");
            if(UtilValidate.isNotEmpty(localeString))
            {
            	rowString.append("localeString" + "=\"" + localeString + "\" ");
            }
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
			
            rowString.setLength(0);
            rowString.append("<" + "PartyContent" + " ");
            rowString.append("partyId" + "=\"" + partyId + "\" ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("partyContentTypeId" + "=\"" + partyContentType + "\" ");
            rowString.append("fromDate" + "=\"" + _sdf.format(contentTimestamp) + "\" ");
            if (UtilValidate.isNotEmpty(contentValueThruDate))
			{
            	java.util.Date formattedThuDate=OsafeAdminUtil.validDate(contentValueThruDate);
           	    contentValueThruDate =_sdf.format(formattedThuDate);
            	rowString.append("thruDate" + "=\"" + contentValueThruDate + "\" ");
			}
			else
			{
	            rowString.append("thruDate" + "=\"" + null + "\" ");
			}
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
    		
    	}
     	 catch (Exception e) {
	         }

     	 return;
    	
    }
    
    
    private static void buildProductCategoryFeatures(List dataRows,String xmlDataDirPath, Boolean removeAll) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
		Map mFeatureExists = FastMap.newInstance();
		Map mFeatureTypeExists = FastMap.newInstance();
		Map mFeatureCategoryGroupApplExists = FastMap.newInstance();
		Map mFeatureGroupExists = FastMap.newInstance();
		Map mFeatureGroupApplExists = FastMap.newInstance();
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        String productId = null;
        String productCategoryId =null;
        String[] productCategoryIds =null;
        Map mProductCategoryIds = FastMap.newInstance();
		try {
			
	        fOutFile = new File(xmlDataDirPath, "010-ProductCategoryFeature.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                Map productFeatureSequenceMap = FastMap.newInstance();
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
              	    Map mRow = (Map)dataRows.get(i);
              	  
              	    int totSelectableFeatures = 5;
            	    if(UtilValidate.isNotEmpty(mRow.get("totSelectableFeatures"))) 
            	    {
            	    	totSelectableFeatures =  Integer.parseInt((String)mRow.get("totSelectableFeatures"));
				    }
        	    
        	        for(int j = 1; j <= totSelectableFeatures; j++)
        	        {
        	    	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("selectabeFeature_"+j));
        	        }
              	    int totDescriptiveFeatures = 5;
              	    if(UtilValidate.isNotEmpty(mRow.get("totDescriptiveFeatures"))) 
              	    {
          	    	    totDescriptiveFeatures =  Integer.parseInt((String)mRow.get("totDescriptiveFeatures"));
				    }
          	    
          	        for(int j = 1; j <= totDescriptiveFeatures; j++)
          	        {
          	    	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("descriptiveFeature_"+j));
          	        }
              	    
	            	masterProductId=(String)mRow.get("masterProductId");
	            	productId = (String)mRow.get("productId");
	            	if ((UtilValidate.isEmpty(productId)) || (UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId)))
	            	{
	                     productCategoryId = (String)mRow.get("productCategoryId");
	                     if(UtilValidate.isNotEmpty(productCategoryId)) 
	                     {
	                    	 productCategoryIds = productCategoryId.split(",");
	                    	 mProductCategoryIds.put(masterProductId, productCategoryIds);
	                     }
	            	}
	            	else
	            	{
	            		if(mProductCategoryIds.containsKey(masterProductId)) {
	            			productCategoryIds = (String[]) mProductCategoryIds.get(masterProductId);	
	            		}
	            	}
	            	if (mFeatureTypeMap.size() > 0)
	            	{
	            		Set featureTypeSet = mFeatureTypeMap.keySet();
	            		Iterator iterFeatureType = featureTypeSet.iterator();
	            		int seqNumber = 0;
	            		while (iterFeatureType.hasNext())
	            		{
	            			
	            			String featureType =(String)iterFeatureType.next();
	            			String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
                			if (featureTypeId.length() > 20)
                			{
                				featureTypeId=featureTypeId.substring(0,20);
                			}
	            			if (!mFeatureTypeExists.containsKey(featureType))
	            			{
	            				mFeatureTypeExists.put(featureType,featureType);
	                            rowString.setLength(0);
	                            rowString.append("<" + "ProductFeatureType" + " ");
	                            rowString.append("productFeatureTypeId" + "=\"" + featureTypeId + "\" ");
	                            rowString.append("parentTypeId" + "=\"" + "" + "\" ");
	                            rowString.append("hasTable" + "=\"" + "N" + "\" ");
	                            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_description")))
	                            {
	                            	rowString.append("description" + "=\"" + mRow.get(featureType.trim()+"_description") + "\" ");
	                            }
	                            else
	                            {
	                            	rowString.append("description" + "=\"" + featureType + "\" ");
	                            }
	                            rowString.append("/>");
	                            bwOutFile.write(rowString.toString());
	                            bwOutFile.newLine();
	                            
	                            rowString.setLength(0);
	                            rowString.append("<" + "ProductFeatureCategory" + " ");
	                            rowString.append("productFeatureCategoryId" + "=\"" + featureTypeId + "\" ");
	                            rowString.append("parentCategoryId" + "=\"" + "" + "\" ");
	                            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_description")))
	                            {
	                            	rowString.append("description" + "=\"" + mRow.get(featureType.trim()+"_description") + "\" ");
	                            }
	                            else
	                            {
	                            	rowString.append("description" + "=\"" + featureType + "\" ");
	                            }
	                            rowString.append("/>");
	                            bwOutFile.write(rowString.toString());
	                            bwOutFile.newLine();

	                            
	                            rowString.setLength(0);
	                            rowString.append("<" + "ProductFeatureGroup" + " ");
	                            rowString.append("productFeatureGroupId" + "=\"" + featureTypeId + "\" ");
	                            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_description")))
	                            {
	                            	rowString.append("description" + "=\"" + mRow.get(featureType.trim()+"_description") + "\" ");
	                            }
	                            else
	                            {
	                            	rowString.append("description" + "=\"" + featureType + "\" ");
	                            }
	                            rowString.append("/>");
	                            bwOutFile.write(rowString.toString());
	                            bwOutFile.newLine();
	            				
	            			}
	            			
	            			if(UtilValidate.isNotEmpty(productCategoryIds)) 
	            			{
	            				for (int j=0;j < productCategoryIds.length;j++)
		                        {
		 	                        String sProductCategoryId= productCategoryIds[j].trim();
			            			if (UtilValidate.isNotEmpty(sProductCategoryId) && !mFeatureCategoryGroupApplExists.containsKey(sProductCategoryId+"_"+featureTypeId))
			            			{
			            				mFeatureCategoryGroupApplExists.put(sProductCategoryId+"_"+featureTypeId,sProductCategoryId+"_"+featureTypeId);
			            				
			            				String productFeatureCatGrpApplFromDate = _sdf.format(UtilDateTime.nowTimestamp());
			            				List<GenericValue> productFeatureCatGrpApplList = _delegator.findByAnd("ProductFeatureCatGrpAppl", UtilMisc.toMap("productCategoryId", sProductCategoryId, "productFeatureGroupId", featureTypeId),UtilMisc.toList("-fromDate"));
			            				productFeatureCatGrpApplList = EntityUtil.filterByDate(productFeatureCatGrpApplList);
			            				if(UtilValidate.isNotEmpty(productFeatureCatGrpApplList))
			            				{
			            					GenericValue productFeatureCatGrpAppl = EntityUtil.getFirst(productFeatureCatGrpApplList);
			            					productFeatureCatGrpApplFromDate = _sdf.format(new Date(productFeatureCatGrpAppl.getTimestamp("fromDate").getTime()));
			            				}
			            				rowString.setLength(0);
			                            rowString.append("<" + "ProductFeatureCatGrpAppl" + " ");
			                            rowString.append("productCategoryId" + "=\"" + sProductCategoryId + "\" ");
			                            rowString.append("productFeatureGroupId" + "=\"" + featureTypeId + "\" ");
			    	                    rowString.append("fromDate" + "=\"" + productFeatureCatGrpApplFromDate + "\" ");
			    	                    rowString.append("sequenceNum" + "=\"" + ((seqNumber +1) *10) + "\" ");
			                            rowString.append("/>");
			                            bwOutFile.write(rowString.toString());
			                            bwOutFile.newLine();
			            				
			                            String productFeatureCategoryApplFromDate = _sdf.format(UtilDateTime.nowTimestamp());
			            				List<GenericValue> productFeatureCategoryApplList = _delegator.findByAnd("ProductFeatureCategoryAppl", UtilMisc.toMap("productCategoryId", sProductCategoryId, "productFeatureCategoryId", featureTypeId),UtilMisc.toList("-fromDate"));
			            				productFeatureCategoryApplList = EntityUtil.filterByDate(productFeatureCategoryApplList);
			            				if(UtilValidate.isNotEmpty(productFeatureCategoryApplList))
			            				{
			            					GenericValue productFeatureCategoryAppl = EntityUtil.getFirst(productFeatureCategoryApplList);
			            					productFeatureCategoryApplFromDate = _sdf.format(new Date(productFeatureCategoryAppl.getTimestamp("fromDate").getTime()));
			            				}
			            				
			            				rowString.setLength(0);
			                            rowString.append("<" + "ProductFeatureCategoryAppl" + " ");
			                            rowString.append("productCategoryId" + "=\"" + sProductCategoryId + "\" ");
			                            rowString.append("productFeatureCategoryId" + "=\"" + featureTypeId + "\" ");
			    	                    rowString.append("fromDate" + "=\"" + productFeatureCategoryApplFromDate + "\" ");
			                            rowString.append("/>");
			                            bwOutFile.write(rowString.toString());
			                            bwOutFile.newLine();
			            			}
		                        	
		                        }
	            			}
	                        
	            			
                            FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
	                		Set featureSet = mFeatureMap.keySet();
	                		Iterator iterFeature = featureSet.iterator();
	                		int iSeq=0;
	                		
	                		while (iterFeature.hasNext())
	                		{
	                			String featureId =(String)iterFeature.next();
	                			String featureDescription = (String) mFeatureMap.get(featureId);
	                			/*String featureId =StringUtil.removeSpaces(feature).toUpperCase();
	                			featureId =StringUtil.replaceString(featureId, "&", "");
	                			featureId=featureTypeId+"_"+featureId;
	                			if (featureId.length() > 20)
	                			{
	                				featureId=featureId.substring(0,20);
	                			}*/
		            			if (!mFeatureExists.containsKey(featureId))
		            			{
		            				mFeatureExists.put(featureId,featureId);
	 	                            rowString.setLength(0);
		                            rowString.append("<" + "ProductFeature" + " ");
		                            rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
		                            rowString.append("productFeatureTypeId" + "=\"" + featureTypeId + "\" ");
		                            rowString.append("productFeatureCategoryId" + "=\"" + featureTypeId + "\" ");
		                            rowString.append("description" + "=\"" + featureDescription + "\" ");
		                            rowString.append("/>");
		                           bwOutFile.write(rowString.toString());
		                           bwOutFile.newLine();
		            			}

		            			if (!mFeatureGroupApplExists.containsKey(featureId))
		            			{
		            				mFeatureGroupApplExists.put(featureId,featureId);
		            				
		            				
		            				
		            				
		            				String productFeatureGroupApplFromDate = _sdf.format(UtilDateTime.nowTimestamp());
		            				List<GenericValue> productFeatureGroupApplList = _delegator.findByAnd("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", featureTypeId, "productFeatureId", featureId),UtilMisc.toList("-fromDate"));
		            				productFeatureGroupApplList = EntityUtil.filterByDate(productFeatureGroupApplList);
		            				
		            				if(UtilValidate.isNotEmpty(productFeatureGroupApplList))
		            				{
		            					GenericValue productFeatureGroupAppl = EntityUtil.getFirst(productFeatureGroupApplList);
		            					productFeatureGroupApplFromDate = _sdf.format(new Date(productFeatureGroupAppl.getTimestamp("fromDate").getTime()));
		            				}
		            				
		            				Map entityFieldMap = FastMap.newInstance();
		                            rowString.setLength(0);
		                            rowString.append("<" + "ProductFeatureGroupAppl" + " ");
		                            rowString.append("productFeatureGroupId" + "=\"" + featureTypeId + "\" ");
		                            rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
		    	                    rowString.append("fromDate" + "=\"" + productFeatureGroupApplFromDate + "\" ");
		    	                    if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_sequenceNum"))) 
	                	            {
	                	            	rowString.append("sequenceNum" + "=\"" + (String) mRow.get(featureType.trim()+"_sequenceNum") + "\" ");
	                	            } 
	                	            else 
	                	            {
		                	            rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
	                	            }
		                            rowString.append("/>");
		                            
		                            if(UtilValidate.isEmpty((String) mRow.get(featureType.trim()+"_sequenceNum"))) 
		                            {
		                            	entityFieldMap.put("productFeatureGroupId", featureTypeId);
			                            entityFieldMap.put("productFeatureId", featureId);
			                            entityFieldMap.put("fromDate", productFeatureGroupApplFromDate);
			                            productFeatureSequenceMap.put(entityFieldMap, featureDescription);
		                            }
		                            bwOutFile.write(rowString.toString());
		                            bwOutFile.newLine();
		            			}
		            			iSeq++;
	                			
	                		}
	                		seqNumber++;
	            		}
	            	}
	            }
                if(UtilValidate.isNotEmpty(productFeatureSequenceMap))
                {
                	buildFeatureSequence(rowString, bwOutFile, productFeatureSequenceMap, "ProductFeatureGroupAppl");	
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

    private static void buildProductDistinguishingFeatures(List dataRows,String xmlDataDirPath ) 
    {
        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        String variantProductId=null;
        Map mMasterProductId=FastMap.newInstance();
        
		try 
		{
			
	        fOutFile = new File(xmlDataDirPath, "060-ProductDistinguishingFeature.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                Map productFeatureSequenceMap = FastMap.newInstance();
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
              	    Map mRow = (Map)dataRows.get(i);
	            	masterProductId=(String)mRow.get("masterProductId");
	            	variantProductId = (String)mRow.get("productId");
             		mFeatureTypeMap.clear();
             		int totDescriptiveFeatures = 5;
            	    if(UtilValidate.isNotEmpty(mRow.get("totDescriptiveFeatures"))) 
            	    {
            	    	totDescriptiveFeatures =  Integer.parseInt((String)mRow.get("totDescriptiveFeatures"));
				    }
            	    
            	    for(int j = 1; j <= totDescriptiveFeatures; j++)
            	    {
            	    	buildFeatureMap(mFeatureTypeMap, (String)mRow.get("descriptiveFeature_"+j));
            	    }
              	    
	            	if (mFeatureTypeMap.size() > 0)
	            	{
	            		Set featureTypeSet = mFeatureTypeMap.keySet();
	            		Iterator iterFeatureType = featureTypeSet.iterator();
	            		while (iterFeatureType.hasNext())
	            		{
	            			String featureType =(String)iterFeatureType.next();
	            			String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
	            			
	            			
                			if (featureTypeId.length() > 20)
                			{
                				featureTypeId=featureTypeId.substring(0,20);
                			}
	            			FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
	                		Set featureSet = mFeatureMap.keySet();
	                		Iterator iterFeature = featureSet.iterator();
	                		int iSeq=0;
	                		while (iterFeature.hasNext())
	                		{
	                			String featureId =(String)iterFeature.next();
	                			String featureValue = (String) mFeatureMap.get(featureId);
	                			/*String featureId =StringUtil.removeSpaces(feature).toUpperCase();
	                			featureId =StringUtil.replaceString(featureId, "&", "");
	                			featureId=featureTypeId+"_"+featureId;
	                			if (featureId.length() > 20)
	                			{
	                				featureId=featureId.substring(0,20);
	                			}*/
		       		            	
		       		            String featureFromDate = _sdf.format(UtilDateTime.nowTimestamp());
		       		            if(UtilValidate.isEmpty((String) mRow.get(featureType.trim()+"_fromDate"))) 
		       		            {
		    		                List<GenericValue> productFeatureAppls = _delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId",masterProductId,"productFeatureId",featureId, "productFeatureApplTypeId","DISTINGUISHING_FEAT"),UtilMisc.toList("-fromDate"));
		    		                if(UtilValidate.isNotEmpty(productFeatureAppls))
		    		                {
		    		                	productFeatureAppls = EntityUtil.filterByDate(productFeatureAppls);
		    		                	if(UtilValidate.isNotEmpty(productFeatureAppls)) 
		    		                	{
		    		                        GenericValue productFeatureAppl = EntityUtil.getFirst(productFeatureAppls);
		    		                    	featureFromDate = _sdf.format(new Date(productFeatureAppl.getTimestamp("fromDate").getTime()));
		    		                    }
		    		                }
		    		            } 
		       		            else 
		       		            {
		    		            	 featureFromDate = (String) mRow.get(featureType.trim()+"_fromDate");
					      			 java.util.Date formattedFromDate=OsafeAdminUtil.validDate(featureFromDate);
					      			 featureFromDate =_sdf.format(formattedFromDate);
		    		            }
		       		            Map entityFieldMap = FastMap.newInstance();	
		                		rowString.setLength(0);
		       	                rowString.append("<" + "ProductFeatureAppl" + " ");
		    	                rowString.append("productId" + "=\"" + masterProductId+ "\" ");
		    	                rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
		    	                rowString.append("productFeatureApplTypeId" + "=\"" + "DISTINGUISHING_FEAT" + "\" ");
		    	                rowString.append("fromDate" + "=\"" + featureFromDate + "\" ");
	                	        if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_thruDate"))) 
	                	        {
	                	        	String featureThruDate = (String) mRow.get(featureType.trim()+"_thruDate");
					      			java.util.Date formattedFromDate=OsafeAdminUtil.validDate(featureFromDate);
					      			featureThruDate =_sdf.format(formattedFromDate);
	                	        	rowString.append("thruDate" + "=\"" + featureThruDate + "\" ");
	                	        }
                	            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_sequenceNum"))) 
                	            {
                	            	rowString.append("sequenceNum" + "=\"" + (String) mRow.get(featureType.trim()+"_sequenceNum") + "\" ");
                	            } 
                	            else 
                	            {
	                	            rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
                	            }
		                        rowString.append("/>");
		                        
		                        entityFieldMap.put("productId", masterProductId);
		                        entityFieldMap.put("productFeatureId", featureId);
		                        entityFieldMap.put("fromDate", featureFromDate);
                	            productFeatureSequenceMap.put(entityFieldMap, featureValue);
                	            
		                        bwOutFile.write(rowString.toString());
		                        bwOutFile.newLine();
		       		            
		                  	    if (UtilValidate.isNotEmpty(variantProductId) && !(masterProductId.equals(variantProductId)))
		                  	    {
		                  	    	featureFromDate = _sdf.format(UtilDateTime.nowTimestamp());
		                  	    	
		                  	    	if(UtilValidate.isEmpty((String) mRow.get(featureType.trim()+"_fromDate"))) 
		                  	    	{
		    		                    List<GenericValue> productFeatureAppls = _delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId",variantProductId,"productFeatureId",featureId, "productFeatureApplTypeId","DISTINGUISHING_FEAT"),UtilMisc.toList("-fromDate"));
		    		                    if(UtilValidate.isNotEmpty(productFeatureAppls))
		    		                    {
		    		                    	productFeatureAppls = EntityUtil.filterByDate(productFeatureAppls);
		    		                    	if(UtilValidate.isNotEmpty(productFeatureAppls)) 
		    		                    	{
		    		                    	    GenericValue productFeatureAppl = EntityUtil.getFirst(productFeatureAppls);
		    		                    	    featureFromDate = _sdf.format(new Date(productFeatureAppl.getTimestamp("fromDate").getTime()));
		    		                    	}
		    		                    }
		    		                } 
		                  	    	else 
		                  	    	{
		    		                	 featureFromDate = (String) mRow.get(featureType.trim()+"_fromDate");
						      			 java.util.Date formattedFromDate=OsafeAdminUtil.validDate(featureFromDate);
						      			 featureFromDate =_sdf.format(formattedFromDate);
		    		                }
		                  	    	
		                  	    	entityFieldMap = FastMap.newInstance();
		                            rowString.setLength(0);
		       	                    rowString.append("<" + "ProductFeatureAppl" + " ");
		    	                    rowString.append("productId" + "=\"" + variantProductId+ "\" ");
		    	                    rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
		    	                    rowString.append("productFeatureApplTypeId" + "=\"" + "DISTINGUISHING_FEAT" + "\" ");
		    	                    rowString.append("fromDate" + "=\"" + featureFromDate + "\" ");
	                	            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_thruDate"))) 
	                	            {
	                	            	String featureThruDate = (String) mRow.get(featureType.trim()+"_thruDate");
						      		    java.util.Date formattedFromDate=OsafeAdminUtil.validDate(featureThruDate);
						      		    featureThruDate =_sdf.format(formattedFromDate);
	                	            	rowString.append("thruDate" + "=\"" + featureThruDate + "\" ");
	                	            }
	                	            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_sequenceNum"))) 
	                	            {
	                	            	rowString.append("sequenceNum" + "=\"" + (String) mRow.get(featureType.trim()+"_sequenceNum") + "\" ");
	                	            } 
	                	            else 
	                	            {
		                	            rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
	                	            }
		                            rowString.append("/>");
		                            
		                            entityFieldMap.put("productId", variantProductId);
		                            entityFieldMap.put("productFeatureId", featureId);
		                            entityFieldMap.put("fromDate", featureFromDate);
	                	            productFeatureSequenceMap.put(entityFieldMap, featureValue);
		                            bwOutFile.write(rowString.toString());
		                            bwOutFile.newLine();
		                            iSeq++;
		                  	    }
	                		}
	            		}
            	    }	            	 
	            }
                if(UtilValidate.isNotEmpty(productFeatureSequenceMap))
                {
                	buildFeatureSequence(rowString, bwOutFile, productFeatureSequenceMap, "ProductFeatureAppl");	
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
    
    private static void buildProductSelectableFeatures(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        String productId=null;
        
		try 
		{
			
	        fOutFile = new File(xmlDataDirPath, "043-ProductSelectableFeature.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                Map productFeatureSequenceMap = FastMap.newInstance();
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
              	    Map mRow = (Map)dataRows.get(i);
              	    masterProductId=(String)mRow.get("masterProductId");
  			        productId=(String)mRow.get("productId");
  			        
              	    mFeatureTypeMap.clear();
              	    
              	    int totSelectableFeatures = 5;
              	    if(UtilValidate.isNotEmpty(mRow.get("totSelectableFeatures"))) 
              	    {
              	    	totSelectableFeatures =  Integer.parseInt((String)mRow.get("totSelectableFeatures"));
				    }
              	    
              	    for(int j = 1; j <= totSelectableFeatures; j++)
          	        {
          	    	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("selectabeFeature_"+j));
          	        }
              	    
                    int iSeq=0;
                    	
              	        if(mFeatureTypeMap.size() > 0) 
              	        {
              	    	    Set featureTypeSet = mFeatureTypeMap.keySet();
	            		    Iterator iterFeatureType = featureTypeSet.iterator(); 
	            		    while (iterFeatureType.hasNext())
	            		    {
	            			    String featureType =(String)iterFeatureType.next();
	            			    String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
	            			    
                			    if (featureTypeId.length() > 20)
                			    {
                				    featureTypeId=featureTypeId.substring(0,20);
                			    }
                			    FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
	                		    Set featureSet = mFeatureMap.keySet();
	                		    Iterator iterFeature = featureSet.iterator();
	                		    
	                		    while (iterFeature.hasNext())
	                		    {
	                			    String featureId =(String)iterFeature.next();
	                			    String featureValue = (String) mFeatureMap.get(featureId);
	                			    /*String featureId = feature;
	                			    StringUtil.removeSpaces(feature).toUpperCase();
	                			    featureId=featureTypeId+"_"+featureId;
	                			    if (featureId.length() > 20)
	                			    {
	                				    featureId=featureId.substring(0,20);
	                			    }*/
	                			    
	                			    String featureFromDate = _sdf.format(UtilDateTime.nowTimestamp());
	                			    if(UtilValidate.isEmpty((String) mRow.get(featureType.trim()+"_fromDate"))) 
	                			    {
		    		                    List<GenericValue> productFeatureAppls = _delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId",productId,"productFeatureId",featureId, "productFeatureApplTypeId","STANDARD_FEATURE"),UtilMisc.toList("-fromDate"));
		    		                    if(UtilValidate.isNotEmpty(productFeatureAppls))
		    		                    {
		    		                    	productFeatureAppls = EntityUtil.filterByDate(productFeatureAppls);
		    		                    	if(UtilValidate.isNotEmpty(productFeatureAppls)) 
		    		                    	{
		    		                    	    GenericValue productFeatureAppl = EntityUtil.getFirst(productFeatureAppls);
		    		                    	    featureFromDate = _sdf.format(new Date(productFeatureAppl.getTimestamp("fromDate").getTime()));
		    		                    	}
		    		                    }
		    		                } 
	                			    else 
	                			    {
		    		                	featureFromDate = (String) mRow.get(featureType.trim()+"_fromDate");
		    		                }
	                			    Map entityFieldMap = FastMap.newInstance();
	                			    rowString.setLength(0);
	                	            rowString.append("<" + "ProductFeatureAppl" + " ");
	                	            rowString.append("productId" + "=\"" + productId + "\" ");
	                	            rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
	                	            rowString.append("productFeatureApplTypeId" + "=\"" + "STANDARD_FEATURE" + "\" ");
	                	            rowString.append("fromDate" + "=\"" + featureFromDate + "\" ");
	                	            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_thruDate"))) 
	                	            {
	                	            	String featureThruDate = (String) mRow.get(featureType.trim()+"_thruDate");
						      		    java.util.Date formattedFromDate=OsafeAdminUtil.validDate(featureThruDate);
						      		    featureThruDate =_sdf.format(formattedFromDate);
	                	            	rowString.append("thruDate" + "=\"" + featureThruDate + "\" ");
	                	            }
	                	            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_sequenceNum"))) 
	                	            {
	                	            	rowString.append("sequenceNum" + "=\"" + (String) mRow.get(featureType.trim()+"_sequenceNum") + "\" ");
	                	            } 
	                	            else 
	                	            {
		                	            rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
	                	            }
	                	            rowString.append("/>");
	                	            
	                	            if(UtilValidate.isEmpty((String) mRow.get(featureType.trim()+"_sequenceNum")))
	                	            {
	                	            	entityFieldMap.put("productId", productId);
		                	            entityFieldMap.put("productFeatureId", featureId);
		                	            entityFieldMap.put("fromDate", featureFromDate);
		                	            productFeatureSequenceMap.put(entityFieldMap, featureValue);
	                	            }
	                	            
	                	            bwOutFile.write(rowString.toString());
	                	            bwOutFile.newLine();

	                	           
	                	            if(UtilValidate.isEmpty((String) mRow.get(featureType.trim()+"_fromDate"))) 
	                	            {
		    		                    List<GenericValue> productFeatureAppls = _delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId",masterProductId,"productFeatureId",featureId, "productFeatureApplTypeId","SELECTABLE_FEATURE"),UtilMisc.toList("-fromDate"));
		    		                    if(UtilValidate.isNotEmpty(productFeatureAppls))
		    		                    {
		    		                    	productFeatureAppls = EntityUtil.filterByDate(productFeatureAppls);
		    		                    	if(UtilValidate.isNotEmpty(productFeatureAppls)) 
		    		                    	{
		    		                    	    GenericValue productFeatureAppl = EntityUtil.getFirst(productFeatureAppls);
		    		                    	    featureFromDate = _sdf.format(new Date(productFeatureAppl.getTimestamp("fromDate").getTime()));
		    		                    	}
		    		                    }
		    		                } 
	                	            else 
	                	            {
		    		                	featureFromDate = (String) mRow.get(featureType.trim()+"_fromDate");
		    		                }
	                	            entityFieldMap = FastMap.newInstance();
	                	            rowString.setLength(0);
	                	            rowString.append("<" + "ProductFeatureAppl" + " ");
	                	            rowString.append("productId" + "=\"" + masterProductId + "\" ");
	                	            rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
	                	            rowString.append("productFeatureApplTypeId" + "=\"" + "SELECTABLE_FEATURE" + "\" ");
	                	            rowString.append("fromDate" + "=\"" + featureFromDate + "\" ");
	                	            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_thruDate"))) 
	                	            {
	                	            	String featureThruDate = (String) mRow.get(featureType.trim()+"_thruDate");
						      		    java.util.Date formattedFromDate=OsafeAdminUtil.validDate(featureThruDate);
						      		    featureThruDate =_sdf.format(formattedFromDate);
	                	            	rowString.append("thruDate" + "=\"" + featureThruDate + "\" ");
	                	            }
	                	            if(UtilValidate.isNotEmpty((String) mRow.get(featureType.trim()+"_sequenceNum"))) 
	                	            {
	                	            	rowString.append("sequenceNum" + "=\"" + (String) mRow.get(featureType.trim()+"_sequenceNum") + "\" ");
	                	            } 
	                	            else 
	                	            {
		                	            rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
	                	            }
	                	            rowString.append("/>");
	                	            if(UtilValidate.isEmpty((String) mRow.get(featureType.trim()+"_sequenceNum")))
	                	            {
	                	            	entityFieldMap.put("productId", masterProductId);
		                	            entityFieldMap.put("productFeatureId", featureId);
		                	            entityFieldMap.put("fromDate", featureFromDate);
		                	            productFeatureSequenceMap.put(entityFieldMap, featureValue);
	                	            }
	                	            
	                	            bwOutFile.write(rowString.toString());
	                	            bwOutFile.newLine();
	                			
	                		    }
	            		    }
              	        }
	            }
                if(UtilValidate.isNotEmpty(productFeatureSequenceMap))
                {
                	buildFeatureSequence(rowString, bwOutFile, productFeatureSequenceMap, "ProductFeatureAppl");	
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
    
    private static void buildFeatureSequence(StringBuilder rowString, BufferedWriter bwOutFile, Map productFeatureSequenceMap, String entityName)
    {
    	List<Map.Entry> productFeatureSequenceMapSort = new ArrayList<Map.Entry>(productFeatureSequenceMap.entrySet());
        Collections.sort(productFeatureSequenceMapSort,
                 new Comparator() {
                     public int compare(Object firstObjToCompare, Object secondObjToCompare) 
                     {
             	    	Map.Entry e1 = (Map.Entry) firstObjToCompare;
             	        Map.Entry e2 = (Map.Entry) secondObjToCompare;
             	        
             		    return OsafeAdminUtil.alphaNumericSort(e1.getValue().toString(), e2.getValue().toString());
                     }
        });
        
        int iSeq = 1;
        for (Map.Entry entry : productFeatureSequenceMapSort) 
        {
        	try 
        	{
        		Map<String, String> entityFieldMap = (Map) entry.getKey();
        		
    		    rowString.setLength(0);
                rowString.append("<" + entityName + " ");
                for (Map.Entry<String, String> entityMap : entityFieldMap.entrySet()) 
                {
                	String entityFieldName = entityMap.getKey();
                	String entityFieldValue = entityFieldMap.get(entityFieldName);
                	rowString.append(entityFieldName + "=\"" + entityFieldValue + "\" ");
                }
                rowString.append("sequenceNum" + "=\"" + (iSeq * 10) + "\" ");
                rowString.append("/>");
                bwOutFile.write(rowString.toString());
                bwOutFile.newLine();
        	}
        	catch (Exception e) 
        	{
        		Debug.logError(e, module);
        	}
        	iSeq++;
        }
    }
    
    private static void buildProductFeatureImage(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl ) 
    {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
        StringBuilder  rowString = new StringBuilder();
        String productId=null;
		Map mFeatureIdImageExists = FastMap.newInstance();
        
		try 
		{
			
	        fOutFile = new File(xmlDataDirPath, "065-ProductFeatureImage.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                	 Map mRow = (Map)dataRows.get(i);
	            	 String selectFeatureImage=(String)mRow.get("plpSwatchImage");
	            	 if (UtilValidate.isNotEmpty(selectFeatureImage))
	            	 {
	              		mFeatureTypeMap.clear();
	              	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("featureId"));
	                	if (mFeatureTypeMap.size() > 0)
	                	{
	                		addProductFeatureImageRow(rowString, bwOutFile, mFeatureTypeMap, FastMap.newInstance(),selectFeatureImage,"plpSwatchImage","PLP_SWATCH_IMAGE_URL",loadImagesDirPath, imageUrl);
	                	}
	            	 }
	            	 selectFeatureImage=(String)mRow.get("pdpSwatchImage");
	            	 if (UtilValidate.isNotEmpty(selectFeatureImage))
	            	 {
	              		mFeatureTypeMap.clear();
	              	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("featureId"));
	                	if (mFeatureTypeMap.size() > 0)
	                	{
	                		mFeatureIdImageExists = addProductFeatureImageRow(rowString, bwOutFile, mFeatureTypeMap, FastMap.newInstance(),selectFeatureImage,"pdpSwatchImage","PDP_SWATCH_IMAGE_URL",loadImagesDirPath, imageUrl);
	                	}
	            	 }
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
    	}
      	 catch (Exception e) {
   	         }
         finally {
             try {
                 if (bwOutFile != null) 
                 {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
      	 
       }
    
    private static void buildProductAssoc(List dataRows,String xmlDataDirPath) 
    {
        File fOutFile =null;
        BufferedWriter bwOutFile=null;
		try 
		{
	        fOutFile = new File(xmlDataDirPath, "070-ProductAssoc.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                int compSeqNum = 0;
                int accessSeqNum = 0;
                int seqNum = 0;
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                    StringBuilder  rowString = new StringBuilder();
	            	Map mRow = (Map)dataRows.get(i);
	                rowString.append("<" + "ProductAssoc" + " ");
	                rowString.append("productId" + "=\"" + mRow.get("productId")+ "\" ");
	                rowString.append("productIdTo" + "=\"" + mRow.get("productIdTo") + "\" ");
	                String productAssocTypeId = "PRODUCT_COMPLEMENT";
	                if(((String)mRow.get("productAssocType")).equalsIgnoreCase("ACCESSORY"))
	                {
	                    productAssocTypeId = "PRODUCT_ACCESSORY";
	                    accessSeqNum = accessSeqNum + 10;
	                    seqNum = accessSeqNum;
	                }
	                else
	                {
	                	compSeqNum = compSeqNum + 10;
	                	seqNum = compSeqNum;
	                }
	                rowString.append("productAssocTypeId" + "=\"" + productAssocTypeId + "\" ");
	                    
	                String productAssocFromDate = _sdf.format(UtilDateTime.nowTimestamp());;
	                if(UtilValidate.isEmpty(mRow.get("fromDate"))) 
	             	{
	                    List<GenericValue> productAssocs = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId",mRow.get("productId"),"productIdTo",mRow.get("productIdTo"),"productAssocTypeId",productAssocTypeId),UtilMisc.toList("-fromDate"));
	                    if(UtilValidate.isNotEmpty(productAssocs)) 
	                    {
	                    	productAssocs = EntityUtil.filterByDate(productAssocs);
	                    	if(UtilValidate.isNotEmpty(productAssocs)) 
	                        {
	                            GenericValue productAssoc = EntityUtil.getFirst(productAssocs);
	                        	productAssocFromDate = _sdf.format(new Date(productAssoc.getTimestamp("fromDate").getTime()));
	                        }
	                    }
	                } 
	             	else 
	                {
	             	    String fromDate=(String)mRow.get("fromDate");
	                    java.util.Date formattedFromDate=OsafeAdminUtil.validDate(fromDate);
	                    productAssocFromDate =_sdf.format(formattedFromDate);
	                }
	                rowString.append("fromDate" + "=\"" + productAssocFromDate + "\" ");
	                    
	                if (UtilValidate.isNotEmpty(mRow.get("thruDate")))
	                {
	                	String thruDate=(String)mRow.get("thruDate");
	                	java.util.Date formattedThuDate=OsafeAdminUtil.validDate(thruDate);
	                	String sThruDate =_sdf.format(formattedThuDate);
	                	if (UtilValidate.isNotEmpty(sThruDate))
	                    {
	                    	rowString.append("thruDate" + "=\"" + sThruDate + "\" ");
	                    }
	                 }
	               
	                rowString.append("sequenceNum" + "=\"" + seqNum + "\" ");
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
    
    static Map featureTypeIdMap = FastMap.newInstance();
    private static Map buildFeatureMap(Map featureTypeMap,String parseFeatureType) 
    {
    	if (UtilValidate.isNotEmpty(parseFeatureType))
    	{
        	int iFeatIdx = parseFeatureType.indexOf(':');
        	if (iFeatIdx > -1)
        	{
            	String featureType = parseFeatureType.substring(0,iFeatIdx).trim();
            	String sFeatures = parseFeatureType.substring(iFeatIdx +1);
                String[] featureTokens = sFeatures.split(",");
            	Map mFeatureMap = FastMap.newInstance();
                for (int f=0;f < featureTokens.length;f++)
                {
                	String featureId = ""; 
                	try 
                	{
                		String featureTypeKey = StringUtil.removeSpaces(featureType).toUpperCase()+"~"+featureTokens[f].trim();
                		if(featureTypeIdMap.containsKey(featureTypeKey))
                		{
                			featureId = (String) featureTypeIdMap.get(featureTypeKey); 
                		}
                		else
                		{
                			List productFeatureList = _delegator.findByAnd("ProductFeature", UtilMisc.toMap("productFeatureTypeId", StringUtil.removeSpaces(featureType).toUpperCase(), "productFeatureCategoryId", StringUtil.removeSpaces(featureType).toUpperCase(), "description", featureTokens[f].trim()));
                			if(UtilValidate.isNotEmpty(productFeatureList))
                			{
                				GenericValue productFeature = EntityUtil.getFirst(productFeatureList);
        						featureId = productFeature.getString("productFeatureId");
                			}
                			else
                			{
                				featureId = _delegator.getNextSeqId("ProductFeature");
                			}
                		}
                		featureTypeIdMap.put(featureTypeKey, featureId);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	mFeatureMap.put(""+featureId,""+featureTokens[f].trim());
                }
        		featureTypeMap.put(featureType, mFeatureMap);
        	}
    		
    	}
    	return featureTypeMap;
    	    	
    }

    private static Map<String, String> formatProductXLSData(Map<String, String> dataMap) {
    	Map<String, String> formattedDataMap = new HashMap<String, String>();
    	for (Map.Entry<String, String> entry : dataMap.entrySet()) {
    		String value = entry.getValue();
    		if(UtilValidate.isNotEmpty(value)) {
    			value = StringUtil.replaceString(value, "&", "&amp");
    			value = StringUtil.replaceString(value, ";", "&#59;");
    	    	value = StringUtil.replaceString(value, "&amp", "&amp;");
    	    	value = StringUtil.replaceString(value, "\"", "&quot;");
    		}
    		formattedDataMap.put(entry.getKey(), value);
    	}
    	return formattedDataMap;
    }
    
    public static Map<String, Object> importEbayProductXls(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String ebayXlsFileName = (String)context.get("uploadFileName");
        String ebayXlsFilePath = (String)context.get("uploadFilePath");
        String productStoreId = (String) context.get("productStoreId");
        String browseRootProductCategoryId = (String) context.get("browseRootProductCategoryId");
        String fileName="clientEbayProductImport.xls";

        
        File inputWorkbook = null;
        File baseDataDir = null;
        BufferedWriter fOutProduct=null;
        
        String importDataPath = FlexibleStringExpander.expandString(OSAFE_ADMIN_PROP.getString("ecommerce-import-data-path"),context);
        
        if (UtilValidate.isNotEmpty(ebayXlsFileName)&& UtilValidate.isNotEmpty(ebayXlsFilePath) && ebayXlsFileName.toUpperCase().endsWith("XLS")) 
        {
            try {
                inputWorkbook = new File(ebayXlsFilePath + ebayXlsFileName);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }
        else {
            messages.add("No path specified for Excel sheet file, doing nothing.");
        }
        
        if (inputWorkbook != null) 
        {
            WritableWorkbook workbook = null;
            
            try 
            {

                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("en", "EN"));
                Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                Sheet ebaySheet = wb.getSheet(0);
                BufferedWriter bw = null; 

                
                File file = new File(importDataPath, "temp" + fileName);
                WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));
                workbook = Workbook.createWorkbook(file, wbSettings);
                int iRows=0;
                Map mWorkBookHeadCaptions = createWorkBookHeaderCaptions();

                WritableSheet excelSheetModHistory = createWorkBookSheet(workbook,"Mod History", 0);
            	createWorkBookHeaderRow(excelSheetModHistory, buildModHistoryHeader(),mWorkBookHeadCaptions);
            	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 1);
            	createWorkBookRow(excelSheetModHistory, "system", 1, 1);
            	createWorkBookRow(excelSheetModHistory, "Auto Generated Product Import Template From Ebay Product", 2, 1);
                
            	WritableSheet excelSheetCategory = createWorkBookSheet(workbook,"Category", 1);
            	createWorkBookHeaderRow(excelSheetCategory, buildCategoryHeader(),mWorkBookHeadCaptions);
            	
                WritableSheet excelSheetProduct = createWorkBookSheet(workbook,"Product", 2);
            	createWorkBookHeaderRow(excelSheetProduct, buildProductHeader(),mWorkBookHeadCaptions);

                WritableSheet excelSheetProductAssoc = createWorkBookSheet(workbook,"Product Association", 3);
            	createWorkBookHeaderRow(excelSheetProductAssoc, buildProductAssocHeader(),mWorkBookHeadCaptions);
            	
                WritableSheet excelSheetManufacturer = createWorkBookSheet(workbook,"Manufacturer", 4);
            	createWorkBookHeaderRow(excelSheetManufacturer, buildManufacturerHeader(),mWorkBookHeadCaptions);

            	List dataRows = buildDataRows(buildEbayProductHeader(),ebaySheet);
            	iRows = createProductCategoryWorkSheetFromEbay(excelSheetCategory, browseRootProductCategoryId,dataRows);
            	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 2);
            	createWorkBookRow(excelSheetModHistory, "system", 1, 2);
            	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Product Categories Generated", 2, 2);

            	iRows = createProductWorkSheetFromEbay(excelSheetProduct, browseRootProductCategoryId,dataRows);
            	createWorkBookRow(excelSheetModHistory, _sdf.format(UtilDateTime.nowDate()), 0, 2);
            	createWorkBookRow(excelSheetModHistory, "system", 1, 2);
            	createWorkBookRow(excelSheetModHistory,"(" +  iRows + ") Products Generated", 2, 2);
            	
            	workbook.write();
                workbook.close();
                
                new File(importDataPath, fileName).delete();
                File renameFile =new File(importDataPath, fileName);
                RandomAccessFile out = new RandomAccessFile(renameFile, "rw");
		        InputStream inputStr = new FileInputStream(file);
		        byte[] bytes = new byte[102400];
		        int bytesRead;
		        while ((bytesRead = inputStr.read(bytes)) != -1)
		        {
		            out.write(bytes, 0, bytesRead);
		        }
		        out.close();
		        inputStr.close();
            	

            } catch (BiffException be) {
                Debug.logError(be, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
            finally {
                new File(importDataPath, ebayXlsFileName).delete();
                if (workbook != null) 
                {
                    try {
                        workbook.close();
                    } catch (Exception exc) {
                        //Debug.warning();
                    }
                }
            }
        }
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
    }
        
    
    public static Map<String, Object> importRemoveEntityData(DispatchContext ctx, Map<String, ?> context)
    {

        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();

        SQLProcessor sqlP = null;
        String[] removeEntities = Constants.IMPORT_REMOVE_ENTITIES;
        // #############################################################
        // Removing entity names which are store in Constants java file.
        // #############################################################

        if (removeEntities != null) 
        {
            for (String entity: removeEntities)
            {
                String sql = null;
                try
                {
                    GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(delegator.getEntityGroupName(entity));
                    sqlP = new SQLProcessor(helperInfo);
                    DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperInfo.getHelperBaseName());

                    int deleteRowCount =0; 
                    String tableName = delegator.getModelEntity(entity).getTableName(datasourceInfo);
                    if (entity.equalsIgnoreCase("ProductCategory"))
                    {
                        String nowDateTime = _sdf.format(UtilDateTime.nowTimestamp());
                        sql = "UPDATE " + tableName;
                        sql += " SET PRIMARY_PARENT_CATEGORY_ID = NULL";
                        sql += ", LAST_UPDATED_STAMP = '"+nowDateTime+"'";
                        sql += " WHERE PRIMARY_PARENT_CATEGORY_ID IS NOT NULL ";
                        sqlP.prepareStatement(sql);
                        sqlP.executeUpdate();

                        sql = "DELETE FROM " + tableName;
                        sql += " WHERE LAST_UPDATED_STAMP = '"+nowDateTime+"'";
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                    }
                    else if (entity.equalsIgnoreCase("Product")) 
                    {   
                    	
                        String nowDateTime = _sdf.format(UtilDateTime.nowTimestamp());
                        sql = "UPDATE " + tableName;
                        sql += " SET PRIMARY_PRODUCT_CATEGORY_ID = NULL";
                        sqlP.prepareStatement(sql);
                        sqlP.executeUpdate();

                        sql = "DELETE FROM " + tableName;
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                    	 
                    }
                    else if (entity.equalsIgnoreCase("InvoiceItem")) 
                    {   
                    	
                        String nowDateTime = _sdf.format(UtilDateTime.nowTimestamp());
                        sql = "UPDATE " + tableName;
                        sql += " SET PARENT_INVOICE_ID = NULL";
                        sql += " , PARENT_INVOICE_ITEM_SEQ_ID=NULL";
                        sqlP.prepareStatement(sql);
                        sqlP.executeUpdate();

                        sql = "DELETE FROM " + tableName;
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                    	 
                    }
                    else
                    {
                        sql = "DELETE FROM " + tableName;
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                    }

                }
                catch (GenericEntityException e)
                {
                    Debug.logInfo("An error occurred executing query"+e, module);
                    return ServiceUtil.returnError("An error "+e.getMessage()+" occurred while executing query "+sql);
                }
                catch (Exception e)
                {
                    Debug.logInfo("An error occurred executing query"+e, module);
                    return ServiceUtil.returnError("An error "+e.getMessage()+" occurred while executing query "+sql);
                }
                finally
                {
                    try
                    {
                        sqlP.close();
                    }
                    catch (GenericDataSourceException e)
                    {
                        Debug.logInfo("An error occurred in closing SQLProcessor"+e, module);
                    }
                    catch (Exception e)
                    {
                    	Debug.logInfo("An error occurred in closing SQLProcessor"+e, module);
                    } 
                }
            }
        }
        else 
        {
            messages.add("No value for remove entities, doing nothing.");
        }
        // send the notification
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
    }
    
    private static String makeOfbizId(String idValue)
    {
    	String id=idValue;
    	try {
        	id=StringUtil.removeSpaces(idValue);
        	id=StringUtil.replaceString(id, "_", "");
        	if (id.length() > 20)
        	{
        		id=id.substring(0,20);
        	}
    		
    	}
    	catch(Exception e)
    	{
    		Debug.logError(e,module);
    	}
    	return id;
    }
    
    private static String getOsafeImagePath(String imageType) {
    	
    	if(UtilValidate.isEmpty(imageType))
    	{
    		return "";
    	}
    	String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
    	
    	Map<Object, Object> imageLocationMap = OsafeManageXml.findByKeyFromXmlFile(XmlFilePath, "key", imageType);
    	if(UtilValidate.isNotEmpty(imageLocationMap.get("value")))
    	{
    	    return (String)imageLocationMap.get("value");
    	} 
    	else 
    	{
    		return "";
        }
    }
    

    public static Map<String, Object> exportOrderXML(DispatchContext ctx, Map<String, ?> context) 
    {
    	_delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        List<String> orderIdList = (List)context.get("orderList");
        String productStoreId = (String)context.get("productStoreId");
        
        ObjectFactory factory = new ObjectFactory();
        BigFishOrderFeedType bfOrderFeedType = factory.createBigFishOrderFeedType();
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("order");
        String orderFileName = "Order";
        if(orderIdList.size() == 1) 
        {
        	orderFileName = orderFileName + orderIdList.get(0);
        }
        orderFileName = orderFileName + "_" + (OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HHmm"));
        
        orderFileName = UtilValidate.stripWhitespace(orderFileName) + ".xml";
        
        if (!new File(downloadTempDir).exists()) 
        {
        	new File(downloadTempDir).mkdirs();
	    }
        
        File file = new File(downloadTempDir + orderFileName);
  	  
        Map result = ServiceUtil.returnSuccess();
        
        List<String> exportedOrderIdList = FastList.newInstance();
        
        List orderList = bfOrderFeedType.getOrder();
        String includeExportOrderStatus = OsafeAdminUtil.getProductStoreParm(_delegator, productStoreId, "ORDER_STATUS_INC_EXPORT");
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
        OrderType order = null;
  	    for(String orderId : orderIdList) 
  	    {
  	    	try 
  	    	{
	  	    	order = factory.createOrderType();
	  	    	
	  	    	OrderReadHelper orderReadHelper = null;
	  	    	String orderStatusId = "";
	  	    	GenericValue orderHeader = _delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
	  	    	if(UtilValidate.isNotEmpty(orderHeader)) 
	  	    	{
	  	    	    orderStatusId = (String)orderHeader.get("statusId");
	  	    	}
	  	    	//Checks if the Order Status is 'Rejected'.
	  	    	if(UtilValidate.isNotEmpty(orderStatusId) && UtilValidate.isNotEmpty(exportedOrderStatusList) && !exportedOrderStatusList.contains(orderStatusId)) 
	  	    	{
	  	    		continue;
	  	    	}
	  	    	if(UtilValidate.isNotEmpty(orderHeader)) 
	  	    	{
	  	    	    orderReadHelper = new OrderReadHelper(orderHeader);
	  	    	    
	  	    	    // Set Order Customer Detail
			        setOrderCustomerFeed(factory, orderHeader, order, orderReadHelper);
			        
			        // Set Order Header Detail
			        setOrderHeaderFeed(factory, orderHeader, order, orderReadHelper);
			        
		  	    	//Set Order Shipment
			        setOrderShipmentFeed(factory, orderHeader, order, orderReadHelper);
			        
			        //Set Order Line Items
			        setOrderLineItemsFeed(factory, orderHeader, order, orderReadHelper);
			        
		    	    // Set Order Payment Detail
			        setOrderPaymentFeed(factory, orderHeader, order, orderReadHelper);
			        
			        //Set Order Attribute Detail
			        setOrderAttributeFeed(factory, orderHeader, order, orderReadHelper);
			        
		  	    	//Set Order Adjustment Detail
			        setOrderAdjustmentFeed(factory, orderHeader, order, orderReadHelper);
	  	    	}
	  	    	
	  	    	orderList.add(order);
	  	    	exportedOrderIdList.add(orderId);
  	    	} 
  	    	catch (Exception e) 
  	    	{
  	    		e.printStackTrace();
  	    	}
  	    }
  	  
        FeedsUtil.marshalObject(new JAXBElement<BigFishOrderFeedType>(new QName("", "BigFishOrderFeed"), BigFishOrderFeedType.class, null, bfOrderFeedType), file);
  	    result.put("feedsDirectoryPath", downloadTempDir);
        result.put("feedsFileName", orderFileName);
        result.put("feedsExportedIdList", exportedOrderIdList);
        return result;
    }
    
    
    public static void setOrderHeaderFeed(ObjectFactory factory, GenericValue orderHeader, OrderType order, OrderReadHelper orderReadHelper)
    {
    	List<GenericValue> orderItems = orderReadHelper.getOrderItems();
    	List<GenericValue> orderAdjustments = orderReadHelper.getAdjustments();
    	
    	OrderHeaderType oh = factory.createOrderHeaderType();
    	
    	String orderProductStoreId = "";
	    String orderStatusId = "";
	    if(UtilValidate.isNotEmpty(orderHeader)) 
	    {
	    	if(UtilValidate.isNotEmpty(orderHeader.getString("productStoreId")))
	    	{
	    		oh.setProductStoreId(orderHeader.getString("productStoreId"));
	    	}
	    	else
	    	{
	    		oh.setProductStoreId("");
	    	}
	    	if(UtilValidate.isNotEmpty(orderHeader.getString("statusId")))
	    	{
	    		oh.setStatusId(orderHeader.getString("statusId"));
	    	}
	    	else
	    	{
	    		oh.setStatusId("");
	    	}
	    	oh.setOrderId(orderHeader.getString("orderId"));
	    	if(UtilValidate.isNotEmpty(orderHeader.get("orderDate")))
	    	{
	    		oh.setOrderDate(orderHeader.get("orderDate").toString());
	    	}
	    	else
	    	{
	    		oh.setOrderDate("");
	    	}
	    	if(UtilValidate.isNotEmpty(orderHeader.get("entryDate")))
	    	{
	    		oh.setEntryDate(orderHeader.get("entryDate").toString());
	    	}
	    	else
	    	{
	    		oh.setEntryDate("");
	    	}
	    	if(UtilValidate.isNotEmpty(orderHeader.get("createdBy")))
	    	{
	    		oh.setCreatedBy(orderHeader.get("createdBy").toString());
	    	}
	    	else
	    	{
	    		oh.setCreatedBy("");
	    	}
	    }
    	
        BigDecimal orderItemsAdjustmentTotal = orderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, true, true, true);
        BigDecimal shippingTotal = orderReadHelper.getShippingTotal();
        BigDecimal taxAmount = orderReadHelper.getTaxTotal();
        
        BigDecimal grandTotal = orderReadHelper.getOrderGrandTotal();
        
        BigDecimal adjustmentTotal = orderReadHelper.getOrderAdjustmentsTotal();
        
        BigDecimal orderItemSubTotal = orderReadHelper.getOrderItemsSubTotal();
        
        adjustmentTotal = (adjustmentTotal.add(orderItemsAdjustmentTotal)).subtract(shippingTotal.add(taxAmount));
    	
        oh.setOrderSubTotal(orderItemSubTotal.toString());
    	oh.setOrderTotalItem(Integer.toString(orderItems.size()));
    	oh.setCurrency(orderReadHelper.getCurrency());
    	oh.setOrderShippingCharge(shippingTotal.toString());
    	oh.setOrderTax(taxAmount.toString());
    	oh.setOrderTotalAmount(grandTotal.toString());
    	oh.setOrderTotalAdjustment(adjustmentTotal.toString());
    	order.setOrderHeader(oh);
    }
    
    public static void setOrderShipmentFeed(ObjectFactory factory, GenericValue orderHeader, OrderType order, OrderReadHelper orderReadHelper)
    {
    	
    	OrderShipmentType orderShipmentType = factory.createOrderShipmentType();
    	String orderId = orderHeader.getString("orderId");
	    List shipmentList = orderShipmentType.getShipment();
	    String productStoreId = orderReadHelper.getProductStoreId();
	    
    	try
    	{
	    	List<GenericValue> orderItemShipGroups = orderReadHelper.getOrderItemShipGroups();
	    	if(UtilValidate.isNotEmpty(orderItemShipGroups))
	    	{
	    		for(GenericValue orderItemShipGroup : orderItemShipGroups)
		    	{
		    		ShipmentType shipment = factory.createShipmentType();
		    		String shipGroupSeqId = orderItemShipGroup.getString("shipGroupSeqId");
		    		if(UtilValidate.isNotEmpty(orderItemShipGroup.getString("shipGroupSeqId")))
		    		{
		    			shipment.setShipGroupSequenceId(orderItemShipGroup.getString("shipGroupSeqId"));
		    		}
		    		else
		    		{
		    			shipment.setShipGroupSequenceId("");
		    		}
		    		if(UtilValidate.isNotEmpty(orderItemShipGroup.getString("shipmentMethodTypeId")))
		    		{
		    			shipment.setShippingMethod(orderItemShipGroup.getString("shipmentMethodTypeId"));
		    		}
		    		else
		    		{
		    			shipment.setShippingMethod("");
		    		}
		    		if(UtilValidate.isNotEmpty(orderItemShipGroup.getString("carrierPartyId")))
		    		{
		    			shipment.setCarrier(orderItemShipGroup.getString("carrierPartyId"));
		    		}
		    		else
		    		{
		    			shipment.setCarrier("");
		    		}
		    		if(UtilValidate.isNotEmpty(orderItemShipGroup.getString("shippingInstructions")))
		    		{
		    			shipment.setShippingInstructions((String)orderItemShipGroup.get("shippingInstructions"));	
		    		}
		    		else
		    		{
		    			shipment.setShippingInstructions("");
		    		}
		    		if(UtilValidate.isNotEmpty(orderItemShipGroup.getString("trackingNumber")))
		    		{
		    			shipment.setTrackingNumber((String)orderItemShipGroup.get("trackingNumber"));	
		    		}
		    		else
		    		{
		    			shipment.setTrackingNumber("");
		    		}
		    		
		    		
		    		
		    		ShippingAddressType shippingAddress = factory.createShippingAddressType();
		    	    GenericValue postalAddress = orderItemShipGroup.getRelatedOne("PostalAddress");
		    	    if(UtilValidate.isNotEmpty(postalAddress))
		    	    {
		    	    	if(UtilValidate.isNotEmpty(postalAddress.getString("toName")))
			    	    {
			    	    	shippingAddress.setToName(postalAddress.getString("toName"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setToName("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address1")))
			    	    {
			    	    	shippingAddress.setAddress1(postalAddress.getString("address1"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setAddress1("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address2")))
			    	    {
			    	    	shippingAddress.setAddress2(postalAddress.getString("address2"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setAddress2("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address3")))
			    	    {
			    	    	shippingAddress.setAddress3(postalAddress.getString("address3"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setAddress3("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("countryGeoId")))
			    	    {
				    	    if (displayCountryFieldAsLong(productStoreId))
				    	    {
				    	    	shippingAddress.setCountry(getGeoName(postalAddress.getString("countryGeoId")));
				    	    }
				    	    else
				    	    {
				    	    	shippingAddress.setCountry(postalAddress.getString("countryGeoId"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setCountry("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("city")))
			    	    {
			    	    	shippingAddress.setCityTown(postalAddress.getString("city"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setCityTown("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId")))
			    	    {
				    	    if (displayStateFieldAsLong(productStoreId))
				    	    {
				    	    	shippingAddress.setStateProvince(getGeoName(postalAddress.getString("stateProvinceGeoId")));
				    	    }
				    	    else
				    	    {
				    	    	shippingAddress.setStateProvince(postalAddress.getString("stateProvinceGeoId"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setStateProvince("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("postalCode")))
			    	    {
				    	    if (displayZipFieldAsLong(productStoreId))
				    	    {
				    	        String postalCode = postalAddress.getString("postalCode");
				    	        if (UtilValidate.isNotEmpty(postalAddress.getString("postalCodeExt")))
				    	        {
				    	            postalCode = postalCode+"-"+postalAddress.getString("postalCodeExt");
				    	        }
				    	        shippingAddress.setZipPostCode(postalCode);
				    	    }
				    	    else
				    	    {
				    	    	shippingAddress.setZipPostCode(postalAddress.getString("postalCode"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setZipPostCode("");
			    	    }
			    	    shipment.setShippingAddress(shippingAddress);
		    	    }
		    		
		    	    // Set Order Line Item detail 
		  	    	List<GenericValue> orderItemShipGroupAssocList = orderItemShipGroup.getRelated("OrderItemShipGroupAssoc");
		  	    	
		  	    	List shipGrouporderLineItemList = shipment.getShipGroupLineItem();
		  	    	if(UtilValidate.isNotEmpty(orderItemShipGroupAssocList))
		  	    	{
		  	    		for(GenericValue orderItemShipGroupAssocGV : orderItemShipGroupAssocList) 
			  	    	{
		  	    			ShipGroupLineItemType shipGroupLineItem = factory.createShipGroupLineItemType();
		  	    			GenericValue orderItem = orderItemShipGroupAssocGV.getRelatedOne("OrderItem");
		  	    			if(UtilValidate.isNotEmpty(orderItem))
		  	    			{
		  	    				shipGroupLineItem.setProductId(orderItem.getString("productId"));
		  	    			}
		  	    			else
		  	    			{
		  	    				shipGroupLineItem.setProductId("");
		  	    			}
		  	    			if(UtilValidate.isNotEmpty(orderItemShipGroupAssocGV.getString("orderItemSeqId")))
		  	    			{
		  	    				shipGroupLineItem.setSequenceId(orderItemShipGroupAssocGV.getString("orderItemSeqId"));
		  	    			}
		  	    			else
		  	    			{
		  	    				shipGroupLineItem.setSequenceId("");
		  	    			}
		  	    			if(UtilValidate.isNotEmpty(orderItemShipGroupAssocGV.get("quantity")))
		  	    			{
		  	    				shipGroupLineItem.setQuantity(UtilMisc.toInteger(orderItemShipGroupAssocGV.get("quantity")));
		  	    			}
		  	    			else
		  	    			{
		  	    				shipGroupLineItem.setSequenceId("");
		  	    			}
		  	    			
		  	    		    // Set Order Line Item Attributes
			  	  	        OrderLineAttributeType orderLineAttributeType = factory.createOrderLineAttributeType();
			  	  	        List<GenericValue> orderItemAttributes = orderItem.getRelated("OrderItemAttribute");
			  	  	        
			  	  	        List itemAttributeList = orderLineAttributeType.getAttribute();	
			  	  	        if(UtilValidate.isNotEmpty(orderItemAttributes))
			  	  	        {
				  	  	        for(GenericValue orderItemAttribute : orderItemAttributes)
			  	  	        	{
			  	  	        	    AttributeType attribute = factory.createAttributeType();
			  	  	        	    String attrName = orderItemAttribute.getString("attrName");
			  	  	        	    if(attrName.startsWith("GIFT_MSG_FROM_") || attrName.startsWith("GIFT_MSG_TO_") || attrName.startsWith("GIFT_MSG_TEXT_"))
			  	  	        	    {
			  	  	        	        int iShipId = attrName.lastIndexOf('_');
			  	  	        	        if(iShipId > -1 && attrName.substring(iShipId+1).equals(shipGroupSeqId))
			  	  	        	        {
				  	  	        	        attribute.setName(orderItemAttribute.getString("attrName"));
					  	    		        if(UtilValidate.isNotEmpty(orderItemAttribute.getString("attrValue")))
					  	    		        {
					  	    		        	attribute.setValue(orderItemAttribute.getString("attrValue"));
					  	    		        }
					  	    		        else
					  	    		        {
					  	    		        	attribute.setValue("");
					  	    		        }
					  	    		        itemAttributeList.add(attribute);
			  	  	        	        }
			  	  	        	    }
			  	  	        	}
			  	  	        }
			  	  	        shipGroupLineItem.setOrderLineAttribute(orderLineAttributeType);
		  	    			
		  	    			shipGrouporderLineItemList.add(shipGroupLineItem);
			  	    	}
		  	    	}
		  	        shipmentList.add(shipment);
		    	}
	    	}
    	}
    	catch(Exception e)
    	{
    		Debug.logInfo("Error in Export Order Shipment "+e, module);
    	}
	    order.setOrderShipment(orderShipmentType);
    }
    
    public static void setOrderLineItemsFeed(ObjectFactory factory, GenericValue orderHeader, OrderType order, OrderReadHelper orderReadHelper)
    {
    	OrderLineItemsType orderLineItems = factory.createOrderLineItemsType();
    	String orderId = orderHeader.getString("orderId");
    	try
    	{
  	    	List<GenericValue> orderItems = orderReadHelper.getOrderItems();
  	    	List orderLineItemsList = orderLineItems.getOrderLine();
  	    	if(UtilValidate.isNotEmpty(orderItems))
  	    	{
  	    		for(GenericValue orderItem : orderItems) 
	  	    	{
	  	    		//GenericValue orderItem = _delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemShipGroupAssocGV.getString("orderItemSeqId")));
	  	    		BigDecimal lineTotalGross = BigDecimal.ZERO;
	  	    		BigDecimal orderItemAdjustmentTotal = orderReadHelper.getOrderItemAdjustmentsTotal(orderItem);
	  	    		BigDecimal offerPrice = null;
	  	    		if(orderItemAdjustmentTotal.compareTo(BigDecimal.ZERO) == -1) 
	  	    		{
	  	    			try 
	  	    			{
	  	    			    offerPrice = ((BigDecimal) orderItem.get("unitPrice")).add((orderItemAdjustmentTotal.divide((BigDecimal) orderItem.get("quantity")))).setScale(scale, rounding);
	  	    			} 
	  	    			catch (ArithmeticException ae) 
	  	    			{
							Debug.logInfo("Error in Calculating Offer Price"+ae, module);
						}
	  	    		}
	  	    		
  	  	       
  	  	            lineTotalGross = orderItem.getBigDecimal("unitPrice").multiply(orderItem.getBigDecimal("quantity")).setScale(scale,rounding);
	  	    		OrderLineType orderLine = factory.createOrderLineType();
	  	    		
	  	    		if(UtilValidate.isNotEmpty(orderItem.getString("statusId")))
	  	    		{
	  	    			orderLine.setStatusId(orderItem.getString("statusId"));	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setStatusId("");
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItem.getString("productId")))
	  	    		{
	  	    			orderLine.setProductId(orderItem.getString("productId"));	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setProductId("");
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItem.getString("orderItemSeqId")))
	  	    		{
	  	    			orderLine.setSequenceId(orderItem.getString("orderItemSeqId"));	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setSequenceId("");
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItem.get("quantity")))
	  	    		{
	  	    			orderLine.setQuantity(UtilMisc.toInteger(orderItem.get("quantity")));	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setQuantity(0);
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItem.get("unitPrice")))
	  	    		{
	  	    			orderLine.setPrice(orderItem.get("unitPrice").toString());	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setPrice("");
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItem.get("unitListPrice")))
	  	    		{
	  	    			orderLine.setListPrice(orderItem.get("unitListPrice").toString());	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setListPrice("");
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItem.getString("isPromo")))
	  	    		{
	  	    			orderLine.setIsPromo(orderItem.getString("isPromo"));	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setIsPromo("");
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItem.get("isModifiedPrice")))
	  	    		{
	  	    			orderLine.setIsModifiedPrice(orderItem.get("isModifiedPrice").toString());	
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setIsModifiedPrice("");
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(offerPrice)) 
	  	    		{
	  	    		    orderLine.setOfferPrice(offerPrice.toString());
	  	    		}
	  	    		else
	  	    		{
	  	    			orderLine.setOfferPrice("");
	  	    		}
	  	    		orderLine.setLineTotalAmount(lineTotalGross.toString());
	  	    		
	  	    		//Set Order Line Tax
	  	    		List orderLineSalexTaxList = orderLine.getOrderLineSalesTax();
	  	    		List<GenericValue> orderItemAdjustments = orderReadHelper.getOrderItemAdjustments(orderItem);
	  	    		List<GenericValue> orderItemSalesTaxAdjustments = FastList.newInstance();
	  	    		if(UtilValidate.isNotEmpty(orderItemAdjustments))
	  	    		{
	  	    			orderItemSalesTaxAdjustments = EntityUtil.filterByAnd(orderItemAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "SALES_TAX"));
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItemSalesTaxAdjustments))
	  	    		{
	  	    			for (GenericValue orderItemSalesTaxAdjustment : orderItemSalesTaxAdjustments) 
	  	  	    	    {
	  	    				OrderLineSalesTaxType orderLineSalesTax = factory.createOrderLineSalesTaxType();
	  	    				if(UtilValidate.isNotEmpty(orderItemSalesTaxAdjustment.get("shipGroupSeqId")))
	  	    				{
	  	    					orderLineSalesTax.setShipGroupSequenceId(orderItemSalesTaxAdjustment.getString("shipGroupSeqId"));
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLineSalesTax.setShipGroupSequenceId("");
	  	    				}
	  	    				if(UtilValidate.isNotEmpty(orderItemSalesTaxAdjustment.get("sourcePercentage")))
	  	    				{
	  	    					orderLineSalesTax.setTaxPercent((orderItemSalesTaxAdjustment.getBigDecimal("sourcePercentage").setScale(3, rounding)).toString());
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLineSalesTax.setTaxPercent("");
	  	    				}
	  	    				if(UtilValidate.isNotEmpty(orderItemSalesTaxAdjustment.getString("taxAuthGeoId")))
	  	    				{
	  	    					orderLineSalesTax.setTaxAuthorityGeo(orderItemSalesTaxAdjustment.getString("taxAuthGeoId"));
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLineSalesTax.setTaxAuthorityGeo("");
	  	    				}
	  	    				if(UtilValidate.isNotEmpty(orderItemSalesTaxAdjustment.getString("taxAuthPartyId")))
	  	    				{
	  	    					orderLineSalesTax.setTaxAuthorityParty(orderItemSalesTaxAdjustment.getString("taxAuthPartyId"));
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLineSalesTax.setTaxAuthorityParty("");
	  	    				}
	  	    				if(UtilValidate.isNotEmpty(orderItemSalesTaxAdjustment.get("amount")))
	  	    				{
	  	    					orderLineSalesTax.setSalesTax((orderItemSalesTaxAdjustment.getBigDecimal("amount").setScale(3, rounding)).toString());
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLineSalesTax.setSalesTax("");
	  	    				}
	  	    				orderLineSalexTaxList.add(orderLineSalesTax);
	  	  	    	    }
	  	    		}
	  	    		else
	  	  	    	{
	  	    			OrderLineSalesTaxType orderLineSalesTax = factory.createOrderLineSalesTaxType();
	  	    			orderLineSalexTaxList.add(orderLineSalesTax);
	  	  	    	}
	  	    		
	  	    	    //Set Order Line Shipping Charge
	  	    		List orderLineShippingList = orderLine.getOrderLineShippingCharge();
	  	    		List<GenericValue> orderItemShippingAdjustments = FastList.newInstance();
	  	    		if(UtilValidate.isNotEmpty(orderItemAdjustments))
	  	    		{
	  	    			orderItemShippingAdjustments = EntityUtil.filterByAnd(orderItemAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "SHIPPING_CHARGES"));
	  	    		}
	  	    		if(UtilValidate.isNotEmpty(orderItemShippingAdjustments))
	  	    		{
	  	    			for (GenericValue orderItemShippingAdjustment : orderItemShippingAdjustments) 
	  	  	    	    {
	  	    				OrderLineShippingChargeType orderLineShippingCharge = factory.createOrderLineShippingChargeType();
	  	    				
	  	    				if(UtilValidate.isNotEmpty(orderItemShippingAdjustment.get("shipGroupSeqId")))
	  	    				{
	  	    					orderLineShippingCharge.setShipGroupSequenceId(orderItemShippingAdjustment.getString("shipGroupSeqId"));
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLineShippingCharge.setShipGroupSequenceId("");
	  	    				}
	  	    				if(UtilValidate.isNotEmpty(orderItemShippingAdjustment.get("amount")))
	  	    				{
	  	    					orderLineShippingCharge.setShippingCharge((orderItemShippingAdjustment.getBigDecimal("amount").setScale(3, rounding)).toString());
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLineShippingCharge.setShippingCharge("");
	  	    				}
	  	    				orderLineShippingList.add(orderLineShippingCharge);
	  	  	    	    }
	  	    		}
	  	    		else
	  	  	    	{
	  	    			OrderLineShippingChargeType orderLineShippingCharge = factory.createOrderLineShippingChargeType();
	  	  	    	    orderLineShippingList.add(orderLineShippingCharge);
	  	  	    	}
	  	    	    // Set Order Line Promotion Detail 
	  	    		List<GenericValue> orderItemPromotionAdjustments = FastList.newInstance();
	  	    		if(UtilValidate.isNotEmpty(orderItemAdjustments))
	  	    		{
	  	    			orderItemPromotionAdjustments = EntityUtil.filterByAnd(orderItemAdjustments, UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT"));
	  	    		}
	  	  	    	List orderLinePromotionList = orderLine.getOrderLinePromotion();
	  	  	    	if(UtilValidate.isNotEmpty(orderItemPromotionAdjustments)) 
	  	  	    	{
	  	  	    	    for (GenericValue orderItemPromoAdjustment : orderItemPromotionAdjustments) 
	  	  	    	    {
	  	  	    		    OrderLinePromotionType orderLinePromotion = factory.createOrderLinePromotionType();
	  	  	    		    GenericValue adjustmentType = orderItemPromoAdjustment.getRelatedOne("OrderAdjustmentType");
	  	  	    		    GenericValue productPromo = orderItemPromoAdjustment.getRelatedOne("ProductPromo");
	  	  	    		    String promoCodeText = "";
	  	  	    		    if(UtilValidate.isNotEmpty(productPromo)) 
	  	  	    		    {
	  	  	    			    List<GenericValue> productPromoCode = productPromo.getRelated("ProductPromoCode");
	  	  	    			    Set<String> promoCodesEntered = orderReadHelper.getProductPromoCodesEntered();
	  	  	    			    if(UtilValidate.isNotEmpty(promoCodesEntered)) 
	  	  	    			    {
	  	  	    				    for(String promoCodeEntered : promoCodesEntered) 
	  	  	    				    {
	  	  	    					    if(UtilValidate.isNotEmpty(productPromoCode)) 
	  	  	    					    {
	  	  	    						    for(GenericValue promoCode : productPromoCode) 
	  	  	    						    {
	  	  	    							    String promoCodeEnteredId = promoCodeEntered;
	  	  	    							    String promoCodeId = (String) promoCode.get("productPromoCodeId");
	  	  	    							    if(UtilValidate.isNotEmpty(promoCodeEnteredId)) 
	  	  	    							    {
	  	  	    								    if(promoCodeId.equals(promoCodeEnteredId)) 
	  	  	    								    {
	  	  	    									    promoCodeText = (String)promoCode.get("productPromoCodeId");
	  	  	    								    }
	  	  	    							    }
	  	  	    						    }
	  	  	    					    }
	  	  	    				    }
	  	  	    			    }
	  	  	    			    else
	  	  	    			    {
	  	  	    			        promoCodeText = (String)productPromo.get("promoName");
	  	  	    			    }
	  	  	    		    }
		  	  	    		if(UtilValidate.isNotEmpty(orderItemPromoAdjustment.get("shipGroupSeqId")))
	  	    				{
		  	  	    		    orderLinePromotion.setShipGroupSequenceId(orderItemPromoAdjustment.getString("shipGroupSeqId"));
	  	    				}
	  	    				else
	  	    				{
	  	    					orderLinePromotion.setShipGroupSequenceId("");
	  	    				}
	  	  	    	        orderLinePromotion.setPromotionCode(promoCodeText);
	  	  	                BigDecimal promotionAmount = orderReadHelper.calcItemAdjustment(orderItemPromoAdjustment, orderItem);
	  	  	    	        orderLinePromotion.setPromotionAmount(promotionAmount.toString());
	  	  	                orderLinePromotionList.add(orderLinePromotion);
	  	  	            }
	  	  	    	}
	  	  	    	else
	  	  	    	{
	  	  	    	    OrderLinePromotionType orderLinePromotion = factory.createOrderLinePromotionType();
	  	  	    		orderLinePromotionList.add(orderLinePromotion);
	  	  	    	}
	  	  	    	
	  	  	    	// Set Order Line Item Attributes
	  	  	        OrderLineAttributeType orderLineAttributeType = factory.createOrderLineAttributeType();
	  	  	        List<GenericValue> orderItemAttributes = orderItem.getRelated("OrderItemAttribute");
	  	  	        
	  	  	        List itemAttributeList = orderLineAttributeType.getAttribute();	
	  	  	        if(UtilValidate.isNotEmpty(orderItemAttributes))
	  	  	        {
		  	  	        for(GenericValue orderItemAttribute : orderItemAttributes)
	  	  	        	{
	  	  	        	    AttributeType attribute = factory.createAttributeType();
	  	    		        attribute.setName(orderItemAttribute.getString("attrName"));
	  	    		        if(UtilValidate.isNotEmpty(orderItemAttribute.getString("attrValue")))
	  	    		        {
	  	    		        	attribute.setValue(orderItemAttribute.getString("attrValue"));
	  	    		        }
	  	    		        else
	  	    		        {
	  	    		        	attribute.setValue("");
	  	    		        }
	  	    		        itemAttributeList.add(attribute);	
	  	  	        	}
	  	  	        }
	  	  	        orderLine.setOrderLineAttribute(orderLineAttributeType);
	  	  	        orderLineItemsList.add(orderLine);
	  	    	}
  	    	}
    	
    	}
    	catch(Exception e)
    	{
    		Debug.logInfo("Error in Export Order Shipment "+e, module);
    	}
	    order.setOrderLineItems(orderLineItems);
    }
    
    public static void setOrderPaymentFeed(ObjectFactory factory, GenericValue orderHeader, OrderType order, OrderReadHelper orderReadHelper)
    {
    	
    	OrderPaymentType orderPaymentType = factory.createOrderPaymentType();
	    List paymentList = orderPaymentType.getPayment();
	    	
	    List<GenericValue> orderPayments = orderReadHelper.getPaymentPreferences();
	    try
	    {
	    	if(UtilValidate.isNotEmpty(orderPayments)) 
	    	{
	    		for(GenericValue orderPaymentPreference : orderPayments) 
	    		{
	    			GenericValue paymentMethod = null;
	    	    	GenericValue creditCard = null;
	    	    	String transactionId = "";
	    	    	String paymentId = "";
	    	    	String merchantRefNo = "";
	    	    	String paymentMethodTypeId = "";
	    	    	String paymentMethodId = "";
	    	    	String statusId = "";
	    	    	BigDecimal amount = BigDecimal.ZERO;
	    	    	String cardNumber = "";
	    	    	String cardExpireDate = "";
	    	    	
	    	    	List<GenericValue> gatewayResponses = null;
	    			
	    			PaymentType orderPayment = factory.createPaymentType();
	    			paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod");
	    			gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse");
	    			
	    			paymentMethodTypeId = orderPaymentPreference.getString("paymentMethodTypeId");
	    			statusId = orderPaymentPreference.getString("statusId");
	    			amount = orderPaymentPreference.getBigDecimal("maxAmount");
	    			paymentMethodId = orderPaymentPreference.getString("paymentMethodId");
	    			orderPayment.setPaymentMethod(paymentMethodTypeId);
	    			orderPayment.setAmount(amount.toString());
	    			orderPayment.setStatusId(statusId);
	    			
	    			if(UtilValidate.isNotEmpty(paymentMethodId))
	    			{
	    				orderPayment.setPaymentMethodId(paymentMethodId);
	    			}
	    			else
	    			{
	    				orderPayment.setPaymentMethodId("");
	    			}
    			
	    			if(UtilValidate.isNotEmpty(paymentMethod))
	    			{
	    				
	    				if((paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD"))) 
	    				{
	  	    				creditCard = paymentMethod.getRelatedOne("CreditCard");
	  	    				if(UtilValidate.isNotEmpty(creditCard))
	  	    				{
	  	    					if(UtilValidate.isNotEmpty(creditCard.getString("cardType")))
		  	    				{
	  	    						orderPayment.setCardType(creditCard.getString("cardType"));
		  	    				}
	  	    					else
	  	    					{
	  	    						orderPayment.setCardType("");
	  	    					}
	  	    					if(UtilValidate.isNotEmpty(creditCard.getString("cardNumber")))
		  	    				{
	  	    						orderPayment.setCardNumber(creditCard.getString("cardNumber"));
		  	    				}
	  	    					else
	  	    					{
	  	    						orderPayment.setCardNumber("");
	  	    					}
	  	    					if(UtilValidate.isNotEmpty(creditCard.getString("expireDate")))
		  	    				{
	  	    						orderPayment.setExpiryDate(creditCard.getString("expireDate"));
		  	    				}
	  	    					else
	  	    					{
	  	    						orderPayment.setExpiryDate("");
	  	    					}
	  	    				}
	  	    			} 
	    				
	  	    			if(paymentMethod.getString("paymentMethodTypeId").equals("SAGEPAY_TOKEN")) 
	  	    			{
	  	    				GenericValue sagePayTokenGV = _delegator.findOne("SagePayTokenPaymentMethod", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
	  	    				if(UtilValidate.isNotEmpty(sagePayTokenGV)) 
	  	    				{
	  	    					if(UtilValidate.isNotEmpty(sagePayTokenGV.getString("sagePayToken")))
	  	    					{
	  	    						orderPayment.setSagePayPaymentToken(sagePayTokenGV.getString("sagePayToken"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setSagePayPaymentToken("");
	  	    					}
	  	    				    creditCard = _delegator.findOne("CreditCard", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
	  	    				    if(UtilValidate.isNotEmpty(creditCard) && UtilValidate.isNotEmpty(creditCard.getString("cardType")))
	  	    				    {
	  	    				    	orderPayment.setCardType(creditCard.getString("cardType"));
	  	    				    }
	  	    				    else
	  	    				    {
	  	    				    	orderPayment.setCardType("");
	  	    				    }
	  	    				}
	  	    			}
	  	    			
	  	    		    if(paymentMethod.getString("paymentMethodTypeId").equals("EXT_PAYPAL")) 
	  	    		    {
	  	    				GenericValue payPalMethod = _delegator.findOne("PayPalPaymentMethod", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
	  	    				if(UtilValidate.isNotEmpty(payPalMethod)) 
	  	    				{
	  	    					if(UtilValidate.isNotEmpty(payPalMethod.getString("paypalPayerId")))
	  	    					{
	  	    						orderPayment.setPayPalPayerId(payPalMethod.getString("paypalPayerId"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setPayPalPayerId("");
	  	    					}
	  	    					if(UtilValidate.isNotEmpty(payPalMethod.getString("transactionId")))
	  	    					{
	  	    						orderPayment.setPayPalTransactionId(payPalMethod.getString("transactionId"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setPayPalTransactionId("");
	  	    					}
	  	    					if(UtilValidate.isNotEmpty(payPalMethod.getString("payerStatus")))
	  	    					{
	  	    						orderPayment.setPayPalPayerStatus(payPalMethod.getString("payerStatus"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setPayPalPayerStatus("");
	  	    					}
	  	    					if(UtilValidate.isNotEmpty(payPalMethod.getString("expressCheckoutToken")))
	  	    					{
	  	    						orderPayment.setPayPalPaymentToken(payPalMethod.getString("expressCheckoutToken"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setPayPalPaymentToken("");
	  	    					}
	  	    				}
  	    			    }
	  	    			
	  	    		    if(paymentMethod.getString("paymentMethodTypeId").equals("EXT_EBS")) 
	  	    		    {
	  	    				GenericValue ebsMethod = _delegator.findOne("EbsPaymentMethod", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
	  	    				if(UtilValidate.isNotEmpty(ebsMethod)) 
	  	    				{
	  	    					merchantRefNo = (String)ebsMethod.get("merchantReferenceNum");
	  	    					if(UtilValidate.isNotEmpty(ebsMethod.getString("transactionId")))
	  	    					{
	  	    						orderPayment.setEbsTransactionId(ebsMethod.getString("transactionId"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setEbsTransactionId("");
	  	    					}
	  	    					if(UtilValidate.isNotEmpty(ebsMethod.getString("paymentId")))
	  	    					{
	  	    						orderPayment.setEbsPaymentId(ebsMethod.getString("paymentId"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setEbsPaymentId("");
	  	    					}
	  	    					if(UtilValidate.isNotEmpty(ebsMethod.getString("merchantReferenceNum")))
	  	    					{
	  	    						orderPayment.setMerchantReferenceNumber(ebsMethod.getString("merchantReferenceNum"));
	  	    					}
	  	    					else
	  	    					{
	  	    						orderPayment.setMerchantReferenceNumber("");
	  	    					}
	  	    				}
  	    			    }
	  	    		    if(paymentMethod.getString("paymentMethodTypeId").equals("EXT_PAYNETZ")) 
  	    		        {
	  	    		        GenericValue payNetzMethod = _delegator.findOne("PayNetzPaymentMethod", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
	  	    		        if(UtilValidate.isNotEmpty(payNetzMethod) && UtilValidate.isNotEmpty(payNetzMethod.getString("merchantTransactionId")))
	  	    		        {
	  	    		        	orderPayment.setMerchantTransactionId(payNetzMethod.getString("merchantTransactionId"));
	  	    		        }
	  	    		        else
	  	    		        {
	  	    		        	orderPayment.setMerchantTransactionId("");
	  	    		        }
  	    		        }
	  	  	    		if(paymentMethod.getString("paymentMethodTypeId").equals("GIFT_CARD")) 
	  	    		    {
	  	  	    			if(UtilValidate.isNotEmpty(paymentMethod.getString("description")))
	  	  	    			{
	  	  	    			    orderPayment.setCardType(paymentMethod.getString("description"));
	  	  	    			}
	  	  	    			else
	  	  	    			{
	  	  	    			orderPayment.setCardType("");
	  	  	    			}
	  	  	    		    GenericValue giftCard = orderPaymentPreference.getRelatedOne("GiftCard");
		  	  	    		if(UtilValidate.isNotEmpty(giftCard))
	  	  	    			{
		  	  	    		    cardNumber = giftCard.getString("cardNumber");
	  	    	    		    cardExpireDate = giftCard.getString("expireDate");
	  	  	    			}
		  	  	    		if(UtilValidate.isNotEmpty(cardNumber))
		  	  	    		{
		  	  	    		    orderPayment.setCardNumber(cardNumber);
		  	  	    		}
		  	  	    		else
		  	  	    		{
		  	  	    		    orderPayment.setCardNumber("");
		  	  	    		}
			  	  	    	if(UtilValidate.isNotEmpty(cardExpireDate))
		  	  	    		{
		  	  	    		    orderPayment.setExpiryDate(cardExpireDate);
		  	  	    		}
		  	  	    		else
		  	  	    		{
		  	  	    		    orderPayment.setExpiryDate("");
		  	  	    		}
	  	    		    }
	    			} 
	    	    	
	    	    	if(UtilValidate.isNotEmpty(gatewayResponses)) 
	    	    	{
	    	    		PaymentGatewayResponseType paymentGatewayResponseType = factory.createPaymentGatewayResponseType();
	    	    		List gatewayResponseList = paymentGatewayResponseType.getGatewayResponse();
	    	    		if(UtilValidate.isNotEmpty(gatewayResponses))
	    	    		{
	    	    			for(GenericValue gatewayResponse : gatewayResponses) 
		    	    		{
		    	    			GatewayResponseType gatewayResponseType = factory.createGatewayResponseType();
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.getString("transCodeEnumId")))
		    	    			{
		    	    				gatewayResponseType.setTransCodeEnumId(gatewayResponse.getString("transCodeEnumId"));
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setTransCodeEnumId("");
		    	    			}
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.get("amount")))
		    	    			{
		    	    				gatewayResponseType.setAmount((gatewayResponse.getBigDecimal("amount").setScale(scale, rounding)).toString());
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setAmount("");
		    	    			}
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.getString("referenceNum")))
		    	    			{
		    	    				gatewayResponseType.setReferenceNumber(gatewayResponse.getString("referenceNum"));
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setReferenceNumber("");
		    	    			}
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.getString("altReference")))
		    	    			{
		    	    				gatewayResponseType.setAltReferenceNumber(gatewayResponse.getString("altReference"));
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setAltReferenceNumber("");
		    	    			}
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.get("transactionDate")))
		    	    			{
		    	    				gatewayResponseType.setTransactionDate(gatewayResponse.get("transactionDate").toString());
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setTransactionDate("");
		    	    			}
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.getString("gatewayCode")))
		    	    			{
		    	    				gatewayResponseType.setGatewayCode(gatewayResponse.getString("gatewayCode"));
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setGatewayCode("");
		    	    			}
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.getString("gatewayFlag")))
		    	    			{
		    	    				gatewayResponseType.setGatewayFlag(gatewayResponse.getString("gatewayFlag"));
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setGatewayFlag("");
		    	    			}
		    	    			if(UtilValidate.isNotEmpty(gatewayResponse.getString("gatewayMessage")))
		    	    			{
		    	    				gatewayResponseType.setGatewayMessage(gatewayResponse.getString("gatewayMessage"));
		    	    			}
		    	    			else
		    	    			{
		    	    				gatewayResponseType.setGatewayMessage("");
		    	    			}
		    	    			gatewayResponseList.add(gatewayResponseType);
		    	    		}
	    	    		}
	    	    		orderPayment.setPaymentGatewayResponse(paymentGatewayResponseType);
	    	    	}
	    	    	paymentList.add(orderPayment);
	    		}
	    	}
	    }
	    catch(Exception e)
	    {
	    	Debug.logInfo("Error in Export Order Payment "+e, module);
	    }
	    order.setOrderPayment(orderPaymentType);
    }
    
    public static void setOrderAttributeFeed(ObjectFactory factory, GenericValue orderHeader, OrderType order, OrderReadHelper orderReadHelper)
    {
    	
    	OrderAttributeType orderAttributeType = factory.createOrderAttributeType();
    	String orderId = orderHeader.getString("orderId"); 
	    try
	    {
  	    	List attributeList = orderAttributeType.getAttribute();
  	    	List<GenericValue> orderAttributeList = _delegator.findByAnd("OrderAttribute", UtilMisc.toMap("orderId", orderId));
  	    	if(UtilValidate.isNotEmpty(orderAttributeList)) 
  	    	{
  	    	    for(GenericValue orderAttribute : orderAttributeList) 
  	    	    {
  	    		    AttributeType attribute = factory.createAttributeType();
  	    		    attribute.setName(orderAttribute.getString("attrName"));
  	    		    if(UtilValidate.isNotEmpty(orderAttribute.getString("attrValue")))
  	    		    {
  	    		    	attribute.setValue(orderAttribute.getString("attrValue"));
  	    		    }
  	    		    else
  	    		    {
  	    		    	attribute.setValue("");
  	    		    }
  	    		    attributeList.add(attribute);
  	    	    }
  	    	}
	    }
	    catch(Exception e)
	    {
	    	Debug.logInfo("Error in Export Order Attributes "+e, module);
	    }
	    order.setOrderAttribute(orderAttributeType);
    }
    
    public static void setOrderCustomerFeed(ObjectFactory factory, GenericValue orderHeader, OrderType order, OrderReadHelper orderReadHelper)
    {
    	
    	CustomerType customer = factory.createCustomerType();
    	String orderId = orderHeader.getString("orderId");
    	String orderType = orderHeader.getString("orderTypeId");
    	String productStoreId = orderReadHelper.getProductStoreId();
	    try
	    {
	    	GenericValue displayParty = null;
  	        String displayPartyId = "";
  	        if ("PURCHASE_ORDER".equals(orderType)) 
  	        {
  	            displayParty = orderReadHelper.getSupplierAgent();
  	        }
  	        else 
  	        {
  	            displayParty = orderReadHelper.getPlacingParty();
  	        }
  	        if(UtilValidate.isNotEmpty(displayParty)) 
  	        {
  	        	displayPartyId = (String)displayParty.get("partyId");
  	        	customer.setCustomerId(displayPartyId);
  	        	
  	        	List<GenericValue> partyEmailDetails = (List<GenericValue>) ContactHelper.getContactMech(displayParty, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
            	if(UtilValidate.isNotEmpty(partyEmailDetails))
            	{
            		GenericValue partyEmailDetail = EntityUtil.getFirst(partyEmailDetails);
            		if(UtilValidate.isNotEmpty(partyEmailDetail.getString("infoString")))
            		{
            			customer.setEmailAddress(partyEmailDetail.getString("infoString"));
            		}
            		else
            		{
            			customer.setEmailAddress("");
            		}
            	}
                
                GenericValue person = _delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", displayPartyId));
                
                if(UtilValidate.isNotEmpty(person))
                {
                	if(UtilValidate.isNotEmpty(person.getString("firstName")))
        	        {
        	        	customer.setFirstName(person.getString("firstName"));
        	        }
        	        else
        	        {
        	        	customer.setFirstName("");
        	        }
        	        if(UtilValidate.isNotEmpty(person.getString("lastName")))
        	        {
        	        	customer.setLastName(person.getString("lastName"));
        	        }
        	        else
        	        {
        	        	customer.setLastName("");
        	        }
                }
    	        
    	        String homePhone = FeedsUtil.getPartyPhoneNumber(displayPartyId, "PHONE_HOME", _delegator);
    	        customer.setHomePhone(homePhone);
    	        String cellPhone = FeedsUtil.getPartyPhoneNumber(displayPartyId, "PHONE_MOBILE", _delegator);
    	        customer.setCellPhone(cellPhone);
    	        String workPhone = FeedsUtil.getPartyPhoneNumber(displayPartyId, "PHONE_WORK", _delegator);
    	        customer.setWorkPhone(workPhone);
    	        String workPhoneExt = FeedsUtil.getPartyPhoneExt(displayPartyId, "PHONE_WORK", _delegator);
    	        customer.setWorkPhoneExt(workPhoneExt);
  	        }
  	          
	        List<Map<String, GenericValue>> contactMechValueMaps = ContactMechWorker.getOrderContactMechValueMaps(_delegator, orderId);
	        List billingAddressList = FastList.newInstance();
	        List shippingAddressList = FastList.newInstance();
	        
	        if(UtilValidate.isNotEmpty(contactMechValueMaps))
	        {
	        	for(Map<String, GenericValue> contactMechValueMap : contactMechValueMaps) 
		        {
					GenericValue contactMechPurpose = (GenericValue)contactMechValueMap.get("contactMechPurposeType");
					if(contactMechPurpose.getString("contactMechPurposeTypeId").equals("BILLING_LOCATION"))
					{
						BillingAddressType billingAddress = factory.createBillingAddressType();
						billingAddressList = customer.getBillingAddress();
			    	    GenericValue postalAddress = (GenericValue)contactMechValueMap.get("postalAddress");
			    	    
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("toName")))
			    	    {
			    	    	billingAddress.setToName(postalAddress.getString("toName"));
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setToName("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address1")))
			    	    {
			    	    	billingAddress.setAddress1(postalAddress.getString("address1"));
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setAddress1("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address2")))
			    	    {
			    	    	billingAddress.setAddress2(postalAddress.getString("address2"));
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setAddress2("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address3")))
			    	    {
			    	    	billingAddress.setAddress3(postalAddress.getString("address3"));
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setAddress3("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("countryGeoId")))
			    	    {
				    	    if (displayCountryFieldAsLong(productStoreId))
				    	    {
				    	    	billingAddress.setCountry(getGeoName(postalAddress.getString("countryGeoId")));
				    	    }
				    	    else
				    	    {
				    	    	billingAddress.setCountry(postalAddress.getString("countryGeoId"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setCountry("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("city")))
			    	    {
			    	    	billingAddress.setCityTown(postalAddress.getString("city"));
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setCityTown("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId")))
			    	    {
				    	    if (displayStateFieldAsLong(productStoreId))
				    	    {
				    	    	billingAddress.setStateProvince(getGeoName(postalAddress.getString("stateProvinceGeoId")));
				    	    }
				    	    else
				    	    {
				    	    	billingAddress.setStateProvince(postalAddress.getString("stateProvinceGeoId"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setStateProvince("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("postalCode")))
			    	    {
				    	    if (displayZipFieldAsLong(productStoreId))
				    	    {
				    	        String postalCode = postalAddress.getString("postalCode");
				    	        if (UtilValidate.isNotEmpty(postalAddress.getString("postalCodeExt")))
				    	        {
				    	            postalCode = postalCode+"-"+postalAddress.getString("postalCodeExt");
				    	        }
					    	    billingAddress.setZipPostCode(postalCode);
				    	    }
				    	    else
				    	    {
					    	    billingAddress.setZipPostCode(postalAddress.getString("postalCode"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	billingAddress.setZipPostCode("");
			    	    }
			            billingAddressList.add(billingAddress);
					}
					if(contactMechPurpose.getString("contactMechPurposeTypeId").equals("SHIPPING_LOCATION"))
					{
						ShippingAddressType shippingAddress = factory.createShippingAddressType();
						shippingAddressList = customer.getShippingAddress();
			    	    GenericValue postalAddress = (GenericValue)contactMechValueMap.get("postalAddress");
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("toName")))
			    	    {
			    	    	shippingAddress.setToName(postalAddress.getString("toName"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setToName("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address1")))
			    	    {
			    	    	shippingAddress.setAddress1(postalAddress.getString("address1"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setAddress1("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address2")))
			    	    {
			    	    	shippingAddress.setAddress2(postalAddress.getString("address2"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setAddress2("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("address3")))
			    	    {
			    	    	shippingAddress.setAddress3(postalAddress.getString("address3"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setAddress3("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("countryGeoId")))
			    	    {
				    	    if (displayCountryFieldAsLong(productStoreId))
				    	    {
				    	    	shippingAddress.setCountry(getGeoName(postalAddress.getString("countryGeoId")));
				    	    }
				    	    else
				    	    {
				    	    	shippingAddress.setCountry(postalAddress.getString("countryGeoId"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setCountry("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("city")))
			    	    {
			    	    	shippingAddress.setCityTown(postalAddress.getString("city"));
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setCityTown("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId")))
			    	    {
				    	    if (displayStateFieldAsLong(productStoreId))
				    	    {
				    	    	shippingAddress.setStateProvince(getGeoName(postalAddress.getString("stateProvinceGeoId")));
				    	    }
				    	    else
				    	    {
				    	    	shippingAddress.setStateProvince(postalAddress.getString("stateProvinceGeoId"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setStateProvince("");
			    	    }
			    	    if(UtilValidate.isNotEmpty(postalAddress.getString("postalCode")))
			    	    {
				    	    if (displayZipFieldAsLong(productStoreId))
				    	    {
				    	        String postalCode = postalAddress.getString("postalCode");
				    	        if (UtilValidate.isNotEmpty(postalAddress.getString("postalCodeExt")))
				    	        {
				    	            postalCode = postalCode+"-"+postalAddress.getString("postalCodeExt");
				    	        }
				    	        shippingAddress.setZipPostCode(postalCode);
				    	    }
				    	    else
				    	    {
				    	    	shippingAddress.setZipPostCode(postalAddress.getString("postalCode"));
				    	    }
			    	    }
			    	    else
			    	    {
			    	    	shippingAddress.setZipPostCode("");
			    	    }
			    	    shippingAddressList.add(shippingAddress);
					}
		        }
	        }
	        
	    }
	    catch(Exception e)
	    {
	    	Debug.logInfo("Error in export Order Customer "+e, module);
	    }
	    order.setCustomer(customer);
    }
    
    public static void setOrderAdjustmentFeed(ObjectFactory factory, GenericValue orderHeader, OrderType order, OrderReadHelper orderReadHelper)
    {
    	
    	List<GenericValue> orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
	    OrderAdjustmentType orderAdjustmentType = factory.createOrderAdjustmentType();
	    List adjustmentList = orderAdjustmentType.getAdjustment();
	    try
	    {
	    	if(UtilValidate.isNotEmpty(orderHeaderAdjustments))
	    	{
	    		for(GenericValue orderHeaderAdjustment : orderHeaderAdjustments)
		    	{
		    		AdjustmentType adjustment = factory.createAdjustmentType();
		    		BigDecimal adjAmount = BigDecimal.ZERO;
		    		adjAmount = orderHeaderAdjustment.getBigDecimal("amount").setScale(scale, rounding);
		    		
		    		if(UtilValidate.isNotEmpty(orderHeaderAdjustment.getString("shipGroupSeqId")))
		    		{
		    			adjustment.setShipGroupSequenceId(orderHeaderAdjustment.getString("shipGroupSeqId"));
		    		}
		    		else
		    		{
		    			adjustment.setShipGroupSequenceId("");
		    		}
		    		
		    		if(orderHeaderAdjustment.getString("orderAdjustmentTypeId").equals("LOYALTY_POINTS"))
		    		{
		    			List<GenericValue> orderAdjustmentAttributes = orderHeaderAdjustment.getRelated("OrderAdjustmentAttribute");
		    			for(GenericValue orderAdjustmentAttribute : orderAdjustmentAttributes)
		    			{
		    				String adjustmentAttrValue = "";
		    				if(UtilValidate.isNotEmpty(orderAdjustmentAttribute.getString("attrValue")))
		    				{
		    					adjustmentAttrValue = orderAdjustmentAttribute.getString("attrValue");
		    				}
		    				
		    				if(orderAdjustmentAttribute.getString("attrName").equals("ADJUST_METHOD"))
		    				{
		    					adjustment.setAdjustMethod(adjustmentAttrValue);
		    				}
		    				else if(orderAdjustmentAttribute.getString("attrName").equals("ADJUST_POINTS"))
		    				{
		    					adjustment.setAdjustPoints(adjustmentAttrValue);
		    				}
		    				else if(orderAdjustmentAttribute.getString("attrName").equals("CONVERSION_FACTOR"))
		    				{
		    					adjustment.setAdjustConversion(adjustmentAttrValue);
		    				}
		    				else if(orderAdjustmentAttribute.getString("attrName").equals("MEMBER_ID"))
		    				{
		    					adjustment.setAdjustMemberId(adjustmentAttrValue);
		    				}
		    			}
		    		}
		    		else if(orderHeaderAdjustment.getString("orderAdjustmentTypeId").equals("SALES_TAX"))
		    		{
		    			if(UtilValidate.isNotEmpty(orderHeaderAdjustment.get("sourcePercentage")))
	    				{
		    				adjustment.setTaxPercent((orderHeaderAdjustment.getBigDecimal("sourcePercentage").setScale(3, rounding)).toString());
	    				}
	    				else
	    				{
	    					adjustment.setTaxPercent("");
	    				}
	    				if(UtilValidate.isNotEmpty(orderHeaderAdjustment.getString("taxAuthGeoId")))
	    				{
	    					adjustment.setTaxAuthorityGeo(orderHeaderAdjustment.getString("taxAuthGeoId"));
	    				}
	    				else
	    				{
	    					adjustment.setTaxAuthorityGeo("");
	    				}
	    				if(UtilValidate.isNotEmpty(orderHeaderAdjustment.getString("taxAuthPartyId")))
	    				{
	    					adjustment.setTaxAuthorityParty(orderHeaderAdjustment.getString("taxAuthPartyId"));
	    				}
	    				else
	    				{
	    					adjustment.setTaxAuthorityParty("");
	    				}
		    		}
		    		else if(orderHeaderAdjustment.getString("orderAdjustmentTypeId").equals("PROMOTION_ADJUSTMENT"))
		    		{
		    			GenericValue productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo");
		  	    		String promoCodeText = "";
		  	    		if(UtilValidate.isNotEmpty(productPromo)) 
		  	    		{
		  	    			List<GenericValue> productPromoCode = productPromo.getRelated("ProductPromoCode");
		  	    			Set<String> promoCodesEntered = orderReadHelper.getProductPromoCodesEntered();
		  	    			if(UtilValidate.isNotEmpty(promoCodesEntered)) 
		  	    			{
		  	    				for(String promoCodeEntered : promoCodesEntered) 
		  	    				{
		  	    					if(UtilValidate.isNotEmpty(productPromoCode)) 
		  	    					{
		  	    						for(GenericValue promoCode : productPromoCode) 
		  	    						{
		  	    							String promoCodeEnteredId = promoCodeEntered;
		  	    							String promoCodeId = (String) promoCode.get("productPromoCodeId");
		  	    							if(UtilValidate.isNotEmpty(promoCodeEnteredId)) 
		  	    							{
		  	    								if(promoCodeId.equals(promoCodeEnteredId)) 
		  	    								{
		  	    									promoCodeText = (String)promoCode.get("productPromoCodeId");
		  	    								}
		  	    							}
		  	    						}
		  	    					}
		  	    				}
		  	    			}
		  	    			else
		  	    			{
		  	    				promoCodeText = productPromo.getString("promoName");
		  	    			}
		  	    		}
		  	    	    adjustment.setPromotionCode(promoCodeText);
		    		}
		    		adjustment.setAdjustmentType(orderHeaderAdjustment.getString("orderAdjustmentTypeId"));
		    		adjustment.setAmount(adjAmount.toString());
		    		adjustmentList.add(adjustment);
		    	}
	    	}
	    }
	    catch(Exception e)
	    {
	    	Debug.logInfo("Error in export Order Adjustments "+e, module);
	    }
	    order.setOrderAdjustment(orderAdjustmentType);
    }
    
    public static Map<String, Object> exportCustRequestContactUsXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        List<String> custRequestIdList = (List)context.get("custRequestIdList");
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("custrequest");
        
        Map result = ServiceUtil.returnSuccess();
        
        String custRequestFileName = "ContactUs";
        if(custRequestIdList.size() == 1) 
        {
        	custRequestFileName = custRequestFileName + custRequestIdList.get(0);
        }
        custRequestFileName = custRequestFileName + "_" + (OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HHmm"));
        custRequestFileName = UtilValidate.stripWhitespace(custRequestFileName) + ".xml";
        
        if (!new File(downloadTempDir).exists()) 
        {
        	new File(downloadTempDir).mkdirs();
	    }
        
        File file = new File(downloadTempDir + custRequestFileName);
  	  
        ObjectFactory factory = new ObjectFactory();
        
        BigFishContactUsFeedType bfContactUsFeedType = factory.createBigFishContactUsFeedType();
  	 
        List contactUsList = bfContactUsFeedType.getContactUs();
  	  
        List<String> exportedCustRequestIdList = FastList.newInstance();
        
        ContactUsType contactUs = null;
        
  	    for(String custRequestId : custRequestIdList) 
  	    {
  	    	contactUs = factory.createContactUsType();
  	    	
  	    	try
  	    	{
  	    		GenericValue custRequest = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId", custRequestId), false);
  	    		GenericValue productStore= custRequest.getRelatedOne("ProductStore");
  	    		if (UtilValidate.isNotEmpty(productStore))
  	    		{
  	  	    		contactUs.setProductStoreId(productStore.getString("productStoreId"));
  	  	    		contactUs.setProductStoreName(productStore.getString("storeName"));
  	    		}
  	    		contactUs.setContactUsId(custRequestId);
  	    		String firstName = "";
  	    		String lastName = "";
  	    		String emailAddress = "";
  	    		String orderId = "";
  	    		String contactPhone = "";
  	    		String comment = "";

  	    		List<GenericValue> custReqAttributeList = custRequest.getRelated("CustRequestAttribute");
  	    		for(GenericValue custReqAttribute : custReqAttributeList) 
  	    		{
  	    			if(custReqAttribute.get("attrName").equals("FIRST_NAME")) 
  	    			{
  	    				firstName = (String) custReqAttribute.get("attrValue"); 
  	    			}
                    if(custReqAttribute.get("attrName").equals("LAST_NAME")) 
                    {
                    	lastName = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ORDER_NUMBER")) 
                    {
                    	orderId = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("EMAIL_ADDRESS")) 
                    {
                    	emailAddress = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("CONTACT_PHONE")) 
                    {
                    	contactPhone = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("COMMENT")) 
                    {
                    	comment = (String) custReqAttribute.get("attrValue");
  	    			}
  	    		}
  	    		if(contactPhone.length()> 6) 
  	    		{
  	    			contactPhone = contactPhone.substring(0,3)+"-"+contactPhone.substring(3,6)+"-"+contactPhone.substring(6);
  	    		}
  	    		contactUs.setFirstName(firstName);
  	    		contactUs.setLastName(lastName);
  	    		contactUs.setOrderId(orderId);
  	    		contactUs.setContactPhone(contactPhone);
  	    		contactUs.setComment(StringUtil.wrapString(comment).toString());
  	    		contactUs.setEmailAddress(emailAddress);
  	    		
  	    		contactUsList.add(contactUs);
  	    		exportedCustRequestIdList.add(custRequestId);
  	    	}
  	    	catch (Exception e)
  	    	{
  	    		e.printStackTrace();
  	    		messages.add("Error in Customer Contact Us Export.");
  	    	}
  	    }
  	  
      FeedsUtil.marshalObject(new JAXBElement<BigFishContactUsFeedType>(new QName("", "BigFishContactUsFeed"), BigFishContactUsFeedType.class, null, bfContactUsFeedType), file);
      result.put("feedsDirectoryPath", downloadTempDir);
      result.put("feedsFileName", custRequestFileName);
      result.put("feedsExportedIdList", exportedCustRequestIdList);
      return result;
    }
    
    public static Map<String, Object> exportCustRequestCatalogXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        List<String> custRequestIdList = (List)context.get("custRequestIdList");
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("custrequest");
        
        Map result = ServiceUtil.returnSuccess();
        
        String custRequestFileName = "RequestCatalog";
        if(custRequestIdList.size() == 1)
        {
        	custRequestFileName = custRequestFileName + custRequestIdList.get(0);
        }
        custRequestFileName = custRequestFileName + "_" + (OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HHmm"));
        custRequestFileName = UtilValidate.stripWhitespace(custRequestFileName) + ".xml";
        
        if (!new File(downloadTempDir).exists()) 
        {
        	new File(downloadTempDir).mkdirs();
	    }
        
        File file = new File(downloadTempDir + custRequestFileName);
  	  
        ObjectFactory factory = new ObjectFactory();
        
        BigFishRequestCatalogFeedType bfRequestCatalogFeedType = factory.createBigFishRequestCatalogFeedType();
  	 
        List requestCatalogList = bfRequestCatalogFeedType.getRequestCatalog();
  	  
        List<String> exportedCustRequestIdList = FastList.newInstance();
        
        RequestCatalogType customerRequest = null;
        
  	    for(String custRequestId : custRequestIdList)
  	    {
  	    	customerRequest = factory.createRequestCatalogType();
  	    	
  	    	try
  	    	{
  	    		GenericValue custRequest = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId", custRequestId), false);
  	    		GenericValue productStore= custRequest.getRelatedOne("ProductStore");
  	    		if (UtilValidate.isNotEmpty(productStore))
  	    		{
  	    			customerRequest.setProductStoreId(productStore.getString("productStoreId"));
  	    			customerRequest.setProductStoreName(productStore.getString("storeName"));
  	    		}
  	    		customerRequest.setRequestCatalogId(custRequestId);
  	    		String firstName = "";
  	    		String lastName = "";
  	    		String country = "";
  	    		String address1 = "";
  	    		String address2 = "";
  	    		String address3 = "";
  	    		String city = "";
  	    		String state = "";
  	    		String zip = "";
  	    		String emailAddress = "";
  	    		String contactPhone = "";
  	    		String comment = "";
  	    		
  	    		List<GenericValue> custReqAttributeList = custRequest.getRelated("CustRequestAttribute");
  	    		for(GenericValue custReqAttribute : custReqAttributeList)
  	    		{
  	    			if(custReqAttribute.get("attrName").equals("FIRST_NAME"))
  	    			{
  	    				firstName = (String) custReqAttribute.get("attrValue"); 
  	    			}
                    if(custReqAttribute.get("attrName").equals("LAST_NAME"))
                    {
                    	lastName = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("COUNTRY"))
                    {
                    	country = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ADDRESS1"))
                    {
                    	address1 = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ADDRESS2"))
                    {
                    	address2 = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ADDRESS3"))
                    {
                    	address3 = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("CITY"))
                    {
                    	city = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("STATE_PROVINCE"))
                    {
                    	state = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ZIP_POSTAL_CODE"))
                    {
                    	zip = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("EMAIL_ADDRESS"))
                    {
                    	emailAddress = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("CONTACT_PHONE"))
                    {
                    	contactPhone = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("COMMENT"))
                    {
                    	comment = (String) custReqAttribute.get("attrValue");
  	    			}
  	    		}
  	    		if(contactPhone.length()> 6) 
  	    		{
  	    			contactPhone = contactPhone.substring(0,3)+"-"+contactPhone.substring(3,6)+"-"+contactPhone.substring(6);
  	    		}
  	    		customerRequest.setFirstName(firstName);
  	    		customerRequest.setLastName(lastName);
  	    		customerRequest.setCountry(country);
  	    		customerRequest.setAddress1(address1);
  	    		customerRequest.setAddress2(address2);
  	    		customerRequest.setAddress3(address3);
  	    		customerRequest.setCityTown(city);
  	    		customerRequest.setStateProvince(state);
  	    		customerRequest.setZipPostCode(zip);
  	    		customerRequest.setContactPhone(contactPhone);
  	    		customerRequest.setComment(StringUtil.wrapString(comment).toString());
  	    		customerRequest.setEmailAddress(emailAddress);
  	    		
  	    		requestCatalogList.add(customerRequest);
  	    		
  	    		exportedCustRequestIdList.add(custRequestId);
  	    	}
  	    	catch (Exception e)
  	    	{
  	    		e.printStackTrace();
  	    		messages.add("Error in Customer Export.");
  	    	}
  	    }
  	  
        FeedsUtil.marshalObject(new JAXBElement<BigFishRequestCatalogFeedType>(new QName("", "BigFishRequestCatalogFeed"), BigFishRequestCatalogFeedType.class, null, bfRequestCatalogFeedType), file);
      
        result.put("feedsDirectoryPath", downloadTempDir);
        result.put("feedsFileName", custRequestFileName);
        result.put("feedsExportedIdList", exportedCustRequestIdList);
        return result;
    }
    
    
    
    public static Map<String, Object> exportCustomerXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        _delegator =  ctx.getDelegator();
        String productStoreId = (String)context.get("productStoreId");
        ObjectFactory factory = new ObjectFactory();
        
        BigFishCustomerFeedType bfCustomerFeedType = factory.createBigFishCustomerFeedType();
        
        List<String> customerIdList = (List)context.get("customerList");
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("customer");
        
        Map result = ServiceUtil.returnSuccess();
        
        String customerFileName = "Customer";
        if(customerIdList.size() == 1)
        {
        	customerFileName = customerFileName + customerIdList.get(0);
        }
        customerFileName = customerFileName + "_" + (OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HHmm"));
        customerFileName = UtilValidate.stripWhitespace(customerFileName) + ".xml";
        
        if (!new File(downloadTempDir).exists()) 
        {
        	new File(downloadTempDir).mkdirs();
	    }
        
        File file = new File(downloadTempDir + customerFileName);
        
        CustomerType customerType = null;
        
        List customerList = bfCustomerFeedType.getCustomer();
        
        List<String> exportedCustomerIdList = FastList.newInstance();
  	  
        CustomerType customer = null;
  	    for(String customerId : customerIdList)
  	    {
  	    	GenericValue party = null;
  	    	GenericValue person = null;
  	    	
  	    	try
  	    	{
  	    	    party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", customerId));
  	    	    person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", customerId));
  	    	
  	    	    String partyId = (String)party.get("partyId");
  	    	    
  	    	    customer = factory.createCustomerType();
  	    	    
  	    	    GenericValue partyEmailFormatAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","PARTY_EMAIL_PREFERENCE"));
  	    	    
  	    	    List<GenericValue> partyContactDetails = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", customerId));
                partyContactDetails = EntityUtil.filterByDate(partyContactDetails);
                partyContactDetails = EntityUtil.filterByDate(partyContactDetails, UtilDateTime.nowTimestamp(), "purposeFromDate", "purposeThruDate", true);
  	    	
                List<GenericValue> partyEmailDetails = EntityUtil.filterByAnd(partyContactDetails, UtilMisc.toMap("contactMechPurposeTypeId","PRIMARY_EMAIL"));
                GenericValue partyEmailDetail = EntityUtil.getFirst(partyEmailDetails);
                
                
                List<GenericValue> partyContactMechPurpose = party.getRelated("PartyContactMechPurpose");
                List<GenericValue> billingContactMechList = FastList.newInstance();
                List<GenericValue> shippingContactMechList = FastList.newInstance();
                
            	if (UtilValidate.isNotEmpty(partyContactMechPurpose))
            	{
        	        partyContactMechPurpose = EntityUtil.filterByDate(partyContactMechPurpose,true);
        	
        	        List<GenericValue> partyBillingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
        	        partyBillingLocations = EntityUtil.getRelated("PartyContactMech", partyBillingLocations);
        	        partyBillingLocations = EntityUtil.filterByDate(partyBillingLocations,true);
        	        partyBillingLocations = EntityUtil.orderBy(partyBillingLocations, UtilMisc.toList("fromDate DESC"));
        	        if (UtilValidate.isNotEmpty(partyBillingLocations)) 
        	        {
        	        	GenericValue partyBillingLocation = EntityUtil.getFirst(partyBillingLocations);
        	            billingContactMechList = EntityUtil.getRelated("ContactMech",partyBillingLocations);
        	            //context.billingContactMechList = billingContactMechList;
        	        }
        	
        	        
        	        List<GenericValue> partyShippingLocations = EntityUtil.filterByAnd(partyContactMechPurpose, UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
        	        partyShippingLocations = EntityUtil.getRelated("PartyContactMech", partyShippingLocations);
        	        partyShippingLocations = EntityUtil.filterByDate(partyShippingLocations,true);
        	        partyShippingLocations = EntityUtil.orderBy(partyShippingLocations, UtilMisc.toList("fromDate DESC"));
        	        if (UtilValidate.isNotEmpty(partyShippingLocations)) 
        	        {
        	        	GenericValue partyShippingLocation = EntityUtil.getFirst(partyShippingLocations);
        	            shippingContactMechList = EntityUtil.getRelated("ContactMech",partyShippingLocations);
        	        }
            	}
                List billingAddressList = null;
            	for(GenericValue billingContactMech : billingContactMechList) 
    	        {
    				GenericValue postalAddress = billingContactMech.getRelatedOne("PostalAddress");
    				BillingAddressType billingAddress = factory.createBillingAddressType();
    				billingAddressList = customer.getBillingAddress();
    		    	    
		    	    String address1 = (String)postalAddress.get("address1");
		    	    String address2 = (String)postalAddress.get("address2");
		    	    String address3 = (String)postalAddress.get("address3");
		    	    String city = (String)postalAddress.get("city");
		    	    billingAddress.setAddress1(address1);
		    	    billingAddress.setAddress2(address2);
		    	    billingAddress.setAddress3(address3);
		    	    billingAddress.setCityTown(city);
		    	    if (displayCountryFieldAsLong(productStoreId))
		    	    {
			    	    billingAddress.setCountry(getGeoName(postalAddress.getString("countryGeoId")));
		    	    }
		    	    else
		    	    {
			    	    billingAddress.setCountry(postalAddress.getString("countryGeoId"));
		    	    }
		    	    if (displayStateFieldAsLong(productStoreId))
		    	    {
			    	    billingAddress.setStateProvince(getGeoName(postalAddress.getString("stateProvinceGeoId")));
		    	    }
		    	    else
		    	    {
			    	    billingAddress.setStateProvince(postalAddress.getString("stateProvinceGeoId"));
		    	    }
		    	    if (displayZipFieldAsLong(productStoreId))
		    	    {
		    	        String postalCode = postalAddress.getString("postalCode");
		    	        if (UtilValidate.isNotEmpty(postalAddress.getString("postalCodeExt")))
		    	        {
		    	            postalCode = postalCode+"-"+postalAddress.getString("postalCodeExt");
		    	        }
			    	    billingAddress.setZipPostCode(postalCode);
		    	    }
		    	    else
		    	    {
			    	    billingAddress.setZipPostCode(postalAddress.getString("postalCode"));
		    	    }
		            billingAddressList.add(billingAddress);
    				
    	        }
                
            	List shippingAddressList = null;
            	for(GenericValue shippingContactMech : shippingContactMechList) 
    	        {
    				GenericValue postalAddress = shippingContactMech.getRelatedOne("PostalAddress");
    				ShippingAddressType shippingAddress = factory.createShippingAddressType();
    				shippingAddressList = customer.getShippingAddress();
    		    	    
		    	    String address1 = (String)postalAddress.get("address1");
		    	    String address2 = (String)postalAddress.get("address2");
		    	    String address3 = (String)postalAddress.get("address3");
		    	    String city = (String)postalAddress.get("city");
		    	    shippingAddress.setAddress1(address1);
		    	    shippingAddress.setAddress2(address2);
		    	    shippingAddress.setAddress3(address3);
		    	    shippingAddress.setCityTown(city);
		    	    if (displayCountryFieldAsLong(productStoreId))
		    	    {
		    	    	shippingAddress.setCountry(getGeoName(postalAddress.getString("countryGeoId")));
		    	    }
		    	    else
		    	    {
		    	    	shippingAddress.setCountry(postalAddress.getString("countryGeoId"));
		    	    }
		    	    if (displayStateFieldAsLong(productStoreId))
		    	    {
		    	    	shippingAddress.setStateProvince(getGeoName(postalAddress.getString("stateProvinceGeoId")));
		    	    }
		    	    else
		    	    {
		    	    	shippingAddress.setStateProvince(postalAddress.getString("stateProvinceGeoId"));
		    	    }
		    	    if (displayZipFieldAsLong(productStoreId))
		    	    {
		    	        String postalCode = postalAddress.getString("postalCode");
		    	        if (UtilValidate.isNotEmpty(postalAddress.getString("postalCodeExt")))
		    	        {
		    	            postalCode = postalCode+"-"+postalAddress.getString("postalCodeExt");
		    	        }
		    	        shippingAddress.setZipPostCode(postalCode);
		    	    }
		    	    else
		    	    {
		    	    	shippingAddress.setZipPostCode(postalAddress.getString("postalCode"));
		    	    }
		    	    shippingAddressList.add(shippingAddress);
    				
    	        }
    	        
                GenericValue partyGenderAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","GENDER"));
    	        String gender = "";
    	        if(UtilValidate.isNotEmpty(partyGenderAttr))
    	        {
    	        	if("M".equals(partyGenderAttr.getString("attrValue")))
    	        	{
        	        	gender = "MALE";
        	        }
    	        	else if ("F".equals(partyGenderAttr.getString("attrValue")))
        	        {
        	        	gender = "FEMALE";
        	        }
    	        }
    	        
    	        String allowSolicitation = (String)partyEmailDetail.get("allowSolicitation");
    	        if(UtilValidate.isEmpty(allowSolicitation))
    	        {
    	        	allowSolicitation = "";
    	        }
    	        if(allowSolicitation.equalsIgnoreCase("Y"))
    	        {
    	        	allowSolicitation = "TRUE";
    	        } else if (allowSolicitation.equalsIgnoreCase("N"))
    	        {
    	        	allowSolicitation = "FALSE";
    	        }
    	        
    	        GenericValue partyTitleAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","TITLE"));
    	        String title = "";
    	        if(UtilValidate.isNotEmpty(partyTitleAttr))
    	        {
    	        	title = partyTitleAttr.getString("attrValue");
    	        }
    	        
    	        GenericValue partyIsDownloadedAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","IS_DOWNLOADED"));
    	        String isDownloaded = "";
    	        if(UtilValidate.isNotEmpty(partyIsDownloadedAttr))
    	        {
    	        	isDownloaded = partyIsDownloadedAttr.getString("attrValue");
    	        }
    	        
    	        GenericValue partyDobDdMmYyyyAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","DOB_DDMMYYYY"));
    	        String dobDdMmYyyy = "";
    	        if(UtilValidate.isNotEmpty(partyDobDdMmYyyyAttr))
    	        {
    	        	dobDdMmYyyy = partyDobDdMmYyyyAttr.getString("attrValue");
    	        }
    	        
    	        GenericValue partyDobDdMmAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","DOB_DDMM"));
    	        String dobDdMm = "";
    	        if(UtilValidate.isNotEmpty(partyDobDdMmAttr))
    	        {
    	        	dobDdMm = partyDobDdMmAttr.getString("attrValue");
    	        }
    	        
    	        GenericValue partyDobMmDdYyyyAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","DOB_MMDDYYYY"));
    	        String dobMmDdYyyy = "";
    	        if(UtilValidate.isNotEmpty(partyDobMmDdYyyyAttr))
    	        {
    	        	dobMmDdYyyy = partyDobMmDdYyyyAttr.getString("attrValue");
    	        }
    	        
    	        GenericValue partyDobMmDdAttr = delegator.findByPrimaryKey("PartyAttribute", UtilMisc.toMap("partyId", customerId,"attrName","DOB_MMDD"));
    	        String dobMmDd = "";
    	        if(UtilValidate.isNotEmpty(partyDobMmDdAttr))
    	        {
    	        	dobMmDd = partyDobMmDdAttr.getString("attrValue");
    	        }
    	        
    	        //Set Customer Personal Information
    	        customer.setProductStoreId(productStoreId);
    	        customer.setCustomerId(partyId);
    	        customer.setFirstName((String)person.get("firstName"));
    	        customer.setLastName((String)person.get("lastName"));

    	        if(UtilValidate.isNotEmpty(party.get("createdStamp")))
    	        {
        	        customer.setDateRegistered(party.get("createdStamp").toString());
    	        }
    	        else 
    	        {
        	        customer.setDateRegistered("");
    	        }
    	        customer.setEmailAddress((String)partyEmailDetail.get("infoString"));
    	        customer.setEmailOptIn(allowSolicitation);
    	        String homePhone = FeedsUtil.getPartyPhoneNumber(partyId, "PHONE_HOME", delegator);
    	        customer.setHomePhone(homePhone);
    	        String cellPhone = FeedsUtil.getPartyPhoneNumber(partyId, "PHONE_MOBILE", delegator);
    	        customer.setCellPhone(cellPhone);
    	        String workPhone = FeedsUtil.getPartyPhoneNumber(partyId, "PHONE_WORK", delegator);
    	        customer.setWorkPhone(workPhone);
    	        String workPhoneExt = FeedsUtil.getPartyPhoneExt(partyId, "PHONE_WORK", delegator);
    	        customer.setWorkPhoneExt(workPhoneExt);
    	        
    	        List<GenericValue> userLogins = party.getRelated("UserLogin");
    	        if(UtilValidate.isNotEmpty(userLogins))
    	        {
    	        	GenericValue userLogin = EntityUtil.getFirst(userLogins);
    	        	UserLoginType userLoginType = factory.createUserLoginType();
    	        	userLoginType.setUserName((String)userLogin.get("userLoginId"));
    	        	userLoginType.setPassword("");
    	        	String userEnabled = "";
    	        	if(UtilValidate.isNotEmpty(userLogin.get("enabled")))
    	        	{
    	        		userEnabled = (String)userLogin.get("enabled");
    	        	}
    	        	userLoginType.setUserEnabled(userEnabled);
    	        	
    	        	String userIsSystem = "";
    	        	if(UtilValidate.isNotEmpty(userLogin.get("isSystem")))
    	        	{
    	        		userIsSystem = (String)userLogin.get("isSystem"); 
    	        	}
    	        	userLoginType.setUserIsSystem(userIsSystem);
    	        	
    	        	customer.setUserLogin(userLoginType);
    	        	
    	        }
    	        
    	        //Set Customer Attribute Detail
	  	    	CustomerAttributeType customerAttributeType = factory.createCustomerAttributeType();
	  	    	List attributeList = customerAttributeType.getAttribute();
	  	    	List<GenericValue> customerAttributeList = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("partyId", customerId));
	  	    	if(UtilValidate.isNotEmpty(customerAttributeList)) 
	  	    	{
	  	    	    for(GenericValue customerAttribute : customerAttributeList)
	  	    	    {
	  	    		    AttributeType attribute = factory.createAttributeType();
	  	    		    attribute.setName(customerAttribute.getString("attrName"));
	  	    		    attribute.setValue(customerAttribute.getString("attrValue"));
	  	    		    attributeList.add(attribute);
	  	    	    }
	  	    	}
	  	    	customer.setCustomerAttribute(customerAttributeType);
	  	    	
    	        customerList.add(customer);
    	        exportedCustomerIdList.add(customerId);
    	        Debug.logInfo("Exporting Customer "+customerId, module);
  	    	}
  	    	catch (Exception e)
  	    	{
  	    		e.printStackTrace();
  	    		messages.add("Error in Customer Export.");
  	    	}
  	    }
        
        FeedsUtil.marshalObject(new JAXBElement<BigFishCustomerFeedType>(new QName("", "BigFishCustomerFeed"), BigFishCustomerFeedType.class, null, bfCustomerFeedType), file);  
        result.put("feedsDirectoryPath", downloadTempDir);
        result.put("feedsFileName", customerFileName);
        result.put("feedsExportedIdList", exportedCustomerIdList);
        return result;

    }
    
    
    public static Map<String, Object> importProductXML(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();
        List<String> errorMessages = FastList.newInstance();

        String xmlDataFilePath = (String)context.get("xmlDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        String loadImagesDirPath=(String)context.get("productLoadImagesDir");
        String imageUrl = (String)context.get("imageUrl");
        Boolean removeAll = (Boolean) context.get("removeAll");
        Boolean autoLoad = (Boolean) context.get("autoLoad");
        String productStoreId = (String)context.get("productStoreId");

        if (removeAll == null) removeAll = Boolean.FALSE;
        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        String tempDataFile = null;
        File baseDataDir = null;
        File baseFilePath = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) 
        {
        	baseFilePath = new File(xmlDataFilePath);
            try 
            {
                URL xlsDataFileUrl = UtilURL.fromFilename(xmlDataFilePath);
                InputStream ins = xlsDataFileUrl.openStream();

                if (ins != null && (xmlDataFilePath.toUpperCase().endsWith("XML"))) 
                {
                    baseDataDir = new File(xmlDataDirPath);
                    if (baseDataDir.isDirectory() && baseDataDir.canWrite()) {

                        // ############################################
                        // move the existing xml files in dump directory
                        // ############################################
                        File dumpXmlDir = null;
                        File[] fileArray = baseDataDir.listFiles();
                        for (File file: fileArray) 
                        {
                            try 
                            {
                                if (file.getName().toUpperCase().endsWith("XML")) 
                                {
                                    if (dumpXmlDir == null) 
                                    {
                                        dumpXmlDir = new File(baseDataDir, "dumpxml_"+UtilDateTime.nowDateString());
                                    }
                                    FileUtils.copyFileToDirectory(file, dumpXmlDir);
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
                        // ######################################
                        //save the temp xml data file on server 
                        // ######################################
                        try 
                        {
                        	tempDataFile = UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath);
                            inputWorkbook = new File(baseDataDir,  tempDataFile);
                            if (inputWorkbook.createNewFile()) 
                            {
                                Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
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
                    else {
                        messages.add("xml data dir path not found or can't be write");
                    }
                }
                else 
                {
                    messages.add(" path specified for XML file is wrong , doing nothing.");
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
            messages.add("No path specified for XML file or xml data direcotry, doing nothing.");
        }

        // ######################################
        //read the temp xls file and generate xml 
        // ######################################
        try 
        {
        if (inputWorkbook != null && baseDataDir  != null) 
        {
        	try 
        	{
        		JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
            	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            	JAXBElement<BigFishProductFeedType> bfProductFeedType = (JAXBElement<BigFishProductFeedType>)unmarshaller.unmarshal(inputWorkbook);
            	
            	if(UtilValidate.isNotEmpty(productStoreId))
            	{
            		try
            		{
            			GenericValue productStore = _delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
                		if(UtilValidate.isNotEmpty(productStore))
                		{
                			localeString = productStore.getString("defaultLocaleString");
                		}
            		}
            		catch(GenericEntityException gee)
            		{
            			Debug.log("No Product Store Found For ProductStoreId "+productStoreId, gee.toString());
            		}
            	}
            	
            	List<ProductType> products = FastList.newInstance();
            	List<CategoryType> productCategories = FastList.newInstance();
            	List<AssociationType> productAssociations = FastList.newInstance();
            	List<FeatureSwatchType> productFeatureSwatches = FastList.newInstance();
            	List<ManufacturerType> productManufacturers = FastList.newInstance();
            	
            	ProductsType productsType = bfProductFeedType.getValue().getProducts();
            	if(UtilValidate.isNotEmpty(productsType))
            	{
            	    products = productsType.getProduct();
            	}
            	
            	ProductCategoryType productCategoryType = bfProductFeedType.getValue().getProductCategory();
            	if(UtilValidate.isNotEmpty(productCategoryType))
            	{
            	    productCategories = productCategoryType.getCategory();
            	}
            	
            	ProductAssociationType productAssociationType = bfProductFeedType.getValue().getProductAssociation();
            	if(UtilValidate.isNotEmpty(productAssociationType))
            	{
            	    productAssociations = productAssociationType.getAssociation();
            	}
            	
            	ProductFeatureSwatchType productFeatureSwatchType = bfProductFeedType.getValue().getProductFeatureSwatch();
            	if(UtilValidate.isNotEmpty(productFeatureSwatchType))
            	{
            	    productFeatureSwatches = productFeatureSwatchType.getFeature();
            	}
            	
            	ProductManufacturerType productManufacturerType = bfProductFeedType.getValue().getProductManufacturer();
            	if(UtilValidate.isNotEmpty(productManufacturerType))
            	{
            	    productManufacturers = productManufacturerType.getManufacturer();
            	}
            	
            	if(productCategories.size() > 0) 
            	{
            		List dataRows = buildProductCategoryXMLDataRows(productCategories);
            		buildProductCategory(dataRows, xmlDataDirPath,loadImagesDirPath, imageUrl);
            	}
            	if(products.size() > 0)
            	{
            	    List dataRows = buildProductXMLDataRows(products);
            	    buildProduct(dataRows, xmlDataDirPath);
                	buildProductVariant(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl, removeAll);
                	buildProductSelectableFeatures(dataRows, xmlDataDirPath);
                	buildProductGoodIdentification(dataRows, xmlDataDirPath);
                	buildProductCategoryFeatures(dataRows, xmlDataDirPath, removeAll);
                    buildProductDistinguishingFeatures(dataRows, xmlDataDirPath);
                    buildProductContent(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                    buildProductVariantContent(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                    buildProductAttribute(dataRows, xmlDataDirPath);
            	}
            	if(productAssociations.size() > 0)
            	{
            		List dataRows = buildProductAssociationXMLDataRows(productAssociations);
            		buildProductAssoc(dataRows, xmlDataDirPath);
            	}
            	if(productFeatureSwatches.size() > 0)
            	{
            		List dataRows = buildProductFeatureSwatchXMLDataRows(productFeatureSwatches);
            		buildProductFeatureImage(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
            	}
            	if(productManufacturers.size() > 0)
            	{
            		List dataRows = buildProductManufacturerXMLDataRows(productManufacturers);
            		buildManufacturer(dataRows,xmlDataDirPath,loadImagesDirPath,imageUrl,productStoreId);
            	}
            	
        	} 
        	catch (Exception e) 
        	{
        		Debug.logError(e, module);
			}
        	finally 
        	{
                try {
                    if (fOutProduct != null)
                    {
                    	fOutProduct.close();
                    }
                } catch (IOException ioe)
                {
                    Debug.logError(ioe, module);
                }
            }
        }
        
     // ############################################
        // call the service for remove entity data 
        // if removeAll and autoLoad parameter are true 
        // ############################################
        if (removeAll)
        {
            Map importRemoveEntityDataParams = UtilMisc.toMap();
            try {
            
                Map result = dispatcher.runSync("importRemoveEntityData", importRemoveEntityDataParams);
            
                List<String> serviceMsg = (List)result.get("messages");
                for (String msg: serviceMsg)
                {
                    messages.add(msg);
                }
            } 
            catch (Exception exc) 
            {
                Debug.logError(exc, module);
                autoLoad = Boolean.FALSE;
            }
        }

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
                	if(!(file.getName().equals(tempDataFile)) && (!file.getName().equals(baseFilePath.getName()))){
                		FileUtils.copyFileToDirectory(file, doneXmlDir);
                        file.delete();
                	}
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
	            Map entityImportDirParams = UtilMisc.toMap("path", doneXmlDir.getPath(), 
	                                                     "userLogin", context.get("userLogin"));
	             Map result = dispatcher.runSync("entityImportDir", entityImportDirParams);
	             if(UtilValidate.isNotEmpty(result.get("responseMessage")) && result.get("responseMessage").equals("error"))
	             {
	                 return ServiceUtil.returnError(result.get("errorMessage").toString());
	             }
	             else
	             {
		             List<String> serviceMsg = (List)result.get("messages");
		             for (String msg: serviceMsg)
		             {
		                 messages.add(msg);
		             }
	             }
	        }
        } 
        catch (Exception exc) 
        {
            Debug.logError(exc, module);
        }
        finally
        {
        	inputWorkbook.delete();
        }
        	
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;  

    }
      
    public static List buildProductCategoryXMLDataRows(List<CategoryType> productCategories) 
    {
		List dataRows = FastList.newInstance();

		try {
			
            for (int rowCount = 0 ; rowCount < productCategories.size() ; rowCount++) {
            	CategoryType productCategory = (CategoryType) productCategories.get(rowCount);
            
            	Map mRows = FastMap.newInstance();
                
                mRows.put("productCategoryId",productCategory.getCategoryId());
                mRows.put("parentCategoryId",productCategory.getParentCategoryId());
                mRows.put("categoryName",productCategory.getCategoryName());
                mRows.put("description",productCategory.getDescription());
                mRows.put("longDescription",productCategory.getLongDescription());
                mRows.put("plpText",productCategory.getAdditionalPlpText());
                mRows.put("pdpText",productCategory.getAdditionalPdpText());
                mRows.put("fromDate",productCategory.getFromDate());
                mRows.put("thruDate",productCategory.getThruDate());
                
                PlpImageType plpImage = productCategory.getPlpImage();
                if(UtilValidate.isNotEmpty(plpImage))
                {
                    mRows.put("plpImageName",plpImage.getUrl());
                }
                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    public static List buildProductAssociationXMLDataRows(List<AssociationType> productAssociations) {
		List dataRows = FastList.newInstance();

		try {
			
            for (int rowCount = 0 ; rowCount < productAssociations.size() ; rowCount++) {
            	AssociationType productAssociation = (AssociationType)productAssociations.get(rowCount);
            	Map mRows = FastMap.newInstance();
                
                mRows.put("productId",productAssociation.getMasterProductId());
                mRows.put("productIdTo",productAssociation.getMasterProductIdTo());
                mRows.put("productAssocType",productAssociation.getProductAssocType());
                mRows.put("fromDate",productAssociation.getFromDate());
                mRows.put("thruDate",productAssociation.getThruDate());

                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    public static List buildProductFeatureSwatchXMLDataRows(List<FeatureSwatchType> productFeatureSwatches) {
		List dataRows = FastList.newInstance();

		try {
			
            for (int rowCount = 0 ; rowCount < productFeatureSwatches.size() ; rowCount++) 
            {
            	FeatureSwatchType productFeatureSwatch = (FeatureSwatchType)productFeatureSwatches.get(rowCount);
            	Map mRows = FastMap.newInstance();
                String featureKey = productFeatureSwatch.getFeatureId();
                if(UtilValidate.isNotEmpty(featureKey))
                {
                	String featureValue = productFeatureSwatch.getValue();
                    String featureId = featureKey + ":" + featureValue;
                    mRows.put("featureId",featureId);
                    
                    PlpSwatchType plpSwatch = productFeatureSwatch.getPlpSwatch();
                    if(UtilValidate.isNotEmpty(plpSwatch))
                    {
                    	mRows.put("plpSwatchImage",plpSwatch.getUrl());	
                    }
                    
                    PdpSwatchType pdpSwatch = productFeatureSwatch.getPdpSwatch();
                    if(UtilValidate.isNotEmpty(pdpSwatch))
                    {
                    	mRows.put("pdpSwatchImage",pdpSwatch.getUrl());
                    }
                    
                    mRows = formatProductXLSData(mRows);
                    dataRows.add(mRows);
                }
             }
    	}
      	catch (Exception e) 
      	{
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    public static List buildProductManufacturerXMLDataRows(List<ManufacturerType> productManufacturers) {
		List dataRows = FastList.newInstance();

		try {
			
            for (int rowCount = 0 ; rowCount < productManufacturers.size() ; rowCount++) {
            	ManufacturerType productManufacturer = (ManufacturerType)productManufacturers.get(rowCount);
            	Map mRows = FastMap.newInstance();
                
                mRows.put("partyId",productManufacturer.getManufacturerId());
                mRows.put("manufacturerName",productManufacturer.getManufacturerName());
                mRows.put("shortDescription",productManufacturer.getDescription());
                mRows.put("longDescription",productManufacturer.getLongDescription());
                
                ManufacturerAddressType manufacturerAddress = productManufacturer.getAddress();
                if(UtilValidate.isNotEmpty(manufacturerAddress)) 
                {
                	mRows.put("address1",manufacturerAddress.getAddress1());
                    mRows.put("city",manufacturerAddress.getCityTown());
                    mRows.put("state",manufacturerAddress.getStateProvince());
                    mRows.put("zip",manufacturerAddress.getZipPostCode());
                    mRows.put("country",manufacturerAddress.getCountry());
                }
                
                ManufacturerImageType manufacturerImage = productManufacturer.getManufacturerImage();
                if(UtilValidate.isNotEmpty(manufacturerImage)) 
                {
                	mRows.put("manufacturerImage",manufacturerImage.getUrl());
                	mRows.put("manufacturerImageThruDate",manufacturerImage.getThruDate());
                }
                                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    public static List buildProductXMLDataRows(List<ProductType> products) 
    {
		List dataRows = FastList.newInstance();

		try 
		{

			for (int rowCount = 0 ; rowCount < products.size() ; rowCount++) 
			{
            	ProductType product = (ProductType) products.get(rowCount);
            
            	Map mRows = FastMap.newInstance();
                
                mRows.put("masterProductId",product.getMasterProductId());
                mRows.put("productId",product.getProductId());
                mRows.put("internalName",product.getInternalName());
                mRows.put("productName",product.getProductName());
                mRows.put("salesPitch",product.getSalesPitch());
                mRows.put("longDescription",product.getLongDescription());
                mRows.put("specialInstructions",product.getSpecialInstructions());
                mRows.put("deliveryInfo",product.getDeliveryInfo());
                mRows.put("directions",product.getDirections());
                mRows.put("termsConditions",product.getTermsAndConds());
                mRows.put("ingredients",product.getIngredients());
                mRows.put("warnings",product.getWarnings());
                mRows.put("plpLabel",product.getPlpLabel());
                mRows.put("pdpLabel",product.getPdpLabel());
                mRows.put("productHeight",product.getProductHeight());
                mRows.put("productWidth",product.getProductWidth());
                mRows.put("productDepth",product.getProductDepth());
                mRows.put("returnable",product.getReturnable());
                mRows.put("taxable",product.getTaxable());
                mRows.put("chargeShipping",product.getChargeShipping());
                mRows.put("introDate",product.getIntroDate());
                mRows.put("discoDate",product.getDiscoDate());
                mRows.put("manufacturerId",product.getManufacturerId());
                
                ProductPriceType productPrice = product.getProductPrice();
                if(UtilValidate.isNotEmpty(productPrice)) 
                {
                	ListPriceType listPrice = productPrice.getListPrice();
                	if(UtilValidate.isNotEmpty(listPrice)) 
                	{
                        mRows.put("listPrice",listPrice.getPrice());
                        mRows.put("listPriceCurrency",listPrice.getCurrency());
                        mRows.put("listPriceFromDate",listPrice.getFromDate());
                        mRows.put("listPriceThruDate",listPrice.getThruDate());
                	}
                    
                    SalesPriceType salesPrice = productPrice.getSalesPrice();
                    if(UtilValidate.isNotEmpty(salesPrice)) 
                    {
                        mRows.put("defaultPrice",salesPrice.getPrice());
                        mRows.put("defaultPriceCurrency",salesPrice.getCurrency());
                        mRows.put("defaultPriceFromDate",salesPrice.getFromDate());
                        mRows.put("defaultPriceThruDate",salesPrice.getThruDate());
                    }
                }
                
                
                ProductCategoryMemberType productCategory = product.getProductCategoryMember();
                if(UtilValidate.isNotEmpty(productCategory)) 
                {
                	List<CategoryMemberType> categoryList = productCategory.getCategory();
                    
                    StringBuffer categoryId = new StringBuffer("");
                    if(UtilValidate.isNotEmpty(categoryList)) 
                    {
                    	
                    	for(int i = 0; i < categoryList.size(); i++) 
                    	{
                    		CategoryMemberType category = (CategoryMemberType)categoryList.get(i);
                    		if(!category.getCategoryId().equals("")) 
                    		{
                    		    categoryId.append(category.getCategoryId() + ",");
                    		    mRows.put(category.getCategoryId() + "_sequenceNum",category.getSequenceNum());
                    		    mRows.put(category.getCategoryId() + "_fromDate",category.getFromDate());
                    		    mRows.put(category.getCategoryId() + "_thruDate",category.getThruDate());
                    		}
                    	}
                    	if(categoryId.length() > 1) 
                    	{
                    	    categoryId.setLength(categoryId.length()-1);
                    	}
                    }
                    mRows.put("productCategoryId",categoryId.toString());
                    mRows.put("manufacturerId",product.getManufacturerId());
                }
                
                
                ProductSelectableFeatureType selectableFeature = product.getProductSelectableFeature();
                if(UtilValidate.isNotEmpty(selectableFeature)) 
                {
                	List<FeatureType> selectableFeatureList = selectableFeature.getFeature();
                    if(UtilValidate.isNotEmpty(selectableFeatureList)) 
                    {
                    	for(int i = 0; i < selectableFeatureList.size(); i++) 
                    	{
                    		String featureId = new String("");
                    		FeatureType feature = (FeatureType)selectableFeatureList.get(i);
                    		if(UtilValidate.isNotEmpty(feature.getFeatureId())) 
                    		{
                    		    StringBuffer featureValue = new StringBuffer("");
                    		    List featureValues = feature.getValue();
                    		    if(UtilValidate.isNotEmpty(featureValues)) 
                    		    {
                            	
                            	    for(int value = 0; value < featureValues.size(); value++) 
                            	    {
                            		    if(!featureValues.get(value).equals("")) 
                            		    {
                            		        featureValue.append(featureValues.get(value) + ",");
                            		    }
                            	    }
                            	    if(featureValue.length() > 1) 
                            	    {
                            	        featureValue.setLength(featureValue.length()-1);
                            	    }
                                }
                    		    if(featureValue.length() > 0) 
                    		    {
                    		        featureId = feature.getFeatureId() + ":" + featureValue.toString();
                    		        mRows.put(feature.getFeatureId() + "_sequenceNum",feature.getSequenceNum());
                    		        mRows.put(feature.getFeatureId() + "_fromDate",feature.getFromDate());
                        		    mRows.put(feature.getFeatureId() + "_thruDate",feature.getThruDate());
                        		    mRows.put(feature.getFeatureId() + "_description",feature.getDescription());
                    		    }
                    		}
                    		mRows.put("selectabeFeature_"+(i+1),featureId);
                    	}
                    	mRows.put("totSelectableFeatures",new Integer(selectableFeatureList.size()).toString());
                    }
                }
                else
                {
                	mRows.put("totSelectableFeatures",new Integer(0).toString());
                }
                
                
                ProductDescriptiveFeatureType descriptiveFeature = product.getProductDescriptiveFeature();
                if(UtilValidate.isNotEmpty(descriptiveFeature)) 
                {
                	List<FeatureType> descriptiveFeatureList = descriptiveFeature.getFeature();
                    if(UtilValidate.isNotEmpty(descriptiveFeatureList)) 
                    {
                    	for(int i = 0; i < descriptiveFeatureList.size(); i++) 
                    	{
                    		String featureId = new String("");
                    		FeatureType feature = (FeatureType)descriptiveFeatureList.get(i);
                    		if(UtilValidate.isNotEmpty(feature.getFeatureId())) 
                    		{
                    		    StringBuffer featureValue = new StringBuffer("");
                    		    List featureValues = feature.getValue();
                    		    if(UtilValidate.isNotEmpty(featureValues)) 
                    		    {
                            	
                            	    for(int value = 0; value < featureValues.size(); value++) 
                            	    {
                            		    if(!featureValues.get(value).equals("")) 
                            		    {
                            		        featureValue.append(featureValues.get(value) + ",");
                            		    }
                            	    }
                            	    if(featureValue.length() > 1) 
                            	    {
                            	        featureValue.setLength(featureValue.length()-1);
                            	    }
                                }
                    		    if(featureValue.length() > 0) 
                    		    {
                    		        featureId = feature.getFeatureId() + ":" + featureValue.toString();
                    		        mRows.put(feature.getFeatureId() + "_sequenceNum",feature.getSequenceNum());
                    		        mRows.put(feature.getFeatureId() + "_fromDate",feature.getFromDate());
                        		    mRows.put(feature.getFeatureId() + "_thruDate",feature.getThruDate());
                        		    mRows.put(feature.getFeatureId() + "_description",feature.getDescription());
                    		    }
                    		}
                    		mRows.put("descriptiveFeature_"+(i+1),featureId);
                    	}
                    	mRows.put("totDescriptiveFeatures",new Integer(descriptiveFeatureList.size()).toString());
                    }
                }
                else
                {
                	mRows.put("totDescriptiveFeatures",new Integer(0).toString());
                }
                
                ProductImageType productImage = product.getProductImage();
                if(UtilValidate.isNotEmpty(productImage)) 
                {
                	PlpSwatchType plpSwatch = productImage.getPlpSwatch();
                	if(UtilValidate.isNotEmpty(plpSwatch)) 
                	{
                		mRows.put("plpSwatchImage",plpSwatch.getUrl());
                		mRows.put("plpSwatchImageThruDate",plpSwatch.getThruDate());
                	}
                    
                    PdpSwatchType pdpSwatch = productImage.getPdpSwatch();
                    if(UtilValidate.isNotEmpty(pdpSwatch)) 
                    {
                        mRows.put("pdpSwatchImage",pdpSwatch.getUrl());
                        mRows.put("pdpSwatchImageThruDate",pdpSwatch.getThruDate());
                    }
                    
                    PlpSmallImageType plpSmallImage = productImage.getPlpSmallImage();
                    if(UtilValidate.isNotEmpty(plpSmallImage)) 
                    {
                    	mRows.put("smallImage",plpSmallImage.getUrl());
                    	mRows.put("smallImageThruDate",plpSmallImage.getThruDate());
                    }
                    
                    PlpSmallAltImageType plpSmallAltImage = productImage.getPlpSmallAltImage();
                    if(UtilValidate.isNotEmpty(plpSmallAltImage)) 
                    {
                    	mRows.put("smallImageAlt",plpSmallAltImage.getUrl());
                    	mRows.put("smallImageAltThruDate",plpSmallAltImage.getThruDate());
                    }
                    
                    PdpThumbnailImageType pdpThumbnailImage = productImage.getPdpThumbnailImage();
                    if(UtilValidate.isNotEmpty(pdpThumbnailImage)) 
                    {
                    	mRows.put("thumbImage",pdpThumbnailImage.getUrl());
                    	mRows.put("thumbImageThruDate",pdpThumbnailImage.getThruDate());
                    }
                    
                    PdpLargeImageType plpLargeImage = productImage.getPdpLargeImage();
                    if(UtilValidate.isNotEmpty(plpLargeImage)) 
                    {
                    	mRows.put("largeImage",plpLargeImage.getUrl());
                    	mRows.put("largeImageThruDate",plpLargeImage.getThruDate());
                    }
                    
                    PdpDetailImageType pdpDetailImage = productImage.getPdpDetailImage();
                    if(UtilValidate.isNotEmpty(pdpDetailImage)) 
                    {
                    	mRows.put("detailImage",pdpDetailImage.getUrl());
                    	mRows.put("detailImageThruDate",pdpDetailImage.getThruDate());
                    }
                    
                    PdpVideoType pdpVideo = productImage.getPdpVideoImage();
                    if(UtilValidate.isNotEmpty(pdpVideo)) 
                    {
                    	mRows.put("pdpVideoUrl",pdpVideo.getUrl());
                    	mRows.put("pdpVideoUrlThruDate",pdpVideo.getThruDate());
                    }
                    
                    PdpVideo360Type pdpVideo360 = productImage.getPdpVideo360Image();
                    if(UtilValidate.isNotEmpty(pdpVideo360)) 
                    {
                    	mRows.put("pdpVideo360Url",pdpVideo360.getUrl());
                    	mRows.put("pdpVideo360UrlThruDate",pdpVideo360.getThruDate());
                    }
                    
                    PdpAlternateImageType pdpAlternateImage = productImage.getPdpAlternateImage();
                    if(UtilValidate.isNotEmpty(pdpAlternateImage)) 
                    {
                    	List pdpAdditionalImages = pdpAlternateImage.getPdpAdditionalImage();
                        if(UtilValidate.isNotEmpty(pdpAdditionalImages)) 
                        { 
                        	int totPdpAdditionalThumbImage = 0;
                        	int totPdpAdditionalLargeImage = 0;
                        	int totPdpAdditionalDetailImage = 0;
                        	for(int i = 0; i < pdpAdditionalImages.size(); i++) 
                        	{
                        		PdpAdditionalImageType pdpAdditionalImage = (PdpAdditionalImageType) pdpAdditionalImages.get(i);
                        	    
                        		PdpAdditionalThumbImageType pdpAdditionalThumbImage = pdpAdditionalImage.getPdpAdditionalThumbImage();
                        		if(UtilValidate.isNotEmpty(pdpAdditionalThumbImage)) 
                        		{
                        			mRows.put("addImage"+(i+1),pdpAdditionalThumbImage.getUrl());
                        			mRows.put("addImage"+(i+1)+"ThruDate",pdpAdditionalThumbImage.getThruDate());
                        			totPdpAdditionalThumbImage = totPdpAdditionalThumbImage + 1;
                        		}
                        	    
                        	    PdpAdditionalLargeImageType pdpAdditionalLargeImage = pdpAdditionalImage.getPdpAdditionalLargeImage();
                        	    if(UtilValidate.isNotEmpty(pdpAdditionalLargeImage)) 
                        	    {
                        	    	mRows.put("xtraLargeImage"+(i+1),pdpAdditionalLargeImage.getUrl());
                        	    	mRows.put("xtraLargeImage"+(i+1)+"ThruDate",pdpAdditionalLargeImage.getThruDate());
                        	    	totPdpAdditionalLargeImage = totPdpAdditionalLargeImage + 1;
                        	    }
                        	    
                        	    PdpAdditionalDetailImageType pdpAdditionalDetailImage = pdpAdditionalImage.getPdpAdditionalDetailImage();
                        	    if(UtilValidate.isNotEmpty(pdpAdditionalDetailImage)) 
                        	    {
                        	    	mRows.put("xtraDetailImage"+(i+1),pdpAdditionalDetailImage.getUrl());
                        	    	mRows.put("xtraDetailImage"+(i+1)+"ThruDate",pdpAdditionalDetailImage.getThruDate());
                        	    	totPdpAdditionalDetailImage = totPdpAdditionalDetailImage + 1;
                        	    }
                        	}
                        	mRows.put("totPdpAdditionalThumbImage",new Integer(totPdpAdditionalThumbImage).toString());
                        	mRows.put("totPdpAdditionalLargeImage",new Integer(totPdpAdditionalLargeImage).toString());
                        	mRows.put("totPdpAdditionalDetailImage",new Integer(totPdpAdditionalDetailImage).toString());
                        }
                    }
                    
                }
                
                
                GoodIdentificationType goodIdentification = product.getProductGoodIdentification();
                if(UtilValidate.isNotEmpty(goodIdentification)) 
                {
                	mRows.put("goodIdentificationSkuId",goodIdentification.getSku());
                    mRows.put("goodIdentificationGoogleId",goodIdentification.getGoogleId());
                    mRows.put("goodIdentificationIsbnId",goodIdentification.getIsbn());
                    mRows.put("goodIdentificationManufacturerId",goodIdentification.getManuId());
                }
                
                
                ProductInventoryType productInventory = product.getProductInventory();
                if(UtilValidate.isNotEmpty(productInventory)) 
                {
                	mRows.put("bfInventoryTot",productInventory.getBigfishInventoryTotal());
                    mRows.put("bfInventoryWhs",productInventory.getBigfishInventoryWarehouse());
                }
                
                ProductAttributeType productAttribute = product.getProductAttribute();
                if(UtilValidate.isNotEmpty(productAttribute)) 
                {
                	mRows.put("multiVariant",productAttribute.getPdpSelectMultiVariant());
                	mRows.put("giftMessage",productAttribute.getPdpCheckoutGiftMessage());
                	mRows.put("pdpQtyMin",productAttribute.getPdpQtyMin());
                	mRows.put("pdpQtyMax",productAttribute.getPdpQtyMax());
                	mRows.put("pdpQtyDefault",productAttribute.getPdpQtyDefault());
                }
                
                mRows.put("weight",product.getProductWeight());
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) 
      	{
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    
    public static List buildOrderStatusXMLDataRows(List<OrderStatusType> orderList) 
    {
		List dataRows = FastList.newInstance();
		try 
		{
            for (int rowCount = 0 ; rowCount < orderList.size() ; rowCount++) 
            {
            	OrderStatusType order = (OrderStatusType) orderList.get(rowCount);
            
            	Map mRows = FastMap.newInstance();
                
                mRows.put("orderId",order.getOrderId());
                mRows.put("productStoreId",order.getProductStoreId());
                mRows.put("orderStatus",order.getOrderStatus());
                mRows.put("orderShipDate",order.getOrderShipDate());
                mRows.put("orderShipCarrier",order.getOrderShipCarrier());
                mRows.put("orderShipMethod",order.getOrderShipMethod());
                mRows.put("orderTrackingNumber",order.getOrderTrackingNumber());
                mRows.put("orderNote",order.getOrderNote());
                
                List orderItems = order.getOrderItem();
                if(UtilValidate.isNotEmpty(orderItems)) 
                {
                	for(int i = 0; i < orderItems.size(); i++)
                	{
                		OrderItemType orderItem = (OrderItemType) orderItems.get(i);
                		mRows.put("productId_" + (i + 1),orderItem.getProductId());
                		mRows.put("shipGroupSeqId_" + (i + 1),orderItem.getShipGroupSequenceId());
                		mRows.put("orderItemSequenceId_" + (i + 1),orderItem.getSequenceId());
                        mRows.put("orderItemStatus_" + (i + 1),orderItem.getOrderItemStatus());
                        mRows.put("orderItemShipDate_" + (i + 1),orderItem.getOrderItemShipDate());
                        mRows.put("orderItemCarrier_" + (i + 1),orderItem.getOrderItemCarrier());
                        mRows.put("orderItemShipMethod_" + (i + 1),orderItem.getOrderItemShipMethod());
                        mRows.put("orderItemTrackingNumber_" + (i + 1),orderItem.getOrderItemTrackingNumber());
                	}
                }
                mRows.put("totalOrderItems",new Integer(orderItems.size()).toString());
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) 
      	{
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    

    private static void buildOrderItemShipment(BufferedWriter bwOutFile, String orderId)
    {
         StringBuilder  rowString = new StringBuilder();
		 List<GenericValue> orderItems = FastList.newInstance();
 		 try
 		 {
		     orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
 		 }
 		 catch (GenericEntityException e)
 		 {
		     e.printStackTrace();
	     }
 		 if(UtilValidate.isNotEmpty(orderItems)) 
 		 {
 			 for(GenericValue orderItem : orderItems) 
 			 {
 				 try
 				 {
			         List<GenericValue> orderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", (String)orderItem.get("orderItemSeqId")), UtilMisc.toList("+orderItemSeqId"));
					 if(UtilValidate.isNotEmpty(orderItemShipGroupAssocList)) 
					 {
			            for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) 
			            {
			                //buildOrderItemShipment(bwOutFile, orderItem, orderItemShipGroupAssoc.getString("shipGroupSeqId"));
			            }
					 }
	 	    	 }
	 	      	 catch (Exception e) 
	 	      	 {
	 	      		e.printStackTrace();
	 	   	     }
 			 }
 		 }
    }

    private static void buildOrderItemShipment(BufferedWriter bwOutFile, GenericValue orderItemShip, String shipGroupSeqid, String orderId)
    {
         StringBuilder  rowString = new StringBuilder();
 		 try
			 {
				 //Create Shipment
				 //Create Shipment Package
				 
				 //Create Shipment Item
				 //Create Order Shipment
				 //Create Shipment Package Content
				 //Create Item Issuance
				 List<GenericValue> orderItemShipGroupAssocList = FastList.newInstance();
				 if(UtilValidate.isEmpty(orderItemShip))
				 {
					 orderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId), UtilMisc.toList("+orderItemSeqId"));
					 shipGroupSeqid = EntityUtil.getFirst(orderItemShipGroupAssocList).getString("shipGroupSeqId");
				 }
				 else
				 {
					 orderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", (String)orderItemShip.get("orderItemSeqId")), UtilMisc.toList("+orderItemSeqId"));
				 }
				 
				 List<GenericValue> shipments = _delegator.findByAnd("Shipment", UtilMisc.toMap("primaryOrderId", orderId, "primaryShipGroupSeqId", shipGroupSeqid));
				 String shipmentId = "";
				 if(UtilValidate.isNotEmpty(shipments))
				 {
					 shipmentId = EntityUtil.getFirst(shipments).getString("shipmentId");
				 }
				 else
				 {
					 shipmentId = _delegator.getNextSeqId("Shipment"); 
				 }
				 
                 rowString.setLength(0);
       		     rowString.append("<" + "Shipment" + " ");
	             rowString.append("shipmentId" + "=\"" + shipmentId + "\" ");
	             rowString.append("shipmentTypeId" + "=\"" + "SALES_SHIPMENT" + "\" ");
	             rowString.append("statusId" + "=\"" + "SHIPMENT_SHIPPED" + "\" ");
	             rowString.append("primaryOrderId" + "=\"" + orderId + "\" ");
	             rowString.append("primaryShipGroupSeqId" + "=\"" + shipGroupSeqid + "\" ");
	             rowString.append("/>");
	             bwOutFile.write(rowString.toString());
                 bwOutFile.newLine();
                 
                 List<GenericValue> shipmentPackages = _delegator.findByAnd("ShipmentPackage", UtilMisc.toMap("shipmentId", shipmentId));
				 
                 String shipmentPackageSeqId = "";
				 if(UtilValidate.isNotEmpty(shipmentPackages))
				 {
					 shipmentPackageSeqId = EntityUtil.getFirst(shipmentPackages).getString("shipmentPackageSeqId");
				 }
				 else
				 {
					 shipmentPackageSeqId = _delegator.getNextSeqId("ShipmentPackage"); 
				 }
				 
				 rowString.setLength(0);
       		     rowString.append("<" + "ShipmentPackage" + " ");
	             rowString.append("shipmentId" + "=\"" + shipmentId + "\" ");
	             rowString.append("shipmentPackageSeqId" + "=\"" + shipmentPackageSeqId + "\" ");
	             rowString.append("dateCreated" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
	             rowString.append("/>");
	             bwOutFile.write(rowString.toString());
                 bwOutFile.newLine();
				 
				 if(UtilValidate.isNotEmpty(orderItemShipGroupAssocList)) 
				 {
					 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList)
					 {
						 GenericValue orderItem = orderItemShipGroupAssoc.getRelatedOne("OrderItem");
						 
						 rowString.setLength(0);
		       		     rowString.append("<" + "ShipmentItem" + " ");
			             rowString.append("shipmentId" + "=\"" + shipmentId + "\" ");
			             rowString.append("shipmentItemSeqId" + "=\"" + orderItem.getString("orderItemSeqId") + "\" ");
			             rowString.append("productId" + "=\"" + orderItem.getString("productId") + "\" ");
			             rowString.append("quantity" + "=\"" + orderItem.getString("quantity") + "\" ");
			             rowString.append("/>");
			             bwOutFile.write(rowString.toString());
		                 bwOutFile.newLine();
						 
		                 String itemIssuanceId=_delegator.getNextSeqId("ItemIssuance");
		                 rowString.setLength(0);
		       		     rowString.append("<" + "ItemIssuance" + " ");
			             rowString.append("itemIssuanceId" + "=\"" + itemIssuanceId + "\" ");
			             rowString.append("orderId" + "=\"" + orderItem.getString("orderId") + "\" ");
			             rowString.append("orderItemSeqId" + "=\"" + orderItem.getString("orderItemSeqId") + "\" ");
			             rowString.append("shipGroupSeqId" + "=\"" + shipGroupSeqid + "\" ");
			             rowString.append("shipmentId" + "=\"" + shipmentId + "\" ");
			             rowString.append("shipmentItemSeqId" + "=\"" + orderItem.getString("orderItemSeqId") + "\" ");
			             rowString.append("quantity" + "=\"" + orderItem.getString("quantity") + "\" ");
			             rowString.append("/>");
			             bwOutFile.write(rowString.toString());
		                 bwOutFile.newLine();
		                 
						 List<GenericValue> orderShipmentList = _delegator.findByAnd("OrderShipment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemShipGroupAssoc.getString("orderItemSeqId"), "shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"), "shipmentId", shipmentId), UtilMisc.toList("+orderItemSeqId")); 
		       	         if(UtilValidate.isEmpty(orderShipmentList)) 
		     		     {
			                 rowString.setLength(0);
			       		     rowString.append("<" + "OrderShipment" + " ");
				             rowString.append("orderId" + "=\"" + orderItem.getString("orderId") + "\" ");
				             rowString.append("orderItemSeqId" + "=\"" + orderItem.getString("orderItemSeqId") + "\" ");
				             rowString.append("shipGroupSeqId" + "=\"" + shipGroupSeqid + "\" ");
				             rowString.append("shipmentId" + "=\"" + shipmentId + "\" ");
				             rowString.append("shipmentItemSeqId" + "=\"" + orderItem.getString("orderItemSeqId") + "\" ");
				             rowString.append("quantity" + "=\"" + orderItem.getString("quantity") + "\" ");
				             rowString.append("/>");
				             bwOutFile.write(rowString.toString());
			                 bwOutFile.newLine();
		     		     }
		       	         
		       	         rowString.setLength(0);
		       		     rowString.append("<" + "ShipmentPackageContent" + " ");
			             rowString.append("shipmentId" + "=\"" + shipmentId + "\" ");
			             rowString.append("shipmentPackageSeqId" + "=\"" + shipmentPackageSeqId + "\" ");
			             rowString.append("shipmentItemSeqId" + "=\"" + orderItem.getString("orderItemSeqId") + "\" ");
			             rowString.append("quantity" + "=\"" + orderItem.getString("quantity") + "\" ");
			             rowString.append("subProductId" + "=\"" + orderItem.getString("productId") + "\" ");
			             rowString.append("/>");
			             bwOutFile.write(rowString.toString());
		                 bwOutFile.newLine();
					 }
				 }
 	    	 }
 	      	 catch (Exception e)
 	      	 {
 	      		e.printStackTrace();
 	   	     }
 		 
    }

    
    private static String getOrderStatus(String OrderId, Map mRow) 
    {
    	Map xmlOrderItems = FastMap.newInstance();
    	for(int orderItemNo = 0; orderItemNo < Integer.parseInt((String)mRow.get("totalOrderItems")); orderItemNo++)
    	{
    		if(UtilValidate.isEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1)))) 
    		{
    		    try 
    		    {
					List<GenericValue> orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", OrderId, "productId", mRow.get("productId_" + (orderItemNo + 1))));
					if(UtilValidate.isNotEmpty(orderItems)) 
					{
						for(GenericValue orderItem : orderItems) 
						{
							xmlOrderItems.put((String)orderItem.getString("orderItemSeqId"), "ITEM_"+(String)mRow.get("orderItemStatus_" + (orderItemNo + 1)));
						}
					}
				} 
    		    catch (GenericEntityException e) 
    		    {
					e.printStackTrace();
				}	
    		} 
    		else 
    		{
    			xmlOrderItems.put((String)mRow.get("orderItemSequenceId_" + (orderItemNo + 1)), "ITEM_"+(String)mRow.get("orderItemStatus_" + (orderItemNo + 1)));
    		}
    	}
    	List<GenericValue> orderItems = null;
		try 
		{
			orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", (String)mRow.get("orderId")));
		} 
		catch (GenericEntityException e) 
		{
			e.printStackTrace();
		}
		List totalOrderItemStatus = FastList.newInstance();
   	    if(UtilValidate.isNotEmpty(orderItems)) 
   	    {
   		     for(GenericValue orderItem : orderItems) 
   		     {
   			     if(UtilValidate.isNotEmpty(xmlOrderItems.get(orderItem.getString("orderItemSeqId"))))
   			     {
   			    	totalOrderItemStatus.add(xmlOrderItems.get(orderItem.getString("orderItemSeqId")));
   			     }
   			     else
   			     {
   			    	totalOrderItemStatus.add(orderItem.getString("statusId"));
   			     }
   		     }  
   	    }
   	    if(totalOrderItemStatus.contains("ITEM_APPROVED")) 
   	    {
   	    	return "ORDER_APPROVED";
   	    }
   	    else if(totalOrderItemStatus.contains("ITEM_COMPLETED")) 
   	    {
   	    	return "ORDER_COMPLETED";
   	    }
   	    else if(new HashSet(totalOrderItemStatus).size() == 1) 
   	    {
   	    	if(totalOrderItemStatus.get(0).equals("ITEM_APPROVED")) 
   	    	{
   	    		return "ORDER_APPROVED";
   	    	}
   	    	if(totalOrderItemStatus.get(0).equals("ITEM_COMPLETED")) 
   	    	{
   	    		return "ORDER_COMPLETED";
   	    	}
   	    	if(totalOrderItemStatus.get(0).equals("ITEM_CANCELLED")) 
   	    	{
   	    		return "ORDER_CANCELLED";
   	    	}
   	    }
    	return "";
    }
    
    public static Map<String, Object> importStoreXML(DispatchContext ctx, Map<String, ?> context) 
    {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String xmlDataFilePath = (String)context.get("xmlDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        File baseDataDir = null;
        String tempDataFile = null;
        File baseFilePath = null;
        
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
            try {
            	baseFilePath = new File(xmlDataFilePath);
                URL xlsDataFileUrl = UtilURL.fromFilename(xmlDataFilePath);
                InputStream ins = xlsDataFileUrl.openStream();

                if (ins != null && (xmlDataFilePath.toUpperCase().endsWith("XML"))) {
                    baseDataDir = new File(xmlDataDirPath);
                    if (baseDataDir.isDirectory() && baseDataDir.canWrite()) {

                        // ############################################
                        // move the existing xml files in dump directory
                        // ############################################
                        File dumpXmlDir = null;
                        File[] fileArray = baseDataDir.listFiles();
                        for (File file: fileArray) {
                            try {
                                if (file.getName().toUpperCase().endsWith("XML")) {
                                    if (dumpXmlDir == null) {
                                        dumpXmlDir = new File(baseDataDir, "dumpxml_"+UtilDateTime.nowDateString());
                                    }
                                    FileUtils.copyFileToDirectory(file, dumpXmlDir);
                                    file.delete();
                                }
                            } catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            } catch (Exception exc) {
                                Debug.logError(exc, module);
                            }
                        }
                        // ######################################
                        //save the temp xls data file on server 
                        // ######################################
                        try {
                        	tempDataFile = UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath);
                            inputWorkbook = new File(baseDataDir,  tempDataFile);
                            if (inputWorkbook.createNewFile()) {
                                Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
                            }
                            } catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            } catch (Exception exc) {
                                Debug.logError(exc, module);
                            }
                    }
                    else {
                        messages.add("xml data dir path not found or can't be write");
                    }
                }
                else {
                    messages.add(" path specified for Excel sheet file is wrong , doing nothing.");
                }

            } catch (IOException ioe) {
                Debug.logError(ioe, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }
        else {
            messages.add("No path specified for Excel sheet file or xml data direcotry, doing nothing.");
        }

        // ######################################
        //read the temp xls file and generate xml 
        // ######################################
        try {
        if (inputWorkbook != null && baseDataDir  != null) {
        	try {
        		JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
            	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            	JAXBElement<BigFishStoreFeedType> bfStoreFeedType = (JAXBElement<BigFishStoreFeedType>)unmarshaller.unmarshal(inputWorkbook);
            	if(UtilValidate.isNotEmpty(bfStoreFeedType)) {
            		List dataRows = buildStoreXMLDataRows(bfStoreFeedType);
                	buildStore(dataRows, xmlDataDirPath);
            	}
            	
        	} catch (Exception e) {
        		Debug.logError(e, module);
			}
        	finally {
                try {
                    if (fOutProduct != null) {
                    	fOutProduct.close();
                    }
                } catch (IOException ioe) {
                    Debug.logError(ioe, module);
                }
            }
        }
        
     

        // ##############################################
        // move the generated xml files in done directory
        // ##############################################
        File doneXmlDir = new File(baseDataDir, Constants.DONE_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
        File[] fileArray = baseDataDir.listFiles();
        for (File file: fileArray) {
            try {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("XML")) {
                	if(!(file.getName().equals(tempDataFile)) && (!file.getName().equals(baseFilePath.getName()))){
                		FileUtils.copyFileToDirectory(file, doneXmlDir);
                        file.delete();
                	}
                }
            } catch (IOException ioe) {
                Debug.logError(ioe, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }
        
        // ######################################################################
        // call service for insert row in database  from generated xml data files 
        // by calling service entityImportDir if autoLoad parameter is true
        // ######################################################################
        if (autoLoad) {
            Map entityImportDirParams = UtilMisc.toMap("path", doneXmlDir.getPath(), 
                                                     "userLogin", context.get("userLogin"));
             try {
                 Map result = dispatcher.runSync("entityImportDir", entityImportDirParams);
             
                 List<String> serviceMsg = (List)result.get("messages");
                 for (String msg: serviceMsg) {
                     messages.add(msg);
                 }
             } catch (Exception exc) {
                 Debug.logError(exc, module);
             }
        }
        } catch (Exception exc) {
            Debug.logError(exc, module);
        }
        finally {
            inputWorkbook.delete();
        } 
        	
            	
                
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;  

    }
    
    public static List buildStoreXMLDataRows(JAXBElement<BigFishStoreFeedType> bfStoreFeedType) {
		List dataRows = FastList.newInstance();

		try {
			List stores = bfStoreFeedType.getValue().getStore();
			
            for (int rowCount = 0 ; rowCount < stores.size() ; rowCount++) {
            	StoreType store = (StoreType) stores.get(rowCount);
            
            	Map mRows = FastMap.newInstance();
                
                mRows.put("productStoreId",store.getProductStoreId());
                mRows.put("storeId",store.getStoreId());
                mRows.put("storeCode",store.getStoreCode());
                mRows.put("storeName",store.getStoreName());
                
                StoreAddressType storesAddress = store.getStoreAddress();
                if(UtilValidate.isNotEmpty(storesAddress)) {
                	mRows.put("country",storesAddress.getCountry());
                    mRows.put("address1",storesAddress.getAddress1());
                    mRows.put("address2",storesAddress.getAddress2());
                    mRows.put("address3",storesAddress.getAddress3());
                    mRows.put("city",storesAddress.getCityTown());
                    mRows.put("state",storesAddress.getStateProvince());
                    mRows.put("zip",storesAddress.getZipPostCode());
                    mRows.put("phone",storesAddress.getStorePhone());
                }
                
                mRows.put("openingHours",store.getOpeningHours());
                mRows.put("storeNotice",store.getStoreNotice());
                mRows.put("storeContentSpot",store.getStoreContentSpot());
                mRows.put("status",store.getStatus());
                mRows.put("geoCodeLat",store.getGeoCodeLat());
                mRows.put("geoCodeLong",store.getGeoCodeLong());
                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    private static void buildStore(List dataRows,String xmlDataDirPath) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        
		try {
			
	        fOutFile = new File(xmlDataDirPath, "000-StoreLocation.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                     StringBuilder  rowString = new StringBuilder();
	            	 Map mRow = (Map)dataRows.get(i);
	            	 String partyId = null;
	            	 if(UtilValidate.isNotEmpty(mRow.get("storeId"))) 
	            	 {
	            		 partyId = (String)mRow.get("storeId");
	            	 } 
	            	 else 
	            	 {
	            		 partyId = _delegator.getNextSeqId("Party");
	            	 }
                     rowString.append("<" + "Party" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("partyTypeId" + "=\"" + "PARTY_GROUP" + "\" ");
                     if(((String)mRow.get("status")).equalsIgnoreCase("open")) 
                     {
                         rowString.append("statusId" + "=\"" + "PARTY_ENABLED" + "\" ");
                     }
                     else if(((String)mRow.get("status")).equalsIgnoreCase("closed")) 
                     {
                    	 rowString.append("statusId" + "=\"" + "PARTY_DISABLED" + "\" ");
                     }
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     rowString.setLength(0);
                     rowString.append("<" + "PartyRole" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("roleTypeId" + "=\"" + "STORE_LOCATION" + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();

                     List<GenericValue> productStoreRoles = _delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId",partyId,"roleTypeId","STORE_LOCATION","productStoreId",mRow.get("productStoreId")),UtilMisc.toList("-fromDate"));
 	                 if(UtilValidate.isNotEmpty(productStoreRoles)) 
 	                 {
 	                	productStoreRoles = EntityUtil.filterByDate(productStoreRoles);
 	                 }
 	                 if(UtilValidate.isEmpty(productStoreRoles))
	                 {
	                     rowString.setLength(0);
	                     rowString.append("<" + "ProductStoreRole" + " ");
	                     rowString.append("partyId" + "=\"" + partyId + "\" ");
	                     rowString.append("roleTypeId" + "=\"" + "STORE_LOCATION" + "\" ");
	                     rowString.append("productStoreId" + "=\"" + mRow.get("productStoreId") + "\" ");
	                     rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
	                     rowString.append("/>");
	                     bwOutFile.write(rowString.toString());
	                     bwOutFile.newLine();
	                 }
                     
                     rowString.setLength(0);
                     rowString.append("<" + "PartyGroup" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("groupName" + "=\"" + (String)mRow.get("storeName") + "\" ");
                     rowString.append("groupNameLocal" + "=\"" + (String)mRow.get("storeCode") + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
         			 String contactMechId=_delegator.getNextSeqId("ContactMech");
                     rowString.setLength(0);
                     rowString.append("<" + "ContactMech" + " ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     rowString.append("contactMechTypeId" + "=\"" + "POSTAL_ADDRESS" + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();

                     rowString.setLength(0);
                     rowString.append("<" + "PartyContactMech" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     rowString.setLength(0);
                     rowString.append("<" + "PartyContactMechPurpose" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     rowString.append("contactMechPurposeTypeId" + "=\"" + "GENERAL_LOCATION" + "\" ");
                     rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     rowString.setLength(0);
                     rowString.append("<" + "PostalAddress" + " ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     if(mRow.get("address1") != null) 
                     {
                         rowString.append("address1" + "=\"" +  (String)mRow.get("address1") + "\" ");
                     }
                     if(mRow.get("address2") != null) 
                     {
                    	 rowString.append("address2" + "=\"" +  (String)mRow.get("address2") + "\" "); 
                     }
                     if(mRow.get("address3") != null) 
                     {
                    	 rowString.append("address3" + "=\"" +  (String)mRow.get("address3") + "\" ");
                     }
                     if(mRow.get("city") != null) 
                     {
                    	 rowString.append("city" + "=\"" +  (String)mRow.get("city") + "\" ");
                     }
                     if(mRow.get("state") != null) 
                     {
                    	 rowString.append("stateProvinceGeoId" + "=\"" +  mRow.get("state") + "\" ");
                     }
                     if(mRow.get("country") != null) 
                     {
                    	 rowString.append("countryGeoId" + "=\"" +  mRow.get("country") + "\" ");
                     }
                     if(mRow.get("zip") != null) 
                     {
                    	 rowString.append("postalCode" + "=\"" +  mRow.get("zip") + "\" ");
                     }
                     
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     contactMechId=_delegator.getNextSeqId("ContactMech");
                     rowString.setLength(0);
                     rowString.append("<" + "ContactMech" + " ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     rowString.append("contactMechTypeId" + "=\"" + "TELECOM_NUMBER" + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();

                     rowString.setLength(0);
                     rowString.append("<" + "PartyContactMech" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     rowString.setLength(0);
                     rowString.append("<" + "PartyContactMechPurpose" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     rowString.append("contactMechPurposeTypeId" + "=\"" + "PRIMARY_PHONE" + "\" ");
                     rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     rowString.setLength(0);
                     rowString.append("<" + "TelecomNumber" + " ");
                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                     rowString.append("contactNumber" + "=\"" +  (String)mRow.get("phone") + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     addPartyStoreContentRow(rowString, mRow, bwOutFile, partyId, "text", "STORE_HOURS", "openingHours");
                     addPartyStoreContentRow(rowString, mRow, bwOutFile, partyId, "text", "STORE_NOTICE", "storeNotice");
                     addPartyStoreContentRow(rowString, mRow, bwOutFile, partyId, "text", "STORE_CONTENT_SPOT", "storeContentSpot");
 	            	
                     if(mRow.get("geoCodeLat") != "" || mRow.get("geoCodeLong") != "") 
                     {
                         String geoPointId = _delegator.getNextSeqId("GeoPoint");
                         rowString.setLength(0);
                         rowString.append("<" + "GeoPoint" + " ");
                         rowString.append("geoPointId" + "=\"" + geoPointId + "\" ");
                         rowString.append("dataSourceId" + "=\"" + "GEOPT_GOOGLE" + "\" ");
                         rowString.append("latitude" + "=\"" + (String)mRow.get("geoCodeLat") + "\" ");
                         rowString.append("longitude" + "=\"" + (String)mRow.get("geoCodeLong") + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                     
                         rowString.setLength(0);
                         rowString.append("<" + "PartyGeoPoint" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("geoPointId" + "=\"" + geoPointId + "\" ");
                         rowString.append("fromDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                     }
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
    
    private static void addPartyStoreContentRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile, String partyId, String contentType,String partyContentType,String colName) {

		String contentId=null;
		String dataResourceId=null;
    	try {
    		
			String contentValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(contentValue) && UtilValidate.isEmpty(contentValue.trim()))
			{
				return;
			}
	        List<GenericValue> lPartyContent = _delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",partyId,"partyContentTypeId",partyContentType),UtilMisc.toList("-fromDate"));
			if (UtilValidate.isNotEmpty(lPartyContent))
			{
				GenericValue partyContent = EntityUtil.getFirst(lPartyContent);
				GenericValue content=partyContent.getRelatedOne("Content");
				contentId=content.getString("contentId");
				dataResourceId=content.getString("dataResourceId");
			}
			else
			{
				contentId=_delegator.getNextSeqId("Content");
				dataResourceId=_delegator.getNextSeqId("DataResource");
				
			}
			contentId=_delegator.getNextSeqId("Content");
			dataResourceId=_delegator.getNextSeqId("DataResource");
    		

			if ("text".equals(contentType))
			{
	            rowString.setLength(0);
	            rowString.append("<" + "DataResource" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("dataResourceTypeId" + "=\"" + "ELECTRONIC_TEXT" + "\" ");
	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
	            rowString.append("dataResourceName" + "=\"" + colName + "\" ");
	            if(UtilValidate.isNotEmpty(localeString))
	            {
	            	rowString.append("localeString" + "=\"" + localeString + "\" ");
	            }
	            rowString.append("mimeTypeId" + "=\"" + "application/octet-stream" + "\" ");
	            rowString.append("objectInfo" + "=\"" + "" + "\" ");
	            rowString.append("isPublic" + "=\"" + "Y" + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();

	            rowString.setLength(0);
	            rowString.append("<" + "ElectronicText" + " ");
	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
	            rowString.append("textData" + "=\"" + contentValue + "\" ");
	            rowString.append("/>");
	            bwOutFile.write(rowString.toString());
	            bwOutFile.newLine();
	            
	            
			}

			rowString.setLength(0);
            rowString.append("<" + "Content" + " ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("contentTypeId" + "=\"" + "DOCUMENT" + "\" ");
            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
            rowString.append("contentName" + "=\"" + colName + "\" ");
            if(UtilValidate.isNotEmpty(localeString))
            {
            	rowString.append("localeString" + "=\"" + localeString + "\" ");
            }
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
            String fromDate=(String)mRow.get("fromDate");
            String sFromDate = "";
            if(UtilValidate.isNotEmpty(fromDate))
            {
            	java.util.Date formattedFromDate=OsafeAdminUtil.validDate(fromDate);
                sFromDate = _sdf.format(formattedFromDate);
            }
            else
            {
            	sFromDate = _sdf.format(UtilDateTime.nowTimestamp());
            }
			
            rowString.setLength(0);
            rowString.append("<" + "PartyContent" + " ");
            rowString.append("partyId" + "=\"" + partyId + "\" ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("partyContentTypeId" + "=\"" + partyContentType + "\" ");
            rowString.append("fromDate" + "=\"" + sFromDate + "\" ");
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
    		
    	}
     	 catch (Exception e) {
	     }

     	 return;
    }
    
    
    public static Map<String, Object> importCustomerXML(DispatchContext ctx, Map<String, ?> context) {LocalDispatcher dispatcher = ctx.getDispatcher();
    _delegator = ctx.getDelegator();
    List<String> messages = FastList.newInstance();

    String xmlDataFilePath = (String)context.get("xmlDataFile");
    String xmlDataDirPath = (String)context.get("xmlDataDir");
    Boolean autoLoad = (Boolean) context.get("autoLoad");
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    if (autoLoad == null) autoLoad = Boolean.FALSE;

    File inputWorkbook = null;
    String tempDataFile = null;
    File baseDataDir = null;
    File baseFilePath = null;
    BufferedWriter fOutProduct=null;
    if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
    	baseFilePath = new File(xmlDataFilePath);
        try {
            URL xlsDataFileUrl = UtilURL.fromFilename(xmlDataFilePath);
            InputStream ins = xlsDataFileUrl.openStream();

            if (ins != null && (xmlDataFilePath.toUpperCase().endsWith("XML"))) {
                baseDataDir = new File(xmlDataDirPath);
                if (baseDataDir.isDirectory() && baseDataDir.canWrite()) {

                    // ############################################
                    // move the existing xml files in dump directory
                    // ############################################
                    File dumpXmlDir = null;
                    File[] fileArray = baseDataDir.listFiles();
                    for (File file: fileArray) {
                        try {
                            if (file.getName().toUpperCase().endsWith("XML")) {
                                if (dumpXmlDir == null) {
                                    dumpXmlDir = new File(baseDataDir, "dumpxml_"+UtilDateTime.nowDateString());
                                }
                                FileUtils.copyFileToDirectory(file, dumpXmlDir);
                                file.delete();
                            }
                        } catch (IOException ioe) {
                            Debug.logError(ioe, module);
                        } catch (Exception exc) {
                            Debug.logError(exc, module);
                        }
                    }
                    // ######################################
                    //save the temp xls data file on server 
                    // ######################################
                    try {
                    	tempDataFile = UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath);
                        inputWorkbook = new File(baseDataDir,  tempDataFile);
                        if (inputWorkbook.createNewFile()) {
                            Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
                        }
                        } catch (IOException ioe) {
                            Debug.logError(ioe, module);
                        } catch (Exception exc) {
                            Debug.logError(exc, module);
                        }
                }
                else {
                    messages.add("xml data dir path not found or can't be write");
                }
            }
            else {
                messages.add(" path specified for Excel sheet file is wrong , doing nothing.");
            }

        } catch (IOException ioe) {
            Debug.logError(ioe, module);
        } catch (Exception exc) {
            Debug.logError(exc, module);
        }
    }
    else {
        messages.add("No path specified for Excel sheet file or xml data direcotry, doing nothing.");
    }

    // ######################################
    //read the temp xls file and generate xml 
    // ######################################
    List dataRows = FastList.newInstance();
    try 
    {
    if (inputWorkbook != null && baseDataDir  != null) 
    {
    	try 
    	{
    		JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
        	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        	JAXBElement<BigFishCustomerFeedType> bfCustomerFeedType = (JAXBElement<BigFishCustomerFeedType>)unmarshaller.unmarshal(inputWorkbook);
        	
        	List<CustomerType> customerList = bfCustomerFeedType.getValue().getCustomer();
        	
        	if(customerList.size() > 0) 
        	{
        		dataRows = buildCustomerXMLDataRows(customerList);
        		buildCustomer(dataRows, xmlDataDirPath, messages);
        	}
        	
        	
    	} 
    	catch (Exception e) 
    	{
    		Debug.logError(e, module);
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
            	if(!(file.getName().equals(tempDataFile)) && (!file.getName().equals(baseFilePath.getName())))
            	{
            		FileUtils.copyFileToDirectory(file, doneXmlDir);
                    file.delete();
            	}
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
             if(UtilValidate.isNotEmpty(result.get("responseMessage")) && result.get("responseMessage").equals("error"))
             {
                 return ServiceUtil.returnError(result.get("errorMessage").toString());
             }
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
    catch (Exception exc) 
    {
        Debug.logError(exc, module);
    }
    finally 
    {
        inputWorkbook.delete();
    } 
            
    Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
    return resp;  
  }
    
    
    public static List buildCustomerXMLDataRows(List<CustomerType> customers) {
		List dataRows = FastList.newInstance();

		try 
		{
            for (int rowCount = 0 ; rowCount < customers.size() ; rowCount++) 
            {
            	CustomerType customer = (CustomerType)customers.get(rowCount);
            	Map mRows = FastMap.newInstance();
                
                mRows.put("productStoreId",customer.getProductStoreId());
                mRows.put("customerId",customer.getCustomerId());
                mRows.put("firstName",customer.getFirstName());
                mRows.put("lastName",customer.getLastName());
                mRows.put("dateRegistered",customer.getDateRegistered());
                mRows.put("emailAddress",customer.getEmailAddress());
                mRows.put("emailOptIn",customer.getEmailOptIn());
                mRows.put("homePhone",customer.getHomePhone());
                mRows.put("cellPhone",customer.getCellPhone());
                mRows.put("workPhone",customer.getWorkPhone());
                mRows.put("workPhoneExt",customer.getWorkPhoneExt());
                
                List<BillingAddressType> billingAddressList = customer.getBillingAddress();
                
                if(UtilValidate.isNotEmpty(billingAddressList)) 
                {
                	for(int i = 0; i < billingAddressList.size(); i++) 
                	{
                		BillingAddressType billingAddress = (BillingAddressType)billingAddressList.get(i);
                		
                		mRows.put("billingCountry_"+(i+1),billingAddress.getCountry());
                		mRows.put("billingAddress1_"+(i+1),billingAddress.getAddress1());
                		mRows.put("billingAddress2_"+(i+1),billingAddress.getAddress2());
                		mRows.put("billingAddress3_"+(i+1),billingAddress.getAddress3());
                		mRows.put("billingCity_"+(i+1),billingAddress.getCityTown());
                		mRows.put("billingState_"+(i+1),billingAddress.getStateProvince());
                		mRows.put("billingZip_"+(i+1),billingAddress.getZipPostCode());
                	}
                	mRows.put("totBillingAddress",new Integer(billingAddressList.size()).toString());
                }
                
                List<ShippingAddressType> shippingAddressList = customer.getShippingAddress();
                
                if(UtilValidate.isNotEmpty(shippingAddressList)) 
                {
                	for(int i = 0; i < shippingAddressList.size(); i++) 
                	{
                		ShippingAddressType shippingAddress = (ShippingAddressType)shippingAddressList.get(i);
                		
                		mRows.put("shippingCountry_"+(i+1), shippingAddress.getCountry());
                		mRows.put("shippingAddress1_"+(i+1),shippingAddress.getAddress1());
                		mRows.put("shippingAddress2_"+(i+1),shippingAddress.getAddress2());
                		mRows.put("shippingAddress3_"+(i+1),shippingAddress.getAddress3());
                		mRows.put("shippingCity_"+(i+1),shippingAddress.getCityTown());
                		mRows.put("shippingState_"+(i+1),shippingAddress.getStateProvince());
                		mRows.put("shippingZip_"+(i+1),shippingAddress.getZipPostCode());
                	}
                	mRows.put("totShippingAddress",new Integer(shippingAddressList.size()).toString());
                }
                
                UserLoginType userLogin = customer.getUserLogin();
                
                if(UtilValidate.isNotEmpty(userLogin)) 
                {
                	mRows.put("userName",userLogin.getUserName());
                    mRows.put("password",userLogin.getPassword());
                    mRows.put("userEnabled",userLogin.getUserEnabled());
                    mRows.put("userIsSystem",userLogin.getUserIsSystem());
                }
                
                CustomerAttributeType customerAttribute = customer.getCustomerAttribute();
                
                if(UtilValidate.isNotEmpty(customerAttribute))
                {
                	List<AttributeType> attributeList = customerAttribute.getAttribute();
                	for(int i = 0; i < attributeList.size(); i++) 
                	{
                		AttributeType attribute = (AttributeType)attributeList.get(i);
                		
                		mRows.put("attrName_"+(i+1), attribute.getName());
                		mRows.put("attrValue_"+(i+1),attribute.getValue());
                	}
                	mRows.put("totAttributes",new Integer(attributeList.size()).toString());
                }
                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    
    
    private static void buildCustomer(List dataRows,String xmlDataDirPath, List messages)
    {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));
   	    String partyId = null;
        
		try 
		{
	        fOutFile = new File(xmlDataDirPath, "000-Customer.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                     StringBuilder  rowString = new StringBuilder();
	            	 Map mRow = (Map)dataRows.get(i);
	            	 if(UtilValidate.isNotEmpty(mRow.get("customerId"))) 
	            	 {
	            		 partyId = (String)mRow.get("customerId");
	            	 } 
	            	 else 
	            	 {
	            		 partyId = _delegator.getNextSeqId("Party");
	            	 }
                     rowString.append("<" + "Party" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("partyTypeId" + "=\"" + "PERSON" + "\" ");
                     rowString.append("statusId" + "=\"" + "PARTY_ENABLED" + "\" ");
                     if(UtilValidate.isNotEmpty(mRow.get("dateRegistered"))) 
                     {
                    	 rowString.append("createdDate" + "=\"" +  mRow.get("dateRegistered") + "\" ");
                     }
                     else
                     {
                    	 rowString.append("createdDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");	 
                     }
                     rowString.append("lastModifiedDate" + "=\"" +  _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     rowString.setLength(0);
                     rowString.append("<" + "PartyRole" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("roleTypeId" + "=\"" + "CUSTOMER" + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     List<GenericValue> productStoreRoles = _delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId",partyId,"roleTypeId","CUSTOMER","productStoreId",mRow.get("productStoreId")),UtilMisc.toList("-fromDate"));
 	                 if(UtilValidate.isNotEmpty(productStoreRoles)) 
 	                 {
 	                	productStoreRoles = EntityUtil.filterByDate(productStoreRoles);
 	                 }
 	                 if(UtilValidate.isEmpty(productStoreRoles))
	                 {
	                     rowString.setLength(0);
	                     rowString.append("<" + "ProductStoreRole" + " ");
	                     rowString.append("partyId" + "=\"" + partyId + "\" ");
	                     rowString.append("roleTypeId" + "=\"" + "CUSTOMER" + "\" ");
	                     rowString.append("productStoreId" + "=\"" + mRow.get("productStoreId") + "\" ");
	                     rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
	                     rowString.append("/>");
	                     bwOutFile.write(rowString.toString());
	                     bwOutFile.newLine();
	                 }
 	                 
                     String userName = (String)mRow.get("userName");
                     String password = (String)mRow.get("password");
                     if(UtilValidate.isNotEmpty(password)) 
                     {
                    	 password = useEncryption ? HashCrypt.getDigestHash(password, LoginServices.getHashType()) : password;
                     }
                     
                     rowString.setLength(0);
                     rowString.append("<" + "UserLogin" + " ");
                     rowString.append("userLoginId" + "=\"" + userName + "\" ");
                     rowString.append("currentPassword" + "=\"" + password + "\" ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     if(UtilValidate.isNotEmpty((String)mRow.get("userEnabled"))) 
                     {
                    	 rowString.append("enabled" + "=\"" + (String)mRow.get("userEnabled") + "\" ");
                     }
                     if(UtilValidate.isNotEmpty((String)mRow.get("userIsSystem"))) 
                     {
                    	 rowString.append("isSystem" + "=\"" + (String)mRow.get("userIsSystem") + "\" "); 
                     }
                     
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     
                     rowString.setLength(0);
                     rowString.append("<" + "Person" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("firstName" + "=\"" + (String)mRow.get("firstName") + "\" ");
                     rowString.append("lastName" + "=\"" + (String)mRow.get("lastName") + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                     String contactMechId = null;
                     if(UtilValidate.isNotEmpty((String)mRow.get("homePhone"))) 
                     {
                    	 List<GenericValue> partyContactDetailByPurposeHomePhoneList = _delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_HOME", "contactMechTypeId", "TELECOM_NUMBER", "contactNumber", (String)mRow.get("homePhone")), UtilMisc.toList("-fromDate"));
                    	 partyContactDetailByPurposeHomePhoneList = EntityUtil.filterByDate(partyContactDetailByPurposeHomePhoneList);
                         if(UtilValidate.isNotEmpty(partyContactDetailByPurposeHomePhoneList))
                         {
                        	 GenericValue partyContactDetailByPurposeHomePhone = EntityUtil.getFirst(partyContactDetailByPurposeHomePhoneList);
                             contactMechId = partyContactDetailByPurposeHomePhone.getString("contactMechId");
                         }
                         else
                         {
                        	 contactMechId = _delegator.getNextSeqId("ContactMech");
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "ContactMech" + " ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechTypeId" + "=\"" + "TELECOM_NUMBER" + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();

                         String partyContactMechFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechHomePhoneList = _delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("-fromDate"));
                         partyContectMechHomePhoneList = EntityUtil.filterByDate(partyContectMechHomePhoneList);
                         if(UtilValidate.isNotEmpty(partyContectMechHomePhoneList))
                         {
                        	 GenericValue partyContectMechHomePhone = EntityUtil.getFirst(partyContectMechHomePhoneList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechHomePhone))
                        	 {
                        		 partyContactMechFromDate =_sdf.format(new Date(partyContectMechHomePhone.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMech" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("fromDate" + "=\"" +  partyContactMechFromDate + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         String partyContactMechPurposeFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechPurposeHomePhoneList = _delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "PHONE_HOME"), UtilMisc.toList("-fromDate"));
                         partyContectMechPurposeHomePhoneList = EntityUtil.filterByDate(partyContectMechPurposeHomePhoneList);
                         if(UtilValidate.isNotEmpty(partyContectMechPurposeHomePhoneList))
                         {
                        	 GenericValue partyContectMechPurposeHomePhone = EntityUtil.getFirst(partyContectMechPurposeHomePhoneList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechPurposeHomePhone))
                        	 {
                        		 partyContactMechPurposeFromDate =_sdf.format(new Date(partyContectMechPurposeHomePhone.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMechPurpose" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechPurposeTypeId" + "=\"" + "PHONE_HOME" + "\" ");
                         rowString.append("fromDate" + "=\"" +  partyContactMechPurposeFromDate + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         rowString.setLength(0);
                         rowString.append("<" + "TelecomNumber" + " ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactNumber" + "=\"" +  (String)mRow.get("homePhone") + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                     }
                     
                     if(UtilValidate.isNotEmpty((String)mRow.get("cellPhone"))) 
                     {
                    	 
                    	 List<GenericValue> partyContactDetailByPurposeCellPhoneList = _delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_MOBILE", "contactMechTypeId", "TELECOM_NUMBER", "contactNumber", (String)mRow.get("cellPhone")), UtilMisc.toList("-fromDate"));
                    	 partyContactDetailByPurposeCellPhoneList = EntityUtil.filterByDate(partyContactDetailByPurposeCellPhoneList);
                         if(UtilValidate.isNotEmpty(partyContactDetailByPurposeCellPhoneList))
                         {
                        	 GenericValue partyContactDetailByPurposeCellPhone = EntityUtil.getFirst(partyContactDetailByPurposeCellPhoneList);
                             contactMechId = partyContactDetailByPurposeCellPhone.getString("contactMechId");
                         }
                         else
                         {
                        	 contactMechId = _delegator.getNextSeqId("ContactMech");
                         }
                    	 
                         rowString.setLength(0);
                         rowString.append("<" + "ContactMech" + " ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechTypeId" + "=\"" + "TELECOM_NUMBER" + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();

                         String partyContactMechFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechCellPhoneList = _delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("-fromDate"));
                         partyContectMechCellPhoneList = EntityUtil.filterByDate(partyContectMechCellPhoneList);
                         if(UtilValidate.isNotEmpty(partyContectMechCellPhoneList))
                         {
                        	 GenericValue partyContectMechCellPhone = EntityUtil.getFirst(partyContectMechCellPhoneList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechCellPhone))
                        	 {
                        		 partyContactMechFromDate =_sdf.format(new Date(partyContectMechCellPhone.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMech" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("fromDate" + "=\"" +  partyContactMechFromDate + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         String partyContactMechPurposeFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechPurposeCellPhoneList = _delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "PHONE_MOBILE"), UtilMisc.toList("-fromDate"));
                         partyContectMechPurposeCellPhoneList = EntityUtil.filterByDate(partyContectMechPurposeCellPhoneList);
                         if(UtilValidate.isNotEmpty(partyContectMechPurposeCellPhoneList))
                         {
                        	 GenericValue partyContectMechPurposeCellPhone = EntityUtil.getFirst(partyContectMechPurposeCellPhoneList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechPurposeCellPhone))
                        	 {
                        		 partyContactMechPurposeFromDate =_sdf.format(new Date(partyContectMechPurposeCellPhone.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMechPurpose" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechPurposeTypeId" + "=\"" + "PHONE_MOBILE" + "\" ");
                         rowString.append("fromDate" + "=\"" + partyContactMechPurposeFromDate + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         rowString.setLength(0);
                         rowString.append("<" + "TelecomNumber" + " ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactNumber" + "=\"" +  (String)mRow.get("cellPhone") + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                     }
                     
                     if(UtilValidate.isNotEmpty((String)mRow.get("workPhone"))) 
                     {
                    	 List<GenericValue> partyContactDetailByPurposeWorkPhoneList = _delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_WORK", "contactMechTypeId", "TELECOM_NUMBER", "contactNumber", (String)mRow.get("workPhone")), UtilMisc.toList("-fromDate"));
                    	 partyContactDetailByPurposeWorkPhoneList = EntityUtil.filterByDate(partyContactDetailByPurposeWorkPhoneList);
                         if(UtilValidate.isNotEmpty(partyContactDetailByPurposeWorkPhoneList))
                         {
                        	 GenericValue partyContactDetailByPurposeWorkPhone = EntityUtil.getFirst(partyContactDetailByPurposeWorkPhoneList);
                             contactMechId = partyContactDetailByPurposeWorkPhone.getString("contactMechId");
                         }
                         else
                         {
                        	 contactMechId = _delegator.getNextSeqId("ContactMech");
                         }
                    	 
                         rowString.setLength(0);
                         rowString.append("<" + "ContactMech" + " ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechTypeId" + "=\"" + "TELECOM_NUMBER" + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();

                         String partyContactMechFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechWorkPhoneList = _delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("-fromDate"));
                         partyContectMechWorkPhoneList = EntityUtil.filterByDate(partyContectMechWorkPhoneList);
                         if(UtilValidate.isNotEmpty(partyContectMechWorkPhoneList))
                         {
                        	 GenericValue partyContectMechWorkPhone = EntityUtil.getFirst(partyContectMechWorkPhoneList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechWorkPhone))
                        	 {
                        		 partyContactMechFromDate =_sdf.format(new Date(partyContectMechWorkPhone.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMech" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("fromDate" + "=\"" +  partyContactMechFromDate + "\" ");
                         if(UtilValidate.isNotEmpty((String)mRow.get("workPhoneExt"))) 
                         {
                        	 rowString.append("extension" + "=\"" +  (String)mRow.get("workPhoneExt") + "\" ");
                         }
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         String partyContactMechPurposeFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechPurposeWorkPhoneList = _delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "PHONE_WORK"), UtilMisc.toList("-fromDate"));
                         partyContectMechPurposeWorkPhoneList = EntityUtil.filterByDate(partyContectMechPurposeWorkPhoneList);
                         if(UtilValidate.isNotEmpty(partyContectMechPurposeWorkPhoneList))
                         {
                        	 GenericValue partyContectMechPurposeWorkPhone = EntityUtil.getFirst(partyContectMechPurposeWorkPhoneList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechPurposeWorkPhone))
                        	 {
                        		 partyContactMechPurposeFromDate =_sdf.format(new Date(partyContectMechPurposeWorkPhone.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMechPurpose" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechPurposeTypeId" + "=\"" + "PHONE_WORK" + "\" ");
                         rowString.append("fromDate" + "=\"" +  partyContactMechPurposeFromDate + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         rowString.setLength(0);
                         rowString.append("<" + "TelecomNumber" + " ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactNumber" + "=\"" +  (String)mRow.get("workPhone") + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                     }
                     
                     if(UtilValidate.isNotEmpty((String)mRow.get("emailAddress")))
                     {
                    	 List<GenericValue> partyContactDetailByPurposeEmailList = _delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_EMAIL", "contactMechTypeId", "EMAIL_ADDRESS", "infoString", (String)mRow.get("emailAddress")), UtilMisc.toList("-fromDate"));
                    	 partyContactDetailByPurposeEmailList = EntityUtil.filterByDate(partyContactDetailByPurposeEmailList);
                         if(UtilValidate.isNotEmpty(partyContactDetailByPurposeEmailList))
                         {
                        	 GenericValue partyContactDetailByPurposeEmail = EntityUtil.getFirst(partyContactDetailByPurposeEmailList);
                             contactMechId = partyContactDetailByPurposeEmail.getString("contactMechId");
                         }
                         else
                         {
                        	 contactMechId = _delegator.getNextSeqId("ContactMech");
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "ContactMech" + " ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechTypeId" + "=\"" + "EMAIL_ADDRESS" + "\" ");
                         rowString.append("infoString" + "=\"" + (String)mRow.get("emailAddress") + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         String partyContactMechFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechEmailList = _delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("-fromDate"));
                         partyContectMechEmailList = EntityUtil.filterByDate(partyContectMechEmailList);
                         if(UtilValidate.isNotEmpty(partyContectMechEmailList))
                         {
                        	 GenericValue partyContectMechEmail = EntityUtil.getFirst(partyContectMechEmailList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechEmail))
                        	 {
                        		 partyContactMechFromDate =_sdf.format(new Date(partyContectMechEmail.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMech" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("fromDate" + "=\"" +  partyContactMechFromDate + "\" ");
                         if (UtilValidate.isNotEmpty(mRow.get("emailOptIn")))
                         {
                             if(((String)mRow.get("emailOptIn")).equalsIgnoreCase("TRUE")) 
                             {
                            	 rowString.append("allowSolicitation" + "=\"" + "Y" + "\" ");	 
                             }
                             else if(((String)mRow.get("emailOptIn")).equalsIgnoreCase("FALSE")) 
                             {
                            	 rowString.append("allowSolicitation" + "=\"" + "N" + "\" ");	 
                             }
                             else
                             {
                            	 rowString.append("allowSolicitation" + "=\"" + "N" + "\" ");	 
                             }
                        	 
                         }
                         else
                         {
                        	 rowString.append("allowSolicitation" + "=\"" + "N" + "\" ");	 
                        	 
                         }
                        		 
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                         
                         String partyContactMechPurposeFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                         List partyContectMechPurposeEmailList = _delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "PRIMARY_EMAIL"), UtilMisc.toList("-fromDate"));
                         partyContectMechPurposeEmailList = EntityUtil.filterByDate(partyContectMechPurposeEmailList);
                         if(UtilValidate.isNotEmpty(partyContectMechPurposeEmailList))
                         {
                        	 GenericValue partyContectMechPurposeEmail = EntityUtil.getFirst(partyContectMechPurposeEmailList);
                        	 if(UtilValidate.isNotEmpty(partyContectMechPurposeEmail))
                        	 {
                        		 partyContactMechPurposeFromDate =_sdf.format(new Date(partyContectMechPurposeEmail.getTimestamp("fromDate").getTime())); 
                        	 }
                         }
                         rowString.setLength(0);
                         rowString.append("<" + "PartyContactMechPurpose" + " ");
                         rowString.append("partyId" + "=\"" + partyId + "\" ");
                         rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
                         rowString.append("contactMechPurposeTypeId" + "=\"" + "PRIMARY_EMAIL" + "\" ");
                         rowString.append("fromDate" + "=\"" + partyContactMechPurposeFromDate + "\" ");
                         rowString.append("/>");
                         bwOutFile.write(rowString.toString());
                         bwOutFile.newLine();
                     }
                     
                     
                     if(UtilValidate.isNotEmpty(mRow.get("totBillingAddress")) && Integer.parseInt((String)mRow.get("totBillingAddress")) > 0) 
                     {
		            	 for(int billingAddressNo = 0; billingAddressNo < Integer.parseInt((String)mRow.get("totBillingAddress")); billingAddressNo++) 
		            	 {
		            		 StringBuilder billingAddressFromFile = new StringBuilder("");
		            		 
		            		 if(mRow.get("billingAddress1_"+(billingAddressNo+1)) != null) 
		                     {
		            			 billingAddressFromFile.append((String)mRow.get("billingAddress1_"+(billingAddressNo+1))); 
		                     }
		                     if(mRow.get("billingAddress2_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 billingAddressFromFile.append((String)mRow.get("billingAddress2_"+(billingAddressNo+1))); 
		                     }
		                     if(mRow.get("billingAddress3_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 billingAddressFromFile.append((String)mRow.get("billingAddress3_"+(billingAddressNo+1))); 
		                     }
		                     if(mRow.get("billingCity_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 billingAddressFromFile.append((String)mRow.get("billingCity_"+(billingAddressNo+1)));
		                     }
		                     if(mRow.get("billingState_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 billingAddressFromFile.append(mRow.get("billingState_"+(billingAddressNo+1)));
		                     }
		                     if(mRow.get("billingZip_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 billingAddressFromFile.append(mRow.get("billingZip_"+(billingAddressNo+1)));
		                     }
		                     if(mRow.get("billingCountry_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 billingAddressFromFile.append(mRow.get("billingCountry_"+(billingAddressNo+1)));
		                     }
		                     contactMechId = _delegator.getNextSeqId("ContactMech");
		            		 List<GenericValue> partyContactDetailByPurposeBillingAddressList = _delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "BILLING_LOCATION", "contactMechTypeId", "POSTAL_ADDRESS"), UtilMisc.toList("-fromDate"));
	                    	 partyContactDetailByPurposeBillingAddressList = EntityUtil.filterByDate(partyContactDetailByPurposeBillingAddressList);
	                    	 for(GenericValue partyContactDetailByPurposeBillingAddress : partyContactDetailByPurposeBillingAddressList)
	                    	 {
	                    		 StringBuilder billingAddress = new StringBuilder("");
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeBillingAddress.getString("address1")))
	                    		 {
	                    			 billingAddress.append(partyContactDetailByPurposeBillingAddress.getString("address1"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeBillingAddress.getString("address2")))
	                    		 {
	                    			 billingAddress.append(partyContactDetailByPurposeBillingAddress.getString("address2"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeBillingAddress.getString("address3")))
	                    		 {
	                    			 billingAddress.append(partyContactDetailByPurposeBillingAddress.getString("address3"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeBillingAddress.getString("city")))
	                    		 {
	                    			 billingAddress.append(partyContactDetailByPurposeBillingAddress.getString("city"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeBillingAddress.getString("stateProvinceGeoId")))
	                    		 {
	                    			 billingAddress.append(partyContactDetailByPurposeBillingAddress.getString("stateProvinceGeoId"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeBillingAddress.getString("postalCode")))
	                    		 {
	                    			 billingAddress.append(partyContactDetailByPurposeBillingAddress.getString("postalCode"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeBillingAddress.getString("countryGeoId")))
	                    		 {
	                    			 billingAddress.append(partyContactDetailByPurposeBillingAddress.getString("countryGeoId"));
	                    		 }
	                    		 if(billingAddress.toString().equals(billingAddressFromFile.toString()))
	                    		 {
	                    			 contactMechId =  partyContactDetailByPurposeBillingAddress.getString("contactMechId");
	                    			 break;
	                    		 }
	                    	 }
		            		 
		                     rowString.setLength(0);
		                     rowString.append("<" + "ContactMech" + " ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("contactMechTypeId" + "=\"" + "POSTAL_ADDRESS" + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();

		                     String partyContactMechFromDate = _sdf.format(UtilDateTime.nowTimestamp());
	                         List partyContectMechBillingAddressList = _delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("-fromDate"));
	                         partyContectMechBillingAddressList = EntityUtil.filterByDate(partyContectMechBillingAddressList);
	                         if(UtilValidate.isNotEmpty(partyContectMechBillingAddressList))
	                         {
	                        	 GenericValue partyContectMechBillingAddress = EntityUtil.getFirst(partyContectMechBillingAddressList);
	                        	 if(UtilValidate.isNotEmpty(partyContectMechBillingAddress))
	                        	 {
	                        		 partyContactMechFromDate =_sdf.format(new Date(partyContectMechBillingAddress.getTimestamp("fromDate").getTime())); 
	                        	 }
	                         }
		                     
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyContactMech" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("fromDate" + "=\"" +  partyContactMechFromDate + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     String partyContactMechPurposeFromDate = _sdf.format(UtilDateTime.nowTimestamp());
	                         List partyContectMechPurposeBillingAddressList = _delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "BILLING_LOCATION"), UtilMisc.toList("-fromDate"));
	                         partyContectMechPurposeBillingAddressList = EntityUtil.filterByDate(partyContectMechPurposeBillingAddressList);
	                         if(UtilValidate.isNotEmpty(partyContectMechPurposeBillingAddressList))
	                         {
	                        	 GenericValue partyContectMechPurposeBillingAddress = EntityUtil.getFirst(partyContectMechPurposeBillingAddressList);
	                        	 if(UtilValidate.isNotEmpty(partyContectMechPurposeBillingAddress))
	                        	 {
	                        		 partyContactMechPurposeFromDate =_sdf.format(new Date(partyContectMechPurposeBillingAddress.getTimestamp("fromDate").getTime())); 
	                        	 }
	                         }
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyContactMechPurpose" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("contactMechPurposeTypeId" + "=\"" + "BILLING_LOCATION" + "\" ");
		                     rowString.append("fromDate" + "=\"" + partyContactMechPurposeFromDate + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     List<GenericValue> partyContectMechPurposeGeneralAddressList = EntityUtil.filterByDate(_delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId",partyId, "contactMechPurposeTypeId", "GENERAL_LOCATION")));
		                     partyContectMechPurposeGeneralAddressList = EntityUtil.filterByDate(partyContectMechPurposeGeneralAddressList);
		                     
		                     if(UtilValidate.isEmpty(partyContectMechPurposeGeneralAddressList)) 
		                     {
		                    	 partyContectMechPurposeGeneralAddressList = EntityUtil.filterByAnd(partyContectMechPurposeGeneralAddressList, UtilMisc.toMap("contactMechId", contactMechId));
		                    	 partyContactMechPurposeFromDate = _sdf.format(UtilDateTime.nowTimestamp());
		                         if(UtilValidate.isNotEmpty(partyContectMechPurposeGeneralAddressList))
		                         {
		                        	 GenericValue partyContectMechPurposeGeneralAddress = EntityUtil.getFirst(partyContectMechPurposeGeneralAddressList);
		                        	 if(UtilValidate.isNotEmpty(partyContectMechPurposeGeneralAddress))
		                        	 {
		                        		 partyContactMechPurposeFromDate =_sdf.format(new Date(partyContectMechPurposeGeneralAddress.getTimestamp("fromDate").getTime())); 
		                        	 }
		                         }
			                     rowString.setLength(0);
			                     rowString.append("<" + "PartyContactMechPurpose" + " ");
			                     rowString.append("partyId" + "=\"" + partyId + "\" ");
			                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
			                     rowString.append("contactMechPurposeTypeId" + "=\"" + "GENERAL_LOCATION" + "\" ");
			                     rowString.append("fromDate" + "=\"" +  partyContactMechPurposeFromDate + "\" ");
			                     rowString.append("/>");
			                     bwOutFile.write(rowString.toString());
			                     bwOutFile.newLine();
		                     }
		                     
		                     rowString.setLength(0);
		                     rowString.append("<" + "PostalAddress" + " ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("toName" + "=\"" + (String)mRow.get("firstName") + " " + (String)mRow.get("lastName") + "\" ");
		                     if(mRow.get("billingAddress1_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("address1" + "=\"" +  (String)mRow.get("billingAddress1_"+(billingAddressNo+1)) + "\" "); 
		                     }
		                     if(mRow.get("billingAddress2_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("address2" + "=\"" +  (String)mRow.get("billingAddress2_"+(billingAddressNo+1)) + "\" "); 
		                     }
		                     if(mRow.get("billingAddress3_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("address3" + "=\"" +  (String)mRow.get("billingAddress3_"+(billingAddressNo+1)) + "\" "); 
		                     }
		                     if(mRow.get("billingCity_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("city" + "=\"" +  (String)mRow.get("billingCity_"+(billingAddressNo+1)) + "\" ");
		                     }
		                     if(mRow.get("billingState_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("stateProvinceGeoId" + "=\"" +  mRow.get("billingState_"+(billingAddressNo+1)) + "\" ");
		                     }
		                     if(mRow.get("billingZip_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("postalCode" + "=\"" +  mRow.get("billingZip_"+(billingAddressNo+1)) + "\" ");
		                     }
		                     if(mRow.get("billingCountry_"+(billingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("countryGeoId" + "=\"" +  mRow.get("billingCountry_"+(billingAddressNo+1)) + "\" ");
		                     }
		                     
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		            	 }
                     }
                     
                     if(UtilValidate.isNotEmpty(mRow.get("totShippingAddress")) && Integer.parseInt((String)mRow.get("totShippingAddress")) > 0) 
                     {
		            	 for(int shippingAddressNo = 0; shippingAddressNo < Integer.parseInt((String)mRow.get("totShippingAddress")); shippingAddressNo++) 
		            	 {
                             StringBuilder shippingAddressFromFile = new StringBuilder("");
		            		 
		            		 if(mRow.get("shippingAddress1_"+(shippingAddressNo+1)) != null) 
		                     {
		            			 shippingAddressFromFile.append((String)mRow.get("shippingAddress1_"+(shippingAddressNo+1))); 
		                     }
		                     if(mRow.get("shippingAddress2_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 shippingAddressFromFile.append((String)mRow.get("shippingAddress2_"+(shippingAddressNo+1))); 
		                     }
		                     if(mRow.get("shippingAddress3_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 shippingAddressFromFile.append((String)mRow.get("shippingAddress3_"+(shippingAddressNo+1))); 
		                     }
		                     if(mRow.get("shippingCity_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 shippingAddressFromFile.append((String)mRow.get("shippingCity_"+(shippingAddressNo+1)));
		                     }
		                     if(mRow.get("shippingState_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 shippingAddressFromFile.append(mRow.get("shippingState_"+(shippingAddressNo+1)));
		                     }
		                     if(mRow.get("shippingZip_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 shippingAddressFromFile.append(mRow.get("shippingZip_"+(shippingAddressNo+1)));
		                     }
		                     if(mRow.get("shippingCountry_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 shippingAddressFromFile.append(mRow.get("shippingCountry_"+(shippingAddressNo+1)));
		                     }
		                     contactMechId = _delegator.getNextSeqId("ContactMech");
		            		 List<GenericValue> partyContactDetailByPurposeShippingAddressList = _delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "SHIPPING_LOCATION", "contactMechTypeId", "POSTAL_ADDRESS"), UtilMisc.toList("-fromDate"));
	                    	 partyContactDetailByPurposeShippingAddressList = EntityUtil.filterByDate(partyContactDetailByPurposeShippingAddressList);
	                    	 for(GenericValue partyContactDetailByPurposeShippingAddress : partyContactDetailByPurposeShippingAddressList)
	                    	 {
	                    		 StringBuilder shippingAddress = new StringBuilder("");
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeShippingAddress.getString("address1")))
	                    		 {
	                    			 shippingAddress.append(partyContactDetailByPurposeShippingAddress.getString("address1"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeShippingAddress.getString("address2")))
	                    		 {
	                    			 shippingAddress.append(partyContactDetailByPurposeShippingAddress.getString("address2"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeShippingAddress.getString("address3")))
	                    		 {
	                    			 shippingAddress.append(partyContactDetailByPurposeShippingAddress.getString("address3"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeShippingAddress.getString("city")))
	                    		 {
	                    			 shippingAddress.append(partyContactDetailByPurposeShippingAddress.getString("city"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeShippingAddress.getString("stateProvinceGeoId")))
	                    		 {
	                    			 shippingAddress.append(partyContactDetailByPurposeShippingAddress.getString("stateProvinceGeoId"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeShippingAddress.getString("postalCode")))
	                    		 {
	                    			 shippingAddress.append(partyContactDetailByPurposeShippingAddress.getString("postalCode"));
	                    		 }
	                    		 if(UtilValidate.isNotEmpty(partyContactDetailByPurposeShippingAddress.getString("countryGeoId")))
	                    		 {
	                    			 shippingAddress.append(partyContactDetailByPurposeShippingAddress.getString("countryGeoId"));
	                    		 }
	                    		 if(shippingAddress.toString().equals(shippingAddressFromFile.toString()))
	                    		 {
	                    			 contactMechId =  partyContactDetailByPurposeShippingAddress.getString("contactMechId");
	                    			 break;
	                    		 }
	                    	 }
		            		 
		                     rowString.setLength(0);
		                     rowString.append("<" + "ContactMech" + " ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("contactMechTypeId" + "=\"" + "POSTAL_ADDRESS" + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();

		                     String partyContactMechFromDate = _sdf.format(UtilDateTime.nowTimestamp());
	                         List partyContectMechShippingAddressList = _delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId), UtilMisc.toList("-fromDate"));
	                         partyContectMechShippingAddressList = EntityUtil.filterByDate(partyContectMechShippingAddressList);
	                         if(UtilValidate.isNotEmpty(partyContectMechShippingAddressList))
	                         {
	                        	 GenericValue partyContectMechShippingAddress = EntityUtil.getFirst(partyContectMechShippingAddressList);
	                        	 if(UtilValidate.isNotEmpty(partyContectMechShippingAddress))
	                        	 {
	                        		 partyContactMechFromDate =_sdf.format(new Date(partyContectMechShippingAddress.getTimestamp("fromDate").getTime())); 
	                        	 }
	                         }
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyContactMech" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("fromDate" + "=\"" + partyContactMechFromDate + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     String partyContactMechPurposeFromDate = _sdf.format(UtilDateTime.nowTimestamp());
	                         List partyContectMechPurposeShippingAddressList = _delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIPPING_LOCATION"), UtilMisc.toList("-fromDate"));
	                         partyContectMechPurposeShippingAddressList = EntityUtil.filterByDate(partyContectMechPurposeShippingAddressList);
	                         if(UtilValidate.isNotEmpty(partyContectMechPurposeShippingAddressList))
	                         {
	                        	 GenericValue partyContectMechPurposeShippingAddress = EntityUtil.getFirst(partyContectMechPurposeShippingAddressList);
	                        	 if(UtilValidate.isNotEmpty(partyContectMechPurposeShippingAddress))
	                        	 {
	                        		 partyContactMechPurposeFromDate =_sdf.format(new Date(partyContectMechPurposeShippingAddress.getTimestamp("fromDate").getTime())); 
	                        	 }
	                         }
		                     rowString.setLength(0);
		                     rowString.append("<" + "PartyContactMechPurpose" + " ");
		                     rowString.append("partyId" + "=\"" + partyId + "\" ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("contactMechPurposeTypeId" + "=\"" + "SHIPPING_LOCATION" + "\" ");
		                     rowString.append("fromDate" + "=\"" +  partyContactMechPurposeFromDate + "\" ");
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		                     
		                     rowString.setLength(0);
		                     rowString.append("<" + "PostalAddress" + " ");
		                     rowString.append("contactMechId" + "=\"" + contactMechId + "\" ");
		                     rowString.append("toName" + "=\"" + (String)mRow.get("firstName") + " " + (String)mRow.get("lastName") + "\" ");
		                     if(mRow.get("shippingAddress1_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("address1" + "=\"" +  (String)mRow.get("shippingAddress1_"+(shippingAddressNo+1)) + "\" "); 
		                     }
		                     if(mRow.get("shippingAddress2_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("address2" + "=\"" +  (String)mRow.get("shippingAddress2_"+(shippingAddressNo+1)) + "\" "); 
		                     }
		                     if(mRow.get("shippingAddress3_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("address3" + "=\"" +  (String)mRow.get("shippingAddress3_"+(shippingAddressNo+1)) + "\" "); 
		                     }
		                     if(mRow.get("shippingCity_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("city" + "=\"" +  (String)mRow.get("shippingCity_"+(shippingAddressNo+1)) + "\" ");
		                     }
		                     if(mRow.get("shippingState_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("stateProvinceGeoId" + "=\"" +  mRow.get("shippingState_"+(shippingAddressNo+1)) + "\" ");
		                     }
		                     if(mRow.get("shippingZip_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("postalCode" + "=\"" +  mRow.get("shippingZip_"+(shippingAddressNo+1)) + "\" ");
		                     }
		                     if(mRow.get("shippingCountry_"+(shippingAddressNo+1)) != null) 
		                     {
		                    	 rowString.append("countryGeoId" + "=\"" +  mRow.get("shippingCountry_"+(shippingAddressNo+1)) + "\" ");
		                     }
		                     
		                     rowString.append("/>");
		                     bwOutFile.write(rowString.toString());
		                     bwOutFile.newLine();
		            	 }
		            	 
                     }
 	            	 
                     buildCustomerAttribute(rowString, bwOutFile, mRow, partyId);
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
            
    	}
      	catch (Exception e) 
      	{
            Debug.logError(e.getMessage(), module + ".buildCustomer");
            messages.add("Error: processing party Id[" + partyId + "].  In module:" + module + ".buildCustomer");
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
                Debug.logError(ioe.getMessage(), module + ".buildCustomer");
            }
        }
    }
    
    private static void buildCustomerAttribute(StringBuilder rowString, BufferedWriter bwOutFile, Map mRow, String partyId) 
    {
		try 
		{
			if(UtilValidate.isNotEmpty(mRow.get("totAttributes")) && Integer.parseInt((String)mRow.get("totAttributes")) > 0) 
            {
				
           	    for(int attributeNo = 0; attributeNo < Integer.parseInt((String)mRow.get("totAttributes")); attributeNo++) 
           	    {
           		    if(UtilValidate.isNotEmpty(mRow.get("attrName_"+attributeNo)) && UtilValidate.isNotEmpty(mRow.get("attrValue_"+attributeNo)))
           		    {
           			    rowString.setLength(0);
	                    rowString.append("<" + "PartyAttribute" + " ");
	                    rowString.append("partyId" + "=\"" + partyId + "\" ");
	                    rowString.append("attrName" + "=\"" + (String)mRow.get("attrName_"+attributeNo) + "\" ");
	                    rowString.append("attrValue" + "=\"" + (String)mRow.get("attrValue_"+attributeNo) + "\" ");	 
	                    rowString.append("/>");
	                    bwOutFile.write(rowString.toString());
	                    bwOutFile.newLine();
           		    }
           	    }
            }
    	}
      	catch (Exception e) 
      	{
            Debug.logError(e.getMessage(), module + ".buildCustomerAttribute");
   	    }
    }
    
    public static Map<String, Object> exportProductXML(DispatchContext ctx, Map<String, ?> context) 
    {
        _delegator = ctx.getDelegator();
        _dispatcher = ctx.getDispatcher();
        _locale = (Locale) context.get("locale");
        List<String> messages = FastList.newInstance();
        try {
        String productStoreId = (String) context.get("productStoreId");
        String browseRootProductCategoryId = (String) context.get("browseRootProductCategoryId");
        String isSampleFile = (String) context.get("sampleFile");
        String fileName="clientProductImport.xml";
        
        ObjectFactory factory = new ObjectFactory();
        
        BigFishProductFeedType bfProductFeedType = factory.createBigFishProductFeedType();
        
        if (UtilValidate.isNotEmpty(isSampleFile) && isSampleFile.equals("Y"))
        {
        	fileName="sampleClientProductImport.xml";
        }
        String importDataPath = FlexibleStringExpander.expandString(OSAFE_ADMIN_PROP.getString("ecommerce-import-data-path"),context);
        File file = new File(importDataPath, "temp" + fileName);
        if (UtilValidate.isNotEmpty(isSampleFile) && isSampleFile.equals("Y")) {
        	
        	//Product Category
	        ProductCategoryType productCategoryType = factory.createProductCategoryType();
	        List productCategoryList =  productCategoryType.getCategory();
	        createProductCategoryXmlSample(factory, productCategoryList);
	  	    bfProductFeedType.setProductCategory(productCategoryType);
	  	    
	  	    //Products
	  	    ProductsType productsType = factory.createProductsType();
	  	    List productList = productsType.getProduct();
	  	    createProductXmlSample(factory, productList);
	  	    bfProductFeedType.setProducts(productsType);
	  	    
	  	    //Product Assoc
	  	    ProductAssociationType productAssociationType = factory.createProductAssociationType();
	  	    List productAssocList = productAssociationType.getAssociation();
	  	    createProductAssocXmlSample(factory, productAssocList);
	  	    bfProductFeedType.setProductAssociation(productAssociationType);
	  	    
	  	    //Product Feature Swatches
	  	    ProductFeatureSwatchType productFeatureSwatchType = factory.createProductFeatureSwatchType();
	  	    List featureList = productFeatureSwatchType.getFeature();
	  	    createProductFeatureSwatchSample(factory, featureList);
	  	    bfProductFeedType.setProductFeatureSwatch(productFeatureSwatchType);
	  	    
	  	    //Product Manufactuter
	  	    ProductManufacturerType productManufacturerType = factory.createProductManufacturerType();
	  	    List manufacturerList = productManufacturerType.getManufacturer();
	  	    createProductManufacturerSample(factory, manufacturerList);
	  	    bfProductFeedType.setProductManufacturer(productManufacturerType);
	        
        } else {
        	//Product Category
	        ProductCategoryType productCategoryType = factory.createProductCategoryType();
	        List productCategoryList =  productCategoryType.getCategory();
	  	    createProductCategoryXml(factory, productCategoryList, browseRootProductCategoryId, productStoreId);
	  	    bfProductFeedType.setProductCategory(productCategoryType);
	  	    
	  	    //Products
	  	    ProductsType productsType = factory.createProductsType();
	  	    List productList = productsType.getProduct();
	  	    createProductXml(factory, productList, browseRootProductCategoryId, productStoreId);
	  	    bfProductFeedType.setProducts(productsType);
	  	    
	  	    //Product Assoc
	  	    ProductAssociationType productAssociationType = factory.createProductAssociationType();
	  	    List productAssocList = productAssociationType.getAssociation();
	  	    createProductAssocXml(factory, productAssocList, browseRootProductCategoryId, productStoreId);
	  	    bfProductFeedType.setProductAssociation(productAssociationType);
	  	    
	  	    //Product Feature Swatches
	  	    ProductFeatureSwatchType productFeatureSwatchType = factory.createProductFeatureSwatchType();
	  	    List featureList = productFeatureSwatchType.getFeature();
	  	    createProductFeatureSwatchXml(factory, featureList, browseRootProductCategoryId, productStoreId);
	  	    bfProductFeedType.setProductFeatureSwatch(productFeatureSwatchType);
	  	    
	  	    //Product Manufactuter
	  	    ProductManufacturerType productManufacturerType = factory.createProductManufacturerType();
	  	    List manufacturerList = productManufacturerType.getManufacturer();
	  	    createProductManufacturerXml(factory, manufacturerList, browseRootProductCategoryId, productStoreId);
	  	    bfProductFeedType.setProductManufacturer(productManufacturerType);
        }
  	    FeedsUtil.marshalObject(new JAXBElement<BigFishProductFeedType>(new QName("", "BigFishProductFeed"), BigFishProductFeedType.class, null, bfProductFeedType), file);
  	    
  	    new File(importDataPath, fileName).delete();
        File renameFile =new File(importDataPath, fileName);
        RandomAccessFile out = new RandomAccessFile(renameFile, "rw");
        InputStream inputStr = new FileInputStream(file);
        byte[] bytes = new byte[102400];
        int bytesRead;
        while ((bytesRead = inputStr.read(bytes)) != -1)
        {
            out.write(bytes, 0, bytesRead);
        }
        out.close();
      inputStr.close();
        } catch (Exception e) {
        	Debug.logError(e, module);
		}
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
        
    }
    
    
    
    public static void createProductCategoryXml(ObjectFactory factory, List productCategoryList, String browseRootProductCategoryId, String productStoreId) {
    	try {
    		List<GenericValue> topLavelCategoryList =  _delegator.findByAnd("ProductCategoryRollupAndChild", UtilMisc.toMap("parentProductCategoryId",browseRootProductCategoryId),UtilMisc.toList("sequenceNum"));
            List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, true, false, true);
            GenericValue workingCategory = null;
            GenericValue workingCategoryRollup = null;
            String productCategoryIdPath = null;
            Timestamp tsstamp=null;
            List<String> pathElements=null;
            String categoryImageURL=null;
            CategoryType category = null;
            for (GenericValue topLavelCategory : topLavelCategoryList) 
            {
                if ("CATALOG_CATEGORY".equals(topLavelCategory.getString("productCategoryTypeId"))) 
                {
                	category = factory.createCategoryType();
                    String productCategoryId = (String) topLavelCategory.getString("productCategoryId");
        	        List<GenericValue> lCategoryContent = _delegator.findByAnd("ProductCategoryContent", UtilMisc.toMap("productCategoryId",productCategoryId),UtilMisc.toList("-fromDate"));
        	        lCategoryContent=EntityUtil.filterByDate(lCategoryContent, UtilDateTime.nowTimestamp());
        	        category.setCategoryId(productCategoryId);
                    category.setParentCategoryId(topLavelCategory.getString("parentProductCategoryId"));
                    category.setCategoryName(topLavelCategory.getString("categoryName"));
                    if(UtilValidate.isNotEmpty(topLavelCategory.getString("description"))) {
                    	category.setDescription(topLavelCategory.getString("description"));	
                    } else {
                    	category.setDescription("");
                    }
                    
                    if(UtilValidate.isNotEmpty(topLavelCategory.getString("longDescription"))) {
                    	category.setLongDescription(topLavelCategory.getString("longDescription"));	
                    } else {
                    	category.setLongDescription("");
                    }
                    
                    categoryImageURL =topLavelCategory.getString("categoryImageUrl");
                    
                	PlpImageType plpImage = factory.createPlpImageType();
                	
                    if (UtilValidate.isNotEmpty(categoryImageURL))
                    {
                    	if (!UtilValidate.isUrl(categoryImageURL))
                    	{
                    		String categoryImagePath = getOsafeImagePath("CATEGORY_IMAGE_URL");
                    		pathElements = StringUtil.split(categoryImageURL, "/");
                            plpImage.setUrl(categoryImagePath + pathElements.get(pathElements.size() - 1));
                    	}
                    	else
                    	{
                    		plpImage.setUrl(categoryImageURL);
                    	}
                    }
                    else
                    {
                    	plpImage.setUrl("");
                    }
                    category.setPlpImage(plpImage);
                    
                    if(UtilValidate.isNotEmpty(getProductCategoryContent(productCategoryId,"PLP_ESPOT_CONTENT",lCategoryContent))) {
                    	category.setAdditionalPlpText(getProductCategoryContent(productCategoryId,"PLP_ESPOT_CONTENT",lCategoryContent));	
                    } else {
                    	category.setAdditionalPlpText("");
                    }
                    
                    if(UtilValidate.isNotEmpty(getProductCategoryContent(productCategoryId,"PDP_ADDITIONAL",lCategoryContent))) {
                    	category.setAdditionalPdpText(getProductCategoryContent(productCategoryId,"PDP_ADDITIONAL",lCategoryContent));
                    } else {
                    	category.setAdditionalPdpText("");
                    }
                    
                    tsstamp = topLavelCategory.getTimestamp("fromDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	category.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	category.setFromDate("");
                    }
                    tsstamp = topLavelCategory.getTimestamp("thruDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	category.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	category.setThruDate("");
                    }
                    productCategoryList.add(category);
                }
                
                List<GenericValue> subLavelCategoryList =  _delegator.findByAnd("ProductCategoryRollupAndChild", UtilMisc.toMap("parentProductCategoryId",topLavelCategory.getString("productCategoryId")),UtilMisc.toList("sequenceNum"));
                for (GenericValue subLavelCategory : subLavelCategoryList) 
                {
                    if ("CATALOG_CATEGORY".equals(subLavelCategory.getString("productCategoryTypeId"))) 
                    {
                    	category = factory.createCategoryType();
                        String productCategoryId = (String) subLavelCategory.getString("productCategoryId");
            	        List<GenericValue> lCategoryContent = _delegator.findByAnd("ProductCategoryContent", UtilMisc.toMap("productCategoryId",productCategoryId),UtilMisc.toList("-fromDate"));
            	        lCategoryContent=EntityUtil.filterByDate(lCategoryContent, UtilDateTime.nowTimestamp());
            	        category.setCategoryId(productCategoryId);
                        category.setParentCategoryId(subLavelCategory.getString("parentProductCategoryId"));
                        category.setCategoryName(subLavelCategory.getString("categoryName"));
                        if(UtilValidate.isNotEmpty(subLavelCategory.getString("description"))) {
                        	category.setDescription(subLavelCategory.getString("description"));	
                        } else {
                        	category.setDescription("");
                        }
                        
                        if(UtilValidate.isNotEmpty(subLavelCategory.getString("longDescription"))) {
                        	category.setLongDescription(subLavelCategory.getString("longDescription"));	
                        } else {
                        	category.setLongDescription("");
                        }
                        
                        categoryImageURL =subLavelCategory.getString("categoryImageUrl");
                        
                    	PlpImageType plpImage = factory.createPlpImageType();
                    	
                        if (UtilValidate.isNotEmpty(categoryImageURL))
                        {
                        	if (!UtilValidate.isUrl(categoryImageURL))
                        	{
                        		String categoryImagePath = getOsafeImagePath("CATEGORY_IMAGE_URL");
                        		pathElements = StringUtil.split(categoryImageURL, "/");
                                plpImage.setUrl(categoryImagePath + pathElements.get(pathElements.size() - 1));
                        	}
                        	else
                        	{
                        		plpImage.setUrl(categoryImageURL);
                        	}
                            
                        }
                        else
                        {
                        	plpImage.setUrl("");
                        }
                        category.setPlpImage(plpImage);
                        
                        if(UtilValidate.isNotEmpty(getProductCategoryContent(productCategoryId,"PLP_ESPOT_CONTENT",lCategoryContent))) {
                        	category.setAdditionalPlpText(getProductCategoryContent(productCategoryId,"PLP_ESPOT_CONTENT",lCategoryContent));	
                        } else {
                        	category.setAdditionalPlpText("");
                        }
                        
                        if(UtilValidate.isNotEmpty(getProductCategoryContent(productCategoryId,"PDP_ADDITIONAL",lCategoryContent))) {
                        	category.setAdditionalPdpText(getProductCategoryContent(productCategoryId,"PDP_ADDITIONAL",lCategoryContent));
                        } else {
                        	category.setAdditionalPdpText("");
                        }
                        
                        tsstamp = subLavelCategory.getTimestamp("fromDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	category.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	category.setFromDate("");
                        }
                        tsstamp = subLavelCategory.getTimestamp("thruDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	category.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	category.setThruDate("");
                        }
                        productCategoryList.add(category);
                    }
                }
            }
    	
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    	
    }
    public static void createProductCategoryXmlSample(ObjectFactory factory, List productCategoryList) {
    	try {
            CategoryType category = factory.createCategoryType();
        	category.setCategoryId("");
            category.setParentCategoryId("");
            category.setCategoryName("");
            category.setDescription("");
            category.setLongDescription("");
                    
            PlpImageType plpImage = factory.createPlpImageType();
            plpImage.setUrl("");
                    
            category.setPlpImage(plpImage);
                    
            category.setAdditionalPlpText("");
            category.setAdditionalPdpText("");
                    
            category.setFromDate("");
            category.setThruDate("");
            productCategoryList.add(category);
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    	
    }
    public static void createProductXmlSample(ObjectFactory factory, List productList) {
    	try {

    		ProductType productType = factory.createProductType();
    		productType.setMasterProductId("");
    		productType.setProductId("");
    		productType.setInternalName("");
    		productType.setProductName("");
    		productType.setSalesPitch("");
    		productType.setLongDescription("");
    		productType.setSpecialInstructions("");
    		productType.setDeliveryInfo("");
    		productType.setDirections("");
    		productType.setTermsAndConds("");
    		productType.setIngredients("");
    		productType.setWarnings("");
    		productType.setPlpLabel("");
    		productType.setPdpLabel("");
    		productType.setProductHeight("");
    		productType.setProductWidth("");
    		productType.setProductDepth("");
    		productType.setProductWeight("");
    		productType.setReturnable("");
    		productType.setTaxable("");
    		productType.setChargeShipping("");
    		productType.setIntroDate("");
    		productType.setDiscoDate("");
    		productType.setManufacturerId("");
    		
    		ProductPriceType productPrice = factory.createProductPriceType();
            ListPriceType listPrice = factory.createListPriceType();
            listPrice.setPrice("");
            listPrice.setCurrency("");
            listPrice.setFromDate("");
            listPrice.setThruDate("");
            productPrice.setListPrice(listPrice);
            
            SalesPriceType salesPrice = factory.createSalesPriceType();
            salesPrice.setPrice("");
            salesPrice.setCurrency("");
            salesPrice.setFromDate("");
            salesPrice.setThruDate("");
            productPrice.setSalesPrice(salesPrice);
            productType.setProductPrice(productPrice);
            
            ProductCategoryMemberType productCategory = factory.createProductCategoryMemberType();
            List<CategoryMemberType> categoryList = productCategory.getCategory();
            
            CategoryMemberType categoryMember = factory.createCategoryMemberType();
            categoryMember.setCategoryId("");
            categoryMember.setSequenceNum("");
            categoryMember.setFromDate("");
            categoryMember.setThruDate("");
            categoryList.add(categoryMember);
            
            productType.setProductCategoryMember(productCategory);

            ProductSelectableFeatureType selectableFeature = factory.createProductSelectableFeatureType();
            
            List<FeatureType> selectableFeatureList = selectableFeature.getFeature();
            FeatureType selFeature = (FeatureType)factory.createFeatureType();
            selFeature.setFeatureId("");
            List valueSelList = selFeature.getValue();
            valueSelList.add("");
            selFeature.setDescription("");
            selFeature.setFromDate("");
            selFeature.setThruDate("");
            selFeature.setDescription("");
            selFeature.setSequenceNum("");
            selectableFeatureList.add(selFeature);
            productType.setProductSelectableFeature(selectableFeature);
            
            ProductDescriptiveFeatureType descriptiveFeature = factory.createProductDescriptiveFeatureType();
            
            List<FeatureType> descriptiveFeatureList = descriptiveFeature.getFeature();
            FeatureType delFeature = (FeatureType)factory.createFeatureType();
            delFeature.setFeatureId("");
            List valueDesList = delFeature.getValue();
            valueDesList.add("");
            delFeature.setDescription("");
            delFeature.setFromDate("");
            delFeature.setThruDate("");
            delFeature.setDescription("");
            delFeature.setSequenceNum("");
            descriptiveFeatureList.add(delFeature);
            productType.setProductDescriptiveFeature(descriptiveFeature);
            
            ProductImageType productImage = factory.createProductImageType();
            
            PlpSwatchType plpSwatch = factory.createPlpSwatchType();
            plpSwatch.setUrl("");
            plpSwatch.setThruDate("");
            productImage.setPlpSwatch(plpSwatch);
            
            PdpSwatchType pdpSwatch = factory.createPdpSwatchType();
            pdpSwatch.setUrl("");
            pdpSwatch.setThruDate("");
            productImage.setPdpSwatch(pdpSwatch);
            
            PlpSmallImageType plpSmallImage = factory.createPlpSmallImageType();
            plpSmallImage.setUrl("");
            plpSmallImage.setThruDate("");
            productImage.setPlpSmallImage(plpSmallImage);
            
            PlpSmallAltImageType plpSmallAltImage = factory.createPlpSmallAltImageType();
            plpSmallAltImage.setUrl("");
            plpSmallAltImage.setThruDate("");
            productImage.setPlpSmallAltImage(plpSmallAltImage);
            
            PdpThumbnailImageType pdpThumbnailImage = factory.createPdpThumbnailImageType();
            pdpThumbnailImage.setUrl("");
            pdpThumbnailImage.setThruDate("");
            productImage.setPdpThumbnailImage(pdpThumbnailImage);
            
            PdpLargeImageType pdpLargeImage = factory.createPdpLargeImageType();
            pdpLargeImage.setUrl("");
            pdpLargeImage.setThruDate("");
            productImage.setPdpLargeImage(pdpLargeImage);
            
            PdpDetailImageType pdpDetailImage = factory.createPdpDetailImageType();
            pdpDetailImage.setUrl("");
            pdpDetailImage.setThruDate("");
            productImage.setPdpDetailImage(pdpDetailImage);
            
            PdpVideoType pdpVideo = factory.createPdpVideoType();
            pdpVideo.setUrl("");
            pdpVideo.setThruDate("");
            productImage.setPdpVideoImage(pdpVideo);
            
            PdpVideo360Type pdpVideo360 = factory.createPdpVideo360Type();
            pdpVideo360.setUrl("");
            pdpVideo360.setThruDate("");
            productImage.setPdpVideo360Image(pdpVideo360);
            
            PdpAlternateImageType pdpAlternateImage = factory.createPdpAlternateImageType();
            List pdpAdditionalImages = pdpAlternateImage.getPdpAdditionalImage();
            PdpAdditionalImageType pdpAdditionalImage = factory.createPdpAdditionalImageType();
            	   
            PdpAdditionalThumbImageType pdpAdditionalThumbImage = factory.createPdpAdditionalThumbImageType();
            pdpAdditionalThumbImage.setUrl("");
            pdpAdditionalThumbImage.setThruDate("");
            pdpAdditionalImage.setPdpAdditionalThumbImage(pdpAdditionalThumbImage);
            		
            PdpAdditionalLargeImageType pdpAdditionalLargeImage = factory.createPdpAdditionalLargeImageType();
            pdpAdditionalLargeImage.setUrl("");
            pdpAdditionalLargeImage.setThruDate("");
            pdpAdditionalImage.setPdpAdditionalLargeImage(pdpAdditionalLargeImage);
            	    
            PdpAdditionalDetailImageType pdpAdditionalDetailImage = factory.createPdpAdditionalDetailImageType();
            pdpAdditionalDetailImage.setUrl("");
            pdpAdditionalDetailImage.setThruDate("");
            pdpAdditionalImage.setPdpAdditionalDetailImage(pdpAdditionalDetailImage);
            pdpAdditionalImages.add(pdpAdditionalImage);
            productImage.setPdpAlternateImage(pdpAlternateImage);
            productType.setProductImage(productImage);
            
            GoodIdentificationType goodIdentification = factory.createGoodIdentificationType();
            goodIdentification.setSku("");
            goodIdentification.setIsbn("");
            goodIdentification.setGoogleId("");
            goodIdentification.setManuId("");
            productType.setProductGoodIdentification(goodIdentification);
            
            ProductInventoryType productInventory = factory.createProductInventoryType();
            productInventory.setBigfishInventoryTotal("");
            productInventory.setBigfishInventoryWarehouse("");
            productType.setProductInventory(productInventory);
            
            ProductAttributeType productAttribute = factory.createProductAttributeType();
            productAttribute.setPdpSelectMultiVariant("");
            productAttribute.setPdpCheckoutGiftMessage("");
            productAttribute.setPdpQtyMin("");
            productAttribute.setPdpQtyMax("");
            productAttribute.setPdpQtyDefault("");
            productType.setProductAttribute(productAttribute);            
            
            productList.add(productType);
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    	
    }
    
    public static void createProductAssocXmlSample(ObjectFactory factory, List productAssocList) {
    	try {
    		AssociationType productAssoc = factory.createAssociationType();
            productAssoc.setMasterProductId("");
            productAssoc.setMasterProductIdTo("");
            productAssoc.setProductAssocType("");
            productAssoc.setFromDate("");
            productAssoc.setThruDate("");
            productAssocList.add(productAssoc);
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    }
    
    public static void createProductFeatureSwatchSample(ObjectFactory factory, List featureList) {
    	try {
            FeatureSwatchType featureSwatch = factory.createFeatureSwatchType();
            featureSwatch.setFeatureId("");
            featureSwatch.setValue("");
            PlpSwatchType plpSwatch = factory.createPlpSwatchType();
            plpSwatch.setUrl("");
            featureSwatch.setPlpSwatch(plpSwatch);
            PdpSwatchType pdpSwatch = factory.createPdpSwatchType();
            pdpSwatch.setUrl("");
            featureSwatch.setPdpSwatch(pdpSwatch);
            featureList.add(featureSwatch);
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    }
    
    public static void createProductManufacturerSample(ObjectFactory factory, List manufacturerList) {
    	try {
            ManufacturerType manufacturer= factory.createManufacturerType();
            manufacturer.setManufacturerId("");
            manufacturer.setManufacturerName("");
            manufacturer.setDescription("");
            manufacturer.setLongDescription("");
            ManufacturerImageType manufacturerImage = factory.createManufacturerImageType();
            manufacturerImage.setUrl("");
            manufacturerImage.setThruDate("");
            manufacturer.setManufacturerImage(manufacturerImage);
            
            ManufacturerAddressType manufacturerAddress = factory.createManufacturerAddressType();
            manufacturerAddress.setAddress1("");
            manufacturerAddress.setCityTown("");
            manufacturerAddress.setCountry("");
            manufacturerAddress.setStateProvince("");
            manufacturerAddress.setZipPostCode("");
            manufacturer.setAddress(manufacturerAddress);
            
            manufacturerList.add(manufacturer);
            
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    }
    
    public static void createProductXml(ObjectFactory factory, List productList, String browseRootProductCategoryId, String productStoreId) {
    	try {

    		
            List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, true, false, true);
            GenericValue workingCategory = null;
            String productCategoryIdPath = null;
            Timestamp tsstamp=null;
            List<String> pathElements=null;
            String imageURL=null;
            String productPrice="";
            String productId="";
            HashMap productExists = new HashMap();
            ProductType productType = null;
            
            Map productFeatureTypesMap = FastMap.newInstance();
            List<GenericValue> productFeatureTypesList = _delegator.findList("ProductFeatureType", null, null, null, null, false);
            
            //get the whole list of ProductFeatureGroup and ProductFeatureGroupAndAppl
            List productFeatureGroupList = _delegator.findList("ProductFeatureGroup", null, null, null, null, false);
            List productFeatureGroupAndApplList = _delegator.findList("ProductFeatureGroupAndAppl", null, null, null, null, false);
            productFeatureGroupAndApplList = EntityUtil.filterByDate(productFeatureGroupAndApplList);
            
            if(UtilValidate.isNotEmpty(productFeatureTypesList))
            {
                for (GenericValue productFeatureType : productFeatureTypesList)
                {
                    //filter the ProductFeatureGroupAndAppl list based on productFeatureTypeId to get the ProductFeatureGroupId
                	List productFeatureGroupAndAppls = EntityUtil.filterByAnd(productFeatureGroupAndApplList, UtilMisc.toMap("productFeatureTypeId", productFeatureType.getString("productFeatureTypeId")));
                    String description = "";
                    if(UtilValidate.isNotEmpty(productFeatureGroupAndAppls))
                    {
                        GenericValue productFeatureGroupAndAppl = EntityUtil.getFirst(productFeatureGroupAndAppls);
                        List productFeatureGroups = EntityUtil.filterByAnd(productFeatureGroupList, UtilMisc.toMap("productFeatureGroupId", productFeatureGroupAndAppl.getString("productFeatureGroupId")));
                        GenericValue productFeatureGroup = EntityUtil.getFirst(productFeatureGroups);
                        description = productFeatureGroup.getString("description");
                    }
                    else
                    {
                        description = productFeatureType.getString("description");
                    }
                    productFeatureTypesMap.put(productFeatureType.getString("productFeatureTypeId"),description);
                }
                
            }
            
            for (Map<String, Object> workingCategoryMap : productCategories) 
            {
                workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
                List<GenericValue> productCategoryMembers = workingCategory.getRelated("ProductCategoryMember");
                // Remove any expired
                productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                for (GenericValue productCategoryMember : productCategoryMembers) 
                {
                    GenericValue product = productCategoryMember.getRelatedOne("Product");
                    productId = product.getString("productId");
                    
                    if (UtilValidate.isNotEmpty(product) && !productExists.containsKey(productId))
                    {
                    	
                    	productExists.put(productId, productId);
                    	
                    	String isVariant = product.getString("isVariant");
                        if (UtilValidate.isEmpty(isVariant)) {
                            isVariant = "N";
                        }
                        if ("N".equals(isVariant)) 
                        {
                        	List<GenericValue> productAssocitations = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"),UtilMisc.toList("sequenceNum"));
                	        if (UtilValidate.isNotEmpty(productAssocitations))
                	        {
                	        	productType = factory.createProductType();
                            	addXmlProductRow(factory, productType, product, productFeatureTypesMap);
                            	productList.add(productType);
                            	
                	        	boolean bFirstVariant=false;
                                for (GenericValue productAssoc : productAssocitations) 
                                {
                                	productType = factory.createProductType();
                                	
                                    GenericValue variantProduct = productAssoc.getRelatedOne("AssocProduct");
                                	if (!bFirstVariant)
                                	{
                                		addXmlProductVariantRow(factory, productType,variantProduct,productId,bFirstVariant, productFeatureTypesMap);
                                		productList.add(productType);
                                	}
                                	
                                }
                	        	
                	        }
                	        else
                	        {
                	        	productType = factory.createProductType();
                	        	addXmlProductRow(factory, productType, product, productFeatureTypesMap);
                	        	productList.add(productType);
                	        }
                        }
                    	
            	        
                    	
                    }
                }
            }
    		
    	
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    	
    }
    
    public static void createProductAssocXml(ObjectFactory factory, List productAssocList, String browseRootProductCategoryId, String productStoreId) {
    	try {List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, true, false, true);
        GenericValue workingCategory = null;
        int iColIdx=0;
        Timestamp tsstamp=null;
        String productId="";
        HashMap productExists = new HashMap();
        AssociationType productAssocType = null;
        for (Map<String, Object> workingCategoryMap : productCategories) 
        {
            workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
            List<GenericValue> productCategoryMembers = workingCategory.getRelated("ProductCategoryMember");
            // Remove any expired
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
            for (GenericValue productCategoryMember : productCategoryMembers) 
            {
                GenericValue product = productCategoryMember.getRelatedOne("Product");
                productId = product.getString("productId");
                if (UtilValidate.isNotEmpty(product) && !productExists.containsKey(productId))
                {
                	productExists.put(productId, productId);
                	
                	List<GenericValue> productAssocitations = product.getRelated("MainProductAssoc");
        	        if(UtilValidate.isNotEmpty(productAssocitations)) {
        	            productAssocitations = EntityUtil.filterByDate(productAssocitations, true);
        	        }
        	        List<GenericValue> complementProductAssoc = FastList.newInstance();
        	        if(UtilValidate.isNotEmpty(productAssocitations)) {
        	        	complementProductAssoc = EntityUtil.filterByAnd(productAssocitations, UtilMisc.toMap("productAssocTypeId", "PRODUCT_COMPLEMENT"));
        	        	complementProductAssoc = EntityUtil.orderBy(complementProductAssoc,UtilMisc.toList("sequenceNum"));
        	        }
        	        
                    for (GenericValue productAssoc : complementProductAssoc) 
                    {
                    	productAssocType = factory.createAssociationType();
                    	productAssocType.setMasterProductId(productId);
                    	productAssocType.setMasterProductIdTo(productAssoc.getString("productIdTo"));
                    	productAssocType.setProductAssocType("COMPLEMENT");
                    	
                    	tsstamp = productAssoc.getTimestamp("fromDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	productAssocType.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	productAssocType.setFromDate("");
                        }
                        tsstamp = productAssoc.getTimestamp("thruDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	productAssocType.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	productAssocType.setThruDate("");
                        }
                        productAssocList.add(productAssocType);
                    }
                    
                    List<GenericValue> accessoryProductAssoc = FastList.newInstance();
        	        if(UtilValidate.isNotEmpty(productAssocitations)) {
        	        	accessoryProductAssoc = EntityUtil.filterByAnd(productAssocitations, UtilMisc.toMap("productAssocTypeId", "PRODUCT_ACCESSORY"));
        	        	accessoryProductAssoc = EntityUtil.orderBy(accessoryProductAssoc,UtilMisc.toList("sequenceNum"));
        	        }
        	        
                    for (GenericValue productAssoc : accessoryProductAssoc) 
                    {
                    	productAssocType = factory.createAssociationType();
                    	productAssocType.setMasterProductId(productId);
                    	productAssocType.setMasterProductIdTo(productAssoc.getString("productIdTo"));
                    	productAssocType.setProductAssocType("ACCESSORY");
                    	
                    	tsstamp = productAssoc.getTimestamp("fromDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	productAssocType.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	productAssocType.setFromDate("");
                        }
                        tsstamp = productAssoc.getTimestamp("thruDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	productAssocType.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	productAssocType.setThruDate("");
                        }
                        productAssocList.add(productAssocType);
                    }
                }
            }
        } 
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    }
    
    
    public static void createProductFeatureSwatchXml(ObjectFactory factory, List featureList, String browseRootProductCategoryId, String productStoreId) {
    	try {
    		
    	    FeatureSwatchType featureSwatchType = null;
    	        List<GenericValue> lProductFeatureDataResource = _delegator.findByAnd("ProductFeatureDataResource", UtilMisc.toMap("featureDataResourceTypeId","PLP_SWATCH_IMAGE_URL"),UtilMisc.toList("dataResourceId"));
    	        
    	        Map mFeatureRow = FastMap.newInstance();
    	        for (GenericValue productFeatureDataResource : lProductFeatureDataResource) 
    	        {
    	        	featureSwatchType = factory.createFeatureSwatchType();
    	        	GenericValue productFeature = (GenericValue) productFeatureDataResource.getRelatedOne("ProductFeature");
    	        	GenericValue dataResource  = (GenericValue) productFeatureDataResource.getRelatedOne("DataResource");
    	        	String productFeatureId = productFeature.getString("productFeatureId");
    	        	String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
    	        	String productFeatureDescription = productFeature.getString("description");
    	        	String dataResourceName = dataResource.getString("dataResourceName"); 
    	            
    	            featureSwatchType.setFeatureId(productFeatureTypeId);
    	            featureSwatchType.setValue(productFeatureDescription);
    	            PlpSwatchType plpSwatchType = factory.createPlpSwatchType();
    	            if (!UtilValidate.isUrl(dataResourceName))
                	{
    	            	String featurePLPSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
    	            	plpSwatchType.setUrl(featurePLPSwatchImagePath + dataResourceName);	
                	}
    	            else
    	            {
    	            	plpSwatchType.setUrl(dataResourceName);
    	            }
    	            
    	            featureSwatchType.setPlpSwatch(plpSwatchType);
    	            
    	            mFeatureRow.put(productFeatureId, featureSwatchType);
    	            
    	            featureList.add(featureSwatchType);
    	        }
    	        lProductFeatureDataResource = _delegator.findByAnd("ProductFeatureDataResource", UtilMisc.toMap("featureDataResourceTypeId","PDP_SWATCH_IMAGE_URL"),UtilMisc.toList("dataResourceId"));
    	        String featurePDPSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
    	        for (GenericValue productFeatureDataResource : lProductFeatureDataResource) 
    	        {
    	        	GenericValue productFeature = (GenericValue) productFeatureDataResource.getRelatedOne("ProductFeature");
    	        	GenericValue dataResource  = (GenericValue) productFeatureDataResource.getRelatedOne("DataResource");
    	        	String productFeatureId = productFeature.getString("productFeatureId");
    	        	String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
    	        	String productFeatureDescription = productFeature.getString("description");
    	        	String dataResourceName = dataResource.getString("dataResourceName");
    	        	FeatureSwatchType featureSwatchTypePdp = (FeatureSwatchType) mFeatureRow.get(productFeatureId);
    	        	PdpSwatchType pdpSwatchType = factory.createPdpSwatchType();
    	        	
    	        	if (UtilValidate.isNotEmpty(featureSwatchTypePdp))
    	        	{
    	        		if (!UtilValidate.isUrl(dataResourceName))
                    	{
    	        			pdpSwatchType.setUrl(featurePDPSwatchImagePath + dataResourceName);	
                    	}
    	        		else
    	        		{
    	        			pdpSwatchType.setUrl(dataResourceName);
    	        		}
    	        		
        	        	featureSwatchTypePdp.setPdpSwatch(pdpSwatchType);
        	        	//featureList.add(featureSwatchTypePdp);
    	        	}
    	        	else
    	        	{
    	        		featureSwatchType = factory.createFeatureSwatchType();
    	        		featureSwatchType.setFeatureId(productFeatureTypeId);
        	            featureSwatchType.setValue(productFeatureDescription);
        	            if (!UtilValidate.isUrl(dataResourceName))
                    	{
    	        			pdpSwatchType.setUrl(featurePDPSwatchImagePath + dataResourceName);	
                    	}
    	        		else
    	        		{
    	        			pdpSwatchType.setUrl(dataResourceName);
    	        		}
        	            featureSwatchType.setPdpSwatch(pdpSwatchType);
        	            featureList.add(featureSwatchType);
    	        	}
    	        }
    			
    		} catch (Exception e) 
    		{
    	        Debug.logError(e, module);
    			
    		}
        
    }

    public static void createProductManufacturerXml(ObjectFactory factory, List manufacturerList, String browseRootProductCategoryId, String productStoreId) {
    try {
		
        List<GenericValue> partyManufacturers = _delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId","MANUFACTURER"),UtilMisc.toList("partyId"));
        GenericValue party = null;
        String partyId=null;
        GenericValue partyGroup = null;
        GenericValue partyContactMechPurpose = null;
        String imageURL=null;
        List<String> pathElements=null;
        
        ManufacturerType manufacturerType = null;
        
        for (GenericValue partyManufacturer : partyManufacturers) 
        {
        	manufacturerType = factory.createManufacturerType();
        	party = (GenericValue) partyManufacturer.getRelatedOne("Party");
        	partyId=party.getString("partyId");
	        List<GenericValue> lPartyContent = _delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",partyId),UtilMisc.toList("-fromDate"));
	        lPartyContent=EntityUtil.filterByDate(lPartyContent,UtilDateTime.nowTimestamp());
        	manufacturerType.setManufacturerId(partyId);
        	if(UtilValidate.isNotEmpty(getPartyContent(partyId,"PROFILE_NAME",lPartyContent))) {
        		manufacturerType.setManufacturerName(getPartyContent(partyId,"PROFILE_NAME",lPartyContent));	
        	} else {
        		manufacturerType.setManufacturerName("");
        	}
        	
        	if(UtilValidate.isNotEmpty(getPartyContent(partyId,"DESCRIPTION",lPartyContent))) {
        		manufacturerType.setDescription(getPartyContent(partyId,"DESCRIPTION",lPartyContent));	
        	} else {
        		manufacturerType.setDescription("");
        	}
        	
        	if(UtilValidate.isNotEmpty(getPartyContent(partyId,"LONG_DESCRIPTION",lPartyContent))) {
        		manufacturerType.setLongDescription(getPartyContent(partyId,"LONG_DESCRIPTION",lPartyContent));	
        	} else {
        		manufacturerType.setLongDescription("");
        	}
        	
        	ManufacturerImageType manufacturerImage = factory.createManufacturerImageType();
        	
        	imageURL =getPartyContent(partyId,"PROFILE_IMAGE_URL",lPartyContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String profileImagePath = getOsafeImagePath("PROFILE_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    manufacturerImage.setUrl(profileImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		manufacturerImage.setUrl(imageURL);
            	}
                
                manufacturerImage.setThruDate(getPartyContentThruDate(partyId,"PROFILE_IMAGE_URL",lPartyContent));
            }
            else
            {
            	manufacturerImage.setUrl("");
            	manufacturerImage.setThruDate("");
            }
            manufacturerType.setManufacturerImage(manufacturerImage);
        	
            Collection<GenericValue> partyContactMechPurposes = ContactHelper.getContactMechByPurpose(party,"GENERAL_LOCATION",false);
            Iterator<GenericValue> partyContactMechPurposesIterator = partyContactMechPurposes.iterator();
            while (partyContactMechPurposesIterator.hasNext()) 
            {
            	partyContactMechPurpose = (GenericValue) partyContactMechPurposesIterator.next();
            }
            ManufacturerAddressType manufacturerAddress = null;
            if (UtilValidate.isNotEmpty(partyContactMechPurpose))
            {
            	manufacturerAddress = factory.createManufacturerAddressType();
            	GenericValue postalAddress = partyContactMechPurpose.getRelatedOne("PostalAddress");
            	String address=postalAddress.getString("address1");
            	String city=postalAddress.getString("city");
            	String state=postalAddress.getString("stateProvinceGeoId");
            	String zip=postalAddress.getString("postalCode");
            	String country=postalAddress.getString("countryGeoId");
                if (UtilValidate.isNotEmpty(address))
                {
                    manufacturerAddress.setAddress1(address);
                }
                else
                {
                	manufacturerAddress.setAddress1("");
                }
                if (UtilValidate.isNotEmpty(city))
                {
                	manufacturerAddress.setCityTown(city);
                }
                else
                {
                	manufacturerAddress.setCityTown("");
                }
                if (UtilValidate.isNotEmpty(state))
                {
                	manufacturerAddress.setStateProvince(state);
                }
                else
                {
                	manufacturerAddress.setStateProvince("");
                }
                if (UtilValidate.isNotEmpty(zip))
                {
                	manufacturerAddress.setZipPostCode(zip);
                }
                else
                {
                	manufacturerAddress.setZipPostCode("");
                }
                if (UtilValidate.isNotEmpty(country))
                {
                	manufacturerAddress.setCountry(country);
                }
                else
                {
                	manufacturerAddress.setCountry("");
                }
            }
            else
            {
            	manufacturerAddress.setAddress1("");
            	manufacturerAddress.setCityTown("");
            	manufacturerAddress.setStateProvince("");
            	manufacturerAddress.setZipPostCode("");
            	manufacturerAddress.setCountry("");
            }
            manufacturerType.setAddress(manufacturerAddress);
            manufacturerList.add(manufacturerType);
        }
		
	} catch (Exception e) 
	{
        Debug.logError(e, module);
		
	}
    }
    
    private static void addXmlProductRow(ObjectFactory factory, ProductType productType,GenericValue product, Map productFeatureTypesMap) {
    	List<String> pathElements=null;
    	String imageURL=null;
    	
    	try {
    		String productId = product.getString("productId");
    		
    		productType.setMasterProductId(productId);
    		
    		if(product.getString("isVirtual").equals("Y")) 
        	{
    			productType.setProductId(productId);	
        	}
        	else
        	{
        		productType.setProductId("");
        	}
    		ProductCategoryMemberType productCategoryMemberType = factory.createProductCategoryMemberType();
    		List<GenericValue> categoryMembers = product.getRelated("ProductCategoryMember");
            if(UtilValidate.isNotEmpty(categoryMembers))
            {
                categoryMembers = EntityUtil.filterByDate(categoryMembers, true);
            }
            if(UtilValidate.isNotEmpty(categoryMembers))
            {
            	StringBuffer catMembers =new StringBuffer();
            	
            	List categoryMemberList = productCategoryMemberType.getCategory();
            	CategoryMemberType categoryMemberType = null;
                for (GenericValue categoryMember : categoryMembers) 
                {
                	categoryMemberType = factory.createCategoryMemberType();
                	categoryMemberType.setCategoryId(categoryMember.getString("productCategoryId"));
                	
                	if(UtilValidate.isNotEmpty(categoryMember.getString("sequenceNum"))) {
                		categoryMemberType.setSequenceNum(categoryMember.getString("sequenceNum"));
                	} else {
                		categoryMemberType.setSequenceNum("");
                	}
                	Timestamp tsstamp = categoryMember.getTimestamp("fromDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	categoryMemberType.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	categoryMemberType.setFromDate("");
                    }
                    tsstamp = categoryMember.getTimestamp("thruDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	categoryMemberType.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	categoryMemberType.setThruDate("");
                    }
                    categoryMemberList.add(categoryMemberType);
                }
            }
            else
            {
            	// TODO
            }
            productType.setProductCategoryMember(productCategoryMemberType);
            
            List<GenericValue> lProductContent = _delegator.findByAnd("ProductContent", UtilMisc.toMap("productId",productId),UtilMisc.toList("-fromDate"));
            lProductContent=EntityUtil.filterByDate(lProductContent, UtilDateTime.nowTimestamp());
            
            if(UtilValidate.isNotEmpty(product.getString("internalName"))) 
            {
            	productType.setInternalName(product.getString("internalName"));
            } 
            else 
            {
            	productType.setInternalName("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"PRODUCT_NAME",lProductContent))) 
            {
            	productType.setProductName(getProductContent(productId,"PRODUCT_NAME",lProductContent));
            } 
            else 
            {
            	productType.setProductName("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"SHORT_SALES_PITCH",lProductContent))) 
            {
            	productType.setSalesPitch(getProductContent(productId,"SHORT_SALES_PITCH",lProductContent));
            } 
            else 
            {
            	productType.setSalesPitch("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"LONG_DESCRIPTION",lProductContent))) 
            {
            	productType.setLongDescription(getProductContent(productId,"LONG_DESCRIPTION",lProductContent));
            } 
            else 
            {
            	productType.setLongDescription("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"SPECIALINSTRUCTIONS",lProductContent))) 
            {
            	productType.setSpecialInstructions(getProductContent(productId,"SPECIALINSTRUCTIONS",lProductContent));
            } 
            else 
            {
            	productType.setSpecialInstructions("");
            }

            if(UtilValidate.isNotEmpty(getProductContent(productId,"DELIVERY_INFO",lProductContent))) 
            {
            	productType.setDeliveryInfo(getProductContent(productId,"DELIVERY_INFO",lProductContent));
            } 
            else 
            {
            	productType.setDeliveryInfo("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"DIRECTIONS",lProductContent))) 
            {
            	productType.setDirections(getProductContent(productId,"DIRECTIONS",lProductContent));
            } 
            else 
            {
            	productType.setDirections("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"TERMS_AND_CONDS",lProductContent))) 
            {
            	productType.setTermsAndConds(getProductContent(productId,"TERMS_AND_CONDS",lProductContent));
            } 
            else 
            {
            	productType.setTermsAndConds("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"INGREDIENTS",lProductContent))) 
            {
            	productType.setIngredients(getProductContent(productId,"INGREDIENTS",lProductContent));
            } 
            else 
            {
            	productType.setIngredients("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"WARNINGS",lProductContent))) 
            {
            	productType.setWarnings(getProductContent(productId,"WARNINGS",lProductContent));
            } 
            else 
            {
            	productType.setWarnings("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"PLP_LABEL",lProductContent))) 
            {
            	productType.setPlpLabel(getProductContent(productId,"PLP_LABEL",lProductContent));
            } 
            else 
            {
            	productType.setPlpLabel("");
            }
            
            if(UtilValidate.isNotEmpty(getProductContent(productId,"PDP_LABEL",lProductContent))) 
            {
            	productType.setPdpLabel(getProductContent(productId,"PDP_LABEL",lProductContent));
            } 
            else 
            {
            	productType.setPdpLabel("");
            }

            if(UtilValidate.isNotEmpty(product.getBigDecimal("productHeight"))) 
            {
            	productType.setProductHeight(_df.format(product.getBigDecimal("productHeight")));
            } 
            else 
            {
            	productType.setProductHeight("");
            }
            
            if(UtilValidate.isNotEmpty(product.getBigDecimal("productWidth"))) 
            {
            	productType.setProductWidth(_df.format(product.getBigDecimal("productWidth")));
            } 
            else 
            {
            	productType.setProductWidth("");
            }
            
            if(UtilValidate.isNotEmpty(product.getBigDecimal("productDepth"))) 
            {
            	productType.setProductDepth(_df.format(product.getBigDecimal("productDepth")));
            } 
            else 
            {
            	productType.setProductDepth("");
            }
            
            if(UtilValidate.isNotEmpty(product.getBigDecimal("weight"))) 
            {
            	productType.setProductWeight(_df.format(product.getBigDecimal("weight")));
            } 
            else 
            {
            	productType.setProductWeight("");
            }
            
            if(UtilValidate.isNotEmpty(product.getString("returnable"))) 
            {
            	productType.setReturnable(product.getString("returnable"));
            } 
            else 
            {
            	productType.setReturnable("");
            }
            
            if(UtilValidate.isNotEmpty(product.getString("taxable")))
            {
            	productType.setTaxable(product.getString("taxable"));
            } 
            else 
            {
            	productType.setTaxable("");
            }
            
            if(UtilValidate.isNotEmpty(product.getString("chargeShipping"))) {
            	productType.setChargeShipping(product.getString("chargeShipping"));
            } else {
            	productType.setChargeShipping("");
            }
            
        	Timestamp tsstampProdDate = product.getTimestamp("introductionDate");
            if (UtilValidate.isNotEmpty(tsstampProdDate))
            {
            	productType.setIntroDate(_sdf.format(new Date(tsstampProdDate.getTime())));
            }
            else
            {
            	productType.setIntroDate("");
            }
            tsstampProdDate = product.getTimestamp("salesDiscontinuationDate");
            if (UtilValidate.isNotEmpty(tsstampProdDate))
            {
            	productType.setDiscoDate(_sdf.format(new Date(tsstampProdDate.getTime())));
            }
            else
            {
            	productType.setDiscoDate("");
            }
            
            if(UtilValidate.isNotEmpty(product.getString("manufacturerPartyId"))) {
            	productType.setManufacturerId(product.getString("manufacturerPartyId"));
            } else {
            	productType.setManufacturerId("");
            }
            String productPrice="";
            ProductPriceType productPriceType = factory.createProductPriceType();
            List<GenericValue> productPriceList = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", "LIST_PRICE"), UtilMisc.toList("-fromDate"));
            if(UtilValidate.isNotEmpty(productPriceList))
            {
            	ListPriceType listPrice = factory.createListPriceType();
            	productPriceList = EntityUtil.filterByDate(productPriceList);
    	        if(UtilValidate.isNotEmpty(productPriceList))
    	        { 
    	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
    	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
    	        	listPrice.setPrice(productPrice);
    	        	listPrice.setCurrency(gvProductPrice.getString("currencyUomId"));
    	        	Timestamp tsstamp = gvProductPrice.getTimestamp("fromDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	listPrice.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	listPrice.setFromDate("");
                    }
                    tsstamp = gvProductPrice.getTimestamp("thruDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	listPrice.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	listPrice.setThruDate("");
                    }
                    productPriceType.setListPrice(listPrice);
    	        }
    	    }

            productPrice="";
            productPriceList = _delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",productId, "productPriceTypeId", "DEFAULT_PRICE"), UtilMisc.toList("-fromDate"));
            if(UtilValidate.isNotEmpty(productPriceList))
            {
            	SalesPriceType salesPrice = factory.createSalesPriceType();
            	productPriceList = EntityUtil.filterByDate(productPriceList);
    	        if(UtilValidate.isNotEmpty(productPriceList))
    	        {
    	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
    	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
    	        	salesPrice.setPrice(productPrice);
    	        	salesPrice.setCurrency(gvProductPrice.getString("currencyUomId"));
    	        	Timestamp tsstamp = gvProductPrice.getTimestamp("fromDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	salesPrice.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	salesPrice.setFromDate("");
                    }
                    tsstamp = gvProductPrice.getTimestamp("thruDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	salesPrice.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                    }
                    else
                    {
                    	salesPrice.setThruDate("");
                    }
                    productPriceType.setSalesPrice(salesPrice);
    	        }
    	    }
            productType.setProductPrice(productPriceType);

            String featureProductId = productId;
            //If any virtual have variants then reterive the Features from first variant
            List<GenericValue> productAssocitations = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"),UtilMisc.toList("sequenceNum"));
            GenericValue variantProduct = null;
	        if (UtilValidate.isNotEmpty(productAssocitations))
	        {
	        	GenericValue productAssoc = EntityUtil.getFirst(productAssocitations);
	        	variantProduct = productAssoc.getRelatedOne("AssocProduct");
	        	featureProductId = variantProduct.getString("productId");
	        }
	        
            ProductSelectableFeatureType selectableFeature = factory.createProductSelectableFeatureType();
            
            List<FeatureType> selectableFeatureList = selectableFeature.getFeature();
            FeatureType selFeature = (FeatureType)factory.createFeatureType();
            selFeature.setFeatureId("");
            List valueSelList = selFeature.getValue();
            valueSelList.add("");
            selFeature.setDescription("");
            selFeature.setFromDate("");
            selFeature.setThruDate("");
            selFeature.setDescription("");
            selFeature.setSequenceNum("");
            selectableFeatureList.add(selFeature);
            productType.setProductSelectableFeature(selectableFeature);
            
	        /*List<GenericValue> productSelectableFeatures = FastList.newInstance();
	        if(UtilValidate.isNotEmpty(featureProductId)) {
	        	productSelectableFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", featureProductId, "productFeatureApplTypeId", "STANDARD_FEATURE"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
	            productSelectableFeatures = EntityUtil.filterByDate(productSelectableFeatures);
	        }
                        
            ProductSelectableFeatureType productSelectableFeatureType = factory.createProductSelectableFeatureType();
            List selectableFeaturesList = productSelectableFeatureType.getFeature();
            FeatureType selectableFeature = null;
            for (GenericValue productSelectableFeature : productSelectableFeatures) 
            {   
            	String productFeatureTypeDesc = (String) productFeatureTypesMap.get(productSelectableFeature.getString("productFeatureTypeId"));
        		if(UtilValidate.isEmpty(productFeatureTypeDesc))
        		{
        			productFeatureTypeDesc = productSelectableFeature.getString("productFeatureTypeId");
        		}
            	selectableFeature = factory.createFeatureType();
            	selectableFeature.setFeatureId(productSelectableFeature.getString("productFeatureTypeId"));
            	List valueList = selectableFeature.getValue();
            	valueList.add(productSelectableFeature.getString("description"));
            	selectableFeature.setDescription(productFeatureTypeDesc);
            	Timestamp tsstamp = productSelectableFeature.getTimestamp("fromDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	selectableFeature.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	selectableFeature.setFromDate("");
                }
                tsstamp = productSelectableFeature.getTimestamp("thruDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	selectableFeature.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	selectableFeature.setThruDate("");
                }
                
                if(UtilValidate.isNotEmpty(productSelectableFeature.getString("sequenceNum"))) {
                	selectableFeature.setSequenceNum(productSelectableFeature.getString("sequenceNum"));
            	} else {
            		selectableFeature.setSequenceNum("");
            	}
                
                selectableFeaturesList.add(selectableFeature);
            }
            productType.setProductSelectableFeature(productSelectableFeatureType);*/
            
            List<GenericValue> productDistinguishFeatures = FastList.newInstance();
            if(UtilValidate.isNotEmpty(productId)) 
            {
            	productDistinguishFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
    	        productDistinguishFeatures = EntityUtil.filterByDate(productDistinguishFeatures);	
            }
            
            ProductDescriptiveFeatureType productDescriptiveFeatureType = factory.createProductDescriptiveFeatureType();
            List descriptiveFeaturesList = productDescriptiveFeatureType.getFeature();
            FeatureType descriptiveFeature = null;
            for (GenericValue productDistinguishFeature : productDistinguishFeatures) 
            {   
            	String productFeatureTypeDesc = (String) productFeatureTypesMap.get(productDistinguishFeature.getString("productFeatureTypeId"));
        		if(UtilValidate.isEmpty(productFeatureTypeDesc))
        		{
        			productFeatureTypeDesc = productDistinguishFeature.getString("productFeatureTypeId");
        		}
            	descriptiveFeature = factory.createFeatureType();
            	descriptiveFeature.setFeatureId(productDistinguishFeature.getString("productFeatureTypeId"));
            	List valueList = descriptiveFeature.getValue();
            	valueList.add(productDistinguishFeature.getString("description"));
            	descriptiveFeature.setDescription(productFeatureTypeDesc);
            	Timestamp tsstamp = productDistinguishFeature.getTimestamp("fromDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	descriptiveFeature.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	descriptiveFeature.setFromDate("");
                }
                tsstamp = productDistinguishFeature.getTimestamp("thruDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	descriptiveFeature.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	descriptiveFeature.setThruDate("");
                }
                
                if(UtilValidate.isNotEmpty(productDistinguishFeature.getString("sequenceNum"))) {
                	descriptiveFeature.setSequenceNum(productDistinguishFeature.getString("sequenceNum"));
            	} else {
            		descriptiveFeature.setSequenceNum("");
            	}
                descriptiveFeaturesList.add(descriptiveFeature);
            }
            productType.setProductDescriptiveFeature(productDescriptiveFeatureType);
            
            //iColIdx=createWorkBookProductFeatures(excelSheet,productDistinguishFeatures,iColIdx,iRowIdx);
            
            ProductImageType productImage = factory.createProductImageType();
            
            PlpSwatchType plpSwatch = factory.createPlpSwatchType();
            imageURL =getProductContent(productId,"PLP_SWATCH_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String plpSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    plpSwatch.setUrl(plpSwatchImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		plpSwatch.setUrl(imageURL);
            	}
                
            }
            else
            {
            	plpSwatch.setUrl("");
            }
            productImage.setPlpSwatch(plpSwatch);
            
            PdpSwatchType pdpSwatch = factory.createPdpSwatchType();
            imageURL =getProductContent(productId,"PDP_SWATCH_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
                    pathElements = StringUtil.split(imageURL, "/");
                    pdpSwatch.setUrl(pdpSwatchImagePath + pathElements.get(pathElements.size() - 1));
            	}
            	else
            	{
            		pdpSwatch.setUrl(imageURL);
            	}
            }
            else
            {
            	pdpSwatch.setUrl("");
            }
            productImage.setPdpSwatch(pdpSwatch);
            
            PlpSmallImageType plpSmallImage = factory.createPlpSmallImageType();
            imageURL =getProductContent(productId,"SMALL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String smallImagePath = getOsafeImagePath("SMALL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    plpSmallImage.setUrl(smallImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		plpSmallImage.setUrl(imageURL);
            	}
                
                plpSmallImage.setThruDate(getProductContentThruDate(productId,"SMALL_IMAGE_URL",lProductContent));
            }
            else
            {
            	plpSmallImage.setUrl("");
            	plpSmallImage.setThruDate("");
            }
            productImage.setPlpSmallImage(plpSmallImage);
            
            PlpSmallAltImageType plpSmallAltImage = factory.createPlpSmallAltImageType();
            imageURL =getProductContent(productId,"SMALL_IMAGE_ALT_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String smallAltImagePath = getOsafeImagePath("SMALL_IMAGE_ALT_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    plpSmallAltImage.setUrl(smallAltImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		plpSmallAltImage.setUrl(imageURL);
            	}
                
                plpSmallAltImage.setThruDate(getProductContentThruDate(productId,"SMALL_IMAGE_ALT_URL",lProductContent));
            }
            else
            {
            	plpSmallAltImage.setUrl("");
            	plpSmallAltImage.setThruDate("");
            }
            productImage.setPlpSmallAltImage(plpSmallAltImage);
            
            PdpThumbnailImageType pdpThumbnailImage = factory.createPdpThumbnailImageType();
            imageURL =getProductContent(productId,"THUMBNAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String thumbnailImagePath = getOsafeImagePath("THUMBNAIL_IMAGE_URL");
                    pathElements = StringUtil.split(imageURL, "/");
                    pdpThumbnailImage.setUrl(thumbnailImagePath + pathElements.get(pathElements.size() - 1));
            	}
            	else
            	{
            		pdpThumbnailImage.setUrl(imageURL);
            	}
                pdpThumbnailImage.setThruDate(getProductContentThruDate(productId,"THUMBNAIL_IMAGE_URL",lProductContent));
            }
            else
            {
            	pdpThumbnailImage.setUrl("");
            	pdpThumbnailImage.setThruDate("");
            }
            productImage.setPdpThumbnailImage(pdpThumbnailImage);
            
            PdpLargeImageType pdpLargeImage = factory.createPdpLargeImageType();
            imageURL =getProductContent(productId,"LARGE_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String largeImagePath = getOsafeImagePath("LARGE_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpLargeImage.setUrl(largeImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpLargeImage.setUrl(imageURL);
            	}
                
                pdpLargeImage.setThruDate(getProductContentThruDate(productId,"LARGE_IMAGE_URL",lProductContent));
            }
            else
            {
            	pdpLargeImage.setUrl("");
            	pdpLargeImage.setThruDate("");
            }
            productImage.setPdpLargeImage(pdpLargeImage);
            
            PdpDetailImageType pdpDetailImage = factory.createPdpDetailImageType();
            imageURL =getProductContent(productId,"DETAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String detailImagePath = getOsafeImagePath("DETAIL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpDetailImage.setUrl(detailImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpDetailImage.setUrl(imageURL);
            	}
                
                pdpDetailImage.setThruDate(getProductContentThruDate(productId,"DETAIL_IMAGE_URL",lProductContent));
            }
            else
            {
            	pdpDetailImage.setUrl("");
            	pdpDetailImage.setThruDate("");
            }
            productImage.setPdpDetailImage(pdpDetailImage);
            
            PdpVideoType pdpVideo = factory.createPdpVideoType();
            imageURL =getProductContent(productId,"PDP_VIDEO_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideoUrlPath = getOsafeImagePath("PDP_VIDEO_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpVideo.setUrl(pdpVideoUrlPath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpVideo.setUrl(imageURL);
            	}
                
                pdpVideo.setThruDate(getProductContentThruDate(productId,"PDP_VIDEO_URL",lProductContent));
            }
            else
            {
            	pdpVideo.setUrl("");
            	pdpVideo.setThruDate("");
            }
            productImage.setPdpVideoImage(pdpVideo);

            PdpVideo360Type pdpVideo360 = factory.createPdpVideo360Type();
            imageURL =getProductContent(productId,"PDP_VIDEO_360_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideo360UrlPath = getOsafeImagePath("PDP_VIDEO_360_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpVideo360.setUrl(pdpVideo360UrlPath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpVideo360.setUrl(imageURL);
            	}
                pdpVideo360.setThruDate(getProductContentThruDate(productId,"PDP_VIDEO_360_URL",lProductContent));
            }
            else
            {
            	pdpVideo360.setUrl("");
            	pdpVideo360.setThruDate("");
            }
            productImage.setPdpVideo360Image(pdpVideo360);
            
            //Set Product Alternamte Images
            PdpAlternateImageType pdpAlternateImage = factory.createPdpAlternateImageType();
            List pdpAdditionalImages = pdpAlternateImage.getPdpAdditionalImage();
            PdpAdditionalImageType pdpAdditionalImage = null;
            for (int i=1; i < 11; i++)
            {
            	pdpAdditionalImage = factory.createPdpAdditionalImageType();
            	
            	PdpAdditionalThumbImageType pdpAddtionalThumbImage = factory.createPdpAdditionalThumbImageType();
                imageURL =getProductContent(productId,"ADDITIONAL_IMAGE_" + i,lProductContent);
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalImagePath = getOsafeImagePath("ADDITIONAL_IMAGE_" + i);
                		pathElements = StringUtil.split(imageURL, "/");
                        pdpAddtionalThumbImage.setUrl(additionalImagePath + pathElements.get(pathElements.size() - 1));
                	}
                	else
                	{
                		pdpAddtionalThumbImage.setUrl(imageURL);
                	}
                    pdpAddtionalThumbImage.setThruDate(getProductContentThruDate(productId,"ADDITIONAL_IMAGE_" + i,lProductContent));
                }
                else
                {
                	pdpAddtionalThumbImage.setUrl("");
                	pdpAddtionalThumbImage.setThruDate("");
                }
                pdpAdditionalImage.setPdpAdditionalThumbImage(pdpAddtionalThumbImage);
                
                PdpAdditionalLargeImageType pdpAdditionalLargeImage = factory.createPdpAdditionalLargeImageType();
                imageURL =getProductContent(productId,"XTRA_IMG_" + i +"_LARGE",lProductContent);
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalLargeImagePath = getOsafeImagePath("XTRA_IMG_" + i +"_LARGE");
                		pathElements = StringUtil.split(imageURL, "/");
                        pdpAdditionalLargeImage.setUrl(additionalLargeImagePath + pathElements.get(pathElements.size() - 1));
                	}
                	else
                	{
                		pdpAdditionalLargeImage.setUrl(imageURL);
                	}
                    pdpAdditionalLargeImage.setThruDate(getProductContentThruDate(productId,"XTRA_IMG_" + i +"_LARGE",lProductContent));
                }
                else
                {
                	pdpAdditionalLargeImage.setUrl("");
                	pdpAdditionalLargeImage.setThruDate("");
                }
                pdpAdditionalImage.setPdpAdditionalLargeImage(pdpAdditionalLargeImage);
                
                PdpAdditionalDetailImageType pdpAdditionalDetailImage = factory.createPdpAdditionalDetailImageType();
                imageURL =getProductContent(productId,"XTRA_IMG_" + i + "_DETAIL",lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalDetailImagePath = getOsafeImagePath("XTRA_IMG_" + i + "_DETAIL");
                		pathElements = StringUtil.split(imageURL, "/");
                        pdpAdditionalDetailImage.setUrl(additionalDetailImagePath + pathElements.get(pathElements.size() - 1));
                	}
                	else
                	{
                		pdpAdditionalDetailImage.setUrl(imageURL);
                	}
                    pdpAdditionalDetailImage.setThruDate(getProductContentThruDate(productId,"XTRA_IMG_" + i + "_DETAIL",lProductContent));
                }
                else
                {
                	pdpAdditionalDetailImage.setUrl("");
                	pdpAdditionalDetailImage.setThruDate("");
                }
                pdpAdditionalImage.setPdpAdditionalDetailImage(pdpAdditionalDetailImage);
                pdpAdditionalImages.add(pdpAdditionalImage);
            }
            productImage.setPdpAlternateImage(pdpAlternateImage);
            
            productType.setProductImage(productImage);
            
            //Set Goods Identification
            GoodIdentificationType goodIdentificationType = factory.createGoodIdentificationType();
          
            List<GenericValue> productGoodIdentifications = _delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", productId),UtilMisc.toList("goodIdentificationTypeId"));
            Map mGoodIdentifications = FastMap.newInstance();
            for (GenericValue productGoodIdentification : productGoodIdentifications) 
            {
            	mGoodIdentifications.put(productGoodIdentification.getString("goodIdentificationTypeId"), productGoodIdentification.getString("idValue"));
            }
            
            String goodIdentification = (String)mGoodIdentifications.get("SKU");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setSku(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setSku("");
            }
        	
            goodIdentification = (String)mGoodIdentifications.get("GOOGLE_ID");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setGoogleId(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setGoogleId("");
            }
            
            goodIdentification = (String)mGoodIdentifications.get("ISBN");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setIsbn(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setIsbn("");
            }
            
            goodIdentification = (String)mGoodIdentifications.get("MANUFACTURER_ID_NO");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setManuId(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setManuId("");
            }
        	productType.setProductGoodIdentification(goodIdentificationType);
            
            ProductInventoryType productInventory = factory.createProductInventoryType();
            GenericValue productAttributeTot = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "BF_INVENTORY_TOT"));
            
            if (UtilValidate.isNotEmpty(productAttributeTot))
            {
            	productInventory.setBigfishInventoryTotal((String)productAttributeTot.get("attrValue"));
            }
            else
            {
            	productInventory.setBigfishInventoryTotal("");
            }
            
            GenericValue productAttributeWhs = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "BF_INVENTORY_WHS"));
            
            if (UtilValidate.isNotEmpty(productAttributeWhs))
            {
            	productInventory.setBigfishInventoryWarehouse((String)productAttributeWhs.get("attrValue"));
            }
            else
            {
            	productInventory.setBigfishInventoryWarehouse("");
            }
            productType.setProductInventory(productInventory);
            
            ProductAttributeType productAttribute = factory.createProductAttributeType();
            GenericValue productAttributeMulti = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_SELECT_MULTI_VARIANT"));
            
            if (UtilValidate.isNotEmpty(productAttributeMulti))
            {
            	productAttribute.setPdpSelectMultiVariant((String)productAttributeMulti.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpSelectMultiVariant("");
            }

            GenericValue productAttributeGiftMessage = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "CHECKOUT_GIFT_MESSAGE"));
            
            if (UtilValidate.isNotEmpty(productAttributeGiftMessage))
            {
            	productAttribute.setPdpCheckoutGiftMessage((String)productAttributeGiftMessage.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpCheckoutGiftMessage("");
            }
            
            GenericValue productAttributePdpQtyMin = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_QTY_MIN"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMin))
            {
            	productAttribute.setPdpQtyMin((String)productAttributePdpQtyMin.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpQtyMin("");
            }
            
            GenericValue productAttributePdpQtyMax = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_QTY_MAX"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMax))
            {
            	productAttribute.setPdpQtyMax((String)productAttributePdpQtyMax.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpQtyMax("");
            }
            
            GenericValue productAttributePdpQtyDefault = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", productId,"attrName", "PDP_QTY_DEFAULT"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyDefault))
            {
            	productAttribute.setPdpQtyDefault((String)productAttributePdpQtyDefault.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpQtyDefault("");
            }
            
            productType.setProductAttribute(productAttribute);
    	}
    	catch (Exception e) 
    	{
            Debug.logError(e, module);
    	}
    }
    
    private static void addXmlProductVariantRow(ObjectFactory factory, ProductType productType, GenericValue variantProduct, String productId, boolean bFirstVariant, Map productFeatureTypesMap) {
    	int iColIdx=0;
    	List<String> pathElements=null;
    	String imageURL=null;
    	try {
    		String variantProductId=variantProduct.getString("productId");
    		productType.setProductId(variantProductId);
    		productType.setMasterProductId(productId);
    		List<GenericValue> lProductContent = _delegator.findByAnd("ProductContent", UtilMisc.toMap("productId",variantProductId),UtilMisc.toList("-fromDate"));
            lProductContent=EntityUtil.filterByDate(lProductContent, UtilDateTime.nowTimestamp());
            
        	if (bFirstVariant)
        	{
        		//iColIdx=iColIdx + 15;
        	}
        	else
        	{
                
                String productName=variantProduct.getString("productName");
                if (UtilValidate.isNotEmpty(productName))
                {
                	productType.setProductName(productName);
                }
                else
                {
                	productType.setProductName("");
                }
                
                String internalName=variantProduct.getString("internalName");
                if (UtilValidate.isNotEmpty(internalName))
                {
                	productType.setInternalName(internalName);
                }
                else
                {
                	productType.setInternalName("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"SHORT_SALES_PITCH",lProductContent))) {
                	productType.setSalesPitch(getProductContent(productId,"SHORT_SALES_PITCH",lProductContent));
                } else {
                	productType.setSalesPitch("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"LONG_DESCRIPTION",lProductContent))) {
                	productType.setLongDescription(getProductContent(productId,"LONG_DESCRIPTION",lProductContent));
                } else {
                	productType.setLongDescription("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"SPECIALINSTRUCTIONS",lProductContent))) {
                	productType.setSpecialInstructions(getProductContent(productId,"SPECIALINSTRUCTIONS",lProductContent));
                } else {
                	productType.setSpecialInstructions("");
                }

                if(UtilValidate.isNotEmpty(getProductContent(productId,"DELIVERY_INFO",lProductContent))) {
                	productType.setDeliveryInfo(getProductContent(productId,"DELIVERY_INFO",lProductContent));
                } else {
                	productType.setDeliveryInfo("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"DIRECTIONS",lProductContent))) {
                	productType.setDirections(getProductContent(productId,"DIRECTIONS",lProductContent));
                } else {
                	productType.setDirections("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"TERMS_AND_CONDS",lProductContent))) {
                	productType.setTermsAndConds(getProductContent(productId,"TERMS_AND_CONDS",lProductContent));
                } else {
                	productType.setTermsAndConds("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"INGREDIENTS",lProductContent))) {
                	productType.setIngredients(getProductContent(productId,"INGREDIENTS",lProductContent));
                } else {
                	productType.setIngredients("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"WARNINGS",lProductContent))) {
                	productType.setWarnings(getProductContent(productId,"WARNINGS",lProductContent));
                } else {
                	productType.setWarnings("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"PLP_LABEL",lProductContent))) {
                	productType.setPlpLabel(getProductContent(productId,"PLP_LABEL",lProductContent));
                } else {
                	productType.setPlpLabel("");
                }
                
                if(UtilValidate.isNotEmpty(getProductContent(productId,"PDP_LABEL",lProductContent))) {
                	productType.setPdpLabel(getProductContent(productId,"PDP_LABEL",lProductContent));
                } else {
                	productType.setPdpLabel("");
                }
                BigDecimal productHeight=variantProduct.getBigDecimal("productHeight");
                if (UtilValidate.isNotEmpty(productHeight))
                {
                	productType.setProductHeight(_df.format(productHeight));
                }
                else
                {
                	productType.setProductHeight("");
                }
                BigDecimal productWidth=variantProduct.getBigDecimal("productWidth");
                if (UtilValidate.isNotEmpty(productWidth))
                {
                	productType.setProductWidth(_df.format(productWidth));
                }
                else
                {
                	productType.setProductWidth("");
                }
                BigDecimal productDepth=variantProduct.getBigDecimal("productDepth");
                if (UtilValidate.isNotEmpty(productDepth))
                {
                	productType.setProductDepth(_df.format(productDepth));
                }
                else
                {
                	productType.setProductDepth("");
                }
                BigDecimal weight=variantProduct.getBigDecimal("weight");
                if (UtilValidate.isNotEmpty(weight))
                {
                	productType.setProductWeight(_df.format(weight));
                }
                else
                {
                	productType.setProductWeight("");
                }
                productType.setReturnable("");
                productType.setTaxable("");
                productType.setChargeShipping("");
                productType.setManufacturerId("");
                
                Timestamp tsstampProdDate = variantProduct.getTimestamp("introductionDate");
                if (UtilValidate.isNotEmpty(tsstampProdDate))
                {
                	productType.setIntroDate(_sdf.format(new Date(tsstampProdDate.getTime())));
                }
                else
                {
                	productType.setIntroDate("");
                }
                tsstampProdDate = variantProduct.getTimestamp("salesDiscontinuationDate");
                if (UtilValidate.isNotEmpty(tsstampProdDate))
                {
                	productType.setDiscoDate(_sdf.format(new Date(tsstampProdDate.getTime())));
                }
                else
                {
                	productType.setDiscoDate("");
                }
                ProductCategoryMemberType productCategoryMemberType = factory.createProductCategoryMemberType();
                productType.setProductCategoryMember(productCategoryMemberType);
                
        	}
            
        	String productPrice ="";
        	ProductPriceType productPriceType = factory.createProductPriceType();
        	List<GenericValue> productPriceList = FastList.newInstance();
        	if (bFirstVariant)
        	{
        		productPriceList = EntityUtil.filterByDate(_delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", variantProductId, "productPriceTypeId", "LIST_PRICE"), UtilMisc.toList("-fromDate")));
        		if(UtilValidate.isEmpty(productPriceList)){
        			productPriceList = EntityUtil.filterByDate(_delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", "LIST_PRICE"), UtilMisc.toList("-fromDate")));
        		}
                if(UtilValidate.isNotEmpty(productPriceList))
                {
                	ListPriceType listPrice = factory.createListPriceType();
        	        if(UtilValidate.isNotEmpty(productPriceList))
        	        { 
        	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
        	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
        	        	listPrice.setPrice(productPrice);
        	        	listPrice.setCurrency(gvProductPrice.getString("currencyUomId"));
        	        	Timestamp tsstamp = gvProductPrice.getTimestamp("fromDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	listPrice.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	listPrice.setFromDate("");
                        }
                        tsstamp = gvProductPrice.getTimestamp("thruDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	listPrice.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	listPrice.setThruDate("");
                        }
                        productPriceType.setListPrice(listPrice);
        	        }
        	    }

                productPrice="";
                productPriceList = EntityUtil.filterByDate(_delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",variantProductId, "productPriceTypeId", "DEFAULT_PRICE"), UtilMisc.toList("-fromDate")));
                if(UtilValidate.isEmpty(productPriceList)){
        			productPriceList = EntityUtil.filterByDate(_delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", "DEFAULT_PRICE"), UtilMisc.toList("-fromDate")));
        		}
                if(UtilValidate.isNotEmpty(productPriceList))
                {
                	SalesPriceType salesPrice = factory.createSalesPriceType();
        	        if(UtilValidate.isNotEmpty(productPriceList))
        	        {
        	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
        	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
        	        	salesPrice.setPrice(productPrice);
        	        	salesPrice.setCurrency(gvProductPrice.getString("currencyUomId"));
        	        	Timestamp tsstamp = gvProductPrice.getTimestamp("fromDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	salesPrice.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	salesPrice.setFromDate("");
                        }
                        tsstamp = gvProductPrice.getTimestamp("thruDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	salesPrice.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	salesPrice.setThruDate("");
                        }
                        productPriceType.setSalesPrice(salesPrice);
        	        }
        	    }
        	}
        	else
        	{
        		productPriceList = EntityUtil.filterByDate(_delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", variantProductId, "productPriceTypeId", "LIST_PRICE"), UtilMisc.toList("-fromDate")));

        		if(UtilValidate.isNotEmpty(productPriceList))
                {
                	ListPriceType listPrice = factory.createListPriceType();
        	        if(UtilValidate.isNotEmpty(productPriceList))
        	        { 
        	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
        	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
        	        	listPrice.setPrice(productPrice);
        	        	listPrice.setCurrency(gvProductPrice.getString("currencyUomId"));
        	        	Timestamp tsstamp = gvProductPrice.getTimestamp("fromDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	listPrice.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	listPrice.setFromDate("");
                        }
                        tsstamp = gvProductPrice.getTimestamp("thruDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	listPrice.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	listPrice.setThruDate("");
                        }
                        productPriceType.setListPrice(listPrice);
        	        }
        	    }

                productPrice="";
                productPriceList = EntityUtil.filterByDate(_delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",variantProductId, "productPriceTypeId", "DEFAULT_PRICE"), UtilMisc.toList("-fromDate")));
                if(UtilValidate.isNotEmpty(productPriceList))
                {
                	SalesPriceType salesPrice = factory.createSalesPriceType();
        	        if(UtilValidate.isNotEmpty(productPriceList))
        	        {
        	        	GenericValue gvProductPrice = EntityUtil.getFirst(productPriceList);
        	        	productPrice=_df.format(gvProductPrice.getBigDecimal("price"));
        	        	salesPrice.setPrice(productPrice);
        	        	salesPrice.setCurrency(gvProductPrice.getString("currencyUomId"));
        	        	Timestamp tsstamp = gvProductPrice.getTimestamp("fromDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	salesPrice.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	salesPrice.setFromDate("");
                        }
                        tsstamp = gvProductPrice.getTimestamp("thruDate");
                        if (UtilValidate.isNotEmpty(tsstamp))
                        {
                        	salesPrice.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                        }
                        else
                        {
                        	salesPrice.setThruDate("");
                        }
                        productPriceType.setSalesPrice(salesPrice);
        	        }
        	    }
        	}
        	productType.setProductPrice(productPriceType);
        	
            
            List<GenericValue> productSelectableFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", variantProductId, "productFeatureApplTypeId", "STANDARD_FEATURE"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
            productSelectableFeatures = EntityUtil.filterByDate(productSelectableFeatures);
            
            ProductSelectableFeatureType productSelectableFeatureType = factory.createProductSelectableFeatureType();
            List selectableFeaturesList = productSelectableFeatureType.getFeature();
            FeatureType selectableFeature = null;
            for (GenericValue productSelectableFeature : productSelectableFeatures) 
            {   
            	String productFeatureTypeDesc = (String) productFeatureTypesMap.get(productSelectableFeature.getString("productFeatureTypeId"));
        		if(UtilValidate.isEmpty(productFeatureTypeDesc))
        		{
        			productFeatureTypeDesc = productSelectableFeature.getString("productFeatureTypeId");
        		}
            	selectableFeature = factory.createFeatureType();
            	selectableFeature.setFeatureId(productSelectableFeature.getString("productFeatureTypeId"));
            	List valueList = selectableFeature.getValue();
            	valueList.add(productSelectableFeature.getString("description"));
            	selectableFeature.setDescription(productFeatureTypeDesc);
            	Timestamp tsstamp = productSelectableFeature.getTimestamp("fromDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	selectableFeature.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	selectableFeature.setFromDate("");
                }
                tsstamp = productSelectableFeature.getTimestamp("thruDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	selectableFeature.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	selectableFeature.setThruDate("");
                }
                if(UtilValidate.isNotEmpty(productSelectableFeature.getString("sequenceNum"))) {
                	selectableFeature.setSequenceNum(productSelectableFeature.getString("sequenceNum"));
            	} else {
            		selectableFeature.setSequenceNum("");
            	}
                selectableFeaturesList.add(selectableFeature);
            }
            productType.setProductSelectableFeature(productSelectableFeatureType);
            
            List<GenericValue> productDistinguishFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", variantProductId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
            productDistinguishFeatures = EntityUtil.filterByDate(productDistinguishFeatures);
            
            ProductDescriptiveFeatureType productDescriptiveFeatureType = factory.createProductDescriptiveFeatureType();
            List descriptiveFeaturesList = productDescriptiveFeatureType.getFeature();
            FeatureType descriptiveFeature = null;
            for (GenericValue productDistinguishFeature : productDistinguishFeatures) 
            {   
            	String productFeatureTypeDesc = (String) productFeatureTypesMap.get(productDistinguishFeature.getString("productFeatureTypeId"));
        		if(UtilValidate.isEmpty(productFeatureTypeDesc))
        		{
        			productFeatureTypeDesc = productDistinguishFeature.getString("productFeatureTypeId");
        		}
            	descriptiveFeature = factory.createFeatureType();
            	descriptiveFeature.setFeatureId(productDistinguishFeature.getString("productFeatureTypeId"));
            	List valueList = descriptiveFeature.getValue();
            	valueList.add(productDistinguishFeature.getString("description"));
            	descriptiveFeature.setDescription(productFeatureTypeDesc);
            	Timestamp tsstamp = productDistinguishFeature.getTimestamp("fromDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	descriptiveFeature.setFromDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	descriptiveFeature.setFromDate("");
                }
                tsstamp = productDistinguishFeature.getTimestamp("thruDate");
                if (UtilValidate.isNotEmpty(tsstamp))
                {
                	descriptiveFeature.setThruDate(_sdf.format(new Date(tsstamp.getTime())));
                }
                else
                {
                	descriptiveFeature.setThruDate("");
                }
                if(UtilValidate.isNotEmpty(productDistinguishFeature.getString("sequenceNum"))) {
                	descriptiveFeature.setSequenceNum(productDistinguishFeature.getString("sequenceNum"));
            	} else {
            		descriptiveFeature.setSequenceNum("");
            	}
                descriptiveFeaturesList.add(descriptiveFeature);
            }
            productType.setProductDescriptiveFeature(productDescriptiveFeatureType);
            
            ProductImageType productImage = factory.createProductImageType();
            
            PlpSwatchType plpSwatch = factory.createPlpSwatchType();
            imageURL =getProductContent(variantProductId,"PLP_SWATCH_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String plpSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    plpSwatch.setUrl(plpSwatchImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		plpSwatch.setUrl(imageURL);
            	}
                
                plpSwatch.setThruDate(getProductContentThruDate(variantProductId,"PLP_SWATCH_IMAGE_URL",lProductContent));
            }
            else
            {
            	plpSwatch.setUrl("");
            	plpSwatch.setThruDate("");
            }
            productImage.setPlpSwatch(plpSwatch);
            
            PdpSwatchType pdpSwatch = factory.createPdpSwatchType();
            imageURL =getProductContent(variantProductId,"PDP_SWATCH_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpSwatch.setUrl(pdpSwatchImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpSwatch.setUrl(imageURL);
            	}
                
                pdpSwatch.setThruDate(getProductContentThruDate(variantProductId,"PDP_SWATCH_IMAGE_URL",lProductContent));
            }
            else
            {
            	pdpSwatch.setUrl("");
            	pdpSwatch.setThruDate("");
            }
            productImage.setPdpSwatch(pdpSwatch);
            
            PlpSmallImageType plpSmallImage = factory.createPlpSmallImageType();
            imageURL =getProductContent(variantProductId,"SMALL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String smallImagePath = getOsafeImagePath("SMALL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    plpSmallImage.setUrl(smallImagePath + pathElements.get(pathElements.size() - 1));
            	}
            	else
            	{
            		plpSmallImage.setUrl(imageURL);
            	}
            	
                plpSmallImage.setThruDate(getProductContentThruDate(variantProductId,"SMALL_IMAGE_URL",lProductContent));
            }
            else
            {
            	plpSmallImage.setUrl("");
            	plpSmallImage.setThruDate("");
            }
            productImage.setPlpSmallImage(plpSmallImage);
            
            PlpSmallAltImageType plpSmallAltImage = factory.createPlpSmallAltImageType();
            imageURL =getProductContent(variantProductId,"SMALL_IMAGE_ALT_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String smallAltImagePath = getOsafeImagePath("SMALL_IMAGE_ALT_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    plpSmallAltImage.setUrl(smallAltImagePath + pathElements.get(pathElements.size() - 1));
            	}
            	else
            	{
            		plpSmallAltImage.setUrl(imageURL);
            	}
                
                plpSmallAltImage.setThruDate(getProductContentThruDate(variantProductId,"SMALL_IMAGE_ALT_URL",lProductContent));
            }
            else
            {
            	plpSmallAltImage.setUrl("");
            	plpSmallAltImage.setThruDate("");
            }
            productImage.setPlpSmallAltImage(plpSmallAltImage);
            
            PdpThumbnailImageType pdpThumbnailImage = factory.createPdpThumbnailImageType();
            imageURL =getProductContent(variantProductId,"THUMBNAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String thumbnailImagePath = getOsafeImagePath("THUMBNAIL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpThumbnailImage.setUrl(thumbnailImagePath + pathElements.get(pathElements.size() - 1));
            	}
            	else
            	{
            		pdpThumbnailImage.setUrl(imageURL);
            	}
                
                pdpThumbnailImage.setThruDate(getProductContentThruDate(variantProductId,"THUMBNAIL_IMAGE_URL",lProductContent));
            }
            else
            {
            	pdpThumbnailImage.setUrl("");
            	pdpThumbnailImage.setThruDate("");
            }
            productImage.setPdpThumbnailImage(pdpThumbnailImage);
            
            PdpLargeImageType pdpLargeImage = factory.createPdpLargeImageType();
            imageURL =getProductContent(variantProductId,"LARGE_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String largeImagePath = getOsafeImagePath("LARGE_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpLargeImage.setUrl(largeImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpLargeImage.setUrl(imageURL);
            	}
                
                pdpLargeImage.setThruDate(getProductContentThruDate(variantProductId,"LARGE_IMAGE_URL",lProductContent));
            }
            else
            {
            	pdpLargeImage.setUrl("");
            	pdpLargeImage.setThruDate("");
            }
            productImage.setPdpLargeImage(pdpLargeImage);
            
            PdpDetailImageType pdpDetailImage = factory.createPdpDetailImageType();
            imageURL =getProductContent(variantProductId,"DETAIL_IMAGE_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String detailImagePath = getOsafeImagePath("DETAIL_IMAGE_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpDetailImage.setUrl(detailImagePath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpDetailImage.setUrl(imageURL);
            	}
                
                pdpDetailImage.setThruDate(getProductContentThruDate(variantProductId,"DETAIL_IMAGE_URL",lProductContent));
            }
            else
            {
            	pdpDetailImage.setUrl("");
            	pdpDetailImage.setThruDate("");
            }
            productImage.setPdpDetailImage(pdpDetailImage);
            
            PdpVideoType pdpVideo = factory.createPdpVideoType();
            imageURL =getProductContent(variantProductId,"PDP_VIDEO_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideoUrlPath = getOsafeImagePath("PDP_VIDEO_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpVideo.setUrl(pdpVideoUrlPath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpVideo.setUrl(imageURL);
            	}
                
                pdpVideo.setThruDate(getProductContentThruDate(variantProductId,"PDP_VIDEO_URL",lProductContent));
            }
            else
            {
            	pdpVideo.setUrl("");
            	pdpVideo.setThruDate("");
            }
            productImage.setPdpVideoImage(pdpVideo);

            PdpVideo360Type pdpVideo360 = factory.createPdpVideo360Type();
            imageURL =getProductContent(variantProductId,"PDP_VIDEO_360_URL",lProductContent);
            
            if (UtilValidate.isNotEmpty(imageURL))
            {
            	if (!UtilValidate.isUrl(imageURL))
            	{
            		String pdpVideo360UrlPath = getOsafeImagePath("PDP_VIDEO_360_URL");
            		pathElements = StringUtil.split(imageURL, "/");
                    pdpVideo360.setUrl(pdpVideo360UrlPath + pathElements.get(pathElements.size() - 1));	
            	}
            	else
            	{
            		pdpVideo360.setUrl(imageURL);
            	}
                
                pdpVideo360.setThruDate(getProductContentThruDate(variantProductId,"PDP_VIDEO_360_URL",lProductContent));
            }
            else
            {
            	pdpVideo360.setUrl("");
            	pdpVideo360.setThruDate("");
            }
            productImage.setPdpVideo360Image(pdpVideo360);
            
            //Set Product Alternamte Images
            PdpAlternateImageType pdpAlternateImage = factory.createPdpAlternateImageType();
            List pdpAdditionalImages = pdpAlternateImage.getPdpAdditionalImage();
            PdpAdditionalImageType pdpAdditionalImage = null;
            for (int i=1; i < 11; i++)
            {
            	pdpAdditionalImage = factory.createPdpAdditionalImageType();
            	
            	PdpAdditionalThumbImageType pdpAddtionalThumbImage = factory.createPdpAdditionalThumbImageType();
                imageURL =getProductContent(variantProductId,"ADDITIONAL_IMAGE_" + i,lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalImagePath = getOsafeImagePath("ADDITIONAL_IMAGE_" + i);
                		pathElements = StringUtil.split(imageURL, "/");
                        pdpAddtionalThumbImage.setUrl(additionalImagePath + pathElements.get(pathElements.size() - 1));	
                	}
                	else
                	{
                		pdpAddtionalThumbImage.setUrl(imageURL);
                	}
                    
                    pdpAddtionalThumbImage.setThruDate(getProductContentThruDate(variantProductId,"ADDITIONAL_IMAGE_" + i,lProductContent));
                }
                else
                {
                	pdpAddtionalThumbImage.setUrl("");
                	pdpAddtionalThumbImage.setThruDate("");
                }
                pdpAdditionalImage.setPdpAdditionalThumbImage(pdpAddtionalThumbImage);
                
                PdpAdditionalLargeImageType pdpAdditionalLargeImage = factory.createPdpAdditionalLargeImageType();
                imageURL =getProductContent(variantProductId,"XTRA_IMG_" + i +"_LARGE",lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalLargeImagePath = getOsafeImagePath("XTRA_IMG_" + i +"_LARGE");
                		pathElements = StringUtil.split(imageURL, "/");
                        pdpAdditionalLargeImage.setUrl(additionalLargeImagePath + pathElements.get(pathElements.size() - 1));	
                	}
                	else
                	{
                		pdpAdditionalLargeImage.setUrl(imageURL);
                	}
                    
                    pdpAdditionalLargeImage.setThruDate(getProductContentThruDate(variantProductId,"XTRA_IMG_" + i +"_LARGE",lProductContent));
                }
                else
                {
                	pdpAdditionalLargeImage.setUrl("");
                	pdpAdditionalLargeImage.setThruDate("");
                }
                pdpAdditionalImage.setPdpAdditionalLargeImage(pdpAdditionalLargeImage);
                
                PdpAdditionalDetailImageType pdpAdditionalDetailImage = factory.createPdpAdditionalDetailImageType();
                imageURL =getProductContent(variantProductId,"XTRA_IMG_" + i + "_DETAIL",lProductContent);
                
                if (UtilValidate.isNotEmpty(imageURL))
                {
                	if (!UtilValidate.isUrl(imageURL))
                	{
                		String additionalDetailImagePath = getOsafeImagePath("XTRA_IMG_" + i + "_DETAIL");	
                		pathElements = StringUtil.split(imageURL, "/");
                        pdpAdditionalDetailImage.setUrl(additionalDetailImagePath + pathElements.get(pathElements.size() - 1));	
                	}
                	else
                	{
                		pdpAdditionalDetailImage.setUrl(imageURL);
                	}
                    
                    pdpAdditionalDetailImage.setThruDate(getProductContentThruDate(variantProductId,"XTRA_IMG_" + i + "_DETAIL",lProductContent));
                }
                else
                {
                	pdpAdditionalDetailImage.setUrl("");
                	pdpAdditionalDetailImage.setThruDate("");
                }
                pdpAdditionalImage.setPdpAdditionalDetailImage(pdpAdditionalDetailImage);
                pdpAdditionalImages.add(pdpAdditionalImage);
            }
            productImage.setPdpAlternateImage(pdpAlternateImage);
            
            productType.setProductImage(productImage);
        	
            
            //Set Goods Identification
            GoodIdentificationType goodIdentificationType = factory.createGoodIdentificationType();
          
            List<GenericValue> productGoodIdentifications = _delegator.findByAnd("GoodIdentification", UtilMisc.toMap("productId", variantProductId),UtilMisc.toList("goodIdentificationTypeId"));
            Map mGoodIdentifications = FastMap.newInstance();
            for (GenericValue productGoodIdentification : productGoodIdentifications) 
            {
            	mGoodIdentifications.put(productGoodIdentification.getString("goodIdentificationTypeId"), productGoodIdentification.getString("idValue"));
            }
            
            String goodIdentification = (String)mGoodIdentifications.get("SKU");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setSku(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setSku("");
            }
        	
            goodIdentification = (String)mGoodIdentifications.get("GOOGLE_ID");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setGoogleId(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setGoogleId("");
            }
            
            goodIdentification = (String)mGoodIdentifications.get("ISBN");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setIsbn(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setIsbn("");
            }
            
            goodIdentification = (String)mGoodIdentifications.get("MANUFACTURER_ID_NO");
            if (UtilValidate.isNotEmpty(goodIdentification))
            {
                goodIdentificationType.setManuId(goodIdentification);
            }
            else
            {
            	goodIdentificationType.setManuId("");
            }
        	productType.setProductGoodIdentification(goodIdentificationType);
        	
        	//Set Product Inventory
        	ProductInventoryType productInventory = factory.createProductInventoryType();
        	GenericValue productAttributeTot = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "BF_INVENTORY_TOT"));
            
            if (UtilValidate.isNotEmpty(productAttributeTot))
            {
            	productInventory.setBigfishInventoryTotal((String)productAttributeTot.get("attrValue"));
            }
            else
            {
            	productInventory.setBigfishInventoryTotal("");
            }
            
            GenericValue productAttributeWhs = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "BF_INVENTORY_WHS"));
            
            if (UtilValidate.isNotEmpty(productAttributeWhs))
            {
            	productInventory.setBigfishInventoryWarehouse((String)productAttributeWhs.get("attrValue"));
            }
            else
            {
            	productInventory.setBigfishInventoryWarehouse("");
            }
            productType.setProductInventory(productInventory);
            
            ProductAttributeType productAttribute = factory.createProductAttributeType();
            GenericValue productAttributeMulti = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_SELECT_MULTI_VARIANT"));
            
            if (UtilValidate.isNotEmpty(productAttributeMulti))
            {
            	productAttribute.setPdpSelectMultiVariant((String)productAttributeMulti.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpSelectMultiVariant("");
            }
            
            GenericValue productAttributeGiftMessage = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "CHECKOUT_GIFT_MESSAGE"));
            if (UtilValidate.isNotEmpty(productAttributeGiftMessage))
            {
            	productAttribute.setPdpCheckoutGiftMessage((String)productAttributeGiftMessage.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpCheckoutGiftMessage("");
            }
            
            GenericValue productAttributePdpQtyMin = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_QTY_MIN"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMin))
            {
            	productAttribute.setPdpQtyMin((String)productAttributePdpQtyMin.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpQtyMin("");
            }
            
            GenericValue productAttributePdpQtyMax = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_QTY_MAX"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyMax))
            {
            	productAttribute.setPdpQtyMax((String)productAttributePdpQtyMax.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpQtyMax("");
            }
            
            GenericValue productAttributePdpQtyDefault = _delegator.findByPrimaryKey("ProductAttribute", UtilMisc.toMap("productId", variantProductId,"attrName", "PDP_QTY_DEFAULT"));
            
            if (UtilValidate.isNotEmpty(productAttributePdpQtyDefault))
            {
            	productAttribute.setPdpQtyDefault((String)productAttributePdpQtyDefault.get("attrValue"));
            }
            else
            {
            	productAttribute.setPdpQtyDefault("");
            }
            
            productType.setProductAttribute(productAttribute);
    	}
    	catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}

    }
    
    
    public static Map<String, Object> importProductRatingXML(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String xmlDataFilePath = (String)context.get("xmlDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        String loadImagesDirPath=(String)context.get("productLoadImagesDir");
        String imageUrl = (String)context.get("imageUrl");
        Boolean removeAll = (Boolean) context.get("removeAll");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        if (removeAll == null) removeAll = Boolean.FALSE;
        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        String tempDataFile = null;
        File baseDataDir = null;
        File baseFilePath = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
        	baseFilePath = new File(xmlDataFilePath);
            try {
                URL xlsDataFileUrl = UtilURL.fromFilename(xmlDataFilePath);
                InputStream ins = xlsDataFileUrl.openStream();

                if (ins != null && (xmlDataFilePath.toUpperCase().endsWith("XML"))) {
                    baseDataDir = new File(xmlDataDirPath);
                    if (baseDataDir.isDirectory() && baseDataDir.canWrite()) {

                        // ############################################
                        // move the existing xml files in dump directory
                        // ############################################
                        File dumpXmlDir = null;
                        File[] fileArray = baseDataDir.listFiles();
                        for (File file: fileArray) {
                            try {
                                if (file.getName().toUpperCase().endsWith("XML")) {
                                    if (dumpXmlDir == null) {
                                        dumpXmlDir = new File(baseDataDir, "dumpxml_"+UtilDateTime.nowDateString());
                                    }
                                    FileUtils.copyFileToDirectory(file, dumpXmlDir);
                                    file.delete();
                                }
                            } catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            } catch (Exception exc) {
                                Debug.logError(exc, module);
                            }
                        }
                        // ######################################
                        //save the temp xls data file on server 
                        // ######################################
                        try {
                        	tempDataFile = UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath);
                            inputWorkbook = new File(baseDataDir,  tempDataFile);
                            if (inputWorkbook.createNewFile()) {
                                Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
                            }
                            } catch (IOException ioe) {
                                Debug.logError(ioe, module);
                            } catch (Exception exc) {
                                Debug.logError(exc, module);
                            }
                    }
                    else {
                        messages.add("xml data dir path not found or can't be write");
                    }
                }
                else {
                    messages.add(" path specified for Excel sheet file is wrong , doing nothing.");
                }

            } catch (IOException ioe) {
                Debug.logError(ioe, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }
        else {
            messages.add("No path specified for Excel sheet file or xml data direcotry, doing nothing.");
        }

        // ######################################
        //read the temp xls file and generate xml 
        // ######################################
        try {
        if (inputWorkbook != null && baseDataDir  != null) {
        	try {
        		JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
            	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            	JAXBElement<BigFishProductRatingFeedType> bfProductRatingFeedType = (JAXBElement<BigFishProductRatingFeedType>)unmarshaller.unmarshal(inputWorkbook);
            	
            	List<ProductRatingType> productRatingList = bfProductRatingFeedType.getValue().getProductRating();
            	
            	if(productRatingList.size() > 0) {
            		List dataRows = buildProductRatingXMLDataRows(productRatingList);
            		buildProductRating(dataRows, xmlDataDirPath,messages);
            	}
            	
        	} catch (Exception e) {
        		Debug.logError(e, module);
			}
        	finally {
                try {
                    if (fOutProduct != null) {
                    	fOutProduct.close();
                    }
                } catch (IOException ioe) {
                    Debug.logError(ioe, module);
                }
            }
        }
        
        // ##############################################
        // move the generated xml files in done directory
        // ##############################################
        File doneXmlDir = new File(baseDataDir, Constants.DONE_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
        File[] fileArray = baseDataDir.listFiles();
        for (File file: fileArray) {
            try {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("XML")) {
                	if(!(file.getName().equals(tempDataFile)) && (!file.getName().equals(baseFilePath.getName()))){
                		FileUtils.copyFileToDirectory(file, doneXmlDir);
                        file.delete();
                	}
                }
            } catch (IOException ioe) {
                Debug.logError(ioe, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }

        // ######################################################################
        // call service for insert row in database  from generated xml data files 
        // by calling service entityImportDir if autoLoad parameter is true
        // ######################################################################
        if (autoLoad) {
            //Debug.logInfo("=====657========="+doneXmlDir.getPath()+"=========================", module);
            Map entityImportDirParams = UtilMisc.toMap("path", doneXmlDir.getPath(), 
                                                     "userLogin", context.get("userLogin"));
             try {
                 Map result = dispatcher.runSync("entityImportDir", entityImportDirParams);
                 if(UtilValidate.isNotEmpty(result.get("responseMessage")) && result.get("responseMessage").equals("error"))
	             {
	                 return ServiceUtil.returnError(result.get("errorMessage").toString());
	             }
                 List<String> serviceMsg = (List)result.get("messages");
                 for (String msg: serviceMsg) {
                     messages.add(msg);
                 }
             } catch (Exception exc) {
                 Debug.logError(exc, module);
             }
        }
        } catch (Exception exc) {
            Debug.logError(exc, module);
        }
        finally {
            inputWorkbook.delete();
        } 
        	
            	
                
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;  

    }
    
    public static List buildProductRatingXMLDataRows(List<ProductRatingType> productRatingList) {
		List dataRows = FastList.newInstance();

		try {
			
            for (int rowCount = 0 ; rowCount < productRatingList.size() ; rowCount++) {
            	ProductRatingType productRating = (ProductRatingType) productRatingList.get(rowCount);
            
            	Map mRows = FastMap.newInstance();
            	mRows.put("productStoreId",productRating.getProductStoreId());
                mRows.put("productId",productRating.getProductId());
                mRows.put("sku",productRating.getSku());
                mRows.put("productRatingScore",productRating.getProductRatingScore());
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    private static void buildProductRating(List dataRows,String xmlDataDirPath, List messages) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        String categoryImageName=null;
    	String productId = null;
		try {
			
	        fOutFile = new File(xmlDataDirPath, "000-ProductRating.xml");
            if (fOutFile.createNewFile()) 
            {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
                    StringBuilder  rowString = new StringBuilder();
	            	Map mRow = (Map)dataRows.get(i);
	            	productId = (String)mRow.get("productId");
	            	String sku = (String)mRow.get("sku");
	            	if(UtilValidate.isEmpty(productId) && UtilValidate.isNotEmpty(sku)) 
	            	{
	            		List<GenericValue> goodIdentificationList = _delegator.findByAnd("GoodIdentification", UtilMisc.toMap("goodIdentificationTypeId", "SKU", "idValue", sku));
	            		if(UtilValidate.isNotEmpty(goodIdentificationList)) 
	            		{
	            			productId = EntityUtil.getFirst(goodIdentificationList).getString("productId");
	            		}
	            	}
	            	if(UtilValidate.isNotEmpty(productId)) 
	            	{
	            		rowString.append("<" + "ProductCalculatedInfo" + " ");
                        rowString.append("productId" + "=\"" + productId + "\" ");
                        
                        if(mRow.get("productRatingScore") != null) 
                        {
                        	String productRatingScore = (String)mRow.get("productRatingScore");
                        	if(productRatingScore.equals(""))
                            {
                            	productRatingScore = null;
                            }
                        	rowString.append("averageCustomerRating" + "=\"" + productRatingScore + "\" ");
                        }
                        rowString.append("/>");
                        bwOutFile.write(rowString.toString());
	            	}
                    bwOutFile.newLine();
	            }
                bwOutFile.flush();
         	    writeXmlFooter(bwOutFile);
            }
    	}
      	 catch (Exception e) 
      	 {
             Debug.logError(e.getMessage(), module + ".buildProductRating");
             messages.add("Error: prcessing product Id[" + productId + "]. In Module:" + module + ".buildProductRating");
   	     }
         finally 
         {
             try {
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe.getMessage(), module + ".buildProductRating");
             }
         }
      	 
       }
    public static Map<String, Object> importReevooCsvToFeed(DispatchContext dctx, Map<String, ?> context) {

        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String reevooCsvFileLoc = (String)context.get("reevooCsvFileLoc");
        
        if (UtilValidate.isNotEmpty(reevooCsvFileLoc)) {
            try {
                // ######################################
                // make the input stram for csv data file
                // ######################################
                URL reevooCsvFileUrl = UtilURL.fromFilename(reevooCsvFileLoc);
                InputStream ins = reevooCsvFileUrl.openStream();
                if (ins != null && (reevooCsvFileLoc.toUpperCase().endsWith("CSV"))) {

                    ObjectFactory factory = new ObjectFactory();
                    BigFishProductRatingFeedType bfProductRatingFeedType = factory.createBigFishProductRatingFeedType();
                    String downloadTempDir = FeedsUtil.getFeedDirectory("ProductRating");

                    String productRatingFileName = "ProductRating";
                    productRatingFileName = productRatingFileName + "_" + (OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyy-MM-dd-HHmm"));
                    productRatingFileName = UtilValidate.stripWhitespace(productRatingFileName) + ".xml";

                    if (!new File(downloadTempDir).exists()) {
                        new File(downloadTempDir).mkdirs();
                    }
                    File file = new File(downloadTempDir, productRatingFileName);
                    List productRatingList = bfProductRatingFeedType.getProductRating();

                    // #######################
                    // Read csv file as String
                    // #######################
                    String csvFile  = UtilIO.readString(ins);
                    csvFile = csvFile.replaceAll("\\r", "");
                    String[] records = csvFile.split("\\n");
                    // ########################################
                    // Start row from index 1 for remove header
                    // ########################################
                    for (int i = 1; i < records.length; i++) {
                        try {
                            if (records[i] != null) {
                                String str = records[i].trim();
                                String[] map = str.split(",");
                                if (map.length == 2) {
                                    ProductRatingType productRating = factory.createProductRatingType();
                                    productRating.setSku(map[0]);
                                    productRating.setProductRatingScore(map[1]);
                                    productRatingList.add(productRating);
                                }
                            }
                        } catch(Exception e) {}
                    }
                    FeedsUtil.marshalObject(new JAXBElement<BigFishProductRatingFeedType>(new QName("", "BigFishProductRatingFeed"), BigFishProductRatingFeedType.class, null, bfProductRatingFeedType), file);
                    result.put("feedFile", file);
                    result.put("feedFileAsString", FileUtil.readTextFile(file, Boolean.TRUE).toString());
                }
                
            } catch (Exception exc) {
                ServiceUtil.returnError("Error occured in creating product rating feed xml from reevoo csv");
            }
        }
        return result;
    }
    
    public static void createProductCategoryXmlFromXls(ObjectFactory factory, List productCategoryList, List dataRows) 
    {
    	try 
    	{
    		CategoryType category = null;
    		
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			Map mRow = (Map)dataRows.get(i);
    			category = factory.createCategoryType();
    			category.setCategoryId((String) mRow.get("productCategoryId"));
                category.setParentCategoryId((String) mRow.get("parentCategoryId"));
    			category.setCategoryName((String) mRow.get("categoryName"));
    			category.setDescription((String) mRow.get("description"));
    			category.setLongDescription((String) mRow.get("longDescription"));
    			category.setAdditionalPlpText((String)mRow.get("plpText"));	
    			category.setAdditionalPdpText((String)mRow.get("pdpText"));	
    			
    			if(UtilValidate.isNotEmpty(mRow.get("plpImageName"))) 
    			{
    				PlpImageType plpImage = factory.createPlpImageType();
        			plpImage.setUrl((String)mRow.get("plpImageName"));
        			category.setPlpImage(plpImage);
    			}
    			
    			category.setFromDate((String)mRow.get("fromDate"));
    			category.setThruDate((String)mRow.get("thruDate"));
    			
    			productCategoryList.add(category);
    		}
    	
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    }
    
    public static void createProductXmlFromXls(ObjectFactory factory, List productList, List dataRows)
    {
    	try 
    	{
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			Map mRow = (Map)dataRows.get(i);
    			
    			if(mRow.get("masterProductId").equals(mRow.get("productId")) || UtilValidate.isEmpty(mRow.get("productId")))
    			{
    				//CREATE VIRTUAL/FINISHED GOOD PRODUCT XML ROW
    				ProductType productType = factory.createProductType();
    				String productId = (String)mRow.get("productId");
    				//String selectableFeature = "";
    				productList.add(createProductXMLRow(mRow, factory, productId, productType, null));
    			}
    			else
    			{
    				List<List> selectableFeatureList = FastList.newInstance();
        			int totSelectableFeatures = 5;
              	    for(int j = 1; j <= totSelectableFeatures; j++)
          	        {
              	    	selectableFeatureList = createFeatureVariantProductId(selectableFeatureList , (String)mRow.get("selectabeFeature_"+j));
          	        }
              	    if(selectableFeatureList.size() == 1)
              	    {
              	        //CREATE ONE VARIANT PRODUCT XML ROW
              	    	ProductType productType = factory.createProductType();
              	    	String productId = (String)mRow.get("productId");
              	    	List<String> selectableFeature = (List)selectableFeatureList.get(0);
              	    	productList.add(createProductXMLRow(mRow, factory, productId, productType, selectableFeature));
              	    }
              	    else if(selectableFeatureList.size() > 1)
              	    {
                 	    //CREATE MULTIPLE VARIANT PRODUCT XML ROW
              	    	int variantProductIdNo = 1;
                  	    for(List selectableFeature: selectableFeatureList)
                  	    {
                  	    	ProductType productType = factory.createProductType();
                  	    	String variantProductId = (String)mRow.get("productId");
                  	    	if(variantProductIdNo < 10)
                  	    	{
                  	    		variantProductId = variantProductId + "-0"+variantProductIdNo;
                  	    	}
                  	    	else
                  	    	{
                  	    		variantProductId = variantProductId + "-"+variantProductIdNo;
                  	    	}
                  	    	productList.add(createProductXMLRow(mRow, factory, variantProductId, productType, selectableFeature));
                  	    	variantProductIdNo++;
                  	    }
              	    }
              	    
    			}
	            
    			//productList.add(productType);
    		}
    	} catch (Exception e) {
    		Debug.logError(e, module);
    	}
    	
    }
    
    public static void createProductAssocXmlFromXls(ObjectFactory factory, List productAssocList, List dataRows) 
    {
    	try 
    	{
    		AssociationType association = null;
    		
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			Map mRow = (Map)dataRows.get(i);
    			association = factory.createAssociationType();
    			association.setMasterProductId((String) mRow.get("productId"));
    			association.setMasterProductIdTo((String) mRow.get("productIdTo"));
    			association.setProductAssocType((String) mRow.get("productAssocType"));
    			association.setFromDate((String) mRow.get("fromDate"));
    			association.setThruDate((String) mRow.get("thruDate"));
    			productAssocList.add(association);
    		}
    	
    	} 
    	catch (Exception e) 
    	{
    		Debug.logError(e, module);
    	}
    }
    
    public static void createProductFeatureSwatchXmlFromXls(ObjectFactory factory, List featureList, List dataRows) 
    {
    	try 
    	{
    		
    		FeatureSwatchType featureSwatchType = null;
    		Map mFeatureTypeMap = FastMap.newInstance();
    		
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			mFeatureTypeMap.clear();
    			Map mRow = (Map)dataRows.get(i);
    			buildFeatureMap(mFeatureTypeMap, (String)mRow.get("featureId"));
    			
    			featureSwatchType = factory.createFeatureSwatchType();
    			
    			if(mFeatureTypeMap.size() > 0) 
    			{
    	    	    Set featureTypeSet = mFeatureTypeMap.keySet();
    			    Iterator iterFeatureType = featureTypeSet.iterator(); 
    			    while (iterFeatureType.hasNext())
    			    {
    			    	String featureType =(String)iterFeatureType.next();
    				    String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
    				    featureSwatchType.setFeatureId(featureType);

    				    FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
    	  		        Set featureSet = mFeatureMap.keySet();
    	  		        Iterator iterFeature = featureSet.iterator();
    	  		    
    	  		        while (iterFeature.hasNext())
    	  		        {
    	  			        String featureValue = (String) mFeatureMap.get(iterFeature.next());
    	  			        featureSwatchType.setValue(featureValue);
    	  		        }
    			    }
    	  	    }
    			
    			if(UtilValidate.isNotEmpty(mRow.get("plpSwatchImage"))) 
    			{
    				PlpSwatchType plpSwatch = factory.createPlpSwatchType();
    				plpSwatch.setUrl((String)mRow.get("plpSwatchImage"));
    				plpSwatch.setThruDate("");
    				featureSwatchType.setPlpSwatch(plpSwatch);
	      	    }
    			
    			if(UtilValidate.isNotEmpty(mRow.get("pdpSwatchImage"))) 
    			{
    				PdpSwatchType pdpSwatch = factory.createPdpSwatchType();
    				pdpSwatch.setUrl((String)mRow.get("pdpSwatchImage"));
    				pdpSwatch.setThruDate("");
    				featureSwatchType.setPdpSwatch(pdpSwatch);
	      	    }
    			featureList.add(featureSwatchType);
    		}
    	
    	} 
    	catch (Exception e) 
    	{
    		Debug.logError(e, module);
    	}
    }
    
    public static void createProductManufacturerXmlFromXls(ObjectFactory factory, List manufacturerList, List dataRows) 
    {
    	try 
    	{
    		ManufacturerType manufacturer = null;
    		
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			Map mRow = (Map)dataRows.get(i);
    			manufacturer = factory.createManufacturerType();
    			manufacturer.setManufacturerId((String) mRow.get("partyId"));
    			manufacturer.setManufacturerName((String) mRow.get("manufacturerName"));
    			manufacturer.setDescription((String) mRow.get("shortDescription"));
    			manufacturer.setLongDescription((String) mRow.get("longDescription"));
    			
    			ManufacturerAddressType manufacturerAddress = factory.createManufacturerAddressType();
    			manufacturerAddress.setAddress1((String) mRow.get("address1"));
    			manufacturerAddress.setCityTown((String) mRow.get("city"));
    			manufacturerAddress.setStateProvince((String) mRow.get("state"));
    			manufacturerAddress.setZipPostCode((String) mRow.get("zip"));
    			manufacturerAddress.setCountry((String) mRow.get("country"));
    			manufacturer.setAddress(manufacturerAddress);
    			
    			if(UtilValidate.isNotEmpty(mRow.get("manufacturerImage"))) 
    			{
    				ManufacturerImageType manufacturerImage = factory.createManufacturerImageType();
        			manufacturerImage.setUrl((String) mRow.get("manufacturerImage"));
        			manufacturerImage.setThruDate("");
        			manufacturer.setManufacturerImage(manufacturerImage);
    			}
    			
    			manufacturerList.add(manufacturer);
    		}
    	
    	} 
    	catch (Exception e) 
    	{
    		Debug.logError(e, module);
    	}
    	
    }
    
    public static void createProductRatingXmlFromXls(ObjectFactory factory, List productRatingList, List dataRows, String productStoreId) 
    {
    	try 
    	{
    		ProductRatingType productRating = null;
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			Map mRow = (Map)dataRows.get(i);
    			productRating = factory.createProductRatingType();
    			productRating.setProductId((String)mRow.get("productId"));
    			productRating.setProductRatingScore((String)mRow.get("ratingScore"));
    			if(UtilValidate.isNotEmpty(productStoreId))
    			{
    				productRating.setProductStoreId(productStoreId);
    			}
    			else
    			{
    				productRating.setProductStoreId("");
    			}
    			productRatingList.add(productRating);
    		}
    	
    	} catch (Exception e) 
    	{
    		Debug.logError(e, module);
    	}
    }
    
    
    public static void createStoreXmlFromXls(ObjectFactory factory, List storeList, List dataRows, String productStoreId) 
    {
    	try 
    	{
    		
    		StoreType store = null;
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			Map mRow = (Map)dataRows.get(i);
    			store = factory.createStoreType();
    			
    			store.setStoreId((String)mRow.get("storeId"));
    			store.setStoreCode((String)mRow.get("storeCode"));
    			store.setStoreName((String)mRow.get("storeName"));
    			    			
    			StoreAddressType storeAddress = factory.createStoreAddressType();
    			
    			storeAddress.setCountry((String)mRow.get("country"));
    			storeAddress.setAddress1((String)mRow.get("address1"));
    			storeAddress.setAddress2((String)mRow.get("address2"));
    			storeAddress.setAddress3((String)mRow.get("address3"));
    			storeAddress.setCityTown((String)mRow.get("cityOrTown"));
    			storeAddress.setStateProvince((String)mRow.get("stateOrProvince"));
    			storeAddress.setZipPostCode((String)mRow.get("zipOrPostcode"));
    			storeAddress.setStorePhone((String)mRow.get("telephoneNumber"));
    			store.setStoreAddress(storeAddress);
    			
    			store.setStatus((String)mRow.get("status"));
    			store.setOpeningHours((String)mRow.get("openingHours"));
    			store.setStoreNotice((String)mRow.get("storeNotice"));
    			store.setStoreContentSpot((String)mRow.get("contentSpot"));
    			store.setGeoCodeLong((String)mRow.get("geoCodeLong"));
    			store.setGeoCodeLat((String)mRow.get("geoCodeLat"));
    			
    			if(UtilValidate.isNotEmpty(productStoreId))
    			{
    				store.setProductStoreId(productStoreId);
    			}
    			else
    			{
    				store.setProductStoreId("");
    			}
    			storeList.add(store);
    		}
    	
    	} catch (Exception e) 
    	{
    		Debug.logError(e, module);
    	}
    }
    
    public static void createOrderStatusUpdateXmlFromXls(ObjectFactory factory, List orderStatusUpdateList, List dataRows, String productStoreId) 
    {
    	try 
    	{
    		OrderStatusType orderStatusType = null;
    		for (int i=0 ; i < dataRows.size() ; i++) 
    		{
    			Map mRow = (Map)dataRows.get(i);
    			orderStatusType = factory.createOrderStatusType();
    			orderStatusType.setOrderId((String)mRow.get("orderId"));
    			orderStatusType.setOrderStatus((String)mRow.get("orderStatus"));
    			orderStatusType.setOrderShipDate((String)mRow.get("orderShipDate"));
    			orderStatusType.setOrderShipCarrier((String)mRow.get("orderShipCarrier"));
    			orderStatusType.setOrderShipMethod((String)mRow.get("orderShipMethod"));
    			orderStatusType.setOrderTrackingNumber((String)mRow.get("orderTrackingNumber"));
    			orderStatusType.setOrderNote((String)mRow.get("orderNote"));
    			if(UtilValidate.isNotEmpty(productStoreId))
    			{
    				orderStatusType.setProductStoreId(productStoreId);
    			}
    			else
    			{
    				orderStatusType.setProductStoreId("");
    			}
    			orderStatusUpdateList.add(orderStatusType);
    		}
    	
    	} catch (Exception e) 
    	{
    		Debug.logError(e, module);
    	}
    }
    
    private static ProductType createProductXMLRow(Map mRow, ObjectFactory factory, String productId, ProductType productType, List<String> finalSelectableFeature)
    {
    	//String currencyUomId = OsafeAdminUtil.getProductStoreParm(productStoreId,"CURRENCY_UOM_DEFAULT");
    	String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
    	
    	productType.setMasterProductId((String)mRow.get("masterProductId"));
		productType.setProductId(productId);
		productType.setInternalName((String)mRow.get("internalName"));
		productType.setProductName((String)mRow.get("productName"));
		productType.setSalesPitch((String)mRow.get("salesPitch"));
		productType.setLongDescription((String)mRow.get("longDescription"));
		productType.setSpecialInstructions((String)mRow.get("specialInstructions"));
		productType.setManufacturerId((String)mRow.get("manufacturerId"));
		productType.setDeliveryInfo((String)mRow.get("deliveryInfo"));
		productType.setDirections((String)mRow.get("directions"));
        productType.setTermsAndConds((String)mRow.get("termsConditions"));
		productType.setIngredients((String)mRow.get("ingredients"));
		productType.setWarnings((String)mRow.get("warnings"));
		productType.setPlpLabel((String)mRow.get("plpLabel"));
        productType.setPdpLabel((String)mRow.get("pdpLabel"));
        productType.setProductHeight((String)mRow.get("productHeight"));
		productType.setProductWidth((String)mRow.get("productWidth"));	
		productType.setProductDepth((String)mRow.get("productDepth"));
		productType.setProductWeight((String)mRow.get("weight"));
		productType.setReturnable((String)mRow.get("returnable"));
    	productType.setChargeShipping((String)mRow.get("chargeShipping"));
		productType.setTaxable((String)mRow.get("taxable"));
		productType.setIntroDate((String)mRow.get("introDate"));
		productType.setDiscoDate((String)mRow.get("discoDate"));
		
		ProductPriceType productPrice = factory.createProductPriceType();
        ListPriceType listPrice = factory.createListPriceType();
        listPrice.setPrice((String)mRow.get("listPrice"));
        listPrice.setCurrency(currencyUomId);
        listPrice.setFromDate("");
        listPrice.setThruDate("");
        productPrice.setListPrice(listPrice);
        
        SalesPriceType salesPrice = factory.createSalesPriceType();
        salesPrice.setPrice((String)mRow.get("defaultPrice"));
        salesPrice.setCurrency(currencyUomId);
        salesPrice.setFromDate("");
        salesPrice.setThruDate("");
        productPrice.setSalesPrice(salesPrice);
        productType.setProductPrice(productPrice);
        
        ProductCategoryMemberType productCategory = factory.createProductCategoryMemberType();
        List<CategoryMemberType> categoryList = productCategory.getCategory();
        
        String[] productCategoryIds = null;
        String productCategoryId = (String)mRow.get("productCategoryId");
        if(UtilValidate.isNotEmpty(productCategoryId)) 
        {
       	    productCategoryIds = productCategoryId.split(",");
        }
        String sequenceNum = (String)mRow.get("sequenceNum");
        if(UtilValidate.isNotEmpty(productCategoryIds)) 
        {
            for (int j=0;j < productCategoryIds.length;j++) 
            {
            	CategoryMemberType categoryMember = factory.createCategoryMemberType();
	            categoryMember.setCategoryId(productCategoryIds[j].trim());
	            categoryMember.setSequenceNum(sequenceNum);
	            categoryMember.setFromDate("");
	            categoryMember.setThruDate("");
	            categoryList.add(categoryMember);
            }
        }
        productType.setProductCategoryMember(productCategory);
        
        ProductSelectableFeatureType selectableFeature = factory.createProductSelectableFeatureType();
  	    List<FeatureType> selectableFeatureList = selectableFeature.getFeature();
        String[] selectableFeatures = null;
        /*if(UtilValidate.isNotEmpty(finalSelectableFeature))
        {
        	selectableFeatures = finalSelectableFeature.split(",");
        }*/
        if(UtilValidate.isNotEmpty(finalSelectableFeature)) 
        {
            for (String featureValue : finalSelectableFeature) //int j=0;j < selectableFeatures.length;j++) 
            {
            	FeatureType feature = (FeatureType)factory.createFeatureType();
            	featureValue = featureValue.trim();
            	String[] featureValueArr = featureValue.split("~");
            	if(featureValueArr.length > 0)
            	{
            		feature.setFeatureId(featureValueArr[0].trim());
            	}
            	if(featureValueArr.length > 1)
            	{
            		List valueList = feature.getValue();
          	        valueList.add(featureValueArr[1].trim());
            	}
          	    
          	    feature.setDescription("");
          	    feature.setFromDate("");
          	    feature.setThruDate("");
          	    feature.setDescription("");
          	    feature.setSequenceNum("");
          	    selectableFeatureList.add(feature);
            }
        }
        productType.setProductSelectableFeature(selectableFeature);
        
        ProductDescriptiveFeatureType descriptiveFeature = factory.createProductDescriptiveFeatureType();
  	    List<FeatureType> descriptiveFeatureList = descriptiveFeature.getFeature();
  	    
        Map mFeatureTypeMap = FastMap.newInstance();
        mFeatureTypeMap.clear();
        int totDescriptiveFeatures = 5;
  	    for(int j = 1; j <= totDescriptiveFeatures; j++)
	    {
  	    	
  	    	String parseFeatureType = (String)mRow.get("descriptiveFeature_"+j);
  	    	if (UtilValidate.isNotEmpty(parseFeatureType))
  	    	{
  	    		FeatureType feature = (FeatureType)factory.createFeatureType();
  	        	int iFeatIdx = parseFeatureType.indexOf(':');
  	        	if (iFeatIdx > -1)
  	        	{
  	            	String featureType = parseFeatureType.substring(0,iFeatIdx).trim();
  	            	feature.setFeatureId(featureType);
  	            	
  	            	String sFeatures = parseFeatureType.substring(iFeatIdx +1);
  	                String[] featureTokens = sFeatures.split(",");
  	            	Map mFeatureMap = FastMap.newInstance();
  	            	List valueList = feature.getValue();
  	                for (int f=0;f < featureTokens.length;f++)
  	                {
  	              	    valueList.add(featureTokens[f].trim());
  	                }
  	                feature.setDescription("");
              	    feature.setFromDate("");
              	    feature.setThruDate("");
              	    feature.setDescription("");
              	    feature.setSequenceNum("");
              	    descriptiveFeatureList.add(feature);
  	        	}
  	    		
  	    	}    
	    }
  	    
  	    
        productType.setProductDescriptiveFeature(descriptiveFeature);
        
        ProductImageType productImage = factory.createProductImageType();
        
        if(UtilValidate.isNotEmpty(mRow.get("plpSwatchImage"))) 
        {
        	PlpSwatchType plpSwatch = factory.createPlpSwatchType();
            plpSwatch.setUrl((String)mRow.get("plpSwatchImage"));
            plpSwatch.setThruDate("");
            productImage.setPlpSwatch(plpSwatch);	
        }
        if(UtilValidate.isNotEmpty(mRow.get("pdpSwatchImage"))) 
        {
        	PdpSwatchType pdpSwatch = factory.createPdpSwatchType();
            pdpSwatch.setUrl((String)mRow.get("pdpSwatchImage"));
            pdpSwatch.setThruDate("");
            productImage.setPdpSwatch(pdpSwatch);	
        }
        if(UtilValidate.isNotEmpty(mRow.get("smallImage"))) 
        {
        	PlpSmallImageType plpSmallImage = factory.createPlpSmallImageType();
            plpSmallImage.setUrl((String)mRow.get("smallImage"));
            plpSmallImage.setThruDate("");
            productImage.setPlpSmallImage(plpSmallImage);
        }
        if(UtilValidate.isNotEmpty(mRow.get("smallImageAlt"))) 
        {
        	PlpSmallAltImageType plpSmallAltImage = factory.createPlpSmallAltImageType();
            plpSmallAltImage.setUrl((String)mRow.get("smallImageAlt"));
            plpSmallAltImage.setThruDate("");
            productImage.setPlpSmallAltImage(plpSmallAltImage);
        }
  	    if(UtilValidate.isNotEmpty(mRow.get("thumbImage"))) 
  	    {
  	    	PdpThumbnailImageType pdpThumbnailImage = factory.createPdpThumbnailImageType();
            pdpThumbnailImage.setUrl((String)mRow.get("thumbImage"));
            pdpThumbnailImage.setThruDate("");
            productImage.setPdpThumbnailImage(pdpThumbnailImage);
  	    }
  	    if(UtilValidate.isNotEmpty(mRow.get("largeImage"))) 
  	    {
  	    	PdpLargeImageType pdpLargeImage = factory.createPdpLargeImageType();
            pdpLargeImage.setUrl((String)mRow.get("largeImage"));
            pdpLargeImage.setThruDate("");
            productImage.setPdpLargeImage(pdpLargeImage);
  	    }
  	    if(UtilValidate.isNotEmpty(mRow.get("detailImage"))) 
  	    {
  	    	PdpDetailImageType pdpDetailImage = factory.createPdpDetailImageType();
            pdpDetailImage.setUrl((String)mRow.get("detailImage"));
            pdpDetailImage.setThruDate("");
            productImage.setPdpDetailImage(pdpDetailImage);
  	    }
  	    if(UtilValidate.isNotEmpty(mRow.get("pdpVideoUrl"))) 
  	    {
  	    	PdpVideoType pdpVideo = factory.createPdpVideoType();
            pdpVideo.setUrl((String)mRow.get("pdpVideoUrl"));
            pdpVideo.setThruDate("");
            productImage.setPdpVideoImage(pdpVideo);
  	    }
  	    if(UtilValidate.isNotEmpty(mRow.get("pdpVideo360Url"))) 
  	    {
  	    	PdpVideo360Type pdpVideo360 = factory.createPdpVideo360Type();
            pdpVideo360.setUrl((String)mRow.get("pdpVideo360Url"));
            pdpVideo360.setThruDate("");
            productImage.setPdpVideo360Image(pdpVideo360);
  	    }
        PdpAlternateImageType pdpAlternateImage = factory.createPdpAlternateImageType();
        List pdpAdditionalImages = pdpAlternateImage.getPdpAdditionalImage();
    	for(int j = 0; j < 10; j++) 
    	{
    		PdpAdditionalImageType pdpAdditionalImage = factory.createPdpAdditionalImageType();
    	    if(UtilValidate.isNotEmpty(mRow.get("addImage"+j))) 
    	    {
    	    	PdpAdditionalThumbImageType pdpAdditionalThumbImage = factory.createPdpAdditionalThumbImageType();
        		pdpAdditionalThumbImage.setUrl((String)mRow.get("addImage"+j));
        		pdpAdditionalThumbImage.setThruDate("");
        		pdpAdditionalImage.setPdpAdditionalThumbImage(pdpAdditionalThumbImage);
    	    }
            if(UtilValidate.isNotEmpty(mRow.get("xtraLargeImage"+j))) 
            {
            	PdpAdditionalLargeImageType pdpAdditionalLargeImage = factory.createPdpAdditionalLargeImageType();
        	    pdpAdditionalLargeImage.setUrl((String)mRow.get("xtraLargeImage"+j));
        	    pdpAdditionalLargeImage.setThruDate("");
        	    pdpAdditionalImage.setPdpAdditionalLargeImage(pdpAdditionalLargeImage);
    	    }
            if(UtilValidate.isNotEmpty(mRow.get("xtraDetailImage"+j))) 
            {
            	PdpAdditionalDetailImageType pdpAdditionalDetailImage = factory.createPdpAdditionalDetailImageType();
        	    pdpAdditionalDetailImage.setUrl((String)mRow.get("xtraDetailImage"+j));
        	    pdpAdditionalDetailImage.setThruDate("");
        	    pdpAdditionalImage.setPdpAdditionalDetailImage(pdpAdditionalDetailImage);
    	    }
            if(UtilValidate.isNotEmpty(mRow.get("addImage"+j)) || UtilValidate.isNotEmpty(mRow.get("xtraLargeImage"+j)) || UtilValidate.isNotEmpty(mRow.get("xtraDetailImage"+j))) 
            {
            	pdpAdditionalImages.add(pdpAdditionalImage);
            }
    	}
    	productImage.setPdpAlternateImage(pdpAlternateImage);
    	productType.setProductImage(productImage);
    	
        GoodIdentificationType goodIdentification = factory.createGoodIdentificationType();
        goodIdentification.setSku((String)mRow.get("goodIdentificationSkuId"));
        goodIdentification.setGoogleId((String)mRow.get("goodIdentificationGoogleId"));
        goodIdentification.setIsbn((String)mRow.get("goodIdentificationIsbnId"));
        goodIdentification.setManuId((String)mRow.get("goodIdentificationManufacturerId"));
        productType.setProductGoodIdentification(goodIdentification);
        
        ProductInventoryType productInventory = factory.createProductInventoryType();
        productInventory.setBigfishInventoryTotal((String)mRow.get("bfInventoryTot"));
        productInventory.setBigfishInventoryWarehouse((String)mRow.get("bfInventoryWhs"));
        productType.setProductInventory(productInventory);
        
        ProductAttributeType productAttribute = factory.createProductAttributeType();
        productAttribute.setPdpSelectMultiVariant((String)mRow.get("multiVariant"));
        productAttribute.setPdpCheckoutGiftMessage((String)mRow.get("giftMessage"));
        productAttribute.setPdpQtyMin((String)mRow.get("pdpQtyMin"));
        productAttribute.setPdpQtyMax((String)mRow.get("pdpQtyMax"));
        productAttribute.setPdpQtyDefault((String)mRow.get("pdpQtyDefault"));
        productType.setProductAttribute(productAttribute);
        
        return productType; 
    }
    
    public static List createFeatureVariantProductId(List selectableFeatureList, String selectableFeature)
    {
    	if (UtilValidate.isNotEmpty(selectableFeature))
    	{
    		List tempSelectableFeatureList = FastList.newInstance();
        	int iFeatIdx = selectableFeature.indexOf(':');
        	if (iFeatIdx > -1)
        	{
            	String featureType = selectableFeature.substring(0,iFeatIdx).trim();
            	String sFeatures = selectableFeature.substring(iFeatIdx +1);
                String[] featureTokens = sFeatures.split(",");
            	HashMap mFeatureMap = new HashMap();
            	
            	if(selectableFeatureList.size() > 0)
            	{
            		for (int i=0; i < selectableFeatureList.size();i++)
                    {
            			for (int f=0; f < featureTokens.length; f++)
                        {
            				
            				ArrayList featureList = new ArrayList();
            				ArrayList tempList =  (ArrayList)selectableFeatureList.get(i);
            				featureList.addAll(tempList);
            				featureList.add(featureType+"~"+featureTokens[f].trim());
            				tempSelectableFeatureList.add(featureList);
                        }
                    }
            	}
            	else
            	{
            		for (int f=0; f < featureTokens.length; f++)
                    {
            			ArrayList featureList = new ArrayList();
            			featureList.add(featureType+"~"+featureTokens[f].trim());
            			selectableFeatureList.add(featureList);
                    }	
            	}
        	}
        	if(tempSelectableFeatureList.size() > 0)
        	{
        		selectableFeatureList = tempSelectableFeatureList;
        	}
    	}
    	
    	return selectableFeatureList;
    }
    
    
    public static Map<String, Object> validateProductData(DispatchContext ctx, Map<String, ?> context) 
    {
    	
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        List<Map> productCatDataList = (List) context.get("productCatDataList");
        List<Map> productDataList = (List) context.get("productDataList");
        List<Map> productAssocDataList = (List) context.get("productAssocDataList");
        List<Map> productFeatureSwatchDataList = (List) context.get("productFeatureSwatchDataList");
        List<Map> manufacturerDataList = (List) context.get("manufacturerDataList");
        
        List<String> prodCatErrorList = FastList.newInstance();
        List<String> prodCatWarningList = FastList.newInstance();
        
        List<String> productErrorList = FastList.newInstance();
        List<String> productWarningList = FastList.newInstance();

        List<String> productAssocErrorList = FastList.newInstance();
        List<String> productAssocWarningList = FastList.newInstance();

        List<String> productFeatureSwatchErrorList = FastList.newInstance();
        List<String> productFeatureSwatchWarningList = FastList.newInstance();

        List<String> productManufacturerErrorList = FastList.newInstance();
        List<String> productManufacturerWarningList = FastList.newInstance();

        List<String> errorMessageList = FastList.newInstance();
        
        Set prevProdCatList = FastSet.newInstance();
        List existingProdCatIdList = FastList.newInstance();

        Map result = ServiceUtil.returnSuccess();
        try
        {
        	List existingProdCatList = _delegator.findList("ProductCategory", null, null, null, null, false);
            Map<String, List> itenNoMap = FastMap.newInstance();
            Map<String, List> prodNoMap = FastMap.newInstance();
            if(UtilValidate.isNotEmpty(existingProdCatList))
            {
                existingProdCatIdList = EntityUtil.getFieldListFromEntityList(existingProdCatList, "productCategoryId", true);
            }

            Set productFeatureSet = FastSet.newInstance();
            Map mFeatureTypeMap = FastMap.newInstance();
            int totalSelectableFeature = 5;
            int totalDescriptiveFeature = 5;
            List productFeatures = _delegator.findList("ProductFeature", null, null, null, null, false);
            List productFeatureIds = FastList.newInstance();
            if(UtilValidate.isNotEmpty(productFeatures))
            {
            	productFeatureIds = EntityUtil.getFieldListFromEntityList(productFeatures,"productFeatureId", true);
            }

            String osafeThemeServerPath = FlexibleStringExpander.expandString(OSAFE_PROP.getString("osafeThemeServer"), context);
            String osafeThemeImagePath = osafeThemeServerPath; 
            //Get the DEFAULT_IMAGE_DIRECTORY path from OsafeImagePath.xml

            String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
            	
            List<Map<Object, Object>> imageLocationPrefList = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);

            Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();

            for(Map<Object, Object> imageLocationPref : imageLocationPrefList) 
            {
                imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
            }

            String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
            if(UtilValidate.isNotEmpty(defaultImageDirectory)) 
            {
                osafeThemeImagePath = osafeThemeImagePath + defaultImageDirectory;
            }

            //Validation

            List newProdCatIdList = FastList.newInstance();
            List itemNoList = FastList.newInstance();

            //Validation for Product Category
            Integer rowNo = new Integer(1);
            for(Map productCategory : productCatDataList) 
            {
                String parentCategoryId = (String)productCategory.get("parentCategoryId");
                String productCategoryId = (String)productCategory.get("productCategoryId");
                String categoryName = (String)productCategory.get("categoryName");
                String description = (String)productCategory.get("description");
                String longDescription = (String)productCategory.get("longDescription");
                String plpImageName = (String)productCategory.get("plpImageName");
                String thruDate = (String)productCategory.get("thruDate");
                String fromDate = (String)productCategory.get("fromDate");
                
                if(UtilValidate.isNotEmpty(productCategoryId))
                {
                    if(!OsafeAdminUtil.isValidId(productCategoryId))
                    {
                        prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "CategoryId", "idData", productCategoryId), locale));
                    }
                    if(productCategoryId.length() > 20)
                    {
                    	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Category ID", "fieldData", productCategoryId), locale));
                    }
                    prevProdCatList.add(productCategoryId);
                }

                if(UtilValidate.isNotEmpty(parentCategoryId))
                {
                	boolean parentCategoryIdMatch = false;
                    if(prevProdCatList.contains(parentCategoryId.trim()))
                    {
                    	parentCategoryIdMatch = true;
                    }
                    else
                    {
                    	if(existingProdCatIdList.contains(parentCategoryId.trim()))
                        {
                    		parentCategoryIdMatch = true;
                        }
                    }
                
                    if(!parentCategoryIdMatch)
                    {
                    	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ParentCategoryIdMatchingError", UtilMisc.toMap("rowNo", rowNo.toString(), "parentCategoryId", parentCategoryId), locale));
                    }
                    
                    if(UtilValidate.isEmpty(productCategoryId))
                    {
                        prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ParentCategoryIdAssociationError", UtilMisc.toMap("rowNo", rowNo.toString(), "parentCategoryId", parentCategoryId), locale));
                    }
                    else 
                    {
                        newProdCatIdList.add(productCategoryId);
                    }
                }
                else
                {
                	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankParentCategoryIdError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                }
                if(UtilValidate.isEmpty(categoryName))
                {
                    prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankCategoryNameError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                }
                else
                {
                  if(categoryName.length() > 100)
                    {
                    	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "CatNameLengthExceedError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Category Name", "fieldData", categoryName), locale));
                    }
                } 
                
                if(UtilValidate.isEmpty(description))
                {
                    prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankDescriptionError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                }
                else
                {
                  if(description.length() > 255)
                    {
                    	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "DescLengthExceedError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Category Description", "fieldData", description), locale));
                    }
                }  
                if(UtilValidate.isEmpty(longDescription))
                {
                    prodCatWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankLongDescWarning", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                }
                if(UtilValidate.isNotEmpty(plpImageName))
                {
                	if(!UtilValidate.isUrl(plpImageName))
                	{
            	        boolean isFileExist = (new File(osafeThemeImagePath, plpImageName)).exists();
            	        if(!isFileExist)
            	        {
            	            prodCatWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PLPImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo.toString(), "plpImageData", plpImageName), locale));
            	        }
                	}
                }
                
                if(UtilValidate.isNotEmpty(fromDate))
                {
                    if(!OsafeAdminUtil.isValidDate(fromDate))
                    {
                    	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidProductCategoryFromDateError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", fromDate, "idData", productCategoryId), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(thruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(thruDate))
                    {
                    	prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidProductCategoryThruDateError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", thruDate, "idData", productCategoryId), locale));
                    }    
                }
                
                if(UtilValidate.isNotEmpty(productCategoryId) && UtilValidate.isNotEmpty(parentCategoryId))
                {
                	if(productCategoryId.equals(parentCategoryId))
                	{
                		prodCatErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductCategoryIdSameError", UtilMisc.toMap("rowNo", rowNo.toString(), "productCategoryId", productCategoryId, "parentCategoryId", parentCategoryId), locale));
                	}
                }
                rowNo++;
            }

            List newManufacturerIdList = FastList.newInstance();
            List existingManufacturerIdList = FastList.newInstance();
            for(Map manufacturerData : manufacturerDataList) 
            {
                String manufacturerId = (String)manufacturerData.get("partyId");
                if(UtilValidate.isNotEmpty(manufacturerId)) 
                {
                    newManufacturerIdList.add(manufacturerId);
                } 
            }
            List<GenericValue> partyManufacturers = _delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId","MANUFACTURER"),UtilMisc.toList("partyId"));
            for (GenericValue partyManufacturer : partyManufacturers) 
            {
            	GenericValue party = (GenericValue) partyManufacturer.getRelatedOne("Party");
                String partyId=party.getString("partyId");
                existingManufacturerIdList.add(partyId);
            }

            //Validation for Product
            Map variantProductIdMap = FastMap.newInstance();
            Map virtualProductIdMap = FastMap.newInstance();
            Map finishedGoodProductIdMap = FastMap.newInstance();

            for(Map product : productDataList) 
            {
            	String masterProductId = (String)product.get("masterProductId");
                String productId = (String)product.get("productId");
                if(UtilValidate.isNotEmpty(masterProductId) && UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId))
            	{
                	virtualProductIdMap.put(masterProductId, productId);
            	}
                else
                {
                	if(UtilValidate.isNotEmpty(masterProductId) && UtilValidate.isNotEmpty(productId) && !masterProductId.equals(productId))
                	{
                		variantProductIdMap.put(masterProductId, productId);
                	}
                	else
                	{
                		if(UtilValidate.isNotEmpty(masterProductId) && UtilValidate.isEmpty(productId))
                    	{
                			finishedGoodProductIdMap.put(masterProductId, "");
                    	}	
                	}
                }
                	
            }

            List newProductIdList = FastList.newInstance();
            List existingProductIdList = FastList.newInstance();
            rowNo = new Integer(1);

            Map masterProductIdMap = FastMap.newInstance();
            List virtualFinishProductIdList = FastList.newInstance();

            for(Map product : productDataList) 
            {
            	String productCategoryId = (String)product.get("productCategoryId");
                String longDescription = (String)product.get("longDescription");
                String defaultPrice = (String)product.get("defaultPrice");
                String listPrice = (String)product.get("listPrice");
                String thruDate = (String)product.get("discoDate");
                String fromDate = (String)product.get("introDate");
                String internalName = (String)product.get("internalName");
            	String productName = (String)product.get("productName");
                String plpImage = (String)product.get("smallImage");
                String pdpRegularImage = (String)product.get("largeImage");
                String masterProductId = (String)product.get("masterProductId");
                String productId = (String)product.get("productId");
                String manufacturerId = (String)product.get("manufacturerId");
                String bfTotalInventory = (String)product.get("bfInventoryTot");
                String bfWHInventory = (String)product.get("bfInventoryWhs");
                String multiVariant = (String)product.get("multiVariant");
                String productHeight = (String)product.get("productHeight");
                String productWidth = (String)product.get("productWidth");
                String productDepth = (String)product.get("productDepth");
                String productWeight = (String)product.get("weight");
                String listPriceFromDate = (String)product.get("listPriceFromDate");
                String listPriceThruDate = (String)product.get("listPriceThruDate");
                String defaultPriceFromDate = (String)product.get("defaultPriceFromDate");
                String defaultPriceThruDate = (String)product.get("defaultPriceThruDate");
                String categoryFromDate= (String)product.get(productCategoryId+"_fromDate");
                String categoryThruDate= (String)product.get(productCategoryId+"_thruDate");
                String plpSwatchImageThruDate = (String)product.get("plpSwatchImageThruDate");
                String pdpSwatchImageThruDate = (String)product.get("pdpSwatchImageThruDate");
                String smallImageThruDate = (String)product.get("smallImageThruDate");
                String smallImageAltThruDate = (String)product.get("smallImageAltThruDate");
                String thumbImageThruDate = (String)product.get("thumbImageThruDate");
                String largeImageThruDate = (String)product.get("largeImageThruDate");
                String detailImageThruDate = (String)product.get("detailImageThruDate");
                String pdpVideoUrlThruDate = (String)product.get("pdpVideoUrlThruDate");
                String pdpVideo360UrlThruDate = (String)product.get("pdpVideo360UrlThruDate");
                String totPdpAdditionalThumbImage = (String)product.get("totPdpAdditionalThumbImage");
                String totPdpAdditionalLargeImage = (String)product.get("totPdpAdditionalLargeImage");
                String totPdpAdditionalDetailImage = (String)product.get("totPdpAdditionalDetailImage");
                String giftMessage = (String)product.get("giftMessage");
                String pdpQtyMin = (String)product.get("pdpQtyMin");
                String pdpQtyMax = (String)product.get("pdpQtyMax");
                String pdpQtyDefault = (String)product.get("pdpQtyDefault");
                
                if(UtilValidate.isNotEmpty(product.get("totSelectableFeatures")))
                {
                	totalSelectableFeature = Integer.parseInt((String)product.get("totSelectableFeatures"));
                }
                
                if(UtilValidate.isNotEmpty(product.get("totDescriptiveFeatures")))
                {
                	totalDescriptiveFeature = Integer.parseInt((String)product.get("totDescriptiveFeatures"));
                }
                
                if(UtilValidate.isNotEmpty(masterProductId))
                {
                    if(!OsafeAdminUtil.isValidId(masterProductId))
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Master Product ID", "idData", masterProductId), locale));
                    }
                }
                if(UtilValidate.isNotEmpty(productId))
                {
                    if(!OsafeAdminUtil.isValidId(productId))
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Product ID", "idData", productId), locale));
                    }
                }
                
                if(UtilValidate.isNotEmpty(masterProductId) && masterProductId.length() > 20)
                {
                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Master Product ID", "fieldData", masterProductId), locale));
                }
                if(UtilValidate.isNotEmpty(productId) && productId.length() > 20)
                {
                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Product ID", "fieldData", productId), locale));
                }
                if(UtilValidate.isEmpty(masterProductId))
                {
                    productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "MasterProductIdMissingError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                }
                if(UtilValidate.isNotEmpty(masterProductId))
                {
                    newProductIdList.add(masterProductId);
                }
                //Check that if there is any varaint associated with virtual product either in feed file or in DB
                if(UtilValidate.isNotEmpty(masterProductId) && UtilValidate.isNotEmpty(productId) && masterProductId.equals(productId))
                {
                	boolean variantProductExist = false;
                	if(variantProductIdMap.containsKey(masterProductId))
                	{
                		variantProductExist = true;	
                	}
                	else
                	{
                		List productAssocs = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId",masterProductId, "productAssocTypeId", "PRODUCT_VARIANT"));
                		productAssocs = EntityUtil.filterByDate(productAssocs);
                		if(productAssocs.size() > 0)
                		{
                			variantProductExist = true;
                		}
                	}
                	if(!variantProductExist)
                	{
                		productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "VariantProductNotExistsError", UtilMisc.toMap("rowNo", rowNo.toString(), "masterProductId", masterProductId), locale));
                	}
                }
                
                
                if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                {
                    if(masterProductId.equals(productId)) 
                    {
                        masterProductIdMap.put(masterProductId, masterProductId);
                        if(!virtualFinishProductIdList.contains(masterProductId))
                        {
                            virtualFinishProductIdList.add(masterProductId);
                        }
                        else
                        {
                            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "VirtualFinishProductIdExistingError", UtilMisc.toMap("rowNo", rowNo.toString(), "masterProductId", masterProductId, "productId", productId), locale));
                        }
                        
                    }
                }
                
                if(UtilValidate.isEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                {
                    if(!virtualFinishProductIdList.contains(masterProductId))
                    {
                        virtualFinishProductIdList.add(masterProductId);
                    }
                    else
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "VirtualFinishProductIdExistingError", UtilMisc.toMap("rowNo", rowNo.toString(), "masterProductId", masterProductId), locale));
                    }
                }
                
                if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                {
                    if(!masterProductId.equals(productId)) 
                    {
                        boolean virtualProductExists = false;
            	        if(masterProductIdMap.containsKey(masterProductId))
            	        {
            	            virtualProductExists = true;
            	        }
            	        else
            	        {
            	            if(ProductWorker.isVirtual(_delegator, masterProductId))
            	            {
            	                virtualProductExists = true;
            	            } 
            	        }
            	        
            	        if(!virtualProductExists)
            	        {
            	            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidVirtualProductReferenceError", UtilMisc.toMap("rowNo", rowNo.toString(), "productId", productId, "masterProductId", masterProductId), locale));
            	        }
                    }
                }

                if(UtilValidate.isNotEmpty(productCategoryId))
                {
                   List<String> productCategoryIdList = StringUtil.split(productCategoryId,",");
                   boolean categoryIdMatch = true;
                   for (String productCatId: productCategoryIdList) 
                   {
                       categoryIdMatch = false;
                       if(newProdCatIdList.contains(productCatId.trim()))
                       {
                           categoryIdMatch = true;
                       }
                       if(!categoryIdMatch)
                       {
                           if(existingProdCatIdList.contains(productCatId.trim()))
                           {
                               categoryIdMatch = true;
                           } 
                       }
                       if(!categoryIdMatch)
                       {
                           productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "CategoryIdMatchingError", UtilMisc.toMap("rowNo", rowNo.toString(), "categoryId", productCatId), locale));
                       }
                   }
                }
                
                
                //If VIRTUAL a long description must be entered
                if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                {
                    if(masterProductId.equals(productId)) 
                    {
                        if(UtilValidate.isEmpty(longDescription))
                        {    
                        	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankLongDescError", UtilMisc.toMap("rowNo", rowNo.toString(), "productType", "VIRTUAL"), locale));
                        }
                    }
                }
                else
                {
                    //If FINISHED GOOD a long description must be entered
                    if(UtilValidate.isEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                    {
                        if(UtilValidate.isEmpty(longDescription))
                        {    
                        	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankLongDescError", UtilMisc.toMap("rowNo", rowNo.toString(), "productType", "FINISHED-GOOD"), locale));
                        }
                    }
                }
                
                if(UtilValidate.isNotEmpty(fromDate))
                {
                    if(!OsafeAdminUtil.isValidDate(fromDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidProductIntroDateError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", fromDate, "idData", productId), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(thruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(thruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidProductDiscoDateError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", thruDate, "idData", productId), locale));
                    }    
                }
                
                
               if(UtilValidate.isNotEmpty(listPriceFromDate))
                {
                    if(!OsafeAdminUtil.isValidDate(listPriceFromDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "From", "idData", listPriceFromDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(listPriceThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(listPriceThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", listPriceThruDate), locale));
                    }    
                }
                
                if(UtilValidate.isNotEmpty(defaultPriceFromDate))
                {
                    if(!OsafeAdminUtil.isValidDate(defaultPriceFromDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "From", "idData", defaultPriceFromDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(defaultPriceThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(defaultPriceThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", defaultPriceThruDate), locale));
                    }    
                }
                
                if(UtilValidate.isNotEmpty(categoryFromDate))
                {
                    if(!OsafeAdminUtil.isValidDate(categoryFromDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "From", "idData", categoryFromDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(categoryThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(categoryThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", categoryThruDate), locale));
                    }    
                }
                
                if(UtilValidate.isNotEmpty(plpSwatchImageThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(plpSwatchImageThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", plpSwatchImageThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(pdpSwatchImageThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(pdpSwatchImageThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", pdpSwatchImageThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(smallImageThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(smallImageThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", smallImageThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(smallImageAltThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(smallImageAltThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", smallImageAltThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(thumbImageThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(thumbImageThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", thumbImageThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(largeImageThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(largeImageThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", largeImageThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(detailImageThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(detailImageThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", detailImageThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(pdpVideoUrlThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(pdpVideoUrlThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", pdpVideoUrlThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(pdpVideo360UrlThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(pdpVideo360UrlThruDate))
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", pdpVideo360UrlThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(totPdpAdditionalThumbImage))
                {
                  for(int i=1;i<=Integer.parseInt(totPdpAdditionalThumbImage);i++)
                  {
                	String addImageThruDate = (String)product.get("addImage"+i+"ThruDate");
                	if(UtilValidate.isNotEmpty(addImageThruDate))
                    {
                		if(!OsafeAdminUtil.isValidDate(addImageThruDate))
                        {
                        	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", addImageThruDate), locale));
                        }  
                    }
                  }
                }
                if(UtilValidate.isNotEmpty(totPdpAdditionalLargeImage))
                {	
                  for(int i=1;i<=Integer.parseInt(totPdpAdditionalLargeImage);i++)
                  {
                	String xtraLargeImageThruDate = (String)product.get("xtraLargeImage"+i+"ThruDate");
                	if(UtilValidate.isNotEmpty(xtraLargeImageThruDate))
                    {
                		if(!OsafeAdminUtil.isValidDate(xtraLargeImageThruDate))
                        {
                        	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", xtraLargeImageThruDate), locale));
                        }  
                    }
                  }
                }
                if(UtilValidate.isNotEmpty(totPdpAdditionalLargeImage))
                {
                  for(int i=1;i<=Integer.parseInt(totPdpAdditionalDetailImage);i++)
                  {
                	String xtraDetailImageThruDate = (String)product.get("xtraDetailImage"+i+"ThruDate");
                	if(UtilValidate.isNotEmpty(xtraDetailImageThruDate))
                    {
                		if(!OsafeAdminUtil.isValidDate(xtraDetailImageThruDate))
                        {
                        	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", xtraDetailImageThruDate), locale));
                        }  
                    }
                  }
                }
                
                //If VIRTUAL a sales price must be entered
                if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                {
                    if(masterProductId.equals(productId)) 
                    {
                        if(UtilValidate.isEmpty(defaultPrice))
                        {    
                             productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "EmptySalesPriceError", UtilMisc.toMap("rowNo", rowNo.toString(), "productType", "VIRTUAL"), locale));
                        }
                    }
                }
                else
                {
                    //If FINISHED GOOD a sales price must be entered
                    if(UtilValidate.isEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                    {
                        if(UtilValidate.isEmpty(defaultPrice))
                        {    
                             productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "EmptySalesPriceError", UtilMisc.toMap("rowNo", rowNo.toString(), "productType", "FINISHED-GOOD"), locale));
                        }
                    }
                }
                
                //If entered check if List Price is a valid float
                if(UtilValidate.isNotEmpty(listPrice))
                {
                    boolean checkFloatResult = OsafeAdminUtil.isFloat(listPrice);
                    if(!checkFloatResult)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidListPriceError", UtilMisc.toMap("rowNo", rowNo.toString(), "listPrice", listPrice), locale));
                    }
                }
                
                //If entered check if Sales Price is a valid float
                if(UtilValidate.isNotEmpty(defaultPrice))
                {
                    boolean checkFloatResult = OsafeAdminUtil.isFloat(defaultPrice);
                    if(!checkFloatResult)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidSalesPriceError", UtilMisc.toMap("rowNo", rowNo.toString(), "salesPrice", defaultPrice), locale));
                    }
                }
                if(UtilValidate.isNotEmpty(manufacturerId))
                {
                    boolean manufacturerIdMatch = false;
                
                    if(newManufacturerIdList.contains(manufacturerId) || existingManufacturerIdList.contains(manufacturerId))
                    {
                        manufacturerIdMatch = true;
                    }
                    if(!manufacturerIdMatch)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ManufacturerIdMatchingError", UtilMisc.toMap("rowNo", rowNo.toString(), "manuId", manufacturerId), locale));
                    }
                }
                
                if(UtilValidate.isNotEmpty(plpImage))
                {
                    boolean isPlpImageExist = (new File(osafeThemeImagePath, plpImage)).exists();
                    if(!UtilValidate.isUrl(plpImage))
                	{
                    	if(!isPlpImageExist)
                        {
                            productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PLPImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo.toString(), "plpImageData", plpImage), locale));
                        }
                	}
                }
                if(UtilValidate.isNotEmpty(pdpRegularImage))
                {
                    boolean isPdpRegularImageExist = (new File(osafeThemeImagePath, pdpRegularImage)).exists();
                    if(!UtilValidate.isUrl(pdpRegularImage))
                	{
                    	if(!isPdpRegularImageExist)
                        {
                            productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "PDPRegularImageNotFoundWarning", UtilMisc.toMap("rowNo", rowNo.toString(), "pdpRegularImage", pdpRegularImage), locale));
                        }	
                	}
                    
                }
                   
                if(UtilValidate.isNotEmpty(bfWHInventory))
                {
                    boolean bfWHInventoryVaild = UtilValidate.isSignedInteger(bfWHInventory);
                    if(!bfWHInventoryVaild)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFWHInventoryRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                    }
                    else
                    {
                        if(Integer.parseInt(bfWHInventory) < -9999 || Integer.parseInt(bfWHInventory) > 99999)
                        {
                            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFWHInventoryRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                        } 
                    }
                }   
                  
                if(UtilValidate.isNotEmpty(bfTotalInventory))
                {
                    boolean bfTotalInventoryVaild = UtilValidate.isSignedInteger(bfTotalInventory);
                    if(!bfTotalInventoryVaild)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFTotalInventoryRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                    }
                    else
                    {
                        if(Integer.parseInt(bfTotalInventory) < -9999 || Integer.parseInt(bfTotalInventory) > 99999)
                        {
                            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFTotalInventoryRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                        }
                        else
                        {
                            if((UtilValidate.isNotEmpty(bfWHInventory)) && (Integer.parseInt(bfTotalInventory) <  Integer.parseInt(bfWHInventory)))
                            {
                                productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidBFTotalInventoryRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                            }
                        }
                    }
                }
                    
                if(UtilValidate.isNotEmpty(internalName))
                {
            		if(internalName.length() > 255)
            		{
            			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InternalNameLengthExceedRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
            		}
            		if(!OsafeAdminUtil.isValidName(internalName))
                    {
            			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InternalNameInvalidRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                    }
                    List itenNoRowList = FastList.newInstance();
                    if(itenNoMap.get(internalName) != null)
                    {
                        itenNoRowList = (List)itenNoMap.get(internalName);
                    } 
                    else 
                    {
                        itenNoRowList = FastList.newInstance();
                    }
                    itenNoRowList.add(rowNo);
                    itenNoMap.put(internalName,itenNoRowList);
                }
                
            	if(UtilValidate.isNotEmpty(productName))
            	{
            		if(productName.length() > 100)
            		{
            			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductNameLengthExceedRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
            		}
            		if(!OsafeAdminUtil.isValidName(productName))
                    {
            			productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductNameInvalidRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                    }
            		
            		if(UtilValidate.isNotEmpty(masterProductId) && ((UtilValidate.isEmpty(productId) || masterProductId.equals(productId))))
            		{	
               		  List prodNoRowList = FastList.newInstance();
                      if(prodNoMap.get(productName) != null)
                      {
                    	  prodNoRowList = (List)prodNoMap.get(productName);
                      } 
                      else 
                      {
                    	  prodNoRowList = FastList.newInstance();
                      }
                      prodNoRowList.add(rowNo);
                      prodNoMap.put(productName,prodNoRowList);
            	   }
            		
            	}
            	for(int i = 1; i <= totalSelectableFeature; i++)
                {
            		if(UtilValidate.isNotEmpty(product.get("selectabeFeature_"+i)))
            		{
            			String parseSelectabeFeatureType = (String)product.get("selectabeFeature_"+i);
            			int iSelIdx = parseSelectabeFeatureType.indexOf(':');
            			if(iSelIdx > -1)
            			{
            				String selectabeFeature = parseSelectabeFeatureType.substring(0,iSelIdx).trim();
            				if(UtilValidate.isNotEmpty(selectabeFeature))
                            {
                                if(!OsafeAdminUtil.isValidFeature(selectabeFeature))
                                {
                                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidFeatureError", UtilMisc.toMap("rowNo", rowNo.toString(), "featureData", selectabeFeature), locale));
                                }    
                            }
            				String featureFromDate= (String)product.get(selectabeFeature+"_fromDate");
            	            String featureThruDate= (String)product.get(selectabeFeature+"_thruDate");

            	            if(UtilValidate.isNotEmpty(featureFromDate))
                            {
                                if(!OsafeAdminUtil.isValidDate(featureFromDate))
                                {
                                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "From", "idData", featureFromDate), locale));
                                }    
                            }
            	            if(UtilValidate.isNotEmpty(featureThruDate))
                            {
                                if(!OsafeAdminUtil.isValidDate(featureThruDate))
                                {
                                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", featureThruDate), locale));
                                }    
                            }
            			}
            		}
                }
            	for(int i = 1; i <= totalDescriptiveFeature; i++)
                {
            		if(UtilValidate.isNotEmpty(product.get("descriptiveFeature_"+i)))
            		{	
            			String parseDescriptiveFeatureType = (String)product.get("descriptiveFeature_"+i);
            			int iDescIdx = parseDescriptiveFeatureType.indexOf(':');
            			if(iDescIdx > -1)
            			{	
            				String descriptiveFeature = parseDescriptiveFeatureType.substring(0,iDescIdx).trim();
            				
            				String featureFromDate= (String)product.get(descriptiveFeature+"_fromDate");
            	            String featureThruDate= (String)product.get(descriptiveFeature+"_thruDate");
            	            if(UtilValidate.isNotEmpty(descriptiveFeature))
                            {
                                if(!OsafeAdminUtil.isValidFeature(descriptiveFeature))
                                {
                                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidFeatureError", UtilMisc.toMap("rowNo", rowNo.toString(), "featureData", descriptiveFeature), locale));
                                }    
                            }
            	            if(UtilValidate.isNotEmpty(featureFromDate))
                            {
                                if(!OsafeAdminUtil.isValidDate(featureFromDate))
                                {
                                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "From", "idData", featureFromDate), locale));
                                }    
                            }
            	            if(UtilValidate.isNotEmpty(featureThruDate))
                            {
                                if(!OsafeAdminUtil.isValidDate(featureThruDate))
                                {
                                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", featureThruDate), locale));
                                }    
                            }
            			}
            		}
                }
                for(int i = 1; i <= totalSelectableFeature; i++)
                {
            		if(UtilValidate.isNotEmpty(product.get("selectabeFeature_"+i)))
            		{
            		  String parseSelectabeFeatureType = (String)product.get("selectabeFeature_"+i);
            		  if(UtilValidate.isNotEmpty(parseSelectabeFeatureType))
            		  {
            			  for(int j = 1; j <= totalDescriptiveFeature; j++)
                          {
                			String parseDescriptiveFeatureType = (String)product.get("descriptiveFeature_"+j);
                			if(UtilValidate.isNotEmpty(parseDescriptiveFeatureType))
                			{
                				int iSelIdx = parseSelectabeFeatureType.indexOf(':');
                    			int iDescIdx = parseDescriptiveFeatureType.indexOf(':');
                    	        if (iSelIdx > -1 && iDescIdx > -1)
                    	        {
                    	            String selectabeFeature = parseSelectabeFeatureType.substring(0,iSelIdx).trim();
                    	            String descriptiveFeature = parseDescriptiveFeatureType.substring(0,iDescIdx).trim();
                    	            if(selectabeFeature.equals(descriptiveFeature))
                        	        {
                        	           productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "DuplicateFeatureError", UtilMisc.toMap("rowNo", rowNo.toString(), "idData", productId), locale));
                        	        }
                    	        }
                			}
                			
                	      }  
            		  }
            		  
            		}
                 }
                
                boolean selectableFeatureExist = false; 
            	for(int j = 1; j <= totalSelectableFeature; j++)
                {
            		if(UtilValidate.isNotEmpty(product.get("selectabeFeature_"+j)))
            		{
            			selectableFeatureExist = true;
            			String parseFeatureType = (String)product.get("selectabeFeature_"+j);
            			int iFeatIdx = parseFeatureType.indexOf(':');
            	        if (iFeatIdx > -1)
            	        {
            	            String featureType = parseFeatureType.substring(0,iFeatIdx).trim();
            	            String sFeatures = parseFeatureType.substring(iFeatIdx +1);
            	            String[] featureTokens = sFeatures.split(",");
            	            for (int f=0;f < featureTokens.length;f++)
            	            { 
            					String featureTypeFeatureId = featureType+":"+featureTokens[f].trim();
            					String tempFeatureTypeFeatureId = (featureType+""+featureTokens[f].trim()).replaceAll(" ", "_");
            					//Removing this ID check, we need a beter plan in generating the feature ID
//            					if(!OsafeAdminUtil.isValidId(tempFeatureTypeFeatureId))
//            					{
//            						productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "Feature", "idData", featureTokens[f].trim()), locale));
//            					}
            	                productFeatureSet.add(featureType+":"+featureTokens[f].trim());
            	            }
            	        }
            		}
                }
            	for(int j = 1; j <= totalDescriptiveFeature; j++)
                {
            		if(UtilValidate.isNotEmpty(product.get("descriptiveFeature_"+j)))
            		{
            			String parseFeatureType = (String)product.get("descriptiveFeature_"+j);
            			int iFeatIdx = parseFeatureType.indexOf(':');
            	        if (iFeatIdx > -1)
            	        {
            	            String featureType = parseFeatureType.substring(0,iFeatIdx).trim();
            	            String sFeatures = parseFeatureType.substring(iFeatIdx +1);
            	            String[] featureTokens = sFeatures.split(",");
            	            for (int f=0;f < featureTokens.length;f++)
            	            { 
            	            	String featureTypeFeatureId = featureType+":"+featureTokens[f].trim();
            	            	String tempFeatureTypeFeatureId = (featureType+""+featureTokens[f].trim()).replaceAll(" ", "_");
            					//Removing this ID check, we need a beter plan in generating the feature ID
//            					if(!OsafeAdminUtil.isValidId(tempFeatureTypeFeatureId))
//            					{
//            						productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo, "idField", "Feature", "idData", featureTokens[f].trim()), locale));
//            					}
            	                productFeatureSet.add(featureType+":"+featureTokens[f].trim());
            	            }
            	        }
            		}
                }
            	
            	//If VIRTUAL a Selectable Feature must not be entered
            	if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
                {
                    if(masterProductId.equals(productId)) 
                    {
                    	if(selectableFeatureExist)
                    	{
                    		productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "VirtualSelectableFeatureError", UtilMisc.toMap("rowNo", rowNo.toString(), "idData", masterProductId, "productType", "VIRTUAL"), locale));
                    	}
                    }
                }
            	else
            	{
            		//If FINISHED GOOD a Selectable Feature must not be entered
            		if(UtilValidate.isEmpty(productId) && UtilValidate.isNotEmpty(masterProductId))
            		{
            	        if(selectableFeatureExist)
            	        {
            	            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "VirtualSelectableFeatureError", UtilMisc.toMap("rowNo", rowNo.toString(), "idData", masterProductId, "productType", "FINISHED-GOOD"), locale));
            	        }
            	    }	
            	}
            	
                if(UtilValidate.isNotEmpty(multiVariant) && !(multiVariant.equalsIgnoreCase("NONE")|| multiVariant.equalsIgnoreCase("CHECKBOX")||multiVariant.equalsIgnoreCase("QTY")))
                {
                     productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidMultiVariantError", UtilMisc.toMap("rowNo", rowNo.toString(), "idData", productId), locale));
                }
                
                if(UtilValidate.isNotEmpty(productHeight))
                {
                    boolean checkFloatResult = OsafeAdminUtil.isFloat(productHeight);
                    if(!checkFloatResult)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidNumberError", UtilMisc.toMap("rowNo", rowNo.toString(),  "idField", "Product Height","idData", productHeight), locale));
                    }
                }
                
                if(UtilValidate.isNotEmpty(productWidth))
                {
                    boolean checkFloatResult = OsafeAdminUtil.isFloat(productWidth);
                    if(!checkFloatResult)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidNumberError", UtilMisc.toMap("rowNo", rowNo.toString(),  "idField", "Product Width","idData", productWidth), locale));
                    }
                }
                
                if(UtilValidate.isNotEmpty(productDepth))
                {
                    boolean checkFloatResult = OsafeAdminUtil.isFloat(productDepth);
                    if(!checkFloatResult)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidNumberError", UtilMisc.toMap("rowNo", rowNo.toString(),  "idField", "Product Depth","idData", productDepth), locale));
                    }
                }
                
                if(UtilValidate.isNotEmpty(productWeight))
                {
                    boolean checkFloatResult = OsafeAdminUtil.isFloat(productWeight);
                    if(!checkFloatResult)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidNumberError", UtilMisc.toMap("rowNo", rowNo.toString(),  "idField", "Product Weight","idData", productWeight), locale));
                    }
                }
                
                if((UtilValidate.isNotEmpty(pdpQtyMin) && UtilValidate.isEmpty(pdpQtyMax)) || (UtilValidate.isEmpty(pdpQtyMin) && UtilValidate.isNotEmpty(pdpQtyMax)))
                {
                	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankPdpQtyMinMaxRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                }
                
                boolean pdpQtyMinVaild = false;
                if(UtilValidate.isNotEmpty(pdpQtyMin))
                {
                    pdpQtyMinVaild = OsafeAdminUtil.isNumber(pdpQtyMin);
                    if(!pdpQtyMinVaild)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidPdpQtyMinRowError", UtilMisc.toMap("rowNo", rowNo.toString(), "pdpQtyMin", pdpQtyMin), locale));
                    }
                    else
                    {
                        if(Integer.parseInt(pdpQtyMin) <= 0)
                        {
                            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidPdpQtyMinRowError", UtilMisc.toMap("rowNo", rowNo.toString(), "pdpQtyMin", pdpQtyMin), locale));
                        } 
                    }
                }   
                
                boolean pdpQtyMaxVaild = false;
                if(UtilValidate.isNotEmpty(pdpQtyMax))
                {
                    pdpQtyMaxVaild = OsafeAdminUtil.isNumber(pdpQtyMax);
                    if(!pdpQtyMaxVaild)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidPdpQtyMaxRowError", UtilMisc.toMap("rowNo", rowNo.toString(), "pdpQtyMax", pdpQtyMax), locale));
                    }
                    else
                    {
                        if(Integer.parseInt(pdpQtyMax) <= 0 )
                        {
                            productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidPdpQtyMaxRowError", UtilMisc.toMap("rowNo", rowNo.toString(), "pdpQtyMax", pdpQtyMax), locale));
                        }
                        else
                        {
                            if((UtilValidate.isNotEmpty(pdpQtyMin)) && (Integer.parseInt(pdpQtyMax) <  Integer.parseInt(pdpQtyMin)))
                            {
                                productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidPdpQtyMaxMinRowError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                            }
                        }
                    }
                }
                if(UtilValidate.isNotEmpty(pdpQtyDefault))
                {
                    boolean pdpQtyDefaultVaild = OsafeAdminUtil.isNumber(pdpQtyDefault);
                    if(!pdpQtyDefaultVaild)
                    {
                        productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidPdpQtyDefaultRowError", UtilMisc.toMap("rowNo", rowNo.toString(), "pdpQtyDefault", pdpQtyDefault), locale));
                    }
                }
                
                
                rowNo++;
            }
            for (Map.Entry<String, List> entry : itenNoMap.entrySet()) 
            {
                List<Integer> itenNoRowList = (List)entry.getValue();
                String internalName = (String)entry.getKey();
                if(itenNoRowList.size() > 1)
                {
                    for(Integer itemRowNo : itenNoRowList)
                    {
                        productWarningList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "UniqueItemNoWarning", UtilMisc.toMap("rowNo", itemRowNo.toString(), "internalName", internalName), locale));
                    }
                }
            }
            for (Map.Entry<String, List> entry : prodNoMap.entrySet()) 
            {
                List<Integer> prodNoRowList = (List)entry.getValue();
                if(prodNoRowList.size() > 1)
                {
                    for(Integer prodRowNo : prodNoRowList)
                    {
                    	productErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "UniqueProductNameError", UtilMisc.toMap("rowNo", prodRowNo.toString()), locale));
                    }
                }
            }
            

            //Validation for Product Associations
            rowNo = new Integer(1);
            List existingProductList = _delegator.findList("Product", null, null, null, null, false);
            if(UtilValidate.isNotEmpty(existingProductList))
            {
            	existingProductList = EntityUtil.filterByAnd(existingProductList, UtilMisc.toMap("isVariant" , "N"));
                existingProductIdList = EntityUtil.getFieldListFromEntityList(existingProductList, "productId", true);
            }
            for(Map productAssoc : productAssocDataList) 
            {
                String productId = (String)productAssoc.get("productId");
                String productIdTo = (String)productAssoc.get("productIdTo");
                String thruDate = (String)productAssoc.get("thruDate");
                String fromDate = (String)productAssoc.get("fromDate");
                boolean productIdMatch = false;
                boolean productIdToMatch = false;
                
                if(newProductIdList.contains(productId) || existingProductIdList.contains(productId))
                {
                    productIdMatch = true;
                }
                if(!productIdMatch)
                {
                    productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductIdMatchingError", UtilMisc.toMap("rowNo", rowNo.toString(), "productId", productId), locale));
                }
                
                if(newProductIdList.contains(productIdTo) || existingProductIdList.contains(productIdTo))
                {
                    productIdToMatch = true;
                }
                if(!productIdToMatch)
                {
                    productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductIdToMatchingError", UtilMisc.toMap("rowNo", rowNo.toString(), "productIdTo", productIdTo), locale));
                }
                
                if(UtilValidate.isNotEmpty(fromDate))
                {
                    if(!OsafeAdminUtil.isValidDate(fromDate))
                    {
                    	productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidProductAssocFromDateError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", fromDate, "idData", productId), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(thruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(thruDate))
                    {
                    	productAssocErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidProductAssocThruDateError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", thruDate, "idData", productId), locale));
                    }    
                }
                
                rowNo++;
            }

            //Validation for Product Feature Swatch
            rowNo = new Integer(1);
            for(Map productFeatureSwatch : productFeatureSwatchDataList) 
            {
                String productFeatureId = (String)productFeatureSwatch.get("featureId");
                boolean productFeatureIdMatch = false;
                if(UtilValidate.isNotEmpty(productFeatureId))
                {
                	String[] tempProductFeatureIdArray = null;
                	int countSemicolon = productFeatureId.indexOf(':');

                	String tempProductFeatureId = productFeatureId;
            		String featureValue = "";
            		if(countSemicolon > -1)
            		{
            			//we expect that spaces MIGHT come after the semicolon and we ALWAYS expect one semicolon (ex. "COLOR:Animal Print")
            			tempProductFeatureIdArray = tempProductFeatureId.split(":");
            			if(tempProductFeatureIdArray.length == 2)
            			{
            				featureValue = tempProductFeatureIdArray[1];
            				tempProductFeatureId = tempProductFeatureIdArray[0] + tempProductFeatureIdArray[1].replaceAll(" ", "_");
            			}
            		}
            		
                    if(productFeatureSet.contains(productFeatureId))
                    {
                    	productFeatureIdMatch = true;
                    }
                    else
                    {
                    	if(UtilValidate.isNotEmpty(productFeatureIds))
                    	{
            	        	mFeatureTypeMap.clear();
            	        	OsafeProductLoaderHelper.buildFeatureMap(mFeatureTypeMap, productFeatureId, _delegator);
            	        	
            	        	Set featureTypeSet = mFeatureTypeMap.keySet();
            	        	Map mFeatureIdImageExists = FastMap.newInstance();
            	    		Iterator iterFeatureType = featureTypeSet.iterator();
            	    		while (iterFeatureType.hasNext())
            	    		{
            	    			String featureType =(String)iterFeatureType.next();
            	    			String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
            	    			if (featureTypeId.length() > 20)
            	    			{
            	    				featureTypeId=featureTypeId.substring(0,20);
            	    			}
            	    			FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
            	        		Set featureSet = mFeatureMap.keySet();
            	        		Iterator iterFeature = featureSet.iterator();
            	        		while (iterFeature.hasNext())
            	        		{
            	        			String featureId =(String)iterFeature.next();
            	        			/*String featureId =StringUtil.removeSpaces(feature).toUpperCase();
            	        			featureId =StringUtil.replaceString(featureId, "&", "");
            	        			featureId=featureTypeId+"_"+featureId;
            	        			if (featureId.length() > 20)
            	        			{
            	        				featureId=featureId.substring(0,20);
            	        			}*/
            	        			if(productFeatureIds.contains(featureId))
            	        			{
            	        			    productFeatureIdMatch = true;	
            	        			}
            	        		}
            	    		}
                    	}
                    }
                    if(!productFeatureIdMatch)
                    {
                    	productFeatureSwatchErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductFeatureMatchError", UtilMisc.toMap("rowNo", rowNo.toString(), "featureId", featureValue), locale));
                    }
                }
                rowNo++;
            }


            //Validation for Product Manufacturers
            rowNo = new Integer(1);
            for(Map manufacturer : manufacturerDataList) 
            {
                String manufacturerId = (String)manufacturer.get("partyId");
                String manufacturerImageThruDate = (String)manufacturer.get("manufacturerImageThruDate");
                String manufacturerState = (String)manufacturer.get("state");
                String manufacturerCountry = (String)manufacturer.get("country");
                
                if(UtilValidate.isNotEmpty(manufacturerId))
                {
                    if(!OsafeAdminUtil.isValidId(manufacturerId))
                    {
                        productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidIdError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "ManuId", "idData", manufacturerId), locale));
                    }
                    if(manufacturerId.length() > 20)
                    {
                    	productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "IdLengthExceedError", UtilMisc.toMap("rowNo", rowNo.toString(), "idField", "Manu ID", "fieldData", manufacturerId), locale));
                    }
                }
                else
                {
                	productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "BlankManuIdError", UtilMisc.toMap("rowNo", rowNo.toString()), locale));
                }
                if(UtilValidate.isNotEmpty(manufacturerImageThruDate))
                {
                    if(!OsafeAdminUtil.isValidDate(manufacturerImageThruDate))
                    {
                    	productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InvalidDateError", UtilMisc.toMap("idField", "Thru", "idData", manufacturerImageThruDate), locale));
                    }    
                }
                if(UtilValidate.isNotEmpty(manufacturerState))
                {
                	GenericValue gvManufacturerState = _delegator.findByPrimaryKey("Geo", UtilMisc.toMap("geoId", manufacturerState)); 
                	if(UtilValidate.isEmpty(gvManufacturerState) || (!("STATE".equalsIgnoreCase(gvManufacturerState.getString("geoTypeId"))) && !("PROVINCE".equalsIgnoreCase(gvManufacturerState.getString("geoTypeId")))))
                    {
                		productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ManufacturerStateInvalidError", UtilMisc.toMap("rowNo", rowNo.toString(), "state", manufacturerState, "manufacturerId", manufacturerId), locale));
                    }
                }
                if(UtilValidate.isNotEmpty(manufacturerCountry))
                {
                	GenericValue gvManufacturerCountry = _delegator.findByPrimaryKey("Geo", UtilMisc.toMap("geoId", manufacturerCountry)); 
                	if(UtilValidate.isEmpty(gvManufacturerCountry) || !("COUNTRY".equalsIgnoreCase(gvManufacturerCountry.getString("geoTypeId"))))
                    {
                		productManufacturerErrorList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ManufacturerCountryInvalidError", UtilMisc.toMap("rowNo", rowNo.toString(), "country", manufacturerCountry, "manufacturerId", manufacturerId), locale));
                    }
                }
                
                
                rowNo++;
            }
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	errorMessageList.add(e.getMessage());
        }
        
        result.put("prodCatErrorList", prodCatErrorList);
        result.put("prodCatWarningList", prodCatWarningList);
        result.put("productErrorList", productErrorList);
        result.put("productWarningList", productWarningList);
        result.put("productAssocErrorList", productAssocErrorList);
        result.put("productAssocWarningList", productAssocWarningList);
        result.put("productFeatureSwatchErrorList", productFeatureSwatchErrorList);
        result.put("productFeatureSwatchWarningList", productFeatureSwatchWarningList);
        result.put("productManufacturerErrorList", productManufacturerErrorList);
        result.put("productManufacturerWarningList", productManufacturerWarningList);
        result.put("errorMessageList", errorMessageList);
        
        return result;
    }
    
    public static Map<String, Object> getProductDataListFromFile(DispatchContext ctx, Map<String, ?> context) 
    {
    	
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        List<Map> productCatDataList = FastList.newInstance();
        List<Map> productDataList = FastList.newInstance();
        List<Map> productAssocDataList = FastList.newInstance();
        List<Map> productFeatureSwatchDataList = FastList.newInstance();
        List<Map> manufacturerDataList = FastList.newInstance();
        
        final List<String> errorMessageList = FastList.newInstance();
        
        String productFilePath = (String)context.get("productFilePath");
        String productFileName = (String)context.get("productFileName");
        
        Map result = ServiceUtil.returnSuccess();
        
        if(UtilValidate.isNotEmpty(productFileName) && productFileName.endsWith(".xls"))
        {
          try 
          {
              WorkbookSettings ws = new WorkbookSettings();
              ws.setLocale(new Locale("en", "EN"));
              Workbook wb = Workbook.getWorkbook(new File(productFilePath + productFileName),ws);
              
              // Gets the sheets from workbook
              for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) 
              {
                  BufferedWriter bw = null; 
                  try 
                  {
                      Sheet s = wb.getSheet(sheet);
                      
                      String sTabName=s.getName();
                      if (sheet == 1)
                      {
                      	  List dataRows = OsafeProductLoaderHelper.buildDataRows(ImportServices.buildCategoryHeader(),s);
                          productCatDataList = OsafeProductLoaderHelper.getDataList(dataRows);
                      }
                      if (sheet == 2)
                      {
                      	  List dataRows = OsafeProductLoaderHelper.buildDataRows(ImportServices.buildProductHeader(),s);
                          productDataList = OsafeProductLoaderHelper.getDataList(dataRows);
                      }
                      if (sheet == 3)
                      {
                      	  List dataRows = OsafeProductLoaderHelper.buildDataRows(ImportServices.buildProductAssocHeader(),s);
                          productAssocDataList = OsafeProductLoaderHelper.getDataList(dataRows);
                      }
                      if (sheet == 4)
                      {
                      	  List dataRows = OsafeProductLoaderHelper.buildDataRows(ImportServices.buildProductFeatureSwatchHeader(),s);
                          productFeatureSwatchDataList = OsafeProductLoaderHelper.getDataList(dataRows);
                      }
                      if (sheet == 5)
                      {
                      	  List dataRows = OsafeProductLoaderHelper.buildDataRows(ImportServices.buildManufacturerHeader(),s);
                          manufacturerDataList = OsafeProductLoaderHelper.getDataList(dataRows);
                      }
                  } 
                  catch (Exception exc) 
                  {
                	  errorMessageList.add(exc.getMessage());
                      Debug.logError(exc, module);
                  } 
              }
          }
          catch (FileNotFoundException fne) 
          {
        	  errorMessageList.add(fne.getMessage());
              Debug.logError(fne, module);
          }
          catch (BiffException be) 
          {
        	  errorMessageList.add(be.getMessage());
              Debug.logError(be, module);
          } 
          catch (Exception exc) 
          {
        	  errorMessageList.add(exc.getMessage());
              Debug.logError(exc, module);
          }
        }
        if(productFileName.endsWith(".xml"))
        {
            try 
            {
	            JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
	            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	            
	            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	            Schema schema = schemaFactory.newSchema(new File(schemaLocation));
	            unmarshaller.setSchema(schema);
	            
	            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler()
	            {
	            	public boolean handleEvent(ValidationEvent ve) 
	            	{  
                        // ignore warnings  
                        if (ve.getSeverity() != ValidationEvent.WARNING) 
                        {  
                            ValidationEventLocator vel = ve.getLocator();
                            errorMessageList.add("Line:Col[" + vel.getLineNumber() +  
                                ":" + vel.getColumnNumber() +  
                                "]:" + ve.getMessage());
                              
                        }  
                        return true;  
                    }
	            }
	            );
	            
	            JAXBElement<BigFishProductFeedType> bfProductFeedType = (JAXBElement<BigFishProductFeedType>)unmarshaller.unmarshal(new File(productFilePath + productFileName));
	                  	
	            List<ProductType> products = FastList.newInstance();
	            List<CategoryType> productCategories = FastList.newInstance();
	            List<AssociationType> productAssociations = FastList.newInstance();
	            List<FeatureSwatchType> productFeatureSwatches = FastList.newInstance();
	            List<ManufacturerType> productManufacturers = FastList.newInstance();
	                  	
	            ProductsType productsType = bfProductFeedType.getValue().getProducts();
	            if(UtilValidate.isNotEmpty(productsType)) 
	            {
	                products = productsType.getProduct();
	                if(products.size() > 0) 
	                {
	                    List dataRows = ImportServices.buildProductXMLDataRows(products);
	                    productDataList = OsafeProductLoaderHelper.getDataList(dataRows);
	                }
	            }
	                  	
	            ProductCategoryType productCategoryType = bfProductFeedType.getValue().getProductCategory();
	            if(UtilValidate.isNotEmpty(productCategoryType)) 
	            {
	                productCategories = productCategoryType.getCategory();
	                if(productCategories.size() > 0) 
	                {
	                    List dataRows = ImportServices.buildProductCategoryXMLDataRows(productCategories);
	                    productCatDataList = OsafeProductLoaderHelper.getDataList(dataRows);
	                }
	            }
	                  	
	            ProductAssociationType productAssociationType = bfProductFeedType.getValue().getProductAssociation();
	            if(UtilValidate.isNotEmpty(productAssociationType)) 
	            {
	                productAssociations = productAssociationType.getAssociation();
	                if(productAssociations.size() > 0) 
	                {
	                    List dataRows = ImportServices.buildProductAssociationXMLDataRows(productAssociations);
	                    productAssocDataList = OsafeProductLoaderHelper.getDataList(dataRows);
	                }
	            }
	                  	
	            ProductFeatureSwatchType productFeatureSwatchType = bfProductFeedType.getValue().getProductFeatureSwatch();
	            if(UtilValidate.isNotEmpty(productFeatureSwatchType)) 
	            {
	                productFeatureSwatches = productFeatureSwatchType.getFeature();
	                if(productFeatureSwatches.size() > 0) 
	                {
	                    List dataRows = ImportServices.buildProductFeatureSwatchXMLDataRows(productFeatureSwatches);
	                    productFeatureSwatchDataList = OsafeProductLoaderHelper.getDataList(dataRows);
	                }
	            }
	                  	
	            ProductManufacturerType productManufacturerType = bfProductFeedType.getValue().getProductManufacturer();
	            if(UtilValidate.isNotEmpty(productManufacturerType)) 
	            {
	                productManufacturers = productManufacturerType.getManufacturer();
	                if(productManufacturers.size() > 0) 
	                {
	                    List dataRows = ImportServices.buildProductManufacturerXMLDataRows(productManufacturers);
	                    manufacturerDataList = OsafeProductLoaderHelper.getDataList(dataRows);
	                }
	            }
            }
            catch (UnmarshalException ume)
            {
            	if(UtilValidate.isNotEmpty(errorMessageList))
	            {
	                result.put("errorMessageList", errorMessageList);
	                return result;
	            }
            	errorMessageList.add(ume.getMessage());
            	Debug.logError(ume, module);
            }
            catch(JAXBException je)
            {
            	errorMessageList.add(je.getMessage());
            	Debug.logError(je, module);
            }
            catch(Exception exc)
            {
            	errorMessageList.add(exc.getMessage());
            	Debug.logError(exc, module);
            }
        }
        result.put("productCatDataList", productCatDataList);
        result.put("productDataList", productDataList);
        result.put("productAssocDataList", productAssocDataList);
        result.put("productFeatureSwatchDataList", productFeatureSwatchDataList);
        result.put("manufacturerDataList", manufacturerDataList);
        result.put("errorMessageList", errorMessageList);
        return result;
    }
    
    public static Map<String, Object> getOrderStatusDataListFromFile(DispatchContext ctx, Map<String, ?> context) 
    {
    	
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        List<Map> orderStatusDataList = FastList.newInstance();
        
        final List<String> errorMessageList = FastList.newInstance();
        
        String orderStatusFilePath = (String)context.get("orderStatusFilePath");
        String orderStatusFileName = (String)context.get("orderStatusFileName");
        
        Map result = ServiceUtil.returnSuccess();
        
        if(orderStatusFileName.endsWith(".xml"))
        {
            try 
            {
	            JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
	            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	            
	            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	            Schema schema = schemaFactory.newSchema(new File(schemaLocation));
	            unmarshaller.setSchema(schema);
	            
	            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler()
	            {
	            	public boolean handleEvent(ValidationEvent ve) 
	            	{  
                        // ignore warnings  
                        if (ve.getSeverity() != ValidationEvent.WARNING) 
                        {  
                            ValidationEventLocator vel = ve.getLocator();
                            errorMessageList.add("Line:Col[" + vel.getLineNumber() +  
                                ":" + vel.getColumnNumber() +  
                                "]:" + ve.getMessage());
                              
                        }  
                        return true;  
                    }
	            }
	            );
	            
	            JAXBElement<BigFishOrderStatusUpdateFeedType> bfOrderStatusUpdateFeedType = (JAXBElement<BigFishOrderStatusUpdateFeedType>)unmarshaller.unmarshal(new File(orderStatusFilePath + orderStatusFileName));
	            
                List<OrderStatusType> orderStatusList = bfOrderStatusUpdateFeedType.getValue().getOrder();
            	
            	if(orderStatusList.size() > 0) 
            	{
            		List dataRows = buildOrderStatusXMLDataRows(orderStatusList);
            		orderStatusDataList = OsafeProductLoaderHelper.getDataList(dataRows);
            	}
            }
            catch (UnmarshalException ume)
            {
            	if(UtilValidate.isNotEmpty(errorMessageList))
	            {
	                result.put("errorMessageList", errorMessageList);
	                return result;
	            }
            	errorMessageList.add(ume.getMessage());
            	Debug.logError(ume, module);
            }
            catch(JAXBException je)
            {
            	errorMessageList.add(je.getMessage());
            	Debug.logError(je, module);
            }
            catch(Exception exc)
            {
            	errorMessageList.add(exc.getMessage());
            	Debug.logError(exc, module);
            }
        }
        result.put("errorMessageList", errorMessageList);
        result.put("orderStatusDataList", orderStatusDataList);
        return result;
    }
    
    
    public static Map<String, Object> validateOrderStatusData(DispatchContext ctx, Map<String, ?> context) 
    {
	    LocalDispatcher dispatcher = ctx.getDispatcher();
	    _delegator = ctx.getDelegator();
	    Locale locale = (Locale) context.get("locale");
	    
	    List<Map> orderStatusDataList = (List) context.get("orderStatusDataList");
	    String productStoreId = (String)context.get("productStoreId");
	    
	    List<String> errorMessageList = FastList.newInstance();
	    
	    Map result = ServiceUtil.returnSuccess();
	    
	    
		
		List<String> orderIdList = FastList.newInstance();
		
		List<String> productStoreIdList =  FastList.newInstance();
	    List<String> productIdList =  FastList.newInstance();
	    
		List<GenericValue> carrierShipmentMethodList = FastList.newInstance();
		try 
		{
			carrierShipmentMethodList = _delegator.findByAnd("ProductStoreShipmentMethView", UtilMisc.toMap());
			
			
			List<GenericValue> orderHeaderList = _delegator.findByAnd("OrderHeader", UtilMisc.toMap());
			if(UtilValidate.isNotEmpty(orderHeaderList))
			{
				orderIdList = EntityUtil.getFieldListFromEntityList(orderHeaderList, "orderId", Boolean.TRUE);
			}
			
            List<GenericValue> productStoreList = _delegator.findList("ProductStore", null, UtilMisc.toSet("productStoreId"), null, null, false);
	    	
        	if(UtilValidate.isNotEmpty(productStoreList))
			{
        		productStoreIdList = EntityUtil.getFieldListFromEntityList(productStoreList, "productStoreId", Boolean.TRUE);
			}
		} 
		catch (GenericEntityException e1) 
		{
			e1.printStackTrace();
		}
	    
	    try
	    {
	    	if(orderStatusDataList.size() > 0) 
	    	{
				for (int i=0 ; i < orderStatusDataList.size() ; i++) 
                {
	                Map mRow = (Map)orderStatusDataList.get(i);
	            	
	                List<String> carrierIdList = FastList.newInstance();
	        		List<String> shippingMethodIdList = FastList.newInstance();
	                List<GenericValue> carrierShipmentMethodListStore = FastList.newInstance();
	                
	                String orderId = (String)mRow.get("orderId");
	                String productStoreIdRow = (String)mRow.get("productStoreId");
	                GenericValue orderHeader = null;
	                Timestamp orderDate = null;
	            	boolean orderIdMatch = true;
	            	boolean productStoreIdMatch = true;
	            	if(UtilValidate.isEmpty(orderId) || !orderIdList.contains(orderId))
	            	{
	            	    orderIdMatch = false;
	            	}
	            	else
	            	{
	            		orderHeader = _delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId)); 
	            		orderDate = orderHeader.getTimestamp("orderDate");
	            	}
	            	
	            	
	            	if(!orderIdMatch)
	            	{
	            	    errorMessageList.add(UtilProperties.getMessage(resource, "OrderIdMatchingError", UtilMisc.toMap("orderId", mRow.get("orderId")), locale)); 
	            	}
	            	else
	            	{
	            		if(UtilValidate.isEmpty(productStoreIdRow) || !productStoreIdList.contains(productStoreIdRow))
		            	{
	            			productStoreIdMatch = false;
	            			errorMessageList.add(UtilProperties.getMessage(resource, "ProductStoreIdMatchingError", UtilMisc.toMap("productStoreId", productStoreIdRow), locale));
		            	}
	            		else
	            		{
	            			if(!productStoreIdRow.equals(orderHeader.getString("productStoreId")))
	            			{
	            				errorMessageList.add(UtilProperties.getMessage(resource, "OrderProductStoreIdMatchingError", UtilMisc.toMap("productStoreId", productStoreIdRow), locale));
	            				productStoreIdMatch = false;
	            			}
	            		}
	            		if(productStoreIdMatch)
	            		{
	            			if(UtilValidate.isNotEmpty(carrierShipmentMethodList)) 
	            			{
	            				carrierShipmentMethodListStore = EntityUtil.filterByAnd(carrierShipmentMethodList, UtilMisc.toMap("productStoreId", productStoreIdRow));
	            				for(GenericValue carrierMethod : carrierShipmentMethodListStore) 
	            				{
	            					carrierIdList.add(carrierMethod.getString("partyId"));
	            					shippingMethodIdList.add(carrierMethod.getString("shipmentMethodTypeId"));
	            				}
	            			}
	            		}
	            	    if(Integer.parseInt((String)mRow.get("totalOrderItems")) > 0) 
			            {
			                for(int orderItemNo = 0; orderItemNo < Integer.parseInt((String)mRow.get("totalOrderItems")); orderItemNo++) 
			            	{
			            	    boolean carrierIdMatch = true;
			            		boolean shippingMethodIdMatch = true;
			            		boolean orderItemStatusMatch = true;
			            		boolean orderItemStatusApproved = false;
			            		String currentOrderItemStatus = "";
			            		List<GenericValue> orderItems = FastList.newInstance();
			            		String orderItemCarrier = "";
			            		String orderItemShipMethod = "";
			            		if(UtilValidate.isNotEmpty(mRow.get("orderItemCarrier_" + (orderItemNo + 1))))
			            		{
			            			orderItemCarrier = (String)mRow.get("orderItemCarrier_" + (orderItemNo + 1));
			            		}
			            		if(UtilValidate.isNotEmpty(mRow.get("orderItemShipMethod_" + (orderItemNo + 1))))
			            		{
			            			orderItemShipMethod = (String)mRow.get("orderItemShipMethod_" + (orderItemNo + 1));
			            		}
			            		
			            		if(UtilValidate.isEmpty(mRow.get("productId_" + (orderItemNo + 1))) && UtilValidate.isEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1))) && UtilValidate.isEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))))
	             		    	{
	             		    		errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemProductSeqShipGroupIdBlankError", UtilMisc.toMap("orderId", mRow.get("orderId")), locale));
	             		    	}
			            		else
			            		{
			            			//IF ONLY SHIP_GROUP_SEQUENCE_ID IS SUPPLIED
			            			if(UtilValidate.isNotEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))) && (UtilValidate.isEmpty(mRow.get("productId_" + (orderItemNo + 1))) && UtilValidate.isEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1)))))
		             		    	{
			            				GenericValue orderItemShipGroup = _delegator.findByPrimaryKey("OrderItemShipGroup", UtilMisc.toMap("orderId", mRow.get("orderId"), "shipGroupSeqId", mRow.get("shipGroupSeqId_" + (orderItemNo + 1))));
			            				if(UtilValidate.isNotEmpty(orderItemShipGroup))
			            				{
			            					List<GenericValue> orderItemShipGroupAssocs = orderItemShipGroup.getRelated("OrderItemShipGroupAssoc");
			            					if(UtilValidate.isNotEmpty(orderItemShipGroupAssocs))
			            					{
			            						for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocs)
			            						{
			            							orderItems.add((GenericValue) orderItemShipGroupAssoc.getRelatedOne("OrderItem"));
			            						}
			            					}
			            				}
			            				 
			            				if(UtilValidate.isEmpty(orderItemShipGroup))
			            				{
			            					errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemShipGroupAssociationError", UtilMisc.toMap("orderId", mRow.get("orderId"), "shipGroupSeqId", mRow.get("shipGroupSeqId_" + (orderItemNo + 1))), locale));
			            				}
		             		    	}
			            			
			            			//IF ONLY ORDER_ITEM_SEQUENCE_ID IS SUPPLIED
			            			if((UtilValidate.isEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))) && UtilValidate.isEmpty(mRow.get("productId_" + (orderItemNo + 1)))) && UtilValidate.isNotEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1))))
		             		    	{
			            				orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", mRow.get("orderId"), "orderItemSeqId", mRow.get("orderItemSequenceId_" + (orderItemNo + 1))));
		         					    if(UtilValidate.isEmpty(orderItems)) 
		         					    {
		         						    errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemProductIdSeqIdMatchingError", UtilMisc.toMap("orderId", mRow.get("orderId")), locale));
		         					    }
		             		    	}
			            			
			            			//IF ONLY PRODUCT_ID IS SUPPLIED
			            			if((UtilValidate.isEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))) && UtilValidate.isEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1)))) && UtilValidate.isNotEmpty(mRow.get("productId_" + (orderItemNo + 1))))
		             		    	{
			            				orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", mRow.get("orderId"), "productId", mRow.get("productId_" + (orderItemNo + 1))));
		         					    if(UtilValidate.isEmpty(orderItems)) 
		         					    {
		         					    	errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemProductIdMatchingError", UtilMisc.toMap("orderId", mRow.get("orderId")), locale));
		         					    }
		             		    	}
			            			//IF ONLY SHIP_GROUP_SEQUENCE_ID AND PRODUCT_ID IS SUPPLIED, ORDER_ITEM_SEQUENCE_ID (AND IF IT IS SUPPLIED THEN, WILL BE IGNORED) IS NOT SUPPLIED
			            			if((UtilValidate.isNotEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))) && UtilValidate.isNotEmpty(mRow.get("productId_" + (orderItemNo + 1)))))
		             		    	{
			            				List orderItemAndShipGroupAssocs = _delegator.findByAnd("OrderItemAndShipGroupAssoc", UtilMisc.toMap("orderId", mRow.get("orderId"), "productId", mRow.get("productId_" + (orderItemNo + 1)), "shipGroupSeqId", mRow.get("shipGroupSeqId_" + (orderItemNo + 1))));
			            				orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", mRow.get("orderId"), "productId", mRow.get("productId_" + (orderItemNo + 1))));
		         					    if(UtilValidate.isEmpty(orderItemAndShipGroupAssocs)) 
		         					    {
		         						    errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemShipGroupSeqIdProductIdMatchingError", UtilMisc.toMap("orderId", mRow.get("orderId"), "productId", mRow.get("productId_" + (orderItemNo + 1)), "shipGroupSeqId", mRow.get("shipGroupSeqId_" + (orderItemNo + 1))), locale));
		         					    }
		             		    	}
			            			
			            			//IF ONLY SHIP_GROUP_SEQUENCE_ID AND ORDER_ITEM_SEQUENCE_ID IS SUPPLIED, PRODUCT_ID IS NOT SUPPLIED
			            			if((UtilValidate.isNotEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))) && UtilValidate.isNotEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1)))) && UtilValidate.isEmpty(mRow.get("productId_" + (orderItemNo + 1))))
		             		    	{
			            				List orderItemAndShipGroupAssocs = _delegator.findByAnd("OrderItemAndShipGroupAssoc", UtilMisc.toMap("orderId", mRow.get("orderId"), "orderItemSeqId", mRow.get("orderItemSequenceId_" + (orderItemNo + 1)), "shipGroupSeqId", mRow.get("shipGroupSeqId_" + (orderItemNo + 1))));
			            				orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", mRow.get("orderId"), "orderItemSeqId", mRow.get("orderItemSequenceId_" + (orderItemNo + 1))));
		         					    if(UtilValidate.isEmpty(orderItemAndShipGroupAssocs)) 
		         					    {
		         						    errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemShipGroupSeqIdItemSeqIdMatchingError", UtilMisc.toMap("orderId", mRow.get("orderId"), "orderItemSeqId", mRow.get("orderItemSequenceId_" + (orderItemNo + 1)), "shipGroupSeqId", mRow.get("shipGroupSeqId_" + (orderItemNo + 1))), locale));
		         					    }
		             		    	}
			            			if(UtilValidate.isEmpty(mRow.get("orderItemStatus_"+ (orderItemNo + 1))) || (!mRow.get("orderItemStatus_"+ (orderItemNo + 1)).toString().equalsIgnoreCase("COMPLETED") && !mRow.get("orderItemStatus_"+ (orderItemNo + 1)).toString().equalsIgnoreCase("CANCELLED")))
					            	{
		             		    		orderItemStatusMatch = false;
					            	}
		             		    	else
		             		    	{    
		             		    		if(UtilValidate.isNotEmpty(orderItems))
		             		    	    { 
		             		    		    for(GenericValue orderItem : orderItems)
		             		    		    {
		             		    		    	currentOrderItemStatus = orderItem.getString("statusId");
		             		    			    if(currentOrderItemStatus.equalsIgnoreCase("ITEM_APPROVED"))
		             		    			    {
		             		    			    	orderItemStatusApproved = true;
		             		    			    }
		             		    			    else
		             		    			    {
		             		    			    	orderItemStatusApproved = false;
		             		    			    	break;
		             		    			    }
		             		    	 	    }
		             		    		    if(!orderItemStatusApproved)
							            	{
							            		errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemStatusApprovedError", UtilMisc.toMap("currentOrderItemStatus", currentOrderItemStatus),locale));
							            	}
		             		    	    }
		             		    	}
		             		    	
		             		    	if(!orderItemStatusMatch)
					            	{
					            		errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemStatusMatchingError", UtilMisc.toMap("orderItemStatus", mRow.get("orderItemStatus_"+ (orderItemNo + 1))),locale));
					            	}
		             		    	
		             		    	if(UtilValidate.isNotEmpty(mRow.get("orderItemShipDate_"+ (orderItemNo + 1))))
					                {
					                    if(!OsafeAdminUtil.isValidDate(mRow.get("orderItemShipDate_"+ (orderItemNo + 1)).toString()))
					                    {
					                    	errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidOrderItemShipDateError", UtilMisc.toMap("orderItemShipDate", mRow.get("orderItemShipDate_"+ (orderItemNo + 1))), locale));
					                    }
					                    else
					                    {
					                    	String orderItemShipDateStr = (String)mRow.get("orderItemShipDate_"+ (orderItemNo + 1));
					                    	java.util.Date orderItemShipDate = (java.util.Date) OsafeAdminUtil.validDate(orderItemShipDateStr);
					                    	Timestamp orderItemShipDateTs = OsafeAdminUtil.toTimestamp(_sdf.format(orderItemShipDate), "yyyy-MM-dd HH:mm:ss");
					                    	orderItemShipDateTs = UtilDateTime.getDayEnd(orderItemShipDateTs);
					                    	if(orderItemShipDateTs.before(orderDate))
					                    	{
					                    		errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "OrderItemShipDateBeforeOrderDateError", UtilMisc.toMap(), locale));
					                    	}
					                    }
					                }
		             		    	if(productStoreIdMatch)
		    	            		{
		             		    		if(UtilValidate.isNotEmpty(orderItemCarrier)) 
			             		    	{
			             		    		if(!carrierIdList.contains(orderItemCarrier)) 
			             		    		{
			             		    			carrierIdMatch = false;
			             		    		}
			             		    	}
			             		    	else
			             		    	{
			             		    		if(UtilValidate.isNotEmpty(orderItemShipMethod)) 
				             		    	{
			             		    			carrierIdMatch = false;
				             		    	}
			             		    	}
			             		    	if(UtilValidate.isNotEmpty(orderItemShipMethod)) 
			             		    	{
		                                    if(!shippingMethodIdList.contains(orderItemShipMethod)) 
		                                    {
		                                    	shippingMethodIdMatch = false;
			             		    		}
			             		    	}
			             		    	else
			             		    	{
			             		    		if(UtilValidate.isNotEmpty(orderItemCarrier)) 
				             		    	{
			             		    			shippingMethodIdMatch = false;
			             		    		}
			             		    	}
			             		    	
			             		    	if(UtilValidate.isNotEmpty(orderItemCarrier) && UtilValidate.isNotEmpty(orderItemShipMethod))
			             		    	{
			             		    		if(UtilValidate.isNotEmpty(carrierShipmentMethodListStore)) 
			             					{
			             		    			List carrierShipmentMethodListOrderItem = EntityUtil.filterByAnd(carrierShipmentMethodListStore, UtilMisc.toMap("shipmentMethodTypeId", orderItemShipMethod, "partyId", orderItemCarrier, "roleTypeId", "CARRIER"));
			             		    			if(UtilValidate.isEmpty(carrierShipmentMethodListOrderItem))
			             		    			{
			             		    				errorMessageList.add(UtilProperties.getMessage(resource, "OrderCarrierShippingMethodMatchingError", UtilMisc.toMap("carrierId", orderItemCarrier, "shippingMethodId", orderItemShipMethod),locale));
			             		    			}
			             					}
			             		    	}
			             		    	
			             		    	if(!carrierIdMatch) 
				             		    {
				             		    	errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemCarrierMatchingError", UtilMisc.toMap("orderItemCarrierId", orderItemCarrier),locale));
				             		    }
				             		    if(!shippingMethodIdMatch) 
				             		    {
				             		    	errorMessageList.add(UtilProperties.getMessage(resource, "OrderItemShippingMethodMatchingError", UtilMisc.toMap("orderItemShippingMethodId", orderItemShipMethod),locale));
				             		    }
		    	            		}
			            		}
			            	}
			            } 
			            else 
			            {
			            	String currentOrderStatus = "";
			                boolean carrierIdMatch = true;
			            	boolean shippingMethodIdMatch = true;
			            	boolean orderStatusMatch = true;
			            	boolean orderStatusApproved = false;
			            	String orderShipCarrier = "";
			            	String orderShipMethod = "";
			            	if(UtilValidate.isNotEmpty(mRow.get("orderShipCarrier")))
			            	{
			            		orderShipCarrier = (String)mRow.get("orderShipCarrier");
			            	}
			            	if(UtilValidate.isNotEmpty(mRow.get("orderShipMethod")))
			            	{
			            		orderShipMethod = (String)mRow.get("orderShipMethod");
			            	}
			            	
			            	if(UtilValidate.isEmpty(mRow.get("orderStatus")) || (!mRow.get("orderStatus").toString().equalsIgnoreCase("COMPLETED") && !mRow.get("orderStatus").toString().equalsIgnoreCase("CANCELLED")))
			            	{
			            		orderStatusMatch = false;
			            	}
			            	else
			            	{
			            		if(UtilValidate.isNotEmpty(orderHeader))
				            	{
			            			currentOrderStatus = orderHeader.getString("statusId");
				            		if(UtilValidate.isNotEmpty(currentOrderStatus) && currentOrderStatus.equalsIgnoreCase("ORDER_APPROVED"))
				            		{
				            			orderStatusApproved = true;
				            		}
				            	}
			            	}
			            	
			            	if(!orderStatusMatch)
			            	{
			            		errorMessageList.add(UtilProperties.getMessage(resource, "OrderStatusMatchingError", UtilMisc.toMap("orderStatus", mRow.get("orderStatus")),locale));
			            	}
			            	else if(!orderStatusApproved)
			            	{
			            		errorMessageList.add(UtilProperties.getMessage(resource, "OrderStatusApprovedError", UtilMisc.toMap("currentOrderStatus", currentOrderStatus),locale));
			            	}
			            	
			            	if(UtilValidate.isNotEmpty(mRow.get("orderShipDate")))
			                {
			                    if(!OsafeAdminUtil.isValidDate(mRow.get("orderShipDate").toString()))
			                    {
			                    	errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "InValidOrderShipDateError", UtilMisc.toMap("orderShipDate", mRow.get("orderShipDate")), locale));
			                    }
			                    else
			                    {
			                    	String orderShipDateStr = (String)mRow.get("orderShipDate");
			                    	java.util.Date orderShipDate = (java.util.Date) OsafeAdminUtil.validDate(orderShipDateStr);
			                    	Timestamp orderShipDateTs = OsafeAdminUtil.toTimestamp(_sdf.format(orderShipDate), "yyyy-MM-dd HH:mm:ss");
			                    	orderShipDateTs = UtilDateTime.getDayEnd(orderShipDateTs);
			                    	if(orderShipDateTs.before(orderDate))
			                    	{
			                    		errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "OrderShipDateBeforeOrderDateError", UtilMisc.toMap(), locale));
			                    	}
			                    }
			                }
			            	if(productStoreIdMatch)
    	            		{
			            		if(UtilValidate.isNotEmpty(orderShipCarrier)) 
				            	{
		                            if(!carrierIdList.contains(orderShipCarrier)) 
		                            {
		                                carrierIdMatch = false;
		             		        }
		             		    }
				            	else
				            	{
				            		if(UtilValidate.isNotEmpty(orderShipMethod)) 
					            	{
				            			carrierIdMatch = false;
					            	}
				            	}
				            	if(UtilValidate.isNotEmpty(orderShipMethod)) 
				            	{
		                            if(!shippingMethodIdList.contains(orderShipMethod)) 
		                            {
		                                shippingMethodIdMatch = false;
		             		    	}
		             		    }
				            	else
				            	{
				            		if(UtilValidate.isNotEmpty(orderShipCarrier)) 
					            	{
				            			shippingMethodIdMatch = false;
					            	}
				            	}
		             		    if(!carrierIdMatch) 
		             		    {
		             		    	errorMessageList.add(UtilProperties.getMessage(resource, "OrderCarrierMatchingError", UtilMisc.toMap("carrierId", orderShipCarrier),locale));
		             		    }
		             		    if(!shippingMethodIdMatch) 
		             		    {
		             		    	errorMessageList.add(UtilProperties.getMessage(resource, "OrderShippingMethodMatchingError", UtilMisc.toMap("shippingMethodId", orderShipMethod),locale));
		             		    }
		             		    
		             		    if(UtilValidate.isNotEmpty(orderShipCarrier) && UtilValidate.isNotEmpty(orderShipMethod))
	            		    	{
	            		    		if(UtilValidate.isNotEmpty(carrierShipmentMethodListStore)) 
	            					{
	            		    			List carrierShipmentMethodListOrder = EntityUtil.filterByAnd(carrierShipmentMethodListStore, UtilMisc.toMap("shipmentMethodTypeId", orderShipMethod, "partyId", orderShipCarrier, "roleTypeId", "CARRIER"));
	            		    			if(UtilValidate.isEmpty(carrierShipmentMethodListOrder))
	            		    			{
	            		    				errorMessageList.add(UtilProperties.getMessage(resource, "OrderCarrierShippingMethodMatchingError", UtilMisc.toMap("carrierId", orderShipCarrier, "shippingMethodId", orderShipMethod),locale));
	            		    			}
	            					}
	            		    	}
    	            		}
			            }
	            	}
	            }
	        }
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	        errorMessageList.add(e.getMessage());
	    }
	    result.put("errorMessageList", errorMessageList);
	    return result;
    }
    
    
    public static Map<String, Object> getProductRatingDataListFromFile(DispatchContext ctx, Map<String, ?> context) 
    {
    	
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        List<Map> productRatingDataList = FastList.newInstance();
        
        final List<String> errorMessageList = FastList.newInstance();
        
        String productRatingFilePath = (String)context.get("productRatingFilePath");
        String productRatingFileName = (String)context.get("productRatingFileName");
        
        Map result = ServiceUtil.returnSuccess();
        
        if(productRatingFileName.endsWith(".xml"))
        {
            try 
            {
	            JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
	            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	            
	            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	            Schema schema = schemaFactory.newSchema(new File(schemaLocation));
	            unmarshaller.setSchema(schema);
	            
	            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler()
	            {
	            	public boolean handleEvent(ValidationEvent ve) 
	            	{  
                        // ignore warnings  
                        if (ve.getSeverity() != ValidationEvent.WARNING) 
                        {  
                            ValidationEventLocator vel = ve.getLocator();
                            errorMessageList.add("Line:Col[" + vel.getLineNumber() +  
                                ":" + vel.getColumnNumber() +  
                                "]:" + ve.getMessage());
                              
                        }  
                        return true;  
                    }
	            }
	            );
	            
	            JAXBElement<BigFishProductRatingFeedType> bfProductRatingFeedType = (JAXBElement<BigFishProductRatingFeedType>)unmarshaller.unmarshal(new File(productRatingFilePath + productRatingFileName));
	            
                List<ProductRatingType> productRatingList = bfProductRatingFeedType.getValue().getProductRating();
            	
            	if(productRatingList.size() > 0) 
            	{
            		List dataRows = buildProductRatingXMLDataRows(productRatingList);
            		productRatingDataList = OsafeProductLoaderHelper.getDataList(dataRows);
            	}
            }
            catch (UnmarshalException ume)
            {
            	if(UtilValidate.isNotEmpty(errorMessageList))
	            {
	                result.put("errorMessageList", errorMessageList);
	                return result;
	            }
            	errorMessageList.add(ume.getMessage());
            	Debug.logError(ume, module);
            }
            catch(JAXBException je)
            {
            	errorMessageList.add(je.getMessage());
            	Debug.logError(je, module);
            }
            catch(Exception exc)
            {
            	errorMessageList.add(exc.getMessage());
            	Debug.logError(exc, module);
            }
        }
        result.put("errorMessageList", errorMessageList);
        result.put("productRatingDataList", productRatingDataList);
        return result;
    }
    

    public static Map<String, Object> validateProductRatingData(DispatchContext ctx, Map<String, ?> context) 
    {
	    LocalDispatcher dispatcher = ctx.getDispatcher();
	    _delegator = ctx.getDelegator();
	    Locale locale = (Locale) context.get("locale");
	    
	    List<Map> productRatingDataList = (List) context.get("productRatingDataList");
	
	    List<String> errorMessageList = FastList.newInstance();
	    List<String> productStoreIdList =  FastList.newInstance();
	    List<String> productIdList =  FastList.newInstance();
	    
	    try
	    {
	    	List<GenericValue> productStoreList = _delegator.findList("ProductStore", null, UtilMisc.toSet("productStoreId"), null, null, false);
	    	List<GenericValue> productsList = _delegator.findList("Product", null, UtilMisc.toSet("productId"), null, null, false);
	    	
        	if(UtilValidate.isNotEmpty(productStoreList))
			{
        		productStoreIdList = EntityUtil.getFieldListFromEntityList(productStoreList, "productStoreId", Boolean.TRUE);
			}
        	if(UtilValidate.isNotEmpty(productsList))
			{
        		productIdList = EntityUtil.getFieldListFromEntityList(productsList, "productId", Boolean.TRUE);
			}
        	
	    	if(productRatingDataList.size() > 0) 
	    	{
				for (int i=0 ; i < productRatingDataList.size() ; i++) 
                {
	                Map mRow = (Map)productRatingDataList.get(i);
	                String productStoreId = (String)mRow.get("productStoreId");
	                String productRatingScore = (String)mRow.get("productRatingScore");
	                String productId = (String)mRow.get("productId");
	                String validProductId = "";
	                String sku = (String)mRow.get("sku");
	                
	                boolean productStoreIdMatch = true;
	                boolean productRatingScoreValid = true;
	                
	            	if(UtilValidate.isEmpty(productStoreId) || !productStoreIdList.contains((String)productStoreId))
	            	{
	            		productStoreIdMatch = false;
	            	}
	            	 
	            	if(!productStoreIdMatch)
	            	{
	            	    errorMessageList.add(UtilProperties.getMessage(resource, "ProductStoreIdMatchingError", UtilMisc.toMap("productStoreId", productStoreId), locale)); 
	            	}
	            	
	                if(UtilValidate.isNotEmpty(productRatingScore))
	                {
	                    boolean checkFloatResult = OsafeAdminUtil.isFloat(productRatingScore);
	                    if(!checkFloatResult)
	                    {
	                    	errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidProductRatingScoreError", UtilMisc.toMap("productRatingScore", productRatingScore), locale));
	                    }
	                    else
	                    {
	                    	float productRatingScoreF = Float.parseFloat(productRatingScore);
	                    	if(productRatingScoreF < 0 || productRatingScoreF > 10)
	                    	{
	                    		errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidProductRatingScoreError", UtilMisc.toMap("productRatingScore", productRatingScore), locale));
	                    	}
	                    }
	                }
	                else
	                {
	                	errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ValidProductRatingScoreError", UtilMisc.toMap("productRatingScore", productRatingScore), locale));
	                }
	                if(UtilValidate.isEmpty(productId) && UtilValidate.isEmpty(sku))
	                {
	                	errorMessageList.add(UtilProperties.getMessage("OSafeAdminUiLabels", "ProductIdSkuBlankError", UtilMisc.toMap(), locale));
	                }
	                
	                if(UtilValidate.isNotEmpty(productId))
	            	{
	                	if(!productIdList.contains(productId))
	                	{
	                		errorMessageList.add(UtilProperties.getMessage(resource, "ProductRatingScoreProductIdMatchingError", UtilMisc.toMap("productId", productId), locale));
	                	}
	                	else
	                	{
	                		validProductId = productId;
	                	}
	            	}
	                else
	                {
	                	if(UtilValidate.isNotEmpty(sku))
	                	{
	                		List<GenericValue> goodIdentificationList = _delegator.findByAnd("GoodIdentification", UtilMisc.toMap("goodIdentificationTypeId", "SKU", "idValue", sku));
		            		if(UtilValidate.isEmpty(goodIdentificationList)) 
		            		{
		            			errorMessageList.add(UtilProperties.getMessage(resource, "SkuMatchingError", UtilMisc.toMap("sku", sku), locale));
		            		}
		            		else
		            		{
		            			validProductId = EntityUtil.getFirst(goodIdentificationList).getString("productId");
		            		}
	                	}
	                }
	                if(UtilValidate.isNotEmpty(validProductId))
	                {
	                	GenericValue product = _delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", validProductId));
	                	if(UtilValidate.isNotEmpty(product))
	                	{
	                		if("Y".equals(product.getString("isVariant")))
	                		{
	                			errorMessageList.add(UtilProperties.getMessage(resource, "RatingApplicableTypeError", UtilMisc.toMap(), locale));
	                		}
	                	}
	                }
	            }
	        }
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	        errorMessageList.add(e.getMessage());
	    }
	
	    Map result = ServiceUtil.returnSuccess();
	    
	    result.put("errorMessageList", errorMessageList);
	    
	    return result;
    }
    
    public static Map<String, Object> getCustomerDataListFromFile(DispatchContext ctx, Map<String, ?> context) 
    {
    	
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        List<Map> customerDataList = FastList.newInstance();
        
        final List<String> errorMessageList = FastList.newInstance();
        
        String customerFilePath = (String)context.get("customerFilePath");
        String customerFileName = (String)context.get("customerFileName");
        
        Map result = ServiceUtil.returnSuccess();
        
        if(customerFileName.endsWith(".xml"))
        {
            try 
            {
	            JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
	            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	            
	            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	            Schema schema = schemaFactory.newSchema(new File(schemaLocation));
	            unmarshaller.setSchema(schema);
	            
	            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler()
	            {
	            	public boolean handleEvent(ValidationEvent ve) 
	            	{  
                        // ignore warnings  
                        if (ve.getSeverity() != ValidationEvent.WARNING) 
                        {  
                            ValidationEventLocator vel = ve.getLocator();
                            errorMessageList.add("Line:Col[" + vel.getLineNumber() +  
                                ":" + vel.getColumnNumber() +  
                                "]:" + ve.getMessage());
                              
                        }  
                        return true;  
                    }
	            }
	            );
	            
	            JAXBElement<BigFishCustomerFeedType> bfCustomerFeedType = (JAXBElement<BigFishCustomerFeedType>)unmarshaller.unmarshal(new File(customerFilePath + customerFileName));
	            
                List<CustomerType> customerList = bfCustomerFeedType.getValue().getCustomer();
            	
            	if(customerList.size() > 0) 
            	{
            		List dataRows = buildCustomerXMLDataRows(customerList);
            		customerDataList = OsafeProductLoaderHelper.getDataList(dataRows);
            	}
            }
            catch (UnmarshalException ume)
            {
            	if(UtilValidate.isNotEmpty(errorMessageList))
	            {
	                result.put("errorMessageList", errorMessageList);
	                return result;
	            }
            	errorMessageList.add(ume.getMessage());
            	Debug.logError(ume, module);
            }
            catch(JAXBException je)
            {
            	errorMessageList.add(je.getMessage());
            	Debug.logError(je, module);
            }
            catch(Exception exc)
            {
            	errorMessageList.add(exc.getMessage());
            	Debug.logError(exc, module);
            }
        }
        result.put("errorMessageList", errorMessageList);
        result.put("customerDataList", customerDataList);
        return result;
    }
    
    public static Map<String, Object> validateCustomerData(DispatchContext ctx, Map<String, ?> context) 
    {
	    LocalDispatcher dispatcher = ctx.getDispatcher();
	    _delegator = ctx.getDelegator();
	    Locale locale = (Locale) context.get("locale");
	    
	    List<Map> customerDataList = (List) context.get("customerDataList");
	
	    List<String> errorMessageList = FastList.newInstance();
	    
	    if(customerDataList.size() > 0) 
	    {
			for (int i=0 ; i < customerDataList.size() ; i++) 
            {
            	 Map mRow = (Map)customerDataList.get(i);
            	 if(UtilValidate.isNotEmpty(mRow.get("userName"))) 
            	 {
            		 if(UtilValidate.isNotEmpty(mRow.get("customerId")))
	            	 {
	            		 try 
	            		 {
	            		     GenericValue userLoginGv = _delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", mRow.get("userName")));
	            		     if(UtilValidate.isNotEmpty(userLoginGv) && !userLoginGv.getString("partyId").equals(mRow.get("customerId")))
	            		     {
	            		    	 errorMessageList.add(UtilProperties.getMessage(resource, "UserNameUniqueError", UtilMisc.toMap(), locale));
	            		     }
	            		 }
	            		 catch (GenericEntityException gee) 
	            		 {
	            			 gee.printStackTrace();
	            		 }
	            	 } 
            		 else 
            		 {
	            		 try 
	            		 {
	            			 GenericValue userLoginGv = _delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", mRow.get("userName")));
		            		 if(UtilValidate.isNotEmpty(userLoginGv)) 
		            		 {
		            			 errorMessageList.add(UtilProperties.getMessage(resource, "UserNameAssociateAnotherPartyIdError", UtilMisc.toMap(), locale));
		            		 }	 
	            		 } 
	            		 catch (GenericEntityException gee) 
	            		 {
	            			 gee.printStackTrace();
						}
	            	 }
            	 }
            	 
             }
		}
	
	    Map result = ServiceUtil.returnSuccess();
	    result.put("errorMessageList", errorMessageList);
	    
	    return result;
    }
    
    public static Map<String, Object> importOrderStatusXML(DispatchContext ctx, Map<String, ?> context) {LocalDispatcher dispatcher = ctx.getDispatcher();
    _delegator = ctx.getDelegator();
    List<String> messages = FastList.newInstance();

    String xmlDataFilePath = (String)context.get("xmlDataFile");
    String xmlDataDirPath = (String)context.get("xmlDataDir");
    Boolean autoLoad = (Boolean) context.get("autoLoad");
    GenericValue userLogin = (GenericValue) context.get("userLogin");
    if (autoLoad == null) autoLoad = Boolean.FALSE;

    File inputWorkbook = null;
    String tempDataFile = null;
    File baseDataDir = null;
    File baseFilePath = null;
    BufferedWriter fOutProduct=null;
    if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) 
    {
    	baseFilePath = new File(xmlDataFilePath);
        try 
        {
            URL xlsDataFileUrl = UtilURL.fromFilename(xmlDataFilePath);
            InputStream ins = xlsDataFileUrl.openStream();

            if (ins != null && (xmlDataFilePath.toUpperCase().endsWith("XML"))) 
            {
                baseDataDir = new File(xmlDataDirPath);
                if (baseDataDir.isDirectory() && baseDataDir.canWrite()) 
                {

                    // ############################################
                    // move the existing xml files in dump directory
                    // ############################################
                    File dumpXmlDir = null;
                    File[] fileArray = baseDataDir.listFiles();
                    for (File file: fileArray) 
                    {
                        try 
                        {
                            if (file.getName().toUpperCase().endsWith("XML")) {
                                if (dumpXmlDir == null) 
                                {
                                    dumpXmlDir = new File(baseDataDir, "dumpxml_"+UtilDateTime.nowDateString());
                                }
                                FileUtils.copyFileToDirectory(file, dumpXmlDir);
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
                    // ######################################
                    //save the temp xls data file on server 
                    // ######################################
                    try 
                    {
                    	tempDataFile = UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath);
                        inputWorkbook = new File(baseDataDir,  tempDataFile);
                        if (inputWorkbook.createNewFile()) 
                        {
                            Streams.copy(ins, new FileOutputStream(inputWorkbook), true, new byte[1]); 
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
    //read the temp xls file and generate xml 
    // ######################################
    List dataRows = FastList.newInstance();
    try 
    {
	    if (inputWorkbook != null && baseDataDir  != null) 
	    {
	    	try 
	    	{
	    		JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
	        	Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	        	JAXBElement<BigFishOrderStatusUpdateFeedType> bfOrderStatusUpdateFeedType = (JAXBElement<BigFishOrderStatusUpdateFeedType>)unmarshaller.unmarshal(inputWorkbook);
	        	
	        	List<OrderStatusType> orderList = bfOrderStatusUpdateFeedType.getValue().getOrder();
	        	
	        	if(orderList.size() > 0) 
	        	{
	        		dataRows = buildOrderStatusXMLDataRows(orderList);
	        		updateOrderShipGroupFeed(dataRows, userLogin, dispatcher);
	        	}
	    	} 
	    	catch (Exception e) 
	    	{
	    		Debug.logError(e, module);
			}
	    	finally 
	    	{
	            try 
	            {
	                if (fOutProduct != null) 
	                {
	                	fOutProduct.close();
	                }
	            } catch (IOException ioe) 
	            {
	                Debug.logError(ioe, module);
	            }
	        }
	    }
	    
    } 
    catch (Exception exc) 
    {
        Debug.logError(exc, module);
    }
    finally 
    {
        inputWorkbook.delete();
    } 
            
    Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
    return resp;  
    }
    
    
    private static void updateOrderShipGroupFeed(List dataRows, GenericValue userLogin, LocalDispatcher dispatcher) 
    {
        for (int i=0 ; i < dataRows.size() ; i++) 
        {
        	Map mRow = (Map)dataRows.get(i);
        	 
        	GenericValue orderHeader = null;
			try 
			{
				orderHeader = _delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", (String)mRow.get("orderId")));
			} 
			catch (GenericEntityException e2) 
			{
				e2.printStackTrace();
			} 
        	 
        	 OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);  
        	 
        	 Map<String, Set> shipGroupOrderItemSeqIdMap = FastMap.newInstance();
        	 Map<String, String> shipGroupOrderItemStatusMap = FastMap.newInstance();
        	 HashSet orderItemSeqIdList = new HashSet();
        	 
             if(Integer.parseInt((String)mRow.get("totalOrderItems")) > 0) 
             {
            	 for(int orderItemNo = 0; orderItemNo < Integer.parseInt((String)mRow.get("totalOrderItems")); orderItemNo++) 
            	 {

        			 List andExprs = FastList.newInstance();
        			 
        			 andExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, mRow.get("orderId")));
        			 
        			 if(UtilValidate.isEmpty(mRow.get("productId_" + (orderItemNo + 1))) && UtilValidate.isNotEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1))))
        			 {
        				 andExprs.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, mRow.get("orderItemSequenceId_" + (orderItemNo + 1))));
        			 }
        			 if(UtilValidate.isNotEmpty(mRow.get("productId_" + (orderItemNo + 1))))
        			 {
        				 andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, mRow.get("productId_" + (orderItemNo + 1))));
        			 }
        			 if(UtilValidate.isNotEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))))
        			 {
        				 andExprs.add(EntityCondition.makeCondition("shipGroupSeqId", EntityOperator.EQUALS, mRow.get("shipGroupSeqId_" + (orderItemNo + 1))));
        			 }
        			 
        			 List<GenericValue> orderItemAndShipGroupAssocs = FastList.newInstance();
        			 
             		 try 
             		 {
             		     orderItemAndShipGroupAssocs = _delegator.findList("OrderItemAndShipGroupAssoc", EntityCondition.makeCondition(andExprs, EntityOperator.AND), null, null, null, false);
             		 } 
             		 catch (GenericEntityException e) 
             		 {
         			     e.printStackTrace();
         			 }
             		 if(UtilValidate.isNotEmpty(orderItemAndShipGroupAssocs))
             		 {
             		     for(GenericValue orderItemAndShipGroupAssoc : orderItemAndShipGroupAssocs) 
             			 {
             		    	 boolean sameShipment = false;
             		    	 List<GenericValue> orderItemShipGroupAssocList = FastList.newInstance();
							 try 
							 {
								 orderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId"), "shipGroupSeqId", orderItemAndShipGroupAssoc.getString("shipGroupSeqId")), UtilMisc.toList("+orderItemSeqId"));
							 }  
							 catch (GenericEntityException e1) 
							 {
								 e1.printStackTrace();
							 }
             		    	 if(orderItemShipGroupAssocList.size() > 1) 
            				 {
            					 sameShipment = true;
            				 }
             		    	 if(UtilValidate.isEmpty(mRow.get("orderItemSequenceId_" + (orderItemNo + 1))) && (UtilValidate.isNotEmpty(mRow.get("shipGroupSeqId_" + (orderItemNo + 1))) || UtilValidate.isNotEmpty(mRow.get("productId_" + (orderItemNo + 1)))))
             		    	 {
             		    		sameShipment = false;
             		    	 }
             		    	 if(sameShipment) 
	            			 {
             		    		 //Create New OrderItemShipGroup
             		    		 Long maxShipGroupSeqId = Long.valueOf("1");
             		    		 List<GenericValue> allOrderItemShipGroupAssocList = FastList.newInstance();
								 try 
								 {
									 allOrderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId")), UtilMisc.toList("+shipGroupSeqId"));
								 }
								 catch (GenericEntityException e) 
								 {
									e.printStackTrace();
								 }
	            				 for(GenericValue allOrderItemShipGroupAssoc : allOrderItemShipGroupAssocList) 
	            				 {
	            					 Long curShipGroupSeqId = Long.parseLong(allOrderItemShipGroupAssoc.getString("shipGroupSeqId"));
	            					 if(curShipGroupSeqId > maxShipGroupSeqId) 
	            					 {
	            						 maxShipGroupSeqId = curShipGroupSeqId;
	            					 }
	            				 }
	            				 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) 
	            				 {
	            					 if(orderItemShipGroupAssoc.getString("orderItemSeqId").equals(orderItemAndShipGroupAssoc.getString("orderItemSeqId")))
	            					 {
	            						 Set orderItemSeqIds = FastSet.newInstance();
	            						 maxShipGroupSeqId = maxShipGroupSeqId + 1;
		            					 String shipGroupSeqId = UtilFormatOut.formatPaddedNumber(maxShipGroupSeqId.longValue(), 5);
			            				 GenericValue orderItemShipGroup = null;
										 try 
										 {
											 orderItemShipGroup = _delegator.findByPrimaryKey("OrderItemShipGroup", UtilMisc.toMap("orderId", orderItemShipGroupAssoc.getString("orderId"), "shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId")));
										 }
										 catch (GenericEntityException e) 
										 {
											 e.printStackTrace();
										 }
			            				 GenericValue orderItemShipGroupClone = (GenericValue) orderItemShipGroup.clone();
			            				 orderItemShipGroupClone.set("shipGroupSeqId", shipGroupSeqId);
			            				 if(UtilValidate.isNotEmpty((String)mRow.get("orderItemShipMethod_" + (orderItemNo + 1))))
		    				             {
			            					 orderItemShipGroupClone.set("shipmentMethodTypeId", (String)mRow.get("orderItemShipMethod_" + (orderItemNo + 1)));
		    				             }
			            				 if(UtilValidate.isNotEmpty((String)mRow.get("orderItemCarrier_" + (orderItemNo + 1))))
		    				             {
			            					 orderItemShipGroupClone.set("carrierPartyId", (String)mRow.get("orderItemCarrier_" + (orderItemNo + 1)));
		    				             }
			            				 if(UtilValidate.isNotEmpty((String)mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1)))) 
		    				             {
			            					 orderItemShipGroupClone.set("trackingNumber", (String)mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1)));
		    				             }
			            				 
			            				 String sEstimatedShipDate = (String)mRow.get("orderItemShipDate_" + (orderItemNo + 1));
		    				             if(UtilValidate.isNotEmpty(sEstimatedShipDate))
		    				             {
		    				            	 try 
			                            	 {
												 java.util.Date formattedShipDate=OsafeAdminUtil.validDate(sEstimatedShipDate);
												 Timestamp estimatedShipDate = (Timestamp) ObjectType.simpleTypeConvert(_sdf.format(formattedShipDate), "Timestamp", "yyyy-MM-dd HH:mm:ss", null);
												 orderItemShipGroupClone.set("estimatedShipDate",estimatedShipDate);
											 } 
			                            	 catch (GeneralException e) 
			                            	 {
												e.printStackTrace();
								 			 }
		    				             }
		    				             
		    				             try 
		    				             {
											orderItemShipGroupClone.create();
										 }
		    				             catch (GenericEntityException e) 
		    				             {
											e.printStackTrace();
										 }
		    				             
		    				             //Create the OrderItemShipGroupAssoc for new create ShipGroupSeqId.
		    				             GenericValue newOrderItemShipGroupAssoc = _delegator.makeValue("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderItemShipGroupAssoc.getString("orderId"), "orderItemSeqId", orderItemShipGroupAssoc.getString("orderItemSeqId"), "shipGroupSeqId", shipGroupSeqId));
		    				             newOrderItemShipGroupAssoc.set("quantity", orderItemShipGroupAssoc.getBigDecimal("quantity"));
		    				             if(((String)mRow.get("orderItemStatus_" + (orderItemNo + 1))).equalsIgnoreCase("CANCELLED"))
		    			                 {
		    				            	 newOrderItemShipGroupAssoc.set("cancelQuantity", orderItemShipGroupAssoc.getBigDecimal("quantity"));
		    			                 }
		    				             else
		    				             {
		    				            	 newOrderItemShipGroupAssoc.set("cancelQuantity", orderItemShipGroupAssoc.getBigDecimal("cancelQuantity"));
		    				             }
		    				             try 
		    				             {
											 newOrderItemShipGroupAssoc.create();
										 } 
		    				             catch (GenericEntityException e) 
		    				             {
											e.printStackTrace();
										 }
		    				             
		    				             //Remove the existing orderItemShipGroupAssoc Record
		    				             try 
		    				             {
											_delegator.removeValue(orderItemShipGroupAssoc);
										 } 
		    				             catch (GenericEntityException e) 
										 {
											e.printStackTrace();
										 }
		    				             
		    				             //ONLY REQUIRED FOR COMPLETED STATUS
		    				             if(((String)mRow.get("orderItemStatus_" + (orderItemNo + 1))).equalsIgnoreCase("COMPLETED"))
		    				             {
		    				            	 if(UtilValidate.isNotEmpty(shipGroupOrderItemSeqIdMap.get(shipGroupSeqId)))
			    				             {
			    				            	 Set orderItemSeqIdSet =  shipGroupOrderItemSeqIdMap.get(orderItemShipGroup.getString("shipGroupSeqId"));
		    			                	 orderItemSeqIdSet.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
		    					            	 orderItemSeqIds.addAll(orderItemSeqIdSet);
			    				             }
			    				             else
			    				             {
			    				            	 orderItemSeqIds.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
			    				             }
			    				             shipGroupOrderItemSeqIdMap.put(shipGroupSeqId, orderItemSeqIds);
		    				             }
		    				             
		    				             orderItemSeqIdList.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
		    				             shipGroupOrderItemStatusMap.put(shipGroupSeqId, (String)mRow.get("orderItemStatus_" + (orderItemNo + 1)));
	            					 }
	            				 }
	            			 }
             		    	 else
             		    	 {
             		    	     //Update Existing OrderItemShipGroup
             		    		 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) 
	            				 {
             		    			if(orderItemShipGroupAssoc.getString("orderItemSeqId").equals(orderItemAndShipGroupAssoc.getString("orderItemSeqId")))
	            					{
             		    				 Set orderItemSeqIds = FastSet.newInstance();
             		    				 GenericValue orderItemShipGroup = null; 
										 try 
										 {
											orderItemShipGroup = orderItemShipGroupAssoc.getRelatedOne("OrderItemShipGroup");
										 } 
										 catch (GenericEntityException e1) 
										 {
											e1.printStackTrace();
										 }
	             		    		     
		    				             Map updateOrderItemShipGroupParams = UtilMisc.toMap("orderId", orderItemShipGroup.getString("orderId"),
		    			                            "shipGroupSeqId",orderItemShipGroup.getString("shipGroupSeqId"),
		    			                            "userLogin", userLogin);
		    				             if (UtilValidate.isNotEmpty(mRow.get("orderItemShipMethod_" + (orderItemNo + 1))))
		    				             {
		    				            	 if(!mRow.get("orderItemShipMethod_" + (orderItemNo + 1)).equals(orderItemShipGroup.getString("shipmentMethodTypeId")))
		    				            	 {
		    				            		 updateOrderItemShipGroupParams.put("shipmentMethodTypeId", mRow.get("orderItemShipMethod_" + (orderItemNo + 1)));
		    				            	 }
		    				             }
		    				             if (UtilValidate.isNotEmpty(mRow.get("orderItemCarrier_" + (orderItemNo + 1))))
		    				             {
		    				            	 if(!mRow.get("orderItemCarrier_" + (orderItemNo + 1)).equals(orderItemShipGroup.getString("carrierPartyId")))
		    				            	 {
		    				            		 updateOrderItemShipGroupParams.put("carrierPartyId", mRow.get("orderItemCarrier_" + (orderItemNo + 1)));
		    				            	 }
		    				             }
		    				             if (UtilValidate.isNotEmpty(mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1))))
		    				             {
				            				 if(!mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1)).equals(orderItemShipGroup.getString("trackingNumber"))) 
				            				 {
				            					 updateOrderItemShipGroupParams.put("trackingNumber", mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1)));
				            				 }
		    				             }
		    				             
		    				             String sEstimatedShipDate = (String)mRow.get("orderItemShipDate_" + (orderItemNo + 1));
		    				             if(UtilValidate.isNotEmpty(sEstimatedShipDate))
		    				             {
		    				            	 try 
			                            	 {
												 java.util.Date formattedShipDate=OsafeAdminUtil.validDate(sEstimatedShipDate);
												 Timestamp estimatedShipDate = (Timestamp) ObjectType.simpleTypeConvert(_sdf.format(formattedShipDate), "Timestamp", "yyyy-MM-dd HH:mm:ss", null);
												 updateOrderItemShipGroupParams.put("estimatedShipDate",estimatedShipDate);
											 } 
			                            	 catch (GeneralException e) 
			                            	 {
												e.printStackTrace();
								 			 }
		    				             }
		                            	 
		    			                   try 
		    			                   {
		    			                       Map result = dispatcher.runSync("updateOrderItemShipGroup", updateOrderItemShipGroupParams);
		    			                   } 
		    			                   catch(GenericServiceException e)
		    			                   {
		    			                       Debug.logError(e, module);
		    			                   }
		    			                   
		    			                   //ONLY REQUIRED FOR COMPLETED STATUS
		    			                   if(((String)mRow.get("orderItemStatus_" + (orderItemNo + 1))).equalsIgnoreCase("COMPLETED"))
		    			                   {
		    			                	   if(UtilValidate.isNotEmpty(shipGroupOrderItemSeqIdMap.get(orderItemShipGroup.getString("shipGroupSeqId"))))
			    				               {
			    				            	   Set orderItemSeqIdSet =  shipGroupOrderItemSeqIdMap.get(orderItemShipGroup.getString("shipGroupSeqId"));
		    			                		 orderItemSeqIdSet.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
			    				            	   orderItemSeqIds.addAll(orderItemSeqIdSet);
			    				               }
			    				               else
			    				               {
			    				            	   orderItemSeqIds.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
			    				               }
			    			                   shipGroupOrderItemSeqIdMap.put(orderItemShipGroup.getString("shipGroupSeqId"), orderItemSeqIds);
		    			                   }
		    			                   if(((String)mRow.get("orderItemStatus_" + (orderItemNo + 1))).equalsIgnoreCase("CANCELLED"))
		    			                   {
		    			                	   orderItemShipGroupAssoc.set("cancelQuantity", orderItemShipGroupAssoc.getBigDecimal("quantity"));
		    			                	   try 
		    			                	   {
											       _delegator.store(orderItemShipGroupAssoc);
											   } 
		    			                	   catch (GenericEntityException e) 
		    			                	   {
											       e.printStackTrace();
											   }
		    			                   }
		    			                   
		    			                   orderItemSeqIdList.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
		    			                   shipGroupOrderItemStatusMap.put(orderItemShipGroup.getString("shipGroupSeqId"), (String)mRow.get("orderItemStatus_" + (orderItemNo + 1)));
	            					   }
	            				   }
             		    	   }
             			   }
             		   }
            	 }
             } 
             else 
             {

            	 List<GenericValue> orderItemShipGroupAssocList = FastList.newInstance();
				 try 
				 {
					 orderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId")), UtilMisc.toList("+orderItemSeqId"));
				 } 
				 catch (GenericEntityException e1) 
				 {
					 e1.printStackTrace();
				 }
            	 if(UtilValidate.isNotEmpty(orderItemShipGroupAssocList)) 
            	 {
            		 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) 
            		 {
            			 Set orderItemSeqIds = FastSet.newInstance();
            			 GenericValue orderItemShipGroup = null;
						 try 
						 {
						     orderItemShipGroup = orderItemShipGroupAssoc.getRelatedOne("OrderItemShipGroup");
						 } 
						 catch (GenericEntityException e1) 
						 {
							 e1.printStackTrace();
						 }
 		    		     
			             Map updateOrderItemShipGroupParams = UtilMisc.toMap("orderId", orderItemShipGroup.getString("orderId"),
		                            "shipGroupSeqId",orderItemShipGroup.getString("shipGroupSeqId"),
		                            "userLogin", userLogin);
			             
			             if (UtilValidate.isNotEmpty(mRow.get("orderShipMethod")))
			             {
			            	 if(!mRow.get("orderShipMethod").equals(orderItemShipGroup.getString("shipmentMethodTypeId")))
			            	 {
			            		 updateOrderItemShipGroupParams.put("shipmentMethodTypeId",(String)mRow.get("orderShipMethod"));
			            	 }
			             }
			             
			             if (UtilValidate.isNotEmpty(mRow.get("orderShipCarrier")))
			             {
			            	 if(!mRow.get("orderShipCarrier").equals(orderItemShipGroup.getString("carrierPartyId")))
			            	 {
			            		 updateOrderItemShipGroupParams.put("carrierPartyId",(String)mRow.get("orderShipCarrier"));
			            	 }
			             }
			             
			             if (UtilValidate.isNotEmpty(mRow.get("orderTrackingNumber")))
			             {
            				 if(!mRow.get("orderTrackingNumber").equals(orderItemShipGroup.getString("trackingNumber"))) 
            				 {
            					 updateOrderItemShipGroupParams.put("trackingNumber",(String)mRow.get("orderTrackingNumber"));
            				 }
			             }
			             if(UtilValidate.isNotEmpty(mRow.get("orderShipDate"))) 
                         {
                        	 String sEstimatedShipDate=(String)mRow.get("orderShipDate");
                        	 try 
                        	 {
								java.util.Date formattedShipDate=OsafeAdminUtil.validDate(sEstimatedShipDate);
								 Timestamp estimatedShipDate = (Timestamp) ObjectType.simpleTypeConvert(_sdf.format(formattedShipDate), "Timestamp", "yyyy-MM-dd HH:mm:ss", null);
								 updateOrderItemShipGroupParams.put("estimatedShipDate",estimatedShipDate);
							 } 
                        	 catch (GeneralException e) 
                        	 {
								e.printStackTrace();
				 			 }
                         }
                         else
                         {
                        	 updateOrderItemShipGroupParams.put("estimatedShipDate", UtilDateTime.nowTimestamp());
                         }
			             
		                 try 
		                 {
		                     Map result = dispatcher.runSync("updateOrderItemShipGroup", updateOrderItemShipGroupParams);
		                 } 
		                 catch(GenericServiceException e)
		                 {
		                     Debug.logError(e, module);
		                 }
		                 
		                 if(((String)mRow.get("orderStatus")).equalsIgnoreCase("COMPLETED"))
		                 {
		                	 if(UtilValidate.isNotEmpty(shipGroupOrderItemSeqIdMap.get(orderItemShipGroup.getString("shipGroupSeqId"))))
				             {
				                  Set orderItemSeqIdSet =  shipGroupOrderItemSeqIdMap.get(orderItemShipGroup.getString("shipGroupSeqId"));
		                		 orderItemSeqIdSet.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
				            	 orderItemSeqIds.addAll(orderItemSeqIdSet);
				             }
				             else
				             {
				                 orderItemSeqIds.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
				             }
			                 shipGroupOrderItemSeqIdMap.put(orderItemShipGroup.getString("shipGroupSeqId"), orderItemSeqIds);
		                 }
		                 
		                 if(((String)mRow.get("orderStatus")).equalsIgnoreCase("CANCELLED"))
		                 {
		                	 orderItemShipGroupAssoc.set("cancelQuantity", orderItemShipGroupAssoc.getBigDecimal("quantity"));
		                	 try 
		                	 {
							     _delegator.store(orderItemShipGroupAssoc);
							 } 
		                	 catch (GenericEntityException e) 
		                	 {
							     e.printStackTrace();
							 }
		                 }
		                 
		                 orderItemSeqIdList.add(orderItemShipGroupAssoc.getString("orderItemSeqId"));
		                 shipGroupOrderItemStatusMap.put(orderItemShipGroup.getString("shipGroupSeqId"), (String)mRow.get("orderStatus"));
            		 }
            	 }
             }
             
             //Create Shipment for Order Items.
             if(UtilValidate.isNotEmpty(shipGroupOrderItemSeqIdMap))
             {
            	 for (Map.Entry<String, Set> entry : shipGroupOrderItemSeqIdMap.entrySet())
                 {
                 	List orderItemSeqIdListTemp = FastList.newInstance();
            		  orderItemSeqIdListTemp.addAll(entry.getValue());
                	 Map quickShipOrderItemsParams = UtilMisc.toMap("orderId", (String)mRow.get("orderId"),
                             "shipmentShipGroupSeqId",entry.getKey(), "orderItemSeqIdList", orderItemSeqIdListTemp,
                             "userLogin", userLogin);
                	 try 
                     {
                         Map result = dispatcher.runSync("quickShipOrderItems", quickShipOrderItemsParams);
                     } 
                     catch(GenericServiceException e)
                     {
                         Debug.logError(e, module);
                     }
                 }
             }
             
             //Change Order Item Status.
             if(UtilValidate.isNotEmpty(shipGroupOrderItemStatusMap))
             {
            	 List processedOrderItemSeqIds = FastList.newInstance();
            	 for (Map.Entry<String, String> entry : shipGroupOrderItemStatusMap.entrySet())
                 {
            		 String shipGroupSeqId = (String)entry.getKey();
            		 List<GenericValue> orderItemShipGroupAssocs = FastList.newInstance();
            		 try 
            		 {
						orderItemShipGroupAssocs = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId"), "shipGroupSeqId", shipGroupSeqId));
					 } 
            		 catch (GenericEntityException e) 
            		 {
						e.printStackTrace();
					 }
            		 if(UtilValidate.isNotEmpty(orderItemShipGroupAssocs))
            		 {
            			 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocs)
            			 {
            				 try 
            				 {
								GenericValue orderItem = orderItemShipGroupAssoc.getRelatedOne("OrderItem");
								if(!processedOrderItemSeqIds.contains(orderItem.getString("orderItemSeqId")))
								{
									BigDecimal itemShippedQty  = BigDecimal.ZERO;
									
									List<GenericValue> orderItemShipments = _delegator.findByAnd("OrderShipment", UtilMisc.toMap("orderId",orderItem.getString("orderId"), "orderItemSeqId", orderItem.getString("orderItemSeqId")));
									if(UtilValidate.isNotEmpty(orderItemShipments))
									{
										for(GenericValue orderItemShipment : orderItemShipments)
										{
											itemShippedQty = itemShippedQty.add(orderItemShipment.getBigDecimal("quantity"));
											
										}
									}
									BigDecimal orderedQty = orderItem.getBigDecimal("quantity");
									BigDecimal shipGroupCancelQty = BigDecimal.ZERO;
									List<GenericValue> itemShipGroupAssocs = orderReadHelper.getOrderItemShipGroupAssocs(orderItem);
									for(GenericValue itemShipGroupAssoc: itemShipGroupAssocs)
									{
										if(UtilValidate.isNotEmpty(itemShipGroupAssoc.getBigDecimal("cancelQuantity")))
										{
											shipGroupCancelQty = shipGroupCancelQty.add(itemShipGroupAssoc.getBigDecimal("cancelQuantity"));
										}
									}
									
									if(itemShippedQty.add(shipGroupCancelQty).compareTo(orderedQty) < 0)
									{
										//ITEM APPROVED -- DO NOTHING
									}
									else
									{
										if(shipGroupCancelQty.compareTo(orderedQty) == 0)
										{
											// ITEM CANCELLED
						                     Map changeOrderItemStatusMap = UtilMisc.toMap("orderId", (String)mRow.get("orderId"),
						                             "orderItemSeqId",orderItem.getString("orderItemSeqId"), "statusId", "ITEM_CANCELLED",
						                             "userLogin", userLogin);
						                	 try 
						                     {
						                         Map result = dispatcher.runSync("changeOrderItemStatus", changeOrderItemStatusMap);
						                     } 
						                     catch(GenericServiceException e)
						                     {
						                         Debug.logError(e, module);
						                     }
										}
										else
										{
											//ITEM COMPLETED
											Map changeOrderItemStatusMap = UtilMisc.toMap("orderId", (String)mRow.get("orderId"),
						                             "orderItemSeqId",orderItem.getString("orderItemSeqId"), "statusId", "ITEM_COMPLETED",
						                             "userLogin", userLogin);
						                	 try 
						                     {
						                         Map result = dispatcher.runSync("changeOrderItemStatus", changeOrderItemStatusMap);
						                     } 
						                     catch(GenericServiceException e)
						                     {
						                         Debug.logError(e, module);
						                     }
											
										}
									}
									processedOrderItemSeqIds.add(orderItem.getString("orderItemSeqId"));
								}
							 } 
            				 catch (GenericEntityException e) 
							 {
								e.printStackTrace();
							 }
            			 }
            		 }
                 }
             }
             
             //Create Order Note
             String orderNote = (String)mRow.get("orderNote");
             if(UtilValidate.isNotEmpty(orderNote))
             {
            	 Map createOrderNoteMap = UtilMisc.toMap("orderId", (String)mRow.get("orderId"),
                         "note",orderNote,"internalNote","Y", "userLogin", userLogin);
            	 try 
                 {
                     Map result = dispatcher.runSync("createOrderNote", createOrderNoteMap);
                 } 
                 catch(GenericServiceException e)
                 {
                     Debug.logError(e, module);
                 }
             }
         }
     }

    private static Boolean displayCountryFieldAsLong(String productStoreId)
	{
    	return displayAddressFieldAsLong(productStoreId, "COUNTRY");
	}

    private static Boolean displayStateFieldAsLong(String productStoreId)
	{
    	return displayAddressFieldAsLong(productStoreId, "STATE");
	}

    private static Boolean displayZipFieldAsLong(String productStoreId)
	{
    	return displayAddressFieldAsLong(productStoreId, "ZIP");
	}

    /**
     * read FORMAT_ADDRESS system parameter and format the field.
     * @param productStoreId String product store id
     * @param fieldType String address field type ex: COUNTRY, STATE, ZIP
     * @return a boolean field
     */
    private static Boolean displayAddressFieldAsLong(String productStoreId, String fieldType)
	{
		Boolean isAddressFieldLong = Boolean.FALSE;
        if (UtilValidate.isEmpty(productStoreId) || UtilValidate.isEmpty(fieldType)) 
        {
            return isAddressFieldLong;
        }
        String addressFormat = OsafeAdminUtil.getProductStoreParm(_delegator, productStoreId, "FORMAT_ADDRESS");
        if(UtilValidate.isNotEmpty(addressFormat))
        {
            for(String column : StringUtil.split(StringUtil.removeSpaces(addressFormat), ","))
            {
                if (column.indexOf("_") > 0)
                {
                    List<String> nameValueList = StringUtil.split(column, "_");
                    {
                    	if (fieldType.equalsIgnoreCase(nameValueList.get(0)) && "LONG".equalsIgnoreCase(nameValueList.get(1)))
                    	{
                    		isAddressFieldLong = Boolean.TRUE;
                    	}
                    }
                }
            }
        }
        return isAddressFieldLong;
	}

    private static String getGeoName(String geoId)
    {
    	if (UtilValidate.isEmpty(geoId))
    	{
    		return null;
    	}
    	String geoName = geoId;
    	try
    	{
    		GenericValue geo = _delegator.findByPrimaryKey("Geo", UtilMisc.toMap("geoId", geoId));
    		if(UtilValidate.isNotEmpty(geo))
    	    {
    			geoName = geo.getString("geoName");
    	    }
    	}
    	catch (Exception e)
    	{
			e.printStackTrace();
	    }
		return geoName;
    }
}

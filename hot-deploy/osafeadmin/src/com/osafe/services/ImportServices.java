package com.osafe.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import javolution.util.FastList;
import javolution.util.FastMap;
import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.osafe.constants.Constants;
import com.osafe.feeds.FeedsUtil;
import com.osafe.feeds.osafefeeds.*;

import com.osafe.util.OsafeAdminUtil;

public class ImportServices {

    public static final String module = ImportServices.class.getName();
    private static final ResourceBundle OSAFE_PROP = UtilProperties.getResourceBundle("osafe", Locale.getDefault());
    private static final ResourceBundle OSAFE_ADMIN_PROP = UtilProperties.getResourceBundle("osafeAdmin", Locale.getDefault());
    private static final Long FEATURED_PRODUCTS_CATEGORY = Long.valueOf(10054);
    private static final SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat _df = new DecimalFormat("##.00");
    private static Delegator _delegator = null;
    private static LocalDispatcher _dispatcher = null;
    private static Locale _locale =null;

    public static final WritableFont cellFont = new WritableFont(WritableFont.TIMES, 10);
    public static final WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
    
    private static Map<String, ?> context = FastMap.newInstance();
	
	private static String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
	
	public static List<Map<Object, Object>> imageLocationPrefList = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
	
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

        String importDir = OSAFE_PROP.getString("import.dir");
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

        String exportDir = OSAFE_PROP.getString("export.dir");
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
                        String definationName = UtilProperties.getPropertyValue("osafe", Constants.IMPOERT_XLS_ENTITY_PROPERTY_MAPPING_PREFIX+UtilValidate.stripWhitespace(fileNameWithoutExt), fileNameWithoutExt);
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

            } catch (Exception exc) {
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
    
    public static Map<String, Object> importProductXls(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String xlsDataFilePath = (String)context.get("xlsDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        //String xmlDataDirPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("import", "import.dir"), context);
        String loadImagesDirPath=(String)context.get("productLoadImagesDir");
        String imageUrl = (String)context.get("imageUrl");
        Boolean removeAll = (Boolean) context.get("removeAll");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        if (removeAll == null) removeAll = Boolean.FALSE;
        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        File baseDataDir = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xlsDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
            try {
                URL xlsDataFileUrl = UtilURL.fromFilename(xlsDataFilePath);
                InputStream ins = xlsDataFileUrl.openStream();

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

                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("en", "EN"));
                Workbook wb = Workbook.getWorkbook(inputWorkbook,ws);
                // Gets the sheets from workbook
                for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) {
                    BufferedWriter bw = null; 
                    try {
                        Sheet s = wb.getSheet(sheet);
                        String sTabName=s.getName();
                        
                        if (sheet == 1)
                        {
                            List dataRows = buildDataRows(buildCategoryHeader(),s);
                            buildProductCategory(dataRows, xmlDataDirPath,loadImagesDirPath, imageUrl);
                        }
                        if (sheet == 2)
                        {
                            List dataRows = buildDataRows(buildProductHeader(),s);
                            buildProduct(dataRows, xmlDataDirPath);
                            buildProductVariant(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                            buildProductSelectableFeatures(dataRows, xmlDataDirPath);
                            buildProductGoodIdentification(dataRows, xmlDataDirPath);
                            buildProductCategoryFeatures(dataRows, xmlDataDirPath);
                            buildProductDistinguishingFeatures(dataRows, xmlDataDirPath);
                            buildProductContent(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                            buildProductVariantContent(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                            buildProductAttribute(dataRows, xmlDataDirPath);
                        }
                        if (sheet == 3)
                        {
                            List dataRows = buildDataRows(buildProductAssocHeader(),s);
                            buildProductAssoc(dataRows, xmlDataDirPath);
                        }
                        if (sheet == 4)
                        {
                            List dataRows = buildDataRows(buildProductFeatureSwatchHeader(),s);
                            buildProductFeatureImage(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                        }
                        if (sheet == 5)
                        {
                            List dataRows = buildDataRows(buildManufacturerHeader(),s);
                            buildManufacturer(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                        }

                        //File to store data in form of CSV
                    } catch (Exception exc) {
                        Debug.logError(exc, module);
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

                // ############################################
                // call the service for remove entity data 
                // if removeAll and autoLoad parameter are true 
                // ############################################
                if (removeAll) {
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
                }

                // ##############################################
                // move the generated xml files in done directory
                // ##############################################
                File doneXmlDir = new File(baseDataDir, Constants.DONE_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
                File[] fileArray = baseDataDir.listFiles();
                for (File file: fileArray) {
                    try {
                        if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("XML")) {
                            FileUtils.copyFileToDirectory(file, doneXmlDir);
                            file.delete();
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
                     
                         List<String> serviceMsg = (List)result.get("messages");
                         for (String msg: serviceMsg) {
                             messages.add(msg);
                         }
                     } catch (Exception exc) {
                         Debug.logError(exc, module);
                     }
                }

            } catch (BiffException be) {
                Debug.logError(be, module);
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
            finally {
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

        //String xmlDataDirPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("import", "import.dir"), context);
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
            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
            headerFormat.setBackground(Colour.DARK_BLUE);
            
            int row=0;
            
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
    	
    	try {
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
    		
    	} catch (Exception e) {
            Debug.logError(e, module);

    	}
        
       }
    private static WritableSheet createWorkBookSheet(WritableWorkbook workbook,String sheetName,int sheetIdx) {

    	WritableSheet excelSheet=null;    	
    	try {
    		
            workbook.createSheet(sheetName,sheetIdx);
            excelSheet=workbook.getSheet(sheetIdx);
    		
    	} catch (Exception e) 
    	{
            Debug.logError(e, module);

    	}
   	    return excelSheet;
        
       }
    
    private static Map createWorkBookHeaderCaptions() {
        Map headerCols = FastMap.newInstance();
   	    headerCols.put("masterProductId","Master Product Id");
   	    headerCols.put("productId","Product Id");
   	    headerCols.put("productCategoryId","Category Id");
   	    headerCols.put("parentCategoryId","Parent Category Id");
   	    headerCols.put("categoryName","Category Name");
   	    headerCols.put("description","Description");
   	    headerCols.put("plpImageName","PLP Image Name");
   	    headerCols.put("plpText","Additional PLP Text");
   	    headerCols.put("pdpText","Additional PDP Text");
   	    headerCols.put("productIdTo","Product Id To");
   	    headerCols.put("productAssocTypeId","Product Association Type");
   	    headerCols.put("internalName","Internal Name");
   	    headerCols.put("productName","Product Name");
   	    headerCols.put("salesPitch","Sales Pitch");
   	    headerCols.put("longDescription","Long Description");
   	    headerCols.put("specialInstructions","Special Instructions");
   	    headerCols.put("deliveryInfo","Delivery Info");
   	    headerCols.put("directions","Directions");
   	    headerCols.put("termsConditions","Terms & Conds");
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
   	    headerCols.put("smallImage","PLP Image [SMALL_IMAGE_URL]");
   	    headerCols.put("smallImageAlt","PLP Image [SMALL_IMAGE_ALT_URL]");
   	    headerCols.put("thumbImage","PDP Image Primary: thumbnail [THUMBNAIL_IMAGE_URL]");
   	    headerCols.put("largeImage","PDP Initial Image Regular [LARGE_IMAGE_URL]");
   	    headerCols.put("detailImage","PDP Image Primary: big popup [DETAIL_IMAGE_URL]");
   	    headerCols.put("addImage1","PDP Image ALT-1: Thumbnail [ADDITIONAL_IMAGE_1]");
   	    headerCols.put("xtraLargeImage1","PDP Image ALT-1: regular [XTRA_IMAGE_1_LARGE]");
   	    headerCols.put("xtraDetailImage1","PDP Image ALT-1: big popup [XTRA_IMAGE_1_DETAIL]");
   	    headerCols.put("addImage2","PDP Image ALT-2: Thumbnail [ADDITIONAL_IMAGE_2]");
   	    headerCols.put("xtraLargeImage2","PDP Image ALT-2: regular [XTRA_IMAGE_2_LARGE]");
   	    headerCols.put("xtraDetailImage2","PDP Image ALT-2: big popup [XTRA_IMAGE_2_DETAIL]");
   	    headerCols.put("addImage3","PDP Image ALT-3: Thumbnail [ADDITIONAL_IMAGE_3]");
   	    headerCols.put("xtraLargeImage3","PDP Image ALT-3: regular [XTRA_IMAGE_3_LARGE]");
   	    headerCols.put("xtraDetailImage3","PDP Image ALT-3: big popup [XTRA_IMAGE_3_DETAIL]");
   	    headerCols.put("addImage4","PDP Image ALT-4: Thumbnail [ADDITIONAL_IMAGE_4]");
   	    headerCols.put("xtraLargeImage4","PDP Image ALT-4: regular [XTRA_IMAGE_4_LARGE]");
   	    headerCols.put("xtraDetailImage4","PDP Image ALT-4: big popup [XTRA_IMAGE_4_DETAIL]");
   	    headerCols.put("addImage5","PDP Image ALT-5: Thumbnail [ADDITIONAL_IMAGE_5]");
   	    headerCols.put("xtraLargeImage5","PDP Image ALT-5: regular [XTRA_IMAGE_5_LARGE]");
   	    headerCols.put("xtraDetailImage5","PDP Image ALT-5: big popup [XTRA_IMAGE_5_DETAIL]");
   	    headerCols.put("addImage6","PDP Image ALT-1: Thumbnail [ADDITIONAL_IMAGE_6]");
   	    headerCols.put("xtraLargeImage6","PDP Image ALT-1: regular [XTRA_IMAGE_6_LARGE]");
   	    headerCols.put("xtraDetailImage6","PDP Image ALT-1: big popup [XTRA_IMAGE_6_DETAIL]");
   	    headerCols.put("addImage7","PDP Image ALT-1: Thumbnail [ADDITIONAL_IMAGE_7]");
   	    headerCols.put("xtraLargeImage7","PDP Image ALT-1: regular [XTRA_IMAGE_7_LARGE]");
   	    headerCols.put("xtraDetailImage7","PDP Image ALT-1: big popup [XTRA_IMAGE_7_DETAIL]");
   	    headerCols.put("addImage8","PDP Image ALT-1: Thumbnail [ADDITIONAL_IMAGE_8]");
   	    headerCols.put("xtraLargeImage8","PDP Image ALT-1: regular [XTRA_IMAGE_8_LARGE]");
   	    headerCols.put("xtraDetailImage8","PDP Image ALT-1: big popup [XTRA_IMAGE_8_DETAIL]");
   	    headerCols.put("addImage9","PDP Image ALT-1: Thumbnail [ADDITIONAL_IMAGE_9]");
   	    headerCols.put("xtraLargeImage9","PDP Image ALT-1: regular [XTRA_IMAGE_9_LARGE]");
   	    headerCols.put("xtraDetailImage9","PDP Image ALT-1: big popup [XTRA_IMAGE_9_DETAIL]");
   	    headerCols.put("addImage10","PDP Image ALT-1: Thumbnail [ADDITIONAL_IMAGE_10]");
   	    headerCols.put("xtraLargeImage10","PDP Image ALT-1: regular [XTRA_IMAGE_10_LARGE]");
   	    headerCols.put("xtraDetailImage10","PDP Image ALT-1: big popup [XTRA_IMAGE_10_DETAIL]");
   	    headerCols.put("productHeight","Product Height");
   	    headerCols.put("productWidth","Product Width");
   	    headerCols.put("productDepth","Product Depth");
   	    headerCols.put("returnable","Returnable (Y or N)");
   	    headerCols.put("taxable","Taxable (Y or N)");
   	    headerCols.put("chargeShipping","Charge Shipping (Y or N)");
   	    headerCols.put("fromDate","From Date");
   	    headerCols.put("thruDate","Thru Date");
   	    headerCols.put("manufacturerId","Manufacturer Id");
   	    headerCols.put("partyId","Manufacturer Id");
   	    headerCols.put("date","Date");
   	    headerCols.put("who","Who");
   	    headerCols.put("changes","Changes");
   	    headerCols.put("manufacturerName","Name");
   	    headerCols.put("address1","Address");
   	    headerCols.put("city","City");
   	    headerCols.put("state","State");
   	    headerCols.put("zip","Zip");
   	    headerCols.put("shortDescription","Short Description");
   	    headerCols.put("manufacturerImage","Profile Image Name");
   	    headerCols.put("profileFbUrl","Facebook URL");
   	    headerCols.put("profileTweetUrl","Tweet URL");
   	    headerCols.put("featureId","Feature");
   	    headerCols.put("plpSwatchImage","PLP Swatch");
   	    headerCols.put("pdpSwatchImage","PDP Swatch");
   	    headerCols.put("goodIdentificationSkuId","GOOD_ID (SKU)");
   	    headerCols.put("goodIdentificationGoogleId","GOOD_ID (Google)");
   	    headerCols.put("goodIdentificationIsbnId","GOOD_ID (ISBN)");
   	    headerCols.put("goodIdentificationManufacturerId","GOOD_ID (Manu-ID)");
   	    headerCols.put("pdpVideoUrl","PDP_VIDEO_URL");
   	    headerCols.put("pdpVideo360Url","PDP_VIDEO_360_URL");
   	    headerCols.put("sequenceNum","SEQUENCE_NUM");
	    headerCols.put("bfInventoryTot","BF_INVENTORY_TOT");
	    headerCols.put("bfInventoryWhs","BF_INVENTORY_WHS");
   	    
   	    
   	    return headerCols;
        
       }
    
    private static int createProductCategoryWorkSheet(WritableSheet excelSheet,String browseRootProductCategoryId) {
        int iRowIdx=1;
    	try {
    		
            List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, false, false, true);
            GenericValue workingCategory = null;
            GenericValue workingCategoryRollup = null;
            String productCategoryIdPath = null;
            int categoryLevel = 0;
            List<String> categoryTrail = null;
            int iColIdx=0;
            String contentValue=null;
            Timestamp tsstamp=null;
            List<String> pathElements=null;
            String categoryImageURL=null;
            for (Map<String, Object> workingCategoryMap : productCategories) 
            {
            	iColIdx=0;
                workingCategory = (GenericValue) workingCategoryMap.get("ProductCategory");
                workingCategoryRollup = (GenericValue) workingCategoryMap.get("ProductCategoryRollup");
                if ("CATALOG_CATEGORY".equals(workingCategory.getString("productCategoryTypeId"))) 
                {
                    String productCategoryId = (String) workingCategory.getString("productCategoryId");
        	        List<GenericValue> lCategoryContent = _delegator.findByAnd("ProductCategoryContent", UtilMisc.toMap("productCategoryId",productCategoryId),UtilMisc.toList("-fromDate"));
        	        lCategoryContent=EntityUtil.filterByDate(lCategoryContent, UtilDateTime.nowTimestamp());
                    createWorkBookRow(excelSheet,workingCategory.getString("productCategoryId"), iColIdx++, iRowIdx);
                    String parentCategoryId = (String) workingCategory.getString("primaryParentCategoryId");
                    createWorkBookRow(excelSheet,workingCategory.getString("primaryParentCategoryId"),iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,workingCategory.getString("categoryName"),iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,workingCategory.getString("description"),iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet,workingCategory.getString("longDescription"),iColIdx++, iRowIdx);
                    categoryImageURL =workingCategory.getString("categoryImageUrl");
                    
                	String categoryImagePath = getOsafeImagePath("CATEGORY_IMAGE_URL");
                    if (UtilValidate.isNotEmpty(categoryImageURL))
                    {
                        pathElements = StringUtil.split(categoryImageURL, "/");
                        createWorkBookRow(excelSheet,categoryImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                    	
                    }
                    createWorkBookRow(excelSheet, getProductCategoryContent(productCategoryId,"PLP_ESPOT_CONTENT",lCategoryContent), iColIdx++, iRowIdx);
                    createWorkBookRow(excelSheet, getProductCategoryContent(productCategoryId,"PDP_ADDITIONAL",lCategoryContent), iColIdx++, iRowIdx);
                    tsstamp = workingCategoryRollup.getTimestamp("fromDate");
                    if (UtilValidate.isNotEmpty(tsstamp))
                    {
                    	
                        createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
                    }
                    else
                    {
                        createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
                    	
                    }
                    tsstamp = workingCategoryRollup.getTimestamp("thruDate");
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
    		
            List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, false, false, true);
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
            	        List<GenericValue> productAssocitations = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", "PRODUCT_VARIANT"),UtilMisc.toList("sequenceNum"));
            	        if (UtilValidate.isNotEmpty(productAssocitations))
            	        {
            	        	boolean bFirstVariant=true;
                            for (GenericValue productAssoc : productAssocitations) 
                            {
                                GenericValue variantProduct = productAssoc.getRelatedOne("AssocProduct");
                            	if (bFirstVariant)
                            	{
                    	        	addWorkSheetProductRow(excelSheet,product,iRowIdx);
                                    addWorkSheetProductVariantRow(excelSheet,variantProduct,productId,iRowIdx,bFirstVariant);
                            		bFirstVariant=false;
                            	}
                            	else
                            	{
                                    addWorkSheetProductVariantRow(excelSheet,variantProduct,productId,iRowIdx,bFirstVariant);
                            		
                            	}
                            	iRowIdx++;
                            }
            	        	
            	        }
            	        else
            	        {
            	        	addWorkSheetProductRow(excelSheet,product,iRowIdx);
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
    

    private static void addWorkSheetProductVariantRow(WritableSheet excelSheet,GenericValue variantProduct, String productId, int iRowIdx, boolean bFirstVariant) {
    	int iColIdx=0;
    	List<String> pathElements=null;
    	String imageURL=null;
    	try {
    		String variantProductId=variantProduct.getString("productId");
        	createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
        	createWorkBookRow(excelSheet,variantProductId,iColIdx++, iRowIdx);
        	if (bFirstVariant)
        	{
        		iColIdx=iColIdx + 15;
        	}
        	else
        	{
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        		
        	}

            List<GenericValue> lProductContent = _delegator.findByAnd("ProductContent", UtilMisc.toMap("productId",variantProductId),UtilMisc.toList("-fromDate"));
            lProductContent=EntityUtil.filterByDate(lProductContent, UtilDateTime.nowTimestamp());
            
            List<GenericValue> productSelectableFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", variantProductId, "productFeatureApplTypeId", "STANDARD_FEATURE"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
            productSelectableFeatures = EntityUtil.filterByDate(productSelectableFeatures);
            
            int iSelectFeatureIdx=1;
            Map mSelectFeature = FastMap.newInstance();
            for (GenericValue productSelectableFeature : productSelectableFeatures) 
            {
            	if (iSelectFeatureIdx < 6)
            	{
            		mSelectFeature.put("selectFeature_" + iSelectFeatureIdx, productSelectableFeature.getString("productFeatureTypeId") + ":" + productSelectableFeature.getString("description"));
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
            	
            }
            imageURL =getProductContent(variantProductId,"PLP_SWATCH_IMAGE_URL",lProductContent);
            String plpSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,plpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(variantProductId,"PDP_SWATCH_IMAGE_URL",lProductContent);
            String pdpSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,pdpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            selectFeature = (String)mSelectFeature.get("selectFeature_2");
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
            	
            }
        	if (bFirstVariant)
        	{
        		iColIdx=iColIdx + 5;
        	}
        	else
        	{
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
        		
        	}
            
            imageURL =getProductContent(variantProductId,"SMALL_IMAGE_URL",lProductContent);
            String smallImagePath = getOsafeImagePath("SMALL_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,smallImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(variantProductId,"SMALL_IMAGE_ALT_URL",lProductContent);
            String smallAltImagePath = getOsafeImagePath("SMALL_IMAGE_ALT_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,smallAltImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(variantProductId,"THUMBNAIL_IMAGE_URL",lProductContent);
            String thumbnailImagePath = getOsafeImagePath("THUMBNAIL_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,thumbnailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(variantProductId,"LARGE_IMAGE_URL",lProductContent);
            String largeImagePath = getOsafeImagePath("LARGE_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,largeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(variantProductId,"DETAIL_IMAGE_URL",lProductContent);
            String detailImagePath = getOsafeImagePath("DETAIL_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,detailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            for (int i=1; i < 11; i++)
            {
                imageURL =getProductContent(variantProductId,"ADDITIONAL_IMAGE_" + i,lProductContent);
                String additionalImagePath = getOsafeImagePath("ADDITIONAL_IMAGE_" + i);
                if (UtilValidate.isNotEmpty(imageURL))
                {
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,additionalImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(variantProductId,"XTRA_IMG_" + i +"_LARGE",lProductContent);
                String additionalLargeImagePath = getOsafeImagePath("XTRA_IMG_" + i +"_LARGE");
                if (UtilValidate.isNotEmpty(imageURL))
                {
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,additionalLargeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(variantProductId,"XTRA_IMG_" + i + "_DETAIL",lProductContent);
                String additionalDetailImagePath = getOsafeImagePath("XTRA_IMG_" + i + "_DETAIL");
                if (UtilValidate.isNotEmpty(imageURL))
                {
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,additionalDetailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
            	
            }
        	if (bFirstVariant)
        	{
        		iColIdx=iColIdx + 16;
        	} else {
        		createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
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
            
            
    	}
    	catch (Exception e) 
    	{
            Debug.logError(e, module);
    		
    	}

    }
    
    private static void addWorkSheetProductRow(WritableSheet excelSheet,GenericValue product,int iRowIdx) {
    	int iColIdx=0;
    	List<String> pathElements=null;
    	String imageURL=null;
    	
    	try {
    		String productId = product.getString("productId");
        	createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
        	createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
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

            List<GenericValue> productSelectableFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "SELECTABLE_FEATURE"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
            productSelectableFeatures = EntityUtil.filterByDate(productSelectableFeatures);
            int iSelectFeatureIdx=1;
            Map mSelectFeature = FastMap.newInstance();
            for (GenericValue productSelectableFeature : productSelectableFeatures) 
            {
            	if (iSelectFeatureIdx < 6)
            	{
            		mSelectFeature.put("selectFeature_" + iSelectFeatureIdx, productSelectableFeature.getString("productFeatureTypeId") + ":" + productSelectableFeature.getString("description"));
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
            	
            }
            imageURL =getProductContent(productId,"PLP_SWATCH_IMAGE_URL",lProductContent);
            String plpSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,plpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(productId,"PDP_SWATCH_IMAGE_URL",lProductContent);
            String pdpSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,pdpSwatchImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            selectFeature = (String)mSelectFeature.get("selectFeature_2");
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
            	
            }
            
            List<GenericValue> productDistinguishFeatures = _delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "DISTINGUISHING_FEAT"),UtilMisc.toList("productFeatureTypeId","sequenceNum"));
            productDistinguishFeatures = EntityUtil.filterByDate(productDistinguishFeatures);
            iColIdx=createWorkBookProductFeatures(excelSheet,productDistinguishFeatures,iColIdx,iRowIdx);
            
            imageURL =getProductContent(productId,"SMALL_IMAGE_URL",lProductContent);
            String smallImagePath = getOsafeImagePath("SMALL_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,smallImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(productId,"SMALL_IMAGE_ALT_URL",lProductContent);
            String smallAltImagePath = getOsafeImagePath("SMALL_IMAGE_ALT_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,smallAltImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }

            imageURL =getProductContent(productId,"THUMBNAIL_IMAGE_URL",lProductContent);
            String thumbnailImagePath = getOsafeImagePath("THUMBNAIL_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,thumbnailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(productId,"LARGE_IMAGE_URL",lProductContent);
            String largeImagePath = getOsafeImagePath("LARGE_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,largeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            imageURL =getProductContent(productId,"DETAIL_IMAGE_URL",lProductContent);
            String detailImagePath = getOsafeImagePath("DETAIL_IMAGE_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,detailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            for (int i=1; i < 11; i++)
            {
                imageURL =getProductContent(productId,"ADDITIONAL_IMAGE_" + i,lProductContent);
                String additionalImagePath = getOsafeImagePath("ADDITIONAL_IMAGE_" + i);
                if (UtilValidate.isNotEmpty(imageURL))
                {
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,additionalImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(productId,"XTRA_IMG_" + i +"_LARGE",lProductContent);
                String additionalLargeImagePath = getOsafeImagePath("XTRA_IMG_" + i +"_LARGE");
                if (UtilValidate.isNotEmpty(imageURL))
                {
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,additionalLargeImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                imageURL =getProductContent(productId,"XTRA_IMG_" + i + "_DETAIL",lProductContent);
                String additionalDetailImagePath = getOsafeImagePath("XTRA_IMG_" + i + "_DETAIL");
                if (UtilValidate.isNotEmpty(imageURL))
                {
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,additionalDetailImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
            	
            }
        	createWorkBookRow(excelSheet,product.getString("productHeight"),iColIdx++, iRowIdx);
        	createWorkBookRow(excelSheet,product.getString("productWidth"),iColIdx++, iRowIdx);
        	createWorkBookRow(excelSheet,product.getString("productDepth"),iColIdx++, iRowIdx);
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
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
            }
            tsstamp = product.getTimestamp("salesDiscontinuationDate");
            if (UtilValidate.isNotEmpty(tsstamp))
            {
            	
                createWorkBookRow(excelSheet,_sdf.format(new Date(tsstamp.getTime())) , iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,null, iColIdx++, iRowIdx);
            	
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
            String pdpVideoUrlPath = getOsafeImagePath("PDP_VIDEO_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,pdpVideoUrlPath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
            }
            else
            {
                createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
            	
            }
            
            imageURL =getProductContent(productId,"PDP_VIDEO_360_URL",lProductContent);
            String pdpVideo360UrlPath = getOsafeImagePath("PDP_VIDEO_360_URL");
            if (UtilValidate.isNotEmpty(imageURL))
            {
                pathElements = StringUtil.split(imageURL, "/");
                createWorkBookRow(excelSheet,pdpVideo360UrlPath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
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
    		
            List<Map<String, Object>> productCategories = OsafeAdminCatalogServices.getRelatedCategories(_delegator, browseRootProductCategoryId, null, false, false, true);
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
            	        List<GenericValue> productAssocitations = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productIdTo", productId, "productAssocTypeId", "PRODUCT_COMPLEMENT"),UtilMisc.toList("sequenceNum"));
            	        if(UtilValidate.isNotEmpty(productAssocitations)) {
            	            productAssocitations = EntityUtil.filterByDate(productAssocitations, true);
            	        }
                        for (GenericValue productAssoc : productAssocitations) 
                        {
                        	iColIdx=0;
                            createWorkBookRow(excelSheet,productId,iColIdx++, iRowIdx);
                            createWorkBookRow(excelSheet,productAssoc.getString("productId"),iColIdx++, iRowIdx);
                        	
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
    	        List<GenericValue> lPartyContent = _delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId",partyId),UtilMisc.toList("-fromDate"));
    	        lPartyContent=EntityUtil.filterByDate(lPartyContent,UtilDateTime.nowTimestamp());
            	partyId=party.getString("partyId");
                createWorkBookRow(excelSheet,partyId,iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet, getPartyContent(partyId,"PROFILE_NAME",lPartyContent), iColIdx++, iRowIdx);
                
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
                String profileImagePath = getOsafeImagePath("PROFILE_IMAGE_URL");
                if (UtilValidate.isNotEmpty(imageURL))
                {
                    pathElements = StringUtil.split(imageURL, "/");
                    createWorkBookRow(excelSheet,profileImagePath + pathElements.get(pathElements.size() - 1), iColIdx++, iRowIdx);
                }
                else
                {
                    createWorkBookRow(excelSheet,"", iColIdx++, iRowIdx);
                	
                }
                createWorkBookRow(excelSheet, getPartyContent(partyId,"PROFILE_FB_LIKE_URL",lPartyContent), iColIdx++, iRowIdx);
                createWorkBookRow(excelSheet, getPartyContent(partyId,"PROFILE_TWEET_URL",lPartyContent), iColIdx++, iRowIdx);

                
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
    	try {
    		
	        List<GenericValue> lProductFeatureDataResource = _delegator.findByAnd("ProductFeatureDataResource", UtilMisc.toMap("prodFeatureDataResourceTypeId","PLP_SWATCH_IMAGE_URL"),UtilMisc.toList("dataResourceId"));
	        String featurePLPSwatchImagePath = getOsafeImagePath("PLP_SWATCH_IMAGE_URL");
	        Map mFeatureRow = FastMap.newInstance();
            for (GenericValue productFeatureDataResource : lProductFeatureDataResource) 
            {
            	GenericValue productFeature = (GenericValue) productFeatureDataResource.getRelatedOne("ProductFeature");
            	GenericValue dataResource  = (GenericValue) productFeatureDataResource.getRelatedOne("DataResource");
            	String productFeatureId = productFeature.getString("productFeatureId");
            	String productFeatureTypeId = productFeature.getString("productFeatureTypeId");
            	String productFeatureDescription = productFeature.getString("description");
            	String dataResourceName = dataResource.getString("dataResourceName"); 
                mFeatureRow.put(productFeatureId,"" + iRowIdx);
                createWorkBookRow(excelSheet,productFeatureTypeId + ":" + productFeatureDescription,0, iRowIdx);
                createWorkBookRow(excelSheet,featurePLPSwatchImagePath + dataResourceName,1, iRowIdx);
                iRowIdx++;
                iFeatureSwatchCnt++;
            }
	        lProductFeatureDataResource = _delegator.findByAnd("ProductFeatureDataResource", UtilMisc.toMap("prodFeatureDataResourceTypeId","PDP_SWATCH_IMAGE_URL"),UtilMisc.toList("dataResourceId"));
	        String featurePDPSwatchImagePath = getOsafeImagePath("PDP_SWATCH_IMAGE_URL");
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
                    createWorkBookRow(excelSheet,featurePDPSwatchImagePath + dataResourceName,2, Integer.parseInt(sRowIdx));
            	}
            	else
            	{
                    createWorkBookRow(excelSheet,productFeatureTypeId + ":" + productFeatureDescription,0, iRowIdx);
                    createWorkBookRow(excelSheet,featurePDPSwatchImagePath + dataResourceName,2, iRowIdx);
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
   	    headerCols.add("fromDate");
   	    headerCols.add("thruDate");
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
   	    
   	    return headerCols;
        
       }

    public static List buildManufacturerHeader() {
        List headerCols = FastList.newInstance();
   	    headerCols.add("partyId");
   	    headerCols.add("manufacturerName");
   	    headerCols.add("address1");
   	    headerCols.add("city");
   	    headerCols.add("state");
   	    headerCols.add("zip");
   	    headerCols.add("shortDescription");
   	    headerCols.add("longDescription");
   	    headerCols.add("manufacturerImage");
   	    headerCols.add("profileFbUrl");
   	    headerCols.add("profileTweetUrl");
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
    
    public static List buildDataRows(List headerCols,Sheet s) {
		List dataRows = FastList.newInstance();

		try {

            for (int rowCount = 1 ; rowCount < s.getRows() ; rowCount++) {
            	Cell[] row = s.getRow(rowCount);
             if (row.length > 0) 
             {
            	Map mRows = FastMap.newInstance();
                for (int colCount = 0; colCount < headerCols.size(); colCount++) {
                	String colContent=null;
                
                	 try {
                		 colContent=row[colCount].getContents();
                	 }
                	   catch (Exception e) {
                		   colContent="";
                		   
                	   }
                  mRows.put(headerCols.get(colCount),StringUtil.replaceString(colContent,"\"","'"));
                }
                mRows = formatProductXLSData(mRows);
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
                     rowString.append("description" + "=\"" + (String)mRow.get("description") + "\" ");
                     rowString.append("longDescription" + "=\"" + (String)mRow.get("longDescription") + "\" ");
                     categoryImageName=(String)mRow.get("plpImageName");
     	             
                     if (UtilValidate.isNotEmpty(categoryImageName))
                     {
                    	 Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
                     	 for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
                     		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
                     	 }
                     	 String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
                     	 if(UtilValidate.isNotEmpty(defaultImageDirectory)) {
                             categoryImageName = defaultImageDirectory + categoryImageName;
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
                    
                    rowString.setLength(0);
                    rowString.append("<" + "ProductCategoryRollup" + " ");
                    rowString.append("productCategoryId" + "=\"" + mRow.get("productCategoryId") + "\" ");
                    rowString.append("parentProductCategoryId" + "=\"" + mRow.get("parentCategoryId") + "\" ");
                    if (UtilValidate.isEmpty(mRow.get("fromDate")))
                    {
                   	 rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
                    }
                    else
                    {
                        rowString.append("fromDate" + "=\"" + mRow.get("fromDate") + "\" ");
                    }
                   		 
                    rowString.append("thruDate" + "=\"" + "" + "\" ");
                    rowString.append("sequenceNum" + "=\"" + ((i +1) *10) + "\" ");
                    rowString.append("/>");
                   bwOutFile.write(rowString.toString());
                   bwOutFile.newLine();
                    
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
    
    private static void buildManufacturer(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl) {

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
                     rowString.append("address1" + "=\"" +  (String)mRow.get("address1") + "\" ");
                     rowString.append("city" + "=\"" +  (String)mRow.get("city") + "\" ");
                     rowString.append("stateProvinceGeoId" + "=\"" +  mRow.get("state") + "\" ");
                     rowString.append("postalCode" + "=\"" +  mRow.get("zip") + "\" ");
                     rowString.append("/>");
                     bwOutFile.write(rowString.toString());
                     bwOutFile.newLine();
                   addPartyContentRow(rowString, mRow, bwOutFile, "text", "DESCRIPTION", "shortDescription",loadImagesDirPath,imageUrl);
                   addPartyContentRow(rowString, mRow, bwOutFile, "text", "LONG_DESCRIPTION", "longDescription",loadImagesDirPath,imageUrl);
                   addPartyContentRow(rowString, mRow, bwOutFile, "image", "PROFILE_IMAGE_URL", "manufacturerImage",loadImagesDirPath,imageUrl);
                   addPartyContentRow(rowString, mRow, bwOutFile, "text", "PROFILE_NAME", "manufacturerName",loadImagesDirPath,imageUrl);
                   addPartyContentRow(rowString, mRow, bwOutFile, "text", "PROFILE_FB_LIKE_URL", "profileFbUrl",loadImagesDirPath,imageUrl);
                   addPartyContentRow(rowString, mRow, bwOutFile, "text", "PROFILE_TWEET_URL", "profileTweetUrl",loadImagesDirPath,imageUrl);
 	            	
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
    private static void buildProduct(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        Map mMasterProductId=FastMap.newInstance();
        
        
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
	            	 masterProductId=(String)mRow.get("masterProductId");
	            	 
	            	 if (!mMasterProductId.containsKey(masterProductId))
	            	 {
	            		 mMasterProductId.put(masterProductId, masterProductId);
	                     rowString.setLength(0);
	                     rowString.append("<" + "Product" + " ");
		            	 rowString.append("productId" + "=\"" + masterProductId + "\" ");
	                     rowString.append("productTypeId" + "=\"" + "FINISHED_GOOD" + "\" ");
	                     String productCategoryId = (String)mRow.get("productCategoryId");
	                     String[] productCategoryIds = productCategoryId.split(",");
	                     String primaryProductCategoryId =productCategoryIds[0].trim();
	                     rowString.append("primaryProductCategoryId" + "=\"" + primaryProductCategoryId + "\" ");
	                     rowString.append("manufacturerPartyId" + "=\"" + mRow.get("manufacturerId") + "\" ");
	                     rowString.append("internalName" + "=\"" + (String)mRow.get("internalName") + "\" ");
	                     rowString.append("brandName" + "=\"" + "" + "\" ");
	         			 String sFromDate = (String)mRow.get("fromDate");
	         			 
	        			 if (UtilValidate.isNotEmpty(sFromDate))
	        			 {
	                         rowString.append("introductionDate" + "=\"" + sFromDate + "\" ");
	        			 }
	        			 else
	        			 {
	                         rowString.append("introductionDate" + "=\"" + "" + "\" ");
	        			 }
	                     rowString.append("productName" + "=\"" + "" + "\" ");
	         			 String sThruDate = (String)mRow.get("thruDate");
	        			 if (UtilValidate.isNotEmpty(sThruDate))
	        			 {
	                         rowString.append("salesDiscontinuationDate" + "=\"" + sThruDate + "\" ");
	        			 }
	        			 else
	        			 {
	                         rowString.append("salesDiscontinuationDate" + "=\"" + "" + "\" ");
	        			 }
	                     rowString.append("requireInventory" + "=\"" + "N"+ "\" ");
	                     rowString.append("returnable" + "=\"" + mRow.get("returnable") + "\" ");
	                     rowString.append("taxable" + "=\"" + mRow.get("taxable") + "\" ");
	                     rowString.append("chargeShipping" + "=\"" + mRow.get("chargeShipping") + "\" ");
	                     rowString.append("productHeight" + "=\"" + mRow.get("productHeight") + "\" ");
	                     rowString.append("productWidth" + "=\"" + mRow.get("productWidth") + "\" ");
	                     rowString.append("productDepth" + "=\"" + mRow.get("productDepth") + "\" ");
	                     String isVirtual="N";
	 	            	 if (UtilValidate.isNotEmpty(mRow.get("selectabeFeature_1")))
	                     {
	                    	 isVirtual="Y";
	                     }
	                     rowString.append("isVirtual" + "=\"" + isVirtual + "\" ");
	                     rowString.append("isVariant" + "=\"" + "N" + "\" ");
	                     rowString.append("/>");
	                     bwOutFile.write(rowString.toString());
	                     bwOutFile.newLine();
						 
	                     for (int j=0;j < productCategoryIds.length;j++)
	                     {
	                    	 String sequenceNum = (String)mRow.get("sequenceNum");
	                    	 String productCategoryFromDate = (String)mRow.get("fromDate");
		         			 String productCategoryThruDate = (String)mRow.get("thruDate");
		         			 
		         			 if(UtilValidate.isEmpty(sequenceNum)) {
		         				sequenceNum = "10";
		         			 }
						     if(UtilValidate.isNotEmpty(productCategoryIds[j].trim())) {
						     
						     if(UtilValidate.isNotEmpty(mRow.get(productCategoryIds[j].trim()+"_sequenceNum"))) {
						         sequenceNum =  (String) mRow.get(productCategoryIds[j].trim()+"_sequenceNum");
						     }
						     if(UtilValidate.isNotEmpty(mRow.get(productCategoryIds[j].trim()+"_fromDate"))) {
						    	 productCategoryFromDate =  (String) mRow.get(productCategoryIds[j].trim()+"_fromDate");
						     }
						     if(UtilValidate.isNotEmpty(mRow.get(productCategoryIds[j].trim()+"_thruDate"))) {
						    	 productCategoryThruDate =  (String) mRow.get(productCategoryIds[j].trim()+"_thruDate");
						     }
	                         rowString.setLength(0);
	                         rowString.append("<" + "ProductCategoryMember" + " ");
	                         rowString.append("productCategoryId" + "=\"" + productCategoryIds[j].trim()+ "\" ");
	                         rowString.append("productId" + "=\"" + masterProductId+ "\" ");
	            			 if (UtilValidate.isNotEmpty(productCategoryFromDate))
	            			 {
	                             rowString.append("fromDate" + "=\"" + productCategoryFromDate + "\" ");
	            			 }
	            			 else
	            			 {
	                             rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
	            			 }
	            			 if (UtilValidate.isNotEmpty(productCategoryThruDate))
	            			 {
	                             rowString.append("thruDate" + "=\"" + productCategoryThruDate + "\" ");
	            			 }
	            			 else
	            			 {
	                             rowString.append("thruDate" + "=\"" + "" + "\" ");
	            			 }
	                         rowString.append("comments" + "=\"" + "" + "\" ");
	                         rowString.append("sequenceNum" + "=\"" + sequenceNum + "\" ");
	                         rowString.append("quantity" + "=\"" + "" + "\" ");
	                         rowString.append("/>");
	                         bwOutFile.write(rowString.toString());
	                         bwOutFile.newLine();
							 }
	                     	
	                     }
	                    if(UtilValidate.isNotEmpty(mRow.get("listPriceCurrency"))) {
		                   	currencyUomId = (String) mRow.get("listPriceCurrency");
		                }
		                if(UtilValidate.isNotEmpty(mRow.get("listPriceFromDate"))) {
		                    priceFromDate = (String) mRow.get("listPriceFromDate");
		                }
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
	                    	rowString.append("thruDate" + "=\"" + mRow.get("listPriceThruDate") + "\" ");
	                    }
	                    rowString.append("/>");
	                    bwOutFile.write(rowString.toString());
	                    bwOutFile.newLine();
	                    
	                    if(UtilValidate.isNotEmpty(mRow.get("defaultPriceCurrency"))) {
	                    	currencyUomId = (String) mRow.get("defaultPriceCurrency");
	                    }
	                    if(UtilValidate.isNotEmpty(mRow.get("defaultPriceFromDate"))) {
	                    	priceFromDate = (String) mRow.get("defaultPriceFromDate");
	                    }
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
	                    	rowString.append("thruDate" + "=\"" + mRow.get("defaultPriceThruDate") + "\" ");
	                    }
	                    rowString.append("/>");
	                    bwOutFile.write(rowString.toString());
	                    bwOutFile.newLine();
	            		 
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
    private static void buildProductVariant(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
        
        StringBuilder  rowString = new StringBuilder();
        
		try {
			
	        fOutFile = new File(xmlDataDirPath, "040-ProductVariant.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
              	    Map mRow = (Map)dataRows.get(i);
              	    String productId=(String)mRow.get("masterProductId");
      			    String featureProductId=(String)mRow.get("productId");
      			    String sDescription = "";
      	            String sInternalName = "";
              	    mFeatureTypeMap.clear();
              	    int iSeq = 0;
              	    
              	    int totSelectableFeatures = 5;
            	    if(UtilValidate.isNotEmpty(mRow.get("totSelectableFeatures"))) {
            	    	totSelectableFeatures =  Integer.parseInt((String)mRow.get("totSelectableFeatures"));
				    }
            	    for(int j = 1; j <= totSelectableFeatures; j++) 
                    {
            	    	buildFeatureMap(mFeatureTypeMap, (String)mRow.get("selectabeFeature_"+j));
                    }
                    
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
	                			    String feature =(String)iterFeature.next();
	                			
	                			    if (productId.equals(featureProductId))
	                			    {
	                				    featureProductId=featureProductId + "-0";
	                			    }
	                			
	                			    sInternalName=(String)mRow.get("internalName");
	                			
	                			    if(UtilValidate.isNotEmpty(sDescription)) {
	                				    sDescription = sDescription +" "+ feature;
	                			    } else {
	                				    sDescription = feature;
	                			    }
	                		    }
	            		    }
              	        }
                    
                    addProductVariantRow(rowString, bwOutFile, mRow, loadImagesDirPath,imageUrl,productId, featureProductId, sDescription, sInternalName, iSeq);
                    
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
        Map mMasterProductId=FastMap.newInstance();
        
        
		try {

	        fOutFile = new File(xmlDataDirPath, "045-ProductGoodIdentification.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 masterProductId=(String)mRow.get("masterProductId");
	            	 if (!mMasterProductId.containsKey(masterProductId))
	            	 {
	            		 mMasterProductId.put(masterProductId, masterProductId);
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationSkuId","SKU");
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationGoogleId", "GOOGLE_ID");
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationIsbnId", "ISBN");
        				 addProductGoodIdentificationRow(rowString, mRow, bwOutFile, masterProductId,"goodIdentificationManufacturerId", "MANUFACTURER_ID_NO");
	            		 
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
        Map mProductId=FastMap.newInstance();
        
        
		try {

	        fOutFile = new File(xmlDataDirPath, "075-ProductAttribute.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 productId=(String)mRow.get("productId");
	            	 if (!mProductId.containsKey(productId))
	            	 {
	            		 mProductId.put(productId, productId);
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"bfInventoryTot","BF_INVENTORY_TOT");
        				 addProductAttributeRow(rowString, mRow, bwOutFile, productId,"bfInventoryWhs","BF_INVENTORY_WHS");
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
    

    private static void addProductVariantRow(StringBuilder rowString,BufferedWriter bwOutFile,Map mRow,String loadImagesDirPath, String imageUrl, String masterProductId,String featureProductId,String description,String internalName,int iSeq) {
    	String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "USD");
    	String priceFromDate = _sdf.format(UtilDateTime.nowTimestamp());
    	try {
    		
		   rowString.setLength(0);
           rowString.append("<" + "Product" + " ");
           rowString.append("productId" + "=\"" + featureProductId + "\" ");
           rowString.append("productTypeId" + "=\"" + "FINISHED_GOOD" + "\" ");
           rowString.append("internalName" + "=\"" + internalName+ "_" + description + "\" ");
           rowString.append("description" + "=\"" + description + "\" ");
           rowString.append("isVirtual" + "=\"" + "N" + "\" ");
           rowString.append("isVariant" + "=\"" + "Y" + "\" ");
           rowString.append("/>");
           bwOutFile.write(rowString.toString());
           bwOutFile.newLine();
           
           List<GenericValue> productAssocList = _delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", masterProductId, "productIdTo", featureProductId, "productAssocTypeId", "PRODUCT_VARIANT"));
           if(UtilValidate.isNotEmpty(productAssocList)) {
        	   productAssocList = EntityUtil.filterByDate(productAssocList, true);
           }
           if(UtilValidate.isEmpty(productAssocList)) {
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
        	   if(UtilValidate.isNotEmpty(mRow.get("listPriceCurrency"))) {
                   currencyUomId = (String) mRow.get("listPriceCurrency");
               }
               if(UtilValidate.isNotEmpty(mRow.get("listPriceFromDate"))) {
                   priceFromDate = (String) mRow.get("listPriceFromDate");
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
               if(UtilValidate.isNotEmpty(mRow.get("listPriceThruDate"))) {
            	   rowString.append("thruDate" + "=\"" + mRow.get("listPriceThruDate") + "\" ");
               }
               rowString.append("/>");
               bwOutFile.write(rowString.toString());
               bwOutFile.newLine();
        	   
           }
           
           sPrice =(String)mRow.get("defaultPrice");
           if (UtilValidate.isNotEmpty(sPrice))
           {
        	   if(UtilValidate.isNotEmpty(mRow.get("defaultPriceCurrency"))) {
                   currencyUomId = (String) mRow.get("defaultPriceCurrency");
               }
               if(UtilValidate.isNotEmpty(mRow.get("defaultPriceFromDate"))) {
                   priceFromDate = (String) mRow.get("defaultPriceFromDate");
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
               if(UtilValidate.isNotEmpty(mRow.get("defaultPriceThruDate"))) {
            	   rowString.append("thruDate" + "=\"" + mRow.get("defaultPriceThruDate") + "\" ");
               }
               rowString.append("/>");
               bwOutFile.write(rowString.toString());
               bwOutFile.newLine();
        	   
           }
           
    	}
    	 catch (Exception e) {
    		 
    	 }
    }

    private static Map addProductFeatureImageRow(StringBuilder rowString,BufferedWriter bwOutFile,Map mFeatureTypeMap,Map mFeatureIdImageExists,String featureImage,String colName,String prodFeatureDataResourceTypeId,String loadImagesDirPath, String imageUrl) {
    	
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
            			String feature =(String)iterFeature.next();
            			String featureId =StringUtil.removeSpaces(feature).toUpperCase();
            			featureId =StringUtil.replaceString(featureId, "&", "");
            			featureId=featureTypeId+"_"+featureId;
            			if (featureId.length() > 20)
            			{
            				featureId=featureId.substring(0,20);
            			}
            			if (!mFeatureIdImageExists.containsKey(featureId))
            			{
                            mFeatureIdImageExists.put(featureId,featureImage);
            				String dataResourceId= _delegator.getNextSeqId("DataResource");
            	            rowString.setLength(0);
            	            rowString.append("<" + "DataResource" + " ");
            	            rowString.append("dataResourceId" + "=\"" + dataResourceId + "\" ");
            	            rowString.append("dataResourceTypeId" + "=\"" + "SHORT_TEXT" + "\" ");
            	            rowString.append("dataTemplateTypeId" + "=\"" + "FTL" + "\" ");
            	            rowString.append("statusId" + "=\"" + "CTNT_PUBLISHED" + "\" ");
            	            rowString.append("dataResourceName" + "=\"" + featureImage + "\" ");
            	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
            	            
            	            Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
                        	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
                        		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
                        	}
                        	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
                        	if(UtilValidate.isNotEmpty(defaultImageDirectory)) {
            	                featureImage = defaultImageDirectory + featureImage;
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
    	                    rowString.append("prodFeatureDataResourceTypeId" + "=\"" + prodFeatureDataResourceTypeId + "\" ");
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
        Map mMasterProductId=FastMap.newInstance();
		try {

	        fOutFile = new File(xmlDataDirPath, "050-ProductContent.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
                    StringBuilder  rowString = new StringBuilder();
	            	 Map mRow = (Map)dataRows.get(i);
	            	 masterProductId=(String)mRow.get("masterProductId");
	            	 if (!mMasterProductId.containsKey(masterProductId))
	            	 {
	            		 mMasterProductId.put(masterProductId, masterProductId);
	              		 addProductContent(rowString, mRow, bwOutFile, masterProductId,loadImagesDirPath, imageUrl);
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
	              	 String selectableFeature = (String)mRow.get("selectabeFeature_1");
	              	 if (UtilValidate.isNotEmpty(selectableFeature))
	              	 {
		            	 String masterProductId=(String)mRow.get("masterProductId");
		            	 String productId=(String)mRow.get("productId");
             			 if (productId.equals(masterProductId))
            			 {
             				productId=productId + "-0";
            			 }
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
			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","SMALL_IMAGE_URL", "smallImage", loadImagesDirPath, imageUrl);
             addProductContentRow(rowString, mRow, bwOutFile, productId,"image","SMALL_IMAGE_ALT_URL", "smallImageAlt", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PLP_SWATCH_IMAGE_URL", "plpSwatchImage", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PDP_SWATCH_IMAGE_URL", "pdpSwatchImage", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","THUMBNAIL_IMAGE_URL", "thumbImage", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","LARGE_IMAGE_URL", "largeImage", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","DETAIL_IMAGE_URL", "detailImage", loadImagesDirPath, imageUrl);

 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_1", "addImage1", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_1_LARGE", "xtraLargeImage1", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_1_DETAIL", "xtraDetailImage1", loadImagesDirPath, imageUrl);
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_2", "addImage2", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_2_LARGE", "xtraLargeImage2", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_2_DETAIL", "xtraDetailImage2", loadImagesDirPath, imageUrl);
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_3", "addImage3", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_3_LARGE", "xtraLargeImage3", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_3_DETAIL", "xtraDetailImage3", loadImagesDirPath, imageUrl);
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_4", "addImage4", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_4_LARGE", "xtraLargeImage4", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_4_DETAIL", "xtraDetailImage4", loadImagesDirPath, imageUrl);
 			 
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","ADDITIONAL_IMAGE_5", "addImage5", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_5_LARGE", "xtraLargeImage5", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","XTRA_IMG_5_DETAIL", "xtraDetailImage5", loadImagesDirPath, imageUrl);

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_6", "addImage6", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_6_LARGE", "xtraLargeImage6", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_6_DETAIL", "xtraDetailImage6", loadImagesDirPath, imageUrl);

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_7", "addImage7", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_7_LARGE", "xtraLargeImage7", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_7_DETAIL", "xtraDetailImage7", loadImagesDirPath, imageUrl);

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_8", "addImage8", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_8_LARGE", "xtraLargeImage8", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_8_DETAIL", "xtraDetailImage8", loadImagesDirPath, imageUrl);

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_9", "addImage9", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_9_LARGE", "xtraLargeImage9", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_9_DETAIL", "xtraDetailImage9", loadImagesDirPath, imageUrl);

 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","ADDITIONAL_IMAGE_10", "addImage10", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_10_LARGE", "xtraLargeImage10", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId, "image","XTRA_IMG_10_DETAIL", "xtraDetailImage10", loadImagesDirPath, imageUrl);

 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","PRODUCT_NAME", "productName", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","SHORT_SALES_PITCH", "salesPitch", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","LONG_DESCRIPTION", "longDescription", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","SPECIALINSTRUCTIONS", "specialInstructions", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","DELIVERY_INFO", "deliveryInfo", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","DIRECTIONS", "directions", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","TERMS_AND_CONDS", "termsConditions", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","INGREDIENTS", "ingredients", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","WARNINGS", "warnings", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","PLP_LABEL", "plpLabel", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"text","PDP_LABEL", "pdpLabel", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PDP_VIDEO_URL", "pdpVideoUrl", loadImagesDirPath, imageUrl);
 			 addProductContentRow(rowString, mRow, bwOutFile, productId,"image","PDP_VIDEO_360_URL", "pdpVideo360Url", loadImagesDirPath, imageUrl);
    		
    	}
    	 catch (Exception e)
    	 {
    		 
    	 }
    }
    private static void addProductContentRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String productId,String contentType,String productContentTypeId,String colName, String productImagesDirPath, String imageUrl) {

		String contentId=null;
		String dataResourceId=null;
    	try {
    		
			String contentValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(contentValue) && UtilValidate.isEmpty(contentValue.trim()))
			{
				return;
			}
	        List<GenericValue> lProductContent = _delegator.findByAnd("ProductContent", UtilMisc.toMap("productId",productId,"productContentTypeId",productContentTypeId),UtilMisc.toList("-fromDate"));
			if (UtilValidate.isNotEmpty(lProductContent))
			{
				GenericValue productContent = EntityUtil.getFirst(lProductContent);
				GenericValue content=productContent.getRelatedOne("Content");
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
	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
	            
	            Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
            	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
            		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
            	}
            	
            	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
            	if(UtilValidate.isNotEmpty(defaultImageDirectory)) {
	                contentValue = defaultImageDirectory + contentValue;
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
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
			
            rowString.setLength(0);
            rowString.append("<" + "ProductContent" + " ");
            rowString.append("productId" + "=\"" + productId + "\" ");
            rowString.append("contentId" + "=\"" + contentId + "\" ");
            rowString.append("productContentTypeId" + "=\"" + productContentTypeId + "\" ");
            rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
    		
    	}
     	 catch (Exception e) {
	         }

     	 return;
    	
    }
    
    private static void addCategoryContentRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String contentType,String categoryContentType,String colName) {

		String objectImagePath = UtilProperties.getPropertyValue("osafe", "product-category.images-path");
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
	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
	            
	            Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
            	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
            		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
            	}
            	
            	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
            	if(UtilValidate.isNotEmpty(defaultImageDirectory)) {
	                contentValue = defaultImageDirectory + contentValue;
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

    private static void addPartyContentRow(StringBuilder rowString,Map mRow,BufferedWriter bwOutFile,String contentType,String partyContentType,String colName,String imagesDirPath, String imageUrl) {

		String contentId=null;
		String partyId=null;
		String dataResourceId=null;
    	try {
    		
			String contentValue=(String)mRow.get(colName);
			if (UtilValidate.isEmpty(contentValue) && UtilValidate.isEmpty(contentValue.trim()))
			{
				return;
			}
			partyId=(String)mRow.get("partyId");
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
	            rowString.append("mimeTypeId" + "=\"" + "text/html" + "\" ");
	            
	            Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
            	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
            		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
            	}
            	
            	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
            	if(UtilValidate.isNotEmpty(defaultImageDirectory)) {
	                contentValue = defaultImageDirectory + contentValue;
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
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
			String sFromDate = (String)mRow.get("fromDate");
			if (UtilValidate.isEmpty(sFromDate))
			{
				sFromDate=_sdf.format(UtilDateTime.nowTimestamp());
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
    
    
    private static void buildProductCategoryFeatures(List dataRows,String xmlDataDirPath ) {

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
        Map mMasterProductId=FastMap.newInstance();
        String productCategoryId =null;
        String[] productCategoryIds =null;
        
		try {
			
	        fOutFile = new File(xmlDataDirPath, "010-ProductCategoryFeature.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
              	    Map mRow = (Map)dataRows.get(i);
              	  
              	    int totSelectableFeatures = 5;
            	    if(UtilValidate.isNotEmpty(mRow.get("totSelectableFeatures"))) {
            	    	totSelectableFeatures =  Integer.parseInt((String)mRow.get("totSelectableFeatures"));
				    }
        	    
        	        for(int j = 1; j <= totSelectableFeatures; j++)
        	        {
        	    	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("selectabeFeature_"+j));
        	        }
              	    int totDescriptiveFeatures = 5;
              	    if(UtilValidate.isNotEmpty(mRow.get("totDescriptiveFeatures"))) {
          	    	    totDescriptiveFeatures =  Integer.parseInt((String)mRow.get("totDescriptiveFeatures"));
				    }
          	    
          	        for(int j = 1; j <= totDescriptiveFeatures; j++)
          	        {
          	    	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("descriptiveFeature_"+j));
          	        }
              	    
	            	masterProductId=(String)mRow.get("masterProductId");
	            	if (!mMasterProductId.containsKey(masterProductId))
	            	 {
	            		 mMasterProductId.put(masterProductId, masterProductId);
	                     productCategoryId = (String)mRow.get("productCategoryId");
	                     productCategoryIds = productCategoryId.split(",");
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
	            			if (!mFeatureTypeExists.containsKey(featureType))
	            			{
	            				mFeatureTypeExists.put(featureType,featureType);
	                            rowString.setLength(0);
	                            rowString.append("<" + "ProductFeatureType" + " ");
	                            rowString.append("productFeatureTypeId" + "=\"" + featureTypeId + "\" ");
	                            rowString.append("parentTypeId" + "=\"" + "" + "\" ");
	                            rowString.append("hasTable" + "=\"" + "N" + "\" ");
	                            rowString.append("description" + "=\"" + featureType + "\" ");
	                            rowString.append("/>");
	                            bwOutFile.write(rowString.toString());
	                            bwOutFile.newLine();
	                            
	                            rowString.setLength(0);
	                            rowString.append("<" + "ProductFeatureCategory" + " ");
	                            rowString.append("productFeatureCategoryId" + "=\"" + featureTypeId + "\" ");
	                            rowString.append("parentCategoryId" + "=\"" + "" + "\" ");
	                            rowString.append("description" + "=\"" + featureType + "\" ");
	                            rowString.append("/>");
	                            bwOutFile.write(rowString.toString());
	                            bwOutFile.newLine();

	                            
	                            rowString.setLength(0);
	                            rowString.append("<" + "ProductFeatureGroup" + " ");
	                            rowString.append("productFeatureGroupId" + "=\"" + featureTypeId + "\" ");
	                            rowString.append("description" + "=\"" + featureType + "\" ");
	                            rowString.append("/>");
	                            bwOutFile.write(rowString.toString());
	                            bwOutFile.newLine();

	            				
	            			}
	                         for (int j=0;j < productCategoryIds.length;j++)
	                        {
	 	                        String sProductCategoryId= productCategoryIds[j].trim();
		            			if (UtilValidate.isNotEmpty(sProductCategoryId) && !mFeatureCategoryGroupApplExists.containsKey(sProductCategoryId+"_"+featureTypeId))
		            			{
		            				mFeatureCategoryGroupApplExists.put(sProductCategoryId+"_"+featureTypeId,sProductCategoryId+"_"+featureTypeId);
		            				
		            				List<GenericValue> productFeatureCatGrpApplList = _delegator.findByAnd("ProductFeatureCatGrpAppl", UtilMisc.toMap("productCategoryId", sProductCategoryId, "productFeatureGroupId", featureTypeId));
		            				if(UtilValidate.isNotEmpty(productFeatureCatGrpApplList)) {
		            					productFeatureCatGrpApplList = EntityUtil.filterByDate(productFeatureCatGrpApplList, true);
		            				}
		            				if(UtilValidate.isEmpty(productFeatureCatGrpApplList)) {
		                                rowString.setLength(0);
		                                rowString.append("<" + "ProductFeatureCatGrpAppl" + " ");
		                                rowString.append("productCategoryId" + "=\"" + sProductCategoryId + "\" ");
		                                rowString.append("productFeatureGroupId" + "=\"" + featureTypeId + "\" ");
		    	                        rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
		                                rowString.append("/>");
		                                bwOutFile.write(rowString.toString());
		                                bwOutFile.newLine();
		            				}
		            				
		            				List<GenericValue> productFeatureCategoryApplList = _delegator.findByAnd("ProductFeatureCategoryAppl", UtilMisc.toMap("productCategoryId", sProductCategoryId, "productFeatureCategoryId", featureTypeId));
		            				if(UtilValidate.isNotEmpty(productFeatureCategoryApplList)) {
		            					productFeatureCategoryApplList = EntityUtil.filterByDate(productFeatureCategoryApplList, true);
		            				}
		            				if(UtilValidate.isEmpty(productFeatureCategoryApplList)) {
		                                rowString.setLength(0);
		                                rowString.append("<" + "ProductFeatureCategoryAppl" + " ");
		                                rowString.append("productCategoryId" + "=\"" + sProductCategoryId + "\" ");
		                                rowString.append("productFeatureCategoryId" + "=\"" + featureTypeId + "\" ");
		    	                        rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
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
	                			String feature =(String)iterFeature.next();
	                			String featureId =StringUtil.removeSpaces(feature).toUpperCase();
	                			featureId =StringUtil.replaceString(featureId, "&", "");
	                			featureId=featureTypeId+"_"+featureId;
	                			if (featureId.length() > 20)
	                			{
	                				featureId=featureId.substring(0,20);
	                			}
		            			if (!mFeatureExists.containsKey(featureId))
		            			{
		            				mFeatureExists.put(featureId,featureId);
	 	                            rowString.setLength(0);
		                            rowString.append("<" + "ProductFeature" + " ");
		                            rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
		                            rowString.append("productFeatureTypeId" + "=\"" + featureTypeId + "\" ");
		                            rowString.append("productFeatureCategoryId" + "=\"" + featureTypeId + "\" ");
		                            rowString.append("description" + "=\"" + feature + "\" ");
		                            rowString.append("/>");
		                           bwOutFile.write(rowString.toString());
		                           bwOutFile.newLine();
		            			}

		            			if (!mFeatureGroupApplExists.containsKey(featureId))
		            			{
		            				mFeatureGroupApplExists.put(featureId,featureId);
		            				List<GenericValue> productFeatureGroupApplList = _delegator.findByAnd("ProductFeatureGroupAppl", UtilMisc.toMap("productFeatureGroupId", featureTypeId, "productFeatureId", featureId));
		            				if(UtilValidate.isNotEmpty(productFeatureGroupApplList)) {
		            					productFeatureGroupApplList = EntityUtil.filterByDate(productFeatureGroupApplList, true);
		            				}
		            				if(UtilValidate.isEmpty(productFeatureGroupApplList)) {
		                                rowString.setLength(0);
		                                rowString.append("<" + "ProductFeatureGroupAppl" + " ");
		                                rowString.append("productFeatureGroupId" + "=\"" + featureTypeId + "\" ");
		                                rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
		    	                        rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
		    	                        rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
		                                rowString.append("/>");
		                                bwOutFile.write(rowString.toString());
		                                bwOutFile.newLine();
		            				}
		            			}
		            			iSeq++;
	                			
	                		}
	            			
	            			
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

    private static void buildProductDistinguishingFeatures(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        Map mMasterProductId=FastMap.newInstance();
        
		try {
			
	        fOutFile = new File(xmlDataDirPath, "060-ProductDistinguishingFeature.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
              	    Map mRow = (Map)dataRows.get(i);
	            	 masterProductId=(String)mRow.get("masterProductId");
	            	 if (!mMasterProductId.containsKey(masterProductId))
	            	 {
	             		mFeatureTypeMap.clear();
	             		int totDescriptiveFeatures = 5;
	            	    if(UtilValidate.isNotEmpty(mRow.get("totDescriptiveFeatures"))) {
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
		            			
		            			String featureFromDate = _sdf.format(UtilDateTime.nowTimestamp());
	            			    if(UtilValidate.isNotEmpty((String) mRow.get(featureTypeId.trim()+"_fromDate"))) {
	            			    	featureFromDate = (String) mRow.get(featureTypeId.trim()+"_fromDate");
	            			    }
	            			    String featureThruDate = null;
	            			    if(UtilValidate.isNotEmpty((String) mRow.get(featureTypeId.trim()+"_thruDate"))) {
	            			    	featureThruDate = (String) mRow.get(featureTypeId.trim()+"_thruDate");
	            			    }
		            			
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
		                			String feature =(String)iterFeature.next();
		                			String featureId =StringUtil.removeSpaces(feature).toUpperCase();
		                			featureId =StringUtil.replaceString(featureId, "&", "");
		                			featureId=featureTypeId+"_"+featureId;
		                			if (featureId.length() > 20)
		                			{
		                				featureId=featureId.substring(0,20);
		                			}
		                			rowString.setLength(0);
		       	                    rowString.append("<" + "ProductFeatureAppl" + " ");
		    	                    rowString.append("productId" + "=\"" + masterProductId+ "\" ");
		    	                    rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
		    	                    rowString.append("productFeatureApplTypeId" + "=\"" + "DISTINGUISHING_FEAT" + "\" ");
		    	                    rowString.append("fromDate" + "=\"" + featureFromDate + "\" ");
	                	            if(UtilValidate.isNotEmpty(featureThruDate)) {
	                	            	rowString.append("thruDate" + "=\"" + featureThruDate + "\" ");
	                	            }
		    	                    rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
		                            rowString.append("/>");
		                            bwOutFile.write(rowString.toString());
		                            bwOutFile.newLine();
		                            iSeq++;
		                		}
		            			
		            			
		            		}
	            		 
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
    
    private static void buildProductSelectableFeatures(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        Map mFeatureTypeMap = FastMap.newInstance();
        StringBuilder  rowString = new StringBuilder();
        String masterProductId=null;
        String productId=null;
        Map mMasterProductId=FastMap.newInstance();
        
		try {
			
	        fOutFile = new File(xmlDataDirPath, "043-ProductSelectableFeature.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
              	    Map mRow = (Map)dataRows.get(i);
              	    masterProductId=(String)mRow.get("masterProductId");
  			        productId=(String)mRow.get("productId");
  			        
  			        if (masterProductId.equals(productId))
      			    {
  			        	productId=productId + "-0";
      			    }
              	    mFeatureTypeMap.clear();
              	    
              	    int totSelectableFeatures = 5;
              	    if(UtilValidate.isNotEmpty(mRow.get("totSelectableFeatures"))) {
              	    	totSelectableFeatures =  Integer.parseInt((String)mRow.get("totSelectableFeatures"));
				    }
              	    
              	    for(int j = 1; j <= totSelectableFeatures; j++)
          	        {
          	    	    buildFeatureMap(mFeatureTypeMap, (String)mRow.get("selectabeFeature_"+j));
          	        }
              	    
                    int iSeq=0;
                    	
              	        if(mFeatureTypeMap.size() > 0) {
              	    	    Set featureTypeSet = mFeatureTypeMap.keySet();
	            		    Iterator iterFeatureType = featureTypeSet.iterator(); 
	            		    while (iterFeatureType.hasNext())
	            		    {
	            			    String featureType =(String)iterFeatureType.next();
	            			    String featureTypeId = StringUtil.removeSpaces(featureType).toUpperCase();
	            			    
	            			    String featureFromDate = _sdf.format(UtilDateTime.nowTimestamp());
	            			    if(UtilValidate.isNotEmpty((String) mRow.get(featureTypeId.trim()+"_fromDate"))) {
	            			    	featureFromDate = (String) mRow.get(featureTypeId.trim()+"_fromDate");
	            			    }
	            			    String featureThruDate = null;
	            			    if(UtilValidate.isNotEmpty((String) mRow.get(featureTypeId.trim()+"_thruDate"))) {
	            			    	featureThruDate = (String) mRow.get(featureTypeId.trim()+"_thruDate");
	            			    }
	            			    
                			    if (featureTypeId.length() > 20)
                			    {
                				    featureTypeId=featureTypeId.substring(0,20);
                			    }
                			    FastMap mFeatureMap=(FastMap)mFeatureTypeMap.get(featureType);
	                		    Set featureSet = mFeatureMap.keySet();
	                		    Iterator iterFeature = featureSet.iterator();
	                		    
	                		    while (iterFeature.hasNext())
	                		    {
	                			    String feature =(String)iterFeature.next();
	                			    String featureId =StringUtil.removeSpaces(feature).toUpperCase();
	                			    featureId=featureTypeId+"_"+featureId;
	                			    if (featureId.length() > 20)
	                			    {
	                				    featureId=featureId.substring(0,20);
	                			    }
	       	            	        
	                			    rowString.setLength(0);
	                	            rowString.append("<" + "ProductFeatureAppl" + " ");
	                	            rowString.append("productId" + "=\"" + productId + "\" ");
	                	            rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
	                	            rowString.append("productFeatureApplTypeId" + "=\"" + "STANDARD_FEATURE" + "\" ");
	                	            rowString.append("fromDate" + "=\"" + featureFromDate + "\" ");
	                	            if(UtilValidate.isNotEmpty(featureThruDate)) {
	                	            	rowString.append("thruDate" + "=\"" + featureThruDate + "\" ");
	                	            }
	                	            rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
	                	            rowString.append("/>");
	                	            bwOutFile.write(rowString.toString());
	                	            bwOutFile.newLine();

	                	           
	                	            rowString.setLength(0);
	                	            rowString.append("<" + "ProductFeatureAppl" + " ");
	                	            rowString.append("productId" + "=\"" + masterProductId + "\" ");
	                	            rowString.append("productFeatureId" + "=\"" + featureId + "\" ");
	                	            rowString.append("productFeatureApplTypeId" + "=\"" + "SELECTABLE_FEATURE" + "\" ");
	                	            rowString.append("fromDate" + "=\"" + featureFromDate + "\" ");
	                	            if(UtilValidate.isNotEmpty(featureThruDate)) {
	                	            	rowString.append("thruDate" + "=\"" + featureThruDate + "\" ");
	                	            }
	                	            rowString.append("sequenceNum" + "=\"" + ((iSeq +1) *10) + "\" ");
	                	            rowString.append("/>");
	                	            bwOutFile.write(rowString.toString());
	                	            bwOutFile.newLine();
	                			
	                		    }
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
    
    private static void buildProductFeatureImage(List dataRows,String xmlDataDirPath,String loadImagesDirPath, String imageUrl ) {

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
                 if (bwOutFile != null) {
                	 bwOutFile.close();
                 }
             } catch (IOException ioe) {
                 Debug.logError(ioe, module);
             }
         }
      	 
       }
    
    private static void buildProductAssoc(List dataRows,String xmlDataDirPath) {
        File fOutFile =null;
        BufferedWriter bwOutFile=null;
		try {
			
	        fOutFile = new File(xmlDataDirPath, "070-ProductAssoc.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
                    StringBuilder  rowString = new StringBuilder();
	            	 Map mRow = (Map)dataRows.get(i);
	                    rowString.append("<" + "ProductAssoc" + " ");
	                    rowString.append("productId" + "=\"" + mRow.get("productIdTo")+ "\" ");
	                    rowString.append("productIdTo" + "=\"" + mRow.get("productId") + "\" ");
	                    rowString.append("productAssocTypeId" + "=\"" + "PRODUCT_COMPLEMENT" + "\" ");
	                    if (UtilValidate.isEmpty(mRow.get("fromDate")))
	                    {
	                   	 rowString.append("fromDate" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
	                    }
	                    else
	                    {
	                        rowString.append("fromDate" + "=\"" + mRow.get("fromDate") + "\" ");
	                   	 
	                    }
	                    if (UtilValidate.isNotEmpty(mRow.get("thruDate")))
	                    {
	                        rowString.append("thruDate" + "=\"" + mRow.get("thruDate") + "\" ");
	                    }
	                    rowString.append("sequenceNum" + "=\"" + ((i +1) *10) + "\" ");
                        rowString.append("/>");
                        bwOutFile.write(rowString.toString());
                        bwOutFile.newLine();
                        

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
    
    private static Map buildFeatureMap(Map featureTypeMap,String parseFeatureType) {
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
                	mFeatureMap.put(""+featureTokens[f].trim(),""+featureTokens[f].trim());
                	
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
    	    	value = StringUtil.replaceString(value, "\"", "'");
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
        
    
    public static Map<String, Object> importRemoveEntityData(DispatchContext ctx, Map<String, ?> context) {

        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();

        SQLProcessor sqlP = null;
        String[] removeEntities = Constants.IMPORT_REMOVE_ENTITIES;
        // #############################################################
        // Removing entity names which are store in Constants java file.
        // #############################################################

        if (removeEntities != null) {
            for (String entity: removeEntities) {
                try {
                    GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(delegator.getEntityGroupName(entity));
                    sqlP = new SQLProcessor(helperInfo);
                    DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperInfo.getHelperBaseName());

                    int deleteRowCount =0; 
                    String tableName = delegator.getModelEntity(entity).getTableName(datasourceInfo);
                    String sql = null;
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
                    } else if (entity.equalsIgnoreCase("Product")) 
                    {   
                    	
                        String nowDateTime = _sdf.format(UtilDateTime.nowTimestamp());
                        sql = "UPDATE " + tableName;
                        sql += " SET PRIMARY_PRODUCT_CATEGORY_ID = NULL";
                        sqlP.prepareStatement(sql);
                        sqlP.executeUpdate();

                        sql = "DELETE FROM " + tableName;
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                    	 
                    }else
                      {
                        sql = "DELETE FROM " + tableName;
                        sqlP.prepareStatement(sql);
                        deleteRowCount = sqlP.executeUpdate();
                       }

                } catch (GenericEntityException e) {
                    Debug.logInfo("An error occurred executing query"+e, module);
                } catch (Exception e) {
                    Debug.logInfo("An error occurred executing query"+e, module);
                } finally {
                    try {
                        sqlP.close();
                    } catch (GenericDataSourceException e) {
                        Debug.logInfo("An error occurred in closing SQLProcessor"+e, module);
                    } catch (Exception e) {
                    	Debug.logInfo("An error occurred in closing SQLProcessor"+e, module);
                    } 
                }
            }
        }
        else {
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
    	
    	if(UtilValidate.isEmpty(imageType)){
    		return "";
    	}
    	String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
    	
    	Map<Object, Object> imageLocationMap = OsafeManageXml.findByKeyFromXmlFile(XmlFilePath, "key", imageType);
    	if(UtilValidate.isNotEmpty(imageLocationMap.get("value"))){
    	    return (String)imageLocationMap.get("value");
    	} else {
    		return "";
        }
    }
    

    public static Map<String, Object> exportOrderXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        List<String> orderIdList = (List)context.get("orderList");
        String productStoreId = (String)context.get("productStoreId");
        
        ObjectFactory factory = new ObjectFactory();
        BigFishOrderFeedType bfOrderFeedType = factory.createBigFishOrderFeedType();
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("order");
        String orderFileName = "Order";
        if(orderIdList.size() == 1) {
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
        
        List orderList = bfOrderFeedType.getOrder();
  	  
        OrderType order = null;
  	    for(String orderId : orderIdList) {
  	    	try {
  	    	order = factory.createOrderType();
  	    	
  	    	OrderReadHelper orderReadHelper = null;
  	    	GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
  	    	if(UtilValidate.isNotEmpty(orderHeader)) {
  	    	    orderReadHelper = new OrderReadHelper(orderHeader);
  	    	}
  	    	String orderType = (String)orderHeader.get("orderTypeId");
  	        // get the display party
  	        GenericValue displayParty = null;
  	        String displayPartyId = null;
  	        if ("PURCHASE_ORDER".equals(orderType)) {
  	            displayParty = orderReadHelper.getSupplierAgent();
  	        } else {
  	            displayParty = orderReadHelper.getPlacingParty();
  	        }
  	        if(UtilValidate.isNotEmpty(displayParty)) {
  	        	displayPartyId = (String)displayParty.get("partyId");
  	        }
  	          
  	        order.setProductStore(productStoreId);
  	        // Set Customer Detail 
  	        CustomerType customer = factory.createCustomerType();
            //Set Customer Billing Address
  	      
  	        List<GenericValue> orderContactMechListBilling = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "BILLING_LOCATION"));
	        List<String> contactMechIdsBilling = EntityUtil.getFieldListFromEntityList(orderContactMechListBilling, "contactMechId", true);
	        
	        List contactDetailPurposeExprBiling = FastList.newInstance();
	        contactDetailPurposeExprBiling.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIdsBilling));
	        contactDetailPurposeExprBiling.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, displayPartyId));
	      
	        List<GenericValue> partyContactDetailsBilling = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(contactDetailPurposeExprBiling, EntityOperator.AND), null, null, null, false);
	        
	        FeedsUtil.getCustomerBillingAddress(factory, customer, partyContactDetailsBilling,"BILLING_LOCATION", delegator);
            
	        //Set Customer Shipping Address
	    	
            List<GenericValue> orderContactMechListShipping = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "SHIPPING_LOCATION"));
  	        List<String> contactMechIdsShipping = EntityUtil.getFieldListFromEntityList(orderContactMechListShipping, "contactMechId", true);
  	        
  	        List contactDetailPurposeExprShipping = FastList.newInstance();
  	        contactDetailPurposeExprShipping.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIdsShipping));
  	        contactDetailPurposeExprShipping.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, displayPartyId));
  	      
  	        List<GenericValue> partyContactDetailsShipping = delegator.findList("PartyContactDetailByPurpose", EntityCondition.makeCondition(contactDetailPurposeExprShipping, EntityOperator.AND), null, null, null, false);
            
            FeedsUtil.getCustomerShippingAddress(factory, customer, partyContactDetailsShipping,"SHIPPING_LOCATION", delegator);
            
            //Set Customer EmailAddress
            
            List<GenericValue> partyEmailDetails = (List<GenericValue>) ContactHelper.getContactMech(displayParty, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
            GenericValue partyEmailDetail = EntityUtil.getFirst(partyEmailDetails);
            
            GenericValue person = null;
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", displayPartyId));
            
	        
	        customer.setCustomerId(displayPartyId);
	        customer.setFirstName((String)person.get("firstName"));
	        customer.setLastName((String)person.get("lastName"));
	        customer.setEmailAddress((String)partyEmailDetail.get("infoString"));
	        String homePhone = FeedsUtil.getPartyPhoneNumber(displayPartyId, "PHONE_HOME", delegator);
	        customer.setHomePhone(homePhone);
	        String cellPhone = FeedsUtil.getPartyPhoneNumber(displayPartyId, "PHONE_MOBILE", delegator);
	        customer.setCellPhone(cellPhone);
	        String workPhone = FeedsUtil.getPartyPhoneNumber(displayPartyId, "PHONE_WORK", delegator);
	        customer.setWorkPhone(workPhone);
	        String workPhoneExt = FeedsUtil.getPartyPhoneExt(displayPartyId, "PHONE_WORK", delegator);
	        customer.setWorkPhoneExt(workPhoneExt);
	        
	        order.setCustomer(customer);
	        
	        // Set Order Header Detail 
	        List<GenericValue> orderAdjustments = orderReadHelper.getAdjustments();
	        List<GenericValue> orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
	        List<GenericValue> headerAdjustmentsToShow = orderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false);
	        
	        BigDecimal orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
	        BigDecimal shippingTotal = orderReadHelper.getAllOrderItemsAdjustmentsTotal(orderReadHelper.getValidOrderItems(), orderAdjustments, false, false, true);
	        shippingTotal = shippingTotal.add(orderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));
	        
	        BigDecimal grandTotal = orderReadHelper.getOrderGrandTotal();
	        
	        BigDecimal adjustmentTotal = BigDecimal.ZERO;
	        for (GenericValue orderHeaderAdjustment : headerAdjustmentsToShow) {
	        	adjustmentTotal = adjustmentTotal.add(orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment));
	        }
	        
	        Object taxAmount = orderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).get("taxGrandTotal");
  	    	OrderHeaderType oh = factory.createOrderHeaderType();
  	    	oh.setOrderId(orderId);
  	    	oh.setOrderDate(orderHeader.get("orderDate").toString());
  	    	oh.setOrderTotalItem(orderSubTotal.toString());
  	    	oh.setCurrency(orderReadHelper.getCurrency());
  	    	oh.setOrderShippingChargeGross(shippingTotal.toString());
  	    	oh.setOrderTotalTax(taxAmount.toString());
  	    	oh.setOrderTotalNet(grandTotal.toString());
  	    	oh.setOrderTotalAdjustment(adjustmentTotal.toString());
  	    	oh.setItemsToRemove("0");
  	    	oh.setValidateWebTotals("TRUE");
  	    	order.setOrderHeader(oh);
  	    	
  	    	// Set Order Line Item detail 
  	    	List<GenericValue> orderItems = orderReadHelper.getOrderItems();
  	    	OrderLineItemsType orderLineItems = factory.createOrderLineItemsType();
  	    	List orderLineItemsList = orderLineItems.getOrderLine();
  	    	
  	    	Integer lineNumber = 1;
  	    	for(GenericValue orderItem : orderItems) {
  	    		BigDecimal orderItemSubTotal = orderReadHelper.getOrderItemSubTotal(orderItem,orderReadHelper.getAdjustments());
  	    		BigDecimal orderItemShippingCharges = orderReadHelper.getOrderItemShipping(orderItem);
  	    		BigDecimal orderItemTax = orderReadHelper.getOrderItemTax(orderItem);
  	    		String shippingMethod = "";
  	    		List<GenericValue> orderItemShipGroupAssocs = orderReadHelper.getOrderItemShipGroupAssocs(orderItem);
  	    		if(UtilValidate.isNotEmpty(orderItemShipGroupAssocs)) {
  	    			for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocs) {
  	    				if(((String)orderItem.get("orderItemSeqId")).equals((String)orderItemShipGroupAssoc.get("orderItemSeqId"))){
  	    					shippingMethod = orderReadHelper.getShippingMethod((String)orderItemShipGroupAssoc.get("shipGroupSeqId"));
  	    				}
  	    			}
  	    		}
  	    		OrderLineType orderLine = factory.createOrderLineType();
  	    		orderLine.setLineNumber(lineNumber.toString());
  	    		orderLine.setOrderLineId((String)orderItem.get("orderItemSeqId"));
  	    		orderLine.setProductId((String)orderItem.get("productId"));
  	    		orderLine.setQuantity(UtilMisc.toInteger(orderItem.get("quantity")));
  	    		orderLine.setPrice(orderItem.get("unitPrice").toString());
  	    		orderLine.setLineTotalGross(orderItemSubTotal.toString());
  	    		orderLine.setShippingCharge(orderItemShippingCharges.toString());
  	    		orderLine.setSalesTax(orderItemTax.toString());
  	    		orderLine.setCarrier(shippingMethod);
  	    		orderLineItemsList.add(orderLine);
  	    		lineNumber++;
  	    	}
  	    	
  	    	order.setOrderLineItems(orderLineItems);
  	    	
  	    	// Set Cart Promotion Detail 
  	    	List cartPromotionList =order.getCartPromotion();
  	    	for (GenericValue orderHeaderAdjustment : headerAdjustmentsToShow) {
  	    		CartPromotionType cartPromotion = factory.createCartPromotionType();
  	    		GenericValue adjustmentType = orderHeaderAdjustment.getRelatedOneCache("OrderAdjustmentType");
  	    		GenericValue productPromo = orderHeaderAdjustment.getRelatedOneCache("ProductPromo");
  	    		String promoCodeText = "";
  	    		if(UtilValidate.isNotEmpty(productPromo)) {
  	    			List<GenericValue> productPromoCode = productPromo.getRelatedCache("ProductPromoCode");
  	    			Set<String> promoCodesEntered = orderReadHelper.getProductPromoCodesEntered();
  	    			if(UtilValidate.isNotEmpty(promoCodesEntered)) {
  	    				for(String promoCodeEntered : promoCodesEntered) {
  	    					if(UtilValidate.isNotEmpty(productPromoCode)) {
  	    						for(GenericValue promoCode : productPromoCode) {
  	    							String promoCodeEnteredId = promoCodeEntered;
  	    							String promoCodeId = (String) promoCode.get("productPromoCodeId");
  	    							if(UtilValidate.isNotEmpty(promoCodeEnteredId)) {
  	    								if(promoCodeId.equals(promoCodeEnteredId)) {
  	    									promoCodeText = (String)promoCode.get("productPromoCodeId");
  	    								}
  	    							}
  	    						}
  	    					}
  	    				}
  	    			}
  	    		}
  	    		cartPromotion.setPromotionCode(promoCodeText);
  	    		BigDecimal promotionAmount = orderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment);
  	    		cartPromotion.setPromotionAmount(promotionAmount.toString());
  	    		cartPromotionList.add(cartPromotion);
	        }
  	    	
  	    	// Set Order Payment Detail 
  	    	OrderPaymentType orderPayment = factory.createOrderPaymentType();
  	    	
  	    	List<GenericValue> orderPayments = orderReadHelper.getPaymentPreferences();
  	    	GenericValue paymentMethod = null;
  	    	GenericValue creditCard = null;
  	    	String paymentToken = "";
  	    	String payerId = "";
  	    	String transactionId = "";
  	    	List<GenericValue> gatewayResponses = null;
  	    	
  	    	if(UtilValidate.isNotEmpty(orderPayments)) {
  	    		for(GenericValue orderPaymentPreference : orderPayments) {
  	    			paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod");
  	    			gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse");
  	    			if((UtilValidate.isNotEmpty(orderPaymentPreference.getString("paymentMethodId")))){
  	    				if((paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD"))) {
  	  	    				creditCard = orderPaymentPreference.getRelatedOne("PaymentMethod").getRelatedOne("CreditCard");
  	  	    				orderPayment.setCardType((String)creditCard.get("cardType"));
  	  	    			} 
  	    				
  	  	    			if(paymentMethod.getString("paymentMethodTypeId").equals("SAGEPAY_TOKEN")) {
  	  	    				GenericValue sagePayTokenGV = delegator.findOne("SagePayTokenPaymentMethod", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
  	  	    				if(UtilValidate.isNotEmpty(sagePayTokenGV)) {
  	  	    				    paymentToken = (String)sagePayTokenGV.get("sagePayToken");
  	  	    				    creditCard = delegator.findOne("CreditCard", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
  	  	    				    orderPayment.setCardType((String)creditCard.get("cardType"));
  	  	    					orderPayment.setPaymentToken(paymentToken);
  	  	    				}
  	  	    			}
  	  	    			
  	  	    		    if(paymentMethod.getString("paymentMethodTypeId").equals("EXT_PAYPAL")) {
	  	    				GenericValue payPalMethod = delegator.findOne("PayPalPaymentMethod", UtilMisc.toMap("paymentMethodId", (String)paymentMethod.get("paymentMethodId")), false);
	  	    				if(UtilValidate.isNotEmpty(payPalMethod)) {
	  	    					payerId = (String)payPalMethod.get("payerId");
	  	    					transactionId = (String)payPalMethod.get("transactionId");
	  	    					paymentToken = (String)payPalMethod.get("expressCheckoutToken");
	  	    					orderPayment.setPayerId(payerId);
	  	    					orderPayment.setTransactionId(transactionId);
	  	    					orderPayment.setPaymentToken(paymentToken);
	  	    				}
	  	    			}
  	    			}
  	    			
  	    		}
  	    	}
  	    	String cardNumber = "";
  	    	String cardExpireDate = "";
  	    	String last4Digits = "";
  	    	if(UtilValidate.isNotEmpty(paymentMethod) && paymentMethod.get("paymentMethodTypeId").equals("CREDIT_CARD")) {
  	    		cardNumber = (String) creditCard.get("cardNumber");
  	    		last4Digits = cardNumber.substring(cardNumber.length() - 4);
  	    		cardExpireDate = creditCard.get("expireDate").toString();
  	    		orderPayment.setCardNumber(cardNumber);
  	  	    	orderPayment.setLast4Digits(last4Digits);
  	  	    	orderPayment.setExpiryDate(cardExpireDate);
  	    	}
  	    	
  	    	if(UtilValidate.isNotEmpty(paymentMethod) && paymentMethod.get("paymentMethodTypeId").equals("SAGEPAY_TOKEN")) {
  	    		  	    		
  	    	}
  	    	orderPayment.setPaymentMethod((String)paymentMethod.get("paymentMethodTypeId"));
  	    	
  	    	String authDate = "";
  	    	String authRefNo = "";
  	    	String authAmount = "";
  	    	String captureDate = "";
  	    	String captureRefNo = "";
  	    	String captureAmount = "";
  	    	
  	    	if(UtilValidate.isNotEmpty(gatewayResponses)) {
  	    		for(GenericValue gatewayResponse : gatewayResponses) {
  	    			GenericValue transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration");
  	    			String enumCode = (String) transactionCode.get("enumCode");
  	    			if(enumCode.equals("AUTHORIZE")) {
  	    				authDate = gatewayResponse.get("transactionDate").toString();
  	    				authRefNo = (String) gatewayResponse.get("referenceNum");
  	    				authAmount = (String) gatewayResponse.get("amount").toString();
  	    			}
                    if(enumCode.equals("CAPTURE")) {
                    	captureDate = gatewayResponse.get("transactionDate").toString();
                    	captureRefNo = (String) gatewayResponse.get("referenceNum");
                    	captureAmount = (String) gatewayResponse.get("amount").toString();
  	    			}
  	    		}
  	    	}
  	    	orderPayment.setAuthReferenceNumber(authRefNo);
  	    	orderPayment.setAuthDateTime(authDate);
  	    	orderPayment.setAuthAmount(authAmount);
  	    	orderPayment.setCaptureDateTime(captureDate);
  	    	orderPayment.setCaptureReferenceNumber(captureRefNo);
  	    	orderPayment.setCaptureAmount(captureAmount);
  	    	order.setOrderPayment(orderPayment);
  	    	
  	    	orderList.add(order);
  	    	} catch (Exception e) {
  	    		e.printStackTrace();
  	    	}
  	    }
  	  
        FeedsUtil.marshalObject(new JAXBElement<BigFishOrderFeedType>(new QName("", "BigFishOrderFeed"), BigFishOrderFeedType.class, null, bfOrderFeedType), file);
  	    result.put("feedsDirectoryPath", downloadTempDir);
        result.put("feedsFileName", orderFileName);
        return result;
    }
    
    
    public static Map<String, Object> exportCustRequestContactUsXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        List<String> custRequestIdList = (List)context.get("custRequestIdList");
        String productStoreId = (String)context.get("productStoreId");
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("custrequest");
        
        Map result = ServiceUtil.returnSuccess();
        
        String custRequestFileName = "ContactUs";
        if(custRequestIdList.size() == 1) {
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
  	  
        ContactUsType contactUs = null;
        
  	    for(String custRequestId : custRequestIdList) {
  	    	contactUs = factory.createContactUsType();
  	    	
  	    	try {
  	    		contactUs.setContactUsId(custRequestId);
  	    		String firstName = "";
  	    		String lastName = "";
  	    		String emailAddress = "";
  	    		String orderId = "";
  	    		String contactPhone = "";
  	    		String comment = "";
  	    		
  	    		List<GenericValue> custReqAttributeList = delegator.findByAnd("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId));
  	    		for(GenericValue custReqAttribute : custReqAttributeList) {
  	    			if(custReqAttribute.get("attrName").equals("FIRST_NAME")) {
  	    				firstName = (String) custReqAttribute.get("attrValue"); 
  	    			}
                    if(custReqAttribute.get("attrName").equals("LAST_NAME")) {
                    	lastName = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ORDER_NUMBER")) {
                    	orderId = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("EMAIL_ADDRESS")) {
                    	emailAddress = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("CONTACT_PHONE")) {
                    	contactPhone = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("COMMENT")) {
                    	comment = (String) custReqAttribute.get("attrValue");
  	    			}
  	    		}
  	    		if(contactPhone.length()> 6) {
  	    			contactPhone = contactPhone.substring(0,3)+"-"+contactPhone.substring(3,6)+"-"+contactPhone.substring(6);
  	    		}
  	    		contactUs.setFirstName(firstName);
  	    		contactUs.setLastName(lastName);
  	    		contactUs.setOrderId(orderId);
  	    		contactUs.setContactPhone(contactPhone);
  	    		contactUs.setComment(StringUtil.wrapString(comment).toString());
  	    		contactUs.setEmailAddress(emailAddress);
  	    		contactUs.setProductStore(productStoreId);
  	    		
  	    		contactUsList.add(contactUs);
  	    	}
  	    	catch (Exception e) {
  	    		e.printStackTrace();
  	    		messages.add("Error in Customer Contact Us Export.");
  	    	}
  	    }
  	  
      FeedsUtil.marshalObject(new JAXBElement<BigFishContactUsFeedType>(new QName("", "BigFishContactUsFeed"), BigFishContactUsFeedType.class, null, bfContactUsFeedType), file);
      result.put("feedsDirectoryPath", downloadTempDir);
      result.put("feedsFileName", custRequestFileName);
      return result;
    }
    
    public static Map<String, Object> exportCustRequestCatalogXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        List<String> custRequestIdList = (List)context.get("custRequestIdList");
        String productStoreId = (String)context.get("productStoreId");
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("custrequest");
        
        Map result = ServiceUtil.returnSuccess();
        
        String custRequestFileName = "RequestCatalog";
        if(custRequestIdList.size() == 1) {
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
  	  
        RequestCatalogType customerRequest = null;
        
  	    for(String custRequestId : custRequestIdList) {
  	    	customerRequest = factory.createRequestCatalogType();
  	    	
  	    	try {
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
  	    		
  	    		List<GenericValue> custReqAttributeList = delegator.findByAnd("CustRequestAttribute",UtilMisc.toMap("custRequestId", custRequestId));
  	    		for(GenericValue custReqAttribute : custReqAttributeList) {
  	    			if(custReqAttribute.get("attrName").equals("FIRST_NAME")) {
  	    				firstName = (String) custReqAttribute.get("attrValue"); 
  	    			}
                    if(custReqAttribute.get("attrName").equals("LAST_NAME")) {
                    	lastName = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("COUNTRY")) {
                    	country = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ADDRESS1")) {
                    	address1 = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ADDRESS2")) {
                    	address2 = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ADDRESS3")) {
                    	address3 = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("CITY")) {
                    	city = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("STATE_PROVINCE")) {
                    	state = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("ZIP_POSTAL_CODE")) {
                    	zip = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("EMAIL_ADDRESS")) {
                    	emailAddress = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("CONTACT_PHONE")) {
                    	contactPhone = (String) custReqAttribute.get("attrValue");
  	    			}
                    if(custReqAttribute.get("attrName").equals("COMMENT")) {
                    	comment = (String) custReqAttribute.get("attrValue");
  	    			}
  	    		}
  	    		if(contactPhone.length()> 6) {
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
  	    		customerRequest.setProductStore(productStoreId);
  	    		
  	    		requestCatalogList.add(customerRequest);
  	    	}
  	    	catch (Exception e) {
  	    		e.printStackTrace();
  	    		messages.add("Error in Customer Export.");
  	    	}
  	    }
  	  
        FeedsUtil.marshalObject(new JAXBElement<BigFishRequestCatalogFeedType>(new QName("", "BigFishRequestCatalogFeed"), BigFishRequestCatalogFeedType.class, null, bfRequestCatalogFeedType), file);
      
        result.put("feedsDirectoryPath", downloadTempDir);
        result.put("feedsFileName", custRequestFileName);
        return result;
    }
    
    
    
    public static Map<String, Object> exportCustomerXML(DispatchContext ctx, Map<String, ?> context) {

        LocalDispatcher dispatcher = ctx.getDispatcher();
        List<String> messages = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        String productStoreId = (String)context.get("productStoreId");
        ObjectFactory factory = new ObjectFactory();
        
        BigFishCustomerFeedType bfCustomerFeedType = factory.createBigFishCustomerFeedType();
        
        List<String> customerIdList = (List)context.get("customerList");
        
        String downloadTempDir = FeedsUtil.getFeedDirectory("customer");
        
        Map result = ServiceUtil.returnSuccess();
        
        String customerFileName = "Customer";
        if(customerIdList.size() == 1) {
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
  	  
        CustomerType customer = null;
  	    for(String customerId : customerIdList) {
  	    	GenericValue party = null;
  	    	GenericValue person = null;
  	    	
  	    	try {
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
                
                //Set Customer Billing Address
                FeedsUtil.getCustomerBillingAddress(factory, customer, partyContactDetails,"BILLING_LOCATION", delegator);
                
    	        //Set Customer Shipping Address
                FeedsUtil.getCustomerShippingAddress(factory, customer, partyContactDetails,"SHIPPING_LOCATION", delegator);
    	        
  	        
    	        String gender = (String)person.get("gender");
    	        if(UtilValidate.isEmpty(gender)) {
    	        	gender = "";
    	        }
    	        if(gender.equalsIgnoreCase("M")) {
    	        	gender = "MALE";
    	        } else if (gender.equalsIgnoreCase("F")) {
    	        	gender = "FEMALE";
    	        }
    	        
    	        String allowSolicitation = (String)partyEmailDetail.get("allowSolicitation");
    	        if(UtilValidate.isEmpty(allowSolicitation)) {
    	        	allowSolicitation = "";
    	        }
    	        if(allowSolicitation.equalsIgnoreCase("Y")) {
    	        	allowSolicitation = "TRUE";
    	        } else if (allowSolicitation.equalsIgnoreCase("N")) {
    	        	allowSolicitation = "FALSE";
    	        }
    	        
    	        //Set Customer Personal Information
    	        customer.setProductStore(productStoreId);
    	        customer.setCustomerId(partyId);
    	        customer.setFirstName((String)person.get("firstName"));
    	        customer.setLastName((String)person.get("lastName"));
    	        customer.setGender(gender);
    	        customer.setDateRegistered(party.get("createdDate").toString());
    	        customer.setEmailAddress((String)partyEmailDetail.get("infoString"));
    	        customer.setEmailOptIn(allowSolicitation);
    	        customer.setEmailFormat((String)partyEmailFormatAttr.get("attrValue"));String homePhone = FeedsUtil.getPartyPhoneNumber(partyId, "PHONE_HOME", delegator);
    	        customer.setHomePhone(homePhone);
    	        String cellPhone = FeedsUtil.getPartyPhoneNumber(partyId, "PHONE_MOBILE", delegator);
    	        customer.setCellPhone(cellPhone);
    	        String workPhone = FeedsUtil.getPartyPhoneNumber(partyId, "PHONE_WORK", delegator);
    	        customer.setWorkPhone(workPhone);
    	        String workPhoneExt = FeedsUtil.getPartyPhoneExt(partyId, "PHONE_WORK", delegator);
    	        customer.setWorkPhoneExt(workPhoneExt);
    	        customerList.add(customer);
  	    	}
  	    	catch (Exception e) {
  	    		e.printStackTrace();
  	    		messages.add("Error in Customer Export.");
  	    	}
  	    }
        
        FeedsUtil.marshalObject(new JAXBElement<BigFishCustomerFeedType>(new QName("", "BigFishCustomerFeed"), BigFishCustomerFeedType.class, null, bfCustomerFeedType), file);  
        result.put("feedsDirectoryPath", downloadTempDir);
        result.put("feedsFileName", customerFileName);
        return result;
    }
    
    
    public static Map<String, Object> importProductXML(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String xmlDataFilePath = (String)context.get("xmlDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        //String xmlDataDirPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("import", "import.dir"), context);
        String loadImagesDirPath=(String)context.get("productLoadImagesDir");
        String imageUrl = (String)context.get("imageUrl");
        Boolean removeAll = (Boolean) context.get("removeAll");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        if (removeAll == null) removeAll = Boolean.FALSE;
        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        File baseDataDir = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
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
                            inputWorkbook = new File(baseDataDir,  UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath));
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
            	JAXBElement<BigFishProductFeedType> bfProductFeedType = (JAXBElement<BigFishProductFeedType>)unmarshaller.unmarshal(inputWorkbook);
            	
            	ProductsType productsType = bfProductFeedType.getValue().getProducts();
            	List<ProductType> products = productsType.getProduct();
            	
            	ProductCategoryType productCategoryType = bfProductFeedType.getValue().getProductCategory();
            	List<CategoryType> productCategories = productCategoryType.getCategory();
            	
            	ProductAssociationType productAssociationType = bfProductFeedType.getValue().getProductAssociation();
            	List<ProductAssocType> productAssociations = productAssociationType.getProductAssoc();
            	
            	ProductFeatureSwatchType productFeatureSwatchType = bfProductFeedType.getValue().getProductFeatureSwatch();
            	List<FeatureSwatchType> productFeatureSwatches = productFeatureSwatchType.getFeature();
            	
            	ProductManufacturerType productManufacturerType = bfProductFeedType.getValue().getProductManufacturer();
            	List<ManufacturerType> productManufacturers = productManufacturerType.getManufacturer();
            	
            	if(productCategories.size() > 0) {
            		List dataRows = buildProductCategoryXMLDataRows(productCategories);
            		buildProductCategory(dataRows, xmlDataDirPath,loadImagesDirPath, imageUrl);
            	}
            	if(products.size() > 0) {
            	    List dataRows = buildProductXMLDataRows(products);
            	    buildProduct(dataRows, xmlDataDirPath);
                	buildProductVariant(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                	buildProductSelectableFeatures(dataRows, xmlDataDirPath);
                	buildProductGoodIdentification(dataRows, xmlDataDirPath);
                	buildProductCategoryFeatures(dataRows, xmlDataDirPath);
                    buildProductDistinguishingFeatures(dataRows, xmlDataDirPath);
                    buildProductContent(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                    buildProductVariantContent(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
                    buildProductAttribute(dataRows, xmlDataDirPath);
            	}
            	if(productAssociations.size() > 0) {
            		List dataRows = buildProductAssociationXMLDataRows(productAssociations);
            		buildProductAssoc(dataRows, xmlDataDirPath);
            	}
            	if(productFeatureSwatches.size() > 0) {
            		List dataRows = buildProductFeatureSwatchXMLDataRows(productFeatureSwatches);
            		buildProductFeatureImage(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
            	}
            	if(productManufacturers.size() > 0) {
            		List dataRows = buildProductManufacturerXMLDataRows(productManufacturers);
            		buildManufacturer(dataRows, xmlDataDirPath,loadImagesDirPath,imageUrl);
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
        
     // ############################################
        // call the service for remove entity data 
        // if removeAll and autoLoad parameter are true 
        // ############################################
        if (removeAll) {
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
        }

        // ##############################################
        // move the generated xml files in done directory
        // ##############################################
        File doneXmlDir = new File(baseDataDir, Constants.DONE_XML_DIRECTORY_PREFIX+UtilDateTime.nowDateString());
        File[] fileArray = baseDataDir.listFiles();
        for (File file: fileArray) {
            try {
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("XML")) {
                    FileUtils.copyFileToDirectory(file, doneXmlDir);
                    file.delete();
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
      
    public static List buildProductCategoryXMLDataRows(List<CategoryType> productCategories) {
		List dataRows = FastList.newInstance();

		try {
			
            for (int rowCount = 0 ; rowCount < productCategories.size() ; rowCount++) {
            	CategoryType productCategory = (CategoryType) productCategories.get(rowCount);
            
            	Map mRows = FastMap.newInstance();
                
                mRows.put("productCategoryId",productCategory.getKey());
                mRows.put("parentCategoryId",productCategory.getParentCategoryId());
                mRows.put("categoryName",productCategory.getCategoryName());
                mRows.put("description",productCategory.getDescription());
                mRows.put("longDescription",productCategory.getLongDescription());
                mRows.put("plpText",productCategory.getAdditionalPLPText());
                mRows.put("pdpText",productCategory.getAdditionalPDPText());
                mRows.put("fromDate",productCategory.getFromDate());
                mRows.put("thruDate",productCategory.getThruDate());
                
                PLPImageType plpImage = productCategory.getPLPImage();
                mRows.put("plpImageName",plpImage.getUrl());
                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    public static List buildProductAssociationXMLDataRows(List<ProductAssocType> productAssociations) {
		List dataRows = FastList.newInstance();

		try {
			
            for (int rowCount = 0 ; rowCount < productAssociations.size() ; rowCount++) {
            	ProductAssocType productAssociation = (ProductAssocType)productAssociations.get(rowCount);
            	Map mRows = FastMap.newInstance();
                
                mRows.put("productId",productAssociation.getProductId());
                mRows.put("productIdTo",productAssociation.getProductIdTo());
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
			
            for (int rowCount = 0 ; rowCount < productFeatureSwatches.size() ; rowCount++) {
            	FeatureSwatchType productFeatureSwatch = (FeatureSwatchType)productFeatureSwatches.get(rowCount);
            	Map mRows = FastMap.newInstance();
                String featureKey = productFeatureSwatch.getKey();
                String featureValue = productFeatureSwatch.getValue();
                String featureId = featureKey + ":" + featureValue;
                mRows.put("featureId",featureId);
                
                PLPSwatchType plpSwatch = productFeatureSwatch.getPLPSwatch();
                mRows.put("plpSwatchImage",plpSwatch.getUrl());
                
                PDPSwatchType pdpSwatch = productFeatureSwatch.getPDPSwatch();
                mRows.put("pdpSwatchImage",pdpSwatch.getUrl());
                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
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
                
                mRows.put("partyId",productManufacturer.getKey());
                mRows.put("manufacturerName",productManufacturer.getManufacturerName());
                mRows.put("shortDescription",productManufacturer.getDescription());
                mRows.put("longDescription",productManufacturer.getLongDescription());
                
                ManufacturerAddressType manufacturerAddress = productManufacturer.getAddress();
                mRows.put("address1",manufacturerAddress.getAddress1());
                mRows.put("city",manufacturerAddress.getCityTown());
                mRows.put("state",manufacturerAddress.getStateProvince());
                mRows.put("zip",manufacturerAddress.getZipPostCode());
                
                ManufacturerImageType manufacturerImage = productManufacturer.getManufacturerImage();
                mRows.put("manufacturerImage",manufacturerImage.getUrl());
                                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    public static List buildProductXMLDataRows(List<ProductType> products) {
		List dataRows = FastList.newInstance();

		try {
						
            for (int rowCount = 0 ; rowCount < products.size() ; rowCount++) {
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
                mRows.put("plpLabel",product.getPLPLabel());
                mRows.put("pdpLabel",product.getPDPLabel());
                mRows.put("productHeight",product.getProductHeight());
                mRows.put("productWidth",product.getProductWidth());
                mRows.put("productDepth",product.getProductDepth());
                mRows.put("returnable",product.getReturnable());
                mRows.put("taxable",product.getTaxable());
                mRows.put("chargeShipping",product.getChargeShipping());
                mRows.put("fromDate",product.getFromDate());
                mRows.put("thruDate",product.getThruDate());
                mRows.put("manufacturerId",product.getManufacturerId());
                
                ProductPriceType productPrice = product.getProductPrice();
                ListPriceType listPrice = productPrice.getListPrice();
                mRows.put("listPrice",listPrice.getPrice());
                mRows.put("listPriceCurrency",listPrice.getCurrency());
                mRows.put("listPriceFromDate",listPrice.getFromDate());
                mRows.put("listPriceThruDate",listPrice.getThruDate());
                
                SalesPriceType salesPrice = productPrice.getSalesPrice();
                mRows.put("defaultPrice",salesPrice.getPrice());
                mRows.put("defaultPriceCurrency",salesPrice.getCurrency());
                mRows.put("defaultPriceFromDate",salesPrice.getFromDate());
                mRows.put("defaultPriceThruDate",salesPrice.getThruDate());
                
                ProductCategoryMemberType productCategory = product.getProductCategoryMember();
                List<CategoryMemberType> categoryList = productCategory.getCategory();
                
                StringBuffer categoryId = new StringBuffer("");
                if(UtilValidate.isNotEmpty(categoryList)) {
                	
                	for(int i = 0; i < categoryList.size(); i++) {
                		CategoryMemberType category = (CategoryMemberType)categoryList.get(i);
                		if(!category.getKey().equals("")) {
                		    categoryId.append(category.getKey() + ",");
                		    mRows.put(category.getKey() + "_sequenceNum",category.getSequenceNum());
                		    mRows.put(category.getKey() + "_fromDate",category.getFromDate());
                		    mRows.put(category.getKey() + "_thruDate",category.getThruDate());
                		}
                	}
                	if(categoryId.length() > 1) {
                	    categoryId.setLength(categoryId.length()-1);
                	}
                }
                mRows.put("productCategoryId",categoryId.toString());
                
                ProductSelectableFeatureType selectableFeature = product.getProductSelectableFeature();
                List<FeatureType> selectableFeatureList = selectableFeature.getFeature();
                if(UtilValidate.isNotEmpty(selectableFeatureList)) {
                	for(int i = 0; i < selectableFeatureList.size(); i++) {
                		String featureId = new String("");
                		FeatureType feature = (FeatureType)selectableFeatureList.get(i);
                		if(UtilValidate.isNotEmpty(feature.getKey())) {
                		    StringBuffer featureValue = new StringBuffer("");
                		    List featureValues = feature.getValue();
                		    if(UtilValidate.isNotEmpty(featureValues)) {
                        	
                        	    for(int value = 0; value < featureValues.size(); value++) {
                        		    if(!featureValues.get(value).equals("")) {
                        		        featureValue.append(featureValues.get(value) + ",");
                        		    }
                        	    }
                        	    if(featureValue.length() > 1) {
                        	        featureValue.setLength(featureValue.length()-1);
                        	    }
                            }
                		    if(featureValue.length() > 0) {
                		        featureId = feature.getKey() + ":" + featureValue.toString();
                		        mRows.put(feature.getKey() + "_fromDate",feature.getFromDate());
                    		    mRows.put(feature.getKey() + "_thruDate",feature.getThruDate());
                		    }
                		}
                		mRows.put("selectabeFeature_"+(i+1),featureId);
                	}
                	mRows.put("totSelectableFeatures",new Integer(selectableFeatureList.size()).toString());
                }
                
                ProductDescriptiveFeatureType descriptiveFeature = product.getProductDescriptiveFeature();
                List<FeatureType> descriptiveFeatureList = descriptiveFeature.getFeature();
                if(UtilValidate.isNotEmpty(descriptiveFeatureList)) {
                	for(int i = 0; i < descriptiveFeatureList.size(); i++) {
                		String featureId = new String("");
                		FeatureType feature = (FeatureType)descriptiveFeatureList.get(i);
                		if(UtilValidate.isNotEmpty(feature.getKey())) {
                		    StringBuffer featureValue = new StringBuffer("");
                		    List featureValues = feature.getValue();
                		    if(UtilValidate.isNotEmpty(featureValues)) {
                        	
                        	    for(int value = 0; value < featureValues.size(); value++) {
                        		    if(!featureValues.get(value).equals("")) {
                        		        featureValue.append(featureValues.get(value) + ",");
                        		    }
                        	    }
                        	    if(featureValue.length() > 1) {
                        	        featureValue.setLength(featureValue.length()-1);
                        	    }
                            }
                		    if(featureValue.length() > 0) {
                		        featureId = feature.getKey() + ":" + featureValue.toString();
                		        mRows.put(feature.getKey() + "_fromDate",feature.getFromDate());
                    		    mRows.put(feature.getKey() + "_thruDate",feature.getThruDate());
                		    }
                		}
                		mRows.put("descriptiveFeature_"+(i+1),featureId);
                	}
                	mRows.put("totDescriptiveFeatures",new Integer(descriptiveFeatureList.size()).toString());
                }
                
                ProductImageType productImage = product.getProductImage();
                PLPSwatchType plpSwatch = productImage.getPLPSwatch();
                mRows.put("plpSwatchImage",plpSwatch.getUrl());
                
                PDPSwatchType pdpSwatch = productImage.getPDPSwatch();
                mRows.put("pdpSwatchImage",pdpSwatch.getUrl());
                
                PLPSmallImageType plpSmallImage = productImage.getPLPSmallImage();
                mRows.put("smallImage",plpSmallImage.getUrl());
                
                PLPSmallAltImageType plpSmallAltImage = productImage.getPLPSmallAltImage();
                mRows.put("smallImageAlt",plpSmallAltImage.getUrl());
                
                PDPThumbnailImageType pdpThumbnailImage = productImage.getPDPThumbnailImage();
                mRows.put("thumbImage",pdpThumbnailImage.getUrl());
                
                PDPLargeImageType plpLargeImage = productImage.getPDPLargeImage();
                mRows.put("largeImage",plpLargeImage.getUrl());
                
                PDPDetailImageType pdpDetailImage = productImage.getPDPDetailImage();
                mRows.put("detailImage",pdpDetailImage.getUrl());
                
                PDPVideoType pdpVideo = productImage.getPDPVideo();
                mRows.put("pdpVideoUrl",pdpVideo.getUrl());
                
                PDPVideo360Type pdpVideo360 = productImage.getPDPVideo360();
                mRows.put("pdpVideo360Url",pdpVideo360.getUrl());
                
                PDPAlternateImageType pdpAlternateImage = productImage.getPDPAlternateImage();
                List pdpAdditionalImages = pdpAlternateImage.getPDPAdditionalImage();
                if(UtilValidate.isNotEmpty(pdpAdditionalImages)) {
                	for(int i = 0; i < pdpAdditionalImages.size(); i++) {
                	    PDPAdditionalImageType pdpAdditionalImage = (PDPAdditionalImageType) pdpAdditionalImages.get(i);
                	    
                	    PDPAdditionalThumbImageType pdpAdditionalThumbImage = pdpAdditionalImage.getPDPAdditionalThumbImage();
                	    mRows.put("addImage"+(i+1),pdpAdditionalThumbImage.getUrl());
                	    
                	    PDPAdditionalLargeImageType pdpAdditionalLargeImage = pdpAdditionalImage.getPDPAdditionalLargeImage();
                	    mRows.put("xtraLargeImage"+(i+1),pdpAdditionalLargeImage.getUrl());
                	    
                	    PDPAdditionalDetailImageType pdpAdditionalDetailImage = pdpAdditionalImage.getPDPAdditionalDetailImage();
                	    mRows.put("xtraDetailImage"+(i+1),pdpAdditionalDetailImage.getUrl());
                	}
                }
                
                GoodIdentificationType goodIdentification = product.getGoodIdentification();
                mRows.put("goodIdentificationSkuId",goodIdentification.getSKU());
                mRows.put("goodIdentificationGoogleId",goodIdentification.getGoogleId());
                mRows.put("goodIdentificationIsbnId",goodIdentification.getISBN());
                mRows.put("goodIdentificationManufacturerId",goodIdentification.getManuId());
                
                ProductInventoryType productInventory = product.getProductInventory();
                mRows.put("bfInventoryTot",productInventory.getBFInventoryTotal());
                mRows.put("bfInventoryWhs",productInventory.getBFInventoryWarehouse());
                
                mRows = formatProductXLSData(mRows);
                dataRows.add(mRows);
             }
    	}
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    
    public static Map<String, Object> importOrderStatusXML(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String xmlDataFilePath = (String)context.get("xmlDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        File baseDataDir = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
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
                            inputWorkbook = new File(baseDataDir,  UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath));
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
            	JAXBElement<BigFishOrderStatusUpdateFeedType> bfOrderStatusUpdateFeedType = (JAXBElement<BigFishOrderStatusUpdateFeedType>)unmarshaller.unmarshal(inputWorkbook);
            	
            	List dataRows = buildOrderStatusXMLDataRows(bfOrderStatusUpdateFeedType);
            	buildOrderHeader(dataRows, xmlDataDirPath);
            	buildOrderItem(dataRows, xmlDataDirPath);
            	buildOrderStatus(dataRows, xmlDataDirPath);
            	buildOrderItemShipGroup(dataRows, xmlDataDirPath);
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
                    FileUtils.copyFileToDirectory(file, doneXmlDir);
                    file.delete();
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
    
    public static List buildOrderStatusXMLDataRows(JAXBElement<BigFishOrderStatusUpdateFeedType> bfOrderStatusUpdateFeedType) {
		List dataRows = FastList.newInstance();

		try {
			List orders = bfOrderStatusUpdateFeedType.getValue().getOrder();
			
            for (int rowCount = 0 ; rowCount < orders.size() ; rowCount++) {
            	OrderStatusType order = (OrderStatusType) orders.get(rowCount);
            
            	Map mRows = FastMap.newInstance();
                
                mRows.put("orderId",order.getOrderId());
                mRows.put("productStore",order.getProductStore());
                mRows.put("orderStatus",order.getOrderStatus());
                mRows.put("orderShipDate",order.getOrderShipDate());
                mRows.put("orderShipCarrier",order.getOrderShipCarrier());
                mRows.put("orderShipMethod",order.getOrderShipMethod());
                mRows.put("orderTrackingNumber",order.getOrderTrackingNumber());
                
                List orderItems = order.getOrderItem();
                if(UtilValidate.isNotEmpty(orderItems)) {
                	for(int i = 0; i < orderItems.size(); i++){
                		OrderItemType orderItem = (OrderItemType) orderItems.get(i);
                		mRows.put("orderItemId_" + (i + 1),orderItem.getOrderItemId());
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
      	catch (Exception e) {
      		e.printStackTrace();
   	    }
      	return dataRows;
   }
    
    
    private static void buildOrderHeader(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        
		try {
	        fOutFile = new File(xmlDataDirPath, "000-OrderHeader.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
            	String priceFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 
	                 rowString.setLength(0);
	                 rowString.append("<" + "OrderHeader" + " ");
		             rowString.append("orderId" + "=\"" + (String)mRow.get("orderId") + "\" ");
		             if(Integer.parseInt((String)mRow.get("totalOrderItems")) > 0) {
		            	 String orderStatus = getOrderStatus((String)mRow.get("orderId"), mRow);
		            	 rowString.append("statusId" + "=\"" + orderStatus + "\" ");
		             } else {
		                 if(mRow.get("orderStatus") != null) {
		                     rowString.append("statusId" + "=\"" + (String)mRow.get("orderStatus") + "\" ");
		                 }
		             }
	                 rowString.append("/>");
	                 bwOutFile.write(rowString.toString());
	                 bwOutFile.newLine();
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
    
    private static void buildOrderItem(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        
		try {
	        fOutFile = new File(xmlDataDirPath, "010-OrderItem.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
            	String priceFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 
		             if(Integer.parseInt((String)mRow.get("totalOrderItems")) > 0) {
		            	 for(int orderItemNo = 0; orderItemNo < Integer.parseInt((String)mRow.get("totalOrderItems")); orderItemNo++) {
		            		 rowString.setLength(0);
		            		 rowString.append("<" + "OrderItem" + " ");
				             rowString.append("orderId" + "=\"" + (String)mRow.get("orderId") + "\" ");
				             rowString.append("orderItemSeqId" + "=\"" + (String)mRow.get("orderItemId_" + (orderItemNo + 1)) + "\" ");
				             rowString.append("statusId" + "=\"" + (String)mRow.get("orderItemStatus_" + (orderItemNo + 1)) + "\" ");
				             rowString.append("/>");
				             bwOutFile.write(rowString.toString());
			                 bwOutFile.newLine();
		            	 }
		             } else {
		            	 List<GenericValue> orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", (String)mRow.get("orderId")));
		            	 if(UtilValidate.isNotEmpty(orderItems)) {
		            		 for(GenericValue orderItem : orderItems) {
		            			 rowString.setLength(0);
		            			 rowString.append("<" + "OrderItem" + " ");
					             rowString.append("orderId" + "=\"" + (String)mRow.get("orderId") + "\" ");
					             rowString.append("orderItemSeqId" + "=\"" + (String)orderItem.get("orderItemSeqId") + "\" ");
					             rowString.append("statusId" + "=\"" + (String)mRow.get("orderStatus") + "\" ");
					             rowString.append("/>");
					             bwOutFile.write(rowString.toString());
				                 bwOutFile.newLine();
		            		 }
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
    
    private static void buildOrderStatus(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        
		try {
	        fOutFile = new File(xmlDataDirPath, "020-OrderStatus.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
            	String priceFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 String orderStatusId = _delegator.getNextSeqId("OrderStatus");
	            	 
		             if(Integer.parseInt((String)mRow.get("totalOrderItems")) > 0) {
		            	 
		            	 String orderStatus = getOrderStatus((String)mRow.get("orderId"), mRow);
		            	 rowString.setLength(0);
	            		 rowString.append("<" + "OrderStatus" + " ");
	            		 rowString.append("orderStatusId" + "=\"" + orderStatusId + "\" ");
	            		 rowString.append("statusId" + "=\"" + orderStatus + "\" ");
			             rowString.append("orderId" + "=\"" + (String)mRow.get("orderId") + "\" ");
			             rowString.append("statusDateTime" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
			             rowString.append("/>");
			             bwOutFile.write(rowString.toString());
		                 bwOutFile.newLine();
		                 
		            	 for(int orderItemNo = 0; orderItemNo < Integer.parseInt((String)mRow.get("totalOrderItems")); orderItemNo++) {
		            		 orderStatusId = _delegator.getNextSeqId("OrderStatus");
		            		 rowString.setLength(0);
		            		 rowString.append("<" + "OrderStatus" + " ");
		            		 rowString.append("orderStatusId" + "=\"" + orderStatusId + "\" ");
		            		 rowString.append("statusId" + "=\"" + (String)mRow.get("orderItemStatus_" + (orderItemNo + 1)) + "\" ");
				             rowString.append("orderId" + "=\"" + (String)mRow.get("orderId") + "\" ");
				             rowString.append("orderItemSeqId" + "=\"" + (String)mRow.get("orderItemId_" + (orderItemNo + 1)) + "\" ");
				             rowString.append("statusDateTime" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
				             rowString.append("/>");
				             bwOutFile.write(rowString.toString());
			                 bwOutFile.newLine();
		            	 }
		             } else {
		            	 rowString.setLength(0);
	            		 rowString.append("<" + "OrderStatus" + " ");
	            		 rowString.append("orderStatusId" + "=\"" + orderStatusId + "\" ");
	            		 rowString.append("statusId" + "=\"" + (String)mRow.get("orderStatus") + "\" ");
			             rowString.append("orderId" + "=\"" + (String)mRow.get("orderId") + "\" ");
			             rowString.append("statusDateTime" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
			             rowString.append("/>");
			             bwOutFile.write(rowString.toString());
		                 bwOutFile.newLine();
		                 
		            	 List<GenericValue> orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", (String)mRow.get("orderId")));
		            	 if(UtilValidate.isNotEmpty(orderItems)) {
		            		 for(GenericValue orderItem : orderItems) {
		            			 orderStatusId = _delegator.getNextSeqId("OrderStatus");
			            		 rowString.setLength(0);
			            		 rowString.append("<" + "OrderStatus" + " ");
			            		 rowString.append("orderStatusId" + "=\"" + orderStatusId + "\" ");
			            		 rowString.append("statusId" + "=\"" + (String)mRow.get("orderStatus") + "\" ");
					             rowString.append("orderId" + "=\"" + (String)mRow.get("orderId") + "\" ");
					             rowString.append("orderItemSeqId" + "=\"" + (String)orderItem.get("orderItemSeqId") + "\" ");
					             rowString.append("statusDateTime" + "=\"" + _sdf.format(UtilDateTime.nowTimestamp()) + "\" ");
					             rowString.append("/>");
					             bwOutFile.write(rowString.toString());
				                 bwOutFile.newLine();
		            		 }
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
    
    private static void buildOrderItemShipGroup(List dataRows,String xmlDataDirPath ) {

        File fOutFile =null;
        BufferedWriter bwOutFile=null;
        StringBuilder  rowString = new StringBuilder();
        
		try {
	        fOutFile = new File(xmlDataDirPath, "040-OrderItemShipGroup.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));
            	String priceFromDate = _sdf.format(UtilDateTime.nowTimestamp());
                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) 
                {
	            	 Map mRow = (Map)dataRows.get(i);
	            	 
		             if(Integer.parseInt((String)mRow.get("totalOrderItems")) > 0) {
		            	 for(int orderItemNo = 0; orderItemNo < Integer.parseInt((String)mRow.get("totalOrderItems")); orderItemNo++) {
		            		 List<GenericValue> orderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId"), "orderItemSeqId", (String)mRow.get("orderItemId_" + (orderItemNo + 1))), UtilMisc.toList("+orderItemSeqId"));
		            		 boolean sameShipment = false;
		            		 if(UtilValidate.isNotEmpty(orderItemShipGroupAssocList)) {
		            			 // check if another order items have the same shipment sequence id-->
		            			 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) {
		            				 List<GenericValue> orderItemShipGroupAssocCount = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId"), "shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId")), UtilMisc.toList("+orderItemSeqId"));
		            				 if(orderItemShipGroupAssocCount.size() > 1) {
		            					 sameShipment = true;
		            				 }
		            			 }
		            			 if(sameShipment) {
		            				 Long maxShipGroupSeqId = Long.valueOf("1");
		            				 List<GenericValue> allOrderItemShipGroupAssocList =  _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId")), UtilMisc.toList("+shipGroupSeqId"));
		            				 for(GenericValue orderItemShipGroupAssoc : allOrderItemShipGroupAssocList) {
		            					 Long curShipGroupSeqId = Long.parseLong(orderItemShipGroupAssoc.getString("shipGroupSeqId"));
		            					 if(curShipGroupSeqId > maxShipGroupSeqId) {
		            						 maxShipGroupSeqId = curShipGroupSeqId;
		            					 }
		            				 }
		            				 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) {
		            					 maxShipGroupSeqId = maxShipGroupSeqId + 1;
		            					 GenericValue orderItemShipGroup = _delegator.findByPrimaryKey("OrderItemShipGroup", UtilMisc.toMap("orderId", orderItemShipGroupAssoc.getString("orderId"), "shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId")));
		            					 GenericValue newOrderItemShipGroup = orderItemShipGroup.create(orderItemShipGroup);
		            					 String shipGroupSeqId = UtilFormatOut.formatPaddedNumber(maxShipGroupSeqId.longValue(), 5);
		            					 rowString.setLength(0);
		    		            		 rowString.append("<" + "OrderItemShipGroup" + " ");
		    				             rowString.append("orderId" + "=\"" + orderItemShipGroup.getString("orderId") + "\" ");
		    				             rowString.append("shipGroupSeqId" + "=\"" + shipGroupSeqId + "\" ");
		    				             rowString.append("shipmentMethodTypeId" + "=\"" + (String)mRow.get("orderItemShipMethod_" + (orderItemNo + 1)) + "\" ");
		    				             rowString.append("supplierPartyId" + "=\"" + orderItemShipGroup.getString("supplierPartyId") + "\" ");
		    				             rowString.append("vendorPartyId" + "=\"" + orderItemShipGroup.getString("vendorPartyId") + "\" ");
		    				             rowString.append("carrierPartyId" + "=\"" + (String)mRow.get("orderItemCarrier_" + (orderItemNo + 1)) + "\" ");
		    				             rowString.append("carrierRoleTypeId" + "=\"" + orderItemShipGroup.getString("carrierRoleTypeId") + "\" ");
		    				             rowString.append("facilityId" + "=\"" + orderItemShipGroup.getString("facilityId") + "\" ");
		    				             rowString.append("contactMechId" + "=\"" + orderItemShipGroup.getString("contactMechId") + "\" ");
		    				             rowString.append("telecomContactMechId" + "=\"" + orderItemShipGroup.getString("telecomContactMechId") + "\" ");
		    				             rowString.append("trackingNumber" + "=\"" + (String)mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1)) + "\" ");
		    				             rowString.append("shippingInstructions" + "=\"" + orderItemShipGroup.getString("shippingInstructions") + "\" ");
		    				             rowString.append("maySplit" + "=\"" + orderItemShipGroup.getString("maySplit") + "\" ");
		    				             rowString.append("giftMessage" + "=\"" + orderItemShipGroup.getString("giftMessage") + "\" ");
		    				             rowString.append("isGift" + "=\"" + orderItemShipGroup.getString("isGift") + "\" ");
		    				             rowString.append("shipAfterDate" + "=\"" + orderItemShipGroup.getString("shipAfterDate") + "\" ");
		    				             rowString.append("shipByDate" + "=\"" + orderItemShipGroup.getString("shipByDate") + "\" ");
		    				             rowString.append("estimatedShipDate" + "=\"" + (String)mRow.get("orderItemShipDate_" + (orderItemNo + 1)) + "\" ");
		    				             rowString.append("estimatedDeliveryDate" + "=\"" + orderItemShipGroup.getString("estimatedDeliveryDate") + "\" ");
		    				             rowString.append("/>");
		    				             bwOutFile.write(rowString.toString());
		    			                 bwOutFile.newLine();
		    			                 
		    			                 
		    			                 rowString.setLength(0);
		    		            		 rowString.append("<" + "OrderItemShipGroupAssoc" + " ");
		    				             rowString.append("orderId" + "=\"" + orderItemShipGroupAssoc.getString("orderId") + "\" ");
		    				             rowString.append("orderItemSeqId" + "=\"" + orderItemShipGroupAssoc.getString("orderItemSeqId") + "\" ");
		    				             rowString.append("shipGroupSeqId" + "=\"" + shipGroupSeqId + "\" ");
		    				             rowString.append("quantity" + "=\"" + orderItemShipGroupAssoc.getString("quantity") + "\" ");
		    				             rowString.append("cancelQuantity" + "=\"" + orderItemShipGroupAssoc.getString("cancelQuantity") + "\" ");
		    				             rowString.append("/>");
		    				             bwOutFile.write(rowString.toString());
		    			                 bwOutFile.newLine();
		    			                 
		    			                 // _delegator.removeValue(orderItemShipGroup);
		            				 }
		            			 } else {
		    		                 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) {
		    		            	     GenericValue orderItemShipGroup = _delegator.findByPrimaryKey("OrderItemShipGroup", UtilMisc.toMap("orderId", orderItemShipGroupAssoc.getString("orderId"), "shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId")));
		    		            	     if(UtilValidate.isNotEmpty(orderItemShipGroup)) {
		    		            	         if(!mRow.get("orderItemShipMethod_" + (orderItemNo + 1)).equals(orderItemShipGroup.getString("shipmentMethodTypeId")) || !mRow.get("orderItemCarrier_" + (orderItemNo + 1)).equals(orderItemShipGroup.getString("carrierPartyId")) || !mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1)).equals(orderItemShipGroup.getString("trackingNumber"))) {
		    		            			     rowString.setLength(0);
		    		    		                 rowString.append("<" + "OrderItemShipGroup" + " ");
		    		    				         rowString.append("orderId" + "=\"" + orderItemShipGroupAssoc.getString("orderId") + "\" ");
		    		    				         rowString.append("shipGroupSeqId" + "=\"" + orderItemShipGroupAssoc.getString("shipGroupSeqId") + "\" ");
		    		    				         rowString.append("shipmentMethodTypeId" + "=\"" + mRow.get("orderItemShipMethod_" + (orderItemNo + 1)) + "\" ");
		    		    				         rowString.append("carrierPartyId" + "=\"" + mRow.get("orderItemCarrier_" + (orderItemNo + 1)) + "\" ");
		    		    				         rowString.append("trackingNumber" + "=\"" + mRow.get("orderItemTrackingNumber_" + (orderItemNo + 1)) + "\" ");
		    		    				         rowString.append("estimatedShipDate" + "=\"" + mRow.get("orderItemShipDate_" + (orderItemNo + 1)) + "\" ");
		    		    				         rowString.append("/>");
		    		    				         bwOutFile.write(rowString.toString());
		    		    			             bwOutFile.newLine();
		    		            		     }
		    		            	     }
		    		                 }
		    		            }
		            			 
		            		 }
		            	 }
		             } else {
		            	 List<GenericValue> orderItemShipGroupAssocList = _delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String)mRow.get("orderId")), UtilMisc.toList("+orderItemSeqId"));
		            	 if(UtilValidate.isNotEmpty(orderItemShipGroupAssocList)) {
		            		 for(GenericValue orderItemShipGroupAssoc : orderItemShipGroupAssocList) {
		            			 GenericValue orderItemShipGroup = _delegator.findByPrimaryKey("OrderItemShipGroup", UtilMisc.toMap("orderId", orderItemShipGroupAssoc.getString("orderId"), "shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId")));
		            			 if(UtilValidate.isNotEmpty(orderItemShipGroup)) {
		            				 if(!mRow.get("orderShipMethod").equals(orderItemShipGroup.getString("shipmentMethodTypeId")) || !mRow.get("orderShipCarrier").equals(orderItemShipGroup.getString("carrierPartyId")) || !mRow.get("orderTrackingNumber").equals(orderItemShipGroup.getString("trackingNumber"))) {
		            					 rowString.setLength(0);
		    		            		 rowString.append("<" + "OrderItemShipGroup" + " ");
		    				             rowString.append("orderId" + "=\"" + orderItemShipGroupAssoc.getString("orderId") + "\" ");
		    				             rowString.append("shipGroupSeqId" + "=\"" + orderItemShipGroupAssoc.getString("shipGroupSeqId") + "\" ");
		    				             rowString.append("shipmentMethodTypeId" + "=\"" + (String)mRow.get("orderShipMethod") + "\" ");
		    				             rowString.append("carrierPartyId" + "=\"" + (String)mRow.get("orderShipCarrier") + "\" ");
		    				             rowString.append("trackingNumber" + "=\"" + (String)mRow.get("orderTrackingNumber") + "\" ");
		    				             rowString.append("estimatedShipDate" + "=\"" + (String)mRow.get("orderShipDate") + "\" ");
		    				             rowString.append("/>");
		    				             bwOutFile.write(rowString.toString());
		    			                 bwOutFile.newLine();
		            				 }
		            			 }
		            		 }
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
    
    private static String getOrderStatus(String OrderId, Map mRow) {
    	Map xmlOrderItems = FastMap.newInstance();
    	for(int orderItemNo = 0; orderItemNo < Integer.parseInt((String)mRow.get("totalOrderItems")); orderItemNo++)
    	{
    		xmlOrderItems.put((String)mRow.get("orderItemId_" + (orderItemNo + 1)), (String)mRow.get("orderItemStatus_" + (orderItemNo + 1)));
    	}
    	List<GenericValue> orderItems = null;
		try {
			orderItems = _delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", (String)mRow.get("orderId")));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List totalOrderItemStatus = FastList.newInstance();
   	    if(UtilValidate.isNotEmpty(orderItems)) {
   		     for(GenericValue orderItem : orderItems) {
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
   	    if(totalOrderItemStatus.contains("ITEM_APPROVED")) {
   	    	return "ORDER_APPROVED";
   	    }
   	    else if(totalOrderItemStatus.contains("ITEM_COMPLETED")) {
   	    	return "ORDER_COMPLETED";
   	    }
   	    else if(new HashSet(totalOrderItemStatus).size() == 1) {
   	    	if(totalOrderItemStatus.get(0).equals("ITEM_APPROVED")) {
   	    		return "ORDER_APPROVED";
   	    	}
   	    	if(totalOrderItemStatus.get(0).equals("ITEM_COMPLETED")) {
   	    		return "ORDER_COMPLETED";
   	    	}
   	    	if(totalOrderItemStatus.get(0).equals("ITEM_CANCELLED")) {
   	    		return "ORDER_CANCELLED";
   	    	}
   	    }
    	return "";
    }
    
    public static Map<String, Object> importStoreXML(DispatchContext ctx, Map<String, ?> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        _delegator = ctx.getDelegator();
        List<String> messages = FastList.newInstance();

        String xmlDataFilePath = (String)context.get("xmlDataFile");
        String xmlDataDirPath = (String)context.get("xmlDataDir");
        Boolean autoLoad = (Boolean) context.get("autoLoad");

        if (autoLoad == null) autoLoad = Boolean.FALSE;

        File inputWorkbook = null;
        File baseDataDir = null;
        BufferedWriter fOutProduct=null;
        if (UtilValidate.isNotEmpty(xmlDataFilePath) && UtilValidate.isNotEmpty(xmlDataDirPath)) {
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
                            inputWorkbook = new File(baseDataDir,  UtilDateTime.nowAsString()+"."+FilenameUtils.getExtension(xmlDataFilePath));
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
            	JAXBElement<BigFishStoreFeedType> bfOrderStoreFeedType = (JAXBElement<BigFishStoreFeedType>)unmarshaller.unmarshal(inputWorkbook);
            	
            	List dataRows = buildStoreXMLDataRows(bfOrderStoreFeedType);
            	buildStore(dataRows, xmlDataDirPath);
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
                    FileUtils.copyFileToDirectory(file, doneXmlDir);
                    file.delete();
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
                
                mRows.put("productStore",store.getProductStore());
                mRows.put("storeCode",store.getStoreCode());
                mRows.put("storeName",store.getStoreName());
                
                StoreAddressType storesAddress = store.getStoreAddress();
                mRows.put("country",storesAddress.getCountry());
                mRows.put("address1",storesAddress.getAddress1());
                mRows.put("address2",storesAddress.getAddress2());
                mRows.put("address3",storesAddress.getAddress3());
                mRows.put("city",storesAddress.getCityTown());
                mRows.put("state",storesAddress.getStateProvince());
                mRows.put("zip",storesAddress.getZipPostCode());
                mRows.put("phone",storesAddress.getStorePhone());
                
                mRows.put("openingHours",store.getOpeningHours());
                mRows.put("storeNotice",store.getStoreNotice());
                mRows.put("storeContentSpot",store.getStoreContentSpot());
                mRows.put("status",store.getStatus());
                mRows.put("geoCodeLat",store.getGEOCodeLat());
                mRows.put("geoCodeLong",store.getGEOCodeLong());
                
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
			
	        fOutFile = new File(xmlDataDirPath, "Store-location.xml");
            if (fOutFile.createNewFile()) {
            	bwOutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fOutFile), "UTF-8"));

                writeXmlHeader(bwOutFile);
                
                for (int i=0 ; i < dataRows.size() ; i++) {
                     StringBuilder  rowString = new StringBuilder();
	            	 Map mRow = (Map)dataRows.get(i);
	            	 
	            	 String partyId = _delegator.getNextSeqId("Party");
                     rowString.append("<" + "Party" + " ");
                     rowString.append("partyId" + "=\"" + partyId + "\" ");
                     rowString.append("partyTypeId" + "=\"" + "PARTY_GROUP" + "\" ");
                     if(((String)mRow.get("status")).equalsIgnoreCase("open")) {
                         rowString.append("statusId" + "=\"" + "PARTY_ENABLED" + "\" ");
                     } else if(((String)mRow.get("status")).equalsIgnoreCase("closed")) {
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
                     rowString.append("address1" + "=\"" +  (String)mRow.get("address1") + "\" ");
                     rowString.append("address1" + "=\"" +  (String)mRow.get("address2") + "\" ");
                     rowString.append("address1" + "=\"" +  (String)mRow.get("address3") + "\" ");
                     rowString.append("city" + "=\"" +  (String)mRow.get("city") + "\" ");
                     rowString.append("stateProvinceGeoId" + "=\"" +  mRow.get("state") + "\" ");
                     rowString.append("countyGeoId" + "=\"" +  mRow.get("country") + "\" ");
                     rowString.append("postalCode" + "=\"" +  mRow.get("zip") + "\" ");
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
 	            	
                     if(mRow.get("geoCodeLat") != "" || mRow.get("geoCodeLong") != "") {
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
            rowString.append("/>");
            bwOutFile.write(rowString.toString());
            bwOutFile.newLine();
			String sFromDate = (String)mRow.get("fromDate");
			if (UtilValidate.isEmpty(sFromDate))
			{
				sFromDate=_sdf.format(UtilDateTime.nowTimestamp());
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
}

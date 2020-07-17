import org.ofbiz.base.util.*
import org.ofbiz.base.util.string.*
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.model.*
import org.ofbiz.entity.transaction.*
import org.ofbiz.entity.util.*
import org.ofbiz.security.*
import org.ofbiz.webapp.pseudotag.*
import org.w3c.dom.*

osafeProperties = UtilProperties.getResourceBundleMap("OsafeProperties.xml", locale);
productStoreId = (session.getAttribute("selectedStore")).get("productStoreId");

//Reads the FileToExport parameter and gets the xmlFilePath.
if(UtilValidate.isNotEmpty(parameters.fileToExport) && UtilValidate.isNotEmpty(productStoreId))
{    
    XmlFilePath = null;
    if(parameters.fileToExport == "uiSequenceFile")
    {
        XmlFilePath = FlexibleStringExpander.expandString(osafeProperties.ecommerceUiSequenceXmlFile, context);
    }
    else if(parameters.fileToExport == "uiLabelFile")
    {
        XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-deployment-UiLabel-xml-file"), context);
    }
    else if(parameters.fileToExport == "adminUiLabelFile")
    {
        XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "admin-deployment-UiLabel-xml-file"), context);
    }
    else if(parameters.fileToExport == "XProductStoreParm")
    {
        efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
        filePath = System.getProperty("ofbiz.home") + "/runtime/tmp/";
        String curEntityName = "XProductStoreParm";
        outdir = new File(filePath);
        fileName = curEntityName;
        numberWritten = 0;
        fileNumber = 1;
        results = [];
        maxRecordsPerFile = 0;
        if (!outdir.exists()) 
        {
            outdir.mkdir();
        }
        if (outdir.isDirectory() && outdir.canWrite())   
        {
            try {
                beganTransaction = TransactionUtil.begin(3600);
                me = delegator.getModelEntity(curEntityName);
                entityStoreCond = EntityCondition.makeCondition("productStoreId", EntityComparisonOperator.EQUALS, productStoreId);
                values = delegator.find(curEntityName, entityStoreCond, null, null, me.getPkFieldNames(), efo);
                isFirst = true;
                writer = null;
                fileSplitNumber = 1;
                valuesIter = values.iterator();
                while ((value = valuesIter.next()) != null) {
                    //Don't bother writing the file if there's nothing
                    //to put into it
                    if (isFirst) 
                    {
                        newFile = new File(outdir, fileName +".xml");
                        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outdir, fileName +".xml")), "UTF-8")));
                        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                        writer.println("<entity-engine-xml>");
                        isFirst = false;
                    }
                    value.writeXmlText(writer, "");
                    numberWritten++;
        
                    // split into small files
                    if (maxRecordsPerFile > 0 && (numberWritten % maxRecordsPerFile == 0)) 
                    {
                        fileSplitNumber++;
                        // close the file
                        writer.println("</entity-engine-xml>");
                        writer.close();
        
                        // create a new file
                        splitNumStr = UtilFormatOut.formatPaddedNumber((long) fileSplitNumber, 3);
                        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outdir, fileName + "_" + splitNumStr +".xml")), "UTF-8")));
                        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                        writer.println("<entity-engine-xml>");
                    }
        
                    if (numberWritten % 500 == 0 || numberWritten == 1) 
                    {
                        Debug.log("Records written [$curEntityName]: $numberWritten");
                    }
        
                }
                if (writer) 
                {
                    writer.println("</entity-engine-xml>");
                    writer.close();
                    String thisResult = "[$fileNumber] [$numberWritten] $curEntityName wrote $numberWritten records";
                    Debug.log(thisResult);
                    results.add(thisResult);
                    XmlFilePath = filePath +'/'+fileName+'.xml';
                }
                else 
                {
                    thisResult = "[$fileNumber] [---] $curEntityName has no records, not writing file";
                    Debug.log(thisResult);
                    results.add(thisResult);
                }
                values.close();
                } catch (Exception ex){
                if (values != null) {
                    values.close();
                }
                thisResult = "[$fileNumber] [xxx] Error when writing $curEntityName: $ex";
                Debug.log(thisResult);
                results.add(thisResult);
                TransactionUtil.rollback(beganTransaction, thisResult, ex);
                } finally {
                // only commit the transaction if we started one... this will throw an exception if it fails
                TransactionUtil.commit(beganTransaction);
                }
        }
    }
	else
	{
		ecommerceConfigPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-config-path"), context);
		exportFile = new File(ecommerceConfigPath, parameters.fileToExport);
		if (exportFile.exists())
	    {
			XmlFilePath = exportFile.getAbsolutePath();
		}
	}
    
    //Logic to write an XML file and displaying on the new browser tab.
    if(UtilValidate.isNotEmpty(XmlFilePath))
    {  
        File file = new File(XmlFilePath);
        String XmlFileName = file.getName();
        if(UtilValidate.isNotEmpty(XmlFilePath) && UtilValidate.isNotEmpty(XmlFileName))
        {   
            response.setContentType("text/xml");
            exportedFilePath = parameters.exportedFilePath;
            exportedFileName = parameters.exportedFileName;
            response.setHeader("Content-Disposition","inline; filename=\"" + XmlFileName + "\";");
            InputStream inputStr = new FileInputStream(XmlFilePath);
            OutputStream out = response.getOutputStream();
            byte[] bytes = new byte[102400];
            int bytesRead;
            while ((bytesRead = inputStr.read(bytes)) != -1)
            {
                out.write(bytes, 0, bytesRead);
            }
            out.flush();
            out.close();
            inputStr.close();
        } 
        //Deleting the temp file.
        if(parameters.fileToExport == "XProductStoreParm")
        {
            file.delete();
        }
    }
    return("success");
}
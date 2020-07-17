package admin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import com.osafe.util.OsafeProductLoaderHelper;

uiLabelMap = UtilProperties.getResourceBundleMap("OsafeAdminUiLabels", locale);

String xlsFileName = parameters.uploadedXLSFile;
String xlsFilePath = parameters.uploadedXLSPath;
String imageUrl = parameters.imageUrl;

String productLoadImagesDir = parameters.productLoadImagesDir;
session.setAttribute("uploadedXLSFile",parameters.uploadedXLSFile);
if(UtilValidate.isEmpty(parameters.uploadedXLSFile))
{
    xlsFileName = session.getAttribute("uploadedXLSFile");
}

if(UtilValidate.isEmpty(parameters.uploadedXLSPath))
{  
    xlsFilePath = parameters.xlsFilePath;
}
String tempDir = xlsFilePath;
String filePath = tempDir + xlsFileName;

List productCatDataList = FastList.newInstance();
List productDataList = FastList.newInstance();
List productAssocDataList = FastList.newInstance();
List productFeatureSwatchDataList = FastList.newInstance();
List manufacturerDataList = FastList.newInstance();

File inputWorkbook = null;
try {
    inputWorkbook = new File(filePath);
    
} catch (IOException ioe) {
    //Debug.logError(ioe, module);
} catch (Exception exc) {
    //Debug.logError(exc, module);
}
if (inputWorkbook != null) {
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
                    productCatDataList = OsafeProductLoaderHelper.getProductCategoryXLSDataList(s);
                }
                if (sheet == 2)
                {
                    productDataList = OsafeProductLoaderHelper.getProductXLSDataList(s);
                }
                if (sheet == 3)
                {
                    productAssocDataList = OsafeProductLoaderHelper.getProductAssocXLSDataList(s);
                }
                if (sheet == 4)
                {
                    productFeatureSwatchDataList = OsafeProductLoaderHelper.getProductFeatureSwatchXLSDataList(s);
                }
                if (sheet == 5)
                {
                    manufacturerDataList = OsafeProductLoaderHelper.getManufacturerXLSDataList(s);
                }
            } catch (Exception exc) {
                //Debug.logError(exc, module);
            } 
        }
    } catch (BiffException be) {
        //Debug.logError(be, module);
    } catch (Exception exc) {
        //Debug.logError(exc, module);
    }
}
context.productCatDataList = productCatDataList;
context.productDataList = productDataList;
context.productAssocDataList = productAssocDataList;
context.productFeatureSwatchDataList = productFeatureSwatchDataList;
context.manufacturerDataList = manufacturerDataList;
context.productLoadImagesDir = productLoadImagesDir;
context.xlsFileName = xlsFileName;
context.xlsFilePath = xlsFilePath;
context.imageUrl = imageUrl;
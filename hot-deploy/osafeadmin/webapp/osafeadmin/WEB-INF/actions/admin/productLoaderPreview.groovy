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
import java.util.Collections;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import com.osafe.util.OsafeProductLoaderHelper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import com.osafe.feeds.FeedsUtil;
import com.osafe.feeds.osafefeeds.*;
import com.osafe.services.ImportServices;

uiLabelMap = UtilProperties.getResourceBundleMap("OSafeAdminUiLabels", locale);

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
try 
{
    inputWorkbook = new File(filePath);
} 
catch (IOException ioe) 
{
    //Debug.logError(ioe, module);
} 
catch (Exception exc) 
{
    //Debug.logError(exc, module);
}
if (inputWorkbook != null) 
{
  if(xlsFileName.endsWith(".xls"))
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
            } 
            catch (Exception exc) 
            {
                //Debug.logError(exc, module);
            } 
        }
    } 
    catch (BiffException be) 
    {
        //Debug.logError(be, module);
    } 
    catch (Exception exc) 
    {
        //Debug.logError(exc, module);
    }
  }
  if(xlsFileName.endsWith(".xml"))
  {
      try 
      {
      JAXBContext jaxbContext = JAXBContext.newInstance("com.osafe.feeds.osafefeeds");
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      JAXBElement<BigFishProductFeedType> bfProductFeedType = (JAXBElement<BigFishProductFeedType>)unmarshaller.unmarshal(inputWorkbook);
            	
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
          List dataRows = ImportServices.buildProductCategoryXMLDataRows(productCategories);
          for (int i=0 ; i < dataRows.size() ; i++) 
          {
            Map mRow = (Map)dataRows.get(i);
            productCatDataList.add(mRow);
          }
      }
      if(products.size() > 0) 
      {
          List dataRows = ImportServices.buildProductXMLDataRows(products);
          for (int i=0 ; i < dataRows.size() ; i++) 
          {
            Map mRow = (Map)dataRows.get(i);
            productDataList.add(mRow);
          }
      }
      if(productAssociations.size() > 0) 
      {
          List dataRows = ImportServices.buildProductAssociationXMLDataRows(productAssociations);
          for (int i=0 ; i < dataRows.size() ; i++) 
          {
            Map mRow = (Map)dataRows.get(i);
            productAssocDataList.add(mRow);
          }
      }
      if(productFeatureSwatches.size() > 0) 
      {
          List dataRows = ImportServices.buildProductFeatureSwatchXMLDataRows(productFeatureSwatches);
          for (int i=0 ; i < dataRows.size() ; i++) 
          {
            Map mRow = (Map)dataRows.get(i);
            productFeatureSwatchDataList.add(mRow);
          }
      }
      if(productManufacturers.size() > 0) 
      {
          List dataRows = ImportServices.buildProductManufacturerXMLDataRows(productManufacturers);
          for (int i=0 ; i < dataRows.size() ; i++) 
          {
            Map mRow = (Map)dataRows.get(i);
            manufacturerDataList.add(mRow);
          }
      }
    } 
    catch (Exception e)
    {
    }
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
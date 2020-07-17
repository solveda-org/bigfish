package com.osafe.util;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.StringUtil;

import com.osafe.services.ImportServices;

import javolution.util.FastList;
import javolution.util.FastMap;

import jxl.Cell;
import jxl.Sheet;

public class OsafeProductLoaderHelper {

    public static final String module = OsafeAdminUtil.class.getName();
    
    public static List getProductXLSDataList(Sheet s) {
        List dataRows = buildDataRows(ImportServices.buildProductHeader(),s);
        List produtcXLSList = FastList.newInstance();
        for (int i=0 ; i < dataRows.size() ; i++) {
            Map mRow = (Map)dataRows.get(i);
            produtcXLSList.add(mRow);
        }
        return produtcXLSList;
    }
    public static List getProductCategoryXLSDataList(Sheet s) {
        List dataRows = buildDataRows(ImportServices.buildCategoryHeader(),s);
        List productCategoryXLSList = FastList.newInstance();
        for (int i=0 ; i < dataRows.size() ; i++) {
            Map mRow = (Map)dataRows.get(i);
            productCategoryXLSList.add(mRow);
        }
        return productCategoryXLSList;
    }
    public static List getProductAssocXLSDataList(Sheet s) {
        List dataRows = buildDataRows(ImportServices.buildProductAssocHeader(),s);
        List productAssocXLSList = FastList.newInstance();
        for (int i=0 ; i < dataRows.size() ; i++) {
            Map mRow = (Map)dataRows.get(i);
            productAssocXLSList.add(mRow);
        }
        return productAssocXLSList;
    }
    
    public static List getProductFeatureSwatchXLSDataList(Sheet s) {
        List dataRows = buildDataRows(ImportServices.buildProductFeatureSwatchHeader(),s);
        List productFeatureSwatchXLSList = FastList.newInstance();
        for (int i=0 ; i < dataRows.size() ; i++) {
            Map mRow = (Map)dataRows.get(i);
            productFeatureSwatchXLSList.add(mRow);
        }
        return productFeatureSwatchXLSList;
    }
        
    public static List getManufacturerXLSDataList(Sheet s) {
        List dataRows = buildDataRows(ImportServices.buildManufacturerHeader(),s);
        List ManufacturerXLSList = FastList.newInstance();
        for (int i=0 ; i < dataRows.size() ; i++) {
            Map mRow = (Map)dataRows.get(i);
            ManufacturerXLSList.add(mRow);
        }
        return ManufacturerXLSList;
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
                    dataRows.add(mRows);
                }
            }
    	}
      	catch (Exception e) {}
      	return dataRows;
        }
    }
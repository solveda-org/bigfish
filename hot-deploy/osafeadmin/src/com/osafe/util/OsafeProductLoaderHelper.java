package com.osafe.util;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import jxl.Cell;
import jxl.Sheet;

public class OsafeProductLoaderHelper {

    public static final String module = OsafeAdminUtil.class.getName();
    
    public static List getDataList(List dataRows) 
    {
        List dataList = FastList.newInstance();
        for (int i=0 ; i < dataRows.size() ; i++) 
        {
            Map mRow = (Map)dataRows.get(i);
            dataList.add(mRow);
        }
        return dataList;
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
                        mRows.put(headerCols.get(colCount),colContent);
                    }
                    dataRows.add(mRows);
                }
            }
    	}
      	catch (Exception e) {}
      	return dataRows;
    }
    
    
    static Map featureTypeIdMap = FastMap.newInstance();
    public static Map buildFeatureMap(Map featureTypeMap,String parseFeatureType, Delegator delegator) 
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
                			List productFeatureList = delegator.findByAnd("ProductFeature", UtilMisc.toMap("productFeatureTypeId", StringUtil.removeSpaces(featureType).toUpperCase(), "productFeatureCategoryId", StringUtil.removeSpaces(featureType).toUpperCase(), "description", featureTokens[f].trim()));
                			if(UtilValidate.isNotEmpty(productFeatureList))
                			{
                				GenericValue productFeature = EntityUtil.getFirst(productFeatureList);
        						featureId = productFeature.getString("productFeatureId");
                			}
                			else
                			{
                				featureId = delegator.getNextSeqId("ProductFeature");
                			}
                		}
                		featureTypeIdMap.put(featureTypeKey, featureId);
					} catch (GenericEntityException e) 
					{
						e.printStackTrace();
					}
                	mFeatureMap.put(""+featureId,""+featureTokens[f].trim());
                }
        		featureTypeMap.put(featureType, mFeatureMap);
        	}
    		
    	}
    	return featureTypeMap;
    	    	
        }
    }

    




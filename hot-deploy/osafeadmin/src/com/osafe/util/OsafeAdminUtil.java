package com.osafe.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Iterator;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.Currency;

import javax.servlet.ServletRequest;

import org.apache.commons.io.FileUtils;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.CommonWorkers;
import com.ibm.icu.util.Calendar;
import com.osafe.services.OsafeManageXml;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class OsafeAdminUtil {

    public static final String module = OsafeAdminUtil.class.getName();
    public static final String decimalPointDelimiter = ".";
    public static final boolean defaultEmptyOK = true;
    /**
     * Return a string formatted as format
     * if format is wrong or null return dateString for the default locale
     * @param time Stamp
     * @param string date format
     * @return return String formatted for given date with given format
     */
    public static String convertDateTimeFormat(Timestamp timestamp, String format) {
        String dateString ="";
        if (UtilValidate.isEmpty(timestamp)) 
        {
            return "";
        }
        try {
            dateString = UtilDateTime.toDateString(new Date(timestamp.getTime()), format);
        } catch (Exception e) {
            dateString = UtilDateTime.toDateString(new Date(timestamp.getTime()), null);
        }
        return dateString;
    }

    public static String makeValidId(String id, String spaceReplacement, boolean makeUpCase) {
        return makeValidId(id, null, spaceReplacement, makeUpCase);
    }

    public static String makeValidId(String id, Integer length, String spaceReplacement, boolean makeUpCase) {

        if (spaceReplacement == null)
        {
            spaceReplacement = " ";
        }
        if (UtilValidate.isEmpty(id)) 
        {
            return null;
        }
        id = id.trim().replaceAll("\\s{1,}", spaceReplacement);
        if (makeUpCase) {
            id = id.toUpperCase();
        }
        if (UtilValidate.isNotEmpty(length))
        {
            if (id.length() > length)
            {
                return null;
            }
        }
        return id;
    }
    
   	/** 
	 * Checks Multiple email addresses separated by delimiter
	 */
    public static Boolean checkMultiEmailAddress(String emailId, String delimiter) {
    	  if (UtilValidate.isEmpty(delimiter)) {
              delimiter = ";";
          }
          if (UtilValidate.isEmpty(emailId)) {
              return false;
          }
          List<String> emailList = StringUtil.split(emailId,delimiter);
          for (String email: emailList) {
              if (!UtilValidate.isEmail(email)) return false;
          }
          return true;
    }
    
   	/** 
	 * Returns a List of Product Id's that are associated
	 * from top level category and it's sublevel categories.
	 */
    public static List<String> getAssociatedProductFromCategory(ServletRequest request, String productCategoryId) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<String> categoryIds = FastList.newInstance();
        List<GenericValue> productCategoryMembers = FastList.newInstance();
        List<String> productIds = FastList.newInstance();
        if (UtilValidate.isNotEmpty(productCategoryId))
        {
    	    List<GenericValue> categories = CategoryWorker.getRelatedCategoriesRet(request, null, productCategoryId, true, false, true);
    	    if (UtilValidate.isNotEmpty(categories))
    	    {
    	    	categoryIds = EntityUtil.getFieldListFromEntityList(categories, "productCategoryId", true);
    	    }
    	    categoryIds.add(productCategoryId);
    	    try {
    	    	productCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryIds), null, null, null, false);
    	    	if (UtilValidate.isNotEmpty(productCategoryMembers)) {
    	    		productIds = EntityUtil.getFieldListFromEntityList(productCategoryMembers, "productId", true);
    	    	}
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
    	return productIds;
    }
    
    /** 
     * Returns a Generic Product Price for given ProductId and ProductPriceTypeId
     */
    public static GenericValue getProductPrice(ServletRequest request, String productId, String productPriceTypeId) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	List<GenericValue> productPriceList = FastList.newInstance();
    	List<GenericValue> productPriceListFiltered = FastList.newInstance();
    	GenericValue productPrice = null;
    	if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productPriceTypeId))
    	{
    		try {
    	        productPriceList = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", productPriceTypeId));
    	        if(UtilValidate.isNotEmpty(productPriceList))
    	        {
    	            productPriceListFiltered = EntityUtil.filterByDate(productPriceList);
    		        if(UtilValidate.isNotEmpty(productPriceListFiltered))
    		        {
    		            productPrice = EntityUtil.getFirst(productPriceListFiltered);
    		        }
    		    }
    		}
    		catch (Exception e) {
    		    Debug.logWarning(e, module);
			}
    	}
    	return productPrice;
    }

    public static boolean isValidDateFormat(String format) {
        if (UtilValidate.isEmpty(format)) {
            return false;
        }
        try {
            UtilDateTime.toDateString(new Date(), format);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     *return the parmValue of given parmKey.
     *
     *@param Delegator
     *@param productStoreId 
     *@param pramKey
     *@return String of the pramKey
     */

    public static String getProductStoreParm(Delegator delegator, String productStoreId, String parmKey) {
        if (UtilValidate.isEmpty(parmKey) || UtilValidate.isEmpty(productStoreId)) {
            return null;
        }
        String parmValue = null;
        GenericValue xProductStoreParam = null;
        try {
            xProductStoreParam = delegator.findOne("XProductStoreParm", UtilMisc.toMap("productStoreId", productStoreId, "parmKey", parmKey), false);
            if (UtilValidate.isNotEmpty(xProductStoreParam))
            {
                parmValue = xProductStoreParam.getString("parmValue");
            }
        } catch (Exception e) {
            Debug.logError(e, e.getMessage(), module);
        }
        return parmValue;
    }

    /**
     *return the parmValue of given parmKey.
     *
     *@param request
     *@param pramKey
     *@return String of the pramKey
     */

    public static String getProductStoreParm(ServletRequest request, String parmKey) {
        if (UtilValidate.isEmpty(parmKey)) {
            return null;
        }
        return getProductStoreParm((Delegator)request.getAttribute("delegator"), ProductStoreWorker.getProductStoreId(request), parmKey);
    }

    public static Timestamp addDaysToTimestamp(Timestamp start, int days) {
        Calendar tempCal = UtilDateTime.toCalendar(start, TimeZone.getDefault(), Locale.getDefault());
        tempCal.add(Calendar.DAY_OF_MONTH, days);
        Timestamp retStamp = new Timestamp(tempCal.getTimeInMillis());
        retStamp.setNanos(0);
        return retStamp;
    }

    public static int getIntervalInDays(Timestamp from, Timestamp thru) {
        Calendar fromCal = UtilDateTime.toCalendar(UtilDateTime.getDayStart(from), TimeZone.getDefault(), Locale.getDefault());
        Calendar thruCal = UtilDateTime.toCalendar(UtilDateTime.getDayEnd(thru), TimeZone.getDefault(), Locale.getDefault());
        return thru != null ? (int) ((thruCal.getTimeInMillis() - fromCal.getTimeInMillis()) / (24*60*60*1000)) : 0;
    }

    public static long daysBetween(Timestamp from, Timestamp thru) {  
        Calendar startDate = UtilDateTime.toCalendar(from, TimeZone.getDefault(), Locale.getDefault());
        Calendar endDate = UtilDateTime.toCalendar(thru, TimeZone.getDefault(), Locale.getDefault());
    	  Calendar date = (Calendar) startDate.clone();  
    	  long daysBetween = 0;  
    	  while (date.before(endDate)) {  
    	    date.add(Calendar.DAY_OF_MONTH, 1);  
    	    daysBetween++;  
    	  }  
    	  return daysBetween;  
    	}      

    public static boolean isFloat(String s) {
        if (UtilValidate.isEmpty(s)) return defaultEmptyOK;

        boolean seenDecimalPoint = false;
        
        if (s.startsWith(decimalPointDelimiter) && s.length() == 1) return false;
        // Search through string's characters one by one
        // until we find a non-numeric character.
        // When we do, return false; if we don't, return true.
        for (int i = 0; i < s.length(); i++) {
            // Check that current character is number.
            char c = s.charAt(i);

            if (c == decimalPointDelimiter.charAt(0)) {
                if (!seenDecimalPoint) {
                    seenDecimalPoint = true;
                } else {
                    return false;
                }
            } else {
                if (!UtilValidate.isDigit(c)) return false;
            }
        }
        // All characters are numbers.
        return true;
    }
    
    public static List<File> getUserContent(String type) {
        List<File> fileList = new ArrayList<File>();
        Map<String, ?> context = FastMap.newInstance();
        String osafeThemeServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "osafe.theme.server"), context);
        String userContentImagePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafe", "user-content.image-path"), context);

        String userContentPath = null;
        if(UtilValidate.isNotEmpty(type)) {
            userContentPath = osafeThemeServerPath + userContentImagePath + type+"/";
            
        } else {
            userContentPath = osafeThemeServerPath + userContentImagePath;
        }
        File userContentDir = new File(userContentPath);
        
        fileList = getFileList(userContentDir, fileList);
        return fileList;
    }

    public static List<GenericValue> getCountryList(ServletRequest request) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue> countryList = FastList.newInstance();
        String countryDefault = "";
        String countryDropdown = "";
        String countryMulti = "";

        countryDefault = getProductStoreParm(delegator, ProductStoreWorker.getProductStoreId(request), "COUNTRY_DEFAULT");
        if (UtilValidate.isEmpty(countryDefault)) {
            countryDefault = "USA";
        }
        countryDropdown =  getProductStoreParm(delegator, ProductStoreWorker.getProductStoreId(request), "COUNTRY_DROPDOWN");
        countryMulti = getProductStoreParm(delegator, ProductStoreWorker.getProductStoreId(request), "COUNTRY_MULTI");
        countryDropdown = countryDropdown+","+countryDefault;

        if (UtilValidate.isNotEmpty(countryMulti) && countryMulti.equalsIgnoreCase("true")) {
            countryList = CommonWorkers.getCountryList(delegator);
            if (UtilValidate.isNotEmpty(countryDropdown) && !(countryDropdown.equalsIgnoreCase("ALL"))) {
                List<String> countryDropdownList = StringUtil.split(countryDropdown,",");
                for(GenericValue country: countryList) {
                    String geoId = country.getString("geoId");
                    if (!countryDropdownList.contains(geoId)) {
                        countryList.remove(country);
                    }
                }
            }
        }
        return countryList;
    }
    
  //Recusive method takes the root directory and returns the files from the folders and their subfolders 
    public static List<File> getFileList(File userContentDir , List<File> fileList) {
        File[] fileArray = userContentDir.listFiles();
        for (File file: fileArray) {
            try {
                if(!file.getName().equals(".svn")) {
                    if(file.isDirectory()) {
                        getFileList(file, fileList);
                    } else {
                        fileList.add(file);
                    }
                }
            } catch (Exception exc) {
                Debug.logError(exc, module);
            }
        }
        return fileList;
    }
    
    /**
     *move the file from source directory to target directory and also delete the source file.
     *@param contentSourcePath
     *@param contentTargetPath
     *@param fileName
     */
    public static void moveContent(String contentSourcePath, String contentTargetPath, String fileName) {
        if (UtilValidate.isNotEmpty(contentSourcePath)) {
            File sourceFile = new File(contentSourcePath + fileName);
            
            //Make the Destination directory and file objects
            File targetDir = new File(contentTargetPath);
            try {
               //create the directory if not exists
               if (!targetDir.exists()) {
                   targetDir.mkdirs();
                }
            // Move file from source directory to destination directory
               FileUtils.copyFileToDirectory(sourceFile, targetDir);
            } catch (Exception e) {
                Debug.logError (e, module);
            } finally {
                try {
                    //delete the source file. 
                    FileUtils.forceDelete(sourceFile);
                } catch(Exception e) {
                    Debug.logError (e, module);
                }
            }
        }
    }

    /**
     *return the XProductStoreParm Map of given parmKey.
     *@param delegator
     *@param webSiteId
     *@param productStoreId
     *@return Map of the XProductStoreParm
     */
    public static Map getProductStoreParmMap(Delegator delegator, String webSiteId, String productStoreId) {
        Map mProductStoreParm = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(webSiteId)  || UtilValidate.isNotEmpty(productStoreId)) 
        {
            try 
            {
                String storeId=null;
                if (UtilValidate.isEmpty(productStoreId))
                {
                    GenericValue webSite = delegator.findByPrimaryKeyCache("WebSite", UtilMisc.toMap("webSiteId", webSiteId));
                    storeId=webSite.getString("productStoreId");
                }
                else
                {
                    storeId=productStoreId;
                }
                if (UtilValidate.isNotEmpty(storeId))
                {
                    List lProductStoreParam = delegator.findByAnd("XProductStoreParm", UtilMisc.toMap("productStoreId", storeId));
                    if (UtilValidate.isNotEmpty(lProductStoreParam))
                    {
                         Iterator parmIter = lProductStoreParam.iterator();
                         while (parmIter.hasNext()) 
                         {
                             GenericValue prodStoreParm = (GenericValue) parmIter.next();
                             mProductStoreParm.put(prodStoreParm.getString("parmKey"),prodStoreParm.getString("parmValue"));
                         }
                    }
                }
            } 
            catch (Exception e) {
                Debug.logError(e, e.getMessage(), module);
            }
        }
        return mProductStoreParm;
    }
    
    public static boolean isProductStoreParmTrue(String parmValue) {
        if (UtilValidate.isEmpty(parmValue)) 
         {
             return false;
         }
         if ("TRUE".equals(parmValue.toUpperCase()))
         {
         	return true;
         }
         return false;
     }
    
    private static Map<String, ?> context = FastMap.newInstance();
	
    public static String buildProductImagePathExt(String productContentTypeId) 
    {
    	String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
    	
    	List<Map<Object, Object>> imageLocationPrefList = OsafeManageXml.getListMapsFromXmlFile(XmlFilePath);
    	
        Map<Object, Object> imageLocationMap = new HashMap<Object, Object>();
        
    	for(Map<Object, Object> imageLocationPref : imageLocationPrefList) {
    		imageLocationMap.put(imageLocationPref.get("key"), imageLocationPref.get("value"));
    	}
    	String defaultImageDirectory = (String)imageLocationMap.get("DEFAULT_IMAGE_DIRECTORY");
    	if(UtilValidate.isEmpty(defaultImageDirectory)) {
    		defaultImageDirectory = "";
    	}
    	StringBuffer sbDefaultImageDirectory = new StringBuffer(defaultImageDirectory);
    	String imageLocationSubDir = (String)imageLocationMap.get(productContentTypeId);
    	if(UtilValidate.isNotEmpty(imageLocationSubDir)) {
    		sbDefaultImageDirectory.append(imageLocationSubDir);
    	}
    	return sbDefaultImageDirectory.toString();
    }
    
    /** Formats a double into a properly currency symbol string based on isoCode and Locale
     * @param isoCode the currency ISO code
     * @param locale The Locale used to format the number
     * @return A String with the currency symbol
     */
    public static String showCurrency(String isoCode, Locale locale) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        if (isoCode != null && isoCode.length() > 1) {
            nf.setCurrency(Currency.getInstance(isoCode));
        }
        return nf.getCurrency().getSymbol(locale);
    }
    
    public static boolean isFileExist(String fileDirPath, String fileName) 
    {
        File file = new File(fileDirPath + fileName);
        return file.exists();
    }
    /** String with in the given limit
     * @param String that need to refactor
     * @param String length
     * @return A String with in the given limit
     */
    public static String formatToolTipText(String toolTiptext, String length) {
        return formatToolTipText(toolTiptext, length, true);
    }
    /** String with in the given limit
     * @param String that need to refactor
     * @param String length
     * @param boolean renderhtmlTag
     * @return A String with in the given limit
     */
    public static String formatToolTipText(String toolTiptext, String length, boolean renderhtmlTag) {
        if (toolTiptext == null) {
            return "";
        }
        int maxLength = 400;
        if (isNumber(length)) {
            maxLength = Integer.parseInt(length);
        }
        if (toolTiptext.length() > maxLength) {
            if (toolTiptext.charAt(maxLength) == ' ') {
                toolTiptext = toolTiptext.substring(0, maxLength);
            } else {
                try {
                    toolTiptext = toolTiptext.substring(0, toolTiptext.lastIndexOf(" ", maxLength));
                } catch (Exception e) {
                    toolTiptext = toolTiptext.substring(0, maxLength);
                }
            }
            toolTiptext = toolTiptext.concat("...");
        }
        if (renderhtmlTag) {
            toolTiptext = toolTiptext.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
        } else {
            toolTiptext = toolTiptext.replaceAll("(\r\n|\r|\n|\n\r)", " ");
        }
        toolTiptext = (toolTiptext.replace("\"","&quot")).replace("\'", "\\'");
        return StringUtil.wrapString(toolTiptext).toString();
    }

    public static boolean isNumber(String number) {
        if (UtilValidate.isEmpty(number)) {
            return false;
        }
        char[] chars = number.toCharArray();
        boolean isNumber = true;
        for (char c: chars) {
            if (!Character.isDigit(c)) {
                isNumber = false;
            }
        }
        return isNumber;
    }
    public static boolean isDateTime(String dateStr) {
        String entryDateFormat = UtilProperties.getPropertyValue("osafeAdmin.properties", "entry-date-format");
        return isDateTime(dateStr, entryDateFormat);
    }

    public static boolean isDateTime(String dateStr, String format) {
        boolean isValid = false;

        try {
            Object convertedDate = ObjectType.simpleTypeConvert(dateStr, "Timestamp", format, null);
            if (convertedDate != null) {
                isValid = true;
            }
        } catch (GeneralException e) {
            isValid = false;
        }
        return isValid;
    }
    
    public static java.sql.Timestamp toTimestamp(String date) {
        String entryDateFormat = UtilProperties.getPropertyValue("osafeAdmin.properties", "entry-date-format");
        try {
            return (Timestamp) ObjectType.simpleTypeConvert(date, "Timestamp", entryDateFormat, null);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return null;
        }
    }
    public static java.sql.Timestamp toTimestamp(String dateStr, String format) {
        if (UtilValidate.isEmpty(dateStr) || UtilValidate.isEmpty(format) ) {
            return null;
        }
        try {
            return (Timestamp) ObjectType.simpleTypeConvert(dateStr, "Timestamp", format, null);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return null;
        }
    }

    
    public static final void writeFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }

    public static void unzipZipFile(String zipFileName, String directoryToExtractTo) {
        Enumeration entriesEnum;
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(zipFileName);
            entriesEnum = zipFile.entries();

            File directory= new File(directoryToExtractTo);

            if(!directory.exists()) {
                new File(directoryToExtractTo).mkdir();
            }
            while (entriesEnum.hasMoreElements()) {
            try {
                ZipEntry entry = (ZipEntry) entriesEnum.nextElement();

                if (entry.isDirectory()) {
                
                } else {
                    int index = 0;
                    String name = entry.getName();
                    index = entry.getName().lastIndexOf("/");
                    if (index > 0 && index != name.length())
                        name = entry.getName().substring(index + 1);

                    writeFile(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(directoryToExtractTo + name)));
                 }
            } catch (Exception e) {
                e.printStackTrace();
            } 
            }
        }
        catch (Exception e) {
    	    e.printStackTrace();
        }
    }
    
    
    //In future this method will need to update then also need to update Util.formatTelephone.
    public static String formatTelephone(String areaCode, String contactNumber) {
    	if (contactNumber == null) {
            return "";
        }
        if (areaCode == null) {
            areaCode = "";
        }
        String fullPhone = areaCode + contactNumber;
        fullPhone = UtilValidate.stripCharsInBag(fullPhone, UtilValidate.phoneNumberDelimiters);

        if (UtilValidate.isEmpty(areaCode)) {
            return fullPhone;
        }

        if (fullPhone.length() == 10) {
            fullPhone = "(" + fullPhone.substring(0, 3) + ") " + fullPhone.substring(3, 6) + "-" + fullPhone.substring(6);
        }

        return fullPhone;
    }
    
    public static List findDuplicates(List<String> values)
    {
        List duplicates = new ArrayList();
        HashSet uniques = new HashSet();
        for (String value : values)
        {
            if (uniques.contains(value))
            {
                duplicates.add(value);
            }
            else
            {
                uniques.add(value);
            }
        }
        removeDuplicates(duplicates);
        return duplicates;
    }
    public static void removeDuplicates(List list) {
        HashSet set = new HashSet(list);
        list.clear();
        list.addAll(set);
    }

}
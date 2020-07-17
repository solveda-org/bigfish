package com.osafe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.ServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ServiceUtil;
import org.w3c.tidy.Tidy;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.Currency;
import com.osafe.services.OsafeManageXml;

public class OsafeAdminUtil {

    public static final String module = OsafeAdminUtil.class.getName();
    public static final String decimalPointDelimiter = ".";
    public static final boolean defaultEmptyOK = true;
    private static String[] dateFormats = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd"};
    private static final ResourceBundle OSAFE_PROPS = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());
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
    
    public static java.util.Date validDate(String date) 
    {
    	java.util.Date formattedDate = new java.util.Date();
    	
    	for(String dateFormat : dateFormats)
    	{
    		try
        	{
    			DateFormat dateFormatSdf = new SimpleDateFormat(dateFormat);
    			dateFormatSdf.setLenient(false);
            	formattedDate = dateFormatSdf.parse(date);
            	return formattedDate;
        	}
        	catch(ParseException pe)
        	{
        	}
    	}
        
        return formattedDate;
    }
    
    public static Boolean isValidDate(String date) 
    {
    	
    	for(String dateFormat : dateFormats)
    	{
    		try
        	{
    			DateFormat dateFormatSdf = new SimpleDateFormat(dateFormat);
    			dateFormatSdf.setLenient(false);
    			dateFormatSdf.parse(date);
        		return true;
        	}
        	catch(ParseException pe)
        	{
        	}
    	}
        return false;
    }
    
    public static String filterNonAscii(String inString) {
        // from http://www.velocityreviews.com/forums/t140837-convert-utf-8-to-ascii.html
        // Create the encoder and decoder for the character encoding
        Charset charset = Charset.forName("US-ASCII");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        // This line is the key to removing "unmappable" characters.
        encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        String result = inString;

        try {
            // Convert a string to bytes in a ByteBuffer
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(inString));

            // Convert bytes in a ByteBuffer to a character ByteBuffer and then to a string.
            CharBuffer cbuf = decoder.decode(bbuf);
            result = cbuf.toString();
        } catch (CharacterCodingException cce) {
            String errorMessage = "Exception during character encoding/decoding: " + cce.getMessage();
            Debug.logError(cce, errorMessage, module);
        }

        return result;
    }
    
    /** 
     * Returns a Generic Product Price for given ProductId and ProductPriceTypeId, ProductPricePurposeId
     */
    public static GenericValue getProductPrice(ServletRequest request, String productId, String productPriceTypeId) {
    	return getProductPrice(request,productId,productPriceTypeId,"PURCHASE");
    }
    
    
    public static GenericValue getProductPrice(ServletRequest request, String productId, String productPriceTypeId, String productPricePurposeId) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue> productPriceList = FastList.newInstance();
        List<GenericValue> productPriceListFiltered = FastList.newInstance();
        GenericValue productPrice = null;
        if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productPriceTypeId))
        {
            try {
                productPriceList = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", productPriceTypeId,"productPricePurposeId", productPricePurposeId), UtilMisc.toList("-fromDate"));
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

    /**
     *return the parmValue of given parmKey.
     *@param productStoreId 
     *@param pramKey
     *@return String of the pramKey
     */

    public static String getProductStoreParm(String productStoreId, String parmKey) {
        if (UtilValidate.isEmpty(parmKey) || UtilValidate.isEmpty(productStoreId)) {
            return null;
        }
        return getProductStoreParm(DelegatorFactory.getDelegator(null), productStoreId, parmKey);
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
        if (UtilValidate.isEmpty(parmKey) || UtilValidate.isEmpty(productStoreId)) 
        {
            return null;
        }
        String parmValue = null;
        GenericValue xProductStoreParam = null;
        try 
        {
            xProductStoreParam = delegator.findOne("XProductStoreParm", UtilMisc.toMap("productStoreId", productStoreId, "parmKey", parmKey), false);
            if (UtilValidate.isNotEmpty(xProductStoreParam))
            {
                parmValue = xProductStoreParam.getString("parmValue");
                if (UtilValidate.isNotEmpty(parmValue)) 
                {
                	parmValue = parmValue.trim();
                }
            }
        } 
        catch (Exception e) 
        {
            Debug.logError(e, e.getMessage(), module);
        }
        return parmValue;
    }

    public static boolean isProductStoreParmTrue(String parmValue) {
        if (UtilValidate.isEmpty(parmValue)) 
         {
             return false;
         }
         if ("TRUE".equals(parmValue.trim().toUpperCase()))
         {
             return true;
         }
         return false;
     }
    
    public static boolean isProductStoreParmTrue(ServletRequest request,String parmName) 
    {
         return isProductStoreParmTrue(getProductStoreParm(request,parmName));
    }
    
    public static boolean isProductStoreParmFalse(String parmValue) 
    {
        if (UtilValidate.isEmpty(parmValue)) 
        {
             return false;
        }
        if ("FALSE".equals(parmValue.trim().toUpperCase()))
        {
             return true;
        }
        return false;
     }
    
    public static boolean isProductStoreParmFalse(ServletRequest request,String parmName) 
    {
         return isProductStoreParmFalse(getProductStoreParm(request,parmName));
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
    
    public static List<File> getUserContent(String type) 
    {
    	return getUserContent(type, false);
    }
    
    public static List<File> getUserContent(String type, boolean makeNewDirectory) {
        List<File> fileList = new ArrayList<File>();
        Map<String, ?> context = FastMap.newInstance();
        String osafeThemeServerPath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("osafeThemeServer"), context);
        String userContentImagePath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("userContentImagePath"), context);

        String userContentPath = null;
        if(UtilValidate.isNotEmpty(type)) 
        {
           userContentPath = osafeThemeServerPath + userContentImagePath + type+"/";
        } 
        else 
        {
           userContentPath = osafeThemeServerPath + userContentImagePath;
        }
        File userContentDir = new File(userContentPath);
        
        if(!userContentDir.exists() && makeNewDirectory)
        {
        	userContentDir.mkdir();
        }
        
        if(userContentDir.exists())
        {
        	fileList = getFileList(userContentDir, fileList);
    	}
    
        return fileList;
    }
    
    public static List<File> getUserContentDirectories() 
    {
        List<File> fileList = new ArrayList<File>();
        Map<String, ?> context = FastMap.newInstance();
        String osafeThemeServerPath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("osafeThemeServer"), context);
        String userContentImagePath = FlexibleStringExpander.expandString(OSAFE_PROPS.getString("userContentImagePath"), context);

        String userContentPath = null;
        userContentPath = osafeThemeServerPath + userContentImagePath;
        File userContentDir = new File(userContentPath);
        
        fileList = getDirectoryList(userContentDir, fileList);
        return fileList;
    }

    public static List<GenericValue> getCountryList(ServletRequest request) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue> countryList = FastList.newInstance();
        String countryDefault = "";
        String countryDropdownParm = "";
        String countryDropdown = "";
        String countryMulti = "";

        countryDefault = getProductStoreParm(delegator, ProductStoreWorker.getProductStoreId(request), "COUNTRY_DEFAULT");
        if (UtilValidate.isEmpty(countryDefault)) 
        {
            countryDefault = "USA";
        }
        countryDropdownParm =  getProductStoreParm(delegator, ProductStoreWorker.getProductStoreId(request), "COUNTRY_DROPDOWN");
        countryMulti = getProductStoreParm(delegator, ProductStoreWorker.getProductStoreId(request), "COUNTRY_MULTI");
        countryDropdown = countryDropdownParm+","+countryDefault;

        if (UtilValidate.isNotEmpty(countryMulti) && countryMulti.equalsIgnoreCase("true")) 
        {
            countryList = CommonWorkers.getCountryList(delegator);
            if (UtilValidate.isNotEmpty(countryDropdown) && !(countryDropdownParm.equalsIgnoreCase("ALL"))) 
            {
                List<String> countryDropdownList = StringUtil.split(StringUtil.removeSpaces(countryDropdown),",");
                for(GenericValue country: countryList) 
                {
                    String geoId = country.getString("geoId");
                    if (!countryDropdownList.contains(geoId)) 
                    {
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
    
    //retrieve a list of Directories (sub-folders) in a given Directory 
    public static List<File> getDirectoryList(File userContentDir , List<File> fileList) 
    {
        File[] fileArray = userContentDir.listFiles();
        for (File file: fileArray) {
            try 
            {
                if(!file.getName().equals(".svn")) 
                {
                    if(file.isDirectory()) 
                    {
                    	fileList.add(file);
                    }
                }
            } 
            catch (Exception exc) 
            {
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
    public static String showCurrency(String isoCode, Locale locale) 
    {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        if (isoCode != null && isoCode.length() > 1) {
            nf.setCurrency(Currency.getInstance(isoCode));
        }
        return nf.getCurrency().getSymbol(locale);
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

    public static String formatSimpleText(String text) {
        if (text == null) {
            return "";
        }
        text = text.replaceAll("(\r\n|\r|\n|\n\r)", " ");
        text = (text.replace("\"","\\\"")).replace("\'", "\\'");
        return StringUtil.wrapString(text).toString();
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
    public static boolean isDateTime(String dateStr) 
    {
        String entryDateFormat = UtilProperties.getPropertyValue("osafeAdmin.properties", "entry-date-format");
        return isDateTime(dateStr, entryDateFormat);
    }

    public static boolean isDateTime(String dateStr, String format) 
    {
    	if (UtilValidate.isEmpty(dateStr) || UtilValidate.isEmpty(format) ) 
        {
            return false;
        }
        boolean isValid = false;
        
        SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false);
        try 
        {
        	Date date = sdf.parse(dateStr);
            isValid = true;
        } 
        catch (Exception e) 
        {
            isValid = false;
        }
        return isValid;
    }

    public static String checkDateRange(String fromDate, String toDate, String format) {

        if (UtilValidate.isEmpty(format) || !isDateTime(fromDate, format) || !isDateTime(toDate, format)) {
            return "invalidFormat";
        }

        try {
            Timestamp convertedFromDate = (Timestamp) ObjectType.simpleTypeConvert(fromDate, "Timestamp", format, null);
            Timestamp convertedToDate = (Timestamp) ObjectType.simpleTypeConvert(toDate, "Timestamp", format, null);
            if (convertedToDate.before(convertedFromDate)) {
                return "invalidRange";
            }
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return "error";
        }

        return "success";
    }
    
    public static boolean isValidId(String id) 
    {
        if (UtilValidate.isEmpty(id)) 
        {
            return false;
        }
        char[] chars = id.toCharArray();
        for (char c: chars) 
        {
            if ((!Character.isLetterOrDigit(c)) && (c!='-') && (c!='_')) 
            {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidDesc(String desc) 
    {
        if (UtilValidate.isEmpty(desc)) 
        {
            return false;
        }
        char[] chars = desc.toCharArray();
        for (char c: chars) 
        {
            if ((!Character.isLetterOrDigit(c)) && (c!='-') && (c!='_')) 
            {
                if(!(c == ' '))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isValidName(String desc) 
    {
        if (UtilValidate.isEmpty(desc)) 
        {
            return false;
        }
        return isValidName(desc, "-_!@#$%:?,;.&/\"");
    }
    
    public static boolean isValidName(String desc, String allowableCharStr) 
    {
        if (UtilValidate.isEmpty(desc)) 
        {
            return false;
        }
        char[] chars = desc.toCharArray();
        for (char c: chars) 
        {
            if ((!Character.isLetterOrDigit(c)) && (Character.UnicodeBlock.of(c) != Character.UnicodeBlock.LATIN_1_SUPPLEMENT) && !(c == ' ')) 
            {
                if(allowableCharStr.indexOf(c) < 0)
                {
                	return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isValidFeatureFormat(String desc) 
    {
        if (UtilValidate.isEmpty(desc)) 
        {
            return false;
        }
        desc = desc.trim();
        int colonIdx = desc.indexOf(":");
        //must contain colon; cannot be first char or last char
        if (colonIdx == -1 || colonIdx == 0 || colonIdx == (desc.length() - 1))
        {
        	return false;
        }
        
        return true;
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

    public static String formatTelephone(String areaCode, String contactNumber) {
        return formatTelephone(areaCode, contactNumber,null);
    }
    
    //If you update this method also update Util.formatTelephone.
    public static String formatTelephone(String areaCode, String contactNumber, String numberFormat) {
        String sAreaCode="";
        String sContactNumber="";
        String sFullPhone="";
        if (UtilValidate.isNotEmpty(areaCode)) 
        {
            sAreaCode=areaCode;
        }
        if (UtilValidate.isNotEmpty(contactNumber)) 
        {
            sContactNumber=contactNumber;
        }
        sFullPhone = sAreaCode + sContactNumber;
        if(UtilValidate.isNotEmpty(numberFormat) && UtilValidate.isNotEmpty(sFullPhone))
        {
            String sFullPhoneNum = sFullPhone.replaceAll("[^0-9]", "");
            //get count of how many digits in phone number
            int digitsCount =sFullPhoneNum.length();
            //get count of how many pounds in format
            String pounds = numberFormat.replaceAll("[^#]", "");
            int poundsCount = pounds.length();
            
            //if number of digits equal the number of pounds 
            if(digitsCount == poundsCount)
            {
                for(int i=0; i<digitsCount; i++)
                {
                    numberFormat=numberFormat.replaceFirst("[#]", "" + sFullPhoneNum.charAt(i));
                }
                sFullPhone=numberFormat;
            }
            else if(digitsCount < poundsCount)
            {
                for(int i=0; i<digitsCount; i++)
                {
                    numberFormat=numberFormat.replaceFirst("[#]", "" + sFullPhoneNum.charAt(i));
                }
                //remove all extra #'s
                numberFormat=numberFormat.replaceAll("[#]", "");
                sFullPhone=numberFormat;
            }
            else if(digitsCount > poundsCount)
            {
                int i = 0;
                for(i=0; i<poundsCount; i++)
                {
                    numberFormat=numberFormat.replaceFirst("[#]", "" + sFullPhoneNum.charAt(i));
                }
                //add extra numbers to the end
                numberFormat=numberFormat + sFullPhoneNum.substring(i);
                sFullPhone=numberFormat;
            }
        }
        return sFullPhone;
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
    public static void removeDuplicates(List list) 
    {
        HashSet set = new HashSet(list);
        list.clear();
        list.addAll(set);
    }

    public static <K, V> Map <K, V> sortMapByValues(final Map <K, V> mapToSort) 
    {
        if (mapToSort == null) 
        {
            return null;
        }
        try 
        {
            List <Map.Entry <K, V>> entries = new ArrayList <Map.Entry <K, V>> (mapToSort.size());
            entries.addAll(mapToSort.entrySet());  
            Collections.sort(entries, new Comparator <Map.Entry <K, V>> () 
            {
                public int compare(final Map.Entry < K, V> entry1,final Map.Entry < K, V> entry2) 
                {
                    return ((Comparable <V>)entry1.getValue()).compareTo(entry2.getValue());  
                }
            });
            Map <K, V> sortedMap = new LinkedHashMap <K, V> ();
            for (Map.Entry < K, V >  entry : entries) 
            {
                sortedMap.put(entry.getKey(), entry.getValue());  
            }
            return sortedMap;
        } 
        catch (Exception e)
        {
            return mapToSort;
        }
    }

    public static <K, V> Map <K, V> setSequenceMap(final Map <K, V> sequenceMap) 
    {
        return setSequenceMapByMultiple(sequenceMap, null);
    }

    public static <K, V> Map <K, V> setSequenceMapByMultiple(final Map <K, V> sequenceMap, Integer multiple) 
    {
        if (sequenceMap == null) 
        {
            return null;
        }
        try 
        {
            Map <K, Integer> sequenceSortedMap = FastMap.newInstance();
            for (Map.Entry < K, V>  entry : sequenceMap.entrySet()) 
            {
                if (UtilValidate.isNotEmpty(entry.getValue()) && UtilValidate.isInteger(entry.getValue().toString()))
                {
                    sequenceSortedMap.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
                }
                else
                {
                    sequenceSortedMap.put(entry.getKey(), -1);
                }
            }

            sequenceSortedMap = sortMapByValues(sequenceSortedMap);

            Map <K, V> sequenceMapInMultiple = new LinkedHashMap <K, V> ();
            int row = 0;
            for (Map.Entry < K, Integer>  entry : sequenceSortedMap.entrySet()) 
            {
                if (entry.getValue() == -1)
                {
                    sequenceMapInMultiple.put(entry.getKey(), (V)"0");
                } 
                else if (entry.getValue() == 0)
                {
                    sequenceMapInMultiple.put(entry.getKey(), (V)entry.getValue().toString());
                }
                else
                {
                    if (multiple == null)
                    {
                        sequenceMapInMultiple.put(entry.getKey(), (V)entry.getValue().toString());
                    }
                    else
                    {
                        Integer seqValue = (++row)*multiple;
                        sequenceMapInMultiple.put(entry.getKey(), (V)seqValue.toString());
                    }
                }
            }
            return sequenceMapInMultiple;
        } 
        catch (Exception e)
        {
            return sequenceMap;
        }
    }

    public static <K, V> Map <K, V> setSequenceMapByMultiple(final Map <K, V> sequenceMap, final Map <K, V> groupMap, Integer multiple) 
    {
        if (UtilValidate.isEmpty(sequenceMap) || UtilValidate.isEmpty(groupMap)) 
        {
            return sequenceMap;
        }
        try 
        {
        	Map <K, V> sortedGroupMap = setSequenceMap(groupMap);
        	TreeMap <Integer, Map <K, V>> seqGroupMap = new TreeMap <Integer, Map <K, V>>();
            for (Map.Entry < K, V>  entry : sortedGroupMap.entrySet()) 
            {
            	Map <K, V> seqMap =  (Map <K, V>)seqGroupMap.get(Integer.parseInt(entry.getValue().toString()));
                if (UtilValidate.isEmpty(seqMap))
                {
                    seqMap = FastMap.newInstance();
                }
                seqMap.put(entry.getKey(), sequenceMap.get(entry.getKey()));
                seqGroupMap.put(Integer.parseInt(entry.getValue().toString()), seqMap);
            }

            if (UtilValidate.isEmpty(multiple)) 
            {
                multiple = 1;
            }
            int row = 0;
        	Map <K, V> sequenceMapInMultiple = new LinkedHashMap <K, V> ();
            for (Map.Entry <Integer, Map <K, V>> entry : seqGroupMap.entrySet()) 
            {
	            	Map <K, V> seqMap =  (Map <K, V>)entry.getValue();
	                if (UtilValidate.isNotEmpty(seqMap))
	                {
	                	Map <K, V> sortedseqMap = setSequenceMap(seqMap);
		                if (entry.getKey() > 0)
		                {
		                    for (Map.Entry <K, V> seqEentry : sortedseqMap.entrySet()) 
		                    {
		                        Integer seqValue = (++row)*multiple;
		                        sequenceMapInMultiple.put(seqEentry.getKey(), (V)seqValue.toString());
		                    }
	                    }
		                else
		                {
		                	sequenceMapInMultiple.putAll(sortedseqMap);
		                }
                    }
            }

            return sequenceMapInMultiple;
        }
        catch (Exception e)
        {
            return sequenceMap;
        }
    }

    /** Returns the ocurrences of a subStr in a String. */
    public static int countOccurrences(String str, String subStr) 
    {
        int subStrCount = 0;
        if (UtilValidate.isNotEmpty(str) && UtilValidate.isNotEmpty(subStr)) 
        {
            subStrCount = str.length() - str.replaceAll(subStr, "").length();
        }
        return subStrCount;
    }
    
    /** Trims all the trailing white spaces of a String. */
    public static String trimTrailSpaces(String str) 
    {
        String trimmedStr = null;
        if (UtilValidate.isNotEmpty(str))
        {
            int i;  
            for ( i = str.length()-1; i > 0; i--)
            {  
                char c = str.charAt(i);  
                if (c != '\u0020') 
                {  
                    break;
                }
            }
        trimmedStr = str.substring(0, i+1);  
        }
        return trimmedStr;
    }
    
    public static boolean isValidURL(String url) 
    {
    	String httpPattern="^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
        String wwwPattern="^www.[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
        String Pattern = "^[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
        if (UtilValidate.isEmpty(url)) 
        {
            return false;
        }
        return (url.matches(httpPattern) || url.matches(wwwPattern) || url.matches(Pattern));
    }

    public static String checkTelecomNumber(String areaCode, String contactNumber, String required) 
    {
        return checkTelecomNumber(areaCode, contactNumber, null, required);
    }

    public static String checkTelecomNumber(String areaCode, String contactNumber, String extension, String required) 
    {

        if (Boolean.parseBoolean(required) || "Y".equalsIgnoreCase(required)) 
        {
            if (UtilValidate.isEmpty(areaCode) && UtilValidate.isEmpty(contactNumber)) 
            {
                return "missing";
            }
            if (UtilValidate.isEmpty(areaCode) || UtilValidate.isEmpty(contactNumber)) 
            {
                return "invalid";
            }
        }

        if (UtilValidate.isNotEmpty(areaCode)) 
        {
            String justNumbers = StringUtil.removeRegex(areaCode, "[\\s-]");
            if (!UtilValidate.isInteger(justNumbers)) 
            {
                return "invalid";
            } 
            else if (justNumbers.length() < 3) 
            {
                return "invalid";
            }
        }
        if (UtilValidate.isNotEmpty(contactNumber)) 
        {
            String justNumbers = StringUtil.removeRegex(contactNumber, "[\\s-]");
            if (!UtilValidate.isInteger(justNumbers)) 
            {
                return "invalid";
            } 
            else if (justNumbers.length() < 7) 
            {
                return "invalid";
            }
        }
        if (UtilValidate.isNotEmpty(extension)) 
        {
            String justNumbers = StringUtil.removeRegex(extension, "[\\s-]");
            if (!UtilValidate.isInteger(justNumbers)) 
            {
                return "invalid";
            }
        }

        return "success";
    }

    public static String checkDateOfBirth(String day, String month, String year, String format, String required) 
    {
    	
        if(UtilValidate.isEmpty(format))
        {
        	format = "MMDDYYYY";
        }
        if(UtilValidate.isEmpty(required))
        {
        	required = "N";
        }
        
        if(format.equalsIgnoreCase("MMDD") || format.equalsIgnoreCase("DDMM"))
        {
                if (UtilValidate.isEmpty(month) && UtilValidate.isEmpty(day)) 
                {
                	if (Boolean.parseBoolean(required) || "Y".equalsIgnoreCase(required) || "YES".equalsIgnoreCase(required)) 
                	{
                        return "missing";
                    }
                }
                else if(UtilValidate.isEmpty(month) && UtilValidate.isNotEmpty(day))
                {
                    return "invalid";
                }
                else if(UtilValidate.isNotEmpty(month) && UtilValidate.isEmpty(day))
                {
                    return "invalid";
                }
        }
        else if(format.equalsIgnoreCase("MMDDYYYY") || format.equalsIgnoreCase("DDMMYYYY"))
        {
            if (UtilValidate.isEmpty(month) && UtilValidate.isEmpty(day) && UtilValidate.isEmpty(year)) 
            {
            	if (Boolean.parseBoolean(required) || "Y".equalsIgnoreCase(required) || "YES".equalsIgnoreCase(required)) 
            	{
                    return "missing";
                }
            }
            else if(UtilValidate.isNotEmpty(day) && (UtilValidate.isEmpty(month) || UtilValidate.isEmpty(year)))
            {
                return "invalid";
            }
            else if(UtilValidate.isNotEmpty(month) && (UtilValidate.isEmpty(day) || UtilValidate.isEmpty(year)))
            {
                return "invalid";
            }
            else if(UtilValidate.isNotEmpty(year) && (UtilValidate.isEmpty(day) || UtilValidate.isEmpty(month)))
            {
                return "invalid";
            }
        }

    	if (UtilValidate.isNotEmpty(month) && UtilValidate.isNotEmpty(day) && UtilValidate.isNotEmpty(year)) 
    	{
            String dobDateString = month.concat("/").concat(day).concat("/").concat(year);
            if(UtilValidate.isNotEmpty(dobDateString) && !UtilValidate.isDate(dobDateString))
            {
            	return "invalid";
            }
        }

        return "success";
    }

    public static Map<String, Object> getCountryGeoInfo(Delegator delegator, String geoId) 
    {
        GenericValue geo = null;
        Map<String, Object> result = FastMap.newInstance();
        try 
        {
            Debug.logInfo("geoId: " + geoId, module);

            geo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", geoId.toUpperCase()));
            Debug.logInfo("Found a geo entity " + geo, module);
            if (UtilValidate.isNotEmpty(geo)) 
            {
                result.put("geoId", (String) geo.get("geoId"));
                result.put("geoName", (String) geo.get("geoName"));
            }
        } 
        catch (Exception e) 
        {
            String errMsg = "Failed to find/setup geo id";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        return result;
    }

    public static String passPattern(String password, String pwdLenStr, String minDigitStr, String minUpCaseStr) 
    {

        int pwdLength = 6;//Need to confirm this value.
        int minDigit = 0;
        int minUpCase = 0;

        if (isNumber(pwdLenStr)  && (Integer.parseInt(pwdLenStr) > 0)) 
        {
            pwdLength = Integer.parseInt(pwdLenStr);
        }
        if (isNumber(minDigitStr)) 
        {
            minDigit = Integer.parseInt(minDigitStr);
        }
        if (isNumber(minUpCaseStr)) 
        {
            minUpCase = Integer.parseInt(minUpCaseStr);
        }
        return passPattern(password, pwdLength, minDigit, minUpCase);
    }

    public static String passPattern(String password, int passwordLength, int minDigit, int minUpperCase)
    {
        if (passwordLength > 0) 
        {
            String digitMsgStr = "digits";
            String upperCaseMsgStr = "letters";
        String errormessage = UtilProperties.getMessage("OSafeUiLabels", "PasswordMinLengthError", UtilMisc.toMap("passwordLength", passwordLength), Locale.getDefault());
        if (minDigit > 0) 
        {
            if (minDigit == 1) 
            {
                digitMsgStr = "digit";
            }
            errormessage = errormessage+" "+UtilProperties.getMessage("OSafeUiLabels", "PasswordDigitError", UtilMisc.toMap("minDigit", (Integer)minDigit, "digitMsgStr", digitMsgStr), Locale.getDefault());
        }
        
        if (minUpperCase == 1) 
        {
            upperCaseMsgStr = "letter";
        }
        if (minDigit > 0 && minUpperCase > 0) 
        {
            errormessage = errormessage+" and "+UtilProperties.getMessage("OSafeUiLabels", "PasswordUpperCaseError", UtilMisc.toMap("minUpperCase", (Integer) minUpperCase, "upperCaseMsgStr", upperCaseMsgStr), Locale.getDefault());
        } 
        else if (minDigit == 0 && minUpperCase > 0) 
        {
            errormessage = errormessage+" "+UtilProperties.getMessage("OSafeUiLabels", "PasswordWithNoDigitUpperCaseError", UtilMisc.toMap("minUpperCase", (Integer) minUpperCase, "upperCaseMsgStr", upperCaseMsgStr), Locale.getDefault());
        }
        
        if (!(password.length() >= passwordLength)) 
        {
            return errormessage;
        } 
        else 
        {
            char[] passwordChars = password.toCharArray();
            int digitCount = 0;
            int upperCount = 0;
            for (char passwordChar: passwordChars) 
            {
                if (Character.isDigit(passwordChar)) 
                {
                    digitCount = digitCount + 1;
                } 
                else if (Character.isUpperCase(passwordChar)) 
                {
                    upperCount = upperCount + 1;
                }
            }
            if (!(digitCount >= minDigit) || !(upperCount >= minUpperCase)) 
            {
                return errormessage;
            }
        }
        }
        return "success";
    }

    public static String stripHTML(String content) 
    {
        return stripHTML(content,800);
    }

    public static String stripHTML(String content,int wrapLen) 
    {
        
        if (content == null) 
        {
            return "";
        }
        String cleanContent = content;//StringUtil.wrapString(content).toString();
        Tidy tidy = new Tidy();
        InputStream inStream = null;
        try 
        {
            cleanContent = filterNonAscii(cleanContent);
            inStream = new ByteArrayInputStream(cleanContent.getBytes("UTF-8"));

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            if (UtilValidate.isNotEmpty(inStream)) 
            {

                PrintWriter pw = new PrintWriter(new StringWriter());
                tidy.setWraplen(wrapLen);
                tidy.setErrout(pw);
                tidy.setShowWarnings(false);
                tidy.setMakeClean(true);
                tidy.parse(inStream, outStream);
                if (outStream != null) 
                {
                    cleanContent = outStream.toString("UTF-8");
                    cleanContent = cleanContent.replaceAll("\\<.*?>", "");
                    String[] split = StringUtils.split(cleanContent, "\n\r");
                    cleanContent  = StringUtils.join(split," ");
                }

            }
        } 
        catch (UnsupportedEncodingException e) 
        {
            Debug.logError(e, e.getMessage(), module);
        }
        return cleanContent;
    }

    public static String stripHTMLInLength(String content) 
    {
        return stripHTMLInLength(content, "800");
    }

    public static String stripHTMLInLength(String content, String length)
   {
        int maxLength = 800;
        if (isNumber(length)) 
        {
            maxLength = Integer.parseInt(length);
        }
        String stripHTMLStr = stripHTML(content);
        return getStringInLength(stripHTMLStr, maxLength);
    }
    
    /** String with in the given limit
     * @param String that need to refactor
     * @param Int length
     * @return A String with in the given limit
     */
    public static String getStringInLength(String str, int length) 
    {
        String strInLength = null;
        if (UtilValidate.isNotEmpty(str))
        { 
            int leng = length - 1;
            if (str.length() > leng)
            {
                if (str.charAt(leng) == ' ')
                {
                    strInLength = str.substring(0, leng);
                }
                else
                {
                    String strWithHalfWord = str.substring(0, leng);
                    try 
                    {
                        strInLength = strWithHalfWord.substring(0, strWithHalfWord.lastIndexOf(" "));
                    }
                    catch (Exception e)
                    {
                        strInLength = strWithHalfWord;
                        Debug.logError(e, e.getMessage(), module);
                    }
                }
            }
            else
            {
                strInLength = str;
            }
        }
        return strInLength;
    }

    public static String htmlSpecialChars(String html) 
    {
        html = StringUtil.replaceString(html, "&", "&amp;");
        html = StringUtil.replaceString(html, "<", "&lt;");
        html = StringUtil.replaceString(html, ">", "&gt;");
        html = StringUtil.replaceString(html, "\"", "&quot;");
        html = StringUtil.replaceString(html, "'", "&#039");
        html = StringUtil.replaceString(html, "\n", "<br>");

        return html;
    }
    // Method to reduce currenttimeStamp by 1 month.
    public static Timestamp getMonthBackTimeStamp(int monthsBack) 
    {
        Timestamp retStamp = null;
        try 
        {
            Calendar tempCal = UtilDateTime.toCalendar(UtilDateTime.nowTimestamp()); 
            tempCal.add(Calendar.MONTH, -monthsBack);
            retStamp = new Timestamp(tempCal.getTimeInMillis());
            retStamp.setNanos(0);
        }
        catch (Exception e)
        {
            Debug.logError(e, e.getMessage(), module);
        }
        return retStamp;
    }

    /*Returns the value 0 if the first string is equal to second string; 
      a value less than 0 if first string is alphanumerically less than the second string ; 
      and a value greater than 0 if first string is alphanumerically greater than the second string. */
    public static int alphaNumericSort(String firstStrToCompare, String secondStrToCompare) 
    {
        String firstString = firstStrToCompare;
        String secondString = secondStrToCompare;
 
        if (UtilValidate.isEmpty(secondString) || UtilValidate.isEmpty(firstString)) 
        {
            return 0;
        }

        int lengthFirstStr = firstString.length();
        int lengthSecondStr = secondString.length();
 
        int index1 = 0;
        int index2 = 0;

        while (index1 < lengthFirstStr && index2 < lengthSecondStr) 
        {
            char ch1 = firstString.charAt(index1);
            char ch2 = secondString.charAt(index2);
 
            char[] space1 = new char[lengthFirstStr];
            char[] space2 = new char[lengthSecondStr];
 
            int loc1 = 0;
            int loc2 = 0;
            
            //Create Two Character Sequence 'space1' and 'space2' of Same Type either Alphabetic or Numeric
            space1[0] = ch1;
            while(index1 < lengthFirstStr)
            {
            	if(Character.isDigit(ch1) == Character.isDigit(space1[0]))
            	{
            		space1[loc1++] = ch1;
            		index1++;
            		if(index1 < lengthFirstStr)
            		{
                	    ch1 = firstString.charAt(index1);
            		}
            	}
            	else
            	{
            		break;
            	}
            }
            space2[0] = ch2;
            while(index2 < lengthSecondStr)
            {
            	if(Character.isDigit(ch2) == Character.isDigit(space2[0]))
            	{
            		space2[loc2++] = ch2;
            		index2++;
            		if(index2 < lengthSecondStr)
            		{
                	    ch2 = secondString.charAt(index2);
            		}
            	}
            	else
            	{
            		break;
            	}
            }
 
            //Build Two Strings 'str1' and 'str2' of Same Type either Alphabetic or Numeric from Character Sequence
            String str1 = new String(space1);
            String str2 = new String(space2);
 
            int result;
 
            //If both Character Sequences starts with numeric value then convert the same type of String to the Integer value and then Compare
            if (Character.isDigit(space1[0]) && Character.isDigit(space2[0])) 
            {
                Integer firstNumberToCompare = new Integer(Integer.parseInt(str1.trim()));
                Integer secondNumberToCompare = new Integer(Integer.parseInt(str2.trim()));
                result = firstNumberToCompare.compareTo(secondNumberToCompare);
            }
            else 
            {
            	//Compare the Same Type of String 'str1' and 'str2'
                result = str1.trim().compareTo(str2.trim());
            }
            /*If both Same type of String 'str1' and 'str2' are equal then continue to the outer loop with the next index position.
            else return the positive or negative integer value */
            if (result != 0) 
            {
                return result;
            }
        }
        return lengthFirstStr - lengthSecondStr;
    }

    /**
     *returns the values in sorted by column
     *
     *@param numberOfCols number of columns to display in generated HTML
     *@param sortList list of GenericValue to sort
     *@param sortByCol name of sorting column
     *@return List of sorted values
     */
    public static List<Map<Object, Object>> sortInColumns(int numberOfCols, List<GenericValue> sortList, String sortByCol) 
    {
        if (UtilValidate.isEmpty(numberOfCols) || UtilValidate.isEmpty(sortList) || UtilValidate.isEmpty(sortByCol)) 
        {
            return null;
        }
        List<Map<Object, Object>> result = FastList.newInstance();
        try 
        {
            // First sort the list base on sorting column
            for (GenericValue value: sortList)
            {
            	Map<String, Object> valueMap = value.getAllFields();
                if (UtilValidate.isNotEmpty(valueMap.get(sortByCol)) && UtilValidate.isInteger(valueMap.get(sortByCol).toString()))
                {
                    valueMap.put(sortByCol, Integer.parseInt(valueMap.get(sortByCol).toString()));
                }
                result.add(UtilGenerics.<Object, Object>checkMap(valueMap));
            }
            result = UtilMisc.sortMaps(result, UtilMisc.toList(sortByCol));

            // Now Add the logic for Algorithm
        	String newSortingCol = sortByCol+"_new";
            int markUpOrder = 1;
            int count = markUpOrder;
            for (Map<Object, Object> valueMap: result)
            {
                valueMap.put(newSortingCol, markUpOrder);
                markUpOrder += numberOfCols;
                if (markUpOrder > result.size())
                {
                	markUpOrder = ++count;
                }
            }
            result = UtilMisc.sortMaps(result, UtilMisc.toList(newSortingCol));
        }
        catch (Exception e)
        {
            Debug.logError(e, e.getMessage(), module);
        }
        return result;
    }

    public static String getNextSeqId(Delegator delegator, String seqName, String entityName, String entityPK)
    {
    	String seqId = delegator.getNextSeqId(seqName);
        // check that id is not already exist
        try 
        {
    	    GenericValue gv = delegator.findByPrimaryKey(entityName, UtilMisc.toMap(entityPK, seqId));
    	    if (UtilValidate.isEmpty(gv))
    	    {
    	        return seqId;
    	    }
    	    else
    	    {
    	    	seqId = getNextSeqId(delegator, seqName, entityName, entityPK);
    	    }
        }
        catch (Exception e)
        {
            Debug.logError(e, e.getMessage(), module);
        }
        return seqId;
    }
    
    //convert Captions to Labels
    public static String stripTrailingColon(String caption)
    {
    	String label = caption;
    	if(UtilValidate.isEmpty(caption))
    	{
    		return "";
    	}
    	else
    	{
    		int colonIndex = caption.indexOf(":");
    		int captionLength = caption.length();
    		if(colonIndex == (captionLength - 1))
    		{
    			label = caption.substring(0, captionLength - 1);
    			return label;
    		}
    	}
    	return label;
    }
     
    public static String checkEvenNumber(int number) 
    {
    	if(number % 2 == 0)
    	{
    		return "even";
    	}
    	else
    	{
    		return "odd";
    	}
    }

    /**
     * find product SEO url according to the configurations.
     * 
     * @return String a product url
     */
    public static String findProductDetailSeoUrl(String productId, String productCategoryId)
    {
        StringBuilder newURL = new StringBuilder();
        newURL.append("eCommerceProductDetail");
        newURL.append("?");
        newURL.append("productId");
        newURL.append("=");
        newURL.append(productId);
        newURL.append("&");
        newURL.append("productCategoryId");
        newURL.append("=");
        newURL.append(productCategoryId);
        return findFriendlyUrl(newURL.toString());
    }

    public static String findFriendlyUrl(String URL)
    {
        StringBuilder urlBuilder = new StringBuilder();
        String solrURLParam=null;
        String origURL=URL;
        try
        {
            urlBuilder.setLength(0);
            //Check URL for SOLR
            int solrIdx = origURL.indexOf("&filterGroup");
            if (solrIdx > -1)
            {
                solrURLParam = origURL.substring(solrIdx+1);
                origURL = origURL.substring(0,solrIdx);
            }
            
            ResourceBundleMapWrapper OSAFE_FRIENDLY_URL = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeSeoUrlMap", Locale.getDefault());            
            String friendlyKey=StringUtil.replaceString(origURL,"&","~");
            friendlyKey=StringUtil.replaceString(friendlyKey,"=","^^");
            if (OSAFE_FRIENDLY_URL.containsKey(friendlyKey))
            {
                String friendlyUrl =(String)OSAFE_FRIENDLY_URL.get(friendlyKey);
                urlBuilder.append(friendlyUrl);
                if (solrIdx > -1)
                {
                    urlBuilder.append("?" + solrURLParam);
                    
                }
            }
            else
            {
                urlBuilder.append(URL);
            }

        }
        catch (Exception e)
        {
             //Debug.log(e, "Friendly URL not found for: " + URL, module);
        }
        return urlBuilder.toString();
    }
    
    public static boolean entityExists(Delegator delegator, String searchEntityName)
    {
    	if(UtilValidate.isEmpty(delegator) || UtilValidate.isEmpty(searchEntityName))
    	{
    		return false;
    	}
    	String delegatorName = delegator.getDelegatorName();
    	try
    	{
	    	ModelReader modelReader = ModelReader.getModelReader(delegatorName);
	    	if(UtilValidate.isNotEmpty(modelReader))
	    	{
	    		ModelEntity modelEntity = modelReader.getModelEntityNoCheck(searchEntityName);
	    		if(UtilValidate.isEmpty(modelEntity))
	        	{
	    			return false;
	        	}
	    	}
    	}
    	catch(GenericEntityException e)
    	{
    		String errorMessage = "Exception during searching model for entity: " + e.getMessage();
            Debug.logError(e, errorMessage, module);
            return false;
    	}
    	return true;
    }
    
}
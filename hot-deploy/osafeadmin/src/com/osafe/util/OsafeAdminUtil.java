package com.osafe.util;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
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
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.CommonWorkers;
import com.ibm.icu.util.Calendar;
import com.osafe.services.OsafeManageXml;

import java.util.*;

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
    	        productPriceList = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", productPriceTypeId), UtilMisc.toList("-fromDate"));
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
    
    public static boolean isProductStoreParmTrue(ServletRequest request,String parmName) {
         return isProductStoreParmTrue(getProductStoreParm(request,parmName));
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
    public static void removeDuplicates(List list) {
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
        } catch (Exception e){
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

            Map <K, V> sequenceMapInMultiple = FastMap.newInstance();
            int row = 0;
            for (Map.Entry < K, Integer>  entry : sequenceSortedMap.entrySet()) 
            {
                if (entry.getValue() == -1)
                {
                    sequenceMapInMultiple.put(entry.getKey(), (V)"");
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
        } catch (Exception e){
            return sequenceMap;
        }
    }
    /** Returns the ocurrences of a subStr in a String. */
    public static int countOccurrences(String str, String subStr) {
        int subStrCount = 0;
        if (UtilValidate.isNotEmpty(str) && UtilValidate.isNotEmpty(subStr)) 
        {
            subStrCount = str.length() - str.replaceAll(subStr, "").length();
        }
        return subStrCount;
    }
    /** Trims all the trailing white spaces of a String. */
    public static String trimTrailSpaces(String str) {
    	String trimmedStr = null;
    	if (UtilValidate.isNotEmpty(str)){
            int i;  
            for ( i = str.length()-1; i > 0; i--){  
                char c = str.charAt(i);  
                if (c != '\u0020') {  
                    break;
                }
            }
        trimmedStr = str.substring(0, i+1);  
        }
    	return trimmedStr;
    }
    
    public static boolean isValidURL(String url) 
    {
        if (UtilValidate.isEmpty(url)) {
            return false;
        }
        if (url.indexOf("//") != -1)
            return true;
        return false;
    }

    public static String checkTelecomNumber(String areaCode, String contactNumber, String required) {
        return checkTelecomNumber(areaCode, contactNumber, null, required);
    }

    public static String checkTelecomNumber(String areaCode, String contactNumber, String extension, String required) {

        if (Boolean.parseBoolean(required) || "Y".equalsIgnoreCase(required)) {
            if (UtilValidate.isEmpty(areaCode) && UtilValidate.isEmpty(contactNumber)) {
                return "missing";
            }
            if (UtilValidate.isEmpty(areaCode) || UtilValidate.isEmpty(contactNumber)) {
                return "invalid";
            }
        }

        if (UtilValidate.isNotEmpty(areaCode)) {
            String justNumbers = StringUtil.removeRegex(areaCode, "[\\s-]");
            if (!UtilValidate.isInteger(justNumbers)) {
                return "invalid";
            } else if (justNumbers.length() < 3) {
                return "invalid";
            }

        }
        if (UtilValidate.isNotEmpty(contactNumber)) {
            String justNumbers = StringUtil.removeRegex(contactNumber, "[\\s-]");
            if (!UtilValidate.isInteger(justNumbers)) {
                return "invalid";
            } else if (justNumbers.length() < 7) {
                return "invalid";
            }
        }
        if (UtilValidate.isNotEmpty(extension)) {
            String justNumbers = StringUtil.removeRegex(extension, "[\\s-]");
            if (!UtilValidate.isInteger(justNumbers)) {
                return "invalid";
            }
        }

        return "success";
    }

    public static Map<String, Object> getCountryGeoInfo(Delegator delegator, String geoId) {
        GenericValue geo = null;
        Map<String, Object> result = FastMap.newInstance();
        try {
            Debug.logInfo("geoId: " + geoId, module);

            geo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", geoId.toUpperCase()));
            Debug.logInfo("Found a geo entity " + geo, module);
            if (UtilValidate.isNotEmpty(geo)) 
            {
                result.put("geoId", (String) geo.get("geoId"));
                result.put("geoName", (String) geo.get("geoName"));
            }
        } catch (Exception e) {
            String errMsg = "Failed to find/setup geo id";
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        return result;
    }

    public static String passPattern(String password, String pwdLenStr, String minDigitStr, String minUpCaseStr) {

        int pwdLength = 6;//Need to confirm this value.
        int minDigit = 0;
        int minUpCase = 0;

        if (isNumber(pwdLenStr)  && (Integer.parseInt(pwdLenStr) > 0)) {
            pwdLength = Integer.parseInt(pwdLenStr);
       }
       if (isNumber(minDigitStr)) {
           minDigit = Integer.parseInt(minDigitStr);
       }
       if (isNumber(minUpCaseStr)) {
           minUpCase = Integer.parseInt(minUpCaseStr);
       }

       return passPattern(password, pwdLength, minDigit, minUpCase);
    
    }

    public static String passPattern(String password, int passwordLength, int minDigit, int minUpperCase) {
        if (passwordLength > 0) {
            String digitMsgStr = "digits";
            String upperCaseMsgStr = "letters";
        String errormessage = UtilProperties.getMessage("OSafeUiLabels", "PasswordMinLengthError", UtilMisc.toMap("passwordLength", passwordLength), Locale.getDefault());
        if (minDigit > 0) {
            if (minDigit == 1) {
                digitMsgStr = "digit";
                
            }
            errormessage = errormessage+" "+UtilProperties.getMessage("OSafeUiLabels", "PasswordDigitError", UtilMisc.toMap("minDigit", (Integer)minDigit, "digitMsgStr", digitMsgStr), Locale.getDefault());
        }
        
        if (minUpperCase == 1) {
            upperCaseMsgStr = "letter";
        }
        if (minDigit > 0 && minUpperCase > 0) {
            errormessage = errormessage+" and "+UtilProperties.getMessage("OSafeUiLabels", "PasswordUpperCaseError", UtilMisc.toMap("minUpperCase", (Integer) minUpperCase, "upperCaseMsgStr", upperCaseMsgStr), Locale.getDefault());
        } else if (minDigit == 0 && minUpperCase > 0) {
            errormessage = errormessage+" "+UtilProperties.getMessage("OSafeUiLabels", "PasswordWithNoDigitUpperCaseError", UtilMisc.toMap("minUpperCase", (Integer) minUpperCase, "upperCaseMsgStr", upperCaseMsgStr), Locale.getDefault());
        }
        
        if (!(password.length() >= passwordLength)) {
            return errormessage;
        } else {
            char[] passwordChars = password.toCharArray();
            int digitCount = 0;
            int upperCount = 0;
            for (char passwordChar: passwordChars) {
                if (Character.isDigit(passwordChar)) {
                    digitCount = digitCount + 1;
                } else if (Character.isUpperCase(passwordChar)) {
                    upperCount = upperCount + 1;
                }
            }
            if (!(digitCount >= minDigit) || !(upperCount >= minUpperCase)) {
                return errormessage;
            }
        }
        }
        return "success";
    }
    
    public static String htmlSpecialChars(String html) {
        html = StringUtil.replaceString(html, "&", "&amp;");
        html = StringUtil.replaceString(html, "<", "&lt;");
        html = StringUtil.replaceString(html, ">", "&gt;");
        html = StringUtil.replaceString(html, "\"", "&quot;");
        html = StringUtil.replaceString(html, "'", "&#039");
        html = StringUtil.replaceString(html, "\n", "<br>");

        return html;
    }
}
package com.osafe.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ritu Raj Lakhera
 * 
 */

public class OsafeManageMerge {

    public static final String delimiter = ",";
    public static final String keyAtrrName = "key";
    public static final String readerAtrrName = "reader-name";
    public static final String serviceResourceNodeName = "service-resource";
    public static final String testSuiteNodeName = "test-suite";
    public static final String keyStoreNodeName = "keystore";
    public static final String webappNodeName = "webapp";
    public static final String divNodeName = "div";
    public static final String screenNodeName = "screen";
    public static final String excludeKeyForUpdate = "style,value,group,mandatory";
    public static final String includeValueForUpdate = "SYS_YES,SYS_NO,NA";
    public static Map<String, String> screenNamePrefixMap = new HashMap<String, String>();
    static
    {
        screenNamePrefixMap.put("manufacturerProductList", "manufacturerItem");
        screenNamePrefixMap.put("showCartOrderItems", "showCart");
        screenNamePrefixMap.put("orderSummaryOrderItems", "orderSummary");
        screenNamePrefixMap.put("orderConfirmOrderItems", "orderConfirm");
        screenNamePrefixMap.put("showCartOrderItemsSummary", "showCart");
        screenNamePrefixMap.put("orderSummaryOrderItemsSummary", "orderSummary");
        screenNamePrefixMap.put("orderConfirmOrderItemsSummary", "orderConfirm");
        screenNamePrefixMap.put("showWishlistOrderItems", "showWishlist");
        screenNamePrefixMap.put("showWishlistOrderItemsSummary", "showWishlist");
        screenNamePrefixMap.put("lightBoxOrderItems", "lightBox");
        screenNamePrefixMap.put("lightBoxOrderItemsSummary", "lightBox");
        screenNamePrefixMap.put("writeReviewProduct", "writeReview");
        screenNamePrefixMap.put("writeReviewRating", "writeReviewRate");
        screenNamePrefixMap.put("writeReviewDetail", "writeReview");
        screenNamePrefixMap.put("writeReviewAboutYou", "writeReview");
        screenNamePrefixMap.put("writeReviewLink", "writeReview");
        screenNamePrefixMap.put("writeReviewButton", "writeReview");
    }


    public static void main(String args[]) throws Exception
    {
        if (args.length == 2 || args.length == 3 || args.length == 5)
        {
            if (args.length == 2)
            {
                createToFile(args[0], args[1]);
            }
            if (args.length == 3)
            {
                customizeMergeXmlFile(args[0], args[1], args[2]);
            }
            if (args.length == 5)
            {
                if ("XML".equalsIgnoreCase(args[3]))
                {
                    mergeXmlFile(args[0], args[1], args[2], args[4]);
                }
                else if ("PROPERTIES".equalsIgnoreCase(args[3]))
                {
                    mergePropertiesFile(args[0], args[1], args[2], args[4]);
                }
            }
        }
        else
        {
            System.out.println("Please include the paths for merge file.");
        }
    }

    public static void customizeMergeXmlFile(String fromXmlFilePath, String toXmlFilePath, String readerNames)
    {
        try
        {
            if (isEmpty(fromXmlFilePath) || isEmpty(toXmlFilePath) || isEmpty(readerNames))
            {
                return;
            }

            // chaeck xml existance on instance; if its not exist then copy the from file on that location
            if (!(new File(toXmlFilePath)).exists())
            {
                FileUtils.copyFile(new File(fromXmlFilePath), new File(toXmlFilePath));
                return;
            }

            List<Map<Object, Object>> fromEntryList = getListMapsFromXmlFile(fromXmlFilePath, null);
            List<Map<Object, Object>> toEntryList = getListMapsFromXmlFile(toXmlFilePath, null);
            String[] readerList = StringUtils.split(readerNames, delimiter);

            Document fromXmlDocument = readXmlDocument(fromXmlFilePath);
            Document toXmlDocument = readXmlDocument(toXmlFilePath);

            /* Logic
            Step 1: First update instance file XML document(toXmlDocument) with newly added version file node in respect of reader name
            Step 2: Write the modified instance file XML document object to instance file location */

            // Step 1 begin
            if (isNotEmpty(fromXmlDocument))
            {
                Iterator<Map<Object, Object>> fromEntryListIter = fromEntryList.iterator();
                while (fromEntryListIter.hasNext())
                {
                    try
                    {
                        Map<Object, Object> fromMapEntry = fromEntryListIter.next();
                        if (isNotEmpty(fromMapEntry.get(readerAtrrName)))
                        {
                            for (String reader : readerList)
                            {
                                if (reader.equals(fromMapEntry.get(readerAtrrName).toString()))
                                {
                                    // check the existence in instance file
                                    Map<Object, Object> toMapEntry = findFromListMaps(toEntryList, fromMapEntry);
                                    if (isEmpty(toMapEntry))
                                    {
                                        List<? extends Node> nodeList = UtilXml.childNodeList(fromXmlDocument.getDocumentElement().getFirstChild());
                                        for (Node node: nodeList)
                                        {
                                            // Get the node from version file
                                            Boolean found = Boolean.FALSE;
                                            if (node.getNodeType() == Node.ELEMENT_NODE) 
                                            {
                                                if (isMatch(getAllNameValueMap(node), fromMapEntry))
                                                {
                                                    found = Boolean.TRUE;
                                                }
                                            }
                                            if (found)
                                            {
                                                List<? extends Node> tonodeList = UtilXml.childNodeList(toXmlDocument.getDocumentElement().getFirstChild());
                                                for (Node tonode: tonodeList)
                                                {
                                                    // Add the node in instance file
                                                    Boolean tofound = Boolean.FALSE;
                                                    if (tonode.getNodeType() == Node.ELEMENT_NODE) 
                                                    {
                                                        if (tonode.getNodeName().equals(serviceResourceNodeName) || tonode.getNodeName().equals(testSuiteNodeName) || tonode.getNodeName().equals(keyStoreNodeName) || tonode.getNodeName().equals(webappNodeName))
                                                        {
                                                            tofound = Boolean.TRUE;
                                                        }
                                                    }
                                                    if (tofound)
                                                    {
                                                        Node importNode = toXmlDocument.importNode(node, true);
                                                        toXmlDocument.getDocumentElement().insertBefore(importNode, tonode);
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception exc)
                    {
                        System.err.println("Error in runRequisite=="+exc.getMessage());
                    }
                }
            }
            // Step 1 End
            // Step 2 begin
            writeXmlDocument(toXmlDocument, toXmlFilePath);
            // Step 2 End
        }
        catch (Exception exc)
        {
            System.err.println("Error in runRequisite=="+exc.getMessage());
        }
    
    }

    public static void createToFile(String fromFilePath, String toFilePath)
    {
        File fromFile = new File(fromFilePath);
        File toFile = new File(toFilePath);
        if (!(toFile.exists()))
        {
            try
            {
                FileUtils.copyFile(fromFile, toFile);
                System.out.println("createToFile=="+fromFilePath+"=="+toFilePath);
            }
            catch (Exception exc)
            {
                System.err.println("Error in createToFile=="+exc.getMessage());
            }
        }
    }

    public static void mergePropertiesFile(String fromPropertiesFilePath, String toPropertiesFilePath, String keyValues, String preserveExtra)
    {
        try
        {
            if (isEmpty(fromPropertiesFilePath) || isEmpty(toPropertiesFilePath))
            {
                return;
            }

            File fromPropertiesFile = new File(fromPropertiesFilePath);
            File toPropertiesFile = new File(toPropertiesFilePath);
            // chaeck Properties existance on instance; if its not exist then copy the from file on that location
            if (!(toPropertiesFile.exists()))
            {
                FileUtils.copyFile(fromPropertiesFile, toPropertiesFile);
                return;
            }

            PropertiesConfiguration fromFileProperties = new PropertiesConfiguration();
            fromFileProperties.setDelimiterParsingDisabled(true);
            fromFileProperties.load(fromPropertiesFile);
            PropertiesConfiguration toFileProperties = new PropertiesConfiguration();
            toFileProperties.setDelimiterParsingDisabled(true);
            toFileProperties.load(toPropertiesFile);

            /* Logic
            Step 1: First update version file PROPERTIES document(fromFileProperties) with instance file values and remove property from instance PROPERTIES document(toFileProperties)
            Step 2: If preserveExtra is set to "true", import all remaining property from instance PROPERTIES document to version file PROPERTIES document.
            Step 3: Write the modified version file PROPERTIES document object to instance file location */

            // Step 1 begin
            if (isNotEmpty(fromFileProperties))
            {
                Iterator<String> fromPropertyListIter = fromFileProperties.getKeys();
                Iterator<String> toPropertyListIter = toFileProperties.getKeys();
                while (fromPropertyListIter.hasNext())
                {
                    try
                    {
                        String fromProperty = fromPropertyListIter.next();
                        Boolean hasToProperty = Boolean.FALSE;
                        while (toPropertyListIter.hasNext())
                        {
                            if (toPropertyListIter.next().equals(fromProperty))
                            {
                                hasToProperty = Boolean.TRUE;
                                break;
                            }
                        }
                        if (hasToProperty)
                        {
                            String toProperty = toFileProperties.getProperty(fromProperty).toString();
                            fromFileProperties.setProperty(fromProperty, toProperty);
                            toFileProperties.clearProperty(fromProperty);
                        }
                    }
                    catch (Exception exc)
                    {
                        System.err.println("Error in mergeing=="+exc.getMessage());
                    }
                }
            }
            // Step 1 End
            // Step 2 begin
            if (isNotEmpty(toFileProperties) && UtilValidate.isNotEmpty(preserveExtra) && preserveExtra.equalsIgnoreCase("true"))
            {
                Iterator<String> toPropertyListIter = toFileProperties.getKeys();
                while (toPropertyListIter.hasNext())
                {
                    try
                    {
                        String toProperty = toPropertyListIter.next();
                        fromFileProperties.addProperty(toProperty, toFileProperties.getProperty(toProperty));
                    }
                    catch (Exception exc)
                    {
                        System.err.println("Error in mergeing=="+exc.getMessage());
                    }
                }
            }
            // Step 2 End
            // Step 3 begin
            fromFileProperties.save(fromFilename(toPropertiesFilePath));
            String fileEncoding = System.getProperty("file.encoding");
            String fileToString = FileUtils.readFileToString(toPropertiesFile, fileEncoding);
            fileToString = fileToString.replaceAll("\\\\/", "/");
            FileUtils.writeStringToFile(toPropertiesFile, fileToString, fileEncoding);
            // Step 3 End
        }
        catch (Exception exc)
        {
            System.err.println("Error in mergeing=="+exc.getMessage());
        }
    }

    public static void mergeXmlFile(String fromXmlFilePath, String toXmlFilePath, String keyValues, String preserveExtra)
    {
        try
        {
            if (isEmpty(fromXmlFilePath) || isEmpty(toXmlFilePath) || isEmpty(keyValues))
            {
                return;
            }

            // chaeck xml existance on instance; if its not exist then copy the from file on that location
            if (!(new File(toXmlFilePath)).exists())
            {
                FileUtils.copyFile(new File(fromXmlFilePath), new File(toXmlFilePath));
                return;
            }

            List<Map<Object, Object>> fromEntryList = getListMapsFromXmlFile(fromXmlFilePath, null);
            List<Map<Object, Object>> toEntryList = getListMapsFromXmlFile(toXmlFilePath, null);
            String[] keyList = StringUtils.split(keyValues, delimiter);
            List<String> excludeKeyList = UtilMisc.toListArray(StringUtils.split(excludeKeyForUpdate, delimiter));
            List<String> includeValueList = UtilMisc.toListArray(StringUtils.split(includeValueForUpdate, delimiter));

            Document fromXmlDocument = readXmlDocument(fromXmlFilePath);
            Document toXmlDocument = readXmlDocument(toXmlFilePath);

            /* Logic
            Step 1: First update version file XML document(fromXmlDocument) with instance file values and remove node from instance XML document(toXmlDocument)
            Step 2: If preserveExtra is set to "true", import all remaining node from instance XML document to version file XML document.
            Step 3: Write the modified version file XML document object to instance file location */

            // Step 1 begin
            if (isNotEmpty(fromXmlDocument))
            {
                Iterator<Map<Object, Object>> fromEntryListIter = fromEntryList.iterator();
                while (fromEntryListIter.hasNext())
                {
                    try
                    {
                        Map<Object, Object> fromMapEntry = fromEntryListIter.next();
                        Map<Object, Object> searchMap = FastMap.newInstance();
                        for (String key : keyList)
                        {
                            searchMap.put(key, fromMapEntry.get(key));
                        }
                        Map<Object, Object> toMapEntry = findFromListMaps(toEntryList, searchMap);

                        // Logic for support old sequence format
                        if (isEmpty(toMapEntry) && isNotEmpty(fromMapEntry.get(screenNodeName)))
                        {
                            //Case 1: IF DIV is replaced by new key
                            searchMap = FastMap.newInstance();
                            searchMap.put(divNodeName, fromMapEntry.get(keyAtrrName));
                            toMapEntry = findFromListMaps(toEntryList, searchMap);

                            if (isEmpty(toMapEntry))
                            {
                                String screenValue = fromMapEntry.get(screenNodeName).toString();
                                screenValue = Character.toLowerCase(screenValue.charAt(0)) + (screenValue.length() > 1 ? screenValue.substring(1) : "");
                                if (isNotEmpty(screenNamePrefixMap.get(screenValue)))
                                {
                                    String oldDivName = fromMapEntry.get(keyAtrrName).toString().replaceFirst(screenValue, screenNamePrefixMap.get(screenValue));

                                    //Case 2: IF DIV is different from new key
                                    searchMap = FastMap.newInstance();
                                    searchMap.put(divNodeName, oldDivName);
                                    toMapEntry = findFromListMaps(toEntryList, searchMap);

                                    if (isEmpty(toMapEntry))
                                    {
                                        //Case 3: IF KEY is different from new key
                                        searchMap = FastMap.newInstance();
                                        searchMap.put(keyAtrrName, oldDivName);
                                        toMapEntry = findFromListMaps(toEntryList, searchMap);
                                    }
                                }
                            }
                        }

                        if (isNotEmpty(toMapEntry))
                        {
                            fromXmlDocument = updateXmlEntry(fromXmlDocument, fromMapEntry, toMapEntry, excludeKeyList, includeValueList);
                            toXmlDocument = removeXmlEntry(toXmlDocument, toMapEntry);
                        }
                    }
                    catch (Exception exc)
                    {
                        System.err.println("Error in mergeing=="+exc.getMessage());
                    }
                }
            }
            // Step 1 End
            // Step 2 begin
            if (isNotEmpty(toXmlDocument) && UtilValidate.isNotEmpty(preserveExtra) && preserveExtra.equalsIgnoreCase("true"))
            {
                toEntryList = getListMapsFromXmlDocument(toXmlDocument, null);
                Iterator<Map<Object, Object>> toEntryListIter = toEntryList.iterator();
                while (toEntryListIter.hasNext())
                {
                    try
                    {
                        Map<Object, Object> toMapEntry = toEntryListIter.next();
                        fromXmlDocument = addXmlEntry(fromXmlDocument, toXmlDocument, toMapEntry);
                    }
                    catch (Exception exc)
                    {
                        System.err.println("Error in mergeing=="+exc.getMessage());
                    }
                }
            }
            // Step 2 End
            // Step 3 begin
            writeXmlDocument(fromXmlDocument, toXmlFilePath);
            // Step 3 End
        }
        catch (Exception exc)
        {
            System.err.println("Error in mergeing=="+exc.getMessage());
        }
    }

    public static Document addXmlEntry(Document fromXmlDocument, Document toXmlDocument, Map<Object, Object> XmlEntry)
    {
        if (isNotEmpty(fromXmlDocument) && isNotEmpty(toXmlDocument)) 
        {
            try
            {
                List<? extends Node> nodeList = UtilXml.childNodeList(toXmlDocument.getDocumentElement().getFirstChild());
                for (Node node: nodeList)
                {
                    Boolean found = Boolean.FALSE;
                    if (node.getNodeType() == Node.ELEMENT_NODE) 
                    {
                        if (isMatch(getAllNameValueMap(node), XmlEntry))
                        {
                            found = Boolean.TRUE;
                        }
                    }
                    if (found)
                    {
                        Node importNode = fromXmlDocument.importNode(node, true);
                        fromXmlDocument.getDocumentElement().appendChild(importNode);
                        break;
                    }
                }
            }
            catch (Exception exc)
            {
                System.err.println("Error in addXmlEntry="+XmlEntry+"==error=="+exc.getMessage());
            }
        }
        return fromXmlDocument;
    }

    public static Document removeXmlEntry(Document xmlDocument, Map<Object, Object> XmlEntry)
    {
        if (isNotEmpty(xmlDocument)) 
        {
            try
            {
                List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
                for (Node node: nodeList)
                {
                    Boolean found = Boolean.FALSE;
                    if (node.getNodeType() == Node.ELEMENT_NODE) 
                    {
                        if (isMatch(getAllNameValueMap(node), XmlEntry))
                        {
                            found = Boolean.TRUE;
                        }
                    }
                    if (found)
                    {
                        node.getParentNode().removeChild(node);
                        break;
                    }
                }
            }
            catch (Exception exc)
            {
                System.err.println("Error in removeXmlEntry="+XmlEntry+"==error=="+exc.getMessage());
            }
        }
        return xmlDocument;
    }

    public static Document updateXmlEntry(Document fromXmlDocument, Map<Object, Object> fromXmlEntry, Map<Object, Object> toXmlEntry, List<String> excludeKeyList, List<String> includeValueList) throws Exception
    {
        if (isNotEmpty(fromXmlDocument)) 
        {
            List<? extends Node> nodeList = UtilXml.childNodeList(fromXmlDocument.getDocumentElement().getFirstChild());
            for (Node node: nodeList)
            {
                Boolean found = Boolean.FALSE;
                if (node.getNodeType() == Node.ELEMENT_NODE) 
                {
                    if (isMatch(getAllNameValueMap(node), fromXmlEntry))
                    {
                        found = Boolean.TRUE;
                    }
                }
                if (found)
                {
                    List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
                    for(Node childNode: childNodeList)
                    {
                        if (excludeKeyList.contains(childNode.getNodeName()) && isNotEmpty(toXmlEntry.get(childNode.getNodeName())) && !(includeValueList.contains((String)toXmlEntry.get(childNode.getNodeName()))))
                        {
                            childNode.setTextContent((String)toXmlEntry.get(childNode.getNodeName()));
                        }
                    }
                    break;
                }
            }
        }
        return fromXmlDocument;
    }

    /**
     * read xml document and make List of Maps of element.
     * @param XmlFilePath String xml file path
     * @return a new List of  Maps.
     */
    public static List<Map<Object, Object>> getListMapsFromXmlFile(String XmlFilePath)
    {
        return getListMapsFromXmlFile(XmlFilePath, null);
        
    }
    
    public static List<Map<Object, Object>> getListMapsFromXmlFile(String XmlFilePath, String activChildName)
    {
        List<Map<Object, Object>> listMaps = FastList.newInstance();
        InputStream ins = null;
        URL xmlFileUrl = null;
        Document xmlDocument = null;
        try
        {
            if (isNotEmpty(XmlFilePath))
            {
                xmlFileUrl = fromFilename(XmlFilePath);
                if (isNotEmpty(xmlFileUrl)) ins = xmlFileUrl.openStream();
                if (isNotEmpty(ins))
                {
                    xmlDocument = readXmlDocument(ins, xmlFileUrl.toString());
                    listMaps = getListMapsFromXmlDocument(xmlDocument, activChildName);
                }
            }
        }
        catch (Exception exc)
        {
            System.err.println("Error reading xml file"+exc.getMessage());
        }
        finally
        {
            try
            {
                if (isNotEmpty(ins)) ins.close();
            }
            catch (Exception exc)
            {
                System.err.println("Error reading xml file"+exc.getMessage());
            }
        }
        return listMaps;
    }
    
    public static List<Map<Object, Object>> getListMapsFromXmlDocument(Document xmlDocument, String activChildName)
    {
        List<Map<Object, Object>> listMaps = FastList.newInstance();
        try
        {
            if (isNotEmpty(xmlDocument))
            {
                List<? extends Node> nodeList = FastList.newInstance();
                if (isNotEmpty(activChildName))
                {
                    nodeList = UtilXml.childElementList(xmlDocument.getDocumentElement(), activChildName);
                }
                else 
                {
                    nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
                }
                
                for (Node node: nodeList)
                {
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        listMaps.add(getAllNameValueMap(node));
                    }
                }
            }
        }
        catch (Exception exc)
        {
            System.err.println("Error reading xml file"+exc.getMessage());
        }
        return listMaps;
    }

    public static Map<Object, Object> findFromListMaps(List<Map<Object, Object>> listOfMaps, Map<Object, Object> searchMap)
    {
        Map<Object, Object> rowMap = FastMap.newInstance();
        try
        {
            if (isEmpty(listOfMaps) || isEmpty(searchMap))
            {
                return rowMap;
            }
            Iterator<Map<Object, Object>> listOfMapsIter = listOfMaps.iterator();
            while (listOfMapsIter.hasNext())
            {
                Map<Object, Object> mapEntry = listOfMapsIter.next();
                if (isMatch(mapEntry, searchMap))
                {
                    rowMap = mapEntry;
                    break;
                }
            }
        } catch (Exception exc) {
            System.err.println("Error in searching"+exc.getMessage());
        }
        return rowMap;
    }

    // Match the two Map as partial
    private static boolean isMatch(Map<Object, Object> fromMap, Map<Object, Object> toMap)
    {
        if (isEmpty(fromMap) || isEmpty(toMap))
        {
            return false;
        }

        Boolean match = Boolean.TRUE;
        Set<Object> toKeys = toMap.keySet();
        Iterator<Object> toKeyIter = toKeys.iterator();
        while (toKeyIter.hasNext())
        {
            Object toKey = toKeyIter.next();
            if (!areEqual(fromMap.get(toKey), toMap.get(toKey)))
            {
                match = Boolean.FALSE;
            }
        }

        return match;
    }

    private static Map<Object, Object> getAllNameValueMap(Node node)
    {

        Map<Object, Object> allFields = FastMap.newInstance();
        if (isNotEmpty(node))
        {
            Map<Object, Object> attrFields = getAttributeNameValueMap(node);
            Set<Object> keys = attrFields.keySet();
            Iterator<Object> attrFieldsIter = keys.iterator();
            while (attrFieldsIter.hasNext())
            {
                Object key = attrFieldsIter.next();
                allFields.put(key, attrFields.get(key));
            }

            List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
            if (isNotEmpty(childNodeList))
            {
                for(Node childNode: childNodeList)
                {
                    allFields.put(childNode.getNodeName(), UtilXml.elementValue((Element)childNode));
                    attrFields = getAttributeNameValueMap(childNode);
                    keys = attrFields.keySet();
                    attrFieldsIter = keys.iterator();
                    while (attrFieldsIter.hasNext())
                    {
                        Object key = attrFieldsIter.next();
                        allFields.put(key, attrFields.get(key));
                    }
                }
            }
        }
        return allFields;
    }
    /**
     * Create a map from element
     * @param node Node container element node
     * @return The resulting Map
     */
    private static Map<Object, Object> getAttributeNameValueMap(Node node)
    {
        Map<Object, Object> attrFields = FastMap.newInstance();
        if (isNotEmpty(node))
        {
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                NamedNodeMap attrNodeList = node.getAttributes();
                for (int a = 0; a < attrNodeList.getLength(); a++)
                {
                    Node attrNode = attrNodeList.item(a);
                    attrFields.put(attrNode.getNodeName(), attrNode.getNodeValue());
                }
            }
        }
        return attrFields;
    }

    public static boolean writeXmlDocument(Document xmlDocument, String XmlFilePath) {
        if (isEmpty(xmlDocument) || isEmpty(XmlFilePath) )
        {
            return false;
        }
        OutputStream os = null;
        URL xmlFileUrl = fromFilename(XmlFilePath);
        if (isEmpty(xmlFileUrl)) return false;
        try
        {
            xmlDocument.normalize();
            os = new FileOutputStream(xmlFileUrl.getPath());
            Transformer transformer = createOutputTransformer("UTF-8", false, true, 4, true);
            UtilXml.transformDomDocument(transformer, xmlDocument, os);
            if (isNotEmpty(os)) os.close();
        }
        catch (Exception exc)
        {
            System.err.println("Error in writeXmlDocument"+exc.getMessage());
            return false;
        }
        os = null;
        return true;
    }

    public static Document readXmlDocument(String XmlFilePath) {
        InputStream ins = null;
        Document xmlDocument = null;

        URL xmlFileUrl = fromFilename(XmlFilePath);
        if (isEmpty(xmlFileUrl))
        {
            return null;
        }
        try {
            ins = xmlFileUrl.openStream();
            if (isNotEmpty(ins))
            {
                xmlDocument = readXmlDocument(ins, xmlFileUrl.toString());
            }
        }
        catch (Exception e)
        {
            System.err.println("Error in readXmlDocument"+e.getMessage());
            return null;
        }
        finally
        {
            try
            {
                if (isNotEmpty(ins)) ins.close();
            }
            catch (Exception e)
            {
                System.err.println("Error in readXmlDocument"+e.getMessage());
            }
        }
        ins = null;
        return xmlDocument;
    }

    public static Document readXmlDocument(InputStream is, String docDescription)
            throws SAXException, ParserConfigurationException, java.io.IOException {
        return readXmlDocument(is, true, docDescription);
    }

    public static Document readXmlDocument(InputStream is, boolean validate, String docDescription)
            throws SAXException, ParserConfigurationException, java.io.IOException {
        if (isEmpty(is))
        {
            System.err.println("[readXmlDocument] InputStream was null, doing nothing");
            return null;
        }

        Document document = null;

        DocumentBuilderFactory factory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
        factory.setValidating(validate);
        factory.setNamespaceAware(true);

        factory.setAttribute("http://xml.org/sax/features/validation", validate);
        factory.setAttribute("http://apache.org/xml/features/validation/schema", validate);

        DocumentBuilder builder = factory.newDocumentBuilder();
        if (validate)
        {
            builder.setEntityResolver(new DefaultHandler());
            builder.setErrorHandler(new DefaultHandler());
        }
        document = builder.parse(is);

        return document;
    }

    /** Creates a JAXP TrAX Transformer suitable for pretty-printing an
     * XML document. 
     * @param encoding Optional encoding, defaults to UTF-8
     * @param omitXmlDeclaration If <code>true</code> the xml declaration
     * will be omitted from the output
     * @param indent If <code>true</code>, the output will be indented
     * @param indentAmount If <code>indent</code> is <code>true</code>,
     * the number of spaces to indent. Default is 4.
     * @param keepSpace If <code>true</code>, the output will preserve the space
     * @return A <code>Transformer</code> instance
     * @see <a href="http://java.sun.com/javase/6/docs/api/javax/xml/transform/package-summary.html">JAXP TrAX</a>
     * @throws TransformerConfigurationException
     */
    public static Transformer createOutputTransformer(String encoding, boolean omitXmlDeclaration, boolean indent, int indentAmount, boolean preserveSpace) throws TransformerConfigurationException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:xalan=\"http://xml.apache.org/xslt\" version=\"1.0\">\n");
        sb.append("<xsl:output method=\"xml\" encoding=\"");
        sb.append(encoding == null ? "UTF-8" : encoding);
        sb.append("\"");
        if (omitXmlDeclaration)
        {
            sb.append(" omit-xml-declaration=\"yes\"");
        }
        sb.append(" indent=\"");
        sb.append(indent ? "yes" : "no");
        sb.append("\"");
        if (indent)
        {
            sb.append(" xalan:indent-amount=\"");
            sb.append(indentAmount <= 0 ? 4 : indentAmount);
            sb.append("\"");
        }
        if (preserveSpace)
        {
            sb.append("/>\n<xsl:preserve-space elements=\"*\"/>\n");
        }
        else
        {
            sb.append("/>\n<xsl:strip-space elements=\"*\"/>\n");
        }
        sb.append("<xsl:template match=\"@*|node()\">\n");
        sb.append("<xsl:copy><xsl:apply-templates select=\"@*|node()\"/></xsl:copy>\n");
        sb.append("</xsl:template>\n</xsl:stylesheet>\n");
        ByteArrayInputStream bis = new ByteArrayInputStream(sb.toString().getBytes());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        return transformerFactory.newTransformer(new StreamSource(bis));
    }

    public static boolean isEmpty(Object value)
    {
        if (value == null) return true;

        if (value instanceof String) return ((value == null) || (((String)value).length() == 0));
        if (value instanceof Collection) return ((value == null) || (((Collection<? extends Object>)value).size() == 0));
        if (value instanceof Map) return ((value == null) || (((Map<? extends Object, ? extends Object>)value).size() == 0));
        if (value instanceof CharSequence) return ((value == null) || (((CharSequence)value).length() == 0));

        // These types would flood the log
        // Number covers: BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short
        if (value instanceof Boolean) return false;
        if (value instanceof Number) return false;
        if (value instanceof Character) return false;
        if (value instanceof java.sql.Timestamp) return false;
        return false;
    }

    public static boolean isNotEmpty(Object value)
    {
        return !isEmpty(value);
    }

    public static URL fromFilename(String filename) {
        if (filename == null) return null;
        File file = new File(filename);
        URL url = null;

        try {
            if (file.exists()) url = file.toURI().toURL();
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
            url = null;
        }
        return url;
    }

    public static boolean areEqual(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        } else {
            return obj.equals(obj2);
        }
    }
}
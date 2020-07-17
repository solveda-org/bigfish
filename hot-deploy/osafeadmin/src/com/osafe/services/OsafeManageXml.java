package com.osafe.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * OsafeManageXml
 */

public class OsafeManageXml
{

    public static final String module = OsafeManageXml.class.getName();

    /** An instance of the generic cache for storing the list of map from xml file.
     *  Each list instance is keyed by the file's URL.
     */
    protected static UtilCache<String, List<Map<Object, Object>>> urlCache = UtilCache.createUtilCache("osafe.ManageXmlUrlCache");

    public static Map<String, ?>  modifyLabelXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While Updating";

        try
        {
            xmlDocument = readUiLabelDocument(context);
            if (xmlDocument != null)
            {
                context.put("xmlDocument", xmlDocument);
                context.put("isCatUpdate", Boolean.TRUE);
                updateXmlDocument(context);
                if (!writeUiLabelDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
            else
            {
                resp = ServiceUtil.returnError(errorMsg);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }

    public static Map<String, ?>  modifyDivSequenceXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While Updating";
        
        try
        {
            xmlDocument = readDivSequenceDocument(context);
            if (xmlDocument != null)
            {
                context.put("xmlDocument", xmlDocument);
                context.put("isCatUpdate", Boolean.TRUE);
                updateXmlDocument(context);
                if (!writeDivSequenceDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
            else 
            {
                resp = ServiceUtil.returnError(errorMsg);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }

    public static Map<String, ?>  modifyImageLocationPreferencesXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While Updating";
        
        try
        {
            xmlDocument = readImageLocationPreferencesDocument(context);
            if (xmlDocument != null)
            {
                context.put("xmlDocument", xmlDocument);
                context.put("isCatUpdate", Boolean.FALSE);
                updateXmlDocument(context);
                if (!writeImageLocationPreferencesDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
            else
            {
                resp = ServiceUtil.returnError(errorMsg);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }

    public static Map<String, ?>  modifyBigfishParameterXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        String parameterFileName = (String)context.get("parameterFileName");
        Document xmlDocument = null;
        String errorMsg = "Error While Updating";
        
        try
        {
            xmlDocument = readBigfishParameterDocument(context);
            if (xmlDocument != null)
            {
                context.put("xmlDocument", xmlDocument);
                context.put("isCatUpdate", Boolean.FALSE);
                updateXmlDocument(context);
                if (!writeBigfishParameterDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
            else
            {
                resp = ServiceUtil.returnError(errorMsg);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        resp.put("parameterFileName", parameterFileName);
        return resp;
    }
    
    public static void updateXmlDocument(Map context) {

        Document xmlDocument = (Document)context.get("xmlDocument");
        String key = (String)context.get("key");
        String category = (String)context.get("category");
        if (category == null) category ="";
        String screen = (String)context.get("screen");
        if (screen == null) screen ="";
        String description = (String)context.get("description");
        String style = (String)context.get("style");
        String mandatory = (String)context.get("mandatory");
        String value = (String)context.get("value");
        String group = (String)context.get("group");

        boolean isCatUpdate = Boolean.TRUE;
        if (UtilValidate.isNotEmpty(context.get("isCatUpdate")) ) 
            isCatUpdate = (Boolean)context.get("isCatUpdate");

        if (xmlDocument != null)
        {
            boolean updated = false;
            List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
            for (Node node: nodeList)
            {
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    boolean keyFound = false;
                    NamedNodeMap attrNodeList = node.getAttributes();
                    for (int a = 0; a < attrNodeList.getLength(); a++)
                    {
                        Node attrNode = attrNodeList.item(a);
                        if (attrNode.getNodeName().equalsIgnoreCase("key") && attrNode.getNodeValue().equals(key))
                        {
                            keyFound = true;
                        }
                     }
                     if(keyFound)
                     {
                         List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
                         boolean catFound = false;
                         if (UtilValidate.isNotEmpty(isCatUpdate) && !isCatUpdate) catFound = true;
                         // check the category or screen match
                         for(Node childNode: childNodeList)
                         {
                             if (childNode.getNodeName().equalsIgnoreCase("category"))
                             {
                                 if (UtilXml.elementValue((Element)childNode).equals(category)) catFound = true;
                             }
                             else if (childNode.getNodeName().equalsIgnoreCase("screen"))
                             {
                                 if (UtilXml.elementValue((Element)childNode).equals(screen)) catFound = true;
                             }
                         }
                         //lets update the key if screen or category match
                         if(catFound)
                         {
                             for(Node childNode: childNodeList)
                             {
                                 if (childNode.getNodeName().equalsIgnoreCase("description"))
                                 {
                                     childNode.setTextContent(description);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("style"))
                                 {
                                     childNode.setTextContent(style);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("mandatory"))
                                 {
                                     childNode.setTextContent(mandatory);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("value"))
                                 {
                                     childNode.setTextContent(value);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("screen"))
                                 {
                                     childNode.setTextContent(screen);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("category"))
                                 {
                                     childNode.setTextContent(category);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("group"))
                                 {
                                     childNode.setTextContent(group);
                                 }
                             }
                             updated = true;
                         }
                     }
                }
                if(updated) break;
            }
        }
        context.put("xmlDocument", xmlDocument);
    }

    public static Map<String, ?> copyLabelXml(DispatchContext dctx, Map<String, ?> context) 
    {
        Map<String, Object> resp = null;
        
        String labelFileProperty = (String)context.get("labelFileProperty");
        String deploymentLabelFileProperty = (String)context.get("deploymentLabelFileProperty");
        
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", labelFileProperty), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", deploymentLabelFileProperty), context);
            
        try
        {
            FileUtils.copyFile(new File(XmlFilePath), new File(deploymentXmlFilePath));
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in copy label file", module);
            resp = ServiceUtil.returnFailure();
        }
        finally
        {
            if (resp == null) resp = ServiceUtil.returnSuccess();
        }
        return resp;
    }

    public static Map<String, ?> copyDivSequenceXml(DispatchContext dctx, Map<String, ?> context)
    {
        Map<String, Object> resp = null;
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-uiSequence-xml-file"), context);
        try
        {
            FileUtils.copyFile(new File(XmlFilePath), new File(deploymentXmlFilePath));
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in copy label file", module);
            resp = ServiceUtil.returnFailure();
        }
        finally
        {
            if (resp == null) resp = ServiceUtil.returnSuccess();
        }
        return resp;
    }

    public static Map<String, ?> copyImageLocationPreferencesXml(DispatchContext dctx, Map<String, ?> context)
    {
        Map<String, Object> resp = null;
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-location-preference-file"), context);
        try
        {
            FileUtils.copyFile(new File(XmlFilePath), new File(deploymentXmlFilePath));
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in copy label file", module);
            resp = ServiceUtil.returnFailure();
        }
        finally
        {
            if (resp == null) resp = ServiceUtil.returnSuccess();
        }
        return resp;
    }

    public static Map<String, ?> copyBigfishParameterXml(DispatchContext dctx, Map<String, ?> context)
    {
        String parameterFileName = (String)context.get("parameterFileName");
        String ecommerceConfigPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-config-path"), context);
        String ecommerceDeploymentConfigPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-deployment-config-path"), context);
        String XmlFilePath = new File(ecommerceConfigPath, parameterFileName).getAbsolutePath();
        String deploymentXmlFilePath = new File(ecommerceDeploymentConfigPath, parameterFileName).getAbsolutePath();
    
        Map<String, Object> resp = null;
        try
        {
            FileUtils.copyFile(new File(XmlFilePath), new File(deploymentXmlFilePath));
        }
        catch (Exception exc) 
        {
            Debug.logError(exc, "Error in copy Bigfish Parameter file", module);
            resp = ServiceUtil.returnFailure();
        }
        finally 
        {
            if (resp == null) resp = ServiceUtil.returnSuccess();
        }
        return resp;
    }

    private static List<Map<Object, Object>> getListMaps(Document xmlDocument,String activChildName)
    {
        List<Map<Object, Object>> listMaps = FastList.newInstance();
        try {
                    List<? extends Node> nodeList = FastList.newInstance();
                    if (UtilValidate.isNotEmpty(activChildName))
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
                            Map<Object, Object> fields = FastMap.newInstance();
                            fields.put(node.getNodeName(), UtilXml.elementValue((Element)node));
                            Map<Object, Object> attrFields = getAttributeNameValueMap(node);
                            Set<Object> keys = attrFields.keySet();
                            Iterator<Object> attrFieldsIter = keys.iterator();
                            while (attrFieldsIter.hasNext())
                            {
                                Object key = attrFieldsIter.next();
                                fields.put(key, attrFields.get(key));
                            }

                            List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
                            if (UtilValidate.isNotEmpty(childNodeList))
                            {
	                            for(Node childNode: childNodeList)
	                            {
	                                fields.put(childNode.getNodeName(), UtilXml.elementValue((Element)childNode));
	                                attrFields = getAttributeNameValueMap(childNode);
	                                keys = attrFields.keySet();
	                                attrFieldsIter = keys.iterator();
	                                while (attrFieldsIter.hasNext())
	                                {
	                                    Object key = attrFieldsIter.next();
	                                    fields.put(key, attrFields.get(key));
	                                }
	                            }
                            }
                            listMaps.add(fields);
                        }
                    }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error building Node List Map", module);
        }
        return listMaps;
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
        try {
            if (UtilValidate.isNotEmpty(XmlFilePath))
            {
                xmlFileUrl = UtilURL.fromFilename(XmlFilePath);
                if (xmlFileUrl  != null)
                {
                	ins = xmlFileUrl.openStream();
                }
                if (ins != null)
                {
                    xmlDocument = UtilXml.readXmlDocument(ins, xmlFileUrl.toString());
                    listMaps = getListMaps(xmlDocument,activChildName);
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error reading xml file", module);
        }
        finally
        {
            try
            {
                if (ins != null) ins.close();
            }
            catch (Exception exc)
            {
                Debug.logError(exc, "Error reading xml file", module);
            }
        }
        return listMaps;
    }
    public static List<Map<Object, Object>> getListMapsFromXmlSource(String Xml)
    {
    	return getListMapsFromXmlSource(Xml,null);
    }
    public static List<Map<Object, Object>> getListMapsFromXmlSource(String Xml, String activChildName)
    {
        List<Map<Object, Object>> listMaps = FastList.newInstance();
        Document xmlDocument = null;
        try {
            if (UtilValidate.isNotEmpty(Xml))
            {
                    xmlDocument = UtilXml.readXmlDocument(Xml);
                    listMaps = getListMaps(xmlDocument,activChildName);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error reading xml source", module);
        }
        return listMaps;
    }

    /**
     * make List of Maps of element using cache.
     * @param XmlFilePath String xml file path
     * @return a new List of  Maps.
     */
    public static List<Map<Object, Object>> getListMapsFromXmlFileUseCache(String XmlFilePath)
    {
        List<Map<Object, Object>> listMaps = FastList.newInstance();
        try
        {
            URL xmlFileUrl = UtilURL.fromFilename(XmlFilePath);
            listMaps = urlCache.get(xmlFileUrl.toString());
            if (listMaps == null)
            {
                listMaps = getListMapsFromXmlFile(XmlFilePath);
                urlCache.put(xmlFileUrl.toString(), listMaps);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error reading xml file using cache", module);
        }
        return listMaps;
    }

    /**
     * Create a map from element
     * @param node Node container element node
     * @return The resulting Map
     */
    private static Map<Object, Object> getAttributeNameValueMap(Node node) throws Exception
    {
        Map<Object, Object> attrFields = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(node))
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

    /**
     * filter the List of Maps of element based on search string.
     * @param listOfMaps list of xml file element
     * @param searchKeyRestriction map of key value restriction for search
     * @param searchString string for search
     * @param searchIgnoreCase boolean for follow case in search
     * @return a new search List of  Maps.
     */
    public static List<Map<Object, Object>> getSearchListFromListMaps(List<Map<Object, Object>> listOfMaps, Map<Object, Object> searchRestrictionMap, String searchString, boolean searchIgnoreCase)
    {
        List<Map<Object, Object>> searchListMaps = FastList.newInstance();
        try
        {
            searchListMaps = getSearchListFromListMaps(listOfMaps, searchRestrictionMap, searchString, searchIgnoreCase, Boolean.TRUE);
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return searchListMaps;
    }
    /**
     * filter the List of Maps of element based on search string.
     * @param listOfMaps list of xml file element
     * @param searchKeyRestriction map of key value restriction for search
     * @param searchString string for search
     * @param searchIgnoreCase boolean for follow case in search
     * @param searchPartial boolean for perform partial or exact search
     * @return a new search List of  Maps.
     */
    public static List<Map<Object, Object>> getSearchListFromListMaps(List<Map<Object, Object>> listOfMaps, Map<Object, Object> searchRestrictionMap, String searchString, boolean searchIgnoreCase, boolean searchPartial)
    {
        List<Map<Object, Object>> searchListMaps = FastList.newInstance();
        try
        {
            Iterator<Map<Object, Object>> listOfMapsIter = listOfMaps.iterator();
            while (listOfMapsIter.hasNext())
            {
                boolean flag = false;
                Map<Object, Object> mapEntry = listOfMapsIter.next();
                Set<Object> keys = mapEntry.keySet();
                Iterator<Object> mapEntryKeyIter = keys.iterator();
                while (mapEntryKeyIter.hasNext())
                {
                    Object mapEntryKey = mapEntryKeyIter.next();
                    if (searchRestrictionMap.containsKey(mapEntryKey))
                    {
                        Object searchMapEntryValue = searchRestrictionMap.get(mapEntryKey);
                        boolean searchIgnoreCaseflag = Boolean.FALSE;
                        if (searchPartial)
                        {
                            searchIgnoreCaseflag = searchIgnoreCase?StringUtils.containsIgnoreCase((String)mapEntry.get(mapEntryKey), searchString):StringUtils.contains((String)mapEntry.get(mapEntryKey), searchString);
                        }
                        else 
                        {
                            searchIgnoreCaseflag = searchIgnoreCase?StringUtils.equalsIgnoreCase((String)mapEntry.get(mapEntryKey), searchString):StringUtils.equals((String)mapEntry.get(mapEntryKey), searchString);
                        }
                        if (searchMapEntryValue.equals("Y") && searchIgnoreCaseflag)
                        {
                            searchListMaps.add(mapEntry);
                            flag = true;
                        }
                    }
                    if(flag) break;
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return searchListMaps;
    }

    /**
     * filter the List of Maps of element based on search string.
     * @param listOfMaps list of xml file element
     * @param key string for search
     * @param keyValue string value of key
     * @return The resulting Map
     */
    public static Map<Object, Object> findByKeyFromListMaps(List<Map<Object, Object>> listOfMaps, String key, String keyValue)
    {
        Map<Object, Object> rowMap = FastMap.newInstance();
        try
        {
            Iterator<Map<Object, Object>> listOfMapsIter = listOfMaps.iterator();
            while (listOfMapsIter.hasNext())
            {
                Map<Object, Object> mapEntry = listOfMapsIter.next();
                if (UtilValidate.areEqual(mapEntry.get(key), keyValue))
                {
                    rowMap = mapEntry;
                    break;
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return rowMap;
    }
    // To search node in xml using Key and Screen
    public static Map<Object, Object> findByKeyAndScreenFromListMaps(List<Map<Object, Object>> listOfMaps, String key, String keyValue,String screen, String screenValue)
    {
    	Map<Object, Object> rowMap = FastMap.newInstance();
        try
        {
            Iterator<Map<Object, Object>> listOfMapsIter = listOfMaps.iterator();
            while (listOfMapsIter.hasNext())
            {
                Map<Object, Object> mapEntry = listOfMapsIter.next();
                if (UtilValidate.areEqual(mapEntry.get(key), keyValue) && UtilValidate.areEqual(mapEntry.get(screen), screenValue) )
                {
                    rowMap = mapEntry;
                    break;
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return rowMap;
    }

    /**
     * filter the List of Maps of element based on search string.
     * @param XmlFilePath String xml file path
     * @param searchKeyRestriction map of key value restriction for search
     * @param searchString string for search
     * @return a new search List of  Maps.
     */
    public static List<Map<Object, Object>> getSearchListFromXmlFile(String XmlFilePath, Map<Object, Object> searchRestrictionMap, String searchString)
    {
        List<Map<Object, Object>> searchListMaps = FastList.newInstance();
        try
        {
            searchListMaps = getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, searchString, Boolean.TRUE);
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return searchListMaps;
    }

    /**
     * filter the List of Maps of element based on search string.
     * @param XmlFilePath String xml file path
     * @param searchKeyRestriction map of key value restriction for search
     * @param searchString string for search
     * @param searchIgnoreCase boolean for follow case in search
     * @return a new search List of  Maps.
     */
    public static List<Map<Object, Object>> getSearchListFromXmlFile(String XmlFilePath, Map<Object, Object> searchRestrictionMap, String searchString, boolean searchIgnoreCase)
    {
        List<Map<Object, Object>> searchListMaps = FastList.newInstance();
        try
        {
            searchListMaps = getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, searchString, searchIgnoreCase, Boolean.TRUE);
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return searchListMaps;
    }

    /**
     * filter the List of Maps of element based on search string.
     * @param XmlFilePath String xml file path
     * @param searchKeyRestriction map of key value restriction for search
     * @param searchString string for search
     * @param searchIgnoreCase boolean for follow case in search
     * @param searchPartial boolean for perform partial or exact search
     * @return a new search List of  Maps.
     */
    public static List<Map<Object, Object>> getSearchListFromXmlFile(String XmlFilePath, Map<Object, Object> searchRestrictionMap, String searchString, boolean searchIgnoreCase, boolean searchPartial)
    {
        List<Map<Object, Object>> searchListMaps = FastList.newInstance();
        try
        {
            searchListMaps = getSearchListFromXmlFile(XmlFilePath, searchRestrictionMap, searchString, searchIgnoreCase, searchPartial, Boolean.FALSE);
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return searchListMaps;
    }

    /**
     * filter the List of Maps of element based on search string.
     * @param XmlFilePath String xml file path
     * @param searchKeyRestriction map of key value restriction for search
     * @param searchString string for search
     * @param searchIgnoreCase boolean for follow case in search
     * @param searchPartial boolean for perform partial or exact search
     * @param usecache boolean for perform search using cache
     * @return a new search List of  Maps.
     */
    public static List<Map<Object, Object>> getSearchListFromXmlFile(String XmlFilePath, Map<Object, Object> searchRestrictionMap, String searchString, boolean searchIgnoreCase, boolean searchPartial, boolean useCache)
    {
        List<Map<Object, Object>> searchListMaps = FastList.newInstance();
        try
        {
            List<Map<Object, Object>> listMaps =  FastList.newInstance();
            if (useCache)
            {
                listMaps = getListMapsFromXmlFileUseCache(XmlFilePath);
            }
            else 
            {
                listMaps = getListMapsFromXmlFile(XmlFilePath);
            }
            searchListMaps = getSearchListFromListMaps(listMaps, searchRestrictionMap, searchString, searchIgnoreCase, searchPartial);
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return searchListMaps;
    }

    /**
     * filter the List of Maps of element based on search string.
     * @param XmlFilePath String xml file path
     * @param key string for search
     * @param keyValue string value of key
     * @return The resulting Map
     */
    public static Map<Object, Object> findByKeyFromXmlFile(String XmlFilePath, String key, String keyValue)
    {
        Map<Object, Object> rowMap = FastMap.newInstance();
        try
        {
            List<Map<Object, Object>> listMaps = getListMapsFromXmlFile(XmlFilePath);
            rowMap = findByKeyFromListMaps(listMaps, key, keyValue);
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error in searching", module);
        }
        return rowMap;
    }
    

    public static List<Map<Object, Object>> getMapListFromXmlFile(String XmlFilePath)
    {
        List<Map<Object, Object>> listMaps = FastList.newInstance();
        InputStream ins = null;
        URL xmlFileUrl = null;
        Document xmlDocument = null;
        try
        {
            if (UtilValidate.isNotEmpty(XmlFilePath))
            {
                xmlFileUrl = UtilURL.fromFilename(XmlFilePath);
                if (UtilValidate.isNotEmpty(xmlFileUrl)) ins = xmlFileUrl.openStream();

                if (UtilValidate.isNotEmpty(ins))
                {
                    xmlDocument = UtilXml.readXmlDocument(ins, xmlFileUrl.toString());
                    List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());

                    for (Node node: nodeList)
                    {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Map<Object, Object> fieldMap = FastMap.newInstance();
                            fieldMap.put(node.getNodeName(), UtilXml.elementValue((Element)node));
                            Map<Object, Object> attrFieldMap = getAttributeNameValueMap(node);
                            Set<Object> mapKeys = attrFieldMap.keySet();
                            Iterator<Object> attrFieldMapIter = mapKeys.iterator();
                            while (attrFieldMapIter.hasNext())
                            {
                                Object mapKey = attrFieldMapIter.next();
                                fieldMap.put(mapKey, attrFieldMap.get(mapKey));
                            }

                            List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
                              if (UtilValidate.isNotEmpty(childNodeList) && childNodeList.size() > 0)
                              {
                                  fieldMap.put("child", getChildMapList(childNodeList));
                              }
                              listMaps.add(fieldMap);
                        }
                    }
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error reading xml file", module);
        }
        finally
        {
            try
            {
                if (UtilValidate.isNotEmpty(ins)) ins.close();
            }
            catch (Exception exc)
            {
                Debug.logError(exc, "Error reading xml file", module);
            }
        }
        return listMaps;
    }
    
    private static List<Map<Object, Object>> getChildMapList(List<? extends Node> childNodeList){
        List<Map<Object, Object>> childMapList = FastList.newInstance();

        try
        {
            for(Node childNode: childNodeList)
            {
                if (childNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Map<Object, Object> childFields = FastMap.newInstance();
                    Map<Object, Object> attrFieldMap = null;
                    attrFieldMap = getAttributeNameValueMap(childNode);
                    Set<Object> mapKeys = attrFieldMap.keySet();
                    Iterator<Object> attrFieldMapIter = mapKeys.iterator();

                    childFields.put(childNode.getNodeName(), UtilXml.elementValue((Element)childNode));
                    while (attrFieldMapIter.hasNext())
                    {
                        Object mapKey = attrFieldMapIter.next();
                        childFields.put(mapKey, attrFieldMap.get(mapKey));
                    }
                    List<? extends Node> childNodes = UtilXml.childNodeList(childNode.getFirstChild());
                    if (UtilValidate.isNotEmpty(childNodes) && childNodes.size() > 0)
                    {
                        childFields.put("child", getChildMapList(childNodes));
                    }
                    childMapList.add(childFields);
                }
            }
        }
        catch (Exception e)
        {
            Debug.logError(e, "Error reading xml file", module);
            e.printStackTrace();
        }
        return childMapList;
    }

    public static Map<String, ?>  deleteUiLabelXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While deleting";
        boolean isKeyExist = false;
        String key = (String)context.get("key");

        try
        {
            xmlDocument = readUiLabelDocument(context);
            if (xmlDocument != null)
            {
                List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
                for (Node node: nodeList)
                {
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        NamedNodeMap attrNodeList = node.getAttributes();
                        for (int a = 0; a < attrNodeList.getLength(); a++)
                        {
                            Node attrNode = attrNodeList.item(a);
                            if (attrNode.getNodeName().equalsIgnoreCase("key") && attrNode.getNodeValue().equals(key))
                            {
                                isKeyExist = true;
                            }
                         }
                         if(isKeyExist)
                         {
                             node.getParentNode().removeChild(node);
                             break;
                         }
                    }
                }
                context.put("xmlDocument", xmlDocument);
                if (!writeUiLabelDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }

    public static Map<String, ?>  deleteDivSeqItemXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While deleting";
        boolean nodeFound = false;
        String key = (String)context.get("key");
        String screen = (String)context.get("screen");
        try
        {
            xmlDocument = readDivSequenceDocument(context);
            if (xmlDocument != null)
            {
                List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
                for (Node node: nodeList)
                {
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        NamedNodeMap attrNodeList = node.getAttributes();
                        for (int a = 0; a < attrNodeList.getLength(); a++)
                        {
                            Node attrNode = attrNodeList.item(a);
                            if (attrNode.getNodeName().equalsIgnoreCase("key") && attrNode.getNodeValue().equals(key))
                            {
                            	List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
                                 // check the screen match
                                 for(Node childNode: childNodeList)
                                 {
                                     if (childNode.getNodeName().equalsIgnoreCase("screen"))
                                     {
                                         if (UtilXml.elementValue((Element)childNode).equals(screen)) nodeFound = true;
                                     }
                                 }
               
                            }
                         }
                         if(nodeFound)
                         {
                             node.getParentNode().removeChild(node);
                             break;
                         }
                    }
                }
                context.put("xmlDocument", xmlDocument);
                if (!writeDivSequenceDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }

    public static Map<String, ?>  deleteBigfishParameterXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While deleting";
        boolean nodeFound = false;
        String key = (String)context.get("key");
        String parameterFileName = (String)context.get("parameterFileName");
        try
        {
            xmlDocument = readBigfishParameterDocument(context);
            if (xmlDocument != null)
            {
                List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
                for (Node node: nodeList)
                {
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        NamedNodeMap attrNodeList = node.getAttributes();
                        for (int a = 0; a < attrNodeList.getLength(); a++)
                        {
                            Node attrNode = attrNodeList.item(a);
                            if (attrNode.getNodeName().equalsIgnoreCase("key") && attrNode.getNodeValue().equals(key))
                            {
                                nodeFound = true;
                            }
                         }
                         if(nodeFound)
                         {
                             node.getParentNode().removeChild(node);
                             break;
                         }
                    }
                }
                context.put("xmlDocument", xmlDocument);
                if (!writeBigfishParameterDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        resp.put("parameterFileName", parameterFileName);
        return resp;
    }

    public static Map<String, ?> addUiLabelXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While adding";

        String key = (String)context.get("key");
        String category = (String)context.get("category");
        String description = (String)context.get("description");
        String value = (String)context.get("value");

        try
        {
            xmlDocument = readUiLabelDocument(context);
            if (xmlDocument != null)
            {
                Element newPropertyElement = UtilXml.addChildElement(xmlDocument.getDocumentElement(), "property", xmlDocument);
                newPropertyElement.setAttribute("key", key);
                UtilXml.addChildElementValue(newPropertyElement, "value", value, xmlDocument).setAttribute("xml:lang", "en");
                if(UtilValidate.isNotEmpty(category))
                {
                	UtilXml.addChildElementValue(newPropertyElement, "category", category, xmlDocument);
                }
                if(UtilValidate.isNotEmpty(description))
                {
                	UtilXml.addChildElementValue(newPropertyElement, "description", description, xmlDocument);
                }
                context.put("xmlDocument", xmlDocument);
                if (!writeUiLabelDocument(context))
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
            else 
            {
                return ServiceUtil.returnError(errorMsg);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }

    public static Document readUiLabelDocument(Map<String, ?> context) 
    {
    	String labelFileProperty = (String)context.get("labelFileProperty");
    	String deploymentLabelFileProperty = (String)context.get("deploymentLabelFileProperty");
    	
    	String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", labelFileProperty), context);
    	String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", deploymentLabelFileProperty), context);
        if (UtilURL.fromFilename(deploymentXmlFilePath) == null) return null;
        
        return readXmlDocument(XmlFilePath);
    }

    public static boolean writeUiLabelDocument(Map context)
    {
    	String labelFileProperty = (String)context.get("labelFileProperty");
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", labelFileProperty), context);
        
        return writeXmlDocument((Document)context.get("xmlDocument"), XmlFilePath);
    }

    public static Document readDivSequenceDocument(Map<String, ?> context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-uiSequence-xml-file"), context);
        if (UtilURL.fromFilename(deploymentXmlFilePath) == null || !StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(deploymentXmlFilePath), "xml")) return null;
        return readXmlDocument(XmlFilePath);
    }

    public static boolean writeDivSequenceDocument(Map context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
        return writeXmlDocument((Document)context.get("xmlDocument"), XmlFilePath);
    }

    public static Document readImageLocationPreferencesDocument(Map<String, ?> context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-location-preference-file"), context);
        if (UtilURL.fromFilename(deploymentXmlFilePath) == null || !StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(deploymentXmlFilePath), "xml")) return null;
        return readXmlDocument(XmlFilePath);
    }

    public static Document readBigfishParameterDocument(Map<String, ?> context)
    {
        String parameterFileName = (String)context.get("parameterFileName");
        String ecommerceConfigPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-config-path"), context);
        String ecommerceDeploymentConfigPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-deployment-config-path"), context);
        String XmlFilePath = new File(ecommerceConfigPath, parameterFileName).getAbsolutePath();
        String deploymentXmlFilePath = new File(ecommerceDeploymentConfigPath, parameterFileName).getAbsolutePath();
        if (UtilURL.fromFilename(deploymentXmlFilePath) == null || !StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(deploymentXmlFilePath), "xml")) return null;
        return readXmlDocument(XmlFilePath);
    }

    public static boolean writeImageLocationPreferencesDocument(Map context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "image-location-preference-file"), context);
        return writeXmlDocument((Document)context.get("xmlDocument"), XmlFilePath);
    }

    public static boolean writeBigfishParameterDocument(Map context)
    {
        String parameterFileName = (String)context.get("parameterFileName");
        String ecommerceConfigPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "ecommerce-config-path"), context);
        String XmlFilePath = new File(ecommerceConfigPath, parameterFileName).getAbsolutePath();
        return writeXmlDocument((Document)context.get("xmlDocument"), XmlFilePath);
    }
    
    public static boolean writeXmlDocument(Document xmlDocument, String XmlFilePath)
    {
        if (UtilValidate.isEmpty(xmlDocument) || UtilValidate.isEmpty(XmlFilePath) )
        {
            return false;
        }
        OutputStream os = null;
        URL xmlFileUrl = UtilURL.fromFilename(XmlFilePath);
        if (UtilValidate.isEmpty(xmlFileUrl)) return false;
        try
        {
            xmlDocument.normalize();
            os = new FileOutputStream(xmlFileUrl.getPath());
            Transformer transformer = createOutputTransformer("UTF-8", false, true, 4, true);
            UtilXml.transformDomDocument(transformer, xmlDocument, os);
            if (os != null) os.close();
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            return false;
        }
        os = null;
        return true;
    }

    public static Document readXmlDocument(String XmlFilePath)
    {
        InputStream ins = null;
        Document xmlDocument = null;

        URL xmlFileUrl = UtilURL.fromFilename(XmlFilePath);
        if (UtilValidate.isEmpty(xmlFileUrl) || !StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(XmlFilePath), "xml"))
        {
            return null;
        }
        try
        {
            ins = xmlFileUrl.openStream();
            if (ins != null)
            {
                xmlDocument = UtilXml.readXmlDocument(ins, xmlFileUrl.toString());
            }
        }
        catch (Exception e)
        {
            Debug.logError(e, "Error ", module);
            return null;
        }
        finally
        {
            try
            {
                if (ins != null) ins.close();
            }
            catch (Exception e){
                Debug.logError(e, "Error ", module);
            }
        }
        ins = null;
        return xmlDocument;
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
    public static Transformer createOutputTransformer(String encoding, boolean omitXmlDeclaration, boolean indent, int indentAmount, boolean preserveSpace) throws TransformerConfigurationException
    {
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

    public static Document readDivSeqDocument(Map<String, ?> context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-uiSequence-xml-file"), context);
        if (UtilURL.fromFilename(deploymentXmlFilePath) == null || !StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(deploymentXmlFilePath), "xml")) return null;
        return readXmlDocument(XmlFilePath);
    }
    
    public static boolean writeDivSeqDocument(Map context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-uiSequence-xml-file"), context);
        return writeXmlDocument((Document)context.get("xmlDocument"), XmlFilePath);
    }
    
    public static Map<String, ?> addDivSeqItemXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While adding";

        String key = (String)context.get("key");
        String screen = (String)context.get("screen");
        String description = (String)context.get("description");
        String style = (String)context.get("style");
        String mandatory = (String)context.get("mandatory");
        String value = (String)context.get("value");
        String group = (String)context.get("group");
        
        try {
            xmlDocument = readDivSeqDocument(context);
            if (xmlDocument != null) {
                Element newPropertyElement = UtilXml.addChildElement(xmlDocument.getDocumentElement(), "property", xmlDocument);
                newPropertyElement.setAttribute("key", key);
                UtilXml.addChildElementValue(newPropertyElement, "value", value, xmlDocument).setAttribute("xml:lang", "en");
                UtilXml.addChildElementValue(newPropertyElement, "screen", screen, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "style", style, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "mandatory", mandatory, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "group", group, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "description", description, xmlDocument);
                context.put("xmlDocument", xmlDocument);
                if (!writeDivSeqDocument(context)) {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            } else {
                return ServiceUtil.returnError(errorMsg);
            }
        } catch (Exception exc) {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }

    public static Map<String, ?> addBigfishParameterXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While adding";

        String key = (String)context.get("key");
        String description = (String)context.get("description");
        String value = (String)context.get("value");
        String parameterFileName = (String)context.get("parameterFileName");
        
        try {
            xmlDocument = readBigfishParameterDocument(context);
            if (xmlDocument != null) {
                Element newPropertyElement = UtilXml.addChildElement(xmlDocument.getDocumentElement(), "property", xmlDocument);
                newPropertyElement.setAttribute("key", key);
                UtilXml.addChildElementValue(newPropertyElement, "value", value, xmlDocument).setAttribute("xml:lang", "en");
                UtilXml.addChildElementValue(newPropertyElement, "description", description, xmlDocument);
                context.put("xmlDocument", xmlDocument);
                if (!writeBigfishParameterDocument(context)) {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            } else {
                return ServiceUtil.returnError(errorMsg);
            }
        } catch (Exception exc) {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        resp.put("parameterFileName", parameterFileName);
        return resp;
    }

    public static Document readCustomPartyAttributeDocument(Map<String, ?> context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-customPartyAttribute-xml-file"), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-customPartyAttribute-xml-file"), context);
        if (UtilURL.fromFilename(deploymentXmlFilePath) == null)
        {
            return null; 
        }
        if(!StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(deploymentXmlFilePath), "xml"))
        {
        	return null;
        }
        return readXmlDocument(XmlFilePath);
    }
    
    public static boolean writeCustomPartyAttributeDocument(Map context)
    {
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-customPartyAttribute-xml-file"), context);
        return writeXmlDocument((Document)context.get("xmlDocument"), XmlFilePath);
    }
    
    public static Map<String, ?> addCustomPartyAttributeItemXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While adding";

        String attrName = (String)context.get("attrName");
        String sequenceNum = (String)context.get("sequenceNum");
        String caption = (String)context.get("caption");
        String type = (String)context.get("type");
        String entryFormat = (String)context.get("entryFormat");
        String maxLength = (String)context.get("maxLength");
        String valueList = (String)context.get("valueList");
        String mandatory = (String)context.get("mandatory");
        String reqMessage = (String)context.get("reqMessage");


        try
        {
            xmlDocument = readCustomPartyAttributeDocument(context);
            if (xmlDocument != null)
            {
                Element newPropertyElement = UtilXml.addChildElement(xmlDocument.getDocumentElement(), "CustomPartyAttribute", xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "AttrName", attrName, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "SequenceNum", sequenceNum, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "Caption", caption, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "Type", type, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "EntryFormat", entryFormat, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "MaxLength", maxLength, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "ValueList", valueList, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "Mandatory", mandatory, xmlDocument);
                UtilXml.addChildElementValue(newPropertyElement, "ReqMessage", reqMessage, xmlDocument);
                context.put("xmlDocument", xmlDocument);
                if (!writeCustomPartyAttributeDocument(context)) 
                {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
            else
            {
                return ServiceUtil.returnError(errorMsg);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }
    
    public static Map<String, ?>  updateCustomPartyAttributeItemXml(DispatchContext dctx, Map context)
    {
        Map<String, Object> resp = null;
        Document xmlDocument = null;
        String errorMsg = "Error While Updating";
        
        try
        {
            xmlDocument = readCustomPartyAttributeDocument(context);
            if (xmlDocument != null)
            {
                context.put("xmlDocument", xmlDocument);
                updateCustomPartyAttributeXmlDocument(context);
                if (!writeCustomPartyAttributeDocument(context)) {
                    resp = ServiceUtil.returnError(errorMsg);
                }
            }
            else
            {
                resp = ServiceUtil.returnError(errorMsg);
            }
        }
        catch (Exception exc)
        {
            Debug.logError(exc, "Error ", module);
            resp = ServiceUtil.returnError(errorMsg);
        }
        if (resp == null) resp = ServiceUtil.returnSuccess();
        return resp;
    }
    
    private static void updateCustomPartyAttributeXmlDocument(Map context)
    {

        Document xmlDocument = (Document)context.get("xmlDocument");
        String attrName = (String)context.get("attrName");
        String sequenceNum = (String)context.get("sequenceNum");
        String caption = (String)context.get("caption");
        String type = (String)context.get("type");
        String entryFormat = (String)context.get("entryFormat");
        String maxLength = (String)context.get("maxLength");
        String valueList = (String)context.get("valueList");
        String mandatory = (String)context.get("mandatory");
        String reqMessage = (String)context.get("reqMessage");

        if (xmlDocument != null)
        {
            boolean updated = false;
            List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
            for (Node node: nodeList)
            {
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    boolean attrNameFound = false;
                    List<? extends Node> attrNodeList = UtilXml.childNodeList(node.getFirstChild());
                    for (Node attrNode : attrNodeList)
                    {
                        if (attrNode.getNodeName().equalsIgnoreCase("AttrName"))
                        {
                        	if (UtilXml.elementValue((Element)attrNode).equals(attrName)) 
                            {
                        	     attrNameFound = true;
                            }
                        }
                     }
                     if(attrNameFound)
                     {
                         List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
                             for(Node childNode: childNodeList) 
                             {
                                 if (childNode.getNodeName().equalsIgnoreCase("SequenceNum"))
                                 {
                                     childNode.setTextContent(sequenceNum);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("Caption"))
                                 {
                                     childNode.setTextContent(caption);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("Type"))
                                 {
                                     childNode.setTextContent(type);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("EntryFormat"))
                                 {
                                     childNode.setTextContent(entryFormat);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("MaxLength"))
                                 {
                                     childNode.setTextContent(maxLength);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("ValueList"))
                                 {
                                     childNode.setTextContent(valueList);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("Mandatory"))
                                 {
                                     childNode.setTextContent(mandatory);
                                 }
                                 else if (childNode.getNodeName().equalsIgnoreCase("ReqMessage"))
                                 {
                                     childNode.setTextContent(reqMessage);
                                 }
                             }
                             updated = true;
                     }
                }
                if(updated) break;
            }
        }
        context.put("xmlDocument", xmlDocument);
    }
    
    public static Map<String, ?> copyCustomPartyAttributeXml(DispatchContext dctx, Map<String, ?> context) 
    {
        Map<String, Object> resp = null;
        String XmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-customPartyAttribute-xml-file"), context);
        String deploymentXmlFilePath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("osafeAdmin.properties", "osafe-deployment-customPartyAttribute-xml-file"), context);
        try 
        {
            FileUtils.copyFile(new File(XmlFilePath), new File(deploymentXmlFilePath));
        } 
        catch (Exception exc) 
        {
            Debug.logError(exc, "Error in copy label file", module);
            resp = ServiceUtil.returnFailure();
        }
        finally 
        {
            if (resp == null) resp = ServiceUtil.returnSuccess();
        }
        return resp;
    }
}
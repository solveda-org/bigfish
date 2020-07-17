/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.osafe.services;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.webapp.website.WebSiteWorker;

/**
 * ControlServlet.java - Master servlet for the web application.
 */
@SuppressWarnings("serial")
public class CatalogUrlServlet extends HttpServlet {

    public static final String module = CatalogUrlServlet.class.getName();

    public static String CATALOG_URL_MOUNT_POINT = "shop";
    public static final String CONTROL_MOUNT_POINT = "control";
    private static final ResourceBundle OSAFE_PROP = UtilProperties.getResourceBundle("OsafeProperties.xml", Locale.getDefault());
    private static ResourceBundleMapWrapper OSAFE_FRIENDLY_URL = null;

    public CatalogUrlServlet() {
        super();
    }

    /**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
         CATALOG_URL_MOUNT_POINT=OSAFE_PROP.getString("urlCatalogPrefix");
        } catch (Exception e) {
        	
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Delegator delegator = (Delegator) getServletContext().getAttribute("delegator");

        String pathInfo = request.getPathInfo();

        // look for productId
        String productId = null;
        String categoryId = null;
        String controlRequest = null;
        String filterGroup = null;
        int idxNextParam=-1;
        int idxProductParams=-1;
        try {
        	
        	String sURL = findUrlKeyByValue(pathInfo);
        	int idxUrl = sURL.indexOf("?");
        	if (idxUrl > -1)
        	{
        		controlRequest = sURL.substring(0,idxUrl);
            	int idxFirstGroupAttribute = sURL.indexOf("=",idxUrl);
            	int idxNextParamGroup=-1;            	
        		String paramGroupValue=null;
        		String paramGroupAttribute = null;
            	if (idxFirstGroupAttribute > -1)
            	{
            		paramGroupValue=null;
            		paramGroupAttribute = sURL.substring((idxUrl + 1),idxFirstGroupAttribute);
            		idxNextParamGroup = sURL.indexOf("&",idxFirstGroupAttribute);
                	if (idxNextParamGroup > -1)
                	{
                		paramGroupValue = sURL.substring((idxFirstGroupAttribute + 1),idxNextParamGroup);
                	}
                	else
                	{
                		paramGroupValue = sURL.substring((idxFirstGroupAttribute + 1));
                	}
                	
                    request.setAttribute(paramGroupAttribute, paramGroupValue);
            	}

            	while (idxNextParamGroup > -1)
            	{
            		paramGroupValue=null;
                	int idxParamGroupAttribute = sURL.indexOf("=",idxNextParamGroup);
            		paramGroupAttribute = sURL.substring((idxNextParamGroup + 1),idxParamGroupAttribute);
            		idxNextParamGroup = sURL.indexOf("&",idxParamGroupAttribute);
                	if (idxNextParamGroup > -1)
                	{
                		paramGroupValue = sURL.substring((idxParamGroupAttribute + 1),idxNextParamGroup);
                	}
                	else
                	{
                		paramGroupValue = sURL.substring((idxParamGroupAttribute + 1));
                	}
                	
                    request.setAttribute(paramGroupAttribute, paramGroupValue);
                	
            	}
        		
                RequestDispatcher rd = request.getRequestDispatcher("/" + CONTROL_MOUNT_POINT + "/" + controlRequest);            
                rd.forward(request, response);
            	
        		
        	}
        	else
        	{
                List<String> pathElements = StringUtil.split(pathInfo, "/");
                String lastPathElement = pathElements.get(pathElements.size() - 1);
                RequestDispatcher rd = request.getRequestDispatcher("/" + CONTROL_MOUNT_POINT + "/" + lastPathElement);            
                rd.forward(request, response);
        		
        	}
        } catch (Exception e) {
            Debug.logError(e, "Error looking up product info for ProductUrl with path info [" + pathInfo + "]: " + e.toString(), module);
        }

    }

    /**
     * @see javax.servlet.Servlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    public static String makeCatalogFriendlyUrl(HttpServletRequest request, String URL) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        StringBuilder urlBuilder = new StringBuilder();
        String pathInfo = request.getRequestURI();
        List<String> pathElements = StringUtil.split(pathInfo, "/");
        String solrURLParam=null;
        String origURL=URL;
        try {
        	//Check URL for SOLR
        	int solrIdx = origURL.indexOf("&filterGroup");
        	if (solrIdx > -1)
        	{
        		solrURLParam = origURL.substring(solrIdx+1);
        		origURL = origURL.substring(0,solrIdx);
        	}
        	
            OSAFE_FRIENDLY_URL = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeSeoUrlMap", Locale.getDefault());        	
        	String friendlyKey=StringUtil.replaceString(origURL,"&","~");
        	friendlyKey=StringUtil.replaceString(friendlyKey,"=","^^");
        	if (OSAFE_FRIENDLY_URL.containsKey(friendlyKey))
        	{
        		urlBuilder.setLength(0);  
                urlBuilder.append("/" + pathElements.get(0) + "/" + CATALOG_URL_MOUNT_POINT + "/");
            	String friendlyUrl =(String)OSAFE_FRIENDLY_URL.get(friendlyKey);
                urlBuilder.append(friendlyUrl);
            	if (solrIdx > -1)
            	{
                    urlBuilder.append("?" + solrURLParam);
            		
            	}
        	}
        	else
        	{
        		urlBuilder.setLength(0);  
                urlBuilder.append("/" + pathElements.get(0) + "/" + CONTROL_MOUNT_POINT + "/");
                urlBuilder.append(URL);
        	}

        }
         catch (Exception e) {
             //Debug.log(e, "Friendly URL not found for: " + URL, module);
        	 
         }

        return makeLink(request,urlBuilder.toString());
    }

    private static String makeLink(HttpServletRequest request,String url) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String webSiteId = WebSiteWorker.getWebSiteId(request);

        String httpsPort = null;
        String httpsServer = null;
        String httpPort = null;
        String httpServer = null;
        Boolean enableHttps = null;

        // load the properties from the website entity
        GenericValue webSite;
        if (webSiteId != null) {
            try {
                webSite = delegator.findByPrimaryKeyCache("WebSite", UtilMisc.toMap("webSiteId", webSiteId));
                if (webSite != null) {
                    httpsPort = webSite.getString("httpsPort");
                    httpsServer = webSite.getString("httpsHost");
                    httpPort = webSite.getString("httpPort");
                    httpServer = webSite.getString("httpHost");
                    enableHttps = webSite.getBoolean("enableHttps");
                }
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Problems with WebSite entity; using global defaults", module);
            }
        }

        // fill in any missing properties with fields from the global file
        if (UtilValidate.isEmpty(httpsPort)) {
            httpsPort = UtilProperties.getPropertyValue("url.properties", "port.https", "443");
        }
        if (UtilValidate.isEmpty(httpsServer)) {
            httpsServer = UtilProperties.getPropertyValue("url.properties", "force.https.host");
        }
        if (UtilValidate.isEmpty(httpPort)) {
            httpPort = UtilProperties.getPropertyValue("url.properties", "port.http", "80");
        }
        if (UtilValidate.isEmpty(httpServer)) {
            httpServer = UtilProperties.getPropertyValue("url.properties", "force.http.host");
        }
        if (enableHttps == null) {
            enableHttps = UtilProperties.propertyValueEqualsIgnoreCase("url.properties", "port.https.enabled", "Y");
        }

        // create the path the the control servlet
        String controlPath = (String) request.getAttribute("_CONTROL_PATH_");
        String server = httpServer;
        if (UtilValidate.isEmpty(server)) 
        {
            server = request.getServerName();
        }


        StringBuilder newURL = new StringBuilder();
        newURL.append("http://");
        newURL.append(server);
        if (!httpPort.equals("80")) 
        {
            newURL.append(":").append(httpPort);
        }
        if (!url.startsWith("/")) {
            newURL.append("/");
        }
        newURL.append(url);
        String sessionId = ";jsessionid=" + request.getSession().getId();
        // this should be inserted just after the "?" for the parameters, if there is one, or at the end of the string
        int questionIndex = newURL.indexOf("?");
        if (questionIndex == -1)
        {
            newURL.append(sessionId);
        } 
        else
        {
            newURL.insert(questionIndex, sessionId);
        }

        return newURL.toString();
    }
    

    public static String findUrlKeyByValue(String sURLvalue) {
    	String URL = "";
        String solrURLParam=null;
        
        try {
        	//Check URL for SOLR
        	int solrIdx = sURLvalue.indexOf("?filterGroup");
        	if (solrIdx > -1)
        	{
        		solrURLParam = sURLvalue.substring(solrIdx+1);
        		sURLvalue = sURLvalue.substring(0,solrIdx);
        	}
            OSAFE_FRIENDLY_URL = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeSeoUrlMap", Locale.getDefault());        	
        	Iterator iter = OSAFE_FRIENDLY_URL.keySet().iterator();
        	while (iter.hasNext())
        	{
        		String sKey = (String)iter.next();
        		String sValue = "/" + OSAFE_FRIENDLY_URL.get(sKey);
        		if (sValue.equalsIgnoreCase(sURLvalue))
        		{
        			URL=StringUtil.replaceString(sKey,"~","&");
        			URL=StringUtil.replaceString(URL,"^^","=");
                	if (solrIdx > -1)
                	{
                		URL = URL + "&" + solrURLParam;
                	}
                    return URL;        			
        		}
        	}
        }
         catch (Exception e) {
             //Debug.log(e, "Friendly URL mapping not found for: " + sURLvalue, module);
         }

        return URL;
    }

}

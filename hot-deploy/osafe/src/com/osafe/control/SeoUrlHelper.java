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
package com.osafe.control;

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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


public class SeoUrlHelper
{
    public final static String module = SeoUrlHelper.class.getName();
    private static ResourceBundleMapWrapper OSAFE_FRIENDLY_URL = null;
    public static final String PRODUCT_DETAIL_REQUEST_URL = "eCommerceProductDetail";
    public static final String PRODUCT_LIST_REQUEST_URL = "eCommerceProductList";
    public static final String CATEGORY_LIST_REQUEST_URL = "eCommerceCategoryList";
    public static final String CONTENT_REQUEST_URL = "eCommerceContent";
    public static final String PRODUCT_REQUEST = "productId";
    public static final String CATEGORY_REQUEST = "productCategoryId";
    public static final String CONTENT_REQUEST = "contentId";

    public static String makeSeoFriendlyUrl(HttpServletRequest request, String URL)
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
            
            OSAFE_FRIENDLY_URL = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("OSafeSeoUrlMap", Locale.getDefault());            
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
        return makeLink(request,urlBuilder.toString());
    }

    private static String makeLink(HttpServletRequest request,String url)
    {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String webSiteId = WebSiteWorker.getWebSiteId(request);

        String httpsPort = null;
        String httpsServer = null;
        String httpPort = null;
        String httpServer = null;
        Boolean enableHttps = null;

        // load the properties from the website entity
        GenericValue webSite;
        if (UtilValidate.isNotEmpty(webSiteId))
        {
            try
            {
                webSite = delegator.findByPrimaryKeyCache("WebSite", UtilMisc.toMap("webSiteId", webSiteId));
                if (UtilValidate.isNotEmpty(webSite))
                {
                    httpsPort = webSite.getString("httpsPort");
                    httpsServer = webSite.getString("httpsHost");
                    httpPort = webSite.getString("httpPort");
                    httpServer = webSite.getString("httpHost");
                    enableHttps = webSite.getBoolean("enableHttps");
                }
            }
            catch (GenericEntityException e)
            {
                Debug.logWarning(e, "Problems with WebSite entity; using global defaults", module);
            }
        }

        // fill in any missing properties with fields from the global file
        if (UtilValidate.isEmpty(httpsPort))
        {
            httpsPort = UtilProperties.getPropertyValue("url.properties", "port.https", "443");
        }
        if (UtilValidate.isEmpty(httpsServer))
        {
            httpsServer = UtilProperties.getPropertyValue("url.properties", "force.https.host");
        }
        if (UtilValidate.isEmpty(httpPort))
        {
            httpPort = UtilProperties.getPropertyValue("url.properties", "port.http", "80");
        }
        if (UtilValidate.isEmpty(httpServer))
        {
            httpServer = UtilProperties.getPropertyValue("url.properties", "force.http.host");
        }
        if (UtilValidate.isEmpty(enableHttps))
        {
            enableHttps = UtilProperties.propertyValueEqualsIgnoreCase("url.properties", "port.https.enabled", "Y");
        }

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

    public static String findUrlKeyByValue(String sURLvalue)
    {
        String URL = "";
        String solrURLParam=null;
        
        try
        {
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
        catch (Exception e)
        {
            //Debug.log(e, "Friendly URL mapping not found for: " + sURLvalue, module);
        }
        return URL;
    }

    public static String makeFullUrl(HttpServletRequest request,String url)
    {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.setLength(0); 
        urlBuilder.append(url);
        return makeLink(request, urlBuilder.toString());
    }

}

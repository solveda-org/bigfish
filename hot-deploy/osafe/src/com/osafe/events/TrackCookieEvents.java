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
package com.osafe.events;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.category.CategoryWorker;

import com.osafe.util.Util;
import com.sun.org.apache.bcel.internal.generic.LCONST;

/**
 * Events used for maintaining TrackingCode related information
 */
public class TrackCookieEvents {

    public static final String module = TrackCookieEvents.class.getName();

    
    
    /** If TrackingCode monitoring is desired this event should be added to the list
     * of events that run on every request. This event looks for the parameter
     * <code>autoTrackingCode</code> or a shortened version: <code>atc</code>.
     */
    /** If attaching TrackingCode Cookies to the visit is desired this event should be added to the list
     * of events that run on the first hit in a visit.
     */
    public static String checkoutConfirmCookies(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (UtilValidate.isNotEmpty(cookies)) 
        {
          String lCheckoutConfirmCookies  = Util.getProductStoreParm(request, "CHECKOUT_CONFIRM_GET_COOKIE");
          if (UtilValidate.isNotEmpty(lCheckoutConfirmCookies))
          {
           List<String> lCheckoutConfirmCookieNames = StringUtil.split(lCheckoutConfirmCookies, ",");
           for (String confirmCookieName: lCheckoutConfirmCookieNames) 
           {
	          boolean bFoundCookie=false;
	          for (Cookie cookie : cookies) 
		      {
			         if (confirmCookieName.equals(cookie.getName())) 
			         {
			           request.getSession().setAttribute(confirmCookieName, cookie.getValue());
			           bFoundCookie=true;
			           break;
			         }
		      }
	          if(!bFoundCookie)
              {
	            request.getSession().removeAttribute(confirmCookieName);
	          }
	        	  
           }
           
            	
          }
        }
    	
    	
        return "success";
    }




}

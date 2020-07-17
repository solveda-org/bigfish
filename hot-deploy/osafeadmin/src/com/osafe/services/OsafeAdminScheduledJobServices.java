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

import static org.ofbiz.base.util.UtilGenerics.checkCollection;

import static org.ofbiz.base.util.UtilGenerics.checkMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.service.calendar.RecurrenceRule;
import org.ofbiz.webapp.control.RequestHandler;
import org.ofbiz.webapp.control.ConfigXMLReader.Event;

import org.ofbiz.webapp.event.ServiceEventHandler;
import org.ofbiz.webapp.event.EventHandlerException;
import org.ofbiz.service.ServiceUtil;

import java.text.ParseException;

import com.ibm.icu.util.Calendar;
import com.osafe.util.OsafeAdminUtil;
import org.ofbiz.base.util.MessageString;

/**
 * OsafeAdminScheduledJobServices - WebApp Events Related To Framework pieces
 */
public class OsafeAdminScheduledJobServices {

    public static final String module = OsafeAdminScheduledJobServices.class.getName();
    public static final String err_resource = "OSafeAdminUiLabels";

    /**
     * Schedule a service for a specific time or recurrence
     *  Request Parameters which are used for this service:
     *
     *  SERVICE_NAME      - Name of the service to invoke
     *  SERVICE_TIME      - First time the service will occur
     *  SERVICE_FREQUENCY - The type of recurrence (SECONDLY,MINUTELY,DAILY,etc)
     *  SERVICE_INTERVAL  - The interval of the frequency (every 5 minutes, etc)
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Response code string
     */
    public static String scheduleService(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        List<MessageString> error_list = new ArrayList<MessageString>();
        MessageString tmp = null;
        Map<String, Object> params = UtilHttp.getParameterMap(request);
        // get the schedule parameters
        String jobName = (String) params.remove("JOB_NAME");
        String serviceName = (String) params.remove("SERVICE_NAME");
        String poolName = (String) params.remove("POOL_NAME");
        String serviceDate = (String) params.remove("SERVICE_DATE");
        String serviceEndTime = (String) params.remove("SERVICE_END_TIME");
        String serviceFreq = (String) params.remove("SERVICE_FREQUENCY");
        String serviceIntr = (String) params.remove("SERVICE_INTERVAL");
        String serviceCnt = (String) params.remove("SERVICE_COUNT");
        String retryCnt = (String) params.remove("SERVICE_MAXRETRY");
        
        String prefDateFormat = (String) params.remove("entryDateTimeFormat");
        //default to -1
        retryCnt = "-1";
      //set default to DAILY
        if(serviceFreq.equals("0"))
        {
        	serviceFreq = "";
        }
        else if(serviceFreq.equals("2"))
        {
        	serviceFreq = "MINUTELY";
        }
        else if(serviceFreq.equals("3"))
        {
            serviceFreq = "HOURLY";
        }
        else if(serviceFreq.equals("4"))
        {
            serviceFreq = "DAILY";
        }
        else if(serviceFreq.equals("5"))
        {
            serviceFreq = "WEEKLY";
        }
        else if(serviceFreq.equals("6"))
        {
            serviceFreq = "MONTHLY";
        }
        else if(serviceFreq.equals("7"))
        {
            serviceFreq = "YEARLY";
        }
        
        //validation
        //serviceName
        ModelService modelService = null;
        if (serviceName.length() == 0) 
        {
        	//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "MissingServiceNameError", locale));
        	tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "MissingServiceNameError", locale),"SERVICE_NAME",true);
        	error_list.add(tmp);
        }
        else
        {
        	// lookup the service definition to see if this service is externally available, if not require the SERVICE_INVOKE_ANY permission
            
            try 
            {
                modelService = dispatcher.getDispatchContext().getModelService(serviceName);
            } catch (GenericServiceException e) 
            {

            }
            if (modelService == null) 
            {
                //error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "InvalidServiceNameError1", locale));
                tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "InvalidServiceNameError1", locale),"SERVICE_NAME",true);
            	error_list.add(tmp);
            }
        }
        
        Map<String, Object> serviceContext = FastMap.newInstance();
     // make the context valid; using the makeValid method from ModelService
        if (UtilValidate.isNotEmpty(modelService)) 
        {
	        Iterator<String> ci = modelService.getInParamNames().iterator();
	        List<String> modelParamRequired = modelService.getParameterNames("IN", false, false);

	        while (ci.hasNext()) 
	        {
	            String name = ci.next();
	
	            // don't include userLogin, that's taken care of below
	            if ("userLogin".equals(name)) continue;
	            // don't include locale, that is also taken care of below
	            if ("locale".equals(name)) continue;
	
	            Object value = request.getParameter(name);
	
	            // if the parameter wasn't passed and no other value found, don't pass on the null
	            if (value == null) 
	            {
	                value = request.getAttribute(name);
	            }
	            if (value == null) 
	            {
	                value = request.getSession().getAttribute(name);
	            }
	            if (value == null) 
	            {
	            	if(modelParamRequired.contains(name))
	            	{	            		
	            		Map<String, String> errorArgs = FastMap.newInstance();
	            		errorArgs.put("serviceName", serviceName);
	            		errorArgs.put("name", name);
	            		//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "MissingParamError", errorArgs, locale));
	            		tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "MissingParamError", locale),"SERVICE_NAME",true);
	                	error_list.add(tmp);
	            	}
	            }
	
	            // set even if null so that values will get nulled in the db later on
	            serviceContext.put(name, value);
	        }
	        
	
	        if (userLogin != null) 
	        {
	            serviceContext.put("userLogin", userLogin);
	        }
	
	        if (locale != null) 
	        {
	            serviceContext.put("locale", locale);
	        }
        }

        
        //jobName
        if (jobName.length() == 0) 
        {
        	//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "MissingJobNameError", locale));
        	tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "MissingJobNameError", locale),"JOB_NAME",true);
        	error_list.add(tmp);
        }
 
        
        //check start datetime
        long starterTime = (new Date()).getTime();
        long endingTime = 0;
        String serviceDateTime = "";
        
        
        //check if input date is before today
        String serviceDateEOD = getValidJobDate(serviceDate, prefDateFormat);
        if (UtilValidate.isNotEmpty(serviceDateEOD)) 
        {
            if (!checkPassedJobDate(serviceDateEOD)) 
            {
                //error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "StartTimePassedError", locale));
            	tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "StartTimePassedError", locale),"SERVICE_DATE",true);
            	error_list.add(tmp);
            }
            else
            {
        		//SERVICE_TIME manipulation (serviceDate + serviceTime + serviceAMPM --> serviceDatetime)
                serviceDateTime = getValidJobDate(serviceDate, prefDateFormat);
                if (UtilValidate.isNotEmpty(serviceDateTime)) 
                {
                    if (!checkPassedJobDate(serviceDateTime)) 
                    {
                        //error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "StartTimePassedError", locale));
                        tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "StartTimePassedError", locale),"SERVICE_DATE",true);
                    	error_list.add(tmp);
                    }
                }
            }
        }
        else
        {
        	//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "BlankStartDateError", locale));
        	tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "BlankStartDateError", locale),"SERVICE_DATE",true);
        	error_list.add(tmp);
        }
        
        //check interval
        int intervals = 1;
        boolean validNumInt = true;
        if (UtilValidate.isNotEmpty(serviceIntr)) 
        {
            try 
            {
                intervals = Integer.parseInt(serviceIntr);
            } catch (NumberFormatException nfe) 
            {
            	//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "InvalidIntervalError", locale));
            	tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "InvalidIntervalError", locale),"SERVICE_INTERVAL",true);
            	error_list.add(tmp);
            	validNumInt = false;
            }
            //check if it is between 1 and 999
            if((!serviceFreq.equals("0")) && (validNumInt==true))
            {
            	if(intervals < 1 || intervals > 999)
            	{
            		//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "OutRangeIntervalError", locale));
            		tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "OutRangeIntervalError", locale),"SERVICE_INTERVAL",true);
                	error_list.add(tmp);
            	}
            }
        }
        //check freq count
        int counter = 1;
        boolean validNumFreq = true;
        if (UtilValidate.isNotEmpty(serviceCnt)) 
        {
            try 
            {
                counter = Integer.parseInt(serviceCnt);
            } catch (NumberFormatException nfe) {
            	//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "InvalidFrequencyCountError", locale));
            	tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "InvalidFrequencyCountError", locale),"SERVICE_FREQUENCY",true);
            	error_list.add(tmp);
            	validNumFreq = false;
            }
          //check if it is between 1 and 999
            if((!serviceFreq.equals("0")) && (validNumFreq == true))
            {
            	if(counter == -1 || ((counter > 0) && (counter < 1000)))
            	{
            		//do nothing
            	}
            	else
            	{
            		//error_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "OutRangeFreqError", locale));
            		tmp = new MessageString(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "OutRangeFreqError", locale),"SERVICE_FREQUENCY",true);
                	error_list.add(tmp);
            	}
            }
        }
        
        if(error_list.size() != 0)
        {//return error
        	request.setAttribute("_ERROR_MESSAGE_LIST_", error_list);
        	return "error";
        }
        
        //Code below is from CoreEvents.java scheduleService method

        // the frequency map
        Map<String, Integer> freqMap = FastMap.newInstance();

        freqMap.put("SECONDLY", Integer.valueOf(1));
        freqMap.put("MINUTELY", Integer.valueOf(2));
        freqMap.put("HOURLY", Integer.valueOf(3));
        freqMap.put("DAILY", Integer.valueOf(4));
        freqMap.put("WEEKLY", Integer.valueOf(5));
        freqMap.put("MONTHLY", Integer.valueOf(6));
        freqMap.put("YEARLY", Integer.valueOf(7));

        // some defaults
        long startTime = (new Date()).getTime();
        long endTime = 0;
        int maxRetry = -1;
        int count = 1;
        int interval = 1;
        int frequency = RecurrenceRule.DAILY;

        StringBuilder errorBuf = new StringBuilder();
        
        //serviceName exist and all needed params were passed
        serviceContext = modelService.makeValid(serviceContext, ModelService.IN_PARAM, true, null, timeZone, locale);

        // some conversions
        if (UtilValidate.isNotEmpty(serviceDateTime)) 
        {
            try 
            {
                Timestamp ts1 = Timestamp.valueOf(serviceDateTime);
                startTime = ts1.getTime();
            } catch (IllegalArgumentException e) 
            {
                try 
                {
                    startTime = Long.parseLong(serviceDateTime);
                } catch (NumberFormatException nfe) 
                {
                    String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.invalid_format_time", locale);
                    errorBuf.append("<li>" + errMsg);
                }
            }
            if (startTime < (new Date()).getTime()) 
            {
                String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.service_time_already_passed", locale);
                errorBuf.append("<li>" + errMsg);
            }
        }
        if (UtilValidate.isNotEmpty(serviceEndTime)) 
        {
            try 
            {
                Timestamp ts1 = Timestamp.valueOf(serviceEndTime);
                endTime = ts1.getTime();
            } catch (IllegalArgumentException e) 
            {
                try {
                    endTime = Long.parseLong(serviceDateTime);
                } catch (NumberFormatException nfe) {
                    String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.invalid_format_time", locale);
                    errorBuf.append("<li>" + errMsg);
                }
            }
            if (endTime < (new Date()).getTime()) 
            {
                String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.service_time_already_passed", locale);
                errorBuf.append("<li>" + errMsg);
            }
        }
        if (UtilValidate.isNotEmpty(serviceIntr)) 
        {
            try 
            {
                interval = Integer.parseInt(serviceIntr);
            } catch (NumberFormatException nfe) 
            {
                String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.invalid_format_interval", locale);
                errorBuf.append("<li>" + errMsg);
            }
        }
        if (UtilValidate.isNotEmpty(serviceCnt)) 
        {
            try {
                count = Integer.parseInt(serviceCnt);
            } catch (NumberFormatException nfe) 
            {
                String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.invalid_format_count", locale);
                errorBuf.append("<li>" + errMsg);
            }
        }
        if (UtilValidate.isNotEmpty(serviceFreq)) 
        {
            int parsedValue = 0;

            try 
            {
                parsedValue = Integer.parseInt(serviceFreq);
                if (parsedValue > 0 && parsedValue < 8)
                    frequency = parsedValue;
            } catch (NumberFormatException nfe) 
            {
                parsedValue = 0;
            }
            if (parsedValue == 0) 
            {
                if (!freqMap.containsKey(serviceFreq.toUpperCase())) 
                {
                    String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.invalid_format_frequency", locale);
                    errorBuf.append("<li>" + errMsg);
                } else 
                {
                    frequency = freqMap.get(serviceFreq.toUpperCase()).intValue();
                }
            }
        }
        if (UtilValidate.isNotEmpty(retryCnt)) 
        {
            int parsedValue = -2;

            try 
            {
                parsedValue = Integer.parseInt(retryCnt);
            } catch (NumberFormatException e) 
            {
                parsedValue = -2;
            }
            if (parsedValue > -2) 
            {
                maxRetry = parsedValue;
            } else 
            {
                maxRetry = modelService.maxRetry;
            }
        } 
        else 
        {
            maxRetry = modelService.maxRetry;
        }

        // return the errors
        if (errorBuf.length() > 0) 
        {
            request.setAttribute("_ERROR_MESSAGE_", errorBuf.toString());
            return "error";
        }

        Map<String, Object> syncServiceResult = null;
        // schedule service
        try 
        {
            if (null!=request.getParameter("_RUN_SYNC_") && request.getParameter("_RUN_SYNC_").equals("Y")) 
            {
                syncServiceResult = dispatcher.runSync(serviceName, serviceContext);
            } else 
            {
                dispatcher.schedule(jobName, poolName, serviceName, serviceContext, startTime, frequency, interval, count, endTime, maxRetry);
            }
        } catch (GenericServiceException e) 
        {
            String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.service_dispatcher_exception", locale);
            request.setAttribute("_ERROR_MESSAGE_", "<li>" + errMsg + e.getMessage());
            return "error";
        }

        String errMsg = UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "coreEvents.service_scheduled", locale);
        request.setAttribute("_EVENT_MESSAGE_", errMsg);
        if (null!=syncServiceResult) 
        {
            request.getSession().setAttribute("_RUN_SYNC_RESULT_", syncServiceResult);
            return "sync_success";
        }
        List<String> success_list = new ArrayList<String>();
        success_list.add(UtilProperties.getMessage(OsafeAdminScheduledJobServices.err_resource, "SuccessSchedulJob", locale));
        request.setAttribute("osafeSuccessMessageList", success_list);
        return "success";
    }


    public static String getValidJobDate(String serviceDate, String prefDateFormat)
    {
    	 //check start datetime
        String serviceDateTime = "";
        Timestamp serviceDateTs = OsafeAdminUtil.toTimestamp(serviceDate, prefDateFormat);
        if (UtilValidate.isNotEmpty(serviceDateTs))
        {
            serviceDateTime = serviceDateTs.toString();
        }
        return serviceDateTime;
    }


    public static String getValidJobDate(String serviceDate, String serviceHour, String serviceMinute, String serviceAMPMString, String prefDateFormat)
    {
    	 //check start datetime
        String serviceDateTime = "";
        if(UtilValidate.isNotEmpty(serviceDate) && UtilValidate.isNotEmpty(serviceHour) && UtilValidate.isNotEmpty(serviceMinute) && UtilValidate.isNotEmpty(serviceAMPMString))
        {
	        Timestamp serviceDateTs = OsafeAdminUtil.toTimestamp(serviceDate, prefDateFormat);
	        if (UtilValidate.isNotEmpty(serviceDateTs)) 
	        {
	        	int hour = Integer.parseInt(serviceHour);
		        if (serviceAMPMString.equals("1")) 
		        {
		        	hour = hour%12;
		        } else if(serviceAMPMString.equals("2")) 
		        {
		        	hour = (hour%12)+12;
		        }
	        	int minute = Integer.parseInt(serviceMinute);
	        	int addTimeInMilliSec = (hour*60*60*1000)+(minute*60*1000);
	        	//serviceDateTs = new Timestamp(serviceDateTs.getTime() + addTimeInMilliSec);
		        serviceDateTime = serviceDateTs.toString();
	        }
        }
        return serviceDateTime;
    }
    
    public static boolean checkPassedJobDate(String date)
    {
    	 long starterTime = (new Date()).getTime();
         //check start datetime
         if (UtilValidate.isNotEmpty(date)) 
         {
             try 
             {
                 Timestamp ts1 = Timestamp.valueOf(date);
                 starterTime = ts1.getTime();
             } 
             catch (IllegalArgumentException e) 
             {
                 try 
                 {
                 	starterTime = Long.parseLong(date);
                 } catch (NumberFormatException nfe) 
                 {
                     return false;
                 }
             }
             if (starterTime < (new Date()).getTime()) 
             {
                 return false;
             }
         }
         return true;
    }
    
    public static boolean checkValidServiceName(String serviceName, LocalDispatcher dispatch)
    //public static boolean checkValidServiceName(String serviceName)
    {
        	// lookup the service definition to see if this service is externally available, if not require the SERVICE_INVOKE_ANY permission
            ModelService modelServices = null;
            LocalDispatcher dispatcher = dispatch;
            try 
            {
                modelServices = dispatcher.getDispatchContext().getModelService(serviceName);
            } catch (GenericServiceException e) 
            {

            }
            if (modelServices == null) 
            {
                return false;
            }
        return true;
    }
    
    public static boolean checkValidInterval(String serviceIntr)
    {
    	 int intervals = 1;
         if (UtilValidate.isNotEmpty(serviceIntr)) 
         {
             try 
             {
                 intervals = Integer.parseInt(serviceIntr);
             } catch (NumberFormatException nfe) 
             {
             	return false;
             }
         }
         return true;
    }
    
    public static boolean checkValidIntervalRange(String serviceIntr, String serviceFreq)
    {
    	 int intervals = 1;
         boolean validNumInt = true;
         if (UtilValidate.isNotEmpty(serviceIntr)) 
         {
             try 
             {
                 intervals = Integer.parseInt(serviceIntr);
             } catch (NumberFormatException nfe) 
             {
             	validNumInt = false;
             }
             //check if it is between 1 and 999
             if((!serviceFreq.equals("0")) && (validNumInt==true))
             {
             	if(intervals < 1 || intervals > 999)
             	{
             		return false;
             	}
             }
         }
         return true;
    }
    
    public static boolean checkValidCountRange(String serviceCnt, String serviceFreq)
    {
    	int counter = 1;
        boolean validNumFreq = true;
        if (UtilValidate.isNotEmpty(serviceCnt)) 
        {
            try 
            {
                counter = Integer.parseInt(serviceCnt);
            } catch (NumberFormatException nfe) 
            {
            	validNumFreq = false;
            }
          //check if it is between 1 and 999
            if((!serviceFreq.equals("0")) && (validNumFreq == true))
            {
            	if(counter == 0 || ((counter > 1) || (counter < 999)))
            	{
            		//do nothing
            	}
            	else
            	{
            		return false;
            	}
            }
        }
         return true;
    }

}

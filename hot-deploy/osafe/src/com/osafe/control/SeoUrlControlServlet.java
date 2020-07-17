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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilJ2eeCompat;
import org.ofbiz.base.util.UtilTimer;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.security.authz.Authorization;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.control.ControlServlet;
import org.ofbiz.webapp.control.RequestHandler;
import org.ofbiz.webapp.control.RequestHandlerException;
import org.ofbiz.webapp.stats.ServerHitBin;
import org.ofbiz.webapp.stats.VisitHandler;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.ServletContextHashModel;

/**
 * SeoControlServlet.java - SEO Master servlet for the web application.
 */
@SuppressWarnings("serial")
public class SeoUrlControlServlet extends ControlServlet
{

    public static final String module = SeoUrlControlServlet.class.getName();

    public SeoUrlControlServlet()
    {
        super();
    }

    /**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        doGet(request, response);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String pathInfo = request.getPathInfo();
        String controlRequest = null;
        try 
        {
            String sURL = SeoUrlHelper.findUrlKeyByValue(pathInfo);
            int idxUrl = sURL.indexOf("?");
            if (idxUrl > -1)
            {
                controlRequest = sURL.substring(0,idxUrl);
                int idxFirstGroupAttribute = sURL.indexOf("=",idxUrl);
                int idxNextParamGroup = -1;
                String paramGroupValue = null;
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
                RequestDispatcher rd = request.getRequestDispatcher("/" + controlRequest); 
                rd.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
            Debug.logError(e, "Error looking up  path info [" + pathInfo + "]: " + e.toString(), module);
        }
        
        // we're done checking; continue on
        long requestStartTime = System.currentTimeMillis();
        RequestHandler requestHandler = this.getRequestHandler();
        HttpSession session = request.getSession();

        // setup DEFAULT chararcter encoding and content type, this will be overridden in the RequestHandler for view rendering
        String charset = getServletContext().getInitParameter("charset");
        if (UtilValidate.isEmpty(charset))
        {
        	charset = request.getCharacterEncoding();
        }
        if (UtilValidate.isEmpty(charset))
        {
        	charset = "UTF-8";
        }
        if (Debug.verboseOn())
        {
        	Debug.logVerbose("The character encoding of the request is: [" + request.getCharacterEncoding() + "]. The character encoding we will use for the request and response is: [" + charset + "]", module);
        }

        if (!"none".equals(charset)) 
        {
            request.setCharacterEncoding(charset);
        }

        // setup content type
        String contentType = "text/html";
        if (charset.length() > 0 && !"none".equals(charset)) 
        {
            response.setContentType(contentType + "; charset=" + charset);
            response.setCharacterEncoding(charset);
        } 
        else 
        {
            response.setContentType(contentType);
        }

        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        //Debug.logInfo("Cert Chain: " + request.getAttribute("javax.servlet.request.X509Certificate"), module);

        // set the Entity Engine user info if we have a userLogin
        if (UtilValidate.isNotEmpty(userLogin)) 
        {
            GenericDelegator.pushUserIdentifier(userLogin.getString("userLoginId"));
        }

        // workaraound if we are in the root webapp
        String webappName = UtilHttp.getApplicationName(request);

        String rname = "";
        if (UtilValidate.isNotEmpty(request.getPathInfo())) 
        {
            rname = request.getPathInfo().substring(1);
        }
        if (rname.indexOf('/') > 0) 
        {
            rname = rname.substring(0, rname.indexOf('/'));
        }

        UtilTimer timer = null;
        if (Debug.timingOn()) 
        {
            timer = new UtilTimer();
            timer.setLog(true);
            timer.timerString("[" + rname + "] Request Begun, encoding=[" + charset + "]", module);
        }

        // Setup the CONTROL_PATH for JSP dispatching.
        String contextPath = request.getContextPath();
        if (UtilValidate.isEmpty(contextPath) || "/".equals(contextPath)) 
        {
            contextPath = "";
        }
        request.setAttribute("_CONTROL_PATH_", contextPath + request.getServletPath());
        if (Debug.verboseOn())
        {
            Debug.logVerbose("Control Path: " + request.getAttribute("_CONTROL_PATH_"), module);
        }

        // for convenience, and necessity with event handlers, make security and delegator available in the request:
        // try to get it from the session first so that we can have a delegator/dispatcher/security for a certain user if desired
        Delegator delegator = null;
        String delegatorName = (String) session.getAttribute("delegatorName");
        if (UtilValidate.isNotEmpty(delegatorName)) 
        {
            delegator = DelegatorFactory.getDelegator(delegatorName);
        }
        if (UtilValidate.isEmpty(delegator)) 
        {
            delegator = (Delegator) getServletContext().getAttribute("delegator");
        }
        if (UtilValidate.isEmpty(delegator)) 
        {
            Debug.logError("[ControlServlet] ERROR: delegator not found in ServletContext", module);
        } 
        else 
        {
            request.setAttribute("delegator", delegator);
            // always put this in the session too so that session events can use the delegator
            session.setAttribute("delegatorName", delegator.getDelegatorName());
        }

        LocalDispatcher dispatcher = (LocalDispatcher) session.getAttribute("dispatcher");
        if (UtilValidate.isEmpty(dispatcher)) 
        {
            dispatcher = (LocalDispatcher) getServletContext().getAttribute("dispatcher");
        }
        if (UtilValidate.isEmpty(dispatcher)) 
        {
            Debug.logError("[ControlServlet] ERROR: dispatcher not found in ServletContext", module);
        }
        request.setAttribute("dispatcher", dispatcher);

        Authorization authz = (Authorization) session.getAttribute("authz");
        if (UtilValidate.isEmpty(authz))
        {
            authz = (Authorization) getServletContext().getAttribute("authz");
        }
        if (UtilValidate.isEmpty(authz))
        {
            Debug.logError("[ControlServlet] ERROR: authorization not found in ServletContext", module);
        }
        request.setAttribute("authz", authz); // maybe we should also add the value to 'security'

        Security security = (Security) session.getAttribute("security");
        if (UtilValidate.isEmpty(security))
        {
            security = (Security) getServletContext().getAttribute("security");
        }
        if (UtilValidate.isEmpty(security))
        {
            Debug.logError("[ControlServlet] ERROR: security not found in ServletContext", module);
        }
        request.setAttribute("security", security);

        request.setAttribute("_REQUEST_HANDLER_", requestHandler);

        ServletContextHashModel ftlServletContext = new ServletContextHashModel(this, BeansWrapper.getDefaultInstance());
        request.setAttribute("ftlServletContext", ftlServletContext);

        // setup some things that should always be there
        UtilHttp.setInitialRequestInfo(request);
        VisitHandler.getVisitor(request, response);

        // set the Entity Engine user info if we have a userLogin
        String visitId = VisitHandler.getVisitId(session);
        if (UtilValidate.isNotEmpty(visitId)) 
        {
            GenericDelegator.pushSessionIdentifier(visitId);
        }

        // display details on the servlet objects
        if (Debug.verboseOn()) 
        {
            logRequestInfo(request);
        }

        // some containers call filters on EVERY request, even forwarded ones, so let it know that it came from the control servlet
        request.setAttribute(ContextFilter.FORWARDED_FROM_SERVLET, Boolean.TRUE);

        String errorPage = null;
        try 
        {
            // the ServerHitBin call for the event is done inside the doRequest method
            requestHandler.doRequest(request, response, null, userLogin, delegator);
        }
        catch (RequestHandlerException e)
        {
            Throwable throwable = UtilValidate.isNotEmpty(e.getNested()) ? e.getNested() : e;
            if (throwable instanceof IOException) 
            {
                // when an IOException occurs (most of the times caused by the browser window being closed before the request is completed)
                // the connection with the browser is lost and so there is no need to serve the error page; a message is logged to record the event
                if (Debug.warningOn())
                {
                	Debug.logWarning("Communication error with the client while processing the request: " + request.getAttribute("_CONTROL_PATH_") + request.getPathInfo(), module);
                }
                if (Debug.verboseOn())
                {
                	Debug.logVerbose(throwable, module);
                }
            } 
            else 
            {
                if (Debug.verboseOn())
                {
                   Debug.logVerbose(throwable, "Error in request handler: ", module);
                }
                StringUtil.HtmlEncoder encoder = new StringUtil.HtmlEncoder();
                request.setAttribute("_ERROR_MESSAGE_", encoder.encode(throwable.toString()));
                errorPage = requestHandler.getDefaultErrorPage(request);
            }
        } 
        catch (Exception e)
        {
            Debug.logError(e, "Error in request handler: ", module);
            StringUtil.HtmlEncoder encoder = new StringUtil.HtmlEncoder();
            request.setAttribute("_ERROR_MESSAGE_", encoder.encode(e.toString()));
            errorPage = requestHandler.getDefaultErrorPage(request);
        }

        // Forward to the JSP
        // if (Debug.infoOn()) Debug.logInfo("[" + rname + "] Event done, rendering page: " + nextPage, module);
        // if (Debug.timingOn()) timer.timerString("[" + rname + "] Event done, rendering page: " + nextPage, module);

        if (UtilValidate.isNotEmpty(errorPage))
        {
            Debug.logError("An error occurred, going to the errorPage: " + errorPage, module);

            RequestDispatcher rd = request.getRequestDispatcher(errorPage);

            // use this request parameter to avoid infinite looping on errors in the error page...
            if (UtilValidate.isEmpty(request.getAttribute("_ERROR_OCCURRED_")) && UtilValidate.isNotEmpty(rd)) 
            {
                request.setAttribute("_ERROR_OCCURRED_", Boolean.TRUE);
                Debug.logError("Including errorPage: " + errorPage, module);

                // NOTE DEJ20070727 after having trouble with all of these, try to get the page out and as a last resort just send something back
                try 
                {
                    rd.forward(request, response);
                    return;
                }
                catch (Throwable t)
                {
                    Debug.logWarning("Error while trying to send error page using rd.include (will try response.getOutputStream or response.getWriter): " + t.toString(), module);

                    String errorMessage = "ERROR rendering error page [" + errorPage + "], but here is the error text: " + request.getAttribute("_ERROR_MESSAGE_");
                    try 
                    {
                        if (UtilJ2eeCompat.useOutputStreamNotWriter(getServletContext())) 
                        {
                            response.getOutputStream().print(errorMessage);
                        } 
                        else 
                        {
                            response.getWriter().print(errorMessage);
                        }
                    }
                    catch (Throwable t2)
                    {
                        try 
                        {
                            int errorToSend = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                            Debug.logWarning("Error while trying to write error message using response.getOutputStream or response.getWriter: " + t.toString() + "; sending error code [" + errorToSend + "], and message [" + errorMessage + "]", module);
                            response.sendError(errorToSend, errorMessage);
                        }
                        catch (Throwable t3)
                        {
                            // wow, still bad... just throw an IllegalStateException with the message and let the servlet container handle it
                            throw new IllegalStateException(errorMessage);
                        }
                    }
                }

            } 
            else 
            {
            	if (UtilValidate.isEmpty(rd))
            	{
                    Debug.logError("Could not get RequestDispatcher for errorPage: " + errorPage, module);
                }

                String errorMessage = "<html><body>ERROR in error page, (infinite loop or error page not found with name [" + errorPage + "]), but here is the text just in case it helps you: " + request.getAttribute("_ERROR_MESSAGE_") + "</body></html>";
                if (UtilJ2eeCompat.useOutputStreamNotWriter(getServletContext()))
                {
                    response.getOutputStream().print(errorMessage);
                } 
                else 
                {
                    response.getWriter().print(errorMessage);
                }
            }
        }

        // sanity check: make sure we don't have any transactions in place
        try 
        {
            // roll back current TX first
            if (TransactionUtil.isTransactionInPlace()) 
            {
                Debug.logWarning("*** NOTICE: ControlServlet finished w/ a transaction in place! Rolling back.", module);
                TransactionUtil.rollback();
            }

            // now resume/rollback any suspended txs
            if (TransactionUtil.suspendedTransactionsHeld()) 
            {
                int suspended = TransactionUtil.cleanSuspendedTransactions();
                Debug.logWarning("Resumed/Rolled Back [" + suspended + "] transactions.", module);
            }
        } catch (GenericTransactionException e) {
            Debug.logWarning(e, module);
        }

        // run these two again before the ServerHitBin.countRequest call because on a logout this will end up creating a new visit
        if (response.isCommitted() && UtilValidate.isEmpty(request.getSession(false))) 
        {
            // response committed and no session, and we can't get a new session, what to do!
            // without a session we can't log the hit, etc; so just do nothing; this should NOT happen much!
            Debug.logError("Error in ControlServlet output where response isCommitted and there is no session (probably because of a logout); not saving ServerHit/Bin information because there is no session and as the response isCommitted we can't get a new one. The output was successful, but we just can't save ServerHit/Bin info.", module);
        } 
        else 
        {
            try 
            {
                UtilHttp.setInitialRequestInfo(request);
                VisitHandler.getVisitor(request, response);
                if (requestHandler.trackStats(request)) 
                {
                    ServerHitBin.countRequest(webappName + "." + rname, request, requestStartTime, System.currentTimeMillis() - requestStartTime, userLogin);
                }
            }
            catch (Throwable t)
            {
                Debug.logError(t, "Error in ControlServlet saving ServerHit/Bin information; the output was successful, but can't save this tracking information. The error was: " + t.toString(), module);
            }
        }
        if (Debug.timingOn())
        {
        	timer.timerString("[" + rname + "] Request Done", module);
        }

        // sanity check 2: make sure there are no user or session infos in the delegator, ie clear the thread
        GenericDelegator.clearUserIdentifierStack();
        GenericDelegator.clearSessionIdentifierStack();
    }
}

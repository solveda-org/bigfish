/*
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
 */
package com.osafe.services;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * WebTools Services
 */

public class OsafeWebToolsServices {

    public static final String module = OsafeWebToolsServices.class.getName();

    public static Map<String, Object> entityImportDir(DispatchContext dctx, Map<String, ? extends Object> context) 
    {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = dctx.getDispatcher();

        List<String> messages = FastList.newInstance();

        String path = (String) context.get("path");
        String mostlyInserts = (String) context.get("mostlyInserts");
        String maintainTimeStamps = (String) context.get("maintainTimeStamps");
        String createDummyFks = (String) context.get("createDummyFks");
        boolean deleteFiles = (String) context.get("deleteFiles") != null;
        String checkDataOnly = (String) context.get("checkDataOnly");

        Integer txTimeout = (Integer)context.get("txTimeout");
        Long filePause = (Long)context.get("filePause");

        if (txTimeout == null) 
        {
            txTimeout = Integer.valueOf(7200);
        }
        if (filePause == null) 
        {
            filePause = Long.valueOf(0);
        }

        if (UtilValidate.isNotEmpty(path)) 
        {
            long pauseLong = filePause != null ? filePause.longValue() : 0;
            File baseDir = new File(path);

            if (baseDir.isDirectory() && baseDir.canRead()) 
            {
                File[] fileArray = baseDir.listFiles();
                FastList<File> files = FastList.newInstance();
                
                for (File file: fileArray) 
                {
                    if (file.getName().toUpperCase().endsWith("XML")) {
                        files.add(file);
                    }
                }
                
                Collections.sort(files, new Comparator<File>() 
                {
                    public int compare(File f1, File f2)
                    {
                        return f1.getName().compareTo(f2.getName());
                    } 
                });
                
                int passes=0;
                int initialListSize = files.size();
                int lastUnprocessedFilesCount = 0;
                FastList<File> unprocessedFiles = FastList.newInstance();
                while (files.size()>0 && files.size() != lastUnprocessedFilesCount) 
                {
                    lastUnprocessedFilesCount = files.size();
                    unprocessedFiles = FastList.newInstance();
                    for (File f: files) 
                    {
                        Map<String, Object> parseEntityXmlFileArgs = UtilMisc.toMap("mostlyInserts", mostlyInserts,
                                "createDummyFks", createDummyFks,
                                "checkDataOnly", checkDataOnly,
                                "maintainTimeStamps", maintainTimeStamps,
                                "txTimeout", txTimeout,
                                "userLogin", userLogin);

                        try 
                        {
                            URL furl = f.toURI().toURL();
                            parseEntityXmlFileArgs.put("url", furl);
                            Map<String, Object> outputMap = dispatcher.runSync("parseEntityXmlFile", parseEntityXmlFileArgs);
                            if(UtilValidate.isNotEmpty(outputMap.get("responseMessage"))&& outputMap.get("responseMessage").equals("error"))
                            {
                            	return ServiceUtil.returnError(outputMap.get("errorMessage").toString());
                            }
                            Long numberRead = (Long) outputMap.get("rowProcessed");
                            messages.add("Got " + numberRead.longValue() + " entities from " + f);
                            if (deleteFiles) 
                            {
                                messages.add("Deleting " + f);
                                f.delete();
                            }
                        } 
                        catch (Exception e) 
                        {
                            unprocessedFiles.add(f);
                            messages.add("Failed " + f + " adding to retry list for next pass");
                        }
                        // pause in between files
                        if (pauseLong > 0) 
                        {
                            Debug.log("Pausing for [" + pauseLong + "] seconds - " + UtilDateTime.nowTimestamp());
                            try 
                            {
                                Thread.sleep((pauseLong * 1000));
                            } 
                            catch (InterruptedException ie) 
                            {
                                Debug.log("Pause finished - " + UtilDateTime.nowTimestamp());
                            }
                        }
                    }
                    files = unprocessedFiles;
                    passes++;
                    messages.add("Pass " + passes + " complete");
                    Debug.logInfo("Pass " + passes + " complete", module);
                }
                lastUnprocessedFilesCount=unprocessedFiles.size();
                messages.add("---------------------------------------");
                messages.add("Succeeded: " + (initialListSize-lastUnprocessedFilesCount) + " of " + initialListSize);
                messages.add("Failed:    " + lastUnprocessedFilesCount + " of " + initialListSize);
                messages.add("---------------------------------------");
                messages.add("Failed Files:");
                if(lastUnprocessedFilesCount == 0) 
                {
                    messages.add("SUCCESS");
                } 
                else 
                {
                    messages.add("ERROR");
                }
                for (File file: unprocessedFiles) 
                {
                    messages.add(file.toString());
                }
            } 
            else 
            {
                messages.add("path not found or can't be read");
            }
        } 
        else 
        {
            messages.add("No path specified, doing nothing.");
        }
        // send the notification
        Map<String, Object> resp = UtilMisc.toMap("messages", (Object) messages);
        return resp;
    }
    
}

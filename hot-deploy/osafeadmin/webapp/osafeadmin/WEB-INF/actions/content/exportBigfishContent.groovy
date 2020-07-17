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

import java.util.*;
import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import org.ofbiz.security.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.webapp.pseudotag.*;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.transaction.*;
import org.ofbiz.entity.condition.*;
import com.osafe.util.OsafeAdminUtil;

tmpDir = FileUtil.getFile("runtime/tmp");
exportFileName = "bigfish-content-export"+(OsafeAdminUtil.convertDateTimeFormat(UtilDateTime.nowTimestamp(), "yyyyMMdd-HHmm"))+".xml";
exportFile = new File(tmpDir, exportFileName);
if(!exportFile.exists()) {
    exportFile.createNewFile();
}

productStoreId = parameters.productStoreId;
passedContentTypeIds = parameters.contentTypeId instanceof Collection ? parameters.contentTypeId as TreeSet : [parameters.contentTypeId] as TreeSet;
context.passedContentTypeIds = passedContentTypeIds;
numberOfContentTypeIds = passedContentTypeIds?.size() ?: 0;
context.numberOfContentTypeIds = numberOfContentTypeIds;

numberWritten = 0;
if (exportFile && numberOfContentTypeIds) {

    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile.getAbsolutePath()), "UTF-8")));
    writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.println("<entity-engine-xml>");

    for(contentTypeId in passedContentTypeIds) {
        beganTransaction = TransactionUtil.begin(3600);
        try {
            TransactionUtil.commit(beganTransaction);
            contentType = delegator.findByPrimaryKey("ContentType", ["contentTypeId" : contentTypeId]);
            if (contentType!= null) {
                contentType.writeXmlText(writer, "");
                numberWritten++;
                findXContentXrefMap = ["productStoreId" : productStoreId, "contentTypeId" : contentTypeId];
                xContentXrefList = delegator.findByAnd("XContentXref", findXContentXrefMap);
                for(xContentXref in xContentXrefList) {
                    content = xContentXref.getRelatedOne("Content");
                    if (content != null) {
                        content.writeXmlText(writer, "");
                        numberWritten++;
                        xContentXref.writeXmlText(writer, "");
                        numberWritten++;
                        dataResource = content.getRelatedOne("DataResource");
                        if (dataResource != null) {
                            dataResource.writeXmlText(writer, "");
                            numberWritten++;
                            electronicText = dataResource.getRelatedOne("ElectronicText");
                            if (electronicText != null) {
                                electronicText.writeXmlText(writer, "");
                                numberWritten++;
                            }
                        }
                    }
                
                }
            }
        } catch (Exception exc) {
            String thisResult = "wrote $numberWritten records";
            Debug.logError(exc, thisResult, "JSP");
            TransactionUtil.rollback(beganTransaction, thisResult, exc);
        }finally {
            // only commit the transaction if we started one... this will throw an exception if it fails
            TransactionUtil.commit(beganTransaction);
        } 
    }
    writer.println("</entity-engine-xml>");
    writer.close();
    Debug.log("Total records written from all entities: $numberWritten");
    context.numberWritten = numberWritten;

    /*Send xml for browser.*/
    response.setContentType("text/xml");
    response.setHeader("Content-Disposition","attachment; filename=\"" + exportFileName + "\";");

    InputStream inputStr = new FileInputStream(exportFile.getAbsolutePath());
    OutputStream out = response.getOutputStream();
    byte[] bytes = new byte[102400];
    int bytesRead;
    while ((bytesRead = inputStr.read(bytes)) != -1)
    {
        out.write(bytes, 0, bytesRead);
    }
    out.flush();
    out.close();
    inputStr.close();
}


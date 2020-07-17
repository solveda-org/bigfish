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
import javolution.util.FastList;

outpath = parameters.outpath;
filename = "c:/clients/bigfish/moda/test.xml";
maxRecStr = parameters.maxrecords;
entitySyncId = parameters.entitySyncId;
passedEntityNames = parameters.entityName instanceof Collection ? parameters.entityName as TreeSet : [parameters.entityName] as TreeSet;

String writeValues(EntityListIterator values, PrintWriter writer,String curEntityName) {
            try {
                curNumberWritten = 0;
                 numberWritten = 0;
                valuesIter = values.iterator();
                while ((value = valuesIter.next()) != null) {
                    value.writeXmlText(writer, "");
                    numberWritten++;
                    curNumberWritten++;
                    if (curNumberWritten % 500 == 0 || curNumberWritten == 1) {
                        Debug.log("Records written [" +  curEntityName + "]: $curNumberWritten Total: $numberWritten");
                    }
                }
                Debug.log("Wrote [$curNumberWritten] from entity :" + curEntityName);
            } catch (Exception e) {
                errMsg = "Error reading data for XML export:";
                Debug.logError(e, errMsg, "JSP");
            }
    return "return";
}


// get the max records per file setting and convert to a int
maxRecordsPerFile = 0;
if (maxRecStr) {
    try {
        maxRecordsPerFile = Integer.parseInt(maxRecStr);
    }
    catch (Exception e) {
    }
}
orderBy = ["contentId"];
spotListMenuId=context.spotListMenuId;
List contentAssocList = FastList.newInstance();
context.userLoginId = userLogin.userLoginId;
passedEntityNames = new LinkedHashSet();
passedEntityNames.add("Content");



contentCond = null;
dataResourceCond = null;
if(UtilValidate.isNotEmpty(spotListMenuId))
 {
	List conds = FastList.newInstance();
	conds.add(EntityCondition.makeCondition("contentId", spotListMenuId));
	contentAssocList = delegator.findList("ContentAssoc",EntityCondition.makeCondition(conds), null, orderBy, null, false);
    if (UtilValidate.isNotEmpty(contentAssocList))
     {
       condsExpr = FastList.newInstance();
       List condExprList = [];
       List dataResourceExprList = [];
       for (GenericValue contentAssoc: contentAssocList) 
       {
          condExprList.add(EntityCondition.makeCondition("contentId", EntityOperator.EQUALS,contentAssoc.contentIdTo));
          dataResourceExprList.add(EntityCondition.makeCondition("dataResourceId", EntityOperator.EQUALS,contentAssoc.contentIdTo));
       }
       contentCond = EntityCondition.makeCondition(condExprList, EntityOperator.OR); 
       dataResourceCond = EntityCondition.makeCondition(dataResourceExprList, EntityOperator.OR); 
     }
 }


reader = delegator.getModelReader();
modelEntities = reader.getEntityCache().values() as TreeSet;
context.modelEntities = modelEntities;

    efo = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
    numberOfEntities = passedEntityNames?.size() ?: 0;
    context.numberOfEntities = numberOfEntities;
    numberWritten = 0;

    // single file
    if (filename && numberOfEntities) 
    {
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")));
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<entity-engine-xml>");

        passedEntityNames.each { curEntityName ->

            beganTransaction = TransactionUtil.begin(3600);
            try {
                me = reader.getModelEntity(curEntityName);
                drValues = delegator.find("DataResource", dataResourceCond, null, null, UtilMisc.toList("-createdTxStamp"), efo);
                writeValues(drValues,writer,"DataResource");
                drValues.close();
                etValues = delegator.find("ElectronicText", dataResourceCond, null, null, UtilMisc.toList("-createdTxStamp"), efo);
                writeValues(etValues,writer,"ElectronicText");
                etValues.close();
                contentValues = delegator.find("Content", contentCond, null, null, UtilMisc.toList("-createdTxStamp"), efo);
                writeValues(contentValues,writer,"Content");
                contentValues.close();
                //Debug.log("Wrote [$curNumberWritten] from entity : $curEntityName");
                TransactionUtil.commit(beganTransaction);
            } catch (Exception e) {
                errMsg = "Error reading data for XML export:";
                Debug.logError(e, errMsg, "JSP");
                TransactionUtil.rollback(beganTransaction, errMsg, e);
            }
        }
        writer.println("</entity-engine-xml>");
        writer.close();
        Debug.log("Total records written from all entities: $numberWritten");
        context.numberWritten = numberWritten;
    }






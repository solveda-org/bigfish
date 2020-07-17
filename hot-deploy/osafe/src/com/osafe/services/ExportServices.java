package com.osafe.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ExportServices {

    public static final String module = ExportServices.class.getName();

    public static Map<String, Object> exportCommunicationEvent(DispatchContext dctx, Map<String, ?> context) {
        Delegator delegator = dctx.getDelegator();

        String outpath = (String)context.get("outpath"); // mandatory
        List<String> results = FastList.newInstance();
        String commEventId=null;

        if (UtilValidate.isNotEmpty(outpath)) {
            File outdir = new File(outpath);
            if (!outdir.exists()) {
                outdir.mkdir();
            }
            if (outdir.isDirectory() && outdir.canWrite()) {

                String entityName = "CommunicationEvent";
                long numberWritten = 0;
                EntityListIterator values = null;
                WritableWorkbook workbook = null;

                List conditionExprs = FastList.newInstance();
                conditionExprs.add(EntityCondition.makeCondition("communicationEventTypeId", EntityOperator.EQUALS, "EMAIL_COMMUNICATION"));
                conditionExprs.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"));
                List orExprs = FastList.newInstance();
                String cntctUsSbjStr = "%Contact us%".toUpperCase();
                String reqCatSbjStr = "%Request Catalog%".toUpperCase();
                orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("subject"), EntityOperator.LIKE, cntctUsSbjStr));
                orExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("subject"), EntityOperator.LIKE, reqCatSbjStr));
                conditionExprs.add(EntityCondition.makeCondition(orExprs, EntityOperator.OR));
                EntityConditionList whereEntityCondition = EntityCondition.makeCondition(conditionExprs, EntityOperator.AND);

                try {
                    boolean beganTx = TransactionUtil.begin();
                    try {
                        values = delegator.find(entityName, whereEntityCondition, null, null, UtilMisc.toList("communicationEventId"), null);
                    } catch (Exception entityEx) {
                        results.add("error while process retrieving from db");
                    }

                    GenericValue value = (GenericValue) values.next();
                    if (value != null) {
                        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outdir, entityName +".xml")), "UTF-8")));
                        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                        writer.println("<entity-engine-xml>");

                        do {
                            value.writeXmlText(writer, "");
                            numberWritten++;
                            if (numberWritten % 500 == 0) {
                                TransactionUtil.commit(beganTx);
                                beganTx = TransactionUtil.begin();
                            }
                        } while ((value = (GenericValue) values.next()) != null);
                        writer.println("</entity-engine-xml>");
                        writer.close();
                    } else {
                        results.add("No record found");
                    }
                    values.close();
                    TransactionUtil.commit(beganTx);

                    File file = new File(outdir, entityName +".xls");
                    WorkbookSettings wbSettings = new WorkbookSettings();
                    wbSettings.setLocale(new Locale("en", "EN"));
                    workbook = Workbook.createWorkbook(file, wbSettings);
                    workbook.createSheet(entityName, 0);
                    WritableSheet excelSheet = workbook.getSheet(0);
                    int row = 0;

                    WritableFont headerFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
                    WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
                    CellView cv = new CellView();
                    cv.setAutosize(true);

                    Label label  = new Label(0, row, "ID", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(0, cv);
                    label  = new Label(1, row, "Date", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(1, cv);
                    label  = new Label(2, row, "Subject", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(2, cv);
                    label  = new Label(3, row, "Email Address", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(3, cv);
                    label  = new Label(4, row, "First Name", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(4, cv);
                    label  = new Label(5, row, "Last Name", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(5, cv);
                    label  = new Label(6, row, "Contact Email Address", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(6, cv);
                    label  = new Label(7, row, "Reason For Contact ", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(7, cv);
                    label  = new Label(8, row, "Address1", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(8, cv);
                    label  = new Label(9, row, "Address2", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(9, cv);
                    label  = new Label(10, row, "City State Zip", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(10, cv);
                    label  = new Label(11, row, "Phone", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(11, cv);
                    label  = new Label(12, row, "Order #", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(12, cv);
                    label  = new Label(13, row, "Comment", headerFormat);
                    excelSheet.addCell(label);
                    excelSheet.setColumnView(13, cv);
                    //label  = new Label(12, row, "Email Content Body", headerFormat);
                    //excelSheet.addCell(label);
                    //excelSheet.setColumnView(12, cv);
                    Debug.logInfo("wrote header", module);
                    row++;

                    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
                    WritableCellFormat times = new WritableCellFormat(times10pt);

                    String XmlFilePath = new File(outdir, entityName +".xml").getAbsolutePath();
                    List<Map<Object, Object>> listMaps = getListMapsFromXmlFile(XmlFilePath);
                    for (Map entityDataRow: listMaps) 
                    {
                        String fname = "", lname = "", contactEmail ="", reasonOfContact = "", emailContentAddress = "", address1 = "", address2 ="", cityStateZip= "", phone ="", comment ="",orderNum="";
                    	commEventId=entityDataRow.get("communicationEventId").toString();
                    	contactEmail=entityDataRow.get("fromString").toString();
                        //Debug.logInfo("Extracting column value for commEventId:" + commEventId, module);
                        label  = new Label(0, row, entityDataRow.get("communicationEventId").toString(), times);
                        excelSheet.addCell(label);

                        label  = new Label(1, row, entityDataRow.get("createdTxStamp").toString(), times);
                        excelSheet.addCell(label);
                        
                        label  = new Label(2, row, entityDataRow.get("subject").toString(), times);
                        excelSheet.addCell(label);

                        label  = new Label(3, row, entityDataRow.get("toString").toString(), times);
                        excelSheet.addCell(label);

                        String emailContent = entityDataRow.get("content").toString();
                        String emailContentBody = emailContent.substring(emailContent.indexOf("<body>")+"<body>".length(), emailContent.indexOf("</body>"));
                        emailContentBody = emailContentBody.replaceAll("&#58;()", "").replaceAll("&nbsp;"," ").replaceAll("&#64;()", "@").replaceAll(","," ").replaceAll(";"," ");

                        try
                        {
                            if (emailContentBody.indexOf("First Name:") > -1) 
                            {
                            	int idxStart = emailContentBody.indexOf("First Name:")+"First Name:</td>".length();
                            	fname = emailContentBody.substring(idxStart, emailContentBody.indexOf("</td>",idxStart));
                            	fname = fname.substring(fname.lastIndexOf(">")+1);
                            	fname.trim();
                            	
                            }
                            else
                            {
                                if (emailContentBody.indexOf(">First Name") > -1) {
                                	int idxStart = emailContentBody.indexOf(">First Name")+">First Name".length();
                                	int idxEnd = emailContentBody.indexOf("</p>", emailContentBody.indexOf(">First Name"));
                                	fname = emailContentBody.substring(idxStart, idxEnd);
                                    if (fname.indexOf("<span class=\"data\">") > -1) {
                                    	fname = fname.substring(fname.indexOf("\">")+2,fname.indexOf("</span>"));
                                    }
                                    else
                                    {
                                        if (fname.indexOf("\"infoValue\">") > -1) 
                                        {
                                        	idxStart = fname.indexOf("\"infoValue\">")+"\"infoValue\">".length();
                                        	fname = fname.substring(idxStart, fname.indexOf("</div>",idxStart));
                                        	
                                        }
                                        else if (fname.indexOf(">") > -1) 
                                        {
                                        	fname = fname.substring(fname.lastIndexOf(">")+1);
                                        }
                                    	
                                    }
                                    fname.trim();
                                    
                                }
                            	
                            }
                            if (emailContentBody.indexOf("Last Name:") > -1) 
                            {
                            	int idxStart = emailContentBody.indexOf("Last Name:")+"Last Name:</td>".length();
                            	lname = emailContentBody.substring(idxStart, emailContentBody.indexOf("</td>",idxStart));
                            	lname = lname.substring(lname.lastIndexOf(">")+1);
                            	lname.trim();
                            	
                            }
                            else
                            {
                                if (emailContentBody.indexOf(">Last Name") > -1) {
                                	int idxStart = emailContentBody.indexOf(">Last Name")+">Last Name".length();
                                	int idxEnd = emailContentBody.indexOf("</p>", emailContentBody.indexOf(">Last Name"));
                                	lname = emailContentBody.substring(idxStart, idxEnd);
                                    if (lname.indexOf("<span class=\"data\">") > -1) {
                                    	lname = lname.substring(lname.indexOf("\">")+2,lname.indexOf("</span>"));
                                    }
                                    else
                                    {
                                        if (lname.indexOf("\"infoValue\">") > -1) 
                                        {
                                        	idxStart = lname.indexOf("\"infoValue\">")+"\"infoValue\">".length();
                                        	lname = lname.substring(idxStart, lname.indexOf("</div>",idxStart));
                                        	
                                        }
                                        else if (lname.indexOf(">") > -1) 
                                        {
                                        	lname = lname.substring(lname.lastIndexOf(">")+1);
                                        }
                                    	
                                    }
                                    lname.trim();
                                    
                                }
                            	
                            }
                            if (UtilValidate.isEmpty(contactEmail))
                            {
                                if (emailContentBody.indexOf(">Email Address") > -1) {
                                    contactEmail = emailContentBody.substring(emailContentBody.indexOf(">Email Address")+">Email Address".length(), emailContentBody.indexOf("</p>", emailContentBody.indexOf(">Email Address")));
                                    if (contactEmail.indexOf(">") > -1) {
                                        contactEmail = contactEmail.substring(contactEmail.lastIndexOf(">")+1);
                                    }
                                }
                            }
                            if (emailContentBody.indexOf("Reason For Contact:") > -1) 
                            {
                            	int idxStart = emailContentBody.indexOf("Reason For Contact:")+"Reason For Contact:</td>".length();
                                reasonOfContact = emailContentBody.substring(idxStart, emailContentBody.indexOf("</td>",idxStart));
                                reasonOfContact = reasonOfContact.substring(reasonOfContact.lastIndexOf(">")+1);
                                reasonOfContact.trim();
                            	
                            }
                            else
                            {
                                if (emailContentBody.indexOf(">Reason For Contact") > -1) 
                                {
                                	int idxStart = emailContentBody.indexOf(">Reason For Contact")+">Reason For Contact".length();
                                	int idxEnd = emailContentBody.indexOf("</p>", emailContentBody.indexOf(">Reason For Contact"));
                                    reasonOfContact = emailContentBody.substring(idxStart, idxEnd);
                                    if (reasonOfContact.indexOf("<span class=\"data\">") > -1) {
                                        reasonOfContact = reasonOfContact.substring(reasonOfContact.indexOf("\">")+2,reasonOfContact.indexOf("</span>"));
                                    }
                                    else
                                    {
                                        if (reasonOfContact.indexOf("\"infoValue\">") > -1) 
                                        {
                                        	idxStart = reasonOfContact.indexOf("\"infoValue\">")+"\"infoValue\">".length();
                                            reasonOfContact = reasonOfContact.substring(idxStart, reasonOfContact.indexOf("</div>",idxStart));
                                        	
                                        }
                                        else if (reasonOfContact.indexOf(">") > -1) 
                                        {
                                            reasonOfContact = reasonOfContact.substring(reasonOfContact.lastIndexOf(">")+1);
                                        }
                                    	
                                    }
                                    reasonOfContact.trim();
                                    
                                }
                            	
                            }

                            if (emailContentBody.indexOf(">Address") > -1) {
                                emailContentAddress = emailContentBody.substring(emailContentBody.indexOf(">Address")+">Address".length(), emailContentBody.indexOf(">Contact Phone"));
                            }
                            if (emailContentAddress.indexOf("<p>") > -1) {
                                address1 = emailContentAddress.substring(emailContentAddress.indexOf("<p>")+"<p>".length(), emailContentAddress.indexOf("</p>", emailContentAddress.indexOf("<p>")));
                                emailContentAddress = emailContentAddress.replaceFirst(emailContentAddress.substring(emailContentAddress.indexOf("<p>"), emailContentAddress.indexOf("</p>", emailContentAddress.indexOf("<p>"))), "");
                                if (address1.indexOf(">") > -1) {
                                    address1 = address1.substring(address1.lastIndexOf(">")+1);
                                }
                            }
                            if (emailContentAddress.indexOf("<p>") > -1) {
                                address2 = emailContentAddress.substring(emailContentAddress.indexOf("<p>")+"<p>".length(), emailContentAddress.indexOf("</p>", emailContentAddress.indexOf("<p>")));
                                emailContentAddress = emailContentAddress.replaceFirst(emailContentAddress.substring(emailContentAddress.indexOf("<p>"), emailContentAddress.indexOf("</p>", emailContentAddress.indexOf("<p>"))), "");
                                if (address2.indexOf(">") > -1) {
                                    address2 = address2.substring(address2.lastIndexOf(">")+1);
                                }
                            }
                            if (emailContentAddress.indexOf("<p>") > -1) {
                                cityStateZip = emailContentAddress.substring(emailContentAddress.indexOf("<p>")+"<p>".length(), emailContentAddress.indexOf("</p>", emailContentAddress.indexOf("<p>")));
                                if (cityStateZip.indexOf(">") > -1) {
                                    cityStateZip = cityStateZip.substring(cityStateZip.lastIndexOf(">")+1);
                                }
                            }

                            if (emailContentBody.indexOf("Contact Phone:") > -1) 
                            {
                            	int idxStart = emailContentBody.indexOf("Contact Phone:")+"Contact Phone:</td>".length();
                            	phone = emailContentBody.substring(idxStart, emailContentBody.indexOf("</td>",idxStart));
                            	phone = phone.substring(phone.lastIndexOf(">")+1);
                            	phone.trim();
                            	
                            }
                            else
                            {
                                if (emailContentBody.indexOf(">Contact Phone") > -1) {
                                	int idxStart = emailContentBody.indexOf(">Contact Phone")+">Contact Phone".length();
                                	int idxEnd = emailContentBody.indexOf("</p>", emailContentBody.indexOf(">Contact Phone"));
                                	phone = emailContentBody.substring(idxStart, idxEnd);
                                    if (phone.indexOf("<span class=\"data\">") > -1) {
                                    	phone = phone.substring(phone.indexOf("\">")+2,phone.indexOf("</span>"));
                                    }
                                    else
                                    {
                                        if (phone.indexOf("\"infoValue\">") > -1) 
                                        {
                                        	idxStart = phone.indexOf("\"infoValue\">")+"\"infoValue\">".length();
                                        	phone = phone.substring(idxStart, phone.indexOf("</div>",idxStart));
                                        	
                                        }
                                        else if (phone.indexOf(">") > -1) 
                                        {
                                        	phone = phone.substring(phone.lastIndexOf(">")+1);
                                        }
                                    	
                                    }
                                    phone.trim();
                                    
                                }
                            	
                            }
                            if (emailContentBody.indexOf("Order #:") > -1) 
                            {
                            	int idxStart = emailContentBody.indexOf("Order #:")+"Order #:</td>".length();
                            	orderNum = emailContentBody.substring(idxStart, emailContentBody.indexOf("</td>",idxStart));
                            	orderNum = orderNum.substring(orderNum.lastIndexOf(">")+1);
                            	orderNum.trim();
                            	
                            }
                            else
                            {
                                if (emailContentBody.indexOf(">Order #") > -1) {
                                	int idxStart = emailContentBody.indexOf(">Order #")+">Order #".length();
                                	int idxEnd = emailContentBody.indexOf("</p>", emailContentBody.indexOf(">Order #"));
                                	orderNum = emailContentBody.substring(idxStart, idxEnd);
                                    if (orderNum.indexOf("<span class=\"data\">") > -1) {
                                    	orderNum = orderNum.substring(orderNum.indexOf("\">")+2,orderNum.indexOf("</span>"));
                                    }
                                    else
                                    {
                                        if (orderNum.indexOf("\"infoValue\">") > -1) 
                                        {
                                        	idxStart = orderNum.indexOf("\"infoValue\">")+"\"infoValue\">".length();
                                        	orderNum = orderNum.substring(idxStart, orderNum.indexOf("</div>",idxStart));
                                        	
                                        }
                                        else if (orderNum.indexOf(">") > -1) 
                                        {
                                        	orderNum = orderNum.substring(orderNum.lastIndexOf(">")+1);
                                        }
                                    	
                                    }
                                    orderNum.trim();
                                    
                                }
                            	
                            }
                            
                            if (emailContentBody.indexOf("Comment:") > -1) 
                            {
                            	int idxStart = emailContentBody.indexOf("Comment:")+"Comment:</td>".length();
                            	comment = emailContentBody.substring(idxStart, emailContentBody.indexOf("</td>",idxStart));
                            	comment = comment.substring(comment.lastIndexOf(">")+1);
                            	comment.trim();
                            	
                            }
                            else
                            {
                                if (emailContentBody.indexOf(">Comment") > -1) {
                                	int idxStart = emailContentBody.indexOf(">Comment")+">Comment".length();
                                	int idxEnd = emailContentBody.indexOf("</p>", idxStart);
                                	comment = emailContentBody.substring(idxStart, idxEnd);
                                    if (comment.indexOf("<span class=\"data\">") > -1) {
                                    	comment = comment.substring(comment.indexOf("\">")+2,comment.indexOf("</span>"));
                                    }
                                    else
                                    {
                                        if (comment.indexOf("\"infoValue\">") > -1) 
                                        {
                                        	idxStart = comment.indexOf("\"infoValue\">")+"\"infoValue\">".length();
                                        	comment = comment.substring(idxStart, comment.indexOf("</div>",idxStart));
                                        	
                                        }
                                        else if (comment.indexOf(">") > -1) 
                                        {
                                        	comment = comment.substring(comment.lastIndexOf(">")+1);
                                        }
                                    	
                                    }
                                    
                                }
                            	
                            }
                            
                        } 
                        catch (IndexOutOfBoundsException iobex) 
                        {
                            Debug.logError(iobex, "Error extracting column value for commEventId:" + commEventId, module);
                            return ServiceUtil.returnError("Error extracting column value for commEventId:" + commEventId);
                            
                        } catch (Exception ex) {
                            Debug.logError(ex, "Error commEventId:" + commEventId, module);
                            return ServiceUtil.returnError("Error extracting column value for commEventId:" + commEventId);
                        }
                        label  = new Label(4, row, fname.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(5, row, lname.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(6, row, contactEmail.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(7, row, reasonOfContact.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(8, row, address1.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(9, row, address2.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(10, row, cityStateZip.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(11, row, phone.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(12, row, orderNum.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        label  = new Label(13, row, comment.replaceAll("\n", "").trim(), times);
                        excelSheet.addCell(label);

                        //label  = new Label(12, row, emailContentBody, times);
                        //excelSheet.addCell(label);
                        row++;
                    }
                    Debug.logInfo("Finished loop", module);
                    
                    workbook.write();
                    workbook.close();
                    new File(outdir, entityName +".xml").delete();
                } catch (Exception ex) 
                {
                    if (values != null) {
                        try {
                            values.close();
                        } catch (Exception exc) 
                        {
                            //Debug.warning();
                        }
                    }
                    if (workbook != null) 
                    {
                        try {
                            workbook.close();
                        } catch (Exception exc) {
                            //Debug.warning();
                        }
                    }
                }
            } else 
            {
                results.add("Path not found or no write access.");
                return ServiceUtil.returnError("Path not found or no write access.");
            }
        } else {
            results.add("No path specified, doing nothing.");
            return ServiceUtil.returnError("No path specified, doing nothing.");
        }
        // send the notification
        Debug.logInfo("Finished job", module);
        Map<String, Object> resp = UtilMisc.<String, Object>toMap("results", results);
        return resp;
    }
    private static List<Map<Object, Object>> getListMapsFromXmlFile(String XmlFilePath) {
        List<Map<Object, Object>> listMaps = FastList.newInstance();
        InputStream ins = null;
        URL xmlFileUrl = null;
        Document xmlDocument = null;
        try {
            if (UtilValidate.isNotEmpty(XmlFilePath)) {
                xmlFileUrl = UtilURL.fromFilename(XmlFilePath);
                if (xmlFileUrl  != null) ins = xmlFileUrl.openStream();
                if (ins != null) {
                    xmlDocument = UtilXml.readXmlDocument(ins, xmlFileUrl.toString());
                    List<? extends Node> nodeList = UtilXml.childNodeList(xmlDocument.getDocumentElement().getFirstChild());
                    for (Node node: nodeList) {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Map<Object, Object> fields = FastMap.newInstance();
                            fields.put(node.getNodeName(), UtilXml.elementValue((Element)node));
                            Map<Object, Object> attrFields = getAttributeNameValueMap(node);
                            Set<Object> keys = attrFields.keySet();
                            Iterator<Object> attrFieldsIter = keys.iterator();
                            while (attrFieldsIter.hasNext()) {
                                Object key = attrFieldsIter.next();
                                fields.put(key, attrFields.get(key));
                            }

                            List<? extends Node> childNodeList = UtilXml.childNodeList(node.getFirstChild());
                            for(Node childNode: childNodeList) {
                                fields.put(childNode.getNodeName(), UtilXml.elementValue((Element)childNode));
                                attrFields = getAttributeNameValueMap(childNode);
                                keys = attrFields.keySet();
                                attrFieldsIter = keys.iterator();
                                while (attrFieldsIter.hasNext()) {
                                    Object key = attrFieldsIter.next();
                                    fields.put(key, attrFields.get(key));
                                }
                            }
                            listMaps.add(fields);
                        }
                    }
                }
            }
        } catch (Exception exc) {
            Debug.logError(exc, "Error reading xml file", module);
        }
        finally {
            try {
                if (ins != null) ins.close();
            } catch (Exception exc) {
                Debug.logError(exc, "Error reading xml file", module);
            }
        }
        return listMaps;
    }
    private static Map<Object, Object> getAttributeNameValueMap(Node node) throws Exception {
        Map<Object, Object> attrFields = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(node)) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attrNodeList = node.getAttributes();
                for (int a = 0; a < attrNodeList.getLength(); a++) {
                    Node attrNode = attrNodeList.item(a);
                    attrFields.put(attrNode.getNodeName(), attrNode.getNodeValue());
                }
            }
        }
        return attrFields;
    }
}

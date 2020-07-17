package admin;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;

//contact us reasons
adminToolsList = FastList.newInstance();
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageContactUsReasonHeading);
adminTool.put("toolDesc", uiLabelMap.ManageContactUsReasonInfo);
adminTool.put("toolDetail", "contactUsReasonList");
adminToolsList.add(adminTool);

//credit card type
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageCreditCardTypeHeading);
adminTool.put("toolDesc", uiLabelMap.ManageCreditCardTypeInfo);
adminTool.put("toolDetail", "creditCardTypeList");
adminToolsList.add(adminTool);

//titles
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManagePersonTitleHeading);
adminTool.put("toolDesc", uiLabelMap.ManagePersonTitleInfo);
adminTool.put("toolDetail", "personTitleList");
adminToolsList.add(adminTool);

//review ages
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageReviewAgesHeading);
adminTool.put("toolDesc", uiLabelMap.ManageReviewAgesInfo);
adminTool.put("toolDetail", "reviewAgesList");
adminToolsList.add(adminTool);

//gift Message
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageGiftMessageHeading);
adminTool.put("toolDesc", uiLabelMap.ManageGiftMessageInfo);
adminTool.put("toolDetail", "giftMessageList");
adminToolsList.add(adminTool);

//review ages
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageReviewCustomAttribute01Heading);
adminTool.put("toolDesc", uiLabelMap.ManageReviewCustomAttribute01Info);
adminTool.put("toolDetail", "reviewCustomAttribute01List");
adminToolsList.add(adminTool);

//credit card type
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageCreditCardTypeHeading);
adminTool.put("toolDesc", uiLabelMap.ManageCreditCardTypeInfo);
adminTool.put("toolDetail", "creditCardTypeList");
adminToolsList.add(adminTool);

//recurrence frequency type
adminTool = FastMap.newInstance();
adminTool.put("toolType", uiLabelMap.ManageRecurrenceFreqTypeHeading);
adminTool.put("toolDesc", uiLabelMap.ManageRecurrenceFreqTypeInfo);
adminTool.put("toolDetail", "recurrenceFreqTypeList");
adminToolsList.add(adminTool);

context.resultList = adminToolsList;
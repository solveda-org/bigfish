package email;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilValidate;


templates = FastList.newInstance();
emailTypeId = request.getAttribute("emailTypeId");
if(UtilValidate.isNotEmpty(emailTypeId)) 
{
	if(emailTypeId.equals("CONT_NOTI_EMAIL")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_CONTACT_US'>E_CONTACT_US</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PARTY_REGIS_CONFIRM")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_NEW_CUSTOMER'>E_NEW_CUSTOMER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_ABD_CART")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_ABANDON_CART'>E_ABANDON_CART</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_CUST_REGISTER")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_NEW_CUSTOMER'>E_NEW_CUSTOMER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_EMAIL_TEST")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_TEST_EMAIL'>E_TEST_EMAIL</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_EMAIL_VERIFY")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_GC_PURCHASE")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_GC_RELOAD")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_MAILING_LIST")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_MAILING_LIST'>E_MAILING_LIST</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_ODR_BACKORDER")) 
	{ //is defined in EmailScreens.xml
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_ODR_CHANGE")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_ORDER_CHANGE'>E_ORDER_CHANGE</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_ODR_COMPLETE")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_ORDER_CHANGE'>E_ORDER_CHANGE</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_ODR_CONFIRM")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_ORDER_CONFIRM'>E_ORDER_CONFIRM</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_ODR_PAYRETRY")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_ODR_SHIP_COMPLT")) 
	{ //is defined in EmailScreens.xml
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_PWD_RETRIEVE")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_FORGOT_PASSWORD'>E_FORGOT_PASSWORD</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_QUO_CONFIRM")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_RTN_ACCEPT")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_RTN_CANCEL")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_RTN_COMPLETE")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_SCHED_JOB_ALERT")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_SCHED_JOB_ALERT '>E_SCHED_JOB_ALERT</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_SHIP_REVIEW")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_SHIP_REVIEW'>E_SHIP_REVIEW</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("PRDS_TELL_FRIEND")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_TELL_FRIEND'>E_TELL_FRIEND</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("REQCAT_NOTI_EMAIL")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_REQUEST_CATALOG'>E_REQUEST_CATALOG</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("UNSUB_CONT_LIST_NOTI")) 
	{
		context.emailTypeId = emailTypeId;
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		//templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	if(emailTypeId.equals("UPD_PRSNL_INF_CNFRM")) 
	{
		context.emailTypeId = emailTypeId;
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_HEADER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_CHANGE_CUSTOMER'>E_CHANGE_CUSTOMER</a>");
		templates.add("<a href='emailSpotDetail?contentId=E_COMMON_HEADER'>E_COMMON_FOOTER</a>");
		context.templates = templates;
	}
	
	
	if(UtilValidate.isEmpty(templates))
	{
		templates.add("<p>" + uiLabelMap.CommonNone + "</p>");
		context.templates = templates;
	}
}

<script type="text/javascript">
    jQuery(document).ready(function () {
    
        //disabled or enabled the Selectable feature based on the Finished/Virtual 
        var virtualRadio = jQuery('input:radio[name=isVirtual]:checked');
        selectFinishedProduct(virtualRadio);
        
    	//handle the disable date
		var enabledCheckBox = jQuery('input:radio[name=enabled]:checked').val();
		if(enabledCheckBox == "Y")
		{
			//if it equals Y, then disable the disable date USER_DISABLED_DATE_TIME
			jQuery('#USER_DISABLED_DATE').prop('disabled', true);	
			jQuery('#USER_DISABLED_DATE').val('');
			jQuery('#USER_DISABLED_HOUR').prop('disabled', true);	
			jQuery('#USER_DISABLED_HOUR').val('');
			jQuery('#USER_DISABLED_MINUTE').prop('disabled', true);	
			jQuery('#USER_DISABLED_MINUTE').val('');
			jQuery('#USER_DISABLED_AMPM').prop('disabled', true);	
			jQuery('#USER_DISABLED_AMPM').val('');
			
			jQuery(".ui-datepicker-trigger").hide();
			jQuery("#USER_DISABLED_DATE_TIME").attr('class', 'textEntry');
			
		}	
		if(enabledCheckBox == "N")
		{
			user_disabled_date = jQuery('#USER_DISABLED_DATE').val();
			user_disabled_hour = jQuery('#USER_DISABLED_HOUR').val();
			user_disabled_min = jQuery('#USER_DISABLED_MINUTE').val();
			user_disabled_ampm = jQuery('#USER_DISABLED_AMPM').val();
		
			//else enable the disable date USER_DISABLED_DATE_TIME
			jQuery('#USER_DISABLED_DATE').prop('disabled', false);	
			jQuery('#USER_DISABLED_HOUR').prop('disabled', false);	
			jQuery('#USER_DISABLED_MINUTE').prop('disabled', false);
			jQuery('#USER_DISABLED_AMPM').prop('disabled', false);	
			
			jQuery("#USER_DISABLED_DATE_TIME").attr('class', 'dateEntry');
			jQuery(".ui-datepicker-trigger").show();
		}	
	
		//when values are changed, run this:
		jQuery('.USER_ENABLED_CHECKBOX_').change(function() {
			var enabledCheckBox = jQuery('input:radio[name=enabled]:checked').val();
			if(enabledCheckBox == "Y")
			{
				//if it equals Y, then disable the disable date USER_DISABLED_DATE_TIME
				jQuery('#USER_DISABLED_DATE').prop('disabled', true);	
				jQuery('#USER_DISABLED_DATE').val('');
				jQuery('#USER_DISABLED_HOUR').prop('disabled', true);	
				jQuery('#USER_DISABLED_HOUR').val('');
				jQuery('#USER_DISABLED_MINUTE').prop('disabled', true);	
				jQuery('#USER_DISABLED_MINUTE').val('');
				jQuery('#USER_DISABLED_AMPM').prop('disabled', true);	
				jQuery('#USER_DISABLED_AMPM').val('');
				
				jQuery(".ui-datepicker-trigger").hide();
				jQuery("#USER_DISABLED_DATE_TIME").attr('class', 'textEntry');
	
			}	
			if(enabledCheckBox == "N")
			{
				//else enable the disable date USER_DISABLED_DATE_TIME
				jQuery('#USER_DISABLED_DATE').prop('disabled', false);	
				jQuery('#USER_DISABLED_HOUR').prop('disabled', false);	
				jQuery('#USER_DISABLED_MINUTE').prop('disabled', false);
				jQuery('#USER_DISABLED_AMPM').prop('disabled', false);	
				
				jQuery("#USER_DISABLED_DATE_TIME").attr('class', 'dateEntry');
				jQuery(".ui-datepicker-trigger").show();
			}
			
		});
	
	
        jQuery('.displayBox.slidingClose').each(function(){
            slidingInit(this, 'slidePlusIcon');
        });
        
        jQuery('.displayBox.slidingOpen').each(function(){
            slidingInit(this, 'slideMinusIcon');
        });
        
        if(jQuery('#createMediaContent').length) {
          setUploadUrl("${parameters.mediaType!'images'}");
        }
    
        jQuery('tr.noResult td').attr("colspan", jQuery('tr.heading th').size());
        if (jQuery('#productPromoActionEnumId').length){
            getDisplayFormat('#productPromoActionEnumId');
            jQuery('#productPromoActionEnumId').change(function(){
                getDisplayFormat('#productPromoActionEnumId');
                clearField();
            });
        }
        if (jQuery('#inputParamEnumId').length){
            getDisplayFormat('#inputParamEnumId');
            jQuery('#inputParamEnumId').change(function(){
                getDisplayFormat('#inputParamEnumId');
            });
        }
        //getOrderItemCheckDisplay('changeStatusAll');
        /* jQuery('input:radio[name=changeStatusAll]').click(function(event) {
            getOrderItemCheckDisplay('changeStatusAll');
        }); */

        paymentOptionDisplay('paymentOption');
        jQuery('input:radio[name=paymentOption]').click(function(event) {
            paymentOptionDisplay('paymentOption');
        });
       jQuery("#closeButton").click(function (e){
           hideDialog('#dialog', '#displayDialog');
           e.preventDefault();
       });
       jQuery("#lookupCloseButton").click(function (e){
           hideDialog('#lookUpDialog', '#displayLookUpDialog');
           e.preventDefault();
       });
       <#if focusField?has_content>
           jQuery("#${focusField!""}").focus();
       </#if>

       jQuery('input:checkbox.homeSpotCheck').change(function()
       {
           if (jQuery('input:checkbox[name=contentId]:checked').length) {
               jQuery('#previewHomeSpot').attr("target","_new");
               var url = jQuery('#previewHomeSpotAction').val()+"?"+jQuery('input:checkbox[name=contentId]:checked').serialize();
               jQuery('#previewHomeSpot').attr("href",url);
           } else {
               jQuery('#previewHomeSpot').attr("href",jQuery('#previewHomeSpotAction').val());
               jQuery('#previewHomeSpot').attr("target","");
           }
       });
       
       /* jQuery('input:checkbox.checkBoxEntry').change(function()
       {
           alert("Hii2222222222");
	        var url = "<@ofbizUrl>getOrderStatusRefundDetail</@ofbizUrl>";
           jQuery.ajax(
           {
               type: "POST",
               url: url,
               data: jQuery("#orderStatusForm").serialize(), // serializes the form's elements.
               success: function(data)
               {
                   jQuery('#orderRefundInfoBox').replaceWith(data);
               }
           });

           return false; 

       }); */
       
       getOrderStatusChangeDisplay('#actionId');
       jQuery('input:radio[name=actionId]').click(function(event) {
            getOrderStatusChangeDisplay('#actionId');
        });
        
       /* if (jQuery('#actionId').length){
           alert("Hiii");
           getOrderStatusChangeDisplay('#actionId');
           jQuery('#actionId').click(function(){
               getOrderStatusChangeDisplay('#actionId');
           });
       } */
       <#if review?has_content>
           updateReview("${parameters.statusId!review.statusId}");
           setStars("${parameters.productRating!review.productRating}");
       </#if>

       if (jQuery('#productCategoryId').length){
           <#if !(errorMessage?has_content || errorMessageList?has_content) >
               loadProductCategoryFeture('#productCategoryId');
           </#if>
           jQuery('#productCategoryId').change(function(){
               loadProductCategoryFeture('#productCategoryId');
           });
       }
        if (jQuery('#simpleTest').length){
            getTestTemplateFormat(jQuery('input:radio[name=simpleTest]:checked').val(), 'Y');
        }
        if (jQuery('#templateId').length){
            jQuery('#templateId').change(function(){
                showTestTemplateDiv('#templateId');
            });
        }
        if (jQuery('#USER_country').length)
        {
            getAddressFormat('USER');
        }
        
       jQuery("div.actionIconMenu").mouseenter(function(event)
       {
           showActionIcontip(event, this)
       }).mouseleave(function(event)
       {
           hideActionIcontip(event, this)
       });
    });

    function getOrderRefundData()
    {
        if (jQuery('input:radio[name=actionId]:checked').val() == "cancelOrder" || jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
           var url = "<@ofbizUrl>getOrderStatusRefundDetail</@ofbizUrl>";
           jQuery.ajax(
           {
               type: "POST",
               url: url,
               data: jQuery("#orderStatusForm").serialize(), // serializes the form's elements.
               success: function(data)
               {
                   jQuery('#orderRefundInfoBox').html('');
                   jQuery('#orderRefundInfoBox').html(data);
               }
           });
        }  
    }
    // Popup window code
    function newPopupWindow(url) {
        popupWindow = window.open(
            url,'popUpWindow','height=350,width=500,left=400,top=200,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes')
    }

    function getTestTemplateFormat(simpleTestValue, isPageLoad) {
        if (simpleTestValue == "N") 
        {
            jQuery('.textDiv').hide();
            jQuery('.templateDdDiv').show();
            showTestTemplateDiv('#templateId');
        } else 
        {
            jQuery('.textDiv').show();
            jQuery('.templateDdDiv').hide();
            jQuery('.customerIdDiv').hide();
            jQuery('.orderIdDiv').hide();
        }
        if (isPageLoad == "N") 
        {
            $('toAddress').value = "";
        }
    }

    function showTestTemplateDiv(templateId) {
        var templateIdValue = jQuery(templateId).val();
        if ((templateIdValue == "E_SCHED_JOB_ALERT")) 
        {
            //TBD;
        } 
        else if ((templateIdValue == "E_CHANGE_CUSTOMER") || (templateIdValue == "E_NEW_CUSTOMER") || (templateIdValue == "E_FORGOT_PASSWORD") ) {
            jQuery('.customerIdDiv').show();
            jQuery('.orderIdDiv').hide();
        }
        else if ((templateIdValue == "E_ORDER_CHANGE") || (templateIdValue == "E_ORDER_CONFIRM") || (templateIdValue == "E_ORDER_DETAIL") || (templateIdValue == "E_SHIP_REVIEW") || (templateIdValue == "E_ABANDON_CART")) 
        {
            jQuery('.customerIdDiv').hide();
            jQuery('.orderIdDiv').show();
        }
        else if ((templateIdValue == "E_CONTACT_US") || (templateIdValue == "E_REQUEST_CATALOG") || (templateIdValue == "E_MAILING_LIST")) 
        {
            jQuery('.customerIdDiv').hide();
            jQuery('.orderIdDiv').hide();
        }
        else if ((templateIdValue == "TXT_CHANGE_CUSTOMER") || (templateIdValue == "TXT_NEW_CUSTOMER") || (templateIdValue == "TXT_FORGOT_PASSWORD") ) {
            jQuery('.customerIdDiv').show();
            jQuery('.orderIdDiv').hide();
        }
        else if ((templateIdValue == "TXT_ORDER_CHANGE") || (templateIdValue == "TXT_ORDER_CONFIRM") || (templateIdValue == "TXT_SHIP_REVIEW") || (templateIdValue == "TXT_ABANDON_CART")) 
        {
            jQuery('.customerIdDiv').hide();
            jQuery('.orderIdDiv').show();
        } 
    }
    function setDateRange(dateFrom,dateTo,formName) {
        var form = jQuery(formName);
        jQuery(formName).find('input[name="dateFrom"]').val(dateFrom);
        jQuery(formName).find('input[name="dateTo"]').val(dateTo);
        jQuery(formName).submit();
    }
    function setReviewSearchParams(statusId,dateFrom,dateTo,searchDays,count) {
        jQuery('#status').val(statusId);
        jQuery('#from').val(dateFrom);
        jQuery('#to').val(dateTo);
        jQuery('#srchDays').val(searchDays);
        if(count > ${ADM_WARN_LIST_ROWS!"0"})
        {
            setConfirmDialogContent('','${uiLabelMap.ShowAllError}','reviewManagement');
            submitDetailForm('', 'CF');
        } else{
            jQuery('#reviewSummary').submit();
        }
        
    }
    function loadProductCategoryFeture(productCategoryId) {
       productCategoryId = jQuery(productCategoryId).val();
       productId = jQuery('#productId').val();
       isVirtual = jQuery('input:radio[name=isVirtual]:checked').val();
       jQuery.get('<@ofbizUrl>loadProductCategoryFeture?productId='+productId+'&productCategoryId='+productCategoryId+'&isVirtual='+isVirtual+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
            jQuery('#productCategoryFetureDetail').replaceWith(data);
        });
    }

    function showFeature(elm, index) {
        if (jQuery(elm).val() == "DISTINGUISHING_FEAT") {
            jQuery('#productFeatureId_' + index).show();
        } else {
            jQuery('#productFeatureId_' + index).hide();
        }
        if (jQuery(elm).val() == "DISTINGUISHING_FEAT") 
        {
            jQuery('#selectedHelperIcon_' + index).hide();
            jQuery('#notApplicableHelperIcon_' + index).hide();
            jQuery('#descriptiveHelperIcon_' + index).show();
        }
        else if(jQuery(elm).val() == "SELECTABLE_FEATURE")
        {
            jQuery('#descriptiveHelperIcon_' + index).hide();
            jQuery('#notApplicableHelperIcon_' + index).hide();
            jQuery('#selectedHelperIcon_' + index).show();
            
        }
        else
        {
            jQuery('#selectedHelperIcon_' + index).hide();
            jQuery('#descriptiveHelperIcon_' + index).hide();
            jQuery('#notApplicableHelperIcon_' + index).show();
        }
    }

    function slidingInit(elm, slidingClass) {
        if(slidingClass == 'slidePlusIcon') {
            jQuery(elm).find('.boxBody').hide();
        }
        var slidingTrigger = "<span class='slidingTrigger "+ slidingClass + "'></span>";
        jQuery(elm).find('.header h2').append(slidingTrigger);
        addListener(jQuery(elm).find('.header h2 span.slidingTrigger'));
    }
    
    function addListener(elm) {
        jQuery(elm).click(function(){
            jQuery(this).parent('h2').parent('.header').next('.boxBody').slideToggle(1000);
            jQuery(this).toggleClass("slidePlusIcon");
            jQuery(this).toggleClass("slideMinusIcon");
        });
    }
    
    /* function getOrderItemCheckDisplay(changeStatusAll) {
        if (jQuery('input:radio[name=changeStatusAll]:checked').val() == "Y") 
        {
            jQuery('input:checkbox[name^=orderItemSeqId]:checked').removeAttr('checked');
            jQuery('.selectOrderItem').hide();
        } 
        else if (jQuery('input:radio[name=changeStatusAll]:checked').val() == "N") 
        {
            jQuery('.selectOrderItem').show();
        } 
        else 
        {
            jQuery('.selectOrderItem').hide();
        }
    }*/
    
    function getOrderStatusChangeDisplay(actionId) 
    {
        if (jQuery('input:radio[name=actionId]:checked').val() == "completeOrder") 
        {
            jQuery('.COMPLETED').show();
        }
        else 
        {
            jQuery('.COMPLETED').hide();
        }
        if (jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
            jQuery('.productReturnOrderCheckbox').show();
            jQuery('.statusChangeOrderCheckbox').hide();
        }
        else 
        {
            jQuery('.productReturnOrderCheckbox').hide();
            jQuery('.statusChangeOrderCheckbox').show();
        }
        if (jQuery('input:radio[name=actionId]:checked').val() == "changeOrderQty") 
        {
            jQuery('.orderItemNewQty').show();
        }
        else 
        {
            jQuery('.orderItemNewQty').hide();
        }
        if (jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
            jQuery('.orderItemReturningQty').show();
        }
        else 
        {
            jQuery('.orderItemReturningQty').hide();
        }
        if (jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
            jQuery('.orderItemReturnReason').show();
        }
        else 
        {
            jQuery('.orderItemReturnReason').hide();
        }
        
        if (jQuery('input:radio[name=actionId]:checked').val() == "cancelOrder") 
        {
            jQuery('#statusId').val("ORDER_CANCELLED");
        }
        else if (jQuery('input:radio[name=actionId]:checked').val() == "changeOrderQty") 
        {
            jQuery('#statusId').val("ORDER_CANCELLED");
        }
        else if (jQuery('input:radio[name=actionId]:checked').val() == "completeOrder") 
        {
            jQuery('#statusId').val("ORDER_COMPLETED");
        }
        else if (jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
            jQuery('#statusId').val("PRODUCT_RETURN");
        } 
    }
    function showPostalAddress(contactMechId,divType) {
        jQuery('.'+divType).hide();
        jQuery('.'+divType+' :input').attr('disabled', 'disabled');
        jQuery('#'+contactMechId).show();
        jQuery('#'+contactMechId+' :input').removeAttr('disabled');
    }

    function paymentOptionDisplay(paymentOption) {
       var selectedPaymentOption = jQuery('input:radio[name=paymentOption]:checked').val();
       jQuery('.CCExist').hide();
       jQuery('.CCNew').hide();
       jQuery('.OffLine').hide();
       jQuery('.Paypal').hide();
       
        if (selectedPaymentOption == "CCExist") {
            jQuery('.CCExist').show();
        } else if (selectedPaymentOption == "CCNew") {
            jQuery('.CCNew').show();
        } else if (selectedPaymentOption == "OffLine") {
            jQuery('.OffLine').show();
        } else if (selectedPaymentOption == "Paypal") {
            jQuery('.Paypal').show();
        } else {
            jQuery('.CCExist').show();
        }

    }

    function displayDialogBox(dialogContent) {
        showDialog('#dialog', '#displayDialog',dialogContent);
    }
    
    
    function showDialog(dialog, displayDialog,dialogContent) {
        jQuery('.commonHide').hide();
        jQuery(dialog).show();
        jQuery(displayDialog).fadeIn(300);
        jQuery(dialog).unbind("click");
        jQuery(dialogContent).show();
    }
    function hideDialog(dialog, displayDialog) {
        jQuery(dialog).hide();
        jQuery(displayDialog).fadeOut(300);
    }
    
   function updateStatusBtn(ActiveLabel,InActiveLabel,FormName,spanDescId,btnIdField) {
	    // Set form value
	    form = $(FormName);
	    var buttonLabel = $(btnIdField).value;
	
	    if (buttonLabel==ActiveLabel){
	        form.elements['statusId'].value='CTNT_PUBLISHED';
	        $(spanDescId).innerHTML = 'Active';
	        $(btnIdField).value = InActiveLabel;
	    } 
	    else 
	    {
	        form.elements['statusId'].value='CTNT_DEACTIVATED';
	        $(spanDescId).innerHTML = 'Inactive';
	        $(btnIdField).value = ActiveLabel;
	    }
   }
   
    function setCheckboxes(formName,checkBoxName) {
        // This would be clearer with camelCase variable names
        var allCheckbox = document.forms[formName].elements[checkBoxName + "all"];
        for(i = 0;i < document.forms[formName].elements.length;i++) {
            var elem = document.forms[formName].elements[i];
            if (elem.id.indexOf(checkBoxName) == 0 && elem.id.indexOf("_") < 0 && elem.type == "checkbox" && allCheckbox.type == "checkbox") {
                elem.checked = allCheckbox.checked;
            }
        }
    }

    function moveCategory(parentCategoryId, parentCategoryName, catIdField, catNameField) {
        document.getElementById(catIdField).value = parentCategoryId;
        document.getElementById(catNameField).innerHTML = parentCategoryName;
        hideDialog('#dialog', '#displayDialog');
    }

    
    function showTooltip(e, text)
    {
        if(document.all)e = event;
        var tooltipBox = document.getElementById('tooltip');
	    var obj2 = document.getElementById('tooltipText');
	    obj2.innerHTML = text;
	    tooltipBox.style.display = 'block';
	    var st = Math.max(document.body.scrollTop,document.documentElement.scrollTop);
	    var leftPos = e.clientX - 100;
	    if (leftPos<0)leftPos = 0;
	    tooltipBox.style.left = leftPos + 'px';
	    
        var tooltipBoxHeight = jQuery(tooltipBox).height();
        var elemPosBottom = e.clientY;
        var browserVieportHeight = jQuery(window).height();
        
	    if((tooltipBoxHeight + elemPosBottom + 25) > browserVieportHeight)
	    {
	        tooltipBox.style.top = e.clientY - tooltipBox.offsetHeight -1 + st + 'px';
	        jQuery('#tooltipBottom').addClass("tooltipBottomArrow");
	        jQuery('#tooltipTop').removeClass("tooltipTopArrow");
	    }
	    else
	    {
	        tooltipBox.style.top = e.clientY + 5 + st + 'px';
	        jQuery('#tooltipBottom').removeClass("tooltipBottomArrow");
	        jQuery('#tooltipTop').addClass("tooltipTopArrow");
	    }
    }
    
    function showTooltipImage(e, text, imageUrl)
    {
    
        if(document.all)e = event;
        var tooltipBox = document.getElementById('tooltip');
	    var obj2 = document.getElementById('tooltipText');
	    
	    obj2.innerHTML = "<img src='"+imageUrl+"' class='toolTipImg' id='imgId'/><div class='toolTipImgText'>"+text+"</div>";
	    obj2.style.display = 'none';
	    var img = document.getElementById('imgId');
	    resize(img);
	    obj2.style.display = 'block';
	    tooltipBox.style.display = 'block';
	    var st = Math.max(document.body.scrollTop,document.documentElement.scrollTop);
	    var leftPos = e.clientX - 100;
	    if (leftPos<0)leftPos = 0;
	    tooltipBox.style.left = leftPos + 'px';
	    
        var tooltipBoxHeight = jQuery(tooltipBox).height();
        var elemPosBottom = e.clientY;
        var browserVieportHeight = jQuery(window).height();
        
	    if((tooltipBoxHeight + elemPosBottom + 25) > browserVieportHeight)
	    {
	        tooltipBox.style.top = e.clientY - tooltipBox.offsetHeight -1 + st + 'px';
	        jQuery('#tooltipBottom').addClass("tooltipBottomArrow");
	        jQuery('#tooltipTop').removeClass("tooltipTopArrow");
	    }
	    else
	    {
	        tooltipBox.style.top = e.clientY + 5 + st + 'px';
	        jQuery('#tooltipBottom').removeClass("tooltipBottomArrow");
	        jQuery('#tooltipTop').addClass("tooltipTopArrow");
	    }
        
    }

    function showActionIcontip(e, elm, nextDiv)
    {
    
        var actionIconBox = jQuery(elm).find('div:first');
        var actionIconBoxHeight = jQuery(actionIconBox).height();
        var elemPosBottom = e.clientY;
        var browserVieportHeight = jQuery(window).height();
        if(actionIconBox+':hidden')
        {
	        if(document.all)e = event;
	        jQuery(actionIconBox).css(
	          {
	             position:'absolute'
	          }
	        );
	        
	        if((actionIconBoxHeight + elemPosBottom) > browserVieportHeight)
	        {
	            jQuery(actionIconBox).addClass("actionIconBoxArrowBottomRight");
	            jQuery(actionIconBox).removeClass("actionIconBoxArrowTopRight");
	        }
	        else
	        {
	            jQuery(actionIconBox).removeClass("actionIconBoxArrowBottomRight");
	            jQuery(actionIconBox).addClass("actionIconBoxArrowTopRight");
	        }
	        jQuery(actionIconBox).show();
        }
    } 
    
    function hideActionIcontip(e, elm, nextDiv)
    {
        if(document.all)e = event;
        
        var actionIconBox = jQuery(elm).find('div:first');
        jQuery(actionIconBox).hide();
    } 

    function resize(img)
    {
        if(img.width >= 3500)
        {
            img.width = img.width *(10/100);
        }
        else if(img.width < 3500 && img.width >= 2800)
        {
            img.width = img.width *(20/100);
        }
        else if(img.width < 2800 && img.width >= 2100)
        {
            img.width = img.width *(30/100);
        }
        else if(img.width < 2100 && img.width >= 1400)
        {
            img.width = img.width *(40/100);
        }
        else if(img.width < 1400 && img.width >= 700)
        {
            img.width = img.width *(50/100);
        }
    }
    
    function hideTooltip()
    {
        document.getElementById('tooltip').style.display = "none";
    }
    
    function confirmDialogResult(result) {
        hideDialog('#dialog', '#displayDialog');
        if (result == 'Y') {
            postConfirmDialog();
        }
    }

    
	function submitDetailForm(form, mode) {
	    if (mode == "NE") {
	        // create action
	        form.action="<@ofbizUrl>${createAction!""}</@ofbizUrl>";
	        form.submit();
	    }else if (mode == "ED") {
	        // update action
	        form.action="<@ofbizUrl>${updateAction!""}</@ofbizUrl>";
	        form.submit();
	    }else if (mode == "DE") {
	        // update action
	        form.action="<@ofbizUrl>${deleteAction!""}</@ofbizUrl>";
	        form.submit();
        }else if (mode == "EX") {
            // execute action
            form.action="<@ofbizUrl>${execAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "CEX") {
            // execute action
            form.action="<@ofbizUrl>${conditionedExecAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "EXC") {
            // execute cache action
            form.action="<@ofbizUrl>${execCacheAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "CO") {
            // common action
            form.action="<@ofbizUrl>${commonAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "GP") {
            // get Geo Point action
            form.action="<@ofbizUrl>${getGeoCodeAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "RW") {
            // replace with action
            form.action="<@ofbizUrl>${replaceWithAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "MA") {
            // make active action
            form.action="<@ofbizUrl>${makeActiveAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "CF") {
	        // confirm action
	        displayDialogBox()
        }else if (mode == "PC") {
            form.action="<@ofbizUrl>${previewAction!""}</@ofbizUrl>";
            form.setAttribute("target", "_new");
            form.submit();
            form.setAttribute("target", "");
	    }else if (mode == "MT") {
	    	//go to meta tag page
            form.action="<@ofbizUrl>${metaAction!""}</@ofbizUrl>";
            form.submit();
	    }else if (mode == "SF") {
            // execute action
            form.action="<@ofbizUrl>${submitFormAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "UC") {
            // update cart action
            var modifyCart = "adminModifyCart"; 
            if (updateCart()) {
                form.action="<@ofbizUrl>" + modifyCart + "</@ofbizUrl>";
                form.submit();
            }
        }else if (mode == "UCPS") {
            // update cart pickup in store
            var setStorePickup = "setStorePickup";
            form.action="<@ofbizUrl>" + setStorePickup + "</@ofbizUrl>";
            form.submit();
        }
	}
	
	function refreshFromBottomCart(){
		//set the values from bottom cart to the top cart
		jQuery('.BOTTOM_CART_ITEM').each(function () {
        		var newQty = jQuery(this).val(); 
        		var bottomCartInput = jQuery(this).attr("name");
        		var bottomLineNo = bottomCartInput.split('_')[1];
        		
        		jQuery('#update_'+bottomLineNo).val(newQty);
     	});
     	//refresh the top cart
     	var modifyCart = "adminModifyCart"; 
     	if (updateCart()) {
                document.adminCheckoutFORM.action="<@ofbizUrl>" + modifyCart + "</@ofbizUrl>";
                document.adminCheckoutFORM.submit();
        }
     		
     		
	}
	
	var isWhole_re = /^\s*\d+\s*$/;
    function isWhole (s) {
        return String(s).search (isWhole_re) != -1
    }
	
    function updateCart() {
      var cartItemsNo = ${shoppingCartSize!"0"};
      <#assign PDP_QTY_MIN = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MIN")!"1"/>
      <#assign PDP_QTY_MAX = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MAX")!"99"/> 
      var lowerLimit = ${PDP_QTY_MIN!"1"};
      var upperLimit = ${PDP_QTY_MAX!"99"};
      var zeroQty = false;
      
      for (var i=0;i<cartItemsNo;i++)
      {
          var quantity = jQuery('#update_'+i).val();
          if(quantity != 0) 
          {
          	<#if eCommerceUiLabel?exists >
	          if(quantity < lowerLimit)
	          {
	              alert("${StringUtil.wrapString(StringUtil.replaceString(eCommerceUiLabel.PDPMinQtyError,'\"','\\"'))}");
	              return false;
	          }
	          if(upperLimit!= 0 && quantity > upperLimit)
	          {
	              alert("${StringUtil.wrapString(StringUtil.replaceString(eCommerceUiLabel.PDPMaxQtyError,'\"','\\"'))}");
	              return false;
	          }
	          if(!isWhole(quantity))
	          {
	              alert("${StringUtil.wrapString(StringUtil.replaceString(eCommerceUiLabel.PDPQtyDecimalNumberError,'\"','\\"'))}");
	              return false;
	          }
	        </#if>
          } 
          else
          {
              zeroQty = true;
          }
      }
      if(zeroQty == true)
      {
          window.location='<@ofbizUrl>deleteProductFromCart</@ofbizUrl>';
      }
      return true;
    }
    
	
	function submitDetailUploadForm(form) {
        if(form.action == "")
	    {
	        form.action="<@ofbizUrl>${uploadAction!""}</@ofbizUrl>";
	    }
        form.submit();
	}
	
	function setUploadUrl(fieldId) {
	  var form = document.${detailFormName!"detailForm"};
	  var fieldValue = document.getElementById(fieldId).value;
	  form.action="<@ofbizUrl>${uploadAction!}?${uploadParmName!}=" +fieldValue+ "</@ofbizUrl>";
	}
	
	function postConfirmDialog() {
	    form = document.${detailFormName!"detailForm"};
	    var action = "${confirmAction!'confirmAction'}";
	    if(action.substr(0, 6) == "delete")
	    {
	    	form.action="<@ofbizUrl>${confirmAction!'confirmAction'}?rowDeleted=Y</@ofbizUrl>";
	    }
	    else
	    {
	    	form.action="<@ofbizUrl>${confirmAction!'confirmAction'}</@ofbizUrl>";
	    }
	    form.submit();
	}
	
	function confirmDialogResultAction(result,action) {
        hideDialog('#dialog', '#displayDialog');
        if (result == 'Y') {
            postConfirmDialogAction(action);
        }
        if (result == 'N') {
	        jQuery(".buttontext")[0].onclick = null;
        }
    }
    function hideShowCssDetail(showDiv,hideDiv) {
        jQuery("#"+showDiv).show();
        jQuery("#"+hideDiv).hide();
    }
    function postConfirmDialogAction(action) {
	    form = document.${detailFormName!"detailForm"};
	    form.action="<@ofbizUrl>" + action + "</@ofbizUrl>";
	    if(action.substr(0, 6) == "delete")
	    {
	    	form.action="<@ofbizUrl>" + action + "?rowDeleted=Y</@ofbizUrl>";
	    }
	    else
	    {
	    	form.action="<@ofbizUrl>" + action + "</@ofbizUrl>";
	    }
	    form.submit();
	}
	
	function setConfirmDialogContent(idValue,confirmDialogText,action) {
    	if(!idValue)
    	{
    		jQuery('.confirmTxt').html(confirmDialogText);
    	}
    	else
    	{
    		var idValues = idValue.split(':');
    		count = 0;
    		jQuery('.confirmHiddenFields').each(function () {
        		jQuery(this).val(idValues[count]);
        		count = count +1;
     		});
     		jQuery('.confirmTxt').html(confirmDialogText + ' ' + idValues[0]);
    	}
        jQuery(".buttontext")[0].onclick = null;
		jQuery('input[name="yesBtn"]').click(function() {confirmDialogResultAction('Y',action)});
    }
	
    function showVolumePricing(volumePricingId) {
       jQuery('#'+volumePricingId).show();
       $('default_price').innerHTML=$('Default_Sale_Price').value;
    }
    function hideVolumePricing(volumePricingId) {
       jQuery('#'+volumePricingId).hide();
       $('default_price').innerHTML=$('Sale_Price').value;
    }
    
    
    function setNewRowNo(rowNo)
    {
        jQuery('#rowNo').val(rowNo);
    }
   
    function addNewRow(tableId) {
        var table=document.getElementById(tableId);
        var rows = table.getElementsByTagName('tr');
        var indexPos = jQuery('#rowNo').val();
        addHtmlContent(indexPos, table);
    }
    function removeRow(tableId){
        var table=document.getElementById(tableId);
        var inputRow = table.getElementsByTagName('tr');
        var indexPos = jQuery('#rowNo').val();
        table.deleteRow(indexPos);
        setIndexPos(table);
    }
    
    function addHtmlContent(indexPos, table) {
        var newRow = jQuery('#newRow tr').clone();
        jQuery(newRow).find('input').removeAttr('disabled');
        jQuery(jQuery(table).find('tr')[parseInt(indexPos)-1]).after(newRow);
        setIndexPos(table);
    }
    function deleteConfirmTxt(appendText)
    {
        jQuery('.confirmTxt').html('${confirmDialogText!""} '+appendText+'?');
        displayDialogBox();
    }
    function setIndexPos(table) {
      //var extraRows = jQuery('.extraTr').size()
        var rows = table.getElementsByTagName('tr');
        for (i = rows.length- 1; i >=1 ; i--) {
            var inputs = rows[i].getElementsByTagName('input');
            for (j = 0; j < inputs.length; j++) {
                attrType = inputs[j].getAttribute("type");
                attrId = inputs[j].getAttribute("id");
                inputs[j].setAttribute("name",attrId+"_"+i)
            }
            var anchors = rows[i].getElementsByTagName('a');
            if(anchors.length == 3)  {  
                    var deleteAnchor = anchors[0];
                    if (jQuery(deleteAnchor).hasClass('normalAnchor'))
                    {
                    } else
                    {
                        var deleteTagSecondMethodIndex = deleteAnchor.getAttribute("href").indexOf(";");
                        var deleteTagSecondMethod = deleteAnchor.getAttribute("href").substring(deleteTagSecondMethodIndex+1,deleteAnchor.getAttribute("href").length);
                        deleteAnchor.setAttribute("href", "javascript:setNewRowNo('"+i+"');"+deleteTagSecondMethod);
                    }
                    
                    var insertBeforeAnchor = anchors[1];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setNewRowNo('"+i+"');"+insertBeforeTagSecondMethod);
                    
                    var insertAfterAnchor = anchors[2];
                    var insertAfterTagSecondMethodIndex = insertAfterAnchor.getAttribute("href").indexOf(";");
                    var insertAfterTagSecondMethod = insertAfterAnchor.getAttribute("href").substring(insertAfterTagSecondMethodIndex+1,insertAfterAnchor.getAttribute("href").length);
                    insertAfterAnchor.setAttribute("href", "javascript:setNewRowNo('"+(i+1)+"');"+insertAfterTagSecondMethod);
            }
            if(anchors.length == 1) {
                    var insertBeforeAnchor = anchors[0];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setNewRowNo('"+i+"');"+insertBeforeTagSecondMethod);
            }
        }
        if(rows.length > jQuery('.extraTr').size()) {
            jQuery('#addIconRow').hide();
        } else {
            jQuery('#addIconRow').show();
        }
        jQuery('#totalRows').val(rows.length - jQuery('tr.extraTr').size());
        hideTooltip();
    }
    
    function Status(curBtnVal, displayText, changeBtnVal, hiddenVal)
    {
        this.curBtnVal=curBtnVal;
        this.displayText=displayText;
        this.changeBtnVal=changeBtnVal;
        this.hiddenVal=hiddenVal;
    }
    function updateStatus(statusArray, spanDescId, btnIdField, hiddenField) {
        var btnVal = $(btnIdField).value;
        for(var i=0; i<statusArray.length; i++) {
            if(statusArray[i].curBtnVal == btnVal) {
                $(hiddenField).value=statusArray[i].hiddenVal;
                $(spanDescId).innerHTML = statusArray[i].displayText;
                $(btnIdField).value = statusArray[i].changeBtnVal;
            }
        }
    }

    function getStoreAddressFormat(countryId) {
        if ($F(countryId) == "USA") {
            jQuery('.CAN').hide();
            jQuery('.OTHER').hide();
            jQuery('.USA').show();
        } else if ($F(countryId) == "CAN") {
            jQuery('.USA').hide();
            jQuery('.OTHER').hide();
            jQuery('.CAN').show();
        } else{
            jQuery('.USA').hide();
            jQuery('.CAN').hide();
            jQuery('.OTHER').show();
        }
    }
    function getAddressFormat(idPrefix) {
        var countryId = '#'+idPrefix+'_country';
        if (jQuery(countryId).val() == "USA") {
            jQuery('.'+idPrefix+'_CAN').hide();
            jQuery('.'+idPrefix+'_OTHER').hide();
           jQuery('.'+idPrefix+'_USA').show();
        } else if (jQuery(countryId).val() == "CAN") {
            jQuery('.'+idPrefix+'_USA').hide();
            jQuery('.'+idPrefix+'_OTHER').hide();
            jQuery('.'+idPrefix+'_CAN').show();
        } else{
            jQuery('.'+idPrefix+'_USA').hide();
            jQuery('.'+idPrefix+'_CAN').hide();
            jQuery('.'+idPrefix+'_OTHER').show();
        }
      }

    function copyAddress(fromAddr, fromAddrContainer, toAddr, toAddrSection, triggerElt, isBlindup) {
        jQuery(fromAddrContainer).find('input, select, textarea').change(function(){
          if(jQuery(triggerElt).is(":checked")){
            copyFieldvalue(fromAddr, this, toAddr);
          }
        });
        if(jQuery(triggerElt).is(":checked") && jQuery(toAddrSection).length){
          if (isBlindup) {
            jQuery(toAddrSection).hide();
          }
          copyFieldsInitially(fromAddr, fromAddrContainer, toAddr, triggerElt);
        }
        jQuery(triggerElt).click(function(){
          if (isBlindup) {
            //jQuery(toAddrSection).slideToggle(1000);
            if(jQuery(triggerElt).is(":checked")){
                jQuery(toAddrSection).hide();
            } else {
                jQuery(toAddrSection).show();
            }
          }
          copyFieldsInitially(fromAddr, fromAddrContainer, toAddr, triggerElt);
        });
    }
    
    function copyFieldsInitially(fromAddr, fromAddrContainer, toAddr, triggerElt) {
        jQuery(fromAddrContainer).find('input, select, textarea').each(function(){
          if(jQuery(triggerElt).is(":checked")){
            copyFieldvalue(fromAddr, this, toAddr);
          }
        });
    }
    
    function copyFieldvalue(fromAddrPurpose, fromElm, toAddrPurpose) {
        fromElmId = jQuery(fromElm).attr('id');
        var toElmId = '#'+toAddrPurpose + fromElmId.sub(fromAddrPurpose, "");
        if(jQuery(toElmId).length) {
          if(fromElmId == fromAddrPurpose+'AddressContactMechId') {
              return;
          }
          jQuery(toElmId).val(jQuery(fromElm).val());
          jQuery(toElmId).change();
        }
    }

    function getAssociatedStateList(countryId, stateId, divStateId, addressLine3) {
        var optionList = "";
        jQuery.ajaxSetup({async:false});
        jQuery.post("<@ofbizUrl>getAssociatedStateList</@ofbizUrl>", {countryGeoId: jQuery("#"+countryId).val()}, function(data) {
          var stateList = data.stateList;
          jQuery(stateList).each(function() {
            if (this.geoId) {
              optionList = optionList + "<option value = "+this.geoId+" >"+this.geoName+"</option>";
            } else {
              optionList = optionList + "<option value = >"+this.geoName+"</option>";
            }
          });
          jQuery("#"+stateId).html(optionList);
          if (jQuery(stateList).size() <= 1) {
            jQuery("#"+addressLine3).show();
            jQuery("#"+divStateId).hide();
          } else {
            jQuery("#"+addressLine3).hide();
            jQuery("#"+divStateId).show();
          }
        });
    }
    
    function setProdContentTypeId(prodContentTypeId) {
        jQuery('#productContentTypeId').val(prodContentTypeId);
    }

    function showXLSData(dataDivId, errorDivId, heading) {
        jQuery('.commonDivHide').hide();
        jQuery('#'+dataDivId).show();
        if(errorDivId != '') {
            jQuery('#'+errorDivId).show();
        }
        jQuery('#productLoaderHeader').html('<h2>'+heading+'</h2>');
    }
    
    function setTopNav() {
        jQuery('#topNav').val(jQuery('#topNavBar').val());
    }
    function setMediaDetail(currentMediaName,currentMediaType) {
        jQuery('#currentMediaType').val(currentMediaType);
        jQuery('#currentMediaName').val(currentMediaName);
        jQuery('.confirmTxt').html('${confirmDialogText!""}'+currentMediaName);
    }

    function changeImageRef(showFieldId1,showFieldId2,hideFieldId1, hideFieldId2) 
    { 
 	    jQuery('#'+showFieldId1).show();
 	    jQuery('#'+showFieldId2).show();
 	    jQuery('#'+hideFieldId1).hide();
 	    jQuery('#'+hideFieldId2).hide();
    }

    function addDivRow(processObject) {
        var processDiv = document.getElementById(processObject.divId);
        //var indexPos = jQuery('#'+processObject.divId).children(".row").length - 1;
        var indexPos = 0;
        var insertBeforeDiv;
        var childDiv = processDiv.getElementsByTagName('div');
        for (var i = 0, j = childDiv.length; i < j; i++) {
            var styleClass = childDiv[i].className.split(" ");
            for (var k = 0, l = styleClass.length; k < l; k++) {
                if (styleClass[k] == "dataRow") {
                    insertBeforeDiv = childDiv[i];
                    indexPos++;
                }
            }
        }
        indexPos--;
        var rowDiv = new Element('DIV');
        rowDiv.setAttribute("class", "dataRow");

        //create selected operator name div
        var columnDiv = new Element('DIV');
        columnDiv.setAttribute("class", "dataColumn operDataColumn");
        var selectObj = document.getElementById(processObject.processTypeSelectId);
        var textNode = document.createTextNode(selectObj.options[selectObj.selectedIndex].text);
        columnDiv.appendChild(textNode);
        rowDiv.appendChild(columnDiv);

        //create selected category/product name div
        columnDiv = new Element('DIV');
        columnDiv.setAttribute("class", "dataColumn nameDataColumn");
        textNode = document.createTextNode(document.getElementById(processObject.processTypeHiddenId2).value);
        columnDiv.appendChild(textNode);
        rowDiv.appendChild(columnDiv);

        //create sremove button div
        columnDiv = new Element('DIV');
        columnDiv.setAttribute("class", "dataColumn actionDataColumn");
        //create remove button
        var buttonAnchor = document.createElement("A");
        buttonAnchor.setAttribute("class", "standardBtn secondary");
        buttonAnchor.setAttribute("href", "javascript:deleteDivRow('"+processObject.divId+"', '"+processObject.dataRows+"', "+indexPos+")");
        buttonAnchor.appendChild(document.createTextNode('${uiLabelMap.RemoveBtn}'));
        columnDiv.appendChild(buttonAnchor);
        //create selected operator hidden field
        var element = document.createElement("input");
        element.setAttribute("type", "hidden");
        element.setAttribute("value", document.getElementById(processObject.processTypeSelectId).value);
        element.setAttribute("id", processObject.newTypeHiddenNamePrefix1+indexPos)
        element.setAttribute("name", processObject.newTypeHiddenNamePrefix1+indexPos)
        columnDiv.appendChild(element);
        //create selected category/product id hidden field
        element = document.createElement("input");
        element.setAttribute("type", "hidden");
        element.setAttribute("value", document.getElementById(processObject.processTypeHiddenId1).value);
        element.setAttribute("id", processObject.newTypeHiddenNamePrefix2+indexPos)
        element.setAttribute("name", processObject.newTypeHiddenNamePrefix2+indexPos)
        columnDiv.appendChild(element);
        rowDiv.appendChild(columnDiv);

        //processDiv.appendChild(rowDiv);
        processDiv.insertBefore(rowDiv,insertBeforeDiv);
        updateIndexPosition(processObject.divId, processObject.dataRows);
    }
    function deleteDivRow(divId, dataRows, deleteIndexPos){
        var processDiv = document.getElementById(divId);
        var indexPos = 0;
        var childDiv = processDiv.getElementsByTagName('div');
        for (var i = 0; i < childDiv.length; i++) {
            if (childDiv[i].className == "dataRow") {
                if (indexPos == deleteIndexPos) {
                    childDiv[i].parentNode.removeChild(childDiv[i]);
                }
                indexPos++
            }
        }
        updateIndexPosition(divId, dataRows);
    }
    function updateIndexPosition(divId, dataRows) {
        var processDiv = document.getElementById(divId);
        var dataRows = document.getElementById(dataRows);
        var indexPos = 0;
        var childDiv = processDiv.getElementsByTagName('div');
        for (var i = 0; i < childDiv.length; i++) {
            if (childDiv[i].className == "dataRow") {
                var inputs = childDiv[i].getElementsByTagName('input');
                for (j = 0; j < inputs.length; j++) {
                    var attrName =  inputs[j].getAttribute("name");
                    inputs[j].setAttribute("name",attrName.substring(0, attrName.length-1)+indexPos)
                    var attrId =  inputs[j].getAttribute("id");
                    inputs[j].setAttribute("id",attrId.substring(0, attrId.length-1)+indexPos)
                }
                var anchorTags = childDiv[i].getElementsByTagName('A');
                for (j = 0; j < anchorTags.length; j++) {
                    var anchorHref =  anchorTags[j].getAttribute("href");
                    anchorTags[j].setAttribute("href",anchorHref.substring(0, anchorHref.lastIndexOf(")")-1)+indexPos+")")
                }
                indexPos++
            }
        }
        dataRows.value = indexPos;
    }
    
    function clearField() {
        $('quantity').value = "";
        $('amount').value = "";
        $('productId').value = "";
    }
    function getDisplayFormat(productPromoActionEnumId) {
        var enumId = jQuery(productPromoActionEnumId).val();
        if (enumId == "PROMO_GWP") {
            jQuery('.QTYDIV').show();
            jQuery('.QTY').show();
            jQuery('.MINQTY').hide();
            jQuery('.AMOUNTDIV').hide();
            jQuery('.PRICE').hide();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').hide();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').show();
            jQuery('.promoActionCategory').hide();
            jQuery('.promoActionProduct').hide();
        } else if (enumId == "PROMO_PROD_DISC") {
            jQuery('.QTYDIV').show();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').show();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").addClass('smallLabel');
            jQuery('.PRICE').hide();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').show();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').show();
            jQuery('.promoActionProduct').show();
        } else if (enumId == "PROMO_PROD_AMDISC") {
            jQuery('.QTYDIV').show();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').show();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").addClass('smallLabel');
            jQuery('.PRICE').hide();
            jQuery('.DISC').show();
            jQuery('.DISCPER').hide();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').show();
            jQuery('.promoActionProduct').show();
        } else if (enumId == "PROMO_PROD_PRICE") {
            jQuery('.QTYDIV').show();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').show();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").addClass('smallLabel');
            jQuery('.PRICE').show();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').hide();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').show();
            jQuery('.promoActionProduct').show();
        } else if (enumId == "PROMO_ORDER_PERCENT") {
            jQuery('.QTYDIV').hide();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').hide();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").removeClass('smallLabel');
            jQuery('.PRICE').hide();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').show();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').hide();
            jQuery('.promoActionProduct').hide();
        } else if (enumId == "PROMO_ORDER_AMOUNT") {
            jQuery('.QTYDIV').hide();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').hide();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").removeClass('smallLabel');
            jQuery('.PRICE').show();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').hide();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').hide();
            jQuery('.promoActionProduct').hide();
        } else if (enumId == "PROMO_PROD_SPPRC") {
            jQuery('.QTYDIV').hide();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').hide();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").removeClass('smallLabel');
            jQuery('.PRICE').show();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').hide();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').show();
            jQuery('.promoActionProduct').show();
        } else if (enumId == "PROMO_SHIP_CHARGE") {
            jQuery('.QTYDIV').hide();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').hide();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").removeClass('smallLabel');
            jQuery('.PRICE').hide();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').hide();
            jQuery('.SHIPDISCPER').show();
            jQuery('.TAXDISCPER').hide();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').hide();
            jQuery('.promoActionProduct').hide();
        } else if (enumId == "PROMO_TAX_PERCENT") {
            jQuery('.QTYDIV').hide();
            jQuery('.QTY').hide();
            jQuery('.MINQTY').hide();
            jQuery('.AMOUNTDIV').show();
            jQuery("div.AMOUNTDIV div:first-child label").removeClass('smallLabel');
            jQuery('.PRICE').hide();
            jQuery('.DISC').hide();
            jQuery('.DISCPER').hide();
            jQuery('.SHIPDISCPER').hide();
            jQuery('.TAXDISCPER').show();
            jQuery('.ITEMDIV').hide();
            jQuery('.promoActionCategory').hide();
            jQuery('.promoActionProduct').hide();
        } else if (enumId == "PPIP_ORDER_TOTAL") {
            jQuery('.promoConditionCategory').hide();
            jQuery('.promoConditionProduct').hide();
            jQuery('.promoConditionShippingMethod').hide();
        } else if (enumId == "PPIP_PRODUCT_TOTAL") {
            jQuery('.promoConditionCategory').show();
            jQuery('.promoConditionProduct').show();
            jQuery('.promoConditionShippingMethod').hide();
        } else if (enumId == "PPIP_PRODUCT_AMOUNT") {
            jQuery('.promoConditionCategory').show();
            jQuery('.promoConditionProduct').show();
            jQuery('.promoConditionShippingMethod').hide();
        } else if (enumId == "PPIP_PRODUCT_QUANT") {
            jQuery('.promoConditionCategory').show();
            jQuery('.promoConditionProduct').show();
            jQuery('.promoConditionShippingMethod').hide();
        } else if (enumId == "PPIP_ORDER_SHIPTOTAL") {
            jQuery('.promoConditionCategory').hide();
            jQuery('.promoConditionProduct').hide();
            jQuery('.promoConditionShippingMethod').show();
        }
    }
    function changeColor(inputId) {
        var input=document.getElementById(inputId);
        input.style.backgroundColor = "white";
    }
    function setStyleName(styleFileName,inputField) {
        document.getElementById(inputField).value = styleFileName;
        <#if detailFormName?has_content>
          submitDetailForm(document.${detailFormName!""}, 'MA');
        </#if>
    }
    function setNewValue(key,value) {
        document.getElementById('newValue').value = value;
        document.getElementById('key').value = key;
        
        <#if detailFormName?has_content>
          document.${detailFormName!""}.submit();
          //submitDetailForm(document.${detailFormName!""}, 'MA');
        </#if>
    }
    function setStars(starValue) {
       // Change stars image
       var ratingPerct = ((starValue / 5) * 100);
        $('productRatingStars').style.width = ratingPerct+ '%';

       // Set new stars value in form
        var form = document.reviewFORM;
        form.elements['productRating'].value=starValue;
    }
    function updateReview(status) {
        // Set form value
        var form = document.${detailFormName!"reviewForm"};
        form.elements['statusId'].value=status;
        // Chnage display value
        if (status=='PRR_APPROVED'){
            jQuery('#reviewStatus').html("${uiLabelMap.ApprovedLabel}");
            jQuery('.PRR_APPROVED').show();
            jQuery('.PRR_PENDING').hide();
            jQuery('.PRR_DELETED').hide();
        } else if(status=='PRR_PENDING'){
            jQuery('#reviewStatus').html("${uiLabelMap.PendingLabel}");
            jQuery('.PRR_APPROVED').hide();
            jQuery('.PRR_PENDING').show();
            jQuery('.PRR_DELETED').hide();
        } else if(status=='PRR_DELETED'){
            jQuery('#reviewStatus').html("${uiLabelMap.DeletedLabel}");
            jQuery('.PRR_APPROVED').hide();
            jQuery('.PRR_PENDING').hide();
            jQuery('.PRR_DELETED').show();
        }
    }
  
//begin JQuery for scheduledJobRule 
//handle the display of the helper text for the Unit of the frequency interval 
//when page is displayed, this will run		
jQuery(document).ready(function(){
		var servFreq = jQuery('#SERVICE_FREQUENCY').val();
		if(servFreq=="")
		{
			servFreq = jQuery('#SERVICE_FREQUENCYspan').text();
		}
		var servInter = jQuery('#SERVICE_INTERVAL').val();
		if(servInter=="")
		{
			servInter = jQuery('#SERVICE_INTERVALspan').text();
		}
		var intervalUnit = "";
		if(servFreq != "")
		{
			
			if(servInter != "")
			{
				if(servFreq == "4")
				{
					intervalUnit= "${uiLabelMap.Days}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Day}";
					}
				}
				if(servFreq == "5")
				{
					intervalUnit= "${uiLabelMap.Weeks}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Week}";
					}
				}
				if(servFreq == "6")
				{
					intervalUnit= "${uiLabelMap.Months}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Month}";
					}
				}
				if(servFreq == "7")
				{
					intervalUnit= "${uiLabelMap.Years}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Year}";
					}
				}
				if(servFreq == "3")
				{
					intervalUnit= "${uiLabelMap.Hours}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Hour}";
					}
				}
				if(servFreq == "2")
				{
					intervalUnit= "${uiLabelMap.Minutes}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Minute}";
					}
				}
			}
		jQuery("#intervalUnit").text(intervalUnit);
		}	
	//when values are changed, run this:
	jQuery('.intervalUnitSet').change(function() {
		var servFreq = jQuery('#SERVICE_FREQUENCY').val();
		var servInter = jQuery('#SERVICE_INTERVAL').val();
		var intervalUnit = "";
		if(servFreq != "")
		{
			if(servInter != "")
			{
				if(servFreq == "4")
				{
					intervalUnit= "${uiLabelMap.Days}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Day}";
					}
				}
				if(servFreq == "5")
				{
					intervalUnit= "${uiLabelMap.Weeks}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Week}";
					}
				}
				if(servFreq == "6")
				{
					intervalUnit= "${uiLabelMap.Months}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Month}";
					}
				}
				if(servFreq == "7")
				{
					intervalUnit= "${uiLabelMap.Years}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Year}";
					}
				}
				if(servFreq == "3")
				{
					intervalUnit= "${uiLabelMap.Hours}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Hour}";
					}
				}
				if(servFreq == "2")
				{
					intervalUnit= "${uiLabelMap.Minutes}";
					if(servInter == "1")
					{
						intervalUnit= "${uiLabelMap.Minute}";
					}
				}
			}
		jQuery("#intervalUnit").text(intervalUnit);
		}	
	});
});//end of JQuery for scheduledJobsRule

function deleteCategoryMemberRow(categoryName, parentCategoryName)
{
    jQuery('#confirmDeleteTxt').html('${confirmDialogText!""} '+parentCategoryName+'/'+categoryName+'?');
    displayDialogBox();
}

function removeCategoryMemberRow(tableId){
    var table=document.getElementById(tableId);
    var inputRow = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    table.deleteRow(indexPos);
    hideDialog('#dialog', '#displayDialog');
    setTableIndexPos(table);
}
function addCategoryMemberRow(tableId) 
{
    var table = document.getElementById(tableId);
    var rows = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    var row = table.insertRow(indexPos);
    
    productCategoryId =  jQuery('#productCategoryId').val();
    productCategoryName = jQuery('#productCategoryName').val(); 
    
    jQuery.get('<@ofbizUrl>addCategoryMemberRow?productCategoryId='+productCategoryId+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
        jQuery(row).replaceWith(data);
        setTableIndexPos(table);
    });
}

//jQuery for userSecurityGroup

function deleteGroupTableRow(groupId)
{
    var textConfirmDelete = "${confirmDialogText!""}";
	textConfirmDelete = textConfirmDelete.replace("_SECURITY_GROUP_ID_", groupId);
    jQuery('#confirmDeleteTxt').html(textConfirmDelete);
    displayDialogBox();
}

function removeGroupRow(tableId){
    var table=document.getElementById(tableId);
    var inputRow = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    table.deleteRow(indexPos);
    hideDialog('#dialog', '#displayDialog');
    setTableIndexPos(table);
}
function addGroupRow(tableId) 
{
    var table = document.getElementById(tableId);
    var rows = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    var row = table.insertRow(indexPos);
    
    groupId =  jQuery('#addGroupId').val();
    jQuery.get('<@ofbizUrl>addSecurityGroupRow?groupId='+groupId+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
        jQuery(row).replaceWith(data);
        setTableIndexPos(table);
    });
}
//end of jQuery for userSecurityGroup

//jQuery for securityGroupPermission
function deletePermissionTableRow(permissionId)
{
	var textConfirmDelete = "${confirmDialogText!""}";
	textConfirmDelete = textConfirmDelete.replace("_PERMISSION_ID_", permissionId);
    jQuery('#confirmDeleteTxt').html(textConfirmDelete);
    displayDialogBox();
}

function removePermissionRow(tableId){
    var table=document.getElementById(tableId);
    var inputRow = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    table.deleteRow(indexPos);
    hideDialog('#dialog', '#displayDialog');
    setTableIndexPos(table);
}
function addPermissionRow(tableId) 
{
    var table = document.getElementById(tableId);
    var rows = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    var row = table.insertRow(indexPos);
    
    permissionId =  jQuery('#addPermissionId').val();
    jQuery.get('<@ofbizUrl>addPermissionRow?permissionId='+permissionId+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
        jQuery(row).replaceWith(data);
        setTableIndexPos(table);
    });
}
//end of jQuery for securityGroupPermission
function setTableIndexPos(table)
{
    var rows = table.getElementsByTagName('tr');
    for (i = 1; i < rows.length; i++) {
        var columns = rows[i].getElementsByTagName('td');
        for (j = 0; j < columns.length; j++) {
            if(j ==  (columns.length-1)) {
                var anchors = columns[j].getElementsByTagName('a');
                if(anchors.length == 3) {
                    var deleteAnchor = anchors[0];
                    var deleteTagSecondMethodIndex = deleteAnchor.getAttribute("href").indexOf(";");
                    var deleteTagSecondMethod = deleteAnchor.getAttribute("href").substring(deleteTagSecondMethodIndex+1,deleteAnchor.getAttribute("href").length);
                    deleteAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+deleteTagSecondMethod);
                    
                    var insertBeforeAnchor = anchors[1];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+insertBeforeTagSecondMethod);
                    
                    var insertAfterAnchor = anchors[2];
                    var insertAfterTagSecondMethodIndex = insertAfterAnchor.getAttribute("href").indexOf(";");
                    var insertAfterTagSecondMethod = insertAfterAnchor.getAttribute("href").substring(insertAfterTagSecondMethodIndex+1,insertAfterAnchor.getAttribute("href").length);
                    insertAfterAnchor.setAttribute("href", "javascript:setRowNo('"+(i+1)+"');"+insertAfterTagSecondMethod);
                }
                    
                if(anchors.length == 1) {
                    var insertBeforeAnchor = anchors[0];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+insertBeforeTagSecondMethod);
                }
            }
        }
        var inputs = rows[i].getElementsByTagName('input');
        for (j = 0; j < inputs.length; j++) {
            attrId = inputs[j].getAttribute("id");
            inputs[j].setAttribute("name",attrId+"_"+i)
        }
    }
    if(rows.length > 2) {
       jQuery('#addIconRow').hide();
    } else {
       jQuery('#addIconRow').show();
    }
    $('totalRows').value = rows.length-2;
}
function setRowNo(rowNo) {
    jQuery('#rowNo').val(rowNo);
}


 function setParamsForList(status,size) { 
 	if(status != "")
 	{
 		jQuery('.confirmHiddenParamStatusId').val(status);
 	}
 	if(size != "")
 	{
 		jQuery('.confirmHiddenParamViewSize').val(size);
 	}
 }
 
    // update the shopping cart sections and update the promotion section
    function setShippingMethod(selectedShippingOption, isOnLoad) {
        if (jQuery('#shoppingCartContainer').length) {
            jQuery('#shoppingCartContainer').load('<@ofbizUrl>setShippingOption?shipMethod='+selectedShippingOption+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>',  function(){
	            if (jQuery('#shoppingCartBottomContainer').length) {
		            jQuery('#shoppingCartBottomContainer').load('<@ofbizUrl>setShippingOptionBottom?shipMethod='+selectedShippingOption+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(){
		            	if((isOnLoad != null) && (isOnLoad =='N')) {
		            		if (jQuery('#promoCodeContainer').length) { 
				                jQuery('#promoCodeContainer').load('<@ofbizUrl>reloadPromoCode?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>');
				            }
		            	}
		            });
		        }
            });
        }
        
        if(selectedShippingOption != "NO_SHIPPING@_NA_")
        {
        	jQuery('#checkoutStoreName').hide();
        }
    }
    
    //add promo code for checkout screen
    function addManualPromoCode() {
        if (jQuery('#manualOfferCode').length && jQuery('#manualOfferCode').val() != null) {
          promo = jQuery('#manualOfferCode').val().toUpperCase();
          promoCodeWithoutSpace = promo.replace(/^\s+|\s+$/g, "");
        }
        var cform = document.${detailFormName!"adminCheckoutFORM"};
        cform.action="<@ofbizUrl>adminValidatePromoCode?productPromoCodeId="+promoCodeWithoutSpace+"</@ofbizUrl>";
        cform.submit();
    }
    
    //remove promo code for checkout screen
    function removePromoCode(promoCode) {
        if (promoCode != null) {
          var cform = document.${detailFormName!"adminCheckoutFORM"};
          cform.action="<@ofbizUrl>adminRemovePromoCode?productPromoCodeId="+promoCode+"</@ofbizUrl>";
          cform.submit();
        }
    }
    
    //disabled or enabled the Selectable feature based on the Finished/Virtual
    function selectFinishedProduct(elm) {
        if(jQuery(elm+':checked').val() == "N"){
            jQuery('.selectableRadio').hide();
        } else {
            jQuery('.selectableRadio').show();
        }
    }
    function clearCache(cacheName, cacheType)
    {
        document.getElementById('cacheName').value=cacheName;
        document.getElementById('cacheType').value=cacheType;
        var textConfirmClear = "${StringUtil.wrapString(confirmDialogText!"")}";
        textConfirmClear = textConfirmClear.replace("_CACHE_TYPE_", cacheType);
        jQuery('.confirmTxt').html(textConfirmClear);
        displayDialogBox();
    }
    
    function setManufacturerIdDisplay() 
	{
	    var manufacturerId = jQuery("#manufacturerPartyId").val();
	    jQuery("#productManufacturer").text(manufacturerId);
	}
	
	
	var flag = "false";
	function activateService()
	{
	    
	    jQuery.post("<@ofbizUrl>solrReIndexAjax</@ofbizUrl>", jQuery('input:hidden').serialize(), function(data) {
	        flag = "true";
        });
	                 var img = new Image();
                     jQuery(img).attr('src','<@ofbizContentUrl>/osafe_theme/images/user_content/images/loading.gif</@ofbizContentUrl>');
                     jQuery('#loadingImg').html(img);
	
	(function keepBrowserAlive()
	{
	    setTimeout(function() 
	         {
                 if (flag != "true") 
                 {
                     var img = new Image();
                     jQuery(img).attr('src','<@ofbizContentUrl>/osafe_theme/images/user_content/images/loading.gif</@ofbizContentUrl>');
                     jQuery('#loadingImg').html(img);
                     keepBrowserAlive();
                 }
                 else
                 {
                     jQuery('#loadingImg').hide();
                 }
             }, 5000);
	   
	})();
	
	}
</script>

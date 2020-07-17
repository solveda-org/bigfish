<script type="text/javascript">
    jQuery(document).ready(function () 
    {
        //Prevents the Page to Hit the Enter Key
        jQuery(window).keydown(function(event)
        {
            if(event.target.type != 'textarea')
            {
                if(event.keyCode == 13) 
                {
		            event.preventDefault();
		            return false;
		        }
            }
		});
        
        if(jQuery('#ckeditor').length)
        {
            CKEDITOR.disableAutoInline = true;
            CKEDITOR.config.enterMode = CKEDITOR.ENTER_BR;
            var editor = CKEDITOR.replace( 'ckeditor',{
              toolbar: [
		        { name: 'document',items:['Cut','Copy','Paste','-','Undo','Redo']},	
		        { name: 'basicstyles',items:[ 'Bold', 'Italic','Underline' ]},
                { name: 'paragraph',items:[ 'NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl']},
                { name: 'colors',items:['TextColor','BGColor']},
                { name: 'styles',items:['Font','FontSize']}
	          ]
            });
        }
		<#if errorMessageList?has_content>
          <#list errorMessageList as errorMsg> 
          	<#if errorMsg?has_content && errorMsg.getClass()?has_content>         
	            try
	            { 				
	                <#if errorMsg.getClass().getName() == "org.ofbiz.base.util.MessageString">
	                	var fld="${errorMsg.getFieldName()}";
	    				if(fld!="" || fld!=undefined)
	    				{
	    		  			jQuery('[name='+fld+']').addClass("inError");
	    		  			
	    		  			<#-- For Custom Attributes Error Field -->
	    		  			if(fld.startsWith("FIELD_ERROR_"))
	    		  			{
	    		  			    jQuery('.'+fld).addClass("inError");
	    		  			}
	    				}            
					</#if>
				}catch(e){} 
			</#if>   
            </#list>
        </#if>
    
    
        if (jQuery('#type').change.length)
        {
           jQuery('#type').change(function()
	       {
	           var type =  jQuery('#type').attr('value');
	           setCustomAttributeFields(type);
	       });
        }
        if(jQuery('#type').length)
        {
    	    setCustomAttributeFields(jQuery('#type').attr('value'));
    	}
    	
    	if (jQuery('input:radio[name="mandatory"]:checked').length) 
        {
            var requiredRadio = jQuery('input:radio[name="mandatory"]:checked');
            setCustomAttributeRequiredMessageField(requiredRadio);
        }
        
        if (jQuery('input:radio[name="dataResourceTypeId"]:checked').length) 
        {
            var dataResourceTypeId = jQuery('input:radio[name="dataResourceTypeId"]:checked');
            setFileEnabledContent(dataResourceTypeId);
        }
        
        <#-- disabled or enabled the Selectable feature based on the Finished/Virtual -->
        if (jQuery('input:radio[name="isVirtual"]:checked').length) 
        {
            var virtualRadio = jQuery('input:radio[name="isVirtual"]:checked');
            selectFinishedProduct(virtualRadio);
        }
        
    	<#-- handle the disable date -->
		var enabledCheckBox = jQuery('input:radio[name=enabled]:checked').val();
		if(enabledCheckBox == "Y")
		{
			<#-- if it equals Y, then disable the disable date USER_DISABLED_DATE_TIME -->
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
		
			<#-- else enable the disable date USER_DISABLED_DATE_TIME -->
			jQuery('#USER_DISABLED_DATE').prop('disabled', false);	
			jQuery('#USER_DISABLED_HOUR').prop('disabled', false);	
			jQuery('#USER_DISABLED_MINUTE').prop('disabled', false);
			jQuery('#USER_DISABLED_AMPM').prop('disabled', false);	
			
			jQuery("#USER_DISABLED_DATE_TIME").attr('class', 'dateEntry');
			jQuery(".ui-datepicker-trigger").show();
		}	
	
		<#-- when values are changed, run this: -->
		jQuery('.USER_ENABLED_CHECKBOX_').change(function() {
			var enabledCheckBox = jQuery('input:radio[name=enabled]:checked').val();
			if(enabledCheckBox == "Y")
			{
				<#-- if it equals Y, then disable the disable date USER_DISABLED_DATE_TIME -->
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
				<#-- else enable the disable date USER_DISABLED_DATE_TIME -->
				jQuery('#USER_DISABLED_DATE').prop('disabled', false);	
				jQuery('#USER_DISABLED_HOUR').prop('disabled', false);	
				jQuery('#USER_DISABLED_MINUTE').prop('disabled', false);
				jQuery('#USER_DISABLED_AMPM').prop('disabled', false);	
				
				jQuery("#USER_DISABLED_DATE_TIME").attr('class', 'dateEntry');
				jQuery(".ui-datepicker-trigger").show();
			}
			
		});
	
	    jQuery('.orderItemSeqId.checkBoxEntry').each(function(){
	        if(jQuery(this+':checked').length)
	        {
	            if(jQuery(this+':checked'))
	            {
	                if(jQuery('#statusId').val() == 'PRODUCT_RETURN' || jQuery('#statusId').val() == 'ORDER_CANCELLED')
	                {
	                    getOrderRefundData();
	                }
	            }
	        }
        });
        
        if (jQuery("#orderItemSeqIdall").length)
        {
        	<#assign orderAdjustmentDesc = parameters.orderAdjustmentDescription!""/>
        	<#assign orderAdjustmentAmount = parameters.orderAdjustmentAmount!""/>
        	<#if orderAdjustmentDesc?has_content>
        		jQuery("#orderAdjustmentDescription").val("${orderAdjustmentDesc}");
        	</#if>
        	<#if orderAdjustmentAmount?has_content>
        		jQuery("#orderAdjustmentAmount").val("${StringUtil.wrapString(orderAdjustmentAmount!)}");
        	</#if>
        }
        
	
        jQuery('.displayBox.slidingClose').each(function(){
            slidingInit(this, 'slidePlusIcon');
        });
        
        jQuery('.displayBox.slidingOpen').each(function(){
            slidingInit(this, 'slideMinusIcon');
        });
        
        jQuery('.displayListBox.slidingClose').each(function(){
            slidingInit(this, 'slidePlusIcon');
        });
        
        jQuery('.displayListBox.slidingOpen').each(function(){
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
           getOrderStatusChangeDisplay('#actionId');
           jQuery('#actionId').click(function(){
               getOrderStatusChangeDisplay('#actionId');
           });
       } */
       <#if review?has_content>
           updateReview("${parameters.statusId!review.statusId}");
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

        if (jQuery('#countryGeoId').length)
        {
            getStoreAddressFormat('countryGeoId');
            Event.observe($('countryGeoId'), 'change', function(){
                getAssociatedStateList('countryGeoId', 'stateProvinceGeoId', 'divStateProvinceGeoId', 'divAddress3');
                getStoreAddressFormat('countryGeoId');
              });
        }
        
       jQuery("div.actionIconMenu").mouseenter(function(event)
       {
           showActionIcontip(event, this)
       }).mouseleave(function(event)
       {
           hideActionIcontip(event, this)
       });

        if(jQuery('#shipByDate').length && (jQuery('#shipByDate') != "" || jQuery('#shipByDate') != null))
        {
            validateInput('#shipByDate', '+DATE', '${uiLabelMap.OrderShipDateWarning}')
        }

        if (jQuery('.priceRuleInputParamEnumId').length) 
        {
            showLookupIconPageLoad();
        }
    });

    function setCustomAttributeFields(type)
    {
        if(type == "ENTRY")
	    {
	        jQuery('.ENTRY_FORMAT').show();
	    }
	    else
	    {
	        jQuery('.ENTRY_FORMAT').hide();
	    }
	    if(type == "ENTRY" || type == "ENTRY_BOX")
	    {
	        jQuery('.MAX_LENGTH').show();
	    }
	    else
	    {
	        jQuery('.MAX_LENGTH').hide();
	    }
	    if(type == "RADIO_BUTTON" || type == "CHECKBOX" || type == "DROP_DOWN" || type == "DROP_DOWN_MULTI")
	    {
	        jQuery('.VALUE_LIST').show();
	    }
	    else
	    {
	        jQuery('.VALUE_LIST').hide();
	    }
    }

    function setCustomAttributeRequiredMessageField(elm)
    {
        if (jQuery(elm).attr('name') == undefined)
        {
            return;
        }
        if(jQuery(elm+':checked').val() == "Y")
        {
            jQuery('.REQ_MESSAGE').show();
        } 
        else 
        {
            jQuery('.REQ_MESSAGE').hide();
        }
    }

    function getOrderRefundData()
    {
        jQuery('.shipQuantity').each(function()
        {
        	var shipQuantityId = jQuery(this).attr("id");
		    if(shipQuantityId != "")
		    {
		  	  var shipQuantityIdArray = shipQuantityId.split("_");
		  	  var shipQauntityIndex = shipQuantityIdArray[1];
		  	  if(jQuery("#orderItemSeqId-" + shipQauntityIndex).length)
		  	  {
		  	  	if(jQuery("#orderItemSeqId-" + shipQauntityIndex).is(':checked'))
		  	  	{
		  	  		if(jQuery("#returnQuantity_" + shipQauntityIndex).length)
		  	  		{
		  	  			var returnQuantityVal = jQuery("#returnQuantity_" + shipQauntityIndex).val();
		  	  			if(returnQuantityVal == "")
		  	  			{
		  	  				var shipQtyVal = jQuery("#shipQuantity_" + shipQauntityIndex).val();
		  	  				jQuery("#returnQuantity_" + shipQauntityIndex).val(shipQtyVal)
		  	  			}
		  	  		}
		  	  	}
		  	  }
		    }
        });
        
        
        var orderAdjustmentDesc = jQuery("#orderAdjustmentDescription").val();
        var orderAdjustmentAmount = jQuery("#orderAdjustmentAmount").val();
        
        if (jQuery('input:radio[name=actionId]:checked').val() == "cancelOrder" || jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
           var url = "<@ofbizUrl>getOrderStatusRefundDetail</@ofbizUrl>";
           jQuery.ajaxSetup({async:false});
           jQuery.ajax(
           {
               type: "POST",
               url: url,
               data: jQuery("#orderStatusForm").serialize(), // serializes the form's elements.
               success: function(data)
               {
                   jQuery('#orderRefundInfoBox').html('');
                   jQuery('#orderRefundInfoBox').html(data);
                   jQuery('#orderRefundInfoBox').show();
               }
           });
        }
        
        if(orderAdjustmentDesc != "")
        {
        	jQuery("#orderAdjustmentDescription").val(orderAdjustmentDesc);
        }
        
        if(orderAdjustmentAmount != "")
        {
        	jQuery("#orderAdjustmentAmount").val(orderAdjustmentAmount);
        }
    }
    <#-- Popup window code -->
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
            jQuery('.customerIdDiv').hide();
            jQuery('.orderIdDiv').hide();
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
        else if ((templateIdValue == "TXT_CHANGE_CUSTOMER") || (templateIdValue == "TXT_NEW_CUSTOMER") || (templateIdValue == "TXT_FORGOT_PASSWORD") )
        {
            jQuery('.customerIdDiv').show();
            jQuery('.orderIdDiv').hide();
        }
        else if ((templateIdValue == "TXT_ORDER_CHANGE") || (templateIdValue == "TXT_ORDER_CONFIRM") || (templateIdValue == "TXT_SHIP_REVIEW") || (templateIdValue == "TXT_ABANDON_CART")) 
        {
            jQuery('.customerIdDiv').hide();
            jQuery('.orderIdDiv').show();
        } 
        else
        {
            jQuery('.customerIdDiv').hide();
            jQuery('.orderIdDiv').hide();
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

    function showFeature(elm, index) 
    {
        if (jQuery(elm).val() == "DISTINGUISHING_FEAT") 
        {
            if(jQuery('#distinguishProductFeatureMulti_'+index).val().length)
            {
                jQuery('.productFeatureId_' + index).show();
            }
            else
            {
                jQuery('#productFeatureId_' + index).show();
            }
        } 
        else 
        {
            jQuery('#productFeatureId_' + index).hide();
            jQuery('.productFeatureId_' + index).hide();
        }
        if (jQuery(elm).val() == "DISTINGUISHING_FEAT") 
        {
            jQuery('#selectedHelperIcon_' + index).hide();
            jQuery('#notApplicableHelperIcon_' + index).hide();
            jQuery('#descriptiveHelperIcon_' + index).show();
            jQuery('#descriptiveFeaturePickerIcon_' + index).show();
        }
        else if(jQuery(elm).val() == "SELECTABLE_FEATURE")
        {
            jQuery('#descriptiveHelperIcon_' + index).hide();
            jQuery('#notApplicableHelperIcon_' + index).hide();
            jQuery('#selectedHelperIcon_' + index).show();
            jQuery('#descriptiveFeaturePickerIcon_' + index).hide();
        }
        else
        {
            jQuery('#selectedHelperIcon_' + index).hide();
            jQuery('#descriptiveHelperIcon_' + index).hide();
            jQuery('#notApplicableHelperIcon_' + index).show();
            jQuery('#descriptiveFeaturePickerIcon_' + index).hide();
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
            jQuery(this).parent('h2').parent('.header').nextAll('.boxBody').slideToggle(1000);
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
            jQuery('.changeOrderStatus').hide();
            jQuery('.completeMultiShipGroups').show();
            
            jQuery('.productReturnCheckboxInfo').hide();
            jQuery('.itemCancelCheckboxInfo').hide();
            jQuery('.itemCompleteCheckboxInfo').show();
        }
        else 
        {
            jQuery('.changeOrderStatus').show();
            jQuery('.completeMultiShipGroups').hide();
            jQuery('.COMPLETED').hide();
        }
        
        if (jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
            jQuery('.productReturnOrderCheckbox').show();
            jQuery('.statusChangeOrderCheckbox').hide();
            
            jQuery('th.returnItemHead').show();
            jQuery('td.returnItemData').show();
            
            jQuery('.productReturnCheckboxInfo').show();
            jQuery('.itemCancelCheckboxInfo').hide();
            jQuery('.itemCompleteCheckboxInfo').hide();
        }
        else 
        {
            jQuery('.productReturnOrderCheckbox').hide();
            jQuery('.statusChangeOrderCheckbox').show();
            jQuery('th.returnItemHead').hide();
            jQuery('td.returnItemData').hide();
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
            jQuery('.productReturnCheckboxInfo').hide();
            jQuery('.itemCancelCheckboxInfo').show();
            jQuery('.itemCompleteCheckboxInfo').hide();
            jQuery('.action').each(function()
            {
                var hrefValue = jQuery(this).attr('href');
                if(hrefValue!= null && hrefValue.startsWith("javascript:submitDetailForm"))
                {
                    jQuery(this).html("${uiLabelMap.CancelOrderBtn}");
                }
            });
        }
        else if (jQuery('input:radio[name=actionId]:checked').val() == "changeOrderQty") 
        {
            jQuery('#statusId').val("ORDER_CANCELLED");
            jQuery('.action').each(function()
            {
                var hrefValue = jQuery(this).attr('href');
                if(hrefValue!= null && hrefValue.startsWith("javascript:submitDetailForm"))
                {
                    jQuery(this).html("${uiLabelMap.SaveBtn}");
                }
            });
        }
        else if (jQuery('input:radio[name=actionId]:checked').val() == "completeOrder") 
        {
            jQuery('#statusId').val("ORDER_COMPLETED");
            jQuery('#orderRefundInfoBox').hide();
            jQuery('.action').each(function()
            {
                var hrefValue = jQuery(this).attr('href');
                if(hrefValue!= null && hrefValue.startsWith("javascript:submitDetailForm"))
                {
                    jQuery(this).html("${uiLabelMap.SaveBtn}");
                }
            });
        }
        else if (jQuery('input:radio[name=actionId]:checked').val() == "productReturn") 
        {
            jQuery('#statusId').val("PRODUCT_RETURN");
            jQuery('.action').each(function()
            {
                var hrefValue = jQuery(this).attr('href');
                if(hrefValue!= null && hrefValue.startsWith("javascript:submitDetailForm"))
                {
                    jQuery(this).html("${uiLabelMap.ProcessReturnsBtn}");
                }
            });
            getOrderRefundData();
        } 
    }
    
    function quickShipOrder(formName)
    {
        jQuery("#actionIdComplete").attr('checked', 'checked');
        getOrderStatusChangeDisplay("actionId");
        jQuery("#orderItemAndShipGroupSeqIdall").attr('checked', 'checked');
        setCheckboxes(formName,'orderItemAndShipGroupSeqId');
        
    }
    function quickCancelOrder(formName)
    {
        jQuery("#actionIdCancel").attr('checked', 'checked');
        getOrderStatusChangeDisplay("actionId");
        jQuery("#orderItemSeqIdall").attr('checked', 'checked');
        setCheckboxes(formName,'orderItemSeqId');
    }
    function quickReturnOrder(formName)
    {
        jQuery("#actionIdReturn").attr('checked', 'checked');
        getOrderStatusChangeDisplay("actionId");
        jQuery("#orderItemSeqIdall").attr('checked', 'checked');
        var rowIndex = 0;
        jQuery('.shipQuantity').each(function()
        {
            jQuery('#returnQuantity_'+rowIndex).val(jQuery('#shipQuantity_'+rowIndex).val());
            rowIndex = rowIndex + 1;
        });
        
        setCheckboxes(formName,'orderItemSeqId');
    }
    
    function showPostalAddress(contactMechId,divType) {
        jQuery('.'+divType).hide();
        jQuery('.'+divType+' :input').attr('disabled', 'disabled');
        jQuery('#'+contactMechId).show();
        jQuery('#'+contactMechId+' :input').removeAttr('disabled');
        var contactMechIdSplit = contactMechId.split("_");
        var shippingContactMechId = contactMechIdSplit[1];
        updateShippingOption(shippingContactMechId);
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
    	jQuery("#lookupCloseButton").hide();
        jQuery(dialog).hide();
        jQuery(displayDialog).fadeOut(300);
    }
    
   function updateStatusBtn(ActiveLabel,InActiveLabel,FormName,spanDescId,btnIdField) {
	    <#-- Set form value -->
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
        <#-- This would be clearer with camelCase variable names -->
        var allCheckbox = document.forms[formName].elements[checkBoxName + "all"];
        var statusId = jQuery("#statusId").val();
        for(i = 0;i < document.forms[formName].elements.length;i++) 
        {
            var elem = document.forms[formName].elements[i];
            if (elem.id.indexOf(checkBoxName) == 0 && elem.type == "checkbox" && allCheckbox.type == "checkbox") 
            {
                elem.checked = allCheckbox.checked;
                if(statusId == "ORDER_CANCELLED" || statusId == "PRODUCT_RETURN")
                {
                    getOrderRefundData();
                }
            }
        }
        
        if(statusId == "ORDER_COMPLETED")
        {
	        jQuery('.toShipQuantity').each(function()
	        {
	        	var toShipQuantityId = jQuery(this).attr("id");
			    if(toShipQuantityId != "")
			    {
			  	  var toShipQuantityIdArray = toShipQuantityId.split("_");
			  	  var toShipQuantityIndex = toShipQuantityIdArray[1];
			  	  if(jQuery("#orderedQuantity_" + toShipQuantityIndex).length)
			  	  {
		  	  			var toShipQuantityVal = jQuery(this).val();
		  	  			if(toShipQuantityVal == "")
		  	  			{
		  	  				var orderQtyVal = jQuery("#orderedQuantity_" + toShipQuantityIndex).val();
		  	  				jQuery(this).val(orderQtyVal)
		  	  			}
			  	  }
			    }
	        });
        }
    }

    function moveCategory(parentCategoryId, parentCategoryName, catIdField, catNameField) {
        document.getElementById(catIdField).value = parentCategoryId;
        document.getElementById(catNameField).innerHTML = parentCategoryName;
        hideDialog('#dialog', '#displayDialog');
    }

    
    function showTooltip(event, text)
    {
        if(!event) event = window.event;
        if(event.currentTarget) 
        {
            elm = event.currentTarget;
        }
        else if(event.srcElement)
        {
            var srcElement = event.srcElement;
            elm = jQuery(srcElement).parent();
        }
        
        var tooltipBox = document.getElementById('tooltip');
	    var obj2 = document.getElementById('tooltipText');
	    obj2.innerHTML = text;
	    tooltipBox.style.display = 'block';
	    
	    <#-- get the ScrollTop and ScrollLeft to add in top and left postion of tooltip. -->
	    var st = Math.max(document.body.scrollTop,document.documentElement.scrollTop);
	    var sl = Math.max(document.body.scrollLeft,document.documentElement.scrollLeft);
	    
	    var WW = jQuery(window).width();
	    var WH = jQuery(window).height();
	    
	    var EX = 0;
	    var EY = 0;
	    
	    if (jQuery.browser.msie && jQuery.browser.version == '8.0')
	    {
	        <#-- Not substract the scrollLeft and scrollTop because the IE 8 doesn't include the scroll to the left and top position. -->
	        EX = jQuery(elm).children().offset().left;
	        EY = jQuery(elm).children().offset().top;
	    }
	    else
	    {
	        <#-- subtracting the sl and st from element left and top position because element left and top includes the scroll pixels. -->
	        EX = jQuery(elm).children().offset().left - sl;
	        EY = jQuery(elm).children().offset().top - st;
	    }
	    
	    var TTW = 0;
	    var TTH = 0;
	    TTW = jQuery(tooltipBox).width();
	    TTH = jQuery(tooltipBox).height();
	    
	    var LP = 0;
	    var TP = 0;
	    var EH = jQuery(elm).children().height();
		var EW = jQuery(elm).children().width();
	    
	    var TOP = eval(EY > TTH);
	    var BOTTOM = eval(!(TOP));
	    var LEFT = eval((TTW + EX) > WW);
	    var RIGHT = eval(!(LEFT));
	    
	    /*These TOP, BOTTOM, LEFT and RIGHT are the position of tooltip and our arrow would be opposite from the tootip, 
	      means if tooltip is on TOP then the Arrow would be at bottom of tooltip. 
	      If the tooltip is in LEFT then the Arrow would be in right of the tooltip. */
	    
	    if(BOTTOM && LEFT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('#tooltipTop').addClass("tooltipTopRightArrow");
	    }
	    else if(BOTTOM && RIGHT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('#tooltipTop').addClass("tooltipTopLeftArrow");
	    }
	    else if(TOP && LEFT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('#tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('#tooltipBottom').addClass("tooltipBottomRightArrow");
	    }
	    else if(TOP && RIGHT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('#tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('#tooltipBottom').addClass("tooltipBottomLeftArrow");
	    }
	    
	    <#-- determine the left position and top position to set to the tooltip -->
	    if(LEFT)
	    {
	       <#-- adding Element Width EW so that the tooltip starts (horizontally) from right of the icon. -->
	       LP = EX -TTW + sl + EW; 
	    }
	    else
	    {
	       LP = EX + sl;
	    }
	    
	    if(BOTTOM)
	    {
	        <#-- adding Element Height EH so that the tooltip starts(vertically) from bottom of the icon. -->
	        TP = (EY + st + EH);
	    }
	    else
	    {
	        TP = (EY - TTH + st);
	    }
	    jQuery(tooltipBox).css({ top: TP+'px' });
	    jQuery(tooltipBox).css({ left: LP+'px' });
    }
    
    function showTooltipImage(event, text, imageUrl)
    {
	    if(!event) event = window.event;
        if(event.currentTarget) 
        {
            elm = event.currentTarget;
        }
        else if(event.srcElement)
        {
            var srcElement = event.srcElement;
            elm = jQuery(srcElement).parent();
        }
        
        var tooltipBox = document.getElementById('tooltip');
	    var obj2 = document.getElementById('tooltipText');
	    obj2.innerHTML = "<img src='"+imageUrl+"' class='toolTipImg' id='imgId'/><div class='toolTipImgText'>"+text+"</div>";
	    obj2.style.display = 'none';
	    var img = document.getElementById('imgId');
	    resize(img);
	    
	    obj2.style.display = 'block';
	    tooltipBox.style.display = 'block';
	    
	    <#-- get the ScrollTop and ScrollLeft to add in top and left postion of tooltip. -->
	    var st = Math.max(document.body.scrollTop,document.documentElement.scrollTop);
	    var sl = Math.max(document.body.scrollLeft,document.documentElement.scrollLeft);
	    
	    var WW = jQuery(window).width();
	    var WH = jQuery(window).height();
	    
	    var EX = 0;
	    var EY = 0;
	    if (jQuery.browser.msie && jQuery.browser.version == '8.0')
	    {
	        <#-- Not substract the scrollLeft and scrollTop because the IE 8 doesn't include the scroll to the left and top position. -->
	        EX = jQuery(elm).children().offset().left;
	        EY = jQuery(elm).children().offset().top;
	    }
	    else
	    {
	        <#-- subtracting the sl and st from element left and top position because element left and top includes the scroll pixels. -->
	        EX = jQuery(elm).children().offset().left - sl;
	        EY = jQuery(elm).children().offset().top - st;
	    }
	    
	    var TTW = 0;
	    var TTH = 0;
	    TTW = jQuery(tooltipBox).width();
	    TTH = jQuery(tooltipBox).height();
	    
	    var LP = 0;
	    var TP = 0;
	    var EH = jQuery(elm).children().height();
		var EW = jQuery(elm).children().width();
	    
	    var TOP = eval(EY > TTH);
	    var BOTTOM = eval(!(TOP));
	    var LEFT = eval((TTW + EX) > WW);
	    var RIGHT = eval(!(LEFT));
	    
	    /*These TOP, BOTTOM, LEFT and RIGHT are the position of tooltip and our arrow would be opposite from the tootip, 
	      means if tooltip is on TOP then the Arrow would be at bottom of tooltip. 
	      If the tooltip is in LEFT then the Arrow would be in right of the tooltip. */
	    
	    if(BOTTOM && LEFT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('#tooltipTop').addClass("tooltipTopRightArrow");
	    }
	    else if(BOTTOM && RIGHT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('#tooltipTop').addClass("tooltipTopLeftArrow");
	    }
	    else if(TOP && LEFT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('#tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('#tooltipBottom').addClass("tooltipBottomRightArrow");
	    }
	    else if(TOP && RIGHT)
	    {
	        jQuery('#tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('#tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('#tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('#tooltipBottom').addClass("tooltipBottomLeftArrow");
	    }
	    
	    <#-- determine the left position and top position to set to the tooltip -->
	    if(LEFT)
	    {
	       <#-- adding Element Width EW so that the tooltip starts (horizontally) from right of the icon. -->
	       LP = EX -TTW + sl + EW; 
	    }
	    else
	    {
	       LP = EX + sl;
	    }
	    
	    if(BOTTOM)
	    {
	        <#-- adding Element Height EH so that the tooltip starts(vertically) from bottom of the icon. -->
	        TP = (EY + st + EH);
	    }
	    else
	    {
	        TP = (EY - TTH + st);
	    }
	    jQuery(tooltipBox).css({ top: TP+'px' });
	    jQuery(tooltipBox).css({ left: LP+'px' });
	    
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
	        <#-- create action -->
	        form.action="<@ofbizUrl>${createAction!""}</@ofbizUrl>";
	        form.submit();
	    }else if (mode == "ED") {
	        <#-- update action -->
	        form.action="<@ofbizUrl>${updateAction!""}</@ofbizUrl>";
	        form.submit();
	    }else if (mode == "DE") {
	        <#-- update action -->
	        form.action="<@ofbizUrl>${deleteAction!""}</@ofbizUrl>";
	        form.submit();
        }else if (mode == "EX") {
            <#-- execute action -->
            form.action="<@ofbizUrl>${execAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "CEX") {
            <#-- execute action -->
            form.action="<@ofbizUrl>${conditionedExecAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "EXC") {
            <#-- execute cache action -->
            form.action="<@ofbizUrl>${execCacheAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "CO") {
            <#-- common action -->
            form.action="<@ofbizUrl>${commonAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "GP") {
            <#-- get Geo Point action -->
            form.action="<@ofbizUrl>${getGeoCodeAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "RW") {
            <#-- replace with action -->
            form.action="<@ofbizUrl>${replaceWithAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "MA") {
            <#-- make active action -->
            form.action="<@ofbizUrl>${makeActiveAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "CF") {
	        <#-- confirm action -->
	        displayDialogBox()
        }else if (mode == "PC") {
            form.action="<@ofbizUrl>${previewAction!""}</@ofbizUrl>";
            form.setAttribute("target", "_new");
            form.submit();
            form.setAttribute("target", "");
	    }else if (mode == "MT") {
	    	<#-- go to meta tag page -->
            form.action="<@ofbizUrl>${metaAction!""}</@ofbizUrl>";
            form.submit();
	    }else if (mode == "SF") {
            <#-- execute action -->
            form.action="<@ofbizUrl>${submitFormAction!""}</@ofbizUrl>";
            form.submit();
        }else if (mode == "UC") {
            <#-- update cart action -->
            var modifyCart = "adminModifyCart"; 
            if (updateCart()) {
                form.action="<@ofbizUrl>" + modifyCart + "</@ofbizUrl>";
                form.submit();
            }
        }else if (mode == "UCPS") {
            <#-- update cart pickup in store -->
            var setStorePickup = "setStorePickup";
            form.action="<@ofbizUrl>" + setStorePickup + "</@ofbizUrl>";
            form.submit();
        }else if (mode == "EXA") {
            <#-- execute through AJAX action -->
            executeAjaxRequest();
        }
	}
    
    function executeAjaxRequest()
    {
        var flag = true;
        jQuery('#pageContainer').append("<div class=loadingAjaxImg></div>");
        jQuery.post("<@ofbizUrl>${execAjaxAction!""}</@ofbizUrl>", jQuery('input:hidden').serialize(), function(data)
        {
            var pageContainer = jQuery(data).find('#pageContainer');
            jQuery('#pageContainer').replaceWith(pageContainer);
            flag = false;
        });

        (function keepBrowserAlive()
        {
           setTimeout(function() 
           {
               if (flag) 
               {
                   keepBrowserAlive();
               }
           }, 5000);
        })();
    }
	
	function refreshFromBottomCart(){
		<#-- set the values from bottom cart to the top cart -->
		jQuery('.BOTTOM_CART_ITEM').each(function () {
        		var newQty = jQuery(this).val(); 
        		var bottomCartInput = jQuery(this).attr("name");
        		var bottomLineNo = bottomCartInput.split('_')[1];
        		
        		jQuery('#update_'+bottomLineNo).val(newQty);
     	});
     	<#-- refresh the top cart -->
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
	
    function updateCart() 
    {
      <#if shoppingCart?has_content>
        <#assign shoppingCartItemSize = shoppingCart.items().size()! />
      </#if>
      var cartItemsNo = ${shoppingCartItemSize!"0"};
      <#assign PDP_QTY_MIN = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MIN")!"1"/>
      <#assign PDP_QTY_MAX = Static["com.osafe.util.OsafeAdminUtil"].getProductStoreParm(request,"PDP_QTY_MAX")!"99"/> 
      var lowerLimit = ${PDP_QTY_MIN!"1"};
      var upperLimit = ${PDP_QTY_MAX!"99"};
      var zeroQty = false;
      
      for (var i=0;i<cartItemsNo;i++)
      {
          var qtyInCartAttrName = jQuery('#qtyInCart_'+i).attr("name");
          var quantity = Number(0);
          jQuery('.'+qtyInCartAttrName).each(function () 
    	  {
    	      quantity = quantity + Number(jQuery(this).val());
    	  });
      
          //var quantity = jQuery('#update_'+i).val();
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
	    else
	    {
	    	var newFolder = jQuery('#newFolderName').val();
	    	var fieldId = "${parameters.mediaType!'newFolder'}";
	    	if(newFolder != "")
	    	{
		    	if(fieldId=="newFolder")
	          	{
	          		setUploadUrlAndNewFolder(fieldId,newFolder);
	          	}
          	}
	    }
	    
        form.submit();
	}
	
	function setUploadUrlAndNewFolder(fieldId,newFolder) 
	{
	  var form = document.${detailFormName!"detailForm"};
	  var fieldValue = document.getElementById(fieldId).value;
	  form.action="<@ofbizUrl>${uploadAction!}?${uploadParmName!}=" +fieldValue+ "&newFolderName=" +newFolder+ "</@ofbizUrl>";
	}
	
	function disableNewFolderName(fieldId) 
	{
	  if(fieldId != "newFolder")
	  {
	  	jQuery('#newFolderName').attr("disabled", "disabled");
	  }
	  else
	  {
	  	jQuery('#newFolderName').removeAttr("disabled")
	  }
	}
	
	function setUploadUrl(fieldId) 
	{
	  var form = document.${detailFormName!"detailForm"};
	  var fieldValue = document.getElementById(fieldId).value;
	  form.action="<@ofbizUrl>${uploadAction!}?${uploadParmName!}=" +fieldValue+ "</@ofbizUrl>";
	}
	
	function postConfirmDialog() 
	{
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
     		jQuery('.confirmTxt').html(confirmDialogText);
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
        jQuery(newRow).find('select').removeAttr('disabled');
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
            var selects = rows[i].getElementsByTagName('select');
            for (k = 0; k < selects.length; k++) {
                attrId = selects[k].getAttribute("id");
                selects[k].setAttribute("name",attrId+"_"+i)
            }
            var anchors = rows[i].getElementsByTagName('a');
            if(anchors.length >= 3)  {  
                    var index = 0
                    if(anchors.length > 3) {
                        index = 1
                    }
                    var deleteAnchor = anchors[index++];
                    if (jQuery(deleteAnchor).hasClass('normalAnchor'))
                    {
                    } else
                    {
                        var deleteTagSecondMethodIndex = deleteAnchor.getAttribute("href").indexOf(";");
                        var deleteTagSecondMethod = deleteAnchor.getAttribute("href").substring(deleteTagSecondMethodIndex+1,deleteAnchor.getAttribute("href").length);
                        deleteAnchor.setAttribute("href", "javascript:setNewRowNo('"+i+"');"+deleteTagSecondMethod);
                    }
                    
                    var insertBeforeAnchor = anchors[index++];
                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
                    var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
                    insertBeforeAnchor.setAttribute("href", "javascript:setNewRowNo('"+i+"');"+insertBeforeTagSecondMethod);
                    
                    var insertAfterAnchor = anchors[index++];
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

        <#-- create selected operator name div -->
        var columnDiv = new Element('DIV');
        columnDiv.setAttribute("class", "dataColumn operDataColumn");
        var selectObj = document.getElementById(processObject.processTypeSelectId);
        var textNode = document.createTextNode(selectObj.options[selectObj.selectedIndex].text);
        columnDiv.appendChild(textNode);
        rowDiv.appendChild(columnDiv);

        <#-- create selected category/product name div -->
        columnDiv = new Element('DIV');
        columnDiv.setAttribute("class", "dataColumn nameDataColumn");
        textNode = document.createTextNode(document.getElementById(processObject.processTypeHiddenId2).value);
        columnDiv.appendChild(textNode);
        rowDiv.appendChild(columnDiv);

        <#-- create sremove button div -->
        columnDiv = new Element('DIV');
        columnDiv.setAttribute("class", "dataColumn actionDataColumn");
        <#-- create remove button -->
        var buttonAnchor = document.createElement("A");
        buttonAnchor.setAttribute("class", "standardBtn secondary");
        buttonAnchor.setAttribute("href", "javascript:deleteDivRow('"+processObject.divId+"', '"+processObject.dataRows+"', "+indexPos+")");
        buttonAnchor.appendChild(document.createTextNode('${uiLabelMap.RemoveBtn}'));
        columnDiv.appendChild(buttonAnchor);
        <#-- create selected operator hidden field -->
        var element = document.createElement("input");
        element.setAttribute("type", "hidden");
        element.setAttribute("value", document.getElementById(processObject.processTypeSelectId).value);
        element.setAttribute("id", processObject.newTypeHiddenNamePrefix1+indexPos)
        element.setAttribute("name", processObject.newTypeHiddenNamePrefix1+indexPos)
        columnDiv.appendChild(element);
        <#-- create selected category/product id hidden field -->
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
    function setStyleName(styleFileName,inputField, detailFormName) 
    {
        jQuery('#'+inputField).val(styleFileName);
        if(detailFormName.length)
        {
            submitDetailForm(document.forms[detailFormName], 'MA');
        }
    }
    function setNewValue(key,value) {
        document.getElementById('newValue').value = value;
        document.getElementById('key').value = key;
        
        <#if detailFormName?has_content>
          document.${detailFormName!""}.submit();
          //submitDetailForm(document.${detailFormName!""}, 'MA');
        </#if>
    }
    function setStars(starValue, divId, inputName) 
    {
       <#-- Change stars image -->
       var ratingPerct = ((starValue / 5) * 100);
        $(divId).style.width = ratingPerct+ '%';

        <#-- Set new stars value in form -->
        var form = document.reviewFORM;
        form.elements[inputName].value=starValue;
    }
    function updateReview(status) 
    {
        <#-- Set form value -->
        var form = document.${detailFormName!"reviewForm"};
        form.elements['statusId'].value=status;
        <#-- Chnage display value -->
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
  
<#--
  begin JQuery for scheduledJobRule 
  handle the display of the helper text for the Unit of the frequency interval 
  when page is displayed, this will run		
-->
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
	<#-- when values are changed, run this: -->
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
});
<#-- end of JQuery for scheduledJobsRule -->

function deleteCategoryMemberRow(categoryName, parentCategoryName)
{
    jQuery('#confirmDeleteTxt').html('${confirmDialogText!""} '+parentCategoryName+'/'+categoryName+'?');
    displayDialogBox();
}

function deleteOrganizationCustomerRow(partyId, customerName)
{
    jQuery('#confirmDeleteTxt').html('${confirmDialogText!""} '+customerName+'?');
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

function removeOrganizationCustomerRow(tableId){
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

function addOrganizationCustomerRow(tableId) 
{
    var table = document.getElementById(tableId);
    var rows = table.getElementsByTagName('tr');
    var indexPos = jQuery('#rowNo').val();
    var row = table.insertRow(indexPos);
    
    organizationCustomerPartyId =  jQuery('#organizationCustomerPartyId').val();
    organizationCustomerPartyTypeId = jQuery('#organizationCustomerPartyTypeId').val(); 
    
    jQuery.get('<@ofbizUrl>addOrganizationCustomerRow?organizationCustomerPartyId='+organizationCustomerPartyId+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
        jQuery(row).replaceWith(data);
        setTableIndexPos(table);
    });
}

<#-- jQuery for userSecurityGroup -->

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
<#-- end of jQuery for userSecurityGroup -->

<#-- jQuery for securityGroupPermission  -->
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
<#-- end of jQuery for securityGroupPermission -->
function setTableIndexPos(table)
{
    var rows = table.getElementsByTagName('tr');
    for (i = 1; i < rows.length; i++) 
    {
        var columns = rows[i].getElementsByTagName('td');
        for (j = 0; j < columns.length; j++) 
        {
            if(j == (columns.length-1)) 
            {
                var anchors = columns[j].getElementsByTagName('a');
                if(anchors.length > 1) 
                {
                    var deleteAnchor = anchors[0];
                    var deleteTagSecondMethodIndex = deleteAnchor.getAttribute("href").indexOf(";");
                    var deleteTagSecondMethod = deleteAnchor.getAttribute("href").substring(deleteTagSecondMethodIndex+1,deleteAnchor.getAttribute("href").length);
                    deleteAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+deleteTagSecondMethod);
                    
                    if(jQuery(anchors[1]).length)
                    {
                        var insertBeforeAnchor = anchors[1];
	                    var insertBeforeTagSecondMethodIndex = insertBeforeAnchor.getAttribute("href").indexOf(";");
	                    if(insertBeforeTagSecondMethodIndex != -1)
	                    {
	                        var insertBeforeTagSecondMethod = insertBeforeAnchor.getAttribute("href").substring(insertBeforeTagSecondMethodIndex+1,insertBeforeAnchor.getAttribute("href").length);
	                    	insertBeforeAnchor.setAttribute("href", "javascript:setRowNo('"+i+"');"+insertBeforeTagSecondMethod);
	                    }
                    }
                    if(jQuery(anchors[2]).length)
                    {
                        var insertAfterAnchor = anchors[2];
	                    var insertAfterTagSecondMethodIndex = insertAfterAnchor.getAttribute("href").indexOf(";");
	                    if(insertAfterTagSecondMethodIndex != -1)
	                    {
	                        var insertAfterTagSecondMethod = insertAfterAnchor.getAttribute("href").substring(insertAfterTagSecondMethodIndex+1,insertAfterAnchor.getAttribute("href").length);
	                    	insertAfterAnchor.setAttribute("href", "javascript:setRowNo('"+(i+1)+"');"+insertAfterTagSecondMethod);
	                    }
                    }
                }
                    
                if(anchors.length == 1) 
                {
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
    if(jQuery('#addIconRow').length)
    {
        $('totalRows').value = rows.length-2;
    }
    else
    {
        $('totalRows').value = rows.length-1;
    }
    //Set Rowno on Add Button
    if(jQuery('.addCustomerRow').length)
    {
        var addBtnAnchor = jQuery('.addCustomerRow').find('a');
	    var insertBeforeTagSecondMethodIndex = jQuery(addBtnAnchor).attr("href").indexOf(";");
	    var insertBeforeTagSecondMethod = jQuery(addBtnAnchor).attr("href").substring(insertBeforeTagSecondMethodIndex+1,jQuery(addBtnAnchor).attr("href").length);
	    jQuery(addBtnAnchor).attr("href", "javascript:setRowNo('"+rows.length+"');"+insertBeforeTagSecondMethod)
    }
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
 
    <#-- update the shopping cart sections and update the promotion section -->
    function setShippingMethod(selectedShippingOption, isOnLoad) 
    {
        if (jQuery('#shoppingCartContainer').length) 
        {
        	jQuery.ajaxSetup({async:false});
            jQuery('#shoppingCartContainer').load('<@ofbizUrl>setShippingOption?shipMethod='+selectedShippingOption+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>',  function()
            {
	            if (jQuery('#shoppingCartBottomContainer').length) 
	            {
		            jQuery('#shoppingCartBottomContainer').load('<@ofbizUrl>setShippingOptionBottom?shipMethod='+selectedShippingOption+'&rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function()
		            {
		            });
		        }
            });
            
            if((isOnLoad != null) && (isOnLoad =='N')) 
	        {
	        	if (jQuery('#loyaltyPointsContainer').length) 
	             {
	                jQuery.get('<@ofbizUrl>adminReloadLoyaltyPoints?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(lpData)
	             	{
	             		jQuery('#loyaltyPointsContainer').replaceWith(lpData);	
		            });
	             }
	            jQuery.get('<@ofbizUrl>reloadPromoCode?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(promoData)
	         	{
	         		jQuery('#promoCodeContainer').replaceWith(promoData);	
	            });
	        }
	        
	        <#-- capture warning message since it will dissapear when gift card section is reloaded -->
	        var gcWarningMessTest;
	        if((isOnLoad == null) || (isOnLoad !='N')) 
	        {
	        	gcWarningMessText = jQuery("#js_gcWarningMessText");
	        }
	        if (jQuery('#giftCardContainer').length) 
		    {
		     	jQuery.get('<@ofbizUrl>adminReloadGiftCard?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(gcData)
		     	{
		     		jQuery('#giftCardContainer').find('#enteredGiftCardContainter').replaceWith(jQuery(gcData).find('#enteredGiftCardContainter'));	
		     		if((isOnLoad == null) || (isOnLoad !='N')) 
					{
						if(jQuery(gcWarningMessText).length)
						{
							<#-- re-apply error message -->
							jQuery(".giftCardSummary").append(gcWarningMessTest);
						}
					}
		        });
		    }
            
            
            jQuery.get('<@ofbizUrl>reloadBalance?rnd=' + String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) 
            {
               var balanceSection = jQuery(data).find("#balanceDue");
               jQuery("#balanceDue").replaceWith(balanceSection);
               
               var remainingBalance = Number(jQuery("#remainingPayment").val());
               if(Number(remainingBalance) > 0)
               {
               		jQuery("#paymentOptionInfo").show();
               }
               else
               {
               		jQuery("#paymentOptionInfo").hide();
               }
               
            });
        }
        
        if(selectedShippingOption != "NO_SHIPPING@_NA_")
        {
        	jQuery('#checkoutStoreName').hide();
        }
    }
    
    <#-- add promo code for checkout screen -->
    function addManualPromoCode() {
        if (jQuery('#manualOfferCode').length && jQuery('#manualOfferCode').val() != null) {
          promo = jQuery('#manualOfferCode').val().toUpperCase();
          promoCodeWithoutSpace = promo.replace(/^\s+|\s+$/g, "");
        }
        var cform = document.${detailFormName!"adminCheckoutFORM"};
        cform.action="<@ofbizUrl>adminValidatePromoCode?productPromoCodeId="+promoCodeWithoutSpace+"</@ofbizUrl>";
        cform.submit();
    }
    
    <#-- remove promo code for checkout screen -->
    function removePromoCode(promoCode) {
        if (promoCode != null) {
          var cform = document.${detailFormName!"adminCheckoutFORM"};
          cform.action="<@ofbizUrl>adminRemovePromoCode?productPromoCodeId="+promoCode+"</@ofbizUrl>";
          cform.submit();
        }
    }
    
    <#-- add promo code for checkout screen -->
    function addGiftCardNumber()
    {
        if (jQuery('#giftCardNumber').length && jQuery('#giftCardNumber').val() != null)
        {
          giftCardNumber = jQuery('#giftCardNumber').val();
          giftCardNumberWithoutSpace = giftCardNumber.replace(/^\s+|\s+$/g, "");
        }
        var cform = document.${detailFormName!"adminCheckoutFORM"};
        cform.action="<@ofbizUrl>${addGiftCardNumberRequest!}?gcNumber="+giftCardNumberWithoutSpace+"</@ofbizUrl>";
        cform.submit();
    }
    
    <#-- remove Gift Card for checkout screen -->
    function removeGiftCardNumber(gcPaymentMethodId)
    {
        if (gcPaymentMethodId != null)
        {
          var cform = document.${detailFormName!"adminCheckoutFORM"};
          cform.action="<@ofbizUrl>${removeGiftCardNumberRequest!}?gcPaymentMethodId="+gcPaymentMethodId+"</@ofbizUrl>";
          cform.submit();
        }
    }
    
    
    
    
    function addLoyaltyPoints()
    {
    	jQuery('#js_applyLoyaltyCard').bind('click', false);
        var cform = document.${formName!"adminCheckoutFORM"};
        cform.action="<@ofbizUrl>${addLoyaltyPointsRequest!}</@ofbizUrl>";
        cform.submit();
    }
    function removeLoyaltyPoints()
    {
    	jQuery('#js_removeLoyaltyCard').bind('click', false);
	    var cform = document.${formName!"adminCheckoutFORM"};
	    cform.action="<@ofbizUrl>${removeLoyaltyPointsRequest!}</@ofbizUrl>";
	    cform.submit();
    }
    function updateLoyaltyPoints(indexOfAdj)
    {
    	jQuery('#js_updateLoyaltyPointsAmount').bind('click', false);
	    var cform = document.${formName!"adminCheckoutFORM"};
	    cform.action="<@ofbizUrl>${updateLoyaltyPointsRequest!}</@ofbizUrl>";
	    cform.submit();
    }
    
    
    
    
    <#-- disabled or enabled the Selectable feature based on the Finished/Virtual -->
    function selectFinishedProduct(elm) 
    {
        if (jQuery(elm).attr('name') == undefined)
        {
            return;
        }
        if(jQuery(elm+':checked').val() == "N"){
            jQuery('.selectableRadio').hide();
            jQuery('.multiSelectVaraint').hide();
            jQuery('.buyableProductAttribute').show();
        } else {
            jQuery('.selectableRadio').show();
            jQuery('.multiSelectVaraint').show();
            jQuery('.buyableProductAttribute').hide();
        }
    }
    function setFileEnabledContent(elm)
    {
        if (jQuery(elm).attr('name') == undefined)
        {
            return;
        }
        if(jQuery(elm+':checked').val() == "ELECTRONIC_TEXT"){
            jQuery('.CONTEXT_FILE').hide();
            jQuery('.ELECTRONIC_TEXT').show();
        } else {
            jQuery('.CONTEXT_FILE').show();
            jQuery('.ELECTRONIC_TEXT').hide();
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
	
	<#-- when gift message text is empty and a help text is selected, copy the help text to the message -->
    function giftMessageHelpCopy(count)
    {
    	var helpText = jQuery("#giftMessageEnum_"+count).val();
    	if(helpText != "")
    	{
    		jQuery("#giftMessageText_"+count).val(helpText);
    		restrictTextLength(jQuery("#giftMessageText_"+count));
    	}
    }
    
    <#-- show and hide the group input for the div sequencer -->
    function showHideGroupInput(selected)
    {
    	var selectedValue = selected.options[selected.selectedIndex].value; 
    	if(selectedValue != "PDPTabs")
    	{
    		jQuery('.divSequenceGroup').hide();
    	}
    	else
    	{
    		jQuery('.divSequenceGroup').show();
    	}
    }
    
    function setFeatureDisplay(elm)
    {
        var distinguishProductFeatureMultiId = jQuery(elm).attr('name');
        var featureTypeId = distinguishProductFeatureMultiId.split('_')[1];
        var selectedMultiFeature = jQuery(elm).val() 
        var selectedMultiFeatures = selectedMultiFeature.split(',');
        if(selectedMultiFeatures.length > 1)
        {
            jQuery('#multipleInfo_'+featureTypeId).html("Multiple ...");
            jQuery('#multipleInfo_'+featureTypeId).show();
            jQuery('#distinguishFeatureValue_'+featureTypeId+' option:selected').removeAttr('selected');
            jQuery('#distinguishFeatureValue_'+featureTypeId).children('select:first').hide();
        }
        else
        {
            jQuery('#multipleInfo_'+featureTypeId).hide();
            jQuery("#distinguishFeatureValue_"+featureTypeId+" option[value='"+selectedMultiFeature+"']").attr("selected", "selected");
            jQuery('#distinguishFeatureValue_'+featureTypeId).children('select:first').show();
            jQuery(elm).val("");
        } 
    }
    
    function setVirtualFeatureDisplay(elm)
    {
        var distinguishProductFeatureMultiId = jQuery(elm).attr('name');
        var featureTypeId = distinguishProductFeatureMultiId.split('_')[1];
        var selectedMultiFeature = jQuery(elm).val() 
        var selectedMultiFeatures = selectedMultiFeature.split(',');
        if(selectedMultiFeatures.length > 1)
        {
            jQuery('#multipleInfo_'+featureTypeId).html("Multiple ...");
            jQuery('#multipleInfo_'+featureTypeId).show();
            jQuery('#distinguishFeatureValue_'+featureTypeId+' option:selected').removeAttr('selected');
            jQuery('#distinguishFeatureValue_'+featureTypeId).children('select:first').hide();
        }
        else
        {
            var productFeatureId = selectedMultiFeature.split('@')[0];
            jQuery('#multipleInfo_'+featureTypeId).hide();
            jQuery('#distinguishFeatureValue_'+featureTypeId+' option[value='+productFeatureId+']').attr('selected', 'selected');
            jQuery('#distinguishFeatureValue_'+featureTypeId).children('select:first').show();
            jQuery(elm).val("");
        } 
    }
	
	<#assign shipmentId = parameters.shipmentId!/>
	
        <#if shipmentId?has_content>
            window.open(
            "<@ofbizUrl>ShippingLabel.pdf?shipmentId="+${shipmentId}+"</@ofbizUrl>",'popUpWindow','height=500,width=600,left=400,top=200,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes')
        </#if>
        
    <#assign convertedFileName = parameters.convertedFileName!/>   
    <#if convertedFileName?has_content>
        var win = window.open("<@ofbizUrl>downloadConvertedXmlFile?convertedFileName=${convertedFileName}</@ofbizUrl>",'_blank');
        win.focus();
    </#if>
    
   function updateShippingOption(shippingContactMechId) 
   {
        if (jQuery('.shippingOption').length) 
        {
            if (jQuery('#SHIPPING_SELECT_ADDRESS').length) 
            {
	            if(jQuery('.shippingOption').find('.boxBody').find('#shippingOptionList').length)
	            {
	                jQuery.get('<@ofbizUrl>updateShippingOptions?shippingContactMechId='+shippingContactMechId+'&callback=Y&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) 
	                {
	                    jQuery('.shippingOption').find('.boxBody').find('#shippingOptionList').replaceWith(data);
	                    <#-- also update the cart -->
	                    var selectedShippingOptionValue = "";
	                    var selectedShippingOption = jQuery('input[name="shipping_method"]:radio:checked');
	                    if(jQuery(selectedShippingOption).length)
	                    {
	                    	selectedShippingOptionValue = jQuery(selectedShippingOption).val();
	                    }
	                    else
	                    {
	                    	jQuery('input[name=shipping_method]:eq(0)').attr('checked', 'checked');
	                    	selectedShippingOption = jQuery('input[name="shipping_method"]:radio:checked');
	                    	selectedShippingOptionValue = jQuery(selectedShippingOption).val();
	                    }
	                    setShippingMethod(selectedShippingOptionValue, 'N')
                	});
	            }
            } 
        }
    }
    
    jQuery(document).ready(function () 
    {
	    if (jQuery('.shippingOption').length) 
	    {
	        if (jQuery('#SHIPPING_SELECT_ADDRESS').length) 
	        {
	        	var shippingContactMechId = jQuery('input[name="SHIPPING_SELECT_ADDRESS"]:radio:checked').val();
	        	if(shippingContactMechId != "")
	        	{
	        		updateShippingOption(shippingContactMechId);
	        	}
	        }
	    }
    });
    
    jQuery(document).ready(function () 
    {
    	<#if mode?exists && mode?has_content && mode=="add">
    	   if(jQuery('#divSequenceKey').length)
           {
    		var newKeyText = jQuery('#divSequenceKey').val();
	    	jQuery('input[name="key"]').val(newKeyText);
			jQuery('#divSequenceKey').change(function () 
	    	{ 
	    		var newKeyText = jQuery('#divSequenceKey').val();
	    		jQuery('input[name="key"]').val(newKeyText);
	    		
	    	});
	       }
    	</#if>
    	
    	
    	
    	if(jQuery('#isSameAsBilling').length)
	  	{
	  	  	if(jQuery('#isSameAsBilling').is(":checked"))
	  	  	{
	  	  		jQuery('#shipping_addressSection').hide();
	  	  	}
	  	}
	  	  
		jQuery('#isSameAsBilling').click(function()
		{
		    if(jQuery('#isSameAsBilling').is(":checked"))
		    {  
		    	jQuery('#shipping_addressSection').hide();
		    }
		    else
		    {
		    	jQuery('#shipping_addressSection').show();
		    }
		});
		
    });
    
    function addOrderAdjustment()
    {
        var cform = document.${detailFormName!"adminCheckoutFORM"};
        cform.action="<@ofbizUrl>${addOrderAdjustmentRequest!}</@ofbizUrl>";
        cform.submit();
    }
    function removeOrderAdjustment(orderAdjustmentId) 
    {
        if (orderAdjustmentId != "") 
        {
          var cform = document.${detailFormName!"adminCheckoutFORM"};
          cform.action="<@ofbizUrl>adminRemoveOrderAdjustment?orderAdjustmentId="+orderAdjustmentId+"</@ofbizUrl>";
          cform.submit();
        }
    }

    function setScheduledJobDeleteParams(isOld, serviceName, statusId)
    {
        document.getElementById('isOld').value = isOld;
        document.getElementById('serviceName').value= serviceName;
        document.getElementById('statusId').value = statusId;
    }

    function validateInput(element, inputType, warningMessage)
    {
        // First remove the warning message if exist on page.
        if(jQuery(element).parent().find('.inputWarning').length)
        {
            jQuery(element).parent().find('.inputWarning').remove();
        }

        var showWarning = false;
        var today = new Date();

        if (inputType == '+DATE')
        {
            //show warning for future Date
            var inputDate = jQuery(element).datepicker( 'getDate' );
            if (inputDate > today)
            {
                showWarning = true;
            }
        }
        else if (inputType == '-DATE')
        {
            //show warning for past Date
            var inputDate = jQuery(element).datepicker( 'getDate' );
            if (inputDate < today)
            {
                showWarning = true;
            }
        }

        if (showWarning)
        {
            jQuery(element).parent().append("<div class='inputWarning'>"+warningMessage+"</div>");
        }
    }

    function showLookupIconPageLoad()
    {
        jQuery('.priceRuleInputParamEnumId').each(function(){
            showLookupIcon(this);
        });
    }

    function showLookupIcon(elm)
    {
        var value = jQuery(elm).val();
        var hypenIndex = jQuery(elm).attr('name').indexOf("_");
        var rowNo = jQuery(elm).attr('name').substring(hypenIndex+1);

        if (isNotEmpty(rowNo)) 
        {
            var condValueElement = jQuery(elm).parent().parent().find('#condValue');
            var appendTdElement = condValueElement.parent();
    
            var previewIconAnchor = document.createElement("A");
            var previewIconSpan = new Element('SPAN');
            previewIconSpan.setAttribute("class", "previewIcon");
            previewIconAnchor.appendChild(previewIconSpan)

            // First remove the lookup icon if exist on page.
            if(appendTdElement.find('a').length)
            {
                appendTdElement.find('a').remove();
            }
    
            if (value == "PRIP_PRODUCT_ID") 
            {
                // add product look up
                previewIconAnchor.setAttribute("href", "javascript:openLookup(document.${detailFormName!}.condValue_"+rowNo+", document.${detailFormName!}.productName, 'lookupProduct','500','700','center','true')");
                appendTdElement.append(previewIconAnchor);
            }
            else if (value == "PRIP_PROD_CAT_ID") 
            {
                // add category look up
                previewIconAnchor.setAttribute("href", "javascript:openLookup(document.${detailFormName!}.condValue_"+rowNo+", document.${detailFormName!}.categoryName, 'lookupCategory','500','700','center','true')");
                appendTdElement.append(previewIconAnchor);
            }
            else if (value == "PRIP_PARTY_ID") 
            {
                // add customer look up
                previewIconAnchor.setAttribute("href", "javascript:openLookup(document.${detailFormName!}.condValue_"+rowNo+", document.${detailFormName!}.customerName, 'lookupCustomer','500','700','center','true')");
                appendTdElement.append(previewIconAnchor);
            }
            else if (value == "PRIP_PROD_FEAT_ID") 
            {
                // add product feature look up
                previewIconAnchor.setAttribute("href", "javascript:openLookup(document.${detailFormName!}.condValue_"+rowNo+", document.${detailFormName!}.featureDescription, 'lookupProductFeature','500','700','center','true')");
                appendTdElement.append(previewIconAnchor);
            }
            else if (value == "PRIP_PARTY_CLASS") 
            {
                // add Party Classification Group look up
                previewIconAnchor.setAttribute("href", "javascript:openLookup(document.${detailFormName!}.condValue_"+rowNo+", document.${detailFormName!}.partyClassificationGroupDescription, 'lookupPartyClassificationGroup','500','700','center','true')");
                appendTdElement.append(previewIconAnchor);
            }
        }
    }

    function isNotEmpty(value) 
    {
        return !isEmpty(value);
    }

    function isEmpty(value) 
    {
        valueWithoutSpace = value.replace(/^\s+|\s+$/g, "");
        if (valueWithoutSpace == null || valueWithoutSpace == "") 
        {
            return true;
        }
        else
        {
            return false;
        }
    }
</script>

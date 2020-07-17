
<script type="text/javascript">
    jQuery(document).ready(function () {
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
        getOrderItemCheckDisplay('changeStatusAll');
        jQuery('input:radio[name=changeStatusAll]').click(function(event) {
            getOrderItemCheckDisplay('changeStatusAll');
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

       jQuery('input:checkbox.homeSpotCheck').change(function(){
           if (jQuery('input:checkbox[name=contentId]:checked').length) {
               jQuery('#previewHomeSpot').attr("target","_new");
               var url = jQuery('#previewHomeSpotAction').val()+"?"+jQuery('input:checkbox[name=contentId]:checked').serialize();
               jQuery('#previewHomeSpot').attr("href",url);
           } else {
               jQuery('#previewHomeSpot').attr("href",jQuery('#previewHomeSpotAction').val());
               jQuery('#previewHomeSpot').attr("target","");
           }
       });
       if (jQuery('#statusId').length){
           getOrderStatusChangeDisplay('#statusId');
           jQuery('#statusId').change(function(){
               getOrderStatusChangeDisplay('#statusId');
           });
       }
       <#if review?has_content>
           updateReview("${parameters.statusId!review.statusId}");
           setStars("${parameters.productRating!review.productRating}");
       </#if>
    });
    
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
    
    function getOrderItemCheckDisplay(changeStatusAll) {
        if (jQuery('input:radio[name=changeStatusAll]:checked').val() == "Y") {
            jQuery('input:checkbox[name^=orderItemSeqId]:checked').removeAttr('checked');
            jQuery('.selectOrderItem').hide();
        } else if (jQuery('input:radio[name=changeStatusAll]:checked').val() == "N") {
            jQuery('.selectOrderItem').show();
        } 
    }
    function getOrderStatusChangeDisplay(statusId) {
        if (jQuery(statusId).val() == "ORDER_COMPLETED") {
            jQuery('.COMPLETED').show();
        } else {
            jQuery('.COMPLETED').hide();
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
	    tooltipBox.style.top = e.clientY - tooltipBox.offsetHeight -1 + st + 'px';
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
	    if(leftPos<0)leftPos = 0;
	    tooltipBox.style.left = leftPos + 'px';
	    tooltipBox.style.top = e.clientY - tooltipBox.offsetHeight -1 + st + 'px';
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
	    }
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
	    form.action="<@ofbizUrl>${confirmAction!"confirmAction"}</@ofbizUrl>";
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
    
    function postConfirmDialogAction(action) {
	    form = document.${detailFormName!"detailForm"};
	    form.action="<@ofbizUrl>" + action + "</@ofbizUrl>";
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
        var rows = table.getElementsByTagName('tr');
        for (i = 1; i < rows.length; i++) {
            var inputs = rows[i].getElementsByTagName('input');
            for (j = 0; j < inputs.length; j++) {
                attrType = inputs[j].getAttribute("type");
                attrId = inputs[j].getAttribute("id");
                inputs[j].setAttribute("name",attrId+"_"+i)
            }
            var anchors = rows[i].getElementsByTagName('a');
            if(anchors.length == 3)  {  
                    var deleteAnchor = anchors[0];
                    var deleteTagSecondMethodIndex = deleteAnchor.getAttribute("href").indexOf(";");
                    var deleteTagSecondMethod = deleteAnchor.getAttribute("href").substring(deleteTagSecondMethodIndex+1,deleteAnchor.getAttribute("href").length);
                    deleteAnchor.setAttribute("href", "javascript:setNewRowNo('"+i+"');"+deleteTagSecondMethod);
                    
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
        if(rows.length > 2) {
            jQuery('#addIconRow').hide();
        } else {
            jQuery('#addIconRow').show();
        }
        $('totalRows').value = rows.length-2;
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

    function getAddressFormat(countryId) {
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

    function getAssociatedStateList(countryId, stateId, divStateId, addressLine3) {
        var optionList = "";
        var requestToSend = "getAssociatedStateList";
        new Ajax.Request(requestToSend, {
            asynchronous: false,
            parameters: {countryGeoId:$F(countryId)},
            onSuccess: function(transport) {
                var data = transport.responseText.evalJSON(true);
                stateList = data.stateList;
                stateList.each(function(state) {
                    if (state.geoId) {
                        optionList = optionList + "<option value = "+state.geoId+" >"+state.geoName+"</option>";
                    } else {
                        optionList = optionList + "<option value = >"+state.geoName+"</option>";
                    }
                });
                $(stateId).update(optionList);
                if (stateList.size() <= 1) {
                    if ($(divStateId).visible()) {
                        Effect.Fade(divStateId, {duration: 0.0});
                        Event.stopObserving(stateId, 'blur');
                        //Show the Address 3 Field if States are not found.
                        Effect.Appear(addressLine3, {duration: 0.0});
                    }
                } else {
                    //Hide the Address 3 Field if States are found.
                    Effect.Fade(addressLine3, {duration: 0.0});
                    Effect.Appear(divStateId, {duration: 0.0});
                }
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

    
    function showImageField(fieldDivId) {
        jQuery('.commonDivHide').hide();
        if(fieldDivId != '') {
            jQuery('#'+fieldDivId).show();
        }
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
    function setStyleName(styleFileName) {
        document.getElementById("styleFileName").value = styleFileName;
        <#if detailFormName?has_content>
          submitDetailForm(document.${detailFormName!""}, 'MA');
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
            jQuery('#reviewStatus').html("${uiLabelMap.RejectedLabel}");
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
			if(servFreq == "0")
			{//not set	
					jQuery("#SERVICE_INTERVAL").prop('disabled', true);	
					jQuery("#SERVICE_INTERVAL").val('');
					jQuery("#SERVICE_COUNT").prop('disabled', true);	
					jQuery("#SERVICE_COUNT").val('');
			}	
			if(servInter != "")
			{
				if(servFreq == "0")
				{//not set					
				}
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
			if(servFreq == "0")
			{//not set
					jQuery("#SERVICE_INTERVAL").prop('disabled', true);	
					jQuery("#SERVICE_INTERVAL").val('');
					jQuery("#SERVICE_COUNT").prop('disabled', true);	
					jQuery("#SERVICE_COUNT").val('');
			}	
			if(servFreq != "0")
			{//not set
					jQuery("#SERVICE_INTERVAL").prop('disabled', false);	
					jQuery("#SERVICE_COUNT").prop('disabled', false);
			}	
			if(servInter != "")
			{
				if(servFreq == "0")
				{//not set					
				}
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
</script>

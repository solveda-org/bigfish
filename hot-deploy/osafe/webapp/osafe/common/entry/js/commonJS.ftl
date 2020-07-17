<script type="text/javascript">
	
	function submitCommonForm(form, mode, value) 
    {
        if (mode == "DN") {
            <#-- done action -->
            form.action="<@ofbizUrl>${doneAction!""}</@ofbizUrl>";
            form.submit();
        }
    }
    
    
    var displayDialogId;
    var myDialog;
    var titleText;
    function displayDialogBox(dialogPurpose) 
    {
       var dialogId = '#' + dialogPurpose + 'dialog';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       showDialog(dialogId, displayDialogId, titleText);
    }
   
    function showDialog(dialog, displayDialog, titleText) 
    {
        myDialog = jQuery(displayDialog).dialog({
            modal: true,
            draggable: true,
            resizable: true,
            autoResize: true,
            width: 'auto',
            position: 'center',
            title: titleText
        });
        jQuery(myDialog).parent().addClass('uiDialogBox');
        var dialogClass = displayDialog;
        dialogClass = dialogClass.replace(/^#+/, "");
        jQuery(myDialog).parent().addClass(dialogClass);
        <#-- adjust titlebar width mannualy - Workaround for IE7 titlebar width bug -->
        jQuery(myDialog).siblings('.ui-dialog-titlebar').width(jQuery(myDialog).width());
    }
    
    function confirmDialogResult(result, dialogPurpose) 
    {
        dialogId = '#'+ dialogPurpose +'dialog';
        displayDialogId = '#'+ dialogPurpose +'displayDialog';
        jQuery(displayDialogId).dialog('close');
        if (result == 'Y') 
        {
            postConfirmDialog();
        }
    }
    function postConfirmDialog() 
    {
        form = document.${commonConfirmDialogForm!"detailForm"};
        form.action="<@ofbizUrl>${commonConfirmDialogAction!"confirmAction"}</@ofbizUrl>";
        form.submit();
    }
    <#-- Popup window code -->
    function newPopupWindow(url) 
    {
        popupWindow = window.open(
            url,'popUpWindow','height=350,width=500,left=400,top=200,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes')
    }
    function newPopupWindow(url, name) 
    {
        popupWindow = window.open(
            url,name,'height=500,width=700,left=400,top=200,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes')
            
            jQuery(popupWindow.document).ready(function() {
		        popupWindow.document.title = name;
		    });
    }
			
    function setDeleteId(deleteId,hiddenInputId)
    {
    	if (jQuery('#'+hiddenInputId).length) 
	    {
        	jQuery('#'+hiddenInputId).val(deleteId);
        }
    }
    function deleteConfirm(appendText)
    {
        jQuery('.confirmTxt').html('${confirmDialogText!""} '+appendText+'?');
        displayDialogBox('confirm_');
    }
    function submitSearchForm(form) 
    {
        var searchText = form.searchText.value;
        <#assign SEARCH_DEFAULT_TEXT = Static["com.osafe.util.Util"].getProductStoreParm(request,"SEARCH_DEFAULT_TEXT")!""/>
        if(searchText == "" || searchText == "${StringUtil.wrapString(SEARCH_DEFAULT_TEXT!)}") {
            displayDialogBox('search_');
            return false;
        } else {
            form.submit();
        }
    }
    
    function prepareActionDialog(dialogPurpose) 
    {
       <#-- when dialog first appears, it will display whatever error is in context. So we need to hide it -->
       jQuery('#'+ dialogPurpose +'displayDialog').find('.eCommerceErrorMessage').hide();
        
    }
   
    function displayActionDialogBox(dialogPurpose,elm) 
    {
       var params = jQuery(elm).siblings('input.param').serialize();
       var dialogId = '#' + dialogPurpose + 'dialog';
       var displayContainerId = '#js_' + dialogPurpose + 'Container';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       jQuery(displayContainerId).html('<div id=loadingDiv class=loadingImg></div>');
       getActionDialog(displayContainerId,params);
       showDialog(dialogId, displayDialogId, titleText);
        
    }
   
    function displayActionDialogBoxQuicklook(dialogPurpose,elm) 
    {
       var params = jQuery(elm).siblings('input.param').serialize();
       var oldhtml = jQuery(elm).html();
       jQuery(elm).find('img').remove();
       jQuery(elm).append('<div id=loadingDiv class=loadingImg></div>');
       //jQuery(elm).html('<div id=loadingDiv class=loadingImg></div>');
       var dialogId = '#' + dialogPurpose + 'dialog';
       var displayContainerId = '#js_' + dialogPurpose + 'Container';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       
       var url = "";
       if (params)
       {
          url = '<@ofbizUrl>${dialogActionRequest!"dialogActionRequest"}</@ofbizUrl>?'+params;
       } 
       else 
       {
          url = '<@ofbizUrl>${dialogActionRequest!"dialogActionRequest"}</@ofbizUrl>';
       }
      
       jQuery.get(url, function(data)
       {
           jQuery(elm).find('#loadingDiv').remove();
           jQuery(elm).append(oldhtml);
           <#if QUICKLOOK_DELAY_MS?has_content && Static["com.osafe.util.Util"].isNumber(QUICKLOOK_DELAY_MS) && QUICKLOOK_DELAY_MS != "0">
    	       jQuery(elm).parent("div.js_plpQuicklook").find('img').hide();
    	   </#if>
           jQuery(displayContainerId).replaceWith(data);
           showDialog(dialogId, displayDialogId, titleText);
       });
    }
   
    function displayProductScrollActionDialogBox(dialogPurpose,elm) 
    {
       var params = jQuery(elm).siblings('input.param').serialize();
       var dialogId = '#' + dialogPurpose + 'dialog';
       var displayContainerId = '#' + dialogPurpose + 'Container';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       jQuery(displayContainerId).html('<div id=loadingImg></div>');
       getActionDialog(displayContainerId,params);
    }
   
  function getActionDialog (displayContainerId,params) 
  {
      var url = "";
      if (params)
      {
          url = '${dialogActionRequest!"dialogActionRequest"}?'+params;
      } else {
          url = '${dialogActionRequest!"dialogActionRequest"}';
      }
      jQuery.get(url, function(data)
      {
          jQuery(displayContainerId).replaceWith(data);
          //jQuery(myDialog).dialog( "option", "position", 'center' );
      });
  }
  

    var isWhole_re = /^\s*\d+\s*$/;
    function isWhole (s) {
        return String(s).search (isWhole_re) != -1
    }

    function onImgError(elem,type) 
    {
      var imgUrl = "/osafe_theme/images/user_content/images/";
      var imgName= "NotFoundImage.jpg";
      switch (type) {
        case "PLP-Thumb":
          imgName="NotFoundImagePLPThumb.jpg";
          break;
        case "PLP-Swatch":
          imgName="NotFoundImagePLPSwatch.jpg";
          break;
        case "PDP-Large":
          imgName="NotFoundImagePDPLarge.jpg";
          break;
        case "PDP-Alt":
          imgName="NotFoundImagePDPAlt.jpg";
          break;
        case "PDP-Detail":
          imgName="NotFoundImagePDPDetail.jpg";
          break;
        case "PDP-Swatch":
          imgName="NotFoundImagePDPSwatch.jpg";
          break;
        case "CLP-Thumb":
          imgName="NotFoundImageCLPThumb.jpg";
          break;
        case "MANU-Image":
          imgName="NotFoundImage.jpg";
          break;
      }
      elem.src = imgUrl + imgName;
      <#-- disable onerror to prevent endless loop -->
      elem.onerror = "";
      return true;
    }
    
    <#-- 
         utility function to retrieve a future expiration date in proper format;
         pass three integer parameters for the number of days, hours,
         and minutes from now you want the cookie to expire; all three
         parameters required, so use zeros where appropriate 
    -->
    function getExpDate(days, hours, minutes) {
        var expDate = new Date();
        if (typeof days == "number" && typeof hours == "number" && typeof hours == "number") {
            expDate.setDate(expDate.getDate() + parseInt(days));
            expDate.setHours(expDate.getHours() + parseInt(hours));
            expDate.setMinutes(expDate.getMinutes() + parseInt(minutes));
            return expDate.toGMTString();
        }
    }
    
    <#-- utility function called by getCookie() -->
    function getCookieVal(offset) 
    {
        var endstr = document.cookie.indexOf (";", offset);
        if (endstr == -1) 
        {
            endstr = document.cookie.length;
        }
        return unescape(document.cookie.substring(offset, endstr));
    }
    
    <#-- primary function to retrieve cookie by name -->
    function getCookie(name) 
    {
        var arg = name + "=";
        var alen = arg.length;
        var clen = document.cookie.length;
        var i = 0;
        while (i < clen) 
        {
            var j = i + alen;
            if (document.cookie.substring(i, j) == arg) 
            {
                return getCookieVal(j);
            }
            i = document.cookie.indexOf(" ", i) + 1;
            if (i == 0) break; 
        }
        return null;
    }
    
    <#-- store cookie value with optional details as needed -->
    function setCookie(name, value, expires, path, domain, secure) 
    {
        document.cookie = name + "=" + escape (value) +
            ((expires) ? "; expires=" + expires : "") +
            ((path) ? "; path=" + path : "") +
            ((domain) ? "; domain=" + domain : "") +
            ((secure) ? "; secure" : "");
    }
    
    <#-- remove the cookie by setting ancient expiration date -->
    function deleteCookie(name,path,domain) 
    {
        if (getCookie(name)) 
        {
            document.cookie = name + "=" +
                ((path) ? "; path=" + path : "") +
                ((domain) ? "; domain=" + domain : "") +
                "; expires=Thu, 01-Jan-70 00:00:01 GMT";
        }
    }
    
    <#-- Light Box Cart -->

    jQuery(document).ready(function () 
    {
       <#if errorMessageList?has_content>
          <#list errorMessageList as errorMsg>           
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
			}
			 catch(e){}    
            </#list>
        </#if>
    
        <#-- Hide/Show Navigation Widget -->
        jQuery('.showNavWidget').click(function() 
        {
	        jQuery('.showNavWidget').hide();
	        jQuery('.hideNavWidget').show();
	        jQuery('#eCommerceNavBarMenu').addClass("showNavBarMenu");
	        jQuery('#eCommerceNavBarMenu').removeClass("hideNavBarMenu");
         });
        jQuery('.hideNavWidget').click(function() 
        {
	        jQuery('.hideNavWidget').hide();
	        jQuery('.showNavWidget').show();
	        jQuery('#eCommerceNavBarMenu').addClass("hideNavBarMenu");
	        jQuery('#eCommerceNavBarMenu').removeClass("showNavBarMenu");
         });
        
    
    
        jQuery('.dateEntry').each(function(){datePicker(this);});
       
        jQuery('.showLightBoxCart').hover(
            function(e) {
               <#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request)! />  
                    <#if shoppingCart?has_content >
                        <#assign cartCount = shoppingCart.getTotalQuantity()!0 />
                        <#assign cartSubTotal = shoppingCart.getSubTotal()!0 />
                    </#if>
                    <#if (cartCount?if_exists > 0) >
                        e.preventDefault();
                        displayLightDialogBox('lightCart_');
                        var dialogHolder = jQuery('#lightCart_displayDialog').parent();
                        jQuery(dialogHolder).hide();
                        var x = jQuery(this).offset().left - jQuery(this).outerWidth() + 25; //the plus 25 is temporary until CSS changes are complete to avoid unwanted mouseout from triggering
                        var y = jQuery(this).offset().top - jQuery(document).scrollTop() + jQuery(this).outerHeight();
                        jQuery(dialogHolder).css('left', x + 'px');
                        jQuery(dialogHolder).css('top', y + 'px');
                        jQuery(dialogHolder).slideDown(850);
                        jQuery(dialogHolder).addClass('js_lightBoxCartContainer');
                        var titlebar = jQuery(dialogHolder).find(".ui-dialog-titlebar");
                        jQuery(titlebar).attr('id', 'js_lightBoxCartTitleBar');
                        jQuery('.js_lightBoxCartContainer').attr('id', 'js_lightBoxCartContainerId');
                    </#if>
            },
            function() 
            {
                <#-- do nothing. Let functions below handle mouseout -->
            }
        );
        
        <#assign lightDelayMs = Static["com.osafe.util.Util"].getProductStoreParm(request,"LIGHTBOX_DELAY_MS")!0/>  
        jQuery('#eCommerceNavBar').mouseover(function(e)
        {
            var id = e.target.id;
            if((id != "lightCart_displayDialog") && (id != "js_lightBoxCartTitleBar"))
            {
                jQuery('.js_lightBoxCartContainer').delay(${lightDelayMs}).slideUp(850, function() {
                jQuery('.js_lightBoxCartContainer').remove();  
                }); 
            }
        }); 
        jQuery('#eCommercePageBody').mouseover(function(e)
        {
            var id = e.target.id;
            if((id != "lightCart_displayDialog") && (id != "js_lightBoxCartTitleBar"))
            {
                jQuery('.js_lightBoxCartContainer').delay(${lightDelayMs}).slideUp(850, function() 
                {
                	jQuery('.js_lightBoxCartContainer').remove();  
                }); 
            }
        });
        jQuery('#eCommerceHeader').mouseover(function(e)
        {
            var id = e.target.id;
            if((id != "siteShoppingCartSize") && (id != "lightCart_displayDialog"))
            {
                jQuery('.js_lightBoxCartContainer').delay(${lightDelayMs}).slideUp(850, function() {
                jQuery('.js_lightBoxCartContainer').remove();  
                }); 
            }
        });
        
        jQuery(window).scroll(function()
        {
	         var heightBody = jQuery('#eCommercePageBody').height(); 
	         var y = jQuery(window).scrollTop(); 
	         if( y > (heightBody*.10) )
	         {
	           jQuery(".js_scrollToTop").fadeIn("slow"); 
	         }
	         else
	         {
	          jQuery('.js_scrollToTop').fadeOut('slow');
	         }
        });          
        
        var autoSuggestionList = [""];
        jQuery(function() 
        {
            jQuery("#searchText").autocomplete({source: autoSuggestionList});
        });
        
        jQuery("#searchText").keyup(function(e) 
        {
            var keyCode = e.keyCode;
            if(keyCode != 40 && keyCode != 38)
            {
            	var searchText = jQuery(this).attr('value');
            
              	jQuery("#searchText").autocomplete({
                	appendTo:"#searchAutoComplete",
                	source: function(request, response) 
                	{
	                	jQuery.ajax({
	                    	url: "<@ofbizUrl secure="${request.isSecure()?string}">findAutoSuggestions?searchText="+searchText+"</@ofbizUrl>",
	                    	dataType: "json",
	                    	type: "POST",
	                    	success: function(data) 
	                    	{
	                    		if(data.autoSuggestionList != null)
	                    		{
	                        		response(jQuery.map(data.autoSuggestionList, function(item) 
	                        		{
	                            		return {label: __highlight(item, searchText),
	                    				value: item};
	                        		}))
	                    		}
	                    		else
	                    		{
	                        		response(function() 
	                        		{
	                            		return {
	                                		value: ""
	                            		}
	                        		})
	                    		}
	                		}
	                
	            		});
          			},
          			minLength: 1
          		});
        	}
    	});
    
    
        
    jQuery('.checkDelivery').click(function(e)
    {
        displayActionDialogBox('pincodeChecker_',this);
    });
        
    jQuery('.pincodeChecker_Form').submit(function(event) 
    {
            event.preventDefault();
            jQuery.get(jQuery(this).attr('action')+'?'+jQuery(this).serialize(), function(data) 
            {
                jQuery('#js_pincodeCheckContainer').replaceWith(data);
            });
    });
    
    jQuery('.js_cancelPinCodeChecker').click(function(event) 
    {
            event.preventDefault();
            jQuery(displayDialogId).dialog('close');
    });
    
    <#-- Set column numbers on table -->
    setTableColumnNumber();
    
}); 
<#-- END DOCUMENT ON READY  -->



function __highlight(s, t) 
{
  var matcher = new RegExp("("+jQuery.ui.autocomplete.escapeRegex(t)+")", "ig" );
  return s.replace(matcher, "<span class=\"searchHighlight\">$1</span>");
}
    
    function displayLightDialogBox(dialogPurpose) 
    {
       var dialogId = '#' + dialogPurpose + 'dialog';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       showLightBoxDialog(dialogId, displayDialogId, titleText);
    }
    
    function showLightBoxDialog(dialog, displayDialog, titleText) 
    {
        myDialog = jQuery(displayDialog).dialog({
            modal: false,
            draggable: true,
            resizable: true,
            width: 'auto',
            autoResize:true,
            position: 'center',
            title: titleText
        });
        var dialogClass = displayDialog;
        dialogClass = dialogClass.replace(/^#+/, "");
        jQuery(myDialog).parent().addClass(dialogClass);
        <#-- adjust titlebar width mannualy - Workaround for IE7 titlebar width bug -->
        jQuery(myDialog).siblings('.ui-dialog-titlebar').width(jQuery(myDialog).width());
    }
    
    
    function datePicker(triger)
    {
	   jQuery(triger).datepicker({
	       showOn: 'button',
	       buttonImage: '<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>',
	       buttonImageOnly: false,
	       <#if preferredDateFormat?exists && preferredDateFormat?has_content>
	         <#assign format = StringUtil.wrapString(preferredDateFormat.toLowerCase()) />
	         <#assign format = format?replace("yy", "y") />
	       <#else>
	         <#assign format = "mm/dd/y" />
	       </#if>
	       dateFormat: '${format}'
	   });
 }
<#-- end Light Box Cart -->

    function addMultiOrderItems() 
    {
    	var addItemsToCart = "true";
        var itemSelected = false;
    	var count = 0;
    	jQuery('.js_add_multi_product_quantity').each(function () 
    	{
    		reOrderQtyIdArr = jQuery(this).attr("id").split("_");
    	    variantIsChecked = jQuery('#js_add_multi_product_id_'+reOrderQtyIdArr[5]).is(":checked");
    		if(variantIsChecked)
    		{
                itemSelected = true;
    			var quantity = jQuery(this).val();
    			var add_productId = jQuery('#js_add_multi_product_id_'+reOrderQtyIdArr[5]).val();
    			var productName = jQuery('#js_productName_'+count).val();
    			<#-- check if qty is whole number -->
    			if(quantity != "") 
				{
					if(isQtyWhole(quantity,productName))
					{
						if(!(isQtyZero(quantity,productName,add_productId)))
						{
							<#-- check how many already in cart and add to qty -->
			           		quantity = Number(quantity) + Number(getQtyInCart(add_productId));
			                <#-- validate qty limits -->
			                if(!(validateQtyMinMax(add_productId,productName,quantity)))
			                {
			                	addItemsToCart = "false";
			                }
		                }
		                else
		                {
		                	addItemsToCart = "false";
		                }
					}
					else
					{
						addItemsToCart = "false";
					}
				}
				else
				{
				    alert("${uiLabelMap.ReOredrBlankQtyError}");
					addItemsToCart = "false";
				}
    		}
    		count = count + 1;
    	});
 
        if (!itemSelected)
        {
            alert("${uiLabelMap.NoItemSelectedError}");
            addItemsToCart = "false";
        }
		if(addItemsToCart == "true")
		{
	    	<#-- add to cart action -->
	        document.reOrderItemForm.action="<@ofbizUrl>${addMultiItemsToCartAction!""}</@ofbizUrl>";
	        document.reOrderItemForm.submit();
        }
    }
    
    function seqReOrderCheck(elm)
    {
        reOrderQtyIdArr = jQuery(elm).attr("id").split("_");
        if(jQuery(elm).val()=="")
        {
            jQuery('#js_add_multi_product_id_'+reOrderQtyIdArr[5]).attr("checked", false);
        }
        else
        {
            jQuery('#js_add_multi_product_id_'+reOrderQtyIdArr[5]).attr("checked", true);
        }
        
    }
    function findOrderItems(elm) 
    {
        document.reOrderItemSearchForm.action="<@ofbizUrl>eCommerceReOrderItems</@ofbizUrl>";
	    document.reOrderItemSearchForm.submit();
    }
    
    function showTooltip(text, elm, elmType)
    {
        var tooltipBox = jQuery('.js_tooltip')[0];
        if(text != "")
        {
		    var obj2 = jQuery('.js_tooltipText')[0];
		    obj2.innerHTML = text;
	    }
	    tooltipBox.style.display = 'block';
	    <#assign decorator = "${parameters.eCommerceDecoratorName!}"/>
	    <#-- get the ScrollTop and ScrollLeft to add in top and left postion of tooltip. -->
	    var st = Math.max(document.body.scrollTop,document.documentElement.scrollTop);
	    var sl = Math.max(document.body.scrollLeft,document.documentElement.scrollLeft);
	    
	    <#--
	      determine the margin between window left edge and  main content screen.
	      when we set the left position of tooltip using tooltip.style.left, it sets the position from main content screen. 
	      So we need to subtract the eCommerceContentLeftPos from Element X position. 
	    -->
	      <#if decorator == 'ecommerce-basic-bevel-decorator'>
	          var eCommerceContentLeftPos = 0;
	      <#else>
	          var eCommerceContentLeftPos = jQuery('#eCommerceContent').offset().left;
	      </#if>
	    
	    var WW = jQuery(window).width();
	    var WH = jQuery(window).height();
	    
	    var EX = "";
		var EY = "";
	    if(elmType == "icon")
	    {
		    //subtracting the sl and st from element left and top position because element left and top includes the scroll pixels. 
		    EX = jQuery(elm).children().offset().left - sl;
		    EY = jQuery(elm).children().offset().top - st;
	    }
	    else if(elmType == "input")
	    {
	    	//subtracting the sl and st from element left and top position because element left and top includes the scroll pixels. 
		    EX = jQuery(elm).offset().left - sl;
		    EY = jQuery(elm).offset().top - st;
	    }
	    
	    var TTW = jQuery(tooltipBox).width();
	    var TTH = jQuery(tooltipBox).height();
	    var LP = 0;
	    var TP = 0;
	    var EH = jQuery(elm).children().height();
		var EW = jQuery(elm).children().width();
		
		var EH = "";
		var EW = "";
	    if(elmType == "icon")
	    {
		    EH = jQuery(elm).children().height();
			EW = jQuery(elm).children().width();
	    }
	    else if(elmType == "input")
	    {
	    	EH = jQuery(elm).height();
			EW = jQuery(elm).width();
	    }
	    
	    var TOP = eval(EY > TTH);
	    var BOTTOM = eval(!(TOP));
	    var LEFT = eval((TTW + EX) > WW);
	    var RIGHT = eval(!(LEFT));
	    
	    <#--
	      These TOP, BOTTOM, LEFT and RIGHT are the position of tooltip and our arrow would be opposite from the tootip, 
	      means if tooltip is on TOP then the Arrow would be at bottom of tooltip. 
	      If the tooltip is in LEFT then the Arrow would be in right of the tooltip. 
	    -->
	    
	    if(BOTTOM && LEFT)
	    {
	        jQuery('.js_tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('.js_tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('.js_tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('.js_tooltipTop').addClass("tooltipTopRightArrow");
	    }
	    else if(BOTTOM && RIGHT)
	    {
	        jQuery('.js_tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('.js_tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('.js_tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('.js_tooltipTop').addClass("tooltipTopLeftArrow");
	    }
	    else if(TOP && LEFT)
	    {
	        jQuery('.js_tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('.js_tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('.js_tooltipBottom').removeClass("tooltipBottomLeftArrow");
	        jQuery('.js_tooltipBottom').addClass("tooltipBottomRightArrow");
	    }
	    else if(TOP && RIGHT)
	    {
	        jQuery('.js_tooltipTop').removeClass("tooltipTopLeftArrow");
	        jQuery('.js_tooltipBottom').removeClass("tooltipBottomRightArrow");
	        jQuery('.js_tooltipTop').removeClass("tooltipTopRightArrow");
	        jQuery('.js_tooltipBottom').addClass("tooltipBottomLeftArrow");
	    }
	    
	    <#-- determine the left position and top position to set to the tooltip -->
	    if(LEFT)
	    {
	       <#-- adding Element Width EW so that the tooltip starts (horizontally) from right of the icon. -->
	       LP = EX - eCommerceContentLeftPos -TTW + sl + EW; 
	    }
	    else
	    {
	       LP = EX - eCommerceContentLeftPos + sl;
	    }
	    
	    if(BOTTOM)
	    {
	        <#-- adding Element Height EH so that the tooltip starts(vertically) from bottom of the icon. -->
	        TP = (EY + st + EH);
	    }
	    else
	    {
	        TP = (EY- TTH + st);
	    }
	    jQuery(tooltipBox).css({ top: TP+'px' });
	    jQuery(tooltipBox).css({ left: LP+'px' }); 
    }
    
    function hideTooltip()
    {
        document.getElementById('tooltip').style.display = "none";
    }
    
    
    
    
    
    <#-- QTY validation functions -->
    function validateQtyMinMax(productId,productName,quantity) <#-- throws alert -->
    {
    	<#-- get lower and upper limits for quantity -->
   		var lowerLimit = Number(getMinQty(productId));
        var upperLimit = Number(getMaxQty(productId));
    	if(quantity != 0) 
        {
        	if(quantity < lowerLimit)
          	{
          	  	<#assign pdpMinQtyError = Static["org.ofbiz.base.util.StringUtil"].replaceString(uiLabelMap.PDPMinQtyError, '\"', '\\"')/>
          	  	<#assign pdpMinQtyError = Static["org.ofbiz.base.util.StringUtil"].wrapString(pdpMinQtyError)/>
       		  	var pdpMinQtyErrorText = "${pdpMinQtyError!""}";
       		  	pdpMinQtyErrorText = pdpMinQtyErrorText.replace('_PRODUCT_NAME_',productName);
       		  	pdpMinQtyErrorText = pdpMinQtyErrorText.replace('_PDP_QTY_MIN_',lowerLimit);
       		
              	alert(pdpMinQtyErrorText);
            
              	return false;
          	}
          	else if(upperLimit!= 0 && quantity > upperLimit)
          	{
              	<#assign pdpMaxQtyError = Static["org.ofbiz.base.util.StringUtil"].replaceString(uiLabelMap.PDPMaxQtyError, '\"', '\\"')/>
              	<#assign pdpMaxQtyError = Static["org.ofbiz.base.util.StringUtil"].wrapString(pdpMaxQtyError)/>
       		  	var pdpMaxQtyErrorText = "${pdpMaxQtyError!""}";
       		  	pdpMaxQtyErrorText = pdpMaxQtyErrorText.replace('_PRODUCT_NAME_',productName);
       		  	pdpMaxQtyErrorText = pdpMaxQtyErrorText.replace('_PDP_QTY_MAX_',upperLimit);
       		
              	alert(pdpMaxQtyErrorText);
            
              	return false;
          	}
        }
        return true;
    }
    
    <#assign PDP_QTY_MIN = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MIN")!"1"/>
    <#assign PDP_QTY_MAX = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MAX")!"99"/>
    function getMaxQty(productId)
    {
    	<#-- Use system parameter -->
    	var upperLimit = Number(${PDP_QTY_MAX!});
    	<#-- If product has a PDP_QTY_MAX Attribute to override system parameter -->
      	if(jQuery('#js_pdpQtyMaxAttributeValue_'+productId).length)
		{
			upperLimit = Number(jQuery('#js_pdpQtyMaxAttributeValue_'+productId).val());
		}
		return upperLimit;
    }
    
    function getMinQty(productId)
    {
    	<#-- Use system parameter -->
    	var lowerLimit = Number(${PDP_QTY_MIN!});
    	<#-- If product has a PDP_QTY_MIN Attribute to override system parameter -->
      	if(jQuery('#js_pdpQtyMinAttributeValue_'+productId).length)
		{
			lowerLimit = Number(jQuery('#js_pdpQtyMinAttributeValue_'+productId).val());
		}
		return lowerLimit;
    }
    
    function isQtyWhole(quantity,productName) <#-- throws alert -->
    {
    	if(!isWhole(quantity))
  		{
  			<#assign pdpQtyDecimalNumberError = Static["org.ofbiz.base.util.StringUtil"].replaceString(uiLabelMap.PDPQtyDecimalNumberError, '\"', '\\"')/>
   		 	var pdpMaxQtyErrorText = "${pdpQtyDecimalNumberError!""}";
   		  	pdpMaxQtyErrorText = pdpMaxQtyErrorText.replace('_PRODUCT_NAME_',productName);
   		
          	alert(pdpMaxQtyErrorText);
      		return false;
  		}
  		return true;
    }
    
    function isQtyZero(quantity,productName,productId) <#-- throws alert -->
    {
    	var lowerLimit = Number(getMinQty(productId));
    	if(quantity == 0)
      	{
      	  	<#assign pdpMinQtyError = Static["org.ofbiz.base.util.StringUtil"].replaceString(uiLabelMap.PDPMinQtyError, '\"', '\\"')/>
   		  	var pdpMinQtyErrorText = "${pdpMinQtyError!""}";
   		  	pdpMinQtyErrorText = pdpMinQtyErrorText.replace('_PRODUCT_NAME_',productName);
   		  	pdpMinQtyErrorText = pdpMinQtyErrorText.replace('_PDP_QTY_MIN_',lowerLimit);
   		
          	alert(pdpMinQtyErrorText);
        
          	return true;
      	}
  		return false;
    }
    
    function getQtyInCart(productId) 
    {
    	<#-- check how many items are already in the cart -->
    	var qtyInCart = Number(0);
    	<#if shoppingCart?has_content>
        	<#assign shoppingCartItemSize = shoppingCart.items().size()/>
        	<#assign shoppingCartItems = shoppingCart.items()/>
        	<#list shoppingCartItems as shoppingCartItem>
        		cartItemProductId = '${shoppingCartItem.getProductId()}';
        		if(cartItemProductId == productId)
        		{
        			cartItemQty = Number(${shoppingCartItem.getQuantity()});
        			qtyInCart = Number(qtyInCart) + Number(cartItemQty);
        		}
        	</#list>
      	</#if>    
      	return qtyInCart;
    }
    
    function getTotalQtyFromScreen(inputName,rowNum) 
    {
    	var quantity = Number(0);
    	var quantityInputClassAttr = jQuery('#'+inputName+rowNum).attr("class");
        jQuery('.'+quantityInputClassAttr).each(function () 
    	{
	     	if (jQuery(this).val() != '')
	     	{
    	    	quantity = quantity + Number(jQuery(this).val());
    	    }
    	});
    	return quantity;
    }
    
    function isItemSelectedPdp() <#-- throws alert -->
    {
    	if (document.addform.add_product_id.value == 'NULL' || document.addform.add_product_id.value == '') 
        {
           OPT = eval("getFormOption()");
           for (i = 0; i < OPT.length; i++) 
           {
            var optionName = OPT[i];
            var indexSelected = document.forms["addform"].elements[optionName].selectedIndex;
            if(indexSelected <= 0)
            {
                <#-- Trim the FT prefix and convert to title case -->
                var properName = OPT[i].substr(2);
                <#-- capitalize comes from prototype, do capitalize to each part -->
                var parts = properName.split('_');
                parts.each(function(element,index){
                    parts[index] = element.capitalize();
                });
                properName = parts.join(" ");
                alert("Please select a " + properName);
                break;
            }
           }
           return false;
        } 
        return true;
    }
    
    function isItemSelectedPlp(selectFeatureDiv) <#-- throws alert -->
    {
        if (!jQuery('#'+selectFeatureDiv+'_add_product_id').length || jQuery('#'+selectFeatureDiv+'_add_product_id').val() == 'NULL' || jQuery('#'+selectFeatureDiv+'_add_product_id').val() == '') 
        {
           OPT = eval("getFormOption" + selectFeatureDiv + "()");
           for (i = 0; i < OPT.length; i++) 
           {
            var optionName = OPT[i];
            var indexSelected = jQuery('div#'+selectFeatureDiv+' select.'+optionName).prop("selectedIndex");
            if(indexSelected <= 0 || !jQuery('#'+selectFeatureDiv+'_add_product_id').length)
            {
                <#-- Trim the FT prefix and convert to title case -->
                var properName = OPT[i].substr(2);
                var properName = properName.replace("_"+selectFeatureDiv,""); 
                <#-- capitalize comes from prototype, do capitalize to each part -->
                var parts = properName.split('_');
                parts.each(function(element,index){
                    parts[index] = element.capitalize();
                });
                properName = parts.join(" ");
                alert("Please select a " + properName);
                break;
            }
           }
           return false;
        } 
        return true;
    }
    
    function validateCart()
    {
    	var cartIsValid = true;
    	var productId = "";
        var productName = "";
        var quantity = ""; 
        <#-- iterate through cart -->
        <#if shoppingCart?has_content>
            <#assign shoppingCartItems = shoppingCart.items()/>
            <#list shoppingCartItems as shoppingCartItem>
                <#-- get productId and quantity -->
                <#assign productId = shoppingCartItem.getProductId()>
                <#assign product = shoppingCartItem.getProduct()>
                <#assign productName = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(shoppingCartItem.getProduct(), "PRODUCT_NAME", locale, dispatcher)!"" >
                <#assign productQty = 0>
                <#list shoppingCartItems as otherShoppingCartItem>
                    <#assign otherProductId = otherShoppingCartItem.getProductId()>
                    <#if productId == otherProductId>
                        <#assign productQty = productQty + otherShoppingCartItem.getQuantity()>
                    </#if>
                </#list>
                <#assign PDP_QTY_MIN = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MIN")!"1"/>
                <#if !PDP_QTY_MIN?has_content || !(Static["com.osafe.util.Util"].isNumber(PDP_QTY_MIN))>
                  <#assign PDP_QTY_MIN = "1"/>
                </#if>
                <#assign PDP_QTY_MAX = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MAX")!/>
                <#if !PDP_QTY_MAX?has_content || !(Static["com.osafe.util.Util"].isNumber(PDP_QTY_MAX))>
                  <#assign PDP_QTY_MAX = "99"/>
                </#if>
                <#assign productAttributes = product.getRelatedCache("ProductAttribute")!"" />  
                <#if productAttributes?has_content>
                  <#assign productAttrPdpQtyMin = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId, "attrName", "PDP_QTY_MIN"))?if_exists /> 
                  <#assign productAttrPdpQtyMax = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(productAttributes,Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId, "attrName", "PDP_QTY_MAX"))?if_exists />
                  <#assign productAttrPdpQtyMin = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productAttrPdpQtyMin!)?if_exists /> 
                  <#assign productAttrPdpQtyMax = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(productAttrPdpQtyMax!)?if_exists /> 
                  <#if productAttrPdpQtyMin?has_content && productAttrPdpQtyMax?has_content && productAttrPdpQtyMin.attrValue?has_content && productAttrPdpQtyMax.attrValue?has_content>
                    <#assign PDP_QTY_MIN = productAttrPdpQtyMin.attrValue/>
                    <#assign PDP_QTY_MAX = productAttrPdpQtyMax.attrValue/>
                  </#if>
                  <#if productQty &lt; PDP_QTY_MIN?number || productQty &gt; PDP_QTY_MAX?number>
                	cartIsValid = false;
                    productId = "${productId!}";
                    productName = "${StringUtil.wrapString(productName!)}";
                    quantity = "${productQty!}";
                    <#-- If on ShowCart, then show errors -->
                    if (jQuery('.showCartOrderItems').length) 
			        {
			            validateQtyMinMax(productId, productName, quantity);
			        }
			        else
			        {
			        	<#-- redirect to show cart page -->
            			window.location.replace("<@ofbizUrl>eCommerceShowcart</@ofbizUrl>");
                    }
                  </#if> 
                </#if>
            </#list>
        </#if>
        
        return cartIsValid;
    }

    function submitMultiSearchForm(form) 
    {
        var isValid = false;
        jQuery('form[name=${formName!"entryForm"}] input[type="text"]').each(function() 
        {
            if (jQuery.trim(jQuery(this).val()) != '') 
            {
                isValid = true;
            }
        });
        if (isValid == false)
        {
            displayDialogBox('search_');
            return false;
        }
        else
        {
            form.submit();
        }
    }
    
    function addRow(elm, index)
    {
        var addRowElm = elm;
        var lastDivElm = jQuery('#searchItemDiv');
        var rowDiv = new Element('DIV');
        rowDiv.setAttribute("class", "entry");
        var innerText =  "<label>${uiLabelMap.FindItemCaption}<\/label><div class=\"entryField\"><input type=\"text\" name=\"searchItem"+index+"\"\/><\/div>";
        jQuery(rowDiv).html(innerText);
        jQuery(rowDiv).insertAfter('#searchItemDiv');
        jQuery(lastDivElm).removeAttr("id");
        jQuery(rowDiv).attr("id","searchItemDiv");
        updateIndexPos(addRowElm, index);
    }
    
    function updateIndexPos(addRowElm, index)
    {
        index = index + 1;
        addRowElm.setAttribute("onClick", "javascript:addRow(this,"+index+");");
    }
    
    //nextRowNumberInputId: the next row number will be stored in a hidden field with this ID
    //rowClass: all rows will have a common class
    //placeholderDivId: added Div will go before this DivId
    //getRowAction: this is the controller action to retrieve the screen that is added as the next row
	function addNewRow(nextRowNumberInputId, rowClass, placeholderDivId, getRowAction)
	{
		//set the next row number as a hidden input on the screen
		var rowNum = Number(jQuery("#"+nextRowNumberInputId).val());
	    
		jQuery("#"+nextRowNumberInputId).val(Number(rowNum + 1));
		
		jQuery.get('<@ofbizUrl>'+ getRowAction +'?addRowIndex='+rowNum+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "")+'</@ofbizUrl>', function(data) {
	        jQuery(data).insertBefore("#" + placeholderDivId);
	    });
	}
		
	function deleteExistingRow(addRemoveIndex,rowClass,nextRowNumberInputId)
	{
		jQuery('.'+addRemoveIndex).remove();
		if (jQuery('.'+rowClass).length) 
	    {
	    	//do nothing
	    }
	    else
	    {
	    	//set the hidden input rowNumber to 0
	    	jQuery("#"+nextRowNumberInputId).val("0");
	    }
	}
	
	function elmIsEmpty(elm)
	{
      return !jQuery.trim(elm.html())
    }
    
    function setTableColumnNumber()
    {
    
    	jQuery('table.dataTable').each(function () 
    	{
    		var count = 1;
    		var theadTrTh = jQuery(this).find('thead').find('tr').find('th');
    		jQuery(theadTrTh).each(function () 
    		{
    			jQuery(this).addClass("column" + count);
    			count++;
    		});
    		
    		var tbodyTr = jQuery(this).find('tbody').find('tr');
    		jQuery(tbodyTr).each(function () 
    		{
    			count = 1;
    			jQuery(this).find('td').each(function () 
    			{
    				jQuery(this).addClass("column" + count);
    				count++;
    			});
    		});
    		
    		count = 1;
    		var tfootTrTd = jQuery(this).find('tfoot').find('tr').find('td');
    		jQuery(tfootTrTd).each(function () 
    		{
    			jQuery(this).addClass("column" + count);
    			count ++;
    		});
    		
    	});
    }
    
    <#-- Big Fish Simple slider -->
    function bfSimpleSlide(interval)
	{
		var firstSlide = jQuery('.bf-slide:first');
		var currentSlide = jQuery('.bf-slide:first');
		time=setInterval(function(){
				var nextSlide = jQuery(currentSlide).next('.bf-slide');
				if (jQuery(nextSlide).length)
				{
					jQuery(nextSlide).fadeIn(300,'linear');
					jQuery(currentSlide).hide();
					currentSlide = jQuery(nextSlide);
				}
				else
				{
					jQuery(firstSlide).fadeIn(300,'linear');
					jQuery(currentSlide).hide();
					currentSlide = jQuery(firstSlide);
				}
		 },interval);
	}
	
	<#-- convert input field entry with single quotes to smart quotes -->
    function convertApostrophe(inputId)
	{
		var inputField = jQuery('#'+inputId);
		if(jQuery(inputField).length)
		{
			var oldValue = jQuery(inputField).val();
			if(oldValue != null && oldValue != "")
	       	{
	       		if (oldValue.indexOf("'") >= 0)
	       		{
					newValue = oldValue.replace(/'/g,"&#39;");
					jQuery(inputField).val(newValue);
				}
			}
		}
	}
	
	<#-- Variables to use for transitions for jssor slider -->
    var jssor_SlideshowTransitions = [
        //Rotate Overlap
        { $Duration: 1200, $Zoom: 11, $Rotate: -1, $Easing: { $Zoom: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Round: { $Rotate: 0.5 }, $Brother: { $Duration: 1200, $Zoom: 1, $Rotate: 1, $Easing: $JssorEasing$.$EaseSwing, $Opacity: 2, $Round: { $Rotate: 0.5 }, $Shift: 90 } },
        //Switch
        { $Duration: 1400, x: 0.25, $Zoom: 1.5, $Easing: { $Left: $JssorEasing$.$EaseInWave, $Zoom: $JssorEasing$.$EaseInSine }, $Opacity: 2, $ZIndex: -10, $Brother: { $Duration: 1400, x: -0.25, $Zoom: 1.5, $Easing: { $Left: $JssorEasing$.$EaseInWave, $Zoom: $JssorEasing$.$EaseInSine }, $Opacity: 2, $ZIndex: -10 } },
        //Rotate Relay
        { $Duration: 1200, $Zoom: 11, $Rotate: 1, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Round: { $Rotate: 1 }, $ZIndex: -10, $Brother: { $Duration: 1200, $Zoom: 11, $Rotate: -1, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Round: { $Rotate: 1 }, $ZIndex: -10, $Shift: 600 } },
        //Doors
        { $Duration: 1500, x: 0.5, $Cols: 2, $ChessMode: { $Column: 3 }, $Easing: { $Left: $JssorEasing$.$EaseInOutCubic }, $Opacity: 2, $Brother: { $Duration: 1500, $Opacity: 2 } },
        //Rotate in+ out-
        { $Duration: 1500, x: -0.3, y: 0.5, $Zoom: 1, $Rotate: 0.1, $During: { $Left: [0.6, 0.4], $Top: [0.6, 0.4], $Rotate: [0.6, 0.4], $Zoom: [0.6, 0.4] }, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Brother: { $Duration: 1000, $Zoom: 11, $Rotate: -0.5, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Shift: 200 } },
        //Fly Twins
        { $Duration: 1500, x: 0.3, $During: { $Left: [0.6, 0.4] }, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Outside: true, $Brother: { $Duration: 1000, x: -0.3, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } },
        //Rotate in- out+
        { $Duration: 1500, $Zoom: 11, $Rotate: 0.5, $During: { $Left: [0.4, 0.6], $Top: [0.4, 0.6], $Rotate: [0.4, 0.6], $Zoom: [0.4, 0.6] }, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Brother: { $Duration: 1000, $Zoom: 1, $Rotate: -0.5, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Shift: 200 } },
        //Rotate Axis up overlap
        { $Duration: 1200, x: 0.25, y: 0.5, $Rotate: -0.1, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Brother: { $Duration: 1200, x: -0.1, y: -0.7, $Rotate: 0.1, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2 } },
        //Chess Replace TB
        { $Duration: 1600, x: 1, $Rows: 2, $ChessMode: { $Row: 3 }, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1600, x: -1, $Rows: 2, $ChessMode: { $Row: 3 }, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } },
        //Chess Replace LR
        { $Duration: 1600, y: -1, $Cols: 2, $ChessMode: { $Column: 12 }, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1600, y: 1, $Cols: 2, $ChessMode: { $Column: 12 }, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } },
        //Shift TB
        { $Duration: 1200, y: 1, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1200, y: -1, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } },
        //Shift LR
        { $Duration: 1200, x: 1, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1200, x: -1, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } },
        //Return TB
        { $Duration: 1200, y: -1, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $ZIndex: -10, $Brother: { $Duration: 1200, y: -1, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $ZIndex: -10, $Shift: -100 } },
        //Return LR
        { $Duration: 1200, x: 1, $Delay: 40, $Cols: 6, $Formation: $JssorSlideshowFormations$.$FormationStraight, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $ZIndex: -10, $Brother: { $Duration: 1200, x: 1, $Delay: 40, $Cols: 6, $Formation: $JssorSlideshowFormations$.$FormationStraight, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $ZIndex: -10, $Shift: -100 } },
        //Rotate Axis down
        { $Duration: 1500, x: -0.1, y: -0.7, $Rotate: 0.1, $During: { $Left: [0.6, 0.4], $Top: [0.6, 0.4], $Rotate: [0.6, 0.4] }, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Brother: { $Duration: 1000, x: 0.2, y: 0.5, $Rotate: -0.1, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2 } },
        //Extrude Replace
        { $Duration: 1600, x: -0.2, $Delay: 40, $Cols: 12, $During: { $Left: [0.4, 0.6] }, $SlideOut: true, $Formation: $JssorSlideshowFormations$.$FormationStraight, $Assembly: 260, $Easing: { $Left: $JssorEasing$.$EaseInOutExpo, $Opacity: $JssorEasing$.$EaseInOutQuad }, $Opacity: 2, $Outside: true, $Round: { $Top: 0.5 }, $Brother: { $Duration: 1000, x: 0.2, $Delay: 40, $Cols: 12, $Formation: $JssorSlideshowFormations$.$FormationStraight, $Assembly: 1028, $Easing: { $Left: $JssorEasing$.$EaseInOutExpo, $Opacity: $JssorEasing$.$EaseInOutQuad }, $Opacity: 2, $Round: { $Top: 0.5 } } }
    ];
	
	var jssor_RotateOverlap = [
        //Rotate Overlap
        { $Duration: 1200, $Zoom: 11, $Rotate: -1, $Easing: { $Zoom: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Round: { $Rotate: 0.5 }, $Brother: { $Duration: 1200, $Zoom: 1, $Rotate: 1, $Easing: $JssorEasing$.$EaseSwing, $Opacity: 2, $Round: { $Rotate: 0.5 }, $Shift: 90 } }
    ];
	
	var jssor_Switch = [
        //Switch
        { $Duration: 1400, x: 0.25, $Zoom: 1.5, $Easing: { $Left: $JssorEasing$.$EaseInWave, $Zoom: $JssorEasing$.$EaseInSine }, $Opacity: 2, $ZIndex: -10, $Brother: { $Duration: 1400, x: -0.25, $Zoom: 1.5, $Easing: { $Left: $JssorEasing$.$EaseInWave, $Zoom: $JssorEasing$.$EaseInSine }, $Opacity: 2, $ZIndex: -10 } }
    ];
    
    var jssor_RotateRelay = [
        //Rotate Relay
        { $Duration: 1200, $Zoom: 11, $Rotate: 1, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Round: { $Rotate: 1 }, $ZIndex: -10, $Brother: { $Duration: 1200, $Zoom: 11, $Rotate: -1, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Round: { $Rotate: 1 }, $ZIndex: -10, $Shift: 600 } }
    ];
    
    var jssor_Doors = [
        //Doors
        { $Duration: 1500, x: 0.5, $Cols: 2, $ChessMode: { $Column: 3 }, $Easing: { $Left: $JssorEasing$.$EaseInOutCubic }, $Opacity: 2, $Brother: { $Duration: 1500, $Opacity: 2 } }
    ];
    
    var jssor_FlyTwins = [
        //Fly Twins
        { $Duration: 1500, x: 0.3, $During: { $Left: [0.6, 0.4] }, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Outside: true, $Brother: { $Duration: 1000, x: -0.3, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } }
    ];
    
    var jssor_RotateInOut = [
        //Rotate in+ out-
        { $Duration: 1500, x: -0.3, y: 0.5, $Zoom: 1, $Rotate: 0.1, $During: { $Left: [0.6, 0.4], $Top: [0.6, 0.4], $Rotate: [0.6, 0.4], $Zoom: [0.6, 0.4] }, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Brother: { $Duration: 1000, $Zoom: 11, $Rotate: -0.5, $Easing: { $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Shift: 200 } }
    ];
    
    var jssor_RotateAxisUpOverlap = [
        //Rotate Axis up overlap
        { $Duration: 1200, x: 0.25, y: 0.5, $Rotate: -0.1, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2, $Brother: { $Duration: 1200, x: -0.1, y: -0.7, $Rotate: 0.1, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Top: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInQuad }, $Opacity: 2 } }
    ];
    
    var jssor_ChessReplaceTB = [
        //Chess Replace TB
        { $Duration: 1600, x: 1, $Rows: 2, $ChessMode: { $Row: 3 }, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1600, x: -1, $Rows: 2, $ChessMode: { $Row: 3 }, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } }
    ];
    
    var jssor_ChessReplaceLR = [
        //Chess Replace LR
        { $Duration: 1600, y: -1, $Cols: 2, $ChessMode: { $Column: 12 }, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1600, y: 1, $Cols: 2, $ChessMode: { $Column: 12 }, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } }
    ];
    
    var jssor_ShiftTB = [
        //Shift TB
        { $Duration: 1200, y: 1, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1200, y: -1, $Easing: { $Top: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } }
    ];
    
    var jssor_ShiftLR = [
        //Shift LR
        { $Duration: 1200, x: 1, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Brother: { $Duration: 1200, x: -1, $Easing: { $Left: $JssorEasing$.$EaseInOutQuart, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 } }
    ];
   

    var _CaptionTransitions = [
    //CLIP|LR
    {$Duration: 900, $Clip: 3, $Easing: $JssorEasing$.$EaseInOutCubic },
    //CLIP|TB
    {$Duration: 900, $Clip: 12, $Easing: $JssorEasing$.$EaseInOutCubic },

    //DDGDANCE|LB
    {$Duration: 1800, x: 0.3, y: -0.3, $Zoom: 1, $Easing: { $Left: $JssorEasing$.$EaseInJump, $Top: $JssorEasing$.$EaseInJump, $Zoom: $JssorEasing$.$EaseOutQuad }, $Opacity: 2, $During: { $Left: [0, 0.8], $Top: [0, 0.8] }, $Round: { $Left: 0.8, $Top: 2.5} },
    //DDGDANCE|RB
    {$Duration: 1800, x: -0.3, y: -0.3, $Zoom: 1, $Easing: { $Left: $JssorEasing$.$EaseInJump, $Top: $JssorEasing$.$EaseInJump, $Zoom: $JssorEasing$.$EaseOutQuad }, $Opacity: 2, $During: { $Left: [0, 0.8], $Top: [0, 0.8] }, $Round: { $Left: 0.8, $Top: 2.5} },

    //TORTUOUS|HL
    {$Duration: 1500, x: 0.2, $Zoom: 1, $Easing: { $Left: $JssorEasing$.$EaseOutWave, $Zoom: $JssorEasing$.$EaseOutCubic }, $Opacity: 2, $During: { $Left: [0, 0.7] }, $Round: { $Left: 1.3} },
    //TORTUOUS|VB
    {$Duration: 1500, y: -0.2, $Zoom: 1, $Easing: { $Top: $JssorEasing$.$EaseOutWave, $Zoom: $JssorEasing$.$EaseOutCubic }, $Opacity: 2, $During: { $Top: [0, 0.7] }, $Round: { $Top: 1.3} },

    //ZMF|10
    {$Duration: 600, $Zoom: 11, $Easing: { $Zoom: $JssorEasing$.$EaseInExpo, $Opacity: $JssorEasing$.$EaseLinear }, $Opacity: 2 },

    //ZML|R
    {$Duration: 600, x: -0.6, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2 },
    //ZML|B
    {$Duration: 600, y: -0.6, $Zoom: 11, $Easing: { $Top: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2 },

    //ZMS|B
    {$Duration: 700, y: -0.6, $Zoom: 1, $Easing: { $Top: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2 },

    //ZM*JDN|LB
    {$Duration: 1200, x: 0.8, y: -0.5, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseOutCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Top: [0, 0.5]} },
    //ZM*JUP|LB
    {$Duration: 1200, x: 0.8, y: -0.5, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Top: [0, 0.5]} },
    //ZM*JUP|RB
    {$Duration: 1200, x: -0.8, y: -0.5, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Top: [0, 0.5]} },

    //ZM*WVR|LT
    {$Duration: 1200, x: 0.5, y: 0.3, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseInWave }, $Opacity: 2, $Round: { $Rotate: 0.8} },
    //ZM*WVR|RT
    {$Duration: 1200, x: -0.5, y: 0.3, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseInWave }, $Opacity: 2, $Round: { $Rotate: 0.8} },
    //ZM*WVR|TL
    {$Duration: 1200, x: 0.3, y: 0.5, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseInWave, $Top: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Round: { $Rotate: 0.8} },
    //ZM*WVR|BL
    {$Duration: 1200, x: 0.3, y: -0.5, $Zoom: 11, $Easing: { $Left: $JssorEasing$.$EaseInWave, $Top: $JssorEasing$.$EaseLinear }, $Opacity: 2, $Round: { $Rotate: 0.8} },

    //RTT|10
    {$Duration: 700, $Zoom: 11, $Rotate: 1, $Easing: { $Zoom: $JssorEasing$.$EaseInExpo, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInExpo }, $Opacity: 2, $Round: { $Rotate: 0.8} },

    //RTTL|R
    {$Duration: 700, x: -0.6, $Zoom: 11, $Rotate: 1, $Easing: { $Left: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $Round: { $Rotate: 0.8} },
    //RTTL|B
    {$Duration: 700, y: -0.6, $Zoom: 11, $Rotate: 1, $Easing: { $Top: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic, $Opacity: $JssorEasing$.$EaseLinear, $Rotate: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $Round: { $Rotate: 0.8} },

    //RTTS|R
    {$Duration: 700, x: -0.6, $Zoom: 1, $Rotate: 1, $Easing: { $Left: $JssorEasing$.$EaseInQuad, $Zoom: $JssorEasing$.$EaseInQuad, $Rotate: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseOutQuad }, $Opacity: 2, $Round: { $Rotate: 1.2} },
    //RTTS|B
    {$Duration: 700, y: -0.6, $Zoom: 1, $Rotate: 1, $Easing: { $Top: $JssorEasing$.$EaseInQuad, $Zoom: $JssorEasing$.$EaseInQuad, $Rotate: $JssorEasing$.$EaseInQuad, $Opacity: $JssorEasing$.$EaseOutQuad }, $Opacity: 2, $Round: { $Rotate: 1.2} },

    //RTT*JDN|RT
    {$Duration: 1000, x: -0.8, y: 0.5, $Zoom: 11, $Rotate: 0.2, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseOutCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Top: [0, 0.5]} },
    //RTT*JDN|LB
    {$Duration: 1000, x: 0.8, y: -0.5, $Zoom: 11, $Rotate: 0.2, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseOutCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Top: [0, 0.5]} },
    //RTT*JUP|RB
    {$Duration: 1000, x: -0.8, y: -0.5, $Zoom: 11, $Rotate: 0.2, $Easing: { $Left: $JssorEasing$.$EaseLinear, $Top: $JssorEasing$.$EaseInCubic, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Top: [0, 0.5]} },
    {$Duration: 1000, x: -0.5, y: 0.8, $Zoom: 11, $Rotate: 1, $Easing: { $Left: $JssorEasing$.$EaseInCubic, $Top: $JssorEasing$.$EaseLinear, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Left: [0, 0.5] }, $Round: { $Rotate: 0.5 } },
    //RTT*JUP|BR
    {$Duration: 1000, x: -0.5, y: -0.8, $Zoom: 11, $Rotate: 0.2, $Easing: { $Left: $JssorEasing$.$EaseInCubic, $Top: $JssorEasing$.$EaseLinear, $Zoom: $JssorEasing$.$EaseInCubic }, $Opacity: 2, $During: { $Left: [0, 0.5]} },

    //R|IB
    {$Duration: 900, x: -0.6, $Easing: { $Left: $JssorEasing$.$EaseInOutBack }, $Opacity: 2 },
    //B|IB
    {$Duration: 900, y: -0.6, $Easing: { $Top: $JssorEasing$.$EaseInOutBack }, $Opacity: 2 },

    ];
    <#-- END Variables to use for transitions for jssor slider -->
    
</script>
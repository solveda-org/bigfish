<script type="text/javascript">

    var displayDialogId;
    var myDialog;
    var titleText;
    function displayDialogBox(dialogPurpose) {
       var dialogId = '#' + dialogPurpose + 'dialog';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       showDialog(dialogId, displayDialogId, titleText);
    }
   
    function showDialog(dialog, displayDialog, titleText) {
        myDialog = jQuery(displayDialog).dialog({
            modal: true,
            draggable: true,
            resizable: true,
            width: 'auto',
            autoResize: true,
            position: 'center',
            title: titleText
        });
        // adjust titlebar width mannualy - Workaround for IE7 titlebar width bug
        jQuery(myDialog).siblings('.ui-dialog-titlebar').width(jQuery(myDialog).width());
    }
    
    function confirmDialogResult(result, dialogPurpose) {
        dialogId = '#'+ dialogPurpose +'dialog';
        displayDialogId = '#'+ dialogPurpose +'displayDialog';
        jQuery(displayDialogId).dialog('close');
        if (result == 'Y') {
            postConfirmDialog();
        }
    }
    function postConfirmDialog() {
        form = document.${commonConfirmDialogForm!"detailForm"};
        form.action="<@ofbizUrl>${commonConfirmDialogAction!"confirmAction"}</@ofbizUrl>";
        form.submit();
    }
    // Popup window code
    function newPopupWindow(url) {
        popupWindow = window.open(
            url,'popUpWindow','height=350,width=500,left=400,top=200,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes')
    }
    function deleteConfirm(appendText)
    {
        jQuery('.confirmTxt').html('${confirmDialogText!""} '+appendText+'?');
        displayDialogBox('confirm_');
    }
    function submitSearchForm(form) {
        var searchText = form.searchText.value;
        <#assign SEARCH_DEFAULT_TEXT = Static["com.osafe.util.Util"].getProductStoreParm(request,"SEARCH_DEFAULT_TEXT")!""/>
        if(searchText == "" || searchText == "${StringUtil.wrapString(SEARCH_DEFAULT_TEXT!)}") {
            displayDialogBox('search_');
            return false;
        } else {
            form.submit();
        }
    }
   
    function displayActionDialogBox(dialogPurpose,elm) 
    {
       var params = jQuery(elm).siblings('input.param').serialize();
       var dialogId = '#' + dialogPurpose + 'dialog';
       var displayContainerId = '#' + dialogPurpose + 'Container';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       jQuery(displayContainerId).html('<div id=loadingImg></div>');
       getActionDialog(displayContainerId,params);
       showDialog(dialogId, displayDialogId, titleText);
        
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

    function onImgError(elem,type) {
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
      // disable onerror to prevent endless loop
      elem.onerror = "";
      return true;
    }
    
    // utility function to retrieve a future expiration date in proper format;
    // pass three integer parameters for the number of days, hours,
    // and minutes from now you want the cookie to expire; all three
    // parameters required, so use zeros where appropriate
    function getExpDate(days, hours, minutes) {
        var expDate = new Date();
        if (typeof days == "number" && typeof hours == "number" && typeof hours == "number") {
            expDate.setDate(expDate.getDate() + parseInt(days));
            expDate.setHours(expDate.getHours() + parseInt(hours));
            expDate.setMinutes(expDate.getMinutes() + parseInt(minutes));
            return expDate.toGMTString();
        }
    }
    
    // utility function called by getCookie()
    function getCookieVal(offset) {
        var endstr = document.cookie.indexOf (";", offset);
        if (endstr == -1) {
            endstr = document.cookie.length;
        }
        return unescape(document.cookie.substring(offset, endstr));
    }
    
    // primary function to retrieve cookie by name
    function getCookie(name) {
        var arg = name + "=";
        var alen = arg.length;
        var clen = document.cookie.length;
        var i = 0;
        while (i < clen) {
            var j = i + alen;
            if (document.cookie.substring(i, j) == arg) {
                return getCookieVal(j);
            }
            i = document.cookie.indexOf(" ", i) + 1;
            if (i == 0) break; 
        }
        return null;
    }
    
    // store cookie value with optional details as needed
    function setCookie(name, value, expires, path, domain, secure) {
        document.cookie = name + "=" + escape (value) +
            ((expires) ? "; expires=" + expires : "") +
            ((path) ? "; path=" + path : "") +
            ((domain) ? "; domain=" + domain : "") +
            ((secure) ? "; secure" : "");
    }
    
    // remove the cookie by setting ancient expiration date
    function deleteCookie(name,path,domain) {
        if (getCookie(name)) {
            document.cookie = name + "=" +
                ((path) ? "; path=" + path : "") +
                ((domain) ? "; domain=" + domain : "") +
                "; expires=Thu, 01-Jan-70 00:00:01 GMT";
        }
    }
    
 //Light Box Cart

    jQuery(document).ready(function () { 
        jQuery('.dateEntry').each(function(){datePicker(this);});
       
        jQuery('.showLightBoxCart').hover(
            function(e) {
               <#assign shoppingCart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request)! />  
                    <#if shoppingCart?has_content >
                        <#assign cartCount = shoppingCart.getTotalQuantity()!"0" />
                        <#assign cartSubTotal = shoppingCart.getSubTotal()!"0" />
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
                        jQuery(dialogHolder).addClass('lightBoxCartContainer');
                        var titlebar = jQuery(dialogHolder).find(".ui-dialog-titlebar");
                        jQuery(titlebar).attr('id', 'lightBoxCartTitleBar');
                        jQuery('.lightBoxCartContainer').attr('id', 'lightBoxCartContainerId');
                    </#if>
            },
            function() {
                //do nothing. Let functions below handle mouseout
            }
        );
        
        <#assign lightDelayMs = Static["com.osafe.util.Util"].getProductStoreParm(request,"LIGHTBOX_DELAY_MS")!0/>  
        jQuery('#eCommerceNavBar').mouseover(function(e){
            var id = e.target.id;
            if((id != "lightCart_displayDialog") && (id != "lightBoxCartTitleBar"))
            {
                jQuery('.lightBoxCartContainer').delay(${lightDelayMs}).slideUp(850, function() {
                jQuery('.lightBoxCartContainer').remove();  
                }); 
            }
        }); 
        jQuery('#eCommercePageBody').mouseover(function(e){
            var id = e.target.id;
            if((id != "lightCart_displayDialog") && (id != "lightBoxCartTitleBar"))
            {
                jQuery('.lightBoxCartContainer').delay(${lightDelayMs}).slideUp(850, function() {
                jQuery('.lightBoxCartContainer').remove();  
                }); 
            }
        });
        jQuery('#eCommerceHeader').mouseover(function(e){
            var id = e.target.id;
            if((id != "siteShoppingCartSize") && (id != "lightCart_displayDialog"))
            {
                jQuery('.lightBoxCartContainer').delay(${lightDelayMs}).slideUp(850, function() {
                jQuery('.lightBoxCartContainer').remove();  
                }); 
            }
        });
        
        jQuery(window).scroll(function(){
         var heightBody = jQuery('#eCommercePageBody').height(); 
         var y = jQuery(window).scrollTop(); 
         if( y > (heightBody*.10) )
         {
           jQuery("#scrollToTop").fadeIn("slow"); 
         }
         else
         {
          jQuery('#scrollToTop').fadeOut('slow');
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
                source: function(request, response) {
                jQuery.ajax({
                    url: "<@ofbizUrl secure="${request.isSecure()?string}">findAutoSuggestions?searchText="+searchText+"</@ofbizUrl>",
                    dataType: "json",
                    type: "POST",
                    success: function(data) {
                    if(data.autoSuggestionList != null)
                    {
                        response(jQuery.map(data.autoSuggestionList, function(item) 
                        {
                            return {
                                value: item
                            }
                        }))
                    }
                    else
                    {
                        response(function() {
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
                jQuery('#pincodeCheckContainer').replaceWith(data);
            });
    });
    
    jQuery('.cancelPinCodeChecker').click(function(event) {
            event.preventDefault();
            jQuery(displayDialogId).dialog('close');
    });
    
    });
    
    function displayLightDialogBox(dialogPurpose) {
       var dialogId = '#' + dialogPurpose + 'dialog';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       dialogTitleId = '#' + dialogPurpose + 'dialogBoxTitle';
       titleText = jQuery(dialogTitleId).val();
       showLightBoxDialog(dialogId, displayDialogId, titleText);
    }
    
    function showLightBoxDialog(dialog, displayDialog, titleText) {
        myDialog = jQuery(displayDialog).dialog({
            modal: false,
            draggable: true,
            resizable: true,
            width: 'auto',
            autoResize:true,
            position: 'center',
            title: titleText
        });
        // adjust titlebar width mannualy - Workaround for IE7 titlebar width bug
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
//end Light Box Cart

    

</script>
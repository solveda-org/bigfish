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
            autoResize:true,
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
   
    function displayActionDialogBox(dialogPurpose,elm) {
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
          jQuery(myDialog).dialog( "option", "position", 'top' );
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
}</script>
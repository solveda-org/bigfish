<script type="text/javascript">

    var displayDialogId;
    var myDialog;
    function displayDialogBox(dialogPurpose) {
       var dialogId = '#' + dialogPurpose + 'dialog';
       displayDialogId = '#' + dialogPurpose + 'displayDialog';
       showDialog(dialogId, displayDialogId);
    }
   
    function showDialog(dialog, displayDialog) {
        myDialog = jQuery(displayDialog).dialog({
            modal: true,
            draggable: true,
            resizable: true,
            width: 'auto',
            autoResize:true,
            position: ['center','Top']
        });
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
       var displayDialogId = '#' + dialogPurpose + 'displayDialog';
       var displayContainerId = '#' + dialogPurpose + 'Container';
       jQuery(displayContainerId).html('<div id=loadingImg></div>');
       getActionDialog(displayContainerId,params);
       showDialog(dialogId, displayDialogId);
        
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
      jQuery.getScript(url, function(data, textStatus, jqxhr)
      {
          jQuery(displayContainerId).replaceWith(data);
      });
  }

   function updateCart() {
      var cartItemsNo = ${shoppingCart.items()?size!"0"};
      var lowerLimit = ${PDP_QTY_MIN!"1"};
      var upperLimit = ${PDP_QTY_MAX!"99"};
      
      for (var i=0;i<cartItemsNo;i++)
      {
          var quantity = jQuery('#update_'+i).val();
      
	      if(quantity < lowerLimit)
	      {
	          alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPMinQtyError,'\"','\\"'))}");
	          return false;
	      }
	      if(upperLimit!= 0 && quantity > upperLimit)
	      {
	          alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPMaxQtyError,'\"','\\"'))}");
	          return false;
	      }
	      if(!isWhole(quantity))
	      {
	          alert("${StringUtil.wrapString(StringUtil.replaceString(uiLabelMap.PDPQtyDecimalNumberError,'\"','\\"'))}");
	          return false;
	      }
      }
      
      document.cartform.submit();
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
</script>
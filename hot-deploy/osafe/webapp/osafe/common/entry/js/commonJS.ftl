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
      jQuery.getScript(url, function(data, textStatus, jqxhr)
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
</script>
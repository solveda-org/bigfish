
<script type="text/javascript">
    jQuery(document).ready(function () {
        jQuery('.dateEntry').each(function(){datePicker(this);});
        jQuery('.characterLimit').each(function(){restrictTextLength(this);});

        var ts = jQuery.tablesorter;
        ts.addParser({
            id: "customDate",
            is: function (s) {
                return /\d{1,2}[\/\-]\d{1,2}[\/\-]\d{2,4}/.test(s);
            }, format: function (s, table) {
                var c = table.config;
                s = s.replace(/\-/g, "/");
                if (c.dateFormat == "us") {
                    // reformat the string in ISO format
                    s = s.replace(/(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})/, "$3/$1/$2");
                } else if (c.dateFormat == "uk") {
                    // reformat the string in ISO format
                    s = s.replace(/(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})/, "$3/$2/$1");
                } else if (c.dateFormat == "dd/mm/yy" || c.dateFormat == "dd-mm-yy" || c.dateFormat == "dd/mm/y" ) {
                    s = s.replace(/(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{2})/, "$2/$1/$3");
                }
                return jQuery.tablesorter.formatFloat(new Date(s).getTime());
            }, type: "numeric"
        });

        var inner = {};
        jQuery('#sortTable th.dateCol').each(function() {
            inner[jQuery(this).index('')] = { sorter: 'customDate'};
        });
        jQuery("#sortTable").tablesorter({
          <#if FORMAT_DATE?exists && FORMAT_DATE?has_content>
            <#assign format = StringUtil.wrapString(FORMAT_DATE.toLowerCase()) />
          <#else>
            <#assign format = "mm/dd/yy" />
          </#if>
          dateFormat: '${format}',
          headers: inner
        });

    });

 function datePicker(triger){
   jQuery(triger).datepicker({
       showOn: 'button',
       buttonImage: '<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>',
       buttonImageOnly: false,
       <#if FORMAT_DATE?exists && FORMAT_DATE?has_content>
         <#assign format = StringUtil.wrapString(FORMAT_DATE.toLowerCase()) />
         <#assign format = format?replace("yy", "y") />
       <#else>
         <#assign format = "mm/dd/y" />
       </#if>
       dateFormat: '${format}'
   });
 }
 function dateTimePicker(triger){
	 <#assign datform = FORMAT_DATE?default("mm/dd/y") />
 	  jQuery(triger).dateTimepicker({
           showOn: 'button',
           buttonImage: '<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>',
           buttonImageOnly: false,
           dateFormat:datform
         });
 }
 function restrictTextLength(textArea){
	        var maxchar = jQuery(textArea).attr('maxlength');
            var curLen = jQuery(textArea).val().length;
            var regCharLen = lineBreakCount(jQuery(textArea).val());
            jQuery(textArea).next('.textCounter').html("* "+maxchar+" ${uiLabelMap.CharactersLimitLabel} ("+(maxchar - (curLen+regCharLen))+" ${uiLabelMap.CharactersLeftLabel})");
            jQuery(textArea).keyup(function() {
                var cnt = jQuery(this).val().length;
                var regCharLen = lineBreakCount(jQuery(this).val());
                var remainingchar = maxchar - (cnt + regCharLen);
                if(remainingchar < 0){
                    jQuery(this).next('.textCounter').html("* "+maxchar+' ${uiLabelMap.CharactersLimitLabel} (0 ${uiLabelMap.CharactersLeftLabel})');
                    jQuery(this).val(jQuery(this).val().slice(0, (maxchar-regCharLen)));
                } else{
                    jQuery(this).next('.textCounter').html("* "+maxchar+' ${uiLabelMap.CharactersLimitLabel} ('+remainingchar+' ${uiLabelMap.CharactersLeftLabel})');
                }
            });
 }
 function lineBreakCount(str){
        /* counts \n */
        try {
            return((str.match(/[^\n]*\n[^\n]*/gi).length));
        } catch(e) {
            return 0;
		}
	}


    function showContextInfotip(e, elm, nextDiv)
    {
        var contextIconBox = jQuery(elm).find('div.'+nextDiv+':first');
        var contextIconBoxHeight = jQuery(contextIconBox).height();
        var elemPosBottom = e.clientY;
        var browserVieportHeight = jQuery(window).height();
        if(contextIconBox+':hidden')
        {
        if(document.all)e = event;
        jQuery(contextIconBox).css(
          {
             position:'absolute',
             'z-index': '9999'
          }
        );
        
        if((contextIconBoxHeight + elemPosBottom) > browserVieportHeight)
        {
            jQuery(contextIconBox).addClass("contextIconBoxArrowBottomRight");
            jQuery(contextIconBox).removeClass("contextIconBoxArrowTopRight");
        }
        else
        {
            jQuery(contextIconBox).removeClass("contextIconBoxArrowBottomRight");
            jQuery(contextIconBox).addClass("contextIconBoxArrowTopRight");
        }
        jQuery(contextIconBox).show();
        }
    } 
    
    function hideContextInfotip(e, elm, nextDiv)
    {
        if(document.all)e = event;
        
        var contextIconBox = jQuery(elm).find('div.'+nextDiv+':first');
        jQuery(contextIconBox).hide();
    }
    
    function setMaxLength(textArea)
    {
	        var maxchar = jQuery(textArea).attr('maxlength');
            var curLen = jQuery(textArea).val().length;
            var regCharLen = lineBreakCount(jQuery(textArea).val());
            jQuery(textArea).keyup(function() {
                var cnt = jQuery(this).val().length;
                var regCharLen = lineBreakCount(jQuery(this).val());
                var remainingchar = maxchar - (cnt + regCharLen);
                if(remainingchar < 0){
                    jQuery(this).val(jQuery(this).val().slice(0, (maxchar-regCharLen)));
                } else{
                }
            });
    }
</script>
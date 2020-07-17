<script type="text/javascript">
    jQuery(document).ready(function () 
    {
    	function hideDialog(dialog, displayDialog) 
    	{
	    	jQuery("#lookupCloseButton").hide();
	        jQuery(dialog).hide();
	        jQuery(displayDialog).fadeOut(300);
	    }
	    
	    jQuery("#lookupCloseButton").click(function (e){
           hideDialog('#lookUpDialog', '#displayLookUpDialog');
           e.preventDefault();
       });
       <#if parameters.featureIdValue?has_content>
           var selectedFeatureJS = jQuery("#${parameters.featureIdValue}").val();
           var selectedFeatureValues = selectedFeatureJS.split(',');
    		count = 0;
    		jQuery('#distinguishProductFeatureMulti option').each(function () 
    		{
    		    if(selectedFeatureValues.indexOf(jQuery(this).val()) != -1)
        		{
        		    jQuery(this).attr('selected', 'selected');
        		}
        		count = count +1;
     		});
           
       </#if>
     });
     
     function setFeatureId(elm)
     {
         var selectedValue = jQuery("#distinguishProductFeatureMulti").val();
         set_values(selectedValue,'');
         //jQuery("#donePickedFeature").attr("href", "javascript:set_values('"+selectedValue+"','')");
     }
</script>

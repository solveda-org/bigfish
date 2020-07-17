
var $j = jQuery.noConflict();
jQuery(document).ready(function() {

    //Code for dependent select functionality, assign "dependentSelectMaster" class for respective select box
    if (jQuery.isFunction(jQuery('.' + jQuery(this).attr('id') + ":first").dependent)) {
        jQuery('form .dependentSelectMaster').each(function () {
           jQuery('.' + jQuery(this).attr('id') + ":first").dependent({ parent: jQuery(this).attr('id')});
        });
    }
    });

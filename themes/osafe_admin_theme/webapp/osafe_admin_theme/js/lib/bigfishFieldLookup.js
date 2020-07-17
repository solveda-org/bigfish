var tar1;
var tar2;
function openLookup(target, target2, viewName, lookupWidth, lookupHeight, lookupPosition, fadeBackground) {
    jQuery('#lookupBody').html("");
    tar1 = target;
    tar2 = target2;
    jQuery.get(viewName, function(data) {
    	updateAndAddListener(data);
    });
    jQuery('#displayLookUpDialog').width(lookupWidth);
    jQuery('#displayLookUpDialog').height(lookupHeight);
    setPosition(lookupPosition, jQuery('#displayLookUpDialog'));
    showDialog('#lookUpDialog', '#displayLookUpDialog', "lookup");
}

function updateAndAddListener(data) {
	jQuery('#lookupBody').html(data);
    jQuery('#lookupBody').find('form').each(function(){
    	var frm = jQuery(this);
        jQuery(frm).submit(function() {
        	items = {};
        	items = jQuery(frm).serialize();
        	url = jQuery(frm).attr("action");
        	if(""==url) {
        		alert("Cannot submit form. No action specified");
        		return false;
        	}
        	jQuery.post(url,items,function(data){
        		updateAndAddListener(data)
        	});
        	return false;
        });
    });
}

function set_values(val1, val2){
	jQuery(tar1).val(val1).change();
	jQuery(tar2).val(val2).change();
	hideDialog('#lookUpDialog', '#displayLookUpDialog');
}

function lookupPaginationAjaxRequest(paginationUrl) {
    jQuery.get(paginationUrl, function(data) {
        updateAndAddListener(data);
    });
}

function setPosition(position, lookupDiv) {
    //set layer position
    var bdy = document.body;
    var lookupLeft;
    var lookupTop;
    if (position == "center") {
        lookupLeft = (bdy.offsetWidth / 2) - (180);
        var winHeight = document.viewport.getHeight();
        lookupTop = (winHeight / 2) - (180);
    } else if (position == "right") {
        lookupLeft = (bdy.offsetWidth) - (lookupDiv.width() + 5);
        var scrollOffY = document.viewport.getScrollOffsets().top;
        var winHeight = document.viewport.getHeight();
        lookupTop = (scrollOffY + winHeight / 2) - (lookupDiv.height() / 2);
    } else if (position == "left") {
        lookupLeft = 5;
        var scrollOffY = document.viewport.getScrollOffsets().top;
        var winHeight = document.viewport.getHeight();
        lookupTop = (scrollOffY + winHeight / 2) - (lookupDiv.height() / 2);
    } else if (position == "topright") {
        lookupLeft = (bdy.offsetWidth) - (lookupDiv.width() + 5);
        lookupTop = 5;
    } else if (position == "topleft") {
        lookupLeft = 5;
        lookupTop = 5;
    } else if (position == "topcenter") {
        lookupLeft = (bdy.offsetWidth / 2) - (lookupDiv.width() / 2);
        lookupTop = 5;
    } else {
        //for 'normal', empty etc.
        if (this.pn != null) {
            // IE Fix
        }
    }
    lookupDiv.css("left", lookupLeft + "px");
    lookupDiv.css("top", lookupTop + "px");
}

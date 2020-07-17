/*--------------------------------------------------------------------------*/

Event.onDOMReady(function() {
	var hideFacets = $$('.facetValue.hideThem');
	hideFacets.each(function(element,idx) {
		element.hide();
    });
	
	
	$$(".seeMoreLink").each(function(elmt) {
        elmt.observe('click',function(){
            var facetId = elmt.id.sub('more_', 'facet_');
		    var id = $(facetId).id;
	        var facets = $(id).up('ul.facetGroup').childElements();
		    facets.each(function(element) {
		        var facetClass = element.getAttribute('class');
			    if (element.hasClassName('facetValue hideThem')) {
			        element.toggle();
			    }
		    });
            if (elmt.innerHTML == $('lessLabelLink').value) {
                var remainingId = elmt.id.sub('more_', 'remaining_');
                var remainValue = $('moreLabelLink').value;
                if ($('facetShowItemCnt').value == "true") {
                    remainValue = "";
                    remainValue = $('moreLabelLink').value +' ('+$(remainingId).value+')';
                }
                elmt.update(remainValue);
		    }
		    else
            {
			    elmt.update($('lessLabelLink').value);
            }
        });	
    });	
});

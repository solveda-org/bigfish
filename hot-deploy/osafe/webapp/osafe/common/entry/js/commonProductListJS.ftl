<script language="JavaScript" type="text/javascript">
    <#assign PDP_QTY_MIN = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MIN")!/>
    <#if !PDP_QTY_MIN?has_content>
  	    <#assign PDP_QTY_MIN = "1"/>
    </#if>
    <#assign PDP_QTY_MAX = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_MAX")!/>
    <#if !PDP_QTY_MAX?has_content>
  	    <#assign PDP_QTY_MAX = "99"/>
    </#if>
    <#assign PDP_QTY_DEFAULT = Static["com.osafe.util.Util"].getProductStoreParm(request,"PDP_QTY_DEFAULT")!/>
    <#if !PDP_QTY_DEFAULT?has_content>
  	    <#assign PDP_QTY_DEFAULT = "1"/>
    </#if>
    var detailImageUrl = null;
    function setAddProductIdPlp(name, selectFeatureDiv) 
    {
        jQuery('#'+selectFeatureDiv+"_add_product_id").val(name);
        if(jQuery('#js_plp_qty_'+selectFeatureDiv).val() == null) return;
    }
    function setProductStockPlp(name, selectFeatureDiv) 
    {
        var elm = document.getElementById("plpAddtoCart_"+selectFeatureDiv);
        if(VARSTOCK[name]=="outOfStock")
        {
            elm.setAttribute("onClick","javascript:void(0)");
            jQuery('#plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
        } 
        else 
        {
            jQuery('#plpAddtoCart_'+selectFeatureDiv).removeClass("inactiveAddToCart");
            elm.setAttribute("onClick","javascript:addItemPlpToCart('"+ selectFeatureDiv+"')");
        }
        var elm = document.getElementById("addToWishlist_"+selectFeatureDiv);
        if (elm !=null )
        {
            elm.setAttribute("onClick","javascript:addItemPlpToWishlist('"+ selectFeatureDiv+"')");
            jQuery('#addToWishlist_'+selectFeatureDiv).removeClass("inactiveAddToWishlist");
        }
    }
    
    function addItemPlpToCart(selectFeatureDiv) 
    {
       if(isItemSelectedPlp(selectFeatureDiv)) 
       {
       	   <#-- Get Quantity, Product Id, and Product Name -->
       	   <#-- if quantity div is displayed then get the input value, else use default value of 1 -->
       	   var quantity = Number(1);
       	   if(jQuery('#js_plp_qty_'+selectFeatureDiv).length)
           {
           		var quantitiyValue = jQuery('#js_plp_qty_'+selectFeatureDiv).val();
           		if(quantitiyValue != "")
           		{
           			quantity = quantitiyValue;
           		}
           }
           var add_product_id = jQuery('#'+selectFeatureDiv+"_add_product_id").val();
           var productName = jQuery('#'+selectFeatureDiv+"_add_product_name").val();
           var add_category_id = jQuery('#'+selectFeatureDiv+"_add_category_id").val();
           
           <#-- set hidden input field values that are submitted -->
           jQuery('#plp_add_product_id').val(add_product_id);
           jQuery('#plp_qty').val(quantity);
           jQuery('#plp_add_category_id').val(add_category_id);
           
           <#-- check if qty is whole number -->
		   if(isQtyWhole(quantity,productName))
		   {
		   		if(!(isQtyZero(quantity,productName,add_product_id)))
		   		{
		        	<#-- add qty currently in cart to the qty input -->
	       			quantity = Number(quantity) + Number(getQtyInCart(add_product_id));
		        	
		            <#-- validate qty limits -->
	        		if(validateQtyMinMax(add_product_id,productName,quantity))
	        		{
			           <#-- add to cart action -->
		    		   recurrenceIsChecked = jQuery('#js_pdpPriceRecurrenceCB').is(":checked");
			    	   if(recurrenceIsChecked)
			    	   {
		                  document.${formName!}.action="<@ofbizUrl>${addToCartRecurrenceAction!""}</@ofbizUrl>";
		               }
		               else
		               {
		                  document.${formName!}.action="<@ofbizUrl>${addToCartPlpAction!""}</@ofbizUrl>";
		               }
		               document.${formName!}.submit();
	        		}
        		}
		   }
	   }
	}
	
	
	function addItemPlpToWishlist(selectFeatureDiv) 
    {
       if(isItemSelectedPlp(selectFeatureDiv)) 
       {
       	   <#-- Get Quantity, Product Id, and Product Name -->
       	   <#-- if quantity div is displayed then get the input value, else use default value of 1 -->
       	   var quantity = Number(1);
       	   if(jQuery('#js_plp_qty_'+selectFeatureDiv).length)
           {
           		var quantitiyValue = jQuery('#js_plp_qty_'+selectFeatureDiv).val();
           		if(quantitiyValue != "")
           		{
           			quantity = quantitiyValue;
           		}
           }
           var add_product_id = jQuery('#'+selectFeatureDiv+"_add_product_id").val();
           var productName = jQuery('#detailLink_'+add_product_id).attr("title");
           var add_category_id = jQuery('#'+selectFeatureDiv+"_add_category_id").val();
           
           <#-- set hidden input field values that are submitted -->
           jQuery('#plp_add_product_id').val(add_product_id);
           jQuery('#plp_qty').val(quantity);
           jQuery('#plp_add_category_id').val(add_category_id);
           
           <#-- check if qty is whole number -->
		   if(isQtyWhole(quantity,productName))
		   {
		   		if(!(isQtyZero(quantity,productName,add_product_id)))
		   		{
		        	<#-- add to wish list action -->
	                document.${formName!}.action="<@ofbizUrl>${addToWishListPlpAction!""}</@ofbizUrl>";
	                document.${formName!}.submit();
                }
		   }
	   }
	}
    
    function findIndexPlp(name, selectFeatureDiv) 
    {
        OPT = eval("getFormOption" + selectFeatureDiv + "()");
        for (i = 0; i < OPT.length; i++) 
        {
            if (OPT[i] == name) 
            {
                return i;
            }
        }
        return -1;
    }
    var firstNoSelection = "false";
    function getListPlp(name, index, src, selectFeatureDiv) 
    {
        OPT = eval("getFormOption" + selectFeatureDiv + "()");
    	var noSelection = "false";
        currentFeatureIndex = findIndexPlp(name, selectFeatureDiv);
        if(firstNoSelection == "true")
        {
        	noSelection ="true";
        }
        if(index != -1)
        {
        	var liElm = jQuery('#Li'+name+" li").get(index);
		}
		else
		{
			var liElm = jQuery('#Li'+name+" li").get(0);
			noSelection ="true";
		}
        jQuery(liElm).siblings("li").removeClass("selected");
        jQuery(liElm).addClass("selected");
        
        <#-- set the drop down index for swatch selection -->
        var selectElement = jQuery('div#'+selectFeatureDiv+' select.'+name);
        
        selectElement.selectedIndex = (index*1)+1;
        jQuery(selectElement).find('option').eq((index*1)+1).prop('selected', true);
        
        if (currentFeatureIndex < (OPT.length-1)) 
        {
		    
            <#-- eval the next list if there are more -->
            var selectedValue = jQuery(selectElement).find('option').eq((index*1)+1).val();
            var selectedText = jQuery(selectElement).find('option').eq((index*1)+1).text();
            
            var mapKey = name+'_'+selectedText;

            var VARMAP = eval("getFormOptionVarMap"+ selectFeatureDiv + "()");
            
            if(VARMAP[mapKey]) 
            {
                if(jQuery('#js_pdpQtyDefaultAttributeValue_' + VARMAP[mapKey]).length)
            	{
            		var productAttrPdpQtyDefault = jQuery('#js_pdpQtyDefaultAttributeValue_' + VARMAP[mapKey]).val();
            	}
            	else
            	{
            		var productAttrPdpQtyDefault = Number('${PDP_QTY_DEFAULT!}');
            	}
            	jQuery('#js_plp_qty_'+selectFeatureDiv).val(productAttrPdpQtyDefault);
            }
            if (index == -1) 
            {
               for (i = currentFeatureIndex; i < OPT.length; i++) 
               {
                   var featureName = jQuery('div#'+selectFeatureDiv+' select.js_selectableFeature_'+(i+1)).attr("name");
               
                   if(i == 0)
                   {
                       var selFeaturName = featureName.substr(2,featureName.length);
                       var Variable1 = eval("list" + selFeaturName + "()");
                       jQuery('#plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
	                   jQuery('#addToWishlist_'+selectFeatureDiv).addClass("inactiveAddToWishlist");
                   }
                   else
                   {    
	                   if(i == currentFeatureIndex)
	                   {
	                       var Variable1 = eval("list" + featureName + jQuery('div#'+selectFeatureDiv+' select.js_selectableFeature_'+i).val() + "()");
	                       var Variable1 = eval("listLi" + featureName + jQuery('div#'+selectFeatureDiv+' select.js_selectableFeature_'+i).val() + "()");
	                       jQuery('div#'+selectFeatureDiv+' select.js_selectableFeature_'+(i+1)).children().removeAttr("disabled"); 
	                   }
	                   else
	                   {
	                       var Variable1 = eval("list" + featureName + "()");
	                       var Variable1 = eval("listLi" + featureName + "()");
	                   }
                   }
               } 
              
              
              firstNoSelection = "true";
            } 
            else 
            {
                firstNoSelection = "false";
                var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                var Variable2 = eval("listLi" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                  
                  var elm = document.getElementById("plpAddtoCart_"+selectFeatureDiv);
                  elm.setAttribute("onClick","javascript:addItemPlpToCart('"+ selectFeatureDiv+"')");
                  var elm = document.getElementById("addToWishlist_"+selectFeatureDiv);
                  if (elm !=null )
                  {
                    elm.setAttribute("onClick","javascript:addItemPlpToWishlist('"+ selectFeatureDiv+"')");
                  }
                  if (currentFeatureIndex+1 <= (OPT.length-1) ) 
                  {
	                    
	                    var nextFeatureLength = jQuery('div#'+selectFeatureDiv+' select.'+OPT[(currentFeatureIndex+1)]).find('option').size();
	                    
	                    if(nextFeatureLength == 2) 
	                    {
	                      getListPlp(OPT[(currentFeatureIndex+1)],'0',1, selectFeatureDiv);
	                      jQuery('#plpAddtoCart_'+selectFeatureDiv).removeClass("inactiveAddToCart");
	                      if (elm !=null )
	                      {
	                          jQuery('#addToWishlist_'+selectFeatureDiv).removeClass("inactiveAddToWishlist");
	                      }
	                      return;
	                    } 
	                    else 
	                    {
	                      jQuery('#plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
	                      if (elm !=null )
	                      {
	                          jQuery('#addToWishlist_'+selectFeatureDiv).addClass("inactiveAddToWishlist");
	                      }
	                    }
                  }
                   
            }
            <#-- set the product ID to NULL to trigger the alerts -->
            setAddProductIdPlp('NULL', selectFeatureDiv);

        }
        else 
        {
            
			<#-- this is the final selection -- locate the selected index of the last selection -->
            var indexSelected = selectElement.selectedIndex;
            <#-- using the selected index locate the sku -->
            var sku = jQuery(selectElement).find('option').eq(indexSelected).val();
            
            <#-- set the product ID -->
            if(firstNoSelection == "false")
            {
            	setAddProductIdPlp(sku, selectFeatureDiv);
            }
            else
            {
            	setAddProductIdPlp("", selectFeatureDiv);
            }
            
            var varProductId = jQuery('#'+selectFeatureDiv+"_add_product_id").val();
			
            if(varProductId == "")
            {
            	jQuery('#plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
				jQuery('#addToWishlist_'+selectFeatureDiv).addClass("inactiveAddToWishlist");
			}
			else 
			{
				setProductStockPlp(sku, selectFeatureDiv);
			}
			
			if(noSelection=="true" || varProductId == "")
			{
            	var indexDisplayed = 1;
            	varProductId = jQuery(selectElement).find('option').eq(indexDisplayed).val();
            }
            
            var productAttrPdpQtyDefault="";
            if(jQuery('#js_pdpQtyDefaultAttributeValue_'+varProductId).length)
    		{
    			productAttrPdpQtyDefault = jQuery('#js_pdpQtyDefaultAttributeValue_'+varProductId).val();
    		}
    		else
    		{
    			productAttrPdpQtyDefault = Number('${PDP_QTY_DEFAULT!}');
    		}
    		if(productAttrPdpQtyDefault)
    		{
    		    jQuery('#js_plp_qty_'+selectFeatureDiv).val(productAttrPdpQtyDefault);
    		}
        }
    }
    
    jQuery(document).ready(function () {
  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"QUICKLOOK_ACTIVE")>
    <#if QUICKLOOK_DELAY_MS?has_content && Static["com.osafe.util.Util"].isNumber(QUICKLOOK_DELAY_MS) && QUICKLOOK_DELAY_MS != "0">
      jQuery("div.js_eCommerceThumbNailHolder").hover(function(){jQuery(this).find("div.js_plpQuicklook").fadeIn(${QUICKLOOK_DELAY_MS});},function () {jQuery(this).find("div.js_plpQuicklook").fadeOut(${QUICKLOOK_DELAY_MS});});
    <#else>
      jQuery("div.js_eCommerceThumbNailHolder div.js_plpQuicklook").show();
    </#if>
  </#if>

    jQuery('.js_facetValue.js_hideThem').hide();
    jQuery('.js_facetValue.js_showAllOfThem').hide();
    jQuery('.js_seeLessLink').hide();
    jQuery('.js_showAllLink').hide();

    jQuery('.js_plpFeatureSwatchImage').click(function() {
        var swatchVariant = jQuery(this).next('.js_swatchVariant').clone();

        var swatchVariantOnlinePrice = jQuery(this).nextAll('.js_swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('js_swatchVariantOnlinePrice').addClass('js_plpPriceOnline');
        
        jQuery(this).parents('.productItem').find('.js_plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(this).nextAll('.js_swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('js_swatchVariantListPrice').addClass('js_plpPriceList');
        jQuery(this).parents('.productItem').find('.js_plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(this).nextAll('.js_swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('js_swatchVariantSaveMoney').addClass('js_plpPriceSavingMoney');
        jQuery(this).parents('.productItem').find('.js_plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(this).nextAll('.js_swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('js_swatchVariantSavingPercent').addClass('js_plpPriceSavingPercent');
        jQuery(this).parents('.productItem').find('.js_plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(this).parents('.productItem').find('.js_eCommerceThumbNailHolder').find('.js_swatchProduct').replaceWith(swatchVariant);
        jQuery('.js_eCommerceThumbNailHolder').find('.js_swatchVariant').show().attr("class", "js_swatchProduct");
        jQuery(this).siblings('.js_plpFeatureSwatchImage').removeClass("selected");
        jQuery(this).addClass("selected");
        makePDPUrl(this);
        
        <#if PLP_FACET_GROUP_VARIANT_MATCH?has_content>
          var descFeatureGroup = jQuery(this).prev("input.js_featureGroup").val();
          if(descFeatureGroup != '') {
            jQuery.each( jQuery('.'+descFeatureGroup), function(idx, elm){
              changeSwatchImg(elm);
            });
          }
          
          var title = jQuery(this).attr("title");
          jQuery.each( jQuery('.'+title), function(idx, elm){
            changeSwatchImg(elm);
          });
        </#if>
    });

    jQuery('.js_seeMoreLink').click(function() {
        jQuery(this).hide().parents('li').siblings('li.js_hideThem').show();
        <#-- show js_showAllLink if number of items to show is greater than sys param FACET_VALUE_MAX -->
        
        if(jQuery(this).siblings('.js_showAllLink').length > 0)
        {
        	jQuery(this).siblings('.js_showAllLink').show();
        }
        else
        {
        	jQuery(this).siblings('.js_seeLessLink').show();
        }
    });
    
    jQuery('.js_showAllLink').click(function() {
        jQuery(this).hide().parents('li').siblings('li.js_hideThem').show();
        <#-- show showAll li -->
        jQuery(this).hide().parents('li').siblings('li.js_showAllOfThem').show();
        jQuery(this).siblings('.js_seeLessLink').show();
        <#-- hide js_showAllLink if number of items to show is greater than sys param FACET_VALUE_MAX -->
        jQuery(this).siblings('.js_showAllLink').hide();
    });

    jQuery('.js_seeLessLink').click(function() {
        jQuery(this).hide().parents('li').siblings('li.js_hideThem').hide();
        <#-- if showAll, then also hide showAll li -->
        jQuery(this).hide().parents('li').siblings('li.js_showAllOfThem').hide();
        jQuery(this).siblings('.js_seeMoreLink').show();
    });
    
    jQuery('.js_showHideFacetGroupLink').click(function() 
    {
        jQuery(this).toggleClass("js_seeMoreFacetGroupLink");
        jQuery(this).toggleClass("js_seeLessFacetGroupLink");
        
        jQuery(this).siblings('ul').find('li.js_hideThem').slideToggle();
        jQuery(this).siblings('ul').find('li.js_showAllOfThem').hide();
        <#-- check if js_seeLessLink is currently displayed. If so, hide everything -->
        var seeLessLink = jQuery(this).siblings('ul').find('li').find('.js_seeLessLink');
        var seeMoreLink = jQuery(this).siblings('ul').find('li').find('.js_seeMoreLink');
        if(jQuery(seeLessLink).css('display') != 'none')
        {
        	jQuery(seeLessLink).hide();
        	jQuery(this).siblings('ul').find('li').find('.js_showAllLink').hide();
        	
        }
        else if(jQuery(seeMoreLink).css('display') != 'none')
        {
        	jQuery(seeMoreLink).hide();
        	jQuery(this).siblings('ul').find('li').find('.js_showAllLink').hide();
        	jQuery(this).siblings('ul').find('li.js_hideThem').hide();
        	
        }
        else
        {
        
	        if(jQuery(this).siblings('ul').find('li').find('.js_showAllLink').length > 0)
	        {
	        	jQuery(this).siblings('ul').find('li').find('.js_showAllLink').slideToggle();
	        	jQuery(seeMoreLink).hide();
	        }
	        else
	        {
	        	jQuery(this).siblings('ul').find('li').find('.js_seeLessLink').slideToggle();
	        	jQuery(seeMoreLink).hide();
	        }
        
        }
        
    });
    
    function changeSwatchImg(elm) {
        var swatchVariant = jQuery(elm).next('.js_swatchVariant').clone();
        var swatchVariantOnlinePrice = jQuery(elm).nextAll('.js_swatchVariantOnlinePrice:first').clone().show();
        swatchVariantOnlinePrice.removeClass('js_swatchVariantOnlinePrice').addClass('js_plpPriceOnline');
        jQuery(elm).parents('.productItem').find('.js_plpPriceOnline').replaceWith(swatchVariantOnlinePrice);

        var swatchVariantListPrice = jQuery(elm).nextAll('.js_swatchVariantListPrice:first').clone().show();
        swatchVariantListPrice.removeClass('js_swatchVariantListPrice').addClass('js_plpPriceList');
        jQuery(elm).parents('.productItem').find('.js_plpPriceList').replaceWith(swatchVariantListPrice);
        
        var swatchVariantSaveMoney = jQuery(elm).nextAll('.js_swatchVariantSaveMoney:first').clone().show();
        swatchVariantSaveMoney.removeClass('js_swatchVariantSaveMoney').addClass('js_plpPriceSavingMoney');
        jQuery(elm).parents('.productItem').find('.js_plpPriceSavingMoney').replaceWith(swatchVariantSaveMoney);
        
        var swatchVariantSavingPercent = jQuery(elm).nextAll('.js_swatchVariantSavingPercent:first').clone().show();
        swatchVariantSavingPercent.removeClass('js_swatchVariantSavingPercent').addClass('js_plpPriceSavingPercent');
        jQuery(elm).parents('.productItem').find('.js_plpPriceSavingPercent').replaceWith(swatchVariantSavingPercent);
        
        jQuery(elm).parents('.productItem').find('.js_eCommerceThumbNailHolder').find('.js_swatchProduct').replaceWith(swatchVariant);
        jQuery('.js_eCommerceThumbNailHolder').find('.js_swatchVariant').show().attr("class", "js_swatchProduct");
        jQuery(elm).siblings('.js_plpFeatureSwatchImage').removeClass("selected");
        jQuery(elm).addClass("selected");
        makePDPUrl(elm);
    }
    
    function makePDPUrl(elm) {
        var plpFeatureSwatchImageId = jQuery(elm).attr("id");
        var plpFeatureSwatchImageIdArr = plpFeatureSwatchImageId.split("|");
        var pdpUrlId = plpFeatureSwatchImageIdArr[1]+plpFeatureSwatchImageIdArr[0]; 
        var pdpUrl = document.getElementById(pdpUrlId).value;
        
        var productFeatureType = plpFeatureSwatchImageIdArr[0];
        
        jQuery('#'+plpFeatureSwatchImageIdArr[1]+'_productFeatureType').val(productFeatureType); 
        jQuery(elm).parents('.productItem').find('a.pdpUrl').attr("href",pdpUrl);
        jQuery(elm).parents('.productItem').find('a.pdpUrl.review').attr("href",pdpUrl+"#productReviews");
    }
  
});
    
</script>

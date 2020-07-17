<script language="JavaScript" type="text/javascript">
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
    	var elm = document.getElementById("js_plpAddtoCart_"+selectFeatureDiv);
    	if(jQuery(elm).length)
    	{
	        if(VARSTOCK[name]=="outOfStock")
	        {
	            elm.setAttribute("onClick","javascript:void(0)");
	            jQuery('#js_plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
	        } 
	        else 
	        {
	            jQuery('#js_plpAddtoCart_'+selectFeatureDiv).removeClass("inactiveAddToCart");
	            elm.setAttribute("onClick","javascript:addItemPlpToCart('"+ selectFeatureDiv+"')");
	        }
	        var elm = document.getElementById("js_addToWishlist_"+selectFeatureDiv);
	        if (elm !=null )
	        {
	            elm.setAttribute("onClick","javascript:addItemPlpToWishlist('"+ selectFeatureDiv+"')");
	            jQuery('#js_addToWishlist_'+selectFeatureDiv).removeClass("inactiveAddToWishlist");
	        }
	        
	        checkProductInStorePlp(VARINSTORE[name], selectFeatureDiv);
        }
        
    }
    
    function checkProductInStorePlp(value,selectFeatureDiv) 
    {
        if(value !=null && value=="Y")
        {
            if (jQuery('#js_plpPdpInStoreOnlyLabel_'+selectFeatureDiv).length) 
            {
                jQuery('#js_plpPdpInStoreOnlyLabel_'+selectFeatureDiv).show();
            }
            if (jQuery('#js_plp_div_qty_'+selectFeatureDiv).length) 
            {
                jQuery('#js_plp_div_qty_'+selectFeatureDiv).hide();
            }
            if (jQuery('#js_plpAddtoCart_div_'+selectFeatureDiv).length) 
            {
                jQuery('#js_plpAddtoCart_div_'+selectFeatureDiv).hide();
            }
            if (jQuery('#js_addToWishlist_div_'+selectFeatureDiv).length) 
            {
                jQuery('#js_addToWishlist_div_'+selectFeatureDiv).hide();
            }
        }
        else
        {
            if (jQuery('#js_plpPdpInStoreOnlyLabel_'+selectFeatureDiv).length) 
            {
                jQuery('#js_plpPdpInStoreOnlyLabel_'+selectFeatureDiv).hide();
            }
            if (jQuery('#js_plp_div_qty_'+selectFeatureDiv).length) 
            {
                jQuery('#js_plp_div_qty_'+selectFeatureDiv).show();
            }
            if (jQuery('#js_plpAddtoCart_div_'+selectFeatureDiv).length) 
            {
                jQuery('#js_plpAddtoCart_div_'+selectFeatureDiv).show();
            }
            if (jQuery('#js_addToWishlist_div_'+selectFeatureDiv).length) 
            {
                jQuery('#js_addToWishlist_div_'+selectFeatureDiv).show();
            }
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
    
    jQuery(document).ready(function()
    {
    	jQuery('[id^="js_plp_qty_"]').keyup(function(){
		  var changedQtyId = jQuery(this).attr("id");
		  if(changedQtyId != "")
		  {
		  	var changedQtyIdArray = changedQtyId.split("_");
		  	jQuery("#js_plp_qty_modified_" + changedQtyIdArray[3] + "_" + changedQtyIdArray[4]).val("Y");
		  }
		}); 
    });
    
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
                if(jQuery('#js_swatchProduct_'+VARMAP[mapKey]).length) 
                { 
                    var variantMainImages = jQuery('#js_swatchProduct_'+VARMAP[mapKey]).clone();
                    jQuery('#js_plpThumbImageContainer_'+selectFeatureDiv).html(variantMainImages.html());
                }
                if(jQuery('#js_plpPriceOnline_'+VARMAP[mapKey]).length)
                {
                    var variantOnlinePrice = jQuery('#js_plpPriceOnline_'+VARMAP[mapKey]).clone();
                    jQuery('#js_plpPriceOnline_'+selectFeatureDiv).html(variantOnlinePrice.html());
                }
                if(jQuery('#js_plpPriceList_'+VARMAP[mapKey]).length)
                {
                    var variantListPrice = jQuery('#js_plpPriceList_'+VARMAP[mapKey]).clone();
                    jQuery('#js_plpPriceList_'+selectFeatureDiv).html(variantListPrice.html());
                }
            
            	if(jQuery('#js_plp_qty_'+selectFeatureDiv).length && jQuery('#js_plp_qty_'+selectFeatureDiv).val() == "")
            	{
            		var productAttrPdpQtyDefault = "";
	                if(jQuery('#js_pdpQtyDefaultAttributeValue_' + VARMAP[mapKey]).length)
	            	{
	            		productAttrPdpQtyDefault = jQuery('#js_pdpQtyDefaultAttributeValue_' + VARMAP[mapKey]).val();
	            	}
	            	else
	            	{
	            		productAttrPdpQtyDefault = Number('${PDP_QTY_DEFAULT!}');
	            	}
	            	var userModifiedQty = jQuery('#js_plp_qty_modified_'+selectFeatureDiv).val();
	            	if(productAttrPdpQtyDefault && userModifiedQty == "N")
	            	{
	            		jQuery('#js_plp_qty_'+selectFeatureDiv).val(productAttrPdpQtyDefault);
	            	}
            	}
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
                       jQuery('#js_plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
	                   jQuery('#js_addToWishlist_'+selectFeatureDiv).addClass("inactiveAddToWishlist");
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
                  if (selectedValue !=null )
                  {
                    var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                    var Variable2 = eval("listLi" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
                  }
                  
                  var elmCart = document.getElementById("js_plpAddtoCart_"+selectFeatureDiv);
                  if (elmCart !=null )
                  {
                  	elmCart.setAttribute("onClick","javascript:addItemPlpToCart('"+ selectFeatureDiv+"')");
                  }
                  var elmWishlist = document.getElementById("js_addToWishlist_"+selectFeatureDiv);
                  if (elmWishlist !=null )
                  {
                    elmWishlist.setAttribute("onClick","javascript:addItemPlpToWishlist('"+ selectFeatureDiv+"')");
                  }
                  if (currentFeatureIndex+1 <= (OPT.length-1) ) 
                  {
	                    
	                    var nextFeatureLength = jQuery('div#'+selectFeatureDiv+' select.'+OPT[(currentFeatureIndex+1)]).find('option').size();
	                    
	                    if(nextFeatureLength == 2) 
	                    {
	                      getListPlp(OPT[(currentFeatureIndex+1)],'0',1, selectFeatureDiv);
	                      jQuery('#js_plpAddtoCart_'+selectFeatureDiv).removeClass("inactiveAddToCart");
	                      if (elmWishlist !=null )
	                      {
	                          jQuery('#js_addToWishlist_'+selectFeatureDiv).removeClass("inactiveAddToWishlist");
	                      }
	                      return;
	                    } 
	                    else 
	                    {
	                      jQuery('#js_plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
	                      if (elmWishlist !=null )
	                      {
	                          jQuery('#js_addToWishlist_'+selectFeatureDiv).addClass("inactiveAddToWishlist");
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
            	jQuery('#js_plpAddtoCart_'+selectFeatureDiv).addClass("inactiveAddToCart");
				jQuery('#js_addToWishlist_'+selectFeatureDiv).addClass("inactiveAddToWishlist");
			}
			else 
			{
				setProductStockPlp(sku, selectFeatureDiv);
			}
			if(noSelection=="true" || varProductId == "")
			{
            	var indexDisplayed = 1;
            	varProductId = jQuery(selectElement).find('option').eq(indexDisplayed).val();
            	var variantOnlinePrice = jQuery('#js_plpPriceOnline_Virtual_'+selectFeatureDiv).clone();
                var variantListPrice = jQuery('#js_plpPriceList_Virtual_'+selectFeatureDiv).clone();
                var variantPriceSavingPercent = jQuery('#js_plpPriceSavingPercent_Virtual'+selectFeatureDiv).clone();
                var variantPriceSavingMoney = jQuery('#js_plpPriceSavingMoney_Virtual_'+selectFeatureDiv).clone();
            	sku = varProductId;
            }
            else
            {
                var variantOnlinePrice = jQuery('#js_plpPriceOnline_'+sku).clone();
                var variantListPrice = jQuery('#js_plpPriceList_'+sku).clone();
                var variantPriceSavingPercent = jQuery('#js_plpPriceSavingPercent_'+sku).clone();
                var variantPriceSavingMoney = jQuery('#js_plpPriceSavingMoney_'+sku).clone();
            }
            if(jQuery('#js_plp_qty_'+selectFeatureDiv).length)
            {
	            var productAttrPdpQtyDefault="";
	            if(jQuery('#js_pdpQtyDefaultAttributeValue_'+varProductId).length)
	    		{
	    			productAttrPdpQtyDefault = jQuery('#js_pdpQtyDefaultAttributeValue_'+varProductId).val();
	    		}
	    		else
	    		{
	    			productAttrPdpQtyDefault = Number('${PDP_QTY_DEFAULT!}');
	    		}
	    		var userModifiedQty = jQuery('#js_plp_qty_modified_'+selectFeatureDiv).val();
	    		if(productAttrPdpQtyDefault && userModifiedQty == "N")
	    		{
	    		    jQuery('#js_plp_qty_'+selectFeatureDiv).val(productAttrPdpQtyDefault);
	    		}
    		}
    		
            if(jQuery('#js_swatchProduct_'+sku).length) 
            { 
                var variantMainImages = jQuery('#js_swatchProduct_'+sku).clone();
                jQuery('#js_plpThumbImageContainer_'+selectFeatureDiv).html(variantMainImages.html());
            }
            
            jQuery('#js_plpPriceOnline_'+selectFeatureDiv).html(variantOnlinePrice.html());
            jQuery('#js_plpPriceList_'+selectFeatureDiv).html(variantListPrice.html());
            jQuery('#js_plpPriceSavingPercent_'+selectFeatureDiv).html(variantPriceSavingPercent.html());
            jQuery('#js_plpPriceSavingMoney_'+selectFeatureDiv).html(variantPriceSavingMoney.html());
                
        }
    }
    
    jQuery(document).ready(function () {
           processPLPPageLoad();
    });

    function processPLPPageLoad() {
    	  <#if Static["com.osafe.util.Util"].isProductStoreParmTrue(request,"QUICKLOOK_ACTIVE")>
    	    <#if QUICKLOOK_DELAY_MS?has_content && Static["com.osafe.util.Util"].isNumber(QUICKLOOK_DELAY_MS) && QUICKLOOK_DELAY_MS != "0">
    	      jQuery("div.js_eCommerceThumbNailHolder").hover(function()
    	      {
    	          jQuery(this).find("div.js_plpQuicklook").find('img').fadeIn(${QUICKLOOK_DELAY_MS});
    	      },
    	      function () 
    	      {
    	          jQuery(this).find("div.js_plpQuicklook").find('img').fadeOut(${QUICKLOOK_DELAY_MS});
    	      });
    	    <#else>
    	      jQuery("div.js_eCommerceThumbNailHolder div.js_plpQuicklook").find('img').show();
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
        
    }
    
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

    function solrSearch(elm, searchURL, removeURL, src)
    {
        var ajaxUrl = "";
        jQuery('#eCommercePageBody').append("<div class=facetFilterAjaxImg></div>");
        if(src == "checkbox")
        {
	        if(jQuery(elm).is(":checked"))
	        {
	            ajaxUrl=searchURL;
	        }
	        else
	        {
	            ajaxUrl=removeURL;
	        }
        }
        else
        {
        	ajaxUrl = jQuery(elm).val();
        }
        
        if (ajaxUrl.indexOf("?") == -1)
        {
            ajaxUrl=ajaxUrl+'?rnd='+String((new Date()).getTime()).replace(/\D/gi, "");
        }
        else
        {
            ajaxUrl=ajaxUrl+'&rnd='+String((new Date()).getTime()).replace(/\D/gi, "");
        }
        jQuery.get(ajaxUrl, function(data)
        {
            var eCommercePageBody = jQuery(data).find('#eCommercePageBody');
            jQuery('#eCommercePageBody').replaceWith(eCommercePageBody);
            processPLPPageLoad();
        });
        
    }

    
    function showPLPFlyout(elm)
    {
    	var newHtml = jQuery(elm).find('.js_tooltipHtmlHolder').find('.js_tooltipHtml').html();
    	if (!elmIsEmpty(jQuery(elm).find('.js_tooltipHtmlHolder').find('.js_tooltipHtml').find('.productItem').find('.productItemList'))) 
    	{
	    	var obj2 = jQuery('.js_tooltipText')[0];
			obj2.innerHTML = newHtml;
	    	showTooltip('',elm,'icon');
		} 
    }

</script>
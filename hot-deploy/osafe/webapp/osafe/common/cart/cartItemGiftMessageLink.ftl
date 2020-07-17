<li class="${request.getAttribute("attributeClass")!}<#if lineIndex == 0> firstRow</#if>">
  <div>
	<#if showGiftMessageLink?has_content && showGiftMessageLink>
	  <label>${uiLabelMap.CartItemGiftMessageLinkCaption}</label>
	  <a href="<@ofbizUrl>eCommerceGiftMessage?cartLineIndex=${cartLineIndex!}</@ofbizUrl>">
          <span><#if showEditLabel?exists && showEditLabel?has_content && showEditLabel == "Y">${uiLabelMap.EditGiftMessageLink}<#else>${uiLabelMap.GiftMessageLink}</#if></span>
      </a>
	</#if>
  </div>
 </li>


		


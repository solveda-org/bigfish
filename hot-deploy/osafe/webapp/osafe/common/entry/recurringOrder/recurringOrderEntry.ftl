<#include "component://osafe/webapp/osafe/includes/CommonMacros.ftl"/>
<div id="recurringOrderEntry" class="displayBox">

<input type="hidden" name="productId" value="${urlProductId?if_exists}"/>
<input type="hidden" name="productName" value="${wrappedProductName?if_exists}"/>
<input type="hidden" name="productQtyMin" value="${productQtyMin?if_exists}"/>
<input type="hidden" name="productQtyMax" value="${productQtyMax?if_exists}"/>
<input type="hidden" name="shoppingListId" value="${parameters.shoppingListId?if_exists}"/>
<input type="hidden" name="entryDateTimeFormat" value="${preferredDateFormat?if_exists}"/>

  ${screens.render("component://osafe/widget/EcommerceDivScreens.xml#recurringOrderDetailDivSequence")}
</div>
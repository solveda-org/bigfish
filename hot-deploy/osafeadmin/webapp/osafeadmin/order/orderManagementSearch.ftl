
<!-- start searchBox -->
     <div class="entryRow">
      <div class="entry">
          <label>${uiLabelMap.OrderNoSearchCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="orderId" name="orderId" maxlength="40" value="${parameters.orderId!""}"/>
          </div>
      </div>
      <div class="entry daterange">
          <label>${uiLabelMap.OrderDateCaption}</label>
          <div class="entryInput from">
                <input class="dateEntry" type="text" name="orderDateFrom" maxlength="40" value="${parameters.orderDateFrom!periodFrom!""}"/>
          </div>
          <label class="tolabel">${uiLabelMap.ToCaption}</label>
          <div class="entryInput to">
                <input class="dateEntry" type="text" name="orderDateTo" maxlength="40" value="${parameters.orderDateTo!periodTo!""}"/>
          </div>
      </div>
     </div>
     <div class="entryRow">
      <div class="entry">
          <label>${uiLabelMap.CustomerNoCaption}</label>
          <div class="entryInput">
                <input class="textEntry" type="text" id="partyId" name="partyId" maxlength="40" value="${parameters.partyId!""}"/>
          </div>
      </div>
      <div class="entry">
          <label>${uiLabelMap.EmailCaption}</label>
          <div class="entryInput">
                <input class="textEntry emailEntry" type="text" id="orderEmail" name="orderEmail" maxlength="40" value="${parameters.orderEmail!""}"/>
          </div>
      </div>
     </div>
     <div class="entryRow">
	      <div class="entry long">
	          <label>${uiLabelMap.OrderStatusCaption} </label>
	          <#assign intiCb = "${initializedCB}"/>
	          <div class="entryInput checkbox">
	                    <input type="checkbox" class="checkBoxEntry" name="viewall" id="viewall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','view')" <#if parameters.viewall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="viewcreated" id="viewcreated"  <#if parameters.viewcreated?has_content || viewcreated?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.CreatedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="viewprocessing" name="viewprocessing" value="Y" <#if parameters.viewprocessing?has_content || viewprocessing?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ProcessingLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="viewapproved" id="viewapproved" value="Y" <#if parameters.viewapproved?has_content || viewapproved?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ApprovedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="viewhold" id="viewhold" value="Y" <#if parameters.viewhold?has_content || viewhold?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.HeldLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="viewcompleted" id="viewcompleted" value="Y" <#if parameters.viewcompleted?has_content || viewcompleted?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.CompletedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="viewrejected" id="viewrejected" value="Y" <#if parameters.viewrejected?has_content || viewrejected?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.RejectedLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="viewcancelled" id="viewcancelled" value="Y" <#if parameters.viewcancelled?has_content || viewcancelled?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.CancelledLabel}
	          </div>
	     </div>
     </div>
     <div class="entryRow">
	      <div class="entry short">
	          <label>${uiLabelMap.ExportStatusCaption}</label>
	          <div class="entryInput checkbox small">
	                    <input type="checkbox" class="checkBoxEntry" name="downloadall" id="downloadall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','download')" <#if parameters.downloadall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="downloadnew" id="downloadnew" value="Y" <#if parameters.downloadnew?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.NewLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="downloadloaded" name="downloadloaded" value="Y" <#if parameters.downloadloaded?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportedLabel}
	          </div>
	     </div>
      <div class="entry">
          <label>${uiLabelMap.PromoCodeCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="productPromoCodeId" name="productPromoCodeId" maxlength="40" value="${parameters.productPromoCodeId!""}"/>
          </div>
      </div>
     </div>
<!-- end searchBox -->


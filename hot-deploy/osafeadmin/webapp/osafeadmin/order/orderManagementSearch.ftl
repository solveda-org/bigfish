
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
      <div class="entry medium">
          <label>${uiLabelMap.EmailCaption}</label>
          <div class="entryInput">
                <input class="textEntry emailEntry long" type="text" id="orderEmail" name="orderEmail" maxlength="40" value="${parameters.orderEmail!""}"/>
          </div>
      </div>
     </div>
     <div class="entryRow">
	      <div class="entry long">
	          <label>${uiLabelMap.OrderStatusCaption} </label>
	          <#assign intiCb = "${initializedCB}"/>
	          <div class="entryInput checkbox large">
	                    <input type="checkbox" class="checkBoxEntry" name="viewall" id="viewall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','view')" <#if parameters.viewall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
	                    <#assign orderStatusIncSearch = ORDER_STATUS_INC_SEARCH!"" />
	                    <#if orderStatusIncSearch?has_content>
	                      <#assign orderStatusIncSearchList = Static["org.ofbiz.base.util.StringUtil"].split(orderStatusIncSearch, ",")/>
	                      <#if orderStatusIncSearchList?has_content>
	                        <#list orderStatusIncSearchList as orderStatusId>
	                          <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : orderStatusId?trim}, false)?if_exists/>
	                          <#if statusItem?has_content && statusItem.description?has_content>
	                            <#assign statusDescription = statusItem.description?lower_case />
	                          </#if>
	                          <input type="checkbox" class="checkBoxEntry" name="view${statusDescription!}" id="view${statusDescription!}" value="Y" <#if parameters.get('view${statusDescription!}')?has_content || context.get('view${statusDescription!}')?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${statusItem.description!}
	                        </#list>
	                      </#if>
	                    </#if>    
	          </div>
	     </div>
     </div>
     <div class="entryRow">
	      <div class="entry short">
	          <label>${uiLabelMap.ExportStatusCaption}</label>
	          <div class="entryInput checkbox small">
	              <input type="checkbox" class="checkBoxEntry" name="downloadall" id="downloadall" value="Y" onclick="javascript:setCheckboxes('${searchFormName!""}','download')" <#if parameters.downloadall?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.AllLabel}
	                    
	                    <input type="checkbox" class="checkBoxEntry" name="downloadnew" id="downloadnew" value="Y" <#if parameters.downloadnew?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.NewLabel}
	                    <input type="checkbox" class="checkBoxEntry" name="downloadloaded" id="downloadloaded" value="Y" <#if parameters.downloadloaded?has_content || ((intiCb?exists) && (intiCb == "N"))>checked</#if> />${uiLabelMap.ExportedLabel}
	          </div>
	      </div>   
          <div class="entry">
             <label>${uiLabelMap.ProductNoCaption}</label>
             <div class="entryInput">
               <input class="textEntry" type="text" id="productId" name="productId" maxlength="40" value="${parameters.productId!""}"/>
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


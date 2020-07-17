<!-- start searchBox -->
     <div class="entryRow">
          <label>${uiLabelMap.CustomerNoCaption}</label>
          <div class="entryInput">
            <input class="textEntry" type="text" id="partyId" name="partyId" maxlength="40" value="${parameters.partyId!""}"/>
          </div>
     </div>
     <div class="entryRow">
          <label>${uiLabelMap.CustomerNameCaption}</label>
          <div class="entryInput">
            <input class="textEntry nameEntry" type="text" id="partyName" name="partyName" maxlength="50" value="${parameters.partyName!""}"/>
          </div>
     </div>
     <input type="hidden" name="productStoreall" value="true"/>
     <input type="hidden" name="roleCustomerId" value="true"/>
   
<!-- end searchBox -->
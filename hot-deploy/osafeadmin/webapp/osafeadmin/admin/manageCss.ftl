<div class="displayBox generalInfo">
    <div class="header"><h2>${generalInfoBoxHeading!}</h2></div>
    <div class="boxBody">
<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>&nbsp;</label>
        </div>
        <div class="entryInput checkbox medium">
            <input class="checkBoxEntry" type="radio" onclick="hideShowCssDetail('manageEcommerceCssDetail','manageAdminCssDetail')" id="cssType" name="cssType"  value="Y" checked="checked" />${uiLabelMap.EcommerceCssLabel}
        </div>
    </div>
</div>

<div class="infoRow row">
    <div class="infoEntry long">
        <div class="infoCaption">
            <label>&nbsp</label>
        </div>
        <div class="entryInput checkbox medium">
            <input class="checkBoxEntry" type="radio" onclick="hideShowCssDetail('manageAdminCssDetail','manageEcommerceCssDetail')" id="cssType" name="cssType" value="N" />${uiLabelMap.AdminCssLabelLabel}
        </div>
    </div>
</div>
</div>
 </div>
</div>